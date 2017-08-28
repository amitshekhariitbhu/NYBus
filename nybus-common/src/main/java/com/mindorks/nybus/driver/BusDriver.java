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

import com.mindorks.nybus.event.Event;
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

    ConcurrentHashMap<Class<?>, ConcurrentHashMap<Object, Set<SubscriberHolder>>> mEventsToTargetsMap;

    BusDriver(Publisher publisher) {
        this.mPublisher = publisher;
        this.mEventsToTargetsMap = new ConcurrentHashMap<>();
    }

    PublishSubject<Event> getPostingThreadPublisher() {
        return mPublisher.forPostingThread();
    }

    PublishSubject<Event> getMainThreadPublisher() {
        return mPublisher.forMainThread();
    }

    PublishSubject<Event> getIOThreadPublisher() {
        return mPublisher.forIOThread();
    }

    PublishSubject<Event> getComputationThreadPublisher() {
        return mPublisher.forComputationThread();
    }

    PublishSubject<Event> getTrampolineThreadPublisher() {
        return mPublisher.forTrampolineThread();
    }

    PublishSubject<Event> getExecutorThreadPublisher() {
        return mPublisher.forExecutorThread();
    }

    PublishSubject<Event> getNewThreadPublisher() {
        return mPublisher.forNewThread();
    }

}
