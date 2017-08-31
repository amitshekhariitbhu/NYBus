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
import android.view.View;
import android.widget.TextView;

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
import sample.mindorks.com.nybus.utils.AppConstants;
import sample.mindorks.com.nybus.utils.ThreadUtils;

/**
 * Created by amitshekhar on 31/08/17.
 */

public class ThreadActivity extends AppCompatActivity {

    private static final String TAG = "ThreadActivity";
    private static final String POSTING_THREAD_MAIN = "posting_thread_main";
    private static final String POSTING_THREAD_BACKGROUND = "posting_thread_background";

    private TextView mainThreadEventFromMainThread;
    private TextView iOThreadEventFromMainThread;
    private TextView computationThreadEventFromMainThread;
    private TextView executorThreadEventFromMainThread;
    private TextView newThreadEventFromMainThread;
    private TextView postingThreadEventFromMainThread;
    private TextView trampolineThreadEventFromMainThread;

    private TextView mainThreadEventFromBgThread;
    private TextView iOThreadEventFromBgThread;
    private TextView computationThreadEventFromBgThread;
    private TextView executorThreadEventFromBgThread;
    private TextView newThreadEventFromBgThread;
    private TextView postingThreadEventFromBgThread;
    private TextView trampolineThreadEventFromBgThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        mainThreadEventFromMainThread = (TextView) findViewById(R.id.mainThreadEventFromMainThread);
        iOThreadEventFromMainThread = (TextView) findViewById(R.id.iOThreadEventFromMainThread);
        computationThreadEventFromMainThread = (TextView) findViewById(R.id.computationThreadEventFromMainThread);
        executorThreadEventFromMainThread = (TextView) findViewById(R.id.executorThreadEventFromMainThread);
        newThreadEventFromMainThread = (TextView) findViewById(R.id.newThreadEventFromMainThread);
        postingThreadEventFromMainThread = (TextView) findViewById(R.id.postingThreadEventFromMainThread);
        trampolineThreadEventFromMainThread = (TextView) findViewById(R.id.trampolineThreadEventFromMainThread);

