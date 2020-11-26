package com.onedream.wedoxdb_demo;

import android.app.Application;

import com.onedream.wedoxdb.config.WeDoxDBConfigManager;
import com.onedream.wedoxdb_demo.db.DbConfig;

/**
 * @author jdallen
 * @since 2020/11/26
 */
public class AppApplication  extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化WeDoxDB数据库配置
        WeDoxDBConfigManager.init(new DbConfig());
    }
}
