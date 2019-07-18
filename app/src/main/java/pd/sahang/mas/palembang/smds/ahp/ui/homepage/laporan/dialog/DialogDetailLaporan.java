package pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.DetailLaporanBottomAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.DetailLaporan;
import pd.sahang.mas.palembang.smds.ahp.models.DetailLaporanItem;
import pd.sahang.mas.palembang.smds.ahp.models.Laporan;
import pd.sahang.mas.palembang.smds.ahp.models.Users;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan.inisiasi.DialogDetailLaporanInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class DialogDetailLaporan extends BottomSheetDialogFragment implements DialogDetailLaporanInterface {

    public static final String TAG = DialogDetailLaporan.class.getSimpleName();

    public static final String ID = "id";
    public static final String NAMA = "nama";
    public static final String GRADE = "grade";
    public static final String NILAI = "nilai";
    public static final String GAMBAR = "gambar";
    public static final String ID_USER = "idUser";
    public static final String DOCUMENT_ID = "document_id";

    private View mDialogDetail;

    @BindView(R.id.titleToolbar) TextView tvtitle;
    @BindView(R.id.imgKarungDetail) AppCompatImageView mImageKarung;
    @BindView(R.id.tvIsiNamaKarungDetail) TextView tvIsiNamaKarungDetail;
    @BindView(R.id.tvIsiGradeKarungDetail) TextView tvIsiGrageKarungDetail;
    @BindView(R.id.tvIsiNilaiGradeDetail) TextView tvIsiNilaiGradeDetail;
    @BindView(R.id.tvIsiNamaUserDetail) TextView tvNamaUserKarung;
    @BindView(R.id.tvGradeNilaiDetail) TextView tvGradeNilaiKarung;
    @BindView(R.id.shimmer_view_detail_laporan) ShimmerFrameLayout mShimmerFrameLayout;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.rvKriteriaDetailLaporan) RecyclerView rvKriteriaDetailLaporan;

    private SnackBarUtils snackBarUtils;

    private FirebaseFirestore mFirestore;
    private DocumentReference mUserRef, mDetailItemLaporanRef;
    private ListenerRegistration mUserRegister;
    private Query mQuery;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    private DetailLaporanBottomAdapter mAdapter;

    private String documentId, id, nama, grade, gambar, user;
    private double nilai;

    private Callbacks mCallbacks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BaseBottomSheetDialog);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ID);
            nama = getArguments().getString(NAMA);
            grade = getArguments().getString(GRADE);
            nilai = getArguments().getDouble(NILAI);
            gambar = getArguments().getString(GAMBAR);
            user = getArguments().getString(ID_USER);
            documentId = getArguments().getString(DOCUMENT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDialogDetail = inflater.inflate(R.layout.bottom_sheet_detail_laporan, container, false);
        ButterKnife.bind(this, mDialogDetail);

        tvtitle.setText(getString(R.string.toolbar_detail, getString(R.string.menu_7)));
        tvGradeNilaiKarung.setText(getString(R.string.hint_nilai_karung, "Karung"));

        snackBarUtils = new SnackBarUtils(mDialogDetail.getContext());

        onInitFirestore();

        if (mCallbacks != null) {
            mCallbacks.onAttachBottomSheetDetailLaporan();
            tvIsiNamaKarungDetail.setText(nama);
            tvIsiGrageKarungDetail.setText(grade);
            tvIsiNilaiGradeDetail.setText(String.valueOf(nilai));
        }

        return mDialogDetail;
    }

    @Override
    public void onStart() {
        super.onStart();

        mUserRef = mFirestore.collection(Users.COLLECTION).document(user);

        mUserRegister = mUserRef.addSnapshotListener(this);

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

        if (mUserRegister != null) {
            mUserRegister.remove();
            mUserRegister = null;
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
    public void onInitFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        mDetailItemLaporanRef = mFirestore.collection(Laporan.COLLECTION).document(documentId)
                .collection(DetailLaporan.COLLECTION).document(id);

        mQuery = mDetailItemLaporanRef.collection(DetailLaporanItem.COLLECTION)
                .orderBy(DetailLaporanItem.FIELD_NAMA_KRITERIA, Query.Direction.ASCENDING)
                .limit(500);

        onRecyclerView();

        onInitStorage();
    }

    @Override
    public void onRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No Query, not installizing Recycler View");
        }

        rvKriteriaDetailLaporan.setLayoutManager(new LinearLayoutManager(mDialogDetail.getContext()));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setChangeDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvKriteriaDetailLaporan.setItemAnimator(itemAnimator);
        rvKriteriaDetailLaporan.setHasFixedSize(true);

        mAdapter = new DetailLaporanBottomAdapter(mQuery) {
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
                        rvKriteriaDetailLaporan.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        rvKriteriaDetailLaporan.setVisibility(View.VISIBLE);
                        mEmptyView.setVisibility(View.GONE);
                        rvKriteriaDetailLaporan.setAdapter(mAdapter);
                    }
                }, 2000);
            }
        };
    }

    @Override
    public void onInitStorage() {
        mStorage = FirebaseStorage.getInstance();

        mStorageReference = mStorage.getReference();

        mStorageReference
                .child("laporan/" + gambar)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> Glide.with(this)
                        .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.placeholder_karung))
                        .load(uri.toString())
                        .centerCrop()
                        .into(mImageKarung))
                .addOnFailureListener(e -> Glide.with(mDialogDetail.getContext())
                        .load(R.drawable.placeholder_karung)
                        .centerCrop()
                        .into(mImageKarung));
    }

    @Override
    public void onSetUsers(Users users) {
        if (users.getFullname() != null) {
            tvNamaUserKarung.setText(users.getFullname());
        } else {
            tvNamaUserKarung.setText("N/A");
        }
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.e(TAG, "onEvent: ", e);
            return;
        }

        onSetUsers(snapshot.toObject(Users.class));
    }

    @OnClick(R.id.close_bottom) void close() {
        dismiss();
    }

    public interface Callbacks {
        void onAttachBottomSheetDetailLaporan();
    }
}
