package com.u.notes;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchNoteActivity extends FragmentActivity {

    public static final String TAG = "SearchNoteActivity";

    public static FragmentManager fm;

    public static ArrayList<NotesInstance> searchNotesList;
    public static NotesAdapter searchAdapter;
    public static RecyclerView searchRecyclerView;

    public EditText searchText;
    public Button btnSearch;

    public boolean filer_for_whom;
    public boolean filer_title;
    public boolean filer_data;

    public static boolean active = false;

    ArrayList<String> column_list = new ArrayList<String>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_note);

        active = true;
        fm = getSupportFragmentManager();

        searchRecyclerView = findViewById(R.id.rv_search);
        searchRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        searchNotesList = new ArrayList<NotesInstance>();

        searchText = findViewById(R.id.et_seach_text);
        btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToBeSearched = searchText.getText().toString();
                Log.d(TAG, "search " + textToBeSearched);
                searchInDB(v, textToBeSearched);
            }
        });

        // TODO, FIXME
        searchAdapter = new NotesAdapter(searchNotesList);
        searchRecyclerView.setAdapter(searchAdapter);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.checkbox_for_whom:
                if (checked) {
                    Log.d(TAG, "for whom");
                    filer_for_whom = true;
                    column_list.add(NotesInstanceContract.NotesEntry.COLUMN_NAME_FOR_WHOM);
                } else {
                    filer_for_whom = false;
                    column_list.remove(NotesInstanceContract.NotesEntry.COLUMN_NAME_FOR_WHOM);
                }
                break;
            case R.id.checkbox_title:
                if (checked) {
                    Log.d(TAG, "title");
                    filer_title = true;
                    column_list.add(NotesInstanceContract.NotesEntry.COLUMN_NAME_TITLE);
                } else {
                    filer_title = false;
                    column_list.remove(NotesInstanceContract.NotesEntry.COLUMN_NAME_TITLE);
                }
                break;
            case R.id.checkbox_data:
                if (checked) {
                    Log.d(TAG, "data");
                    filer_data = true;
                    column_list.add(NotesInstanceContract.NotesEntry.COLUMN_NAME_DATA);
                } else {
                    filer_data = false;
                    column_list.remove(NotesInstanceContract.NotesEntry.COLUMN_NAME_DATA);
                }
                break;

        }
        // TODO
        Log.d(TAG, "checked");
    }

    public void searchInDB(View view, String text) {
        final NoteDBHelper dbHelper = new NoteDBHelper(view.getContext());
        final SQLiteDatabase searchInDB = dbHelper.getReadableDatabase();
        String[] column = column_list.toArray(new String[column_list.size()]);
        Log.d(TAG, column[0]);
        String[] selectionargs = new String[]{"%" + text + "%"};
        searchNotesList = new ArrayList<NotesInstance>();

        String[] projection = {NotesInstanceContract.NotesEntry.COLUMN_NAME_CREATED_DATE,
                NotesInstanceContract.NotesEntry.COLUMN_NAME_LAST_MODIFIED_DATE,
                NotesInstanceContract.NotesEntry.COLUMN_NAME_FOR_WHOM,
                NotesInstanceContract.NotesEntry.COLUMN_NAME_TITLE,
                NotesInstanceContract.NotesEntry.COLUMN_NAME_DATA};

        String sortOrder = NotesInstanceContract.NotesEntry.COLUMN_NAME_LAST_MODIFIED_DATE + " DESC";
        // FIXME
        Cursor cursor = searchInDB.query(NotesInstanceContract.NotesEntry.TABLE_NAME, projection, NotesInstanceContract.NotesEntry.COLUMN_NAME_TITLE + " LIKE ?", selectionargs, null, null, sortOrder);
        while (cursor.moveToNext()) {
            NotesInstance ni = new NotesInstance();
            ni.setCreatedDate(cursor.getString(0));
            ni.setLastModifiedDate(cursor.getString(1));
            ni.setForWhom(cursor.getString(2));
            ni.setTitle(cursor.getString(3));
            ni.setData(cursor.getString(4));
            searchNotesList.add(ni);
            Log.d(TAG, ni.toString());
        }
        searchInDB.close();
        searchAdapter = new NotesAdapter(searchNotesList);
        searchRecyclerView.setAdapter(searchAdapter);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Done Searching");
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
