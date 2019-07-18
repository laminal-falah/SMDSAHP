package pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.Users;
import pd.sahang.mas.palembang.smds.ahp.ui.auth.AuthActivity;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.inisiasi.AddEditUserInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.UserHelper;

public class AddEditUserActivity extends AppCompatActivity implements AddEditUserInterface {

    private static final String TAG = AddEditUserActivity.class.getSimpleName();

    public static final String ADD_DATA = "add_data";

    public static final String KEY_ID = "key_id";

    public static final int REQUEST_ADD = 400;
    public static final int RESULT_ADD_SUCCESS = 401;
    public static final int RESULT_ADD_FAILED = 402;

    public static final int REQUEST_UPDATE = 800;
    public static final int RESULT_UPDATE_SUCCESS = 801;
    public static final int RESULT_UPDATE_FAILED = 802;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tl_fullname) TextInputLayout tlFullname;
    @BindView(R.id.tl_email) TextInputLayout tlEmail;
    @BindView(R.id.tl_phone) TextInputLayout tlPhone;
    @BindView(R.id.tl_password) TextInputLayout tlPassword;
    @BindView(R.id.edt_fullname) TextInputEditText edtFullname;
    @BindView(R.id.edt_email) TextInputEditText edtEmail;
    @BindView(R.id.edt_phone) TextInputEditText edtPhone;
    @BindView(R.id.edt_password) TextInputEditText edtPassword;
    @BindView(R.id.sp_level) Spinner mSpinnerLevel;
    @BindView(R.id.btnSaveUser) Button btn;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;

    private TextView tvLevel;
    private String id;
    private boolean isEdit;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference mRefUser;

    private Intent intent;

    private String fullname, email, phone, password, level;
    private String emailAuth, passwordAuth;

    private Map<String, Object> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_user);

        ButterKnife.bind(this);

        barUtils = new ProgressBarUtils(this);
        snackBarUtils = new SnackBarUtils(this);

        intent = new Intent();

        if (getIntent().getExtras().getString(KEY_ID).equals(ADD_DATA)) {
            isEdit = false;
            toolbar.setTitle(getString(R.string.toolbar_add, getString(R.string.menu_8)));
            edtPassword.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnUser();
                    return true;
                }
                return false;
            });
        }
        else {
            barUtils.show();
            isEdit = true;
            toolbar.setTitle(getString(R.string.toolbar_edit, getString(R.string.menu_8)));
            id = getIntent().getExtras().getString(KEY_ID);
            edtPhone.setImeOptions(EditorInfo.IME_ACTION_DONE);
            edtPhone.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnUser();
                    return true;
                }
                return false;
            });
            tlEmail.setEnabled(false);
            tlPassword.setVisibility(View.GONE);
            btn.setText(getString(R.string.btn_update_user));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        onAuthenticationFirebase();
        onInitFirestore();

        user = new HashMap<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onSpinnerLevel();
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

        if (isEdit) {
            mRefUser = mFirestore.collection(Users.COLLECTION).document(id);
            mRefUser.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    Users users = snapshot.toObject(Users.class);
                    setUser(users);
                } else {
                    intent.putExtra("error", "Data not found");
                    setResult(RESULT_UPDATE_FAILED, intent);
                    finish();
                }
            }).addOnFailureListener(e -> {
                intent.putExtra("error", e.getMessage());
                setResult(RESULT_UPDATE_FAILED, intent);
                finish();
            });
            barUtils.hide();
        }
    }

    @Override
    public void setUser(Users users) {
        edtFullname.setText(users.getFullname());
        edtEmail.setText(users.getEmail());
        edtPhone.setText(users.getPhone());
        int selected = 0;
        String[] level = getResources().getStringArray(R.array.levels);
        for (int i = 0; i < level.length; i++) {
            if (users.getLevel().toLowerCase().equals(level[i])) selected = i;
        }
        mSpinnerLevel.setSelection(selected);
    }

    @Override
    public void onSpinnerLevel() {
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.levels)) {

            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        mSpinnerLevel.setAdapter(levelAdapter);
    }

    @Override
    public boolean validate() {
        boolean valid = true;

        tvLevel = (TextView) mSpinnerLevel.getSelectedView();

        level = mSpinnerLevel.getSelectedItem().toString();
        fullname = tlFullname.getEditText().getText().toString();
        email = tlEmail.getEditText().getText().toString();
        phone = tlPhone.getEditText().getText().toString();
        password = tlPassword.getEditText().getText().toString();

        if (level.equals(getString(R.string.level_choose))) {
            valid = false;
            tvLevel.setError(getString(R.string.error_user_0, getString(R.string.level_choose)));
            tvLevel.requestFocus();
        } else {
            tvLevel.setError(null);
        }

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

        if (TextUtils.isEmpty(email)) {
            valid = false;
            tlEmail.setErrorEnabled(true);
            tlEmail.setError(getString(R.string.error_user_0, getString(R.string.hint_email)));
            tlEmail.requestFocus();
        } else if (!email.contains("@")) {
            valid = false;
            tlEmail.setErrorEnabled(true);
            tlEmail.setError(getString(R.string.error_user_2));
            tlEmail.requestFocus();
        } else {
            tlEmail.setError(null);
            tlEmail.setErrorEnabled(false);
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

        if (!isEdit) {
            if (TextUtils.isEmpty(password)) {
                valid = false;
                tlPassword.setErrorEnabled(true);
                tlPassword.setError(getString(R.string.error_user_0, getString(R.string.hint_password)));
                tlPassword.requestFocus();
            } else if (password.length() < 7) {
                valid = false;
                tlPassword.setErrorEnabled(true);
                tlPassword.setError(getString(R.string.error_user_4));
                tlPassword.requestFocus();
            } else {
                tlPassword.setError(null);
                tlPassword.setErrorEnabled(false);
            }
        }

        return valid;
    }

    @Override
    public void onResetForm() {
        edtFullname.setText(null);
        edtEmail.setText(null);
        edtPhone.setText(null);
        edtPassword.setText(null);
        mSpinnerLevel.setSelection(0);
    }

    @OnClick(R.id.btnSaveUser) void btnUser() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (!validate()) return;

        barUtils.show();

        if (!isEdit) {
            UserHelper.getUser(mFirebaseAuth.getUid())
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            Users u = snapshot.toObject(Users.class);
                            emailAuth = u.getEmail();
                            passwordAuth = u.getPassword();
                            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            createUser(mFirebaseAuth.getUid());
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        barUtils.hide();
                                        snackBarUtils.snackBarLong(e.getMessage());
                                        onResetForm();
                                    });
                        }
                    });
        } else {
            user.put(Users.FIELD_FULLNAME, fullname);
            user.put(Users.FIELD_LEVEL, level);
            user.put(Users.FIELD_PHONE, phone);
            mRefUser.update(user)
                    .addOnSuccessListener(aVoid -> {
                        onResetForm();
                        setResult(RESULT_UPDATE_SUCCESS);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        onResetForm();
                        intent.putExtra("error", e.getMessage());
                        setResult(RESULT_UPDATE_FAILED, intent);
                        finish();
                    });
        }
    }

    private void createUser(String id) {
        user.put(Users.FIELD_EMAIL, email);
        user.put(Users.FIELD_FULLNAME, fullname);
        user.put(Users.FIELD_LEVEL, level);
        user.put(Users.FIELD_PASSWORD, password);
        user.put(Users.FIELD_PHONE, phone);
        user.put(Users.TIMESTAMPS, Timestamp.now());

        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(Users.COLLECTION).document(id).set(user)
                .addOnSuccessListener(aVoid -> {
                    mFirebaseAuth.signOut();
                    mFirebaseAuth.signInWithEmailAndPassword(emailAuth, passwordAuth).isSuccessful();
                    onResetForm();
                    setResult(RESULT_ADD_SUCCESS);
                    finish();
                })
                .addOnFailureListener(e -> {
                    onResetForm();
                    intent.putExtra("error", e.getMessage());
                    setResult(RESULT_ADD_FAILED, intent);
                    finish();
                });
    }
}
