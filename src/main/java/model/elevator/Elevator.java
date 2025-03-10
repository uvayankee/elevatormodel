package model.elevator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

public class Elevator implements Callable<List<Action>> {

    private final int[] floorButtons;
    private final boolean[] interrupts;
    private final List<Action> actionLog;
    private final List<Action> queue;
    private final List<ElevatorCall> callQueue;
    private final int clockSpeed;
    private DoorsState doorsState;
    private int floor;
    private int nextDestination;

    public Elevator() {
        this(2);
    }

    public Elevator(int maxFloor) {
        this(maxFloor, 200);
    }

    public Elevator(int maxFloor, int clockSpeed) {
        doorsState = DoorsState.opened;
        floor = 1;
        nextDestination = 1;
        floorButtons = new int[maxFloor + 1];
        interrupts = new boolean[maxFloor + 1];
        actionLog = new ArrayList<>();
        queue = new ArrayList<>();
        callQueue = new ArrayList<>();
        this.clockSpeed = clockSpeed;

        for (int i = 1; i < maxFloor + 1; i++) {
            floorButtons[i] = i;
            interrupts[i] = false;
        }
    }

    public FutureTask<List<Action>> startElevator() {
        FutureTask<List<Action>> actionLogFuture = new FutureTask<>(this);
        Thread thread = new Thread(actionLogFuture);
        thread.start();
        return actionLogFuture;
    }

    public void stopElevator() throws InterruptedException {
        if (callQueue.isEmpty()) {
            queueOpen();
            queueEnd();
        } else {
            Thread.sleep(clockSpeed);
            stopElevator();
        }
    }

    private void queueEnd() {
        queue.add(Action.end);
    }

    public void callElevator(int floor, Action direction) {
        callQueue.add(new ElevatorCall(floor, direction));
    }

    public List<Action> call() {
        try {
            return this.controlLoop();
        } catch (InterruptedException e) {
            return new ArrayList<>();
        }
    }

    public DoorsState getDoorsState() {
        return doorsState;
    }

    public DoorsState openDoors() {
        if (doorsState != DoorsState.opened) {
            actionLog.add(Action.open);
        }
        doorsState = DoorsState.opened;
        return getDoorsState();
    }

    public DoorsState closeDoors() {
        if (doorsState != DoorsState.closed) {
            actionLog.add(Action.close);
        }
        doorsState = DoorsState.closed;
        return getDoorsState();
    }

    public int getFloor() {
        return floor;
    }

    public void goUp() {
        closeDoors();
        floor++;
        actionLog.add(Action.up);
    }

    public boolean isSameDirection(Action direction) {
        return direction.equals(queue.get(0));
    }

    public void goDown() {
        closeDoors();
        floor--;
        actionLog.add(Action.down);
    }

    private List<Action> controlLoop() throws InterruptedException {
        boolean running = true;
        while (running) {
            handleInterrupts();
            handleTransitCalls();
            Thread.sleep(clockSpeed);
            if (!queue.isEmpty()) {
                Action action = queue.remove(0);
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
                    case end:
                        running = false;
                        break;
                    default:
                        break;
                }
            } else {
                handleNextCall();
            }
        }
        return actionLog;
    }

    public void goToFloor(int floor) {
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
        queue.add(Action.open);
    }

    private boolean isInterruptNeeded(int checkedFloor) {
        return (checkedFloor > this.nextDestination && checkedFloor < this.floor)
                || (checkedFloor < this.nextDestination && checkedFloor > this.floor);
    }

    private void queueMovement(int numberOfFloors, Action direction) {
        for (int i = 0; i < Math.abs(numberOfFloors); i++) {
            queue.add(direction);
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

    public void addInterrupt(int floor) {
        interrupts[floor] = true;
    }

    public void handleInterrupts() {
        if (interrupts[floor]) {
            openDoors();
        }
    }

    public void handleTransitCalls() {
        Set<Integer> calledFloors = callQueue.stream().map(ElevatorCall::getFloor).collect(Collectors.toSet());
        if (calledFloors.contains(getFloor())) {
            for (Iterator<ElevatorCall> iterator = callQueue.iterator(); iterator.hasNext(); ) {
                if (isTransitCall(iterator.next())) {
                    iterator.remove();
                    openDoors();
                }
            }
        }
    }

    private boolean isTransitCall(ElevatorCall elevatorCall) {
        return elevatorCall.getFloor() == getFloor() &&
                ((isSameDirection(Action.down) && elevatorCall.getDirection() == Action.down)
                || (isSameDirection(Action.up) && elevatorCall.getDirection() == Action.up));
    }

    public void handleNextCall() {
        ElevatorCall call = callQueue.remove(0);
        goToFloor(call.getFloor());
    }

    public int[] getFloorButtons() {
        return floorButtons;
    }

    public enum DoorsState {closed, opened}

}

