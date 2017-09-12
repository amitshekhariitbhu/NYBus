package com.mindorks.nybus;

import com.mindorks.nybus.events.Event;
import com.mindorks.nybus.events.EventOne;
import com.mindorks.nybus.events.EventTwo;
import com.mindorks.nybus.events.InterfaceEventImpl;
import com.mindorks.nybus.events.NoTargetEvent;
import com.mindorks.nybus.events.SubClassEvent;
import com.mindorks.nybus.logger.Logger;
import com.mindorks.nybus.targets.ChannelTarget;
import com.mindorks.nybus.targets.ExceptionTarget;
import com.mindorks.nybus.targets.InterfaceEventTarget;
import com.mindorks.nybus.targets.MultipleChannelIDMethod;
import com.mindorks.nybus.targets.OverrideTarget;
import com.mindorks.nybus.targets.SimpleTarget;
import com.mindorks.nybus.targets.SubClassEventTarget;
import com.mindorks.nybus.targets.SuperSimpleTarget;

import org.junit.Test;
import org.mockito.Mock;
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
 * Created by anandgaurav on 12-09-2017.
 */

public class CommonTest {

    public Bus bus;

    @Mock
    Logger logger;

    @Test
    public void testSimpleTarget() throws Exception {
        SimpleTarget simpleTarget = Mockito.spy(new SimpleTarget());
        bus.register(simpleTarget);
        Event event = new Event();
        bus.post(event);
        verify(simpleTarget).onEventOne(event);
        verify(simpleTarget).onEventTwo(event);
        bus.unregister(simpleTarget);
        Event eventOne = new Event();
        bus.post(eventOne);
        verify(simpleTarget, never()).onEventOne(eventOne);
        verify(simpleTarget, never()).onEventTwo(eventOne);
    }

    @Test
    public void testSuperSimpleTarget() throws Exception {
        SuperSimpleTarget superSimpleTarget = Mockito.spy(new SuperSimpleTarget());
        bus.register(superSimpleTarget);
        Event event = new Event();
        bus.post(event);
        verify(superSimpleTarget).onEventOne(event);
        verify(superSimpleTarget).onEventTwo(event);
        verify(superSimpleTarget).onEventThree(event);
        bus.unregister(superSimpleTarget);
        Event eventOne = new Event();
        bus.post(eventOne);
        verify(superSimpleTarget, never()).onEventOne(eventOne);
        verify(superSimpleTarget, never()).onEventTwo(eventOne);
        verify(superSimpleTarget, never()).onEventThree(eventOne);
    }

    @Test
    public void testOverrideTarget() throws Exception {
        OverrideTarget overrideTarget = Mockito.spy(new OverrideTarget());
        bus.register(overrideTarget);
        EventOne eventOne = new EventOne();
        EventTwo eventTwo = new EventTwo();
        bus.post(eventOne);
        verify(overrideTarget).onEvent(eventOne);
        verify(overrideTarget, never()).onEvent(eventTwo);
        bus.post(eventTwo);
        verify(overrideTarget).onEvent(eventTwo);
        bus.unregister(overrideTarget);
    }

    @Test
    public void testChannelTarget() throws Exception {
        ChannelTarget channelTargetOne = Mockito.spy(new ChannelTarget());
        ChannelTarget channelTargetTwo = Mockito.spy(new ChannelTarget());
        bus.register(channelTargetOne, ChannelTarget.CHANNEL_ONE);
        bus.register(channelTargetTwo, ChannelTarget.CHANNEL_TWO);
        bus.post("Message One", ChannelTarget.CHANNEL_ONE);
        verify(channelTargetOne).onEventForTypeOne("Message One");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message One");
        bus.post("Message two", ChannelTarget.CHANNEL_TWO);
        verify(channelTargetTwo).onEventForTypeTwo("Message two");
        verify(channelTargetTwo, never()).onEventForTypeOne("Message two");
        bus.unregister(channelTargetOne, ChannelTarget.CHANNEL_ONE);
        bus.unregister(channelTargetTwo, ChannelTarget.CHANNEL_TWO);
    }

