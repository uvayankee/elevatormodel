package model.elevator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ElevatorTest {

    @Test
    public void testElevator() {
        Elevator elevator = new Elevator();
        assertTrue(elevator.isTrue());
    }

    @Test
    public void testElevatorDoorsStartOpened() {
        Elevator elevator = new Elevator();
        assertSame(Elevator.DoorsState.opened, elevator.getDoorsState());
    }

    @Test
    public void testElevatorDoorsAreClosed() {
        Elevator elevator = new Elevator();
        assertSame(Elevator.DoorsState.closed, elevator.closeDoors());
    }

    @Test
    public void testElevatorInitialFloor() {
        Elevator elevator = new Elevator();
        assertSame(1, elevator.getFloor());
    }

    @Test
    public void testElevatorButtons() {
        Elevator elevator = new Elevator();
        assertSame(2, elevator.getFloorButtons().length);
    }

    @Test
    public void testElevatorButtonsMoreFloors() {
        Elevator elevator = new Elevator(3);
        assertSame(3, elevator.getFloorButtons().length);
    }

}
