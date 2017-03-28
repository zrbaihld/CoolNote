package com.htq.baidu.coolnote.ui;

import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.utils.SystemUtils;
import com.htq.baidu.coolnote.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by htq on 2016/8/12.
 */
public class SettingFragment extends Fragment {
    @BindView(R.id.tb_lock)
    ToggleButton mTbLock;
    @BindView(R.id.tb_tran)
    ToggleButton mTbTran;
    @BindView(R.id.lin)
    LinearLayout mLinL;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container,
                false);

        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onStart() {
        initData();
        super.onStart();
    }


    private void initData() {

        SystemUtils util=new SystemUtils(getActivity());
        boolean isSetLock =util.getBoolean("isSetLock");
        if(isSetLock)
            mTbLock.setToggleOn();
        else
            mTbLock.setToggleOff();

        boolean isTran =util.getBoolean("isTran");
        if(isTran) {
            mTbTran.setToggleOn();
            mLinL.setAlpha(0.55f);
        }
        else {
            mTbTran.setToggleOff();

        }
    }

    private void initView()
    {
        mTbLock.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
               // AppContext.setLoadImage(on);
                if(on) {

                    Intent intent = new Intent(getActivity(), SetLockActivity.class);
                    startActivity(intent);
                }
                else
                {
                    new SystemUtils(getActivity()).setBoolean("isSetLock", false);
                }
            }
        });
        mTbTran.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                // AppContext.setLoadImage(on);
                if(on) {

                    new SystemUtils(getActivity()).setBoolean("isTran", true);
                    mLinL.setAlpha(0.55f);
                }
                else
                {
                    new SystemUtils(getActivity()).setBoolean("isTran", false);
                    mLinL.setAlpha(1);
                }
            }
        });


    }
    
}
