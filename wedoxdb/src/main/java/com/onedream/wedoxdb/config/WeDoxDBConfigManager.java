package com.onedream.wedoxdb.config;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * @author jdallen
 * @since 2020/11/26
 */
public class WeDoxDBConfigManager {
    private static BaseWeDoXDBConfig baseWeDoXDBConfig;

    public static void init(BaseWeDoXDBConfig tempBaseDbConfig) {
        baseWeDoXDBConfig = tempBaseDbConfig;
    }

    public static String providerDbName() {
        return baseWeDoXDBConfig.providerDbName();
    }

    public static int providerCurrentDbVersion() {
        return baseWeDoXDBConfig.providerCurrentDbVersion();
    }

    public static List<Class<?>> providerTableNameListData() {
        return baseWeDoXDBConfig.providerTableNameListData();
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        baseWeDoXDBConfig.onUpgrade(db, oldVersion, newVersion);
    }
}
