package android.academy.spb.sensorsgraphicswithndk;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

/**
 * Created by User on 15.06.2018.
 */

public class SensorData {

    public final static String TAG = SensorData.class.getSimpleName();

    public enum FinishedCollectionDataStates {TIME_ELAPSED, ARRAY_OVERFLOW};

    public interface Callback {
        void onFinishedCollectionData(float[] data1, float[] data2, float[] data3, int arraySize, FinishedCollectionDataStates state);
    }

    private final static long TIMESTAMP_DIV_COEF_IN_MS = (long) 10E5;

    private Callback callback;
    private long first_value_timestamp;
    private float[] valueArr1;
    private float[] valueArr2;
    private float[] valueArr3;
    private int finishCollectionIntervalInMs;

    private int i;
    private int arrSize;

    public SensorData(int arrSize, int finishCollectionIntervalInMs, Callback callback) {
        valueArr1 = new float[arrSize];
        valueArr2 = new float[arrSize];
        valueArr3 = new float[arrSize];

        this.arrSize = arrSize;
        this.callback = callback;
        this.finishCollectionIntervalInMs = finishCollectionIntervalInMs;

        i = 0;
    }

    public float[] getValueArr1() {
        return valueArr1;
    }

    public float[] getValueArr2() {
        return valueArr2;
    }

    public float[] getValueArr3() {
        return valueArr3;
    }

    private boolean is_full() {
        return i == arrSize;
    }

    private boolean is_empty() {
        return i == 0;
    }


    private boolean is_time_elapsed(long timestamp) {
        if (!is_empty()) {
            return (timestamp > first_value_timestamp) ?
                    timestamp - first_value_timestamp > finishCollectionIntervalInMs :
                    first_value_timestamp - timestamp > finishCollectionIntervalInMs;
        }
        return false;
    }

    public void addValues(long timestamp, float value1, float value2, float value3) {
        if (is_full()) {
            if (callback != null) {
                callback.onFinishedCollectionData(valueArr1, valueArr2, valueArr3, i,
                                                    FinishedCollectionDataStates.ARRAY_OVERFLOW);
            }
            clean();
        }

        if (is_empty()) first_value_timestamp = timestamp/TIMESTAMP_DIV_COEF_IN_MS;

        valueArr1[i] = value1;
        valueArr2[i] = value2;
        valueArr3[i] = value3;

        i++;

        if (is_time_elapsed(timestamp/TIMESTAMP_DIV_COEF_IN_MS)) {
            if (callback != null) {
                callback.onFinishedCollectionData(valueArr1, valueArr2, valueArr3, i,
                        FinishedCollectionDataStates.TIME_ELAPSED);
            }
            clean();
        }

    }

    public void addValues(long timestamp, float array[]) {
        if (is_full()) {
            if (callback != null) {
                callback.onFinishedCollectionData(valueArr1, valueArr2, valueArr3, i,
                        FinishedCollectionDataStates.ARRAY_OVERFLOW);
            }
            clean();
        }

        if (is_empty()) first_value_timestamp = timestamp/TIMESTAMP_DIV_COEF_IN_MS;

        valueArr1[i] = array[0];
        valueArr2[i] = array[1];
        valueArr3[i] = array[2];

        i++;

        if (is_time_elapsed(timestamp/TIMESTAMP_DIV_COEF_IN_MS)) {
            if (callback != null) {
                callback.onFinishedCollectionData(valueArr1, valueArr2, valueArr3, i,
                        FinishedCollectionDataStates.TIME_ELAPSED);
            }
            clean();
        }

    }

    public void addValues(SensorEvent event) {
        if (is_full()) {
            if (callback != null) {
                callback.onFinishedCollectionData(valueArr1, valueArr2, valueArr3, i,
                        FinishedCollectionDataStates.ARRAY_OVERFLOW);
            }
            clean();
        }

        if (is_empty()) first_value_timestamp = event.timestamp/TIMESTAMP_DIV_COEF_IN_MS;

        valueArr1[i] = event.values[0];
        valueArr2[i] = event.values[1];
        valueArr3[i] = event.values[2];

        i++;

        if (is_time_elapsed(event.timestamp/TIMESTAMP_DIV_COEF_IN_MS)) {
            if (callback != null) {
                callback.onFinishedCollectionData(valueArr1, valueArr2, valueArr3, i,
                        FinishedCollectionDataStates.TIME_ELAPSED);
            }
            clean();
        }

    }

    private void clean() {
        i = 0;
    }

}
