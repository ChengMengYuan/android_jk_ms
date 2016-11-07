package com.grgbanking.ct;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.grgbanking.ct.rfid.UfhData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.grgbanking.ct.rfid.UfhData.timer;

/**
 * @author ：     cmy
 * @version :     2016/10/25.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class QcodeActivity extends Activity {

    private Context context;

    private Button BT_scan;
    private Button BT_stop;
    private ListView LV_RFID;
    private List<HashMap<String, Object>> mData;

    private Handler mHandler;
    private boolean Scanflag = false;
    private boolean isCanceled = true;
    private static final int MSG_UPDATE_LISTVIEW = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qcode_activity);
        findViewById();
        onClickListener();
        mData = getData();//为刚才的变量赋值
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                getData(),
                R.layout.qcode_list,
                new String[]{"RFID", "Button", "code"},
                new int[]{R.id.qcode_list_tv, R.id.qcode_list_bt, R.id.list_qcdoe});
        LV_RFID.setAdapter(simpleAdapter);

    }


    private void onClickListener() {
        BT_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //连接设备
                int result = UfhData.UhfGetData.OpenUhf(57600, (byte)0xff, 4, 1, null);
                if (result == 0) {
                    UfhData.UhfGetData.GetUhfInfo();
                    Toast.makeText(context, "连接设备成功", Toast.LENGTH_LONG).show();
                    //		mHandler.removeMessages(MSG_SHOW_PROPERTIES);
                    //		mHandler.sendEmptyMessage(MSG_SHOW_PROPERTIES);
                } else {
                    Toast.makeText(context, "连接设备失败，请关闭程序重新登录", Toast.LENGTH_LONG).show();
                }

                // TODO: 2016/10/25 扫描RFID
                if (timer == null) {
                    //声音开关初始化
                    UfhData.Set_sound(true);
                    UfhData.SoundFlag = false;

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
                    isCanceled = true;
                    mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                        UfhData.scanResult6c.clear();
                    }
                    UfhData.Set_sound(false);
                }
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
            list.add(map);
        }
        return list;
    }

}
