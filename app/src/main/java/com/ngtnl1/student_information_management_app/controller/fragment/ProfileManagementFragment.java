package com.ngtnl1.student_information_management_app.controller.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.ngtnl1.student_information_management_app.R;
import com.ngtnl1.student_information_management_app.controller.MainActivity;
import com.ngtnl1.student_information_management_app.model.User;
import com.ngtnl1.student_information_management_app.service.UserService;
import com.ngtnl1.student_information_management_app.service.appstatus.InternetStatus;
import com.ngtnl1.student_information_management_app.service.authentication.FirebaseEmailPasswordAuthentication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileManagementFragment extends Fragment {
    @Inject
    FirebaseEmailPasswordAuthentication firebaseEmailPasswordAuthentication;
    @Inject
    UserService userService;
    @Inject
    StorageReference storageReference;
    @Inject
    InternetStatus internetStatus;
    public static final int REQUEST_CODE_CAMERA = 1;
    private ShapeableImageView imageMainProfileManagementAvatar;
    private ImageButton buttonMainProfileManagementCamera;
    private EditText editTextMainProfileManagementName;
    private EditText editTextMainProfileManagementAge;
    private EditText editTextMainProfileManagementPhone;
    private Button buttonMainProfileManagementSave;

    public ProfileManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_profile_management, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPress();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        setDataFromDatabase();
        setOnClickListeners();
    }

    private void initViews() {
        imageMainProfileManagementAvatar = requireView().findViewById(R.id.imageMainProfileManagementAvatar);
        buttonMainProfileManagementCamera = requireView().findViewById(R.id.buttonMainProfileManagementCamera);
        editTextMainProfileManagementName = requireView().findViewById(R.id.editTextMainProfileManagementName);
        editTextMainProfileManagementAge = requireView().findViewById(R.id.editTextMainProfileManagementAge);
        editTextMainProfileManagementPhone = requireView().findViewById(R.id.editTextMainProfileManagementPhone);
        buttonMainProfileManagementSave = requireView().findViewById(R.id.buttonMainProfileManagementSave);
    }

    private void setDataFromDatabase() {
        firebaseEmailPasswordAuthentication.getUserDataRaw().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);

            if (user != null) {
                editTextMainProfileManagementName.setText(user.getName());
                editTextMainProfileManagementAge.setText(user.getAge());
                editTextMainProfileManagementPhone.setText(user.getPhone());

            } else {
                editTextMainProfileManagementName.setText("");
                editTextMainProfileManagementAge.setText("");
                editTextMainProfileManagementPhone.setText("");
            }

            setProfileImage();
        });
    }

    private void setProfileImage() {
        if (firebaseEmailPasswordAuthentication.isUserSignedIn()) {
            storageReference.child("images/" + firebaseEmailPasswordAuthentication.getUserUid() + ".jpg").getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(this).load(uri).into(imageMainProfileManagementAvatar);
            }).addOnFailureListener(exception -> {
                Glide.with(this).load(R.drawable.img_sample_avatar).into(imageMainProfileManagementAvatar);
            });
        } else {
            Glide.with(this).load(R.drawable.img_sample_avatar).into(imageMainProfileManagementAvatar);
        }
    }

    private void setOnClickListeners() {
        buttonMainProfileManagementCamera.setOnClickListener(view -> openCamera());
        buttonMainProfileManagementSave.setOnClickListener(view -> saveProfile());
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        } else {
            startCamera();
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(requireContext(), "Bạn không cho phép mở camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveImageToFile(imageBitmap);
            imageMainProfileManagementAvatar.setImageBitmap(imageBitmap);
        }
    }

    private void saveImageToFile(Bitmap bitmap) {
        File file = new File(getCurrentPhotoPath());

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentPhotoPath() {
        return requireContext().getFilesDir() + File.separator + "profile_image.jpg";
    }

    private void saveProfile() {
        firebaseEmailPasswordAuthentication.getUserDataRaw().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                User user = documentSnapshot.toObject(User.class);

                if (user != null) {
                    user.setName(editTextMainProfileManagementName.getText().toString());
                    user.setAge(editTextMainProfileManagementAge.getText().toString());
                    user.setPhone(editTextMainProfileManagementPhone.getText().toString());
                    userService.setUserData(user);

                    uploadImage();

                    Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    onBackPress();
                } else {
                    Toast.makeText(requireContext(), "Lỗi, không tìm thấy User để chỉnh sửa", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Lỗi, xin hãy thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage() {
        if (internetStatus.isOnline()) {
            File file = new File(getCurrentPhotoPath());
            Uri fileUri = Uri.fromFile(file);

            if (fileUri != null) {
                storageReference.child("images/" + firebaseEmailPasswordAuthentication.getUserUid() + ".jpg").putFile(fileUri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        MainActivity mainActivity = (MainActivity) requireActivity();
                        mainActivity.setAuthStatusViews(firebaseEmailPasswordAuthentication.isUserSignedIn());
                    } else {
                        Toast.makeText(requireContext(), "Lỗi, không thể cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            file.delete();
        } else {
            Toast.makeText(requireContext(), "Không có kết nối internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void onBackPress() {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, new StudentManagementFragment()).addToBackStack(null).commit();
    }
}

