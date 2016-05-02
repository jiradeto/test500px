package com.jiradeto.liveat500px.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.widget.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.jiradeto.liveat500px.R;
import com.jiradeto.liveat500px.adapter.PhotoListAdapter;
import com.jiradeto.liveat500px.dao.PhotoItemCollectionDao;
import com.jiradeto.liveat500px.manager.PhotoListManager;
import com.jiradeto.liveat500px.manager.http.HttpManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MainFragment extends Fragment {
    //Variable
    ListView listview;
    PhotoListAdapter listAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    PhotoListManager photoListManager;
    Button btnNewPhoto;


    // Function

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize fragment variable
        photoListManager = new PhotoListManager();

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        //return
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void initInstances(View rootView, Bundle savedInstanceState) {


        btnNewPhoto = (Button) rootView.findViewById(R.id.btnNewPhotos);
        btnNewPhoto.setOnClickListener(buttonClickListener);

        listview = (ListView) rootView.findViewById(R.id.lv1);
        listAdapter = new PhotoListAdapter();
        listAdapter.setDao(photoListManager.getDao());
        listview.setAdapter(listAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(poolToRefreshListener);
        // Make swipe working at 1st item listview only
        listview.setOnScrollListener(listViewScrollListener);

        // Initial load dat
        if (savedInstanceState == null)
            refreshData();


    }


    private void reloadData() {
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().LoadPhotoList();
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_RELOAD));
    }


    private void refreshData() {
        if (photoListManager.getCount() == 0)
            reloadData();
        else
            reloadDataNewer();
    }

    private void reloadDataNewer() {
        int maxId = photoListManager.getMaximunId();
        Call<PhotoItemCollectionDao> call = HttpManager
                .getInstance()
                .getService()
                .LoadPhotoListAfterId(maxId);
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_RELOAD_NEWER));
    }


    boolean isLoadingMore = false;

    private void loadMoreData() {
        if (isLoadingMore)
            return;
        isLoadingMore = true;
        int minId = photoListManager.getMinumumId();
        Call<PhotoItemCollectionDao> call = HttpManager
                .getInstance()
                .getService()
                .LoadPhotoListBeforeId(minId);
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_LOAD_MORE));
    }


    private void showButtonNewPhotos() {
        btnNewPhoto.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(Contextor.getInstance().getContext(), R.anim.zoon_fade_in);
        btnNewPhoto.startAnimation(animation);

    }

    private void hideButtonNewPhotos() {
        btnNewPhoto.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(Contextor.getInstance().getContext(), R.anim.zoom_fade_out);
        btnNewPhoto.startAnimation(animation);

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO: Save PhotoManager
        // Save Instance State here
        outState.putBundle("photoListManager", photoListManager.onSaveInstanceState());
    }


    private void onRestoreInstanceState(Bundle savedInstanceState) {
        photoListManager.onRestoreInstanceState(savedInstanceState.getBundle("photoListManager"));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void showToast(String txt) {
        Toast.makeText(Contextor.getInstance().getContext(), txt, Toast.LENGTH_SHORT).show();
    }

    /*************
     * Listener Zone
     **************/

    final View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnNewPhoto) {
                hideButtonNewPhotos();
                listview.smoothScrollToPosition(0);
            }
        }
    };

    final SwipeRefreshLayout.OnRefreshListener poolToRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshData();
        }
    };

    final AbsListView.OnScrollListener listViewScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view,
                             int firstVisibleItem,
                             int visibleItemCount,
                             int totalItemCount) {
            swipeRefreshLayout.setEnabled(firstVisibleItem == 0);
            if (view == listview) {

                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    // Load more handler
                    if (photoListManager.getCount() > 0) {
                        loadMoreData();
                    }
                }
            }
        }
    };


    /*************
     * Inner Class
     **************/

    class PhotoListLoadCallback implements Callback<PhotoItemCollectionDao> {
        public static final int MODE_RELOAD = 1;
        public static final int MODE_RELOAD_NEWER = 2;
        public static final int MODE_LOAD_MORE = 3;
        int mode;

        public PhotoListLoadCallback(int mode) {
            this.mode = mode;

        }

        @Override
        public void onResponse(Call<PhotoItemCollectionDao> call, Response<PhotoItemCollectionDao> response) {

            //set swipe to stop
            swipeRefreshLayout.setRefreshing(false);
            if (response.isSuccess()) {
                PhotoItemCollectionDao dao = response.body();

                int firstVisiblePositoin = listview.getFirstVisiblePosition();
                View c = listview.getChildAt(0);
                int top = c == null ? 0 : c.getTop();

                if (mode == MODE_RELOAD_NEWER) {
                    photoListManager.insertDaoAtTopPosition(dao);
                } else if (mode == MODE_LOAD_MORE) {
                    photoListManager.appendDaoAtBottom(dao);
                    clearLoadingMoreFlagIfCapable(mode);
                } else {
                    photoListManager.setDao(dao);
                }
                listAdapter.setDao(photoListManager.getDao());
                listAdapter.notifyDataSetChanged();

                if (mode == MODE_RELOAD_NEWER) {
                    // maintain scroll position
                    int additionalSize =
                            (dao != null && dao.getData() != null) ? dao.getData().size() : 0;
                    listAdapter.increaseLastPosition(additionalSize);
                    listview.setSelectionFromTop(firstVisiblePositoin + additionalSize
                            , top);
                    if (additionalSize > 0)
                        showButtonNewPhotos();
                } else {

                }
                showToast("Load Completed");
            } else {
                clearLoadingMoreFlagIfCapable(mode);
                try {

                    showToast(response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<PhotoItemCollectionDao> call, Throwable t) {

            clearLoadingMoreFlagIfCapable(mode);
            // TODO : Clear loading more Flag
            swipeRefreshLayout.setRefreshing(false);
            showToast(t.toString());
        }

        private void clearLoadingMoreFlagIfCapable(int mode) {

            if (mode == MODE_LOAD_MORE) {
                isLoadingMore = false;
            }
        }
    }
}
