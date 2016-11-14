package com.grgbanking.ct;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.grgbanking.ct.qcode.QcodeActivity;

/**
 * Created by Administrator on 2016/7/13.
 */


public class PeixiangActivity extends Activity {

    private Button PxButton;
    private Button Pxback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peixiang);

        PxButton = (Button) findViewById(R.id.peixiang_button);
        Pxback = (Button) findViewById(R.id.net_sysout_view);

        PxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(PeixiangActivity.this,QcodeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Pxback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

    }


}
