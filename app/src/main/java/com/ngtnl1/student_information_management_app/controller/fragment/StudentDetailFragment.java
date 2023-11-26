package com.ngtnl1.student_information_management_app.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ngtnl1.student_information_management_app.R;
import com.ngtnl1.student_information_management_app.model.Certificate;
import com.ngtnl1.student_information_management_app.model.Student;
import com.ngtnl1.student_information_management_app.service.StudentService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StudentDetailFragment extends Fragment {
    @Inject
    StudentService studentService;
    private List<Certificate> items;
//    private StudentDetailAdapter adapter;
    private Student student;
    private RecyclerView recyclerView;
    private TextView textViewMainStudentDetailId;
    private TextView textViewMainStudentDetailName;
    private TextView textViewMainStudentDetailMajor;
    private TextView textViewMainStudentDetailSex;
    private TextView textViewMainStudentDetailAge;
    private TextView textViewMainStudentDetailEmail;
    private TextView textViewMainStudentDetailPhone;
    private Button buttonMainStudentDetailEdit;
    public StudentDetailFragment(Student student) {
        this.student = student;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_student_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        setData();
        setupRecyclerView();
        setOnClickListener();
    }

    private void initViews() {
        recyclerView = getView().findViewById(R.id.recyclerViewMainStudentDetail);
        textViewMainStudentDetailId = getView().findViewById(R.id.textViewMainStudentDetailId);
        textViewMainStudentDetailName = getView().findViewById(R.id.textViewMainStudentDetailName);
        textViewMainStudentDetailMajor = getView().findViewById(R.id.textViewMainStudentDetailMajor);
        textViewMainStudentDetailSex = getView().findViewById(R.id.textViewMainStudentDetailSex);
        textViewMainStudentDetailAge = getView().findViewById(R.id.textViewMainStudentDetailAge);
        textViewMainStudentDetailEmail = getView().findViewById(R.id.textViewMainStudentDetailEmail);
        textViewMainStudentDetailPhone = getView().findViewById(R.id.textViewMainStudentDetailPhone);
        buttonMainStudentDetailEdit = getView().findViewById(R.id.buttonMainStudentDetailEdit);
    }

    private void setData() {
        textViewMainStudentDetailId.setText(convertDocumentId(student.getId()));
        textViewMainStudentDetailName.setText(student.getName());
        textViewMainStudentDetailMajor.setText(student.getMajor());

        if (student.isFemale()) {
            textViewMainStudentDetailSex.setText("Nữ");
        } else {
            textViewMainStudentDetailSex.setText("Nam");
        }

        textViewMainStudentDetailAge.setText(student.getAge());
        textViewMainStudentDetailEmail.setText(student.getEmail());
        textViewMainStudentDetailPhone.setText(student.getPhone());
    }

    private String convertDocumentId(String originalId) {
        String lastFourDigits = originalId.substring(originalId.length() - 4);

        String newId = "S" + lastFourDigits;

        return newId.toUpperCase();
    }

    private void setupRecyclerView() {
    }

    private void setOnClickListener() {
        buttonMainStudentDetailEdit.setOnClickListener(v -> {
            showEditStudentDialog();
        });
    }

    private void showEditStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Sửa sinh viên");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_student, null);

        EditText editTextDialogEditStudentName = view.findViewById(R.id.editTextDialogEditStudentName);
        Spinner spinnerDialogEditStudentMajor = view.findViewById(R.id.spinnerDialogEditStudentMajor);
        Spinner spinnerDialogEditStudentSex = view.findViewById(R.id.spinnerDialogEditStudentSex);
        EditText editTextDialogEditStudentAge = view.findViewById(R.id.editTextDialogEditStudentAge);
        EditText editTextDialogEditStudentEmail = view.findViewById(R.id.editTextDialogEditStudentEmail);
        EditText editTextDialogEditStudentPhone = view.findViewById(R.id.editTextDialogEditStudentPhone);

        editTextDialogEditStudentName.setText(student.getName());
        editTextDialogEditStudentAge.setText(student.getAge());
        editTextDialogEditStudentEmail.setText(student.getEmail());
        editTextDialogEditStudentPhone.setText(student.getPhone());

        List<String> data_major = new ArrayList<>();
        data_major.add("KỸ THUẬT PHẦN MỀM");
        data_major.add("MẠNG MÁY TÍNH");
        data_major.add("HỆ THỐNG THÔNG TIN");
        data_major.add("KHOA HỌC MÁY TÍNH");

        ArrayAdapter<String> spinnerAdapterMajor = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, data_major);
        spinnerAdapterMajor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDialogEditStudentMajor.setAdapter(spinnerAdapterMajor);
        int spinnerPositionMajor = spinnerAdapterMajor.getPosition(student.getMajor());
        spinnerPositionMajor = spinnerPositionMajor == -1 ? 0 : spinnerPositionMajor;
        spinnerDialogEditStudentMajor.setSelection(spinnerPositionMajor);

        List<String> data_sex = new ArrayList<>();
        data_sex.add("Nam");
        data_sex.add("Nữ");

        ArrayAdapter<String> spinnerAdapterSex = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, data_sex);
        spinnerAdapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDialogEditStudentSex.setAdapter(spinnerAdapterSex);
        int spinnerPositionSex = spinnerAdapterSex.getPosition(student.isFemale() ? "Nữ" : "Nam");
        spinnerPositionSex = spinnerPositionSex == -1 ? 0 : spinnerPositionSex;
        spinnerDialogEditStudentSex.setSelection(spinnerPositionSex);


        builder.setView(view);

        builder.setPositiveButton("Sửa sinh viên", (dialog, which) -> {
            String name = editTextDialogEditStudentName.getText().toString();
            String major = spinnerDialogEditStudentMajor.getSelectedItem().toString();
            String sex = spinnerDialogEditStudentSex.getSelectedItem().toString();
            String age = editTextDialogEditStudentAge.getText().toString();
            String email = editTextDialogEditStudentEmail.getText().toString();
            String phone = editTextDialogEditStudentPhone.getText().toString();

            student.setName(name);
            student.setMajor(major);
            student.setFemale(sex.equals("Nữ") ? true : false);
            student.setAge(age);
            student.setEmail(email);
            student.setPhone(phone);

            studentService.updateStudent(student).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Sửa sinh viên thành công", Toast.LENGTH_SHORT).show();
                    updateData();
                } else {
                    Toast.makeText(requireContext(), "Sửa sinh viên thất bại", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        builder.show();
    }

    private void updateData() {
        studentService.findStudentDataRaw(student.getId()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                student = task.getResult().toObject(Student.class);
                setData();
            } else {
                Toast.makeText(requireContext(), "Có lỗi xảy ra. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
