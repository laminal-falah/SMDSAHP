package pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.PerbandinganAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;
import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganKriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.dialog.DialogDetailHasilPerbandingan;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.inisiasi.PerbandinganInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class PerbandinganFragment extends Fragment implements PerbandinganInterface {

    private static final String TAG = PerbandinganFragment.class.getSimpleName();

    private View mViewPerbandingan;

    @BindView(R.id.rvKriteria) RecyclerView rvKriteria;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.shimmer_view_kriteria) ShimmerFrameLayout mShimmerFrameLayout;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private PerbandinganAdapter mAdapter;
    private DialogDetailHasilPerbandingan mPerbandingan;

    private int kriteriaSize, perbandinganKriteriaSize, hitung;

    private Callbacks mCallbacks;

    private Bundle args;

    public PerbandinganFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewPerbandingan = inflater.inflate(R.layout.fragment_perbandingan, container, false);
        ButterKnife.bind(this, mViewPerbandingan);

        barUtils = new ProgressBarUtils(mViewPerbandingan.getContext());
        snackBarUtils = new SnackBarUtils(mViewPerbandingan.getContext());

        mFirestore = FirebaseFirestore.getInstance();

        args = new Bundle();

        mPerbandingan = new DialogDetailHasilPerbandingan();

        onCheckKriteria();

        onCheckHasilPerbandingan();

        onInitHasilPerbandingan();

        onInitFirestore();

        onInitRecyclerView();

        return mViewPerbandingan;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        mShimmerFrameLayout.stopShimmer();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callbacks) {
            mCallbacks = (Callbacks) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement Callbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCheckKriteria() {
        mFirestore.collection(Kriteria.COLLECTION).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    kriteriaSize = task.getResult().size();
                } else {
                    kriteriaSize = 0;
                }
            }
        });
    }

    @Override
    public void onCheckHasilPerbandingan() {
        mFirestore.collection(PerbandinganKriteria.COLLECTION).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    perbandinganKriteriaSize = task.getResult().size();
                } else {
                    perbandinganKriteriaSize = 0;
                }
            }
        });
    }

    @Override
    public void onInitHasilPerbandingan() {
        try {
            hitung = perbandinganKriteriaSize / kriteriaSize;
            if (hitung == 0) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException ex) {
            hitung = 0;
        }
        int timer = (hitung + 1) * 1000;

        new Handler().postDelayed(() -> {
            if (mCallbacks != null) {
                mCallbacks.onAttachPerbandinganFragment(kriteriaSize, perbandinganKriteriaSize);
            }
        }, timer);
    }

    @Override
    public void onInitFirestore() {
        mQuery = mFirestore.collection(PerbandinganKriteria.COLLECTION)
                .orderBy(PerbandinganKriteria.FIELD_KODE_KRITERIA, Query.Direction.DESCENDING)
                .limit(500);
    }

    @Override
    public void onInitRecyclerView() {
        if (mQuery == null) {
            Log.e(TAG, "No Query, not installizing Recycler View");
        }
        rvKriteria.setLayoutManager(new LinearLayoutManager(mViewPerbandingan.getContext()));
        rvKriteria.addItemDecoration(new DividerItemDecoration(rvKriteria.getContext(), RecyclerView.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setChangeDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvKriteria.setItemAnimator(itemAnimator);

        mAdapter = new PerbandinganAdapter(mQuery, this) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                snackBarUtils.snackbarShort(e.getMessage());
            }

            @Override
            protected void onDataChanged() {
                if (mCallbacks != null) {
                    new Handler().postDelayed(() -> {
                        mShimmerFrameLayout.stopShimmer();
                        mShimmerFrameLayout.setVisibility(View.GONE);
                        if (getItemCount() == 0) {
                            rvKriteria.setVisibility(View.GONE);
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            rvKriteria.setVisibility(View.VISIBLE);
                            mEmptyView.setVisibility(View.GONE);
                            rvKriteria.setAdapter(mAdapter);
                        }
                    }, 2000);
                    mCallbacks.onAttachPerbandinganFragment(kriteriaSize, perbandinganKriteriaSize);
                }
            }
        };
        if (mCallbacks != null) mCallbacks.onAttachPerbandinganFragment(kriteriaSize, perbandinganKriteriaSize);
    }

    @Override
    public void onDetailPerbandingan(DocumentSnapshot snapshot) {
        args.putString(DialogDetailHasilPerbandingan.KODE, snapshot.getString(Kriteria.FIELD_KODE_KRITERIA));
        args.putString(DialogDetailHasilPerbandingan.NAMA, snapshot.getString(Kriteria.FIELD_NAMA_KRITERIA));
        mPerbandingan.setArguments(args);
        mPerbandingan.setCancelable(false);
        mPerbandingan.show(getChildFragmentManager(), DialogDetailHasilPerbandingan.TAG);
    }

    @Override
    public void onAttachDialogDetailHasilPerbandingan() {
        Log.d(TAG, "onAttachDialogDetailHasilPerbandingan() called");
    }

    public interface Callbacks {
        void onAttachPerbandinganFragment(int kriteria, int perbandingan);
        void onAddPerbandinganFragment(int perbandingan);
    }
}
