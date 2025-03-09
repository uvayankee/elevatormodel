package model.elevator;

public class ElevatorCall implements Comparable<ElevatorCall> {

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

    @Override
    public int compareTo(ElevatorCall o) {
        if(getFloor() == o.floor && getDirection() == o.direction) {
            return 0;
        }
        else return getFloor() < o.floor ? -1 : 1;
    }

    public enum Direction {up, down}
}
