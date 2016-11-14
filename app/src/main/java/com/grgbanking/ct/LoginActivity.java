package com.grgbanking.ct;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ct.cach.DataCach;
import com.grgbanking.ct.database.DBManager;
import com.grgbanking.ct.database.Person;
import com.grgbanking.ct.entity.LoginUser;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.update.CheckUpdateInfos;
import com.grgbanking.ct.utils.IntenetUtil;
import com.grgbanking.ct.utils.StringTools;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.grgbanking.ct.utils.IntenetUtil.NETWORN_NONE;
import static com.grgbanking.ct.utils.IntenetUtil.NETWORN_WIFI;

public class LoginActivity extends Activity {
    private static String TAG = "LoginActivity";

    private Button loginButtonView;
    private EditText loginNameView;
    private EditText passwordView;
    private CheckBox remPasswordView;

    String flag;//登录的状态
    String loginNameViewValue = null; //UI控件内容
    String passwordViewValue = null;//UI控件内容
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    String userId = null; //登录成功后的用户ID
    String userName = null;//登录成功后的用户姓名
    int network;//网络连接状态
    private Context context;
    private Person person;
    TextView detail_branch_name;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        //		startService(new Intent(context, GrgbankService.class));


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);

        findViewById();
        initCach();


        //登录操作
        loginButtonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginNameViewValue = loginNameView.getText().toString();
                passwordViewValue = passwordView.getText().toString();

                if (StringTools.isEmpty(loginNameViewValue) || StringTools.isEmpty(passwordViewValue)) {
                    Log.v("V1", "用户名或密码为空");
                    Toast.makeText(context, "用户名或密码不能为空！", Toast.LENGTH_LONG).show();
                    return;
                }

                loginButtonView.setText("正在登录中...");
                loginButtonView.setEnabled(false);


                //            	params.add(new BasicNameValuePair("loginName",loginNameViewValue));
                //            	params.add(new BasicNameValuePair("password",passwordViewValue));
                params.add(new BasicNameValuePair("login_name", loginNameViewValue));
                params.add(new BasicNameValuePair("login_password", passwordViewValue));


                network = IntenetUtil.getNetworkState(context);
                switch (network) {
                    case NETWORN_NONE: {
                        databaseLogin();

                        break;
                    }
                    case NETWORN_WIFI: {
                        //		/** 检测是否要更新  */
                        //		checkUpdate();
                        //
                        //		判断是否记住密码，如果有记住用户名密码，将自动将用户名密码控件内容自动填充
                        //        person= PersonTableHelper.queryEntity(this);
                        //        if(person==null){
                        //        	person=new Person();
                        //        }
                        //        if("1".equals(person.getSelected())){
                        //        	remPasswordView.setChecked(true);
                        //        	loginNameView.setText(person.getLogin_name());
                        //        	passwordView.setText(person.getPassword());
                        //        }

                        wifiLogin();
                        break;
                    }
                }


            }
        });
    }

    /**
     * 无网络连接的情况下，访问数据库进行登录操作
     */
    private void databaseLogin() {
        //访问数据库进行操作
        try {
            DBManager db = new DBManager(context);
            flag = db.queryLogin(loginNameViewValue, passwordViewValue);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "帐号密码有误,请重新输入", Toast.LENGTH_SHORT).show();
        }

        //押运人员
        if (flag.equals(ResultInfo.CODE_GUARDMANIINFO)) {
            success();
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, NetOutInActivity.class);
            startActivity(intent);
            finish();
        }
        //配箱人员
        else if (flag.equals(ResultInfo.CODE_PEIXIANG)) {
            success();
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, PeixiangActivity.class);
            startActivity(intent);
            finish();
        } else {
            //帐号密码错误
            Toast.makeText(context, "帐号密码有误,请重新输入", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 使用wifi连接的情况下，访问后台服务器进行登录操作
     */
    private void wifiLogin() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, PeixiangActivity.class);
        startActivity(intent);
        finish();
    }
