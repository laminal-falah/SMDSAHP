package pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.inisiasi;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.Query;

import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.filter.FilterKriteria;

public interface DialogFilterKriteriaInterface {
    @Nullable
    String getSelectedSortBy();
    @Nullable
    Query.Direction getSortDirection();
    FilterKriteria getFilters();
    void onResetFilters();
}
