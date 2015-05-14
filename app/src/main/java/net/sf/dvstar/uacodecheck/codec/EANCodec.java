package net.sf.dvstar.uacodecheck.codec;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import net.sf.dvstar.uacodecheck.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by dstarzhynskyi on 28.04.2015.
 */
public class EANCodec implements ICodec {


    private static final String TAG = "EANCodec";
    private final Activity mParent;
    private final String mCheckValue;
    private final EANCountriesList mEANCountriesList;

    public EANCodec(Activity parent, EANCountriesList vEANCountriesList, String vCheckValue){
        this.mParent = parent;
        this.mCheckValue = vCheckValue;
        this.mEANCountriesList = vEANCountriesList;
    }


    @Override
    public String getCheckSum() {
        String ret;
        int sum =0;
        int sum_c = 0;
        int sum_n = 0;
        int osta = 0;
        for( int pos=0; pos < 12; pos++) {
            int cur = Integer.parseInt(""+mCheckValue.charAt(pos));
            if( pos % 2 == 0 ){
                sum_n += cur;
            } else {
                sum_c += cur;
            }
            osta = (sum_c*3+sum_n) % 10;
            if (osta !=0)
                sum = 10 - osta;
        }
        //Log.v(TAG,"c["+sum_c+"]n["+sum_n+"]o["+osta+"]");
        ret = ""+sum;
        return ret;
    }

    @Override
    public String getValue() {
        return mCheckValue.substring(0,13-1)+getCheckSum();
    }

    @Override
    public int getMinLen() {
        return 13;
    }

    @Override
    public int getMaxLen() {
        return 13;
    }

