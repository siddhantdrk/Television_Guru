package com.app.televisionguru.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.app.televisionguru.R;
import com.app.televisionguru.dao.Task;
import com.app.televisionguru.room.DatabaseClient;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddNameActivity extends AppCompatActivity {

    private AppCompatSpinner sp_types;
    EditText etName;
    private ArrayList<String> type_list = new ArrayList<>();
    String selectedItem = "Animes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_name);

        type_list.add("Animes");
        type_list.add("Movies");
        type_list.add("Television Shows");

        sp_types = findViewById(R.id.sp_types);
        etName = findViewById(R.id.etName);

        ((MaterialToolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(view -> finish());
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, type_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_types.setAdapter(adapter);
        sp_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = type_list.get(i);
                Log.e("<<!!>>", selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findViewById(R.id.mbAdd).setOnClickListener(view -> {
            if (etName.getText().toString().length() > 0 && selectedItem.length() > 0) {
                SaveTask st = new SaveTask();
                st.execute();
            } else {
                Toast.makeText(AddNameActivity.this, "Please fill the details", Toast.LENGTH_LONG).show();
            }
        });

    }

    class SaveTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            //creating a task
            Task task = new Task();
            task.setName(etName.getText().toString());
            task.setType(selectedItem);
            task.setVisible(false);
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .taskDao()
                    .insert(task);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
            Toast toast = Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 250);
            toast.show();
        }
    }

}