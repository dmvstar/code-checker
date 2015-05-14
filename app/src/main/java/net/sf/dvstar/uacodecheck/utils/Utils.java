package net.sf.dvstar.uacodecheck.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by dstarzhynskyi on 13.05.2015.
 */
public class Utils {
    private static boolean debug = true;

    public static void showDebugToast(Context baseContext, String message) {
        if(debug)
            Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show();
    }
}
