package pd.sahang.mas.palembang.smds.ahp.ui.homepage.tentang;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.utils.ProgressBarUtils;

public class TentangFragment extends Fragment {

    private static final String TAG = TentangFragment.class.getSimpleName();

    private View mTentang;

    @BindView(R.id.tvAppName) TextView tvAppName;
    @BindView(R.id.tvVersion) TextView tvVersion;
    @BindView(R.id.tvContent) TextView tvContent;
    @BindView(R.id.shimmer_view_tentang) ShimmerFrameLayout mShimmerFrameLayout;

    private ProgressBarUtils barUtils;

    private Callbacks mCallbacks;

    public TentangFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTentang = inflater.inflate(R.layout.fragment_tentang, container, false);
        ButterKnife.bind(this, mTentang);

        barUtils = new ProgressBarUtils(mTentang.getContext());

        gone();

        if (mCallbacks != null) {
            new Handler().postDelayed(this::visible, 2000);

            mCallbacks.onAttachTentang();
        }

        return mTentang;
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
            throw new RuntimeException(context.toString() + " must implement Callbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void gone() {
        tvAppName.setVisibility(View.GONE);
        tvVersion.setVisibility(View.GONE);
        tvContent.setVisibility(View.GONE);

        tvAppName.setText(null);
        tvVersion.setText(null);
        tvContent.setText(null);
    }

    private void visible() {
        tvAppName.setVisibility(View.VISIBLE);
        tvVersion.setVisibility(View.VISIBLE);
        tvContent.setVisibility(View.VISIBLE);

        mShimmerFrameLayout.stopShimmer();
        mShimmerFrameLayout.setVisibility(View.GONE);

        tvAppName.setText(getString(R.string.app_name));
        tvVersion.setText(getString(R.string.app_version));
        tvContent.setText(getString(R.string.text_about));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tvContent.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }
    }

    public interface Callbacks {
        void onAttachTentang();
    }
}
