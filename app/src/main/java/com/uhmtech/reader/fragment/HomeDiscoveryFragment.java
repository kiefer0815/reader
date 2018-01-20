package com.uhmtech.reader.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.uhmtech.reader.R;
import com.uhmtech.reader.adapter.AlbumAdapter;
import com.uhmtech.reader.base.BaseDataBindingFragment;
import com.uhmtech.reader.bean.AlbumsList;
import com.uhmtech.reader.component.log.RsLogger;
import com.uhmtech.reader.component.log.RsLoggerManager;
import com.uhmtech.reader.databinding.FragmentHomeDiscoveryBinding;
import com.uhmtech.reader.network.BaseSubscriber;
import com.uhmtech.reader.network.RestClientFactory;
import com.uhmtech.reader.ui.BookSearchActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kiefer on 2017/7/31.
 */

public class HomeDiscoveryFragment extends BaseDataBindingFragment<FragmentHomeDiscoveryBinding> implements
        View.OnClickListener{

        RsLogger logger = RsLoggerManager.getLogger();
        public static final String TAG = HomeDiscoveryFragment.class.getSimpleName();
        private Toolbar toolbar;
        private AlbumAdapter mAlbumAdapter;
        private GridLayoutManager mLayoutManager;
        private boolean mIsFirst = true;

        @Override
        public int setContent() {
                return R.layout.fragment_home_discovery;
        }

        public static HomeDiscoveryFragment newInstance() {
                return new HomeDiscoveryFragment();
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                initId();
                initListener();
                showContentView();
                mLayoutManager = new GridLayoutManager(getActivity(), 3);
                bindingView.xrvAblums.setLayoutManager(mLayoutManager);
        }

        @Override
        protected void loadData() {
                if (!mIsVisible || !mIsFirst) {
                        return;
                }

                addSubscription(RestClientFactory.createApi()
                        .getAlbums()
                        .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<AlbumsList>() {
                                @Override
                                public void onSuccess(AlbumsList listBean) {
                                        if (listBean !=null && listBean.getList().size() > 0) {
                                                if (mAlbumAdapter == null) {
                                                        mAlbumAdapter = new AlbumAdapter();
                                                }

                                                mAlbumAdapter.addAll(listBean.getList());
                                                mAlbumAdapter.notifyDataSetChanged();
                                                bindingView.xrvAblums.setAdapter(mAlbumAdapter);
                                                mIsFirst = false;
                                        }
                                }

                                @Override
                                public void onError(Throwable e) {
                                        super.onError(e);
                                        logger.e(TAG, e.getMessage());
                                }

                                @Override
                                public void onFinally(Throwable e) {
                                        super.onFinally(e);
                                }
                        }));
        }

        private void initId() {
             toolbar = bindingView.toolbar;
        }

        private void initListener(){
                bindingView.llTitleSearch.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
                switch (v.getId()) {
                case R.id.ll_title_search:
                        BookSearchActivity.start(getActivity());
                        break;
                default:
                        break;
                }
        }

        @Override
        public void onResume() {
                super.onResume();
        }

        @Override
        public void onPause() {
                super.onPause();
        }
}
