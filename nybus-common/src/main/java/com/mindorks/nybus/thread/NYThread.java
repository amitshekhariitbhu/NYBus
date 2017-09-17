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

package com.mindorks.nybus.thread;

/**
 * Created by amitshekhar on 27/08/17.
 */

/**
 * Enumeration for various types of thread.
 */
public enum NYThread {

    /**
     * Post on the posting thread on which the event was posted.
     */
    POSTING,

    /**
     * Post on the Android main thread.
     */
    MAIN,

    /**
     * Post on the IO thread.
     */
    IO,

    /**
     * Post on the new thread.
     */
    NEW,

    /**
     * Post on the computation thread.
     */
    COMPUTATION,

    /**
     * Post on the trampoline thread.
     */
    TRAMPOLINE,

    /**
     * Post on the executor thread.
     */
    EXECUTOR,

}
