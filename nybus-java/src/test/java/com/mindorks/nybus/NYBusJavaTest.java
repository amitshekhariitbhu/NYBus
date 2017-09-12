package com.mindorks.nybus;

import com.mindorks.nybus.scheduler.TestSchedulerProvider;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created by gaura on 12-09-2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class NYBusJavaTest extends CommonTest{

    @Before
    public void before() throws Exception {
        bus = NYBus.get();
        bus.setSchedulerProvider(new TestSchedulerProvider());
        bus.setLogger(logger);
    }

}
