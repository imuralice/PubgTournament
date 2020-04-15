package com.skteam.thesquad.profile;



import java.util.List;

public class ProfileItemResponse {
    private boolean error;
    private List<ProfileItem> res;

    public boolean isError() {
        return error;
    }

    public List<ProfileItem> getRes() {
        return res;
    }
}
