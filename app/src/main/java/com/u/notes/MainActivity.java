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

import androidx.appcompat.app.AppCompatActivity;
import android.widget.PopupMenu;

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

public class MainActivity extends AppCompatActivity {

    public ArrayList<NotesInstance> notesList;
    public static final String TAG = "MAIN";

    public String selectedTitle = "Last Modified Date";
    public String asc_desc = " ASC"; // ascending order default

    public NotesAdapter adapter;
    public RecyclerView recyclerView;
    public Button btnShowMenu;
    public Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        FloatingActionButton fab_add = findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(view.getContext(), AddNoteActivity.class);
                startActivityForResult(newIntent, 0);
            }
        });
        FloatingActionButton fab_search = findViewById(R.id.fab_search);
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
//        TODO
//        DEBUGGING
//        NotesInstance t = new NotesInstance();
//        t.setTitle("test");
//        t.setLastModifiedDate("1234");
//        t.setData("aaaa");
//        t.setCreatedDate("333");
//        t.setForWhom("u");
//        saveInstanceData(t);
//
//        NotesInstance t1 = new NotesInstance();
//        t1.setTitle("teqst");
//        t1.setLastModifiedDate("3333");
//        t1.setData("fff");
//        t1.setCreatedDate("asa");
//        t1.setForWhom("r");
//        saveInstanceData(t1);
//
//        NotesInstance t2 = new NotesInstance();
//        t2.setTitle("teasdqst");
//        t2.setLastModifiedDate("788");
//        t2.setData("dhh");
//        t2.setCreatedDate("bbbb");
//        t2.setForWhom("q");
//        saveInstanceData(t2);

        refreshRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            // need to point to any view in the main activity
            Snackbar.make(recyclerView, "Error Adding a Note", Snackbar.LENGTH_SHORT).show();
        } else { // only 1 requestCode, for AddNoteActivity
            Bundle ret = data.getExtras();
            NotesInstance ni = (NotesInstance) ret.getParcelable("notesInstance");
            saveInstanceData(ni);
            Snackbar.make(recyclerView, "Note Added", Snackbar.LENGTH_SHORT).show();
            refreshRecyclerView();
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
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
        long newRowId = writeDB.insert(NotesInstanceContract.NotesEntry.TABLE_NAME, null, values);
        // TODO update noteslist?
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
        readDB.close();
    }

    private void deleteInstanceData(String date) {
        final NoteDBHelper dbHelper = new NoteDBHelper(context);
        final SQLiteDatabase deleteDB = dbHelper.getReadableDatabase();
        // TODO update noteslist?
        deleteDB.delete(NotesInstanceContract.NotesEntry.TABLE_NAME, NotesInstanceContract.NotesEntry.COLUMN_NAME_CREATED_DATE + "=?", new String[]{date});
        deleteDB.close();
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
        btnShowMenu.setText("Sorted By " + selectedTitle.toString());
        adapter = new NotesAdapter(notesList);
        recyclerView.setAdapter(adapter);
    }
}
