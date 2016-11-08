package com.grgbanking.ct;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.grgbanking.ct.database.DBManager;
import com.grgbanking.ct.database.ExtractBoxs;
import com.grgbanking.ct.rfid.UfhData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author ：     cmy
 * @version :     2016/10/25.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class QcodeActivity extends Activity {

    private static final int MSG_UPDATE_LISTVIEW = 0;
    HashMap<String, String> mapAll = new HashMap<String, String>();
    private Context context;
    private Button BT_scan;
    private Button BT_stop;
    private ListView LV_RFID;
    private ArrayList<HashMap<String, String>> listItem;
    private Timer timer;
    private Handler mHandler;
    private SimpleAdapter listItemAdapter;
    private boolean Scanflag = false;
    private boolean isCanceled = true;
    private Map<String, Integer> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qcode_activity);
        findViewById();
        onClickListener();
        //        mData = getData();//为刚才的变量赋值
        // 生成动态数组，加入数据
        listItem = new ArrayList<HashMap<String, String>>();

        listItemAdapter = new SimpleAdapter(this,
                listItem,
                R.layout.qcode_list,
                new String[]{"RFID", "Button", "code"},
                new int[]{R.id.qcode_list_tv, R.id.qcode_list_bt, R.id.list_qcdoe});
        LV_RFID.setAdapter(listItemAdapter);
        flashInfo();


    }


    private void onClickListener() {
        BT_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connDevices();
                startDevices();
            }
        });

        BT_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/10/25 停止扫描
                cancelScan();
                UfhData.Set_sound(false);
            }
        });

    }

    private void findViewById() {
        BT_scan = (Button) findViewById(R.id.qcode_bt_scan);
        BT_stop = (Button) findViewById(R.id.qcode_bt_stop);
        LV_RFID = (ListView) findViewById(R.id.list_qcdoe);
    }

    //    /**
    //     * 初始化一个List
    //     *
    //     * @return
    //     */
    //    public List<HashMap<String, Object>> getData() {
    //        //新建一个集合类，用于存放多条数据。
    //        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    //        HashMap<String, Object> map = null;
    //        for (int i = 1; i <= 10; i++) {
    //            map = new HashMap<String, Object>();
    ////            map.put("RFID","075500200000001000000001");
    //            map.put("Button", "开始");
    //            map.put("code", "11111111111");
    //            list.add(map);
    //        }
    //        return list;
    //    }

    private void connDevices() {
        int result = UfhData.UhfGetData.OpenUhf(57600, (byte) 0xff, 4, 1, null);

        if (result == 0) {
            UfhData.UhfGetData.GetUhfInfo();
            Log.i("~~", "成功");
        } else {
            //            Toast.makeText(context, "连接设备失败，请关闭程序重新登录", Toast.LENGTH_LONG).show();
        }
    }

    private void startDevices() {
        if (!UfhData.isDeviceOpen()) {
            Toast.makeText(this, R.string.detail_title, Toast.LENGTH_LONG).show();
            return;
        }
        if (timer == null) {
            UfhData.Set_sound(true);
            UfhData.SoundFlag = false;

            isCanceled = false;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (Scanflag)
                        return;
                    Scanflag = true;
                    UfhData.read6c();
                    mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
                    mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW);
                    Scanflag = false;
                }
            }, 0, 10);
        } else {
            cancelScan();
            UfhData.Set_sound(false);
        }

    }


    private void cancelScan() {
        isCanceled = true;
        mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
        if (timer != null) {
            timer.cancel();
            timer = null;
            UfhData.scanResult6c.clear();
        }
    }

    private void flashInfo() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (isCanceled)
                    return;
                switch (msg.what) {
                    case MSG_UPDATE_LISTVIEW:
                        data = UfhData.scanResult6c;

                        //判断是否是正确的RFID
                        DBManager db = new DBManager(QcodeActivity.this);
                        List<ExtractBoxs> extractBoxsList = db.queryExtractBoxs();
                        Map<String, String> extractBoxsMap = new HashMap<String, String>();
                        if (extractBoxsList != null && extractBoxsList.size() > 0) {
                            for (ExtractBoxs ex : extractBoxsList) {
                                String rfid = ex.getRfidNum();
                                String boxsn = ex.getBoxSn();
                                extractBoxsMap.put(rfid, boxsn);
                            }
                        } else {
                            Toast.makeText(QcodeActivity.this, "请先下载数据", Toast.LENGTH_SHORT).show();
                        }

                        Iterator it = data.keySet().iterator();
                        while (it.hasNext()) {
                            String key = (String) it.next();
                            String RFID = key;
                            if (extractBoxsMap.get(key) != null) {
                                if (mapAll.get(key) == null) {
                                    String boxSn = extractBoxsMap.get(key);
                                    Log.i("=====", "=====" + key);
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("RFID", RFID);
                                    map.put("Button", "开始扫描:" + boxSn);
                                    mapAll.put(key, boxSn);
                                    listItem.add(map);
                                }
                            }
                        }
                        listItemAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

}
