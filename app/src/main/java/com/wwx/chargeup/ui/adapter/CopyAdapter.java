package com.wwx.chargeup.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.wwx.chargeup.R;
import com.wwx.chargeup.app.AppAdapter;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/05
 *    desc   : 可进行拷贝的副本
 */
public final class CopyAdapter extends AppAdapter<String> {

    public CopyAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private ViewHolder() {
            super(R.layout.copy_item);
        }

        @Override
        public void onBindView(int position) {

        }
    }
}