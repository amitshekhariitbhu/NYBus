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
import com.mindorks.nybus.publisher.Publisher;
import com.mindorks.nybus.subscriber.SubscriberHolder;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by amitshekhar on 28/08/17.
 */

abstract class BusDriver {

    Publisher mPublisher;

    SubscribeMethodFinder mSubscribeMethodFinder;

    EventClassFinder mEventClassFinder;

    ConcurrentHashMap<Class<?>, ConcurrentHashMap<Object, Set<SubscriberHolder>>> mEventsToTargetsMap;

    BusDriver(Publisher publisher,
              SubscribeMethodFinder subscribeMethodFinder,
              EventClassFinder eventClassFinder) {
        this.mPublisher = publisher;
        this.mSubscribeMethodFinder = subscribeMethodFinder;
        this.mEventClassFinder = eventClassFinder;
        this.mEventsToTargetsMap = new ConcurrentHashMap<>();
    }

    PublishSubject<NYEvent> getPostingThreadPublisher() {
        return mPublisher.forPostingThread();
    }

    PublishSubject<NYEvent> getMainThreadPublisher() {
        return mPublisher.forMainThread();
    }

    PublishSubject<NYEvent> getIOThreadPublisher() {
        return mPublisher.forIOThread();
    }

    PublishSubject<NYEvent> getComputationThreadPublisher() {
        return mPublisher.forComputationThread();
    }

    PublishSubject<NYEvent> getTrampolineThreadPublisher() {
        return mPublisher.forTrampolineThread();
    }

    PublishSubject<NYEvent> getExecutorThreadPublisher() {
        return mPublisher.forExecutorThread();
    }

    PublishSubject<NYEvent> getNewThreadPublisher() {
        return mPublisher.forNewThread();
    }

}