    @Test
    public void testChannelTargetDefault() throws Exception {
        ChannelTarget channelTargetOne = Mockito.spy(new ChannelTarget());
        bus.register(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_DEFAULT);
        bus.post("Message Default");
        verify(channelTargetOne, never()).onEventForTypeOne("Message Default");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message Default");
        verify(channelTargetOne).onEventForTypeDefault("Message Default");
        bus.post("Message One", ChannelTarget.CHANNEL_ONE);
        verify(channelTargetOne).onEventForTypeOne("Message One");
        verify(channelTargetOne, never()).onEventForTypeDefault("Message One");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message One");
        bus.unregister(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_DEFAULT);
    }

    @Test
    public void testChannelTargetUnregister() throws Exception {
        ChannelTarget channelTargetOne = Mockito.spy(new ChannelTarget());
        bus.register(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_DEFAULT);
        bus.post("Message Default");
        verify(channelTargetOne, never()).onEventForTypeOne("Message Default");
        verify(channelTargetOne, never()).onEventForTypeTwo("Message Default");
        verify(channelTargetOne).onEventForTypeDefault("Message Default");
        bus.unregister(channelTargetOne);
        bus.post("Message Two", ChannelTarget.CHANNEL_TWO);
        verify(channelTargetOne, never()).onEventForTypeOne("Message Two");
        verify(channelTargetOne, never()).onEventForTypeDefault("Message Two");
        verify(channelTargetOne).onEventForTypeTwo("Message Two");
        bus.unregister(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO);
    }

    @Test
    public void testChannelMultipleChannelMethod() throws Exception {
        MultipleChannelIDMethod multipleChannelIDMethod = Mockito.spy(new MultipleChannelIDMethod());
        bus.register(multipleChannelIDMethod, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO);
        bus.post("Message on One", ChannelTarget.CHANNEL_ONE);
        verify(multipleChannelIDMethod).onEventForTypeString("Message on One");
        bus.post("Message on two", ChannelTarget.CHANNEL_TWO);
        verify(multipleChannelIDMethod).onEventForTypeString("Message on two");
        bus.post("Message on default");
        verify(multipleChannelIDMethod, never()).onEventForTypeString("Message on default");
        bus.unregister(multipleChannelIDMethod, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO);
    }

    @Test
    public void testHugeNumberOfEvents() throws Exception {

        final CountDownLatch latch = new CountDownLatch(200);

        SimpleTarget simpleTarget = Mockito.spy(new SimpleTarget());
        bus.register(simpleTarget);
        final Event event = new Event();

        ThreadPoolExecutor executorOne = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());

