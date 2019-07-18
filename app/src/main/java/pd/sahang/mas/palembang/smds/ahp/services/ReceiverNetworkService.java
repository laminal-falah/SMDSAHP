package pd.sahang.mas.palembang.smds.ahp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pd.sahang.mas.palembang.smds.ahp.App;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.utils.NetworkUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class ReceiverNetworkService extends BroadcastReceiver {

    private boolean isOffline = false;
    private final SnackBarUtils snackbarUtils = new SnackBarUtils(App.mActivity);

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            NetworkUtils.getConnectivityStatus(context);
            if (!NetworkUtils.isNetworkConnected(context)) {
                snackbarUtils.snackBarInfinite(App.mActivity.getResources().getString(R.string.msg_no_internet));
                isOffline = true;
            } else {
                if (isOffline) {
                    snackbarUtils.snackBarLong(App.mActivity.getResources().getString(R.string.msg_back_online));
                    isOffline = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
