package pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.App;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.TabAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.inisiasi.PerbandinganSubKriteriaFragmentInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.ViewPagerCustom;

public class PerbandinganSubKriteriaFragment extends Fragment implements PerbandinganSubKriteriaFragmentInterface {

    private static final String TAG = PerbandinganSubKriteriaFragment.class.getSimpleName();

    private View mPerbandinganSub, tabItem;

    @BindView(R.id.tabKriteria) TabLayout tabKriteria;
    @BindView(R.id.frame_sub_kriteria) ViewPagerCustom mViewPager;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;

    private FirebaseFirestore mFirestore;

    private int jumlahData;
    private ArrayList<Kriteria> kriterias;
    private List<Kriteria> kriteriaList;
    private TabAdapter mAdapter;

    private Callbacks mCallbacks;

    public PerbandinganSubKriteriaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPerbandinganSub = inflater.inflate(R.layout.fragment_perbandingan_sub_kriteria, container, false);
        ButterKnife.bind(this, mPerbandinganSub);
        barUtils = new ProgressBarUtils(mPerbandinganSub.getContext());

        if (mCallbacks != null) {
            mCallbacks.onAttachPerbandinganSubKriteriaFragment();

            barUtils.show();

            onSetupFirestore();
        }

        return mPerbandinganSub;
    }

    @Override
    public void onSetupFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(Kriteria.COLLECTION).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getKriteria(task);
                    } else {
                        Log.e(TAG, "onSetupFirebase: error getting documents");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "onSetupFirebase: ", e));
    }

    @Override
    public void getKriteria(Task<QuerySnapshot> task) {
        kriterias = new ArrayList<>(task.getResult().toObjects(Kriteria.class));
        kriteriaList = new ArrayList<>();
        for (int i = 0; i < task.getResult().size(); i++) {
            kriteriaList.add(new Kriteria(kriterias.get(i).getKodeKriteria(), kriterias.get(i).getNamaKriteria()));
        }

        if (kriteriaList.size() == 0 || kriteriaList.size() < 3) {
            tabKriteria.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            tabKriteria.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            jumlahData = task.getResult().size();
            onSetupTabLayout();
        }
    }

    @Override
    public void onSetupTabLayout() {
        for (int i = 0; i < jumlahData; i++) {
            tabKriteria.addTab(tabKriteria.newTab().setText(kriteriaList.get(i).getNamaKriteria()));
        }
        mAdapter = new TabAdapter(getChildFragmentManager(), kriteriaList, tabKriteria.getTabCount());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setEnabled(false);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabKriteria));
        tabKriteria.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        if (jumlahData > 3) {
            tabKriteria.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tabKriteria.setTabMode(TabLayout.MODE_FIXED);
        }

        barUtils.hide();

        tabItemSelected();
    }

    @Override
    public void tabItemSelected() {
        for (int i = 0; i < tabKriteria.getTabCount(); i++) {
            tabItem = ((ViewGroup) tabKriteria.getChildAt(0)).getChildAt(i);
            int s = tabKriteria.getSelectedTabPosition();
            if (tabItem.isSelected()) {
                tabItem.setClickable(true);
                tabItem.setAlpha(1F);
            }
            else {
                if (!tabItem.isSelected() && i < s) {
                    tabItem.setClickable(true);
                    tabItem.setAlpha(1F);
                }
                else {
                    tabItem.setClickable(false);
                    tabItem.setAlpha(0.3F);
                }
            }
        }
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

    @Override
    public void onAttachSubKriteriaFragment() {
        Log.d(TAG, "onAttachSubKriteriaFragment() called");
    }

    @Override
    public void onDetachSubKriteriaFragment() {
        App.mActivity.onBackPressed();
    }

    @Override
    public void onLanjutSub(int position) {
        barUtils.hide();
        new Handler().postDelayed(this::tabItemSelected, 100);
        mViewPager.setCurrentItem(position + 1);
    }

    public interface Callbacks {
        void onAttachPerbandinganSubKriteriaFragment();
        void onDetachPerbandinganSubKriteriaFragment();
    }
}
