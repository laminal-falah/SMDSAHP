package pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.style.ThreeBounce;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.DetailUserAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.DetailUser;
import pd.sahang.mas.palembang.smds.ahp.models.Users;
import pd.sahang.mas.palembang.smds.ahp.utils.UserHelper;

public class DialogDetailUser extends DialogFragment {

    public static final String TAG = DialogDetailUser.class.getSimpleName();

    public static final String ID = "id";

    @BindView(R.id.tvDetailUser) TextView tvTitle;
    @BindView(R.id.rvDetailUser) RecyclerView rvDetailUser;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    private View mViewDetailUser;

    private ArrayList<DetailUser> detailUsers;
    private String[] listDetail;
    private DetailUserAdapter mAdapter;

    private String id;

    private Callbacks mCallbacks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewDetailUser = inflater.inflate(R.layout.dialog_detail_user, container, false);
        ButterKnife.bind(this, mViewDetailUser);
        getDialog().setCanceledOnTouchOutside(false);
        tvTitle.setText(getString(R.string.toolbar_detail, getString(R.string.menu_8)));
        ThreeBounce threeBounce = new ThreeBounce();
        threeBounce.setColor(getResources().getColor(R.color.colorAccent));
        mProgressBar.setIndeterminate(true);
        mProgressBar.setClickable(false);
        mProgressBar.setIndeterminateDrawable(threeBounce);
        mProgressBar.setVisibility(View.VISIBLE);

        init();

        return mViewDetailUser;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    private void init() {
        rvDetailUser.setLayoutManager(new LinearLayoutManager(mViewDetailUser.getContext()));
        rvDetailUser.addItemDecoration(new DividerItemDecoration(rvDetailUser.getContext(), RecyclerView.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        rvDetailUser.setItemAnimator(itemAnimator);
        itemAnimator.setAddDuration(500);
        rvDetailUser.setHasFixedSize(true);

        listDetail = getResources().getStringArray(R.array.list_detail_user);
        detailUsers = new ArrayList<>();
        UserHelper.getUser(id)
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Users u = snapshot.toObject(Users.class);
                        detailUsers.add(new DetailUser(listDetail[0], u.getFullname()));
                        detailUsers.add(new DetailUser(listDetail[1], u.getEmail()));
                        detailUsers.add(new DetailUser(listDetail[2], u.getPhone()));
                        detailUsers.add(new DetailUser(listDetail[3], u.getLevel().toUpperCase()));
                        mAdapter = new DetailUserAdapter(detailUsers);
                        rvDetailUser.setAdapter(mAdapter);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    @OnClick(R.id.btn_close) void close() {
        dismiss();
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

    public interface Callbacks {
        void onCallbackDialogDetail();
    }
}
