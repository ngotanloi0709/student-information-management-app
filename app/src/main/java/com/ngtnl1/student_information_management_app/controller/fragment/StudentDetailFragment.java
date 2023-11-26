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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.ngtnl1.student_information_management_app.R;
import com.ngtnl1.student_information_management_app.controller.adapter.StudentDetailAdapter;
import com.ngtnl1.student_information_management_app.controller.adapter.StudentManagementAdapter;
import com.ngtnl1.student_information_management_app.model.Certificate;
import com.ngtnl1.student_information_management_app.model.Student;
import com.ngtnl1.student_information_management_app.service.CertificateService;
import com.ngtnl1.student_information_management_app.service.StudentService;
import com.ngtnl1.student_information_management_app.service.UserService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StudentDetailFragment extends Fragment {
    @Inject
    StudentService studentService;
    @Inject
    CertificateService certificateService;
    @Inject
    UserService userService;
    private List<Certificate> items;
    private StudentDetailAdapter adapter;
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
    private Button buttonMainStudentDetailAddCertificate;

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
        setStudentData();
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
        buttonMainStudentDetailAddCertificate = getView().findViewById(R.id.buttonMainStudentDetailAddCertificate);
    }

    private void setStudentData() {
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
        items = new ArrayList<>();
        adapter = new StudentDetailAdapter(items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        registerForContextMenu(recyclerView);

        adapter.setOnStudentDetailItemClickListener(new StudentDetailAdapter.OnStudentDetailItemClickListener() {
            @Override
            public void onButtonDeleteClick(int position, Certificate certificate) {
                if (userService.current_role.equals("EMPLOYEE")) {
                    Toast.makeText(requireContext(), "Bạn không có xóa chứng chỉ của sinh viên", Toast.LENGTH_SHORT).show();
                    return;
                }

                showDeleteStudentDialog(certificate);
            }
        });

        updateStudentCertificateData();
    }

    private void setOnClickListener() {
        buttonMainStudentDetailEdit.setOnClickListener(v -> {
            if (userService.current_role.equals("EMPLOYEE")) {
                Toast.makeText(requireContext(), "Bạn không có quyền sửa sinh viên", Toast.LENGTH_SHORT).show();
                return;
            }

            showEditStudentDialog();
        });

        buttonMainStudentDetailAddCertificate.setOnClickListener(v -> {
            if (userService.current_role.equals("EMPLOYEE")) {
                Toast.makeText(requireContext(), "Bạn không có quyền thêm chứng chỉ cho sinh viên", Toast.LENGTH_SHORT).show();
                return;
            }

            showAddCertificateDialog();
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
                    updateStudentData();
                } else {
                    Toast.makeText(requireContext(), "Sửa sinh viên thất bại", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        builder.show();
    }

    private void showAddCertificateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thêm chứng chỉ");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_certificate, null);

        Spinner spinnerDialogAddCertificateName = view.findViewById(R.id.spinnerDialogAddCertificateName);

        List<Certificate> data = new ArrayList<>();

        certificateService.findAllCertificate().addOnCompleteListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isSuccessful()) {
                List<String> data_name = new ArrayList<>();

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getResult()) {
                    Certificate certificate = documentSnapshot.toObject(Certificate.class);

                    assert certificate != null;
                    certificate.setId(documentSnapshot.getId());

                    data.add(certificate);
                    data_name.add(certificate.getName());
                }

                ArrayAdapter<String> spinnerAdapterName = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, data_name);
                spinnerAdapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDialogAddCertificateName.setAdapter(spinnerAdapterName);
            }
        });

        builder.setView(view);

        builder.setPositiveButton("Thêm chứng chỉ", (dialog, which) -> {
            String name = spinnerDialogAddCertificateName.getSelectedItem().toString();

            for (Certificate certificate : data) {
                if (certificate.getName().equals(name)) {
                    if (student.getCertificates() == null) {
                        student.setCertificates(new ArrayList<>());
                    }

                    if (student.getCertificates().contains(certificate.getId())) {
                        Toast.makeText(requireContext(), "Sinh viên đã có chứng chỉ này", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    student.getCertificates().add(certificate.getId());
                    break;
                }
            }

            studentService.updateStudent(student).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Thêm chứng chỉ thành công", Toast.LENGTH_SHORT).show();
                    updateStudentData();
                    updateStudentCertificateData();
                } else {
                    Toast.makeText(requireContext(), "Thêm chứng chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        builder.show();
    }

    private void showDeleteStudentDialog(Certificate certificate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xóa chứng chỉ");

        builder.setPositiveButton("Xóa chứng chỉ", (dialog, which) -> {
            student.getCertificates().remove(certificate.getId());

            studentService.updateStudent(student).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Xóa chứng chỉ thành công", Toast.LENGTH_SHORT).show();
                    updateStudentData();
                    updateStudentCertificateData();
                } else {
                    Toast.makeText(requireContext(), "Xóa chứng chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        builder.show();
    }

    private void updateStudentData() {
        studentService.findStudentDataRaw(student.getId()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                student = task.getResult().toObject(Student.class);
                setStudentData();
            }
        });
    }

    private void updateStudentCertificateData() {
        certificateService.findAllCertificate().addOnCompleteListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isSuccessful()) {
                items.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getResult()) {
                    Certificate certificate = documentSnapshot.toObject(Certificate.class);

                    assert certificate != null;
                    certificate.setId(documentSnapshot.getId());

                    if (student.getCertificates() != null && student.getCertificates().contains(certificate.getId())) {
                        items.add(certificate);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });
    }
}
