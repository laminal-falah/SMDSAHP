package pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.inisiasi;

import pd.sahang.mas.palembang.smds.ahp.adapters.KriteriaAdapter;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.dialog.DialogFilterKriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.dialog.DialogSearchKriteria;

public interface KriteriaInterface extends KriteriaAdapter.OnKriteriaSelectedListener,
        DialogFilterKriteria.Callbacks, DialogSearchKriteria.Callbacks {
    void onInitFirestore();
    void onInitRecyclerView();
    void onRefreshLayout();
    void onRunningGetSubKriteria(String id);
    void onRunningDeleteSubKriteria(String idKriteria, String idSubKriteria);
    void onStopRunningDeleteSubKriteria();
    void onRunningDeleteKriteria(String id);
}
