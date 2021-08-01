package com.beetalab.stickynote.adapter;

import android.content.Context;
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
import com.beetalab.stickynote.model.ToDoModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class TagAdapterHorizontal extends RecyclerView.Adapter<TagAdapterHorizontal.TagViewHolder> {

    private static final int VIEW_TYPE_SELECT = 1;
    private int selected_pos = -1;
    private int editPos = -1;

    private final Context context;
    private final List<TagModel> data;
    private final TagAdapterHorizontal.OnItemClick listtener;


    public TagAdapterHorizontal(Context context, List<TagModel> data, OnItemClick listtener, int pos) {
        this.context = context;
        this.data = data;
        this.listtener = listtener;
        this.editPos = pos;
    }


    @NonNull

    @Override
    public TagViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        if(viewType == VIEW_TYPE_SELECT)
            return new TagViewHolder(LayoutInflater.from(context).inflate(R.layout.component_tag_list_item_select,parent,false));
        else
            return new TagViewHolder(LayoutInflater.from(context).inflate(R.layout.component_tag_list_item_not_select,parent,false));
    }

    @Override
    public void onBindViewHolder(TagAdapterHorizontal.TagViewHolder holder, int position) {

        final TagModel tagModel = data.get(position);

       if(tagModel.isTag_is_custom()){
           Glide.with(context).load(R.drawable.image_cutom_tag).into(holder.ivIcon);
       }
       else
           setIcon(tagModel.getTag_id(),holder.ivIcon);

        holder.tvTitle.setText(tagModel.getTag_name());
        String count = tagModel.getTask_count() +" Tasks";
        holder.tvTaskCount.setText(count);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if(editPos ==-1){
               selected_pos = position;
               listtener.onItemSelect(position,tagModel);
               notifyDataSetChanged();
           }
            }
        });



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

    @Override
    public int getItemViewType(int position) {

        if(position == selected_pos || editPos == position)
            return VIEW_TYPE_SELECT;
        else
            return -1;

    }

    class TagViewHolder extends RecyclerView.ViewHolder{

        ImageView ivIcon;
        TextView tvTitle,tvTaskCount;

        public TagViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tag_view_name_txt);
            tvTaskCount = itemView.findViewById(R.id.tag_view_task_count_txt);
            ivIcon = itemView.findViewById(R.id.tag_list_image);

        }
    }

    public interface OnItemClick{
        void onItemSelect(int pos,TagModel data);
    }

    public void setEditPos(){
        this.editPos = -1;
    }

}
