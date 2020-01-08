package com.u.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private String TAG = "NotesAdapter:";

    public final int MAX_DISPLAY_LENGTH = 20;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_created_date;
        TextView text_last_modified_date;
        TextView text_for_whom;
        TextView text_title;
        TextView text_data;

        public ViewHolder(View itemView) {
            super(itemView);
//            text_created_date = itemView.findViewById(R.id.tv);
//            text_last_modified_date = itemView.findViewById(R.id.tv_last_modified_date);
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
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View noteItemView = inflater.inflate(R.layout.note_list_item, viewGroup, false);
        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(noteItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder viewHolder, int i) {
        // curr
        NotesInstance inst = instanceList.get(i);

        TextView tvMain = viewHolder.text_title;
        tvMain.setText(inst.getTitle().length() > MAX_DISPLAY_LENGTH ? inst.getTitle().substring(0, MAX_DISPLAY_LENGTH) + "..." : inst.getTitle());

        TextView tvSub = viewHolder.text_data;
        tvSub.setText(inst.getData().length() > MAX_DISPLAY_LENGTH ? inst.getData().substring(0, MAX_DISPLAY_LENGTH) + "..." : inst.getData());

        TextView tvAdd = viewHolder.text_for_whom;
        tvAdd.setText(inst.getForWhom().length() > MAX_DISPLAY_LENGTH ? inst.getForWhom().substring(0, MAX_DISPLAY_LENGTH) + "..." : inst.getForWhom());
    }

    @Override
    public int getItemCount() {
        return instanceList.size();
    }
}