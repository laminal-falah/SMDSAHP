package pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.PerbandinganSubAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganKriteria;
import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganSubKriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.inisiasi.DialogDetailHasilPerbandinganInteface;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class DialogDetailHasilPerbandingan extends BottomSheetDialogFragment implements DialogDetailHasilPerbandinganInteface {

    public static final String TAG = DialogDetailHasilPerbandingan.class.getSimpleName();

    public static final String KODE = "kode";
    public static final String NAMA = "nama";

    private View mDialogDetail;

    @BindView(R.id.titleToolbar) TextView tvTitle;
    @BindView(R.id.tvIsiKodePerbandingan) TextView tvKodeKriteria;
    @BindView(R.id.tvIsiNamaPerbandingan) TextView tvNamaKriteria;
    @BindView(R.id.tvIsiNilaiEigenVektor) TextView tvEigenVektorKriteria;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.shimmer_view_sub_perbandingan) ShimmerFrameLayout mShimmerFrameLayout;
    @BindView(R.id.rvPerbandinganSub) RecyclerView rvPerbandinganSub;

    private SnackBarUtils snackBarUtils;

    private FirebaseFirestore mFirestore;
    private DocumentReference mDetailRef, mSubKriteriaRef;
    private ListenerRegistration mDetailRegister;
    private Query mQuery;

    private String kode, nama;
    private double nilai;

    private PerbandinganSubAdapter mAdapter;

    private Callbacks mCallbacks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BaseBottomSheetDialog);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            kode = getArguments().getString(KODE);
            nama = getArguments().getString(NAMA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDialogDetail = inflater.inflate(R.layout.bottom_sheet_detail_perbandingan, container, false);
        ButterKnife.bind(this, mDialogDetail);
        snackBarUtils = new SnackBarUtils(mDialogDetail.getContext());
        tvTitle.setText(getString(R.string.toolbar_detail, getString(R.string.menu_5)));
        mFirestore = FirebaseFirestore.getInstance();

        onSubPerbandinganFirestore();

        onRecyclerView();

        return mDialogDetail;
    }

    @Override
    public void onStart() {
        super.onStart();

        mDetailRef = mFirestore.collection(PerbandinganKriteria.COLLECTION).document(kode);

        mDetailRegister = mDetailRef.addSnapshotListener(this);

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

        if (mDetailRegister != null) {
            mDetailRegister.remove();
            mDetailRegister = null;
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
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "onEvent: ", e);
            return;
        }
        onPerbandinganKriteriaLoad(snapshot.toObject(PerbandinganKriteria.class));
    }

    @Override
    public void onPerbandinganKriteriaLoad(PerbandinganKriteria kriteria) {
        nilai = kriteria.getNilaiEigenVektorKriteria();

        tvKodeKriteria.setText(kriteria.getKodeKriteria());
        tvNamaKriteria.setText(nama);
        tvEigenVektorKriteria.setText(String.valueOf(nilai));
    }

    @Override
    public void onSubPerbandinganFirestore() {
        mSubKriteriaRef = mFirestore.collection(PerbandinganKriteria.COLLECTION).document(kode);
        mQuery = mSubKriteriaRef.collection(PerbandinganSubKriteria.COLLECTION)
                .orderBy(PerbandinganSubKriteria.FIELD_SUB_KODE_KRITERIA, Query.Direction.ASCENDING)
                .limit(500);
    }

    @Override
    public void onRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No Query, not installizing Recycler View");
        }

        rvPerbandinganSub.setLayoutManager(new LinearLayoutManager(mDialogDetail.getContext()));
        rvPerbandinganSub.setHasFixedSize(true);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setChangeDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvPerbandinganSub.setItemAnimator(itemAnimator);

        mAdapter = new PerbandinganSubAdapter(mQuery) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                snackBarUtils.snackbarShort(e.getMessage());
            }

            @Override
            protected void onDataChanged() {
                if (mCallbacks != null) {
                    mShimmerFrameLayout.stopShimmer();
                    mShimmerFrameLayout.setVisibility(View.GONE);
                    if (getItemCount() == 0) {
                        rvPerbandinganSub.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        rvPerbandinganSub.setVisibility(View.VISIBLE);
                        mEmptyView.setVisibility(View.GONE);
                        rvPerbandinganSub.setAdapter(mAdapter);
                    }
                    mCallbacks.onAttachDialogDetailHasilPerbandingan();
                }
            }
        };
    }

    @OnClick(R.id.close_bottom) void close() {
        dismiss();
    }

    public interface Callbacks {
        void onAttachDialogDetailHasilPerbandingan();
    }
}
