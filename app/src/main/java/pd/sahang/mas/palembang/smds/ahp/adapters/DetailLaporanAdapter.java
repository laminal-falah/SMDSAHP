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
import pd.sahang.mas.palembang.smds.ahp.models.DetailLaporan;

public class DetailLaporanAdapter extends FirestoreAdapter<DetailLaporanAdapter.ViewHolder> {

    private final OnDetailLaporanListener mListener;

    public DetailLaporanAdapter(Query query, OnDetailLaporanListener mListener) {
        super(query);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_detail_laporan, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNamaKarung) TextView tvNamaKarung;
        @BindView(R.id.tvNamaGrade) TextView tvGradeKarung;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(DocumentSnapshot snapshot, OnDetailLaporanListener mListener) {
            tvNamaKarung.setText(snapshot.getString(DetailLaporan.FIELD_NAMA_KARUNG));
            tvGradeKarung.setText(snapshot.getString(DetailLaporan.FIELD_GRADE_KARUNG));
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onSelectedDetailLaporan(snapshot);
                }
            });
        }
    }

    public interface OnDetailLaporanListener {
        void onSelectedDetailLaporan(DocumentSnapshot snapshot);
    }
}
