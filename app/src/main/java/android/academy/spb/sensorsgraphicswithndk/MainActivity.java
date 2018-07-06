package android.academy.spb.sensorsgraphicswithndk;

import android.academy.spb.sensorsgraphicswithndk.db.AppDatabase;
import android.academy.spb.sensorsgraphicswithndk.db.Constants;
import android.academy.spb.sensorsgraphicswithndk.db.Measurement;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final String TAG = MainActivity.class.getSimpleName();

    public static final String BROADCAST_ACTION = "DataFromSensorsService";

    private int[] mGraphXAxisSamples;
    private MyService myService;

    private BroadcastReceiver sensorsDataReceiver = new BroadcastReceiver() {

        Handler h = new Handler(Looper.getMainLooper());

        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, "in Receiver");
            if (intent.getAction() == BROADCAST_ACTION) {
                if (intent.getIntExtra(MyService.SENSOR_TYPE, 9999) == Sensor.TYPE_ACCELEROMETER) {
                    final float[] data = intent.getFloatArrayExtra(MyService.SENSOR_DATA);

                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAccelerometerSeries.appendData(new DataPoint(mGraphXAxisSamples[0]++, data[0]), false, 600);
                        }
                    }, 100);

                    //Log.d(TAG, "Accelerometer Data: " + data[0] + ", " + data[1] + ", " + data[2]);
                } else if (intent.getIntExtra(MyService.SENSOR_TYPE, 9999) == Sensor.TYPE_GYROSCOPE) {
                    float[] data = intent.getFloatArrayExtra(MyService.SENSOR_DATA);
                    mGyroscopeSeries.appendData(new DataPoint(mGraphXAxisSamples[1]++, data[0]), false, 600);
                    //Log.d(TAG, "Gyroscope Data: " + data[0] + ", " + data[1] + ", " + data[2]);
                }
            }
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, name.getShortClassName() + " connected");
            myService = ((MyService.MyBinder) service).getService();
            myService.test_method();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, name.flattenToString() + " disconnected");
        }
    };

    private IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private SensorsController mSensorsController;
    private GraphView mAccelerometerPlot;
    private GraphView mGyroscopePlot;
    private LineGraphSeries<DataPoint> mAccelerometerSeries;
    private LineGraphSeries<DataPoint> mGyroscopeSeries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent serviceIntent = new Intent(getBaseContext(), MyService.class);

        startService(serviceIntent);

        //boolean isServiceBinded = bindService(serviceIntent, mServiceConnection, 0);
        //Log.d(TAG, "isServiceBinded: " + Boolean.toString(isServiceBinded));

        mGraphXAxisSamples = new int[]{0,0};

        mAccelerometerPlot = findViewById(R.id.accelerometer_plot);
        mAccelerometerPlot.setTitle(getString(R.string.accelerometer_plot_title));
        mAccelerometerSeries = new LineGraphSeries<>();
        mAccelerometerPlot.addSeries(mAccelerometerSeries);

        mGyroscopePlot = findViewById(R.id.gyroscope_plot);
        mGyroscopePlot.setTitle(getString(R.string.gyroscope_plot_title));
        mGyroscopeSeries = new LineGraphSeries<>();
        mGyroscopePlot.addSeries(mGyroscopeSeries);

        ExecutorProvider.getCommonExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Measurement> measurements = AppDatabase.getInstance(getApplicationContext()).meausrementDao().getAllForPeriodFirst();
                //List<Measurement> measurements = AppDatabase.getInstance(getApplicationContext()).meausrementDao().getAll();

                Date timestamp = new Date();
                for (Measurement measurement:
                     measurements) {
                    //Log.d(TAG, "BD: " + measurement.getId());
                    if (timestamp.getTime() != measurement.getTimestamp().getTime()) {
                        timestamp = measurement.getTimestamp();
                        if (measurement.getSensorType() == Sensor.TYPE_ACCELEROMETER) {
                            mAccelerometerSeries.appendData(new DataPoint(mGraphXAxisSamples[0]++, measurement.getData()),
                                    false, 600);
                            //Log.d(TAG, "Accelerometer BD: " + measurement.getId());

                        } else if (measurement.getSensorType() == Sensor.TYPE_GYROSCOPE) {
                            mGyroscopeSeries.appendData(new DataPoint(mGraphXAxisSamples[1]++, measurement.getData()),
                                    false, 600);
                            //Log.d(TAG, "Gyroscope BD: " + measurement.getId());
                        }
                    }
                }
            }
        });

        /*
        for (Sensor sensor:
                mSensorsController.getSensorManager().getSensorList(Sensor.TYPE_ALL)) {
            Log.d(TAG, "Sensor name: " + sensor.getName());
        }*/

        // Example of a call to a native method
        /*TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        */

        /*
        float[] results;

        results = nativeAverageFloatArray(new float[]{9,4,5,8,6,7,4,7}, new float[]{9,4,5}, new float[]{9,4,5,6});

        Log.d(TAG, "Arr size from native func: " + results[0] + " - " + results[1] + " - " + results[2]);
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mSensorsController.register();
        registerReceiver(sensorsDataReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    //    mSensorsController.unregister();
        unregisterReceiver(sensorsDataReceiver);
        //unbindService(mServiceConnection);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unbindService(mServiceConnection);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native float[] nativeAverageFloatArray(float[] arr1, float[] arr2, float[] arr3);
}
