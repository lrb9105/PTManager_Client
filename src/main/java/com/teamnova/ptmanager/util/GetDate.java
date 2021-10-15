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

    // 오늘날짜 출력 yyyy-MM-dd HH:mm:ss:
    public static String getTodayDateWithTime(){
        String pattern = "yyyy-MM-dd HH:mm:ss";
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

    // 특정날짜 출력 yyyy.MM.dd
    public static String getDateWithYMDAndDot(String dateOfYYYYMMDD){
        String year = dateOfYYYYMMDD.substring(0,4);
        String month = dateOfYYYYMMDD.substring(4,6);
        String day = dateOfYYYYMMDD.substring(6,8);

        return year + "." + month + "." + day;
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

    /**
     * 오전, 오후 시간 구하기
     * */
    public static String getAmPmTime(int hour, int minute){
        String time = null;
        String minStr = null;
        if(minute < 10){
            minStr = "0"+minute;
        } else{
            minStr = ""+minute;
        }

        if(hour == 0){
            time = "오전 " + 12 + ":" + minStr;
        } else if(hour < 12){
            time = "오전 " + hour + ":" + minStr;
        } else if(hour == 12){
            time = "오후 " + hour + ":" + minStr;
        } else{
            time = "오후 " + (hour - 12) + ":" + minStr;
        }
        return time;
    }

    // 현재시간 구하기
    public static String getCurrentTime(boolean isLaterTime) throws Exception {
        SimpleDateFormat format2 = new SimpleDateFormat( "hh:mm");
        Date today = new Date();
        String time = format2.format(today);

        if(isLaterTime){
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            cal.add(Calendar.HOUR, 1);

            time = format2.format(cal.getTime());
        }

        return time;
    }

    // 두 날짜의 일 수 차이 구하기
    public static long getDayDifference(String date1, String date2){
        long calDateDays = 0;

        try{ // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            // date1, date2 두 날짜를 parse()를 통해 Date형으로 변환.
            Date FirstDate = format.parse(date1);
            Date SecondDate = format.parse(date2);

            Log.d("FirstDate:", FirstDate.toString());
            Log.d("SecondDate:", SecondDate.toString());

            // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
            // 연산결과 -950400000. long type 으로 return 된다.
            long calDate = SecondDate.getTime() - FirstDate.getTime();

            Log.d("calDate:", "" + calDate);


            // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
            // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            calDateDays = calDate / ( 24*60*60*1000);

            System.out.println("두 날짜의 날짜 차이: "+ calDateDays);

        }
        catch(ParseException e)
        {
            // 예외 처리
        }
        return calDateDays;
    }
}
