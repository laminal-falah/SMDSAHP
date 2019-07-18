package pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.inisiasi;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.fragment.SubKriteriaFragment;

public interface PerbandinganSubKriteriaFragmentInterface extends SubKriteriaFragment.Callbacks {
    void onSetupFirestore();
    void getKriteria(Task<QuerySnapshot> task);
    void onSetupTabLayout();
    void tabItemSelected();
}
