package com.beetalab.stickynote.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.model.NoteModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter {

    private final Context context;
    private final List<NoteModel> data;
    private final OnItemClick onItemClick;

    private SimpleDateFormat dateFormat;

    public NoteAdapter(Context context, List<NoteModel> data, OnItemClick onItemClick) {
        this.context = context;
        this.data = data;
        this.onItemClick = onItemClick;

        this.dateFormat = new SimpleDateFormat("dd MMM yyyy");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_list_item,parent,false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {

        final NoteModel model = data.get(position);

        if(holder instanceof NoteViewHolder){
            setNoteData((NoteViewHolder)holder,model);
        }

    }

    private void setNoteData(NoteViewHolder holder, NoteModel model) {
        holder.title.setText(model.getTitle());
        holder.note.setText(model.getNote());
        Date date = new Date(model.getId());

        String dateTxt = dateFormat.format(date);
        holder.time.setText(dateTxt);

        setBachground(model.getColor(),holder.parent);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onClick(holder.getAdapterPosition(), model, holder.title, holder.note);
            }
        });
    }

    private void setBachground(String color, ConstraintLayout parent) {

        final int i = Color.parseColor(color);
        final Drawable background = parent.getBackground();
        background.setColorFilter(i, PorterDuff.Mode.SRC_IN);
        background.setAlpha(25);
        parent.setBackground(background);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView title,time,note;
        ConstraintLayout parent;

        public NoteViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.date_txt);
            title = itemView.findViewById(R.id.title_txt);
            note = itemView.findViewById(R.id.note_txt);
            parent = itemView.findViewById(R.id.note_parent);
        }
    }

    public interface OnItemClick{
        void onClick(int pos, NoteModel data,TextView view,TextView view1);
    }
}
