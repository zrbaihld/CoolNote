package com.htq.baidu.coolnote.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.CalendarContract;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;


import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.entity.NotebookData;
import com.htq.baidu.coolnote.ui.NoteEditFragment;
import com.htq.baidu.coolnote.utils.SystemUtils;
import com.htq.baidu.coolnote.widget.HTQDragGridView;
import com.htq.baidu.coolnote.widget.TextViewFixTouchConsume;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NotebookAdapter extends BaseAdapter implements HTQDragGridView.DragGridBaseAdapter {
    private List<NotebookData> datas;
    private final Activity aty;
    private int currentHidePosition = -1;
    private final int width;
    private final int height;
    private boolean dataChange = false;

    public NotebookAdapter(Activity aty, List<NotebookData> datas) {
        super();
        Collections.sort(datas);
        this.datas = datas;
        this.aty = aty;
        width = new SystemUtils(aty).getScreenW(aty) / 2;
        height = (int) aty.getResources().getDimension(R.dimen.space_35);
    }

    public void refurbishData(List<NotebookData> datas) {
        if (datas == null) {
            datas = new ArrayList<NotebookData>(1);
        }
        Collections.sort(datas);
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<NotebookData> getDatas() {
        return datas;
    }

    /**
     * 数据是否发生了改变
     *
     * @return
     */
    public boolean getDataChange() {
        return dataChange;
    }

    static class ViewHolder {
        TextView date;
        TextView note_class;
        TextView tv_title;
        ImageView state;
        ImageView img_back;
        ImageView thumbtack;
        View titleBar;
        TextViewFixTouchConsume content;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        datas.get(position).setIid(position);
        NotebookData data = datas.get(position);

        ViewHolder holder = null;
        if (v == null) {
            holder = new ViewHolder();
            v = View.inflate(aty, R.layout.item_notebook, null);
            holder.titleBar = v.findViewById(R.id.item_note_titlebar);
            holder.date = (TextView) v.findViewById(R.id.item_note_tv_date);
            holder.note_class = (TextView) v.findViewById(R.id.item_note_class);
            holder.tv_title = (TextView) v.findViewById(R.id.item_note_tv_title);
            holder.state = (ImageView) v.findViewById(R.id.item_note_img_state);
            holder.img_back = (ImageView) v.findViewById(R.id.item_note_img_back);
            holder.thumbtack = (ImageView) v
                    .findViewById(R.id.item_note_img_thumbtack);
            holder.content = (TextViewFixTouchConsume) v.findViewById(R.id.item_note_content);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        LayoutParams params = (LayoutParams) holder.content
                .getLayoutParams();
        params.width = width;
        params.height = (params.width - height);
        holder.content.setLayoutParams(params);
        holder.note_class.setLayoutParams(params);
        holder.img_back.setLayoutParams(params);


        holder.date.setText(data.getDate());
        holder.state.setVisibility(View.GONE);
//        if (data.getId() > 0) {
//            holder.state.setVisibility(View.GONE);
//        } else {
//            holder.state.setVisibility(View.VISIBLE);
//        }
        holder.content.setBackgroundColor(NoteEditFragment.sBackGrounds[data
                .getColor()]);
        holder.thumbtack.setImageResource(NoteEditFragment.sThumbtackImgs[data
                .getColor()]);
        holder.titleBar
                .setBackgroundColor(NoteEditFragment.sTitleBackGrounds[data
                        .getColor()]);

        if (!TextUtils.isEmpty(data.getTitle())) {
            holder.tv_title.setText(data.getTitle());
            holder.tv_title.setVisibility(View.VISIBLE);
        }else {
            holder.tv_title.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(data.getContent())) {
            holder.content.setText(data.getContent());
            holder.note_class.setVisibility(View.GONE);
            holder.img_back.setVisibility(View.GONE);
            holder.content.setVisibility(View.VISIBLE);
            holder.thumbtack.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(data.getClassified())) {
            holder.content.setVisibility(View.GONE);
            holder.note_class.setVisibility(View.VISIBLE);
            holder.img_back.setVisibility(View.GONE);
            holder.thumbtack.setVisibility(View.GONE);

            holder.note_class.setText(data.getClassified());
            holder.titleBar
                    .setBackgroundColor(0xffFFA726);
            holder.note_class.setBackgroundColor(0xffFFB74D);
        } else if (!TextUtils.isEmpty(data.getImgpath())) {
            holder.note_class.setVisibility(View.GONE);
            holder.img_back.setVisibility(View.VISIBLE);
            holder.content.setVisibility(View.GONE);
            holder.thumbtack.setVisibility(View.VISIBLE);

            holder.img_back.setImageBitmap(getLoacalBitmap(data.getImgpath()));
        }

        if (position == currentHidePosition) {
            v.setVisibility(View.GONE);
        } else {
            v.setVisibility(View.VISIBLE);
        }
        return v;
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition) {
        dataChange = true;
        if (oldPosition >= datas.size() || oldPosition < 0) {
            return;
        }
        NotebookData temp = datas.get(oldPosition);
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(datas, i, i + 1);
            }
        } else if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(datas, i, i - 1);
            }
        }
        datas.set(newPosition, temp);
    }

    @Override
    public void setHideItem(int hidePosition) {
        this.currentHidePosition = hidePosition;
        notifyDataSetChanged();
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
