package android.academy.spb.sensorsgraphicswithndk;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by User on 28.06.2018.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Stetho.initializeWithDefaults(this);
    }
}
