package pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.SubKriteriaAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;
import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganKriteria;
import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganSubKriteria;
import pd.sahang.mas.palembang.smds.ahp.models.SubKriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.auth.AuthActivity;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.dialog.DialogDetailSubKriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.inisiasi.DetailKriteriaInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SharedPrefManager;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class DetailKriteriaActivity extends AppCompatActivity implements DetailKriteriaInterface {

    private static final String TAG = DetailKriteriaActivity.class.getSimpleName();

    public static final String ID = "id";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tvIsiKode) TextView tvKodeKriteria;
    @BindView(R.id.tvIsiNama) TextView tvNamaKriteria;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.shimmer_view_kriteria) ShimmerFrameLayout mShimmerFrameLayout;
    @BindView(R.id.rvSubKriteria) RecyclerView rvSubKriteria;
    @BindView(R.id.addSubKriteria) FloatingActionButton addSubKriteria;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;
    private SharedPrefManager mPrefManager;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference mKriteriaRef, mPerbandinganRef;
    private ListenerRegistration mKriteriaRegistration;
    private Query mQuery;

    private SubKriteriaAdapter mAdapter;

    private String kriteriaId, id;
    private Intent intent;
    private Bundle args;
    private DialogDetailSubKriteria mDialogDetailSubKriteria;

    private final int LIMIT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kriteria);
        ButterKnife.bind(this);

        if (getIntent().getStringExtra(ID) == null) {
            throw new IllegalArgumentException("Must pass id " + ID);
        }

        kriteriaId = getIntent().getStringExtra(ID);

        barUtils = new ProgressBarUtils(this);
        snackBarUtils = new SnackBarUtils(this);
        mPrefManager = new SharedPrefManager(this);

        barUtils.show();

        toolbar.setTitle(getString(R.string.toolbar_detail, getString(R.string.menu_1)));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        onAuthenticationFirebase();

        onInitFirestore();

        onRecyclerView();

        args = new Bundle();

        mDialogDetailSubKriteria = new DialogDetailSubKriteria();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAdapter != null) {
            mAdapter.startListening();
        }

        mKriteriaRegistration = mKriteriaRef.addSnapshotListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAdapter != null) {
            mAdapter.stopListening();
        }

        if (mKriteriaRegistration != null) {
            mKriteriaRegistration.remove();
            mKriteriaRegistration = null;
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
        if (requestCode == AddEditSubKriteriaActivity.REQUEST_ADD) {
            if (resultCode == AddEditSubKriteriaActivity.RESULT_ADD_SUCCESS) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_success,
                        getString(R.string.snack_bar_add).toLowerCase(),
                        getString(R.string.menu_2).toLowerCase()
                ));
            } else if (resultCode == AddEditSubKriteriaActivity.RESULT_ADD_FAILED) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_error,
                        data.getStringExtra("error")
                ));
            }
        } else if (requestCode == AddEditSubKriteriaActivity.REQUEST_UPDATE) {
            if (resultCode == AddEditSubKriteriaActivity.RESULT_UPDATE_SUCCESS) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_success,
                        getString(R.string.snack_bar_update).toLowerCase(),
                        getString(R.string.menu_2).toLowerCase()
                ));
            } else if (resultCode == AddEditSubKriteriaActivity.RESULT_UPDATE_FAILED) {
                snackBarUtils.snackBarLong(getString(R.string.snack_bar_error,
                        data.getStringExtra("error")
                ));
            }
        }
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "onEvent: ", e);
            return;
        }
        onKriteriaLoad(documentSnapshot.toObject(Kriteria.class));
    }

    @Override
    public void onKriteriaLoad(Kriteria kriteria) {
        tvKodeKriteria.setText(kriteria.getKodeKriteria());
        tvNamaKriteria.setText(kriteria.getNamaKriteria());

        barUtils.hide();
    }

    @Override
    public void onAuthenticationFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFirebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), AuthActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    @Override
    public void onInitFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        mKriteriaRef = mFirestore.collection(Kriteria.COLLECTION).document(kriteriaId);

        mQuery = mKriteriaRef.collection(SubKriteria.COLLECTION)
                .orderBy(SubKriteria.TIMESTAMPS, Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    @Override
    public void onRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No Query, not installizing Recycler View");
        }

        rvSubKriteria.setLayoutManager(new LinearLayoutManager(this));
        rvSubKriteria.addItemDecoration(new DividerItemDecoration(rvSubKriteria.getContext(), RecyclerView.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setChangeDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvSubKriteria.setItemAnimator(itemAnimator);

        if (mPrefManager.getSpLevel().equals("admin")) {
            addSubKriteria.show();

            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

                private final Drawable deleteIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete_white_24dp);
                private final Drawable editIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_edit_white_24dp);
                private final ColorDrawable backgroundDelete = new ColorDrawable(Color.parseColor("#f44336"));
                private final ColorDrawable backgroundEdit = new ColorDrawable(Color.parseColor("#2bc657"));

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    if (i == ItemTouchHelper.LEFT) {
                        mAdapter.deleteSubKriteria(viewHolder.getAdapterPosition());
                    }
                    if (i == ItemTouchHelper.RIGHT) {
                        mAdapter.editSubKriteria(viewHolder.getAdapterPosition());
                    }
                }

                @Override
                public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
                    return animationType == ItemTouchHelper.ANIMATION_TYPE_DRAG ? DEFAULT_DRAG_ANIMATION_DURATION
                            : DEFAULT_SWIPE_ANIMATION_DURATION;
                }

                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        View itemView = viewHolder.itemView;
                        int dXSwipe = (int) (dX * 1.05);
                        if (dX > 0) {
                            // draw background
                            backgroundEdit.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + dXSwipe, itemView.getBottom());
                            backgroundEdit.draw(c);
                            // draw icon
                            int top = (itemView.getTop() + itemView.getBottom() - editIcon.getIntrinsicHeight()) / 2;
                            int left = itemView.getLeft() + 48;
                            editIcon.setBounds(left, top, left + editIcon.getIntrinsicWidth(), top + editIcon.getIntrinsicHeight());
                            editIcon.draw(c);
                        } else {
                            // draw background
                            backgroundDelete.setBounds(itemView.getRight() + dXSwipe, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                            backgroundDelete.draw(c);
                            // draw icon
                            int top = (itemView.getTop() + itemView.getBottom() - deleteIcon.getIntrinsicHeight()) / 2;
                            int right = itemView.getRight() - 48;
                            deleteIcon.setBounds(right - deleteIcon.getIntrinsicWidth(), top, right, top + deleteIcon.getIntrinsicHeight());
                            deleteIcon.draw(c);
                        }
                    }
                }

            }).attachToRecyclerView(rvSubKriteria);

        } else {
            addSubKriteria.hide();
        }

        mAdapter = new SubKriteriaAdapter(mQuery, this) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                snackBarUtils.snackbarShort(e.getMessage());
            }

            @Override
            protected void onDataChanged() {
                if (getItemCount() < 15) {
                    addSubKriteria.show();
                } else {
                    addSubKriteria.hide();
                }
                new Handler().postDelayed(() -> {
                    mShimmerFrameLayout.stopShimmer();
                    mShimmerFrameLayout.setVisibility(View.GONE);
                    if (getItemCount() == 0) {
                        rvSubKriteria.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        rvSubKriteria.setVisibility(View.VISIBLE);
                        mEmptyView.setVisibility(View.GONE);
                        rvSubKriteria.setAdapter(mAdapter);
                    }
                }, 2000);
            }
        };
    }

    @Override
    public void onEditSubKriteria(DocumentSnapshot snapshot) {
        intent = new Intent(this, AddEditSubKriteriaActivity.class);
        intent.putExtra(AddEditSubKriteriaActivity.KEY_REF, kriteriaId);
        intent.putExtra(AddEditSubKriteriaActivity.KEY_ID, snapshot.getId());
        startActivityForResult(intent, AddEditSubKriteriaActivity.REQUEST_UPDATE);
    }

    @Override
    public void onDetailSubKriteria(DocumentSnapshot snapshot) {
        args.putString(DialogDetailSubKriteria.KODE, snapshot.getString(SubKriteria.FIELD_KODE_SUB_KRITERIA));
        args.putString(DialogDetailSubKriteria.NAMA, snapshot.getString(SubKriteria.FIELD_NAMA_SUB_KRITERIA));
        args.putDouble(DialogDetailSubKriteria.TIPE, snapshot.getDouble(SubKriteria.FIELD_TIPE_NILAI));
        args.putDouble(DialogDetailSubKriteria.MIN, snapshot.getDouble(SubKriteria.FIELD_NILAI_MIN_SUB_KRITERIA));
        args.putDouble(DialogDetailSubKriteria.MAX, snapshot.getDouble(SubKriteria.FIELD_NILAI_MAX_SUB_KRITERIA));
        mDialogDetailSubKriteria.setArguments(args);
        mDialogDetailSubKriteria.setCancelable(false);
        mDialogDetailSubKriteria.show(getSupportFragmentManager(), DialogDetailSubKriteria.TAG);
    }

    @Override
    public void onDeleteSubKriteria(DocumentSnapshot snapshot) {
        id = snapshot.getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_hapus));
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.message_hapus, snapshot.getString(SubKriteria.FIELD_NAMA_SUB_KRITERIA), getString(R.string.menu_2)));
        builder.setPositiveButton(getString(R.string.positive_hapus), (dialog, which) -> {
            mKriteriaRef = mFirestore.collection(Kriteria.COLLECTION).document(kriteriaId);
            mKriteriaRef.collection(SubKriteria.COLLECTION).document(id).delete();
            mPerbandinganRef = mFirestore.collection(PerbandinganKriteria.COLLECTION).document(kriteriaId);
            mPerbandinganRef.collection(PerbandinganSubKriteria.COLLECTION).document(id).delete();
            snackBarUtils.snackBarLong(
                    getString(R.string.snack_bar_success,
                            getString(R.string.snack_bar_delete).toLowerCase(),
                            getString(R.string.menu_2).toLowerCase()
                    )
            );
        });
        builder.setNegativeButton(getString(R.string.negative_hapus), (dialog, which) -> {
            dialog.dismiss();
            mAdapter.notifyDataSetChanged();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onAttachBottomSheetSubKriteria() {
        Log.d(TAG, "onAttachBottomSheetSubKriteria() called");
    }

    @OnClick(R.id.addSubKriteria) void addSubKriteria() {
        intent = new Intent(this, AddEditSubKriteriaActivity.class);
        intent.putExtra(AddEditSubKriteriaActivity.KEY_REF, kriteriaId);
        intent.putExtra(AddEditSubKriteriaActivity.KEY_ID, AddEditSubKriteriaActivity.ADD_DATA);
        startActivityForResult(intent, AddEditSubKriteriaActivity.REQUEST_ADD);
    }
}
