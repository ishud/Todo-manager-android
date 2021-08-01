package com.beetalab.stickynote.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.model.TagModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    private final Context context;
    private final List<TagModel> data;
    private final OnItemClick listtener;

    public TagAdapter(Context context, List<TagModel> data, OnItemClick listtener) {
        this.context = context;
        this.data = data;
        this.listtener = listtener;

    }


    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        return new TagViewHolder(LayoutInflater.from(context).inflate(R.layout.component_tag_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder( TagAdapter.TagViewHolder holder, int position) {

        final TagModel tagModel = data.get(position);
            try {
                final int color = Color.parseColor(tagModel.getTag_color());
                createBackgroundDrawable(color,holder.ivIcon,tagModel.isTag_is_custom());

            }catch (Exception e){
                e.printStackTrace();
            }

            if(tagModel.isTag_is_custom()) {
                holder.option_menu.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.drawable.image_cutom_tag).into(holder.ivIcon);
            }
            else{
                holder.option_menu.setVisibility(View.INVISIBLE);
                setIcon(tagModel.getTag_id(),holder.ivIcon);}



        holder.tvTitle.setText(tagModel.getTag_name());
        String count = tagModel.getTask_count() +" Tasks";
        holder.tvTaskCount.setText(count);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listtener.onClick(position,tagModel,-3);
            }
        });

        holder.option_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listtener.onClick(position, data.get(position),-4);
            }
        });
    }

    private void createBackgroundDrawable(int color,ImageView view,boolean iscustom) {

        if(view != null){

            final Drawable background = view.getBackground();
            background.setColorFilter(color, PorterDuff.Mode.SRC_IN);

            if(iscustom)
                background.setAlpha(70);
           else
                background.setAlpha(20);
            view.setBackground(background);
        }



    }

    private void setIcon(String id, ImageView view){

        Drawable drawable = null;

        switch (id){

            case "tag_1" :
               drawable = ContextCompat.getDrawable(context,R.drawable.image_work); break;

            case "tag_2" :
                drawable = ContextCompat.getDrawable(context,R.drawable.icon_learning); break;
            case "tag_3" :
                drawable = ContextCompat.getDrawable(context,R.drawable.image_shpoing); break;
            case "tag_4" :
                drawable = ContextCompat.getDrawable(context,R.drawable.image_art); break;
            case "tag_5" :
                drawable = ContextCompat.getDrawable(context,R.drawable.image_movie); break;

            case "tag_6" :
                drawable = ContextCompat.getDrawable(context,R.drawable.image_meeting); break;

            case "tag_7" :
                drawable = ContextCompat.getDrawable(context,R.drawable.image_dating); break;

            case "tag_8" :
                drawable = ContextCompat.getDrawable(context,R.drawable.image_sports); break;

            case "tag_9" :
                drawable = ContextCompat.getDrawable(context,R.drawable.image_fitness); break;

            case "tag_10" :
                drawable = ContextCompat.getDrawable(context,R.drawable.image_dining); break;

            case "tag_11" :
                drawable = ContextCompat.getDrawable(context,R.drawable.image_helh); break;
        }

        Glide.with(context).load(drawable).into(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder{

        ImageView ivIcon,option_menu;
        TextView tvTitle,tvTaskCount;

        public TagViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tag_view_name_txt);
            tvTaskCount = itemView.findViewById(R.id.tag_view_task_count_txt);
            ivIcon = itemView.findViewById(R.id.tag_list_image);
            option_menu = itemView.findViewById(R.id.option_menu);
        }
    }

    public interface OnItemClick{
        void onClick(int pos, TagModel data,int type);
    }
}
