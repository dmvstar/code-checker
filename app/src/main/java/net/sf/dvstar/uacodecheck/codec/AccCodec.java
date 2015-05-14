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
public class AccCodec implements ICodec {


    public static final int[] weightOfControl = {1,3,7,1,3,3,7,1,3,7,1,3,7,1,3,7,1,3,7};
    private String mChk;
    private String mAcc;
    private String mMfo;
    private Activity mParent;
    private String TAG = "AccCodec";

    public AccCodec(Activity parent, String vMfo, String vAcc){
        this.mParent = parent;
        this.mMfo = vMfo;
        this.mAcc = vAcc;
        this.mChk = mMfo.substring(0,5)+ mAcc.substring(0,4) + "0" + mAcc.substring(5, mAcc.length() );;
    }

    public String getCheckSum(){
        String ret = "";
        char   cCheckAcc[] = mChk.toCharArray();
        Log.v(TAG, "cCheckAcc["+mChk.length()+"]["+mChk+"]["+mAcc+"]");
        int sum = 0;
        for(int i=0;i<cCheckAcc.length;i++){
            int a1=Integer.parseInt(""+(cCheckAcc[i]));
            int a2=weightOfControl[i];
            int part = a1*a2;
            //Log.v(TAG, "cCheckAcc["+i+"]["+a1+"]["+a2+"]["+part+"]["+(part % 10)+"]");
            sum += (part % 10);
        }
        Log.v(TAG, "getCheckSum() sum1 ="+sum+" "+mAcc.length());
        sum += mAcc.length();
        Log.v(TAG, "getCheckSum() sum2 ="+sum);

        ret = ""+(sum % 10 * 7) % 10;
        Log.v(TAG, "getCheckSum() checkAcc ["+ret+"]");

        return ret;
    }

    public String getValue(){
        char[] cAcc = mAcc.toCharArray();
        cAcc[4]=getCheckSum().charAt(0);
        String ret = new String( cAcc );
        Log.v(TAG, "getValue() ["+cAcc[4]+"]["+mAcc+"]["+ret+"]");
        return ret;
    }

    @Override
    public int getMinLen() {
        return 5;
    }

    @Override
    public int getMaxLen() {
        return 14;
    }

    @Override
    public Spannable getSpannable() {
        Spannable ret = new SpannableString(getValue());
        ret.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ret.setSpan(new ForegroundColorSpan(Color.RED), 4, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ret;
    }

}
