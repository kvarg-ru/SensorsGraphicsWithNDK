package android.academy.spb.sensorsgraphicswithndk;

import android.academy.spb.sensorsgraphicswithndk.db.AppDatabase;
import android.academy.spb.sensorsgraphicswithndk.db.Measurement;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class MyService extends Service {

    public static final String TAG = MyService.class.getSimpleName();
    public static final String SENSOR_TYPE = "sensor_type";
    public static final String SENSOR_DATA = "sensor_data";

    private SensorsController mSensorsController;
    private MyBinder mBinder = new MyBinder();
    private boolean running = false;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "created service");

        ExecutorProvider.getCommonExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getInstance(getApplicationContext()).meausrementDao().deleteAll();
            }
        });

        mSensorsController = new SensorsController(getBaseContext(), new SensorsController.Callback() {
            @Override
            public void onAppearedNewData(float[] data, int valuesCount, int sensorType) {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MainActivity.BROADCAST_ACTION);
                broadcastIntent.putExtra(SENSOR_TYPE, sensorType);
                broadcastIntent.putExtra(SENSOR_DATA, data);
                sendBroadcast(broadcastIntent);

                Date currentDateTime = Calendar.getInstance().getTime();

                for (int i = 0; i < valuesCount; i++) {
                    final Measurement measurement = new Measurement(sensorType, data[i], currentDateTime);

                    ExecutorProvider.getCommonExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            AppDatabase.getInstance(getApplicationContext()).meausrementDao().Insert(measurement);
                        }
                    });

                }

                //Log.d(TAG, "send broadcast");
            }
        });

        mSensorsController.register();

    }

    public void test_method() {
        Log.d(TAG, "in test_method");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        //return null;
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Start Service " + startId + " " + running);

        if (!running) {
            running = true;
        } else {
            stopSelf(startId - 1);
            Log.d(TAG, "Stop Service " + (startId - 1));
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroy Service");
        mSensorsController.unregister();
        ExecutorProvider.getCommonExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getInstance(getApplicationContext()).meausrementDao().deleteAll();
            }
        });
        super.onDestroy();
    }

    class MyBinder extends Binder {

        public MyService getService() {
            return MyService.this;
        }

    }
}
