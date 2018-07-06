package android.academy.spb.sensorsgraphicswithndk;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by User on 29.06.2018.
 */

public final class ExecutorProvider {

    private final static ExecutorService commonExecutor = Executors.newSingleThreadExecutor();

    public static ExecutorService getCommonExecutor() {
        return commonExecutor;
    }
    
}
