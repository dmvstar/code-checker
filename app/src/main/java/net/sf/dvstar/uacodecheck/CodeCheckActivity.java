package net.sf.dvstar.uacodecheck;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import net.sf.dvstar.uacodecheck.activity.AboutActivity;
import net.sf.dvstar.uacodecheck.activity.SimpleScannerActivity;
import net.sf.dvstar.uacodecheck.codec.AccCodec;
import net.sf.dvstar.uacodecheck.codec.EANCodec;
import net.sf.dvstar.uacodecheck.codec.INNCodec;
import net.sf.dvstar.uacodecheck.codec.MFOCodec;
import net.sf.dvstar.uacodecheck.codec.TAXCodec;
import net.sf.dvstar.uacodecheck.utils.Utils;
import net.sf.dvstar.uacodecheck.view.ISwipeCallback;
import net.sf.dvstar.uacodecheck.view.MyGestureListener;
import net.sf.dvstar.uacodecheck.view.OnSwipeGestureListener;
import net.sf.dvstar.uacodecheck.view.OnSwipeTouchListener;


public class CodeCheckActivity extends ActionBarActivity implements ISwipeCallback {

    private static final String TAG = "CodeCheckActivity";

    private EditText mEditCheck;

    private EditText mEditBirth;
    private EditText mEditChkReal;
    private EditText mEditChkCalc;

    private Spinner mSpinnerGender;
    private Spinner mSpinnerCodeType;

    private EditText mEditCheckMfo;

    private EditText mEditEanCountry;
    private ImageView mImgEanFlag;

    int DIALOG_DATE = 1;
    int myYear = 2011;
    int myMonth = 02;
    int myDay = 03;
    private ViewFlipper mFlipper;

