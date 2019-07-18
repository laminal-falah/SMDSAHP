package pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.inisiasi;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;

public interface ProfileFragmentInterface extends EventListener<DocumentSnapshot> {
    void initProfile();
    void getData(DocumentSnapshot snapshot);
}
