package com.onedream.wedoxdb_demo.db.bean;

import com.onedream.wedoxdb.annotation_key.Column;
import com.onedream.wedoxdb.annotation_key.PrimaryKey;

/**
 * @author jdallen
 * @since 2020/11/26
 */
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

/*    @Override
    public String toString() {
        return "CatBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }*/

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

  /*  @Override
    public String toString() {
        return "CatBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                '}';
    }*/

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "CatBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                ", height=" + height +
                '}';
    }
}
