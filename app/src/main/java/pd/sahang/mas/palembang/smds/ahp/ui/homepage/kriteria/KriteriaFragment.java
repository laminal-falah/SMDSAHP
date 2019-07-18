package pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.KriteriaAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;
import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganKriteria;
import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganSubKriteria;
import pd.sahang.mas.palembang.smds.ahp.models.SubKriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.dialog.DialogFilterKriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.dialog.DialogSearchKriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.filter.FilterKriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.filter.KriteriaViewModel;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.inisiasi.KriteriaInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SharedPrefManager;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class KriteriaFragment extends Fragment implements KriteriaInterface {

    private static final String TAG = KriteriaFragment.class.getSimpleName();

    private View mViewKriteria;

    @BindView(R.id.rvKriteria) RecyclerView rvKriteria;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.shimmer_view_kriteria) ShimmerFrameLayout mShimmerFrameLayout;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;
    private SharedPrefManager mPrefManager;

    private FirebaseFirestore mFirestore;
    private DocumentReference mSubKriteriaRef, mSubKriteriaDeleteRef, mPerbandinganRef;
    private ListenerRegistration mSubKriteriaRegistration;
    private Query mQuery;

    private KriteriaAdapter mAdapter;
    private KriteriaViewModel mViewModel;
    private DialogFilterKriteria mDialogFilterKriteria;
    private DialogSearchKriteria mDialogSearchKriteria;

    private String id;
    private int totalDataSub, count = 0;
    private final int LIMIT = 500;

    private Callbacks mCallbacks;

    public KriteriaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewKriteria = inflater.inflate(R.layout.fragment_kriteria, container, false);
        ButterKnife.bind(this, mViewKriteria);

        barUtils = new ProgressBarUtils(mViewKriteria.getContext());
        snackBarUtils = new SnackBarUtils(mViewKriteria.getContext());
        mPrefManager = new SharedPrefManager(mViewKriteria.getContext());

        mViewModel = ViewModelProviders.of(this).get(KriteriaViewModel.class);

        onInitFirestore();
        onInitRecyclerView();

        mDialogFilterKriteria = new DialogFilterKriteria();
        mDialogSearchKriteria = new DialogSearchKriteria();

        return mViewKriteria;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.kriteria, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_refresh) {
            onRefreshLayout();
        } else if (id == R.id.menu_filter) {
            mDialogFilterKriteria.show(getChildFragmentManager(), DialogFilterKriteria.TAG);
        } else if (id == R.id.menu_search) {
            mDialogSearchKriteria.show(getChildFragmentManager(), DialogSearchKriteria.TAG);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        onFilterKriteria(mViewModel.getFilterKriteria());

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
    public void onInitFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection(Kriteria.COLLECTION)
                .orderBy(Kriteria.TIMESTAMPS, Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    @Override
    public void onInitRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No Query, not installizing Recycler View");
        }

        rvKriteria.setLayoutManager(new LinearLayoutManager(mViewKriteria.getContext()));
        rvKriteria.addItemDecoration(new DividerItemDecoration(rvKriteria.getContext(), RecyclerView.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setChangeDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvKriteria.setItemAnimator(itemAnimator);

        if (mPrefManager.getSpLevel().equals("admin")) {
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

                private final Drawable deleteIcon = ContextCompat.getDrawable(mViewKriteria.getContext(), R.drawable.ic_delete_white_24dp);
                private final Drawable editIcon = ContextCompat.getDrawable(mViewKriteria.getContext(), R.drawable.ic_edit_white_24dp);
                private final ColorDrawable backgroundDelete = new ColorDrawable(Color.parseColor("#f44336"));
                private final ColorDrawable backgroundEdit = new ColorDrawable(Color.parseColor("#2bc657"));

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    if (i == ItemTouchHelper.LEFT) {
                        mAdapter.deleteKriteria(viewHolder.getAdapterPosition());
                    }
                    if (i == ItemTouchHelper.RIGHT) {
                        mAdapter.editKriteria(viewHolder.getAdapterPosition());
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

            }).attachToRecyclerView(rvKriteria);
        }

        mAdapter = new KriteriaAdapter(mQuery, this) {
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
                        barUtils.hide();
                    }, 2000);

                    mCallbacks.onAttachKriteria(getItemCount());
                }
            }
        };
    }

    @Override
    public void onRefreshLayout() {
        onFilterKriteria(mViewModel.getFilterKriteria());
    }

    @Override
    public void onKriteriaSelectedDetail(DocumentSnapshot snapshot) {
        if (mCallbacks != null) {
            mCallbacks.onDetailKriteriaFragment(snapshot.getId());
        }
    }

    @Override
    public void onKriteriaSelectedEdit(DocumentSnapshot snapshot) {
        if (mCallbacks != null) {
            mCallbacks.onEditKriteriaFragment(snapshot.getId());
        }
    }

    @Override
    public void onKriteriaSelectedDelete(DocumentSnapshot snapshot) {
        id = snapshot.getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(mViewKriteria.getContext());
        builder.setTitle(getString(R.string.title_hapus));
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.message_hapus, snapshot.getString(Kriteria.FIELD_NAMA_KRITERIA), getString(R.string.menu_1)));
        builder.setPositiveButton(getString(R.string.positive_hapus), (dialog, which) -> onRunningGetSubKriteria(id));
        builder.setNegativeButton(getString(R.string.negative_hapus), (dialog, which) -> {
            dialog.dismiss();
            mAdapter.notifyDataSetChanged();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRunningGetSubKriteria(String id) {
        totalDataSub = 0;
        count = 0;
        barUtils.show();
        mSubKriteriaRef = mFirestore.collection(Kriteria.COLLECTION).document(id);
        mSubKriteriaRegistration = mSubKriteriaRef.collection(SubKriteria.COLLECTION)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots.size() > 0) {
                        totalDataSub = queryDocumentSnapshots.size();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            onRunningDeleteSubKriteria(id, documentSnapshot.getId());
                            count++;
                        }
                        if (queryDocumentSnapshots.size() == count) {
                            onStopRunningDeleteSubKriteria();
                            onRunningDeleteKriteria(id);
                        }
                    } else {
                        onRunningDeleteKriteria(id);
                        onStopRunningDeleteSubKriteria();
                    }
                });
    }

    @Override
    public void onRunningDeleteSubKriteria(String idKriteria, String idSubKriteria) {
        mSubKriteriaDeleteRef = mFirestore.collection(Kriteria.COLLECTION).document(idKriteria);
        mSubKriteriaDeleteRef.collection(SubKriteria.COLLECTION).document(idSubKriteria).delete();
        mPerbandinganRef = mFirestore.collection(PerbandinganKriteria.COLLECTION).document(idKriteria);
        mPerbandinganRef.collection(PerbandinganSubKriteria.COLLECTION).document(idSubKriteria).delete();
    }

    @Override
    public void onRunningDeleteKriteria(String id) {
        int timer = totalDataSub * 1000;
        new Handler().postDelayed(() -> {
            mFirestore.collection(Kriteria.COLLECTION).document(id).delete();
            mFirestore.collection(PerbandinganKriteria.COLLECTION).document(id).delete();
            snackBarUtils.snackBarLong(
                    getString(R.string.snack_bar_success,
                            getString(R.string.snack_bar_delete).toLowerCase(),
                            getString(R.string.menu_1).toLowerCase()
                    )
            );
            barUtils.hide();
        }, timer);
    }

    @Override
    public void onStopRunningDeleteSubKriteria() {
        if (mSubKriteriaRegistration != null) {
            mSubKriteriaRegistration.remove();
            mSubKriteriaRegistration = null;
        }
    }

    @Override
    public void onFilterKriteria(FilterKriteria filterKriteria) {
        Query query = mFirestore.collection(Kriteria.COLLECTION);

        if (filterKriteria.hasSortBy()) {
            query = query.orderBy(filterKriteria.getSortBy(), filterKriteria.getSortDirection());
        }

        query = query.limit(LIMIT);

        mQuery = query;

        mAdapter.setQuery(mQuery);

        mViewModel.setFilterKriteria(filterKriteria);
    }

    @Override
    public void onSearchListener(String query) {
        barUtils.show();

        if (!TextUtils.isEmpty(query)) {
            mQuery = mFirestore.collection(Kriteria.COLLECTION)
                    .orderBy(Kriteria.FIELD_NAMA_KRITERIA)
                    .startAt(query)
                    .endAt(query+'\uf8ff');
            mAdapter.setQuery(mQuery);
        } else {
            snackBarUtils.snackbarShort("Field is required");
        }
    }

    @Override
    public void onResetSearchListener(FilterKriteria filterKriteria) {
        barUtils.show();

        Query query = mFirestore.collection(Kriteria.COLLECTION);

        if (filterKriteria.hasSortBy()) {
            query = query.orderBy(filterKriteria.getSortBy(), filterKriteria.getSortDirection());
        }

        query = query.limit(LIMIT);

        mQuery = query;

        mAdapter.setQuery(mQuery);

        mViewModel.setFilterKriteria(filterKriteria);
    }

    public interface Callbacks {
        void onAttachKriteria(int count);
        void onAddKriteriaFragment();
        void onEditKriteriaFragment(String id);
        void onDetailKriteriaFragment(String id);
    }
}
