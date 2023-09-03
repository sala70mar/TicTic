package com.qboxus.tictic.simpleclasses;

import android.content.Context;
import android.content.SharedPreferences;

public class DarkModePrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "education-dark-mode";

    private static final String IS_NIGHT_MODE = "IsNightMode";
    private static final String SCREENRESET = "ScreenReset";

    public DarkModePrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setDarkMode(boolean isNightMode) {
        editor.putBoolean(IS_NIGHT_MODE, isNightMode);
        editor.commit();
    }

    public boolean isNightMode() {
        return pref.getBoolean(IS_NIGHT_MODE, false);
    }

    public void setDarkToReset(boolean isReset) {
        editor.putBoolean(SCREENRESET, isReset);
        editor.commit();
    }

    public boolean isDarkToReset() {
        return pref.getBoolean(SCREENRESET, false);
    }

}