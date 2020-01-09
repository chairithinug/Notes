package com.u.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddNoteActivity extends Activity {

    public static final String TAG = "AddNoteActivity";

    public EditText for_whom;
    public EditText title;
    public EditText data;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        for_whom = findViewById(R.id.et_for_whom);
        title = findViewById(R.id.et_title);
        data = findViewById(R.id.et_data);

//         Font
        Typeface tf = ResourcesCompat.getFont(getApplicationContext(),
                R.font.angsa);
        for_whom.setTypeface(tf);
        title.setTypeface(tf);
        data.setTypeface(tf);

        Button create = findViewById(R.id.btn_create);
        create.setTypeface(tf);
        create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (data.getText().toString().isEmpty()) {
                    data.setError("Must Not Be Empty");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("notesInstance", createNote());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "Yes, delete!");
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "No, stay!");
            }
        });

        builder.setMessage("Do you want to exit without saving this note?")
                .setTitle("Action Required");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public NotesInstance createNote() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        String date = Calendar.getInstance().getTime().toString();
        String date = dateFormat.format(Calendar.getInstance().getTime());
        NotesInstance ret = new NotesInstance();
        ret.setCreatedDate(date);
        ret.setLastModifiedDate(date);
        ret.setForWhom(for_whom.getText().toString().isEmpty() ? "Me" : for_whom.getText().toString());
        ret.setTitle(title.getText().toString().isEmpty() ? "Untitled Note" : title.getText().toString());
        ret.setData(data.getText().toString()); // will not be empty, required
        return ret;
    }
}
