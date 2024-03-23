package org.example;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {

        Task t1 = Task.of(TaskPriority.LOW, 100);

        List<Task> tasks = List.of(t1,
                ExtendedTask.of(TaskPriority.MIDDLE, 500), Task.of(TaskPriority.HIGH, 500),
                Task.of(TaskPriority.LOW, 4000), Task.of(TaskPriority.HIGH, 1000),
                Task.of(TaskPriority.MIDDLE, 500), ExtendedTask.of(TaskPriority.LOWEST, 1000),
                Task.of(TaskPriority.HIGH, 2000), ExtendedTask.of(TaskPriority.HIGH, 2000)
        );

        Planner planner = new Planner();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(planner);

        tasks.forEach(x -> {
            planner.addTask(x);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
    }
}
