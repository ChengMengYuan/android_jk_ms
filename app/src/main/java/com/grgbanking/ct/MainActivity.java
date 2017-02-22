package com.grgbanking.ct;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ct.cach.DataCach;
import com.grgbanking.ct.database.Extract;
import com.grgbanking.ct.database.Person;
import com.grgbanking.ct.database.PersonTableHelper;
import com.grgbanking.ct.entity.PdaCashboxInfo;
import com.grgbanking.ct.entity.PdaLoginMsg;
import com.grgbanking.ct.entity.PdaNetInfo;
import com.grgbanking.ct.entity.PdaNetPersonInfo;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity {
    private static String TAG = "MainActivity";

    PopupMenu popupMenu;
    Menu menu;
    ListView listView;
    ImageView saomiaoImageView;
    SimpleAdapter listItemAdapter;
    ArrayList<HashMap<String, Object>> listItem;
    Person person = null;
    TextView mainTitle;
    Button mainBackButton = null;
    OnClickListener saomiaoButtonclick = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            //			startActivity(new Intent(getApplicationContext(), CaptureActivity.class));
            //清空缓存
            DataCach.clearAllDataCach();

            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };
    private SharedPreferences sp;
    private Context context;
    private PdaLoginMsg pdaLoginMsg;
    private ProgressDialog pd = null;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this.getApplicationContext();

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        popupMenu = new PopupMenu(this, findViewById(R.id.popupmenu_btn));
        menu = popupMenu.getMenu();

        try {
            //接收数据
            pdaLoginMsg = (PdaLoginMsg) getIntent().getSerializableExtra("pdaLoginMsg");
            //放入缓存
            DataCach.setPdaLoginMsg(pdaLoginMsg);
        } catch (Exception e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
        }


        // 通过XML文件添加菜单项
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.popupmenu, menu);


        mainTitle = (TextView) findViewById(R.id.main_title_view);
        listView = (ListView) findViewById(R.id.ListView01);
        //		saomiaoImageView=(ImageView) findViewById(R.id.saosao_button);
        //		saomiaoImageView.setOnClickListener(saomiaoButtonclick);
        mainBackButton = (Button) findViewById(R.id.main_btn_back);

        String netType = DataCach.netType;

        if (netType.equals(Constants.NET_COMMIT_TYPE_IN)) {
            mainTitle.setText("网点入库任务列表");
        } else {
            mainTitle.setText("网点出库任务列表");
        }

        // 生成动态数组，加入数据
        listItem = new ArrayList<HashMap<String, Object>>();
        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this, listItem, R.layout.main_list_item, new String[]{"list_img", "list_title", "list_position", "list_worktime"}, new int[]{R.id.list_img, R.id.list_title, R.id.list_position, R.id.list_worktime});
        // 添加并且显示
        listView.setAdapter(listItemAdapter);

        person = PersonTableHelper.queryEntity(this);
        //		String userId =person.getUser_id();
        //		String name = person.getUser_name();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //		params.add(new BasicNameValuePair("userId", userId));
        showWaitDialog("正在加载中...");

        try {
            loadLoginMessageCach();
        } catch (Exception e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
        }


        hideWaitDialog();


        // 添加点击
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {

                if (DataCach.taskMap.get(arg2 + "") != null) {
                    HashMap<String, Object> map = DataCach.taskMap.get(arg2 + "");
                    if (map.get("list_worktime").equals("已完成")) {
                        Toast.makeText(context, "该任务已完成", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, DetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("count", arg2);
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        // 点击返回按钮操作内容
        mainBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空缓存
                //                DataCach.clearAllDataCach();
                Intent intent = new Intent(MainActivity.this, NetOutInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.item_back:
                        //						Toast.makeText(MainActivity.this, "退出",
                        //								Toast.LENGTH_LONG).show();
                        //			startActivity(new Intent(getApplicationContext(), CaptureActivity.class));
                        //清空缓存
                        DataCach.clearAllDataCach();

                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }

                return false;
            }
        });
    }

    public void popupmenu(View v) {
        popupMenu.show();
    }

    private void loadLoginMessageCach() {
        //如果缓存中有数据
        if (DataCach.taskMap != null && DataCach.taskMap.size() > 0) {
            Iterator it = DataCach.taskMap.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                HashMap<String, Object> map = DataCach.taskMap.get(key);
                listItem.add(map);
            }
        } else {
            if (DataCach.netType.equals("1")) {//网点入库
                if (pdaLoginMsg != null) {
                    List<PdaNetInfo> netInfoList = pdaLoginMsg.getNetInfoList();
                    if (netInfoList != null && netInfoList.size() > 0) {
                        for (int i = 0; i < netInfoList.size(); i++) {
                            PdaNetInfo pni = netInfoList.get(i);
                            String bankId = pni.getBankId();
                            String bankName = pni.getBankName();
                            String netstatus = pni.getNetTaskStatus();
                            List<PdaNetPersonInfo> personList = pni.getNetPersonInfoList();
                            List<PdaCashboxInfo> cashBoxList = pni.getCashBoxInfoList();

                            int count = 0;
                            for (PdaCashboxInfo cashinfo : cashBoxList) {
                                if (cashinfo.getBankId().equals(bankId)) {
                                    count++;
                                }
                            }
                            HashMap<String, Object> map1 = new HashMap<String, Object>();
                            //判断网点是否已完成
                            if (pni.getNetTaskStatus().equals(Constants.NET_TASK_STATUS_FINISH)) {
                                map1.put("list_img", R.drawable.task_1);// 图像资源的ID
                                map1.put("list_title", pni.getBankName());
                                map1.put("list_position", count);
                                map1.put("list_worktime", "已完成");
                                //建立一個新的對象
                                PdaNetInfo net = new PdaNetInfo();
                                //存儲新的款箱信息
                                List<PdaCashboxInfo> cashList = new ArrayList<PdaCashboxInfo>();
                                //获取當前所有的款箱
                                List<PdaCashboxInfo> cashBoxList2 = pni.getCashBoxInfoList();
                                for (PdaCashboxInfo cashinfo2 : cashBoxList2) {
                                    if (cashinfo2.getBankId().equals(bankId)) {
                                        cashList.add(cashinfo2);
                                    }
                                }
                                net.setBankId(bankId);
                                net.setNetTaskStatus(netstatus);
                                net.setBankName(bankName);
                                net.setCashBoxInfoList(cashList);
                                net.setNetPersonInfoList(personList);
                                map1.put("data", net);

                            } else {
                                map1.put("list_img", R.drawable.task_2);// 图像资源的ID
                                map1.put("list_title", pni.getBankName());
                                map1.put("list_position", count);
                                map1.put("list_worktime", "未完成");
                                //建立一個新的對象
                                PdaNetInfo net = new PdaNetInfo();
                                //存儲新的款箱信息
                                List<PdaCashboxInfo> cashList = new ArrayList<PdaCashboxInfo>();
                                //獲取當前所有的款箱
                                List<PdaCashboxInfo> cashBoxList2 = pni.getCashBoxInfoList();
                                for (PdaCashboxInfo cashinfo2 : cashBoxList2) {
                                    if (cashinfo2.getBankId().equals(bankId)) {
                                        cashList.add(cashinfo2);
                                    }
                                }
                                net.setBankId(bankId);
                                net.setNetTaskStatus(netstatus);
                                net.setBankName(bankName);
                                net.setCashBoxInfoList(cashList);
                                net.setNetPersonInfoList(personList);
                                map1.put("data", net);
                            }
                            DataCach.taskMap.put("" + i, map1);
                            listItem.add(map1);
                        }
                    }
                }
            } else {//网点出库
                List<Extract> extractList = pdaLoginMsg.getExtracts();
                for (int i = 0; i < extractList.size(); i++) {
                    Extract et = extractList.get(i);
                    String bankId = et.getBankId();
                    String bankName = et.getBankName();
                    String netstatus = et.getNetTaskStatus();
                    List<PdaNetPersonInfo> personList = et.getNetPersonInfoList();
                    //List<PdaCashboxInfo> cashBoxList = pni.getCashBoxInfoList();
                    Map<String, String> ExtractBoxsMap = pdaLoginMsg.getAllPdaBoxsMap();
                    List<PdaCashboxInfo> pdaCashboxInfolist = new ArrayList<PdaCashboxInfo>();
                    Set set = ExtractBoxsMap.keySet();
                    if (ExtractBoxsMap != null && ExtractBoxsMap.size() > 0) {
                        for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                            String rfidNum = (String) iterator.next();
                            String value = ExtractBoxsMap.get(rfidNum);
                            String[] tmpList = value.split("&");
                            String boxSn = tmpList[0];
                            String allbankId = tmpList[1];
                            PdaCashboxInfo pdaCashboxInfo = new PdaCashboxInfo();
                            pdaCashboxInfo.setRfidNum(rfidNum);
                            pdaCashboxInfo.setBoxSn(boxSn);
                            pdaCashboxInfo.setBankId(allbankId);
                            pdaCashboxInfolist.add(pdaCashboxInfo);
                        }
                    }
                    int count = 0;
                    HashMap<String, Object> map1 = new HashMap<String, Object>();
                    //判断网点是否已完成
                    if (et.getNetTaskStatus().equals(Constants.NET_TASK_STATUS_FINISH)) {
                        map1.put("list_img", R.drawable.task_1);// 图像资源的ID
                        map1.put("list_title", et.getBankName());
                        map1.put("list_position", count);
                        map1.put("list_worktime", "已完成");
                        //建立一個新的對象
                        PdaNetInfo net = new PdaNetInfo();
                        //存儲新的款箱信息
                        List<PdaCashboxInfo> cashList = new ArrayList<PdaCashboxInfo>();
                        //获取當前所有的款箱
                        List<PdaCashboxInfo> cashBoxList2 = pdaCashboxInfolist;
                        for (PdaCashboxInfo cashinfo2 : cashBoxList2) {
                            if (cashinfo2.getBankId().equals(bankId)) {
                                cashList.add(cashinfo2);
                            }
                        }
                        net.setBankId(bankId);
                        net.setNetTaskStatus(netstatus);
                        net.setBankName(bankName);
                        net.setCashBoxInfoList(cashList);
                        net.setNetPersonInfoList(personList);
                        map1.put("data", net);

                    } else {
                        map1.put("list_img", R.drawable.task_2);// 图像资源的ID
                        map1.put("list_title", et.getBankName());
                        map1.put("list_position", count);
                        map1.put("list_worktime", "未完成");
                        //建立一個新的對象
                        PdaNetInfo net = new PdaNetInfo();
                        //存儲新的款箱信息
                        List<PdaCashboxInfo> cashList = new ArrayList<PdaCashboxInfo>();
                        //獲取當前所有的款箱
                        List<PdaCashboxInfo> cashBoxList2 = pdaCashboxInfolist;
                        for (PdaCashboxInfo cashinfo2 : cashBoxList2) {
                            if (cashinfo2.getBankId().equals(bankId)) {
                                cashList.add(cashinfo2);
                            }
                        }
                        net.setBankId(bankId);
                        net.setNetTaskStatus(netstatus);
                        net.setBankName(bankName);
                        net.setCashBoxInfoList(cashList);
                        net.setNetPersonInfoList(personList);
                        map1.put("data", net);
                    }
                    DataCach.taskMap.put("" + i, map1);
                    listItem.add(map1);
                }
            }
        }

        listItemAdapter.notifyDataSetChanged();
    }

    private void showWaitDialog(String msg) {
        if (pd == null) {
            pd = new ProgressDialog(this);
        }
        pd.setCancelable(false);
        pd.setMessage(msg);
        pd.show();
    }

    private void hideWaitDialog() {
        if (pd != null) {
            pd.cancel();
        }
    }
}
