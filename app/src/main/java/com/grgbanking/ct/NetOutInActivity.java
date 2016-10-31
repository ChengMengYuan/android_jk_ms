package com.grgbanking.ct;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.grgbanking.ct.cach.DataCach;
import com.grgbanking.ct.database.CashBox;
import com.grgbanking.ct.database.ConvoyMan;
import com.grgbanking.ct.database.DBManager;
import com.grgbanking.ct.database.LoginMan;
import com.grgbanking.ct.database.NetInfo;
import com.grgbanking.ct.database.NetMan;
import com.grgbanking.ct.entity.ConvoyManInfo;
import com.grgbanking.ct.entity.LoginInfo;
import com.grgbanking.ct.entity.LoginUser;
import com.grgbanking.ct.entity.PdaCashboxInfo;
import com.grgbanking.ct.entity.PdaGuardManInfo;
import com.grgbanking.ct.entity.PdaLoginManInfo;
import com.grgbanking.ct.entity.PdaLoginMessage;
import com.grgbanking.ct.entity.PdaLoginMsg;
import com.grgbanking.ct.entity.PdaNetInfo;
import com.grgbanking.ct.entity.PdaNetPersonInfo;
import com.grgbanking.ct.http.HttpPostUtils;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.http.UICallBackDao;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.grgbanking.ct.http.ResultInfo.CODE_GUARDMANIINFO;

public class NetOutInActivity extends Activity {

    private static final String TAG = "NetOutInActivity";

    private List<ConvoyManInfo> convoyManInfo = new ArrayList<ConvoyManInfo>();
    private List<NetInfo> netInfos = new ArrayList<NetInfo>();
    private List<LoginInfo> loginInfos = new ArrayList<LoginInfo>();
    private List<PdaCashboxInfo> pdaCashboxInfos = new ArrayList<PdaCashboxInfo>();
    private List<CashBox> cashBoxes = new ArrayList<CashBox>();

    private Context context;
    Button sysOutButton;
    Button netInButton = null;
    Button netOutButton = null;
    Button downButton = null;

    static PdaGuardManInfo guardManInfo = null;//保存押运人员
    static ConvoyManInfo convoyManinfo = null;//保存押运人员

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        //		startService(new Intent(context, GrgbankService.class));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.netoutin);


        sysOutButton = (Button) findViewById(R.id.net_sysout_view);
        sysOutButton.setOnClickListener(saomiaoButtonclick);

        netInButton = (Button) findViewById(R.id.peixiang_button);
        netOutButton = (Button) findViewById(R.id.net_out_button);

        downButton = (Button) findViewById(R.id.down_button);

