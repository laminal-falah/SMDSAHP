package pd.sahang.mas.palembang.smds.ahp.ui.homepage;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.MenuDashboard;
import pd.sahang.mas.palembang.smds.ahp.models.Users;
import pd.sahang.mas.palembang.smds.ahp.ui.auth.AuthActivity;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.dashboard.DashboardFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.inisiasi.DashboardActivityInterface;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.AddEditKriteriaActivity;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.DetailKriteriaActivity;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.KriteriaFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan.AddEditLaporanActivity;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan.LaporanFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.AddEditUserActivity;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.PenggunaFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.HitungPerbandinganActivity;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.PerbandinganFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.ProfileFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.UpdateProfileActivity;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.tentang.TentangFragment;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SharedPrefFirebase;
import pd.sahang.mas.palembang.smds.ahp.utils.SharedPrefManager;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.UserHelper;

public class DashboardActivity extends AppCompatActivity implements DashboardActivityInterface {

    private static final String TAG = DashboardActivity.class.getSimpleName();

    private View mNavHeader;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.addKriteria) FloatingActionButton addKriteria;
    @BindView(R.id.addPerbandingan) FloatingActionButton addPerbandingan;
    @BindView(R.id.refreshPerbandingan) FloatingActionButton refreshPerbandingan;
    @BindView(R.id.addLaporan) FloatingActionButton addLaporan;
    @BindView(R.id.addUser) FloatingActionButton addUser;

    private TextView tvHeaderName;
    private TextView tvHeaderEmail;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore mFirestore;
    private DocumentReference mReference;
    private ListenerRegistration mHeaderRegister;

    private SharedPrefManager mPrefManager;

    private int navItemIndex = 0;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;
    private ActionBarDrawerToggle toggle;

    private String name, email;
    private String[] title;
    private int position;
    private int jumlahData;
    private int kriteriaSize, perbandinganSize;
    private boolean exist;

    private List<MenuDashboard> menuDashboardList;

    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        barUtils = new ProgressBarUtils(this);
        snackBarUtils = new SnackBarUtils(this);
        mPrefManager = new SharedPrefManager(this);

        menuDashboardList = new ArrayList<>();

        if (savedInstanceState == null) {
            if (mPrefManager.getSpLevel().equals("admin")) {
                title = getResources().getStringArray(R.array.menu_admin);
                if (menuDashboardList.size() == 0) {
                    navItemIndex = 0;
                    onViewDashboard();
                    position = 0;
                }
            } else {
                title = getResources().getStringArray(R.array.menu_user);
                if (menuDashboardList.size() == 0) {
                    navItemIndex = 0;
                    onViewLaporan();
                    position = 0;
                }
            }
        }

        if (mPrefManager.getSpLevel().equals("user")) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.user_dashboard_drawer);
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.admin_dashboard_drawer);
        }

        if (navItemIndex == 0 && menuDashboardList.size() > 0) {
            setToolbarTitle();
            setNavigationMenu();
        }

        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        mNavHeader = navigationView.getHeaderView(0);
        tvHeaderEmail = mNavHeader.findViewById(R.id.headerEmail);
        tvHeaderName = mNavHeader.findViewById(R.id.headerName);

        onAuthenticationFirebase();

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mFirestore = FirebaseFirestore.getInstance();

        mReference = mFirestore.collection(Users.COLLECTION).document(mFirebaseUser.getUid());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHeaderRegister = mReference.addSnapshotListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mHeaderRegister != null) {
            mHeaderRegister.remove();
            mHeaderRegister = null;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddEditKriteriaActivity.REQUEST_ADD) {
            if (resultCode == AddEditKriteriaActivity.RESULT_ADD_SUCCESS) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_success,
                        getString(R.string.snack_bar_add).toLowerCase(),
                        getString(R.string.menu_1).toLowerCase()
                ));
            } else if (resultCode == AddEditKriteriaActivity.RESULT_ADD_FAILED) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_error,
                        data.getStringExtra("error")
                ));
            }
        } else if (requestCode == AddEditKriteriaActivity.REQUEST_UPDATE) {
            if (resultCode == AddEditKriteriaActivity.RESULT_UPDATE_SUCCESS) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_success,
                        getString(R.string.snack_bar_update).toLowerCase(),
                        getString(R.string.menu_1).toLowerCase()
                ));
            } else if (resultCode == AddEditKriteriaActivity.RESULT_UPDATE_FAILED) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_error,
                        data.getStringExtra("error")
                ));
            }
        } else if (requestCode == HitungPerbandinganActivity.REQUEST_ADD) {
            if (resultCode == HitungPerbandinganActivity.RESULT_ADD_SUCCESS) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_success,
                        getString(R.string.snack_bar_add).toLowerCase(),
                        getString(R.string.menu_5).toLowerCase()
                ));
            } else if (resultCode == HitungPerbandinganActivity.RESULT_ADD_FAILED) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_error,
                        data.getStringExtra("error")
                ));
            }
        } else if (requestCode == AddEditLaporanActivity.REQUEST_ADD_TIPE_2) {
            if (resultCode == AddEditLaporanActivity.RESULT_ADD_TIPE_2_SUCCESS) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_success,
                        getString(R.string.snack_bar_add).toLowerCase(),
                        getString(R.string.menu_7).toLowerCase()
                ));
            } else if (resultCode == AddEditLaporanActivity.RESULT_ADD_TIPE_2_FAILED) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_error,
                        data.getStringExtra("error")
                ));
            }
        } else if (requestCode == HitungPerbandinganActivity.REQUEST_REFRESH) {
            if (resultCode == HitungPerbandinganActivity.RESULT_REFRESH_SUCCESS) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_success,
                        getString(R.string.snack_bar_update).toLowerCase(),
                        getString(R.string.menu_5).toLowerCase()
                ));
            } else if (resultCode == HitungPerbandinganActivity.RESULT_REFRESH_FAILED) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_error,
                        data.getStringExtra("error")
                ));
            }
        } else if (requestCode == AddEditUserActivity.REQUEST_ADD) {
            if (resultCode == AddEditUserActivity.RESULT_ADD_SUCCESS) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_success,
                        getString(R.string.snack_bar_add).toLowerCase(),
                        getString(R.string.menu_8).toLowerCase()
                ));
            } else if (resultCode == AddEditUserActivity.RESULT_ADD_FAILED && data != null) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_error,
                        data.getStringExtra("error")
                ));
            }
        } else if (requestCode == AddEditUserActivity.REQUEST_UPDATE) {
            if (resultCode == AddEditUserActivity.RESULT_UPDATE_SUCCESS) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_success,
                        getString(R.string.snack_bar_update).toLowerCase(),
                        getString(R.string.menu_8).toLowerCase()
                ));
            } else if (resultCode == AddEditUserActivity.RESULT_UPDATE_FAILED && data != null) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_error,
                        data.getStringExtra("error")
                ));
            }
        } else if (requestCode == UpdateProfileActivity.REQUEST_EDIT) {
            if (resultCode == UpdateProfileActivity.RESULT_EDIT_SUCCESS) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_success,"ubah", "data diri"));
            } else if (resultCode == UpdateProfileActivity.RESULT_EDIT_FAILED) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_error,data.getStringExtra("error")));
            } else if (resultCode == UpdateProfileActivity.RESULT_PASS_SUCCESS) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_success,"ubah", "kata sandi"));
            } else if (resultCode == UpdateProfileActivity.RESULT_PASS_FAILED) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_error,data.getStringExtra("error")));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mPrefManager.getSpLevel().equals("admin")) {
            if (menuDashboardList.get(position).getPosition() == 5) {
                getMenuInflater().inflate(R.menu.profile, menu);
            }
        } else {
            if (menuDashboardList.get(position).getPosition() == 1) {
                getMenuInflater().inflate(R.menu.profile, menu);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (mPrefManager.getSpLevel().equals("admin")) {
            if (menuDashboardList.get(position).getPosition() == 5) {
                if (id == R.id.menu_logout) {
                    onLogoutProfile();
                }
                if (id == R.id.menu_edit_profile) {
                    onUpdateProfile();
                }
            }
        } else {
            if (menuDashboardList.get(position).getPosition() == 1) {
                if (id == R.id.menu_logout) {
                    onLogoutProfile();
                }
                if (id == R.id.menu_edit_profile) {
                    onUpdateProfile();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (menuDashboardList.size() < 2) {
                if (back_pressed + 3000 > System.currentTimeMillis()) {
                    finish();
                    menuDashboardList.remove(0);
                    position = 0;
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_exit), Toast.LENGTH_SHORT).show();
                }
                back_pressed = System.currentTimeMillis();
            } else {
                menuDashboardList.remove(position);
                position--;
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    if (getSupportFragmentManager().findFragmentByTag(menuDashboardList.get(position).getTag()) != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                .remove(menuDashboardList.get(position).getFragment())
                                .commit();
                    }
                    getSupportFragmentManager().popBackStackImmediate();
                }
                setToolbarTitle();
                setNavigationMenu();

                invalidateOptionsMenu();

            }
            setFabPosition(menuDashboardList.get(position).getPosition());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            onViewDashboard();
        } else if (id == R.id.nav_kriteria) {
            onViewKriteria();
        } else if (id == R.id.nav_hasil) {
            onViewHasilPerbandingan();
        } else if (id == R.id.nav_laporan) {
            onViewLaporan();
        } else if (id == R.id.nav_users) {
            onViewDataPengguna();
        } else if (id == R.id.nav_profil) {
            onViewProfile();
        } else if (id == R.id.nav_tentang) {
            onViewTentang();
        }

        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }

        item.setChecked(true);

        setToolbarTitle();
        setNavigationMenu();

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "onEvent: ", e);
            return;
        }

        if (snapshot != null) {
            email = null;
            name = null;
            tvHeaderEmail.setText(null);
            tvHeaderName.setText(null);
            getUserFirebase(snapshot);
        }
    }

    @Override
    public void onAuthenticationFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFirebaseAuth.getCurrentUser() == null) {
            mPrefManager.clearShared();
            startActivity(new Intent(getApplicationContext(), AuthActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    @Override
    public void getUserFirebase(DocumentSnapshot snapshot) {
        if (snapshot.exists()) {
            Users u = snapshot.toObject(Users.class);
            email = u.getEmail();
            name = u.getFullname();

            tvHeaderEmail.setText(email);
            tvHeaderName.setText(name);
        } else {
            onLogoutProfile();
        }
    }

    @Override
    public void setToolbarTitle() {
        toolbar.setTitle(menuDashboardList.get(position).getTitle());
    }

    @Override
    public void setNavigationMenu() {
        navigationView.getMenu().getItem(menuDashboardList.get(position).getPosition()).setChecked(true);
    }

    @Override
    public void onViewDashboard() {
        navItemIndex = 0;
        setEnableDrawerLayout(false);
        onFragmentAttach(new DashboardFragment());
    }

    @Override
    public void onViewKriteria() {
        navItemIndex = 1;
        setEnableDrawerLayout(false);
        onFragmentAttach(new KriteriaFragment());
    }

    @Override
    public void onViewHasilPerbandingan() {
        navItemIndex = 2;
        setEnableDrawerLayout(false);
        onFragmentAttach(new PerbandinganFragment());
    }

    @Override
    public void onViewLaporan() {
        if (mPrefManager.getSpLevel().equals("admin")) {
            navItemIndex = 3;
        } else {
            navItemIndex = 0;
        }
        setEnableDrawerLayout(false);
        onFragmentAttach(new LaporanFragment());
    }

    @Override
    public void onViewDataPengguna() {
        navItemIndex = 4;
        setEnableDrawerLayout(false);
        onFragmentAttach(new PenggunaFragment());
    }

    @Override
    public void onViewProfile() {
        if (mPrefManager.getSpLevel().equals("admin")) {
            navItemIndex = 5;
        } else {
            navItemIndex = 1;
        }
        setEnableDrawerLayout(false);
        onFragmentAttach(new ProfileFragment());
    }

    @Override
    public void onViewTentang() {
        if (mPrefManager.getSpLevel().equals("admin")) {
            navItemIndex = 6;
        } else {
            navItemIndex = 2;
        }
        setEnableDrawerLayout(false);
        onFragmentAttach(new TentangFragment());
    }

    @Override
    public void onFragmentAttach(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.frame_dashboard, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();

        if (menuDashboardList.size() > 0) {
            position++;
        } else {
            position = 0;
        }
        menuDashboardList.add(new MenuDashboard(fragment.getClass().getSimpleName(), fragment, navItemIndex, title[navItemIndex]));

        setFabPosition(menuDashboardList.get(position).getPosition());

        invalidateOptionsMenu();
    }

    @Override
    public void setEnableDrawerLayout(boolean enabled) {
        int mode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawer.setDrawerLockMode(mode);
    }

    @Override
    public void setFabPosition(int position) {
        if (mPrefManager.getSpLevel().equals("admin")) {
            if (position == 0) {
                addKriteria.hide();
                addPerbandingan.hide();
                refreshPerbandingan.hide();
                addLaporan.hide();
                addUser.hide();
            } else if (position == 1) {
                if (getJumlahData() < 15) {
                    addKriteria.show();
                    addKriteria.setOnClickListener(v -> onAddKriteriaFragment());
                } else {
                    addKriteria.hide();
                }
                addUser.hide();
                addPerbandingan.hide();
                refreshPerbandingan.hide();
                addLaporan.hide();
            } else if (position == 2) {
                addKriteria.hide();
                if (getKriteriaSize() > 2) {
                    if (getKriteriaSize() == getPerbandinganSize()) {
                        refreshPerbandingan.show();
                        refreshPerbandingan.setOnClickListener(v -> onAddPerbandinganFragment(getPerbandinganSize()));
                    } else {
                        refreshPerbandingan.hide();
                        addPerbandingan.hide();
                        if (getKriteriaSize() < getPerbandinganSize()) {
                            refreshPerbandingan.show();
                            refreshPerbandingan.setOnClickListener(v -> onAddPerbandinganFragment(getPerbandinganSize()));
                            snackBarUtils.snackBarLong("Hitung ulang perbandingan !");
                        } else if (getKriteriaSize() > getPerbandinganSize()) {
                            refreshPerbandingan.show();
                            refreshPerbandingan.setOnClickListener(v -> onAddPerbandinganFragment(getPerbandinganSize()));
                            snackBarUtils.snackBarLong("Hitung ulang perbandingan !");
                        } else {
                            addPerbandingan.show();
                            addPerbandingan.setOnClickListener(v -> onAddPerbandinganFragment(getPerbandinganSize()));
                        }
                    }
                } else {
                    addPerbandingan.hide();
                    refreshPerbandingan.hide();
                }
                addLaporan.hide();
                addUser.hide();
            } else if (position == 3) {
                addKriteria.hide();
                addPerbandingan.hide();
                refreshPerbandingan.hide();
                if (!isExist()) {
                    addLaporan.show();
                    addLaporan.setOnClickListener(v -> onAddLaporanFragment());
                } else {
                    addLaporan.hide();
                }
                addUser.hide();
            } else if (position == 4) {
                addKriteria.hide();
                addPerbandingan.hide();
                refreshPerbandingan.hide();
                addLaporan.hide();
                addUser.show();
                addUser.setOnClickListener(v -> onAddPenggunaFragment());
            } else if (position == 5) {
                addUser.hide();
                addKriteria.hide();
                addPerbandingan.hide();
                refreshPerbandingan.hide();
                addLaporan.hide();
            } else if (position == 6) {
                addUser.hide();
                addKriteria.hide();
                addPerbandingan.hide();
                refreshPerbandingan.hide();
                addLaporan.hide();
            }
        } else {
            if (position == 0) {
                addKriteria.hide();
                addPerbandingan.hide();
                refreshPerbandingan.hide();
                if (!isExist()) {
                    addLaporan.show();
                    addLaporan.setOnClickListener(v -> onAddLaporanFragment());
                } else {
                    addLaporan.hide();
                }
                addUser.hide();
            } else if (position == 1 || position == 2) {
                addUser.hide();
                addKriteria.hide();
                addPerbandingan.hide();
                refreshPerbandingan.hide();
                addLaporan.hide();
            }
        }
    }

    @Override
    public void onAttachDashboard() {
        new Handler().postDelayed(() -> setEnableDrawerLayout(true), 2000);
    }

    @Override
    public void onAttachKriteria(int count) {
        new Handler().postDelayed(() -> setEnableDrawerLayout(true), 2000);
        if (count < 15) {
            addKriteria.show();
        } else {
            addKriteria.hide();
        }
        setJumlahData(count);
    }

    @Override
    public void onAddKriteriaFragment() {
        startActivityForResult(new Intent(this, AddEditKriteriaActivity.class)
                        .putExtra(AddEditKriteriaActivity.KEY_ID, AddEditKriteriaActivity.ADD_DATA),
                AddEditKriteriaActivity.REQUEST_ADD);
    }

    @Override
    public void onEditKriteriaFragment(String id) {
        startActivityForResult(new Intent(this, AddEditKriteriaActivity.class)
                        .putExtra(AddEditKriteriaActivity.KEY_ID, id),
                AddEditKriteriaActivity.REQUEST_UPDATE);
    }

    @Override
    public void onDetailKriteriaFragment(String id) {
        startActivity(new Intent(this, DetailKriteriaActivity.class)
                .putExtra(DetailKriteriaActivity.ID, id)
        );
    }

    @Override
    public void onAttachPerbandinganFragment(int kriteria, int perbandingan) {
        new Handler().postDelayed(() -> setEnableDrawerLayout(true), 2000);
        setKriteriaSize(kriteria);
        setPerbandinganSize(perbandingan);
        if (getKriteriaSize() > 2) {
            if (getKriteriaSize() == getPerbandinganSize()) {
                refreshPerbandingan.show();
                refreshPerbandingan.setOnClickListener(v -> onAddPerbandinganFragment(getPerbandinganSize()));
            } else {
                refreshPerbandingan.hide();
                addPerbandingan.hide();
                if (getKriteriaSize() < getPerbandinganSize()) {
                    refreshPerbandingan.show();
                    refreshPerbandingan.setOnClickListener(v -> onAddPerbandinganFragment(getPerbandinganSize()));
                    snackBarUtils.snackBarLong("Hitung ulang perbandingan !");
                } else if (getKriteriaSize() > getPerbandinganSize()) {
                    refreshPerbandingan.show();
                    refreshPerbandingan.setOnClickListener(v -> onAddPerbandinganFragment(getPerbandinganSize()));
                    snackBarUtils.snackBarLong("Hitung ulang perbandingan !");
                } else {
                    addPerbandingan.show();
                    addPerbandingan.setOnClickListener(v -> onAddPerbandinganFragment(getPerbandinganSize()));
                }
            }
        } else {
            addPerbandingan.hide();
            refreshPerbandingan.hide();
        }
    }

    @Override
    public void onAddPerbandinganFragment(int perbandingan) {
        if (perbandingan > 0) {
            startActivityForResult(new Intent(this, HitungPerbandinganActivity.class),
                    HitungPerbandinganActivity.REQUEST_REFRESH);
        } else {
            startActivityForResult(new Intent(this, HitungPerbandinganActivity.class),
                    HitungPerbandinganActivity.REQUEST_ADD);
        }
    }

    @Override
    public void onAttachLaporanFragment(boolean exist) {
        new Handler().postDelayed(() -> setEnableDrawerLayout(true), 2000);
        setExist(exist);
        if (!isExist()) {
            addLaporan.show();
            addLaporan.setOnClickListener(v -> onAddLaporanFragment());
        } else {
            addLaporan.hide();
        }
    }

    @Override
    public void onAddLaporanFragment() {
        startActivityForResult(new Intent(this, AddEditLaporanActivity.class)
                        .putExtra(AddEditLaporanActivity.KEY_ID, AddEditLaporanActivity.ADD_DATA_TIPE_2),
                AddEditLaporanActivity.REQUEST_ADD_TIPE_2);
    }

    @Override
    public void onAttachPenggunaFragment() {
        new Handler().postDelayed(() -> setEnableDrawerLayout(true), 2000);
    }

    @Override
    public void onAddPenggunaFragment() {
        startActivityForResult(new Intent(this, AddEditUserActivity.class)
                        .putExtra(AddEditUserActivity.KEY_ID, AddEditUserActivity.ADD_DATA),
                AddEditUserActivity.REQUEST_ADD);
    }

    @Override
    public void onEditPenggunaFragment(String id) {
        startActivityForResult(new Intent(this, AddEditUserActivity.class)
                        .putExtra(AddEditUserActivity.KEY_ID, id),
                AddEditUserActivity.REQUEST_UPDATE);
    }

    @Override
    public void onAttachProfile() {
        new Handler().postDelayed(() -> setEnableDrawerLayout(true), 2000);
    }

    @Override
    public void onUpdateProfile() {
        startActivityForResult(new Intent(this, UpdateProfileActivity.class), UpdateProfileActivity.REQUEST_EDIT);
    }

    @Override
    public void onLogoutProfile() {
        barUtils.show();
        if (mPrefManager.getSpLevel().equals("admin")) {
            UserHelper.updateToken(mFirebaseUser.getUid(), null);
        }
        new Handler().postDelayed(() -> {
            mFirebaseAuth.signOut();
            mPrefManager.clearShared();
            SharedPrefFirebase.getInstance(this).saveDeviceToken(null);
            Toast.makeText(getApplicationContext(), getString(R.string.msg_logout), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }, 2000);
    }

    @Override
    public void onAttachTentang() {
        new Handler().postDelayed(() -> setEnableDrawerLayout(true), 2000);
    }

    public int getJumlahData() {
        return jumlahData;
    }

    public void setJumlahData(int jumlahData) {
        this.jumlahData = jumlahData;
    }

    public int getKriteriaSize() {
        return kriteriaSize;
    }

    public void setKriteriaSize(int kriteriaSize) {
        this.kriteriaSize = kriteriaSize;
    }

    public int getPerbandinganSize() {
        return perbandinganSize;
    }

    public void setPerbandinganSize(int perbandinganSize) {
        this.perbandinganSize = perbandinganSize;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }
}
