package com.wwx.chargeup.ui.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.base.util.ClipBoardUtil;
import com.hjq.base.util.CollectionUtils;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.shape.layout.ShapeRecyclerView;
import com.hjq.toast.ToastUtils;
import com.hjq.widget.layout.SettingBar;
import com.tencent.mmkv.MMKV;
import com.wwx.chargeup.R;
import com.wwx.chargeup.aop.Log;
import com.wwx.chargeup.aop.SingleClick;
import com.wwx.chargeup.app.TitleBarFragment;
import com.wwx.chargeup.bean.ChargeUpBean;
import com.wwx.chargeup.bean.ChargeUpConstants;
import com.wwx.chargeup.ui.activity.HomeActivity;
import com.hjq.widget.view.SwitchButton;
import com.wwx.chargeup.ui.adapter.ChargeUpAdapter;
import com.wwx.chargeup.ui.dialog.ChargeUpDialog;
import com.wwx.chargeup.ui.dialog.MessageDialog;
import com.wwx.chargeup.ui.dialog.WaitDialog;
import com.wwx.chargeup.widget.StatusLayout;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
  * <pre>
  *      author:         wangweixu
  *      date:           2022-07-14 17:21:14
  *      description:    记账 Fragment
  *      version:        v1.0
  * </pre>
 */
