package com.ngtnl1.student_information_management_app.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.ngtnl1.student_information_management_app.R;
import com.ngtnl1.student_information_management_app.controller.adapter.StudentManagementAdapter;
import com.ngtnl1.student_information_management_app.model.Student;
import com.ngtnl1.student_information_management_app.service.StudentService;
import com.ngtnl1.student_information_management_app.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StudentManagementFragment extends Fragment {
    @Inject
    UserService userService;
    @Inject
    StudentService studentService;
    private List<Student> items;
    private StudentManagementAdapter adapter;
    private RecyclerView recyclerView;
    private Button buttonMainStudentManagementCreateStudent;
    public StudentManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_student_management, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        setupRecyclerView();
        setOnClickListener();
        initSearchWidgets(view);
        sortStudentManagement();
    }

    private void initViews() {
        recyclerView = requireView().findViewById(R.id.recyclerViewMainStudentManagement);
        buttonMainStudentManagementCreateStudent = requireView().findViewById(R.id.buttonMainStudentManagementCreateStudent);
    }

    private void setupRecyclerView() {
        items = new ArrayList<>();
        adapter = new StudentManagementAdapter(items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        registerForContextMenu(recyclerView);

        adapter.setOnStudentItemClickListener(new StudentManagementAdapter.OnStudentItemClickListener() {
            @Override
            public void onButtonDetailClick(int position, Student student) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new StudentDetailFragment(student)).addToBackStack(null).commit();
            }

            @Override
            public void onButtonDeleteClick(int position, Student student) {
                if (userService.current_role.equals("EMPLOYEE")) {
                    Toast.makeText(requireContext(), "Bạn không có quyền xóa sinh viên", Toast.LENGTH_SHORT).show();
                    return;
                }

                showDeleteStudentDialog(student);
            }
        });

        updateData();
    }

    private void showDeleteStudentDialog(Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xóa sinh viên");
        builder.setMessage("Bạn có chắc chắn muốn xóa sinh viên này?");

        builder.setPositiveButton("Xóa sinh viên", (dialog, which) -> {
            studentService.deleteStudent(student.getId()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Xóa sinh viên thành công", Toast.LENGTH_SHORT).show();
                    updateData();
                } else {
                    Toast.makeText(requireContext(), "Xóa sinh viên thất bại", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    private void updateData() {
        studentService.findAllStudent().addOnCompleteListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isSuccessful()) {
                items.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getResult()) {
                    Student student = documentSnapshot.toObject(Student.class);
                    student.setId(documentSnapshot.getId());

                    if (student != null) {
                        items.add(student);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setOnClickListener() {
        buttonMainStudentManagementCreateStudent.setOnClickListener(v -> {
            showCreateStudentDialog();
        });
    }

    private void showCreateStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thêm người dùng");

        View view = getLayoutInflater().inflate(R.layout.dialog_create_student, null);

        EditText editTextDialogCreateStudentName = view.findViewById(R.id.editTextDialogCreateStudentName);
        Spinner spinnerDialogCreateStudentMajor = view.findViewById(R.id.spinnerDialogCreateStudentMajor);
        Spinner spinnerDialogCreateStudentSex = view.findViewById(R.id.spinnerDialogCreateStudentSex);
        EditText editTextDialogCreateStudentAge = view.findViewById(R.id.editTextDialogCreateStudentAge);
        EditText editTextDialogCreateStudentEmail = view.findViewById(R.id.editTextDialogCreateStudentEmail);
        EditText editTextDialogCreateStudentPhone = view.findViewById(R.id.editTextDialogCreateStudentPhone);

        List<String> data_major = new ArrayList<>();
        data_major.add("KỸ THUẬT PHẦN MỀM");
        data_major.add("MẠNG MÁY TÍNH");
        data_major.add("HỆ THỐNG THÔNG TIN");
        data_major.add("KHOA HỌC MÁY TÍNH");

        ArrayAdapter<String> spinnerAdapterMajor = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, data_major);
        spinnerAdapterMajor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDialogCreateStudentMajor.setAdapter(spinnerAdapterMajor);

        List<String> data_sex = new ArrayList<>();
        data_sex.add("Nam");
        data_sex.add("Nữ");

        ArrayAdapter<String> spinnerAdapterSex = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, data_sex);
        spinnerAdapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDialogCreateStudentSex.setAdapter(spinnerAdapterSex);

        builder.setView(view);

        builder.setPositiveButton("Thêm sinh viên", (dialog, which) -> {
            String name = editTextDialogCreateStudentName.getText().toString();
            String major = spinnerDialogCreateStudentMajor.getSelectedItem().toString();
            String sex = spinnerDialogCreateStudentSex.getSelectedItem().toString();
            String age = editTextDialogCreateStudentAge.getText().toString();
            String email = editTextDialogCreateStudentEmail.getText().toString();
            String phone = editTextDialogCreateStudentPhone.getText().toString();

            studentService.createStudent(new Student(name, major, sex, age, email, phone)).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Thêm sinh viên thành công", Toast.LENGTH_SHORT).show();
                    updateData();
                } else {
                    Toast.makeText(requireContext(), "Thêm sinh viên thất bại", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        builder.show();
    }

    private void sortStudentManagement() {
        // Use the existing spinner in your layout
        View view = getLayoutInflater().inflate(R.layout.fragment_main_student_management, null);
        Spinner spinnerSortStudentManagement = requireView().findViewById(R.id.spinnerSortStudentManagement);

        List<String> sortType = new ArrayList<>();
        sortType.add("A to Z");
        sortType.add("Z to A");
        sortType.add("Major");
        sortType.add("Age");

        ArrayAdapter<String> spinnerAdapterSort = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, sortType);
        spinnerAdapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortStudentManagement.setAdapter(spinnerAdapterSort);

        // Set a listener to handle sorting when an item is selected
        spinnerSortStudentManagement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSortOption = spinnerSortStudentManagement.getSelectedItem().toString();
                performSort(selectedSortOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private void performSort(String selectedSortOption) {
        switch (selectedSortOption) {
            case "A to Z":
                Collections.sort(items, (s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
                break;
            case "Z to A":
                Collections.sort(items, (s1, s2) -> s2.getName().compareToIgnoreCase(s1.getName()));
                break;
            case "Major":
                Collections.sort(items, (s1, s2) -> s1.getMajor().compareToIgnoreCase(s2.getMajor()));
                break;
            case "Age":
                Collections.sort(items, (s1, s2) -> Integer.compare(Integer.parseInt(s1.getAge()), Integer.parseInt(s2.getAge())));
                break;
        }

        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateData();
    }

    private void initSearchWidgets(View view){
        SearchView searchView = (SearchView) view.findViewById(R.id.searchViewStudentManagement);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // If the search query is empty, display the original list
                    updateData();
                } else {
                    List<Student> filteredStudents = new ArrayList<>();
                    for (Student student : items) {
                        if (student.getName().toLowerCase().contains(newText.toLowerCase()) || student.getMajor().toLowerCase().contains(newText.toLowerCase())) {
                            filteredStudents.add(student);
                        }
                    }
                    StudentManagementAdapter adapter = new StudentManagementAdapter(filteredStudents);
                    recyclerView.setAdapter(adapter);
                }
                return false;
            }

        });
    }
}
