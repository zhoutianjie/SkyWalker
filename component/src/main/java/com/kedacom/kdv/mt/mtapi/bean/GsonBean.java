package com.kedacom.kdv.mt.mtapi.bean;

import com.google.gson.Gson;

/**
 * Created by zhoutianjie on 2018/11/20.
 */

public class GsonBean {

    public String toJson() {
        return new Gson().toJson(this);
    }

    public GsonBean fromJson(String gson) {
        return new Gson().fromJson(gson, getClass());
    }

}
