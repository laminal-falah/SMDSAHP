package pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.inisiasi;

import com.google.firebase.firestore.DocumentSnapshot;

public interface DataUserInterface {
    void initFirestore();
    void getData(DocumentSnapshot snapshot);
    boolean validate();
}
