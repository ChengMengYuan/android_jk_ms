package com.grgbanking.ct.qcode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.grgbanking.ct.R;
import com.grgbanking.ct.cach.DataCach;

/**
 * @author ：     cmy
 * @version :     2016/11/11.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class ScanActivity extends Activity {
    private final static String SCAN_ACTION = "android.intent.ACTION_DECODE_DATA";
//    List<Qcode> qcodes = new ArrayList<>();
    private EditText showScanResult;
    private Button btn;
    private Button mScan;
    private Button mClose;
    private int type;
    private int outPut;
    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid;
    private String barcodeStr;
    private boolean isScaning = false;
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            isScaning = false;
            soundpool.play(soundid, 1, 1, 0, 0, 1);
            showScanResult.setText("");
            mVibrator.vibrate(100);
            byte[] barcode = intent.getByteArrayExtra("barcode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);
            barcodeStr = new String(barcode, 0, barocodelen);

            String barcodeStr = intent.getStringExtra("barcode_string");//直接获取字符串
//            Qcode qcode = new Qcode();
            //            qcode.setQcode(barcodeStr);
            //            qcodes.add(qcode);
//            DataCachcodeMap.put(barcodeStr,"value");
            DataCach.codeMap.put("qcode",barcodeStr);
            showScanResult.setText(barcodeStr);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.sacn_activity);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setupView();
    }

    private void initScan() {
        mScanManager = new ScanManager();
        mScanManager.openScanner();
        mScanManager.switchOutputMode(0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
    }


    private void setupView() {
        // TODO Auto-generated method stub
        showScanResult = (EditText) findViewById(R.id.scan_result);
        btn = (Button) findViewById(R.id.manager);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //                if (mScanManager.getTriggerMode() != Triggering.CONTINUOUS)
                //                    mScanManager.setTriggerMode(Triggering.CONTINUOUS);
            }
        });
        mScan = (Button) findViewById(R.id.scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScanManager.stopDecode();
                isScaning = true;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mScanManager.startDecode();

            }
        });

        mClose = (Button) findViewById(R.id.close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mScanManager.stopDecode();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mScanManager != null) {
            mScanManager.stopDecode();
            isScaning = false;
        }
        unregisterReceiver(mScanReceiver);

        //通知childAdapter更新ListView
        LayoutInflater inflater = LayoutInflater.from(ScanActivity.this);
        final View child = inflater.inflate(R.layout.qcode_list, null);
        ListView childListview = (ListView) child.findViewById(R.id.qcode_list_item);
        childListview.setAdapter(new ChildAdapter(ScanActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initScan();
        showScanResult.setText("");
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(mScanReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
