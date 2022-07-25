package com.wwx.chargeup.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.base.action.HandlerAction;
import com.hjq.base.util.CollectionUtils;
import com.wwx.chargeup.R;
import com.wwx.chargeup.action.StatusAction;
import com.wwx.chargeup.aop.Log;
import com.wwx.chargeup.app.AppAdapter;
import com.wwx.chargeup.bean.ChargeUpBean;
import com.wwx.chargeup.widget.StatusLayout;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * <pre>
 *      author:         wangweixu
 *      date:           2022-07-14 17:57:45
 *      description:    记账数据列表
 *      version:        v1.0
 * </pre>
 */
public final class ChargeUpAdapter extends AppAdapter<ChargeUpBean>
        implements StatusAction, HandlerAction {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

    private StatusLayout mStatusLayout;

    public ChargeUpAdapter(Context context) {
        super(context);

        postDelayed(() -> {
            showStatusView();
        }, 150);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    @Override
    public void addItem(@NonNull @NotNull ChargeUpBean item) {
        super.addItem(item);

        showStatusView();
    }

    @Override
    public void addItem(int position, @NonNull @NotNull ChargeUpBean item) {
        super.addItem(position, item);

        showStatusView();
    }

    @Override
    public void addData(List<ChargeUpBean> data) {
        super.addData(data);

        showStatusView();
    }

    @Override
    public void setData(@Nullable List<ChargeUpBean> data) {
        super.setData(data);

        showStatusView();
    }

    @Override
    public void removeItem(int position) {
        super.removeItem(position);

        showStatusView();
    }

    @Override
    public void removeItem(@NonNull @NotNull ChargeUpBean item) {
        super.removeItem(item);

        showStatusView();
    }

    /** 显示状态页面 */
    @Log
    private void showStatusView() {
        if (!CollectionUtils.isEmpty(getData()))
            showComplete();
        else
            showEmpty();
    }


    /**
     * 获取状态布局
     */
    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    public void setStatusLayout(StatusLayout mStatusLayout) {
        this.mStatusLayout = mStatusLayout;
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView tvAccountsType, tvAccountsTime, tvAccountsMoney;
        private final View viewBottomLine;

        private ViewHolder() {
            super(R.layout.charge_up_item);
            tvAccountsType = findViewById(R.id.tv_accounts_type);
            tvAccountsTime = findViewById(R.id.tv_accounts_time);
            tvAccountsMoney = findViewById(R.id.tv_accounts_money);
            viewBottomLine = findViewById(R.id.view_bottom_line);
        }

        @Override
        public void onBindView(int position) {
            // 如果是最后一条, 底部分割线 隐藏
            viewBottomLine.setVisibility(position == (getItemCount() - 1) ? View.GONE : View.VISIBLE);

            ChargeUpBean item = getItem(position);
            tvAccountsType.setText(item.getAccountsType());
            try {
                tvAccountsTime.setText(dateFormat.format(new Date(Long.parseLong(item.getCreateTime()))));
            } catch (NumberFormatException e) {
                tvAccountsTime.setText(dateFormat.format(new Date(System.currentTimeMillis())));
                Timber.e(e);
                // e.printStackTrace();
            }

            switch (item.getAccountsState()) {
                case "+":
                    tvAccountsMoney.setTextColor(getResources().getColor(R.color.black));
                    break;
                case "-":
                    tvAccountsMoney.setTextColor(getResources().getColor(R.color.red));
                    break;
                case " ":
                    tvAccountsMoney.setTextColor(getResources().getColor(R.color.black30));

                    break;
            }
            tvAccountsMoney.setText(MessageFormat.format(
                    "{0}{1}",
                    item.getAccountsState(),
                    item.getAccountsMoney()));

        }
    }
}