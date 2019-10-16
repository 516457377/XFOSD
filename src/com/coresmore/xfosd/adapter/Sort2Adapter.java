package com.coresmore.xfosd.adapter;

import java.util.List;

import com.coresmore.xfosd.R;
import com.coresmore.xfosd.bean.SortModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class Sort2Adapter extends BaseAdapter {
    private List<String> list = null;
    private Context mContext;
    private int checkNum = 0;

    public int getCheckNum() {
        return checkNum;
    }
    
    public void setCheckNum(int checkNum) {
        this.checkNum = checkNum;
        notifyDataSetInvalidated();
    }

    public Sort2Adapter(Context context, List<String> list, int checkNum) {
        this.mContext = context;
        this.list = list;
        this.checkNum = checkNum;
    }

    public void updateListView(List<String> list, int checkNum) {
        this.list = list;
        this.checkNum = checkNum;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
//        final SortModel mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_sort2, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.imgCheck = (ImageView) view.findViewById(R.id.img_check);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // check
        if (checkNum == position)
            viewHolder.imgCheck.setVisibility(View.VISIBLE);
        else
            viewHolder.imgCheck.setVisibility(View.GONE);

        // name
        viewHolder.tvTitle.setText(list.get(position));

        return view;
    }
    

    final static class ViewHolder {
        TextView tvTitle;
        ImageView imgCheck;
    }

}
