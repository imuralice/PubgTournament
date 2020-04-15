package com.skteam.thesquad.home;

import java.util.List;

public class HomeItemResponse {
    private boolean error;
    private List<HomeItem> res;

    public boolean isError() {
        return error;
    }

    public List<HomeItem> getRes() {
        return res;
    }
}
