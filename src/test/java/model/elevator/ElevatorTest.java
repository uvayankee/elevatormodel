package model.elevator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest
    @ValueSource(ints = {2,3,4,5,6,7})
    public void testElevatorButtonsMoreFloors(int floors) {
        Elevator elevator = new Elevator(floors);
        assertSame(floors, elevator.getFloorButtons().length);
    }

    @Test
    public void testFloorMovement() {
        Elevator elevator = new Elevator();
        assertSame(1, elevator.getFloor());
        elevator.goToFloor(2);
        assertSame(2, elevator.getFloor());
    }

    @Test
    public void testFloorMovementThreeFloors() {
        Elevator elevator = new Elevator(3);
        assertSame(1, elevator.getFloor());
        elevator.goToFloor(2);
        assertSame(2, elevator.getFloor());
        elevator.goToFloor(3);
        assertSame(3, elevator.getFloor());
        elevator.goToFloor(1);
        assertSame(1, elevator.getFloor());
    }

}
