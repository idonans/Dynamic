package com.idonans.dynamic.page.uniontype.impl;

import android.view.View;

import androidx.annotation.NonNull;

import com.idonans.dynamic.R;
import com.idonans.dynamic.page.uniontype.OnRetryActionListener;
import com.idonans.lang.util.ViewUtil;
import com.idonans.uniontype.Host;
import com.idonans.uniontype.UnionTypeViewHolder;

public class UnionTypeStatusLoadFailViewHolder extends UnionTypeViewHolder<Object> {

    public UnionTypeStatusLoadFailViewHolder(@NonNull Host host, int layout) {
        super(host, layout);
    }

    @Override
    public void onBind(int position, Object itemObject) {
        View retry = itemView.findViewById(R.id.retry);
        if (retry != null) {
            ViewUtil.onClick(retry, v -> {
                if (itemObject instanceof OnRetryActionListener) {
                    ((OnRetryActionListener) itemObject).onRetryAction();
                }
            });
        }
    }

}
