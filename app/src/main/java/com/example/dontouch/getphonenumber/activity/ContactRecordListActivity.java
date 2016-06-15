package com.example.dontouch.getphonenumber.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.example.dontouch.getphonenumber.Bean.ContactRecordBean;
import com.example.dontouch.getphonenumber.R;
import com.example.dontouch.getphonenumber.adpter.ContactRecordListAdapter;
import com.example.dontouch.getphonenumber.constant.Constant;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dontouch on 16/6/14.
 */
public class ContactRecordListActivity extends Activity {

    private ContactRecordListAdapter adapter;
    private ListView contactRecordList;
    private List<ContactRecordBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_contact_record_list);

        init();

    }

    private void init() {


        contactRecordList = (ListView) findViewById(R.id.call_log_list);

        contactRecordList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list.get(position).setAdd(
                        !list.get(position).isAdd());
                adapter.notifyDataSetChanged();
            }
        });

        Uri calllog_uri = CallLog.Calls.CONTENT_URI; //获得联系人默认uri
        ContentResolver resolver = this.getContentResolver();  //获得ContentResolver对象

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Cursor cursor = resolver.query(calllog_uri,Constant.CALLLOG_PROJECTION,null,null,CallLog.Calls.DEFAULT_SORT_ORDER);

        list = new ArrayList<ContactRecordBean>();

        if (null != cursor) {
            while (cursor.moveToNext()) {
                ContactRecordBean contactRecord = new ContactRecordBean();

                String number = cursor.getString(Constant.CALLLOG_NUMBER_INDEX);
                String name = cursor.getString(Constant.CALLLOG_CACHED_NAME_INDEX);
                String type = cursor.getString(Constant.CALLLOG_TYPE_INDEX);
                String date = cursor.getString(Constant.CALLLOG_DATE_INDEX);
                String duration = cursor.getString(Constant.CALLLOG_DURATION_INDEX);



                if (name == null) {
                    name = "";
                }

                if ("".equals(name)) {
                    contactRecord.setName("");
                } else {
                    contactRecord.setName(name);
                }



                contactRecord.setNumber(number);
                contactRecord.setDate(date);
                contactRecord.setDuration(duration);
                contactRecord.setType(type);

                list.add(contactRecord);

            }

            cursor.close();
        }


        adapter = new ContactRecordListAdapter(this,list);
        contactRecordList.setAdapter(adapter);

    }




}
