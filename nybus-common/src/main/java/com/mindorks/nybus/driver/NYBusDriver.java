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

package com.mindorks.nybus.driver;

import com.mindorks.nybus.annotation.Subscribe;
import com.mindorks.nybus.consumer.ConsumerProvider;
import com.mindorks.nybus.event.Event;
import com.mindorks.nybus.publisher.Publisher;
import com.mindorks.nybus.scheduler.SchedulerProvider;
import com.mindorks.nybus.subscriber.SubscriberHolder;
import com.mindorks.nybus.thread.NYThread;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by Jyoti on 14/08/17.
 */

public class NYBusDriver extends BusDriver {

    public NYBusDriver(Publisher publisher) {
        super(publisher);
    }

    public void initPublishers(SchedulerProvider schedulerProvider) {
        ConsumerProvider consumerProvider = new ConsumerProvider();
        consumerProvider.setPostingThreadConsumer(getConsumer());
        consumerProvider.setMainThreadConsumer(getConsumer());
        consumerProvider.setIOThreadConsumer(getConsumer());
        consumerProvider.setComputationThreadConsumer(getConsumer());
        consumerProvider.setTrampolineThreadConsumer(getConsumer());
        consumerProvider.setExecutorThreadConsumer(getConsumer());
        consumerProvider.setNewThreadConsumer(getConsumer());
        mPublisher.initPublishers(schedulerProvider, consumerProvider);
    }

    private Consumer<Event> getConsumer() {
        return new Consumer<Event>() {
            @Override
            public void accept(@NonNull Event event) throws Exception {
                deliverEventToTargetMethod(event.targetObject, event.subscribedMethod, event.object);
            }
        };
    }

    private void findTargetsAndDeliver(ConcurrentHashMap<Object, Set<SubscriberHolder>> mTargetMap,
                                       Object eventObject, String channelId) {
        for (Map.Entry<Object, Set<SubscriberHolder>> mTargetMapEntry :
                mTargetMap.entrySet()) {
            Set<SubscriberHolder> mSubscribedMethods = mTargetMapEntry.getValue();
            for (SubscriberHolder subscribedMethodHolder : mSubscribedMethods) {
                List<String> methodChannelId = subscribedMethodHolder.subscribedChannelID;
                if (methodChannelId.contains(channelId)) {
                    Event event = new Event(eventObject, mTargetMapEntry.getKey(),
                            subscribedMethodHolder);
                    determineThreadAndDeliverEvent(event);
                }
            }
        }
    }

    public void register(Object object, List<String> channelId) {
        List<SubscriberHolder> subscribeMethods =
                provideMethodsWithSubscribeAnnotation(object.getClass(), channelId);
        if (subscribeMethods.size() != 0) {
            for (SubscriberHolder subscribedMethod : subscribeMethods) {
                addEntriesInTargetMap(object, subscribedMethod);
            }
        }
    }

    public void post(Object eventObject, String channelId) {
        ConcurrentHashMap<Object, Set<SubscriberHolder>> mTargetMap = mEventsToTargetsMap.
                get(eventObject.getClass());
        if (mTargetMap != null) {
            findTargetsAndDeliver(mTargetMap, eventObject, channelId);
        }
    }

    private void determineThreadAndDeliverEvent(Event event) {
        final NYThread thread = event.subscribedMethod.subscribedThreadType;
        switch (thread) {
            case POSTING:
                getPostingThreadPublisher().onNext(event);
                break;
            case MAIN:
                getMainThreadPublisher().onNext(event);
                break;
            case IO:
                getIOThreadPublisher().onNext(event);
                break;
            case NEW:
                getNewThreadPublisher().onNext(event);
                break;
            case COMPUTATION:
                getComputationThreadPublisher().onNext(event);
                break;
            case TRAMPOLINE:
                getTrampolineThreadPublisher().onNext(event);
                break;
            case EXECUTOR:
                getExecutorThreadPublisher().onNext(event);
                break;
            default:
                getPostingThreadPublisher().onNext(event);
                break;
        }
    }

    public void unregister(Object targetObject, List<String> targetChannelId) {
        for (Map.Entry<Class<?>, ConcurrentHashMap<Object, Set<SubscriberHolder>>>
                mEventsToTargetsMapEntry :
                mEventsToTargetsMap.entrySet()) {
            ConcurrentHashMap<Object, Set<SubscriberHolder>> mTargetMap =
                    mEventsToTargetsMapEntry.getValue();
            if (mTargetMap != null) {
                for (Map.Entry<Object, Set<SubscriberHolder>> mTargetMapEntry : mTargetMap.entrySet()) {
                    if (mTargetMapEntry.getKey().equals(targetObject)) {
                        removeMethodFromCurrentMethodSet(mTargetMap, targetObject, targetChannelId);
                        removeEventIfRequired(mTargetMap, mEventsToTargetsMapEntry);
                    }
                }
            }
        }
    }

    private List<SubscriberHolder> provideMethodsWithSubscribeAnnotation(Class<?> subscriber,
                                                                         List<String> channelId) {
        List<SubscriberHolder> subscribeAnnotatedMethods = new ArrayList<>();
        Method[] declaredMethods = subscriber.getDeclaredMethods();
        for (Method method : declaredMethods) {
            boolean isMethodValid = hasSubscribeAnnotation(method) && isAccessModifierPublic(method)
                    && isReturnTypeVoid(method) && hasSingleParameter(method);
            if (isMethodValid) {
                SubscriberHolder subscriberHolder = generateSubscribedMethodHolder(method,
                        channelId);
                subscribeAnnotatedMethods.add(subscriberHolder);
            }
        }
        return subscribeAnnotatedMethods;
    }

