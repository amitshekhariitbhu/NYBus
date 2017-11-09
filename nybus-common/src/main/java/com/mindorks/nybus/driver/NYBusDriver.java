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

/**
 * The class responsible for posting, registering and un-registering.
 */
public class NYBusDriver extends BusDriver {

    public NYBusDriver(Publisher publisher,
                       SubscribeMethodFinder subscribeMethodFinder,
                       EventClassFinder eventClassFinder,
                       Logger logger) {
        super(publisher, subscribeMethodFinder, eventClassFinder, logger);
    }

    /**
     * Initialize the publisher with scheduler provider.
     *
     * @param schedulerProvider the scheduler provider.
     */
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

    /**
     * Set the logger.
     *
     * @param logger the logger.
     */
    public void setLogger(Logger logger) {
        this.mLogger = logger;
    }

    /**
     * Enable logging.
     */
    public void enableLogging() throws IllegalAccessException {
        if (this.mLogger == null) {
            throw new IllegalAccessException("Logger is null");
        }

        this.log = true;
    }

    /**
     * Register for the event.
     *
     * @param object           the object.
     * @param targetChannelIds the target channel ids.
     */
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
                            if (log) {
                                mLogger.log("Subscriber " + object.getClass()
                                        + " and its super classes have no public methods with the " +
                                        "@Subscribe annotation on ChannelID " + targetChannelId);
                            }
                        }
                    }
                    for (SubscriberHolder subscriberHolder : subscriberHolders) {
                        addEntriesInTargetMap(object, subscriberHolder);
                    }
                } else {
                    if (log) {
                        mLogger.log("Subscriber " + object.getClass()
                                + " and its super classes have no public methods" +
                                " with the @Subscribe annotation");
                    }
                }

            } else {
                if (log) {
                    mLogger.log(object.getClass()
                            + " is already registered on same channel ids");
                }
            }
        }
    }

    /**
     * Post the event.
     *
     * @param eventObject the event object.
     * @param channelId   the channel ids.
     */
    public void post(Object eventObject, String channelId) {
        boolean isAnyTargetRegistered = false;
        List<Class<?>> eventClasses = mEventClassFinder.getAll(eventObject.getClass());
        for (Class<?> eventClass : eventClasses) {
            boolean hasPostedSingle = postSingle(eventObject, channelId, eventClass);
            if (hasPostedSingle) {
                isAnyTargetRegistered = true;
            }
        }
        if (!isAnyTargetRegistered && log) {
            mLogger.log("No target found for the event" + eventObject.getClass());
        }
    }

    /**
     * Check if event registered.
     *
     * @param targetObject    the target object.
     * @param targetChannelId the target channel id.
     * @return is registered.
     */
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

    /**
     * Is target registered.
     *
     * @param targetObject    the target object.
     * @param targetChannelId the target channel id.
     * @return is target registered.
     */
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


    /**
     * Get method channel ids.
     *
     * @param mTargetMapEntry the target map entry.
     * @return the set of string.
     */
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


    /**
     * Unregister from the event.
     *
     * @param targetObject    the target object.
     * @param targetChannelId the target channel ids.
     */
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
                if (log) {
                    mLogger.log(targetObject.getClass()
                            + " is either not subscribed(on some channel ID you wish to unregister " +
                            "from) " +
                            "or has " +
                            "already been " +
                            "unregistered");
                }
            }
        }
    }

    /**
     * Post Single event.
     *
     * @param eventObject the event object.
     * @param channelId   the channel ids.
     * @param eventClass  the event class.
     * @return has delivered.
     */
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

    /**
     * Get consumer.
     *
     * @return the consumers.
     */
    private Consumer<NYEvent> getConsumer() {
        return new Consumer<NYEvent>() {
            @Override
            public void accept(@NonNull NYEvent event) throws Exception {
                deliverEventToTargetMethod(event);

            }
        };
    }

    /**
     * Determine thread and deliver event.
     *
     * @param event the event.
     */
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

    /**
     * Find the target and deliver.
     *
     * @param mTargetMap  the target map.
     * @param eventObject the event object.
     * @param channelId   the channel id.
     */
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
        if (!isTargetAvailable && log) {
            mLogger.log("No method found for the event" +
                    eventObject.getClass() + " on channel ID" + channelId);
        }
    }

    /**
     * Deliver event to target method.
     *
     * @param event the event.
     */
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

    /**
     * Add to entries in target map.
     *
     * @param targetObject          the target object.
     * @param subscribeMethodHolder the subscribeMethodHolder.
     */
    private void addEntriesInTargetMap(Object targetObject,
                                       SubscriberHolder subscribeMethodHolder) {
        if (mEventsToTargetsMap.containsKey(subscribeMethodHolder.
                subscribedMethod.getParameterTypes()[0])) {
            addOrUpdateMethodsInTargetMap(targetObject, subscribeMethodHolder);
        } else {
            createNewEventInEventsToTargetsMap(targetObject, subscribeMethodHolder);
        }
    }

    /**
     * Create new event in events to targets map.
     *
     * @param targetObject          the target object.
     * @param subscribeMethodHolder the subscribeMethodHolder.
     */
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

    /**
     * Add methods in target map.
     *
     * @param targetObject          the target object.
     * @param subscribeMethodHolder the subscribeMethodHolder.
     */
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

    /**
     * Update methods in set.
     *
     * @param targetObject    the target object.
     * @param subscribeMethod the subscribe method.
     * @param mTargetMap      the target map.
     */
    private void updateMethodInSet(Object targetObject,
                                   SubscriberHolder subscribeMethod,
                                   ConcurrentHashMap<Object, ConcurrentHashMap<String,
                                           SubscriberHolder>> mTargetMap) {
        ConcurrentHashMap<String, SubscriberHolder> methodSet = mTargetMap.get(targetObject);
        methodSet.put(subscribeMethod.getKeyForSubscribeHolderMap(), subscribeMethod);
    }

    /**
     * Add entry in target map.
     *
     * @param targetObject    the target object.
     * @param subscribeMethod the subscribe method.
     * @param mTargetMap      the target map.
     */
    private void addEntryInTargetMap(Object targetObject,
                                     SubscriberHolder subscribeMethod,
                                     ConcurrentHashMap<Object, ConcurrentHashMap<String,
                                             SubscriberHolder>> mTargetMap) {
        ConcurrentHashMap<String, SubscriberHolder> methodSet = new ConcurrentHashMap<>();
        methodSet.put(subscribeMethod.getKeyForSubscribeHolderMap(), subscribeMethod);
        mTargetMap.put(targetObject, methodSet);
    }

    /**
     * Remove method from methods map.
     *
     * @param mTargetMap      the target map.
     * @param targetObject    the target object.
     * @param targetChannelId the target channel ids.
     */
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

    /**
     * Remove the target.
     *
     * @param subscribedMethods the subscribed methods.
     * @param mTargetMap        the target map.
     * @param targetObject      the target object.
     */
    private void removeTargetIfRequired(ConcurrentHashMap<String, SubscriberHolder> subscribedMethods,
                                        ConcurrentHashMap<Object,
                                                ConcurrentHashMap<String, SubscriberHolder>> mTargetMap,
                                        Object targetObject) {
        if (subscribedMethods.size() == 0) {
            mTargetMap.remove(targetObject);
        }
    }

    /**
     * Remove the event.
     *
     * @param mTargetMap               the target map.
     * @param mEventsToTargetsMapEntry the event to target map entry.
     */
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