    @Override
    public Spannable getSpannable() {
        Spannable ret = new SpannableString(getValue());
        ret.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ret.setSpan(new ForegroundColorSpan(Color.GRAY), 3, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ret.setSpan(new ForegroundColorSpan(Color.YELLOW), 8, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ret.setSpan(new ForegroundColorSpan(Color.RED), 12, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ret;
    }

    public String getCountryName(){
        String code = mCheckValue.substring(0,3);
        String ret = mEANCountriesList.getCountryName(code);
        return ret;
    }

    public EANCountriesItem getCountryItem() {
        String code = mCheckValue.substring(0, 3);
        EANCountriesItem ret = mEANCountriesList.getEANCountriesItem(code);
        return ret;
    }

    public EANCountriesItem getDefaultItem() {
        return new EANCountriesItem(mParent);
    }


    public static class EANCountriesList {

        private final String[] mCountriesList;
        private Map<EANCountriesItem, EANCountriesItem> eanCountriesItemMap;
        private final Activity mParent;

        public EANCountriesList(Activity aParent, String[] aCountriesList){
            mCountriesList = aCountriesList;
            mParent = aParent;
            loadCountryList();
        }

        private void loadCountryList() {
            eanCountriesItemMap = new HashMap<>();
            for(int i=0;i<mCountriesList.length;i++) {
                EANCountriesItem eanCountriesItem = new EANCountriesItem( mParent, mCountriesList[i]);
                Log.v(TAG, "loadCountryList"+eanCountriesItem);
                eanCountriesItemMap.put(eanCountriesItem, eanCountriesItem);
            }
        }

        public String getCountryName(String code){
            String ret = mParent.getResources().getString(R.string.text_country_unknown);
            EANCountriesItem find = new EANCountriesItem(mParent, code+"|");
            EANCountriesItem foun = eanCountriesItemMap.get(find);
            if(foun != null){
                ret = foun.mISOCountry;
            }
            return ret;
        }

        public EANCountriesItem getEANCountriesItem(String code) {
            EANCountriesItem find = new EANCountriesItem(null, code+"|");
            EANCountriesItem foun = eanCountriesItemMap.get(find);
            return foun;
        }
    }

    public static class EANCountriesItem {
        public static final int MODE_SINGLE = 1;
        public static final int MODE_DOUBLE = 2;
        public static final int MODE_MULTIP = 3;
        private final Activity mParent;

        int mMode;
        int mCodeFr;
        int mCodeTo;
        String mISOCode;
        String mISOCountry;
        String mEANCountry;
        private Drawable mCountryFlagDraw;
        private Bitmap mCountryFlagBitm;

        public EANCountriesItem(Activity aParent) {
            mCodeFr=000;
            mCodeTo=000;
            mParent = aParent;
            mISOCode = "zz";
            mISOCountry = mParent.getResources().getString(R.string.text_country_unknown);
            mEANCountry = mParent.getResources().getString(R.string.text_country_unknown);

            if(mParent!=null)
                loadCountryFlag();

        }

        public EANCountriesItem(Activity aParent, String item) {
            mParent = aParent;
            int tokenPos = 1;
            String tokenItem;
            StringTokenizer token = new StringTokenizer(item, "|");
            while (token.hasMoreTokens()){
                //93|au|Австралия|EAN Australia
                tokenItem = token.nextToken();
                switch(tokenPos){
                    case 1:
                        int posDelimM;
                        if((posDelimM=tokenItem.indexOf('+'))>0){
                            mMode = MODE_DOUBLE;
                            mCodeFr = Integer.parseInt(tokenItem.substring(0, posDelimM));
                            mCodeTo = Integer.parseInt(tokenItem.substring(posDelimM+1));
                        } else
                        if((posDelimM=tokenItem.indexOf('-'))>0){
                            mMode = MODE_MULTIP;
                            mCodeFr = Integer.parseInt(tokenItem.substring(0, posDelimM));
                            mCodeTo = Integer.parseInt(tokenItem.substring(posDelimM+1));
                        } else {
                            mMode = MODE_SINGLE;
                            mCodeFr = Integer.parseInt(tokenItem);
                            mCodeTo = mCodeFr;
                        }
                        break;
                    case 2:
                        mISOCode    = tokenItem;
                        break;
                    case 3:
                        mISOCountry = tokenItem;
                        break;
                    case 4:
                        mEANCountry = tokenItem;
                        break;

                }

                tokenPos++;
            }

/*
        int posDelim = item.indexOf('|');
            if(posDelim>0)
            {
                String codes = item.substring(0, posDelim);
                String names = item.substring(posDelim + 1);
                mISOCountry = names;
                int posDelimM;
                if((posDelimM=codes.indexOf('+'))>0){
                    mMode = MODE_DOUBLE;
                    mCodeFr = Integer.parseInt(codes.substring(0, posDelimM));
                    mCodeTo = Integer.parseInt(codes.substring(posDelimM+1));
                } else
                if((posDelimM=codes.indexOf('-'))>0){
                    mMode = MODE_MULTIP;
                    mCodeFr = Integer.parseInt(codes.substring(0, posDelimM));
                    mCodeTo = Integer.parseInt(codes.substring(posDelimM+1));
                } else {
                    mMode = MODE_SINGLE;
                    mCodeFr = Integer.parseInt(codes);
                    mCodeTo = mCodeFr;
                }
            }
*/
            if(mParent!=null)
                loadCountryFlag();
        }

        private void loadCountryFlag(){
            mCountryFlagDraw = null;
            mCountryFlagBitm = null;
                try {
                        InputStream is = mParent.getAssets().open("flags/" + mISOCode + ".png");
                        if (is != null)
                            mCountryFlagDraw = Drawable.createFromStream(is, null);
                            is.reset();
                            mCountryFlagBitm = BitmapFactory.decodeStream(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mCountryFlagDraw == null) {
                    mCountryFlagDraw = mParent.getResources().getDrawable(R.drawable.pirates_jolly_roger_64);
                }
                //Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.index);
                if (mCountryFlagBitm == null) {
                    mCountryFlagBitm  = BitmapFactory.decodeResource(mParent.getResources(), R.drawable.pirates_jolly_roger_64);
                }
/*
                if (mCountryFlagBitm == null) {
                    mCountryFlagBitm = Bitmap.createBitmap(mCountryFlagDraw.getBounds().width(), mCountryFlagDraw.getBounds().height(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(mCountryFlagBitm);
                    mCountryFlagDraw.setBounds(0, 0, mCountryFlagDraw.getBounds().width(), mCountryFlagDraw.getBounds().height());
                    mCountryFlagDraw.draw(canvas);
                }
*/
        }


        public Drawable getCountryFlagDrawable(){
            Log.v(TAG, "getCountryFlag("+mISOCode
                    +") d["
                    + mCountryFlagDraw.getBounds()
                    + " m["
                    +mCountryFlagBitm.getHeight()
                    +"x"
                    +mCountryFlagBitm.getWidth()
                    +"]");
            return mCountryFlagDraw;
        }

        public Bitmap getCountryFlagBitmap(){

            Log.v(TAG, "getCountryFlag("+mISOCode
                    +") d["
                    + mCountryFlagDraw.getBounds()
                    + " m["
                    +mCountryFlagBitm.getHeight()
                    +"x"
                    +mCountryFlagBitm.getWidth()
                    +"]");

            return mCountryFlagBitm;
        }


        @Override
        public String toString(){
            return "["+mMode+"]["+mCodeFr+"]["+mCodeTo+"]["+ mISOCountry +"]";
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object another) {

            if((another == null) || (another.getClass() != this.getClass())) {
                return false;
            }

            boolean ret = false;

            EANCountriesItem fromObject = (EANCountriesItem)another;
            switch(fromObject.mMode){
                case MODE_SINGLE:
                    if(this.mCodeFr == fromObject.mCodeFr) ret = true;
                    break;
                case MODE_DOUBLE:
                    if(this.mCodeFr == fromObject.mCodeFr || this.mCodeFr == fromObject.mCodeTo) ret = true;
                    break;
                case MODE_MULTIP:
                    if(this.mCodeFr >= fromObject.mCodeFr && this.mCodeFr <= fromObject.mCodeTo) ret = true;
                    break;
            }
            //Log.v(TAG, "equals ["+ret+"] this = ["+this.toString()+"] from ["+fromObject.toString()+"]");

            return ret;
        }

        public String getCountryName() {
            return mISOCountry +" ("+mISOCode+")";
        }
    }
}
