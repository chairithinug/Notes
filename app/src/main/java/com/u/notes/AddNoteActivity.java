package com.u.notes;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNoteActivity extends FragmentActivity {

    public static final String TAG = "AddNoteActivity";

    public EditText for_whom;
    public EditText title;
    public EditText data;

    public boolean anyMod = false;
    public boolean isCreate;
    public static NotesInstance argInstance;
    public static FragmentManager fm;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        fm = getSupportFragmentManager();

        anyMod = false;

        for_whom = findViewById(R.id.et_for_whom);
        title = findViewById(R.id.et_title);
        data = findViewById(R.id.et_data);

//         Font
//        Typeface tf = ResourcesCompat.getFont(getApplicationContext(),
//                R.font.angsa);
//        for_whom.setTypeface(tf);
//        title.setTypeface(tf);
//        data.setTypeface(tf);

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
//        create_save.setTypeface(tf);
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
        // try create an unempty note, ask before exiting
        if (isCreate && (!for_whom.getText().toString().isEmpty() || !title.getText().toString().isEmpty() || !data.getText().toString().isEmpty()))
            anyMod = true;
        // edit and make changes, ask before exiting
        if (!isCreate && !isSameText(argInstance))
            anyMod = true;
        if (anyMod) {
            BackOnCreateDialogFragment frag = new BackOnCreateDialogFragment();
            frag.show(AddNoteActivity.fm, "BackOnCreateDialog");
        } else {
            Log.d(TAG, "No Mod, Back Pressed");
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    public NotesInstance createNote() {
        String date = getDate();
        NotesInstance ret = new NotesInstance();
        ret.setCreatedDate(date);
        ret.setLastModifiedDate(date);
        ret.setForWhom(for_whom.getText().toString().isEmpty() ? "Me" : for_whom.getText().toString());
        ret.setTitle(title.getText().toString().isEmpty() ? "Untitled Note" : title.getText().toString());
        ret.setData(data.getText().toString()); // will not be empty, required
        return ret;
    }

    public NotesInstance modifyNote(NotesInstance ni) {
        String date = getDate();
        ni.setLastModifiedDate(date);
        ni.setForWhom(for_whom.getText().toString().isEmpty() ? "Me" : for_whom.getText().toString());
        ni.setTitle(title.getText().toString().isEmpty() ? "Untitled Note" : title.getText().toString());
        ni.setData(data.getText().toString()); // will not be empty, required
        return ni;
    }

    // Compare the old instance data with the current data in fields
    public boolean isSameText(NotesInstance ni1) {
        return ni1.getData().equals(data.getText().toString()) && ni1.getTitle().equals(title.getText().toString()) && ni1.getForWhom().equals(for_whom.getText().toString());
    }

    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        String getDate = Calendar.getInstance().getTime().toString();
        String date = dateFormat.format(Calendar.getInstance().getTime());

//        Log.d(TAG, date);
//        long mili = Calendar.getInstance().getTimeInMillis();
//        Date dd = new Date(mili);
//        String d = dateFormat.format(dd);
//        Log.d(TAG, dd.toString());
//        Log.d(TAG, d);
        return date;
    }
}
