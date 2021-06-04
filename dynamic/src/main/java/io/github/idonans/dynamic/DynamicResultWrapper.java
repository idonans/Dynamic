package io.github.idonans.dynamic;

import androidx.annotation.NonNull;

public class DynamicResultWrapper<A, B> {

    private final DynamicResult<A, B> mTarget;

    public DynamicResultWrapper(@NonNull DynamicResult<A, B> target) {
        mTarget = target;
    }

    @NonNull
    public final DynamicResult<A, B> getTarget() {
        return mTarget;
    }

}
