package com.htq.baidu.coolnote.ui;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.adapter.BgPicGridAdapter;
import com.htq.baidu.coolnote.entity.BgPicEntity;
import com.htq.baidu.coolnote.utils.SystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by htq on 2016/8/11.
 */
public class ChangeBgFragment extends Fragment {

    private List<BgPicEntity> mBgPicList;
    private View baseView;
    private GridView mGridView;
    private BgPicGridAdapter mBgPicAdapter;
    private SystemUtils systemUtils;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_change_background, null);
        initBackgroundPic();
        initView();
        return baseView;
    }
        private void initView()
        {

            mGridView = (GridView) baseView.findViewById(R.id.change_background_grid);
            mBgPicAdapter = new BgPicGridAdapter(getActivity(),mBgPicList);
            mGridView.setOnItemClickListener(gridItemClickListener);
            mGridView.setAdapter(mBgPicAdapter);

        }
        private void initBackgroundPic()
        {
            AssetManager am = getActivity().getAssets();
            try {
                String[] drawableList = am.list("bkgs");
                mBgPicList = new ArrayList<BgPicEntity>();
                for (String path : drawableList) {
                    BgPicEntity bg = new BgPicEntity();
                    InputStream is = am.open("bkgs/" + path);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    bg.path = path;
                    bg.bitmap = bitmap;
                    mBgPicList.add(bg);
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        AdapterView.OnItemClickListener gridItemClickListener=new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                String path = ((BgPicEntity) mBgPicAdapter.getItem(position)).path;

                systemUtils = new SystemUtils(getActivity());
                systemUtils.saveBgPicPath(path);
                Bitmap bitmap = systemUtils.getBitmapByPath(getActivity(), path);
                if (bitmap != null) {
                    ((MainActivity) getActivity()).drawer.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                     mBgPicAdapter.notifyDataSetChanged();

                }
            }
        };


    }
