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


import com.mindorks.nybus.events.Event;
import com.mindorks.nybus.targets.ChannelTarget;
import com.mindorks.nybus.targets.SimpleTarget;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by amitshekhar on 25/08/17.
 */
public class NYBusTest {
    ArrayList<String> channelIdForRegistration = new ArrayList<>();

    ArrayList<String> channelIdForRegistrationTwo = new ArrayList<>();
    @Test
    public void testSimpleTarget() throws Exception {
        SimpleTarget simpleTarget = Mockito.spy(new SimpleTarget());
        simpleTarget.register();
        Event event = new Event();
        NYBus.get().post(event);
        verify(simpleTarget).onEventOne(event);
        verify(simpleTarget).onEventTwo(event);
//        simpleTarget.unregister();
    }

    @Test
    public void testOverrideTarget() throws Exception {

    }

    @Test
    public void testChannelTarget() throws Exception {
        channelIdForRegistration.add(ChannelTarget.CHANNEL_ONE);
        ChannelTarget channelTargetOne = Mockito.spy(new ChannelTarget(channelIdForRegistration));
        channelIdForRegistrationTwo.add(ChannelTarget.CHANNEL_TWO);
        ChannelTarget channelTargetTwo = Mockito.spy(new ChannelTarget(channelIdForRegistrationTwo));
        channelTargetOne.register();
        channelTargetTwo.register();
        NYBus.get().post("Message One", ChannelTarget.CHANNEL_ONE);
        NYBus.get().post("Message two", ChannelTarget.CHANNEL_TWO);
        verify(channelTargetOne).onEventForTypeOne("Message One");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message One");
        verify(channelTargetTwo).onEventForTypeTwo("Message two");
        verify(channelTargetTwo, never()).onEventForTypeOne("Message two");
        channelTargetOne.unregister();
        channelTargetTwo.unregister();
    }

    @Test
    public void testChannelTargetDefault() throws Exception {
        channelIdForRegistration.add(ChannelTarget.CHANNEL_ONE);
        channelIdForRegistration.add(ChannelTarget.CHANNEL_TWO);
        channelIdForRegistration.add(ChannelTarget.CHANNEL_DEFAULT);
        ChannelTarget channelTargetOne = Mockito.spy(new ChannelTarget(channelIdForRegistration));
        channelTargetOne.register();
        NYBus.get().post("Message Default");
        verify(channelTargetOne, never()).onEventForTypeOne("Message Default");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message Default");
        verify(channelTargetOne).onEventForTypeDefault("Message Default");

        NYBus.get().post("Message One" ,ChannelTarget.CHANNEL_ONE);
        verify(channelTargetOne).onEventForTypeOne("Message One");
        verify(channelTargetOne, never()).onEventForTypeDefault("Message One");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message One");



        channelTargetOne.unregister();

    }

}
