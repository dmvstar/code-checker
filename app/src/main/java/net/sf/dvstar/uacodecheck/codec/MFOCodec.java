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
public class MFOCodec implements ICodec {
    public static final int[] weightOfControl ={
            1,
            3,
            7,
            1,
            3,
            7
    };
    private String mCheckValue;
    private Activity mParent;
    private String TAG="MFOCodec";

    public MFOCodec(Activity parent, String vCheckValue){
        this.mParent = parent;
        this.mCheckValue = vCheckValue;
    }

    public String getCheckSum(){
        String ret = "";
        String aMfo = mCheckValue.substring(0, 5)+"0";
        int sum = 0;
        for(int i=0;i<weightOfControl.length;i++){
            sum += Integer.parseInt( ""+aMfo.charAt(i) ) * weightOfControl[i];
        }
        sum = ((sum % 10)*7)%10;
        ret += sum;
        return ret;
    }

    public String getValue(){
        String ret = mCheckValue.substring(0, 5)+ getCheckSum();
        Log.v(TAG, "getValue() [" + mCheckValue + "][" + ret + "]");
        return ret;
    }

    @Override
    public int getMinLen() {
        return 6;
    }

    @Override
    public int getMaxLen() {
        return 6;
    }

    @Override
    public Spannable getSpannable() {
        Spannable ret = new SpannableString(getValue());
        ret.setSpan(new ForegroundColorSpan(Color.GREEN),   0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ret.setSpan(new ForegroundColorSpan(Color.RED),     5, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ret;
    }
}
