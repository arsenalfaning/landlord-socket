package com.flower.game.util;

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
}
