package io.github.idonans.dynamic;

import androidx.annotation.Nullable;

import java.util.Collection;

public class DynamicResult<A, B> {

    @Nullable
    public Collection<A> items;

    @Nullable
    public B payload;

    @Nullable
    public Throwable error;

    public DynamicResult<A, B> setItems(@Nullable Collection<A> items) {
        this.items = items;
        return this;
    }

    public DynamicResult<A, B> setPayload(@Nullable B payload) {
        this.payload = payload;
        return this;
    }

    public DynamicResult<A, B> setError(@Nullable Throwable error) {
        this.error = error;
        return this;
    }

    /**
     * 当前数据是否出错(数据加载失败)
     */
    public boolean isError() {
        return this.items == null;
    }

    /**
     * 当前数据是否已经结束(没有更多数据了)
     */
    public boolean isEnd() {
        return this.items != null && this.items.isEmpty();
    }

}
