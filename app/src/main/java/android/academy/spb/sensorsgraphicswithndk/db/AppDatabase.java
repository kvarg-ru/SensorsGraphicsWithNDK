package android.academy.spb.sensorsgraphicswithndk.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by User on 27.06.2018.
 */

@Database(entities = {Measurement.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MeausrementDao meausrementDao();

    private static final String DB_NAME = "MeasurementsDB";

    private static AppDatabase mINSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (mINSTANCE == null) {
            mINSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    //.allowMainThreadQueries()
                    .build();
        }
        return mINSTANCE;
    }

    public static void deleteInstance() {
        mINSTANCE = null;
    }

}
