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
import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;

public class KriteriaAdapter extends FirestoreAdapter<KriteriaAdapter.KriteriaViewHolder> {

    private final OnKriteriaSelectedListener mListener;

    public KriteriaAdapter(Query query, OnKriteriaSelectedListener mListener) {
        super(query);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public KriteriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new KriteriaViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kriteria, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull KriteriaViewHolder kriteriaViewHolder, int i) {
        kriteriaViewHolder.bind(getSnapshot(i), mListener);
    }

    public void editKriteria(int position) {
        if (mListener != null) {
            mListener.onKriteriaSelectedEdit(getSnapshot(position));
        }
    }

    public void deleteKriteria(int position) {
        if (mListener != null) {
            mListener.onKriteriaSelectedDelete(getSnapshot(position));
        }
    }

    static class KriteriaViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNameKriteria) TextView tvNamaKriteria;

        KriteriaViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(DocumentSnapshot snapshot, OnKriteriaSelectedListener mListener) {
            Kriteria kriteria = snapshot.toObject(Kriteria.class);
            tvNamaKriteria.setText(kriteria.getNamaKriteria());
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onKriteriaSelectedDetail(snapshot);
                }
            });
        }
    }

    public interface OnKriteriaSelectedListener {
        void onKriteriaSelectedDetail(DocumentSnapshot snapshot);
        void onKriteriaSelectedEdit(DocumentSnapshot snapshot);
        void onKriteriaSelectedDelete(DocumentSnapshot snapshot);
    }
}
