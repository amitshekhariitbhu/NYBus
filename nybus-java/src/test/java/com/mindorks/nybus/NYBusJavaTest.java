package com.mindorks.nybus;

import com.mindorks.nybus.scheduler.TestSchedulerProvider;

import org.junit.Before;

/**
 * Created by gaura on 12-09-2017.
 */

public class NYBusJavaTest extends CommonTest{

    @Before
    public void before() throws Exception {
        bus = NYBus.get();
        bus.setSchedulerProvider(new TestSchedulerProvider());
    }

}
