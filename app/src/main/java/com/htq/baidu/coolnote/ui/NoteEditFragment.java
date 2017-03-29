package com.htq.baidu.coolnote.ui;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.db.NoteDatabase;
import com.htq.baidu.coolnote.entity.NotebookData;
import com.htq.baidu.coolnote.entity.OnResponseListener;
import com.htq.baidu.coolnote.entity.Response;
import com.htq.baidu.coolnote.utils.AccountUtils;
import com.htq.baidu.coolnote.utils.Constants;
import com.htq.baidu.coolnote.utils.DialogHelp;
import com.htq.baidu.coolnote.utils.HTQAnimations;

import com.htq.baidu.coolnote.utils.ResourceParser;
import com.htq.baidu.coolnote.utils.SPUtils;
import com.htq.baidu.coolnote.utils.StringUtils;
import com.htq.baidu.coolnote.utils.SystemUtils;
import com.htq.baidu.coolnote.widget.TextViewFixTouchConsume;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by htq on 2016/8/8.
 */
public class NoteEditFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.note_detail_edit)
    EditText mEtContent;

    @BindView(R.id.note_detail_tv)
    TextView mTvContent;

    @BindView(R.id.note_detail_tv_date)
    TextView mTvDate;
    @BindView(R.id.note_detail_titlebar)
    RelativeLayout mLayoutTitle;
    @BindView(R.id.note_detail_img_thumbtack)
    ImageView mImgThumbtack;

    @BindView(R.id.note_detail_img_button)
    ImageView mImgMenu;
    @BindView(R.id.note_detail_menu)
    RelativeLayout mLayoutMenu;

    @BindView(R.id.note_detail_img_green)
    ImageView mImgGreen;
    @BindView(R.id.note_detail_img_blue)
    ImageView mImgBlue;
    @BindView(R.id.note_detail_img_purple)
    ImageView mImgPurple;
    @BindView(R.id.note_detail_img_yellow)
    ImageView mImgYellow;
    @BindView(R.id.note_detail_img_red)
    ImageView mImgRed;

    @BindView(R.id.menu_item_text_font)
    FloatingActionButton menuTextFont;
    @BindView(R.id.menu_item_clock)
    FloatingActionButton menuClock;
    @BindView(R.id.menu_item_desktop)
    FloatingActionButton menuDesktop;
    @BindView(R.id.menu_item_share)
    FloatingActionButton menuShare;

    @BindView(R.id.font_size_selector)
    View mFontSizeSelector;
    private NotebookData editData;
    private NoteDatabase noteDb;
    protected boolean isNewNote;
    private int whereFrom = QUICK_DIALOG;// 从哪个界面来

    public static final String NOTE_KEY = "notebook_key";
    public static final String NOTE_FROMWHERE_KEY = "fromwhere_key";
    public static final int QUICK_DIALOG = 0;
    public static final int NOTEBOOK_ITEM = 1;
    public static final String DESKTOP = "desktop";


    private int mFontSizeId;
    private static final Map<Integer, Integer> sFontSelectorSelectionMap = new HashMap<Integer, Integer>();

    static {
        sFontSelectorSelectionMap.put(Constants.TEXT_LARGE, R.id.iv_large_select);
        sFontSelectorSelectionMap.put(Constants.TEXT_SMALL, R.id.iv_small_select);
        sFontSelectorSelectionMap.put(Constants.TEXT_MEDIUM, R.id.iv_medium_select);
        sFontSelectorSelectionMap.put(Constants.TEXT_SUPER, R.id.iv_super_select);
    }

    private static final Map<Integer, Integer> sFontSizeBtnsMap = new HashMap<Integer, Integer>();

    static {
        sFontSizeBtnsMap.put(R.id.ll_font_large, Constants.TEXT_LARGE);
        sFontSizeBtnsMap.put(R.id.ll_font_small, Constants.TEXT_SMALL);
        sFontSizeBtnsMap.put(R.id.ll_font_normal, Constants.TEXT_MEDIUM);
        sFontSizeBtnsMap.put(R.id.ll_font_super, Constants.TEXT_SUPER);
    }

    public static final int[] sBackGrounds = {0xffe5fce8,// 绿色
            0xfffffdd7,// 黄色
            0xffffddde,// 红色
            0xffccf2fd,// 蓝色
            0xfff7f5f6,// 紫色
    };
    public static final int[] sTitleBackGrounds = {0xffcef3d4,// 绿色
            0xffebe5a9,// 黄色
            0xffecc4c3,// 红色
            0xffa9d5e2,// 蓝色
            0xffddd7d9,// 紫色
    };

    public static final int[] sThumbtackImgs = {R.drawable.green,
            R.drawable.yellow, R.drawable.red, R.drawable.blue,
            R.drawable.purple};
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.note_edit_fraglayout,
                container, false);
        ButterKnife.bind(this, rootView);
        initData();
        initView(rootView);

        return rootView;
    }


    private void initView(View view) {

        mImgGreen.setOnClickListener(this);
        mImgBlue.setOnClickListener(this);
        mImgPurple.setOnClickListener(this);
        mImgYellow.setOnClickListener(this);
        mImgRed.setOnClickListener(this);


        mEtContent.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mEtContent.setSingleLine(false);
        mEtContent.setHorizontallyScrolling(false);

        mEtContent.setBackgroundColor(sBackGrounds[editData.getColor()]);

        mTvContent.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mTvContent.setSingleLine(false);
        mTvContent.setHorizontallyScrolling(false);

        mTvContent.setFocusable(false);
        mTvContent.setClickable(false);
        mTvContent.setLongClickable(false);

        mTvContent.setBackgroundColor(sBackGrounds[editData.getColor()]);

        mTvContent.setMovementMethod(
                TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance()
        );


        if (TextUtils.isEmpty(editData.getContent())) {
            mEtContent.setText(Html.fromHtml(editData.getContent()).toString());
            mTvContent.setVisibility(View.GONE);
        } else {
            mTvContent.setText(Html.fromHtml(editData.getContent()).toString());
        }
        mTvDate.setText(editData.getDate());


        mLayoutTitle.setBackgroundColor(sTitleBackGrounds[editData.getColor()]);
        mImgThumbtack.setImageResource(sThumbtackImgs[editData.getColor()]);

        mImgMenu.setOnTouchListener(this);
        mLayoutMenu.setOnTouchListener(this);


//        mImgThumbtack.setImageResource(sThumbtackImgs[editData.getColor()]);
//        mEtContent.setBackgroundColor(sBackGrounds[editData.getColor()]);
//        mLayoutTitle.setBackgroundColor(sTitleBackGrounds[editData.getColor()]);
//        closeMenu();
    }


    public void initData() {
        noteDb = new NoteDatabase(getActivity());
        if (editData == null) {
            editData = new NotebookData();
            editData.setContent(new SystemUtils(getActivity()).getNoteDraft());
            isNewNote = true;
        }
        if (StringUtils.isEmpty(editData.getDate())) {
            editData.setDate(StringUtils.getDataTime("yyyy/MM/dd"));
        }

        mFontSizeId = (int) SPUtils.get(getActivity(), Constants.TEXT_SIZE, Constants.TEXT_MEDIUM);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.note_detail_img_green:
                editData.setColor(0);
                break;
            case R.id.note_detail_img_blue:
                editData.setColor(3);
                break;
            case R.id.note_detail_img_purple:
                editData.setColor(4);
                break;
            case R.id.note_detail_img_yellow:
                editData.setColor(1);
                break;
            case R.id.note_detail_img_red:
                editData.setColor(2);
                break;

        }
        mImgThumbtack.setImageResource(sThumbtackImgs[editData.getColor()]);
        mEtContent.setBackgroundColor(sBackGrounds[editData.getColor()]);
        mLayoutTitle.setBackgroundColor(sTitleBackGrounds[editData.getColor()]);
        closeMenu();
    }

    @OnClick({R.id.menu_item_share, R.id.menu_item_text_font, R.id.menu_item_clock, R.id.menu_item_desktop, R.id.menu_item_text_edit})
    public void click(FloatingActionButton menuItem) {
        String editContent = mEtContent.getText().toString();
        switch (menuItem.getId()) {
            case R.id.menu_item_share: {

                if (editContent.equals("")) {
                    Toast.makeText(getActivity(), "您还未输入内容哦", Toast.LENGTH_LONG).show();
                } else {
                    SystemUtils.shareNote(getActivity(), editContent);
                }
            }
            break;
            case R.id.menu_item_text_font: {
                showTextSelectorPanel();

            }
            break;
            case R.id.menu_item_clock: {
                if (!editContent.equals("")) {
                    setReminder(editContent);
                } else {
                    Toast.makeText(getActivity(), "亲,内容为空哦", Toast.LENGTH_LONG).show();
                }

            }
            break;

            case R.id.menu_item_desktop: {

                if (!editContent.equals("")) {
                    addToDesktop(editContent);
                } else {
                    Toast.makeText(getActivity(), "内容为空，添加失败", Toast.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.menu_item_text_edit: {
//编辑内容
                mTvContent.setVisibility(View.GONE);
                mEtContent.setText(mTvContent.getText());
            }
            break;


        }


    }


    @OnClick({R.id.ll_font_small, R.id.ll_font_normal, R.id.ll_font_large, R.id.ll_font_super})
    public void ClickTextSizeSelector(View v) {
        int id = v.getId();
        getActivity().findViewById(sFontSelectorSelectionMap.get(mFontSizeId)).setVisibility(View.GONE);
        mFontSizeId = sFontSizeBtnsMap.get(id);
        SPUtils.put(getActivity(), Constants.TEXT_SIZE, mFontSizeId);
        getActivity().findViewById(sFontSelectorSelectionMap.get(mFontSizeId)).setVisibility(View.VISIBLE);
        mEtContent.setTextAppearance(getActivity(),
                ResourceParser.TextAppearanceResources.getTexAppearanceResource(mFontSizeId));
        getActivity().findViewById(R.id.font_size_selector).setVisibility(View.GONE);

    }

    private void showTextSelectorPanel() {
        mFontSizeSelector.setVisibility(View.VISIBLE);
        getActivity().findViewById(sFontSelectorSelectionMap.get(mFontSizeId)).setVisibility(View.VISIBLE);
    }

    private void addToDesktop(String title) {
        Intent intent = new Intent();
        Intent shortcutIntent = new Intent(getActivity(), NoteEditActivity.class);
        // SPUtils.put(getActivity(),DESKTOP,title);//将内容保存至share中
        shortcutIntent.putExtra(DESKTOP, title);
        shortcutIntent.setAction(Intent.ACTION_VIEW);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getActivity(), R.mipmap.app_icon));
        intent.putExtra("duplicate", true);
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        Toast.makeText(getActivity(), "已添加至桌面", Toast.LENGTH_LONG).show();
        // showToast(R.string.info_note_enter_desktop);
        getActivity().sendBroadcast(intent);
    }

    private void setReminder(final String str) {
        DateTimePickerDialog d = new DateTimePickerDialog(getActivity(), System.currentTimeMillis());
        d.setOnDateTimeSetListener(new DateTimePickerDialog.OnDateTimeSetListener() {
            public void OnDateTimeSet(AlertDialog dialog, long date) {
                setClock(date, true, str);
                // mEditNote.setAlertDate(date, true);
            }
        });
        d.show();
    }

    private void setClock(Long date, boolean set, String str) {

        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        intent.putExtra(Constants.ALART_CONTENT, str);
        //  intent.setData(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, mWorkingNote.getNoteId()));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        AlarmManager alarmManager = ((AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE));
        if (!set) {
            alarmManager.cancel(pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            if (mLayoutMenu.getVisibility() == View.GONE) {
                openMenu();
            } else {
                closeMenu();
            }
        }
        return true;
    }

    /**
     * 切换便签颜色的菜单
     */
    private void openMenu() {
        HTQAnimations.openAnimation(mLayoutMenu, mImgMenu, 700);
    }

    /**
     * 切换便签颜色的菜单
     */
    private void closeMenu() {
        HTQAnimations.closeAnimation(mLayoutMenu, mImgMenu, 500);
    }

    /**
     * 保存已编辑内容到数据库
     */
    private void save() {
        setNoteProperty();
        noteDb.save(editData);
    }

    private void setNoteProperty() {
        if (editData.getId() == 0) {
            editData.setId(-1
                    * StringUtils.toInt(
                    StringUtils.getDataTime("dddHHmmss"), 0));
        }
        String userId = AccountUtils.getUserId(getActivity());
        editData.setUserId(userId);

        editData.setUnixTime(StringUtils.getDataTime("yyyy-MM-dd HH:mm:ss"));
        editData.setContent(mEtContent.getText().toString());
        editData.setLevel(editData.getLevel());
        editData.setFather(MainActivity.FATHER);
        editData.setObjectId(editData.getObjectId());
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = getActivity().getIntent().getBundleExtra(Constants.BUNDLE_KEY_ARGS);
        if (bundle != null) {
            editData = (NotebookData) bundle.getSerializable(NOTE_KEY);
            whereFrom = bundle.getInt(NOTE_FROMWHERE_KEY, QUICK_DIALOG);
        }
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onResume() {
        String desktopStr = getActivity().getIntent().getStringExtra(DESKTOP);

        if (desktopStr != null)//表明用户从桌面快捷方式点击过来的
        {
            mEtContent.setText(desktopStr);
        }
        super.onResume();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notebook_edit_menu, menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.public_menu_send:


                if (!StringUtils.isEmpty(mEtContent.getText().toString())) {
                    if (mTvContent.getVisibility() == View.VISIBLE) {
                        getActivity().finish();
                    }
                    save();
                    mTvContent.setVisibility(View.VISIBLE);
                    mTvContent.setText(mEtContent.getText().toString());
                } else {
                    Toast.makeText(getActivity(), "亲,内容为空哦", Toast.LENGTH_LONG).show();
                }


                break;
        }
        return true;
    }


    public boolean onBackPressed() {
        final String content = mEtContent.getText().toString();
        if (mTvContent.getVisibility() == View.GONE) {
            if (!TextUtils.isEmpty(content)) {
                DialogHelp.getConfirmDialog(getActivity(), "是否保存?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //
                        save();
                        mTvContent.setVisibility(View.VISIBLE);
                        mTvContent.setText(content);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new SystemUtils(getActivity()).setNoteDraft("");
                        mTvContent.setVisibility(View.VISIBLE);
                    }
                }).show();
                return true;
            }
        }
        return false;
    }


    /**
     * notice :inner Broadcast receiver must be static ( to be registered through Manifest)
     * or Non-static broadcast receiver must be registered and unregistered inside the Parent class
     */
    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            intent.setClass(context, AlarmAlertActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Log.i("alarm content",intent.getStringExtra(Constants.ALART_CONTENT));
            //intent.putExtra(Constants.ALART_CONTENT,intent.getStringExtra(Constants.ALART_CONTENT));
            context.startActivity(intent);
        }
    }
}
