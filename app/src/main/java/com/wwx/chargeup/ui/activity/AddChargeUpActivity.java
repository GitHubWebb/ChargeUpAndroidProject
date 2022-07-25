package com.wwx.chargeup.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wwx.chargeup.R;
import com.wwx.chargeup.aop.Log;
import com.wwx.chargeup.aop.SingleClick;
import com.wwx.chargeup.app.AppActivity;
import com.wwx.chargeup.http.api.PasswordApi;
import com.wwx.chargeup.http.model.HttpData;
import com.wwx.chargeup.manager.InputTextManager;
import com.wwx.chargeup.ui.dialog.TipsDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;


/**
 * <pre>
 *      author:         wangweixu
 *      date:           2022-07-14 19:34:23
 *      description:    添加账目数据
 *      version:        v1.0
 * </pre>
 */
public final class AddChargeUpActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    @Log
    public static void start(Context context) {
        Intent intent = new Intent(context, AddChargeUpActivity.class);
        /*intent.putExtra(INTENT_KEY_IN_PHONE, phone);
        intent.putExtra(INTENT_KEY_IN_CODE, code);*/
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private Button mCommitView;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_add_charge_up;
    }

    @Override
    protected void initView() {
        mCommitView = findViewById(R.id.btn_password_reset_commit);

        setOnClickListener(mCommitView);

        /*InputTextManager.with(this)
                .addView(mFirstPassword)
                .addView(mSecondPassword)
                .setMain(mCommitView)
                .build();*/
    }

    @Override
    protected void initData() {

    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mCommitView) {

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

            if (true) {
                new TipsDialog.Builder(this)
                        .setIcon(TipsDialog.ICON_FINISH)
                        .setMessage(R.string.password_reset_success)
                        .setDuration(2000)
                        .addOnDismissListener(dialog -> finish())
                        .show();
                return;
            }
        }
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击提交按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }
}