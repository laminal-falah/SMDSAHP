package pd.sahang.mas.palembang.smds.ahp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefFirebase {
    private static final String SHARED_PREF_NAME = "FCMSharedPref";
    private static final String TAG_TOKEN = "tagtoken";

    private static SharedPrefFirebase mInstance;
    private static Context mCtx;

    private SharedPrefFirebase(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefFirebase getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefFirebase(context);
        }
        return mInstance;
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TAG_TOKEN, null);
    }
}
