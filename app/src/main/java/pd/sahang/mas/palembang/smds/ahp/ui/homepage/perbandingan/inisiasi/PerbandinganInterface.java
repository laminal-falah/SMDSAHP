package pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.inisiasi;

import pd.sahang.mas.palembang.smds.ahp.adapters.PerbandinganAdapter;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.dialog.DialogDetailHasilPerbandingan;

public interface PerbandinganInterface extends PerbandinganAdapter.OnPerbandinganListener,
        DialogDetailHasilPerbandingan.Callbacks {
    void onCheckKriteria();
    void onCheckHasilPerbandingan();
    void onInitHasilPerbandingan();
    void onInitFirestore();
    void onInitRecyclerView();
}
