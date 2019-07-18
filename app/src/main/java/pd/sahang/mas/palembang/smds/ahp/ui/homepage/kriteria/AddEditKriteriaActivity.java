package pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.auth.AuthActivity;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.inisiasi.AddEditKriteriaInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.Validation;

public class AddEditKriteriaActivity extends AppCompatActivity implements AddEditKriteriaInterface {

    private static final String TAG = AddEditKriteriaActivity.class.getSimpleName();

    public static final String ADD_DATA = "add_data";

    public static final String KEY_ID = "key_id";

    public static final int REQUEST_ADD = 600;
    public static final int RESULT_ADD_SUCCESS = 601;
    public static final int RESULT_ADD_FAILED = 602;

    public static final int REQUEST_UPDATE = 1000;
    public static final int RESULT_UPDATE_SUCCESS = 1001;
    public static final int RESULT_UPDATE_FAILED = 1002;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tl_kode_ka) TextInputLayout tlKodeKa;
    @BindView(R.id.tl_nama_ka) TextInputLayout tlNamaKa;
    @BindView(R.id.edt_kode_ka) TextInputEditText edtKodeKa;
    @BindView(R.id.edt_nama_ka) TextInputEditText edtNamaKa;
    @BindView(R.id.btn_action_ka) Button btnKa;

    private ProgressBarUtils barUtils;

    private String id;
    private boolean isEdit;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference mDocKriteria;

    private Intent intent;

    private String kode, nama;

    private Map<String, Object> kriterias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_kriteria);
        ButterKnife.bind(this);

        barUtils = new ProgressBarUtils(this);

        intent = new Intent();

        if (getIntent().getExtras().getString(KEY_ID).equals(ADD_DATA)) {
            isEdit = false;
            toolbar.setTitle(getString(R.string.toolbar_add, getString(R.string.menu_1)));
            btnKa.setText(getString(R.string.btn_save_ka));
        }
        else {
            barUtils.show();
            isEdit = true;
            toolbar.setTitle(getString(R.string.toolbar_edit, getString(R.string.menu_1)));
            id = getIntent().getExtras().getString(KEY_ID);
            tlKodeKa.setEnabled(false);
            btnKa.setText(getString(R.string.btn_update_ka));
        }

        edtNamaKa.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnKriteria();
                return true;
            }
            return false;
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        onAuthenticationFirebase();
        onInitFirestore();

        kriterias = new HashMap<>();
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
            mDocKriteria = mFirestore.collection(Kriteria.COLLECTION).document(id);
            mDocKriteria.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    Kriteria kriteria = snapshot.toObject(Kriteria.class);
                    setKriteria(kriteria);
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
    public void setKriteria(Kriteria kriteria) {
        edtKodeKa.setText(kriteria.getKodeKriteria());
        edtNamaKa.setText(kriteria.getNamaKriteria());
    }

    @Override
    public boolean validate() {
        boolean valid = true;

        kode = tlKodeKa.getEditText().getText().toString();
        nama = tlNamaKa.getEditText().getText().toString();

        if (Validation.isEmptyString(kode)) {
            valid = false;
            tlKodeKa.setErrorEnabled(true);
            tlKodeKa.setError(getString(R.string.error_ka_0, getString(R.string.hint_kode_kriteria)));
            tlKodeKa.requestFocus();
        } else if (Validation.isLengthKode(kode,3,3)) {
            valid = false;
            tlKodeKa.setErrorEnabled(true);
            tlKodeKa.setError(getString(R.string.error_ka_1, getString(R.string.hint_kode_kriteria), 3, 3));
            tlKodeKa.requestFocus();
        } else if (!Validation.isRegexKodeKriteria(kode)) {
            valid = false;
            tlKodeKa.setErrorEnabled(true);
            tlKodeKa.setError(getString(R.string.error_ka_2, getString(R.string.hint_kode_kriteria), "K"));
            tlKodeKa.requestFocus();
        } else {
            tlKodeKa.setError(null);
            tlKodeKa.setErrorEnabled(false);
        }

        if (Validation.isEmptyString(nama)) {
            valid = false;
            tlNamaKa.setErrorEnabled(true);
            tlNamaKa.setError(getString(R.string.error_ka_0, getString(R.string.hint_nama_kriteria)));
            tlNamaKa.requestFocus();
        } else if (Validation.isLengthNama(nama, 3, 20)) {
            valid = false;
            tlNamaKa.setErrorEnabled(true);
            tlNamaKa.setError(getString(R.string.error_ka_1, getString(R.string.hint_kode_kriteria), 3, 20));
            tlNamaKa.requestFocus();
        } else {
            tlNamaKa.setError(null);
            tlNamaKa.setErrorEnabled(false);
        }

        return valid;
    }

    @Override
    public void onResetForm() {
        edtNamaKa.setText(null);
        edtKodeKa.setText(null);
    }

    @OnClick(R.id.btn_action_ka) void btnKriteria() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (!validate()) return;

        barUtils.show();

        kriterias.put(Kriteria.FIELD_NAMA_KRITERIA, nama);

        if (!isEdit) {
            kriterias.put(Kriteria.FIELD_KODE_KRITERIA, kode);
            kriterias.put(Kriteria.TIMESTAMPS, Timestamp.now());
            mFirestore = FirebaseFirestore.getInstance();
            mFirestore.collection(Kriteria.COLLECTION).document(kode).set(kriterias)
                    .addOnSuccessListener(aVoid -> {
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
        } else {
            mDocKriteria.update(kriterias)
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
}
