package pd.sahang.mas.palembang.smds.ahp.ui.auth.fragment;

import android.app.Activity;
import android.content.Context;
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

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.Auth;
import pd.sahang.mas.palembang.smds.ahp.ui.auth.inisiasi.ForgetInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class ForgetPasswordFragment extends Fragment implements ForgetInterface {

    private static final String TAG = ForgetPasswordFragment.class.getSimpleName();

    private View mForget;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;

    @BindView(R.id.tlEmailForget) TextInputLayout txtEmail;
    @BindView(R.id.emailForget) TextInputEditText edtEmail;

    private FirebaseAuth mFirebaseAuth;

    private String email;

    private Callbacks mCallbacks;

    public ForgetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mForget = inflater.inflate(R.layout.fragment_forget_password, container, false);
        ButterKnife.bind(this, mForget);

        barUtils = new ProgressBarUtils(mForget.getContext());
        snackBarUtils = new SnackBarUtils(mForget.getContext());

        edtEmail.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEND) {
                reset();
                return true;
            }
            return false;
        });

        barUtils.hide();

        mFirebaseAuth = FirebaseAuth.getInstance();

        return mForget;
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
    public Auth setForget() {
        Auth forget = new Auth();
        if (mForget != null) {
            forget.setEmail(email);
        }
        return forget;
    }

    @Override
    public void running(Auth auth) {
        barUtils.show();
        String a = auth.getEmail();

        mFirebaseAuth.sendPasswordResetEmail(a)
                .addOnCompleteListener(this::taskForget)
                .addOnFailureListener(e -> {
                    try {
                        throw e;
                    } catch (FirebaseAuthInvalidUserException invalidEmail) {
                        errorEmail();
                    } catch (Exception ex) {
                        Log.e(TAG, "taskForget: ", ex);
                    }
                    Log.w(TAG, "onFailure: " + mForget.getContext().toString(), e);
                });
    }

    @Override
    public void taskForget(Task<Void> task) {
        if (task.isSuccessful()) {
            snackBarUtils.snackBarLong(getString(R.string.msg_reset_password));
            edtEmail.setText(null);
            mCallbacks.onAttachViewLogin();
        } else {
            snackBarUtils.snackBarLong(getString(R.string.error_invalid_email));
        }
        barUtils.hide();
    }

    @Override
    public void errorEmail() {
        txtEmail.setErrorEnabled(true);
        txtEmail.setError(getString(R.string.error_credentials_email));
        txtEmail.requestFocus();
    }

    @OnClick(R.id.btnReset) void reset() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (!validate()) return;

        email = txtEmail.getEditText().getText().toString();

        if (mCallbacks != null) {
            running(setForget());
        }
    }

    public interface Callbacks {
        void onAttachForgetPasswordFragment();
        void onAttachViewLogin();
    }
}
