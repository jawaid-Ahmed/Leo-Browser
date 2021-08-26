package com.jawaid.liobrowser.Helpers;

import android.content.Context;
import android.widget.Toast;

public class Constants {

    public static String LOG_TAG="myTag";

    public static void showToast(Context context,String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
