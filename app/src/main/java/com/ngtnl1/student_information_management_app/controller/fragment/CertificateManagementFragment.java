package com.ngtnl1.student_information_management_app.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.ngtnl1.student_information_management_app.R;
import com.ngtnl1.student_information_management_app.controller.adapter.CertificateManagementAdapter;
import com.ngtnl1.student_information_management_app.controller.adapter.StudentManagementAdapter;
import com.ngtnl1.student_information_management_app.model.Certificate;
import com.ngtnl1.student_information_management_app.model.Student;
import com.ngtnl1.student_information_management_app.model.User;
import com.ngtnl1.student_information_management_app.service.CertificateService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CertificateManagementFragment extends Fragment {
    @Inject
    CertificateService certificateService;
    private List<Certificate> items;
    private CertificateManagementAdapter adapter;
    private RecyclerView recyclerView;
    private Button buttonMainCertificateManagementAdd;

    public CertificateManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_certificate_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        setupRecyclerView();
        setOnClickListener();
    }

    private void initViews() {
        recyclerView = requireView().findViewById(R.id.recyclerViewMainCertificatemanagement);
        buttonMainCertificateManagementAdd = getView().findViewById(R.id.buttonMainCertificateManagementAdd);
    }

    private void setupRecyclerView() {
        items = new ArrayList<>();
        adapter = new CertificateManagementAdapter(items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        registerForContextMenu(recyclerView);

        adapter.setOnCertificateItemClickListener(new CertificateManagementAdapter.OnStudentItemClickListener() {
            @Override
            public void onButtonEditClick(int position, Certificate certificate) {
                showEditCertificateDialog(certificate);
            }

            @Override
            public void onButtonDeleteClick(int position, Certificate certificate) {
                showDeleteCertificateDialog(certificate);
            }
        });

        updateData();
    }

    private void updateData() {
        certificateService.findAllCertificate().addOnCompleteListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isSuccessful()) {
                items.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getResult()) {
                    Certificate certificate = documentSnapshot.toObject(Certificate.class);

                    assert certificate != null;
                    certificate.setId(documentSnapshot.getId());

                    items.add(certificate);
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setOnClickListener() {
        buttonMainCertificateManagementAdd.setOnClickListener(v -> {
            showCreateCertificateDialog();
        });
    }

    private void showCreateCertificateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thêm chứng chỉ");

        View view = getLayoutInflater().inflate(R.layout.dialog_create_certificate, null);

        EditText editTextDialogCreateCertificateName = view.findViewById(R.id.editTextDialogCreateCertificateName);

        builder.setView(view);

        builder.setPositiveButton("Thêm chứng chỉ", (dialog, which) -> {
            String name = editTextDialogCreateCertificateName.getText().toString();

            Certificate certificate = new Certificate(name);

            certificateService.createCertificate(certificate).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Thêm chứng chỉ thành công", Toast.LENGTH_SHORT).show();
                    updateData();
                } else {
                    Toast.makeText(requireContext(), "Thêm chứng chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });
        builder.show();
    }

    private void showEditCertificateDialog(Certificate certificate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Sửa chứng chỉ");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_certificate, null);

        EditText editTextDialogEditCertificateName = view.findViewById(R.id.editTextDialogEditCertificateName);

        editTextDialogEditCertificateName.setText(certificate.getName());

        builder.setView(view);

        builder.setPositiveButton("Sửa chứng chỉ", (dialog, which) -> {
            String name = editTextDialogEditCertificateName.getText().toString();

            certificate.setName(name);

            certificateService.updateCertificate(certificate.getId(), certificate).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Sửa chứng chỉ thành công", Toast.LENGTH_SHORT).show();
                    updateData();
                } else {
                    Toast.makeText(requireContext(), "Sửa chứng chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        builder.show();
    }

    private void showDeleteCertificateDialog(Certificate certificate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xóa chứng chỉ");
        builder.setMessage("Bạn có chắc chắn muốn xóa chứng chỉ này?");

        builder.setPositiveButton("Xóa chứng chỉ", (dialog, which) -> {
            certificateService.deleteCertificate(certificate.getId()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Xóa chứng chỉ thành công", Toast.LENGTH_SHORT).show();
                    updateData();
                } else {
                    Toast.makeText(requireContext(), "Xóa chứng chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
