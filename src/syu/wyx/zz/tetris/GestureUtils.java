package syu.wyx.zz.tetris;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class GestureUtils {
    public static Screen getScreenPix(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return new Screen(dm.widthPixels,dm.heightPixels);
    }}   

