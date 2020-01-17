package com.u.notes;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Environment;
import android.widget.PopupMenu;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.u.notes.NoteDBHelper.DATABASE_NAME;

public class MainActivity extends FragmentActivity {

    public static ArrayList<NotesInstance> notesList;
    public static final String TAG = "MAIN";

    public String selectedTitle = "Last Modified Date";
    public String asc_desc = " DESC"; // descending order default

    public static NotesAdapter adapter;
    public static RecyclerView recyclerView;
    public Button btnShowMenu;
    public Context context;

    public static FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        context = getApplicationContext();

        // ask for permission
        askWriteExternalStoragePermission();

        FloatingActionButton fab_add = findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(view.getContext(), AddNoteActivity.class);
                newIntent.putExtra("isCreate", true);
                startActivityForResult(newIntent, ConstantVar.REQUEST_CODE_ADD_NOTE);
            }
        });
        FloatingActionButton fab_search = findViewById(R.id.fab_search);
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newIntent = new Intent(view.getContext(), SearchNoteActivity.class);
                startActivityForResult(newIntent, ConstantVar.REQUEST_CODE_SEARCH_NOTE);

            }
        });

        FloatingActionButton fab_export = findViewById(R.id.fab_export);
        fab_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    exportNotes(notesList);
                    Snackbar.make(recyclerView, "Note(s) Exported", Snackbar.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        });

        btnShowMenu = findViewById(R.id.btn_show_dropdown);
        btnShowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu dropDownMenu = new PopupMenu(context, btnShowMenu);
                dropDownMenu.getMenuInflater().inflate(R.menu.note_drop_down, dropDownMenu.getMenu());
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String TAG = "DropDownMenu";
                        selectedTitle = menuItem.getTitle().toString();
                        Toast.makeText(context, "Sorted By " + selectedTitle, Toast.LENGTH_SHORT).show();
                        refreshRecyclerView();
                        Log.d(TAG, selectedTitle);
                        return true;
                    }
                });
                dropDownMenu.show();
            }
        });

