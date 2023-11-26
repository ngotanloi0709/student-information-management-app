package com.ngtnl1.student_information_management_app.controller.fragment;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.ngtnl1.student_information_management_app.R;
import com.ngtnl1.student_information_management_app.controller.adapter.UserManagementAdapter;
import com.ngtnl1.student_information_management_app.model.User;
import com.ngtnl1.student_information_management_app.service.UserService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserManagementFragment extends Fragment {
    @Inject
    UserService userService;
    @Inject
    StorageReference storageReference;
    private List<User> items;
    private UserManagementAdapter adapter;
    private RecyclerView recyclerView;
    private Button buttonMainUserManagementCreateUser;
    private User selectedUser;
    private FirebaseAuth firebaseAuthAdmin;

    public UserManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_user_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        setupRecyclerView();
        setOnClickListener();
        setUpFirebaseAdminInstance();
    }

    private void initViews() {
        recyclerView = requireView().findViewById(R.id.recyclerViewMainUserManagement);
        buttonMainUserManagementCreateUser = requireView().findViewById(R.id.buttonMainUserManagementCreateUser);
    }

    private void setupRecyclerView() {
        items = new ArrayList<>();
        adapter = new UserManagementAdapter(items, storageReference);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        registerForContextMenu(recyclerView);

        adapter.setOnUserItemClickListener(new UserManagementAdapter.OnUserItemClickListener() {
            @Override
            public void onItemClick(int position, User user) {
                showUserDetailsDialog(user);
            }

            @Override
            public void onItemLongClick(int position, User user) {
                selectedUser = user;
                showContextMenu();
            }
        });

        updateData();
    }

    private void showUserDetailsDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thông tin người dùng");

        View view = getLayoutInflater().inflate(R.layout.dialog_user_detail, null);

        TextView textViewDialogUserDetailName = view.findViewById(R.id.editTextDialogUserDetailName);
        TextView textViewDialogUserDetailAge = view.findViewById(R.id.editTextDialogUserDetailAge);
        TextView textViewDialogUserDetailEmail = view.findViewById(R.id.textViewDialogUserDetailEmail);
        TextView textViewDialogUserDetailPhone = view.findViewById(R.id.editTextDialogUserDetailPhone);
        TextView textViewDialogUserDetailIsLocked = view.findViewById(R.id.textViewDialogUserDetailIsLocked);
        TextView textViewDialogUserDetailRole = view.findViewById(R.id.textViewDialogUserDetailRole);
        TextView textViewDialogUserDetailLoginHistory = view.findViewById(R.id.textViewDialogUserDetailLoginHistory);

        textViewDialogUserDetailName.setText(user.getName());
        textViewDialogUserDetailAge.setText(user.getAge());
        textViewDialogUserDetailEmail.setText(user.getEmail());
        textViewDialogUserDetailPhone.setText(user.getPhone());
        textViewDialogUserDetailIsLocked.setText(user.isLocked() ? "Đã khóa" : "Chưa khóa");
        textViewDialogUserDetailRole.setText(user.getRole());
        textViewDialogUserDetailLoginHistory.append("\n");

        if (user.getLoginHistory() != null) {
            for (String loginHistory : user.getLoginHistory()) {
                textViewDialogUserDetailLoginHistory.append(loginHistory + "\n");
            }
        }

        builder.setView(view);

        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void showContextMenu() {
        registerForContextMenu(recyclerView);
        getActivity().openContextMenu(recyclerView);
        unregisterForContextMenu(recyclerView);
    }

    private void updateData() {
        userService.findAllUser().addOnCompleteListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isSuccessful()) {
                items.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getResult()) {
                    User user = documentSnapshot.toObject(User.class);

                    if (user != null) {
                        items.add(user);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setOnClickListener() {
        buttonMainUserManagementCreateUser.setOnClickListener(v -> showCreateUserDialog());
    }

    private void showCreateUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thêm người dùng");

        View view = getLayoutInflater().inflate(R.layout.dialog_create_user, null);

        EditText editTextDialogCreateUserName = view.findViewById(R.id.editTextDialogCreateUserName);
        EditText editTextDialogCreateUserEmail = view.findViewById(R.id.editTextDialogCreateUserEmail);
        EditText editTextDialogCreateUserPassword = view.findViewById(R.id.editTextDialogCreateUserPassword);
        EditText editTextDialogCreateUserAge = view.findViewById(R.id.editTextDialogCreateUserAge);
        EditText editTextDialogCreateUserPhone = view.findViewById(R.id.editTextDialogCreateUserPhone);
        CheckBox checkBoxDialogCreateUserIsLocked = view.findViewById(R.id.checkBoxDialogCreateUserIsLocked);
        Spinner spinnerDialogCreateUserRole = view.findViewById(R.id.spinnerDialogCreateUserRole);

        List<String> data = new ArrayList<>();
        data.add("MANAGER");
        data.add("EMPLOYEE");
        data.add("ADMIN");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, data);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDialogCreateUserRole.setAdapter(spinnerAdapter);
        int defaultPosition = spinnerAdapter.getPosition("EMPLOYEE");
        spinnerDialogCreateUserRole.setSelection(defaultPosition);

        builder.setView(view);

        builder.setPositiveButton("Thêm người dùng", (dialog, which) -> {
            String username = editTextDialogCreateUserName.getText().toString();
            String email = editTextDialogCreateUserEmail.getText().toString();
            String password = editTextDialogCreateUserPassword.getText().toString();
            String age = editTextDialogCreateUserAge.getText().toString();
            String phone = editTextDialogCreateUserPhone.getText().toString();
            boolean isLocked = checkBoxDialogCreateUserIsLocked.isChecked();
            String role = spinnerDialogCreateUserRole.getSelectedItem().toString();

            firebaseAuthAdmin.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = new User(email, username, age, phone, isLocked, role);

                    userService.createUser(user).addOnSuccessListener(aVoid ->
                            Toast.makeText(requireContext(), "Thêm người dùng thành công!", Toast.LENGTH_SHORT).show()
                    ).addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Thêm người dùng thất bại!", Toast.LENGTH_SHORT).show()
                    );
                    updateData();
                } else {
                    Toast.makeText(requireContext(), "Thêm người dùng thất bại!", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.dismiss();
        });
        builder.show();
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_main_item_user, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuMainItemUserEdit) {
            showEditUserDetailsDialog();
            return true;
        } else if (id == R.id.menuMainItemUserDelete) {
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void showEditUserDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thay đổi thông tin người dùng");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_user_detail, null);

        EditText editTextDialogEditUserDetailName = view.findViewById(R.id.editTextDialogEditUserDetailName);
        EditText editTextDialogEditUserDetailAge = view.findViewById(R.id.editTextDialogEditUserDetailAge);
        EditText editTextDialogEditUserDetailPhone = view.findViewById(R.id.editTextDialogEditUserDetailPhone);
        CheckBox checkBoxDialogEditUserDetailIsLocked = view.findViewById(R.id.checkBoxDialogEditUserDetailIsLocked);
        Spinner spinnerDialogEditUserDetailRole = view.findViewById(R.id.spinnerDialogEditUserDetailRole);

        List<String> data = new ArrayList<>();
        data.add("MANAGER");
        data.add("EMPLOYEE");
        data.add("ADMIN");

        editTextDialogEditUserDetailName.setText(selectedUser.getName());
        editTextDialogEditUserDetailAge.setText(selectedUser.getAge());
        editTextDialogEditUserDetailPhone.setText(selectedUser.getPhone());
        checkBoxDialogEditUserDetailIsLocked.setChecked(selectedUser.isLocked());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, data);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDialogEditUserDetailRole.setAdapter(spinnerAdapter);
        int defaultPosition = spinnerAdapter.getPosition(selectedUser.getRole());
        spinnerDialogEditUserDetailRole.setSelection(defaultPosition);

        builder.setView(view);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            selectedUser.setName(editTextDialogEditUserDetailName.getText().toString());
            selectedUser.setAge(editTextDialogEditUserDetailAge.getText().toString());
            selectedUser.setPhone(editTextDialogEditUserDetailPhone.getText().toString());
            selectedUser.setLocked(checkBoxDialogEditUserDetailIsLocked.isChecked());
            selectedUser.setRole(spinnerDialogEditUserDetailRole.getSelectedItem().toString());

            saveUserDetails();
            dialog.dismiss();
        });

        builder.show();
    }

    private void saveUserDetails() {
        userService.setUser(selectedUser).addOnSuccessListener(aVoid ->
                Toast.makeText(requireContext(), "Lưu thông tin thành công!", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(requireContext(), "Lưu thông tin thất bại!", Toast.LENGTH_SHORT).show()
        );
        updateData();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận xóa người dùng");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            userService.deleteUser(selectedUser.getEmail()).addOnSuccessListener(aVoid ->
                    Toast.makeText(requireContext(), "Xóa người dùng thành công!", Toast.LENGTH_SHORT).show()
            ).addOnFailureListener(e ->
                    Toast.makeText(requireContext(), "Xóa người dùng thất bại!", Toast.LENGTH_SHORT).show()
            );

            updateData();
            dialog.dismiss();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void setUpFirebaseAdminInstance() {
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyDg6LaXrusPd0vxF8tXSHdmCmBhKyWYn2U")
                .setApplicationId("student-infor-management-app").build();

        try {FirebaseApp myApp = FirebaseApp.initializeApp(requireContext(), firebaseOptions, "student-infor-management-app");
            firebaseAuthAdmin = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e){
            firebaseAuthAdmin = FirebaseAuth.getInstance(FirebaseApp.getInstance("student-infor-management-app"));
        }
    }
}
