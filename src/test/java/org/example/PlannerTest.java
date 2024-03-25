package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Интеграционный тест
 */
class PlannerTest {

    @Test
    void shouldCompleteAllTaskInRightOrder() throws InterruptedException {

        Task t1 = Task.of(TaskPriority.LOW, 150);
        Task t2 = ExtendedTask.of(TaskPriority.LOWEST, 250, 100, 200);
        Task t3 = Task.of(TaskPriority.HIGH, 300);
        Task t4 = Task.of(TaskPriority.MIDDLE, 400);
        Task t5 = ExtendedTask.of(TaskPriority.HIGH, 400, 150, 300);
        Task t6 = Task.of(TaskPriority.LOWEST, 200);

        List<Task> tasksToComplete = List.of(t1, t2, t3, t4, t5, t6);

        Planner planner = new Planner();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(planner);

        for (Task t : tasksToComplete) {
            planner.addTask(t);
            Thread.sleep(100L);
        }
        Thread.sleep(2000L);

        Assertions.assertThat(planner.getCompletedTasks()).containsExactly(t1, t3, t5, t4, t6, t2);
    }
}
