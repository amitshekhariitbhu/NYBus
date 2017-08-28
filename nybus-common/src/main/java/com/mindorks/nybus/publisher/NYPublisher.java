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
import com.mindorks.nybus.event.Event;
import com.mindorks.nybus.scheduler.SchedulerProvider;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by amitshekhar on 28/08/17.
 */

public class NYPublisher implements Publisher {

    private PublishSubject<Event> postingThreadPublisher;
    private PublishSubject<Event> mainThreadPublisher;
    private PublishSubject<Event> iOThreadPublisher;
    private PublishSubject<Event> computationThreadPublisher;
    private PublishSubject<Event> trampolineThreadPublisher;
    private PublishSubject<Event> executorThreadPublisher;
    private PublishSubject<Event> newThreadPublisher;

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

    @Override
    public PublishSubject<Event> forPostingThread() {
        return postingThreadPublisher;
    }

    @Override
    public PublishSubject<Event> forMainThread() {
        return mainThreadPublisher;
    }

    @Override
    public PublishSubject<Event> forIOThread() {
        return iOThreadPublisher;
    }

    @Override
    public PublishSubject<Event> forComputationThread() {
        return computationThreadPublisher;
    }

    @Override
    public PublishSubject<Event> forTrampolineThread() {
        return trampolineThreadPublisher;
    }

    @Override
    public PublishSubject<Event> forExecutorThread() {
        return executorThreadPublisher;
    }

    @Override
    public PublishSubject<Event> forNewThread() {
        return newThreadPublisher;
    }

}
