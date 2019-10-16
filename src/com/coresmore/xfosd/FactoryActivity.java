package com.coresmore.xfosd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.coresmore.xfosd.adapter.SortAdapter;
import com.coresmore.xfosd.bean.SortModel;
import com.coresmore.xfosd.utils.PinyinComparator;
import com.coresmore.xfosd.utils.Utlis;
import com.coresmore.xfosd.view.SideBar;
import com.coresmore.xfosd.view.SideBar.OnTouchingLetterChangedListener;
import com.coresmore.xfosd.view.Sort_Dialog;
import com.coresmore.xfosd.view.Sort_Dialog.OnDateChange;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class FactoryActivity extends Activity {

    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private Sort_Dialog sort_Dialog;
    private TextView tv_versions;

    private String versions_DPS;
    /** 车型，车号 */
    private int carFactory, carNum;
    // private ClearEditText mClearEditText;
    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int lastFirstVisibleItem = -1;
    private List<SortModel> SourceDateList;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    /**
     * 分组的布局
     */
    private LinearLayout titleLayout;

    /**
     * 分组上显示的字母
     */
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_factory);

        Intent intent = getIntent();
        if (null != intent) {
            carFactory = intent.getIntExtra("carFactory", carFactory);
            carNum = intent.getIntExtra("carNum", carNum);
            versions_DPS = intent.getStringExtra("versions");
        }
        initView();
        initData();
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        title = (TextView) findViewById(R.id.title);

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        tv_versions = findViewById(R.id.tv_versions);

        pinyinComparator = new PinyinComparator();
        sort_Dialog = new Sort_Dialog(this);

    }

    private void initData() {
        sideBar.setTextView(dialog);// 设置响应的字体背景样式
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(touchingLetterChangedListener);

        SourceDateList = getData(getResources().getStringArray(R.array.date));
        // 根据a-z进行排序源数据
        adapter = new SortAdapter(this, SourceDateList);
        Collections.sort(SourceDateList, pinyinComparator);
        adapter.setCheckNum(carFactory);
        sortListView.setAdapter(adapter);

        sortListView.setOnItemClickListener(itemClickListener);
        sortListView.setOnScrollListener(onScrollListener);

        sort_Dialog.setDateChange(new OnDateChange() {
            @Override
            public void onDate(int mun) {
                carNum = mun;
            }
        });
        versions_DPS = TextUtils.isEmpty(versions_DPS) ? "1.0" : versions_DPS;
        String text = getString(R.string.str_versions, versions_DPS, Utlis.getVerName(this));
        tv_versions.setText(text);
    }
    
    /** 获取数据 */
    private List<SortModel> getData(String[] data) {
        List<SortModel> listarray = new ArrayList<SortModel>();
        for (int i = 0; i < data.length; i++) {
            String pinyin = getPingYin(data[i]);
            String Fpinyin = pinyin.substring(0, 1).toUpperCase();
            SortModel person = new SortModel();
            person.setName(data[i]);
            // person.setPinYin(pinyin);
            // 正则表达式，判断首字母是否是英文字母
            if (Fpinyin.matches("[A-Z]")) {
                person.setSortLetters(Fpinyin);
            } else {
                person.setSortLetters("#");
            }
            listarray.add(person);
        }
        return listarray;
    }

    /** 右侧字母栏定位listview的位置 */
    private SideBar.OnTouchingLetterChangedListener touchingLetterChangedListener = new OnTouchingLetterChangedListener() {

        @Override
        public void onTouchingLetterChanged(String s) {
            // 该字母首次出现的位置
            int position = adapter.getPositionForSection(s.charAt(0));
            if (position != -1) {
                sortListView.setSelection(position);
            }
        }
    };

    /** item 点击事件 */
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

            // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
            // TODO
            // Toast.makeText(getApplication(),
            // adapter.getItem(position).getName() + "=" + position,
            // Toast.LENGTH_SHORT).show();
            boolean isHere = false;
            if (position == carFactory) {
                isHere = true;
            }
            adapter.setCheckNum(position);
            carFactory = position;

            sort_Dialog.show();
            int arrayid = 0;
            switch (position) {
            case 0:
                arrayid = R.array.item0;
                break;
            case 1:
                arrayid = R.array.item1;
                break;
            case 2:
                arrayid = R.array.item2;
                break;
            case 3:
                arrayid = R.array.item3;
                break;
            case 4:
                arrayid = R.array.item4;
                break;
            case 5:
                arrayid = R.array.item5;
                break;
            case 6:
                arrayid = R.array.item6;
                break;
            case 7:
                arrayid = R.array.item7;
                break;
            case 8:
                arrayid = R.array.item8;
                break;
            case 9:
                arrayid = R.array.item9;
                break;
            case 10:
                arrayid = R.array.item10;
                break;
            case 11:
                arrayid = R.array.item11;
                break;
            case 12:
                arrayid = R.array.item12;
                break;
            case 13:
                arrayid = R.array.item13;
                break;
            case 14:
                arrayid = R.array.item14;
                break;
            case 15:
                arrayid = R.array.item15;
                break;

            default:
                break;
            }
            List<String> baseDate = Arrays.asList(getResources().getStringArray(arrayid));
            sort_Dialog.setData(adapter.getItem(position).getName(), baseDate, isHere ? carNum : 0);
            carNum = isHere ? carNum : 0;
        }
    };

    private AbsListView.OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            // LogUtils.i(+visibleItemCount+"=当前对呀的Item是="+firstVisibleItem);

            // 字母连续断层使不能置顶，例如 D （空） F使D到F阶段不存在置顶
            int section;
            try {
                section = adapter.getSectionForPosition(firstVisibleItem);
            } catch (Exception e) {
                return;
            }
            int nextSecPosition = adapter.getPositionForSection(section + 1);
            // 解决断层置顶
            for (int i = 1; i < 30; i++) {
                // 26个英文字母充分循环
                if (nextSecPosition == -1) {
                    // 继续累加
                    int data = section + 1 + i;
                    nextSecPosition = adapter.getPositionForSection(data);
                } else {
                    break;
                }
            }
            if (firstVisibleItem != lastFirstVisibleItem) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout.getLayoutParams();
                params.topMargin = 0;
                titleLayout.setLayoutParams(params);
                title.setText(String.valueOf((char) section));

            }
            if (nextSecPosition == firstVisibleItem + 1) {
                View childView = view.getChildAt(0);
                if (childView != null) {
                    int titleHeight = titleLayout.getHeight();
                    int bottom = childView.getBottom();
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout.getLayoutParams();
                    if (bottom < titleHeight) {
                        float pushedDistance = bottom - titleHeight;
                        params.topMargin = (int) pushedDistance;
                        titleLayout.setLayoutParams(params);
                    } else {
                        if (params.topMargin != 0) {
                            params.topMargin = 0;
                            titleLayout.setLayoutParams(params);
                        }
                    }
                }
            }
            lastFirstVisibleItem = firstVisibleItem;
        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.img_back:
            onBackPressed();
            break;

        default:
            break;
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.putExtra("carFactory", carFactory);
        intent.putExtra("carNum", carNum);
        setResult(1, intent);
        super.onBackPressed();
    }

    /** 汉字转拼音 */
    public static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        char[] input = inputString.trim().toCharArray();
        String output = "";
        try {
            for (char curchar : input) {
                if (java.lang.Character.toString(curchar).matches("[\u4e00-\u9fa5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(curchar, format);
                    output += temp[0];
                } else
                    output += java.lang.Character.toString(curchar);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output;
    }

}
