package pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.ProfileAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.Profile;
import pd.sahang.mas.palembang.smds.ahp.models.Users;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.inisiasi.ProfileFragmentInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SharedPrefManager;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class ProfileFragment extends Fragment implements ProfileFragmentInterface {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private View mProfile;

    @BindView(R.id.viewEmpty) ViewGroup mEmptyGroup;
    @BindView(R.id.rvProfile) RecyclerView rvProfile;
    @BindView(R.id.shimmer_view_profile) ShimmerFrameLayout mShimmerFrameLayout;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;
    private SharedPrefManager mPrefManager;

    private ArrayList<Profile> profiles;
    private String[] listProfile;

    private FirebaseFirestore mFirestore;
    private DocumentReference mReference;
    private FirebaseUser mFirebaseUser;
    private ListenerRegistration mProfileRegister;

    private ProfileAdapter mAdapter;

    private Callbacks mCallbacks;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mProfile = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, mProfile);

        barUtils = new ProgressBarUtils(mProfile.getContext());
        snackBarUtils = new SnackBarUtils(mProfile.getContext());
        mPrefManager = new SharedPrefManager(mProfile.getContext());

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mFirestore = FirebaseFirestore.getInstance();

        mReference = mFirestore.collection(Users.COLLECTION).document(mFirebaseUser.getUid());

        initProfile();

        return mProfile;
    }

    @Override
    public void onStart() {
        super.onStart();
        mProfileRegister = mReference.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mProfileRegister != null) {
            mProfileRegister.remove();
            mProfileRegister = null;
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
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if (snapshot != null) {
            profiles.clear();
            getData(snapshot);
        }
    }

    @Override
    public void initProfile() {
        rvProfile.setLayoutManager(new LinearLayoutManager(mProfile.getContext()));
        rvProfile.addItemDecoration(new DividerItemDecoration(rvProfile.getContext(), RecyclerView.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setChangeDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvProfile.setItemAnimator(itemAnimator);
        rvProfile.setHasFixedSize(true);

        listProfile = getResources().getStringArray(R.array.menu_profile);
        profiles = new ArrayList<>();
    }

    @Override
    public void getData(DocumentSnapshot snapshot) {
        if (snapshot.exists()) {
            Users u = snapshot.toObject(Users.class);
            profiles.add(new Profile(listProfile[0], u.getFullname()));
            profiles.add(new Profile(listProfile[1], u.getEmail()));
            profiles.add(new Profile(listProfile[2], u.getPhone()));
            profiles.add(new Profile(listProfile[3], u.getLevel().toUpperCase()));
        } else {
            if (mCallbacks != null) {
                mCallbacks.onLogoutProfile();
            }
        }

        if (mCallbacks != null) {
            new Handler().postDelayed(() -> {
                if (profiles.size() > 0) {
                    mEmptyGroup.setVisibility(View.GONE);
                    mShimmerFrameLayout.stopShimmer();
                    mShimmerFrameLayout.setVisibility(View.GONE);
                    rvProfile.setVisibility(View.VISIBLE);
                    mAdapter = new ProfileAdapter(profiles);
                    rvProfile.setAdapter(mAdapter);
                } else {
                    mEmptyGroup.setVisibility(View.VISIBLE);
                    mShimmerFrameLayout.stopShimmer();
                    mShimmerFrameLayout.setVisibility(View.GONE);
                    rvProfile.setVisibility(View.GONE);
                }
            }, 2000);

            mCallbacks.onAttachProfile();
        }
    }

    public interface Callbacks {
        void onAttachProfile();
        void onLogoutProfile();
        void onUpdateProfile();
    }
}
