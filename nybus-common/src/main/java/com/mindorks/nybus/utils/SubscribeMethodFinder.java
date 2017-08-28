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

package com.mindorks.nybus.utils;

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

public class SubscribeMethodFinder {

    private SubscribeMethodFinder() {
        // no instance
    }

    public static HashMap<String, SubscriberHolder> getAll(Object object,
                                                           List<String> channelId) {
        HashMap<String, SubscriberHolder> uniqueSubscriberHolderMap = new HashMap<>();
        Set<Class<?>> classes = getAllClasses(object.getClass());
        for (Class<?> clazz : classes) {
            List<SubscriberHolder> subscriberHolders =
                    provideMethodsWithSubscribeAnnotation(clazz, channelId);
            if (subscriberHolders != null && subscriberHolders.size() != 0) {
                for (SubscriberHolder subscriberHolder : subscriberHolders) {
                    final String key = subscriberHolder.subscribedMethod.getName() + "_" +
                            subscriberHolder.subscribedMethod.getParameterTypes()[0].toString();
                    uniqueSubscriberHolderMap.put(key, subscriberHolder);
                }
            }
        }
        return uniqueSubscriberHolderMap;
    }

    private static Set<Class<?>> getAllClasses(Class<?> concreteClass) {
        List<Class<?>> parentClasses = new LinkedList<>();
        Set<Class<?>> classes = new HashSet<>();
        parentClasses.add(concreteClass);
        while (!parentClasses.isEmpty()) {
            Class<?> clazz = parentClasses.remove(0);
            classes.add(clazz);
            Class<?> parentClass = clazz.getSuperclass();
            if (parentClass != null) {
                parentClasses.add(parentClass);
            }
        }
        return classes;
    }

    private static List<SubscriberHolder> provideMethodsWithSubscribeAnnotation(Class<?> subscriber,
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

    private static boolean hasSubscribeAnnotation(Method method) {
        Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);
        return subscribeAnnotation != null;
    }

    private static boolean isAccessModifierPublic(Method method) {
        return (method.getModifiers() & Modifier.PUBLIC) != 0;
    }

    private static boolean isReturnTypeVoid(Method method) {
        return (method.getReturnType().equals(Void.TYPE));
    }

    private static boolean hasSingleParameter(Method method) {
        return method.getParameterTypes().length == 1;
    }

    private static SubscriberHolder generateSubscribedMethodHolder(Method method,
                                                                   List<String> targetChannelId) {
        List<String> methodChannelIds = new ArrayList<>(getMethodChannelId(method));
        NYThread subscribedThreadType = getMethodThread(method);
        methodChannelIds.retainAll(targetChannelId);
        return new SubscriberHolder(method, methodChannelIds, subscribedThreadType);
    }

    private static List<String> getMethodChannelId(Method subscribeMethod) {
        Subscribe subscribeAnnotation = subscribeMethod.getAnnotation(Subscribe.class);
        return Arrays.asList(subscribeAnnotation.channelId());
    }

    private static NYThread getMethodThread(Method subscribeMethod) {
        Subscribe subscribeAnnotation = subscribeMethod.getAnnotation(Subscribe.class);
        return subscribeAnnotation.threadType();
    }

}
