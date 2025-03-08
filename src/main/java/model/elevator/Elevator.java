package model.elevator;

public class Elevator {

    private final int[] floorButtons;
    private DoorsState doorsState;
    private int floor;

    public Elevator() {
        doorsState = DoorsState.opened;
        floor = 1;
        floorButtons = new int[]{1, 2};
    }

    public Elevator(int maxFloor) {
        doorsState = DoorsState.opened;
        floor = 1;
        floorButtons = new int[maxFloor];
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

    public enum DoorsState {closed, opened}

}

