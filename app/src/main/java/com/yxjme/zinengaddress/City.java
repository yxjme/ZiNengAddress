package com.yxjme.zinengaddress;

import java.util.List;

/**
 * Created by zbsdata on 2017/2/25.
 */

public class City {
    String name;
    List<Area> subdb;
    public String getName() {
        return name;
    }
    public List<Area> getSubdb() {
        return subdb;
    }

    public void setSubdb(List<Area> subdb) {
        this.subdb = subdb;
    }

    public void setName(String name) {
        this.name = name;
    }
}
