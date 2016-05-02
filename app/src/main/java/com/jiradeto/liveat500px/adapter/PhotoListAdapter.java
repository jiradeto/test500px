package com.jiradeto.liveat500px.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jiradeto.liveat500px.R;
import com.jiradeto.liveat500px.dao.PhotoItemCollectionDao;
import com.jiradeto.liveat500px.dao.PhotoItemDao;
import com.jiradeto.liveat500px.manager.PhotoListManager;
import com.jiradeto.liveat500px.view.PhotoListItem;

/**
 * Created by 515895 on 2/12/2016.
 */
public class PhotoListAdapter extends BaseAdapter {
    int lastPosition = -1;
    PhotoItemCollectionDao dao;

    public void setDao(PhotoItemCollectionDao dao) {
        this.dao = dao;
    }

    @Override
    public int getCount() {
        if (dao == null) {
            return 1;
        }
        if (dao == null) {
            return 1;
        }
        return dao.getData().size() + 1;

    }

    @Override
    public Object getItem(int position) {

        return dao.getData().get(position);
        //return PhotoListManager.getInstance().getDao().getData().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == getCount() - 1 ? 1 : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == getCount() - 1) {
            //PregressBar
            ProgressBar item;
            if (convertView != null) {
                item = (ProgressBar) convertView;
            } else {
                item = new ProgressBar(parent.getContext());
            }
            return item;

        }
        PhotoListItem item;

        if (convertView != null)
            item = (PhotoListItem) convertView;
        else
            item = new PhotoListItem(parent.getContext());

        PhotoItemDao dao = (PhotoItemDao) getItem(position);
        item.setNameText(dao.getCaption());
        item.setDescriptionText(dao.getUsername() + "\n" + dao.getCamera());
        item.setImageUrl(dao.getImageUrl());


        if (position > lastPosition) {
            Animation anim = AnimationUtils.loadAnimation(parent.getContext(), R.anim.up_from_bottom);
            item.startAnimation(anim);
            lastPosition = position;
        }


        return item;


    }

    public void increaseLastPosition(int amout) {
        lastPosition += amout;
    }

}
