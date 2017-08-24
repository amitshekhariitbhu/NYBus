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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by amitshekhar on 25/08/17.
 */
public class NYBusTest {

    @Test
    public void testSimpleTarget() throws Exception {
        SimpleTarget simpleTarget = Mockito.spy(new SimpleTarget());
        simpleTarget.register();
        Event event = new Event();
        NYBus.get().post(event);
        verify(simpleTarget).onEventOne(event);
        verify(simpleTarget).onEventTwo(event);
        simpleTarget.unregister();
    }

    @Test
    public void testOverrideTarget() throws Exception {

    }

    @Test
    public void testChannelTarget() throws Exception {
        ChannelTarget channelTargetOne = Mockito.spy(new ChannelTarget(ChannelTarget.CHANNEL_ONE));
        ChannelTarget channelTargetTwo = Mockito.spy(new ChannelTarget(ChannelTarget.CHANNEL_TWO));
        channelTargetOne.register();
        channelTargetTwo.register();
        NYBus.get().post("Amit", ChannelTarget.CHANNEL_ONE);
        NYBus.get().post("Amit", ChannelTarget.CHANNEL_TWO);
        verify(channelTargetOne).onEventForTypeOne("Amit");
        verify(channelTargetOne, never()).onEventForTypeTwo("Amit");
        verify(channelTargetTwo).onEventForTypeTwo("Amit");
        verify(channelTargetTwo, never()).onEventForTypeOne("Amit");
        channelTargetOne.unregister();
        channelTargetTwo.unregister();
    }

}
