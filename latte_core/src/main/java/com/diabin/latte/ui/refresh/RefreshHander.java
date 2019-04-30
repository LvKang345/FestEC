package com.diabin.latte.ui.refresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.diabin.latte.app.Latte;
import com.diabin.latte.net.RestClient;
import com.diabin.latte.net.callback.ISuccess;
import com.diabin.latte.ui.recycler.DataConverter;
import com.diabin.latte.ui.recycler.MultipleRecyclerAdapter;

/**
 * Copyright (C)
 *
 * @file: RefreshHander
 * @author: 345
 * @Time: 2019/4/27 13:16
 * @description: 这是SwipeRefreshLayout内部的一个接口，用来监听 Refresh 的操作
 */
public class RefreshHander implements
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener {

    private final SwipeRefreshLayout REFRESH_LAYOUT ;
    private final PagingBean BEAN;
    private final RecyclerView RECYCLERVIEW ;
    private MultipleRecyclerAdapter mAdapter = null;
    private final DataConverter CONVERTER;



    private RefreshHander(SwipeRefreshLayout refreshLayout,
                         RecyclerView recyclerView,
                         DataConverter converter,
                         PagingBean bean) {
        REFRESH_LAYOUT = refreshLayout;
        //监听滑动事件
        REFRESH_LAYOUT.setOnRefreshListener(this);
        this.RECYCLERVIEW = recyclerView;
        this.CONVERTER = converter;
        this.BEAN = bean;
    }

    public static RefreshHander creawte(SwipeRefreshLayout swipeRefreshLayout,
                                        RecyclerView recyclerView,
                                        DataConverter converter){
        return new RefreshHander(swipeRefreshLayout,recyclerView,converter,new PagingBean());

    }

    private void refresh(){
        //要开始加载了
        REFRESH_LAYOUT.setRefreshing(true);
        Latte.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新事件结束
            }
        },2000);

    }

    public void firstPage(String url){
                REFRESH_LAYOUT.setRefreshing(false);
        BEAN.setDelayed(1000);
        RestClient.builder()
                .url(url)
                .success(new ISuccess() {
                    @Override
                    public void OnSuccess(String response) {
                        final JSONObject object= JSON.parseObject(response);
                        BEAN.setTotal(object.getInteger("total"))
                                .setPageSize(object.getInteger("page_size"));
                        //设置Adapter
                        mAdapter = MultipleRecyclerAdapter.create(CONVERTER.setJsonData(response));
                        //该接口已经在本类中实现,当滑动到在最后一个item时回调
                        mAdapter.setOnLoadMoreListener(RefreshHander.this,RECYCLERVIEW);
                        //设置适配器
                        RECYCLERVIEW.setAdapter(mAdapter);
                        BEAN.addIndex();
                    }
                })
                .build()
                .get();
    }
    @Override
    public void onRefresh() {
        refresh();
    }

    /**
     * 当滑动到最后一个item时 回调
     */
    @Override
    public void onLoadMoreRequested() {
        //加载完成
//        mAdapter.loadMoreComplete();
        //加载失败
//        mAdapter.loadMoreFail();
        //没有更多数据
        mAdapter.loadMoreEnd();
    }
}