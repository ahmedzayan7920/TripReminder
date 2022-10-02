package com.example.tripreminder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddNotesActivity extends AppCompatActivity {

    private FloatingActionButton addNote;
    private ListView list;
    private AutoCompleteTextView txtNote;
    private Button btn;

    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        list = findViewById(R.id.bubble_list_view);
        txtNote = findViewById(R.id.txtNote);
        addNote = findViewById(R.id.fab_add_note);
        btn = findViewById(R.id.btn_finish);
        if (!AddAndEditActivity.notes.isEmpty()) {
            if (AddAndEditActivity.notes.get(0).equals(""))
                AddAndEditActivity.notes.remove(0);
        }

        adapter = new ArrayAdapter(AddNotesActivity.this, android.R.layout.simple_list_item_1, AddAndEditActivity.notes);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = txtNote.getText().toString();
                if (!s.isEmpty()) {
                    AddAndEditActivity.notes.add(s);
                    txtNote.setText("");
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.i("012301230123654", AddAndEditActivity.notes.toString());
                } else {
                    txtNote.setError("Empty!!!");
                }
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddNotesActivity.this);
                builder.setTitle("Deleting Notes")
                        .setMessage("Are you Sure of Deleting this Note")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AddAndEditActivity.notes.remove(i);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}