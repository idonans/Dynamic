package io.github.idonans.dynamic;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import java.util.ArrayList;
import java.util.List;

import io.github.idonans.core.WeakAbortSignal;
import io.github.idonans.lang.DisposableHolder;

public class DynamicPresenter<T extends DynamicView> extends WeakAbortSignal {

    protected final List<DisposableHolder> mRequestHolderList = new ArrayList<>();

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

    /**
     * 除了指定的请求外，清除其它请求
     */
    @UiThread
    protected void clearRequestExcept(DisposableHolder... excepts) {
        for (DisposableHolder target : mRequestHolderList) {
            boolean matchExcept = false;
            if (excepts != null) {
                for (DisposableHolder except : excepts) {
                    if (target == except) {
                        matchExcept = true;
                        break;
                    }
                }
            }
            if (!matchExcept) {
                if (target != null) {
                    target.clear();
                }
            }
        }
    }

    /**
     * 清除指定请求
     */
    @UiThread
    public static void clearRequest(DisposableHolder... targets) {
        if (targets != null) {
            for (DisposableHolder target : targets) {
                if (target != null) {
                    target.clear();
                }
            }
        }
    }

}
