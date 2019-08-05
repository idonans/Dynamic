package com.idonans.dynamic.page.uniontype;

import com.idonans.dynamic.page.PagePresenter;
import com.idonans.dynamic.page.PageView;
import com.idonans.uniontype.UnionTypeAdapter;
import com.idonans.uniontype.UnionTypeItemObject;

import java.util.Collection;

public class UnionTypePageView implements PageView<UnionTypeItemObject> {

    private final UnionTypeAdapter mUnionTypeAdapter;
    private PagePresenter<UnionTypeItemObject, UnionTypePageView> mPresenter;

    private boolean mEnablePrePage;
    private boolean mEnableNextPage;

    public UnionTypePageView(UnionTypeAdapter unionTypeAdapter, boolean enablePrePage, boolean enableNextPage) {
        mUnionTypeAdapter = unionTypeAdapter;
        mEnablePrePage = enablePrePage;
        mEnableNextPage = enableNextPage;
        mUnionTypeAdapter.setOnLoadPrePageListener(new UnionTypeAdapter.OnLoadPrePageListener() {
            @Override
            public void onLoadPrePage() {

            }
        });
    }

    public void setPresenter(PagePresenter<UnionTypeItemObject, UnionTypePageView> presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showInitLoading() {

    }

    @Override
    public void hideInitLoading() {

    }

    @Override
    public void showPrePageLoading() {

    }

    @Override
    public void hidePrePageLoading() {

    }

    @Override
    public void showNextPageLoading() {

    }

    @Override
    public void hideNextPageLoading() {

    }

    @Override
    public void onInitDataLoad(Collection<UnionTypeItemObject> items) {

    }

    @Override
    public void onInitDataLoadFail(Throwable e) {

    }

    @Override
    public void onPrePageDataLoad(Collection<UnionTypeItemObject> items) {

    }

    @Override
    public void onPrePageDataLoadFail(Throwable e) {

    }

    @Override
    public void onNextPageDataLoad(Collection<UnionTypeItemObject> items) {

    }

    @Override
    public void onNextPageDataLoadFail(Throwable e) {

    }
}
