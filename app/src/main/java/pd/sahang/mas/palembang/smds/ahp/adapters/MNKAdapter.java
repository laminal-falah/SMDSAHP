package pd.sahang.mas.palembang.smds.ahp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.MatriksNilaiKriteria;

public class MNKAdapter extends RecyclerView.Adapter<MNKAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<MatriksNilaiKriteria> matriksNilaiKriterias;

    public MNKAdapter(Context mContext, ArrayList<MatriksNilaiKriteria> matriksNilaiKriterias) {
        this.mContext = mContext;
        this.matriksNilaiKriterias = matriksNilaiKriterias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_jumlah_mnk, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(matriksNilaiKriterias.get(position));
    }

    @Override
    public int getItemCount() {
        return matriksNilaiKriterias.size() > 0 ? matriksNilaiKriterias.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvItemJumlahMnk0) TextView tvItem0;
        @BindView(R.id.tvItemJumlahMnk1) TextView tvItem1;
        final DecimalFormat df = new DecimalFormat("#.#####");

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(MatriksNilaiKriteria nilaiKriteria) {
            tvItem0.setText(itemView.getResources().getString(R.string.hint_jumlah_mnk_0,
                    nilaiKriteria.getNamaKriteria(),
                    df.format(nilaiKriteria.getJumlah())
            ));
            tvItem1.setText(itemView.getResources().getString(R.string.hint_jumlah_mnk_1,
                    df.format(nilaiKriteria.getPv())
            ));
        }
    }
}
