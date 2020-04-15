package com.skteam.thesquad.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.skteam.thesquad.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class HomeSliderAdapter extends
        SliderViewAdapter<HomeSliderAdapter.ImageviewHolder> {

    private Context context;
    private List<HomeItem> mSliderItems = new ArrayList<>();

    public HomeSliderAdapter(Context context, List<HomeItem> mSliderItems) {
        this.context = context;
        this.mSliderItems = mSliderItems;
    }

    public void renewItems(List<HomeItem> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(HomeItem sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public HomeSliderAdapter.ImageviewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_slider, null);
        return new HomeSliderAdapter.ImageviewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(HomeSliderAdapter.ImageviewHolder viewHolder, final int position) {

        HomeItem data = mSliderItems.get(position);

        Glide.with(viewHolder.itemView)
                .load(data.getImage())
                .fitCenter()
                .into(viewHolder.imageView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class ImageviewHolder extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageView;


        public ImageviewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            this.itemView = itemView;
        }
    }

}
