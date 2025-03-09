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
    private final List<Action> actionLog;
    private final List<Action> queue;

    public Elevator() {
        this(2);
    }

    public Elevator(int maxFloor) {
        doorsState = DoorsState.opened;
        floor = 1;
        nextDestination = 1;
        floorButtons = new int[maxFloor];
        actionLog = new ArrayList<>();
        queue = new ArrayList<>();
        for (int i = 0; i < maxFloor; i++) {
            floorButtons[i] = i + 1;
        }
    }

    public FutureTask<List<Action>> startElevator() {
        FutureTask<List<Action>> actionLogFuture = new FutureTask<>(this);
        Thread thread = new Thread(actionLogFuture);
        thread.start();
        return actionLogFuture;
    }

    public void stopElevator() {
        queue.add(Action.open);
        queue.add(Action.end);
    }

    public List<Action> call() {
        return this.controlLoop();
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

    private List<Action> controlLoop() {
        boolean running = true;
        while (running) {
            if (!queue.isEmpty()) {
                Action action = queue.removeFirst();
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
                }
            } else {
                queue.add(Action.open);
            }
        }
        return actionLog;
    }

    public void goToFloor(int floor) {
        if (floor > this.nextDestination) {
            for (int i = this.nextDestination; i < floor; i++) {
                queue.add(Action.up);
            }
        } else {
            for (int i = floor; i < this.nextDestination; i++) {
                queue.add(Action.down);
            }
        }
        queue.add(Action.open);
        this.nextDestination = floor;
    }

    public int[] getFloorButtons() {
        return floorButtons;
    }

    public enum DoorsState {closed, opened}

}

