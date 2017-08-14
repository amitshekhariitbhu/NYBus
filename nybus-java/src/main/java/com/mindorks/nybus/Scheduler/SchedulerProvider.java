package com.mindorks.nybus.Scheduler;

import io.reactivex.Scheduler;

/**
 * Created by Jyoti on 14/08/17.
 */

public interface SchedulerProvider {
    Scheduler provideIOScheduler();
    Scheduler provideMainScheduler();
    Scheduler provideComputationScheduler();
    Scheduler provideNewThreadScheduler();
}
