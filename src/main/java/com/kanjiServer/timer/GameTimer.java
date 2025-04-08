package com.kanjiServer.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameTimer {
    private int timeLeft;
    private boolean isRunning;
    private ScheduledExecutorService scheduler;
    private final TimerListener listener;

    public GameTimer(int duration, TimerListener listener) {
        this.timeLeft = duration;
        this.listener = listener;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        isRunning = true;
        if (scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        scheduler.scheduleWithFixedDelay(() -> {
            if (isRunning) {
                tick();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void tick() {
        if (timeLeft > 0) {
            timeLeft -= 1;
            listener.onTimeUpdate();
        } else {
            stop();
            listener.onTimeUp();
        }
    }

    public void addTime(int addTime) {
        System.out.println("GameTimer.addTime");
        timeLeft += addTime;
        listener.onTimeUpdate();
    }

    public void stop() {
        isRunning = false;
        scheduler.shutdown();
    }

    public int getTimeLeft() {
        return timeLeft;
    }
}


