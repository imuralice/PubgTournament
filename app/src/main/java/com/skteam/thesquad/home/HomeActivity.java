package com.skteam.thesquad.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.skteam.thesquad.R;
import com.skteam.thesquad.account.AccountActivity;
import com.skteam.thesquad.common.Common;
import com.skteam.thesquad.more.MoreActivity;
import com.skteam.thesquad.mycontest.MyContestActivity;
import com.skteam.thesquad.profile.ProfileActivity;
import com.skteam.thesquad.retrofit.TheSquadApi;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HOMEACTIVTYTEST";
    private SliderView sliderView;
    private List<HomeItem> list;
    private List<HomeItem> gameList;
    private TheSquadApi mService;
    private LinearLayout lyt_contest, lyt_account, lyt_more;
    private ImageView img_profile;
    private GamesAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
        initActions();
        loadHomeSliderImage();
        loadGamesList();

    }


    //=====================Initializing all the views ids==============//
    private void initViews() {
        sliderView = findViewById(R.id.imageSlider);
        mService = Common.getAPI();
        lyt_contest = findViewById(R.id.lyt_contest);
        lyt_account = findViewById(R.id.lyt_wallet);
        lyt_more = findViewById(R.id.lyt_more);
        img_profile = findViewById(R.id.img_profile);

//        sliderView.setSliderAdapter(new SliderAdapterExample(this, list));
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);//set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();

        recyclerView = findViewById(R.id.rv_games);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

    }
    //=====================Initializing all the views ids==============//


    //==================Actions on All the views============//
    private void initActions() {
        lyt_contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MyContestActivity.class);
                startActivity(intent);
            }
        });
        lyt_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
        lyt_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MoreActivity.class);
                startActivity(intent);
            }
        });
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
    //==================Actions on All the views============//


    //===================HomeActivity Slider banners images Api service=====================//
    private void loadHomeSliderImage() {
        try {
            mService.getHomeSliderImages().enqueue(new Callback<HomeItemResponse>() {
                @Override
                public void onResponse(@NonNull Call<HomeItemResponse> call,@NonNull Response<HomeItemResponse> response) {
                    assert response.body() != null;
                    if (!response.body().isError()) {
                        list = response.body().getRes();
                        sliderView.setSliderAdapter(new HomeSliderAdapter(HomeActivity.this, list));
                    } else {
                        Log.d(TAG, "onResponse: Error" + response.body().isError());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<HomeItemResponse> call,@NonNull Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
    //===================HomeActivity Slider banners images Api service=====================//


    //======================Games list all images getting using retrofit Api service============//
    private void loadGamesList() {
        try {
            mService.getGamesList().enqueue(new Callback<HomeItemResponse>() {
                @Override
                public void onResponse(@NonNull Call<HomeItemResponse> call,@NonNull Response<HomeItemResponse> response) {
                    assert response.body() != null;
                    if (!response.body().isError()) {
                        gameList = response.body().getRes();
                        adapter = new GamesAdapter(HomeActivity.this, gameList);
                        recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<HomeItemResponse> call,@NonNull Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
    //======================Games list all images getting using retrofit Api service============//

}
