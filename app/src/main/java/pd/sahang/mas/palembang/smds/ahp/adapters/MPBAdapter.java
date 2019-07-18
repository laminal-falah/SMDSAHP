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
import pd.sahang.mas.palembang.smds.ahp.models.MatriksPerbandinganBerpasangan;

public class MPBAdapter extends RecyclerView.Adapter<MPBAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<MatriksPerbandinganBerpasangan> matriksPerbandinganBerpasangans;

    public MPBAdapter(Context mContext, ArrayList<MatriksPerbandinganBerpasangan> matriksPerbandinganBerpasangans) {
        this.mContext = mContext;
        this.matriksPerbandinganBerpasangans = matriksPerbandinganBerpasangans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_jumlah_mpb, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(matriksPerbandinganBerpasangans.get(position));
    }

    @Override
    public int getItemCount() {
        return matriksPerbandinganBerpasangans.size() > 0 ? matriksPerbandinganBerpasangans.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvItemJumlahMpb) TextView tvItem;
        final DecimalFormat df = new DecimalFormat("#.#####");

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(MatriksPerbandinganBerpasangan berpasangan) {
            tvItem.setText(itemView.getResources().getString(R.string.hint_jumlah_mpb,
                    berpasangan.getNamaKriteria(),
                    df.format(berpasangan.getJumlah())
            ));
        }
    }
}
