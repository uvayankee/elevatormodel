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
        elevator.closeDoors();
        assertSame(Elevator.DoorsState.closed, elevator.getDoorsState());
    }
}
