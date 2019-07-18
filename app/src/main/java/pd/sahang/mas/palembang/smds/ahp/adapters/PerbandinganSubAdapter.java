package pd.sahang.mas.palembang.smds.ahp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganSubKriteria;

public class PerbandinganSubAdapter extends FirestoreAdapter<PerbandinganSubAdapter.ViewHolder> {

    private static final String TAG = PerbandinganSubAdapter.class.getSimpleName();

    public PerbandinganSubAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_perbandingan, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvIsiSubKodePerbandingan) TextView tvSubKode;
        @BindView(R.id.tvIsiSubNamaPerbandingan) TextView tvSubNama;
        @BindView(R.id.tvIsiNilaiMinPerbandingan) TextView tvSubNilaiMin;
        @BindView(R.id.tvIsiNilaiMaxPerbandingan) TextView tvSubNilaiMax;
        @BindView(R.id.tvIsiSubEigenVektor) TextView tvSubEigenVektor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(DocumentSnapshot snapshot) {
            PerbandinganSubKriteria perbandinganSubKriteria = snapshot.toObject(PerbandinganSubKriteria.class);
            tvSubKode.setText(perbandinganSubKriteria.getSubKodeKriteria());
            tvSubNama.setText(perbandinganSubKriteria.getSubNamaKriteria());
            if (perbandinganSubKriteria.getSubTipeNilaiKriteria() == 1) {
                tvSubNilaiMin.setText(String.valueOf((int) perbandinganSubKriteria.getSubNilaiMinKriteria()));
                tvSubNilaiMax.setText(String.valueOf((int) perbandinganSubKriteria.getSubNilaiMaxKriteria()));
            } else {
                tvSubNilaiMin.setText(String.valueOf(perbandinganSubKriteria.getSubNilaiMinKriteria()));
                tvSubNilaiMax.setText(String.valueOf(perbandinganSubKriteria.getSubNilaiMaxKriteria()));
            }
            tvSubEigenVektor.setText(String.valueOf(perbandinganSubKriteria.getSubNilaiEigenVektorKriteria()));
        }
    }

}
