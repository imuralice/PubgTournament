package com.skteam.thesquad.mycontest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.skteam.thesquad.R;
import com.skteam.thesquad.account.AccountActivity;
import com.skteam.thesquad.common.Common;
import com.skteam.thesquad.home.HomeActivity;
import com.skteam.thesquad.more.MoreActivity;
import com.skteam.thesquad.profile.ProfileActivity;
import com.skteam.thesquad.retrofit.TheSquadApi;

public class MyContestActivity extends AppCompatActivity {
    private static final String TAG = "MOREACTIVTYTEST";
    private TheSquadApi mService;
    private LinearLayout lyt_more, lyt_home,lyt_account;
    private ImageView img_profile;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contest);
        initViews();
        initActions();
    }

    private void initViews() {
        mService = Common.getAPI();
        lyt_account = findViewById(R.id.lyt_wallet);
        lyt_home = findViewById(R.id.lyt_home);
        lyt_more = findViewById(R.id.lyt_more);
        img_profile = findViewById(R.id.img_profile);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

    }
    private void initActions() {
        lyt_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyContestActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
        lyt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyContestActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        lyt_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyContestActivity.this, MoreActivity.class);
                startActivity(intent);
            }
        });
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyContestActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        ViewPagerAdapter adapter =  new ViewPagerAdapter(getSupportFragmentManager(),0);

        adapter.addFragment(new PubgFragment(),getString(R.string.pubg));
        adapter.addFragment(new PubgFragment(),getString(R.string.call_of_duty));
        adapter.addFragment(new PubgFragment(),getString(R.string.free_fire));
        adapter.addFragment(new PubgFragment(),getString(R.string.clash_royale));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
