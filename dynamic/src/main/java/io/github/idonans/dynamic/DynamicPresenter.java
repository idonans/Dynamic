package io.github.idonans.dynamic;

import androidx.annotation.Nullable;

import io.github.idonans.core.WeakAbortSignal;

public class DynamicPresenter<T extends DynamicView> extends WeakAbortSignal {

    public DynamicPresenter(T view) {
        super(view);
    }

    @Nullable
    public T getView() {
        //noinspection unchecked
        T view = (T) getObject();
        if (isAbort()) {
            return null;
        }
        return view;
    }

}
