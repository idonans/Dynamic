package com.idonans.example.dynamic;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.idonans.dynamic.page.PageView;
import com.idonans.dynamic.page.StatusPagePresenter;
import com.idonans.dynamic.page.UnionTypeStatusPageView;
import com.idonans.dynamic.pulllayout.PullLayout;
import com.idonans.dynamic.uniontype.loadingstatus.UnionTypeLoadingStatus;
import com.idonans.lang.thread.Threads;
import com.idonans.uniontype.Host;
import com.idonans.uniontype.UnionTypeAdapter;
import com.idonans.uniontype.UnionTypeItemObject;
import com.idonans.uniontype.UnionTypeViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private PullLayout mPullLayout;
    private RecyclerView mRecyclerView;
    private UnionTypeStatusPageView mView;
    private Presenter mPresenter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPullLayout = findViewById(R.id.pull_layout);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        UnionTypeAdapter adapter = new UnionTypeAdapter();
        adapter.setHost(Host.Factory.create(this, mRecyclerView, adapter));
        adapter.setUnionTypeMapper(new UnionType());
        mRecyclerView.setAdapter(adapter);

        mView = new UnionTypeStatusPageView(adapter) {
            @Override
            public void hideInitLoading() {
                super.hideInitLoading();
                if (mPullLayout != null) {
                    mPullLayout.setRefreshing(false, false);
                }
            }
        };
        mView.setAlwaysHidePrePageNoMoreData(true);
        mPresenter = new Presenter(mView);
        mView.setPresenter(mPresenter);
        mPresenter.requestInit();
        mPullLayout.setOnRefreshListener(pullLayout -> {
            Timber.v("pull layout on refresh");
            if (mPresenter != null) {
                Timber.v("pull layout on refresh request init");
                mPresenter.requestInit(true);
            }
        });
    }

    private static class Presenter extends StatusPagePresenter<UnionTypeItemObject, UnionTypeStatusPageView> {

        private int mPrePageNo;
        private int mNextPageNo;

        public Presenter(UnionTypeStatusPageView view) {
            super(view, false, true);
        }

        @Nullable
        @Override
        protected SingleSource<Collection<UnionTypeItemObject>> createInitRequest() throws Exception {
            return Single.fromCallable(() -> {
                Threads.sleepQuietly(2000);
                List<UnionTypeItemObject> items = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    items.add(UnionTypeItemObject.valueOf(UnionType.UNION_TYPE_TEXT, "page 0 #" + i));
                }
                return items;
            });
        }

        @Override
        protected void onInitRequestResult(@NonNull PageView<UnionTypeItemObject> view, @NonNull Collection<UnionTypeItemObject> items) {
            mPrePageNo = 0;
            mNextPageNo = 0;
            super.onInitRequestResult(view, items);
        }

        @Nullable
        @Override
        protected SingleSource<Collection<UnionTypeItemObject>> createPrePageRequest() throws Exception {
            return Single.fromCallable(() -> {
                Threads.sleepQuietly(2000);
                List<UnionTypeItemObject> items = new ArrayList<>();
                for (int i = 0; i < 10 && mPrePageNo > -3; i++) {
                    items.add(UnionTypeItemObject.valueOf(UnionType.UNION_TYPE_TEXT, "page " + (mPrePageNo - 1) + " #" + i));
                }
                return items;
            });
        }

        @Override
        protected void onPrePageRequestResult(@NonNull PageView<UnionTypeItemObject> view, @NonNull Collection<UnionTypeItemObject> items) {
            mPrePageNo--;
            super.onPrePageRequestResult(view, items);
        }

        @Nullable
        @Override
        protected SingleSource<Collection<UnionTypeItemObject>> createNextPageRequest() throws Exception {
            return Single.fromCallable(() -> {
                Threads.sleepQuietly(2000);
                List<UnionTypeItemObject> items = new ArrayList<>();
                for (int i = 0; i < 10 && mNextPageNo < 3; i++) {
                    items.add(UnionTypeItemObject.valueOf(UnionType.UNION_TYPE_TEXT, "page " + (mNextPageNo + 1) + " #" + i));
                }
                return items;
            });
        }

        @Override
        protected void onNextPageRequestResult(@NonNull PageView<UnionTypeItemObject> view, @NonNull Collection<UnionTypeItemObject> items) {
            mNextPageNo++;
            super.onNextPageRequestResult(view, items);
        }
    }

    private class UnionType extends UnionTypeLoadingStatus {
        private static final int UNION_TYPE_TEXT = 1;

        private UnionType() {
            put(UNION_TYPE_TEXT, UnionTypeTextViewHolder::new);
        }
    }

    private class UnionTypeTextViewHolder extends UnionTypeViewHolder<Object> {

        private TextView mText1;

        public UnionTypeTextViewHolder(@NonNull Host host) {
            super(host, R.layout.activity_main_union_type_text);
            mText1 = itemView.findViewById(R.id.text_1);
        }

        @Override
        public void onBind(int position, Object itemObject) {
            mText1.setText(String.valueOf(itemObject));
        }
    }

}

