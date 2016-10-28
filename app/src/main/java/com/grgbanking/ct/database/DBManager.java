package com.grgbanking.ct.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.grgbanking.ct.entity.ConvoyManInfo;
import com.grgbanking.ct.entity.LoginInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cmy on 2016/10/13.
 * emil ：mengyuan.cheng.mier@gmail.com
 */

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context, db);
        //        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        //        db = helper.getWritableDatabase();
    }

    /**
     * add convoyMan
     *
     * @param convoyMen
     */
    public void addConvoyMan(List<ConvoyManInfo> convoyMen) {

        try {
            ContentValues values = new ContentValues();
            for (ConvoyManInfo convoyMan : convoyMen) {
                db = helper.getWritableDatabase();
                //                db.beginTransaction();  //开启事务
                values.put("guardManId", convoyMan.getGuardManId());
                values.put("guardManName", convoyMan.getGuardManName());
                values.put("guardManRFID", convoyMan.getGuardManRFID());
                db.insert(DBHelper.TABLE_ConvoyMan_NAME, null, values);
                //                db.setTransactionSuccessful();//设置事物成功完成
                db.close();
            }
        } finally {
            //            db.endTransaction();//结束事物
        }
    }

    /**
     * add netMan
     *
     * @param netMen
     */
    public void addNetMan(List<NetMan> netMen) {
        //        db.beginTransaction();//开始事物
        try {
            ContentValues values = new ContentValues();
            for (NetMan netMan : netMen) {
                db = helper.getWritableDatabase();
                values.put("netPersonId", netMan.getNetPersonId());
                values.put("bankId", netMan.getBankId());
                values.put("netPersonName", netMan.getNetPersonName());
                values.put("netPersonRFID", netMan.getNetPersonRFID());
                db.insert(DBHelper.TABLE_NetMan_NAME, null, values);
                db.close();
            }
            //            db.setTransactionSuccessful();//设置事物成功完成
        } finally {
            //            db.endTransaction();//结束事物
        }
    }

    /**
     * add LoginMan
     *
     * @param loginMen
     */
    public void addLoginMan(List<LoginInfo> loginMen) {
        try {
            ContentValues values = new ContentValues();
            for (LoginInfo loginMan : loginMen) {
                db = helper.getWritableDatabase();
                values.put("loginId", loginMan.getLoginId());
                values.put("loginName", loginMan.getLogin_name());
                values.put("password", loginMan.getPassword());
                values.put("flag", loginMan.getFlag());
                values.put("line", loginMan.getLine());
                db.insert(DBHelper.TABLE_LoginMan_NAME, null, values);
                db.close();
            }
        } finally {
            //            db.endTransaction();//结束事物
        }
    }

    /**
     * add cashBox
     *
     * @param cashBoxes
     */
    public void addCashBox(List<CashBox> cashBoxes) {
        try {
            ContentValues values = new ContentValues();
            for (CashBox cashBox : cashBoxes) {
                db = helper.getWritableDatabase();
                values.put("rfidNum", cashBox.getRfidNum());
                values.put("bankId", cashBox.getBankId());
                values.put("boxSn", cashBox.getBoxSn());
                db.insert(DBHelper.TABLE_PdaCashboxInfo_NAME, null, values);
                db.close();
            }
        } finally {
            //            db.endTransaction();//结束事物
        }
    }

    /**
     * add NetInfo
     *
     * @param netInfos
     */
    public void addNetInfo(List<NetInfo> netInfos) {
        try {
            ContentValues values = new ContentValues();
            Log.i("", "===size is ===" + netInfos.size());
            for (NetInfo netInfo : netInfos) {
                db = helper.getWritableDatabase();
                values.put("bankId", netInfo.getBankId());
                Log.i("", "==bankid is===" + netInfo.getBankId());
                values.put("bankName", netInfo.getBankName());
                values.put("netTaskStatus", netInfo.getNetTaskStatus());
                db.insert(DBHelper.TABLE_PdaNetInfo_NAME, null, values);
                db.close();
            }
        } finally {
            //            db.endTransaction();//结束事物
        }
    }

    /**
     * add NetTask
     *
     * @param netTasks
     */
    public void addNetTask(List<NetTask> netTasks) {
        try {
            ContentValues values = new ContentValues();
            for (NetTask netTask : netTasks) {
                db = helper.getWritableDatabase();
                values.put("bankId", netTask.getBankId());
                values.put("bankName", netTask.getBankName());
                values.put("netTaskStatus", netTask.getNetTaskStatus());
                values.put("rfidNum", netTask.getRfidNum());
                values.put("boxSn", netTask.getBoxSn());
                db.insert(DBHelper.TABLE_NetTask_NAME, null, values);
                db.close();
            }
        } finally {
            //            db.endTransaction();//结束事物
        }
    }


    /**
     * 网点人员查询
     *
     * @return ConvoyMan list
     */
    public List<ConvoyMan> queryConvoyMan() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<ConvoyMan> manList = new ArrayList<ConvoyMan>();
        Cursor c = db.rawQuery("SELECT * FROM ConvoyMan", null);
        c.moveToFirst();
        while (c.moveToNext()) {
            ConvoyMan cMan = new ConvoyMan();
            String id = c.getString(0);
            String name = c.getString(1);
            String rfid = c.getString(2);
            cMan.setGuardManId(id);
            cMan.setGuardManName(name);
            cMan.setGuardManRFID(rfid);
            manList.add(cMan);
        }
        return manList;
    }

    /**
     * 查询网点人员
     *
     * @return
     */
    public List<NetMan> queryNetMan() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<NetMan> manList = new ArrayList<NetMan>();
        Cursor c = db.rawQuery("SELECT * FROM NetMan", null);
        c.moveToFirst();
        while (c.moveToNext()) {
            NetMan nMan = new NetMan();
            String NetPersonId = c.getString(0);
            String bankId = c.getString(1);
            String netPersonName = c.getString(2);
            String netPersonRFID = c.getString(3);
            nMan.setNetPersonId(NetPersonId);
            nMan.setBankId(bankId);
            nMan.setNetPersonName(netPersonName);
            nMan.setNetPersonRFID(netPersonRFID);
            manList.add(nMan);
        }
        return manList;
    }

    /**
     * 查询登录人员
     *
     * @return
     */
    public List<LoginMan> queryLoginMan() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<LoginMan> manList = new ArrayList<LoginMan>();
        Cursor c = db.rawQuery("SELECT * FROM LoginMan", null);
        c.moveToFirst();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    LoginMan lMan = new LoginMan();
                    String loginId = c.getString(0);
                    String login_name = c.getString(1);
                    String password = c.getString(2);
                    String flag = c.getString(3);
                    String line = c.getString(4);
                    lMan.setLoginId(loginId);
                    lMan.setLogin_name(login_name);
                    lMan.setPassword(password);
                    lMan.setFlag(flag);
                    lMan.setLine(line);
                    manList.add(lMan);
                } while (c.moveToNext());
            }
        }
        return manList;
    }

    /**
     * 查询所有款箱
     *
     * @return
     */
    public List<CashBox> queryCashBox() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<CashBox> boxList = new ArrayList<CashBox>();
        Cursor c = db.rawQuery("SELECT * FROM CashBox", null);
        c.moveToFirst();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    CashBox cb = new CashBox();
                    String rfidNum = c.getString(0);
                    String bankId = c.getString(1);
                    String boxSn = c.getString(2);
                    cb.setRfidNum(rfidNum);
                    cb.setBankId(bankId);
                    cb.setBoxSn(boxSn);
                    boxList.add(cb);
                } while (c.moveToNext());
            }
        }
        return boxList;
    }

    /**
     * 查询网点信息
     *
     * @return
     */
    public List<NetInfo> queryNetInfo() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<NetInfo> netInfos = new ArrayList<NetInfo>();
        Cursor c = db.rawQuery("SELECT * FROM PdaNetInfo", null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    NetInfo nf = new NetInfo();
                    String bankId = c.getString(0);
                    String bankName = c.getString(1);
                    String netTaskStatus = c.getString(2);
                    nf.setBankId(bankId);
                    nf.setBankName(bankName);
                    nf.setNetTaskStatus(netTaskStatus);
                    netInfos.add(nf);
                } while (c.moveToNext());
            }
        }

        return netInfos;
    }

    /**
     * 查询NetTask
     *
     * @return
     */
    public List<NetTask> queryNetTask() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<NetTask> netTasks = new ArrayList<NetTask>();
        Cursor c = db.rawQuery("SELECT * FROM NetTask", null);
        c.moveToFirst();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    NetTask nt = new NetTask();
                    String bankId = c.getString(0);
                    String bankName = c.getString(1);
                    String netTaskStatus = c.getString(2);
                    String rfidNum = c.getString(3);
                    String boxSn = c.getString(4);
                    nt.setBankId(bankId);
                    nt.setBankName(bankName);
                    nt.setNetTaskStatus(netTaskStatus);
                    nt.setRfidNum(rfidNum);
                    nt.setBoxSn(boxSn);
                    netTasks.add(nt);
                } while (c.moveToNext());
            }
        }

        return netTasks;
    }


    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }

    public void delete() {
        db = helper.getWritableDatabase();
        db.delete(DBHelper.TABLE_ConvoyMan_NAME, null, null);
        db.delete(DBHelper.TABLE_NetMan_NAME, null, null);
        db.delete(DBHelper.TABLE_NetTask_NAME, null, null);
        db.delete(DBHelper.TABLE_PdaNetInfo_NAME, null, null);
        db.delete(DBHelper.TABLE_PdaCashboxInfo_NAME, null, null);
        db.delete(DBHelper.TABLE_LoginMan_NAME, null, null);
        db.close();
        //        db.endTransaction();//结束事物
    }
}
