package com.skteam.thesquad.profile;

import com.skteam.thesquad.common.Common;

public class ProfileItem {
    private String image;
    private String title;
    private String points;

    public ProfileItem(String image, String title, String points) {
        this.image = image;
        this.title = title;
        this.points = points;
    }

    public String getImage() {
        return Common.IMAGE_URL + image;
    }

    public String getTitle() {
        return title;
    }

    public String getPoints() {
        return points;
    }
}
