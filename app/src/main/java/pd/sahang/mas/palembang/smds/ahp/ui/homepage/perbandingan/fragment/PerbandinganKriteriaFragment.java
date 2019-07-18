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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.HitungPerbandinganAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.AhpModelHelper;
import pd.sahang.mas.palembang.smds.ahp.models.HitungPerbandinganModel;
import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;
import pd.sahang.mas.palembang.smds.ahp.models.MatriksNilaiKriteria;
import pd.sahang.mas.palembang.smds.ahp.models.MatriksPerbandinganBerpasangan;
import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganKriteria;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.dialog.DialogResultPerbandingan;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.inisiasi.PerbandinganKriteriaFragmentInterface;
import pd.sahang.mas.palembang.smds.ahp.utils.AhpHelper;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SnackBarUtils;

public class PerbandinganKriteriaFragment extends Fragment implements PerbandinganKriteriaFragmentInterface {

    private static final String TAG = PerbandinganKriteriaFragment.class.getSimpleName();

    private View mPerbandinganKriteria;

    @BindView(R.id.rvHitungPerbandinganKriteria) RecyclerView rvHitungKriteria;
    @BindView(R.id.btnSubmitPerbandinganKriteria) Button btnSubmit;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.shimmer_view_hitung_perbandingan) ShimmerFrameLayout mShimmerFrameLayout;

    private ProgressBarUtils barUtils;
    private SnackBarUtils snackBarUtils;

    private FirebaseFirestore mFirestore;

    private ArrayList<Kriteria> kriterias;
    private ArrayList<HitungPerbandinganModel> models;
    private ArrayList<AhpModelHelper> ahpModelHelpers;
    private HitungPerbandinganAdapter mAdapter;
    private AhpHelper mHelper;

    private int jumlahKriteria;
    private int positionKepentingan;
    private double eigenVektor, consIndex, consRatio;
    private double[] priorityVektor;

    private RadioGroup rg;
    private RadioButton rb;
    private AppCompatSpinner spinner;

    private int value, pilih;
    private DecimalFormat df;
    private Map<Object, Object> perbandingan;
    private boolean completed;
    private int timer;

    private Callbacks mCallbacks;

    private ArrayList<MatriksPerbandinganBerpasangan> matriksPerbandinganBerpasangans;
    private ArrayList<MatriksNilaiKriteria> matriksNilaiKriterias;
    private DialogResultPerbandingan mDialogResultPerbandingan;
    private Bundle args;

    public PerbandinganKriteriaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPerbandinganKriteria = inflater.inflate(R.layout.fragment_perbandingan_kriteria, container, false);
        ButterKnife.bind(this, mPerbandinganKriteria);
        barUtils = new ProgressBarUtils(mPerbandinganKriteria.getContext());
        snackBarUtils = new SnackBarUtils(mPerbandinganKriteria.getContext());
        df = new DecimalFormat("#.#####");

        if (mCallbacks != null) {
            mCallbacks.onAttachPerbandinganKriteriaFragment();
            onFirestoreSetup();
        }

        mDialogResultPerbandingan = new DialogResultPerbandingan();
        mDialogResultPerbandingan.setCancelable(false);
        mDialogResultPerbandingan.setLastPosition(false);
        mDialogResultPerbandingan.setSub(false);

        args = new Bundle();

        return mPerbandinganKriteria;
    }

    @Override
    public void onFirestoreSetup() {
        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection(Kriteria.COLLECTION).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onKriteriaLoad(task);
            } else {
                mCallbacks.onDetachPerbandinganKriteriaFragment();
            }
        });
    }

    @Override
    public void onKriteriaLoad(Task<QuerySnapshot> task) {
        jumlahKriteria = task.getResult().size();
        kriterias = new ArrayList<>(task.getResult().toObjects(Kriteria.class));
        models = new ArrayList<>();
        for (int i = 0; i <= (jumlahKriteria - 2); i++) {
            for (int j = (i+1); j <= (jumlahKriteria - 1); j++) {
                models.add(new HitungPerbandinganModel(
                        kriterias.get(i).getKodeKriteria(),
                        kriterias.get(j).getKodeKriteria(),
                        kriterias.get(i).getNamaKriteria(),
                        kriterias.get(j).getNamaKriteria()
                ));
            }
        }

        new Handler().postDelayed(() -> {
            if (models.size() == 0 || models.size() < 3) {
                rvHitungKriteria.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mShimmerFrameLayout.stopShimmer();
                mShimmerFrameLayout.setVisibility(View.GONE);
            } else {
                rvHitungKriteria.setVisibility(View.VISIBLE);
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
        rvHitungKriteria.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setChangeDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvHitungKriteria.setItemAnimator(itemAnimator);

        mAdapter = new HitungPerbandinganAdapter(models);
        rvHitungKriteria.setAdapter(mAdapter);
    }

    @Override
    public boolean onValidate() {
        boolean valid = true;
        for (int i = 0; i < rvHitungKriteria.getChildCount(); i++) {
            for (int j = 0; j < ((ViewGroup)rvHitungKriteria.getChildAt(i)).getChildCount(); j++) {
                ViewGroup vg = ((ViewGroup) ((ViewGroup)rvHitungKriteria.getChildAt(i)).getChildAt(j));
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

        for (int i = 0; i < rvHitungKriteria.getChildCount(); i++) {
            for (int j = 0; j < ((ViewGroup) rvHitungKriteria.getChildAt(i)).getChildCount(); j++) {
                ViewGroup vg = ((ViewGroup) ((ViewGroup)rvHitungKriteria.getChildAt(i)).getChildAt(j));
                rg = ((RadioGroup) vg.getChildAt(2));
                spinner = ((AppCompatSpinner) vg.getChildAt(3));
                if (rg.getCheckedRadioButtonId() != -1) {
                    pilih = rg.getCheckedRadioButtonId();
                    rb = rg.findViewById(pilih);
                    if (rb.getId() == R.id.pilihanA && rb.isChecked()) {
                        value = 1;
                    } else {
                        value = 2;
                    }
                }
                positionKepentingan = spinner.getSelectedItemPosition();
                ahpModelHelpers.add(new AhpModelHelper(value, positionKepentingan));
            }
        }

        mHelper = new AhpHelper(ahpModelHelpers, jumlahKriteria);
        mHelper.running(true);

        eigenVektor = mHelper.getEigenVektor();
        consIndex = mHelper.getConsIndex();
        consRatio = mHelper.getConsRatio();
        priorityVektor = new double[jumlahKriteria];

        for (int i = 0; i <= (jumlahKriteria - 1); i++) {
            matriksPerbandinganBerpasangans.add(new MatriksPerbandinganBerpasangan(kriterias.get(i).getNamaKriteria(), mHelper.getJmlmpb()[i]));
            priorityVektor[i] = mHelper.getPriorityVektor()[i];
            matriksNilaiKriterias.add(new MatriksNilaiKriteria(kriterias.get(i).getNamaKriteria(), mHelper.getJmlmnk()[i], priorityVektor[i]));
            setPerbandinganKriteria(kriterias.get(i).getKodeKriteria(), kriterias.get(i).getNamaKriteria(), df.format(priorityVektor[i]));

            if (i == (jumlahKriteria - 1)) completed = true;
        }

        timer = (1 + jumlahKriteria) * 1000;

        new Handler().postDelayed(() -> {
            if (completed) {
                args.putDouble(DialogResultPerbandingan.EV, eigenVektor);
                args.putDouble(DialogResultPerbandingan.CI, consIndex);
                args.putDouble(DialogResultPerbandingan.CR, consRatio);
                args.putString(DialogResultPerbandingan.TITLE, getString(R.string.menu_3));
                mDialogResultPerbandingan.setMatriksPerbandinganBerpasangans(matriksPerbandinganBerpasangans);
                mDialogResultPerbandingan.setMatriksNilaiKriterias(matriksNilaiKriterias);
                mDialogResultPerbandingan.setArguments(args);
                mDialogResultPerbandingan.show(getChildFragmentManager(), DialogResultPerbandingan.TAG);
                barUtils.hide();
            }
        }, timer);
    }

    @Override
    public void setPerbandinganKriteria(String kodeKriteria, String namaKriteria, String priorityVektor) {
        perbandingan.put(PerbandinganKriteria.FIELD_KODE_KRITERIA, kodeKriteria);
        perbandingan.put(PerbandinganKriteria.FIELD_NAMA_KRITERIA, namaKriteria);
        perbandingan.put(PerbandinganKriteria.FIELD_NILAI_KRITERIA, Double.parseDouble(priorityVektor));
        mFirestore.collection(PerbandinganKriteria.COLLECTION).document(kodeKriteria).set(perbandingan)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "setPerbandinganKriteria() called with: kodeKriteria = [" + kodeKriteria + "], priorityVektor = [" + priorityVektor + "]"))
                .addOnFailureListener(e -> Log.e(TAG, "setPerbandinganKriteria: ", e));
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
                throw new RuntimeException(context.toString() + "must implement Callbacks");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @OnClick(R.id.btnSubmitPerbandinganKriteria) void submit() {
        if (!onValidate()) return;

        barUtils.show();

        onProcessCounting();
    }

    public interface Callbacks {
        void onAttachPerbandinganKriteriaFragment();
        void onDetachPerbandinganKriteriaFragment();
    }
}
