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

package com.mindorks.nybus.finder;

import com.mindorks.nybus.annotation.Subscribe;
import com.mindorks.nybus.subscriber.SubscriberHolder;
import com.mindorks.nybus.thread.NYThread;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by amitshekhar on 28/08/17.
 */

/**
 * The implementation of {@link SubscribeMethodFinder}.
 */
public class NYSubscribeMethodFinder implements SubscribeMethodFinder {

    private static final String SEPARATOR = "_";

    public NYSubscribeMethodFinder() {

    }

    /**
     * Get Subscribed method.
     *
     * @param object          the target object.
     * @param targetChannelId the target channel ids.
     * @return the target data.
     */
    @Override
    public TargetData getData(Object object, List<String> targetChannelId) {
        HashMap<String, SubscriberHolder> uniqueSubscriberHolderMap = new HashMap<>();
        Set<String> methodChannelIDs = new HashSet<>();
        try {
            Method[] declaredMethodsOfConcreteClass = object.getClass().getDeclaredMethods();
            getAndAddSubscribeHolderToUniqueMap(declaredMethodsOfConcreteClass,
                    targetChannelId, methodChannelIDs, uniqueSubscriberHolderMap);
            Set<Class<?>> classes = getAllSuperClasses(object.getClass());
            for (Class<?> clazz : classes) {
                Method[] declaredMethods = clazz.getDeclaredMethods();
                getAndAddSubscribeHolderToUniqueMap(declaredMethods,
                        targetChannelId, methodChannelIDs, uniqueSubscriberHolderMap);
            }
        } catch (Throwable ignored) {
            // sometimes the above used getDeclaredMethods throw exception
            // in that case, use getMethods, as getMethods give all the methods
            // including the superclasses, so no need to call getAllSuperClasses
            Method[] declaredMethodsOfConcreteClass = object.getClass().getMethods();
            getAndAddSubscribeHolderToUniqueMap(declaredMethodsOfConcreteClass,
                    targetChannelId, methodChannelIDs, uniqueSubscriberHolderMap);
        }
        TargetData targetData = new TargetData();
        targetData.subscriberHolders = new ArrayList<>(uniqueSubscriberHolderMap.values());
        targetData.methodChannelIDs = methodChannelIDs;
        return targetData;
    }

    /**
     * Get and add subscribe holder to unique map.
     *
     * @param methods                   the methods.
     * @param targetChannelId           the target channel ids.
     * @param methodChannelIDs          the method channel ids.
     * @param uniqueSubscriberHolderMap the unique subscribe holder map.
     */
    private void getAndAddSubscribeHolderToUniqueMap(Method[] methods,
                                                     List<String> targetChannelId,
                                                     Set<String> methodChannelIDs,
                                                     HashMap<String, SubscriberHolder>
                                                             uniqueSubscriberHolderMap) {
        List<SubscriberHolder> subscriberHolders = new ArrayList<>();
        for (Method method : methods) {
            boolean isMethodValid = hasSubscribeAnnotation(method)
                    && isAccessModifierPublic(method)
                    && isReturnTypeVoid(method)
                    && hasSingleParameter(method);
            if (isMethodValid) {
                SubscriberHolder subscriberHolder = generateSubscribedMethodHolder(method,
                        targetChannelId);
                if (subscriberHolder != null) {
                    subscriberHolders.add(subscriberHolder);
                    addMethodChannelIDsToList(subscriberHolder, methodChannelIDs);
                }
            }
        }
        if (subscriberHolders.size() != 0) {
            for (SubscriberHolder subscriberHolder : subscriberHolders) {
                uniqueSubscriberHolderMap.put(getKeyForSubscribeHolder(subscriberHolder),
                        subscriberHolder);
            }
        }
    }

    /**
     * Provides unique key for {@link SubscriberHolder}.
     *
     * @return the unique key.
     */
    private String getKeyForSubscribeHolder(SubscriberHolder subscriberHolder) {
        return subscriberHolder.subscribedMethod.getName()
                + SEPARATOR
                + subscriberHolder.subscribedMethod.getParameterTypes()[0].toString();
    }

