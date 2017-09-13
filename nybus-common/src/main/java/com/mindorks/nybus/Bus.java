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

package com.mindorks.nybus;

import com.mindorks.nybus.logger.Logger;
import com.mindorks.nybus.scheduler.SchedulerProvider;

import java.util.List;

/**
 * Created by anandgaurav on 12-09-2017.
 */

public interface Bus {

    void setSchedulerProvider(SchedulerProvider schedulerProvider);

    void setLogger(Logger logger);

    void register(Object object, String... channelIDs);

    void register(Object object, List<String> channelId);

    void unregister(Object object, String... channelIDs);

    void unregister(Object object, List<String> channelId);

    void post(Object object);

    void post(Object object, String channelId);

    boolean isRegistered(Object object, String... channelIDs);

    void enableLogging();
}
