package pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.Users;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.filter.FilterUser;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.inisiasi.DialogFilterUserInterface;

public class DialogFilterUser extends DialogFragment implements DialogFilterUserInterface {
    public static final String TAG = DialogFilterUser.class.getSimpleName();

    private View mViewFilter;

    @BindView(R.id.spinner_sort) Spinner mSpinnerSort;

    private Callbacks mCallbacks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewFilter = inflater.inflate(R.layout.app_filter, container, false);
        ButterKnife.bind(this, mViewFilter);
        getDialog().setCanceledOnTouchOutside(true);
        return mViewFilter;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.x = 200;
        getDialog().getWindow().setAttributes(p);
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callbacks) {
            mCallbacks = (Callbacks) context;
        } else {
            if (getParentFragment() != null && getParentFragment() instanceof Callbacks) {
                mCallbacks = (Callbacks) getParentFragment();
            } else {
                throw new RuntimeException(context.toString() + " must implement Callbacks");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Nullable
    @Override
    public String getSelectedSortBy() {
        String selected = (String) mSpinnerSort.getSelectedItem();
        if (getString(R.string.value_any_sort).equals(selected)) {
            return Users.TIMESTAMPS;
        }
        if (getString(R.string.sort_by_name_0).equals(selected) || getString(R.string.sort_by_name_1).equals(selected)) {
            return Users.FIELD_FULLNAME;
        }
        return null;
    }

    @Nullable
    @Override
    public Query.Direction getSortDirection() {
        String selected = (String) mSpinnerSort.getSelectedItem();
        if (getString(R.string.sort_by_name_0).equals(selected)) {
            return Query.Direction.ASCENDING;
        }
        if (getString(R.string.sort_by_name_1).equals(selected)) {
            return Query.Direction.DESCENDING;
        }
        return Query.Direction.DESCENDING;
    }

    @Override
    public FilterUser getFilters() {
        FilterUser filterUser = new FilterUser();
        if (mViewFilter != null) {
            filterUser.setSortBy(getSelectedSortBy());
            filterUser.setSortDirection(getSortDirection());
        }
        return filterUser;
    }

    @Override
    public void onResetFilters() {
        if (mViewFilter != null) {
            mSpinnerSort.setSelection(0);
        }
    }

    @OnClick(R.id.button_apply) public void onSearchClicked() {
        if (mCallbacks != null) {
            mCallbacks.onFilterUser(getFilters());
        }
        dismiss();
    }

    @OnClick(R.id.button_cancel) public void onCancelClicked() {
        dismiss();
    }

    @OnClick(R.id.button_reset) public void onResetClicked() {
        onResetFilters();
        if (mCallbacks != null) {
            mCallbacks.onFilterUser(getFilters());
        }

        dismiss();
    }

    public interface Callbacks {
        void onFilterUser(FilterUser filterUser);
    }
}
