package model.elevator;

public class ElevatorCall implements Comparable<ElevatorCall> {

    private final int floor;
    private final Action direction;

    public ElevatorCall(int floor, Action direction) {
        this.floor = floor;
        if (direction == Action.down || direction == Action.up) {
            this.direction = direction;
        }
        else throw new IllegalArgumentException();
    }

    public int getFloor() {
        return floor;
    }

    public Action getDirection() {
        return direction;
    }

    @Override
    public int compareTo(ElevatorCall o) {
        if (getFloor() == o.floor && getDirection() == o.direction) {
            return 0;
        } else return getFloor() < o.floor ? -1 : 1;
    }

    public enum Direction {up, down}
}