    private void deliverEventToTargetMethod(Object targetObject,
                                            SubscriberHolder subscribeMethodHolder,
                                            Object eventObject) throws InvocationTargetException {
        try {
            Method method = subscribeMethodHolder.subscribedMethod;
            method.setAccessible(true);
            method.invoke(targetObject, eventObject);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void addEntriesInTargetMap(Object targetObject,
                                       SubscriberHolder subscribeMethodHolder) {
        if (mEventsToTargetsMap.containsKey(subscribeMethodHolder.
                subscribedMethod.getParameterTypes()[0])) {
            addOrUpdateMethodsInTargetMap(targetObject, subscribeMethodHolder);
        } else {
            createNewEventInEventsToTargetsMap(targetObject, subscribeMethodHolder);
        }
    }

    private List<String> getMethodChannelId(Method subscribeMethod) {
        Subscribe subscribeAnnotation = subscribeMethod.getAnnotation(Subscribe.class);
        return Arrays.asList(subscribeAnnotation.channelId());
    }

    private NYThread getMethodThread(Method subscribeMethod) {
        Subscribe subscribeAnnotation = subscribeMethod.getAnnotation(Subscribe.class);
        return subscribeAnnotation.threadType();
    }

    private void createNewEventInEventsToTargetsMap(Object targetObject,
                                                    SubscriberHolder subscribeMethodHolder) {
        ConcurrentHashMap<Object, Set<SubscriberHolder>> valuesForEventsToTargetsMap =
                new ConcurrentHashMap<>();
        Set<SubscriberHolder> methodSet = new HashSet<>();
        methodSet.add(subscribeMethodHolder);
        valuesForEventsToTargetsMap.put(targetObject, methodSet);
        mEventsToTargetsMap.put(subscribeMethodHolder.subscribedMethod.getParameterTypes()[0],
                valuesForEventsToTargetsMap);
    }

    private void addOrUpdateMethodsInTargetMap(Object targetObject,
                                               SubscriberHolder subscribeMethodHolder) {
        ConcurrentHashMap<Object, Set<SubscriberHolder>> mTargetMap =
                mEventsToTargetsMap.get(subscribeMethodHolder.subscribedMethod.
                        getParameterTypes()[0]);
        if (mTargetMap.containsKey(targetObject)) {
            updateMethodInSet(targetObject, subscribeMethodHolder, mTargetMap);
        } else {
            addEntryInTargetMap(targetObject, subscribeMethodHolder, mTargetMap);
        }
    }

    private void updateMethodInSet(Object targetObject,
                                   SubscriberHolder subscribeMethod,
                                   ConcurrentHashMap<Object, Set<SubscriberHolder>> mTargetMap) {
        Set<SubscriberHolder> methodSet = mTargetMap.get(targetObject);
        methodSet.add(subscribeMethod);
    }

    private void addEntryInTargetMap(Object targetObject,
                                     SubscriberHolder subscribeMethod,
                                     ConcurrentHashMap<Object, Set<SubscriberHolder>> mTargetMap) {
        Set<SubscriberHolder> methodSet = new HashSet<>();
        methodSet.add(subscribeMethod);
        mTargetMap.put(targetObject, methodSet);
    }

    private void removeMethodFromCurrentMethodSet(ConcurrentHashMap<Object,
            Set<SubscriberHolder>> mTargetMap, Object targetObject, List<String> targetChannelId) {
        Set<SubscriberHolder> subscribedMethods = mTargetMap.get(targetObject);
        Iterator subscribedMethodsIterator = subscribedMethods.iterator();
        while (subscribedMethodsIterator.hasNext()) {
            SubscriberHolder subscriberHolder = (SubscriberHolder) subscribedMethodsIterator.next();
            List<String> methodChannelId = subscriberHolder.subscribedChannelID;
            if (methodChannelId.containsAll(targetChannelId)) {
                subscribedMethodsIterator.remove();
                removeTargetIfRequired(subscribedMethods, mTargetMap, targetObject);
            }
        }
    }

    private void removeTargetIfRequired(Set<SubscriberHolder> subscribedMethods, ConcurrentHashMap<Object,
            Set<SubscriberHolder>> mTargetMap, Object targetObject) {
        if (subscribedMethods.size() == 0) {
            mTargetMap.remove(targetObject);
        }
    }

    private void removeEventIfRequired(ConcurrentHashMap<Object, Set<SubscriberHolder>> mTargetMap,
                                       Map.Entry<Class<?>,
                                               ConcurrentHashMap<Object, Set<SubscriberHolder>>>
                                               mEventsToTargetsMapEntry) {
        if (mTargetMap.size() == 0) {
            mEventsToTargetsMap.remove(mEventsToTargetsMapEntry.getKey());
        }
    }

    private boolean hasSubscribeAnnotation(Method method) {
        Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);
        return subscribeAnnotation != null;
    }

    private boolean isAccessModifierPublic(Method method) {
        return (method.getModifiers() & Modifier.PUBLIC) != 0;
    }

    private boolean isReturnTypeVoid(Method method) {
        return (method.getReturnType().equals(Void.TYPE));
    }

    private boolean hasSingleParameter(Method method) {
        return method.getParameterTypes().length == 1;
    }

    private SubscriberHolder generateSubscribedMethodHolder(Method method,
                                                            List<String> targetChannelId) {
        List<String> methodChannelIds = new ArrayList<>(getMethodChannelId(method));
        NYThread subscribedThreadType = getMethodThread(method);
        methodChannelIds.retainAll(targetChannelId);
        return new SubscriberHolder(method, methodChannelIds, subscribedThreadType);
    }

}

