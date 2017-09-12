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

import com.mindorks.nybus.consumer.ConsumerProvider;
import com.mindorks.nybus.event.NYEvent;
import com.mindorks.nybus.finder.EventClassFinder;
import com.mindorks.nybus.finder.SubscribeMethodFinder;
import com.mindorks.nybus.finder.TargetData;
import com.mindorks.nybus.logger.Logger;
import com.mindorks.nybus.publisher.Publisher;
import com.mindorks.nybus.scheduler.SchedulerProvider;
import com.mindorks.nybus.subscriber.SubscriberHolder;
import com.mindorks.nybus.thread.NYThread;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
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

    public NYBusDriver(Publisher publisher,
                       SubscribeMethodFinder subscribeMethodFinder,
                       EventClassFinder eventClassFinder,
                       Logger logger) {
        super(publisher, subscribeMethodFinder, eventClassFinder, logger);
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

    public void setLogger(Logger logger) {
        this.mLogger = logger;
    }

    public void register(Object object, List<String> targetChannelIds) {
        synchronized (this) {
            if (!isTargetRegistered(object, targetChannelIds)) {
                TargetData targetData = mSubscribeMethodFinder.getData(object, targetChannelIds);
                List<SubscriberHolder> subscriberHolders = targetData.subscriberHolders;
                Set<String> uniqueChannelIdHolderSet = targetData.methodChannelIDs;
                if (subscriberHolders.size() > 0) {
                    targetChannelIds.removeAll(uniqueChannelIdHolderSet);
                    if (targetChannelIds.size() > 0) {
                        for (String targetChannelId : targetChannelIds) {
                            mLogger.log("Subscriber " + object.getClass()
                                    + " and its super classes have no public methods with the " +
                                    "@Subscribe annotation on ChannelID " + targetChannelId);
                        }
                    }
                    for (SubscriberHolder subscriberHolder : subscriberHolders) {
                        addEntriesInTargetMap(object, subscriberHolder);
                    }
                } else {
                    mLogger.log("Subscriber " + object.getClass()
                            + " and its super classes have no public methods" +
                            " with the @Subscribe annotation");
                }

            } else {
                mLogger.log(object.getClass()
                        + " is already registered on same channel ids");
            }
        }
    }

    public void post(Object eventObject, String channelId) {
        boolean isAnyTargetRegistered = false;
        List<Class<?>> eventClasses = mEventClassFinder.getAll(eventObject.getClass());
        for (Class<?> eventClass : eventClasses) {
            boolean hasPostedSingle = postSingle(eventObject, channelId, eventClass);
            if (hasPostedSingle) {
                isAnyTargetRegistered = true;
            }
        }
        if (!isAnyTargetRegistered) {
            mLogger.log("No target found for the event" + eventObject.getClass());
        }
    }

    public boolean isRegistered(Object targetObject, List<String> targetChannelId) {
        boolean isRegistered = false;
        for (Map.Entry<Class<?>, ConcurrentHashMap<Object, ConcurrentHashMap<String, SubscriberHolder>>>
                mEventsToTargetsMapEntry : mEventsToTargetsMap.entrySet()) {
            ConcurrentHashMap<Object, ConcurrentHashMap<String, SubscriberHolder>> mTargetMap =
                    mEventsToTargetsMapEntry.getValue();
            if (mTargetMap != null) {
                for (Map.Entry<Object, ConcurrentHashMap<String, SubscriberHolder>> mTargetMapEntry :
                        mTargetMap.entrySet()) {
                    if (mTargetMapEntry.getKey().equals(targetObject)) {
                        isRegistered = getMethodChannelIds(mTargetMapEntry).containsAll
                                (targetChannelId);
                    }
                }
            }
        }
        return isRegistered;
    }

    private boolean isTargetRegistered(Object targetObject, List<String> targetChannelId) {
        Set<String> currentlyRegisteredChannelId = new HashSet<>();
        for (Map.Entry<Class<?>, ConcurrentHashMap<Object, ConcurrentHashMap<String,
                SubscriberHolder>>> mEventsToTargetsMapEntry : mEventsToTargetsMap.entrySet()) {
            ConcurrentHashMap<Object, ConcurrentHashMap<String, SubscriberHolder>> mTargetMap =
                    mEventsToTargetsMapEntry.getValue();
            if (mTargetMap.containsKey(targetObject)) {
                ConcurrentHashMap<String, SubscriberHolder> subscribeMethods = mTargetMap.get
                        (targetObject);
                for (Map.Entry<String, SubscriberHolder> subscribeMethodEntry : subscribeMethods.entrySet()) {
                    for (String methodChannelID : subscribeMethodEntry.getValue().subscribedChannelID) {
                        currentlyRegisteredChannelId.add(methodChannelID);

                    }
                }
            }
        }
        return currentlyRegisteredChannelId.size() > 0 && currentlyRegisteredChannelId.containsAll(targetChannelId);
    }


    private Set<String> getMethodChannelIds(Map.Entry<Object, ConcurrentHashMap<String, SubscriberHolder>>
                                                    mTargetMapEntry) {
        Set<String> methodChannelIDSet = new HashSet<>();
        ConcurrentHashMap<String, SubscriberHolder> subscribedMethods = mTargetMapEntry.getValue();
        for (Map.Entry<String, SubscriberHolder> subscribedMethodsEntry : subscribedMethods
                .entrySet()) {
            List<String> subscribedChannelIDs = subscribedMethodsEntry.getValue().subscribedChannelID;
            for (String channelId : subscribedChannelIDs) {
                methodChannelIDSet.add(channelId);
            }
        }
        return methodChannelIDSet;
    }


    public void unregister(Object targetObject, List<String> targetChannelId) {
        synchronized (this) {
            if (isTargetRegistered(targetObject, targetChannelId)) {
                for (Map.Entry<Class<?>, ConcurrentHashMap<Object, ConcurrentHashMap<String, SubscriberHolder>>>
                        mEventsToTargetsMapEntry : mEventsToTargetsMap.entrySet()) {
                    ConcurrentHashMap<Object, ConcurrentHashMap<String, SubscriberHolder>> mTargetMap =
                            mEventsToTargetsMapEntry.getValue();
                    if (mTargetMap != null) {
                        for (Map.Entry<Object, ConcurrentHashMap<String, SubscriberHolder>> mTargetMapEntry :
                                mTargetMap.entrySet()) {
                            if (mTargetMapEntry.getKey().equals(targetObject)) {
                                removeMethodFromMethodsMap(mTargetMap, targetObject, targetChannelId);
                                removeEventIfRequired(mTargetMap, mEventsToTargetsMapEntry);
                            }
                        }
                    }
                }
            } else {
                mLogger.log(targetObject.getClass()
                        + " is either not subscribed(on some channel ID you wish to unregister " +
                        "from) " +
                        "or has " +
                        "already been " +
                        "unregistered");
            }
        }
    }

    private boolean postSingle(Object eventObject, String channelId, Class<?> eventClass) {
        boolean hasDelivered = false;
        ConcurrentHashMap<Object, ConcurrentHashMap<String, SubscriberHolder>> mTargetMap =
                mEventsToTargetsMap.get(eventClass);
        if (mTargetMap != null) {
            hasDelivered = true;
            findTargetsAndDeliver(mTargetMap, eventObject, channelId);
        }
        return hasDelivered;
    }

    private Consumer<NYEvent> getConsumer() {
        return new Consumer<NYEvent>() {
            @Override
            public void accept(@NonNull NYEvent event) throws Exception {
                deliverEventToTargetMethod(event);

            }
        };
    }

    private void determineThreadAndDeliverEvent(NYEvent event) {
        synchronized (DELIVER_LOCK) {
            final NYThread thread = event.subscriberHolder.subscribedThreadType;
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
    }

    private void findTargetsAndDeliver(ConcurrentHashMap<Object,
            ConcurrentHashMap<String, SubscriberHolder>> mTargetMap,
                                       Object eventObject, String channelId) {
        boolean isTargetAvailable = false;
        for (Map.Entry<Object, ConcurrentHashMap<String, SubscriberHolder>> mTargetMapEntry :
                mTargetMap.entrySet()) {
            ConcurrentHashMap<String, SubscriberHolder> mSubscribedMethods =
                    new ConcurrentHashMap<>(mTargetMapEntry.getValue());
            for (Map.Entry<String, SubscriberHolder> subscribedMethodHolder : mSubscribedMethods.entrySet()) {
                List<String> methodChannelId = subscribedMethodHolder.getValue().subscribedChannelID;
                if (methodChannelId.contains(channelId)) {
                    isTargetAvailable = true;
                    NYEvent event = new NYEvent(eventObject, mTargetMapEntry.getKey(),
                            subscribedMethodHolder.getValue());
                    determineThreadAndDeliverEvent(event);
                }
            }
        }
        if (!isTargetAvailable) {
            mLogger.log("No target found for the event" +
                    eventObject.getClass() + " on channel ID" + channelId);
        }
    }

    private void deliverEventToTargetMethod(NYEvent event) {
        try {
            Method method = event.subscriberHolder.subscribedMethod;
            method.setAccessible(true);
            method.invoke(event.targetObject, event.eventObject);
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
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

    private void createNewEventInEventsToTargetsMap(Object targetObject,
                                                    SubscriberHolder subscribeMethodHolder) {
        ConcurrentHashMap<Object, ConcurrentHashMap<String, SubscriberHolder>>
                valuesForEventsToTargetsMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, SubscriberHolder> methodSet = new ConcurrentHashMap<>();
        methodSet.put(Integer.toString(subscribeMethodHolder.hashCode()), subscribeMethodHolder);
        valuesForEventsToTargetsMap.put(targetObject, methodSet);
        mEventsToTargetsMap.put(subscribeMethodHolder.subscribedMethod.getParameterTypes()[0],
                valuesForEventsToTargetsMap);
    }

    private void addOrUpdateMethodsInTargetMap(Object targetObject,
                                               SubscriberHolder subscribeMethodHolder) {
        ConcurrentHashMap<Object, ConcurrentHashMap<String, SubscriberHolder>> mTargetMap =
                mEventsToTargetsMap.get(subscribeMethodHolder.subscribedMethod.
                        getParameterTypes()[0]);
        if (mTargetMap != null) {
            if (mTargetMap.containsKey(targetObject)) {
                updateMethodInSet(targetObject, subscribeMethodHolder, mTargetMap);
            } else {
                addEntryInTargetMap(targetObject, subscribeMethodHolder, mTargetMap);
            }
        }
    }

    private void updateMethodInSet(Object targetObject,
                                   SubscriberHolder subscribeMethod,
                                   ConcurrentHashMap<Object, ConcurrentHashMap<String,
                                           SubscriberHolder>> mTargetMap) {
        ConcurrentHashMap<String, SubscriberHolder> methodSet = mTargetMap.get(targetObject);
        methodSet.put(subscribeMethod.getKeyForSubscribeHolderMap(subscribeMethod), subscribeMethod);
    }

    private void addEntryInTargetMap(Object targetObject,
                                     SubscriberHolder subscribeMethod,
                                     ConcurrentHashMap<Object, ConcurrentHashMap<String,
                                             SubscriberHolder>> mTargetMap) {
        ConcurrentHashMap<String, SubscriberHolder> methodSet = new ConcurrentHashMap<>();
        methodSet.put(subscribeMethod.getKeyForSubscribeHolderMap(subscribeMethod), subscribeMethod);
        mTargetMap.put(targetObject, methodSet);
    }

    private void removeMethodFromMethodsMap(ConcurrentHashMap<Object,
            ConcurrentHashMap<String, SubscriberHolder>> mTargetMap,
                                            Object targetObject,
                                            List<String> targetChannelId) {
        ConcurrentHashMap<String, SubscriberHolder> mSubscribedMethodsMap =
                mTargetMap.get(targetObject);
        for (Map.Entry<String, SubscriberHolder> mSubscribedMethodsMapEntry :
                mSubscribedMethodsMap.entrySet()) {
            SubscriberHolder subscribedMethod = mSubscribedMethodsMapEntry.getValue();
            List<String> methodChannelId = subscribedMethod.subscribedChannelID;
            if (targetChannelId.containsAll(methodChannelId)) {
                mSubscribedMethodsMap.remove(mSubscribedMethodsMapEntry.getKey());
                removeTargetIfRequired(mSubscribedMethodsMap, mTargetMap, targetObject);
            }
        }
    }

    private void removeTargetIfRequired(ConcurrentHashMap<String, SubscriberHolder> subscribedMethods,
                                        ConcurrentHashMap<Object,
                                                ConcurrentHashMap<String, SubscriberHolder>> mTargetMap,
                                        Object targetObject) {
        if (subscribedMethods.size() == 0) {
            mTargetMap.remove(targetObject);
        }
    }

    private void removeEventIfRequired(ConcurrentHashMap<Object,
            ConcurrentHashMap<String, SubscriberHolder>> mTargetMap,
                                       Map.Entry<Class<?>, ConcurrentHashMap<Object,
                                               ConcurrentHashMap<String,
                                                       SubscriberHolder>>> mEventsToTargetsMapEntry) {
        if (mTargetMap.size() == 0) {
            mEventsToTargetsMap.remove(mEventsToTargetsMapEntry.getKey());
        }
    }

}

