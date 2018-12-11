package com.kedacom.baseutil;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 线程池工具类
 * Created by zhoutianjie on 2018/11/19.
 */

public class AppExecutors {

    private Executor cacheThreadPool;
    private Executor singleThreadPool;

    private AppExecutors(){
    }

    private static class SingleTonHolder{
        private static final AppExecutors INSTANCE = new AppExecutors();
    }

    public static AppExecutors Instance(){
        return SingleTonHolder.INSTANCE;
    }

    public Executor getCacheThreadPool(){
        if(cacheThreadPool == null){
            cacheThreadPool = Executors.newCachedThreadPool();
        }
        return cacheThreadPool;
    }

    public Executor getSingleThreadPool(){
        if(singleThreadPool == null){
            singleThreadPool = Executors.newSingleThreadExecutor();
        }
        return singleThreadPool;
    }
}
