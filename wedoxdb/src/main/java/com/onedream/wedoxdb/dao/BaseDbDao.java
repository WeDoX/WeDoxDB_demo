package com.onedream.wedoxdb.dao;


import android.content.ContentValues;
import android.content.Context;

import com.onedream.wedoxdb.SQLiteHelper;

import java.util.List;

//ASC  -顺序
//DESC -倒序
public abstract class BaseDbDao<T> {

    public abstract Class<T> getType();

    /**
     * 新增或更新数据，如果数据主键已存在，则更新数据，否则新增一条记录
     *
     * @param context
     * @param objs    更新数据对象列表
     */
    public long upsert(Context context, List<T> objs) {
        if (objs != null && objs.size() > 0) {
            return SQLiteHelper.getInstanceUpsert(context).upsert(objs, getType());
        }
        return 0;
    }


    public long update(Context context, List<T> objs, String selection, String[] selectionArgs) {
        return SQLiteHelper.getInstanceUpsert(context).upDate(objs, getType(), selection, selectionArgs);
    }


    public long upsetdate(Context context, T obj, String selection, String[] selectionArgs) {
        return SQLiteHelper.getInstanceUpsert(context).upsetDate(obj, getType(), selection, selectionArgs);
    }

    public long upsertValue(Context context, List<ContentValues> objs) {
        if (objs != null && objs.size() > 0) {
            return SQLiteHelper.getInstanceUpsert(context).upsertValue(objs, getType());

        }
        return 0;
    }

    /**
     * 从数据库查询列表
     *
     * @param context       上下文
     * @param selection     筛选条件
     * @param selectionArgs 筛选参数
     * @param orderBy       排序条件
     * @return
     */
    public List<T> querry(Context context, String selection, String[] selectionArgs, String orderBy) {
        return SQLiteHelper.getInstanceQuery(context).query(getType(), selection, selectionArgs, orderBy);
    }

    public int delete(Context context, String selection, String[] selectionArgs, String orderBy) {
        return SQLiteHelper.getInstanceUpsert(context).delete(getType(), selection, selectionArgs);
    }

    public long clearData(Context context) {
        return SQLiteHelper.getInstanceUpsert(context).clearData(getType());
    }
}
