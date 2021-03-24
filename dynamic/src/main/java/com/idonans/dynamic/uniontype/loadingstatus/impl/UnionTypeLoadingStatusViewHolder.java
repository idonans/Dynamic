package com.idonans.dynamic.uniontype.loadingstatus.impl;

import android.view.View;

import androidx.annotation.NonNull;

import com.idonans.dynamic.LoadingStatusCallback;
import com.idonans.dynamic.LoadingStatusCallbackHost;
import com.idonans.dynamic.R;
import com.idonans.lang.util.ViewUtil;
import com.idonans.uniontype.Host;
import com.idonans.uniontype.UnionTypeViewHolder;

public class UnionTypeLoadingStatusViewHolder extends UnionTypeViewHolder {

    public UnionTypeLoadingStatusViewHolder(@NonNull Host host, int layout) {
        super(host, layout);
    }

    @Override
    public void onBind(int position, @NonNull Object itemObject) {
        View retry = itemView.findViewById(R.id.retry);
        if (retry != null) {
            ViewUtil.onClick(retry, v -> {
                if (itemObject instanceof LoadingStatusCallbackHost) {
                    LoadingStatusCallback callback = ((LoadingStatusCallbackHost) itemObject).getLoadingStatusCallback();
                    if (callback != null) {
                        callback.onRetry();
                    }
                }
            });
        }
    }

}
