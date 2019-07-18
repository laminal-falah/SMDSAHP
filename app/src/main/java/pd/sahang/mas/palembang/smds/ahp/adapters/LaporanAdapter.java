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
import pd.sahang.mas.palembang.smds.ahp.models.Laporan;

public class LaporanAdapter extends FirestoreAdapter<LaporanAdapter.ViewHolder> {

    private final OnLaporanSelectedListener mListener;

    public LaporanAdapter(Query query, OnLaporanSelectedListener mListener) {
        super(query);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_laporan, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNameLaporan) TextView tvNameLaporan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(DocumentSnapshot snapshot, OnLaporanSelectedListener mListener) {
            Laporan laporan = snapshot.toObject(Laporan.class);
            tvNameLaporan.setText(laporan.getNamaLaporan());
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onLaporanSelectedDetail(snapshot);
                }
            });
        }
    }

    public interface OnLaporanSelectedListener {
        void onLaporanSelectedDetail(DocumentSnapshot snapshot);
    }
}
