package net.sf.dvstar.uacodecheck.view;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by dstarzhynskyi on 14.05.2015.
 */
public class OnSwipeGestureListener implements GestureDetector.OnGestureListener {
    private final ISwipeCallback mCallback;
    private Context mContext;

    public OnSwipeGestureListener(Context aContext, ISwipeCallback aCallback){
        mContext = aContext;
        mCallback = aCallback;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float distanceX = e2.getX() - e1.getX();
        float distanceY = e2.getY() - e1.getY();
        if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (distanceX > 0)
                onSwipeRight();
            else
                onSwipeLeft();
            return true;
        }
        return false;
    }

    private void onSwipeLeft() {
        Toast.makeText(mContext, "Swipe Left", Toast.LENGTH_SHORT).show();
        mCallback.onSwipeLeft();
    }

    private void onSwipeRight() {
        Toast.makeText(mContext, "Swipe Right", Toast.LENGTH_SHORT).show();
        mCallback.onSwipeRight();
    }

    private static final int SWIPE_DISTANCE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;


}
