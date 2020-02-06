package com.idonans.dynamic.single;

import androidx.annotation.NonNull;

import com.idonans.dynamic.DynamicView;

import java.util.Collection;

public interface SingleView<E> extends DynamicView {

    boolean isClearContentWhenRequestInit();

    boolean hasContent();

    void showInitLoading();

    void hideInitLoading();

    void onInitDataLoad(@NonNull Collection<E> items);

    void onInitDataEmpty();

    void onInitDataLoadFail(@NonNull Throwable e);

}
