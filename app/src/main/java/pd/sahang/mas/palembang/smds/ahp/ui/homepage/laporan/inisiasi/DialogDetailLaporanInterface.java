package pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan.inisiasi;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;

import pd.sahang.mas.palembang.smds.ahp.models.Users;

public interface DialogDetailLaporanInterface extends EventListener<DocumentSnapshot> {
    void onInitFirestore();
    void onInitStorage();
    void onRecyclerView();
    void onSetUsers(Users users);
}
