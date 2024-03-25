package org.example;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void shouldHaveSuspendedStateWhenEndRunTaskSuccessfully() {
        Task underTest = Task.of(TaskPriority.LOW, 1000L);
        underTest.setCurrentState(TaskState.READY);

        underTest.call();

        Assertions.assertThat(underTest.getCurrentState()).isEqualTo(TaskState.SUSPENDED);
    }

    @Test
    void shouldHaveWaitStateWhenEndRunExtendedTaskSuccessfully() {
        Task underTest = ExtendedTask.of(TaskPriority.LOW, 1000L);
        underTest.setCurrentState(TaskState.READY);

        underTest.call();

        Assertions.assertThat(underTest.getCurrentState()).isEqualTo(TaskState.WAIT);
    }

    @Test
    void shouldHaveSuspendedStateWhenEndExtendedRunTaskSuccessfully() {
        Task underTest = ExtendedTask.of(TaskPriority.LOW, 1000L);

        underTest.setCurrentState(TaskState.READY);
        underTest.call();

        underTest.waitSomething();

        underTest.setCurrentState(TaskState.READY);
        underTest.call();

        Assertions.assertThat(underTest.getCurrentState()).isEqualTo(TaskState.SUSPENDED);
    }

    @Test
    void shouldThrowErrorWhenIllegalTransition() {
        Task underTest = ExtendedTask.of(TaskPriority.LOW, 1000L);

        Assertions.assertThatThrownBy(() -> underTest.setCurrentState(TaskState.RUNNING))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
