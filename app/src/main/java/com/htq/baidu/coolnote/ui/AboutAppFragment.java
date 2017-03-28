package com.htq.baidu.coolnote.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htq.baidu.coolnote.R;

/**
 * Created by htq on 2016/8/11.
 */
public class AboutAppFragment extends Fragment {
    private View baseView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseView=inflater.inflate(R.layout.about_me_fragment, null);
        initView();
        return baseView;

    }
    private void initView()
    {
        TextView aboutMeTv=(TextView) baseView.findViewById(R.id.about_me_tv);
        Linkify.addLinks(aboutMeTv, Linkify.ALL);
    }

}
