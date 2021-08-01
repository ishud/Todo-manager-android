package com.beetalab.stickynote.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.Utils.AppUtils;
import com.beetalab.stickynote.config.Constant;
import com.beetalab.stickynote.model.AdView;
import com.beetalab.stickynote.model.BaseModel;
import com.beetalab.stickynote.model.ToDoModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TodoListAdapter extends RecyclerView.Adapter{

    public static final int TYPE_RAW = 0;
    public static final int TYPE_AD =1;

    private final Context context;
    private final List<BaseModel> data;
    private final OnListItemClick listener;
    private final OnItemLongClick longClick;

    private long currentDate;
    private Date date;
    private SimpleDateFormat simpleDateFormat;
    private String formatDate;

    private final SharedPreferences mPref;
    private boolean hour24enable;

    public TodoListAdapter(Context context, List<BaseModel> data, OnListItemClick listener, OnItemLongClick longClick) {
        this.context = context;
        this.data = data;
        this.listener = listener;
        this.longClick = longClick;

        this.currentDate = System.currentTimeMillis();
        this.date = new Date(this.currentDate);

        this.simpleDateFormat = new SimpleDateFormat("MM_dd_yyyy hh : mm a");
        formatDate = simpleDateFormat.format(date);

        this.mPref = AppUtils.getSharedPreference(context);
        this.hour24enable =mPref.getBoolean(Constant.SETTING_KEY_TIME_24_ENABLE,false);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View  view;
        if(viewType == TYPE_RAW) {
            view = LayoutInflater.from(context).inflate(R.layout.todo_list_item, parent, false);
            return new TodoViewHolder(view);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.ad_raw, parent, false);
            return new AdView(view,context);
        }
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {

       BaseModel baseModel = data.get(position);

       if(baseModel instanceof ToDoModel)
       {
           final ToDoModel model = (ToDoModel) data.get(position);
           setTodoRawData(holder,model,position);
       }
    }

    private void setTodoRawData(RecyclerView.ViewHolder holder,ToDoModel doModel,int pos) {

        TodoViewHolder viewHolder = (TodoViewHolder)holder;

        viewHolder.tvDescription.setText(doModel.getDescription());
        viewHolder.tvTitle.setText(doModel.getTask_title());
        viewHolder.tag_name.setText(doModel.getTag_name());

        final String timeString = AppUtils.getTimeString(hour24enable, doModel.getDate_as_long());

        viewHolder.tvTime.setText(timeString);

        if(doModel.isIs_complete()){
            setTickcolor(viewHolder.ivStatus,doModel);
            viewHolder.tvTitle.getPaint().setStrikeThruText(true);
        }
        else
            setColors(viewHolder.ivStatus,doModel);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    listener.onClick(doModel,pos);
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longClick.onLongClick(doModel,pos);
                return false;
            }
        });



    }

    private void setTickcolor(ImageView ivStatus, ToDoModel doModel) {

        if(ivStatus != null){
            int co = Color.parseColor(doModel.getTag_color());
            final Drawable background = ivStatus.getBackground();
            background.setColorFilter(co, PorterDuff.Mode.SRC_IN);
            background.setAlpha(100);
            ivStatus.setBackground(background);

            final Drawable drawable = AppUtils.generateIcon(context, R.drawable.image_tick, co,false);
            drawable.setAlpha(100);
            ivStatus.setImageDrawable(drawable);

        }

    }

    private void setColors(ImageView ivStatus, ToDoModel doModel) {

        if(ivStatus != null){
            final Drawable background = ivStatus.getBackground();
            final Drawable drawable = ivStatus.getDrawable();

            int co = Color.parseColor(doModel.getTag_color());

            background.setColorFilter(co, PorterDuff.Mode.SRC_IN);
            drawable.setColorFilter(co, PorterDuff.Mode.SRC_IN);
            background.setAlpha(100);
            drawable.setAlpha(100);

            ivStatus.setBackground(background);
            ivStatus.setImageDrawable(drawable);

        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(data.get(position) instanceof com.beetalab.stickynote.model.AdView)
            return TYPE_AD;
        else
            return TYPE_RAW;
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder{

        ImageView ivStatus;
        TextView tvTitle,tvDescription,tvTime,tag_name;

        public TodoViewHolder( View itemView) {
            super(itemView);

            ivStatus = itemView.findViewById(R.id.item_priority_icon);
            tvTitle = itemView.findViewById(R.id.to_do_list_item_title);
            tvDescription = itemView.findViewById(R.id.txt_description);
            tvTime = itemView.findViewById(R.id.todo_item_time);
            tag_name = itemView.findViewById(R.id.tag_txt);
        }
    }

    public static class AdView extends RecyclerView.ViewHolder{

        private Context context;
        private NativeAdView adView;
        private final FrameLayout adLayout;

        public AdView(View itemView , Context context) {
            super(itemView);
            this.context = context;
            adLayout = itemView.findViewById(R.id.frame_layout_component_news_ad_view);
            initAdd();
        }

        private void initAdd(){

            AdLoader adLoader = new AdLoader.Builder(context, Constant.ADMOB_NATIVE_ID).
                    forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded( NativeAd nativeAd) {
                            setAdViewData(nativeAd);
                            populateNativeAdView(nativeAd);
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    }).build();
            adLoader.loadAd(new AdRequest.Builder().build());
        }

        private void setAdViewData(NativeAd nativeAd) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.native_ad_layout, null);

            this.adView = itemView.findViewById(R.id.root_news_ad_unified);
            adView.setHeadlineView(adView.findViewById(R.id.tv_headline_news_ad_unified));
            adView.setBodyView(adView.findViewById(R.id.tv_body_news_ad_unified));
            adView.setCallToActionView(adView.findViewById(R.id.btn_news_ad_unified));
            adView.setIconView(adView.findViewById(R.id.iv_icon_news_ad_unified));
            ImageView ivPic = adView.findViewById(R.id.iv_pic_news_ad_unified);
            ivPic.getLayoutParams().width = AppUtils.getScreenWidth(context) / 3;
            adView.setImageView(ivPic);
            adLayout.addView(itemView);
        }

        private void populateNativeAdView(NativeAd nativeAd) {
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

            final NativeAd.Image icon = nativeAd.getIcon();

            if (icon == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }

            if (!nativeAd.getImages().isEmpty()) {
                ((ImageView) adView.getImageView()).setImageDrawable(nativeAd.getImages().get(0).getDrawable());
            } else {
                adView.getImageView().setVisibility(View.GONE);
            }

            adView.setNativeAd(nativeAd);
        }
    }


    public interface OnListItemClick{
        void onClick(ToDoModel data,int position);
    }

    public interface OnItemLongClick{

        void onLongClick(ToDoModel data,int position);
    }

}
