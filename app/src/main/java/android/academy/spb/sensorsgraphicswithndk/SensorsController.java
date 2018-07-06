package android.academy.spb.sensorsgraphicswithndk;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by User on 15.06.2018.
 */

public class SensorsController implements SensorEventListener {

    public final static String TAG = SensorsController.class.getSimpleName();

    public interface Callback {
        void onAppearedNewData(float[] data, int valuesCount, int sensorType);
    }

    private Callback callback;
    private final static int ARR_SIZE = 1000;
    private final static int mDataCollectionIntervalInMs = 1000;

    private SensorManager mSensorManager;
    private SensorData AccelerometerData;
    private SensorData GyroscopeData;

    public SensorsController(Context context, final Callback callback) {
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        this.callback = callback;

        AccelerometerData = new SensorData(ARR_SIZE, mDataCollectionIntervalInMs, new SensorData.Callback() {
            @Override
            public void onFinishedCollectionData(float[] data1, float[] data2, float[] data3, int arraySize, SensorData.FinishedCollectionDataStates state) {
                try {
                    float[] averageValues = nativeAverageFloatArray(data1, data2, data3, arraySize);

                    if (callback != null) {
                        callback.onAppearedNewData(averageValues, 3, Sensor.TYPE_ACCELEROMETER);
                    }

                    /*Log.d(TAG, "Accelerometer Data collection finished[" + arraySize + "] = "
                            + averageValues[0]
                            + ", " + averageValues[1]
                            + ", " + averageValues[2]);
                    */


                } catch (Exception e) {
                    //e.printStackTrace();
                }

            }
        });
        GyroscopeData = new SensorData(ARR_SIZE, mDataCollectionIntervalInMs, new SensorData.Callback() {
            @Override
            public void onFinishedCollectionData(float[] data1, float[] data2, float[] data3, int arraySize, SensorData.FinishedCollectionDataStates state) {
                try {
                    float[] averageValues = nativeAverageFloatArray(data1, data2, data3, arraySize);

                    if (callback != null) {
                        callback.onAppearedNewData(averageValues, 3, Sensor.TYPE_GYROSCOPE);
                    }

                    /*
                    Log.d(TAG, "Gyroscope Data collection finished[" + arraySize + "] = "
                            + averageValues[0]
                            + ", " + averageValues[1]
                            + ", " + averageValues[2]);
                            */

                } catch (Exception e) {
                    //e.printStackTrace();
                }

            }
        });
    }

    public void register() {

        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
    }

    public void unregister() {
        mSensorManager.unregisterListener(this);
    }

    public SensorManager getSensorManager() {
        return mSensorManager;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //float[] values = new float[3];

        //Log.d(TAG, "on onSensorChanged method");
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            /*
            values[0] = event.values[0];
            values[1] = event.values[1];
            values[2] = event.values[2];

            AccelerometerData.addValues(event.timestamp, values);
            */
            AccelerometerData.addValues(event);

            //Log.d(TAG, (long) (event.timestamp/10E8) + ": Accelerometer sensor data: "
            //        + event.values[0] + " " + event.values[1] + " " + event.values[2]);

        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            /*
            values[0] = event.values[0];
            values[1] = event.values[1];
            values[2] = event.values[2];

            GyroscopeData.addValues(event.timestamp, values);
            */
            GyroscopeData.addValues(event);

            //Log.d(TAG, event.timestamp/10E8 + ": Gyroscope sensor data: "
            //           + event.values[0] + " " + event.values[1] + " " + event.values[2]);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "on Accurace changed method");
    }

    public native float[] nativeAverageFloatArray(float[] arr1, float[] arr2, float[] arr3, int arrSize) throws Exception;

}
