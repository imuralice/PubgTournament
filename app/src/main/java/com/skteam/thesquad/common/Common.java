package com.skteam.thesquad.common;



import com.skteam.thesquad.retrofit.RetrofitClient;
import com.skteam.thesquad.retrofit.TheSquadApi;

public class Common {
    public static boolean isPhoneLogin=false;
    public static boolean isNewPhoneLogin=false;
    private static final String BASE_URL = "https://lyricsfuse.com/TheSquad/";

    public static TheSquadApi getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(TheSquadApi.class);
    }
}

