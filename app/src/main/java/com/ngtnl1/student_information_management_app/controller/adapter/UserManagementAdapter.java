package com.ngtnl1.student_information_management_app.controller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.StorageReference;
import com.ngtnl1.student_information_management_app.R;
import com.ngtnl1.student_information_management_app.model.User;

import java.util.List;

import lombok.Setter;

@Setter
public class UserManagementAdapter extends RecyclerView.Adapter<UserManagementAdapter.ViewHolder>{
    public interface OnUserItemClickListener {
        void onItemLongClick(int position, User user);
        void onItemClick(int position, User user);
    }

    StorageReference storageReference;
    private List<User> items;
    private OnUserItemClickListener onUserItemClickListener;

    public void setOnUserItemClickListener(OnUserItemClickListener listener) {
        this.onUserItemClickListener = listener;
    }

    public UserManagementAdapter(List<User> items, StorageReference storageReference) {
        this.items = items;
        this.storageReference = storageReference;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMainUserManagementName;
        TextView textViewMainUserManagementAge;
        TextView textViewMainUserManagementEmail;
        TextView textViewMainUserManagementPhone;
        ShapeableImageView imageMainUserManagementAvatar;

        ViewHolder(View itemView) {
            super(itemView);

            textViewMainUserManagementName = itemView.findViewById(R.id.textViewMainUserManagementName);
            textViewMainUserManagementAge = itemView.findViewById(R.id.textViewMainUserManagementAge);
            textViewMainUserManagementEmail = itemView.findViewById(R.id.textViewMainUserManagementEmail);
            textViewMainUserManagementPhone = itemView.findViewById(R.id.textViewMainUserManagementPhone);
            imageMainUserManagementAvatar = itemView.findViewById(R.id.imageMainUserManagementAvatar);
        }

        void bind(User user) {
            textViewMainUserManagementName.setText(user.getName());
            textViewMainUserManagementAge.setText(user.getAge());
            textViewMainUserManagementEmail.setText(user.getEmail());
            textViewMainUserManagementPhone.setText(user.getPhone());
            setProfileImage(user.getEmail());

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onUserItemClickListener != null) {
                    onUserItemClickListener.onItemLongClick(position, items.get(position));
                    return true;
                }
                return false;
            });

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onUserItemClickListener != null) {
                    onUserItemClickListener.onItemClick(position, items.get(position));
                }
            });
        }

        private void setProfileImage(String id) {
            storageReference.child("images/" +id + ".jpg").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(itemView.getContext()).load(uri).into(imageMainUserManagementAvatar)).addOnFailureListener(exception -> Glide.with(itemView.getContext()).load(R.drawable.img_sample_avatar).into(imageMainUserManagementAvatar));
        }
    }
}
