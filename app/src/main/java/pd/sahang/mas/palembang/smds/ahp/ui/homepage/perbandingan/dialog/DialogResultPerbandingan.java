package pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.adapters.MNKAdapter;
import pd.sahang.mas.palembang.smds.ahp.adapters.MPBAdapter;
import pd.sahang.mas.palembang.smds.ahp.models.MatriksNilaiKriteria;
import pd.sahang.mas.palembang.smds.ahp.models.MatriksPerbandinganBerpasangan;
import pd.sahang.mas.palembang.smds.ahp.ui.homepage.perbandingan.inisiasi.DialogResultPerbandinganInterface;

public class DialogResultPerbandingan extends BottomSheetDialogFragment implements DialogResultPerbandinganInterface {

    public static final String TAG = DialogDetailHasilPerbandingan.class.getSimpleName();

    public static final String EV = "ev";
    public static final String CI = "ci";
    public static final String CR = "cr";
    public static final String TITLE = "title";

    private View mDialogResult;

    @BindView(R.id.titleToolbar) TextView tvTitle;
    @BindView(R.id.tvEigenVektorAll) TextView tvEigenVektor;
    @BindView(R.id.tvConsIndex) TextView tvConsIndex;
    @BindView(R.id.tvConsRatio) TextView tvConsRatio;
    @BindView(R.id.tvMsgConsRatio) TextView tvMsgRatio;
    @BindView(R.id.rvJumlahMpb) RecyclerView rvJumlahMpb;
    @BindView(R.id.rvJumlahMnk) RecyclerView rvJumlahMnk;
    @BindView(R.id.btnPerbandingan) Button btnPerbandingan;

    private int position;
    private boolean lastPosition, sub;
    private ArrayList<MatriksPerbandinganBerpasangan> matriksPerbandinganBerpasangans;
    private ArrayList<MatriksNilaiKriteria> matriksNilaiKriterias;
    private MPBAdapter mpbAdapter;
    private MNKAdapter mnkAdapter;

