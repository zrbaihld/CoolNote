package com.htq.baidu.coolnote.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.adapter.NotebookAdapter;
import com.htq.baidu.coolnote.db.NoteDatabase;
import com.htq.baidu.coolnote.entity.NotebookData;

import com.htq.baidu.coolnote.entity.OnResponseListener;
import com.htq.baidu.coolnote.entity.Response;
import com.htq.baidu.coolnote.entity.User;
import com.htq.baidu.coolnote.utils.AccountUtils;
import com.htq.baidu.coolnote.utils.Constants;
import com.htq.baidu.coolnote.utils.HTQAnimations;
import com.htq.baidu.coolnote.utils.SystemUtils;
import com.htq.baidu.coolnote.widget.HTQDragGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NoteBookFragment extends Fragment implements
        OnItemClickListener, OnRefreshListener {

    @BindView(R.id.frag_note_list)
    HTQDragGridView mGrid;
    @BindView(R.id.frag_note_trash)
    ImageView mImgTrash;
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private NoteDatabase noteDb;
    private List<NotebookData> datas;
    private NotebookAdapter adapter;
    private MainActivity aty;
    public static final int STATE_NONE = 0;
    public static final int STATE_REFRESH = 1;
    public static final int STATE_LOADMORE = 2;
    public static final int STATE_NOMORE = 3;
    public static final int STATE_PRESSNONE = 4;// 正在下拉但还没有到刷新的状态
    public static int mState = STATE_NONE;
    /**
     * 用来做更进一步人性化的防手抖策略时使用<br>
     * 比如由于手抖动上下拉菜单时拉动一部分，但又没有达到可刷新的时候，暂停一段时间，这个时候用户的逻辑应该是要移动item的。<br>
     * 其实应该还有一种根据setOnFocusChangeListener来改写的方法，我没有尝试。
     */
    // private static final Handler mHandler = new Handler() {
    // @Override
    // public void handleMessage(android.os.Message msg) {
    // mList.setDragEnable(true);
    // };
    // };

    /*********************************
     * function method
     ******************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_note, null,
                false);
        aty = (MainActivity) getActivity();
        ButterKnife.bind(this, rootView);
        initData();
        initView(rootView);
        if (new SystemUtils(getActivity()).isTarn())
            mGrid.setAlpha(0.55f);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).fab.showMenu(true);
        //setListCanPull();
    }

    @Override
    public void onDestroy() {
        noteDb.destroy();
        super.onDestroy();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Bundle bundle = new Bundle();
        bundle.putInt(NoteEditFragment.NOTE_FROMWHERE_KEY,
                NoteEditFragment.NOTEBOOK_ITEM);
        bundle.putSerializable(NoteEditFragment.NOTE_KEY, datas.get(position));

        Intent intent = new Intent(getActivity(), NoteEditActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_ARGS, bundle);
        startActivity(intent);


    }

    /*****************************
     * initialization method
     ***************************/


    public void initData() {
        if (noteDb == null) {
            noteDb = new NoteDatabase(aty);
        }
        if (datas == null) {
            datas = new ArrayList<>();
        }
        datas.clear();
        datas.addAll(noteDb.query(-1));
        // 查询操作，忽略耗时
        if (datas != null) {
            adapter = new NotebookAdapter(aty, datas);
        }
        Log.e("exce", "datas  " + datas.size());
        for (NotebookData data : datas) {
            Log.e("exce", "data  " + data.getClassified());
        }
        adapter.notifyDataSetChanged();
    }


    public void initView(View view) {
        mGrid.setAdapter(adapter);
        mGrid.setOnItemClickListener(this);
        mGrid.setTrashView(mImgTrash);
        mGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mGrid.setOnDeleteListener(new HTQDragGridView.OnDeleteListener() {
            @Override
            public void onDelete(final int position) {
                datas.get(position).deleteNoteInServe(getActivity(), new OnResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        if (!response.isSucces()) {
                            Toast.makeText(getActivity(), response.getMsg(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                delete(position);
            }
        });
        mGrid.setOnMoveListener(new HTQDragGridView.OnMoveListener() {
            @Override
            public void startMove() {
                mSwipeRefreshLayout.setEnabled(false);
                mImgTrash.startAnimation(HTQAnimations.getTranslateAnimation(0,
                        0, mImgTrash.getTop(), 0, 500));
                mImgTrash.setVisibility(View.VISIBLE);
            }

            @Override
            public void finishMove() {
                //   setListCanPull();
                mImgTrash.setVisibility(View.INVISIBLE);
                mImgTrash.startAnimation(HTQAnimations.getTranslateAnimation(0,
                        0, 0, mImgTrash.getTop(), 500));
                if (adapter.getDataChange()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            noteDb.reset(adapter.getDatas());
                        }
                    }).start();
                }
            }

            @Override
            public void cancleMove() {
            }
        });


        mSwipeRefreshLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    mState = STATE_PRESSNONE;
                    mGrid.setDragEnable(false);
                    // mHandler.sendMessageDelayed(Message.obtain(), 400);
                } else {
                    mGrid.setDragEnable(true);
                }
                return false;
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
//        mEmptyLayout.setErrorType(EmptyLayout.NODATA);
//        if (!datas.isEmpty()) {
//            mEmptyLayout.setVisibility(View.GONE);
//        }
    }

    /*********************************
     * GridView method
     ******************************/

    @Override
    public void onRefresh() {//同步数据,先从服务器端获取数据，然后重置到数据库中
        if (mState == STATE_REFRESH) {
            return;
        }
        // 设置顶部正在刷新
        mGrid.setSelection(0);
        setSwipeRefreshLoadingState();
//        getServerData();
        datas = noteDb.query(aty.level);
        adapter.refurbishData(datas);
        noteDb.reset(datas);//将结果重置到数据库
//        for(int i=0;i<datas.size();i++)
//        {
//
//            noteDb.save(datas.get(i));
//        }

        //refurbish();
        setSwipeRefreshLoadedState();
        Snackbar.make(mGrid, "已从服务器端同步数据！", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /**
     * 设置顶部正在加载的状态
     */
    private void setSwipeRefreshLoadingState() {
        mState = STATE_REFRESH;
        mGrid.setDragEnable(false);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            // mSwipeRefreshLayout.setEnabled(false);
        }
    }

    /**
     * 设置顶部加载完毕的状态
     */
    private void setSwipeRefreshLoadedState() {
        mState = STATE_NOMORE;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }
        mGrid.setDragEnable(true);
    }


    /********************************* synchronize method ******************************/

    /**
     * 使用自带缓存功能的网络请求，防止多次刷新造成的流量损耗以及服务器压力
     */
    private void refurbish() {
        datas = noteDb.query(aty.level);
        if (datas != null) {
            if (adapter != null) {
                adapter.refurbishData(datas);
            } else {
                adapter = new NotebookAdapter(aty, datas);
                mGrid.setAdapter(adapter);
            }
        }

    }

    /**
     * 删除数据
     *
     * @param index
     */
    private void delete(int index) {
        final int noteId = datas.get(index).getId();

        noteDb.delete(noteId);
        datas.remove(index);
        if (datas != null && adapter != null) {
            adapter.refurbishData(datas);

            mGrid.setAdapter(adapter);
        }

        //  updateEmptyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            new SystemUtils(getActivity()).setBoolean("isTran", true);
            mGrid.setAlpha(0.55f);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}