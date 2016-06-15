package com.example.dontouch.getphonenumber.activity;

import android.app.Activity;
import android.content.ContentResolver;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;

import android.view.View;

import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.example.dontouch.getphonenumber.Bean.ContactBean;
import com.example.dontouch.getphonenumber.R;
import com.example.dontouch.getphonenumber.adpter.ContactListAdapter;
import com.example.dontouch.getphonenumber.constant.Constant;
import com.example.dontouch.getphonenumber.parser.CharacterParser;
import com.example.dontouch.getphonenumber.util.PinyinComparator;
import com.example.dontouch.getphonenumber.view.SideBar;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Dontouch on 16/6/14.
 */
public class ContactListActivity extends Activity {

    private ContactListAdapter adapter;
    private ListView contactList;
    private List<ContactBean> list;
    private SideBar alphabeticBar; // 快速索引条
    private TextView fast_position;

    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;  //拼音排序


    private LinearLayout xuanfuLayout; // 顶部悬浮的layout
    private TextView xuanfaText; // 悬浮的文字

    private int lastFirstVisibleItem = -1;

    private Map<Integer, ContactBean> contactIdMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_contact_list);

        init();

    }

    private void init(){

        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        xuanfuLayout = (LinearLayout) findViewById(R.id.top_layout);
        xuanfaText = (TextView) findViewById(R.id.top_char);
        contactList = (ListView) findViewById(R.id.contact_list);
        alphabeticBar = (SideBar) findViewById(R.id.fast_scroller);
        fast_position =(TextView) findViewById(R.id.fast_position);
        alphabeticBar.setVisibility(View.VISIBLE);

        alphabeticBar.setTextView(fast_position);

        alphabeticBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener(){

            @Override
            public void onTouchingLetterChanged(String s) {

                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    contactList.setSelection(position);
                }
            }
        });


        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list.get(position).setAdd(
                        !list.get(position).isAdd());
                adapter.notifyDataSetChanged();
            }
        });

        Uri contact_uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; //获得联系人默认uri
        ContentResolver resolver = this.getContentResolver();  //获得ContentResolver对象
        Cursor cursor = resolver.query(contact_uri, Constant.PHONES_PROJECTION, null, null, null); //获取电话本中开始一项光标

        list = new ArrayList<ContactBean>();

        if(null !=cursor){
            while (cursor.moveToNext())
            {
                ContactBean contact = new ContactBean();
                /*
                 * 获取电话号码
                 */
                String number = cursor.getString(Constant.PHONES_NUMBER_INDEX);
                /*
                 *  当手机号码为空的或者为空字段 跳过当前循环
                 */
                if (TextUtils.isEmpty(number)) continue;

                /*
                 *  得到联系人名称
                 */
                String name = cursor.getString(Constant.PHONES_DISPLAY_NAME_INDEX);
                /*
                 * 得到联系人ID
                 */
                Long id = cursor.getLong(Constant.PHONES_CONTACT_ID_INDEX);
                /*
                 * 得到联系人头像ID
                 */
                Long photoId = cursor.getLong(Constant.PHONES_PHOTO_ID_INDEX);



                contact.setNumber(number);
                contact.setName(name);
                contact.setPhoto(photoId);
                contact.setContactId(id);

                String pinyin = characterParser.getSelling(name);
                String sortString = pinyin.substring(0, 1).toUpperCase();

                if (sortString.matches("[A-Z]")) {
                    contact.setSortLetters(sortString.toUpperCase());
                } else {
                    contact.setSortLetters("#");
                }

                list.add(contact);

            }

            cursor.close();
        }


        Collections.sort(list, pinyinComparator);
        adapter = new ContactListAdapter(this,list);
        contactList.setAdapter(adapter);


        /**
         * 设置滚动监听， 实时跟新悬浮的字母的值
         */
        contactList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int section = adapter.getSectionForPosition(firstVisibleItem);
                int nextSecPosition = adapter
                        .getPositionForSection(section + 1);
                if (firstVisibleItem != lastFirstVisibleItem) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) xuanfuLayout
                            .getLayoutParams();
                    params.topMargin = 0;
                    xuanfuLayout.setLayoutParams(params);
                    xuanfaText.setText(String.valueOf((char) section));
                }
                if (nextSecPosition == firstVisibleItem + 1) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = xuanfuLayout.getHeight();
                        int bottom = childView.getBottom();
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) xuanfuLayout
                                .getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            xuanfuLayout.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                xuanfuLayout.setLayoutParams(params);
                            }
                        }
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;
            }
        });

    }


}
