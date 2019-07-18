package pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.inisiasi;

import pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.sub.DataUserFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.sub.PasswordFragment;

public interface UpdateProfileInterface extends DataUserFragment.Callbacks, PasswordFragment.Callbacks {
    void onAuthenticationFirebase();
    void initTabLayout();
}
