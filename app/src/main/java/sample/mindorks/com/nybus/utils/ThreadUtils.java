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

package sample.mindorks.com.nybus.utils;

import android.os.Looper;

/**
 * Created by amitshekhar on 31/08/17.
 */

public class ThreadUtils {

    private static final String TAG = "ThreadUtils";

    private ThreadUtils() {
        // no instance
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean isComputationThread() {
        return Thread.currentThread().getName().contains("RxComputationThreadPool");
    }

    public static boolean isExecutorThread() {
        return Thread.currentThread().getName().contains("pool")
                && Thread.currentThread().getName().contains("thread");
    }

    public static boolean isIOThread() {
        return Thread.currentThread().getName().contains("RxCachedThreadScheduler");
    }

    public static boolean isNewThreadThread() {
        return Thread.currentThread().getName().contains("RxNewThreadScheduler");
    }

    public static boolean isPostingBackgroundThread() {
        return Thread.currentThread().getName().contains("posting_thread_background");
    }

}
