package com.u.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private String TAG = "NotesAdapter";

    public final int MAX_DATA_DISPLAY_LENGTH = 20;
    public final int MAX_TITLE_DISPLAY_LENGTH = 15;
    public final int MAX_FOR_WHOM_DISPLAY_LENGTH = 10;

    private int selectedPos = RecyclerView.NO_POSITION;

    public static NotesInstance remove;

    public class ViewHolder extends RecyclerView.ViewHolder {
        //        TextView text_created_date;
        TextView text_last_modified_date;
        TextView text_for_whom;
        TextView text_title;
        TextView text_data;

        public ViewHolder(View itemView) {
            super(itemView);
//            text_created_date = itemView.findViewById(R.id.tv);
            text_last_modified_date = itemView.findViewById(R.id.tv_date);
            text_for_whom = itemView.findViewById(R.id.tv_add);
            text_title = itemView.findViewById(R.id.tv_main);
            text_data = itemView.findViewById(R.id.tv_sub);
        }
    }

    private ArrayList<NotesInstance> instanceList;

    public NotesAdapter(ArrayList<NotesInstance> notes) {
        instanceList = notes;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View noteItemView = inflater.inflate(R.layout.note_list_item, viewGroup, false);

        noteItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = MainActivity.recyclerView.getChildAdapterPosition(view);
                if (pos >= 0 && pos < getItemCount()) {
//                    Log.d(TAG, "short click");
//                    Toast.makeText(view.getContext(), "" + pos, Toast.LENGTH_SHORT).show();
                    NotesInstance ni = MainActivity.notesList.get(pos);
                    Log.d(TAG, ni.toString());

//                    setHighLightSelected(pos); // User does not really see this

                    Intent newIntent = new Intent(view.getContext(), AddNoteActivity.class);
                    newIntent.putExtra("isCreate", false);
                    newIntent.putExtra("notesInstance", ni);
                    ((Activity) context).startActivityForResult(newIntent, ConstantVar.REQUEST_CODE_ADD_NOTE);

//                    setHighLightSelected(RecyclerView.NO_POSITION);
                }
            }
        });
        noteItemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int pos = MainActivity.recyclerView.getChildAdapterPosition(view);
//                MainActivity.recyclerView.setBackgroundColor(Color.BLUE);
                if (pos >= 0 && pos < getItemCount()) {
                    NotesInstance ni = MainActivity.notesList.get(pos);
                    Log.d(TAG, ni.toString());

                    setHighLightSelected(pos);

                    deleteNote(view, ni);
//                    Toast.makeText(view.getContext(), "Long Clicked " + pos, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        return new ViewHolder(noteItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder viewHolder, int i) {

        viewHolder.itemView.setBackgroundColor(selectedPos == i ? Color.LTGRAY : Color.TRANSPARENT);

        // curr
        NotesInstance inst = instanceList.get(i);

        TextView tvDate = viewHolder.text_last_modified_date;
        tvDate.setText(inst.getLastModifiedDate());

        TextView tvMain = viewHolder.text_title;
        tvMain.setText(inst.getTitle().length() > MAX_TITLE_DISPLAY_LENGTH ? inst.getTitle().substring(0, MAX_TITLE_DISPLAY_LENGTH) + "..." : inst.getTitle());

        TextView tvSub = viewHolder.text_data;
        tvSub.setText(inst.getData().length() > MAX_DATA_DISPLAY_LENGTH ? inst.getData().substring(0, MAX_DATA_DISPLAY_LENGTH) + "..." : inst.getData());

        TextView tvAdd = viewHolder.text_for_whom;
        tvAdd.setText(inst.getForWhom().length() > MAX_FOR_WHOM_DISPLAY_LENGTH ? inst.getForWhom().substring(0, MAX_FOR_WHOM_DISPLAY_LENGTH) + "..." : inst.getForWhom());
    }

    @Override
    public int getItemCount() {
        return instanceList.size();
    }

    public void deleteNote(final View view, NotesInstance ni) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        remove = ni;

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "Yes, delete!");
                setHighLightSelected(RecyclerView.NO_POSITION);
                MainActivity.notesList.remove(remove);
                removeFromDB(view, remove);

                Snackbar.make(MainActivity.recyclerView, "Note Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener(){
                            public void onClick(View v) {
                                NotesInstance ni = NotesAdapter.remove;
                                addBackToDB(v, ni);
                                MainActivity.notesList.add(ni);
                                notifyDataSetChanged(); // refresh recyclerView data
                            }
                        }).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "No, stay!");
                setHighLightSelected(RecyclerView.NO_POSITION);
            }
        });

        builder.setMessage("Do you want to delete this note?")
                .setTitle("Action Required");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setHighLightSelected(int pos) {
        notifyItemChanged(selectedPos);
        selectedPos = pos;
        notifyItemChanged(selectedPos);
    }

    public void removeFromDB(View view, NotesInstance ni) {
        final NoteDBHelper dbHelper = new NoteDBHelper(view.getContext());
        final SQLiteDatabase removeDB = dbHelper.getReadableDatabase();
        removeDB.delete(NotesInstanceContract.NotesEntry.TABLE_NAME, NotesInstanceContract.NotesEntry.COLUMN_NAME_CREATED_DATE + "=?", new String[]{ni.getCreatedDate()});
        removeDB.close();
    }

    public void addBackToDB(View view, NotesInstance ni){
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

}