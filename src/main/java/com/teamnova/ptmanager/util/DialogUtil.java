package com.teamnova.ptmanager.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyEnLargeActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 자주 사용하는 메시지 util
 */
public class DialogUtil {
    /** 메세지 다이얼로그
     *  특정 메시지를 출력하는 다이얼로그이다.
     * */
    public static void msgDialog(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message);

        // 취소버튼 클릭
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        // 확인버튼 클릭
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {}
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /** 메세지 다이얼로그
     *  확인, 취소 버튼 클릭 시 특정 동작을 수행하는 다이얼로그이다.
     *  1. 파라미터
     *      1) context: dialog가 출력되는 화면의 context
     *      2) message: 출력할 메세지
     *      3) positiveActListener: 확인버튼 클릭 시 수행할 일
     *      4) negativeActListener: 취소버튼 클릭 시 수행할 일
     * */
    public static void msgDialog(Context context, String message, DialogInterface.OnClickListener positiveActListener, DialogInterface.OnClickListener negativeActListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message);

        // 취소버튼 클릭
        builder.setNegativeButton("취소", negativeActListener);

        // 확인버튼 클릭
        builder.setPositiveButton("확인", positiveActListener);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
