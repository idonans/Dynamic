package com.idonans.dynamic.page;

import androidx.annotation.NonNull;

import com.idonans.dynamic.DynamicView;

import java.util.Collection;

public interface PageView<E> extends DynamicView {

    boolean isClearPageContentWhenRequestInit();

    boolean hasPageContent();

    void showInitLoading();

    void hideInitLoading();

    void showPrePageLoading();

    void hidePrePageLoading();

    void showNextPageLoading();

    void hideNextPageLoading();

    void onInitDataLoad(@NonNull Collection<E> items);

    void onInitDataEmpty();

    void onInitDataLoadFail(@NonNull Throwable e);

    void onPrePageDataLoad(@NonNull Collection<E> items);

    void onPrePageDataEmpty();

    void onPrePageDataLoadFail(@NonNull Throwable e);

    void onPrePageManualToLoadMore();

    void onNextPageDataLoad(@NonNull Collection<E> items);

    void onNextPageDataEmpty();

    void onNextPageDataLoadFail(@NonNull Throwable e);

    void onNextPageManualToLoadMore();

}
