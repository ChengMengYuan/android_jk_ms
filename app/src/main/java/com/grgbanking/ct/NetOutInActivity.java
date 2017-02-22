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
import com.grgbanking.ct.database.Extract;
import com.grgbanking.ct.database.ExtractBoxs;
import com.grgbanking.ct.database.LoginMan;
import com.grgbanking.ct.database.NetInfo;
import com.grgbanking.ct.database.NetMan;
import com.grgbanking.ct.entity.ConvoyManInfo;
import com.grgbanking.ct.entity.LoginInfo;
import com.grgbanking.ct.entity.LoginUser;
import com.grgbanking.ct.entity.PdaCashboxInfo;
import com.grgbanking.ct.entity.PdaGuardManInfo;
import com.grgbanking.ct.entity.PdaLoginManInfo;
import com.grgbanking.ct.entity.PdaLoginMsg;
import com.grgbanking.ct.entity.PdaNetInfo;
import com.grgbanking.ct.entity.PdaNetPersonInfo;
import com.grgbanking.ct.scan.Recordnet;
import com.grgbanking.ct.scan.Waternet;
import com.grgbanking.ct.utils.FileUtil;
import com.hlct.framework.pda.common.entity.ResultInfo;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: 2017/2/17 加多一个flag字段 0是入库，1是出库 ，并且加以区分 
public class NetOutInActivity extends Activity {

    private static final String TAG = "NetOutInActivity";
    static PdaGuardManInfo guardManInfo = null;//保存押运人员
    static ConvoyManInfo convoyManinfo = null;//保存押运人员
    Button sysOutButton;
    Button netInButton = null;
    Button netOutButton = null;
    Button downButton = null;
    Button upButton = null;
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
    private List<ConvoyManInfo> convoyManInfo = new ArrayList<ConvoyManInfo>();
    private List<NetInfo> netInfos = new ArrayList<NetInfo>();
    private List<LoginInfo> loginInfos = new ArrayList<LoginInfo>();
    private List<PdaCashboxInfo> pdaCashboxInfos = new ArrayList<PdaCashboxInfo>();
    private List<CashBox> cashBoxes = new ArrayList<CashBox>();
    private Context context;
    private ProgressDialog pd = null;

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
        upButton = (Button) findViewById(R.id.up_button);

