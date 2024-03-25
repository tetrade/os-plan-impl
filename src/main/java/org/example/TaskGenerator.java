package org.example;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TaskGenerator {

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private TaskGenerator() {}

    public static Task createRandomTask() {
        if (random.nextInt(4) <= 1) {
            return Task.of(randomEnum(TaskPriority.class), random.nextLong(500L, 5000L));
        }
        return ExtendedTask.of(randomEnum(TaskPriority.class), random.nextLong(500L, 5000L));
    }

    public static List<Task> createTasks(int count) {
        return IntStream.range(0, count).mapToObj(i -> createRandomTask()).collect(Collectors.toList());
    }

    private static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
}
