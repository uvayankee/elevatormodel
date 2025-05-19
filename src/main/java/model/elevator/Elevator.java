package model.elevator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Elevator implements Callable<List<Action>> {

    private final int[] floorButtons;
    private final boolean[] interrupts;
    private final List<Action> actionLog;
    private final BlockingQueue<Action> queue;
    private final BlockingQueue<ElevatorCall> callQueue;
    private final int clockSpeed;
    private volatile DoorsState doorsState;
    private volatile int floor;
    private volatile int nextDestination;
    private volatile boolean running;
    private Thread elevatorThread;
    private FutureTask<List<Action>> actionLogFuture;

    public Elevator() {
        this(2);
    }

    public Elevator(int maxFloor) {
        this(maxFloor, 200);
    }

    public Elevator(int maxFloor, int clockSpeed) {
        this.running = false; // Initialize running state
        this.actionLogFuture = null; // Initialize actionLogFuture
        doorsState = DoorsState.opened;
        floor = 1;
        nextDestination = 1;
        floorButtons = new int[maxFloor + 1];
        interrupts = new boolean[maxFloor + 1];
        actionLog = new CopyOnWriteArrayList<>();
        queue = new LinkedBlockingQueue<>();
        callQueue = new LinkedBlockingQueue<>();
        this.clockSpeed = clockSpeed;

        for (int i = 1; i < maxFloor + 1; i++) {
            floorButtons[i] = i;
            interrupts[i] = false;
        }
    }

    public synchronized FutureTask<List<Action>> startElevator() {
        if (this.elevatorThread != null && this.elevatorThread.isAlive()) {
            return this.actionLogFuture; // Already running, return existing future
        }

        // If not running or never started (or stopped and restarted), create and start a new thread
        this.running = true;
        this.actionLogFuture = new FutureTask<>(this);
        this.elevatorThread = new Thread(this.actionLogFuture);
        this.elevatorThread.setName("ElevatorThread-" + System.currentTimeMillis());
        this.elevatorThread.start();
        return this.actionLogFuture;
    }

    public Thread getElevatorThread() {
        return elevatorThread;
    }

    public void stopElevator() {
        this.running = false;
        if (this.elevatorThread != null && this.elevatorThread.isAlive()) {
            this.elevatorThread.interrupt();
        }
    }

    // Removed queueEnd method as it's no longer used

    public void callElevator(int floor, Action direction) {
        try {
            callQueue.put(new ElevatorCall(floor, direction));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public List<Action> call() {
        try {
            return this.controlLoop();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
            return actionLog; // Return accumulated logs on interrupt
        }
    }

    public synchronized DoorsState getDoorsState() {
        return doorsState;
    }

    public synchronized DoorsState openDoors() {
        if (doorsState != DoorsState.opened) {
            actionLog.add(Action.open);
        }
        doorsState = DoorsState.opened;
        return getDoorsState();
    }

    public synchronized DoorsState closeDoors() {
        if (doorsState != DoorsState.closed) {
            actionLog.add(Action.close);
        }
        doorsState = DoorsState.closed;
        return getDoorsState();
    }

    public synchronized int getFloor() {
        return floor;
    }

    public synchronized void goUp() {
        closeDoors();
        floor++;
        actionLog.add(Action.up);
    }

    public boolean isSameDirection(Action direction) {
        return direction.equals(queue.peek());
    }

    public synchronized void goDown() {
        closeDoors();
        floor--;
        actionLog.add(Action.down);
    }

    private List<Action> controlLoop() throws InterruptedException {
        while (this.running) {
            try {
                handleInterrupts();
                handleTransitCalls();

                // Try to take an action, may block.
                Action action = queue.take(); // This can throw InterruptedException

                switch (action) {
                    case open:
                        openDoors();
                        break;
                    case close:
                        closeDoors();
                        break;
                    case up:
                        goUp();
                        break;
                    case down:
                        goDown();
                        break;
                    // Action.end is no longer handled here; loop terminates via this.running
                    default:
                        break;
                }
                // Only sleep if running is still true after processing an action.
                if (this.running) {
                    Thread.sleep(clockSpeed); // This can also throw InterruptedException
                }
            } catch (InterruptedException e) {
                if (!this.running) {
                    // If stopElevator() caused the interrupt, break the loop
                    break;
                }
                // Otherwise, re-interrupt the thread if it was for another reason
                Thread.currentThread().interrupt();
            }

            if (this.running && queue.isEmpty()) {
                handleNextCall();
            }
        }
        return actionLog;
    }

    public synchronized void goToFloor(int floor) {
        int checkedFloor = boundsCheckFloor(floor);
        if (isInterruptNeeded(checkedFloor)) {
            addInterrupt(checkedFloor);
        } else if (checkedFloor > this.nextDestination) {
            queueMovement(checkedFloor - this.nextDestination, Action.up);
        } else if (checkedFloor < this.nextDestination) {
            queueMovement(checkedFloor - this.nextDestination, Action.down);
        }
        queueOpen();
        this.nextDestination = checkedFloor;
    }

    private void queueOpen() {
        try {
            queue.put(Action.open);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private synchronized boolean isInterruptNeeded(int checkedFloor) {
        return (checkedFloor > this.nextDestination && checkedFloor < this.floor)
                || (checkedFloor < this.nextDestination && checkedFloor > this.floor);
    }

    private void queueMovement(int numberOfFloors, Action direction) {
        for (int i = 0; i < Math.abs(numberOfFloors); i++) {
            try {
                queue.put(direction);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private int boundsCheckFloor(int floor) {
        if (floor >= this.floorButtons.length) {
            floor = this.floorButtons.length - 1;
        } else if (floor < 1) {
            floor = 1;
        }
        return floor;
    }

    public synchronized void addInterrupt(int floor) {
        interrupts[floor] = true;
    }

    public synchronized void handleInterrupts() {
        if (interrupts[floor]) {
            openDoors();
        }
    }

    public void handleTransitCalls() {
        Set<Integer> calledFloors = new ArrayList<>(callQueue).stream().map(ElevatorCall::getFloor).collect(Collectors.toSet());
        if (calledFloors.contains(getFloor())) {
            List<ElevatorCall> callsToRemove = new ArrayList<>();
            for (ElevatorCall elevatorCall : new ArrayList<>(callQueue)) {
                if (isTransitCall(elevatorCall)) {
                    callsToRemove.add(elevatorCall);
                    openDoors();
                }
            }
            callQueue.removeAll(callsToRemove);
        }
    }

    private boolean isTransitCall(ElevatorCall elevatorCall) {
        return elevatorCall.getFloor() == getFloor() &&
                ((isSameDirection(Action.down) && elevatorCall.getDirection() == Action.down)
                || (isSameDirection(Action.up) && elevatorCall.getDirection() == Action.up));
    }

    public void handleNextCall() {
        if (!callQueue.isEmpty()) {
            try {
                ElevatorCall call = callQueue.take(); // This can throw InterruptedException
                goToFloor(call.getFloor());
            } catch (InterruptedException e) {
                if (!this.running) {
                    // If stopping, don't process more calls from queue
                    return; 
                }
                Thread.currentThread().interrupt(); // Preserve interrupt if for other reasons
            }
        }
    }

    public int[] getFloorButtons() {
        return floorButtons;
    }

    public enum DoorsState {closed, opened}

}

