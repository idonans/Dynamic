package io.github.idonans.dynamic;

import androidx.annotation.Nullable;

public interface LoadingStatusCallbackHost {

    @Nullable
    LoadingStatusCallback getLoadingStatusCallback();

    @Nullable
    Throwable getCause();

}
