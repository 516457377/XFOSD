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

public class SortAdapter extends BaseAdapter implements SectionIndexer {
    private List<SortModel> list = null;
    private Context mContext;
    private int checkNum = 0;

    public int getCheckNum() {
        return checkNum;
    }
    
    public void setCheckNum(int checkNum) {
        this.checkNum = checkNum;
        notifyDataSetInvalidated();
    }

    public SortAdapter(Context context, List<SortModel> list) {
        this.mContext = context;
        this.list = list;
    }

    public void updateListView(List<SortModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public SortModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final SortModel mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_sort, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.imgCheck = (ImageView) view.findViewById(R.id.img_check);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // 获取首字母的assii值
        int section = getSectionForPosition(position);
        // 通过首字母的assii值来判断是否显示字母
        int positionForSelection = getPositionForSection(section);

        viewHolder.tvLetter.setOnClickListener(null);
        // 字母
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        // check
        if (checkNum == position)
            viewHolder.imgCheck.setVisibility(View.VISIBLE);
        else
            viewHolder.imgCheck.setVisibility(View.GONE);

        // name
        viewHolder.tvTitle.setText(this.list.get(position).getName());

        return view;

    }

    final static class ViewHolder {
        TextView tvLetter;
        TextView tvTitle;
        ImageView imgCheck;
    }

    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}
