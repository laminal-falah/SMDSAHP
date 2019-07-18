package pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan.inisiasi;

import pd.sahang.mas.palembang.smds.ahp.adapters.LaporanAdapter;

public interface LaporanInterface extends LaporanAdapter.OnLaporanSelectedListener {
    void onInitFirestore();
    void onRecyclerView();
    void onExistDocument();
}
