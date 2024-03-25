package org.example;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class ExtendedTask extends Task {

    private static final long MAX_RANDOM_LONG_WAIT_TIME = 3000;

    private long needRuntimeBeforeWait;
    private boolean needToWait = true;
    private long waitTime;

    protected ExtendedTask(TaskPriority priority, long needRunTime, long waitAt, long waitTime) {
        super(priority, needRunTime);
        this.needRuntimeBeforeWait = waitAt;
        this.waitTime = waitTime;
    }

    public static ExtendedTask of(TaskPriority priority, long needRunTime) {
        long waitAt = ThreadLocalRandom.current().nextLong(needRunTime);
        long waitTime = ThreadLocalRandom.current().nextLong(MAX_RANDOM_LONG_WAIT_TIME);

        return new ExtendedTask(priority, needRunTime - waitTime, waitAt, waitTime);
    }

    @Override
    public Task waitSomething() {
        Logger.log(Level.INFO, this + " BEGIN TO WAIT ... ");
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            throw new IllegalCallerException("Cant interrupt " + this + " while waiting! ");
        }
        Logger.log(Level.INFO, this + " READY TO RUNNING AFTER WAITING");
        needToWait = false;
        return this;
    }

    @Override
    public Task call() {
        Logger.log(Level.INFO, this + " START EXECUTE");
        try {
            startOrResumeWatcher();
            Thread.sleep(getRuntime() - getWatcher().getTime());
        } catch (InterruptedException e) {
            getWatcher().suspend();
            long leftTime = (needToWait ? getNeedRunTime() + getNeedRuntimeBeforeWait() : getNeedRunTime()) - getWatcher().getTime();
                    Logger.log(Level.INFO, this + " INTERRUPTED. STILL NEED " + leftTime + " RUNTIME");
            return this;
        }

        if (needToWait) {
            getWatcher().reset();
            Logger.log(Level.INFO, this + " WANT WAIT");
            setCurrentState(TaskState.WAIT);
        } else {
            getWatcher().stop();
            Logger.log(Level.INFO, this + " END EXECUTE");
        }
        return this;
    }

    @Override
    public String toString() {
        return "Task{" +
                "priority=" + getPriority() +
                ", needRunTime=" + (getNeedRunTime() + getNeedRuntimeBeforeWait()) +
                ", uuid='" + getUuid() + '\'' +
                '}';
    }

    @Override
    protected long getRuntime() {
       return Math.abs(needToWait ? getNeedRuntimeBeforeWait() : getNeedRunTime());
    }

    public long getNeedRuntimeBeforeWait() {
        return needRuntimeBeforeWait;
    }

    public void setNeedRuntimeBeforeWait(long needRuntimeBeforeWait) {
        this.needRuntimeBeforeWait = needRuntimeBeforeWait;
    }
}
