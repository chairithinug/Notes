package com.u.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class NoteDBHelper extends SQLiteOpenHelper {
  public static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "Notes.db";

  public NoteDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(SQL_CREATE_STEPS_ENTRIES);
  }

  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL(SQL_DELETE_STEPS_ENTRIES);
    onCreate(db);
  }

  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    onUpgrade(db, oldVersion, newVersion);
  }

  public static void deleteDatabase(Context mContext) {
    mContext.deleteDatabase(DATABASE_NAME);
  }

  /* BEGIN SQL Strings */
  // Steps Data
  private static final String SQL_CREATE_STEPS_ENTRIES =
          "CREATE TABLE " + NotesInstanceContract.NotesEntry.TABLE_NAME + " (" +
                  NotesInstanceContract.NotesEntry.COLUMN_NAME_CREATED_DATE + " TEXT," +
                  NotesInstanceContract.NotesEntry.COLUMN_NAME_LAST_MODIFIED_DATE + " TEXT," +
                  NotesInstanceContract.NotesEntry.COLUMN_NAME_FOR_WHOM + " TEXT," +
                  NotesInstanceContract.NotesEntry.COLUMN_NAME_TITLE + " TEXT," +
                  NotesInstanceContract.NotesEntry.COLUMN_NAME_DATA + " TEXT," +
                  NotesInstanceContract.NotesEntry.COLUMN_NAME_HAS_POSTED + " INTEGER)";
  private static final String SQL_DELETE_STEPS_ENTRIES = "DROP TABLE IF EXISTS " + NotesInstanceContract.NotesEntry.TABLE_NAME;
}