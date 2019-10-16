package com.coresmore.xfosd.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.coresmore.xfosd.R;
import com.coresmore.xfosd.adapter.Sort2Adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Sort_Dialog extends Dialog implements android.view.View.OnClickListener {

    private TextView tv_name;
    private ImageView img_back;
    private ListView list_sort;
    private List<String> baseDate = new ArrayList<>();
    private Sort2Adapter adapter;
    private OnDateChange dateChange;

    public void setDateChange(OnDateChange dateChange) {
        this.dateChange = dateChange;
    }

    public Sort_Dialog(Context context) {
        super(context);
    }

    public void setData(String name, List<String> baseDate, int checkNum) {
        tv_name.setText(name);
        this.baseDate = baseDate;
        adapter.updateListView(baseDate, checkNum);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//广播、服务钟启动需要添加
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_sort);
        ininView();
    }

    private void ininView() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        img_back = (ImageView) findViewById(R.id.img_back);
        list_sort = (ListView) findViewById(R.id.list_sort);

        adapter = new Sort2Adapter(getContext(), baseDate, 0);
        list_sort.setAdapter(adapter);
        list_sort.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // Toast.makeText(getContext(),"String"+arg2,Toast.LENGTH_SHORT).show();
                adapter.setCheckNum(arg2);
                if (null != dateChange) {
                    dateChange.onDate(arg2);
                }
                dismiss();
            }
        });

        img_back.setOnClickListener(this);

    }

    @Override
    public void dismiss() {
        // tag = -1;
//        Log.d("########", "dismiss");
        super.dismiss();
    }

    /** 加减按钮 */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.img_back:
            dismiss();
            // seek_item.setProgress(seek_item.getProgress() - 1);
            break;
        case R.id.btn_add:
            // seek_item.setProgress(seek_item.getProgress() + 1);
            break;
        default:
            break;
        }
    }
    
    public interface OnDateChange{
        void onDate(int mun);
    }

}