        mainThreadEventFromBgThread = (TextView) findViewById(R.id.mainThreadEventFromBgThread);
        iOThreadEventFromBgThread = (TextView) findViewById(R.id.iOThreadEventFromBgThread);
        computationThreadEventFromBgThread = (TextView) findViewById(R.id.computationThreadEventFromBgThread);
        executorThreadEventFromBgThread = (TextView) findViewById(R.id.executorThreadEventFromBgThread);
        newThreadEventFromBgThread = (TextView) findViewById(R.id.newThreadEventFromBgThread);
        postingThreadEventFromBgThread = (TextView) findViewById(R.id.postingThreadEventFromBgThread);
        trampolineThreadEventFromBgThread = (TextView) findViewById(R.id.trampolineThreadEventFromBgThread);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NYBus.get().register(this);
        createEventsFromMainThread();
        createEventsFromBgThread();
    }

    @Override
    protected void onStop() {
        super.onStop();
        NYBus.get().unregister(this);
    }

    @Subscribe(threadType = NYThread.COMPUTATION)
    public void onEvent(final ComputationThreadEvent event) {
        if (ThreadUtils.isComputationThread()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (event.postingThreadName.equals(POSTING_THREAD_MAIN)) {
                        computationThreadEventFromMainThread.setVisibility(View.VISIBLE);
                        computationThreadEventFromMainThread.setText(AppConstants.ComputationThreadEventFromMainThread);
                    } else if (event.postingThreadName.equals(POSTING_THREAD_BACKGROUND)) {
                        computationThreadEventFromBgThread.setVisibility(View.VISIBLE);
                        computationThreadEventFromBgThread.setText(AppConstants.ComputationThreadEventFromBgThread);
                    }
                }
            });
        }
    }

    @Subscribe(threadType = NYThread.EXECUTOR)
    public void onEvent(final ExecutorThreadEvent event) {
        if (ThreadUtils.isExecutorThread()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (event.postingThreadName.equals(POSTING_THREAD_MAIN)) {
                        executorThreadEventFromMainThread.setVisibility(View.VISIBLE);
                        executorThreadEventFromMainThread.setText(AppConstants.ExecutorThreadEventFromMainThread);
                    } else if (event.postingThreadName.equals(POSTING_THREAD_BACKGROUND)) {
                        executorThreadEventFromBgThread.setVisibility(View.VISIBLE);
                        executorThreadEventFromBgThread.setText(AppConstants.ExecutorThreadEventFromBgThread);
                    }
                }
            });
        }

    }

    @Subscribe(threadType = NYThread.IO)
    public void onEvent(final IOThreadEvent event) {
        if (ThreadUtils.isIOThread()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (event.postingThreadName.equals(POSTING_THREAD_MAIN)) {
                        iOThreadEventFromMainThread.setVisibility(View.VISIBLE);
                        iOThreadEventFromMainThread.setText(AppConstants.IOThreadEventFromMainThread);
                    } else if (event.postingThreadName.equals(POSTING_THREAD_BACKGROUND)) {
                        iOThreadEventFromBgThread.setVisibility(View.VISIBLE);
                        iOThreadEventFromBgThread.setText(AppConstants.IOThreadEventFromBgThread);
                    }
                }
            });
        }
    }

    @Subscribe(threadType = NYThread.MAIN)
    public void onEvent(MainThreadEvent event) {
        if (ThreadUtils.isMainThread()) {
            if (event.postingThreadName.equals(POSTING_THREAD_MAIN)) {
                mainThreadEventFromMainThread.setVisibility(View.VISIBLE);
                mainThreadEventFromMainThread.setText(AppConstants.MainThreadEventFromMainThread);
            } else if (event.postingThreadName.equals(POSTING_THREAD_BACKGROUND)) {
                mainThreadEventFromBgThread.setVisibility(View.VISIBLE);
                mainThreadEventFromBgThread.setText(AppConstants.MainThreadEventFromBgThread);
            }
        }
    }

    @Subscribe(threadType = NYThread.NEW)
    public void onEvent(final NewThreadEvent event) {
        if (ThreadUtils.isNewThreadThread()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (event.postingThreadName.equals(POSTING_THREAD_MAIN)) {
                        newThreadEventFromMainThread.setVisibility(View.VISIBLE);
                        newThreadEventFromMainThread.setText(AppConstants.NewThreadEventFromMainThread);
                    } else if (event.postingThreadName.equals(POSTING_THREAD_BACKGROUND)) {
                        newThreadEventFromBgThread.setVisibility(View.VISIBLE);
                        newThreadEventFromBgThread.setText(AppConstants.NewThreadEventFromBgThread);
                    }
                }
            });
        }
    }

    @Subscribe(threadType = NYThread.POSTING)
    public void onEvent(PostingThreadEvent event) {
        if (event.postingThreadName.equals(POSTING_THREAD_MAIN)) {
            if (ThreadUtils.isMainThread()) {
                postingThreadEventFromMainThread.setVisibility(View.VISIBLE);
                postingThreadEventFromMainThread.setText(AppConstants.PostingThreadEventFromMainThread);
            }
        } else if (event.postingThreadName.equals(POSTING_THREAD_BACKGROUND)) {
            if (ThreadUtils.isPostingBackgroundThread()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postingThreadEventFromBgThread.setVisibility(View.VISIBLE);
                        postingThreadEventFromBgThread.setText(AppConstants.PostingThreadEventFromBgThread);
                    }
                });
            }
        }
    }

    @Subscribe(threadType = NYThread.TRAMPOLINE)
    public void onEvent(TrampolineThreadEvent event) {
        if (event.postingThreadName.equals(POSTING_THREAD_MAIN)) {
            if (ThreadUtils.isMainThread()) {
                trampolineThreadEventFromMainThread.setVisibility(View.VISIBLE);
                trampolineThreadEventFromMainThread.setText(AppConstants.TrampolineThreadEventFromMainThread);
            }
        } else if (event.postingThreadName.equals(POSTING_THREAD_BACKGROUND)) {
            if (ThreadUtils.isPostingBackgroundThread()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        trampolineThreadEventFromBgThread.setVisibility(View.VISIBLE);
                        trampolineThreadEventFromBgThread.setText(AppConstants.TrampolineThreadEventFromBgThread);
                    }
                });
            }
        }
    }

    private void createEventsFromMainThread() {
        NYBus.get().post(new ComputationThreadEvent(POSTING_THREAD_MAIN));
        NYBus.get().post(new ExecutorThreadEvent(POSTING_THREAD_MAIN));
        NYBus.get().post(new IOThreadEvent(POSTING_THREAD_MAIN));
        NYBus.get().post(new MainThreadEvent(POSTING_THREAD_MAIN));
        NYBus.get().post(new NewThreadEvent(POSTING_THREAD_MAIN));
        NYBus.get().post(new PostingThreadEvent(POSTING_THREAD_MAIN));
        NYBus.get().post(new TrampolineThreadEvent(POSTING_THREAD_MAIN));
    }

    private void createEventsFromBgThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000); // delay for posting event after 1 sec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NYBus.get().post(new ComputationThreadEvent(POSTING_THREAD_BACKGROUND));
                NYBus.get().post(new ExecutorThreadEvent(POSTING_THREAD_BACKGROUND));
                NYBus.get().post(new IOThreadEvent(POSTING_THREAD_BACKGROUND));
                NYBus.get().post(new MainThreadEvent(POSTING_THREAD_BACKGROUND));
                NYBus.get().post(new NewThreadEvent(POSTING_THREAD_BACKGROUND));
                NYBus.get().post(new PostingThreadEvent(POSTING_THREAD_BACKGROUND));
                NYBus.get().post(new TrampolineThreadEvent(POSTING_THREAD_BACKGROUND));
            }
        });
        thread.setName(POSTING_THREAD_BACKGROUND);
        thread.start();
    }

}