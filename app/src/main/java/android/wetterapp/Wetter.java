package android.wetterapp;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

// Entity-Klasse
//
// Definiert die Form der Entities und damit eine Tabelle:
// Ein Entity-Objekt entspricht einer Zeile der Tabelle.

@Entity
public class Wetter {

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "zeitstempel")
    public Long zeitstempel;


    @ColumnInfo(name = "temperatur")
    public double gradzahl = 0;

    @ColumnInfo(name = "regen")
    public boolean regen = false;

    @ColumnInfo(name = "wind")
    public boolean wind = false;

    Wetter(int id, Long zeitstempel, double gradzahl, boolean regen, boolean wind){
        this.id = id;
        this.zeitstempel = zeitstempel;
        this.gradzahl = gradzahl;
        this.regen = regen;
        this.wind = wind;
    }

}

