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

package sample.mindorks.com.nybus.targets;

import com.mindorks.nybus.NYBus;
import com.mindorks.nybus.annotation.Subscribe;

/**
 * Created by amitshekhar on 18/08/17.
 */

public class TestTarget {

    public static final String CHANNEL_ONE = "one";
    public static final String CHANNEL_TWO = "two";

    private String channel;

    public TestTarget(String ... channelIdForRegistration) {
        NYBus.get().register(this, channelIdForRegistration);
    }

    @Subscribe(channelId = TestTarget.CHANNEL_ONE)
    public void onEventForTypeOne(String value) {
        System.out.println("Event received on first Channel" + value);
    }

    @Subscribe(channelId = TestTarget.CHANNEL_TWO)
    public void onEventForTypeTwo(String value) {
        System.out.println("Event received on second Channel" + value);
    }

    public void destroy() {

        //NYBus.get().unregister(this, channel);
    }

}
