package pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.DetailLaporanAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.DetailLaporan;
import pd.sahang.mas.palembang.smds.ahp.models.Laporan;
import pd.sahang.mas.palembang.smds.ahp.ui.auth.AuthActivity;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan.dialog.DialogDetailLaporan;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan.inisiasi.DetailLaporanInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SharedPrefManager;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class DetailLaporanActivity extends AppCompatActivity implements DetailLaporanInterface {

    private static final String TAG = DetailLaporanActivity.class.getSimpleName();

    public static final String ID = "id";
    public static final String TITLE = "title";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.shimmer_view_laporan) ShimmerFrameLayout mShimmerFrameLayout;
    @BindView(R.id.rvDetailLaporan) RecyclerView rvDetailLaporan;
    @BindView(R.id.addLaporanDetail) FloatingActionButton addLaporanDetail;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;
    private SharedPrefManager mPrefManager;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference mLaporanRef;
    private ListenerRegistration mLaporanReg;
    private Query mQuery;

    private DetailLaporanAdapter mAdapter;

    private String id, title;
    private Intent intent;
    private Bundle args;
    private DialogDetailLaporan mDialogDetailLaporan;

    private final int LIMIT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_laporan);
        ButterKnife.bind(this);

        if (getIntent().getStringExtra(ID) != null) {
            id = getIntent().getStringExtra(ID);
            title = getIntent().getStringExtra(TITLE);
        } else {
            throw new IllegalArgumentException("must be pass " + ID);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        if (id.equals(sdf.format(new Date()))) {
            addLaporanDetail.show();
        } else {
            addLaporanDetail.hide();
        }

        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        barUtils = new ProgressBarUtils(this);
        snackBarUtils = new SnackBarUtils(this);
        mPrefManager = new SharedPrefManager(this);

        args = new Bundle();
        intent = new Intent(this, AddEditLaporanActivity.class);

        mDialogDetailLaporan = new DialogDetailLaporan();

        onAuthenticationFirebase();

        onInitFirestore();

        onRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShimmerFrameLayout.startShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShimmerFrameLayout.stopShimmer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddEditLaporanActivity.REQUEST_ADD_TIPE_1) {
            if (resultCode == AddEditLaporanActivity.RESULT_ADD_TIPE_1_SUCCESS) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_success,
                        getString(R.string.snack_bar_add).toLowerCase(),
                        getString(R.string.menu_7).toLowerCase()
                ));
            } else if (resultCode == AddEditLaporanActivity.RESULT_ADD_TIPE_1_FAILED) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_error,
                        data.getStringExtra("error")
                ));
            }
        } else if (requestCode == AddEditLaporanActivity.REQUEST_UPDATE_TIPE_1) {
            if (resultCode == AddEditLaporanActivity.RESULT_UPDATE_TIPE_1_SUCCESS) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_success,
                        getString(R.string.snack_bar_update).toLowerCase(),
                        getString(R.string.menu_7).toLowerCase()
                ));
            } else if (resultCode == AddEditLaporanActivity.RESULT_UPDATE_TIPE_1_FAILED) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_error,
                        data.getStringExtra("error")
                ));
            }
        }
    }

    @Override
    public void onAuthenticationFirebase() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), AuthActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    @Override
    public void onInitFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        mLaporanRef = mFirestore.collection(Laporan.COLLECTION).document(id);

        if (mPrefManager.getSpLevel().equals("admin")) {
            mQuery = mLaporanRef.collection(DetailLaporan.COLLECTION)
                    .orderBy(DetailLaporan.TIMESTAMPS, Query.Direction.DESCENDING)
                    .limit(LIMIT);
        } else {
            mQuery = mLaporanRef.collection(DetailLaporan.COLLECTION)
                    .whereEqualTo(DetailLaporan.FIELD_ID_USER, mAuth.getCurrentUser().getUid())
                    .orderBy(DetailLaporan.TIMESTAMPS, Query.Direction.DESCENDING)
                    .limit(LIMIT);
        }
    }

    @Override
    public void onRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No Query, not installizing Recycler View");
        }

        rvDetailLaporan.setLayoutManager(new LinearLayoutManager(this));
        rvDetailLaporan.addItemDecoration(new DividerItemDecoration(rvDetailLaporan.getContext(), RecyclerView.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setChangeDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvDetailLaporan.setItemAnimator(itemAnimator);

        mAdapter = new DetailLaporanAdapter(mQuery, this) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                snackBarUtils.snackbarShort(e.getMessage());
            }

            @Override
            protected void onDataChanged() {
                new Handler().postDelayed(() -> {
                    mShimmerFrameLayout.stopShimmer();
                    mShimmerFrameLayout.setVisibility(View.GONE);
                    if (getItemCount() == 0) {
                        rvDetailLaporan.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        rvDetailLaporan.setVisibility(View.VISIBLE);
                        mEmptyView.setVisibility(View.GONE);
                        rvDetailLaporan.setAdapter(mAdapter);
                    }
                }, 2000);
            }
        };
    }

    @Override
    public void onSelectedDetailLaporan(DocumentSnapshot snapshot) {
        args.putString(DialogDetailLaporan.DOCUMENT_ID, id);
        args.putString(DialogDetailLaporan.ID, snapshot.getId());
        args.putString(DialogDetailLaporan.NAMA, snapshot.getString(DetailLaporan.FIELD_NAMA_KARUNG));
        args.putString(DialogDetailLaporan.GRADE, snapshot.getString(DetailLaporan.FIELD_GRADE_KARUNG));
        args.putDouble(DialogDetailLaporan.NILAI, snapshot.getDouble(DetailLaporan.FIELD_NILAI_GRADE_KARUNG));
        args.putString(DialogDetailLaporan.GAMBAR, snapshot.getString(DetailLaporan.FIELD_GAMBAR_KARUNG));
        args.putString(DialogDetailLaporan.ID_USER, snapshot.getString(DetailLaporan.FIELD_ID_USER));
        mDialogDetailLaporan.setArguments(args);
        mDialogDetailLaporan.setCancelable(false);
        mDialogDetailLaporan.show(getSupportFragmentManager(), DialogDetailLaporan.TAG);
    }

    @Override
    public void onAttachBottomSheetDetailLaporan() {
        Log.d(TAG, "onAttachBottomSheetDetailLaporan() called");
    }

    @OnClick(R.id.addLaporanDetail) void addLaporanDetail() {
        intent.putExtra(AddEditLaporanActivity.DOCUMENT_ID, id);
        intent.putExtra(AddEditLaporanActivity.KEY_ID, AddEditLaporanActivity.ADD_DATA_TIPE_1);
        startActivityForResult(intent, AddEditLaporanActivity.REQUEST_ADD_TIPE_1);
    }
}
