package pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.filter;

import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {

    private FilterUser mFilterUser;

    public UserViewModel() {
        mFilterUser = FilterUser.getDefault();
    }

    public FilterUser getmFilterUser() {
        return mFilterUser;
    }

    public void setmFilterUser(FilterUser mFilterUser) {
        this.mFilterUser = mFilterUser;
    }
}
