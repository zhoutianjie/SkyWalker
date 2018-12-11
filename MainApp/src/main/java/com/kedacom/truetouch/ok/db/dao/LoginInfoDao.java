package com.kedacom.truetouch.ok.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.kedacom.truetouch.ok.db.entity.LoginInfo;

import java.util.List;

/**
 * Created by zhoutianjie on 2018/11/21.
 */

@Dao
public interface LoginInfoDao {

    @Query("SELECT * FROM login_info")
    List<LoginInfo> queryAlluser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(LoginInfo loginInfo);

    @Query("SELECT * FROM login_info WHERE account = :userAccount")
    LoginInfo queryLoginInfo(String userAccount);


}
