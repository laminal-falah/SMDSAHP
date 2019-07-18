package pd.sahang.mas.palembang.smds.ahp.ui.auth;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;
import android.view.Window;

import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.ui.auth.fragment.ForgetPasswordFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.auth.fragment.LoginFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.auth.inisiasi.AuthInterface;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.DashboardActivity;
import pd.sahang.mas.palembang.smds.ahp.utils.SharedPrefFirebase;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class AuthActivity extends AppCompatActivity implements AuthInterface, LoginFragment.Callbacks,
    ForgetPasswordFragment.Callbacks {

    private static final String TAG = AuthActivity.class.getSimpleName();

    private SnackBarUtils snackBarUtils;

    private FirebaseAuth mFirebaseAuth;

    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        snackBarUtils = new SnackBarUtils(this);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "getInstanceId failed", task.getException());
                return;
            }

            String newToken = task.getResult().getToken();
            Log.d(TAG, "onCreate: " + newToken);
            SharedPrefFirebase.getInstance(getApplicationContext()).saveDeviceToken(newToken);
        });

        onAuthenticationFirebase();

        onViewLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
                Log.d(TAG, "ON RESULT CALLED");
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 3000 < System.currentTimeMillis()) {
            snackBarUtils.snackbarShort(getString(R.string.msg_exit));
        } else {
            super.onBackPressed();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public void onAuthenticationFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFirebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    @Override
    public void onAttachLoginFragment() {
        Log.d(TAG, "onAttachLoginFragment()");
    }

    @Override
    public void onAttachForgetPasswordFragment() {
        Log.d(TAG, "onAttachForgetPasswordFragment()");
    }

    @Override
    public void onAttachViewLogin() {
        onViewLogin();
    }

    @Override
    public void onViewLogin() {
        onFragmentAuth(new LoginFragment());
    }

    @Override
    public void onViewForgetPassword() {
        onFragmentAuth(new ForgetPasswordFragment());
    }

    @Override
    public void onClickLogin(View view) {
        onViewLogin();
    }

    @Override
    public void onClickForgetPassword(View view) {
        onViewForgetPassword();
    }

    @Override
    public void onFragmentAuth(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_auth, fragment, fragment.getClass().getSimpleName())
                .commit();
    }
}
