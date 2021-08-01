package com.beetalab.stickynote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.config.Constant;

public class DateListAdapter extends RecyclerView.Adapter<DateListAdapter.DateViewHolder>{

    private final String [] data;
    private final Context context;
    private int pos;
    private OnClickListener listener;
    private final String day;

    public DateListAdapter(String[] data, Context context, int position, String day) {
        this.data = data;
        this.context = context;
        this.pos = position;
        this.day = day;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == 1)
            return new DateViewHolder(LayoutInflater.from(context).inflate(R.layout.day_list_item_select,parent,false));
        else
            return new DateViewHolder(LayoutInflater.from(context).inflate(R.layout.day_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder( DateListAdapter.DateViewHolder holder, int position) {

        if(day.equals(Constant.DAY_MONDAY)){
            switch (position){
                case 0: holder.dateName.setText("MON");break;
                case 1: holder.dateName.setText("TUS");break;
                case 2: holder.dateName.setText("WED");break;
                case 3: holder.dateName.setText("THU");break;
                case 4: holder.dateName.setText("FRI");break;
                case 5: holder.dateName.setText("SAT");break;
                case 6: holder.dateName.setText("SUN");break;
            }
        }
        else
        {
            switch (position){
                case 0: holder.dateName.setText("SUN");break;
                case 1: holder.dateName.setText("MON");break;
                case 2: holder.dateName.setText("TUS");break;
                case 3: holder.dateName.setText("WED");break;
                case 4: holder.dateName.setText("THU");break;
                case 5: holder.dateName.setText("FRI");break;
                case 6: holder.dateName.setText("SAT");break;
            }
        }



        final String[] s = data[position].split("_");
        holder.date.setText(s[1]);

        holder.itemView.setOnClickListener(view -> {
           DateListAdapter.this.pos = position;
           notifyDataSetChanged();
           listener.onClick(data[position]);
        });


    }

    @Override
    public int getItemViewType(int position) {
        if(this.pos == position)
            return 1;
        else
            return 2;
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder{

        TextView date,dateName;


        public DateViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_number);
            dateName = itemView.findViewById(R.id.day_name);
        }
    }

    public void setPos(int pos){
        this.pos = pos;
    }

  public   interface OnClickListener{
        void onClick(String day);
    }
}
