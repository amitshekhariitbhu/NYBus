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

package com.mindorks.nybus.subscriber;

import com.mindorks.nybus.thread.NYThread;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * Created by Jyoti on 27/08/17.
 */

public class SubscriberHolder{
    private static final String SEPARATOR = "_";
    public Method subscribedMethod;
    public List<String> subscribedChannelID;
    public NYThread subscribedThreadType;

    public SubscriberHolder(Method subscribedMethod,
                            List<String> subscribedChannelID,
                            NYThread subscribedThreadType) {
        this.subscribedMethod = subscribedMethod;
        this.subscribedChannelID = subscribedChannelID;
        this.subscribedThreadType = subscribedThreadType;
    }


    public String getKeyForSubscribeHolderMap(SubscriberHolder subscriberHolder) {
        return subscriberHolder.subscribedMethod.getName()
                + SEPARATOR
                + subscriberHolder.subscribedMethod.getParameterTypes()[0].toString();
    }


}
