package pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.inisiasi;

import pd.sahang.mas.palembang.smds.ahp.adapters.UserAdapter;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.dialog.DialogDetailUser;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.dialog.DialogFilterUser;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.dialog.DialogSearchUser;

public interface PenggunaInterface extends UserAdapter.OnUserSelectedListener, DialogDetailUser.Callbacks,
        DialogFilterUser.Callbacks, DialogSearchUser.Callbacks {
    void onInitFirestore();
    void onInitRecyclerView();
    void onRefreshLayout();
}
