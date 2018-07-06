package android.academy.spb.sensorsgraphicswithndk.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by User on 27.06.2018.
 */

@Dao
public interface MeausrementDao {

    @Query("SELECT * FROM Measurement ORDER BY id DESC")
    List<Measurement> getAll();

    @Query("SELECT * " +
            "FROM Measurement " +
            "WHERE datetime(timestamp/1000, 'unixepoch') > datetime('now', '-5 minutes') " +
            "ORDER BY id DESC")
    List<Measurement> getAllForPeriodFirst();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertAll(Measurement... measurements);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void Insert(Measurement measurement);

    @Delete
    void delete(Measurement measurement);

    @Query("DELETE FROM Measurement")
    void deleteAll();

}
