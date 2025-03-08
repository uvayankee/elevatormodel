package model.elevator;

import java.lang.reflect.Array;

public class Elevator {

    private DoorsState doorsState;

    private int floor;

    private int[] floorButtons;

    public enum DoorsState {closed, opened}

    public DoorsState getDoorsState() {
        return doorsState;
    }

    public Elevator() {
        doorsState = DoorsState.opened;
        floor = 1;
        floorButtons = new int[] {1, 2};
    }

    public boolean isTrue() {
        return true;
    }



    public DoorsState openDoors() {
        doorsState = DoorsState.opened;
        return getDoorsState();
    }

    public DoorsState closeDoors() {
        doorsState = DoorsState.closed;
        return getDoorsState();
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int[] getFloorButtons() {
        return floorButtons;
    }

}

;
