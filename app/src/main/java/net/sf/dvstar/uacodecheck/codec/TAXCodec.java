package net.sf.dvstar.uacodecheck.codec;

import android.app.Activity;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

/**
 * Created by dstarzhynskyi on 28.04.2015.
 */
public class TAXCodec implements ICodec {


    public static final int[] weightOfControlS = {1,2,3,4,5,6,7}; // ]0 .. 30 000 000[, ]60 000 000 ..
    public static final int[] weightOfControlG = {7,1,2,3,4,5,6}; // [30 000 000 - 60 000 000]
    private static final String TAG = "TAXCodec";
    private Activity mParent;
    private String mCheckValue;

    public TAXCodec(Activity parent, String vCheckValue){
        this.mParent = parent;
        this.mCheckValue = vCheckValue;
    }


    @Override
    public String getCheckSum() {
        int [] weightOfControl;
        int icheck = Integer.parseInt(mCheckValue);
        if ( icheck >= 30000000 && icheck <= 60000000 )
            weightOfControl = weightOfControlG;
        else
            weightOfControl = weightOfControlS;
        String ret = "";
        String aCheck = mCheckValue;
        int sum = 0;
        for(int i=0;i<7-1;i++){
            sum += Integer.parseInt( ""+aCheck.charAt(i) ) * weightOfControl[i];
        }
        sum = ((sum % 11) % 10); //!!!
        /*
        Якщо отриманий залишок – цифра, то це
        контрольний розряд. Якщо отриманий залишок
        – двоцифрове число (можливий лише один
        варіант – 10), то для забезпечення
        однорозрядності контрольного числа необхідно
        провести його перерахунок, застосовуючи іншу
        послідовність вагових коефіцієнтів:
        3, 4, 5, 6, 7, 8, 9 або
        9, 3, 4, 5, 6, 7, 8.        */
        ret += sum;
        return ret;
    }

    @Override
    public String getValue() {
        String ret = mCheckValue.substring(0, 7)+ getCheckSum();
        Log.v(TAG, "getValue() [" + mCheckValue + "][" + ret + "]");
        return ret;
    }

    @Override
    public int getMinLen() {
        return 8;
    }

    @Override
    public int getMaxLen() {
        return 8;
    }

    @Override
    public Spannable getSpannable() {
        Spannable ret = new SpannableString(getValue());
        ret.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ret.setSpan(new ForegroundColorSpan(Color.RED), 7, 8,   Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ret;
    }
}
