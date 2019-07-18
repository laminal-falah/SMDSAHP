package pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.MenuDashboard;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.fragment.PerbandinganKriteriaFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.fragment.PerbandinganSubKriteriaFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.inisiasi.HitungPerbandinganInterface;

public class HitungPerbandinganActivity extends AppCompatActivity implements HitungPerbandinganInterface {

    private static final String TAG = HitungPerbandinganActivity.class.getSimpleName();

    public static final int REQUEST_ADD = 173;
    public static final int RESULT_ADD_SUCCESS = 164;
    public static final int RESULT_ADD_FAILED = 533;

    public static final int REQUEST_REFRESH = 847;
    public static final int RESULT_REFRESH_SUCCESS = 234;
    public static final int RESULT_REFRESH_FAILED = 325;

    @BindView(R.id.toolbar) Toolbar toolbar;

    private String[] title;
    private int navFragmentIndex = 0;
    private int position;
    private List<MenuDashboard> menus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hitung_perbandingan);
        ButterKnife.bind(this);

        title = getResources().getStringArray(R.array.toolbar_hitung);

        menus = new ArrayList<>();

        if (savedInstanceState == null) {
            navFragmentIndex = 0;
            onViewPerbandinganKriteria();
            position = 0;
        }

        if (navFragmentIndex == 0) {
            setToolbarTitle();
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        if (menus.size() < 2) {
            finish();
            menus.remove(0);
            position = 0;
        } else {
            menus.remove(position);
            position--;
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                if (getSupportFragmentManager().findFragmentByTag(menus.get(position).getTag()) != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .remove(menus.get(position).getFragment())
                            .commit();
                }
                getSupportFragmentManager().popBackStackImmediate();
            }
            setToolbarTitle();
        }
    }

    @Override
    public void setToolbarTitle() {
        toolbar.setTitle(menus.get(position).getTitle());
    }

    @Override
    public void onViewPerbandinganKriteria() {
        navFragmentIndex = 0;
        onFragmentPerbandingan(new PerbandinganKriteriaFragment());
    }

    @Override
    public void onViewPerbandinganSubKriteria() {
        navFragmentIndex = 1;
        onFragmentPerbandingan(new PerbandinganSubKriteriaFragment());
    }

    @Override
    public void onFragmentPerbandingan(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.frame_hitung_perbandingan, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
        if (menus.size() > 0) {
            position++;
        } else {
            position = 0;
        }

        menus.add(new MenuDashboard(fragment.getClass().getSimpleName(), fragment, navFragmentIndex, title[navFragmentIndex]));

        setToolbarTitle();
    }

    @Override
    public void onAttachPerbandinganKriteriaFragment() {
        Log.d(TAG, "onAttachPerbandinganKriteriaFragment() called");
    }

    @Override
    public void onDetachPerbandinganKriteriaFragment() {
        finish();
    }

    @Override
    public void onAttachResultPerbandingan() {
        Log.d(TAG, "onAttachResultPerbandingan() called");
    }

    @Override
    public void onAttachPerbandinganSubKriteriaFragment() {
        Log.d(TAG, "onAttachPerbandinganSubKriteriaFragment() called");
    }

    @Override
    public void onDetachPerbandinganSubKriteriaFragment() {
        finish();
    }

    @Override
    public void onDetachResultPerbandingan() {
        finish();
    }

    @Override
    public void onSubKriteriaFragment() {
        onAttachPerbandinganSubKriteriaFragment();
        onViewPerbandinganSubKriteria();
    }
}
