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
import pd.sahang.mas.palembang.smds.ahp.models.SubKriteria;

public class SubKriteriaAdapter extends FirestoreAdapter<SubKriteriaAdapter.ViewHolder> {

    private final OnSubKriteriaListener mListener;

    public SubKriteriaAdapter(Query query, OnSubKriteriaListener mListener) {
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

    public void editSubKriteria(int position) {
        if (mListener != null) {
            mListener.onEditSubKriteria(getSnapshot(position));
        }
    }

    public void deleteSubKriteria(int position) {
        if (mListener != null) {
            mListener.onDeleteSubKriteria(getSnapshot(position));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNameKriteria) TextView tvNamaSubKriteria;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(DocumentSnapshot snapshot, OnSubKriteriaListener mListener) {
            SubKriteria subKriteria = snapshot.toObject(SubKriteria.class);
            tvNamaSubKriteria.setText(subKriteria.getSubNamaKriteria());
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onDetailSubKriteria(snapshot);
                }
            });
        }
    }

    public interface OnSubKriteriaListener {
        void onEditSubKriteria(DocumentSnapshot snapshot);
        void onDetailSubKriteria(DocumentSnapshot snapshot);
        void onDeleteSubKriteria(DocumentSnapshot snapshot);
    }
}
