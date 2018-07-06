package android.academy.spb.sensorsgraphicswithndk.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by User on 03.07.2018.
 */

public class DateConverter {

    @TypeConverter
    public long dateToLong(Date date) {
        return date.getTime();
    }

    @TypeConverter
    public Date longToData(long unixtimestamp) {
        return new Date(unixtimestamp);
    }

}
