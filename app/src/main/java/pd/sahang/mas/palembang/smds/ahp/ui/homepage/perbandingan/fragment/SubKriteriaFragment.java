package pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.App;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.HitungPerbandinganAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.AhpModelHelper;
import pd.sahang.mas.palembang.smds.ahp.models.HitungPerbandinganModel;
import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;
import pd.sahang.mas.palembang.smds.ahp.models.MatriksNilaiKriteria;
import pd.sahang.mas.palembang.smds.ahp.models.MatriksPerbandinganBerpasangan;
import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganKriteria;
import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganSubKriteria;
import pd.sahang.mas.palembang.smds.ahp.models.SubKriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.dialog.DialogResultPerbandingan;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.inisiasi.SubKriteriaFragmentInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.AhpHelper;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class SubKriteriaFragment extends Fragment implements SubKriteriaFragmentInterface {

    private static final String TAG = SubKriteriaFragment.class.getSimpleName();

    private static final String POSITION = "position";
    private static final String KODE = "kode";
    private static final String SIZE = "size";

    private View mSubKriteria;

    @BindView(R.id.rvHitungPerbandinganSubKriteria) RecyclerView rvHitungSubKriteria;
    @BindView(R.id.btnSubmitPerbandinganSubKriteria) Button btnSubmit;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.shimmer_view_hitung_perbandingan) ShimmerFrameLayout mShimmerFrameLayout;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;

    private FirebaseFirestore mFirestore;

    private ArrayList<SubKriteria> subKriterias;
    private ArrayList<HitungPerbandinganModel> models;
    private ArrayList<AhpModelHelper> ahpModelHelpers;
    private HitungPerbandinganAdapter mAdapter;
    private AhpHelper mHelper;

    private int jumlahSubKriteria;
    private int positionKepentingan;
    private double eigenVektor, consIndex, consRatio;
    private double[] priorityVektor;

    private RadioGroup rg;
    private RadioButton rb;
    private AppCompatSpinner mSpinner;

    private int value, pilih;
    private DecimalFormat df;
    private Map<Object, Object> perbandingan;
    private boolean completed;
    private int timer;

    private ArrayList<MatriksPerbandinganBerpasangan> matriksPerbandinganBerpasangans;
    private ArrayList<MatriksNilaiKriteria> matriksNilaiKriterias;
    private DialogResultPerbandingan mDialogResultPerbandingan;
    private Bundle args;

    private int position, size;
    private String kode;

    private Callbacks mCallbacks;

    public SubKriteriaFragment() {
        // Required empty public constructor
    }

    public static SubKriteriaFragment newInstance(int position, List<Kriteria> kriterias) {
        SubKriteriaFragment fragment = new SubKriteriaFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putInt(SIZE, kriterias.size());
        for (int i = 0; i < kriterias.size(); i++) {
            if (i == position) {
                args.putString(KODE, kriterias.get(i).getKodeKriteria());
                break;
            }
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSubKriteria = inflater.inflate(R.layout.fragment_sub_kriteria, container, false);
        ButterKnife.bind(this, mSubKriteria);
        barUtils = new ProgressBarUtils(mSubKriteria.getContext());
        snackBarUtils = new SnackBarUtils(mSubKriteria.getContext());
        df = new DecimalFormat("#.#####");

        if (getArguments() != null) {
            position = getArguments().getInt(POSITION);
            size = getArguments().getInt(SIZE);
            kode = getArguments().getString(KODE);
        }

        if (mCallbacks != null) {
            mCallbacks.onAttachSubKriteriaFragment();
            onSetupFirestore();
        }
        mDialogResultPerbandingan = new DialogResultPerbandingan();
        mDialogResultPerbandingan.setCancelable(false);
        mDialogResultPerbandingan.setSub(true);
        mDialogResultPerbandingan.setPosition(position);
        args = new Bundle();

        return mSubKriteria;
    }

    @Override
    public void onSetupFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection(Kriteria.COLLECTION).document(kode).collection(SubKriteria.COLLECTION).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onSubKriteriaLoad(task);
                    } else {
                        mCallbacks.onDetachSubKriteriaFragment();
                    }
                });
    }

    @Override
    public void onSubKriteriaLoad(Task<QuerySnapshot> task) {
        jumlahSubKriteria = task.getResult().size();
        subKriterias = new ArrayList<>(task.getResult().toObjects(SubKriteria.class));
        models = new ArrayList<>();
        for (int i = 0; i <= (jumlahSubKriteria - 2); i++) {
            for (int j = (i+1); j <= (jumlahSubKriteria -1); j++) {
                models.add(new HitungPerbandinganModel(
                        subKriterias.get(i).getSubKodeKriteria(),
                        subKriterias.get(j).getSubKodeKriteria(),
                        subKriterias.get(i).getSubNamaKriteria(),
                        subKriterias.get(j).getSubNamaKriteria()
                ));
            }
        }

        new Handler().postDelayed(() -> {
            if (models.size() == 0 || models.size() < 3) {
                rvHitungSubKriteria.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mShimmerFrameLayout.stopShimmer();
                mShimmerFrameLayout.setVisibility(View.GONE);
            } else {
                rvHitungSubKriteria.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
                mShimmerFrameLayout.stopShimmer();
                mShimmerFrameLayout.setVisibility(View.GONE);

                onRecyclerView();
            }
        }, 2000);
    }

    @Override
    public void onRecyclerView() {
        rvHitungSubKriteria.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setChangeDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvHitungSubKriteria.setItemAnimator(itemAnimator);

        mAdapter = new HitungPerbandinganAdapter(models);
        rvHitungSubKriteria.setAdapter(mAdapter);
    }

    @Override
    public boolean onValidate() {
        boolean valid = true;
        for (int i = 0; i < rvHitungSubKriteria.getChildCount(); i++) {
            for (int j = 0; j < ((ViewGroup)rvHitungSubKriteria.getChildAt(i)).getChildCount(); j++) {
                ViewGroup vg = ((ViewGroup) ((ViewGroup)rvHitungSubKriteria.getChildAt(i)).getChildAt(j));
                TextView tvError = (TextView)((AppCompatSpinner) vg.getChildAt(3)).getSelectedView();
                positionKepentingan = ((AppCompatSpinner) vg.getChildAt(3)).getSelectedItemPosition();
                if (positionKepentingan != 0) {
                    tvError.setError(null);
                } else {
                    valid = false;
                    tvError.setError(getString(R.string.error_perbandingan_0));
                    tvError.requestFocus();
                }
            }
        }
        return valid;
    }

    @Override
    public void onProcessCounting() {
        pilih = 0;
        value = 0;
        timer = 0;
        completed = false;

        matriksPerbandinganBerpasangans = new ArrayList<>();
        matriksNilaiKriterias = new ArrayList<>();

        ahpModelHelpers = new ArrayList<>();
        perbandingan = new HashMap<>();

        for (int i = 0; i < rvHitungSubKriteria.getChildCount(); i++) {
            for (int j = 0; j < ((ViewGroup) rvHitungSubKriteria.getChildAt(i)).getChildCount(); j++) {
                ViewGroup vg = ((ViewGroup) ((ViewGroup)rvHitungSubKriteria.getChildAt(i)).getChildAt(j));
                rg = ((RadioGroup) vg.getChildAt(2));
                mSpinner = ((AppCompatSpinner) vg.getChildAt(3));
                if (rg.getCheckedRadioButtonId() != -1) {
                    pilih = rg.getCheckedRadioButtonId();
                    rb = rg.findViewById(pilih);
                    if (rb.getId() == R.id.pilihanA && rb.isChecked()) {
                        value = 1;
                    } else {
                        value = 2;
                    }
                }
                positionKepentingan = mSpinner.getSelectedItemPosition();
                ahpModelHelpers.add(new AhpModelHelper(value, positionKepentingan));
            }
        }

        mHelper = new AhpHelper(ahpModelHelpers, jumlahSubKriteria);
        mHelper.running(true);

        eigenVektor = mHelper.getEigenVektor();
        consIndex = mHelper.getConsIndex();
        consRatio = mHelper.getConsRatio();
        priorityVektor = new double[jumlahSubKriteria];

        for (int i = 0; i <= (jumlahSubKriteria - 1); i++) {
            matriksPerbandinganBerpasangans.add(new MatriksPerbandinganBerpasangan(subKriterias.get(i).getSubNamaKriteria(), mHelper.getJmlmpb()[i]));
            priorityVektor[i] = mHelper.getPriorityVektor()[i];
            matriksNilaiKriterias.add(new MatriksNilaiKriteria(subKriterias.get(i).getSubNamaKriteria(), mHelper.getJmlmnk()[i], priorityVektor[i]));
            setPerbandinganSubKriteria(
                    subKriterias.get(i).getSubKodeKriteria(),
                    subKriterias.get(i).getSubNamaKriteria(),
                    subKriterias.get(i).getSubTipeNilaiKriteria(),
                    subKriterias.get(i).getSubNilaiMinKriteria(),
                    subKriterias.get(i).getSubNilaiMaxKriteria(),
                    df.format(priorityVektor[i])
            );
            if (i == (jumlahSubKriteria - 1)) completed = true;
        }

        timer = (1 + jumlahSubKriteria) * 1000;

        new Handler().postDelayed(() -> {
            if (completed) {
                args.putDouble(DialogResultPerbandingan.EV, eigenVektor);
                args.putDouble(DialogResultPerbandingan.CI, consIndex);
                args.putDouble(DialogResultPerbandingan.CR, consRatio);
                args.putString(DialogResultPerbandingan.TITLE, getString(R.string.menu_4));
                mDialogResultPerbandingan.setMatriksPerbandinganBerpasangans(matriksPerbandinganBerpasangans);
                mDialogResultPerbandingan.setMatriksNilaiKriterias(matriksNilaiKriterias);
                mDialogResultPerbandingan.setArguments(args);
                if (position == (size - 1)) {
                    mDialogResultPerbandingan.setLastPosition(true);
                } else {
                    mDialogResultPerbandingan.setLastPosition(false);
                }
                mDialogResultPerbandingan.show(getChildFragmentManager(), DialogResultPerbandingan.TAG);
                barUtils.hide();
            }
        }, timer);
    }

    @Override
    public void setPerbandinganSubKriteria(String subKodeKriteria, String subNamaKriteria, int subTipeNilai,
                                           double subNilaiMinKriteria, double subNilaiMaxKriteria, String priorityVektor) {
        perbandingan.put(PerbandinganSubKriteria.FIELD_SUB_KODE_KRITERIA, subKodeKriteria);
        perbandingan.put(PerbandinganSubKriteria.FIELD_SUB_NAMA_KRITERIA, subNamaKriteria);
        perbandingan.put(PerbandinganSubKriteria.FIELD_TIPE_NILAI, subTipeNilai);
        perbandingan.put(PerbandinganSubKriteria.FIELD_NILAI_MIN_SUB_KRITERIA, subNilaiMinKriteria);
        perbandingan.put(PerbandinganSubKriteria.FIELD_NILAI_MAX_SUB_KRITERIA, subNilaiMaxKriteria);
        perbandingan.put(PerbandinganSubKriteria.FIELD_SUB_NILAI_KRITERIA, Double.parseDouble(priorityVektor));
        mFirestore.collection(PerbandinganKriteria.COLLECTION).document(kode)
                .collection(PerbandinganSubKriteria.COLLECTION).document(subKodeKriteria)
                .set(perbandingan)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "setPerbandinganSubKriteria() called with: subKodeKriteria = [" + subKodeKriteria + "], priorityVektor = [" + priorityVektor + "]"))
                .addOnFailureListener(e -> Log.e(TAG, "setPerbandinganSubKriteria: ", e));
    }

    @Override
    public void onAttachResultPerbandingan() {
        Log.d(TAG, "onAttachResultPerbandingan() called");
    }

    @Override
    public void onDetachResultPerbandingan() {
        Log.d(TAG, "onDetachResultPerbandingan() called");
    }

    @Override
    public void onSubKriteriaLanjut(int position) {
        if (mCallbacks != null) {
            mCallbacks.onLanjutSub(position);
        }
    }

    @Override
    public void onSubKriteriaLast() {
        App.mActivity.finish();
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

    @OnClick(R.id.btnSubmitPerbandinganSubKriteria) void submit() {
        if (!onValidate()) return;

        barUtils.show();

        onProcessCounting();
    }

    public interface Callbacks {
        void onAttachSubKriteriaFragment();
        void onDetachSubKriteriaFragment();
        void onLanjutSub(int position);
    }
}
