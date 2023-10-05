package com.onedream.wedoxdb;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.onedream.wedoxdb.annotation_key.Column;
import com.onedream.wedoxdb.annotation_key.PrimaryKey;
import com.onedream.wedoxdb.config.WeDoxDBConfigManager;
import com.onedream.wedoxdb.utils.DBUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class SQLiteHelper extends SQLiteOpenHelper {
    private final String TAG = this.getClass().getSimpleName();
    //
    private static SQLiteHelper mSQLiteHelperQuery = null;
    private static SQLiteHelper mSQLiteHelperUpsert = null;
    //
    private final List<Class<?>> clazzList;
    /**
     * 反射注解的类名称(当前目录下的PrimaryKey.java和Column.java）
     */
    private final static String PRIMARY_KEY_NAME_STR = PrimaryKey.class.getSimpleName();
    private final static String COLUMN_KEY_NAME_STR = Column.class.getSimpleName();


    public static synchronized SQLiteHelper getInstanceQuery(Context context) {
        if (mSQLiteHelperQuery == null) {
            mSQLiteHelperQuery = new SQLiteHelper(context, WeDoxDBConfigManager.providerDbName(), WeDoxDBConfigManager.providerTableNameListData());
        }
        return mSQLiteHelperQuery;
    }

    public static synchronized SQLiteHelper getInstanceUpsert(Context context) {
        if (mSQLiteHelperUpsert == null) {
            mSQLiteHelperUpsert = new SQLiteHelper(context, WeDoxDBConfigManager.providerDbName(), WeDoxDBConfigManager.providerTableNameListData());
        }
        return mSQLiteHelperUpsert;
    }


    private SQLiteHelper(Context context, String dbName, List<Class<?>> clazzList) {
        super(context, dbName, null, WeDoxDBConfigManager.providerCurrentDbVersion());
        this.clazzList = clazzList;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db, clazzList);
    }

    //创建数据表
    private void createTable(SQLiteDatabase db, List<Class<?>> classes) {
        printLog("开始创建数据表");
        for (Class<?> c : classes) {
            printLog("开始创建数据表：" + c.getSimpleName());
            String tblName = c.getSimpleName();
            Field[] fields = c.getDeclaredFields();
            if (fields.length == 0) {
                printLog(tblName + "该类没有任何变量，无法创建对应的数据表");
                continue;
            }
            List<String> pk = new ArrayList<String>();
            String createTblSql = "create table if not exists " + tblName + "(";
            for (Field f : fields) {
                String colName = f.getName();
                String colType = DBUtils.getTypeName(f.getType().getSimpleName());
                if (colType == null || !DBUtils.isAnnotationPresentsInField(f, COLUMN_KEY_NAME_STR)) {//如果该变量没有添加Column注释，直接跳过该变量
                    continue;
                }
                if (DBUtils.isAnnotationPresentsInField(f, PRIMARY_KEY_NAME_STR)) {//该变量是主键
                    pk.add(colName);
                }
                createTblSql += colName + " " + colType + ",";
            }

            if (pk.size() <= 0) {
                printLog(tblName + "该表没有主键，无法创建");
                continue;
            }
            //构建主键语句
            String keys = "";
            for (String k : pk) {
                keys += k + ",";
            }
            keys = keys.substring(0, keys.length() - 1);
            //添加主键约束语句
            createTblSql += "constraint pk_" + tblName + " primary key (" + keys + ")";
            createTblSql += ")";
            //
            printLog("创建语句：" + createTblSql);
            // 创建数据库表
            db.execSQL(createTblSql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        printLog("更新数据库版本时调用该方法" + "旧版本为" + oldVersion + "===新版本为" + newVersion);
        //比如增加数据表时，调用创建表
        onCreate(db);
        //一些额外的数据库版本更新配置，比如在之前已经存在的表中对字段的添加与删除，修改等操作
        WeDoxDBConfigManager.onUpgrade(db, oldVersion, newVersion);
    }

    private void clearDb(SQLiteDatabase db) {
        for (Class clazz : WeDoxDBConfigManager.providerTableNameListData()) {
            db.execSQL("drop table if  exists " + clazz.getSimpleName());
        }
    }

    /**
     * 查询数据
     *
     * @param clazz         数据实体类型
     * @param selection     查询条件
     * @param selectionArgs 查询条件参数
     * @param orderBy       排序条件
     * @return
     */
    public <T> List<T> query(Class<T> clazz, String selection, String[] selectionArgs, String orderBy) {
        return query(clazz, selection, selectionArgs, orderBy, null, null);
    }


    /**
     * 查询数据
     *
     * @param clazz         数据实体类型
     * @param selection     查询条件
     * @param selectionArgs 查询条件参数
     * @param orderBy       排序条件
     * @param page          分页页码
     * @param pageSize      分页页面大小
     * @return
     */
    @SuppressLint("DefaultLocale")
    public <T> List<T> query(Class<T> clazz, String selection, String[] selectionArgs, String orderBy, Integer page, Integer pageSize) {
        synchronized (mSQLiteHelperQuery) {
            List<T> retList = new ArrayList<T>();
            // 获得数据库对象
            SQLiteDatabase db = mSQLiteHelperQuery.getReadableDatabase();
            // 查询表中的数据
            Cursor cursor = null;
            if (page != null && pageSize != null) {
                int offset = page * pageSize;
                int limit = pageSize;
                cursor = db.query(clazz.getSimpleName(), null, selection, selectionArgs, null, null, orderBy,
                        offset + "," + limit);
            } else {
                cursor = db.query(clazz.getSimpleName(), null, selection, selectionArgs, null, null, orderBy);
            }
            List<T> cursor2voList = cursor2VOList(cursor, clazz, db);
            if (cursor2voList != null && cursor2voList.size() > 0) {
                retList.addAll(cursor2voList);
            }
            cursor.close();// 关闭结果集
            // 关闭SQLiteDatabase对象
            // db.close();
            return retList;
        }
    }

    @SuppressLint({"DefaultLocale", "NewApi"})
    public <T> long upDate(List<T> objects, Class<T> clazz, String selection, String[] selectionArgs) {
        synchronized (mSQLiteHelperUpsert) {
            long result = 0;
            SQLiteDatabase db = mSQLiteHelperUpsert.getWritableDatabase();
            db.beginTransaction();
            for (T obj : objects) {
                // 使用insert方法向表中插入数据
                ContentValues values = new ContentValues();
                getContentValuesData(obj, clazz, values);
                result = db.update(clazz.getSimpleName(), values, selection, selectionArgs);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            // 关闭SQLiteDatabase对象
            // db.close();
            return result;
        }
    }

    @SuppressLint({"DefaultLocale", "NewApi"})
    public <T> long upsetDate(T obj, Class<T> clazz, String selection, String[] selectionArgs) {
        synchronized (mSQLiteHelperUpsert) {
            long result = 0;
            SQLiteDatabase db = mSQLiteHelperUpsert.getWritableDatabase();
            db.beginTransaction();
            // 使用insert方法向表中插入数据
            ContentValues values = new ContentValues();
            getContentValuesData(obj, clazz, values);
            result = db.update(clazz.getSimpleName(), values, selection, selectionArgs);
            db.setTransactionSuccessful();
            db.endTransaction();
            // 关闭SQLiteDatabase对象
            // db.close();
            return result;
        }
    }


    @SuppressLint("DefaultLocale")
    public <T> long upsert(List<T> objs, Class<T> clazz) {
        synchronized (mSQLiteHelperUpsert) {
            long result = 0;
            // 获取数据库对象
            SQLiteDatabase db = mSQLiteHelperUpsert.getWritableDatabase();
            if (Build.VERSION.SDK_INT >= 11) {
                db.enableWriteAheadLogging();
            }
            db.beginTransaction();
            for (T obj : objs) {
                // 使用insert方法向表中插入数据
                ContentValues values = new ContentValues();
                getContentValuesData(obj, clazz, values);
                result = db.replace(clazz.getSimpleName(), null, values);

            }
            db.setTransactionSuccessful();
            db.endTransaction();
            // 关闭SQLiteDatabase对象
            // db.close();
            return result;
        }
    }

    private <T> void getContentValuesData(T obj, Class<T> clazz, ContentValues values) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            try {
                if (DBUtils.isAnnotationPresentsInField(f, COLUMN_KEY_NAME_STR)) {
                    String getterName = f.getName();
                    getterName = "get"
                            + getterName.substring(0, 1).toUpperCase()
                            + getterName.substring(1,
                            getterName.length());
                    Method getter = clazz.getMethod(getterName);
                    String type = getter.getReturnType().getSimpleName();
                    //
                    putValue(obj, values, getter, f, type);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private <T> void putValue(T obj, ContentValues values, Method getter, Field f, String type) throws Exception {
        if (!TextUtils.isEmpty(type)) {
            switch (type) {
                case "String": {
                    Object invoke = getter.invoke(obj);
                    if (invoke != null) {
                        values.put(f.getName(), invoke + "");
                    }
                }
                break;
                case "long":
                case "Long": {
                    Long value = Long.parseLong("" + getter.invoke(obj));
                    values.put(f.getName(), value);
                }
                break;
                case "int":
                case "Integer": {
                    Integer value = Integer.parseInt("" + getter.invoke(obj));
                    if (value >= 0) {
                        values.put(f.getName(), value);
                    }
                }
                break;
                case "float":
                case "Float": {
                    Float value = Float.parseFloat("" + getter.invoke(obj));
                    if (value >= 0) {
                        values.put(f.getName(), value);
                    }
                }
            }
        }
    }

    public <T> long upsertValue(List<ContentValues> objs, Class<T> class1) {
        synchronized (mSQLiteHelperUpsert) {
            long result = 0;
            SQLiteDatabase db = mSQLiteHelperUpsert.getWritableDatabase();
            if (Build.VERSION.SDK_INT >= 11) {
                db.enableWriteAheadLogging();
            }
            db.beginTransaction();
            for (ContentValues obj : objs) {
                result = db.replace(class1.getSimpleName(), null, obj);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            // 关闭SQLiteDatabase对象
            // db.close();
            return result;
        }
    }

    public <T> List<T> rawQuerry(Class<T> clazz, String rawSql, String[] selectionArgs) {
        synchronized (TAG) {
            List<T> retList = new ArrayList<T>();
            // 获得数据库对象
            SQLiteDatabase db = this.getReadableDatabase();
            // 查询表中的数据
            Cursor cursor = db.rawQuery(rawSql, selectionArgs);
            List<T> cursor2voList = cursor2VOList(cursor, clazz, db);
            if (cursor2voList != null && cursor2voList.size() > 0) {
                retList.addAll(cursor2voList);
            }
            cursor.close();// 关闭结果集
            // 关闭SQLiteDatabase对象
            // db.close();
            return retList;
        }
    }

    @SuppressLint("DefaultLocale")
    public <T> int delete(Class<T> clazz, String selection, String... selectionArgs) {
        synchronized (mSQLiteHelperUpsert) {
            // 获得数据库对象
            SQLiteDatabase db = mSQLiteHelperUpsert.getWritableDatabase();
            if (Build.VERSION.SDK_INT >= 11) {
                db.enableWriteAheadLogging();
            }
            db.beginTransaction();
            int delete = db.delete(clazz.getSimpleName(), selection, selectionArgs);
            db.setTransactionSuccessful();
            db.endTransaction();
            return delete;
        }
    }

    /**
     * 通过SQL语句获得对应的VO的List。注意：Cursor的字段名或者别名一定要和VO的成员名一样
     */
    @SuppressWarnings("rawtypes")
    public static List sql2VOList(SQLiteDatabase db, String sql, Class clazz) {
        Cursor c = db.rawQuery(sql, null);
        return cursor2VOList(c, clazz, db);
    }

    /**
     * 通过SQL语句获得对应的VO的List。注意：Cursor的字段名或者别名一定要和VO的成员名一样
     */
    @SuppressWarnings("rawtypes")
    public static List sql2VOList(SQLiteDatabase db, String sql,
                                  String[] selectionArgs, Class clazz) {
        Cursor c = db.rawQuery(sql, selectionArgs);
        return cursor2VOList(c, clazz, db);
    }

    /**
     * 通过Cursor转换成对应的VO。注意：Cursor里的字段名（可用别名）必须要和VO的属性名一致
     */
    @SuppressWarnings({"rawtypes", "unused"})
    private static Object cursor2VO(Cursor c, Class clazz) {
        if (c == null) {
            return null;
        }
        try {
            c.moveToNext();
            Object obj = setValues2Fields(c, clazz);
            return obj;
        } catch (Exception e) {
            printLog("ERROR @：cursor2VO" + e.toString());
            return null;
        } finally {
            c.close();
        }
    }

    /**
     * 通过Cursor转换成对应的VO集合。注意：Cursor里的字段名（可用别名）必须要和VO的属性名一致
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T> List<T> cursor2VOList(Cursor c, Class<T> clazz, SQLiteDatabase db) {
        if (c == null) {
            return null;
        }
        List list = new LinkedList();
        Object obj;
        try {
            for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
                obj = setValues2Fields(c, clazz);
                list.add(obj);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            //todo bean必须要有一个无参构造方法
            //todo java.lang.InstantiationException: java.lang.Class<com.xxx.db.bean.MovieDownloadedBean> has no zero argument constructor
            printLog("SQLiteHelper的cursor2VOList方法抛出异常：" + e.toString());
            return null;
        } finally {
            c.close();
        }
    }

    /***
     * 删除指定表内的数据
     *
     * @param clazzs
     * @return 操作状态码
     */
    public static long clearData(Class... clazzs) {
        synchronized (mSQLiteHelperUpsert) {
            long result = 0;
            // 获得数据库对象
            SQLiteDatabase db = mSQLiteHelperUpsert.getWritableDatabase();
            if (clazzs != null) {
                for (Class clazz : clazzs) {
                    result += db.delete(clazz.getSimpleName(), null, null);
                }
            }
            return result;
        }

    }


    private static <T> Object setValues2Fields(Cursor c, Class<T> clazz)
            throws Exception {
        String[] columnNames = c.getColumnNames();// 字段数组
        Object obj = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();

        for (Field _field : fields) {
            //修复：报错not find column $.jacocoData
            //private static transient boolean[] .$jacocoData
            if(_field.isSynthetic()){ //判断一下当前字段是否为复合字段
                printLog("我是复合字段，不处理:"+_field.toString());
                break;//复合字段不处理
            }
            //
            Class<? extends Object> typeClass = _field.getType();// 属性类型
            for (String columnName : columnNames) {
                typeClass = DBUtils.getBasicClass(typeClass);

                boolean isBasicType = DBUtils.isBasicType(typeClass);
                if (isBasicType) {
                    if (columnName.equalsIgnoreCase(_field.getName())) {// 是基本类型
                        String _str = c.getString(c.getColumnIndex(columnName));
                        if (_str == null) {
                            break;
                        }
                        _str = _str == null ? "" : _str;
                        Constructor<? extends Object> cons = typeClass.getConstructor(String.class);
                        Object attribute = cons.newInstance(_str);
                        _field.setAccessible(true);
                        _field.set(obj, attribute);
                        break;
                    }
                } else if (typeClass.getName().equals("java.util.ArrayList") || typeClass.getName().equals("java.util.List") || typeClass.getName().equals("com.android.tools.fd.runtime.IncrementalChange")) {
                    break;
                } else {
                    Object obj2 = setValues2Fields(c, typeClass);// 递归
                    _field.set(obj, obj2);
                    break;
                }
            }

        }
        return obj;
    }


    //这里替换你的打印
    private static void printLog(String errMsg) {
        Log.e("ATU SQLiteHelper", errMsg);
    }

}
