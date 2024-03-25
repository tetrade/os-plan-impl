package org.example;

import java.util.List;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CombinedTaskQueueTaskTest {

    @Test
    void shouldAddTaskAndChangeCurrentState_whenAddTask() {
        CombinedTaskQueue underTest = new CombinedTaskQueue();
        IntStream.range(0, 10).forEach(index -> {

            Task taskToAdd = Task.of(TaskPriority.LOW, 1000L);

            Assertions.assertThat(underTest.add(taskToAdd)).isTrue();
            Assertions.assertThat(taskToAdd.getCurrentState()).isEqualTo(TaskState.READY);
        });
    }

    @Test
    void shouldNotAddTask_whenDontHaveSpace() {
        CombinedTaskQueue underTest = new CombinedTaskQueue();
        IntStream.range(0, 10).forEach(t -> underTest.add(Task.of(TaskPriority.LOW, 1000L)));

        Task shouldNotBeAdded = Task.of(TaskPriority.LOW, 1000L);

        Assertions.assertThat(underTest.add(shouldNotBeAdded)).isFalse();
        Assertions.assertThat(shouldNotBeAdded.getCurrentState()).isEqualTo(TaskState.SUSPENDED);
    }

    @Test
    void shouldTakeTaskAndChangeState_whenHaveTask() {
        CombinedTaskQueue underTest = new CombinedTaskQueue();

        Task shouldBeTaken = Task.of(TaskPriority.LOW, 1000L);
        underTest.add(shouldBeTaken);

        Assertions.assertThat(underTest.take()).isEqualTo(shouldBeTaken);
    }

    @Test
    void shouldTakeInRightOrder() {
        CombinedTaskQueue underTest = new CombinedTaskQueue();

        Task shouldBeTaken4 = Task.of(TaskPriority.LOW, 1000L);
        underTest.add(shouldBeTaken4);

        Task shouldBeTaken5 = Task.of(TaskPriority.LOWEST, 1000L);
        underTest.add(shouldBeTaken5);

        Task shouldBeTaken1 = Task.of(TaskPriority.HIGH, 1000L);
        underTest.add(shouldBeTaken1);

        Task shouldBeTaken3 = Task.of(TaskPriority.MIDDLE, 1000L);
        underTest.add(shouldBeTaken3);

        Task shouldBeTaken2 = Task.of(TaskPriority.HIGH, 1000L);
        underTest.add(shouldBeTaken2);

        List<Task> shouldBe =
                List.of(shouldBeTaken1, shouldBeTaken2, shouldBeTaken3, shouldBeTaken4, shouldBeTaken5);

        IntStream.range(0, 5).forEach(i -> Assertions.assertThat(underTest.take()).isEqualTo(shouldBe.get(i)));
        Assertions.assertThat(underTest).isEmpty();
    }

}
