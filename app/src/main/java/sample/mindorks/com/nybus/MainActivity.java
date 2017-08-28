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

package sample.mindorks.com.nybus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mindorks.nybus.NYBus;
import com.mindorks.nybus.annotation.Subscribe;
import com.mindorks.nybus.thread.NYThread;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        NYBus.get().register(this, TestTarget.CHANNEL_ONE);
        NYBus.get().post(1, TestTarget.CHANNEL_ONE);

    }

    @Subscribe(channelId = TestTarget.CHANNEL_ONE, threadType = NYThread.MAIN)
    public void onEvent(Integer value) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            System.out.println("event received in background thread "+value);

        } else {
            System.out.println("event received in main thread "+value);


        }
    }


    @Override
    protected void onStop() {
        NYBus.get().unregister(this, TestTarget.CHANNEL_ONE);
        super.onStop();

    }


}
