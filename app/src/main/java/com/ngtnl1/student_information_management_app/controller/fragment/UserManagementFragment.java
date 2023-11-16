package com.ngtnl1.student_information_management_app.controller.fragment;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private List<User> items;
    private UserManagementAdapter adapter;
    private RecyclerView recyclerView;
    @Inject
    UserService userService;
    @Inject
    StorageReference storageReference;

    public UserManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_user_management, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        setupRecyclerView();
    }

    private void initViews() {
        recyclerView = requireView().findViewById(R.id.recyclerViewMainUserManagement);
    }

    private void setupRecyclerView() {
        items = new ArrayList<>();
        adapter = new UserManagementAdapter(items, storageReference);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        registerForContextMenu(recyclerView);

//        adapter.setOnUserItemClickListener((position, user) -> showContextMenu(user));
        adapter.setOnUserItemClickListener(new UserManagementAdapter.OnUserItemClickListener() {
            @Override
            public void onItemClick(int position, User user) {
                showUserDetailsDialog(user);
            }

            @Override
            public void onItemLongClick(int position, User user) {
                showContextMenu(user);
            }
        });

        updateData();
    }

    private void showContextMenu(User user) {
        registerForContextMenu(recyclerView);
        getActivity().openContextMenu(recyclerView);
        unregisterForContextMenu(recyclerView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_main_item_user, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuMainItemUserEdit) {
            // Xử lý sự kiện edit
            return true;
        } else if (id == R.id.menuMainItemUserDelete) {
            // Xử lý sự kiện delete
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private void showUserDetailsDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thông tin người dùng");

        View view = getLayoutInflater().inflate(R.layout.dialog_user_detail, null);

        TextView textViewDialogUserDetailName = view.findViewById(R.id.textViewDialogUserDetailName);
        TextView textViewDialogUserDetailAge = view.findViewById(R.id.textViewDialogUserDetailAge);
        TextView textViewDialogUserDetailEmail = view.findViewById(R.id.textViewDialogUserDetailEmail);
        TextView textViewDialogUserDetailPhone = view.findViewById(R.id.textViewDialogUserDetailPhone);
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
        for (String loginHistory : user.getLoginHistory()) {
            textViewDialogUserDetailLoginHistory.append(loginHistory + "\n");
        }

        builder.setView(view);

        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());

        builder.show();
    }


    private void updateData() {
        userService.getAllUser().addOnCompleteListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isSuccessful()) {
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
}