//    private void wifiLogin() {
//        //访问后台服务器进行登录操作
//        new HttpPostUtils(Constants.URL_PDA_LOGIN, params, new UICallBackDao() {
//            @Override
//            public void callBack(ResultInfo resultInfo) {
//                Log.i(TAG, "use wifi to logining");
//                if (resultInfo.getCode() != null && !resultInfo.getCode().isEmpty()) {
//                    //押运人员
//                    if (ResultInfo.CODE_GUARDMANIINFO.equals(resultInfo.getCode())) {
//                        success();
//                        Intent intent = new Intent();
//                        intent.setClass(LoginActivity.this, NetOutInActivity.class);
//                        startActivity(intent);
//                        finish();
//                        //配箱人员
//                    } else if (ResultInfo.CODE_PEIXIANG.equals(resultInfo.getCode())) {
//                        success();
//                        Intent intent = new Intent();
//                        intent.setClass(LoginActivity.this, PeixiangActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                    //帐号密码错误
//                    else {
//                        Toast.makeText(context, resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
//                        loginButtonView.setText("登录");
//                    }
//                } else {
//                    Toast.makeText(context, resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
//                    loginButtonView.setText("登录");
//                }
//            }
//        }).execute();
//    }

    /*
     * findViewById.
     */
    public void findViewById() {
        remPasswordView = (CheckBox) this.findViewById(R.id.cb);
        loginNameView = (EditText) this.findViewById(R.id.username_edit);
        passwordView = (EditText) this.findViewById(R.id.password_edit);
        loginButtonView = (Button) this.findViewById(R.id.login_button);
        detail_branch_name = (TextView) this.findViewById(R.id.detail_branch_name);
    }

    /*
     *初始化缓存，将缓存清空
     */
    public void initCach() {
        DataCach.setPdaLoginMessage(null);
    }

    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message arg0) {
            switch (arg0.what) {
                case 0:
                    showUpdataDialog(arg0.getData().getString("address"));
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    /**
     * 弹出更新提示框  ,如果取消更新，刚退出APP
     */
    AlertDialog.Builder update_version;

    void showUpdataDialog(final String url) {
        /** 当发现没有版本更新，则允许登录，否则 继续不可操作  */
        loginButtonView.setEnabled(url == null ? true : false);
        /**显示当前版本 */
        //		try {
        //			detail_branch_name.setText("版本号:v"+getPackageManager().getPackageInfo(context.getPackageName(),0).versionName);
        //		} catch (NameNotFoundException e) {
        //			e.printStackTrace();
        //		}
        detail_branch_name.setText("版本号:v1.0");
        if (url == null) {
            return;
        }
        update_version = new Builder(this);
        update_version.setTitle("版本升级");
        update_version.setMessage("有新的版本需要升级");
        update_version.setNegativeButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                downLoadFile(url);
                showWaitDialog();
            }
        });
        update_version.setNeutralButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        update_version.create().show();
    }

    /**
     * 检测是否要更新
     */
    void checkUpdate() {
        /** 开始检测更新，登录按钮不可操作  */
        loginButtonView.setEnabled(false);
        detail_branch_name.setText("正在检测更新");

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String add = CheckUpdateInfos.getUpdataInfoJSON(LoginActivity.this);
                    Message msg = new Message();
                    Bundle b = new Bundle();
                    b.putString("address", add);
                    msg.what = 0;
                    msg.setData(b);
                    handler.sendMessage(msg);
                    Log.v("tag", "服务器新版本下载地址： " + add);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
    }

    ProgressDialog pd;

    void showWaitDialog() {
        if (pd == null) {
            pd = new ProgressDialog(this);
            pd.setCancelable(false);
            pd.setMessage("正在下载更新，请稍后...");
        }
        pd.show();
    }

    void cancelWaitDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    /**
     * 判断新版本的URL是否为空，如果不为空，说明有新版本，提示用户更新中
     */
    boolean isDown = false;
    long downLoadId = -1;

    @SuppressLint("SdCardPath")
    void downLoadFile(String url) {
        if (!isDown && url != null) {
            isDown = true;
            String path = "/sdcard/ct/ct.apk";
            File appFile = new File(path);
            if (appFile.exists()) {
                appFile.delete();
            }
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDestinationInExternalPublicDir("ct", "ct.apk");
            request.setTitle("MeiLiShuo");
            request.setDescription("MeiLiShuo desc");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            //  request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setMimeType("application/cn.trinea.download.file");
            downLoadId = downloadManager.enqueue(request);
            Log.v("tag", "下载。。。。 。ID 》 " + downLoadId);
            /** 张总，这是设置下载完成的监听  ，下载完成后，回调receiver */
            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("SdCardPath")
        @Override
        public void onReceive(Context context, Intent intent) {
            /** 判断 是否是当前操作的下载ID，有可能是其它应用下载成功的回调*/
            if (downLoadId == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)) {
                String path = "/sdcard/ct/ct.apk";
                File appFile = new File(path);
                if (appFile.exists()) {
                    cancelWaitDialog();
                    installApk(appFile);
                }
            }
        }
    };

    protected void installApk(File file) {
        Intent intent = new Intent();        //执行动作
        intent.setAction(Intent.ACTION_VIEW);        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void success() {
        LoginUser loginUser = DataCach.loginUser;
        loginUser.setLoginName(loginNameViewValue);
        loginUser.setPassword(passwordViewValue);
        loginButtonView.setText("登录");
        loginButtonView.setEnabled(true);
        Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
    }
}
