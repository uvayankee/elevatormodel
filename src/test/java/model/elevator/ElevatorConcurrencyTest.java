package model.elevator;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static org.junit.jupiter.api.Assertions.*;

public class ElevatorConcurrencyTest {

    @Test
    void testConcurrentGoToFloorCalls() throws InterruptedException, ExecutionException {
        Elevator elevator = new Elevator(10, 50); // 10 floors, 50ms clock speed
        FutureTask<List<Action>> elevatorTask = elevator.startElevator();

        int numThreads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        Callable<Void> task = () -> {
            elevator.goToFloor(5);
            elevator.goToFloor(1);
            return null;
        };

        Future<?>[] futures = new Future[numThreads];
        for (int i = 0; i < numThreads; i++) {
            futures[i] = executorService.submit(task);
        }

        // Wait for all calls to complete
        for (Future<?> future : futures) {
            future.get(); // Wait for task completion, check for exceptions
        }

        // Allow elevator to process requests
        Thread.sleep(2000); // Give elevator time to process its queue

        elevator.stopElevator();
        List<Action> actionLog = elevatorTask.get(); // Get action log after stopping

        // Basic assertion: check if the elevator ran and stopped without throwing exceptions.
        // More specific assertions would depend on the desired outcome of concurrent calls,
        // which can be complex to define without a very strict specification.
        // For now, ensure it doesn't deadlock or crash.
        assertNotNull(actionLog);
        // Could check if the elevator eventually reached floor 1 or 5.
        // This requires analyzing the actionLog or final elevator.getFloor() if accessible post-stop.
    }

    @Test
    void testConcurrentCallElevatorAndGoToFloor() throws InterruptedException, ExecutionException {
        Elevator elevator = new Elevator(10, 50);
        FutureTask<List<Action>> elevatorTask = elevator.startElevator();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Thread 1: Calls elevator
        Future<?> callFuture = executorService.submit(() -> {
            elevator.callElevator(8, Action.up);
            elevator.callElevator(2, Action.down);
            return null;
        });

        // Thread 2: Goes to floors
        Future<?> goToFuture = executorService.submit(() -> {
            elevator.goToFloor(3);
            elevator.goToFloor(7);
            return null;
        });

        callFuture.get();
        goToFuture.get();

        Thread.sleep(3000); // Give elevator time

        elevator.stopElevator();
        List<Action> actionLog = elevatorTask.get();
        assertNotNull(actionLog);
        // Further assertions could check if calls and floor requests were processed.
    }

    @Test
    void testStartStopConcurrency() throws InterruptedException, ExecutionException {
        Elevator elevator = new Elevator(5, 50);

        // Start and stop multiple times
        for (int i = 0; i < 3; i++) {
            FutureTask<List<Action>> task = elevator.startElevator();
            assertTrue(elevator.getElevatorThread().isAlive(), "Elevator thread should be alive after start");

            // Simulate some work or calls
            elevator.goToFloor(i + 2);
            Thread.sleep(100); // let it process a bit

            elevator.stopElevator();
            // Wait for the thread to actually terminate
            // Ensure getElevatorThread() is available and returns the current thread instance
            if (elevator.getElevatorThread() != null) {
                 elevator.getElevatorThread().join(2000); // Wait max 2 sec
                 assertFalse(elevator.getElevatorThread().isAlive(), "Elevator thread (" + i + ") should be stopped");
            } else {
                // If thread is null, it means it might have already terminated or wasn't started properly.
                // This path might indicate an issue if stopElevator() nullifies it too early or startElevator failed.
                // For this test, we assume stopElevator doesn't nullify, but waits for termination.
                // If elevator.getElevatorThread() can be null post-stop, the assertion needs adjustment.
                // Considering the current Elevator.java, elevatorThread is not set to null by stopElevator.
            }
            List<Action> actionLog = null;
            try {
                actionLog = task.get(500, TimeUnit.MILLISECONDS); // Wait for future completion with a timeout
            } catch (TimeoutException e) {
                System.err.println("Timeout waiting for actionLogFuture in testStartStopConcurrency iteration " + i);
                // Optionally, dump stack trace or rethrow if test should fail hard on timeout here
            }
            // The original assertNotNull(actionLog) will catch if it's still null due to timeout.
            assertNotNull(actionLog);
        }
    }
    
    // Helper method to get the elevator thread if needed for assertions (requires adding a getter in Elevator.java)
    // For testStartStopConcurrency, Elevator needs a getElevatorThread() method.
    // public Thread getElevatorThread() { return elevatorThread; }
}
