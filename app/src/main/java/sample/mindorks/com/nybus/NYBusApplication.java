package sample.mindorks.com.nybus;

import android.app.Application;

import com.mindorks.nybus.AndroidNYBus;

/**
 * Created by Jyoti on 14/08/17.
 */

public class NYBusApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNYBus.initialiseSchedulerProvider();
    }
}
