package model.elevator;

public class Elevator {

    private DoorsState doorsState;

    public enum DoorsState {closed, opened}

    public DoorsState getDoorsState() {
        return doorsState;
    }

    public Elevator() {
        doorsState = DoorsState.opened;
    }

    public boolean isTrue() {
        return true;
    }



    public boolean openDoors() {
        doorsState = DoorsState.opened;
        return true;
    }

    public boolean closeDoors() {
        doorsState = DoorsState.closed;
        return true;
    }

}

;
