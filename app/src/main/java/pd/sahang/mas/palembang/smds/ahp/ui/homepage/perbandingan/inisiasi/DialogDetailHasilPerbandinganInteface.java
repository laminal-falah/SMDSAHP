package pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.inisiasi;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;

import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganKriteria;

public interface DialogDetailHasilPerbandinganInteface extends EventListener<DocumentSnapshot> {
    void onPerbandinganKriteriaLoad(PerbandinganKriteria kriteria);
    void onSubPerbandinganFirestore();
    void onRecyclerView();
}
