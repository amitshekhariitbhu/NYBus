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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.functions.Consumer;


/**
 * Created by Jyoti on 14/08/17.
 */

public class NYBusHandler {
    private SchedulerProvider mSchedulerProvider;
    private ConcurrentHashMap<Class<?>, HashMap<Object, Set<Method>>> mEventsToTargetsMap;
    private PublishSubject<Event> subject;


    public NYBusHandler() {
        mEventsToTargetsMap = new ConcurrentHashMap<>();
        subject = PublishSubject.create();
        subject.toFlowable(BackpressureStrategy.BUFFER)
                .subscribe(new Consumer<Event>() {
                    @Override
                    public void accept(@NonNull Event event) throws Exception {
                        HashMap<Object, Set<Method>> targets = mEventsToTargetsMap.
                                get(event.object.getClass());
                        if (targets != null) {
                            int count = 0;
                            String methodChannelId;

                            for (Object key : targets.keySet()) {

                                Set<Method> methods = targets.get(key);

                                for (Method method : methods) {
                                    /**
                                     * TODO : Get method channelid from annotations.
                                     * TODO: Remove count after annotations processing is done.
                                     */
                                    count++;
                                    if (count == 1) {
                                        methodChannelId = "one";
                                    } else {
                                        methodChannelId = "two";

                                    }
                                    if (methodChannelId.equals(event.channelId)) {
                                        deliver(key, method, event.object);
                                    }

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
        int count = 0;
        List<Method> subscribeAnnotatedMethods =
                provideMethodsWithSubscribeAnnotation(object.getClass());
        if (subscribeAnnotatedMethods.size() != 0) {
            for (Method method : subscribeAnnotatedMethods) {
                count++;
                addEntriesInTargetMap(count, object, method, channelId);
            }

        }
    }

    public void post(Object eventObject, String channelId) {
        subject.onNext(new Event(eventObject, channelId));
    }


    public void unregister(Object object, String channelId) {


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

    private void deliver(Object targetObject, Method subscribeMethod, Object eventObject) {
        try {
            Method method = subscribeMethod;
            method.setAccessible(true);
            method.invoke(targetObject, eventObject);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }


    private void addEntriesInTargetMap(int count, Object subscriberClassObject,
                                       Method subscribeMethodHolder, String channelId) {
        String methodChannelId;
        /**
         * TODO : Get method channelid from annotations.
         * TODO: Remove count after annotations processing is done.
         */
        if (count == 1) {
            methodChannelId = "one";
        } else {
            methodChannelId = "two";

        }
        if (methodChannelId.equals(channelId)) {
            if (mEventsToTargetsMap.containsKey(subscribeMethodHolder.getParameterTypes()[0])) {
                HashMap<Object, Set<Method>> valuesInEventsToTargetsMap =
                        mEventsToTargetsMap.get(subscribeMethodHolder.getParameterTypes()[0]);
                if (valuesInEventsToTargetsMap.containsKey(subscriberClassObject)) {
                    Set<Method> methodSet = valuesInEventsToTargetsMap.get(subscriberClassObject);
                    methodSet.add(subscribeMethodHolder);
                } else {
                    Set<Method> methodSet = new HashSet<>();
                    methodSet.add(subscribeMethodHolder);
                    valuesInEventsToTargetsMap.put(subscriberClassObject, methodSet);

                }
            } else {
                HashMap<Object, Set<Method>> valuesForEventsToTargetsMap = new HashMap<>();
                Set<Method> methodSet = new HashSet<>();
                methodSet.add(subscribeMethodHolder);
                valuesForEventsToTargetsMap.put(subscriberClassObject, methodSet);
                mEventsToTargetsMap.put(subscribeMethodHolder.getParameterTypes()[0],
                        valuesForEventsToTargetsMap);
            }

        }


    }


}

