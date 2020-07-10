package com.flower.game.util;

import reactor.core.Disposable;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;

public class ScheduleUtil {

    /**
     * 添加延时任务
     * @param task
     * @param seconds
     */
    public static void addDelayTask(Runnable task, int seconds) {
        Schedulers.parallel().schedule(task, seconds, TimeUnit.SECONDS);
    }

    /**
     * 添加周期任务
     * @param task
     * @param period
     * @return
     */
    public static Disposable addIntervalTask(Runnable task, int period) {
        return Schedulers.parallel().schedulePeriodically(task, period, period, TimeUnit.MILLISECONDS);
    }
}
