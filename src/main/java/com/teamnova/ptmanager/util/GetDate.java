package com.teamnova.ptmanager.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetDate {
    // 오늘날짜 출력 yyyymmdd
    public static String getTodayDate(){
        String pattern = "yyyyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());
        return date;
    }

    // 특정날짜 출력 yyyy년 MM월 dd일
    public static String getDateWithYMD(String dateOfYYYYMMDD){
        String year = dateOfYYYYMMDD.substring(0,4);
        String month = dateOfYYYYMMDD.substring(4,6);
        String day = dateOfYYYYMMDD.substring(6,8);

        return year + "년 " + month + "월 " + day + "일";
    }

    // 특정요일 구하기
    public static String getDayOfWeek(String dateOfYYYYMMDD) {
        String pattern = "yyyyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        int dayOfWeekInt = 9999;
        String dayOfWeek = "";

        try {
            Date date = simpleDateFormat.parse(dateOfYYYYMMDD);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            dayOfWeekInt = calendar.get(Calendar.DAY_OF_WEEK);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        switch (dayOfWeekInt){
            case 1:
                dayOfWeek = "일";
                break;
            case 2:
                dayOfWeek = "월";
                break;
            case 3:
                dayOfWeek = "화";
                break;
            case 4:
                dayOfWeek = "수";
                break;
            case 5:
                dayOfWeek = "목";
                break;
            case 6:
                dayOfWeek = "금";
                break;
            case 7:
                dayOfWeek = "토";
                break;
        }
        return dayOfWeek;
    }
}
