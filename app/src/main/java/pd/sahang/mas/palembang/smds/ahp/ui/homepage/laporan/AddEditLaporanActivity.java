package pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.BuildConfig;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.AddLaporanAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.DetailLaporan;
import pd.sahang.mas.palembang.smds.ahp.models.DetailLaporanItem;
import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;
import pd.sahang.mas.palembang.smds.ahp.models.Laporan;
import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganKriteria;
import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganSubKriteria;
import pd.sahang.mas.palembang.smds.ahp.models.SubKriteria;
import pd.sahang.mas.palembang.smds.ahp.models.Users;
import pd.sahang.mas.palembang.smds.ahp.ui.auth.AuthActivity;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan.inisiasi.AddEditLaporanInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.Config;
import pd.sahang.mas.palembang.smds.ahp.utils.FileCompressor;
import pd.sahang.mas.palembang.smds.ahp.utils.MySingleton;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SharedPrefManager;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.UserHelper;
import pd.sahang.mas.palembang.smds.ahp.utils.Validation;

import static pd.sahang.mas.palembang.smds.ahp.App.CHANNEL_ID_UPLOAD;

public class AddEditLaporanActivity extends AppCompatActivity implements AddEditLaporanInterface {

    private static final String TAG = AddEditLaporanActivity.class.getSimpleName();

    public static final String ADD_DATA_TIPE_1 = "add_data_tipe_1";
    public static final String ADD_DATA_TIPE_2 = "add_data_tipe_2";
    public static final String DOCUMENT_ID = "document_id";

    public static final String KEY_ID = "key_id";

    public static final int REQUEST_ADD_TIPE_1 = 7409;
    public static final int RESULT_ADD_TIPE_1_SUCCESS = 7402;
    public static final int RESULT_ADD_TIPE_1_FAILED = 7404;

    public static final int REQUEST_UPDATE_TIPE_1 = 4322;
    public static final int RESULT_UPDATE_TIPE_1_SUCCESS = 5463;
    public static final int RESULT_UPDATE_TIPE_1_FAILED = 4523;

