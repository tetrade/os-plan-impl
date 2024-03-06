package org.example;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;

public class Task implements Callable<Task>, Comparable<Task> {

    private long startWaitingTime = System.currentTimeMillis();
    private long lastRunStartTime;
    private long needRunTime;
    private TaskState currentState = TaskState.SUSPENDED;
    private final TaskPriority priority;
    private final String uuid = UUID.randomUUID().toString();

    public static Task of(TaskPriority priority, long needRunTime) {
        return new Task(priority, needRunTime);
    }

    private Task(TaskPriority priority, long needRunTime) {
        this.needRunTime = needRunTime;
        this.priority = priority;
    }


    @Override
    public Task call() {
        Logger.log(Level.INFO, this + " START EXECUTE");
        lastRunStartTime = System.currentTimeMillis();
        try {
            Thread.sleep(Math.abs(needRunTime));
        } catch (InterruptedException e) {
            updateNeededRunTime();
            Logger.log(Level.INFO, this + " INTERRUPTED. STILL NEED " + needRunTime + "RUNTIME");
            return this;
        }
        Logger.log(Level.INFO, this + " END EXECUTE");
        return this;
    }

    @Override
    public int compareTo(Task o) {
        int comparePriority = this.priority.ordinal() - o.priority.ordinal();
        if (comparePriority == 0) return Long.signum(o.startWaitingTime - this.startWaitingTime);
        return comparePriority;
    }

    public void setCurrentState(TaskState currentState) {
        this.currentState = currentState;
    }

    public TaskPriority getPriority() {
        return this.priority;
    }

    public long getNeedRunTime() {
        return this.needRunTime;
    }

    public TaskState getCurrentState() {
        return currentState;
    }

    public void updateNeededRunTime() {
        startWaitingTime = System.currentTimeMillis();
        this.needRunTime -= (startWaitingTime - lastRunStartTime);
    }


    @Override
    public String toString() {
        return "Task{" +
                "priority=" + priority +
                ", uuid='" + uuid + '\'' +
                '}';
    }

    public String getUuid() {
        return uuid;
    }
}
