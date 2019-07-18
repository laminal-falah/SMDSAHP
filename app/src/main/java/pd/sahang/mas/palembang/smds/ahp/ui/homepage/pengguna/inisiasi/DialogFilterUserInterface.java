package pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.inisiasi;

import com.google.firebase.firestore.Query;

import javax.annotation.Nullable;

import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.filter.FilterUser;

public interface DialogFilterUserInterface {
    @Nullable
    String getSelectedSortBy();
    @Nullable
    Query.Direction getSortDirection();
    FilterUser getFilters();
    void onResetFilters();
}