    public static final int REQUEST_ADD_TIPE_2 = 3421;
    public static final int RESULT_ADD_TIPE_2_SUCCESS = 2132;
    public static final int RESULT_ADD_TIPE_2_FAILED = 1414;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.img_karung) AppCompatImageView mImgKarung;
    @BindView(R.id.tl_nama_karung) TextInputLayout tlNamaKarung;
    @BindView(R.id.edt_nama_karung) TextInputEditText edtNamaKarung;
    @BindView(R.id.rvKriteriaLaporan) RecyclerView rvKriteriaLaporan;
    @BindView(R.id.btnSubmitLaporan) Button btnLaporan;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.shimmer_view_kriteria) ShimmerFrameLayout mShimmerFrameLayout;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;
    private SharedPrefManager mPrefManager;

    private String id;
    private boolean isTipe;
    private boolean isEdit;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference mLaporanRef, mSubKriteriaRef, mLaporanItemRef;
    private ListenerRegistration mKriteriaRegister;
    private Query mQuery;

    private FirebaseStorage mStorage;
    private StorageReference mStorageReference, mStorageLaporan;

    private AddLaporanAdapter mAdapter;
    private DecimalFormat df;

    private Intent intent;
    private String nama, gambar;
    private Uri filePath;
    private Drawable mDrawable;
    private BitmapDrawable mBitmapDrawable;

    private File mPhotoFile;
    private FileCompressor mFileCompressor;

    private Map<String, Object> laporan;
    private Map<String, Object> detailLaporan;
    private Map<String, Object> detailLaporanItem;

    private String documentId;

    private int sizePerbandingan;

    private int jumlahKriteria;
    private ArrayList<PerbandinganKriteria> perbandinganKriterias;
    private List<PerbandinganKriteria> perbandinganKriteriaList;
    private double pvKriteria, pvSubKriteria, value, min, max, total;
    private String sub;

    private String namaUser;

    private NotificationCompat.Builder builder;
    private NotificationManagerCompat notificationManagerCompat;
    private double progress;
    private String grade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_laporan);
        ButterKnife.bind(this);

        documentId = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        mFileCompressor = new FileCompressor(this);
        barUtils = new ProgressBarUtils(this);
        snackBarUtils = new SnackBarUtils(this);
        mPrefManager = new SharedPrefManager(this);

        builder = new NotificationCompat.Builder(this, CHANNEL_ID_UPLOAD);
        notificationManagerCompat = NotificationManagerCompat.from(this);

        df = new DecimalFormat("#.#####");

        intent = new Intent();

        if (getIntent().getExtras().getString(KEY_ID).equals(ADD_DATA_TIPE_1)) {
            isTipe = true;
            isEdit = false;
            documentId = getIntent().getStringExtra(DOCUMENT_ID);
            toolbar.setTitle(getString(R.string.toolbar_add, getString(R.string.menu_7)));
            btnLaporan.setText(getString(R.string.btn_save_ka));
        } else if (getIntent().getExtras().getString(KEY_ID).equals(ADD_DATA_TIPE_2)) {
            isTipe = false;
            isEdit = false;
            toolbar.setTitle(getString(R.string.toolbar_add, getString(R.string.menu_7)));
            btnLaporan.setText(getString(R.string.btn_save_ka));
        } else {
            barUtils.show();
            isTipe = false;
            isEdit = true;
            toolbar.setTitle(getString(R.string.toolbar_edit, getString(R.string.menu_7)));
            id = getIntent().getExtras().getString(KEY_ID);
            btnLaporan.setText(getString(R.string.btn_update_ka));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Glide.with(this).load(R.drawable.placeholder_karung).fitCenter().into(mImgKarung);

        onAuthenticationFirebase();

        onInitFirestore();

        onRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                try {
                    mPhotoFile = mFileCompressor.compressToFile(mPhotoFile);
                    Glide.with(this)
                            .load(mPhotoFile)
                            .apply(new RequestOptions().centerCrop().placeholder(R.drawable.placeholder_karung))
                            .into(mImgKarung);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }

        if (mKriteriaRegister != null) {
            mKriteriaRegister.remove();
            mKriteriaRegister = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShimmerFrameLayout.startShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShimmerFrameLayout.stopShimmer();
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
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        mFirestore.collection(PerbandinganKriteria.COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sizePerbandingan = task.getResult().size();
                    } else {
                        sizePerbandingan = 0;
                    }
                });


        if (isEdit) {
            mLaporanRef = mFirestore.collection(DetailLaporan.COLLECTION).document(id);
            mLaporanRef.get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            DetailLaporan detailLaporan = snapshot.toObject(DetailLaporan.class);
                            onDetailLaporan(detailLaporan);
                        }
                    })
                    .addOnFailureListener(e -> {
                        intent.putExtra("error", e.getMessage());
                        setResult(RESULT_UPDATE_TIPE_1_FAILED, intent);
                        finish();
                    });
        }

        mQuery = mFirestore.collection(Kriteria.COLLECTION)
                .orderBy(Kriteria.FIELD_KODE_KRITERIA, Query.Direction.ASCENDING)
                .limit(500);

        mFirestore.collection(PerbandinganKriteria.COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getKriteria(task);
                    }
                });
    }

    @Override
    public void onDetailLaporan(DetailLaporan detailLaporan) {
        Glide.with(this)
                .load(detailLaporan.getGambarKarung())
                .fitCenter()
                .placeholder(R.drawable.placeholder_karung)
                .into(mImgKarung);
        edtNamaKarung.setText(detailLaporan.getNamaKarung());
    }

    @Override
    public boolean validate() {
        boolean valid = true;

        mDrawable = mImgKarung.getDrawable();
        mBitmapDrawable = mDrawable instanceof BitmapDrawable ? (BitmapDrawable) mDrawable : null;

        nama = tlNamaKarung.getEditText().getText().toString();

        if (mBitmapDrawable == null || mBitmapDrawable.getBitmap() == null) {
            valid = false;
            snackBarUtils.snackBarLong(getString(R.string.hint_gambar_karung));
        }

        if (Validation.isEmptyString(nama)) {
            valid = false;
            tlNamaKarung.setErrorEnabled(true);
            tlNamaKarung.setError(getString(R.string.error_laporan, getString(R.string.hint_nama_karung)));
            tlNamaKarung.requestFocus();
        } else if (Validation.isLengthNama(nama, 3, 40)) {
            valid = false;
            tlNamaKarung.setErrorEnabled(true);
            tlNamaKarung.setError(getString(R.string.error_ka_1, getString(R.string.hint_nama_karung), 3, 40));
            tlNamaKarung.requestFocus();
        } else {
            tlNamaKarung.setError(null);
            tlNamaKarung.setErrorEnabled(false);
        }

        for (int i = 0; i < rvKriteriaLaporan.getChildCount(); i++) {
            for (int j = 0; j < ((ViewGroup)rvKriteriaLaporan.getChildAt(i)).getChildCount(); j++) {
                ViewGroup vg = ((ViewGroup) ((ViewGroup) rvKriteriaLaporan.getChildAt(i)).getChildAt(j));
                TextView kodeKriteria = (TextView) vg.getChildAt(1);
                String kode = kodeKriteria.getText().toString();
                TextInputLayout tlKriteria = (TextInputLayout) vg.getChildAt(2);
                String nilai = tlKriteria.getEditText().getText().toString();
                if (nilai.isEmpty()) {
                    valid = false;
                    tlKriteria.setErrorEnabled(true);
                    tlKriteria.setError(getString(R.string.error_laporan, kode));
                    tlKriteria.requestFocus();
                } else {
                    tlKriteria.setError(null);
                    tlKriteria.setErrorEnabled(false);
                }
            }
        }

        return valid;
    }

    @Override
    public void onResetForm() {
        mImgKarung.setImageDrawable(null);
        edtNamaKarung.setText(null);
        rvKriteriaLaporan.setAdapter(null);
    }

    @Override
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                filePath = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
                startActivityForResult(takePictureIntent, 200);
            }
        }
    }

    @Override
    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }

    @Override
    public void onRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No Query, not installizing Recycler View");
        }

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setChangeDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvKriteriaLaporan.setLayoutManager(new LinearLayoutManager(this));
        rvKriteriaLaporan.setItemAnimator(itemAnimator);

        mAdapter = new AddLaporanAdapter(mQuery) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                snackBarUtils.snackBarLong(e.getMessage());
            }

            @Override
            protected void onDataChanged() {
                new Handler().postDelayed(() -> {
                    mShimmerFrameLayout.stopShimmer();
                    mShimmerFrameLayout.setVisibility(View.GONE);
                    if (getItemCount() == 0 || getItemCount() > sizePerbandingan || getItemCount() < sizePerbandingan) {
                        rvKriteriaLaporan.setVisibility(View.GONE);
                        btnLaporan.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                        snackBarUtils.snackbarShort("Hitung ulang perbandingan !");
                    } else {
                        rvKriteriaLaporan.setVisibility(View.VISIBLE);
                        btnLaporan.setVisibility(View.VISIBLE);
                        mEmptyView.setVisibility(View.GONE);
                        rvKriteriaLaporan.setAdapter(mAdapter);
                    }
                }, 2000);
            }
        };
    }

    @Override
    public void getKriteria(Task<QuerySnapshot> task) {
        jumlahKriteria = task.getResult().size();
        perbandinganKriterias = new ArrayList<>(task.getResult().toObjects(PerbandinganKriteria.class));
        perbandinganKriteriaList = new ArrayList<>();
        for (int i = 0; i < jumlahKriteria; i++) {
            perbandinganKriteriaList.add(
                    new PerbandinganKriteria(
                            perbandinganKriterias.get(i).getKodeKriteria(),
                            perbandinganKriterias.get(i).getNamaKriteria(),
                            perbandinganKriterias.get(i).getNilaiEigenVektorKriteria()
                    )
            );
        }
    }

    @Override
    public double getPvk(String kodeKriteria) {
        double n = 0;
        for (int i = 0; i <= (jumlahKriteria - 1); i++) {
            if (perbandinganKriteriaList.get(i).getKodeKriteria().equals(kodeKriteria)) {
                n = perbandinganKriteriaList.get(i).getNilaiEigenVektorKriteria();
                break;
            }
        }
        return n;
    }

    @Override
    public void getPvsk(String kode, String kodeSubKriteria) {
        mSubKriteriaRef = mFirestore.collection(PerbandinganKriteria.COLLECTION).document(kode);
        mSubKriteriaRef.collection(PerbandinganSubKriteria.COLLECTION).document(kodeSubKriteria).get().addOnCompleteListener(task -> {
            if (task.getResult().getString(PerbandinganSubKriteria.FIELD_SUB_KODE_KRITERIA).equals(kodeSubKriteria)) {
                pvKriteria = getPvk(kode);
                pvSubKriteria = task.getResult().getDouble(PerbandinganSubKriteria.FIELD_SUB_NILAI_KRITERIA);
                total += (pvKriteria * pvSubKriteria);
            }
        });
    }

    @Override
    public void onGetKriteria(String kode, String nilai) {
        mSubKriteriaRef = mFirestore.collection(Kriteria.COLLECTION).document(kode);
        mSubKriteriaRef.collection(SubKriteria.COLLECTION).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    value = Double.parseDouble(nilai);
                    min = snapshot.getDouble(SubKriteria.FIELD_NILAI_MIN_SUB_KRITERIA);
                    max = snapshot.getDouble(SubKriteria.FIELD_NILAI_MAX_SUB_KRITERIA);
                    if (min <= value && max >= value) {
                        sub = snapshot.getString(SubKriteria.FIELD_NAMA_SUB_KRITERIA);
                        getPvsk(kode, snapshot.getString(SubKriteria.FIELD_KODE_SUB_KRITERIA));
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void simpan(String idNyo) {
        for (int i = 0; i < rvKriteriaLaporan.getChildCount(); i++) {
            for (int j = 0; j < ((ViewGroup)rvKriteriaLaporan.getChildAt(i)).getChildCount(); j++) {
                ViewGroup vg = ((ViewGroup) ((ViewGroup) rvKriteriaLaporan.getChildAt(i)).getChildAt(j));
                TextView kodeKriteria = (TextView) vg.getChildAt(0);
                String kode = kodeKriteria.getText().toString();
                TextView kriterianyo = (TextView) vg.getChildAt(1);
                String kriteria = kriterianyo.getText().toString();
                TextInputLayout tlKriteria = (TextInputLayout) vg.getChildAt(2);
                String nilai = tlKriteria.getEditText().getText().toString();
                mSubKriteriaRef = mFirestore.collection(Kriteria.COLLECTION).document(kode);
                mSubKriteriaRef.collection(SubKriteria.COLLECTION).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            detailLaporanItem = new HashMap<>();
                            value = Double.parseDouble(nilai);
                            min = snapshot.getDouble(SubKriteria.FIELD_NILAI_MIN_SUB_KRITERIA);
                            max = snapshot.getDouble(SubKriteria.FIELD_NILAI_MAX_SUB_KRITERIA);
                            if (min <= value && max >= value) {
                                sub = snapshot.getString(SubKriteria.FIELD_NAMA_SUB_KRITERIA);
                                getPvsk(kode, snapshot.getString(SubKriteria.FIELD_KODE_SUB_KRITERIA));
                                detailLaporanItem.put(DetailLaporanItem.FIELD_NAMA_KRITERIA, kriteria);
                                detailLaporanItem.put(DetailLaporanItem.FIELD_NAMA_SUB_KRITERIA, sub);
                                detailLaporanItem.put(DetailLaporanItem.FIELD_NILAI_SUB_KRITERIA, Double.valueOf(nilai));

                                mLaporanItemRef = mFirestore.collection(Laporan.COLLECTION).document(documentId)
                                        .collection(DetailLaporan.COLLECTION).document(idNyo);
                                mLaporanItemRef.collection(DetailLaporanItem.COLLECTION).add(detailLaporanItem)
                                        .addOnCompleteListener(task1 -> setSuccessful(rvKriteriaLaporan.getChildCount(), task1.isSuccessful()))
                                        .addOnFailureListener(this::setErrorFirebase);
                                break;
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void setSuccessful(int count, boolean successful) {
        if (count == rvKriteriaLaporan.getChildCount()) {
            if (successful) {
                if (!isTipe) {
                    onResetForm();
                    setResult(RESULT_ADD_TIPE_2_SUCCESS);
                    finish();
                } else {
                    onResetForm();
                    setResult(RESULT_ADD_TIPE_1_SUCCESS);
                    finish();
                }
                barUtils.hide();
            }
        }
    }

    @Override
    public void setErrorFirebase(Exception e) {
        if (!isTipe) {
            onResetForm();
            intent.putExtra("error", e.getMessage());
            setResult(RESULT_ADD_TIPE_2_FAILED, intent);
            finish();
        } else {
            onResetForm();
            intent.putExtra("error", e.getMessage());
            setResult(RESULT_ADD_TIPE_1_FAILED, intent);
            finish();
        }
        barUtils.hide();
    }

    @Override
    public void sendNotification(String user, String idLaporan, String namaLaporan) {
        UserHelper.getUser(user).addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                namaUser = snapshot.getString(Users.FIELD_FULLNAME);
            }
        });

        new Handler().postDelayed(() -> {
            UserHelper.getUserCollection().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        String token = snapshot.getString(Users.FIELD_FIREBASE_TOKEN);
                        if (snapshot.getString(Users.FIELD_LEVEL).equals("admin") && token != null) {
                            JSONObject notification = new JSONObject();
                            JSONObject notificationBody = new JSONObject();

                            try {
                                notificationBody.put("title", namaLaporan);
                                notificationBody.put("subtitle", "Laporan");
                                notificationBody.put("message", namaUser + " memasukkan data " + namaLaporan);

                                notification.put("to", token);
                                notification.put("data", notificationBody);
                            } catch (JSONException e) {
                                Log.e(TAG, "sendNotification: ", e);
                            }
                            send(notification);
                        }
                    }
                }
            });
        },2000);
    }

    @Override
    public void send(@NonNull JSONObject jsonObject) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Config.getFcmApi(), jsonObject,
                response -> Log.d(TAG, "send: " + response.toString()), error -> {
                    Log.e(TAG, "send: ", error);
                    snackBarUtils.snackBarLong("Request Error");
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", Config.getServerKey());
                params.put("Content-Type", Config.getContentType());
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @OnClick(R.id.img_karung) void openCamera() {
        dispatchTakePictureIntent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (validate()) {
                return true;
            } else {
                snackBarUtils.snackBarLong("Lengkapi Data !");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.btnSubmitLaporan) void submit() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (!validate()) return;

        barUtils.show();

        laporan = new HashMap<>();
        detailLaporan = new HashMap<>();

        for (int i = 0; i < rvKriteriaLaporan.getChildCount(); i++) {
            for (int j = 0; j < ((ViewGroup) rvKriteriaLaporan.getChildAt(i)).getChildCount(); j++) {
                ViewGroup vg = ((ViewGroup) ((ViewGroup) rvKriteriaLaporan.getChildAt(i)).getChildAt(j));
                TextView kodeKriteria = (TextView) vg.getChildAt(0);
                String kode = kodeKriteria.getText().toString();
                TextInputLayout tlKriteria = (TextInputLayout) vg.getChildAt(2);
                String nilai = tlKriteria.getEditText().getText().toString();
                onGetKriteria(kode, nilai);
            }
        }

        builder.setSmallIcon(R.drawable.ic_file_upload_grey_24dp)
                .setContentTitle("Upload")
                .setContentText("Upload in progress")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setChannelId(CHANNEL_ID_UPLOAD)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(0, 0, false);
        notificationManagerCompat.notify(2, builder.build());

        gambar = UUID.randomUUID().toString() + ".jpg";

        mStorageLaporan = mStorageReference.child("laporan/" + gambar);
        mStorageLaporan.putFile(filePath)
                .addOnSuccessListener(taskSnapshot -> {
                    builder.setContentText("Upload finished")
                            .setOngoing(false)
                            .setProgress(0, 0, false);
                    notificationManagerCompat.notify(2, builder.build());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "submit: ", e);
                    snackBarUtils.snackBarLong(e.getMessage());
                })
                .addOnProgressListener(taskSnapshot -> {
                    progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                    builder.setProgress(100, (int) progress, false);
                    notificationManagerCompat.notify(2, builder.build());
                });

        detailLaporan.put(DetailLaporan.FIELD_GAMBAR_KARUNG, gambar);

        new Handler().postDelayed(() -> {
            if (total > 0.5 && total < 0.6) {
                grade = "Grade 1";
            } else if (total > 0.2 && total < 0.49) {
                grade = "Grade 2";
            } else if (total > 0.0 && total < 0.19) {
                grade = "Grade 3";
            }
            detailLaporan.put(DetailLaporan.FIELD_ID_USER, mFirebaseAuth.getCurrentUser().getUid());
            detailLaporan.put(DetailLaporan.FIELD_NAMA_KARUNG, nama);
            detailLaporan.put(DetailLaporan.FIELD_GRADE_KARUNG, grade);
            detailLaporan.put(DetailLaporan.FIELD_NILAI_GRADE_KARUNG, Double.parseDouble(df.format(total)));
            detailLaporan.put(DetailLaporan.TIMESTAMPS, Timestamp.now());

            if (!isEdit) {
                if (!isTipe) {
                    laporan.put(Laporan.FIELD_KODE_LAPORAN, documentId);
                    laporan.put(Laporan.FIELD_NAMA_LAPORAN, Laporan.DEFAULT_NAMA + " " + documentId);
                    laporan.put(Laporan.TIMESTAMPS, Timestamp.now());
                    mFirestore.collection(Laporan.COLLECTION).document(documentId).set(laporan)
                            .addOnFailureListener(e -> Log.e(TAG, "submit: ", e));
                    new Handler().postDelayed(() -> {
                        mLaporanRef = mFirestore.collection(Laporan.COLLECTION).document(documentId);
                        mLaporanRef.collection(DetailLaporan.COLLECTION).add(detailLaporan)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        simpan(task.getResult().getId());
                                        if (mPrefManager.getSpLevel().equals("user")) {
                                            sendNotification(mFirebaseAuth.getCurrentUser().getUid(),
                                                    task.getResult().getId(),
                                                    detailLaporan.get(DetailLaporan.FIELD_NAMA_KARUNG).toString()
                                            );
                                        }
                                    }
                                });
                    }, 2000);
                }
                else {
                    mLaporanRef = mFirestore.collection(Laporan.COLLECTION).document(documentId);
                    mLaporanRef.collection(DetailLaporan.COLLECTION).add(detailLaporan)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    simpan(task.getResult().getId());
                                    if (mPrefManager.getSpLevel().equals("user")) {
                                        sendNotification(mFirebaseAuth.getCurrentUser().getUid(),
                                                task.getResult().getId(),
                                                detailLaporan.get(DetailLaporan.FIELD_NAMA_KARUNG).toString()
                                        );
                                    }
                                }
                            });
                }


            } else {
                mLaporanRef = mFirestore.collection(DetailLaporan.COLLECTION).document(id);
                mLaporanRef.update(detailLaporan)
                        .addOnSuccessListener(aVoid -> {
                            onResetForm();
                            setResult(RESULT_UPDATE_TIPE_1_SUCCESS);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            onResetForm();
                            intent.putExtra("error", e.getMessage());
                            setResult(RESULT_UPDATE_TIPE_1_FAILED, intent);
                            finish();
                        });
            }
        }, rvKriteriaLaporan.getChildCount() * 1500);
    }
}
