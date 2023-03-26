package android.wetterapp;


import androidx.room.Database;
import androidx.room.RoomDatabase;

// Datenbank-Klasse
//
// Definiert die Datenbank-Konfiguration.
// Ein Objekt dieser Klasse ist der Hauptzugriffspunkt für die Datenbank.
// Die Tabellenstruktur wird in der Datei Wetter.java festgelegt, auf die die Annotation @Database(entities = {Wetter.class} ... verweist.

@Database(entities = {Wetter.class}, version = 1)
public abstract class WetterDatabase extends RoomDatabase {
    public abstract WetterDao wetterDao();
}

