package com.idonans.dynamic.page;

import androidx.annotation.NonNull;

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

    void onInitDataLoad(@NonNull Collection<E> items);

    void onInitDataLoadFail(@NonNull Throwable e);

    void onPrePageDataLoad(@NonNull Collection<E> items);

    void onPrePageDataLoadFail(@NonNull Throwable e);

    void onNextPageDataLoad(@NonNull Collection<E> items);

    void onNextPageDataLoadFail(@NonNull Throwable e);

}
