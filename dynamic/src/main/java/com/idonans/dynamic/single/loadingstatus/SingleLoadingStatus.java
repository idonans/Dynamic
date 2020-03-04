package com.idonans.dynamic.single.loadingstatus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.idonans.dynamic.LoadingStatusCallback;
import com.idonans.dynamic.LoadingStatusCallbackHost;
import com.idonans.dynamic.R;
import com.idonans.lang.util.ViewUtil;

public abstract class SingleLoadingStatus<T> {

    public View createEmptyDataView(ViewGroup parent, Object object) {
        View view = createEmptyDataView(parent, R.layout.union_type_loading_status_empty_data_view, object);
        bindObject(view, object);
        return view;
    }

    public void onEmptyDataViewRemoved(View view) {
    }

    protected View createEmptyDataView(ViewGroup parent, int layoutResId, Object object) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
    }

    public View createLoadFailLargeView(ViewGroup parent, Object object) {
        View view = createLoadFailLargeView(parent, R.layout.union_type_loading_status_load_fail_large_view, object);
        bindObject(view, object);
        return view;
    }

    public void onLoadFailLargeViewRemoved(View view) {
    }

    protected View createLoadFailLargeView(ViewGroup parent, int layoutResId, Object object) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
    }

    public View createLoadFailSmallView(ViewGroup parent, Object object) {
        View view = createLoadFailSmallView(parent, R.layout.union_type_loading_status_load_fail_small_view, object);
        bindObject(view, object);
        return view;
    }

    public void onLoadFailSmallViewRemoved(View view) {
    }

    protected View createLoadFailSmallView(ViewGroup parent, int layoutResId, Object object) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
    }

    public View createLoadingSmallView(ViewGroup parent, Object object) {
        View view = createLoadingSmallView(parent, R.layout.union_type_loading_status_loading_small_view, object);
        bindObject(view, object);
        return view;
    }

    public void onLoadingSmallViewRemoved(View view) {
    }

    protected View createLoadingSmallView(ViewGroup parent, int layoutResId, Object object) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
    }

    public View createLoadingLargeView(ViewGroup parent, Object object) {
        View view = createLoadingLargeView(parent, R.layout.union_type_loading_status_loading_large_view, object);
        bindObject(view, object);
        return view;
    }

    public void onLoadingLargeViewRemoved(View view) {
    }

    protected View createLoadingLargeView(ViewGroup parent, int layoutResId, Object object) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
    }

    protected void bindObject(View view, Object object) {
        View retry = view.findViewById(R.id.retry);
        if (retry != null) {
            ViewUtil.onClick(retry, v -> {
                if (object instanceof LoadingStatusCallbackHost) {
                    LoadingStatusCallback callback = ((LoadingStatusCallbackHost) object).getLoadingStatusCallback();
                    if (callback != null) {
                        callback.onRetry();
                    }
                }
            });
        }
    }

    public abstract View createDataView(ViewGroup parent, @NonNull T data);

    public void onDataViewRemoved(View view) {
    }

    public void onViewRemoved(View view) {
    }

    public void setStatusViewAnimation(@NonNull View statusView, @Nullable Animation showAnimation, @Nullable Animation hideAnimation) {
        statusView.setTag(R.id.dynamic_status_view_show_animation_object, showAnimation);
        statusView.setTag(R.id.dynamic_status_view_hide_animation_object, hideAnimation);
    }

    @Nullable
    public Animation getStatusViewShowAnimation(@NonNull View statusView) {
        return (Animation) statusView.getTag(R.id.dynamic_status_view_show_animation_object);
    }

    @Nullable
    public Animation getStatusViewHideAnimation(@NonNull View statusView) {
        return (Animation) statusView.getTag(R.id.dynamic_status_view_hide_animation_object);
    }

}