        //初始化缓存，将缓存清空
        DataCach.clearAllDataCach();
        downButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog("正在下载，请稍候...");
                final LoginUser loginUser = DataCach.loginUser;
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("login_name", loginUser.getLoginName()));
                //访问后台服务器进行登录操作
                new HttpPostUtils(Constants.URL_DOWNINFO, params, new UICallBackDao() {
                    @Override
                    public void callBack(ResultInfo resultInfo) {
                        if (!ResultInfo.CODE_SUCCESS.equals(resultInfo.getCode())) {
                            Toast.makeText(context, resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            hideWaitDialog();
                            return;
                        }

                        JSONObject jsonObject = resultInfo.getJsonObject();
                        PdaLoginMsg pdaLoginMsg = PdaLoginMsg.JSONtoPdaLoginMsg(jsonObject);
                        DataCach.setPdaLoginMsg(pdaLoginMsg);

                        DataCach.netType = CODE_GUARDMANIINFO;

                        hideWaitDialog();
                        Log.v(TAG, resultInfo.getCode());
                        Log.v(TAG, resultInfo.getMessage());
                        Log.v(TAG, "" + resultInfo.getJsonArray());

                        Toast.makeText(context, resultInfo.getMessage(), Toast.LENGTH_LONG).show();
                        //取出押运人员数据
                        List<PdaGuardManInfo> guardManInfoList = pdaLoginMsg.getPdaGuardManInfo();
                        if (guardManInfoList != null && guardManInfoList.size() > 0) {
                            for (PdaGuardManInfo info : guardManInfoList) {
                                ConvoyManInfo manInfo = new ConvoyManInfo();
                                manInfo.setGuardManId(info.getGuardManId());
                                manInfo.setGuardManName(info.getGuardManName());
                                manInfo.setGuardManRFID(info.getGuardManRFID());
                                convoyManInfo.add(manInfo);
                            }
                            //存入数据库
                            DBManager dbmanager = new DBManager(context);
                            dbmanager.delete();
                            dbmanager.addConvoyMan(convoyManInfo);
                        }
                        //查询押运人员数据
                        DBManager db = new DBManager(context);
                        ArrayList<ConvoyMan> manList = (ArrayList<ConvoyMan>) db.queryConvoyMan();
                        if (manList != null && manList.size() > 0) {
                            Log.i(TAG, "开始打印cMan=====");
                            for (ConvoyMan cMan : manList) {
                                Log.i(TAG, "" + cMan.getGuardManId());
                                Log.i(TAG, "" + cMan.getGuardManName());
                                Log.i(TAG, "" + cMan.getGuardManRFID());
                                Log.i(TAG, "-----");
                            }
                        }


                        //取出网点信息
                        List<PdaNetInfo> netInfoList = pdaLoginMsg.getNetInfoList();
                        if (netInfoList != null && netInfoList.size() > 0) {
                            for (PdaNetInfo info : netInfoList) {
                                NetInfo netInfo = new NetInfo();
                                netInfo.setBankId(info.getBankId());
                                netInfo.setNetTaskStatus(info.getNetTaskStatus());
                                netInfo.setBankName(info.getBankName());
                                netInfo.setNetPersonInfoList(info.getNetPersonInfoList());
                                netInfo.setCashBoxInfoList(info.getCashBoxInfoList());
                                netInfos.add(netInfo);
                            }
                            //存入数据库
                            DBManager manager = new DBManager(context);
                            manager.addNetInfo(netInfos);
                        }
                        //查询网点信息
                        DBManager netInfoDb = new DBManager(context);
                        ArrayList<NetInfo> netInfolist = (ArrayList<NetInfo>) netInfoDb.queryNetInfo();
                        if (netInfolist != null && netInfolist.size() > 0) {
                            Log.i(TAG, "开始打印netInfo=====" + netInfolist.size());
                            for (NetInfo netInfo : netInfolist) {
                                Log.i(TAG, "" + netInfo.getBankId());
                                Log.i(TAG, "" + netInfo.getBankName());
                                Log.i(TAG, "" + netInfo.getNetTaskStatus());
                                Log.i(TAG, "-----");
                            }
                        }



                        //保存登录人员
                        List<PdaLoginManInfo> pdaLoginManInfoList = pdaLoginMsg.getPdaLoginManInfo();
                        if (pdaLoginManInfoList != null && pdaLoginManInfoList.size() > 0) {
                            for (PdaLoginManInfo info : pdaLoginManInfoList) {
                                LoginInfo loginInfo = new LoginInfo();
                                loginInfo.setLoginId(info.getLoginId());
                                loginInfo.setLogin_name(info.getLogin_name());
                                loginInfo.setPassword(info.getPassword());
                                loginInfo.setFlag(info.getFlag());
                                loginInfo.setLine(info.getLine());
                                loginInfos.add(loginInfo);
                            }
                            //存入数据库
                            DBManager loginDb = new DBManager(context);
                            loginDb.addLoginMan(loginInfos);
                        }


                        //保存所有款箱
                        List<PdaCashboxInfo> pdaCashboxInfoList = pdaLoginMsg.getPdaCashboxInfo();
                        if (pdaCashboxInfoList != null && pdaCashboxInfoList.size() > 0) {
                            for (PdaCashboxInfo info : pdaCashboxInfoList) {
                                CashBox cashBox = new CashBox();
                                cashBox.setBankId(info.getBankId());
                                cashBox.setBoxSn(info.getBoxSn());
                                cashBox.setRfidNum(info.getRfidNum());
                                Log.i("======RFID:========",info.getRfidNum());
                                cashBoxes.add(cashBox);
                            }
//                            //存入数据库
//                            DBManager cashBoxDB = new DBManager(context);
//                            cashBoxDB.addCashBox(cashBoxes);
                        }
                    }
                }

                ).execute();
            }
        }
        );


        netInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog("正在加载网点入库信息...");
                LoginUser loginUser = DataCach.loginUser;
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("login_name", loginUser.getLoginName()));
                params.add(new BasicNameValuePair("scanning_type", Constants.LOGIN_NET_IN));
                // TODO: 2016/10/27  访问数据库组装数据
                //访问数据库
                DBManager db = new DBManager(context);
                PdaLoginMsg pdaLoginMsg = new PdaLoginMsg();
                //取出 押运人员
                ArrayList<ConvoyMan> manList = (ArrayList<ConvoyMan>) db.queryConvoyMan();
                List<PdaGuardManInfo> pdaGuarManInfoList = new ArrayList<PdaGuardManInfo>();
                //存入pdaLoginMessage
                if (manList != null && manList.size() > 0) {
                    for (ConvoyMan cMan : manList) {
                        PdaGuardManInfo manInfo = new PdaGuardManInfo();
                        manInfo.setGuardManId(cMan.getGuardManId());
                        manInfo.setGuardManName(cMan.getGuardManName());
                        manInfo.setGuardManRFID(cMan.getGuardManRFID());
                        pdaGuarManInfoList.add(manInfo);
                    }
                    pdaLoginMsg.setPdaGuardManInfo(pdaGuarManInfoList);
                }
                //取出网点人员
                List<NetMan> netMen = db.queryNetMan();
                List<PdaNetPersonInfo> pdaNetPersonInfoList = new ArrayList<PdaNetPersonInfo>();
                if (netMen != null && netMen.size() > 0) {
                    for (NetMan info : netMen) {
                        PdaNetPersonInfo pdaNetPersonInfo = new PdaNetPersonInfo();
                        pdaNetPersonInfo.setNetPersonId(info.getNetPersonId());
                        pdaNetPersonInfo.setNetPersonName(info.getNetPersonName());
                        pdaNetPersonInfo.setNetPersonRFID(info.getNetPersonRFID());
                        pdaNetPersonInfoList.add(pdaNetPersonInfo);
                    }
                }
                //取出所有款箱
                List<CashBox> cashBoxes = db.queryCashBox();
                List<PdaCashboxInfo> pdaCashboxInfoList = new ArrayList<PdaCashboxInfo>();
                if (cashBoxes!=null&&cashBoxes.size()>0){
                    for (CashBox info : cashBoxes){
                        PdaCashboxInfo pdaCashboxInfo = new PdaCashboxInfo();
                        pdaCashboxInfo.setBankId(info.getBankId());
                        pdaCashboxInfo.setBoxSn(info.getBoxSn());
                        pdaCashboxInfo.setRfidNum(info.getRfidNum());
                        pdaCashboxInfoList.add(pdaCashboxInfo);
                    }
                }

                //取出网点信息
                List<NetInfo> netInfo = db.queryNetInfo();
                List<PdaNetInfo> pdaNetInfoList = new ArrayList<PdaNetInfo>();
                //存入pdaLoginMessage
                if (netInfo != null && netInfo.size() > 0) {
                    for (NetInfo info : netInfo) {
                        PdaNetInfo pdaNetInfo = new PdaNetInfo();
                        pdaNetInfo.setBankId(info.getBankId());
                        pdaNetInfo.setNetTaskStatus(info.getNetTaskStatus());
                        pdaNetInfo.setBankName(info.getBankName());
                        pdaNetInfo.setCashBoxInfoList(pdaCashboxInfoList);
                        pdaNetInfo.setNetPersonInfoList(pdaNetPersonInfoList);
                        pdaNetInfoList.add(pdaNetInfo);
                    }
                }
                pdaLoginMsg.setNetInfoList(pdaNetInfoList);


                // TODO: 2016/10/28  取出登录人员

                List<LoginMan> loginMan = db.queryLoginMan();
                List<PdaLoginManInfo> pdaLoginManInfoList = new ArrayList<PdaLoginManInfo>();
                if (loginMan!=null&&loginMan.size()>0){
                    for (LoginMan info : loginMan){
                        PdaLoginManInfo pdaLoginManInfo = new PdaLoginManInfo();
                        pdaLoginManInfo.setLogin_name(info.getLogin_name());
                        pdaLoginManInfo.setLoginId(info.getLoginId());
                        pdaLoginManInfo.setPassword(info.getPassword());
                        pdaLoginManInfo.setLine(info.getLine());
                        pdaLoginManInfo.setFlag(info.getFlag());
                        pdaLoginManInfoList.add(pdaLoginManInfo);
                    }
                    pdaLoginMsg.setPdaLoginManInfo(pdaLoginManInfoList);
                }

                // TODO: 2016/10/31 传递 pdaLoginMsg
                hideWaitDialog();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("Parcelable",pdaLoginMsg);
                intent.putExtras(bundle);
                intent.setClass(NetOutInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

                //


//                //访问后台服务器进行登录操作
//                new HttpPostUtils(Constants.URL_NET_OUTIN, params, new UICallBackDao() {
//                    @Override
//                    public void callBack(ResultInfo resultInfo) {
//                        if (!ResultInfo.CODE_SUCCESS.equals(resultInfo.getCode())) {
//                            Toast.makeText(context, resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
//                            hideWaitDialog();
//                            return;
//                        }
//
//                        JSONObject jsonObject = resultInfo.getJsonObject();
//                        PdaLoginMessage pdaLoginMessage = PdaLoginMessage.JSONtoPdaLoginMessage(jsonObject);
//                        DataCach.setPdaLoginMessage(pdaLoginMessage);
//                        DataCach.netType = Constants.NET_COMMIT_TYPE_IN;
//
//                        hideWaitDialog();
//
//                        Intent intent = new Intent();
//                        intent.setClass(NetOutInActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }).execute();
            }
        }

        );

        netOutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                showWaitDialog("正在加载网点出库信息...");
                LoginUser loginUser = DataCach.loginUser;

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("login_name", loginUser.getLoginName()));
                params.add(new BasicNameValuePair("scanning_type", Constants.LOGIN_NET_OUT));

                //访问后台服务器进行登录操作
                new HttpPostUtils(Constants.URL_NET_OUTIN, params, new UICallBackDao() {
                    @Override
                    public void callBack(ResultInfo resultInfo) {
                        if (!ResultInfo.CODE_SUCCESS.equals(resultInfo.getCode())) {
                            Toast.makeText(context, resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            hideWaitDialog();
                            return;
                        }

                        JSONObject jsonObject = resultInfo.getJsonObject();
                        PdaLoginMessage pdaLoginMessage = PdaLoginMessage.JSONtoPdaLoginMessage(jsonObject);
                        DataCach.setPdaLoginMessage(pdaLoginMessage);

                        DataCach.netType = Constants.NET_COMMIT_TYPE_OUT;

                        hideWaitDialog();

                        Intent intent = new Intent();
                        intent.setClass(NetOutInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).execute();
            }
        }

        );
    }


    private ProgressDialog pd = null;

    /**
     * 开始Dialog 请传入显示的字符
     *
     * @param msg
     */

    private void showWaitDialog(String msg) {
        if (pd == null) {
            pd = new ProgressDialog(this);
        }
        pd.setCancelable(false);
        pd.setMessage(msg);
        pd.show();
    }

    /**
     * 结束Dialog
     */
    private void hideWaitDialog() {
        if (pd != null) {
            pd.cancel();
        }
    }

    OnClickListener saomiaoButtonclick = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            //清空缓存
            DataCach.clearAllDataCach();

            Intent intent = new Intent();
            intent.setClass(NetOutInActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };

}
