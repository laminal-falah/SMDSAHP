package pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.sub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.Users;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.inisiasi.DataUserInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.UserHelper;

public class DataUserFragment extends Fragment implements DataUserInterface {

    private static final String TAG = DataUserFragment.class.getSimpleName();

    private View mDataUser;
    @BindView(R.id.tl_fullname_profile) TextInputLayout tlFullname;
    @BindView(R.id.tl_phone_profile) TextInputLayout tlPhone;
    @BindView(R.id.edt_fullname_profile) TextInputEditText edtFullname;
    @BindView(R.id.edt_phone_profile) TextInputEditText edtPhone;
    @BindView(R.id.btnUpdateUser) Button btn;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore mFirestore;
    private DocumentReference mRefUser;

    private String fullname, phone;

    private Map<String, Object> user;

    private Intent intent;

    private Callbacks mCallbacks;

    public DataUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDataUser = inflater.inflate(R.layout.fragment_data_user, container, false);
        ButterKnife.bind(this, mDataUser);

        barUtils = new ProgressBarUtils(mDataUser.getContext());
        snackBarUtils = new SnackBarUtils(mDataUser.getContext());

        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        btn.setEnabled(false);

        edtPhone.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEND) {
                update();
                return true;
            }
            return false;
        });

        barUtils.show();

        initFirestore();

        user = new HashMap<>();

        intent = new Intent();

        return mDataUser;
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
    public void initFirestore () {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UserHelper.getUser(mFirebaseUser.getUid())
                .addOnSuccessListener(this::getData)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: " + mDataUser.getContext().toString(), e);
                    snackBarUtils.snackBarLong(e.getMessage());
                });
        mRefUser = mFirestore.collection(Users.COLLECTION).document(mFirebaseUser.getUid());
    }

    @Override
    public void getData(DocumentSnapshot snapshot) {
        if (snapshot.exists()) {
            Users users = snapshot.toObject(Users.class);
            fullname = users.getFullname();
            phone = users.getPhone();

            edtFullname.setText(fullname);
            edtPhone.setText(phone);
            btn.setEnabled(true);
        } else {
            if (mCallbacks != null) {
                mCallbacks.onFailedLoadData();
            }
        }
        barUtils.hide();
    }

    @Override
    public boolean validate() {
        boolean valid = true;

        fullname = tlFullname.getEditText().getText().toString();
        phone = tlPhone.getEditText().getText().toString();

        if (TextUtils.isEmpty(fullname)) {
            valid = false;
            tlFullname.setErrorEnabled(true);
            tlFullname.setError(getString(R.string.error_user_0, getString(R.string.hint_fullname)));
            tlFullname.requestFocus();
        } else if (fullname.length() < 2) {
            valid = false;
            tlFullname.setErrorEnabled(true);
            tlFullname.setError(getString(R.string.error_user_1));
            tlFullname.requestFocus();
        } else {
            tlFullname.setError(null);
            tlFullname.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(phone)) {
            valid = false;
            tlPhone.setErrorEnabled(true);
            tlPhone.setError(getString(R.string.error_user_0, getString(R.string.hint_phone)));
            tlPhone.requestFocus();
        } else if (!String.valueOf(phone.charAt(0)).equalsIgnoreCase("0") || phone.length() < 10) {
            valid = false;
            tlPhone.setErrorEnabled(true);
            tlPhone.setError(getString(R.string.error_user_3));
            tlPhone.requestFocus();
        } else {
            tlPhone.setError(null);
            tlPhone.setErrorEnabled(false);
        }

        return valid;
    }

    @OnClick(R.id.btnUpdateUser) void update() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (!validate()) return;

        barUtils.show();

        user.put(Users.FIELD_FULLNAME, fullname);
        user.put(Users.FIELD_PHONE, phone);
        mRefUser.update(user)
                .addOnSuccessListener(aVoid -> {
                    if (mCallbacks != null) {
                        mCallbacks.onUpdateDataUserSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    if (mCallbacks != null) {
                        mCallbacks.onUpdateDataUserFailed(intent.putExtra("error", e.getMessage()));
                    }
                });
    }

    public interface Callbacks {
        void onAttachDataUserFragment();
        void onFailedLoadData();
        void onUpdateDataUserSuccess();
        void onUpdateDataUserFailed(Intent intent);
    }
}
