/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.mindorks.nybus.androidScheduler;


import com.mindorks.nybus.scheduler.SchedulerProvider;

import java.util.concurrent.Executors;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jyoti on 14/08/17.
 */

/**
 * The implementation of {@link SchedulerProvider}.
 */
public class SchedulerProviderImplementation implements SchedulerProvider {

    /**
     * Provides the main thread Scheduler.
     *
     * @return provides the main thread Scheduler.
     */
    @Override
    public Scheduler provideMainThreadScheduler() {
        return AndroidSchedulers.mainThread();
    }

    /**
     * Provides the IO thread Scheduler.
     *
     * @return provides the IO thread Scheduler.
     */
    @Override
    public Scheduler provideIOScheduler() {
        return Schedulers.io();
    }

    /**
     * Provides the computation thread Scheduler.
     *
     * @return provides the computation thread Scheduler.
     */
    @Override
    public Scheduler provideComputationScheduler() {
        return Schedulers.computation();
    }

    /**
     * Provides the trampoline thread Scheduler.
     *
     * @return provides the trampoline thread Scheduler.
     */
    @Override
    public Scheduler provideTrampolineScheduler() {
        return Schedulers.trampoline();
    }

    /**
     * Provides the executor thread Scheduler.
     *
     * @return provides the executor thread Scheduler.
     */
    @Override
    public Scheduler provideExecutorScheduler() {
        return Schedulers.from(Executors.newCachedThreadPool());
    }

    /**
     * Provides the new thread Scheduler.
     *
     * @return provides the new thread Scheduler.
     */
    @Override
    public Scheduler provideNewThreadScheduler() {
        return Schedulers.newThread();
    }

}
