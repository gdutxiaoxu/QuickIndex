package com.xujun.quickindex;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xujun.quickindex.adapter.HaoHanAdapter;
import com.xujun.quickindex.bean.Person;
import com.xujun.quickindex.ui.QuickIndexBar;
import com.xujun.quickindex.ui.QuickIndexBar.OnLetterUpdateListener;
import com.xujun.quickindex.util.Cheeses;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {

    private ListView mListView;
    private ArrayList<Person> persons;
    private TextView tv_center;
    private QuickIndexBar mQuickIndexBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        // 设置监听
        initListener();

        initdata();


    }

    private void initView() {
        mQuickIndexBar = (QuickIndexBar) findViewById(R.id.bar);
        mListView = (ListView) findViewById(R.id.lv_main);
        tv_center = (TextView) findViewById(R.id.tv_center);
    }

    private void initListener() {
        mQuickIndexBar.setListener(new OnLetterUpdateListener() {
            @Override
            public void onLetterUpdate(String letter) {
                //				UIUtils.showToast(getApplicationContext(), letter);

                showLetter(letter);
                // 根据字母定位ListView, 找到集合中第一个以letter为拼音首字母的对象,得到索引
                for (int i = 0; i < persons.size(); i++) {
                    Person person = persons.get(i);
                    String l = person.getPinyin().charAt(0) + "";
                    if (TextUtils.equals(letter, l)) {
                        // 匹配成功
                        mListView.setSelection(i);
                        break;
                    }
                }
            }
        });
    }



    private void initdata() {
        persons = new ArrayList<Person>();

        // 填充数据 , 排序
        fillAndSortData(persons);

        mListView.setAdapter(new HaoHanAdapter(MainActivity.this, persons));

    }

    private Handler mHandler = new Handler();

    /**
     * 显示字母
     *
     * @param letter
     */
    protected void showLetter(String letter) {
        tv_center.setVisibility(View.VISIBLE);
        tv_center.setText(letter);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_center.setVisibility(View.GONE);
            }
        }, 2000);

    }

    private void fillAndSortData(ArrayList<Person> persons) {
        // 填充数据
        for (int i = 0; i < Cheeses.NAMES.length; i++) {
            String name = Cheeses.NAMES[i];
            persons.add(new Person(name));
        }

        // 进行排序
        Collections.sort(persons);
    }
}
