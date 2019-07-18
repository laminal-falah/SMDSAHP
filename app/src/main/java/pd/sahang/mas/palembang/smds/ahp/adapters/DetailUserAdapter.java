package pd.sahang.mas.palembang.smds.ahp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.sahang.mas.palembang.smds.ahp.R;
import pd.sahang.mas.palembang.smds.ahp.models.DetailUser;

public class DetailUserAdapter extends RecyclerView.Adapter<DetailUserAdapter.DetailUserViewHolder> {

    private final ArrayList<DetailUser> detailUsers;

    public DetailUserAdapter(ArrayList<DetailUser> detailUsers) {
        this.detailUsers = detailUsers;
    }

    @NonNull
    @Override
    public DetailUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_detail_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailUserViewHolder detailUserViewHolder, int i) {
        detailUserViewHolder.bind(detailUsers.get(i));
    }

    @Override
    public int getItemCount() {
        return detailUsers.size() > 0 ? detailUsers.size() : 0;
    }

    static class DetailUserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTitleDetailUser) TextView tvTitle;
        @BindView(R.id.tvIsiDetailUser) TextView tvContent;

        DetailUserViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(DetailUser detailUser) {
            tvTitle.setText(detailUser.getTitle());
            tvContent.setText(detailUser.getContent());
        }
    }
}
