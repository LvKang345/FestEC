package com.diabin.latte.ec.main.index;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.diabin.latte.deleggate.bottom.BottomItemDelegate;
import com.diabin.latte.ec.R;
import com.diabin.latte.ec.R2;
import com.diabin.latte.ec.main.EcBottomDelegate;
import com.diabin.latte.ui.recycler.BaseDecoration;
import com.diabin.latte.ui.refresh.RefreshHander;
import com.diabin.latte.util.dimen.SetToolBar;
import com.diabin.latte.util.dimen.StatusBarHeight;
import com.joanzapata.iconify.widget.IconTextView;
import butterknife.BindView;

/**
 * Copyright (C)
 *
 * @file: IndexDelegate
 * @author: 345
 * @Time: 2019/4/26 14:28
 * @description: ${DESCRIPTION}
 */
public class IndexDelegate extends BottomItemDelegate {

    @BindView(R2.id.rv_index)
    RecyclerView mRecyclerView = null;
    @BindView(R2.id.srl_index)
    SwipeRefreshLayout mRefreshLayout = null;
    @BindView(R2.id.tb_index)
    Toolbar mToobar = null;
    @BindView(R2.id.icon_index_scan)
    IconTextView mIconScan = null;
    @BindView(R2.id.et_search_view)
    AppCompatEditText mSearchView = null;

    private RefreshHander mRefreshHandler = null;
    private int mDy=0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        SetToolBar.setToolBar(mToobar);
        mRefreshHandler = RefreshHander.creawte(mRefreshLayout,mRecyclerView,new IndexDataConverter());
    }

    private void setToolBar() {
        //获取 状态栏的高度，以像素为单位
        int statusBar = StatusBarHeight.getStaticBarHeight();
        //设置toolbar 位于 状态栏 的下方，并且距底部 20个像素
        //注意：toolbar的布局中高度只能是 wrap_content
        mToobar.setPadding(0,statusBar,0,20);
    }

    /**
     * 初始化 下拉刷新
     */
    private void initRefreshLayout() {
        mRefreshLayout.setColorSchemeColors(
                Color.BLUE, Color.RED, Color.GRAY);
        //第一个参数为true，表示下拉的过程中按钮由小变大，回弹过程有大变小
        // 第二个参数为起始高度，第三个为终止高度
        mRefreshLayout.setProgressViewOffset(true, 120, 300);
    }

    private void initRecyclerView(){
        GridLayoutManager manager = new GridLayoutManager(getContext(),4);
        mRecyclerView.setLayoutManager(manager);
        //设置分割线
        mRecyclerView.addItemDecoration(BaseDecoration.create(ContextCompat.getColor(getContext(), R.color.app_background), 5));
        final EcBottomDelegate ecBottomDelegate  = getParentDelegate();
        //监听事件
        mRecyclerView.addOnItemTouchListener(IndexItemClickListener.create(ecBottomDelegate));
        //滑动监听，设置 toolbar
        mRecyclerView.addOnScrollListener(MyScrollListener.create(mToobar));
    }

    /**
     * 懒加载时 回调的方法，当前界面显示时，会回调该方法
     * @param savedInstanceState
     */
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initRefreshLayout();
        initRecyclerView();
        mRefreshHandler.firstPage("index.json");
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_index;
    }
}
