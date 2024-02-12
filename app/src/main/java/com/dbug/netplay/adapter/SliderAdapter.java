package com.dbug.netplay.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dbug.netplay.R;
import com.dbug.netplay.activity.ItemViewActivity;
import com.dbug.netplay.model.SliderItem;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private List<SliderItem> mSliderItems = new ArrayList<>();

    public void renewItems(List<SliderItem> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(SliderItem sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        SliderItem sliderItem = mSliderItems.get(position);
        viewHolder.textViewDescription.setText(sliderItem.getTitle());
        viewHolder.textViewDescription.setTextSize(16);
        viewHolder.textViewDescription.setTextColor(Color.WHITE);


        Log.d("fdsfsd", "onBindViewHolder: "+sliderItem.getImageUrl());
//        String slideImage = sliderItem.getImageUrl();
//        if(sliderItem.video_type.equals("Youtube")){
//            Glide.with(viewHolder.itemView)
//                    .load("https://img.youtube.com/vi/" + sliderItem.video_id + "/maxresdefault.jpg")
//                    .fitCenter()
//                    .into(viewHolder.imageViewBackground);
//        }else {
            Glide.with(viewHolder.itemView)
                    .load(sliderItem.getImageUrl())
                    .fitCenter()
                    .into(viewHolder.imageViewBackground);
//        }

        viewHolder.itemViewClick.setOnClickListener(v -> {

            SliderItem sliderItem1 = mSliderItems.get(position);

           Intent intent = new Intent(v.getContext(), ItemViewActivity.class);
            intent.putExtra("video_title", sliderItem1.title);
            intent.putExtra("video_url", sliderItem1.video_url);
            intent.putExtra("video_id", sliderItem1.video_id);
            intent.putExtra("vid", sliderItem1.vid+"");
            intent.putExtra("video_duration", sliderItem1.video_duration);
            intent.putExtra("video_description", sliderItem1.video_description);
            intent.putExtra("video_type", sliderItem1.video_type);
            intent.putExtra("video_thumbnail", sliderItem1.video_thumbnail);
            intent.putExtra("size", sliderItem1.size);
            intent.putExtra("total_views", sliderItem1.total_views+"");
            intent.putExtra("date_time", sliderItem1.date_time);
            intent.putExtra("category_name", sliderItem1.category_name);
            intent.putExtra("from", "slider");
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemViewClick;
        ImageView imageViewBackground;
        ImageView imageGifContainer;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemViewClick = itemView;
        }
    }

}
