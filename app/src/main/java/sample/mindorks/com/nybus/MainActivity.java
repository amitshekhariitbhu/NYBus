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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.mindorks.nybus.NYBus;
import com.mindorks.nybus.annotation.Subscribe;


public class MainActivity extends AppCompatActivity {

    private TestTarget targetOne;
    private TestTarget targetTwo;

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
        targetOne = new TestTarget(TestTarget.CHANNEL_ONE);
        targetTwo = new TestTarget(TestTarget.CHANNEL_ONE,TestTarget.CHANNEL_TWO);
        NYBus.get().post("String to be received in one and two" ,TestTarget.CHANNEL_TWO);

    }

    @Subscribe(channelId = "two")
    public void onEventForTypeOne(Integer value) {
        Toast.makeText(this, "Event received on first Channel in activity", Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onStop() {
       //
        // NYBus.get().unregister(this,TestTarget.CHANNEL_ONE);
        super.onStop();
       // targetOne.destroy();
       // targetTwo.destroy();
    }


}
