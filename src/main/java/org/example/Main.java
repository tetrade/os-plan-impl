package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Planner planner = new Planner();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(planner);

        while (true) {
            planner.addTask(TaskGenerator.createRandomTask());
            Thread.sleep(1100L);
        }
    }
}