    private String title;
    private double ev, ci, cr;
    private DecimalFormat df;
    private KriteriaFragmentListener mKriteriaFragmentListener;
    private SubKriteriaFragmentListener mSubKriteriaFragmentListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BaseBottomSheetDialog);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ev = getArguments().getDouble(EV);
            ci = getArguments().getDouble(CI);
            cr = getArguments().getDouble(CR);
            title = getArguments().getString(TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDialogResult = inflater.inflate(R.layout.bottom_sheet_result_perbandingan, container, false);
        ButterKnife.bind(this, mDialogResult);
        df = new DecimalFormat("#.#####");

        tvTitle.setText(getString(R.string.toolbar_detail, title));

        if (isSub()) {
            if (mSubKriteriaFragmentListener != null) {
                mSubKriteriaFragmentListener.onAttachResultPerbandingan();
            }
        } else {
            if (mKriteriaFragmentListener != null) {
                mKriteriaFragmentListener.onAttachResultPerbandingan();
            }
        }

        onSetResult();

        onRecyclerView();

        return mDialogResult;
    }

    @Override
    public void onSetResult() {
        tvEigenVektor.setText(getString(R.string.hint_eigen_vektor_all, df.format(ev)));
        tvConsIndex.setText(getString(R.string.hint_ci, df.format(ci)));
        tvConsRatio.setText(getString(R.string.hint_cr, df.format(cr)));
        if (cr > 0.1) {
            tvMsgRatio.setVisibility(View.VISIBLE);
            btnPerbandingan.setText(getString(R.string.btn_hitung_ulang_perbandingan));
        } else {
            if (isLastPosition() && isSub()) {
                tvMsgRatio.setVisibility(View.GONE);
                btnPerbandingan.setText(getString(R.string.btn_result_perbandingan));
            } else {
                tvMsgRatio.setVisibility(View.GONE);
                btnPerbandingan.setText(getString(R.string.btn_lanjut_perbandingan));
            }
        }
    }

    @Override
    public void onRecyclerView() {
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setChangeDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvJumlahMpb.setLayoutManager(new LinearLayoutManager(mDialogResult.getContext()));
        rvJumlahMpb.setItemAnimator(itemAnimator);
        rvJumlahMpb.setHasFixedSize(true);

        rvJumlahMnk.setLayoutManager(new LinearLayoutManager(mDialogResult.getContext()));
        rvJumlahMnk.setItemAnimator(itemAnimator);
        rvJumlahMnk.setHasFixedSize(true);

        onSetupAdapter();
    }

    @Override
    public void onSetupAdapter() {
        mpbAdapter = new MPBAdapter(mDialogResult.getContext(), getMatriksPerbandinganBerpasangans());
        mnkAdapter = new MNKAdapter(mDialogResult.getContext(), getMatriksNilaiKriterias());

        rvJumlahMpb.setAdapter(mpbAdapter);
        rvJumlahMnk.setAdapter(mnkAdapter);
    }

    private ArrayList<MatriksPerbandinganBerpasangan> getMatriksPerbandinganBerpasangans() {
        return matriksPerbandinganBerpasangans;
    }

    public void setMatriksPerbandinganBerpasangans(ArrayList<MatriksPerbandinganBerpasangan> matriksPerbandinganBerpasangans) {
        this.matriksPerbandinganBerpasangans = matriksPerbandinganBerpasangans;
    }

    private ArrayList<MatriksNilaiKriteria> getMatriksNilaiKriterias() {
        return matriksNilaiKriterias;
    }

    public void setMatriksNilaiKriterias(ArrayList<MatriksNilaiKriteria> matriksNilaiKriterias) {
        this.matriksNilaiKriterias = matriksNilaiKriterias;
    }

    private int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private boolean isSub() {
        return sub;
    }

    public void setSub(boolean sub) {
        this.sub = sub;
    }

    private boolean isLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(boolean lastPosition) {
        this.lastPosition = lastPosition;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (isSub()) {
            if (context instanceof SubKriteriaFragmentListener) {
                mSubKriteriaFragmentListener = (SubKriteriaFragmentListener) context;
            } else {
                if (getParentFragment() != null && getParentFragment() instanceof SubKriteriaFragmentListener) {
                    mSubKriteriaFragmentListener = (SubKriteriaFragmentListener) getParentFragment();
                } else {
                    throw new RuntimeException(context.toString() + " must implement SubKriteriaFragmentListener");
                }
            }
        } else {
            if (context instanceof KriteriaFragmentListener) {
                mKriteriaFragmentListener = (KriteriaFragmentListener) context;
            } else {
                if (getParentFragment() != null && getParentFragment() instanceof KriteriaFragmentListener) {
                    mKriteriaFragmentListener = (KriteriaFragmentListener) getParentFragment();
                } else {
                    throw new RuntimeException(context.toString() + " must implement KriteriaFragmentListener");
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (isSub()) {
            mSubKriteriaFragmentListener = null;
        } else {
            mKriteriaFragmentListener = null;
        }
    }

    @OnClick(R.id.close_bottom) void exit() {
        dismiss();
    }

    @OnClick(R.id.btnPerbandingan) void result() {
        if (cr > 0.1) {
            dismiss();
        } else {
            if (isSub()) {
                if (mSubKriteriaFragmentListener != null) {
                    dismiss();
                    if (isLastPosition()) {
                        mSubKriteriaFragmentListener.onSubKriteriaLast();
                    } else {
                        mSubKriteriaFragmentListener.onSubKriteriaLanjut(getPosition());
                    }
                }
            } else {
                if (mKriteriaFragmentListener != null) {
                    dismiss();
                    mKriteriaFragmentListener.onSubKriteriaFragment();
                }
            }
        }
    }

    public interface KriteriaFragmentListener {
        void onAttachResultPerbandingan();
        void onDetachResultPerbandingan();
        void onSubKriteriaFragment();
    }

    public interface SubKriteriaFragmentListener {
        void onAttachResultPerbandingan();
        void onDetachResultPerbandingan();
        void onSubKriteriaLanjut(int position);
        void onSubKriteriaLast();
    }
}
