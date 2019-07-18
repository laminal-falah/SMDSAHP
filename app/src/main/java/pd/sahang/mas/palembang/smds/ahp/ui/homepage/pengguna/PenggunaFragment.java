package pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.UserAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.Users;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.dialog.DialogDetailUser;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.dialog.DialogFilterUser;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.dialog.DialogSearchUser;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.filter.FilterUser;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.filter.UserViewModel;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.inisiasi.PenggunaInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class PenggunaFragment extends Fragment implements PenggunaInterface {

    private static final String TAG = PenggunaFragment.class.getSimpleName();

    private View mPengguna;

    @BindView(R.id.rvUser) RecyclerView rvUser;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.shimmer_view_user) ShimmerFrameLayout mShimmerFrameLayout;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private UserAdapter mAdapter;
    private UserViewModel mViewModel;
    private DialogDetailUser mDialogDetailUser;
    private DialogFilterUser mDialogFilterUser;
    private DialogSearchUser mDialogSearchUser;

    private Bundle args;

    private String id;
    private final int LIMIT = 500;

    private Callbacks mCallbacks;

    public PenggunaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPengguna = inflater.inflate(R.layout.fragment_pengguna, container, false);
        ButterKnife.bind(this, mPengguna);

        barUtils = new ProgressBarUtils(mPengguna.getContext());
        snackBarUtils = new SnackBarUtils(mPengguna.getContext());

        mViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        mDialogDetailUser = new DialogDetailUser();
        mDialogFilterUser = new DialogFilterUser();
        mDialogSearchUser = new DialogSearchUser();

        args = new Bundle();

        onInitFirestore();
        onInitRecyclerView();

        return mPengguna;
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
            mDialogFilterUser.show(getChildFragmentManager(), DialogFilterUser.TAG);
        } else if (id == R.id.menu_search) {
            mDialogSearchUser.show(getChildFragmentManager(), DialogSearchUser.TAG);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        onFilterUser(mViewModel.getmFilterUser());

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
        mQuery = mFirestore.collection(Users.COLLECTION)
                .orderBy(Users.TIMESTAMPS, Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    @Override
    public void onInitRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No Query, not installizing Recycler View");
        }

        rvUser.setLayoutManager(new LinearLayoutManager(mPengguna.getContext()));
        rvUser.addItemDecoration(new DividerItemDecoration(rvUser.getContext(), RecyclerView.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setChangeDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvUser.setItemAnimator(itemAnimator);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

            private final Drawable deleteIcon = ContextCompat.getDrawable(mPengguna.getContext(), R.drawable.ic_delete_white_24dp);
            private final Drawable editIcon = ContextCompat.getDrawable(mPengguna.getContext(), R.drawable.ic_edit_white_24dp);
            private final ColorDrawable backgroundDelete = new ColorDrawable(Color.parseColor("#f44336"));
            private final ColorDrawable backgroundEdit = new ColorDrawable(Color.parseColor("#2bc657"));

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                if (i == ItemTouchHelper.LEFT) {
                    mAdapter.deleteUser(viewHolder.getAdapterPosition());
                }
                if (i == ItemTouchHelper.RIGHT) {
                    mAdapter.editUser(viewHolder.getAdapterPosition());
                }
            }

            @Override
            public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
                return animationType == ItemTouchHelper.ANIMATION_TYPE_DRAG ? DEFAULT_DRAG_ANIMATION_DURATION
                        : DEFAULT_SWIPE_ANIMATION_DURATION;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
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
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        }).attachToRecyclerView(rvUser);

        mAdapter = new UserAdapter(mQuery, this) {
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
                            rvUser.setVisibility(View.GONE);
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            rvUser.setVisibility(View.VISIBLE);
                            mEmptyView.setVisibility(View.GONE);
                            rvUser.setAdapter(mAdapter);
                        }
                    }, 2000);

                    mCallbacks.onAttachPenggunaFragment();
                }
            }
        };
    }

    @Override
    public void onRefreshLayout() {
        onFilterUser(mViewModel.getmFilterUser());
    }

    @Override
    public void onUserSelectedDetail(DocumentSnapshot snapshot) {
        args.putString(DialogDetailUser.ID, snapshot.getId());
        mDialogDetailUser.setArguments(args);
        mDialogDetailUser.show(getChildFragmentManager(), DialogDetailUser.TAG);
    }

    @Override
    public void onUserSelectedEdit(DocumentSnapshot snapshot) {
        if (mCallbacks != null) {
            mCallbacks.onEditPenggunaFragment(snapshot.getId());
        }
    }

    @Override
    public void onUserSelectedDelete(DocumentSnapshot snapshot) {
        id = snapshot.getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(mPengguna.getContext());
        builder.setTitle(getString(R.string.title_hapus));
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.message_hapus, snapshot.getString(Users.FIELD_FULLNAME), getString(R.string.menu_8)));
        builder.setPositiveButton(getString(R.string.positive_hapus), (dialog, which) -> {
            mFirestore.collection(Users.COLLECTION).document(id).delete();
            snackBarUtils.snackBarLong(
                    getString(R.string.snack_bar_success,
                            getString(R.string.snack_bar_delete).toLowerCase(),
                            getString(R.string.menu_8).toLowerCase()
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
    public void onCallbackDialogDetail() {
        Log.d(TAG, "onCallbackDialogDetail()");
    }

    @Override
    public void onFilterUser(FilterUser filterUser) {
        Query query = mFirestore.collection(Users.COLLECTION);

        if (filterUser.hasSortBy()) {
            query = query.orderBy(filterUser.getSortBy(), filterUser.getSortDirection());
        }

        query = query.limit(LIMIT);

        mQuery = query;

        mAdapter.setQuery(mQuery);

        mViewModel.setmFilterUser(filterUser);
    }

    @Override
    public void onSearchListener(String query) {
        if (!TextUtils.isEmpty(query)) {
            mQuery = mFirestore.collection(Users.COLLECTION)
                    .orderBy(Users.FIELD_FULLNAME)
                    .startAt(query)
                    .endAt(query+'\uf8ff');
            mAdapter.setQuery(mQuery);
        } else {
            snackBarUtils.snackbarShort("Field is required");
        }
    }

    @Override
    public void onResetSearchListener(FilterUser filterUser) {
        Query query = mFirestore.collection(Users.COLLECTION);

        if (filterUser.hasSortBy()) {
            query = query.orderBy(filterUser.getSortBy(), filterUser.getSortDirection());
        }

        query = query.limit(LIMIT);

        mQuery = query;

        mAdapter.setQuery(mQuery);

        mViewModel.setmFilterUser(filterUser);
    }

    public interface Callbacks {
        void onAttachPenggunaFragment();
        void onAddPenggunaFragment();
        void onEditPenggunaFragment(String id);
    }
}
