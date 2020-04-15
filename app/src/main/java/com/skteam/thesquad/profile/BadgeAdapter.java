package com.skteam.thesquad.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.skteam.thesquad.R;

import java.util.List;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.ImageViewHolder> {
private Context context;
private List<ProfileItem> list;

    public BadgeAdapter(Context context, List<ProfileItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_badge_list,parent,false);
    return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ProfileItem data = list.get(position);
        Glide.with(context).load(data.getImage()).into(holder.imageView);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
        imageView = itemView.findViewById(R.id.imageView);

        }
    }
}
