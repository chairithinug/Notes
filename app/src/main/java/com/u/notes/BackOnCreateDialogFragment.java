package com.u.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.DialogFragment;

import static android.app.Activity.RESULT_CANCELED;

public class BackOnCreateDialogFragment extends DialogFragment {

    public static final String TAG = "BackOnCreateDialog";
    public Activity activity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "Yes, delete!");
                Intent intent = new Intent();
                activity.setResult(RESULT_CANCELED, intent);
                activity.finish();
            }
        });
        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "No, stay!");
            }
        });

        builder.setMessage("Do you want to exit without saving this note?")
                .setTitle("Action Required");
        return builder.create();
    }
}
