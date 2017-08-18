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

package com.mindorks.nybus.internal;

import java.lang.reflect.Method;

/**
 * Created by Jyoti on 16/08/17.
 */

public class SubscribeMethodHolder {
    private Method subscribedMethod;
    private Object subscriberTarget;
    private Class<?> subscribedEventType;
    private String channelId;

    public SubscribeMethodHolder(Object subscriberTarget, Method subscribedMethod, Class<?> subscribedEventType,String channelId) {
        this.subscribedMethod = subscribedMethod;
        this.subscriberTarget = subscriberTarget;
        this.subscribedEventType = subscribedEventType;
        this.channelId = channelId;
    }

    public Method getSubscribedMethod() {
        return subscribedMethod;
    }

    public Object getSubscriberTarget() {
        return subscriberTarget;
    }

    public Class<?> getSubscribedEventType() {
        return subscribedEventType;
    }

    public String getChannelId() {
        return channelId;
    }
}
