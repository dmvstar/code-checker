package net.sf.dvstar.uacodecheck.codec;

import android.app.Activity;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import net.sf.dvstar.uacodecheck.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dstarzhynskyi on 28.04.2015.
 */
public class INNCodec {

    private final Activity mParent;
    private final String mInn;
    private String TAG = "INNCodec";

    public INNCodec(Activity parent, String vInn) {
        this.mParent = parent;
        this.mInn = vInn;

        try {
            mDateStart = mFormatter.parse("1899-12-31");
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public static final DateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat mFormatterOut = new SimpleDateFormat("dd.MM.yyyy");
    public static Date mDateStart;
    public static final int[] weightOfControl ={
            -1,
            5,
            7,
            9,
            4,
            6,
            10,
            5,
            7
    };



    public int getIGenger(String val) {
        int ret;
        int vCheck = Integer.parseInt(val);
        if(vCheck%2!=0) ret = 0; else ret = 1;
        return ret;
    }

    public String getCheckSum(){
        String ret = "";
        int sum = 0;
        for(int i=0;i<weightOfControl.length;i++){
            sum += Integer.parseInt( ""+mInn.charAt(i) ) * weightOfControl[i];
        }
        sum = (sum % 11) % 10;
        ret += sum;
        return ret;
    }


    public String getDayOfBirth( String c ){
        String ret;
        int vCheck = Integer.parseInt(c);
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDateStart);
        cal.add(Calendar.DATE, vCheck);
        Date newDate = cal.getTime();
        ret = mFormatterOut.format(newDate);
        return ret;
    }

    public String getSGender( String c ){
        String ret;

        int vCheck = Integer.parseInt(c);

        String genders[] = mParent.getResources().getStringArray(R.array.list_gender);

        if(vCheck%2!=0) ret = genders[0]; else ret = genders[1];

        return ret;
    }

    public String getValue(){
        String ret = mInn.substring(0, 9)+ getCheckSum();
        Log.v(TAG, "getValue() [" + mInn + "][" + ret + "]");
        return ret;
    }



    public Spannable getSpannable(){
        Spannable ret = new SpannableString(mInn);
        ret.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ret.setSpan(new ForegroundColorSpan(Color.GRAY), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ret.setSpan(new ForegroundColorSpan(Color.YELLOW), 8, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ret.setSpan(new ForegroundColorSpan(Color.RED), 9, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ret;
    }

}
