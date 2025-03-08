package model.elevator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ElevatorTest {

    @Test
    public void testElevator() {
        Elevator elevator = new Elevator();
        assertTrue(elevator.isTrue());
    }
}
