package pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.filter;

import androidx.lifecycle.ViewModel;

public class KriteriaViewModel extends ViewModel {
    private FilterKriteria filterKriteria;

    public KriteriaViewModel() {
        filterKriteria = FilterKriteria.getDefault();
    }

    public FilterKriteria getFilterKriteria() {
        return filterKriteria;
    }

    public void setFilterKriteria(FilterKriteria filterKriteria) {
        this.filterKriteria = filterKriteria;
    }
}
