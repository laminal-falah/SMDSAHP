package pd.sahang.mas.palembang.smds.ahp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.HitungPerbandinganModel;

public class HitungPerbandinganAdapter extends RecyclerView.Adapter<HitungPerbandinganAdapter.ViewHolder> {

    private final ArrayList<HitungPerbandinganModel> models;

    public HitungPerbandinganAdapter(ArrayList<HitungPerbandinganModel> models) {
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_hitung_perbandingan, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size() > 0 ? models.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.kodeA) TextView kodeA;
        @BindView(R.id.kodeB) TextView kodeB;
        @BindView(R.id.pilihanA) RadioButton rbPilihanA;
        @BindView(R.id.pilihanB) RadioButton rbPilihanB;
        @BindView(R.id.kepentingan) AppCompatSpinner spKepentingan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(HitungPerbandinganModel model) {
            rbPilihanA.setChecked(true);
            kodeA.setText(model.getKodeA());
            kodeB.setText(model.getKodeB());
            rbPilihanA.setText(model.getNamaPilihanA());
            rbPilihanB.setText(model.getNamaPilihanB());
            ArrayAdapter<String> kepentingan = new ArrayAdapter<String>(itemView.getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    itemView.getResources().getStringArray(R.array.kepentingan)) {

                @Override
                public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view =  super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if (position == 0) {
                        tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

                @Override
                public boolean isEnabled(int position) {
                    return position != 0;
                }
            };
            spKepentingan.setAdapter(kepentingan);
        }
    }
}
