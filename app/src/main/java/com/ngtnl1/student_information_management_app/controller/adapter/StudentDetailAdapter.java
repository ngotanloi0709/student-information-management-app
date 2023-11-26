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
import com.ngtnl1.student_information_management_app.model.Student;

import java.util.List;

public class StudentDetailAdapter extends RecyclerView.Adapter<StudentDetailAdapter.ViewHolder> {
    public interface OnStudentDetailItemClickListener {
        void onButtonDeleteClick(int position, Certificate certificate);
    }

    private OnStudentDetailItemClickListener onStudentDetailItemClickListener;

    public void setOnStudentDetailItemClickListener(OnStudentDetailItemClickListener listener) {
        this.onStudentDetailItemClickListener = listener;
    }

    private List<Certificate> items;

    public StudentDetailAdapter(List<Certificate> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_student_detail, parent, false);
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
        TextView textViewItemStudentDetailName;
        Button buttonItemStudentDetailDelete;

        ViewHolder(View itemView) {
            super(itemView);

            textViewItemStudentDetailName = itemView.findViewById(R.id.textViewItemStudentDetailName);
            buttonItemStudentDetailDelete = itemView.findViewById(R.id.buttonItemStudentDetailDelete);
        }

        void bind(Certificate certificate) {
            textViewItemStudentDetailName.setText(certificate.getName());

            buttonItemStudentDetailDelete.setOnClickListener(v -> {
                if (onStudentDetailItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onStudentDetailItemClickListener.onButtonDeleteClick(position, items.get(position));
                    }
                }
            });
        }
    }
}
