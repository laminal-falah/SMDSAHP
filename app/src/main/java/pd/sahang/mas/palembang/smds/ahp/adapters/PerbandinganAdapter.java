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
import pd.sahang.mas.palembang.smds.ahp.models.PerbandinganKriteria;

public class PerbandinganAdapter extends FirestoreAdapter<PerbandinganAdapter.ViewHolder> {

    private final OnPerbandinganListener mListener;

    public PerbandinganAdapter(Query query, OnPerbandinganListener mListener) {
        super(query);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kriteria, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNameKriteria) TextView tvNamaKritria;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(DocumentSnapshot snapshot, OnPerbandinganListener mListener) {
            PerbandinganKriteria kriteria = snapshot.toObject(PerbandinganKriteria.class);
            tvNamaKritria.setText(kriteria.getNamaKriteria());
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onDetailPerbandingan(snapshot);
                }
            });
        }
    }

    public interface OnPerbandinganListener {
        void onDetailPerbandingan(DocumentSnapshot snapshot);
    }
}
