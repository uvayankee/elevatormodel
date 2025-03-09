package model.elevator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.junit.jupiter.api.Assertions.*;

public class ElevatorTest {

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
        assertSame(2, elevator.getFloorButtons().length-1);
    }

    @ParameterizedTest
    @ValueSource(ints = {2,3,4,5,6,7})
    public void testElevatorButtonsMoreFloors(int floors) {
        Elevator elevator = new Elevator(floors);
        assertSame(floors, elevator.getFloorButtons().length-1);
    }

    @Test
    public void testFloorMovement() throws ExecutionException, InterruptedException {
        Elevator elevator = new Elevator();
        assertSame(1, elevator.getFloor());
        FutureTask<List<Action>> actionLog = elevator.startElevator();
        elevator.goToFloor(2);
        elevator.stopElevator();
        assertEquals(3, actionLog.get().size());
        assertSame(2, elevator.getFloor());
        assertSame(Elevator.DoorsState.opened, elevator.getDoorsState());
    }

    @Test
    public void testFloorMovementThreeFloors() throws ExecutionException, InterruptedException {
        Elevator elevator = new Elevator(3);

        FutureTask<List<Action>> actionLog = elevator.startElevator();
        elevator.goToFloor(3);
        elevator.stopElevator();
        assertEquals(4, actionLog.get().size());
        assertSame(3, elevator.getFloor());
        assertSame(Elevator.DoorsState.opened, elevator.getDoorsState());

        actionLog = elevator.startElevator();
        elevator.goToFloor(1);
        elevator.stopElevator();
        assertEquals(8, actionLog.get().size());
        assertSame(1, elevator.getFloor());
        assertSame(Elevator.DoorsState.opened, elevator.getDoorsState());
    }

    @Test
    public void TestTwoFloorCalls() throws ExecutionException, InterruptedException {
        Elevator elevator = new Elevator(5);
        FutureTask<List<Action>> actionLog = elevator.startElevator();
        elevator.goToFloor(3);
        elevator.goToFloor(5);
        elevator.stopElevator();
        assertEquals(8, actionLog.get().size());
    }

    @Test
    public void TestTwoFloorCallsInverse() throws ExecutionException, InterruptedException {
        Elevator elevator = new Elevator(5);
        FutureTask<List<Action>> actionLog = elevator.startElevator();
        elevator.goToFloor(5);
        elevator.goToFloor(3);
        elevator.stopElevator();
        assertEquals(8, actionLog.get().size());
    }

    @Test
    public void TestCallElevator() throws ExecutionException, InterruptedException {
        Elevator elevator = new Elevator(5);
        FutureTask<List<Action>> actionLog = elevator.startElevator();
        elevator.callElevator(4, ElevatorCall.Direction.down);
        elevator.stopElevator();
        assertEquals(5, actionLog.get().size());
    }

    @Test
    public void TestRunAndCallSequence() throws ExecutionException, InterruptedException {
        int clockSpeed = 200;
        Elevator elevator = new Elevator(5, clockSpeed);
        FutureTask<List<Action>> actionLog = elevator.startElevator();
        elevator.goToFloor(3);
        Thread.sleep(clockSpeed * 3);

        elevator.callElevator(4, ElevatorCall.Direction.down);
        while(elevator.getFloor() != 4) {
            Thread.sleep(clockSpeed / 2);
        }

        elevator.goToFloor(2);
        elevator.callElevator(3, ElevatorCall.Direction.down);
        while(elevator.getFloor() != 3) {
            Thread.sleep(clockSpeed / 2);
        }

        elevator.goToFloor(1);
        elevator.stopElevator();
        assertEquals(16, actionLog.get().size());
    }

}
