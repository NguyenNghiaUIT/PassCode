package com.example.nguyennghia.passcodeview;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by nguyennghia on 12/05/2016.
 */
public class Utils {
    public static int getWidthScreen(Context context) {
        Point sizeScreen = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getSize(sizeScreen);
        return sizeScreen.x;
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }


}