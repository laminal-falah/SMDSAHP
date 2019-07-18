package pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan;

import android.content.Context;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.LaporanAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.Laporan;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan.inisiasi.LaporanInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SharedPrefManager;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class LaporanFragment extends Fragment implements LaporanInterface {

    private static final String TAG = LaporanFragment.class.getSimpleName();

    private View mLaporan;

    @BindView(R.id.rvLaporan) RecyclerView rvLaporan;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.shimmer_view_laporan) ShimmerFrameLayout mShimmerFrameLayout;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;
    private SharedPrefManager mPrefManager;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private LaporanAdapter mAdapter;

    private String documentId;
    private boolean exist;
    private final int LIMIT = 500;

    private Callbacks mCallbacks;

    public LaporanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLaporan = inflater.inflate(R.layout.fragment_laporan, container, false);
        ButterKnife.bind(this, mLaporan);

        barUtils = new ProgressBarUtils(mLaporan.getContext());
        snackBarUtils = new SnackBarUtils(mLaporan.getContext());
        mPrefManager = new SharedPrefManager(mLaporan.getContext());

        documentId = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        onInitFirestore();

        onRecyclerView();

        return mLaporan;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mShimmerFrameLayout.stopShimmer();
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerFrameLayout.startShimmer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
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
    public void onInitFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection(Laporan.COLLECTION)
                .orderBy(Laporan.TIMESTAMPS, Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    @Override
    public void onRecyclerView() {
        onExistDocument();

        if (mQuery == null) {
            Log.w(TAG, "No Query, not installizing Recycler View");
        }

        rvLaporan.setLayoutManager(new LinearLayoutManager(mLaporan.getContext()));
        rvLaporan.addItemDecoration(new DividerItemDecoration(rvLaporan.getContext(), RecyclerView.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setChangeDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvLaporan.setItemAnimator(itemAnimator);

        mAdapter = new LaporanAdapter(mQuery, this) {
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
                            rvLaporan.setVisibility(View.GONE);
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            rvLaporan.setVisibility(View.VISIBLE);
                            mEmptyView.setVisibility(View.GONE);
                            rvLaporan.setAdapter(mAdapter);
                        }
                        mCallbacks.onAttachLaporanFragment(isExist());
                    }, 2000);
                }
            }
        };
    }

    @Override
    public void onExistDocument() {
        mFirestore.collection(Laporan.COLLECTION).document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            setExist(true);
                            Log.d(TAG, "onExistDocument() returned: true");
                        } else {
                            setExist(false);
                            Log.d(TAG, "onExistDocument() returned: false");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    snackBarUtils.snackBarLong(e.getMessage());
                    Log.e(TAG, "onExistDocument: ", e);
                });
    }

    @Override
    public void onLaporanSelectedDetail(DocumentSnapshot snapshot) {
        startActivity(new Intent(mLaporan.getContext(), DetailLaporanActivity.class)
                .putExtra(DetailLaporanActivity.ID, snapshot.getId())
                .putExtra(DetailLaporanActivity.TITLE, snapshot.getString(Laporan.FIELD_NAMA_LAPORAN)));
    }

    private boolean isExist() {
        return exist;
    }

    private void setExist(boolean exist) {
        this.exist = exist;
    }

    public interface Callbacks {
        void onAttachLaporanFragment(boolean exist);
        void onAddLaporanFragment();
    }
}
