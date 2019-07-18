package pd.sahang.mas.palembang.smds.ahp.adapters;

import android.content.res.Resources;
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
import pd.sahang.mas.palembang.smds.ahp.models.Users;

public class UserAdapter extends FirestoreAdapter<UserAdapter.UserViewHolder> {

    private final OnUserSelectedListener mListener;

    public UserAdapter(Query query, OnUserSelectedListener mListener) {
        super(query);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new UserViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_user, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        userViewHolder.bind(getSnapshot(i), mListener);
    }

    public void editUser(int position) {
        if (mListener != null) {
            mListener.onUserSelectedEdit(getSnapshot(position));
        }
    }

    public void deleteUser(int position) {
        if (mListener != null) {
            mListener.onUserSelectedDelete(getSnapshot(position));
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNameUser) TextView tvNamaUser;
        @BindView(R.id.tvLevelUser) TextView tvLevelUser;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final DocumentSnapshot snapshot, final OnUserSelectedListener mListener) {
            Users users = snapshot.toObject(Users.class);
            Resources resources = itemView.getResources();
            tvNamaUser.setText(resources.getString(R.string.list_nama_user, users.getFullname()));
            tvLevelUser.setText(resources.getString(R.string.list_level_user, users.getLevel().toUpperCase()));
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onUserSelectedDetail(snapshot);
                }
            });
        }
    }

    public interface OnUserSelectedListener {
        void onUserSelectedDetail(DocumentSnapshot snapshot);
        void onUserSelectedEdit(DocumentSnapshot snapshot);
        void onUserSelectedDelete(DocumentSnapshot snapshot);
    }
}
