package net.sf.dvstar.uacodecheck.codec;

import android.text.Spannable;

/**
 * Created by dstarzhynskyi on 29.04.2015.
 */
public interface ICodec {

    public String getCheckSum();
    public String getValue();

    public int getMinLen();
    public int getMaxLen();

    public Spannable getSpannable();
}
