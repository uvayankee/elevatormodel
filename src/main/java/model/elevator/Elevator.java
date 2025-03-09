package model.elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Elevator implements Callable<List<Action>> {

    private final int[] floorButtons;
    private DoorsState doorsState;
    private int floor;
    private int nextDestination;
    private final boolean[] interrupts;
    private final List<Action> actionLog;
    private final List<Action> queue;
    private final List<ElevatorCall> callqueue;

    public Elevator() {
        this(2);
    }

    public Elevator(int maxFloor) {
        doorsState = DoorsState.opened;
        floor = 1;
        nextDestination = 1;
        floorButtons = new int[maxFloor+1];
        interrupts = new boolean[maxFloor+1];
        actionLog = new ArrayList<>();
        queue = new ArrayList<>();
        callqueue = new ArrayList<>();

        for (int i = 1; i < maxFloor+1; i++) {
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
        if(callqueue.isEmpty()) {
            queue.add(Action.open);
            queue.add(Action.end);
        } else {
            Thread.sleep(200);
            stopElevator();
        }
    }

    public void callElevator(int floor, ElevatorCall.Direction direction) {
        callqueue.add(new ElevatorCall(floor, direction));
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

    public boolean isTrue() {
        return true;
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

    public void goDown() {
        closeDoors();
        floor--;
        actionLog.add(Action.down);
    }

    private List<Action> controlLoop() throws InterruptedException {
        System.out.println("controlLoop");
        System.out.println(queue);
        boolean running = true;
        while (running) {
            handleInterrupts();
            Thread.sleep(200);
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
                System.out.println("Queue is empty");
                handleNextCall();
            }
        }
        return actionLog;
    }

    public void goToFloor(int floor) {
        if (floor > this.nextDestination) {
            if (floor < this.floor) {
                addInterrupt(floor);
            } else {
                for (int i = this.nextDestination; i < floor; i++) {
                    queue.add(Action.up);
                }
            }
        } else {
            if (floor > this.floor) {
                addInterrupt(floor);
            } else {
                for (int i = floor; i < this.nextDestination; i++) {
                    queue.add(Action.down);
                }
            }
        }
        queue.add(Action.open);
        this.nextDestination = floor;
    }

    public void addInterrupt(int floor) {
        interrupts[floor] = true;
    }

    public void handleInterrupts() {
        if (interrupts[floor]) {
            openDoors();
        }
    }

    public void handleNextCall() {
        ElevatorCall call = callqueue.remove(0);
        System.out.println(call);
        goToFloor(call.getFloor());
    }

    public int[] getFloorButtons() {
        return floorButtons;
    }

    public enum DoorsState {closed, opened}

}

