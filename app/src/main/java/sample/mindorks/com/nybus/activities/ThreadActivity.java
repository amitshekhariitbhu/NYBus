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

package sample.mindorks.com.nybus.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.mindorks.nybus.NYBus;
import com.mindorks.nybus.annotation.Subscribe;
import com.mindorks.nybus.thread.NYThread;

import sample.mindorks.com.nybus.R;
import sample.mindorks.com.nybus.events.ComputationThreadEvent;
import sample.mindorks.com.nybus.events.ExecutorThreadEvent;
import sample.mindorks.com.nybus.events.IOThreadEvent;
import sample.mindorks.com.nybus.events.MainThreadEvent;
import sample.mindorks.com.nybus.events.NewThreadEvent;
import sample.mindorks.com.nybus.events.PostingThreadEvent;
import sample.mindorks.com.nybus.events.TrampolineThreadEvent;
import sample.mindorks.com.nybus.utils.ThreadUtils;

/**
 * Created by amitshekhar on 31/08/17.
 */

public class ThreadActivity extends AppCompatActivity {

    private static final String TAG = "ThreadActivity";
    private static final String POSTING_THREAD_MAIN = "posting_thread_main";
    private static final String POSTING_THREAD_NEW_THREAD = "posting_thread_new_thread";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NYBus.get().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        NYBus.get().unregister(this);
    }

    @Subscribe(threadType = NYThread.COMPUTATION)
    public void onEvent(ComputationThreadEvent event) {
        if (ThreadUtils.isComputationThread()) {
            Log.d(TAG, "onEvent : ComputationThreadEvent");
        } else {
            Log.d(TAG, "onEvent : ComputationThreadEvent is in wrong thread");
        }
    }

    @Subscribe(threadType = NYThread.EXECUTOR)
    public void onEvent(ExecutorThreadEvent event) {
        if (ThreadUtils.isExecutorThread()) {
            Log.d(TAG, "onEvent : ExecutorThreadEvent");
        } else {
            Log.d(TAG, "onEvent : ExecutorThreadEvent is in wrong thread");
        }
    }

    @Subscribe(threadType = NYThread.IO)
    public void onEvent(IOThreadEvent event) {
        if (ThreadUtils.isIOThread()) {
            Log.d(TAG, "onEvent : IOThreadEvent");
        } else {
            Log.d(TAG, "onEvent : IOThreadEvent is in wrong thread");
        }
    }

    @Subscribe(threadType = NYThread.MAIN)
    public void onEvent(MainThreadEvent event) {
        if (ThreadUtils.isMainThread()) {
            Log.d(TAG, "onEvent : MainThreadEvent");
        } else {
            Log.d(TAG, "onEvent : MainThreadEvent is in wrong thread");
        }
    }

    @Subscribe(threadType = NYThread.NEW)
    public void onEvent(NewThreadEvent event) {
        if (ThreadUtils.isNewThreadThread()) {
            Log.d(TAG, "onEvent : NewThreadEvent");
        } else {
            Log.d(TAG, "onEvent : NewThreadEvent is in wrong thread");
        }
    }

    @Subscribe(threadType = NYThread.POSTING)
    public void onEvent(PostingThreadEvent event) {
        if (event.postingThreadName.equals(POSTING_THREAD_MAIN)) {
            if (ThreadUtils.isMainThread()) {
                Log.d(TAG, "onEvent : PostingThreadEvent");
            } else {
                Log.d(TAG, "onEvent : PostingThreadEvent is in wrong thread");
            }
        } else if (event.postingThreadName.equals(POSTING_THREAD_NEW_THREAD)) {
            if (ThreadUtils.isPostingNewThread()) {
                Log.d(TAG, "onEvent : PostingThreadEvent");
            } else {
                Log.d(TAG, "onEvent : PostingThreadEvent is in wrong thread");
            }
        }
    }

    @Subscribe(threadType = NYThread.TRAMPOLINE)
    public void onEvent(TrampolineThreadEvent event) {
        if (event.postingThreadName.equals(POSTING_THREAD_MAIN)) {
            if (ThreadUtils.isMainThread()) {
                Log.d(TAG, "onEvent : TrampolineThreadEvent");
            } else {
                Log.d(TAG, "onEvent : TrampolineThreadEvent is in wrong thread");
            }
        } else if (event.postingThreadName.equals(POSTING_THREAD_NEW_THREAD)) {
            if (ThreadUtils.isPostingNewThread()) {
                Log.d(TAG, "onEvent : TrampolineThreadEvent");
            } else {
                Log.d(TAG, "onEvent : TrampolineThreadEvent is in wrong thread");
            }
        }
    }

    public void createEventFromMain(View view) {
        NYBus.get().post(new ComputationThreadEvent());
        NYBus.get().post(new ExecutorThreadEvent());
        NYBus.get().post(new IOThreadEvent());
        NYBus.get().post(new MainThreadEvent());
        NYBus.get().post(new NewThreadEvent());
        NYBus.get().post(new PostingThreadEvent(POSTING_THREAD_MAIN));
        NYBus.get().post(new TrampolineThreadEvent(POSTING_THREAD_MAIN));
    }

    public void createEventFromBg(View view) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NYBus.get().post(new ComputationThreadEvent());
                NYBus.get().post(new ExecutorThreadEvent());
                NYBus.get().post(new IOThreadEvent());
                NYBus.get().post(new MainThreadEvent());
                NYBus.get().post(new NewThreadEvent());
                NYBus.get().post(new PostingThreadEvent(POSTING_THREAD_NEW_THREAD));
                NYBus.get().post(new TrampolineThreadEvent(POSTING_THREAD_NEW_THREAD));
            }
        });
        thread.setName(POSTING_THREAD_NEW_THREAD);
        thread.start();
    }

}