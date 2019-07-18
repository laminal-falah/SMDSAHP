package pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.inisiasi;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public interface PerbandinganKriteriaFragmentInterface {
    void onFirestoreSetup();
    void onKriteriaLoad(Task<QuerySnapshot> task);
    void onRecyclerView();
    boolean onValidate();
    void onProcessCounting();
    void setPerbandinganKriteria(String kodeKriteria, String namaKriteria, String priorityVektor);
}