//        NoteDBHelper.deleteDatabase(context); // TODO

        recyclerView = findViewById(R.id.rv_home);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        notesList = new ArrayList<NotesInstance>();

        loadInstanceData(NotesInstanceContract.NotesEntry.COLUMN_NAME_LAST_MODIFIED_DATE);

        refreshRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            // need to point to any view in the main activity
            Snackbar.make(recyclerView, "Error Adding/Editing a Note", Snackbar.LENGTH_SHORT).show();
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ConstantVar.REQUEST_CODE_ADD_NOTE) {
                Bundle ret = data.getExtras();
                NotesInstance ni = (NotesInstance) ret.getParcelable("notesInstance");
                boolean isCreate = ret.getBoolean("isCreate");
                String toastToBeDisplayed;
                if (isCreate) {
                    saveInstanceData(ni);
                    toastToBeDisplayed = "Note Added";
                } else {
                    modifyInstanceData(this, ni);
                    toastToBeDisplayed = "Note Edited";
                }
                Snackbar.make(recyclerView, toastToBeDisplayed, Snackbar.LENGTH_SHORT).show();
                refreshRecyclerView();
            } else if (requestCode == ConstantVar.REQUEST_CODE_SEARCH_NOTE) {
                // TODO Return
//            Bundle ret = data.getExtras();
//            NotesInstance ni = (NotesInstance) ret.getParcelable("notesInstance");
//            saveInstanceData(ni);
                Snackbar.make(recyclerView, "Return from Search", Snackbar.LENGTH_SHORT).show();
                refreshRecyclerView();
            }
        } else {
            Log.d(TAG, "Result First User");
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_ascending:
                if (checked)
                    asc_desc = " ASC";
                refreshRecyclerView();
                break;
            case R.id.radio_descending:
                if (checked)
                    asc_desc = " DESC";
                refreshRecyclerView();
                break;
        }
    }


    private void saveInstanceData(NotesInstance ni) {
        final NoteDBHelper dbHelper = new NoteDBHelper(context);
        final SQLiteDatabase writeDB = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_CREATED_DATE, ni.getCreatedDate());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_LAST_MODIFIED_DATE, ni.getLastModifiedDate());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_FOR_WHOM, ni.getForWhom());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_TITLE, ni.getTitle());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_DATA, ni.getData());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_HAS_POSTED, 0); // where 0 is false for sqlite
        writeDB.insert(NotesInstanceContract.NotesEntry.TABLE_NAME, null, values);

        notesList.add(ni);
        writeDB.close();
    }

    private void loadInstanceData(String sortOrder) {
        final NoteDBHelper dbHelper = new NoteDBHelper(context);
        final SQLiteDatabase readDB = dbHelper.getReadableDatabase();
        String[] projection = {NotesInstanceContract.NotesEntry.COLUMN_NAME_CREATED_DATE, NotesInstanceContract.NotesEntry.COLUMN_NAME_LAST_MODIFIED_DATE, NotesInstanceContract.NotesEntry.COLUMN_NAME_FOR_WHOM, NotesInstanceContract.NotesEntry.COLUMN_NAME_TITLE, NotesInstanceContract.NotesEntry.COLUMN_NAME_DATA, NotesInstanceContract.NotesEntry.COLUMN_NAME_HAS_POSTED};
        Cursor cursor = readDB.query(NotesInstanceContract.NotesEntry.TABLE_NAME, projection, null, null, null, null, sortOrder + asc_desc);
        while (cursor.moveToNext()) {
            Log.d(TAG, "loadInstanceData: " + cursor.getString(0));
            NotesInstance ni = new NotesInstance();
            ni.setCreatedDate(cursor.getString(0));
            ni.setLastModifiedDate(cursor.getString(1));
            ni.setForWhom(cursor.getString(2));
            ni.setTitle(cursor.getString(3));
            ni.setData(cursor.getString(4));
            notesList.add(ni);
        }
        cursor.close();
        readDB.close();
    }

    public static void modifyInstanceData(Context context, NotesInstance new_ni) {
        final NoteDBHelper dbHelper = new NoteDBHelper(context);
        final SQLiteDatabase modifyDB = dbHelper.getReadableDatabase();

        // FIXME Consider moving these out of the function
        notesList.remove(AddNoteActivity.argInstance); // remove old instance
        notesList.add(new_ni); // create a new one

        // FIXME Also
        if (SearchNoteActivity.active) {
            SearchNoteActivity.searchNotesList.remove(AddNoteActivity.argInstance);
            SearchNoteActivity.searchNotesList.add(new_ni);
        }

        ContentValues values = new ContentValues();
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_CREATED_DATE, new_ni.getCreatedDate());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_LAST_MODIFIED_DATE, new_ni.getLastModifiedDate());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_FOR_WHOM, new_ni.getForWhom());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_TITLE, new_ni.getTitle());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_DATA, new_ni.getData());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_HAS_POSTED, 0); // where 0 is false for sqlite

        modifyDB.update(NotesInstanceContract.NotesEntry.TABLE_NAME, values, NotesInstanceContract.NotesEntry.COLUMN_NAME_CREATED_DATE + "=?", new String[]{new_ni.getCreatedDate()});
        modifyDB.close();
    }

    public static void removeFromDB(View view, NotesInstance ni) {
        final NoteDBHelper dbHelper = new NoteDBHelper(view.getContext());
        final SQLiteDatabase removeDB = dbHelper.getReadableDatabase();
        removeDB.delete(NotesInstanceContract.NotesEntry.TABLE_NAME, NotesInstanceContract.NotesEntry.COLUMN_NAME_CREATED_DATE + "=?", new String[]{ni.getCreatedDate()});
        removeDB.close();
    }

    public static void addBackToDB(View view, NotesInstance ni) {
        final NoteDBHelper dbHelper = new NoteDBHelper(view.getContext());
        final SQLiteDatabase addBackToDB = dbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_CREATED_DATE, ni.getCreatedDate());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_LAST_MODIFIED_DATE, ni.getLastModifiedDate());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_FOR_WHOM, ni.getForWhom());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_TITLE, ni.getTitle());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_DATA, ni.getData());
        values.put(NotesInstanceContract.NotesEntry.COLUMN_NAME_HAS_POSTED, 0); // where 0 is false for sqlite

        addBackToDB.insert(NotesInstanceContract.NotesEntry.TABLE_NAME, null, values);
        addBackToDB.close();
    }

    private void refreshRecyclerView() {
        notesList = new ArrayList<NotesInstance>();
        if (selectedTitle.equals("Created Date")) {
            loadInstanceData(NotesInstanceContract.NotesEntry.COLUMN_NAME_CREATED_DATE);
        } else if (selectedTitle.equals("Last Modified Date")) {
            loadInstanceData(NotesInstanceContract.NotesEntry.COLUMN_NAME_LAST_MODIFIED_DATE);
        } else if (selectedTitle.equals("For Whom")) {
            loadInstanceData(NotesInstanceContract.NotesEntry.COLUMN_NAME_FOR_WHOM);
        } else if (selectedTitle.equals("Title")) {
            loadInstanceData(NotesInstanceContract.NotesEntry.COLUMN_NAME_TITLE);
        } else if (selectedTitle.equals("Data")) {
            loadInstanceData(NotesInstanceContract.NotesEntry.COLUMN_NAME_DATA);
        } else {
            loadInstanceData(NotesInstanceContract.NotesEntry.COLUMN_NAME_LAST_MODIFIED_DATE);
            Log.d(TAG, "Default");
        }
        String toBeShown = "Sorted By " + selectedTitle;
        btnShowMenu.setText(toBeShown);
        adapter = new NotesAdapter(notesList);
        recyclerView.setAdapter(adapter);
    }

    public static void exportDB(Context context) throws IOException {
        //Open your local db as the input stream
        String inFileName = context.getDatabasePath(DATABASE_NAME).getPath();
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);

        String outFileName = Environment.getExternalStorageDirectory() + "/" + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream output = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        //Close the streams
        output.flush();
        output.close();
        fis.close();
    }

    public static void exportNotes(ArrayList<NotesInstance> noteslist) throws IOException {
        String fileName = "Notes.txt";
        JSONArray ja = new JSONArray();
        for (NotesInstance ni : noteslist) {
            Log.d(TAG, ni.toString());
            ja.put(ni.toJSON());
        }
        Log.d(TAG, ja.toString());

        String toBeExported = ja.toString();

        File outFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
        Log.d(TAG, outFile.getPath());
        FileWriter fw = new FileWriter(outFile);
        fw.write(toBeExported);
        fw.flush();
        fw.close();
    }

    public void askWriteExternalStoragePermission() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Snackbar.make(findViewById(R.id.rv_home), "Write Permission Denied", Snackbar.LENGTH_SHORT).show();

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.d(TAG, "Rationale");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                // Happen the second time user opens the app, if first denied
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ConstantVar.PERMISSION_WRITE_EXTERNAL_STORAGE);
            } else {
                Log.d(TAG, "ask");
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ConstantVar.PERMISSION_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            Snackbar.make(findViewById(R.id.rv_home), "Write Permission Granted", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ConstantVar.PERMISSION_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "Write External Storage Permission Granted");
                    Snackbar.make(findViewById(R.id.rv_home), "Write Permission Granted", Snackbar.LENGTH_SHORT).show();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "Write External Storage Permission Denied");
                    Snackbar.make(findViewById(R.id.rv_home), "Write Permission Denied", Snackbar.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}
