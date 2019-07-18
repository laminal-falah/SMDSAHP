package pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.UpdateProfileAdapter;
import pd.sahang.mas.palembang.smds.ahp.ui.auth.AuthActivity;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.inisiasi.UpdateProfileInterface;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.sub.DataUserFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.sub.PasswordFragment;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.ViewPagerCustom;

public class UpdateProfileActivity extends AppCompatActivity implements UpdateProfileInterface {

    private static final String TAG = UpdateProfileActivity.class.getSimpleName();

    public static final int REQUEST_EDIT = 1024;
    public static final int RESULT_EDIT_SUCCESS = 1025;
    public static final int RESULT_EDIT_FAILED = 1026;
    public static final int RESULT_PASS_SUCCESS = 1027;
    public static final int RESULT_PASS_FAILED = 1028;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tabProfile) TabLayout tabProfile;
    @BindView(R.id.frame_profile) ViewPagerCustom mViewPager;

    private View tabitem;
    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;

    private FirebaseAuth mFirebaseAuth;

    private UpdateProfileAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        ButterKnife.bind(this);

        snackBarUtils = new SnackBarUtils(this);
        barUtils = new ProgressBarUtils(this);

        toolbar.setTitle(getString(R.string.toolbar_edit, getString(R.string.menu_9)));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        onAuthenticationFirebase();

        initTabLayout();
    }

    @Override
    public void onAuthenticationFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFirebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), AuthActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    @Override
    public void initTabLayout() {
        tabProfile.setupWithViewPager(mViewPager);
        mAdapter = new UpdateProfileAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new DataUserFragment(), getString(R.string.tab_data_diri));
        mAdapter.addFragment(new PasswordFragment(), getString(R.string.tab_password));
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setEnabled(false);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabProfile));
        tabProfile.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabProfile.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    public void onAttachDataUserFragment() {
        Log.d(TAG, "onAttachDataUserFragment()");
    }

    @Override
    public void onFailedLoadData() {
        setResult(RESULT_EDIT_FAILED);
        finish();
    }

    @Override
    public void onUpdateDataUserSuccess() {
        setResult(RESULT_EDIT_SUCCESS);
        finish();
    }

    @Override
    public void onUpdateDataUserFailed(Intent intent) {
        setResult(RESULT_EDIT_FAILED, intent);
        finish();
    }

    @Override
    public void onAttachPasswordFragment() {
        Log.d(TAG, "onAttachPasswordFragment() called");
    }

    @Override
    public void onUpdatePasswordSuccess() {
        setResult(RESULT_PASS_SUCCESS);
        finish();
    }

    @Override
    public void onUpdatePasswordFailed(Intent intent) {
        setResult(RESULT_PASS_FAILED, intent);
        finish();
    }
}
