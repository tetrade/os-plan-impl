package org.example;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class CombinedTaskQueue extends PriorityBlockingQueue<Task> {

    private static final int MAX_READY_TASK = 10;

    public CombinedTaskQueue() {
        super(MAX_READY_TASK, Comparator.reverseOrder());
    }

    @Override
    public boolean add(Task task) {
        synchronized (this) {
            boolean added = remainingCapacity() > 0 && super.add(task);
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
            return  null;
        }
        t.setCurrentState(TaskState.RUNNING);
        return t;
    }

    @Override
    public int remainingCapacity() {
        return MAX_READY_TASK - this.size();
    }
}
