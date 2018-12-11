package com.kedacom.truetouch.ok.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by zhoutianjie on 2018/11/21.
 */

@Entity(tableName = "login_info")
public class LoginInfo implements Comparable<LoginInfo>{

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String pwd;

    private String account;

    private long login_time;

    //上次APP最终状态 false 处于登录界面 Yes处于主界面
    private boolean main_state;


    public boolean isMain_state() {
        return main_state;
    }

    public void setMain_state(boolean main_state) {
        this.main_state = main_state;
    }


    public long getLogin_time() {
        return login_time;
    }

    public void setLogin_time(long login_time) {
        this.login_time = login_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "id=" + id +
                ", pwd='" + pwd + '\'' +
                ", account='" + account + '\'' +
                ", login_time=" + login_time +
                '}';
    }


    @Override
    public int compareTo(@NonNull LoginInfo o) {
        if(this.login_time == o.login_time){
            return 0;
        }

        if(this.login_time>o.login_time){
            return 1;
        }else {
            return -1;
        }
    }
}
