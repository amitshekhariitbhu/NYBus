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

import com.mindorks.nybus.annotation.Subscribe;
import com.mindorks.nybus.event.EventChannel;
import com.mindorks.nybus.finder.NYSubscribeMethodFinder;
import com.mindorks.nybus.finder.SubscribeMethodFinder;
import com.mindorks.nybus.subscriber.SubscriberHolder;
import com.mindorks.nybus.thread.NYThread;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by amitshekhar on 03/09/17.
 */

public class NYSubscribeMethodFinderTest {

    private SubscribeMethodFinder subscribeMethodFinder;

    @Before
    public void before() throws Exception {
        subscribeMethodFinder = new NYSubscribeMethodFinder();
    }

    @Test
    public void testSubTestClassMethod() throws Exception {
        SubTestClass subTestClass = new SubTestClass();
        List<String> channels = Arrays.asList("one", "two", EventChannel.DEFAULT);
        HashMap<String, SubscriberHolder> uniqueSubscriberHolderMap =
                subscribeMethodFinder.getAll(subTestClass, channels);
        assertEquals(5, uniqueSubscriberHolderMap.size());

        String onEventSuper = "onEventSuper_class java.lang.String";
        String onEvent = "onEvent_class java.lang.String";
        String onEventOne = "onEventOne_class java.lang.String";
        String onEventTwo = "onEventTwo_class java.lang.String";
        String onEventOneTwo = "onEventOneTwo_class java.lang.String";

        SubscriberHolder onEventSuperSH = uniqueSubscriberHolderMap.get(onEventSuper);
        assertEquals(1, onEventSuperSH.subscribedChannelID.size());
        assertTrue(onEventSuperSH.subscribedChannelID.contains(EventChannel.DEFAULT));
        assertEquals(NYThread.POSTING, onEventSuperSH.subscribedThreadType);
        assertEquals("onEventSuper", onEventSuperSH.subscribedMethod.getName());

        SubscriberHolder onEventSH = uniqueSubscriberHolderMap.get(onEvent);
        assertEquals(1, onEventSH.subscribedChannelID.size());
        assertTrue(onEventSH.subscribedChannelID.contains(EventChannel.DEFAULT));
        assertEquals(NYThread.COMPUTATION, onEventSH.subscribedThreadType);
        assertEquals("onEvent", onEventSH.subscribedMethod.getName());

        SubscriberHolder onEventOneSH = uniqueSubscriberHolderMap.get(onEventOne);
        assertEquals(1, onEventOneSH.subscribedChannelID.size());
        assertTrue(onEventOneSH.subscribedChannelID.contains("one"));
        assertEquals(NYThread.POSTING, onEventOneSH.subscribedThreadType);
        assertEquals("onEventOne", onEventOneSH.subscribedMethod.getName());

        SubscriberHolder onEventTwoSH = uniqueSubscriberHolderMap.get(onEventTwo);
        assertEquals(1, onEventTwoSH.subscribedChannelID.size());
        assertTrue(onEventTwoSH.subscribedChannelID.contains("two"));
        assertEquals(NYThread.POSTING, onEventTwoSH.subscribedThreadType);
        assertEquals("onEventTwo", onEventTwoSH.subscribedMethod.getName());

        SubscriberHolder onEventOneTwoSH = uniqueSubscriberHolderMap.get(onEventOneTwo);
        assertEquals(2, onEventOneTwoSH.subscribedChannelID.size());
        assertTrue(onEventOneTwoSH.subscribedChannelID.contains("one"));
        assertTrue(onEventOneTwoSH.subscribedChannelID.contains("two"));
        assertEquals(NYThread.POSTING, onEventOneTwoSH.subscribedThreadType);
        assertEquals("onEventOneTwo", onEventOneTwoSH.subscribedMethod.getName());
    }

    @Test
    public void testSubTestClassMethodWithoutDefaultChannel() throws Exception {
        SubTestClass subTestClass = new SubTestClass();
        List<String> channels = Arrays.asList("one", "two");
        HashMap<String, SubscriberHolder> uniqueSubscriberHolderMap =
                subscribeMethodFinder.getAll(subTestClass, channels);
        assertEquals(3, uniqueSubscriberHolderMap.size());

        String onEventOne = "onEventOne_class java.lang.String";
        String onEventTwo = "onEventTwo_class java.lang.String";
        String onEventOneTwo = "onEventOneTwo_class java.lang.String";

        SubscriberHolder onEventOneSH = uniqueSubscriberHolderMap.get(onEventOne);
        assertEquals(1, onEventOneSH.subscribedChannelID.size());
        assertTrue(onEventOneSH.subscribedChannelID.contains("one"));
        assertEquals(NYThread.POSTING, onEventOneSH.subscribedThreadType);
        assertEquals("onEventOne", onEventOneSH.subscribedMethod.getName());

        SubscriberHolder onEventTwoSH = uniqueSubscriberHolderMap.get(onEventTwo);
        assertEquals(1, onEventTwoSH.subscribedChannelID.size());
        assertTrue(onEventTwoSH.subscribedChannelID.contains("two"));
        assertEquals(NYThread.POSTING, onEventTwoSH.subscribedThreadType);
        assertEquals("onEventTwo", onEventTwoSH.subscribedMethod.getName());

        SubscriberHolder onEventOneTwoSH = uniqueSubscriberHolderMap.get(onEventOneTwo);
        assertEquals(2, onEventOneTwoSH.subscribedChannelID.size());
        assertTrue(onEventOneTwoSH.subscribedChannelID.contains("one"));
        assertTrue(onEventOneTwoSH.subscribedChannelID.contains("two"));
        assertEquals(NYThread.POSTING, onEventOneTwoSH.subscribedThreadType);
        assertEquals("onEventOneTwo", onEventOneTwoSH.subscribedMethod.getName());
    }

    public static class SuperTestClass {

        @Subscribe
        public void onEventSuper(String value) {

        }
    }

    public static class SubTestClass extends SuperTestClass {

        @Subscribe(threadType = NYThread.COMPUTATION)
        public void onEvent(String value) {

        }

        @Subscribe(channelId = "one")
        public void onEventOne(String value) {

        }

        @Subscribe(channelId = "two")
        public void onEventTwo(String value) {

        }

        @Subscribe(channelId = {"one", "two"})
        public void onEventOneTwo(String value) {

        }

    }

}
