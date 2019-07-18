package pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pd.sahang.mas.palembang.smds.ahp.R;

public class DialogDetailSubKriteria extends BottomSheetDialogFragment {

    public static final String TAG = DialogDetailSubKriteria.class.getSimpleName();

    public static final String KODE = "kode";
    public static final String NAMA = "nama";
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String TIPE = "tipe";

    private View mDialogDetail;

    @BindView(R.id.titleToolbar) TextView tvTitle;
    @BindView(R.id.tvIsiSubKode) TextView tvSubKode;
    @BindView(R.id.tvIsiSubNama) TextView tvSubNama;
    @BindView(R.id.tvIsiNilaiMinDetail) TextView tvNilaiMin;
    @BindView(R.id.tvIsiNilaiMaxDetail) TextView tvNilaiMax;

    private String kode, nama;
    private double min, max, tipe;

    private Callbacks mCallbacks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BaseBottomSheetDialog);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            kode = getArguments().getString(KODE);
            nama = getArguments().getString(NAMA);
            tipe = getArguments().getDouble(TIPE);
            min = getArguments().getDouble(MIN, 0);
            max = getArguments().getDouble(MAX, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDialogDetail = inflater.inflate(R.layout.bottom_sheet_detail_sub_kriteria, container, false);
        ButterKnife.bind(this, mDialogDetail);
        tvTitle.setText(getString(R.string.toolbar_detail, getString(R.string.menu_2)));
        if (mCallbacks != null) {
            mCallbacks.onAttachBottomSheetSubKriteria();
            tvSubKode.setText(kode);
            tvSubNama.setText(nama);
            if ((int) tipe == 1) {
                tvNilaiMin.setText(String.valueOf((int) min));
                tvNilaiMax.setText(String.valueOf((int) max));
            } else {
                tvNilaiMin.setText(String.valueOf(min));
                tvNilaiMax.setText(String.valueOf(max));
            }
        }

        return mDialogDetail;
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

    @OnClick(R.id.close_bottom) void close() {
        dismiss();
    }

    public interface Callbacks {
        void onAttachBottomSheetSubKriteria();
    }
}
