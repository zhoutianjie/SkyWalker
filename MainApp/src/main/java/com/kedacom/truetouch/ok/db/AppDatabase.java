package com.kedacom.truetouch.ok.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.kedacom.truetouch.ok.db.dao.LoginInfoDao;
import com.kedacom.truetouch.ok.db.entity.LoginInfo;

/**
 * Created by zhoutianjie on 2018/11/21.
 */
@Database(entities = {LoginInfo.class},version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{
    private static final String DB_NAME = "SkyDatabase.db";
    private static volatile AppDatabase Instance = null;

    private Context context;

    public static AppDatabase Instance(Context context){
        if(null == Instance){
            synchronized (AppDatabase.class){
                Instance = createDatabase(context);
            }
        }
        return Instance;
    }

    public abstract LoginInfoDao getLoginInfoDao();

    private static AppDatabase createDatabase(Context context){
        return Room.databaseBuilder(context.getApplicationContext()
        ,AppDatabase.class
        ,DB_NAME).build();
    }
}
