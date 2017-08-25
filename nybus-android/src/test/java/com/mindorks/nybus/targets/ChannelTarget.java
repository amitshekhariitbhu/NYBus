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

package com.mindorks.nybus.targets;

import com.mindorks.nybus.NYBus;
import com.mindorks.nybus.annotation.Subscribe;

import java.util.ArrayList;

/**
 * Created by amitshekhar on 25/08/17.
 */

public class ChannelTarget implements Target {

    public static final String CHANNEL_ONE = "one";
    public static final String CHANNEL_TWO = "two";
    public static final String CHANNEL_DEFAULT = "default";
    ArrayList<String> channelIdForRegistration = new ArrayList<>();


    private String channel;

    public ChannelTarget(ArrayList<String> channelIdForRegistration) {
        this.channelIdForRegistration = channelIdForRegistration;
    }

    @Subscribe(channelId = CHANNEL_ONE)
    public void onEventForTypeOne(String value) {
        // only the instance of channel one should get this event
    }

    @Subscribe(channelId = CHANNEL_TWO)
    public void onEventForTypeTwo(String value) {
        // only the instance of channel two should get this event
    }

    @Subscribe
    public void onEventForTypeDefault(String value) {
        // only the instance of channel two should get this event
    }
    @Override
    public void register() {
        NYBus.get().register(this, channelIdForRegistration);
    }

    @Override
    public void unregister() {
        NYBus.get().unregister(this, channel);
    }
}
