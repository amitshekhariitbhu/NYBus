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

import sample.mindorks.com.nybus.activities.ThreadActivity;
import sample.mindorks.com.nybus.idling.ViewIdlingResource;
import sample.mindorks.com.nybus.utils.AppConstants;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by amitshekhar on 31/08/17.
 */
@RunWith(AndroidJUnit4.class)
public class ThreadActivityTest {

    @Rule
    public ActivityTestRule<ThreadActivity> mActivityRule =
            new ActivityTestRule<>(ThreadActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {

        List<View> viewList = Arrays.asList(
                mActivityRule.getActivity()
                        .findViewById(R.id.mainThreadEventFromMainThread),
                mActivityRule.getActivity()
                        .findViewById(R.id.iOThreadEventFromMainThread),
                mActivityRule.getActivity()
                        .findViewById(R.id.computationThreadEventFromMainThread),
                mActivityRule.getActivity()
                        .findViewById(R.id.executorThreadEventFromMainThread),
                mActivityRule.getActivity()
                        .findViewById(R.id.newThreadEventFromMainThread),
                mActivityRule.getActivity()
                        .findViewById(R.id.postingThreadEventFromMainThread),
                mActivityRule.getActivity()
                        .findViewById(R.id.trampolineThreadEventFromMainThread),
                mActivityRule.getActivity()
                        .findViewById(R.id.mainThreadEventFromBgThread),
                mActivityRule.getActivity()
                        .findViewById(R.id.iOThreadEventFromBgThread),
                mActivityRule.getActivity()
                        .findViewById(R.id.computationThreadEventFromBgThread),
                mActivityRule.getActivity()
                        .findViewById(R.id.executorThreadEventFromBgThread),
                mActivityRule.getActivity()
                        .findViewById(R.id.newThreadEventFromBgThread),
                mActivityRule.getActivity()
                        .findViewById(R.id.postingThreadEventFromBgThread),
                mActivityRule.getActivity()
                        .findViewById(R.id.trampolineThreadEventFromBgThread)
        );

        mIdlingResource = new ViewIdlingResource(viewList);

        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void testMainThreadEvent() {
        onView(withId(R.id.mainThreadEventFromMainThread)).check(matches(isDisplayed()));
        onView(withId(R.id.mainThreadEventFromMainThread)).check(matches(withText(AppConstants.MainThreadEventFromMainThread)));

        onView(withId(R.id.iOThreadEventFromMainThread)).check(matches(isDisplayed()));
        onView(withId(R.id.iOThreadEventFromMainThread)).check(matches(withText(AppConstants.IOThreadEventFromMainThread)));

        onView(withId(R.id.computationThreadEventFromMainThread)).check(matches(isDisplayed()));
        onView(withId(R.id.computationThreadEventFromMainThread)).check(matches(withText(AppConstants.ComputationThreadEventFromMainThread)));

        onView(withId(R.id.executorThreadEventFromMainThread)).check(matches(isDisplayed()));
        onView(withId(R.id.executorThreadEventFromMainThread)).check(matches(withText(AppConstants.ExecutorThreadEventFromMainThread)));

        onView(withId(R.id.newThreadEventFromMainThread)).check(matches(isDisplayed()));
        onView(withId(R.id.newThreadEventFromMainThread)).check(matches(withText(AppConstants.NewThreadEventFromMainThread)));

        onView(withId(R.id.postingThreadEventFromMainThread)).check(matches(isDisplayed()));
        onView(withId(R.id.postingThreadEventFromMainThread)).check(matches(withText(AppConstants.PostingThreadEventFromMainThread)));

        onView(withId(R.id.trampolineThreadEventFromMainThread)).check(matches(isDisplayed()));
        onView(withId(R.id.trampolineThreadEventFromMainThread)).check(matches(withText(AppConstants.TrampolineThreadEventFromMainThread)));

        onView(withId(R.id.mainThreadEventFromBgThread)).check(matches(isDisplayed()));
        onView(withId(R.id.mainThreadEventFromBgThread)).check(matches(withText(AppConstants.MainThreadEventFromBgThread)));

        onView(withId(R.id.iOThreadEventFromBgThread)).check(matches(isDisplayed()));
        onView(withId(R.id.iOThreadEventFromBgThread)).check(matches(withText(AppConstants.IOThreadEventFromBgThread)));

        onView(withId(R.id.computationThreadEventFromBgThread)).check(matches(isDisplayed()));
        onView(withId(R.id.computationThreadEventFromBgThread)).check(matches(withText(AppConstants.ComputationThreadEventFromBgThread)));

        onView(withId(R.id.executorThreadEventFromBgThread)).check(matches(isDisplayed()));
        onView(withId(R.id.executorThreadEventFromBgThread)).check(matches(withText(AppConstants.ExecutorThreadEventFromBgThread)));

        onView(withId(R.id.newThreadEventFromBgThread)).check(matches(isDisplayed()));
        onView(withId(R.id.newThreadEventFromBgThread)).check(matches(withText(AppConstants.NewThreadEventFromBgThread)));

        onView(withId(R.id.postingThreadEventFromBgThread)).check(matches(isDisplayed()));
        onView(withId(R.id.postingThreadEventFromBgThread)).check(matches(withText(AppConstants.PostingThreadEventFromBgThread)));

        onView(withId(R.id.trampolineThreadEventFromBgThread)).check(matches(isDisplayed()));
        onView(withId(R.id.trampolineThreadEventFromBgThread)).check(matches(withText(AppConstants.TrampolineThreadEventFromBgThread)));

    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}