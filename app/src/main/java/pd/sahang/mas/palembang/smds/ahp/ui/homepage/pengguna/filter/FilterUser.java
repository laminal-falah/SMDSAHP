package pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.filter;

import android.text.TextUtils;

import com.google.firebase.firestore.Query;

import pd.sahang.mas.palembang.smds.ahp.models.Users;

public class FilterUser {
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public FilterUser() {
    }

    public static FilterUser getDefault() {
        FilterUser filterUser = new FilterUser();
        filterUser.setSortBy(Users.TIMESTAMPS);
        filterUser.setSortDirection(Query.Direction.DESCENDING);
        return filterUser;
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
