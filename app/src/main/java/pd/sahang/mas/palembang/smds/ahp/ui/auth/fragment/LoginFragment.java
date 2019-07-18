package pd.sahang.mas.palembang.smds.ahp.ui.auth.fragment;

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
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.App;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.Auth;
import pd.sahang.mas.palembang.smds.ahp.models.Users;
import pd.sahang.mas.palembang.smds.ahp.ui.auth.inisiasi.LoginInterface;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.DashboardActivity;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SharedPrefFirebase;
import pd.sahang.mas.palembang.smds.ahp.utils.SharedPrefManager;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.UserHelper;

public class LoginFragment extends Fragment implements LoginInterface {

    private static final String TAG = LoginFragment.class.getSimpleName();

    private View mLogin;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;

    @BindView(R.id.tlEmail) TextInputLayout txtEmail;
    @BindView(R.id.tlPassword) TextInputLayout txtPass;

    @BindView(R.id.email) TextInputEditText edtEmail;
    @BindView(R.id.password) TextInputEditText edtPass;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private SharedPrefManager mPrefManager;

    private String email, password;

    private Callbacks mCallbacks;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLogin = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, mLogin);

        barUtils = new ProgressBarUtils(mLogin.getContext());
        snackBarUtils = new SnackBarUtils(mLogin.getContext());
        mPrefManager = new SharedPrefManager(mLogin.getContext());

        edtPass.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEND) {
                login();
                return true;
            }
            return false;
        });

        barUtils.hide();

        mFirebaseAuth = FirebaseAuth.getInstance();

        return mLogin;
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
    public boolean validate() {
        boolean valid = true;

        email = txtEmail.getEditText().getText().toString();
        password = txtPass.getEditText().getText().toString();

        if (TextUtils.isEmpty(password)) {
            txtPass.setErrorEnabled(true);
            txtPass.setError(getString(R.string.error_field_required));
            valid = false;
        } else if (!isPasswordValid(password)) {
            txtPass.setErrorEnabled(true);
            txtPass.setError(getString(R.string.error_invalid_password));
            valid = false;
        } else {
            txtPass.setError(null);
            txtPass.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(email)) {
            txtEmail.setErrorEnabled(true);
            txtEmail.setError(getString(R.string.error_field_required));
            valid = false;
        } else if (!isEmailValid(email)) {
            txtEmail.setErrorEnabled(true);
            txtEmail.setError(getString(R.string.error_invalid_email));
            valid = false;
        } else {
            txtEmail.setError(null);
            txtEmail.setErrorEnabled(false);
        }

        return valid;
    }

    @Override
    public boolean isEmailValid(String email) {
        return email.contains("@");
    }

    @Override
    public boolean isPasswordValid(String password) {
        return password.length() > 7;
    }

    @Override
    public Auth setLogin() {
        Auth login = new Auth();
        if (mLogin != null) {
            login.setEmail(email);
            login.setPassword(password);
        }
        return login;
    }

    @Override
    public void running(Auth auth) {
        barUtils.show();
        String a = auth.getEmail();
        String b = auth.getPassword();

        mFirebaseAuth.signInWithEmailAndPassword(a,b)
                .addOnCompleteListener(this::taskLogin)
                .addOnFailureListener(e -> {
                    try {
                        throw e;
                    } catch (FirebaseAuthInvalidUserException invalidEmail) {
                        errorEmail();
                    } catch (FirebaseAuthInvalidCredentialsException invalidPassword) {
                        errorPassword();
                    } catch (Exception ex) {
                        Log.e(TAG, "taskLogin: ", ex);
                    }
                    Log.w(TAG, "onFailure: " + mLogin.getContext().toString(), e);
                    barUtils.hide();
                });
    }

    @Override
    public void taskLogin(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            checkLevel();
        } else {
            snackBarUtils.snackBarLong(getString(R.string.msg_login_failed));
            barUtils.hide();
        }
    }

    @Override
    public void checkLevel() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UserHelper.getUser(mFirebaseUser.getUid()).addOnSuccessListener(this::onSuccessLogin);
    }

    @Override
    public void onSuccessLogin(DocumentSnapshot snapshot) {
        if (snapshot.exists()) {
            if (snapshot.getString(Users.FIELD_LEVEL).equals("admin")) {
                String token = SharedPrefFirebase.getInstance(getContext()).getDeviceToken();
                UserHelper.updateToken(mFirebaseUser.getUid(), token);
            }
            Toast.makeText(mLogin.getContext(), getString(R.string.msg_login_success), Toast.LENGTH_SHORT).show();
            mPrefManager.saveSPString(SharedPrefManager.SP_LEVEL, snapshot.getString(Users.FIELD_LEVEL));
            startActivity(new Intent(mLogin.getContext(), DashboardActivity.class));
            App.mActivity.finish();
        } else {
            snackBarUtils.snackBarLong(getString(R.string.msg_login_failed));
            mFirebaseAuth.signOut();
        }
        barUtils.hide();
    }

    @Override
    public void errorEmail() {
        txtEmail.setErrorEnabled(true);
        txtEmail.setError(getString(R.string.error_credentials_email));
        txtEmail.requestFocus();
    }

    @Override
    public void errorPassword() {
        txtPass.setErrorEnabled(true);
        txtPass.setError(getString(R.string.error_incorrect_password));
        txtPass.requestFocus();
    }

    @OnClick(R.id.btnSignIn) void login() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (!validate()) return;

        email = txtEmail.getEditText().getText().toString();
        password = txtPass.getEditText().getText().toString();

        if (mCallbacks != null) {
            running(setLogin());
        }
    }

    public interface Callbacks {
        void onAttachLoginFragment();
    }
}
