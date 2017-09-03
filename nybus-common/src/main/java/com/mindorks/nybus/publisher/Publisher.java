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

public interface Publisher {

    void initPublishers(SchedulerProvider schedulerProvider,
                        ConsumerProvider consumerProvider);

    PublishSubject<NYEvent> forPostingThread();

    PublishSubject<NYEvent> forMainThread();

    PublishSubject<NYEvent> forIOThread();

    PublishSubject<NYEvent> forComputationThread();

    PublishSubject<NYEvent> forTrampolineThread();

    PublishSubject<NYEvent> forExecutorThread();

    PublishSubject<NYEvent> forNewThread();

}
