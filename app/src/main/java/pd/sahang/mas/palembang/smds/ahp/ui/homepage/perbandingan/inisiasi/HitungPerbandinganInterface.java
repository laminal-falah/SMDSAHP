package pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.inisiasi;

import androidx.fragment.app.Fragment;

import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.dialog.DialogResultPerbandingan;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.fragment.PerbandinganKriteriaFragment;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.fragment.PerbandinganSubKriteriaFragment;

public interface HitungPerbandinganInterface extends PerbandinganKriteriaFragment.Callbacks,
        PerbandinganSubKriteriaFragment.Callbacks, DialogResultPerbandingan.KriteriaFragmentListener {
    void setToolbarTitle();
    void onViewPerbandinganKriteria();
    void onViewPerbandinganSubKriteria();
    void onFragmentPerbandingan(Fragment fragment);
}
