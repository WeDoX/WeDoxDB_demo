package com.onedream.wedoxdb.config;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * @author jdallen
 * @since 2020/11/26
 */
public abstract class BaseWeDoXDBConfig {
    public abstract String providerDbName();

    public abstract int providerCurrentDbVersion();

    public abstract List<Class<?>> providerTableNameListData();

    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
