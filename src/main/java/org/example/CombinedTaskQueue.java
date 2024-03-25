package org.example;

import java.util.concurrent.PriorityBlockingQueue;

public class CombinedTaskQueue extends PriorityBlockingQueue<Task> {

    private static final int MAX_READY_TASK = 10;

    public CombinedTaskQueue() {
        super(MAX_READY_TASK);
    }

    /**
     * Обратим внимание, что вместимость очереди игнорируется, когда задача добавлять
     * после собственного перехода из состояния ожидания. Данное условие было уточнено у лектора, поскольку
     * в процессорах с таким планировщиком такой ситуации произойти не может, что места на задачу после ожидания не хватает.
     * ------------------------------------------------------------------
     * В целом ничего не мешает резервировать место в очереди на задачу которая сейчас в ожидании,
     * но мне кажется для моделирования процесса это правильне.
     */
    @Override
    public boolean add(Task task) {
        synchronized (this) {
            task.setStartWaitingTime(System.currentTimeMillis());
            boolean added = (task.getCurrentState().equals(TaskState.WAIT) || remainingCapacity() > 0) && super.add(task);
            if (added) {
                task.setCurrentState(TaskState.READY);
            }
            return added;
        }
    }

    @Override
    public Task take() {
        Task t;
        try {
             t = super.take();
        } catch (InterruptedException e) {
            return null;
        }
        return t;
    }

    @Override
    public int remainingCapacity() {
        return MAX_READY_TASK - this.size();
    }
}
