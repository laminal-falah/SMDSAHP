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
import pd.sahang.mas.palembang.smds.ahp.models.DetailLaporanItem;

public class DetailLaporanBottomAdapter extends FirestoreAdapter<DetailLaporanBottomAdapter.ViewHolder> {

    public DetailLaporanBottomAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_detail_laporan_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTitleKriteriaDetail) TextView tvKriteria;
        @BindView(R.id.tvSubTitleKriteriaDetail) TextView tvSubKriteria;
        @BindView(R.id.tvNilaiSubKriteriaDetail) TextView tvNilaiSubKriteria;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(DocumentSnapshot snapshot) {
            tvKriteria.setText(snapshot.getString(DetailLaporanItem.FIELD_NAMA_KRITERIA));
            tvSubKriteria.setText(snapshot.getString(DetailLaporanItem.FIELD_NAMA_SUB_KRITERIA));
            tvNilaiSubKriteria.setText(String.valueOf(snapshot.getDouble(DetailLaporanItem.FIELD_NILAI_SUB_KRITERIA)));
        }
    }
}
