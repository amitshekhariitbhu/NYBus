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
import com.mindorks.nybus.events.EventOne;
import com.mindorks.nybus.events.EventTwo;
import com.mindorks.nybus.events.InterfaceEventImpl;
import com.mindorks.nybus.events.NoTargetEvent;
import com.mindorks.nybus.events.SubClassEvent;
import com.mindorks.nybus.exception.NYBusException;
import com.mindorks.nybus.targets.ChannelTarget;
import com.mindorks.nybus.targets.ExceptionTarget;
import com.mindorks.nybus.targets.InterfaceEventTarget;
import com.mindorks.nybus.targets.MultipleChannelIDMethod;
import com.mindorks.nybus.targets.OverrideTarget;
import com.mindorks.nybus.targets.SimpleTarget;
import com.mindorks.nybus.targets.SubClassEventTarget;
import com.mindorks.nybus.targets.SuperSimpleTarget;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

/**
 * Created by amitshekhar on 25/08/17.
 */
public class NYBusTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() throws Exception {
        NYBus.get().setSchedulerProvider(new TestSchedulerProvider());
    }

    @Test
    public void testSimpleTarget() throws Exception {
        SimpleTarget simpleTarget = Mockito.spy(new SimpleTarget());
        NYBus.get().register(simpleTarget);
        Event event = new Event();
        NYBus.get().post(event);
        verify(simpleTarget).onEventOne(event);
        verify(simpleTarget).onEventTwo(event);
        NYBus.get().unregister(simpleTarget);
        Event eventOne = new Event();
        NYBus.get().post(eventOne);
        verify(simpleTarget, never()).onEventOne(eventOne);
        verify(simpleTarget, never()).onEventTwo(eventOne);
    }

    @Test
    public void testSuperSimpleTarget() throws Exception {
        SuperSimpleTarget superSimpleTarget = Mockito.spy(new SuperSimpleTarget());
        NYBus.get().register(superSimpleTarget);
        Event event = new Event();
        NYBus.get().post(event);
        verify(superSimpleTarget).onEventOne(event);
        verify(superSimpleTarget).onEventTwo(event);
        verify(superSimpleTarget).onEventThree(event);
        NYBus.get().unregister(superSimpleTarget);
        Event eventOne = new Event();
        NYBus.get().post(eventOne);
        verify(superSimpleTarget, never()).onEventOne(eventOne);
        verify(superSimpleTarget, never()).onEventTwo(eventOne);
        verify(superSimpleTarget, never()).onEventThree(eventOne);
    }

    @Test
    public void testOverrideTarget() throws Exception {
        OverrideTarget overrideTarget = Mockito.spy(new OverrideTarget());
        NYBus.get().register(overrideTarget);
        EventOne eventOne = new EventOne();
        EventTwo eventTwo = new EventTwo();
        NYBus.get().post(eventOne);
        verify(overrideTarget).onEvent(eventOne);
        verify(overrideTarget, never()).onEvent(eventTwo);
        NYBus.get().post(eventTwo);
        verify(overrideTarget).onEvent(eventTwo);
        NYBus.get().unregister(overrideTarget);
    }

    @Test
    public void testChannelTarget() throws Exception {
        ChannelTarget channelTargetOne = Mockito.spy(new ChannelTarget());
        ChannelTarget channelTargetTwo = Mockito.spy(new ChannelTarget());
        NYBus.get().register(channelTargetOne, ChannelTarget.CHANNEL_ONE);
        NYBus.get().register(channelTargetTwo, ChannelTarget.CHANNEL_TWO);
        NYBus.get().post("Message One", ChannelTarget.CHANNEL_ONE);
        verify(channelTargetOne).onEventForTypeOne("Message One");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message One");
        NYBus.get().post("Message two", ChannelTarget.CHANNEL_TWO);
        verify(channelTargetTwo).onEventForTypeTwo("Message two");
        verify(channelTargetTwo, never()).onEventForTypeOne("Message two");
        NYBus.get().unregister(channelTargetOne, ChannelTarget.CHANNEL_ONE);
        NYBus.get().unregister(channelTargetTwo, ChannelTarget.CHANNEL_TWO);
    }

    @Test
    public void testChannelTargetDefault() throws Exception {
        ChannelTarget channelTargetOne = Mockito.spy(new ChannelTarget());
        NYBus.get().register(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_DEFAULT);
        NYBus.get().post("Message Default");
        verify(channelTargetOne, never()).onEventForTypeOne("Message Default");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message Default");
        verify(channelTargetOne).onEventForTypeDefault("Message Default");
        NYBus.get().post("Message One", ChannelTarget.CHANNEL_ONE);
        verify(channelTargetOne).onEventForTypeOne("Message One");
        verify(channelTargetOne, never()).onEventForTypeDefault("Message One");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message One");
        NYBus.get().unregister(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_DEFAULT);
    }

    @Test
    public void testChannelTargetUnregister() throws Exception {
        ChannelTarget channelTargetOne = Mockito.spy(new ChannelTarget());
        NYBus.get().register(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_DEFAULT);
        NYBus.get().post("Message Default");
        verify(channelTargetOne, never()).onEventForTypeOne("Message Default");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message Default");
        verify(channelTargetOne).onEventForTypeDefault("Message Default");
        NYBus.get().unregister(channelTargetOne);
        NYBus.get().post("Message Two", ChannelTarget.CHANNEL_TWO);
        verify(channelTargetOne, never()).onEventForTypeOne("Message Two");
        verify(channelTargetOne, never()).onEventForTypeDefault("Message Two");
        verify(channelTargetOne).onEventForTypeTwo("Message Two");
        NYBus.get().unregister(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO);
    }

    @Test
    public void testChannelMultipleChannelMethod() throws Exception {
        MultipleChannelIDMethod multipleChannelIDMethod = Mockito.spy(new MultipleChannelIDMethod());
        NYBus.get().register(multipleChannelIDMethod, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO);
        NYBus.get().post("Message on One", ChannelTarget.CHANNEL_ONE);
        verify(multipleChannelIDMethod).onEventForTypeString("Message on One");
        NYBus.get().post("Message on two", ChannelTarget.CHANNEL_TWO);
        verify(multipleChannelIDMethod).onEventForTypeString("Message on two");
        try {
            NYBus.get().post("Message on default");
        } catch (NYBusException ignore) {

        }

        verify(multipleChannelIDMethod, never()).onEventForTypeString("Message on default");
        NYBus.get().unregister(multipleChannelIDMethod, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO);
    }

    @Test
    public void testHugeNumberOfEvents() throws Exception {

        final CountDownLatch latch = new CountDownLatch(200);

        SimpleTarget simpleTarget = Mockito.spy(new SimpleTarget());
        NYBus.get().register(simpleTarget);
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
        NYBus.get().register(subClassEventTarget);
        SubClassEvent event = new SubClassEvent();
        NYBus.get().post(event);
        verify(subClassEventTarget).onEvent(event);
        verify(subClassEventTarget).onEventSubClass(event);
        NYBus.get().unregister(subClassEventTarget);
    }

    @Test
    public void testInterfaceEvent() throws Exception {
        InterfaceEventTarget interfaceEventTarget = Mockito.spy(new InterfaceEventTarget());
        NYBus.get().register(interfaceEventTarget);
        InterfaceEventImpl event = new InterfaceEventImpl();
        NYBus.get().post(event);
        verify(interfaceEventTarget).onEventInterface(event);
        verify(interfaceEventTarget).onEventInterfaceImpl(event);
        NYBus.get().unregister(interfaceEventTarget);
    }

    @Test
    public void testHugeNumberOfRegisterAndUnRegister() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3000);

        final SimpleTarget simpleTarget = Mockito.spy(new SimpleTarget());
        final Event event = new Event();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    try {
                        NYBus.get().register(simpleTarget);
                    } catch (NYBusException ignore) {

                    }
                    latch.countDown();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    try {
                        NYBus.get().unregister(simpleTarget);
                    } catch (NYBusException ignore) {

                    }
                    latch.countDown();
                }
            }
        }).start();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());

        for (int i = 0; i < 1000; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        NYBus.get().post(event);
                    } catch (NYBusException ignore) {

                    }
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, SECONDS));
    }


    @Test
    public void testIsRegistered() throws Exception {
        boolean isRegistered;
        ChannelTarget channelTargetOne = Mockito.spy(new ChannelTarget());

        isRegistered = NYBus.get().isRegistered(channelTargetOne, ChannelTarget.CHANNEL_ONE);
        assertTrue(!isRegistered);

        NYBus.get().register(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_DEFAULT);
        isRegistered = NYBus.get().isRegistered(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO);
        assertTrue(isRegistered);

        isRegistered = NYBus.get().isRegistered(channelTargetOne);
        assertTrue(isRegistered);

        NYBus.get().unregister(channelTargetOne, ChannelTarget.CHANNEL_ONE);
        isRegistered = NYBus.get().isRegistered(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO);
        assertTrue(!isRegistered);

        NYBus.get().unregister(channelTargetOne, ChannelTarget.CHANNEL_DEFAULT);
        isRegistered = NYBus.get().isRegistered(channelTargetOne);
        assertTrue(!isRegistered);


        NYBus.get().register(channelTargetOne, ChannelTarget.CHANNEL_ONE);
        isRegistered = NYBus.get().isRegistered(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_THREE);
        assertTrue(!isRegistered);

    }

    @Test
    public void testMultipleRegistrationOnSameChannelId() {
        ExceptionTarget exceptionTarget = Mockito.spy(new ExceptionTarget());
        thrown.expect(NYBusException.class);
        thrown.expectMessage(exceptionTarget.getClass()
                + " is already registered on same channel ids");
        NYBus.get().register(exceptionTarget, "one");
        NYBus.get().register(exceptionTarget, "one");

    }

    @Test
    public void testRegisterWithNoSubscribeMethods() {
        ExceptionTarget exceptionTarget = Mockito.spy(new ExceptionTarget());
        thrown.expect(NYBusException.class);
        thrown.expectMessage("Subscriber " + exceptionTarget.getClass()
                + " and its super classes have no public methods with the @Subscribe annotation");
        NYBus.get().register(exceptionTarget, "two");

    }

    @Test
    public void testRegisterWithSomeSubscribeMethods() {
        ExceptionTarget exceptionTarget = Mockito.spy(new ExceptionTarget());
        thrown.expect(NYBusException.class);
        thrown.expectMessage("Subscriber " + exceptionTarget.getClass()
                + " and its super classes have no public methods with the " +
                "@Subscribe annotation on ChannelID two");
        NYBus.get().register(exceptionTarget, "one", "two");

    }

    @Test
    public void testPostWithNoTargetOnParticularChannel() {
        ExceptionTarget exceptionTarget = Mockito.spy(new ExceptionTarget());
        String eventString = "Method not found on channel ID Two";
        thrown.expect(NYBusException.class);
        thrown.expectMessage("No target found for the event" + eventString.getClass()
                + " on channel ID" + "two");
        NYBus.get().register(exceptionTarget, "one");
        NYBus.get().post(eventString, "one");
        NYBus.get().post(eventString, "two");
    }

    @Test
    public void testPostWithNoTarget() {
        ExceptionTarget exceptionTarget = Mockito.spy(new ExceptionTarget());
        final NoTargetEvent eventObject = new NoTargetEvent();
        thrown.expect(NYBusException.class);
        thrown.expectMessage("No target found for the event" + eventObject.getClass());
        NYBus.get().register(exceptionTarget, "one");
        NYBus.get().post(eventObject);
    }

    @Test
    public void testUnregisterWithoutRegistration() {
        ExceptionTarget exceptionTarget = Mockito.spy(new ExceptionTarget());
        thrown.expect(NYBusException.class);
        thrown.expectMessage(exceptionTarget.getClass()
                + " is either not subscribed(on some channel ID you wish to unregister " +
                "from) " +
                "or has " +
                "already been " +
                "unregistered");
        NYBus.get().unregister(exceptionTarget, "one");
    }

    @Test
    public void testUnregisterWithoutRegistrationOnChannelID() {
        ExceptionTarget exceptionTarget = Mockito.spy(new ExceptionTarget());
        thrown.expect(NYBusException.class);
        thrown.expectMessage(exceptionTarget.getClass()
                + " is either not subscribed(on some channel ID you wish to unregister " +
                "from) " +
                "or has " +
                "already been " +
                "unregistered");
        NYBus.get().register(exceptionTarget, "one");
        NYBus.get().unregister(exceptionTarget);
    }

}