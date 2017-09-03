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

import com.mindorks.nybus.finder.EventClassFinder;
import com.mindorks.nybus.finder.NYEventClassFinder;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by amitshekhar on 03/09/17.
 */

public class NYEventClassFinderTest {

    private EventClassFinder eventClassFinder;

    @Before
    public void before() throws Exception {
        eventClassFinder = new NYEventClassFinder();
    }

    @Test
    public void testEventClass() throws Exception {
        List<Class<?>> classes = eventClassFinder.getAll(Event.class);
        assertEquals(2, classes.size());
        assertTrue(classes.contains(Event.class));
        assertTrue(classes.contains(Object.class));
    }

    @Test
    public void testSubEventClass() throws Exception {
        List<Class<?>> classes = eventClassFinder.getAll(SubEvent.class);
        assertEquals(3, classes.size());
        assertTrue(classes.contains(SubEvent.class));
        assertTrue(classes.contains(Event.class));
        assertTrue(classes.contains(Object.class));
    }

    @Test
    public void testEventInterfaceImpl() throws Exception {
        List<Class<?>> classes = eventClassFinder.getAll(InterfaceEventImpl.class);
        assertEquals(3, classes.size());
        assertTrue(classes.contains(InterfaceEventImpl.class));
        assertTrue(classes.contains(InterfaceEvent.class));
        assertTrue(classes.contains(Object.class));
    }


    public static class Event {

    }

    public static class SubEvent extends Event {

    }

    public interface InterfaceEvent {

    }

    public static class InterfaceEventImpl implements InterfaceEvent {

    }

}
