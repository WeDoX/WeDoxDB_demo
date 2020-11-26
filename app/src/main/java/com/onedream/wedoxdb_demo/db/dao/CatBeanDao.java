package com.onedream.wedoxdb_demo.db.dao;

import com.onedream.wedoxdb.dao.BaseDbDao;
import com.onedream.wedoxdb_demo.db.bean.CatBean;

/**
 * @author jdallen
 * @since 2020/11/26
 */
public class CatBeanDao extends BaseDbDao<CatBean> {

    @Override
    public Class<CatBean> getType() {
        return CatBean.class;
    }
}
