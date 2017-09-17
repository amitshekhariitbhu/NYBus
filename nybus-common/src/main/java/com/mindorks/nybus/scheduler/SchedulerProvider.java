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

package com.mindorks.nybus.scheduler;


import io.reactivex.Scheduler;

/**
 * Created by Jyoti on 14/08/17.
 */

/**
 * Interface for providing Schedulers.
 */
public interface SchedulerProvider {

    /**
     * Provides the main thread Scheduler.
     *
     * @return the main thread Scheduler.
     */
    Scheduler provideMainThreadScheduler();

    /**
     * Provides the IO thread Scheduler.
     *
     * @return the IO thread Scheduler.
     */
    Scheduler provideIOScheduler();

    /**
     * Provide the computational Scheduler.
     *
     * @return the computational thread Scheduler.
     */
    Scheduler provideComputationScheduler();

    /**
     * Provide the trampoline thread Scheduler.
     *
     * @return the trampoline thread Scheduler.
     */
    Scheduler provideTrampolineScheduler();

    /**
     * Provide the executor thread Scheduler.
     *
     * @return the executor thread Scheduler.
     */
    Scheduler provideExecutorScheduler();

    /**
     * Provide the new thread Scheduler.
     *
     * @return the new thread Scheduler.
     */
    Scheduler provideNewThreadScheduler();

}
