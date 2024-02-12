package com.dbug.netplay.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dbug.netplay.R;
import com.dbug.netplay.activity.CategoryItemList;
import com.dbug.netplay.ads.InterAdClickInterFace;
import com.dbug.netplay.ads.SessionAds;
import com.dbug.netplay.model.CategoryMain;

import java.util.List;
import static com.dbug.netplay.config.Constant.ADMIN_PANEL_URL;
import static com.dbug.netplay.config.Constant.CATEGORY_IMAGE_URL;

public class CategoryAdapterMain extends RecyclerView.Adapter<CategoryAdapterMain.MyView> implements InterAdClickInterFace {

    private List<CategoryMain.Category> listdata;
    SessionAds sessionAds;
    Context context;
    public CategoryAdapterMain(Context context,List<CategoryMain.Category> listdata) {
        this.listdata = listdata;
        this.context = context;
        this.sessionAds = new SessionAds(context, this);
    }
    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_main, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        CategoryMain.Category categoryM = listdata.get(position);

        holder.categoryItemTextView.setText(categoryM.category_name);

        Glide.with(holder.itemView)
                .load(ADMIN_PANEL_URL+CATEGORY_IMAGE_URL+categoryM.category_image)
                .fitCenter()
                .into(holder.categoryItemImageView);

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), CategoryItemList.class);

            intent.putExtra("cid",categoryM.cid+"");
            intent.putExtra("category_name",categoryM.category_name);

            v.getContext().startActivity(intent);
            sessionAds.show();
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    @Override
    public void onAdClick() {

    }

    @Override
    public void onAdFailed() {

    }

    public class MyView extends RecyclerView.ViewHolder {

        TextView categoryItemTextView;
        ImageView categoryItemImageView;
        LinearLayout category_layout;

        public MyView(@NonNull View itemView) {
            super(itemView);

            categoryItemTextView = itemView.findViewById(R.id.category_item_textView_max);
            categoryItemImageView = itemView.findViewById(R.id.category_item_imageView_max);
            category_layout = itemView.findViewById(R.id.category_layout);
        }
    }
}
