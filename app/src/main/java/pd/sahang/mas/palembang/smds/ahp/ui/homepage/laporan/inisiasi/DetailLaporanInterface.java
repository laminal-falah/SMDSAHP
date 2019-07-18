package pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan.inisiasi;

import pd.sahang.mas.palembang.smds.ahp.adapters.DetailLaporanAdapter;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan.dialog.DialogDetailLaporan;

public interface DetailLaporanInterface extends DetailLaporanAdapter.OnDetailLaporanListener, DialogDetailLaporan.Callbacks {
    void onAuthenticationFirebase();
    void onInitFirestore();
    void onRecyclerView();
}