        //初始化缓存，将缓存清空
        DataCach.clearAllDataCach();
        downButton.setOnClickListener(new OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              showWaitDialog("正在下载，请稍候...");
                                              final LoginUser loginUser = DataCach.loginUser;
                                              List<NameValuePair> params = new ArrayList<NameValuePair>();
                                              params.add(new BasicNameValuePair("login_name", loginUser.getLoginName()));

                                              /** 读取SD卡中的数据*/
                                              ResultInfo resultInfo = new ResultInfo();
                                              resultInfo = (ResultInfo) FileUtil.readString("/sdcard/Download/test.dat");


                                              //                                              访问后台服务器进行登录操作
//                                              new HttpPostUtils(Constants.URL_DOWNINFO, params, new UICallBackDao() {
//                                                  @Override
//                                                  public void callBack(ResultInfo resultInfo) {
//                                                      if (!ResultInfo.CODE_SUCCESS.equals(resultInfo.getCode())) {
//                                                          Toast.makeText(context, resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
//                                                          hideWaitDialog();
//                                                          return;
//                                                      }
//
//
//                                                      JSONObject jsonObject = resultInfo.getJsonObject();
//                                                      PdaLoginMsg pdaLoginMsg = PdaLoginMsg.JSONtoPdaLoginMsg(jsonObject);
//                                                      DataCach.setPdaLoginMsg(pdaLoginMsg);
//
//                                                      DataCach.netType = CODE_GUARDMANIINFO;
//
//                                                      hideWaitDialog();
//                                                      Log.v(TAG, resultInfo.getCode());
//                                                      Log.v(TAG, resultInfo.getMessage());
//                                                      Log.v(TAG, "" + resultInfo.getJsonArray());
//
//                                                      Toast.makeText(context, resultInfo.getMessage(), Toast.LENGTH_LONG).show();
//                                                      //取出押运人员数据
//                                                      List<PdaGuardManInfo> guardManInfoList = pdaLoginMsg.getPdaGuardManInfo();
//                                                      if (guardManInfoList != null && guardManInfoList.size() > 0) {
//                                                          for (PdaGuardManInfo info : guardManInfoList) {
//                                                              ConvoyManInfo manInfo = new ConvoyManInfo();
//                                                              manInfo.setGuardManId(info.getGuardManId());
//                                                              manInfo.setGuardManName(info.getGuardManName());
//                                                              manInfo.setGuardManRFID(info.getGuardManRFID());
//                                                              convoyManInfo.add(manInfo);
//                                                          }
//                                                          //存入数据库
//                                                          DBManager dbmanager = new DBManager(context);
//                                                          try {
//                                                              dbmanager.delete();
//                                                          } catch (Exception e) {
//                                                              Log.e(TAG, "" + e);
//                                                          }
//
//                                                          dbmanager.addConvoyMan(convoyManInfo);
//                                                      }
//                                                      //查询押运人员数据
//                                                      DBManager db = new DBManager(context);
//                                                      ArrayList<ConvoyMan> manList = (ArrayList<ConvoyMan>) db.queryConvoyMan();
//                                                      if (manList != null && manList.size() > 0) {
//                                                          for (ConvoyMan cMan : manList) {
//                                                              Log.i(TAG, "" + cMan.getGuardManId());
//                                                              Log.i(TAG, "" + cMan.getGuardManName());
//                                                              Log.i(TAG, "" + cMan.getGuardManRFID());
//                                                              Log.i(TAG, "-----");
//                                                          }
//                                                      }
//
//
//                                                      //取出网点信息
//                                                      List<PdaNetInfo> netInfoList = pdaLoginMsg.getNetInfoList();
//                                                      if (netInfoList != null && netInfoList.size() > 0) {
//                                                          for (PdaNetInfo info : netInfoList) {
//                                                              NetInfo netInfo = new NetInfo();
//                                                              Extract extract = new Extract();
//                                                              extract.setLineSn(pdaLoginMsg.getLineSn());
//                                                              extract.setBankId(info.getBankId());
//                                                              extract.setNetTaskStatus(info.getNetTaskStatus());
//                                                              extract.setBankName(info.getBankName());
//                                                              extract.setLineId(pdaLoginMsg.getLineId());
//                                                              DBManager extractdb = new DBManager(context);
//                                                              extractdb.addExtract(extract);
//
//                                                              netInfo.setLineSn(pdaLoginMsg.getLineSn());
//                                                              netInfo.setBankId(info.getBankId());
//                                                              netInfo.setNetTaskStatus(info.getNetTaskStatus());
//                                                              netInfo.setBankName(info.getBankName());
//                                                              netInfo.setLineId(pdaLoginMsg.getLineId());
//                                                              netInfo.setNetPersonInfoList(info.getNetPersonInfoList());
//                                                              netInfo.setCashBoxInfoList(info.getCashBoxInfoList());
//                                                              netInfos.add(netInfo);
//                                                          }
//                                                          //存入数据库
//                                                          DBManager manager = new DBManager(context);
//                                                          manager.addNetInfo(netInfos);
//                                                      }
//                                                      //查询网点信息
//                                                      DBManager netInfoDb = new DBManager(context);
//                                                      ArrayList<NetInfo> netInfolist = (ArrayList<NetInfo>) netInfoDb.queryNetInfo();
//                                                      if (netInfolist != null && netInfolist.size() > 0) {
//                                                          Log.i(TAG, "开始打印netInfo=====" + netInfolist.size());
//                                                          for (NetInfo netInfo : netInfolist) {
//                                                              Log.i(TAG, "" + netInfo.getBankId());
//                                                              Log.i(TAG, "" + netInfo.getBankName());
//                                                              Log.i(TAG, "" + netInfo.getNetTaskStatus());
//                                                              Log.i(TAG, "-----");
//                                                          }
//                                                      }
//
//
//                                                      //保存登录人员
//                                                      List<PdaLoginManInfo> pdaLoginManInfoList = pdaLoginMsg.getPdaLoginManInfo();
//                                                      if (pdaLoginManInfoList != null && pdaLoginManInfoList.size() > 0) {
//                                                          for (PdaLoginManInfo info : pdaLoginManInfoList) {
//                                                              LoginInfo loginInfo = new LoginInfo();
//                                                              loginInfo.setLoginId(info.getLoginId());
//                                                              loginInfo.setLogin_name(info.getLogin_name());
//                                                              loginInfo.setPassword(info.getPassword());
//                                                              loginInfo.setFlag(info.getFlag());
//                                                              loginInfo.setLine(info.getLine());
//                                                              loginInfos.add(loginInfo);
//                                                          }
//                                                          //存入数据库
//                                                          DBManager loginDb = new DBManager(context);
//                                                          loginDb.addLoginMan(loginInfos);
//                                                      }
//
//                                                      //保存所有款箱
//                                                      ExtractBoxs extractBoxs = new ExtractBoxs();
//                                                      Map<String, String> ExtractBoxsMap = pdaLoginMsg.getAllPdaBoxsMap();
//                                                      Set set = ExtractBoxsMap.keySet();
//                                                      if (ExtractBoxsMap != null && ExtractBoxsMap.size() > 0) {
//                                                          for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
//                                                              String rfidNum = (String) iterator.next();
//                                                              String value = ExtractBoxsMap.get(rfidNum);
//                                                              String[] tmpList = value.split("&");
//                                                              String bankId = tmpList[1];
//                                                              String boxSn = tmpList[0];
//                                                              extractBoxs.setRfidNum(rfidNum);
//                                                              extractBoxs.setBoxSn(boxSn);
//                                                              extractBoxs.setBankId(bankId);
//                                                              DBManager ExtractBoxsdb = new DBManager(context);
//                                                              ExtractBoxsdb.addExtractBoxs(extractBoxs);
//                                                          }
//                                                      }
//
//
//                                                      //保存pda款箱
//                                                      List<PdaCashboxInfo> pdaCashboxInfoList = pdaLoginMsg.getPdaCashboxInfo();
//                                                      if (pdaCashboxInfoList != null && pdaCashboxInfoList.size() > 0) {
//                                                          for (PdaCashboxInfo info : pdaCashboxInfoList) {
//                                                              CashBox cashBox = new CashBox();
//                                                              cashBox.setBankId(info.getBankId());
//                                                              cashBox.setBoxSn(info.getBoxSn());
//                                                              cashBox.setRfidNum(info.getRfidNum());
//                                                              Log.i("======RFID:========", info.getRfidNum());
//                                                              cashBoxes.add(cashBox);
//                                                          }
//                                                          //                            //存入数据库
//                                                          //                            DBManager cashBoxDB = new DBManager(context);
//                                                          //                            cashBoxDB.addCashBox(cashBoxes);
//                                                      }
//                                                  }
//                                              }
//
//                                              ).execute();
                                          }
                                      }
        );

        upButton.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showWaitDialog("正在上传数据...");
                                            //访问数据库取出数据
                                            DBManager db = new DBManager(context);
                                            //开始组装数据
                                            ResultInfo ri = new ResultInfo();
                                            Map<String, String> dataMap = new HashMap<String, String>();
                                            ArrayList<Recordnet> recordnetList = (ArrayList<Recordnet>) db.queryRecordnet();
                                            if (recordnetList != null && recordnetList.size() > 0) {
                                                for (Recordnet r : recordnetList) {
                                                    int id = r.getId();
                                                    ArrayList<Waternet> waternetList = (ArrayList<Waternet>) db.queryWaternet(id);
                                                    String rightRfidNums = "";
                                                    String missRfidNums = "";
                                                    String errorRfidNums = "";
                                                    if (waternetList != null && waternetList.size() > 0) {
                                                        for (Waternet w : waternetList) {
                                                            String boxid = w.getBoxId();
                                                            String boxStatus = w.getStatus();
                                                            Log.i("==boxid==", boxid);
                                                            Log.i("==boxStatus==", boxStatus);
                                                            if (boxStatus.equals("0")) {
                                                                rightRfidNums = rightRfidNums + boxid + ";";
                                                                Log.i("====rightRfidNums==", rightRfidNums);
                                                            } else if (boxStatus.equals("1")) {
                                                                missRfidNums = missRfidNums + boxid + ";";
                                                            } else {
                                                                errorRfidNums = errorRfidNums + boxid + ";";
                                                            }
                                                        }
                                                    }
                                                    String rightRfidNumsSub = "";
                                                    String missRfidNumsSub = "";
                                                    String errorRfidNumsSub = "";
                                                    if (rightRfidNums.length() > 0) {
                                                        rightRfidNumsSub = rightRfidNums.substring(0, rightRfidNums.length() - 1);
                                                    }
                                                    if (missRfidNums.length() > 0) {
                                                        missRfidNums.substring(0, missRfidNums.length() - 1);
                                                    }
                                                    if (errorRfidNums.length() > 0) {
                                                        errorRfidNumsSub = errorRfidNums.substring(0, errorRfidNums.length() - 1);
                                                    }
                                                    dataMap.put("rightRfidNums", rightRfidNumsSub);
                                                    dataMap.put("missRfidNums", missRfidNumsSub);
                                                    dataMap.put("errorRfidNums", errorRfidNumsSub);
                                                    dataMap.put("lineId", r.getLineId());
                                                    r.getLineSn();
                                                    r.getScanningDate();
                                                    dataMap.put("netPersonName", r.getBankman());
                                                    Log.i("====getBankman==", r.getBankman());
                                                    dataMap.put("guardPersonName", r.getGuardman());

                                                    dataMap.put("scanStatus", r.getScanStatus());
                                                    dataMap.put("note", r.getNote());
                                                    r.getBankId();
                                                    dataMap.put("netPersonId", r.getBankmanId());
                                                    Log.i("====netPersonId==", r.getBankmanId());
                                                    dataMap.put("guradPersonId", r.getGuardmanId());
                                                    dataMap.put("netId", r.getBankId());
                                                    dataMap.put("scanningDate", r.getScanningDate());
                                                    dataMap.put("scanningType", r.getLineType());

//                                                    JSONObject jsonObject = new JSONObject(dataMap);
//                                                    String data = jsonObject.toString();
//                                                    ri.setCode(ri.CODE_SUCCESS);
//                                                    ri.setText(data);
//                                                    List<NameValuePair> params = new ArrayList<NameValuePair>();
//                                                    params.add(new BasicNameValuePair("param", ri.getText()));
//                                                    new HttpPostUtils(Constants.URL_NET_IN_COMMIT, params, new UICallBackDao() {
//                                                        @Override
//                                                        public void callBack(ResultInfo resultInfo) {
//                                                            if (resultInfo.getCode().equals(resultInfo.CODE_ERROR)) {
//                                                                hideWaitDialog();
//                                                                Toast.makeText(context, resultInfo.getMessage(), Toast.LENGTH_LONG).show();
//                                                            } else {
//                                                                hideWaitDialog();
//                                                                Toast.makeText(context, resultInfo.getMessage(), Toast.LENGTH_LONG).show();
//                                                            }
//                                                        }
//                                                    }).execute();
                                                }
                                            }
                                            hideWaitDialog();
                                        }
                                    }
        );

        netInButton.setOnClickListener(new OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               showWaitDialog("正在加载网点入库信息...");
                                               LoginUser loginUser = DataCach.loginUser;
                                               //判断是出库还是入库
                                               DataCach.netType = Constants.NET_COMMIT_TYPE_IN;

                                               List<NameValuePair> params = new ArrayList<NameValuePair>();
                                               params.add(new BasicNameValuePair("login_name", loginUser.getLoginName()));
                                               params.add(new BasicNameValuePair("scanning_type", Constants.LOGIN_NET_IN));
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
                                               Map<String, String> pdaCashboxInfoMap = new HashMap<String, String>();
                                               List<CashBox> cashBoxes = db.queryCashBox();
                                               List<PdaCashboxInfo> pdaCashboxInfoList = new ArrayList<PdaCashboxInfo>();
                                               if (cashBoxes != null && cashBoxes.size() > 0) {
                                                   for (CashBox info : cashBoxes) {
                                                       PdaCashboxInfo pdaCashboxInfo = new PdaCashboxInfo();
                                                       pdaCashboxInfo.setBankId(info.getBankId());
                                                       pdaCashboxInfo.setBoxSn(info.getBoxSn());
                                                       pdaCashboxInfo.setRfidNum(info.getRfidNum());
                                                       pdaCashboxInfoList.add(pdaCashboxInfo);
                                                       pdaCashboxInfoMap.put(info.getRfidNum(), info.getBoxSn());
                                                   }
                                               }

                                               //取出网点信息
                                               List<NetInfo> netInfo = db.queryNetInfo();
                                               List<PdaNetInfo> pdaNetInfoList = new ArrayList<PdaNetInfo>();
                                               //存入pdaLoginMessage
                                               // TODO: 2017/2/17 添加对应的flag判断是出库还是入库
                                               if (netInfo != null && netInfo.size() > 0) {
                                                   for (NetInfo info : netInfo) {
                                                       PdaNetInfo pdaNetInfo = new PdaNetInfo();
                                                       pdaNetInfo.setBankId(info.getBankId());
                                                       pdaNetInfo.setNetTaskStatus(info.getNetTaskStatus());
                                                       pdaNetInfo.setBankName(info.getBankName());
                                                       pdaNetInfo.setLineId(info.getLineId());
                                                       pdaNetInfo.setCashBoxInfoList(pdaCashboxInfoList);
                                                       pdaNetInfo.setNetPersonInfoList(pdaNetPersonInfoList);
                                                       pdaNetInfoList.add(pdaNetInfo);
                                                       pdaLoginMsg.setLineId(info.getLineId());
                                                       pdaLoginMsg.setLineSn(info.getLineSn());
                                                   }
                                               }
                                               pdaLoginMsg.setNetInfoList(pdaNetInfoList);
                                               pdaLoginMsg.setAllPdaBoxsMap(pdaCashboxInfoMap);


                                               //取出登录人员

                                               List<LoginMan> loginMan = db.queryLoginMan();
                                               List<PdaLoginManInfo> pdaLoginManInfoList = new ArrayList<PdaLoginManInfo>();
                                               if (loginMan != null && loginMan.size() > 0) {
                                                   for (LoginMan info : loginMan) {
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


                                               // 传递 pdaLoginMsg
                                               hideWaitDialog();
                                               Intent intent = new Intent(NetOutInActivity.this, MainActivity.class);
                                               intent.putExtra("pdaLoginMsg", pdaLoginMsg);
                                               startActivity(intent);


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
                                                showWaitDialog("正在加载网点出库信息");
                                                LoginUser loginUser = DataCach.loginUser;
                                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                                params.add(new BasicNameValuePair("login_name", loginUser.getLoginName()));
                                                params.add(new BasicNameValuePair("scanning_type", Constants.LOGIN_NET_IN));
                                                //判断是出库还是入库
                                                DataCach.netType = Constants.NET_COMMIT_TYPE_OUT;
                                                //访问数据库
                                                DBManager db = new DBManager(context);
                                                PdaLoginMsg pdaLoginMsg = new PdaLoginMsg();
                                                try {
                                                    //取出所有款箱
                                                    ArrayList<ExtractBoxs> ExtractBoxsList = (ArrayList<ExtractBoxs>) db.queryExtractBoxs();
                                                    Map<String, String> ExtractBoxsmap = new HashMap<String, String>();
                                                    for (ExtractBoxs ExtractBox : ExtractBoxsList) {
                                                        ExtractBox.getRfidNum();
                                                        ExtractBox.getBankId();
                                                        ExtractBox.getBoxSn();
                                                        ExtractBoxsmap.put(ExtractBox.getRfidNum(), ExtractBox.getBoxSn() + "&" + ExtractBox.getBankId());
                                                    }
                                                    pdaLoginMsg.setAllPdaBoxsMap(ExtractBoxsmap);
                                                } catch (Exception e) {
                                                    Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                                                }

                                                try {
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
                                                } catch (Exception e) {
                                                    Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                                                }


                                                try {
                                                    //取出所有网点信息
                                                    List<PdaNetPersonInfo> netPersonInfoList = new ArrayList<PdaNetPersonInfo>();
                                                    List<Extract> extractList = db.queryExtract();
                                                    for (Extract info : extractList) {
                                                        List<NetMan> netMens = db.queryNetManByBankId(info.getBankId());
                                                        for (NetMan net : netMens) {
                                                            PdaNetPersonInfo pdaNetPersonInfo = new PdaNetPersonInfo();
                                                            pdaNetPersonInfo.setNetPersonId(net.getNetPersonId());
                                                            pdaNetPersonInfo.setNetPersonName(net.getNetPersonName());
                                                            pdaNetPersonInfo.setNetPersonRFID(net.getNetPersonRFID());
                                                            netPersonInfoList.add(pdaNetPersonInfo);
                                                        }
                                                        info.setNetPersonInfoList(netPersonInfoList);
                                                    }
                                                    pdaLoginMsg.setExtracts(extractList);
                                                } catch (Exception e) {
                                                    Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                                                }


                                                try {

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
                                                } catch (Exception e) {
                                                    Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                                                }


                                                hideWaitDialog();
                                                // 传递 pdaLoginMsg
                                                Intent intent = new Intent(NetOutInActivity.this, MainActivity.class);
                                                intent.putExtra("pdaLoginMsg", pdaLoginMsg);
                                                startActivity(intent);
                                            }
                                        }

        );
    }

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

}
