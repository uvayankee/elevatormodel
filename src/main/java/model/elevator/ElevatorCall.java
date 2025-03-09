package model.elevator;

public class ElevatorCall {

    private final int floor;
    private final Direction direction;

    public ElevatorCall(int floor, Direction direction) {
        this.floor = floor;
        this.direction = direction;
    }

    public int getFloor() {
        return floor;
    }

    public Direction getDirection() {
        return direction;
    }

    public enum Direction {up, down}
}
