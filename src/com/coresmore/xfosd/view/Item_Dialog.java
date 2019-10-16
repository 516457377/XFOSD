package com.coresmore.xfosd.view;

import com.coresmore.xfosd.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Item_Dialog extends Dialog implements OnSeekBarChangeListener, android.view.View.OnClickListener {

    private TextView tv_ms, tv_cm, tv_name;
    private SeekBar seek_item;
    private OnDialogData onDataChange;
    private ImageButton btn_sub, btn_add;
    private int tag = -1;

    public Item_Dialog(Context context) {
        super(context);
    }

    public void addOnDialogDataListener(OnDialogData dataChange) {
        this.onDataChange = dataChange;
    }

    public void setData(String name, float cm, float ms, int por, int tag) {
        tv_name.setText(name);
        seek_item.setProgress(por);
        tv_cm.setText(getContext().getString(R.string.str_cm, cm));
        tv_ms.setText(getContext().getString(R.string.str_ms, ms));
        this.tag = tag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//广播、服务钟启动需要添加
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_ms);
        ininView();
    }

    private void ininView() {
        tv_cm = (TextView) findViewById(R.id.tv_cm);
        tv_ms = (TextView) findViewById(R.id.tv_ms);
        tv_name = (TextView) findViewById(R.id.tv_name);
        seek_item = (SeekBar) findViewById(R.id.seek_item);
        btn_sub = (ImageButton) findViewById(R.id.btn_sub);
        btn_add = (ImageButton) findViewById(R.id.btn_add);

        btn_sub.setOnClickListener(this);
        btn_add.setOnClickListener(this);

        tv_cm.setText(getContext().getString(R.string.str_cm, 0f));
        tv_ms.setText(getContext().getString(R.string.str_ms, 0f));
        seek_item.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tv_ms.setText(getContext().getString(R.string.str_ms, progress * 0.05f));
        tv_cm.setText(getContext().getString(R.string.str_cm, progress * 1.73f));
        if (null != onDataChange) {
            onDataChange.Data(progress, progress * 0.05f, progress * 1.73f, tag);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void dismiss() {
        tag = -1;
        super.dismiss();
    }

    public interface OnDialogData {
        void Data(int progress, float ms, float cm, int tag);
    }

    /** 加减按钮 */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_sub:
            seek_item.setProgress(seek_item.getProgress() - 1);
            break;
        case R.id.btn_add:
            seek_item.setProgress(seek_item.getProgress() + 1);
            break;
        default:
            break;
        }
    }

}
