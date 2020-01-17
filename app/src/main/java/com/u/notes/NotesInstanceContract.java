package com.u.notes;

import android.provider.BaseColumns;


public class NotesInstanceContract {
  public NotesInstanceContract() {        // blank constructor
  }

  public static class NotesEntry implements BaseColumns {
    public static final String TABLE_NAME = "notesInstances";
    public static final String COLUMN_NAME_CREATED_DATE = "createdDate";
    public static final String COLUMN_NAME_LAST_MODIFIED_DATE = "lastModifiedDate";
    public static final String COLUMN_NAME_FOR_WHOM = "forWhom";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_DATA = "data";
    public static final String COLUMN_NAME_HAS_POSTED = "hasPosted";
  }
}