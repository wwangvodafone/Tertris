package syu.wyx.zz.tetris.param;

import android.content.Context;
import android.widget.Toast;

public class Utils {  
	
    public static void show(Context context, String value){
        Toast.makeText(context, (CharSequence)value, Toast.LENGTH_SHORT).show();
    }
}
