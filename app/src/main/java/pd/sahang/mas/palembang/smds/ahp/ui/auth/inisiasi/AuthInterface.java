package pd.sahang.mas.palembang.smds.ahp.ui.auth.inisiasi;

import android.view.View;

import androidx.fragment.app.Fragment;

public interface AuthInterface {
    void onAuthenticationFirebase();
    void onViewLogin();
    void onViewForgetPassword();
    void onClickLogin(View view);
    void onClickForgetPassword(View view);
    void onFragmentAuth(Fragment fragment);
}
