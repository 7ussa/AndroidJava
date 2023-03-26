package android.wetterapp;



import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// DAO-Interface (DAO = Data Access Object)
//
// Interface für Data Access Objects (DAOs) mit Köpfen von Methoden zum Zugriff auf die Datenbank.
// Eine Klasse, die dieses Interface implementiert und damit Datenbank-Zugriffe ermöglicht, wird automatisch generiert.

@Dao
public interface WetterDao {

    // Für eine Abfrage-Methode muss man nur den Kopf mit dem entsprechenden SELECT-Befehl als Annotation hinschreiben.
    // Der zugehörige Methodenkörper mit dem Datenbank-Zugriff wird automatisch generiert.
    // Dabei wird insbesondere auch die Syntax des SELECT-Befehls geprüft.

    @Query("SELECT * FROM wetter")
    List<Wetter> getAll();

    @Query("SELECT * FROM wetter WHERE regen = 1")
    List<Wetter> getAlleRegenTagen();

    // Für Insert- und Delete-Methoden genügen entsprechende Annotationen mit jeweils einem einfachen Schlüsselwort.
    // Auch ihre Körper werden automatisch generiert.

    @Insert
    void insertAll(Wetter... entries);

    @Delete
    void delete(Wetter entry);

}


