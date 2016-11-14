//package com.grgbanking.ct;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.grgbanking.ct.rfid.UfhData;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Timer;
//
///**
// * @author ：     cmy
// * @version :     2016/10/25.
// * @e-mil ：      mengyuan.cheng.mier@gmail.com
// * @Description :
// */
//
//public class QcodeActivity extends Activity {
//
//    private static final int MSG_UPDATE_LISTVIEW = 0;
//    HashMap<String, Object> mapAll = new HashMap<String, Object>();
//    HashMap<String, Object> map = new HashMap<String, Object>();
//    private Button BT_scan;
//    private Button BT_stop;
//    private ListView LV_RFID;
//    private ArrayList<HashMap<String, Object>> listItem;
//    private Timer timer;
//    private Handler mHandler;
//    private boolean Scanflag = false;
//    private boolean isCanceled = true;
//    private Map<String, Integer> data;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.qcode_activity);
//        findViewById();
//        onClickListener();
//        //        mData = getData();//为刚才的变量赋值
//        // 生成动态数组，加入数据
//        listItem = new ArrayList<HashMap<String, Object>>();Object
//        //        listItemAdapter = new SimpleAdapter(this,
//        //                getData(),
//        //                R.layout.qcode_list,
//        //                new String[]{"RFID", "Button", "code"},
//        //                new int[]{R.id.qcode_list_tv, R.id.qcode_list_bt, R.id.list_qcdoe});
//        MyAdapter adapter = new MyAdapter(this);
//        LV_RFID.setAdapter(adapter);
//        flashInfo();
//    }
//
//
//    private void onClickListener() {
//        BT_scan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                connDevices();
//                startDevices();
//            }
//        });
//
//        BT_stop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO: 2016/10/25 停止扫描
//                cancelScan();
//                UfhData.Set_sound(false);
//            }
//        });
//
//    }
//
//    private void findViewById() {
//        BT_scan = (Button) findViewById(R.id.qcode_bt_scan);
//        BT_stop = (Button) findViewById(R.id.qcode_bt_stop);
//        LV_RFID = (ListView) findViewById(R.id.list_qcdoe);
//    }
//
//    /**
//     * 初始化一个List
//     *
//     * @return
//     */
//    public List<HashMap<String, Object>> getData() {
//        for (int i = 0; i <= listItem.size(); i++) {
//
//        }
//        Map<String, Object> map = new HashMap<String, Object>();
//
//
//        return listItem;
//    }
//
//    private void connDevices() {
//        int result = UfhData.UhfGetData.OpenUhf(57600, (byte) 0xff, 4, 1, null);
//
//        if (result == 0) {
//            UfhData.UhfGetData.GetUhfInfo();
//            Log.i("~~", "成功");
//        } else {
//            //            Toast.makeText(context, "连接设备失败，请关闭程序重新登录", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private void startDevices() {
//        StringBuffer sb = new StringBuffer();
//        if (!UfhData.isDeviceOpen()) {
//            Toast.makeText(this, R.string.detail_title, Toast.LENGTH_LONG).show();
//            return;
//        }
//        UfhData.read6c();
//        data = UfhData.scanResult6c;
//        Iterator it = data.keySet().iterator();
//        while (it.hasNext()) {
//            String t = it.next().toString();
//            Log.i("===key is ==", "==" + t);
//            sb.append(t).append("&");
//            String s = sb.toString();
//            map.put("rfid", s);
//            listItem.add(map);
//            QcodeActivity.MyAdapter adapter = new MyAdapter(QcodeActivity.this);
//            adapter.notifyDataSetChanged();
//        }
//
//
//        //        if (timer == null) {
//        //            UfhData.Set_sound(true);
//        //            UfhData.SoundFlag = false;
//        //
//        //            isCanceled = false;
//        //            timer = new Timer();
//        //            timer.schedule(new TimerTask() {
//        //                @Override
//        //                public void run() {
//        //                    if (Scanflag)
//        //                        return ;
//        //                    Scanflag = true;
//        //                    UfhData.read6c();
//        //////                    mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
//        //////                    mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW);
//        ////                    Scanflag = false;
//        //                }
//        //            }, 0, 10);
//        //        } else {
//        //            cancelScan();
//        //            UfhData.Set_sound(false);
//        //        }
//
//    }
//
//
//    private void cancelScan() {
//        isCanceled = true;
//        //        mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//            UfhData.scanResult6c.clear();
//        }
//    }
//
//    private String flashInfo() {
//        StringBuffer sb = new StringBuffer();
//        //            mHandler = new Handler() {
//        //                @Override
//        //                public void handleMessage(Message msg) {
//        //                    if (isCanceled){
//        //                        return null;
//        //                    }
//
//        //                    switch (msg.what) {
//        //                        case MSG_UPDATE_LISTVIEW:
//        data = UfhData.scanResult6c;
//        Iterator it = data.keySet().iterator();
//        while (it.hasNext()) {
//
//            String t = it.next().toString();
//            Log.i("===key is ==", "==" + t);
//            sb.append(t).append("&");
//        }
//        //                            //判断是否是正确的RFID
//        //                            DBManager db = new DBManager(QcodeActivity.this);
//        //                            List<ExtractBoxs> extractBoxsList = db.queryExtractBoxs();
//        //                            Map<String, String> extractBoxsMap = new HashMap<String, String>();
//        //                            if (extractBoxsList != null && extractBoxsList.size() > 0) {
//        //                                for (ExtractBoxs ex : extractBoxsList) {
//        //                                    String rfid = ex.getRfidNum();
//        //                                    String boxsn = ex.getBoxSn();
//        //                                    extractBoxsMap.put(rfid, boxsn);
//        //                                }
//        //                            } else {
//        //                                Toast.makeText(QcodeActivity.this, "请先下载数据", Toast.LENGTH_SHORT).show();
//        //                            }
//        //
//        //                            Iterator it = data.keySet().iterator();
//        //                            while (it.hasNext()) {
//        //                                String key = (String) it.next();
//        //                                String RFID = key;
//        //                                if (extractBoxsMap.get(key) != null) {
//        //                                    if (mapAll.get(key) == null) {
//        //                                        String boxSn = extractBoxsMap.get(key);
//        //                                        Log.i("=====", "==key===" + key);
//        //                                        HashMap<String, Object> map = new HashMap<String, Object>();
//        //                                        map.put("RFID", RFID);
//        //                                        map.put("Button", "开始扫描:" + boxSn);
//        //                                        mapAll.put(key, boxSn);
//        //                                        listItem.add(map);
//        //                                    }
//        //                                }
//        //                            }
//        //                            adapter.notifyDataSetChanged();
//        //                            break;
//        //                        default:
//        //                            break;
//        //                    }
//        //                    super.handleMessage(msg);
//        //                }
//        //            };
//        return sb.toString();
//    }
//
//    public class MyAdapter extends BaseAdapter {
//
//        private LayoutInflater mInflater;
//
//        public MyAdapter(Context context) {
//            this.mInflater = LayoutInflater.from(context);
//        }
//
//        /**
//         * listView在开始绘制的时候，系统首先调用getCount（）函数，
//         * 根据他的返回值得到listView的长度，然后根据这个长度，调用getView（）逐一绘制每一行。
//         * 如果getCount（）返回值是0的话，列表将不显示同样return 1，就只显示一行。
//         *
//         * @return
//         */
//        @Override
//        public int getCount() {
//            // FIXME: 2016/11/9
//            return 1;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        /**
//         * 系统显示列表时，首先实例化一个适配器（这里将实例化自定义的适配器）。
//         * 当手动完成适配时，必须手动映射数据，这需要重写getView（）方法。
//         * 系统在绘制列表的每一行的时候将调用此方法。
//         *
//         * @param position    position表示将显示的是第几行
//         * @param convertView covertView是从布局文件中inflate来的布局
//         * @param parent
//         * @return
//         */
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder = null;
//
//            if (convertView == null) {
//                holder = new ViewHolder();
//
//                convertView = mInflater.inflate(R.layout.qcode_list, null);
//                holder.rfid = (TextView) convertView.findViewById(R.id.qcode_list_tv);
//                holder.boxSn = (Button) convertView.findViewById(R.id.qcode_list_bt);
//                holder.listItem = (ListView) convertView.findViewById(R.id.qcode_list_item);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            //
//
//
//            holder.boxSn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(QcodeActivity.this, "....", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//
//            return convertView;
//        }
//    }
//
//    public final class ViewHolder {
//        public TextView rfid;
//        public Button boxSn;
//        public ListView listItem;
//    }
//
//}
