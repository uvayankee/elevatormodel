package model.elevator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

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
    public void testElevatorDoorsReOpen() {
        Elevator elevator = new Elevator();
        assertSame(Elevator.DoorsState.closed, elevator.closeDoors());
        assertSame(Elevator.DoorsState.opened, elevator.openDoors());
    }

    @Test
    public void testElevatorInitialFloor() {
        Elevator elevator = new Elevator();
        assertSame(1, elevator.getFloor());
    }

    @Test
    public void testElevatorButtons() {
        Elevator elevator = new Elevator();
        assertSame(2, elevator.getFloorButtons().length - 1);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 7})
    public void testElevatorButtonsMoreFloors(int floors) {
        Elevator elevator = new Elevator(floors);
        assertSame(floors, elevator.getFloorButtons().length - 1);
    }

    @Test
    public void testFloorMovement() throws ExecutionException, InterruptedException {
        List<Action> expectedActions = Arrays.asList(Action.close, Action.up, Action.open);
        Elevator elevator = new Elevator();
        assertSame(1, elevator.getFloor());
        FutureTask<List<Action>> actionLog = elevator.startElevator();
        elevator.goToFloor(2);
        elevator.stopElevator();
        assertEquals(expectedActions, actionLog.get());
        assertSame(2, elevator.getFloor());
        assertSame(Elevator.DoorsState.opened, elevator.getDoorsState());
    }

    @Test
    public void testFloorMovementThreeFloors() throws ExecutionException, InterruptedException {
        Elevator elevator = new Elevator(3);

        List<Action> expectedActions = Arrays.asList(Action.close, Action.up, Action.up, Action.open,
                Action.close, Action.down, Action.down, Action.open);

        FutureTask<List<Action>> actionLog = elevator.startElevator();
        elevator.goToFloor(3);
        elevator.stopElevator();
        assertEquals(expectedActions.subList(0, 4), actionLog.get());
        assertSame(3, elevator.getFloor());
        assertSame(Elevator.DoorsState.opened, elevator.getDoorsState());

        actionLog = elevator.startElevator();
        elevator.goToFloor(1);
        elevator.stopElevator();
        assertEquals(expectedActions, actionLog.get());
        assertSame(1, elevator.getFloor());
        assertSame(Elevator.DoorsState.opened, elevator.getDoorsState());
    }

    @Test
    public void TestTwoFloorCalls() throws ExecutionException, InterruptedException {
        List<Action> expectedActions = Arrays.asList(Action.close, Action.up, Action.up, Action.open,
                Action.close, Action.up, Action.up, Action.open);

        Elevator elevator = new Elevator(5);
        FutureTask<List<Action>> actionLog = elevator.startElevator();
        elevator.goToFloor(3);
        elevator.goToFloor(5);
        elevator.stopElevator();
        assertEquals(expectedActions, actionLog.get());
    }

    @Test
    public void TestTwoFloorCallsInverse() throws ExecutionException, InterruptedException {
        List<Action> expectedActions = Arrays.asList(Action.close, Action.up, Action.up, Action.open,
                Action.close, Action.up, Action.up, Action.open);

        Elevator elevator = new Elevator(5);
        FutureTask<List<Action>> actionLog = elevator.startElevator();
        elevator.goToFloor(5);
        elevator.goToFloor(3);
        elevator.stopElevator();
        assertEquals(expectedActions, actionLog.get());
    }

    @Test
    public void TestCallElevator() throws ExecutionException, InterruptedException {
        List<Action> expectedActions = Arrays.asList(Action.close, Action.up, Action.up, Action.up, Action.open);

        Elevator elevator = new Elevator(5);
        FutureTask<List<Action>> actionLog = elevator.startElevator();
        elevator.callElevator(4, Action.down);
        elevator.stopElevator();
        assertEquals(expectedActions, actionLog.get());
    }

    @Test
    public void TestRunAndCallSequence() throws ExecutionException, InterruptedException {
        List<Action> expectedActions = Arrays.asList(Action.close, Action.up, Action.up, Action.open,
                Action.close, Action.up, Action.open, Action.close, Action.down, Action.open, Action.close, Action.down,
                Action.open, Action.close, Action.down, Action.open);

        int clockSpeed = 200;
        Elevator elevator = new Elevator(5, clockSpeed);
        FutureTask<List<Action>> actionLog = elevator.startElevator();
        elevator.goToFloor(3);
        Thread.sleep(clockSpeed * 3);

        elevator.callElevator(4, Action.down);
        while (elevator.getFloor() != 4) {
            Thread.sleep(clockSpeed / 2);
        }

        elevator.goToFloor(2);
        elevator.callElevator(3, Action.down);
        while (elevator.getFloor() != 3) {
            Thread.sleep(clockSpeed / 2);
        }

        elevator.goToFloor(1);
        elevator.stopElevator();
        assertEquals(expectedActions, actionLog.get());
    }

}
