package pd.sahang.mas.palembang.smds.ahp.ui.homepage.inisiasi;

import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;

import pd.sahang.mas.palembang.smds.ahp.ui.homepage.dashboard.DashboardFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.KriteriaFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan.LaporanFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.PenggunaFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.PerbandinganFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.ProfileFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.tentang.TentangFragment;

public interface DashboardActivityInterface extends NavigationView.OnNavigationItemSelectedListener,
        EventListener<DocumentSnapshot>, DashboardFragment.Callbacks, KriteriaFragment.Callbacks,
        PerbandinganFragment.Callbacks, LaporanFragment.Callbacks, PenggunaFragment.Callbacks,
        ProfileFragment.Callbacks, TentangFragment.Callbacks {

    void onAuthenticationFirebase();
    void getUserFirebase(DocumentSnapshot snapshot);
    void setToolbarTitle();
    void setNavigationMenu();
    void onViewDashboard();
    void onViewKriteria();
    void onViewHasilPerbandingan();
    void onViewLaporan();
    void onViewDataPengguna();
    void onViewProfile();
    void onViewTentang();
    void onFragmentAttach(Fragment fragment);
    void setEnableDrawerLayout(boolean enabled);
    void setFabPosition(int position);
}
