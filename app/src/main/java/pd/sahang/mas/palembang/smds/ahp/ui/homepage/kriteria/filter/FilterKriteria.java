package pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.filter;

import android.text.TextUtils;

import com.google.firebase.firestore.Query;

import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;

public class FilterKriteria {
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public FilterKriteria() {
    }

    public static FilterKriteria getDefault() {
        FilterKriteria filterKriteria = new FilterKriteria();
        filterKriteria.setSortBy(Kriteria.TIMESTAMPS);
        filterKriteria.setSortDirection(Query.Direction.DESCENDING);
        return filterKriteria;
    }

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }
}
