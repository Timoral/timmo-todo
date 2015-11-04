package xyz.timmo.todo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        Preference export = findPreference("export");

        export.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ExportTableToCSV("items");
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ExportTableToCSV(String table) {
        ItemDatabaseHandler notesDatabaseHandler = new ItemDatabaseHandler(SettingsActivity.this);

        File exportDir = new File(Environment.getExternalStorageDirectory().getPath(), "");

        String filename = "ToDoExported" + table + ".csv";

        File file = new File(exportDir, filename);

        try {
            //file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            SQLiteDatabase db = notesDatabaseHandler.getReadableDatabase();

            Cursor curCSV = db.rawQuery("SELECT * FROM " + table, null);
            //  int c = curCSV.getColumnCount();
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1),
                        curCSV.getString(2)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            Toast.makeText(this, filename + " saved to sdcard/", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
    }


}