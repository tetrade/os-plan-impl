package org.example;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.apache.commons.lang3.time.StopWatch;

public class Task implements Callable<Task>, Comparable<Task> {

    private long startWaitingTime = System.currentTimeMillis();
    private long lastRunStartTime;
    private TaskState currentState = TaskState.SUSPENDED;
    private long needRunTime;

    private final TaskPriority priority;
    private final String uuid = UUID.randomUUID().toString();

    public static Task of(TaskPriority priority, long needRunTime) {
        return new Task(priority, needRunTime);
    }

    protected Task(TaskPriority priority, long needRunTime) {
        this.needRunTime = needRunTime;
        this.priority = priority;
    }


    @Override
    public Task call() {
        Logger.log(Level.INFO, this + " START EXECUTE");
        lastRunStartTime = System.currentTimeMillis();
        try {
            Thread.sleep(getRuntime());
        } catch (InterruptedException e) {
            updateRunningTime();
            Logger.log(Level.INFO, this + " INTERRUPTED. STILL NEED " + needRunTime + " RUNTIME");
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

    public void updateNeededRunTime() {
        this.needRunTime -= (startWaitingTime - lastRunStartTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "priority=" + priority +
                ", uuid='" + uuid + '\'' +
                '}';
    }

    protected void updateRunningTime() {
        setStartWaitingTime(System.currentTimeMillis());
        updateNeededRunTime();
    }

    protected long getRuntime() {
        return Math.abs(getNeedRunTime());
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

    public String getUuid() {
        return uuid;
    }

    public long getStartWaitingTime() {
        return startWaitingTime;
    }

    protected void setStartWaitingTime(long startWaitingTime) {
        this.startWaitingTime = startWaitingTime;
    }

    public long getLastRunStartTime() {
        return lastRunStartTime;
    }

    protected void setLastRunStartTime(long lastRunStartTime) {
        this.lastRunStartTime = lastRunStartTime;
    }

    protected void setNeedRunTime(long needRunTime) {
        this.needRunTime = needRunTime;
    }

    public Task waitSomething() {
        Logger.log(Level.INFO, "BRO ... I DONT NEED IT");
        return this;
    }
}
