package com.mindorks.nybus.AndroidScheduler;

import com.mindorks.nybus.Scheduler.SchedulerProvider;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jyoti on 14/08/17.
 */

public class SchedulerProviderImplementation implements SchedulerProvider {
    @Override
    public Scheduler provideIOScheduler() {
        return Schedulers.io();
    }

    @Override
    public Scheduler provideMainScheduler() {
        return AndroidSchedulers.mainThread();
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
