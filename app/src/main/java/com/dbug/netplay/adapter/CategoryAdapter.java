package com.dbug.netplay.adapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyView> implements InterAdClickInterFace {

    private List<CategoryMain.Category> listeData;
    Context context;
    SessionAds sessionAds;

    public CategoryAdapter(Context context,List<CategoryMain.Category> listdata) {
        this.listeData = listdata;
        this.context = context;
        sessionAds = new SessionAds(context);
        this.sessionAds = new SessionAds(context, this);
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item.xml using LayoutInflator
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);

        // return itemView
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        CategoryMain.Category categoryM = listeData.get(position);

        holder.categoryItemTextView.setText(categoryM.category_name);

        Log.d("dfjsdhfsd", "onBindViewHolder: "+categoryM.category_image);
        Glide.with(holder.itemView)
                .load(ADMIN_PANEL_URL+CATEGORY_IMAGE_URL+categoryM.category_image)
                .fitCenter()
                .into(holder.circleImageView);

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
        return listeData.size();
    }

    @Override
    public void onAdClick() {

    }

    @Override
    public void onAdFailed() {

    }

    public class MyView extends RecyclerView.ViewHolder {

        TextView categoryItemTextView;
        ImageView circleImageView;

        public MyView(@NonNull View itemView) {
            super(itemView);

            categoryItemTextView = itemView.findViewById(R.id.category_item_textView);
            circleImageView = itemView.findViewById(R.id.category_item_imageView);

        }
    }
}
