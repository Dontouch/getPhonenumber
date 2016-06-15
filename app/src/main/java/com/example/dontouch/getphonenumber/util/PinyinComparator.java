package com.example.dontouch.getphonenumber.util;

import com.example.dontouch.getphonenumber.Bean.ContactBean;

import java.util.Comparator;

/**
 * Created by Dontouch on 16/6/14.
 */
public class PinyinComparator implements Comparator<ContactBean> {

    public int compare(ContactBean o1, ContactBean o2) {
        if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }

}
