package org.example;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.commons.lang3.time.StopWatch;

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
        setLastRunStartTime(System.currentTimeMillis());
        try {
           Thread.sleep(getRuntime());
        } catch (InterruptedException e) {
            updateRunningTime();
            Logger.log(Level.INFO, this + " INTERRUPTED. STILL NEED " + getNeedRuntimeBeforeWait() + getNeedRunTime() + " RUNTIME");
            return this;
        }

        if (needToWait) {
            setNeedRuntimeBeforeWait(0);
            Logger.log(Level.INFO, this + " WANT WAIT");
            setCurrentState(TaskState.WAIT);
        } else {
            Logger.log(Level.INFO, this + " END EXECUTE");
        }
        return this;
    }

    @Override
    protected void updateRunningTime() {
        setStartWaitingTime(System.currentTimeMillis());
        if (needToWait) {
            updateNeededRunTime();
        } else {
            updateNeedBeforeWaitRuntime();
        }
    }

    @Override
    protected long getRuntime() {
       return Math.abs(needToWait ? getNeedRuntimeBeforeWait() : getNeedRunTime());
    }

    private void updateNeedBeforeWaitRuntime() {
        setNeedRuntimeBeforeWait(getNeedRuntimeBeforeWait() - (getStartWaitingTime() - getLastRunStartTime()));
    }

    @Override
    public void updateNeededRunTime() {
       setNeedRunTime(getNeedRunTime() - (getStartWaitingTime() - getLastRunStartTime()));
    }

    public long getNeedRuntimeBeforeWait() {
        return needRuntimeBeforeWait;
    }

    public void setNeedRuntimeBeforeWait(long needRuntimeBeforeWait) {
        this.needRuntimeBeforeWait = needRuntimeBeforeWait;
    }
}
