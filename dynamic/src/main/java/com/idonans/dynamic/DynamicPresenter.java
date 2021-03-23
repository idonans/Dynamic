package com.idonans.dynamic;

import androidx.annotation.Nullable;

import com.idonans.core.WeakAbortSignal;

public class DynamicPresenter<T extends DynamicView> extends WeakAbortSignal {

    public DynamicPresenter(T view) {
        super(view);
    }

    @Nullable
    public T getView() {
        T view = (T) getObject();
        if (isAbort()) {
            return null;
        }
        return view;
    }

}
