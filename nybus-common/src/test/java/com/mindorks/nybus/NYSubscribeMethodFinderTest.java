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
import com.mindorks.nybus.event.Channel;
import com.mindorks.nybus.finder.NYSubscribeMethodFinder;
import com.mindorks.nybus.finder.SubscribeMethodFinder;
import com.mindorks.nybus.finder.TargetData;
import com.mindorks.nybus.subscriber.SubscriberHolder;
import com.mindorks.nybus.thread.NYThread;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

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
        List<String> channels = Arrays.asList("one", "two", Channel.DEFAULT);
        TargetData targetData = subscribeMethodFinder.getData(subTestClass, channels);
        List<SubscriberHolder> subscriberHolders = targetData.subscriberHolders;
        Set<String> methodChannelIDs = targetData.methodChannelIDs;
        assertEquals(5, subscriberHolders.size());
        assertEquals(3, methodChannelIDs.size());

        SubscriberHolder onEventOneSH = getSubscriberHolder(subscriberHolders, "onEventOne");
        if (onEventOneSH != null) {
            assertEquals(1, onEventOneSH.subscribedChannelID.size());
            assertTrue(onEventOneSH.subscribedChannelID.contains("one"));
            assertEquals(NYThread.POSTING, onEventOneSH.subscribedThreadType);
            assertEquals("onEventOne", onEventOneSH.subscribedMethod.getName());
        } else {
            fail("onEventOneSH should not be null");
        }

        SubscriberHolder onEventSuperSH = getSubscriberHolder(subscriberHolders, "onEventSuper");
        if (onEventSuperSH != null) {
            assertEquals(1, onEventSuperSH.subscribedChannelID.size());
            assertTrue(onEventSuperSH.subscribedChannelID.contains(Channel.DEFAULT));
            assertEquals(NYThread.POSTING, onEventSuperSH.subscribedThreadType);
            assertEquals("onEventSuper", onEventSuperSH.subscribedMethod.getName());
        } else {
            fail("onEventSuperSH should not be null");
        }

        SubscriberHolder onEventSH = getSubscriberHolder(subscriberHolders, "onEvent");
        if (onEventSH != null) {
            assertEquals(1, onEventSH.subscribedChannelID.size());
            assertTrue(onEventSH.subscribedChannelID.contains(Channel.DEFAULT));
            assertEquals(NYThread.COMPUTATION, onEventSH.subscribedThreadType);
            assertEquals("onEvent", onEventSH.subscribedMethod.getName());
        } else {
            fail("onEventSH should not be null");
        }

        SubscriberHolder onEventTwoSH = getSubscriberHolder(subscriberHolders, "onEventTwo");
        if (onEventTwoSH != null) {
            assertEquals(1, onEventTwoSH.subscribedChannelID.size());
            assertTrue(onEventTwoSH.subscribedChannelID.contains("two"));
            assertEquals(NYThread.POSTING, onEventTwoSH.subscribedThreadType);
            assertEquals("onEventTwo", onEventTwoSH.subscribedMethod.getName());
        } else {
            fail("onEventTwoSH should not be null");
        }

        SubscriberHolder onEventOneTwoSH = getSubscriberHolder(subscriberHolders, "onEventOneTwo");
        if (onEventOneTwoSH != null) {
            assertEquals(2, onEventOneTwoSH.subscribedChannelID.size());
            assertTrue(onEventOneTwoSH.subscribedChannelID.contains("one"));
            assertTrue(onEventOneTwoSH.subscribedChannelID.contains("two"));
            assertEquals(NYThread.POSTING, onEventOneTwoSH.subscribedThreadType);
            assertEquals("onEventOneTwo", onEventOneTwoSH.subscribedMethod.getName());
        } else {
            fail("onEventOneTwoSH should not be null");
        }

    }

    @Test
    public void testSubTestClassMethodWithoutDefaultChannel() throws Exception {
        SubTestClass subTestClass = new SubTestClass();
        List<String> channels = Arrays.asList("one", "two");
        TargetData targetData = subscribeMethodFinder.getData(subTestClass, channels);
        List<SubscriberHolder> subscriberHolders = targetData.subscriberHolders;
        Set<String> methodChannelIDs = targetData.methodChannelIDs;
        assertEquals(3, subscriberHolders.size());
        assertEquals(2, methodChannelIDs.size());

        SubscriberHolder onEventOneSH = getSubscriberHolder(subscriberHolders, "onEventOne");
        if (onEventOneSH != null) {
            assertEquals(1, onEventOneSH.subscribedChannelID.size());
            assertTrue(onEventOneSH.subscribedChannelID.contains("one"));
            assertEquals(NYThread.POSTING, onEventOneSH.subscribedThreadType);
            assertEquals("onEventOne", onEventOneSH.subscribedMethod.getName());
        } else {
            fail("onEventOneSH should not be null");
        }

        SubscriberHolder onEventTwoSH = getSubscriberHolder(subscriberHolders, "onEventTwo");
        if (onEventTwoSH != null) {
            assertEquals(1, onEventTwoSH.subscribedChannelID.size());
            assertTrue(onEventTwoSH.subscribedChannelID.contains("two"));
            assertEquals(NYThread.POSTING, onEventTwoSH.subscribedThreadType);
            assertEquals("onEventTwo", onEventTwoSH.subscribedMethod.getName());
        } else {
            fail("onEventTwoSH should not be null");
        }

        SubscriberHolder onEventOneTwoSH = getSubscriberHolder(subscriberHolders, "onEventOneTwo");
        if (onEventOneTwoSH != null) {
            assertEquals(2, onEventOneTwoSH.subscribedChannelID.size());
            assertTrue(onEventOneTwoSH.subscribedChannelID.contains("one"));
            assertTrue(onEventOneTwoSH.subscribedChannelID.contains("two"));
            assertEquals(NYThread.POSTING, onEventOneTwoSH.subscribedThreadType);
            assertEquals("onEventOneTwo", onEventOneTwoSH.subscribedMethod.getName());
        } else {
            fail("onEventOneTwoSH should not be null");
        }
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

    private SubscriberHolder getSubscriberHolder(List<SubscriberHolder> subscriberHolders,
                                                 String methodName) {
        for (SubscriberHolder subscriberHolder : subscriberHolders) {
            if (subscriberHolder.subscribedMethod.getName().equals(methodName)) {
                return subscriberHolder;
            }
        }
        return null;
    }

}
