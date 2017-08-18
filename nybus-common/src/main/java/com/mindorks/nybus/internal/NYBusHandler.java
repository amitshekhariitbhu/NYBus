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

package com.mindorks.nybus.internal;

import com.mindorks.nybus.annotation.Subscribe;
import com.mindorks.nybus.event.Event;
import com.mindorks.nybus.scheduler.SchedulerProvider;


import io.reactivex.annotations.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.openmbean.ArrayType;

import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.functions.Consumer;


/**
 * Created by Jyoti on 14/08/17.
 */

public class NYBusHandler {
    private SchedulerProvider mSchedulerProvider;
    private ConcurrentHashMap<Class<?>, Set<SubscribeMethodHolder>> mTargetToMethodsMap;
    private ConcurrentHashMap<Class<?>, Set<Class<?>>> mEventsToTargetsMap;
    private PublishSubject<Event> subject;


    public NYBusHandler() {
        mTargetToMethodsMap = new ConcurrentHashMap<>();
        mEventsToTargetsMap = new ConcurrentHashMap<>();
        subject = PublishSubject.create();
        subject.toFlowable(BackpressureStrategy.BUFFER)
                .subscribe(new Consumer<Event>() {
                    @Override
                    public void accept(@NonNull Event event) throws Exception {
                        Set<Class<?>> targets = mEventsToTargetsMap.get(event.object.getClass());
                        for (Object target : targets) {
                            Set<SubscribeMethodHolder> subscribeMethodHolders = mTargetToMethodsMap.get(target);
                            for (SubscribeMethodHolder methodHolder : subscribeMethodHolders) {
                                if (methodHolder.getSubscribedEventType().isInstance(event.object)) {
                                    deliver(methodHolder, event.object);

                                }
                            }

                        }
                    }


                });

    }

    public void setSchedulerProvider(SchedulerProvider mSchedulerProvider) {
        this.mSchedulerProvider = mSchedulerProvider;
    }

    public void register(Object object, String channelId) {
        List<Method> subscribeAnnotatedMethods = provideMethodsWithSubscribeAnnotation(object.getClass());
        if (subscribeAnnotatedMethods.size() != 0) {
            for (Method method : subscribeAnnotatedMethods) {
                SubscribeMethodHolder subscribeMethodHolder = new SubscribeMethodHolder(object, method, method.getParameterTypes()[0], channelId);
                addEnteriesInTargetMap(object, subscribeMethodHolder);
                addEnteriesInEventMap(object, method);
            }

        }
    }

    public void post(Object eventObject, String channelId) {
        subject.onNext(new Event(eventObject, channelId));
    }


    public void unregister(Object object, String channelId) {
        removeTargetKeyFromTargetMap(object);
        removeTargetEntryFromEventsMap(object);


    }


    /**
     * TODO Add all constraints for @Subscribe method
     * - Should be public
     * - return type should be void
     * - Single parameter
     **/

    private List<Method> provideMethodsWithSubscribeAnnotation(Class<?> subscriber) {
        List<Method> subscribeAnnotatedMethods = new ArrayList<>();
        Method[] declaredMethods = subscriber.getDeclaredMethods();
        for (Method method : declaredMethods) {
            Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);
            if (subscribeAnnotation != null && (method.getModifiers() & Modifier.PUBLIC) != 0) {
                subscribeAnnotatedMethods.add(method);
            }
        }
        return subscribeAnnotatedMethods;

    }

    private void deliver(SubscribeMethodHolder subscribeMethodHolder, Object subcriberClass) {
        try {
            Method method = subscribeMethodHolder.getSubscribedMethod();
            method.setAccessible(true);
            method.invoke(subscribeMethodHolder.getSubscriberTarget(), subcriberClass);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private void addEnteriesInTargetMap(Object subcriberClass, SubscribeMethodHolder subscribeMethodHolder) {
        if (mTargetToMethodsMap.containsKey(subcriberClass.getClass())) {
            Set<SubscribeMethodHolder> subscribeMethodHolders = mTargetToMethodsMap.get(subcriberClass.getClass());
            subscribeMethodHolders.add(subscribeMethodHolder);
            mTargetToMethodsMap.put(subcriberClass.getClass(), subscribeMethodHolders);
        } else {
            Set<SubscribeMethodHolder> subscribeMethodHolders = new HashSet<>();
            subscribeMethodHolders.add(subscribeMethodHolder);
            mTargetToMethodsMap.put(subcriberClass.getClass(), subscribeMethodHolders);
        }

    }

    private void addEnteriesInEventMap(Object subcriberClass, Method method) {
        if (mEventsToTargetsMap.containsKey(method.getParameterTypes()[0])) {
            Set<Class<?>> subscribedTargets = mEventsToTargetsMap.get(method.getParameterTypes()[0]);
            subscribedTargets.add(subcriberClass.getClass());
            mEventsToTargetsMap.put(method.getParameterTypes()[0], subscribedTargets);
        } else {
            Set<Class<?>> subscribedTargets = new HashSet<>();
            subscribedTargets.add(subcriberClass.getClass());
            mEventsToTargetsMap.put(method.getParameterTypes()[0], subscribedTargets);
        }
    }

    private void removeTargetEntryFromEventsMap(Object subcriberClass) {
        for (Map.Entry<Class<?>, Set<Class<?>>> event : mEventsToTargetsMap.entrySet()) {
            Set<Class<?>> targets = mEventsToTargetsMap.get(event.getKey());
            for (Class<?> target : targets) {
                if (target.isInstance(subcriberClass)) {
                    mEventsToTargetsMap.get(event.getKey()).remove(target);
                }
            }
        }

    }

    private void removeTargetKeyFromTargetMap(Object object) {
        mTargetToMethodsMap.remove(object.getClass());
    }


}

