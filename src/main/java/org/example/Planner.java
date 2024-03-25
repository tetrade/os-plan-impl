package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class Planner implements Runnable {
    private boolean isRunning = false;
    private Task currentTask;

    private Future<Task> future;

    private final ExecutorService mainTaskExecutor = Executors.newSingleThreadExecutor();

    /**
     * Реализует все очереди вместе, поскольку сам сортирует элементы по приоритету при добавлении
     * и является потокобезопасным
     */
    private final CombinedTaskQueue queue = new CombinedTaskQueue();
    private final List<Task> completedTasks = new ArrayList<>();


    @Override
    public void run() {
        if (!isRunning) {
            // запуск процесса на просматривание готовых задач
            isRunning = true;
            while (isRunning) {

                currentTask = queue.take();
                Logger.log(Level.ALL, currentTask + " WANT TO EXECUTE");
                future = mainTaskExecutor.submit(currentTask);
                try {
                    Task endedTask = future.get();

                    if (endedTask.getCurrentState() == TaskState.WAIT) {
                        CompletableFuture.supplyAsync(currentTask::waitSomething).thenAccept(this::addTask);
                    } else {
                        completedTasks.add(endedTask);
                    }

                } catch (InterruptedException | ExecutionException E) {
                } catch (CancellationException c) {
                        queue.add(currentTask); // случай прерывания текущей задачи до завершения
                    }
                }
            }
        }

    public boolean addTask(Task task) {
        synchronized (this) {
            boolean wereAdded = queue.add(task);
            if (wereAdded) {
                Logger.log(Level.INFO, "PLANNER ADD " + task + " REMAINING CAPACITY - " + queue.remainingCapacity());
                /**
                 * Проверяем что задача добавленная в очередь на исполнение является более приоритетной
                 * и то, что в очереди хватит места на задачу, которая вернется в очередь на исполнение после
                 * прерывания
                 */
                if (currentTask != null && task.comparePriority(currentTask) < 0 && queue.remainingCapacity() >= 1) {
                    Logger.log(Level.INFO, "WANT INTERRUPT " + currentTask + " TO EXECUTE " + task);
                    future.cancel(true);
                }
            } else {
                Logger.log(Level.INFO, "PLANNER CANT ADD " + task + " NOT ENOUGH SPACE");
            }
            return wereAdded;
        }
    }

    public void stop() {
        this.isRunning = false;
    }

    public List<Task> getCompletedTasks() {
        return completedTasks;
    }
}
