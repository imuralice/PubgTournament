package com.skteam.thesquad.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.skteam.thesquad.R;
import com.skteam.thesquad.account.AccountActivity;
import com.skteam.thesquad.common.Common;
import com.skteam.thesquad.home.HomeActivity;
import com.skteam.thesquad.home.HomeItemResponse;
import com.skteam.thesquad.more.MoreActivity;
import com.skteam.thesquad.mycontest.MyContestActivity;
import com.skteam.thesquad.retrofit.TheSquadApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "PROFILEACTIVTYTEST";
    private TheSquadApi mService;
    private LinearLayout lyt_more, lyt_home, lyt_account, lyt_contest;
    private List<ProfileItem> badgeList;
    private BadgeAdapter adapter;

    private TextView gamer_tv;
    private TextView personal_tv;
    private TextView edit_tv;
    private ScrollView personal_scroll_view;
    private ScrollView gamer_scroll_view;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        initAction();
        loadProfileBadges();
    }


    //===============All the Views ids are initialized============//
    private void initView() {
        mService = Common.getAPI();
        gamer_tv = findViewById(R.id.gamer_tv);
        personal_tv = findViewById(R.id.personal_tv);
        edit_tv = findViewById(R.id.edit_tv);
        personal_scroll_view = findViewById(R.id.personal_scroll_view);
        gamer_scroll_view = findViewById(R.id.gamer_scroll_view);
        mService = Common.getAPI();
        lyt_contest = findViewById(R.id.lyt_contest);
        lyt_home = findViewById(R.id.lyt_home);
        lyt_account = findViewById(R.id.lyt_wallet);
        lyt_more = findViewById(R.id.lyt_more);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        badgeList = new ArrayList<>();

    }
    //===============All the Views ids are initialized============//


    //===================Actions on all Views===================//
    private void initAction() {
        gamer_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gamer_tv.setTextColor(Color.WHITE);
                personal_tv.setTextColor(Color.BLACK);
                gamer_tv.setBackground(getResources().getDrawable(R.drawable.background_round));
                personal_tv.setBackground(getResources().getDrawable(R.drawable.background_round_white));
                edit_tv.setVisibility(View.GONE);
                gamer_scroll_view.setVisibility(View.VISIBLE);
                personal_scroll_view.setVisibility(View.INVISIBLE);


            }
        });

        personal_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personal_tv.setTextColor(Color.WHITE);
                gamer_tv.setTextColor(Color.BLACK);
                personal_tv.setBackground(getResources().getDrawable(R.drawable.background_round));
                gamer_tv.setBackground(getResources().getDrawable(R.drawable.background_round_white));
                edit_tv.setVisibility(View.VISIBLE);
                gamer_scroll_view.setVisibility(View.INVISIBLE);
                personal_scroll_view.setVisibility(View.VISIBLE);
            }
        });

        lyt_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
        lyt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        lyt_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MoreActivity.class);
                startActivity(intent);
            }
        });
        lyt_contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MyContestActivity.class);
                startActivity(intent);
            }
        });
    }
    //===================Actions on all Views===================//


    //=====================Retrofit Api for badges=================//
    private void loadProfileBadges() {
        try {
            mService.getProfileBadgesList().enqueue(new Callback<ProfileItemResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProfileItemResponse> call, @NonNull Response<ProfileItemResponse> response) {
                    assert response.body() != null;
                    if (!response.body().isError()) {
                        badgeList = response.body().getRes();
                        Log.d(TAG, "onResponse: " + badgeList);
                        adapter = new BadgeAdapter(ProfileActivity.this, badgeList);
                        recyclerView.setAdapter(adapter);

                    } else {
                        Log.d(TAG, "onResponse: failed to load badges");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProfileItemResponse> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //=====================Retrofit Api for badges=================//

}
