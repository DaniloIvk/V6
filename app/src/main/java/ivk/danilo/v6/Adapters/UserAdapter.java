package ivk.danilo.v6.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ivk.danilo.v6.Models.User;
import ivk.danilo.v6.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> users;

    public UserAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<User> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView userName;
        private final TextView userAge;
        private final TextView userDepartment;
        private final TextView userGpa;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.student_name);
            userAge = itemView.findViewById(R.id.student_age);
            userDepartment = itemView.findViewById(R.id.student_department);
            userGpa = itemView.findViewById(R.id.student_gpa);
        }

        public void bind(User user) {
            userName.setText(user.name);
            userAge.setText(String.valueOf(user.age));
            userDepartment.setText(user.department);
            userGpa.setText(String.valueOf(user.gpa));
        }
    }
}

