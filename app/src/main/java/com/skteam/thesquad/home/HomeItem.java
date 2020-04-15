package com.skteam.thesquad.home;

import com.skteam.thesquad.common.Common;

public class HomeItem {
    private String image;


    public HomeItem(String image) {
        this.image = image;
    }


    public String getImage() {
        return Common.IMAGE_URL + image;
    }


}

