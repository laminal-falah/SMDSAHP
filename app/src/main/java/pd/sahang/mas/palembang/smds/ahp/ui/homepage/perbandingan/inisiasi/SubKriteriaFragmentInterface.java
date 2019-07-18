package pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.inisiasi;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.dialog.DialogResultPerbandingan;

public interface SubKriteriaFragmentInterface extends DialogResultPerbandingan.SubKriteriaFragmentListener {
    void onSetupFirestore();
    void onSubKriteriaLoad(Task<QuerySnapshot> task);
    void onRecyclerView();
    boolean onValidate();
    void onProcessCounting();
    void setPerbandinganSubKriteria(String subKodeKriteria, String subNamaKriteria, int subTipeNilai, double subNilaiMinKriteria, double subNilaiMaxKriteria, String priorityVektor);
}
