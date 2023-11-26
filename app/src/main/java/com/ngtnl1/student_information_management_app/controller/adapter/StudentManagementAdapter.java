package com.ngtnl1.student_information_management_app.controller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ngtnl1.student_information_management_app.R;
import com.ngtnl1.student_information_management_app.model.Student;

import java.util.List;

import lombok.Setter;

@Setter
public class StudentManagementAdapter extends RecyclerView.Adapter<StudentManagementAdapter.ViewHolder> {
    public interface OnStudentItemClickListener {
        void onButtonDetailClick(int position, Student student);
        void onButtonDeleteClick(int position, Student student);
    }

    private OnStudentItemClickListener onStudentItemClickListener;

    public void setOnStudentItemClickListener(OnStudentItemClickListener listener) {
        this.onStudentItemClickListener = listener;
    }

    private List<Student> items;

    public StudentManagementAdapter(List<Student> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_student, parent, false);
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
        TextView textViewMainStudentManagementName;
        TextView textViewMainStudentManagementId;
        TextView textViewMainStudentManagementMajor;
        TextView textViewMainStudentManagementEmail;
        TextView textViewMainStudentManagementPhone;
        Button buttonMainStudentManagementDetail;
        Button buttonMainStudentManagementDelete;

        ViewHolder(View itemView) {
            super(itemView);

            textViewMainStudentManagementName = itemView.findViewById(R.id.textViewMainStudentManagementName);
            textViewMainStudentManagementId = itemView.findViewById(R.id.textViewMainStudentManagementId);
            textViewMainStudentManagementMajor = itemView.findViewById(R.id.textViewMainStudentManagementMajor);
            textViewMainStudentManagementEmail = itemView.findViewById(R.id.textViewMainStudentManagementEmail);
            textViewMainStudentManagementPhone = itemView.findViewById(R.id.textViewMainStudentManagementPhone);
            buttonMainStudentManagementDetail = itemView.findViewById(R.id.buttonMainStudentManagementDetail);
            buttonMainStudentManagementDelete = itemView.findViewById(R.id.buttonMainStudentManagementDelete);
        }

        void bind(Student student) {
            textViewMainStudentManagementName.setText(student.getName());
            textViewMainStudentManagementId.setText(convertDocumentId(student.getId()));
            textViewMainStudentManagementMajor.setText(student.getMajor());
            textViewMainStudentManagementEmail.setText(student.getEmail());
            textViewMainStudentManagementPhone.setText(student.getPhone());

            buttonMainStudentManagementDetail.setOnClickListener(v -> {
                if (onStudentItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onStudentItemClickListener.onButtonDetailClick(position, items.get(position));
                    }
                }
            });

            buttonMainStudentManagementDelete.setOnClickListener(v -> {
                if (onStudentItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onStudentItemClickListener.onButtonDeleteClick(position, items.get(position));
                    }
                }
            });
        }

        private String convertDocumentId(String originalId) {
            String lastFourDigits = originalId.substring(originalId.length() - 4);

            String newId = "S" + lastFourDigits;

            return newId.toUpperCase();
        }
    }
}
