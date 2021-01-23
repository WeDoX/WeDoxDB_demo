# WeDoxDB_demo [![](https://jitpack.io/v/WeDox/WeDoxDB_demo.svg)](https://jitpack.io/#WeDox/WeDoxDB_demo)
Sqlite数据库操作框架WeDoXDB

#### How to use?
Step 0.Add it in your root build.gradle at the end of repositories:
~~~~~~~~~
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
~~~~~~~~~
Step 1. Add the dependency
~~~~~~~~~
dependencies {
	        implementation 'com.github.WeDox:WeDoxDB_demo:1.0.0'
	}
~~~~~~~~~
Step 2.to use

# 使用指南
### 1、创建Bean类（必须要有无参构造方法、有主键、主键也需要加上 @Column）
~~~~
public class CatBean {
    @PrimaryKey
    @Column
    private int id;
    @Column
    private String name;
    @Column
    private int age;
    //新增地址字段
    @Column
    private String address;
    @Column
    private int height;



    public CatBean() {
    }
    。。。。。
 }
 ~~~~
 
 ### 2、配置数据库信息
 ##### 2_1、创建配置类
 ~~~~
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
 ~~~~
  ##### 2_2、全局设置数据库配置类
  在自定义的Application中初始化
  ~~~~
  public class AppApplication  extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化WeDoxDB数据库配置
        WeDoxDBConfigManager.init(new DbConfig());
    }
}
  ~~~~
 ### 3、创建数据表操作类
 ~~~~
 public class CatBeanDao extends BaseDbDao<CatBean> {

    @Override
    public Class<CatBean> getType() {
        return CatBean.class;
    }
}
 ~~~~
  ### 4、具体使用
~~~~
  CatBeanDao catBeanDao = new CatBeanDao();
        List<CatBean> catBeanList = new ArrayList<>();
        CatBean catBean = new CatBean();
        catBean.setId(1);
        catBean.setAge(2009);
        catBean.setName("小红2009");
        catBeanList.add(catBean);
        long result = catBeanDao.upsert(this, catBeanList);
        Log.e("ATU", "结果为：" + result);
        List<CatBean> resultList = catBeanDao.querry(this, null, null, null);
        //
        String info = "";
        for (CatBean tempCat : resultList) {
            Log.e("ATU", "结果为：" + tempCat);
            info += tempCat.toString();
        }
~~~~

