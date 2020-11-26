package com.onedream.wedoxdb_demo.db;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.onedream.wedoxdb.config.BaseWeDoXDBConfig;
import com.onedream.wedoxdb.utils.WeDoXDBOperatorHelper;
import com.onedream.wedoxdb_demo.db.bean.CatBean;

import java.util.ArrayList;
import java.util.List;

public class DbConfig extends BaseWeDoXDBConfig {
    private static final String DB_NAME = "wedox_db_test.db";
    private static final int DB_VERSION = 10;//TODO 每次新增表或者表的结构改变时，自增1，并在onUpgrade方法内做对应的操作
    private static final List<Class<?>> DB_ENTITIES;

    static {
        DB_ENTITIES = new ArrayList<Class<?>>();
        DB_ENTITIES.add(CatBean.class);
    }

    @Override
    public String providerDbName() {
        return DB_NAME;
    }

    @Override
    public int providerCurrentDbVersion() {
        return DB_VERSION;
    }

    @Override
    public List<Class<?>> providerTableNameListData() {
        return DB_ENTITIES;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("ATU", "版本更新===》当前数据库版本为：" + newVersion + "===之前的版本为:" + oldVersion);
        //版本8新增一个地址字段
        if (newVersion >= 8 && oldVersion < 8) { //
            WeDoXDBOperatorHelper.addStringColumn(db, CatBean.class, "address", "默认产地");
        }
        //版本10新增一个身高字段
        if (newVersion >= 10 && oldVersion < 10) {
            WeDoXDBOperatorHelper.addIntColumn(db, CatBean.class, "height");
        }
    }
}
