package com.wwx.chargeup.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.framelibrary.util.DateUtils;
import com.framelibrary.util.DeviceUtils;
import com.hjq.base.BaseDialog;
import com.hjq.base.util.StringUtils;
import com.hjq.base.util.ViewUtils;
import com.hjq.toast.ToastUtils;
import com.hjq.widget.view.RegexEditText;
import com.umeng.commonsdk.debug.UMLogUtils;
import com.wwx.chargeup.R;
import com.wwx.chargeup.aop.Log;
import com.wwx.chargeup.aop.SingleClick;
import com.wwx.chargeup.bean.ChargeUpBean;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;

/**
 * <pre>
 *      author:         wangweixu
 *      date:           2022-07-14 21:52:50
 *      description:    记账对话框
 *      version:        v1.0
 * </pre>
 */
public final class ChargeUpDialog {

    public static final class Builder
            extends CommonDialog.Builder<Builder> {

        private final RegexEditText cetAccountsType, cetAccountsMoney;
        private final TextView tvAccountsDate, tvAccountsTime;
        private final RadioGroup rdgAccountsState;

        @Nullable
        private OnListener mListener;

        /** 时间日期输入框内容 */
        private String accountsCreateDate, accountsCreateTime,
        /** + 收入 - 支出 " " 禁用 */
                        accountsState = "-";

                        /** 是否是编辑状态 false 为新增 */
        private boolean isEdit;
        private int editPosition;

        public Builder(Context context) {
            super(context);
            setTitle(R.string.dialog_accounts_title);

            ViewGroup.LayoutParams layoutParams = getCdvDialogRoot().getLayoutParams();
            layoutParams.width =
                     DeviceUtils.deviceWidth(context) - DeviceUtils.dp2px(context, 40);
            // 不调用则设置会很奇怪不是想要的效果
            setWidth(layoutParams.width);

            setCustomView(R.layout.edit_charge_up_dialog);
            cetAccountsType = findViewById(R.id.cet_accounts_type);
            rdgAccountsState = findViewById(R.id.rdg_accounts_state);
            cetAccountsMoney = findViewById(R.id.cet_accounts_money);
            tvAccountsDate = findViewById(R.id.cet_accounts_date);
            tvAccountsTime = findViewById(R.id.cet_accounts_time);

            setOnClickListener(R.id.cet_accounts_date, R.id.cet_accounts_time);

            rdgAccountsState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.rdg_accounts_state_on:
                            // 收入
                            accountsState = "+";
                            break;
                        case R.id.rdg_accounts_state_off:
                            // 支出
                            accountsState = "-";
                            break;
                        case R.id.rdg_accounts_state_un:
                            // 禁用
                            accountsState = " ";
                            break;
                    }
                }
            });
        }

        /** 是否是编辑状态 false 为新增 */
        public Builder setEdit(boolean isEdit, int position) {
            this.isEdit = isEdit;
            this.editPosition = position;
            return this;
        }

        public Builder setBean(ChargeUpBean chargeUpBean) {
            if (chargeUpBean == null)
                return this;

            editPosition = isEdit ? editPosition : 0;

            cetAccountsType.setText(StringUtils.emptyNullValue(chargeUpBean.getAccountsType()));
            cetAccountsMoney.setText(StringUtils.emptyNullValue(chargeUpBean.getAccountsMoney()));

            switch (StringUtils.emptyNullValue(chargeUpBean.getAccountsState())) {
                case "-":
                    rdgAccountsState.check(R.id.rdg_accounts_state_off);
                break;
                case "+":
                    rdgAccountsState.check(R.id.rdg_accounts_state_on);
                break;
                case "":
                default:
                    rdgAccountsState.check(R.id.rdg_accounts_state_un);
                break;
            }

            try {
                tvAccountsDate.setText(DateUtils.formatDatePattern(Long.parseLong(chargeUpBean.getCreateTime())));
            } catch (NumberFormatException e) {
                tvAccountsDate.setText(DateUtils.formatDatePattern(System.currentTimeMillis()));
                Timber.e(e);
            }
            try {
                tvAccountsTime.setText(DateUtils.formatDateByPattern(Long.parseLong(chargeUpBean.getCreateTime()), "HH:mm:ss"));
            } catch (NumberFormatException e) {
                tvAccountsTime.setText(DateUtils.formatDateByPattern(System.currentTimeMillis(), "HH:mm:ss"));
                Timber.e(e);
            }

            return this;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        @SingleClick
        @Override
        public void onClick(View view) {
            int viewId = view.getId();

            switch (viewId) {
                case R.id.tv_ui_confirm:

                    String accountsType = ViewUtils.getStringText(cetAccountsType);
                    String accountsMoney = ViewUtils.getStringText(cetAccountsMoney);
                    accountsCreateDate = ViewUtils.getStringText(tvAccountsDate);
                    accountsCreateTime = ViewUtils.getStringText(tvAccountsTime);
                    String accountsCreateDateTime = null;

                    if (TextUtils.isEmpty(accountsType)) {
                        ToastUtils.show(R.string.dialog_accounts_type_error);
                        return;
                    }

                    if (TextUtils.isEmpty(accountsMoney)) {
                        ToastUtils.show(R.string.dialog_accounts_money_error);
                        return;
                    }

                    Calendar calendar = Calendar.getInstance();

                    if (TextUtils.isEmpty(accountsCreateDate)) {
                        accountsCreateDateTime = String.valueOf(calendar.getTimeInMillis());
                    } else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                        String timeStr = MessageFormat.format(
                                "{0} {1}",
                                accountsCreateDate,
                                accountsCreateTime);

                        Date time = null;
                        try {
                            time = dateFormat.parse(timeStr);
                        } catch (ParseException e) {
                            time = calendar.getTime();
                            Timber.e(e);
                        }

                        accountsCreateDateTime = String.valueOf(time.getTime());
                    }

                    Timber.i("accountsState: %s", accountsState);
                    ChargeUpBean chargeUpBean = ChargeUpBean.builder()
                            .accountsType(accountsType)
                            .accountsState(accountsState)
                            .accountsMoney(accountsMoney)
                            .createTime(accountsCreateDateTime)
                            .updateTime(isEdit ? String.valueOf(System.currentTimeMillis()) : "")
                            .build();

                    if (chargeUpBean != null) {
                        autoDismiss();
                        if (mListener == null) {
                            return;
                        }
                        mListener.onConfirm(getDialog(), chargeUpBean, isEdit, editPosition);
                        return;
                    }

                case R.id.tv_ui_cancel:
                    autoDismiss();
                    if (mListener == null) {
                        return;
                    }
                    mListener.onCancel(getDialog());
                    break;
                case R.id.cet_accounts_date:
                    // 日期选择对话框
                    new DateDialog.Builder(getContext())
                            .setTitle(getString(R.string.date_title))
                            // 确定按钮文本
                            .setConfirm(getString(R.string.common_confirm))
                            // 设置 null 表示不显示取消按钮
                            .setCancel(getString(R.string.common_cancel))
                            .setListener(new DateDialog.OnListener() {
                                @Override
                                public void onSelected(BaseDialog dialog, int year, int month, int day) {
                                    // toast(year + getString(R.string.common_year) + month + getString(R.string.common_month) + day + getString(R.string.common_day));

                                    // 如果不指定时分秒则默认为现在的时间
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.YEAR, year);
                                    // 月份从零开始，所以需要减 1
                                    calendar.set(Calendar.MONTH, month - 1);
                                    calendar.set(Calendar.DAY_OF_MONTH, day);
                                    // toast("时间戳：" + calendar.getTimeInMillis());
                                    tvAccountsDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
                                    if (TextUtils.isEmpty(accountsCreateTime)) {
                                        tvAccountsTime.setText(new SimpleDateFormat("kk:mm:ss").format(calendar.getTime()));

                                    }
                                }

                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    // toast("取消了");
                                }
                            })
                            .show();

                    break;
                case R.id.cet_accounts_time:

                    // 时间选择对话框
                    new TimeDialog.Builder(getContext())
                            .setTitle(getString(R.string.time_title))
                            // 确定按钮文本
                            .setConfirm(getString(R.string.common_confirm))
                            // 设置 null 表示不显示取消按钮
                            .setCancel(getString(R.string.common_cancel))
                            .setListener(new TimeDialog.OnListener() {

                                @Override
                                public void onSelected(BaseDialog dialog, int hour, int minute, int second) {
                                    // toast(hour + getString(R.string.common_hour) + minute + getString(R.string.common_minute) + second + getString(R.string.common_second));

                                    // 如果不指定年月日则默认为今天的日期
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                                    calendar.set(Calendar.MINUTE, minute);
                                    calendar.set(Calendar.SECOND, second);
                                    // toast("时间戳：" + calendar.getTimeInMillis());
                                    tvAccountsTime.setText(new SimpleDateFormat("kk:mm:ss").format(calendar.getTime()));

                                    if (TextUtils.isEmpty(accountsCreateDate)) {
                                        tvAccountsDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));

                                    }
                                    //toast(new SimpleDateFormat("yyyy年MM月dd日 kk:mm:ss").format(calendar.getTime()));
                                }

                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    // toast("取消了");
                                }
                            })
                            .show();

                    break;
            }
        }
    }

    public interface OnListener {

        /**
         * 点击确定时回调
         * @param isEdit true 编辑状态 false 新增
         * @param editPosition 编辑位下标
         */
        void onConfirm(BaseDialog dialog, ChargeUpBean chargeUpBean, boolean isEdit, int editPosition);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {
        }
    }
}