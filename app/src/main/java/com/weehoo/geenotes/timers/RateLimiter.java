package com.weehoo.geenotes.timers;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Manages running a Runnable with rate limiting, based on a timer and a pending threshold.
 */
public class RateLimiter {
    /**
     * Max number of pending runs.
     * When exceeded, run will execute.
     */
    public int runsPendingMax = 20;
    public long intervalMilliseconds = 5000;

    private Runnable mRunnable;
    private Date mLastRunDate;
    private int mRunsPending;
    private boolean mTimerIsRunning;

    public RateLimiter(Runnable runnable) {
        mRunnable = runnable;
        mRunsPending = 0;

        // Set last save date so that it is initially elapsed.
        mLastRunDate = new Date(new Date().getTime() - intervalMilliseconds);
        mTimerIsRunning = false;
    }

    public void run() {
        Date now = new Date();

        if (now.getTime() - mLastRunDate.getTime() >= intervalMilliseconds || mRunsPending > runsPendingMax) {
            // Interval elapsed or pending runs threshold exceeded.
            // Ok to run.
            this.start();
        }
        else {
            // Need to run, but interval has not elapsed since last run.
            mRunsPending++;

            // Set a new timer to run
            if (!mTimerIsRunning) {
                mTimerIsRunning = true;

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        start();
                    }
                }, intervalMilliseconds);
            }
        }
    }

    private void start() {
        Thread thread = new Thread(mRunnable);
        thread.start();

        mRunsPending = 0;
        mLastRunDate = new Date();
        mTimerIsRunning = false;
    }
}
