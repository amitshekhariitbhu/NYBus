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

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import sample.mindorks.com.nybus.activities.HugeEventActivity;
import sample.mindorks.com.nybus.idling.ViewIdlingResource;
import sample.mindorks.com.nybus.utils.AppConstants;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by amitshekhar on 01/09/17.
 */
@RunWith(AndroidJUnit4.class)
public class HugeEventActivityTest {

    @Rule
    public ActivityTestRule<HugeEventActivity> mActivityRule =
            new ActivityTestRule<>(HugeEventActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        List<View> viewList = Arrays.asList(mActivityRule.getActivity()
                .findViewById(R.id.textView));

        mIdlingResource = new ViewIdlingResource(viewList);

        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void testAllEvents() {
        onView(withId(R.id.textView)).check(matches(isDisplayed()));
        onView(withId(R.id.textView)).check(matches(withText(AppConstants.AllEventsReceived)));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }

}
