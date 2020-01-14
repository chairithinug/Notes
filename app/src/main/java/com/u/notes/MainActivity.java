package com.u.notes;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.widget.PopupMenu;

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

import java.util.ArrayList;

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
                // TODO New Activity
//                Intent newIntent = new Intent(view.getContext(), SearchNoteActivity.class);
//                startActivityForResult(newIntent, ConstantVar.REQUEST_CODE_SEARCH_NOTE);

                Snackbar.make(view, "Search", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
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
                    modifyInstanceData(ni);
                    toastToBeDisplayed = "Note Edited";
                }
                Snackbar.make(recyclerView, toastToBeDisplayed, Snackbar.LENGTH_SHORT).show();
                refreshRecyclerView();
            } else if (requestCode == ConstantVar.REQUEST_CODE_SEARCH_NOTE) {
//            Bundle ret = data.getExtras();
//            NotesInstance ni = (NotesInstance) ret.getParcelable("notesInstance");
//            saveInstanceData(ni);
//            Snackbar.make(recyclerView, "", Snackbar.LENGTH_SHORT).show();
//            refreshRecyclerView();
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

    private void modifyInstanceData(NotesInstance new_ni) {
        final NoteDBHelper dbHelper = new NoteDBHelper(context);
        final SQLiteDatabase modifyDB = dbHelper.getReadableDatabase();

        notesList.remove(AddNoteActivity.argInstance); // remove old instance
        notesList.add(new_ni); // create a new one
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
}
