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
 * The implementation of {@link Publisher}.
 */
public class NYPublisher implements Publisher {

    private PublishSubject<NYEvent> postingThreadPublisher;
    private PublishSubject<NYEvent> mainThreadPublisher;
    private PublishSubject<NYEvent> iOThreadPublisher;
    private PublishSubject<NYEvent> computationThreadPublisher;
    private PublishSubject<NYEvent> trampolineThreadPublisher;
    private PublishSubject<NYEvent> executorThreadPublisher;
    private PublishSubject<NYEvent> newThreadPublisher;

    /**
     * Initialize the publisher with scheduler provider and consumer provider.
     *
     * @param schedulerProvider the {@link SchedulerProvider}
     * @param consumerProvider  the {@link ConsumerProvider}
     */
    @Override
    public void initPublishers(SchedulerProvider schedulerProvider,
                               ConsumerProvider consumerProvider) {
        postingThreadPublisher = PublishSubject.create();
        mainThreadPublisher = PublishSubject.create();
        iOThreadPublisher = PublishSubject.create();
        computationThreadPublisher = PublishSubject.create();
        trampolineThreadPublisher = PublishSubject.create();
        executorThreadPublisher = PublishSubject.create();
        newThreadPublisher = PublishSubject.create();

        postingThreadPublisher.subscribe(consumerProvider.getPostingThreadConsumer());

        if (schedulerProvider.provideMainThreadScheduler() != null) {
            mainThreadPublisher.observeOn(schedulerProvider.provideMainThreadScheduler())
                    .subscribe(consumerProvider.getMainThreadConsumer());
        } else {
            mainThreadPublisher.subscribe(consumerProvider.getMainThreadConsumer());
        }

        iOThreadPublisher.observeOn(schedulerProvider.provideIOScheduler())
                .subscribe(consumerProvider.getIOThreadConsumer());

        computationThreadPublisher.observeOn(schedulerProvider.provideComputationScheduler())
                .subscribe(consumerProvider.getComputationThreadConsumer());

        trampolineThreadPublisher.observeOn(schedulerProvider.provideTrampolineScheduler())
                .subscribe(consumerProvider.getTrampolineThreadConsumer());

        executorThreadPublisher.observeOn(schedulerProvider.provideExecutorScheduler())
                .subscribe(consumerProvider.getExecutorThreadConsumer());

        newThreadPublisher.observeOn(schedulerProvider.provideNewThreadScheduler())
                .subscribe(consumerProvider.getNewThreadConsumer());
    }

    /**
     * The publisher for  the posting thread.
     *
     * @return the publisher for the posting thread.
     */
    @Override
    public PublishSubject<NYEvent> forPostingThread() {
        return postingThreadPublisher;
    }

    /**
     * The publisher for the main thread.
     *
     * @return the publisher for the main thread.
     */
    @Override
    public PublishSubject<NYEvent> forMainThread() {
        return mainThreadPublisher;
    }

    /**
     * The publisher for the IO thread.
     *
     * @return the publisher for the IO thread.
     */
    @Override
    public PublishSubject<NYEvent> forIOThread() {
        return iOThreadPublisher;
    }

    /**
     * The publisher for the computation thread.
     *
     * @return the publisher for the computation thread.
     */
    @Override
    public PublishSubject<NYEvent> forComputationThread() {
        return computationThreadPublisher;
    }

    /**
     * The publisher for the trampoline thread.
     *
     * @return the publisher for the trampoline thread.
     */
    @Override
    public PublishSubject<NYEvent> forTrampolineThread() {
        return trampolineThreadPublisher;
    }

    /**
     * The publisher for the executor thread.
     *
     * @return the publisher for the executor thread.
     */
    @Override
    public PublishSubject<NYEvent> forExecutorThread() {
        return executorThreadPublisher;
    }

    /**
     * The publisher for the new thread.
     *
     * @return the publisher for the new thread.
     */
    @Override
    public PublishSubject<NYEvent> forNewThread() {
        return newThreadPublisher;
    }

}
