package com.sunfusheng.googlemap.adapter;

import android.content.Context;

import com.sunfusheng.GroupRecyclerViewAdapter;
import com.sunfusheng.GroupViewHolder;
import com.sunfusheng.googlemap.R;

/**
 * @author sunfusheng on 2018/9/23.
 */
public class StickyGroupAdapter extends GroupRecyclerViewAdapter<String> {

    public StickyGroupAdapter(Context context, String[][] groups) {
        super(context, groups);
    }

    @Override
    public boolean showFooter() {
        return false;
    }

    @Override
    public int getHeaderLayoutId(int viewType) {
        return R.layout.item_group_header;
    }

    @Override
    public int getChildLayoutId(int viewType) {
        return R.layout.item_group_item;
    }

    @Override
    public int getFooterLayoutId(int viewType) {
        return 0;
    }

    @Override
    public void onBindHeaderViewHolder(GroupViewHolder holder, String item, int groupPosition) {
        holder.setText(R.id.text, item);
    }

    @Override
    public void onBindChildViewHolder(GroupViewHolder holder, String item, int groupPosition, int childPosition) {
        holder.setText(R.id.text, item);
    }

    @Override
    public void onBindFooterViewHolder(GroupViewHolder holder, String item, int groupPosition) {

    }

}
