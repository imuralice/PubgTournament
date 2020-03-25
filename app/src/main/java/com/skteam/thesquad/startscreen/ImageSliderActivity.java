package com.skteam.thesquad.startscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.skteam.thesquad.R;
import com.skteam.thesquad.loginregisteration.LoginActivity;
import com.skteam.thesquad.loginregisteration.RegisterActivty;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderActivity extends AppCompatActivity {
    private SliderView sliderView;
    private TextView login_tv, register_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        sliderView = findViewById(R.id.imageSlider);
        login_tv = findViewById(R.id.login_tv);
        register_tv = findViewById(R.id.register_tv);

        List<SliderItem> list = new ArrayList<>();
        list.add(new SliderItem(R.drawable.sliderone, getResources().getString(R.string.title1), getResources().getString(R.string.description1)));
        list.add(new SliderItem(R.drawable.slidertwo, getResources().getString(R.string.title2), getResources().getString(R.string.description2)));
        list.add(new SliderItem(R.drawable.sliderthree, getResources().getString(R.string.title3), getResources().getString(R.string.description3)));
        list.add(new SliderItem(R.drawable.sliderfour, getResources().getString(R.string.title4), getResources().getString(R.string.description4)));

        sliderView.setSliderAdapter(new SliderAdapterExample(this, list));


        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);//set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();

        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImageSliderActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        register_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImageSliderActivity.this, RegisterActivty.class);
                startActivity(intent);
            }
        });
    }
}