public final class ChargeUpFragment extends TitleBarFragment<HomeActivity>
        implements SwitchButton.OnCheckedChangeListener, BaseAdapter.OnItemClickListener,
        BaseAdapter.OnItemLongClickListener {
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(); //建立货币格式化引用(默认地区Local格式)

    private SettingBar sbAmount;
    private StatusLayout mStatusLayout;
    private RecyclerView spRvChargeUp;
    private ChargeUpAdapter mAdapter;
    private Gson gson;
    private MMKV mMmkv;
    private BaseDialog mWaitDialog;

    public static ChargeUpFragment newInstance() {
        return new ChargeUpFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.charge_up_fragment;
    }

    @Override
    protected void initView() {
        sbAmount = findViewById(R.id.sb_amount);
        mStatusLayout = findViewById(R.id.hl_charge_up_select_hint);
        spRvChargeUp = findViewById(R.id.sp_rv_charge_up);

        mMmkv = MMKV.mmkvWithID(ChargeUpConstants.MMKV_CHARGEUP_ID);

        mAdapter = new ChargeUpAdapter(getContext());
        mAdapter.setStatusLayout(mStatusLayout);

        ToastUtils.show("可通过将内容复制到剪切板中进行导入操作~");
        initListener();
        // setOnClickListener(mCountdownView);

        // mSwitchButton.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        spRvChargeUp.setAdapter(mAdapter);

        CopyOnWriteArrayList<ChargeUpBean> chargeUpBeanList = getCacheChargeUpBeans();
        mAdapter.setData(chargeUpBeanList);
        getTotalBalance();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoading())

            getActivity().getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    readClipBoardData();
                }
            }, 100);


    }

    /** 读取剪切板数据 */
    private void readClipBoardData() {

        //把获取到的内容打印出来
        String clipBoardPaste = ClipBoardUtil.paste(getActivity());
        Timber.i("ClipBoardUtil.paste: %s", clipBoardPaste);

        // TODO 假数据
        // clipBoardPaste = "123";
        if (TextUtils.isEmpty(clipBoardPaste))
            return;

        if (mWaitDialog == null)
            mWaitDialog = new WaitDialog.Builder(getContext())
                // 消息文本可以不用填写
                .setMessage(getString(R.string.common_loading))
                .create();

        if (!mWaitDialog.isShowing()) {
            mWaitDialog.show();
            // postDelayed(mWaitDialog::dismiss, 2000);
        }

        List<ChargeUpBean> chargeUpBeanList = getCacheChargeUpBeans();
        if (chargeUpBeanList == null)
            chargeUpBeanList = new ArrayList<>();

        String clipboardStrs = "515.42-117.45（燃气费）=397.97\n" +
                "397.97-19.85（多多买菜：小葱，蟹棒，火锅丸子，鸡心）=378.12\n" +
                "378.12-9.09 (京喜拼拼 : 火锅底料，香菜)=369.03\n" +
                "369.03-10（打火机）=359.03\n" +
                "359.03-15.66（两瓶六神驱蚊液）=343.37\n" +
                "343.37-19.7（三瓶蜜雪冰城）=323.67\n" +
                "323.67-18.3（麻辣鸡架）=305.37\n" +
                "305.37-93.19（都市恋人坊内衣）=212.19\n" +
                "212.19+53.9（裤子退款）=266.09\n" +
                "266.09-17.5猪肉-10.5拉皮，豆腐，干豆腐-13桃子-10玉米-6香蕉-5.3红薯=203.79\n" +
                "203.79-22.3（药:肠炎宁）=181.49\n" +
                "181.49-24(药:胃康灵)=157.49\n" +
                "157.49-30（咸鱼:小熊加热饭盒）=127.49\n" +
                "127.49-27.27（家乐福：鸡腿鸡胸蒜蓉辣酱）=100.22\n" +
                "100.22-9.06=91.16（麦片）=91.16\n" +
                "91.16-43.7（多多买菜：蒜苔，蜜瓜，大娃娃菜，胡萝卜，红肠，腊肠，大葱，丸子，番茄酱）=47.46\n" +
                "47.46-15西瓜-5.9杏-2.8油桃-9.8脆桃=13.96\n" +
                "13.96+15西瓜+5.9杏+2.8油桃+9.8香脆桃=47.46";

        ClipBoardUtil.clear(getActivity());
        // TODO 注释则使用假数据
         clipboardStrs = clipBoardPaste;
        // 既能将\r\n结尾的windows格式文本处理好，
        // 也能将\n结尾的linux格式处理好，
        // 还可以将以\r结尾的Mac的文本处理好。
        String clipboardStrLines[] = clipboardStrs.split("\\r?\\n");
        for (int i = 0; i < clipboardStrLines.length; i++) {
            // 匹配双字节字符, 包括汉字在内
            String REGEX_CHINESE = "[^\\x00-\\xff]*";
            Matcher chineseMatcher = Pattern.compile(REGEX_CHINESE).matcher(clipboardStrLines[i]);

            Timber.i("clipboardStr: %s", clipboardStrLines[i]);

            // 匹配出的 账目类型(备注)
            StringBuffer accountsType = new StringBuffer();
            StringBuffer accountsTypeRegex = new StringBuffer();
            while (chineseMatcher.find()) {
                String group = chineseMatcher.group();
                if (!TextUtils.isEmpty(group)) {
                    accountsType.append(group);
                    accountsTypeRegex.append(group);
                    accountsTypeRegex.append("|");
                }
            }

            ArrayList<String> priceList = null;
            try {
                // 将每一行的数据集 去掉备注内容 后 将金额摘取出来; {"+100", "-10"}
                List<String> converPriceList = Arrays.asList(clipboardStrLines[i].split(accountsTypeRegex.substring(0, accountsTypeRegex.length() - 1)));
                priceList = new ArrayList<String>(converPriceList);
            } catch (Exception e) {
                priceList = new ArrayList<>();
                Timber.e(e);
            }

            BigDecimal result = null;
            try {
                result = getCalculationAmount(priceList);
            } catch (Exception e) {
                ToastUtils.show("转换异常: " + "第"+ (i +1) +"条数据异常, " + clipboardStrLines[i] + "\n请修改元数据!\n" + e.getMessage());
                continue;
            }

            Timber.i("chineseMatcher.group(): %s , %s, %s ", accountsType , priceList, result);

            chargeUpBeanList.add(
                    0,
                    ChargeUpBean.builder()
                            .accountsType(accountsType.toString())
                            .accountsState("-")
                            .accountsMoney(result.toPlainString())
                            .createTime(String.valueOf(System.currentTimeMillis()))
                            .build()
            );
        }

        postDelayed(mWaitDialog::dismiss, 3000);
        mAdapter.setData(chargeUpBeanList);

        putCacheChargeUpBeans();
    }

    private BigDecimal getCalculationAmount(ArrayList<String> priceList) {
        if (CollectionUtils.isEmpty(priceList)) {
            return new BigDecimal("0");
        }

        // 避免使用remove后索引改变 不能删除临近的数据, 将索引归位 或者 倒序排列也可以
        for (int j = 0; j < priceList.size(); j++) {
            String priceStr = priceList.get(j);
            if (priceStr.indexOf("=") != -1) {
                priceList.remove(priceStr);
                j--;
            }

            if (priceStr.contains(":") || priceStr.contains(" : ")) {
                priceList.remove(priceStr);
                j--;
            }
        }

        BigDecimal result = BigDecimal.valueOf(0);
        Timber.i("priceList %s", priceList);
        for (String priceStr : priceList) {
            // 可以替换大部分空白字符，不限于空格；
            priceStr = priceStr.replaceAll("\\s*","");
            String[] kongSplit = priceStr.split(" ");

            if (!kongSplit[0].isEmpty())
                priceStr = kongSplit[0];

            // 计算符号截取
            String[] calculateSplit = priceStr.split("-|\\+");
            try {

                BigDecimal splitArrBigDecimal = new BigDecimal("0");

                // 说明读取到的数据异常 类似 "515.42-117.45" "515.42+117.45" 需要进行拆分
                for (int i = 0; i < calculateSplit.length; i++) {
                    if (calculateSplit[i].isEmpty()) {
                        continue;
                    }

                    // 如果数组中有多个值 将数组单独计算
                    if (calculateSplit.length > 1) {
                        if (i == 0)
                            splitArrBigDecimal = new BigDecimal(calculateSplit[i]);
                        else {
                            // 获取加上正负类型的价格
                            String lastPriceStr = null;
                            try {
                                lastPriceStr = priceStr.substring(priceStr.indexOf(calculateSplit[i]) - 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String calculate = lastPriceStr.contains("-") ? "-" : "+";
                            switch (calculate) {
                                case "-":
                                    splitArrBigDecimal = splitArrBigDecimal.subtract(new BigDecimal(calculateSplit[i]));
                                    break;
                                case "+":
                                    splitArrBigDecimal = splitArrBigDecimal.add(new BigDecimal(calculateSplit[i]));
                                    break;
                            }
                            Timber.i("priceList calculateSplit result %s %s", calculate + calculateSplit[i], result);

                        }

                    }

                    result = result.add(splitArrBigDecimal);

                }

            } catch (Exception e) {
                throw e;
            }
        }
        return result;
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        /*if (view == mCountdownView) {
            toast(R.string.common_code_send_hint);
            mCountdownView.start();
        }*/
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public void onRightClick(View view) {

        new ChargeUpDialog.Builder(getContext())
                .setListener((dialog, chargeUpBean, isEdit, editPosition) -> {
                    mAdapter.addItem(0, chargeUpBean);
                    spRvChargeUp.scrollToPosition(0);
                    putCacheChargeUpBeans();
                })
                .show();
        // 菜单弹窗
        /*new ListPopup.Builder(getContext())
                .setList("选择拍照", "选取相册")
                .addOnShowListener(popupWindow -> toast("PopupWindow 显示了"))
                .addOnDismissListener(popupWindow -> toast("PopupWindow 销毁了"))
                .setListener((ListPopup.OnListener<String>) (popupWindow, position, s) -> toast("点击了：" + s))
                .showAsDropDown(view);*/
    }

    /** 存储记账数据缓存 */
    private void putCacheChargeUpBeans() {
        getTotalBalance();
        mMmkv.encode(ChargeUpConstants.MMKV_CHARGEUP_DATA_KEY, gson.toJson(mAdapter.getData()));
    }

    private CopyOnWriteArrayList<ChargeUpBean> getCacheChargeUpBeans() {
        gson = GsonFactory.getSingletonGson();

        String chargeUpBeans = mMmkv.getString(ChargeUpConstants.MMKV_CHARGEUP_DATA_KEY, gson.toJson(mAdapter.getData()));
        CopyOnWriteArrayList<ChargeUpBean> chargeUpBeanList = gson.fromJson(chargeUpBeans, new TypeToken<CopyOnWriteArrayList<ChargeUpBean>>() {
        }.getType());

        return chargeUpBeanList;
    }

    /** 获取总余额 */
    private void getTotalBalance() {

        if (CollectionUtils.isEmpty(mAdapter.getData()))
            return;

        ArrayList<String> priceList = new ArrayList<>();
        for (ChargeUpBean chargeUpBean : mAdapter.getData()) {
            priceList.add(chargeUpBean.getAccountsState() + chargeUpBean.getAccountsMoney());
        }

        BigDecimal result = null;
        try {
            result = getCalculationAmount(priceList);
            Timber.i("getTotalBalance() 当前数据源: %s", priceList);
        } catch (Exception e) {
            ToastUtils.show("转换异常: \n请修改元数据!\n" + e.getMessage());
            result = new BigDecimal("0");
            Timber.e(e, "getTotalBalance() 当前数据源: %s", priceList);

        }

        sbAmount.setRightText(currency.format(result));
    }

    /**
     * {@link SwitchButton.OnCheckedChangeListener}
     */
    @Override
    public void onCheckedChanged(SwitchButton button, boolean checked) {
        toast(checked);
    }

    /**
     * 当 RecyclerView 某个条目被长按时回调
     *
     * @param recyclerView RecyclerView 对象
     * @param itemView     被点击的条目对象
     * @param position     被点击的条目位置
     * @return 是否拦截事件
     */
    @Log
    @Override
    public boolean onItemLongClick(RecyclerView recyclerView, View itemView, int position) {
        // 消息对话框
        final ChargeUpBean chargeUpBean = mAdapter.getData().get(position);
        new MessageDialog.Builder(getActivity())
                // 标题可以不用填写
                // .setTitle("我是标题")
                // 内容必须要填写
                .setMessage("确认删除" + chargeUpBean.getAccountsType() + "吗? 该操作不可恢复")
                // 确定按钮文本
                .setConfirm(getString(R.string.common_confirm))
                // 设置 null 表示不显示取消按钮
                .setCancel(getString(R.string.common_cancel))
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setListener(new MessageDialog.OnListener() {

                    @Override
                    public void onConfirm(BaseDialog dialog) {
                        mAdapter.removeItem(position);
                        putCacheChargeUpBeans();
                        toast("删除成功");
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        // toast("取消了");
                    }
                })
                .show();
        return false;
    }

    /**
     * 当 RecyclerView 某个条目被点击时回调
     *
     * @param recyclerView RecyclerView 对象
     * @param itemView     被点击的条目对象
     * @param position     被点击的条目位置
     */
    @Log
    @SingleClick
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {

        new ChargeUpDialog.Builder(getContext())
                .setEdit(true, position)
                .setBean(mAdapter.getData().get(position))
                .setListener((dialog, chargeUpBean, isEdit, editPosition) -> {
                    if (isEdit) {
                        mAdapter.removeItem(editPosition);
                    }
                    mAdapter.addItem(editPosition, chargeUpBean);
                    // spRvChargeUp.scrollToPosition(editPosition + 2);
                    putCacheChargeUpBeans();
                })
                .show();
    }
}