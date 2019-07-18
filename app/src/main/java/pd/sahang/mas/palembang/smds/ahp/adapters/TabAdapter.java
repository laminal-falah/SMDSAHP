package pd.sahang.mas.palembang.smds.ahp.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.fragment.SubKriteriaFragment;

public class TabAdapter extends FragmentStatePagerAdapter {
    private final List<Kriteria> kriterias;
    private final int position;

    public TabAdapter(FragmentManager fm, List<Kriteria> kriterias, int position) {
        super(fm);
        this.kriterias = kriterias;
        this.position = position;
    }

    @Override
    public Fragment getItem(int position) {
        return SubKriteriaFragment.newInstance(position, kriterias);
    }

    @Override
    public int getCount() {
        return position;
    }
}
