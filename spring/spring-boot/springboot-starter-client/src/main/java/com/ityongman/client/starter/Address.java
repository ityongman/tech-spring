package com.ityongman.client.starter;

/**
 * @Author shedunze
 * @Date 2020-03-10 09:04
 * @Description
 */

public class Address {
    /**
     * 国家
     */
    private String country ;
    /**
     * 省份
     */
    private String provice ;
    /**
     * 城市
     */
    private String city ;
    /**
     * 组织
     */
    private String org ;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvice() {
        return provice;
    }

    public void setProvice(String provice) {
        this.provice = provice;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }
}
