package android.academy.spb.sensorsgraphicswithndk.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

/**
 * Created by User on 27.06.2018.
 */

@Entity
public class Measurement {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int sensorType;
    private float data;

    @TypeConverters({DateConverter.class})
    private Date timestamp;

    public Measurement() {}

    public Measurement(int sensorType, float data, Date timestamp) {
        this.sensorType = sensorType;
        this.data = data;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    public int getSensorType() {
        return sensorType;
    }

    public void setSensorType(int sensorType) {
        this.sensorType = sensorType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
