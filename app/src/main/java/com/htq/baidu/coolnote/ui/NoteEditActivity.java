package com.htq.baidu.coolnote.ui;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.FrameLayout;

import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.utils.DialogHelp;
import com.htq.baidu.coolnote.utils.SystemUtils;

/**
 * Created by htq on 2016/8/9.
 */
public class NoteEditActivity extends AppCompatActivity {


    private  NoteEditFragment noteEditFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        setTitle("添加记录");
        initFragment();

    }

    private void initFragment()
    {
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        noteEditFragment=new  NoteEditFragment();
        ft.replace(R.id.main_fraglayout, noteEditFragment,null);
        ft.commit();
    }
    @Override
    public void onBackPressed() {

       if(!noteEditFragment.onBackPressed())
          super.onBackPressed();

    }


}
