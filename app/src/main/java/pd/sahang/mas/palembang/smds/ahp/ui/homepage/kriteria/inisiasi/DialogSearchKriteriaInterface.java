package pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.inisiasi;

import com.google.firebase.firestore.Query;

import javax.annotation.Nullable;

import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.filter.FilterKriteria;

public interface DialogSearchKriteriaInterface {
    @Nullable
    String getQuery();
    @Nullable
    String getSelectedSortBy();
    @Nullable
    Query.Direction getSortDirection();
    FilterKriteria getFilters();
}
