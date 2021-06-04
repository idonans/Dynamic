package io.github.idonans.dynamic.page;

import androidx.annotation.NonNull;

import java.util.Collection;

import io.github.idonans.dynamic.DynamicView;

public interface PageView<E> extends DynamicView {

    boolean isClearPageContentWhenRequestInit();

    void showInitLoading();

    void hideInitLoading();

    void showPrePageLoading();

    void showPrePageManualToLoadMore();

    void hidePrePageLoading();

    void showNextPageLoading();

    void showNextPageManualToLoadMore();

    void hideNextPageLoading();

    void onInitDataLoad(@NonNull Collection<E> items);

    void onInitDataEmpty();

    void onInitDataLoadFail(@NonNull Throwable e);

    void onPrePageDataLoad(@NonNull Collection<E> items);

    void onPrePageDataEmpty();

    void onPrePageDataLoadFail(@NonNull Throwable e);

    void onNextPageDataLoad(@NonNull Collection<E> items);

    void onNextPageDataEmpty();

    void onNextPageDataLoadFail(@NonNull Throwable e);

}
