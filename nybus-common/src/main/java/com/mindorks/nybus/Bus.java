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

/**
 * The interface for publish/subscribe system. Targets are registered in the system with
 * {@link #register(Object, String...) or {@link #register(Object, List)}} and unregistered with
 * {@link #unregister(Object, String...)} or {@link #unregister(Object, List)}. Events are posted
 * with ({@link #post(Object)}) or {@link #post(Object, String)} to the bus, which delivers it to
 * subscribers that have a matching handler method for the event type.
 */
public interface Bus {

    /**
     * Set the {@link SchedulerProvider}.
     *
     * @param schedulerProvider the scheduler provider.
     */
    void setSchedulerProvider(SchedulerProvider schedulerProvider);

    /**
     * Set {@link Logger}.
     *
     * @param logger the logger.
     */
    void setLogger(Logger logger);

    /**
     * Register for the event.
     *
     * @param object     the context object.
     * @param channelIDs channel ids.
     */
    void register(Object object, String... channelIDs);

    /**
     * Register for the event.
     *
     * @param object    the context object.
     * @param channelId list of channel ids.
     */
    void register(Object object, List<String> channelId);

    /**
     * Unregister from the event.
     *
     * @param object     the context object.
     * @param channelIDs channel ids.
     */
    void unregister(Object object, String... channelIDs);

    /**
     * Unregister from the event.
     *
     * @param object    the context object.
     * @param channelId list of channel ids.
     */
    void unregister(Object object, List<String> channelId);

    /**
     * Post the event.
     *
     * @param object the event.
     */
    void post(Object object);

    /**
     * Post the event.
     *
     * @param object    the context object.
     * @param channelId channel id.
     */
    void post(Object object, String channelId);

    /**
     * Check if registered.
     *
     * @param object     the context object.
     * @param channelIDs the channel ids.
     * @return is registered.
     */
    boolean isRegistered(Object object, String... channelIDs);

    /**
     * Enable logging.
     */
    void enableLogging();
}
