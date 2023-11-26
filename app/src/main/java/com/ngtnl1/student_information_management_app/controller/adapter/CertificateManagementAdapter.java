package com.ngtnl1.student_information_management_app.controller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ngtnl1.student_information_management_app.R;
import com.ngtnl1.student_information_management_app.model.Certificate;

import java.util.List;

import lombok.Setter;

@Setter
public class CertificateManagementAdapter extends RecyclerView.Adapter<CertificateManagementAdapter.ViewHolder> {
    public interface OnStudentItemClickListener {
        void onButtonEditClick(int position, Certificate certificate);
        void onButtonDeleteClick(int position, Certificate certificate);
    }

    private OnStudentItemClickListener onCertificateItemClickListener;

    public void setOnCertificateItemClickListener(OnStudentItemClickListener listener) {
        this.onCertificateItemClickListener = listener;
    }

    private List<Certificate> items;

    public CertificateManagementAdapter(List<Certificate> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_certificate, parent, false);
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
        private TextView textViewItemCertificateName;
        private Button buttonItemCertificateEdit;
        private Button buttonItemCertificateDelete;

        ViewHolder(View itemView) {
            super(itemView);

            textViewItemCertificateName = itemView.findViewById(R.id.textViewItemCertificateName);
            buttonItemCertificateEdit = itemView.findViewById(R.id.buttonItemCertificateEdit);
            buttonItemCertificateDelete = itemView.findViewById(R.id.buttonItemCertificateDelete);
        }

        void bind(Certificate certificate) {
            textViewItemCertificateName.setText(certificate.getName());

            buttonItemCertificateEdit.setOnClickListener(v -> {
                if (onCertificateItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onCertificateItemClickListener.onButtonEditClick(position, items.get(position));
                    }
                }
            });

            buttonItemCertificateDelete.setOnClickListener(v -> {
                if (onCertificateItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onCertificateItemClickListener.onButtonDeleteClick(position, items.get(position));
                    }
                }
            });
        }
    }
}
