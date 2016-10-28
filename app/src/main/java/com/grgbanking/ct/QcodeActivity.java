package com.grgbanking.ct;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author ：     cmy
 * @version :     2016/10/25.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class QcodeActivity extends Activity {

    private Button BT_scan;
    private Button BT_stop;
    private ListView LV_RFID;
    private List<HashMap<String, Object>> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qcode_activity);
        findViewById();
        onClickListener();
        mData = getData();//为刚才的变量赋值
        MyAdapter listadapter = new MyAdapter(this);//创建一个适配器
        LV_RFID.setAdapter(listadapter);//为ListView控件绑定适配器
    }


    private void onClickListener() {
        BT_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/10/25 扫描RFID
            }
        });

        BT_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/10/25 停止扫描
            }
        });

    }

    private void findViewById() {
        BT_scan = (Button) findViewById(R.id.qcode_bt_scan);
        BT_stop = (Button) findViewById(R.id.qcode_bt_stop);
        LV_RFID = (ListView) findViewById(R.id.list_qcdoe);
    }

    /**
     * 初始化一个List
     *
     * @return
     */
    public List<HashMap<String, Object>> getData() {
        //新建一个集合类，用于存放多条数据。
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = null;
        for (int i = 1; i <= 10; i++) {
            map = new HashMap<String, Object>();
            map.put("RFID", "123456");
            map.put("Button", "开始");
            map.put("code", "11111111111");
            //            map.put("list", "haha");
            list.add(map);
        }
        return list;
    }

    /**
     * 自定义适配器
     */
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;// 动态布局映射
        private Context context;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        // 决定ListView有几行可见
        @Override
        public int getCount() {
            return mData.size();// ListView的条目数
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.qcode_list, null);//根据布局文件实例化view
            TextView RFID = (TextView) convertView.findViewById(R.id.qcode_list_tv);//找某个控件
            RFID.setText(mData.get(position).get("RFID").toString());//给该控件设置数据(数据从集合类中来)
            Button start = (Button) convertView.findViewById(R.id.qcode_list_bt);//找某个控件
            ListView list = (ListView) convertView.findViewById(R.id.qcode_list_item);//找某个控件
            ItemAdapter itemadapter = new ItemAdapter(context);
            list.setAdapter(itemadapter);
            return convertView;
        }
    }

    public class ItemAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext;

        public ItemAdapter(Context context) {
            super();
            this.mInflater = LayoutInflater.from(context);
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.peixiangdt_list_item, null);//根据布局文件实例化view
            TextView CODE = (TextView) convertView.findViewById(R.id.code);
            CODE.setText(mData.get(position).get("code").toString());
            return convertView;
        }
    }
}
