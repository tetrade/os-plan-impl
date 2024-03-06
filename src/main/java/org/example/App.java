package org.example;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {

        Task t1 = Task.of(TaskPriority.LOW, 100);
        System.out.println(t1.getUuid());

        List<Task> tasks = List.of(t1,
                Task.of(TaskPriority.LOW, 4000), Task.of(TaskPriority.HIGH, 1000),
                Task.of(TaskPriority.MIDDLE, 500), Task.of(TaskPriority.HIGH, 2000)
        );

//        CombinedTaskQueue queue = new CombinedTaskQueue();
//        tasks.forEach(x -> queue.add(x));
//
//        while(queue.size() > 0) {
//            System.out.println(queue.poll());
//        }

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