    /**
     * Get all the superclasses.
     *
     * @param concreteClass any concrete class.
     * @return the set of classes.
     */
    private Set<Class<?>> getAllSuperClasses(Class<?> concreteClass) {
        List<Class<?>> parentClasses = new LinkedList<>();
        Set<Class<?>> classes = new HashSet<>();
        parentClasses.add(concreteClass);
        while (!parentClasses.isEmpty()) {
            Class<?> clazz = parentClasses.remove(0);
            if (!concreteClass.equals(clazz)) {
                classes.add(clazz);
            }
            Class<?> parentClass = clazz.getSuperclass();
            if (parentClass != null && !skipClass(parentClass.getName())) {
                parentClasses.add(parentClass);
            }
        }
        return classes;
    }

    /**
     * Skip the class.
     *
     * @param className any class name.
     * @return is class skipped.
     */
    private boolean skipClass(String className) {
        return className.startsWith("java.")
                || className.startsWith("javax.")
                || className.startsWith("android.");
    }

    /**
     * Add methods channel ids to the list.
     *
     * @param subscriberHolder the subscriber holder.
     * @param channelIdSet     the set of channel ids.
     */
    private void addMethodChannelIDsToList(SubscriberHolder subscriberHolder, Set<String>
            channelIdSet) {
        for (String methodChannel : subscriberHolder.subscribedChannelID) {
            channelIdSet.add(methodChannel);
        }

    }

    /**
     * Check if method has subscribe annotation.
     *
     * @param method the method.
     * @return has subscribe annotation.
     */
    private boolean hasSubscribeAnnotation(Method method) {
        Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);
        return subscribeAnnotation != null;
    }

    /**
     * Check if access modifier is public.
     *
     * @param method the method name.
     * @return is access modifier public.
     */
    private boolean isAccessModifierPublic(Method method) {
        return (method.getModifiers() & Modifier.PUBLIC) != 0;
    }

    /**
     * Check if return type is void.
     *
     * @param method the method name.
     * @return is return type void.
     */
    private boolean isReturnTypeVoid(Method method) {
        return (method.getReturnType().equals(Void.TYPE));
    }

    /**
     * Check if method has single parameter.
     *
     * @param method the method name.
     * @return has single parameter.
     */
    private boolean hasSingleParameter(Method method) {
        return method.getParameterTypes().length == 1;
    }

    /**
     * Generate subscriber method holder.
     *
     * @param method          the method name.
     * @param targetChannelId the target channel ids.
     * @return the SubscriberHolder.
     */
    private SubscriberHolder generateSubscribedMethodHolder(Method method,
                                                            List<String> targetChannelId) {
        SubscriberHolder subscriberHolder;
        List<String> methodChannelIds = new ArrayList<>(getMethodChannelId(method));
        NYThread subscribedThreadType = getMethodThread(method);
        methodChannelIds.retainAll(targetChannelId);
        subscriberHolder = methodChannelIds.size() > 0 ? new SubscriberHolder(method,
                methodChannelIds,
                subscribedThreadType) : null;
        return subscriberHolder;
    }

    /**
     * Get method channel ids.
     *
     * @param subscribeMethod the subscribe method.
     * @return the list of string.
     */
    private List<String> getMethodChannelId(Method subscribeMethod) {
        Subscribe subscribeAnnotation = subscribeMethod.getAnnotation(Subscribe.class);
        return Arrays.asList(subscribeAnnotation.channelId());
    }

    /**
     * Get method thread.
     *
     * @param subscribeMethod the subscribe method.
     * @return the NYThread.
     */
    private NYThread getMethodThread(Method subscribeMethod) {
        Subscribe subscribeAnnotation = subscribeMethod.getAnnotation(Subscribe.class);
        return subscribeAnnotation.threadType();
    }

}
