package com.wangln.shoplist;

/**
 * Created by Administrator on 2017/9/15 0015.
 */

public interface OnRefreshListner {
    //下拉状态过程
    public void onPull(float value, int mode);
    //松手到刷新状态
    public void onPullToRefresh(float value, int mode);
    //开始刷新
    public void onRefresh(int mode);
    //刷新结束后，回到初始状态过程
    public void onRelease(float value, int mode);
}
