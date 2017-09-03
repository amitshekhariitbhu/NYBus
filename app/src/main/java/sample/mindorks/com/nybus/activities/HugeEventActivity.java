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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mindorks.nybus.NYBus;
import com.mindorks.nybus.annotation.Subscribe;
import com.mindorks.nybus.event.EventChannel;
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

/**
 * Created by amitshekhar on 31/08/17.
 */

public class HugeEventActivity extends AppCompatActivity {

    private volatile int numberOfEventsReceived = 0;
    private static final int NUMBER_FOR_UNIQUE_EVENT = 7;
    private static final int NUMBER_OF_EVENT_CREATOR = 3;
    private static final int NUMBER_FOR_ONE_LOOP = 1000;
    private static final int TOTAL_NUMBER_OF_EVENTS = NUMBER_OF_EVENT_CREATOR *
            NUMBER_FOR_UNIQUE_EVENT
            * NUMBER_FOR_ONE_LOOP;
    private static final int BREAK_FOR_EVENT_FROM_MAIN_THREAD = 10;
    private static final String CHANNEL_ONE = "one";
    private static final String CHANNEL_TWO = "two";
    private TextView textView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huge_event);
        textView = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        numberOfEventsReceived = 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        NYBus.get().register(this, EventChannel.DEFAULT, CHANNEL_ONE, CHANNEL_TWO);
        createHugeNumberOfEvents();
    }

    @Override
    protected void onStop() {
        super.onStop();
        NYBus.get().unregister(this, EventChannel.DEFAULT, CHANNEL_ONE, CHANNEL_TWO);
    }

    @Subscribe(threadType = NYThread.COMPUTATION)
    public void onEvent(ComputationThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(threadType = NYThread.EXECUTOR)
    public void onEvent(ExecutorThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(threadType = NYThread.IO)
    public void onEvent(IOThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(threadType = NYThread.MAIN)
    public void onEvent(MainThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(threadType = NYThread.NEW)
    public void onEvent(NewThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(threadType = NYThread.POSTING)
    public void onEvent(PostingThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(threadType = NYThread.TRAMPOLINE)
    public void onEvent(TrampolineThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(channelId = CHANNEL_ONE, threadType = NYThread.COMPUTATION)
    public void onEventOnChannelOne(ComputationThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(channelId = CHANNEL_ONE, threadType = NYThread.EXECUTOR)
    public void onEventOnChannelOne(ExecutorThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(channelId = CHANNEL_ONE, threadType = NYThread.IO)
    public void onEventOnChannelOne(IOThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(channelId = CHANNEL_ONE, threadType = NYThread.MAIN)
    public void onEventOnChannelOne(MainThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(channelId = CHANNEL_ONE, threadType = NYThread.NEW)
    public void onEventOnChannelOne(NewThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(channelId = CHANNEL_ONE, threadType = NYThread.POSTING)
    public void onEventOnChannelOne(PostingThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(channelId = CHANNEL_ONE, threadType = NYThread.TRAMPOLINE)
    public void onEventOnChannelOne(TrampolineThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }


    @Subscribe(channelId = CHANNEL_TWO, threadType = NYThread.COMPUTATION)
    public void onEventOnChannelTwo(ComputationThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(channelId = CHANNEL_TWO, threadType = NYThread.EXECUTOR)
    public void onEventOnChannelTwo(ExecutorThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(channelId = CHANNEL_TWO, threadType = NYThread.IO)
    public void onEventOnChannelTwo(IOThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(channelId = CHANNEL_TWO, threadType = NYThread.MAIN)
    public void onEventOnChannelTwo(MainThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(channelId = CHANNEL_TWO, threadType = NYThread.NEW)
    public void onEventOnChannelTwo(NewThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(channelId = CHANNEL_TWO, threadType = NYThread.POSTING)
    public void onEventOnChannelTwo(PostingThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    @Subscribe(channelId = CHANNEL_TWO, threadType = NYThread.TRAMPOLINE)
    public void onEventOnChannelTwo(TrampolineThreadEvent event) {
        increaseAndCheckForTotalNumberOfEvents();
    }

    private synchronized void increaseAndCheckForTotalNumberOfEvents() {
        numberOfEventsReceived++;
        if (numberOfEventsReceived == TOTAL_NUMBER_OF_EVENTS) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(AppConstants.AllEventsReceived);
                }
            });
        }
    }

    private void createHugeNumberOfEvents() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                delay(1000);
                for (int i = 0; i < NUMBER_FOR_ONE_LOOP; i++) {
                    if (i % BREAK_FOR_EVENT_FROM_MAIN_THREAD == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                postOnDefaultChannel("main thread");
                            }
                        });
                    } else {
                        postOnDefaultChannel("new thread");
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                delay(1000);
                for (int i = 0; i < NUMBER_FOR_ONE_LOOP; i++) {
                    if (i % BREAK_FOR_EVENT_FROM_MAIN_THREAD == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                postOnGivenChannel("main thread", CHANNEL_ONE);
                            }
                        });
                    } else {
                        postOnGivenChannel("new thread", CHANNEL_ONE);
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                delay(1000);
                for (int i = 0; i < NUMBER_FOR_ONE_LOOP; i++) {
                    if (i % BREAK_FOR_EVENT_FROM_MAIN_THREAD == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                postOnGivenChannel("main thread", CHANNEL_TWO);
                            }
                        });
                    } else {
                        postOnGivenChannel("new thread", CHANNEL_TWO);
                    }
                }
            }
        }).start();
    }

    private void postOnDefaultChannel(String postingThread) {
        NYBus.get().post(new ComputationThreadEvent(postingThread));
        NYBus.get().post(new ExecutorThreadEvent(postingThread));
        NYBus.get().post(new IOThreadEvent(postingThread));
        NYBus.get().post(new MainThreadEvent(postingThread));
        NYBus.get().post(new NewThreadEvent(postingThread));
        NYBus.get().post(new PostingThreadEvent(postingThread));
        NYBus.get().post(new TrampolineThreadEvent(postingThread));
    }

    private void postOnGivenChannel(String postingThread, String channel) {
        NYBus.get().post(new ComputationThreadEvent(postingThread), channel);
        NYBus.get().post(new ExecutorThreadEvent(postingThread), channel);
        NYBus.get().post(new IOThreadEvent(postingThread), channel);
        NYBus.get().post(new MainThreadEvent(postingThread), channel);
        NYBus.get().post(new NewThreadEvent(postingThread), channel);
        NYBus.get().post(new PostingThreadEvent(postingThread), channel);
        NYBus.get().post(new TrampolineThreadEvent(postingThread), channel);
    }

    private void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
