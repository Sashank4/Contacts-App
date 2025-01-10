package com.example.contacts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public abstract class BaseContactsFragment extends Fragment {

    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean readContactsGranted = result.getOrDefault(Manifest.permission.READ_CONTACTS, false);
                Boolean writeContactsGranted = result.getOrDefault(Manifest.permission.WRITE_CONTACTS, false);

                if (readContactsGranted != null && readContactsGranted && writeContactsGranted != null && writeContactsGranted) {
                    onPermissionsGranted();
                } else {
                    onPermissionsDenied();
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Method to check and request permissions
    protected void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsLauncher.launch(new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
            });
        } else {
            // Permissions already granted
            onPermissionsGranted();
        }
    }

    protected abstract void giveCTAClicked();

    protected void setupCTAListener(View rootView, int ctaId) {
        View ctaButton = rootView.findViewById(ctaId);
        if (ctaButton != null) {
            ctaButton.setOnClickListener(v -> giveCTAClicked());
        }
    }

    // To be overridden in child fragments
    protected void onPermissionsGranted() {
        // Default implementation can be empty or for debugging
    }

    // To be overridden in child fragments
    protected void onPermissionsDenied() {
        // Default implementation can be empty or for debugging
    }
}
