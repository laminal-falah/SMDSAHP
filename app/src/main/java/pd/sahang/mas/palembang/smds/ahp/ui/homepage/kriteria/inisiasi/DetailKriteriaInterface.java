package pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.inisiasi;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;

import pd.sahang.mas.palembang.smds.ahp.adapters.SubKriteriaAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.dialog.DialogDetailSubKriteria;

public interface DetailKriteriaInterface extends EventListener<DocumentSnapshot>,
        SubKriteriaAdapter.OnSubKriteriaListener, DialogDetailSubKriteria.Callbacks {
    void onKriteriaLoad(Kriteria kriteria);
    void onAuthenticationFirebase();
    void onInitFirestore();
    void onRecyclerView();
}
