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

package com.mindorks.nybus.driver;

import com.mindorks.nybus.event.NYEvent;
import com.mindorks.nybus.finder.EventClassFinder;
import com.mindorks.nybus.finder.SubscribeMethodFinder;
import com.mindorks.nybus.logger.Logger;
import com.mindorks.nybus.publisher.Publisher;
import com.mindorks.nybus.subscriber.SubscriberHolder;

import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by amitshekhar on 28/08/17.
 */

/**
 * This abstract class provides the {@link Publisher} to its subclass {@link NYBusDriver}.
 */
abstract class BusDriver {

    /**
     * The lock used while delivering event.
     */
    final Object DELIVER_LOCK = new Object();

    /**
     * The Publisher required for posting events.
     */
    Publisher mPublisher;

    /**
     * The SubscribeMethodFinder for finding the subscribed method.
     */
    SubscribeMethodFinder mSubscribeMethodFinder;

    /**
     * The EventClassFinder for finding the event class associated.
     */
    EventClassFinder mEventClassFinder;

    /**
     * The Logger for logging exceptions.
     */
    Logger mLogger;

    /**
     * Check if login is enabled.
     */
    boolean log = false;

    /**
     * The main map which holds the event class, target and SubscriberHolder.
     */
    ConcurrentHashMap<Class<?>, ConcurrentHashMap<Object,
            ConcurrentHashMap<String, SubscriberHolder>>> mEventsToTargetsMap;

    BusDriver(Publisher publisher,
              SubscribeMethodFinder subscribeMethodFinder,
              EventClassFinder eventClassFinder,
              Logger logger) {
        this.mPublisher = publisher;
        this.mSubscribeMethodFinder = subscribeMethodFinder;
        this.mEventClassFinder = eventClassFinder;
        this.mLogger = logger;
        this.mEventsToTargetsMap = new ConcurrentHashMap<>();
    }

    /**
     * The Publisher for posting thread.
     *
     * @return the Publisher for posting thread.
     */
    PublishSubject<NYEvent> getPostingThreadPublisher() {
        return mPublisher.forPostingThread();
    }

    /**
     * The Publisher for main thread.
     *
     * @return the Publisher for main thread.
     */
    PublishSubject<NYEvent> getMainThreadPublisher() {
        return mPublisher.forMainThread();
    }

    /**
     * The Publisher for IO thread.
     *
     * @return the Publisher for IO thread.
     */
    PublishSubject<NYEvent> getIOThreadPublisher() {
        return mPublisher.forIOThread();
    }

    /**
     * The Publisher for computation thread.
     *
     * @return the Publisher for computation thread.
     */
    PublishSubject<NYEvent> getComputationThreadPublisher() {
        return mPublisher.forComputationThread();
    }

    /**
     * The Publisher for trampoline thread.
     *
     * @return the Publisher for trampoline thread.
     */
    PublishSubject<NYEvent> getTrampolineThreadPublisher() {
        return mPublisher.forTrampolineThread();
    }

    /**
     * The Publisher for executor thread.
     *
     * @return the Publisher for executor thread.
     */
    PublishSubject<NYEvent> getExecutorThreadPublisher() {
        return mPublisher.forExecutorThread();
    }

    /**
     * The Publisher for new thread.
     *
     * @return the Publisher for new thread.
     */
    PublishSubject<NYEvent> getNewThreadPublisher() {
        return mPublisher.forNewThread();
    }

}
