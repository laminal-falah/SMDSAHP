package pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.Query;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.filter.FilterKriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.inisiasi.DialogSearchKriteriaInterface;

public class DialogSearchKriteria extends DialogFragment implements DialogSearchKriteriaInterface {

    public static final String TAG = DialogSearchKriteria.class.getSimpleName();

    private View mViewSearch;

    @BindView(R.id.icon_back) ImageView iconBack;
    @BindView(R.id.icon_search) ImageView iconSearch;
    @BindView(R.id.edt_query) EditText edtSearch;

    private Callbacks mCallbacks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewSearch = inflater.inflate(R.layout.app_search, container, false);
        ButterKnife.bind(this, mViewSearch);
        getDialog().setCanceledOnTouchOutside(true);
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearch();
            }
            return false;
        });
        return mViewSearch;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtSearch.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onBack();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setGravity(Gravity.TOP | Gravity.CENTER);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
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
            if (getParentFragment() instanceof Callbacks) {
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
    public String getQuery() {
        return edtSearch.getText().toString();
    }

    @Nullable
    @Override
    public String getSelectedSortBy() {
        return Kriteria.TIMESTAMPS;
    }

    @Nullable
    @Override
    public Query.Direction getSortDirection() {
        return Query.Direction.DESCENDING;
    }

    @Override
    public FilterKriteria getFilters() {
        FilterKriteria filterKriteria = new FilterKriteria();

        if (mCallbacks != null) {
            filterKriteria.setSortBy(getSelectedSortBy());
            filterKriteria.setSortDirection(getSortDirection());
        }

        return filterKriteria;
    }

    @OnClick(R.id.icon_back) void onBack() {
        if (mViewSearch != null) {
            edtSearch.setText(null);
        }
        if (mCallbacks != null) {
            mCallbacks.onResetSearchListener(getFilters());
        }
        dismiss();
    }

    @OnClick(R.id.icon_search) void onSearch() {
        if (mCallbacks != null) {
            mCallbacks.onSearchListener(getQuery());
        }
    }

    public interface Callbacks {
        void onSearchListener(String query);
        void onResetSearchListener(FilterKriteria filterKriteria);
    }
}
