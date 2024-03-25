package org.example;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskGeneratorTest {

    @Test
    void shouldCreate10Tasks() {
        Assertions.assertThat(TaskGenerator.createTasks(10)).size().isEqualTo(10);
    }

    @Test
    void shouldCreateDifferentTasks() {
        Assertions.assertThat(TaskGenerator.createRandomTask()).isNotEqualTo(TaskGenerator.createRandomTask())
                .hasNoNullFieldsOrProperties();
    }
}
