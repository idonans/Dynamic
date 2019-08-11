package com.idonans.dynamic.page;

import com.idonans.dynamic.DynamicView;

import java.util.Collection;

public interface PageView<E> extends DynamicView {

    boolean hasPageContent();

    void showInitLoading();

    void hideInitLoading();

    void showPrePageLoading();

    void hidePrePageLoading();

    void showNextPageLoading();

    void hideNextPageLoading();

    void onInitDataLoad(Collection<E> items);

    void onInitDataLoadFail(Throwable e);

    void onPrePageDataLoad(Collection<E> items);

    void onPrePageDataLoadFail(Throwable e);

    void onNextPageDataLoad(Collection<E> items);

    void onNextPageDataLoadFail(Throwable e);

}
