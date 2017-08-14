package com.mindorks.nybus.Scheduler;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jyoti on 14/08/17.
 */

public class SchedulerProviderImpl implements SchedulerProvider{
    @Override
    public Scheduler provideIOScheduler() {
        return Schedulers.io();
    }

    @Override
    public Scheduler provideMainScheduler() {
        return null;
    }

    @Override
    public Scheduler provideComputationScheduler() {
        return Schedulers.computation();
    }

    @Override
    public Scheduler provideNewThreadScheduler() {
        return Schedulers.newThread();
    }
}
