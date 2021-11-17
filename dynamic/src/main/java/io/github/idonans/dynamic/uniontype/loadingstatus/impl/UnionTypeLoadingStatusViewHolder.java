package io.github.idonans.dynamic.uniontype.loadingstatus.impl;

import android.view.View;

import androidx.annotation.NonNull;

import io.github.idonans.dynamic.LoadingStatusCallback;
import io.github.idonans.dynamic.LoadingStatusCallbackHost;
import io.github.idonans.dynamic.R;
import io.github.idonans.lang.util.ViewUtil;
import io.github.idonans.uniontype.Host;
import io.github.idonans.uniontype.UnionTypeViewHolder;

public class UnionTypeLoadingStatusViewHolder extends UnionTypeViewHolder {

    public UnionTypeLoadingStatusViewHolder(@NonNull Host host, int layout) {
        super(host, layout);
    }

    @Override
    public void onBindUpdate() {
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
