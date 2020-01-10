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

import androidx.core.content.res.ResourcesCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNoteActivity extends Activity {

    public static final String TAG = "AddNoteActivity";

    public EditText for_whom;
    public EditText title;
    public EditText data;

    public boolean anyMod = false;

    public boolean isCreate;

    public static NotesInstance argInstance;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        anyMod = false;

        for_whom = findViewById(R.id.et_for_whom);
        title = findViewById(R.id.et_title);
        data = findViewById(R.id.et_data);

//         Font
        Typeface tf = ResourcesCompat.getFont(getApplicationContext(),
                R.font.angsa);
        for_whom.setTypeface(tf);
        title.setTypeface(tf);
        data.setTypeface(tf);

        final Intent thisIntent = getIntent();
        isCreate = thisIntent.getExtras().getBoolean("isCreate");
        argInstance = (NotesInstance) thisIntent.getExtras().getParcelable("notesInstance");
        if (!isCreate) {
            Log.d(TAG, "From Read-Edit");
            for_whom.setText(argInstance.getForWhom());
            title.setText(argInstance.getTitle());
            data.setText(argInstance.getData());
        } else {
            Log.d(TAG, "From Create");
        }

        Button create_save = findViewById(R.id.btn_create_save);
        // coming from editing notes
        if (!isCreate) {
            String toBeDisplayed = "Save";
            create_save.setText(toBeDisplayed);
        }
        create_save.setTypeface(tf);
        create_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (data.getText().toString().isEmpty()) {
                    data.setError("Must Not Be Empty");
                } else {
                    Intent intent = new Intent();
                    NotesInstance thisInstance = isCreate ? createNote() : modifyNote(argInstance);
                    intent.putExtra("notesInstance", thisInstance);
                    intent.putExtra("isCreate", isCreate);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    // to get rid of default back button on Android
    @Override
    public void onBackPressed() {

        // try create an unempty note, ask before
        if (isCreate && (!for_whom.getText().toString().isEmpty() || !title.getText().toString().isEmpty() || !data.getText().toString().isEmpty()))
            anyMod = true;
        // edit and make changes, ask before
        if (!isCreate && !isSameText(argInstance))
            anyMod = true;
        if (anyMod) {
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
        } else {
            Log.d(TAG, "No Mod, Back Pressed");
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
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

    public NotesInstance modifyNote(NotesInstance ni) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String date = dateFormat.format(Calendar.getInstance().getTime());

        ni.setLastModifiedDate(date);
        ni.setForWhom(for_whom.getText().toString().isEmpty() ? "Me" : for_whom.getText().toString());
        ni.setTitle(title.getText().toString().isEmpty() ? "Untitled Note" : title.getText().toString());
        ni.setData(data.getText().toString()); // will not be empty, required
        return ni;
    }

    public boolean isSameText(NotesInstance ni1) {
//        if (ni1 != null && ni2 != null)
            return ni1.getData().equals(data.getText().toString()) && ni1.getTitle().equals(title.getText().toString()) && ni1.getForWhom().equals(for_whom.getText().toString());
//        else
//            return false;
    }
}
