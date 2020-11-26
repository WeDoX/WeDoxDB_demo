package com.onedream.wedoxdb_demo;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.onedream.wedoxdb_demo.databinding.ActivityMainBinding;
import com.onedream.wedoxdb_demo.db.bean.CatBean;
import com.onedream.wedoxdb_demo.db.dao.CatBeanDao;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //
        initData();
    }

    private void initData() {
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
        binding.tvShow.setText(info);
    }
}
