package com.news.arsalan.myapplication.utility;

/**
 * Created by Arsalan Bahojb
 * on 2017-12-02.
 */

public class StringUtil {
    public static boolean isNotEmpty(String inputString){
        return inputString!=null && !inputString.isEmpty();
    }

    public static String getDate(String articlePublishedDate){
       return (StringUtil.isNotEmpty(articlePublishedDate) && (articlePublishedDate.length() > 10)) ? articlePublishedDate.substring(0, 10) : "";
    }
}
