package android.wetterapp;

import static android.content.ContentValues.TAG;

import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity {

    EditText temperatur;        // Hier wird die Temperatur eingegeben
    Button uebernehmen;         // Danach wird die Anzeige aktualisiert

    CheckBox regenCheckbox;     // Regnet es?
    CheckBox windCheckbox;      // Ist es windig?
    TextView aktuellesWetter;   // Zusammenfassung des eingegebenen Wetters
    TextView anzahl;
    TextView dTemp;
    TextView anzRegen;
    TextView anzWind;

    double gradzahl = 0;
    String grad;
    boolean regen = false;
    boolean wind = false;
    double summeTemperatur = 0;
    int anzahlEingaben = 0;
    int anzahlRegen = 0;
    int anzahlWind = 0;

    WetterDatabase wetterDb;
    WetterDao wetterDao;

    ArrayList<Wetter> wetterDatenListe = new ArrayList<Wetter>();


    TextView standort;
    String aktstandort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setTitle("Wetter App");


        // Verbinden der Views
        temperatur = (EditText) findViewById(R.id.tempEingabe);
        uebernehmen = (Button) findViewById(R.id.uebernehmenButton);
        regenCheckbox = (CheckBox) findViewById(R.id.regenCheckbox);
        windCheckbox = (CheckBox) findViewById(R.id.windCheckbox);
        aktuellesWetter = (TextView) findViewById(R.id.wetterText);
        anzahl = (TextView) findViewById(R.id.anzahl);
        dTemp = (TextView) findViewById(R.id.dTemp);
        anzRegen = (TextView) findViewById(R.id.anzRegen);
        anzWind = (TextView) findViewById(R.id.anzWind);

        standort = (TextView) findViewById(R.id.standort);

        wetterDb = Room.databaseBuilder(getApplicationContext(), WetterDatabase.class, "wetter-database").allowMainThreadQueries().build();

        wetterDao = wetterDb.wetterDao();

        (new Thread() {
            public void run() {
                wetterDb.clearAllTables();
            }
        }).start();


    }

    public String onLocationChanged(Location location) {
        // Get address from location
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                // Display address in a Toast message
                //Toast.makeText(this, "Current location: " + address, Toast.LENGTH_LONG).show();
                String address = addresses.get(0).getAddressLine(0);
                return address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown location";
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater mi = new MenuInflater(this);
        mi.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, WetterDaten.class));
        return true;
    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putDouble("Temperatur",gradzahl);
        savedInstanceState.putBoolean("Regen", regen);
        savedInstanceState.putBoolean("Wind", wind);
        savedInstanceState.putDouble("SummeTemperatur",summeTemperatur);
        savedInstanceState.putInt("Anzahl Regen", anzahlRegen);
        savedInstanceState.putInt("Anzahl Wind", anzahlWind);
        savedInstanceState.putInt("Anzahl Eingaben", anzahlEingaben);

        savedInstanceState.putString("standort", aktstandort);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        regen = savedInstanceState.getBoolean("Regen");
        wind = savedInstanceState.getBoolean("Wind");
        gradzahl = savedInstanceState.getDouble("Temperatur");
        summeTemperatur = savedInstanceState.getDouble("SummeTemperatur");
        anzahlRegen = savedInstanceState.getInt("Anzahl Regen");
        anzahlWind = savedInstanceState.getInt("Anzahl Wind");
        anzahlEingaben = savedInstanceState.getInt("Anzahl Eingaben");

        aktstandort = savedInstanceState.getString("standort");
    }

    public void clickReaktion(View v) {

        // Beim Übernehmen Button wird das aktuelle Wetter eingelesen und zusammengefasst ausgegeben
        if (v.getId()==R.id.uebernehmenButton ) {
            Log.v("DEMO","---> clickReaktion() <--- ");
            try {
                grad = temperatur.getText().toString();
                //wenn keine Eingaben im Feld "pflichtfeld"
                if (grad.length() == 0) {
                    temperatur.setError("Pflichtfeld*");
                }
                gradzahl = Double.parseDouble(grad);
                summeTemperatur += gradzahl;
                regen = regenCheckbox.isChecked();
                wind = windCheckbox.isChecked();
                aktuellesWetter.setText(ausgeben());
                anzahlEingaben++;
                String temp = "" + anzahlEingaben;
                anzahl.setText(temp);
                double durchschnitt = summeTemperatur / anzahlEingaben;
                temp = "" + durchschnitt;
                dTemp.setText(temp);
                temp = "" + anzahlRegen;
                anzRegen.setText(temp);
                temp = "" + anzahlWind;
                anzWind.setText(temp);


                standort.setText(ermittlelocation());
                    /*hier ist das id des neuen Datensatzes
                    wird mit der aktuelle Laenge der Liste + 1
                     */
                int ids = wetterDao.getAll().size()+1;


                Wetter neueWetterDaten = new Wetter(
                        ids++,
                        System.currentTimeMillis(),
                        gradzahl,
                        regen,
                        wind);

                wetterDatenListe.add(neueWetterDaten);
                wetterDao.insertAll(neueWetterDaten);


            }catch (Exception e){

                System.out.println(e.getMessage());

            }


            if (gradzahl <=4 && grad.length() !=0)
                Toast.makeText(this,"Vorsicht vor Glätte!", Toast.LENGTH_LONG).show();

        }
        else if(v.getId() == R.id.resetButton){
            temperatur.setText("");
            summeTemperatur = 0;
            gradzahl = 0;
            regenCheckbox.setChecked(false);
            regen = false;
            windCheckbox.setChecked(false);
            wind = false;
            aktuellesWetter.setText("aktuelles Wetter");
            anzahlRegen = 0;
            anzahlWind = 0;
            dTemp.setText("");
            anzahlEingaben = 0;
            String temp = ""+0;
            anzRegen.setText(temp);
            anzWind.setText(temp);
            anzahl.setText(temp);
            standort.setText("");
        }


    }

    public String ausgeben(){
        String istRegen;
        String istWind;
        Log.v("DEMO","---> ausgeben() <--- ");
        if (regen) {
            istRegen = "Regen, ";
            anzahlRegen++;
        }
        else
            istRegen = "kein Regen, ";
        if (wind) {
            istWind = "Wind";
            anzahlWind++;
        }
        else
            istWind = "kein Wind";
        String ausgabe;



        ausgabe = "In " + ermittlelocation() + " ist " + grad+" Grad, "+istRegen+istWind;
        return ausgabe;
    }
    public String ermittlelocation(){
        String location = "";
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions not granted, return a default value
            return "Unknown location, 0 Grad, kein Regen, kein Wind";
        }

        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //Toast.makeText(this, "Current location: " + lastLocation.getLatitude()+" "+ lastLocation.getLongitude(), Toast.LENGTH_LONG).show();


        if (lastLocation != null) {
            location = onLocationChanged(lastLocation);
        }

        return location;
    }

}