package io.github.idonans.dynamic;

public class RequestStatus {

    private boolean mLoading;
    private boolean mError;
    private boolean mEnd;
    private boolean mManualToLoad; // 手动触发模式，如点击加载

    public boolean isLoading() {
        return mLoading;
    }

    public boolean isError() {
        return mError;
    }

    public boolean isEnd() {
        return mEnd;
    }

    public boolean isManualToLoad() {
        return mManualToLoad;
    }

    public void reset() {
        this.mLoading = false;
        this.mError = false;
        this.mEnd = false;
        this.mManualToLoad = false;
    }

    public boolean allowRequest() {
        return !this.mLoading && !this.mError && !this.mEnd && !this.mManualToLoad;
    }

    public void setLoading() {
        mLoading = true;
        mError = false;
        mEnd = false;
        mManualToLoad = false;
    }

    public void setError() {
        mLoading = false;
        mError = true;
        mEnd = false;
        mManualToLoad = false;
    }

    public void setEnd(boolean end) {
        mLoading = false;
        mError = false;
        mEnd = end;
        mManualToLoad = false;
    }

    public void setManualToLoad() {
        mLoading = false;
        mError = false;
        mEnd = false;
        mManualToLoad = true;
    }

}
