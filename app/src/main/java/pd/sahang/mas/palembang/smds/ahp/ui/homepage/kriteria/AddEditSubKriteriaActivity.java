package pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria;

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
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;
import pd.sahang.mas.palembang.smds.ahp.models.SubKriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.auth.AuthActivity;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.inisiasi.AddEditSubKriteriaInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.Validation;

public class AddEditSubKriteriaActivity extends AppCompatActivity implements AddEditSubKriteriaInterface {

    private static final String TAG = AddEditSubKriteriaActivity.class.getSimpleName();

    public static final String ADD_DATA = "add_data";

    public static final String KEY_REF = "key_ref";
    public static final String KEY_ID = "key_id";

    public static final int REQUEST_ADD = 2034;
    public static final int RESULT_ADD_SUCCESS = 2044;
    public static final int RESULT_ADD_FAILED = 2054;

    public static final int REQUEST_UPDATE = 9876;
    public static final int RESULT_UPDATE_SUCCESS = 9786;
    public static final int RESULT_UPDATE_FAILED = 9666;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tl_sub_kode_kriteria) TextInputLayout tlSubKodeKriteria;
    @BindView(R.id.tl_sub_nama_kriteria) TextInputLayout tlSubNamaKriteria;
    @BindView(R.id.satuan_nilainya) AppCompatSpinner spSatuanNilai;
    @BindView(R.id.tl_sub_nilai_min_kriteria) TextInputLayout tlSubNilaiMinKriteria;
    @BindView(R.id.tl_sub_nilai_max_kriteria) TextInputLayout tlSubNilaiMaxKriteria;
    @BindView(R.id.edt_sub_kode_kriteria) TextInputEditText edtSubKodeKriteria;
    @BindView(R.id.edt_sub_nama_kriteria) TextInputEditText edtSubNamaKriteria;
    @BindView(R.id.edt_sub_nilai_min) TextInputEditText edtSubNilaiMinKriteria;
    @BindView(R.id.edt_sub_nilai_max) TextInputEditText edtSubNilaiMaxKriteria;
    @BindView(R.id.btn_action_sub_kriteria) Button btnSubKriteria;

    private ProgressBarUtils barUtils;

    private String kriteriaId, id;
    private boolean isEdit;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference mKriteriaRef;

    private TextView tvSatuan;
    private Intent intent;

    private int idSatuan, satuan = 0;
    private String kode, nama, min, max;
    private Map<String, Object> subKriterias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_sub_kriteria);
        ButterKnife.bind(this);

        barUtils = new ProgressBarUtils(this);

        intent = new Intent();

        kriteriaId = getIntent().getExtras().getString(KEY_REF);

        if (kriteriaId == null) {
            throw new IllegalArgumentException("must pass reference id " + kriteriaId);
        }

        if (getIntent().getExtras().getString(KEY_ID).equals(ADD_DATA)) {
            isEdit = false;
            toolbar.setTitle(getString(R.string.toolbar_add, getString(R.string.menu_2)));
            btnSubKriteria.setText(getString(R.string.btn_save_ka));
        }
        else {
            barUtils.show();
            isEdit = true;
            toolbar.setTitle(getString(R.string.toolbar_edit, getString(R.string.menu_2)));
            id = getIntent().getExtras().getString(KEY_ID);
            tlSubKodeKriteria.setEnabled(false);
            btnSubKriteria.setText(getString(R.string.btn_update_ka));
        }

        edtSubNilaiMaxKriteria.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnSubKriteria();
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

        subKriterias = new HashMap<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onSatuan();
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
            mKriteriaRef = mFirestore.collection(Kriteria.COLLECTION).document(kriteriaId);
            mKriteriaRef.collection(SubKriteria.COLLECTION).document(id).get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            SubKriteria subKriteria = snapshot.toObject(SubKriteria.class);
                            setSubKriteria(subKriteria);
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
    public void onSatuan() {
        ArrayAdapter<String> satuan = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.satuan_nilai)) {

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view =  super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }

            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        spSatuanNilai.setAdapter(satuan);

        idSatuan = 0;

        spSatuanNilai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    idSatuan = 0;
                    tlSubNilaiMinKriteria.setEnabled(false);
                    tlSubNilaiMaxKriteria.setEnabled(false);
                } else {
                    tlSubNilaiMinKriteria.setEnabled(true);
                    tlSubNilaiMaxKriteria.setEnabled(true);
                }
                if (position == 1) {
                    idSatuan = 1;
                    edtSubNilaiMinKriteria.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
                    edtSubNilaiMaxKriteria.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
                }
                if (position == 2) {
                    idSatuan = 2;
                    edtSubNilaiMinKriteria.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    edtSubNilaiMaxKriteria.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void setSubKriteria(SubKriteria subKriteria) {
        edtSubKodeKriteria.setText(subKriteria.getSubKodeKriteria());
        edtSubNamaKriteria.setText(subKriteria.getSubNamaKriteria());
        if (subKriteria.getSubTipeNilaiKriteria() == 1) {
            edtSubNilaiMinKriteria.setText(String.valueOf((int) subKriteria.getSubNilaiMinKriteria()));
            edtSubNilaiMaxKriteria.setText(String.valueOf((int) subKriteria.getSubNilaiMaxKriteria()));
        } else {
            edtSubNilaiMinKriteria.setText(String.valueOf(subKriteria.getSubNilaiMinKriteria()));
            edtSubNilaiMaxKriteria.setText(String.valueOf(subKriteria.getSubNilaiMaxKriteria()));
        }
        spSatuanNilai.setSelection(subKriteria.getSubTipeNilaiKriteria());
    }

    @Override
    public boolean validate() {
        boolean valid = true;

        tvSatuan = (TextView) spSatuanNilai.getSelectedView();

        satuan = idSatuan;
        kode = tlSubKodeKriteria.getEditText().getText().toString();
        nama = tlSubNamaKriteria.getEditText().getText().toString();
        min = tlSubNilaiMinKriteria.getEditText().getText().toString();
        max = tlSubNilaiMaxKriteria.getEditText().getText().toString();

        if (Validation.isEmptyString(kode)) {
            valid = false;
            tlSubKodeKriteria.setErrorEnabled(true);
            tlSubKodeKriteria.setError(getString(R.string.error_ka_0, getString(R.string.hint_kode_sub_kriteria)));
            tlSubKodeKriteria.requestFocus();
        }
        else if (Validation.isLengthKode(kode,6,6)) {
            valid = false;
            tlSubKodeKriteria.setErrorEnabled(true);
            tlSubKodeKriteria.setError(getString(R.string.error_ka_1, getString(R.string.hint_kode_sub_kriteria), 6, 6));
            tlSubKodeKriteria.requestFocus();
        }
        else if (!Validation.isRegexKodeSubKriteria(kode)) {
            valid = false;
            tlSubKodeKriteria.setErrorEnabled(true);
            tlSubKodeKriteria.setError(getString(R.string.error_ka_2, getString(R.string.hint_kode_sub_kriteria), "SK"));
            tlSubKodeKriteria.requestFocus();
        }
        else {
            tlSubKodeKriteria.setError(null);
            tlSubKodeKriteria.setErrorEnabled(false);
        }

        if (Validation.isEmptyString(nama)) {
            valid = false;
            tlSubNamaKriteria.setErrorEnabled(true);
            tlSubNamaKriteria.setError(getString(R.string.error_ka_0, getString(R.string.hint_nama_kriteria)));
            tlSubNamaKriteria.requestFocus();
        }
        else if (Validation.isLengthNama(nama, 3, 20)) {
            valid = false;
            tlSubNamaKriteria.setErrorEnabled(true);
            tlSubNamaKriteria.setError(getString(R.string.error_ka_1, getString(R.string.hint_kode_kriteria), 3, 20));
            tlSubNamaKriteria.requestFocus();
        }
        else {
            tlSubNamaKriteria.setError(null);
            tlSubNamaKriteria.setErrorEnabled(false);
        }

        if (spSatuanNilai.getSelectedItemPosition() == 0) {
            valid = false;
            tvSatuan.setError(getString(R.string.error_ka_0, getString(R.string.hint_std_nilai_satuan)));
            tvSatuan.requestFocus();
        }
        else {
            tvSatuan.setError(null);
        }

        if (Validation.isEmptyString(min)) {
            valid = false;
            tlSubNilaiMinKriteria.setErrorEnabled(true);
            tlSubNilaiMinKriteria.setError(getString(R.string.error_ka_0, getString(R.string.hint_nilai_min_sub_kriteria)));
            tlSubNilaiMinKriteria.requestFocus();
        }
        else {
            tlSubNilaiMinKriteria.setError(null);
            tlSubNilaiMinKriteria.setErrorEnabled(false);
        }

        if (Validation.isEmptyString(max)) {
            valid = false;
            tlSubNilaiMaxKriteria.setErrorEnabled(true);
            tlSubNilaiMaxKriteria.setError(getString(R.string.error_ka_0, getString(R.string.hint_nilai_max_sub_kriteria)));
            tlSubNilaiMaxKriteria.requestFocus();
        }
        else {
            tlSubNilaiMaxKriteria.setError(null);
            tlSubNilaiMaxKriteria.setErrorEnabled(false);
        }

        if (satuan == 1) {
            tvSatuan.setError(null);

            if (!Validation.isNumberInteger(min)) {
                valid = false;
                tlSubNilaiMinKriteria.setErrorEnabled(true);
                tlSubNilaiMinKriteria.setError(getString(R.string.error_ka_6, getString(R.string.hint_nilai_min_sub_kriteria)));
                tlSubNilaiMinKriteria.requestFocus();
            }
            else if (Integer.parseInt(min) > Integer.parseInt(max)) {
                valid = false;
                tlSubNilaiMinKriteria.setErrorEnabled(true);
                tlSubNilaiMinKriteria.setError(getString(R.string.error_ka_4, getString(R.string.hint_nilai_min_sub_kriteria), getString(R.string.hint_nilai_max_sub_kriteria)));
                tlSubNilaiMinKriteria.requestFocus();
            }
            else {
                tlSubNilaiMinKriteria.setError(null);
                tlSubNilaiMinKriteria.setErrorEnabled(false);
            }

            if (!Validation.isNumberInteger(max)) {
                valid = false;
                tlSubNilaiMaxKriteria.setErrorEnabled(true);
                tlSubNilaiMaxKriteria.setError(getString(R.string.error_ka_6, getString(R.string.hint_nilai_max_sub_kriteria)));
                tlSubNilaiMaxKriteria.requestFocus();
            }
            else if (Integer.parseInt(max) > 9999) {
                valid = false;
                tlSubNilaiMaxKriteria.setErrorEnabled(true);
                tlSubNilaiMaxKriteria.setError(getString(R.string.error_ka_5, getString(R.string.hint_nilai_max_sub_kriteria), "9999"));
                tlSubNilaiMaxKriteria.requestFocus();
            }
            else if (Integer.valueOf(max).equals(Integer.valueOf(min))) {
                valid = false;
                tlSubNilaiMaxKriteria.setErrorEnabled(true);
                tlSubNilaiMaxKriteria.setError(getString(R.string.error_ka_7, getString(R.string.hint_nilai_max_sub_kriteria), getString(R.string.hint_nilai_min_sub_kriteria)));
                tlSubNilaiMaxKriteria.requestFocus();
            }
            else {
                tlSubNilaiMaxKriteria.setError(null);
                tlSubNilaiMaxKriteria.setErrorEnabled(false);
            }

        }
        else if (satuan == 2) {
            tvSatuan.setError(null);

            if (!Validation.isNumberDouble(min)) {
                valid = false;
                tlSubNilaiMinKriteria.setErrorEnabled(true);
                tlSubNilaiMinKriteria.setError(getString(R.string.error_ka_6, getString(R.string.hint_nilai_min_sub_kriteria)));
                tlSubNilaiMinKriteria.requestFocus();
            }
            else if (Double.parseDouble(min) > Double.parseDouble(max)) {
                valid = false;
                tlSubNilaiMinKriteria.setErrorEnabled(true);
                tlSubNilaiMinKriteria.setError(getString(R.string.error_ka_4, getString(R.string.hint_nilai_min_sub_kriteria), getString(R.string.hint_nilai_max_sub_kriteria)));
                tlSubNilaiMinKriteria.requestFocus();
            }
            else {
                tlSubNilaiMinKriteria.setError(null);
                tlSubNilaiMinKriteria.setErrorEnabled(false);
            }

            if (!Validation.isNumberDouble(max)) {
                valid = false;
                tlSubNilaiMaxKriteria.setErrorEnabled(true);
                tlSubNilaiMaxKriteria.setError(getString(R.string.error_ka_6, getString(R.string.hint_nilai_max_sub_kriteria)));
                tlSubNilaiMaxKriteria.requestFocus();
            }
            else if (Double.parseDouble(max) > 100) {
                valid = false;
                tlSubNilaiMaxKriteria.setErrorEnabled(true);
                tlSubNilaiMaxKriteria.setError(getString(R.string.error_ka_5, getString(R.string.hint_nilai_max_sub_kriteria), "100.0"));
                tlSubNilaiMaxKriteria.requestFocus();
            }
            else if (Double.parseDouble(max) == Double.parseDouble(min)) {
                valid = false;
                tlSubNilaiMaxKriteria.setErrorEnabled(true);
                tlSubNilaiMaxKriteria.setError(getString(R.string.error_ka_7, getString(R.string.hint_nilai_max_sub_kriteria), getString(R.string.hint_nilai_min_sub_kriteria)));
                tlSubNilaiMaxKriteria.requestFocus();
            }
            else {
                tlSubNilaiMaxKriteria.setError(null);
                tlSubNilaiMaxKriteria.setErrorEnabled(false);
            }

        }
        else {
            valid = false;
            tvSatuan.setError(getString(R.string.error_ka_0, getString(R.string.hint_std_nilai_satuan)));
            tvSatuan.requestFocus();
        }

        return valid;
    }

    @Override
    public void onResetForm() {
        edtSubKodeKriteria.setText(null);
        edtSubNamaKriteria.setText(null);
        spSatuanNilai.setSelection(0);
        edtSubNilaiMinKriteria.setText(null);
        edtSubNilaiMaxKriteria.setText(null);
    }

    @OnClick(R.id.btn_action_sub_kriteria) void btnSubKriteria() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (!validate()) return;

        barUtils.show();

        subKriterias.put(SubKriteria.FIELD_NAMA_SUB_KRITERIA, nama);
        subKriterias.put(SubKriteria.FIELD_TIPE_NILAI, satuan);
        subKriterias.put(SubKriteria.FIELD_NILAI_MIN_SUB_KRITERIA, Double.valueOf(min));
        subKriterias.put(SubKriteria.FIELD_NILAI_MAX_SUB_KRITERIA, Double.valueOf(max));

        if (!isEdit) {
            subKriterias.put(SubKriteria.FIELD_KODE_SUB_KRITERIA, kode);
            subKriterias.put(SubKriteria.TIMESTAMPS, Timestamp.now());
            mKriteriaRef = mFirestore.collection(Kriteria.COLLECTION).document(kriteriaId);
            mKriteriaRef.collection(SubKriteria.COLLECTION).document(kode).set(subKriterias)
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
            mKriteriaRef = mFirestore.collection(Kriteria.COLLECTION).document(kriteriaId);
            mKriteriaRef.collection(SubKriteria.COLLECTION).document(id).update(subKriterias)
                    .addOnSuccessListener(aVoid -> {
                        onResetForm();
                        setResult(RESULT_UPDATE_SUCCESS);
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
}
