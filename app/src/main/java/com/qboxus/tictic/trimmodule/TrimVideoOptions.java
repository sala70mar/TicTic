package com.qboxus.tictic.trimmodule;

public class TrimVideoOptions {


    public TrimType trimType = TrimType.DEFAULT;

    public long minDuration, fixedDuration;

    public boolean hideSeekBar;

    public boolean showFileLocationAlert;

    public long[] minToMax;

    public String title;

    public String local;

    public CompressOption compressOption;

    public TrimVideoOptions() {
    }

}