        for (int i = 0; i < 100; i++) {
            executorOne.execute(new Runnable() {
                @Override
                public void run() {
                    bus.post(event);
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
                    bus.post(event);
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
        bus.register(subClassEventTarget);
        SubClassEvent event = new SubClassEvent();
        bus.post(event);
        verify(subClassEventTarget).onEvent(event);
        verify(subClassEventTarget).onEventSubClass(event);
        bus.unregister(subClassEventTarget);
    }

    @Test
    public void testInterfaceEvent() throws Exception {
        InterfaceEventTarget interfaceEventTarget = Mockito.spy(new InterfaceEventTarget());
        bus.register(interfaceEventTarget);
        InterfaceEventImpl event = new InterfaceEventImpl();
        bus.post(event);
        verify(interfaceEventTarget).onEventInterface(event);
        verify(interfaceEventTarget).onEventInterfaceImpl(event);
        bus.unregister(interfaceEventTarget);
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
                    bus.register(simpleTarget);
                    latch.countDown();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    bus.unregister(simpleTarget);
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
                    bus.post(event);
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

        isRegistered = bus.isRegistered(channelTargetOne, ChannelTarget.CHANNEL_ONE);
        assertTrue(!isRegistered);

        bus.register(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_DEFAULT);
        isRegistered = bus.isRegistered(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO);
        assertTrue(isRegistered);

        isRegistered = bus.isRegistered(channelTargetOne);
        assertTrue(isRegistered);

        bus.unregister(channelTargetOne, ChannelTarget.CHANNEL_ONE);
        isRegistered = bus.isRegistered(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO);
        assertTrue(!isRegistered);

        bus.unregister(channelTargetOne, ChannelTarget.CHANNEL_DEFAULT);
        isRegistered = bus.isRegistered(channelTargetOne);
        assertTrue(!isRegistered);


        bus.register(channelTargetOne, ChannelTarget.CHANNEL_ONE);
        isRegistered = bus.isRegistered(channelTargetOne, ChannelTarget.CHANNEL_ONE,
                ChannelTarget.CHANNEL_TWO, ChannelTarget.CHANNEL_THREE);
        assertTrue(!isRegistered);

    }

    @Test
    public void testMultipleRegistrationOnSameChannelId() {
        ExceptionTarget exceptionTarget = Mockito.spy(new ExceptionTarget());
        bus.register(exceptionTarget, "one");
        bus.register(exceptionTarget, "one");
        verify(logger).log(exceptionTarget.getClass()
                + " is already registered on same channel ids");
    }

    @Test
    public void testRegisterWithNoSubscribeMethods() {
        ExceptionTarget exceptionTarget = Mockito.spy(new ExceptionTarget());
        bus.register(exceptionTarget, "two");
        verify(logger).log("Subscriber " + exceptionTarget.getClass()
                + " and its super classes have no public methods with the @Subscribe annotation");
    }

    @Test
    public void testRegisterWithSomeSubscribeMethods() {
        ExceptionTarget exceptionTarget = Mockito.spy(new ExceptionTarget());
        bus.register(exceptionTarget, "one", "two");
        verify(logger).log("Subscriber " + exceptionTarget.getClass()
                + " and its super classes have no public methods with the " +
                "@Subscribe annotation on ChannelID two");
    }

    @Test
    public void testPostWithNoTargetOnParticularChannel() {
        ExceptionTarget exceptionTarget = Mockito.spy(new ExceptionTarget());
        String eventString = "Method not found on channel ID Two";
        bus.register(exceptionTarget, "one");
        bus.post(eventString, "one");
        bus.post(eventString, "two");
        verify(logger).log("No target found for the event" + eventString.getClass()
                + " on channel ID" + "two");
    }

    @Test
    public void testPostWithNoTarget() {
        ExceptionTarget exceptionTarget = Mockito.spy(new ExceptionTarget());
        final NoTargetEvent eventObject = new NoTargetEvent();
        bus.register(exceptionTarget, "one");
        bus.post(eventObject);
        verify(logger).log("No target found for the event" + eventObject.getClass());
    }

    @Test
    public void testUnregisterWithoutRegistration() {
        ExceptionTarget exceptionTarget = Mockito.spy(new ExceptionTarget());
        bus.unregister(exceptionTarget, "one");
        verify(logger).log(exceptionTarget.getClass()
                + " is either not subscribed(on some channel ID you wish to unregister " +
                "from) " +
                "or has " +
                "already been " +
                "unregistered");
    }

    @Test
    public void testUnregisterWithoutRegistrationOnChannelID() {
        ExceptionTarget exceptionTarget = Mockito.spy(new ExceptionTarget());
        bus.register(exceptionTarget, "one");
        bus.unregister(exceptionTarget);
        verify(logger).log(exceptionTarget.getClass()
                + " is either not subscribed(on some channel ID you wish to unregister " +
                "from) " +
                "or has " +
                "already been " +
                "unregistered");
    }

}
