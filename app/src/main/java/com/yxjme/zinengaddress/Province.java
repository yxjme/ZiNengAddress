package com.yxjme.zinengaddress;

import java.util.List;

/**
 * Created by zbsdata on 2017/2/25.
 */

public class Province {
        String name;
        List<City> subdb;

    public String getName() {
        return name;
    }
    public List<City> getSubdb() {
        return subdb;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setSubdb(List<City> subdb) {
        this.subdb = subdb;
    }
}
