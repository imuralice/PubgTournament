package com.skteam.thesquad.home;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.skteam.thesquad.R;

import java.util.List;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.ImageViewHolder>{
    private Context mContext;
    private List<HomeItem> mList;


    public GamesAdapter(Context context, List<HomeItem> list) {
    mContext = context ;
        mList = list;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_game_cards,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
 HomeItem data = mList.get(position);
        Glide.with(mContext).load(data.getImage()).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    static class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView title;
        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);


        }
    }
}
