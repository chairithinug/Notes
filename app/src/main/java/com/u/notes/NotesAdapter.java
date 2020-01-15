package com.u.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private String TAG = "NotesAdapter";

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
                int pos;
                if (SearchNoteActivity.active) {
                    pos = SearchNoteActivity.searchRecyclerView.getChildAdapterPosition(view);
                } else {
                    pos = MainActivity.recyclerView.getChildAdapterPosition(view);
                }
                if (pos >= 0 && pos < getItemCount()) {
//                    Log.d(TAG, "short click");
//                    Toast.makeText(view.getContext(), "" + pos, Toast.LENGTH_SHORT).show();
                    NotesInstance ni;
                    if (SearchNoteActivity.active) {
                        ni = SearchNoteActivity.searchNotesList.get(pos);
                    } else {
                        ni = MainActivity.notesList.get(pos);
                    }
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
                int pos;
                if (SearchNoteActivity.active) {
                    pos = SearchNoteActivity.searchRecyclerView.getChildAdapterPosition(view);
                } else {
                    pos = MainActivity.recyclerView.getChildAdapterPosition(view);
                }
//                MainActivity.recyclerView.setBackgroundColor(Color.BLUE);
                if (pos >= 0 && pos < getItemCount()) {
                    NotesInstance ni;
                    if (SearchNoteActivity.active) {
                        ni = SearchNoteActivity.searchNotesList.get(pos);
                    } else {
                        ni = MainActivity.notesList.get(pos);
                    }
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
        tvMain.setText(inst.getTitle().length() > ConstantVar.MAX_TITLE_DISPLAY_LENGTH ? inst.getTitle().substring(0, ConstantVar.MAX_TITLE_DISPLAY_LENGTH) + "..." : inst.getTitle());

        TextView tvSub = viewHolder.text_data;
        tvSub.setText(inst.getData().length() > ConstantVar.MAX_DATA_DISPLAY_LENGTH ? inst.getData().substring(0, ConstantVar.MAX_DATA_DISPLAY_LENGTH) + "..." : inst.getData());

        TextView tvAdd = viewHolder.text_for_whom;
        tvAdd.setText(inst.getForWhom().length() > ConstantVar.MAX_FOR_WHOM_DISPLAY_LENGTH ? inst.getForWhom().substring(0, ConstantVar.MAX_FOR_WHOM_DISPLAY_LENGTH) + "..." : inst.getForWhom());
    }

    @Override
    public int getItemCount() {
        return instanceList.size();
    }

    public void deleteNote(final View view, NotesInstance ni) {
        DeleteDialogFragment frag = new DeleteDialogFragment(view, ni);
        if (SearchNoteActivity.active)
            frag.show(SearchNoteActivity.fm, "DeleteDialog");
        else
            frag.show(MainActivity.fm, "DeleteDialog");
    }

    public void setHighLightSelected(int pos) {
        notifyItemChanged(selectedPos);
        selectedPos = pos;
        notifyItemChanged(selectedPos);
    }
}