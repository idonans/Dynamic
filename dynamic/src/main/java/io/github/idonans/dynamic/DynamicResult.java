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

}
