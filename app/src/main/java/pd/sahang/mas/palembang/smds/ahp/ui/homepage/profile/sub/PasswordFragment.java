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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.Users;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.inisiasi.PasswordFragmentInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class PasswordFragment extends Fragment implements PasswordFragmentInterface {

    private static final String TAG = PasswordFragment.class.getSimpleName();

    private View mPassword;

    @BindView(R.id.tl_pass_old) TextInputLayout tlPasswordLama;
    @BindView(R.id.tl_pass_new) TextInputLayout tlPasswordBaru;
    @BindView(R.id.edt_pass_old) TextInputEditText edtPasswordLama;
    @BindView(R.id.edt_pass_new) TextInputEditText edtPasswordBaru;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore mFirestore;
    private DocumentReference mRefPass;
    private AuthCredential credential;

    private String passOld, passNew;

    private Intent intent;

    private Map<String, Object> pass;

    private Callbacks mCallbacks;

    public PasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPassword = inflater.inflate(R.layout.fragment_password, container, false);
        ButterKnife.bind(this, mPassword);

        barUtils = new ProgressBarUtils(mPassword.getContext());
        snackBarUtils = new SnackBarUtils(mPassword.getContext());

        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        edtPasswordBaru.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEND) {
                update();
                return true;
            }
            return false;
        });

        mRefPass = mFirestore.collection(Users.COLLECTION).document(mFirebaseUser.getUid());

        pass = new HashMap<>();

        intent = new Intent();

        return mPassword;
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

        passOld = tlPasswordLama.getEditText().getText().toString();
        passNew = tlPasswordBaru.getEditText().getText().toString();

        if (TextUtils.isEmpty(passOld)) {
            tlPasswordLama.setErrorEnabled(true);
            tlPasswordLama.setError(getString(R.string.error_field_required));
            valid = false;
        } else if (!isPasswordValid(passOld)) {
            tlPasswordLama.setErrorEnabled(true);
            tlPasswordLama.setError(getString(R.string.error_invalid_password));
            valid = false;
        } else {
            tlPasswordLama.setError(null);
            tlPasswordLama.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(passNew)) {
            tlPasswordBaru.setErrorEnabled(true);
            tlPasswordBaru.setError(getString(R.string.error_field_required));
            valid = false;
        } else if (!isPasswordValid(passNew)) {
            tlPasswordBaru.setErrorEnabled(true);
            tlPasswordBaru.setError(getString(R.string.error_invalid_password));
            valid = false;
        } else {
            tlPasswordBaru.setError(null);
            tlPasswordBaru.setErrorEnabled(false);
        }

        return valid;
    }

    @Override
    public boolean isPasswordValid(String password) {
        return password.length() > 7;
    }

    @Override
    public void reAuthentication() {
        if (mFirebaseUser.getEmail() != null) {
            credential = EmailAuthProvider.getCredential(mFirebaseUser.getEmail(), passOld);
            mFirebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updatePassword();
                        } else {
                            snackBarUtils.snackBarLong("Gagal autentikasi");
                        }
                    })
                    .addOnFailureListener(e -> {
                        try {
                            throw e;
                        } catch (FirebaseAuthInvalidCredentialsException invalidPassword) {
                            errorPasswordOld();
                        } catch (Exception ex) {
                            Log.d(TAG, "onFailure: " + ex);
                        }
                        Log.e(TAG, "onFailure: " + mPassword.getContext().toString(), e);
                        barUtils.hide();
                    });
        }
    }

    @Override
    public void updatePassword() {
        mFirebaseUser.updatePassword(passNew)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateCollectionPassword();
                    } else {
                        snackBarUtils.snackBarLong(task.getException().getMessage());
                    }
                });
    }

    @Override
    public void updateCollectionPassword() {
        pass.put(Users.FIELD_PASSWORD, passNew);
        mRefPass.update(pass)
                .addOnSuccessListener(aVoid -> {
                    if (mCallbacks != null) {
                        mCallbacks.onUpdatePasswordSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    if (mCallbacks != null) {
                        mCallbacks.onUpdatePasswordFailed(intent.putExtra("error", e.getMessage()));
                    }
                });
    }

    @Override
    public void errorPasswordOld() {
        tlPasswordLama.setErrorEnabled(true);
        tlPasswordLama.setError(getString(R.string.error_incorrect_password));
        tlPasswordLama.requestFocus();
    }

    @OnClick(R.id.btnUpdatePassword) void update() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (!validate()) return;

        barUtils.show();

        reAuthentication();
    }

    public interface Callbacks {
        void onAttachPasswordFragment();
        void onUpdatePasswordSuccess();
        void onUpdatePasswordFailed(Intent intent);
    }
}
