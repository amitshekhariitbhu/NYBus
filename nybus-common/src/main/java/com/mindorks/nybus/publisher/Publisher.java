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

package com.mindorks.nybus.publisher;

import com.mindorks.nybus.consumer.ConsumerProvider;
import com.mindorks.nybus.event.NYEvent;
import com.mindorks.nybus.scheduler.SchedulerProvider;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by amitshekhar on 28/08/17.
 */

/**
 * Interface for providing publishers associated with the thread.
 */
public interface Publisher {

    /**
     * Init the publishers.
     *
     * @param schedulerProvider the {@link SchedulerProvider}
     * @param consumerProvider  the {@link ConsumerProvider}
     */
    void initPublishers(SchedulerProvider schedulerProvider,
                        ConsumerProvider consumerProvider);

    /**
     * The publisher for the posting thread.
     *
     * @return the publisher for the posting thread.
     */
    PublishSubject<NYEvent> forPostingThread();

    /**
     * The publisher for the main thread.
     *
     * @return the publisher for the main thread.
     */
    PublishSubject<NYEvent> forMainThread();

    /**
     * The publisher for the IO thread.
     *
     * @return the publisher for the IO thread.
     */
    PublishSubject<NYEvent> forIOThread();

    /**
     * The publisher for the computation thread.
     *
     * @return the publisher for the computation thread.
     */
    PublishSubject<NYEvent> forComputationThread();

    /**
     * The publisher for the trampoline thread.
     *
     * @return the publisher for the trampoline thread.
     */
    PublishSubject<NYEvent> forTrampolineThread();

    /**
     * The publisher for the executor thread.
     *
     * @return the publisher for the executor thread.
     */
    PublishSubject<NYEvent> forExecutorThread();

    /**
     * The publisher for the new thread.
     *
     * @return the publisher for the new thread.
     */
    PublishSubject<NYEvent> forNewThread();

}
