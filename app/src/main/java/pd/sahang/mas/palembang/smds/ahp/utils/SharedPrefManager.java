package pd.sahang.mas.palembang.smds.ahp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import pd.sahang.mas.palembang.smds.ahp.App;
import pd.sahang.mas.palembang.smds.ahp.R;

public class SharedPrefManager {

    private static final String SP_APP = App.mActivity.getResources().getString(R.string.app_name);

    public static final String SP_LEVEL = "SP_LEVEL";

    private final SharedPreferences sp;
    private final SharedPreferences.Editor editor;

    public SharedPrefManager(@NonNull Context context) {
        this.sp = context.getSharedPreferences(SP_APP, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.apply();
    }

    public void saveSPString(String keySP, String value){
        editor.putString(keySP, value);
        editor.apply();
    }

    public void saveSPInt(String keySP, int value){
        editor.putInt(keySP, value);
        editor.apply();
    }

    public void saveSPBoolean(String keySP, boolean value){
        editor.putBoolean(keySP, value);
        editor.apply();
    }

    public String getSpLevel() {
        return sp.getString(SP_LEVEL, null);
    }

    public void clearShared() {
        editor.putString(SP_LEVEL, null);
        editor.commit();
    }
}
