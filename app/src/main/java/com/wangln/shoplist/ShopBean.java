package com.wangln.shoplist;

/**
 * Created by Administrator on 2017/9/25 0025.
 */

public class ShopBean {
    int shopid;
    String shoptitle;
    double shopprice;
    int shoptype;
    String shoppictureurl;

    @Override
    public String toString() {
        return "ShopBean{" +
                "shopid=" + shopid +
                ", shoptitle='" + shoptitle + '\'' +
                ", shopprice=" + shopprice +
                ", shoptype=" + shoptype +
                ", shoppictureurl='" + shoppictureurl + '\'' +
                '}';
    }

    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public String getShoptitle() {
        return shoptitle;
    }

    public void setShoptitle(String shoptitle) {
        this.shoptitle = shoptitle;
    }

    public double getShopprice() {
        return shopprice;
    }

    public void setShopprice(double shopprice) {
        this.shopprice = shopprice;
    }

    public int getShoptype() {
        return shoptype;
    }

    public void setShoptype(int shoptype) {
        this.shoptype = shoptype;
    }

    public String getShoppictureurl() {
        return shoppictureurl;
    }

    public void setShoppictureurl(String shoppictureurl) {
        this.shoppictureurl = shoppictureurl;
    }
}
