package model.elevator;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class Elevator {

    private final int[] floorButtons;
    private DoorsState doorsState;
    private int floor;
    private final List<String> actionLog;

    public Elevator() {
        this(2);
    }

    public Elevator(int maxFloor) {
        doorsState = DoorsState.opened;
        floor = 1;
        floorButtons = new int[maxFloor];
        actionLog = new ArrayList<>();
        for (int i = 0; i < maxFloor; i++) {
            floorButtons[i] = i + 1;
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
            actionLog.add("Opening doors");
        }
        doorsState = DoorsState.opened;
        return getDoorsState();
    }

    public DoorsState closeDoors() {
        if (doorsState != DoorsState.closed) {
            actionLog.add("Closing doors");
        }
        doorsState = DoorsState.closed;
        return getDoorsState();
    }

    public int getFloor() {
        return floor;
    }

    public void goUp() {
        floor++;
        actionLog.add("Going up to floor " + getFloor());
    }
    public void goDown() {
        floor--;
        actionLog.add("Going down to floor " + getFloor());
    }

    public void goToFloor(int floor) {
        closeDoors();
        if(floor > this.floor) {
            while (floor > this.floor) {
                goUp();
            }
        } else {
            while (floor < this.floor) {
                goDown();
            }
        }
        openDoors();
    }

    public int[] getFloorButtons() {
        return floorButtons;
    }

    public List<String> getActionLog() {
        return actionLog;
    }

    public enum DoorsState {closed, opened}

}

