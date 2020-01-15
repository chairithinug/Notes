package com.u.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

public class DeleteDialogFragment extends DialogFragment {
    public static final String TAG = "DeleteDialog";
    public Activity activity;
    public NotesInstance remove;
    public View view;

    public DeleteDialogFragment(View vi, NotesInstance ni) {
        remove = ni;
        view = vi;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final DeleteDialogFragment thisDialog = this;

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "Yes, delete!");
                // on Search page
                if (SearchNoteActivity.active) {
                    SearchNoteActivity.searchAdapter.setHighLightSelected(RecyclerView.NO_POSITION);
                    SearchNoteActivity.searchNotesList.remove(remove);

                    MainActivity.removeFromDB(view, remove);

                    Snackbar.make(SearchNoteActivity.searchRecyclerView, "Note Deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                public void onClick(View v) {
                                    NotesInstance ni = thisDialog.remove;
                                    MainActivity.addBackToDB(v, ni);
                                    SearchNoteActivity.searchNotesList.add(ni);
                                    SearchNoteActivity.searchAdapter.notifyDataSetChanged(); // refresh recyclerView data

                                }
                            }).show();
                } else {
                    MainActivity.adapter.setHighLightSelected(RecyclerView.NO_POSITION);
                    MainActivity.notesList.remove(remove);
                    MainActivity.removeFromDB(view, remove);

                    Snackbar.make(MainActivity.recyclerView, "Note Deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                public void onClick(View v) {
                                    NotesInstance ni = thisDialog.remove;
                                    MainActivity.addBackToDB(v, ni);
                                    MainActivity.notesList.add(ni);
                                    MainActivity.adapter.notifyDataSetChanged(); // refresh recyclerView data
                                }
                            }).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "No, stay!");
                if (SearchNoteActivity.active) {
                    SearchNoteActivity.searchAdapter.setHighLightSelected(RecyclerView.NO_POSITION);
                } else {
                    MainActivity.adapter.setHighLightSelected(RecyclerView.NO_POSITION);
                }
            }
        });
        builder.setMessage("Do you want to delete this note?")
                .setTitle("Action Required");
        return builder.create();
    }
}
