package pd.sahang.mas.palembang.smds.ahp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;

public class AddLaporanAdapter extends FirestoreAdapter<AddLaporanAdapter.ViewHolder> {

    public AddLaporanAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kriteria_laporan, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tl_nilai_karung) TextInputLayout tvNilaiKarung;
        @BindView(R.id.kodeKriteria) TextView tvKodeKriteria;
        @BindView(R.id.namaKriteria) TextView tvNamaKriteria;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(DocumentSnapshot snapshot) {
            tvKodeKriteria.setText(snapshot.getString(Kriteria.FIELD_KODE_KRITERIA));
            tvNamaKriteria.setText(snapshot.getString(Kriteria.FIELD_NAMA_KRITERIA));
            tvNilaiKarung.setHint(
                    itemView.getResources()
                            .getString(R.string.hint_nilai_karung,
                                    snapshot.getString(Kriteria.FIELD_NAMA_KRITERIA)
                            )
            );
        }
    }
}
