package android.wetterapp;


import androidx.room.Room;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

public class WetterDaten extends ListActivity {

    String [] optionsliste = {"Zeige alle Einträge", "WetterDaten an RegenTagen", "Exportiere DB in TXT", "Zeige Inhalt der TXT"};


    WetterDatabase wetterDatabase;
    WetterDao wetterDao;

    Wetter wetter;
    String regen = "Regen";
    String wind = "Wind";
    String txtFile = "DBInTXT.txt";
    private EditText ausgabeFeld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {  // mit Initialisierung der Datenbank

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wetter_daten);

        // Erzeugt eine Datenbank-Instanz ...

        wetterDatabase = Room.databaseBuilder(getApplicationContext(), WetterDatabase.class,"wetter-database" ).allowMainThreadQueries().build();

        wetterDao = wetterDatabase.wetterDao();

        ausgabeFeld = findViewById(R.id.ausgabe);
        ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, optionsliste);
        setListAdapter(listAdapter);

    }



    protected void onListItemClick(ListView liste, View datenElement, int position, long id) {

        super.onListItemClick(liste, datenElement, position, id);

        CharSequence optionsText = ((TextView)datenElement).getText();

        if(optionsText.equals("Zeige alle Einträge")){
            String ausgabeString = new String();
            String regenString = new String();
            String windString = new String();
            List<Wetter> entries = wetterDao.getAll();

            for (Iterator<Wetter> it = entries.iterator(); it.hasNext();) {
                wetter = it.next();

                if(wetter.regen){
                    regenString = " "+ regen;
                } else {
                    regenString = " kein Regen";
                }
                if(wetter.wind){
                    windString = " "+ wind;
                } else {
                    windString = " kein Wind";
                }

                String zeitSimple = String.format("%1$TH:%1$TM:%1$TS", wetter.zeitstempel);
                ausgabeString += wetter.id + " | " + zeitSimple + " - grad: " + wetter.gradzahl + " - " + regenString+ " - " + windString + "\n";
            }

            ausgabeFeld.setText(ausgabeString);
        }
        if(optionsText.equals("WetterDaten an RegenTagen")){
            List<Wetter> entries = wetterDao.getAlleRegenTagen();
            String ausgabeString = new String();
            String regenString = new String();

            for (Iterator<Wetter> it = entries.iterator(); it.hasNext();) {
                wetter = it.next();
                if (wetter.regen) {
                    regenString = " " + regen;
                } else {
                    regenString = " kein Regen";
                }
                String windString = wetter.wind? wind : "kein Wind";
                String zeitSimple = String.format("%1$TH:%1$TM:%1$TS", wetter.zeitstempel);
                ausgabeString += wetter.id + " | " + zeitSimple + " - grad: " + wetter.gradzahl + " - " + regen + " - " + windString + "\n";
            }
            ausgabeFeld.setText(ausgabeString);
        }
        if(optionsText.equals("Exportiere DB in TXT")){
            String ausgabeString = new String();
            String regenString = new String();
            String windString = new String();
            List<Wetter> entries = wetterDao.getAll();

            FileOutputStream fileOutputStream = null;
            PrintWriter printWriter = null;

            try {
                fileOutputStream = openFileOutput(txtFile, 0);
                printWriter = new PrintWriter(fileOutputStream);
            } catch ( FileNotFoundException e) {
                e.printStackTrace();
            }

            for (Iterator<Wetter> it = entries.iterator(); it.hasNext();) {
                wetter = it.next();

                regenString = wetter.regen ? regen : "kein Regen";
                windString = wetter.wind ? wind : "kein Wind";

                String zeitSimple = String.format("%1$TH:%1$TM:%1$TS", wetter.zeitstempel);
                ausgabeString = wetter.id + " | " + zeitSimple + " - grad: " + wetter.gradzahl + " - " + regenString+ " - " + windString + "\n";

                try {
                    printWriter.println(ausgabeString);
                    Toast.makeText(getApplicationContext(), "Schreiben in TxtDatei ", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this,"Fehler beim Schreiben: "+e.getClass(), Toast.LENGTH_LONG).show();
                    Log.v("DEMO",">>>> Fehler beim Schreiben: "+e.getClass());
                }
            }

            try {
                printWriter.close();
                fileOutputStream.close();
            } catch (Exception e) {
                Log.v("DEMO",">>>> Fehler beim Schliessen: "+e.getClass());
            }
        }

        if(optionsText.equals("Zeige Inhalt der TXT")){
            String leseString = new String();
            String ausgabeString = new String();

            FileInputStream fileInputStream = null;
            BufferedReader bufferedReader = null;
            try {
                fileInputStream = openFileInput(txtFile);
                bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            try {

                while ( (leseString = bufferedReader.readLine()) != null) {
                    ausgabeString += leseString + "\n";
                }
                Toast.makeText(this,"Lesen aus TxtDatei: ", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this,"Fehler beim Lesen: "+e.getClass(), Toast.LENGTH_LONG).show();
                Log.v("DEMO",">>>> Fehler beim Lesen: "+e.getClass());
            }

            ausgabeFeld.setText(ausgabeString);

            try {
                fileInputStream.close();
                bufferedReader.close();
            } catch (Exception e) {
                Toast.makeText(this,"Fehler beim Schließen: "+e.getClass(), Toast.LENGTH_LONG).show();
                Log.v("DEMO",">>>> Fehler beim Schließen: "+e.getClass());
            }

        }

    }

}