    private GestureDetector mGestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_flip);

        mEditCheck = (EditText) findViewById(R.id.edit_text_check);
        mEditChkReal = (EditText) findViewById(R.id.edit_text_cntr_real);
        mEditChkCalc = (EditText) findViewById(R.id.edit_text_cntr_calc);

        mEditBirth = (EditText) findViewById(R.id.edit_text_birth);
        mSpinnerGender = (Spinner) findViewById(R.id.spinner_gender);
        ArrayAdapter<CharSequence> adapter_gener = ArrayAdapter.createFromResource(
                this, R.array.list_gender, android.R.layout.simple_spinner_item);
        adapter_gener.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerGender.setAdapter(adapter_gener);

        mSpinnerCodeType = (Spinner) findViewById(R.id.spinner_code_type);
        ArrayAdapter<CharSequence> adapter_codes = ArrayAdapter.createFromResource(
                this, R.array.list_codes, android.R.layout.simple_spinner_item);
        adapter_codes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCodeType.setAdapter(adapter_codes);
        mCurrentViewFlipperMax = adapter_codes.getCount();
        mCurrentViewFlipperChild = 0;

        mSpinnerCodeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                Utils.showDebugToast(getBaseContext(), "Position = " + position);
                setViewFlipperChild(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        mEANCountriesList = new EANCodec.EANCountriesList( this, getResources().getStringArray(R.array.ean_country_list_add) );
        mGestureDetector = new GestureDetector(this, new OnSwipeGestureListener(this, this));

        /*
        this.getWindow().getDecorView().getRootView().setOnTouchListener(
                new OnSwipeTouchListener(this) {
                    public void onSwipeRight() {
                        Toast.makeText(CodeCheckActivity.this, "right", Toast.LENGTH_SHORT).show();
                    }

                    public void onSwipeLeft() {
                        Toast.makeText(CodeCheckActivity.this, "left", Toast.LENGTH_SHORT).show();
                    }
                });
        */
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_code_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            showAbout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int mCurrentViewFlipperChild = 0;
    private int mCurrentViewFlipperMax = 0;
    private void setViewFlipperChild(int position){
        mCurrentViewFlipperChild = position;
        mFlipper = (ViewFlipper) findViewById( R.id.viewFlipper );
        mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
                R.anim.fade_in));
        mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.fade_out));
        switch(position) {
            case 0: mFlipper.setDisplayedChild(0);
                break;
            case 1: mFlipper.setDisplayedChild(1);
                break;
            case 2: mFlipper.setDisplayedChild(2);
                break;
            case 3: mFlipper.setDisplayedChild(3);
                break;
            case 4: mFlipper.setDisplayedChild(4);
                break;
        }
    }

    public void onCalculateClick(View view){
        int position = mSpinnerCodeType.getSelectedItemPosition();
        switch(position) {
            case 0: decodeValueINN();
                break;
            case 1: decodeValueMFO();
                break;
            case 2: decodeValueACC();
                break;
            case 3: decodeValueTAX();
                break;
            case 4: decodeValueEAN();
                break;
        }
    }

    public void onScanClick(View view){
        showBarcodeScan();
    }

    private void showAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        this.startActivity(intent);
    }


    private void showBarcodeScan() {
        Intent intent = new Intent(this, SimpleScannerActivity.class);
        this.startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        String code = data.getStringExtra("scanned_ean_code");
        mEditCheck = (EditText) findViewById(R.id.edit_text_check_ean);
        if(code.length()==12) {
            code = "0"+code;
        }
        mEditCheck.setText(code);
        decodeValueEAN();
    }

    public void onclick(View view) {
        showDialog(DIALOG_DATE);
    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }


    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;
            mEditBirth.setText(myDay + "." + myMonth + "." + myYear);
        }
    };

    private void decodeValueACC() {
        mEditCheck = (EditText) findViewById(R.id.edit_text_check_mfo_acc);
        mEditCheckMfo = (EditText) findViewById(R.id.edit_text_check_mfo_acc);
        mEditCheck = (EditText) findViewById(R.id.edit_text_check_acc);
        mEditChkReal = (EditText) findViewById(R.id.edit_text_cntr_real_acc);
        mEditChkCalc = (EditText) findViewById(R.id.edit_text_cntr_calc_acc);
        String sValueAcc = mEditCheck.getText().toString();
        String sValueMfo = mEditCheckMfo.getText().toString();
        if(sValueAcc.length()<5) return;

        AccCodec accCodec = new AccCodec(this, sValueMfo, sValueAcc);
        accCodec.getValue();

        String sCheckReal = sValueAcc.substring(4,5);
        mEditChkReal.setText(sCheckReal);
        String sCheckCalc = accCodec.getCheckSum();
        mEditChkCalc.setText(sCheckCalc);
        MFOCodec mfoCodec = new MFOCodec( this, sValueMfo );
        mfoCodec.getValue();

        mEditCheck.setText(accCodec.getSpannable());
        mEditCheckMfo.setText(mfoCodec.getSpannable());

    }

    private void decodeValueMFO() {
        mEditCheck = (EditText) findViewById(R.id.edit_text_check_mfo);
        mEditChkReal = (EditText) findViewById(R.id.edit_text_cntr_real_mfo);
        mEditChkCalc = (EditText) findViewById(R.id.edit_text_cntr_calc_mfo);

        String sValue = mEditCheck.getText().toString();
        Log.v(TAG, "decodeValueMFO = " + sValue);

        if(sValue.length()!=6) return;
        MFOCodec codec = new MFOCodec( this, sValue );
        codec.getValue();
        String sCheckReal = sValue.substring(5, 6);
        mEditChkReal.setText(sCheckReal);
        String sCheckCalc = codec.getCheckSum();
        mEditChkCalc.setText(sCheckCalc);
        mEditCheck.setText(codec.getSpannable());

    }

    private EANCodec.EANCountriesList mEANCountriesList;

    private void decodeValueEAN() {
        mEditCheck = (EditText) findViewById(R.id.edit_text_check_ean);
        mEditChkReal = (EditText) findViewById(R.id.edit_text_cntr_real_ean);
        mEditChkCalc = (EditText) findViewById(R.id.edit_text_cntr_calc_ean);
        mEditEanCountry = (EditText) findViewById(R.id.edit_text_check_country_ean);
        mImgEanFlag = (ImageView) findViewById(R.id.image_country_flag);

        String sValue = mEditCheck.getText().toString();
        Log.v(TAG, "decodeValueEAN = " + sValue);
        if(sValue.length()!=13) return;
        EANCodec codec = new EANCodec(this, mEANCountriesList, sValue);
        codec.getValue();
        String sCheckReal = sValue.substring(12, 13);
        mEditChkReal.setText(sCheckReal);
        String sCheckCalc = codec.getCheckSum();
        mEditChkCalc.setText(sCheckCalc);
        mEditCheck.setText(codec.getSpannable());
        EANCodec.EANCountriesItem item = codec.getCountryItem();
        mEditEanCountry.setText(item != null ? item.getCountryName() : codec.getDefaultItem().getCountryName());
        //mImgEanFlag.setImageDrawable(item != null ? item.getCountryFlag(): codec.getDefaultItem().getCountryFlag());
        Bitmap bitmap;
        if(item != null) {
            bitmap = item.getCountryFlagBitmap();
            if (bitmap != null)
                mImgEanFlag.setImageBitmap(bitmap);
        } else {
            bitmap = codec.getDefaultItem().getCountryFlagBitmap();
            if (bitmap != null)
                mImgEanFlag.setImageBitmap(bitmap);
        }
    }

    private void decodeValueTAX() {
        mEditCheck   = (EditText) findViewById(R.id.edit_text_check_tax);
        mEditChkReal = (EditText) findViewById(R.id.edit_text_cntr_real_tax);
        mEditChkCalc = (EditText) findViewById(R.id.edit_text_cntr_calc_tax);

        String sValue = mEditCheck.getText().toString();
        Log.v(TAG,"decodeValueTAX = "+sValue);

        if(sValue.length()!=8) return;
        TAXCodec taxCodec = new TAXCodec( this, sValue );
        taxCodec.getValue();
        String sCheckReal = sValue.substring(7,8);
        mEditChkReal.setText(sCheckReal);
        String sCheckCalc = taxCodec.getCheckSum();
        mEditChkCalc.setText(sCheckCalc);
        mEditCheck.setText(taxCodec.getSpannable());
    }



    private void decodeValueINN() {
        mEditCheck = (EditText) findViewById(R.id.edit_text_check);
        mEditChkReal = (EditText) findViewById(R.id.edit_text_cntr_real);
        mEditChkCalc = (EditText) findViewById(R.id.edit_text_cntr_calc);

        String sValue = mEditCheck.getText().toString();
        Log.v(TAG, "decodeValueINN = " + sValue);

        if(sValue.length()!=10) return;

        AccCodec accCodec = new AccCodec( this, "320478", "26201214587451");
        accCodec.getValue();

        INNCodec innCodec = new INNCodec( this, sValue );

        String sSex = innCodec.getSGender(sValue.substring(8, 9));
        int iSex = innCodec.getIGenger(sValue.substring(8, 9));
        Log.v(TAG, "decodeValue = " + sSex);

        mSpinnerGender.setSelection(iSex);

        String sDate = innCodec.getDayOfBirth(sValue.substring(0, 5));
        Log.v(TAG, "decodeValue = " + sDate);
        mEditBirth.setText(sDate);
        String sCheckReal = sValue.substring(9,10);
        mEditChkReal.setText(sCheckReal);
        String sCheckCalc = innCodec.getCheckSum(); // sINN.substring(0, 9)
        mEditChkCalc.setText(sCheckCalc);
        mEditCheck.setText(innCodec.getSpannable());
    }


    @Override
    public void onSwipeLeft() {
        mCurrentViewFlipperChild++;
        if(mCurrentViewFlipperChild >= mCurrentViewFlipperMax){
            mCurrentViewFlipperChild = 0;
        }
        mSpinnerCodeType.setSelection(mCurrentViewFlipperChild);
        //setViewFlipperChild(mCurrentViewFlipperChild);
    }

    @Override
    public void onSwipeRight() {
        mCurrentViewFlipperChild--;
        if(mCurrentViewFlipperChild <= 0){
            mCurrentViewFlipperChild = mCurrentViewFlipperMax-1;
        }
        mSpinnerCodeType.setSelection(mCurrentViewFlipperChild);
        //setViewFlipperChild(mCurrentViewFlipperChild);
    }
}
