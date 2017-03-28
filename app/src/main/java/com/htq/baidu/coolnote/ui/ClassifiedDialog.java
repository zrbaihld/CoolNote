package com.htq.baidu.coolnote.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2017/3/28.
 */

public class ClassifiedDialog extends AlertDialog implements View.OnClickListener {
    protected ClassifiedDialog(Context context) {
        super(context);
    }

    protected ClassifiedDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected ClassifiedDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void onClick(View v) {

    }
}
