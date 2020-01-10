package com.u.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private String TAG = "NotesAdapter:";

    public final int MAX_DATA_DISPLAY_LENGTH = 20;
    public final int MAX_TITLE_DISPLAY_LENGTH = 15;
    public final int MAX_FOR_WHOM_DISPLAY_LENGTH = 10;

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

                    Intent newIntent = new Intent(view.getContext(), AddNoteActivity.class);
                    newIntent.putExtra("isCreate", false);
                    newIntent.putExtra("notesInstance", ni);
                    ((Activity) context).startActivityForResult(newIntent, ConstantVar.REQUEST_CODE_ADD_NOTE);

                }
            }
        });
        noteItemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int pos = MainActivity.recyclerView.getChildAdapterPosition(view);
                if (pos >= 0 && pos < getItemCount()) {
                    Toast.makeText(view.getContext(), "Long Clicked " + pos, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(noteItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder viewHolder, int i) {
        // curr
        NotesInstance inst = instanceList.get(i);

        TextView tvDate = viewHolder.text_last_modified_date;
        tvDate.setText(inst.getLastModifiedDate());

        TextView tvMain = viewHolder.text_title;
        tvMain.setText(inst.getTitle().length() > MAX_TITLE_DISPLAY_LENGTH ? inst.getTitle().substring(0, MAX_TITLE_DISPLAY_LENGTH) + "..." : inst.getTitle());
//        tvMain.setText(inst.getTitle());
        TextView tvSub = viewHolder.text_data;
        tvSub.setText(inst.getData().length() > MAX_DATA_DISPLAY_LENGTH ? inst.getData().substring(0, MAX_DATA_DISPLAY_LENGTH) + "..." : inst.getData());
//        tvSub.setText(inst.getData());
        TextView tvAdd = viewHolder.text_for_whom;
        tvAdd.setText(inst.getForWhom().length() > MAX_FOR_WHOM_DISPLAY_LENGTH ? inst.getForWhom().substring(0, MAX_FOR_WHOM_DISPLAY_LENGTH) + "..." : inst.getForWhom());
//        tvAdd.setText(inst.getForWhom());
    }

    @Override
    public int getItemCount() {
        return instanceList.size();
    }
}