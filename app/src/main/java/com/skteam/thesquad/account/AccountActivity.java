package com.skteam.thesquad.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.skteam.thesquad.R;
import com.skteam.thesquad.common.Common;
import com.skteam.thesquad.home.HomeActivity;
import com.skteam.thesquad.more.MoreActivity;
import com.skteam.thesquad.mycontest.MyContestActivity;
import com.skteam.thesquad.profile.ProfileActivity;
import com.skteam.thesquad.retrofit.TheSquadApi;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "ACOUNTACTIVTYTEST";
    private TheSquadApi mService;
    private LinearLayout lyt_contest, lyt_home,lyt_more;
    private ImageView img_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initViews();
        initActions();
    }


    private void initViews() {
        mService = Common.getAPI();
        lyt_contest = findViewById(R.id.lyt_contest);
        lyt_home = findViewById(R.id.lyt_home);
        lyt_more = findViewById(R.id.lyt_more);
        img_profile = findViewById(R.id.img_profile);

    }
    private void initActions() {
        lyt_contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, MyContestActivity.class);
                startActivity(intent);
            }
        });
        lyt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        lyt_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, MoreActivity.class);
                startActivity(intent);
            }
        });
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
