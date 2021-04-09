package io.github.idonans.dynamic.single;

import androidx.annotation.NonNull;

import java.util.Collection;

public interface StatusSingleViewAdapter<E> {

    boolean hasContent();

    void showLargeLoading(Object object);

    void showSmallLoading(Object object);

    void hideLoading();

    void showData(@NonNull Collection<E> items);

    void showEmptyData(Object object);

    void showLargeError(Object object);

    void showSmallError(Object object);

    void clearContent();

}
