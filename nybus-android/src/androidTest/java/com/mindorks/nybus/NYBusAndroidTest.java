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

import android.support.test.runner.AndroidJUnit4;

import com.mindorks.nybus.events.Event;
import com.mindorks.nybus.events.EventOne;
import com.mindorks.nybus.events.EventTwo;
import com.mindorks.nybus.events.InterfaceEventImpl;
import com.mindorks.nybus.events.SubClassEvent;
import com.mindorks.nybus.targets.ChannelTarget;
import com.mindorks.nybus.targets.InterfaceEventTarget;
import com.mindorks.nybus.targets.OverrideTarget;
import com.mindorks.nybus.targets.SimpleTarget;
import com.mindorks.nybus.targets.SubClassEventTarget;
import com.mindorks.nybus.targets.SuperSimpleTarget;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class NYBusAndroidTest {

    @Test
    public void testSimpleTarget() throws Exception {
        SimpleTarget simpleTarget = Mockito.spy(new SimpleTarget());
        simpleTarget.register();
        Event event = new Event();
        NYBus.get().post(event);
        verify(simpleTarget).onEventOne(event);
        verify(simpleTarget).onEventTwo(event);
        simpleTarget.unregister();
        Event eventOne = new Event();
        NYBus.get().post(eventOne);
        verify(simpleTarget, never()).onEventOne(eventOne);
        verify(simpleTarget, never()).onEventTwo(eventOne);
    }

    @Test
    public void testSuperSimpleTarget() throws Exception {
        SuperSimpleTarget superSimpleTarget = Mockito.spy(new SuperSimpleTarget());
        superSimpleTarget.register();
        Event event = new Event();
        NYBus.get().post(event);
        verify(superSimpleTarget).onEventOne(event);
        verify(superSimpleTarget).onEventTwo(event);
        verify(superSimpleTarget).onEventThree(event);
        superSimpleTarget.unregister();
        Event eventOne = new Event();
        NYBus.get().post(eventOne);
        verify(superSimpleTarget, never()).onEventOne(eventOne);
        verify(superSimpleTarget, never()).onEventTwo(eventOne);
        verify(superSimpleTarget, never()).onEventThree(eventOne);
    }

    @Test
    public void testOverrideTarget() throws Exception {
        OverrideTarget overrideTarget = Mockito.spy(new OverrideTarget());
        overrideTarget.register();
        EventOne eventOne = new EventOne();
        EventTwo eventTwo = new EventTwo();
        NYBus.get().post(eventOne);
        verify(overrideTarget).onEvent(eventOne);
        verify(overrideTarget, never()).onEvent(eventTwo);
        NYBus.get().post(eventTwo);
        verify(overrideTarget).onEvent(eventTwo);
        overrideTarget.unregister();
    }

    @Test
    public void testChannelTarget() throws Exception {
        ChannelTarget channelTargetOne = Mockito.spy(new ChannelTarget());
        ChannelTarget channelTargetTwo = Mockito.spy(new ChannelTarget());
        channelTargetOne.register(ChannelTarget.CHANNEL_ONE);
        channelTargetTwo.register(ChannelTarget.CHANNEL_TWO);
        NYBus.get().post("Message One", ChannelTarget.CHANNEL_ONE);
        verify(channelTargetOne).onEventForTypeOne("Message One");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message One");
        NYBus.get().post("Message two", ChannelTarget.CHANNEL_TWO);
        verify(channelTargetTwo).onEventForTypeTwo("Message two");
        verify(channelTargetTwo, never()).onEventForTypeOne("Message two");
        channelTargetOne.unregister(ChannelTarget.CHANNEL_ONE);
        channelTargetTwo.unregister(ChannelTarget.CHANNEL_TWO);
    }

    @Test
    public void testChannelTargetDefault() throws Exception {
        ChannelTarget channelTargetOne = Mockito.spy(new ChannelTarget());
        channelTargetOne.register(ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_DEFAULT);
        NYBus.get().post("Message Default");
        verify(channelTargetOne, never()).onEventForTypeOne("Message Default");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message Default");
        verify(channelTargetOne).onEventForTypeDefault("Message Default");
        NYBus.get().post("Message One", ChannelTarget.CHANNEL_ONE);
        verify(channelTargetOne).onEventForTypeOne("Message One");
        verify(channelTargetOne, never()).onEventForTypeDefault("Message One");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message One");
        channelTargetOne.unregister(ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_DEFAULT);
    }

    @Test
    public void testChannelTargetUnregister() throws Exception {
        ChannelTarget channelTargetOne = Mockito.spy(new ChannelTarget());
        channelTargetOne.register(ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_DEFAULT);
        NYBus.get().post("Message Default");
        verify(channelTargetOne, never()).onEventForTypeOne("Message Default");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message Default");
        verify(channelTargetOne).onEventForTypeDefault("Message Default");
        channelTargetOne.unregister();
        NYBus.get().post("Message Two", ChannelTarget.CHANNEL_TWO);
        verify(channelTargetOne, never()).onEventForTypeOne("Message Two");
        verify(channelTargetOne, never()).onEventForTypeDefault("Message Two");
        verify(channelTargetOne).onEventForTypeTwo("Message Two");
        channelTargetOne.unregister(ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_DEFAULT);
    }

    @Test
    public void testHugeNumberOfEvents() throws Exception {

        final CountDownLatch latch = new CountDownLatch(200);

        SimpleTarget simpleTarget = Mockito.spy(new SimpleTarget());
        simpleTarget.register();
        final Event event = new Event();

        ThreadPoolExecutor executorOne = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());

        for (int i = 0; i < 100; i++) {
            executorOne.execute(new Runnable() {
                @Override
                public void run() {
                    NYBus.get().post(event);
                    latch.countDown();
                }
            });
        }

        ThreadPoolExecutor executorTwo = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());

        for (int i = 0; i < 100; i++) {
            executorTwo.execute(new Runnable() {
                @Override
                public void run() {
                    NYBus.get().post(event);
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, SECONDS));

        verify(simpleTarget, times(200)).onEventOne(event);
        verify(simpleTarget, times(200)).onEventTwo(event);
    }

    @Test
    public void testSubClassEvent() throws Exception {
        SubClassEventTarget subClassEventTarget = Mockito.spy(new SubClassEventTarget());
        subClassEventTarget.register();
        SubClassEvent event = new SubClassEvent();
        NYBus.get().post(event);
        verify(subClassEventTarget).onEvent(event);
        verify(subClassEventTarget).onEventSubClass(event);
        subClassEventTarget.unregister();
    }

    @Test
    public void testInterfaceEvent() throws Exception {
        InterfaceEventTarget interfaceEventTarget = Mockito.spy(new InterfaceEventTarget());
        interfaceEventTarget.register();
        InterfaceEventImpl event = new InterfaceEventImpl();
        NYBus.get().post(event);
        verify(interfaceEventTarget).onEventInterface(event);
        verify(interfaceEventTarget).onEventInterfaceImpl(event);
        interfaceEventTarget.unregister();
    }

    public void testIsRegistered() throws Exception {
        boolean isRegistered;
        ChannelTarget channelTargetOne = Mockito.spy(new ChannelTarget());

        channelTargetOne.register(ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_DEFAULT);
        isRegistered = channelTargetOne.isRegistered(ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO);
        assertTrue(isRegistered);

        channelTargetOne.unregister(ChannelTarget.CHANNEL_ONE);
        isRegistered = channelTargetOne.isRegistered(ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO);
        assertTrue(!isRegistered);

        channelTargetOne.register(ChannelTarget.CHANNEL_ONE);
        isRegistered = channelTargetOne.isRegistered(ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_FOUR);
        assertTrue(!isRegistered);

    }


}
