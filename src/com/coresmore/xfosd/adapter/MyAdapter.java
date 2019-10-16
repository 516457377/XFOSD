package com.coresmore.xfosd.adapter;

import java.util.List;

import com.coresmore.xfosd.R;
import com.coresmore.xfosd.libs.data.BleDevice;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
    
    private List<BleDevice> mList;
    private Context mContext;
    
    
    public MyAdapter(List<BleDevice> mList, Context mContext) {
        super();
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device, null);
            holder = new Holder((TextView) convertView.findViewById(R.id.device_name),
                    (TextView) convertView.findViewById(R.id.device_mac),
                    (TextView) convertView.findViewById(R.id.device_rssi));
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        holder.device_mac.setText(mList.get(position).getMac());
        holder.device_rssi.setText(mContext.getString(R.string.str_rssi, mList.get(position).getRssi()));
        String name = mList.get(position).getName();
        if (TextUtils.isEmpty(name)) {
            holder.device_name.setText("未知设备"); 
        } else {
            holder.device_name.setText(name); 
        }
        return convertView;
    }
    
    public void setData(List<BleDevice> mList){
        this.mList = mList;
        notifyDataSetChanged();
    }
    
    public class Holder {
        public TextView device_name,device_mac,device_rssi;

        public Holder(TextView device_name, TextView device_mac, TextView device_rssi) {
            super();
            this.device_name = device_name;
            this.device_mac = device_mac;
            this.device_rssi = device_rssi;
        }
        
    }
}
