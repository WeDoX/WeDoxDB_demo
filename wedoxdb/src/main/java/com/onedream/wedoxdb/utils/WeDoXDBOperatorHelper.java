package com.onedream.wedoxdb.utils;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author jdallen
 * @since 2020/11/26
 */
public class WeDoXDBOperatorHelper {

    public static void addStringColumn(SQLiteDatabase db, Class<?> clazz, String columnName) {
        addStringColumn(db, clazz, columnName, "");
    }

    public static void addStringColumn(SQLiteDatabase db, Class<?> clazz, String columnName, String defaultValue) {
        String format = "ALTER TABLE '%s' ADD '%s' VARCHAR(100) DEFAULT '%s'";
        db.execSQL(String.format(format, clazz.getSimpleName(), columnName, defaultValue));
    }

    public static void addIntColumn(SQLiteDatabase db, Class<?> clazz, String columnName) {
        addIntColumn(db, clazz, columnName, 0);
    }

    public static void addIntColumn(SQLiteDatabase db, Class<?> clazz, String columnName, int defaultValue) {
        String format = "ALTER TABLE '%s' ADD '%s' integer DEFAULT %s";
        db.execSQL(String.format(format, clazz.getSimpleName(), columnName, defaultValue));
    }


}
