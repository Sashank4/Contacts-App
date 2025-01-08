package com.example.contacts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class ShowContactsDeniedFragment extends Fragment {


    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean readContactsGranted = result.getOrDefault(Manifest.permission.READ_CONTACTS, false);
                Boolean writeContactsGranted = result.getOrDefault(Manifest.permission.WRITE_CONTACTS, false);

                if (readContactsGranted && writeContactsGranted) {
                    // Both permissions granted, navigate to the next fragment
                    navigateToNextFragment();
                } else {
                    // Permissions denied, show a toast or message
                    Toast.makeText(getContext(), "Permissions denied!", Toast.LENGTH_SHORT).show();
                    Log.e("hehe", "Permission denied");
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_contacts_denied, container, false);

        // Set up Toolbar
        Toolbar toolbar = view.findViewById(R.id.show_contacts_toolbar);
        toolbar.setTitle(getString(R.string.show_contacts_screen_title));
        Button givePermissionButton = view.findViewById(R.id.give_permission_button);
        givePermissionButton.setOnClickListener(v -> {
            // Check if permissions are already granted
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                // If not, request the permissions using the result launcher
                requestPermissionsLauncher.launch(new String[]{
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                });
                Log.e("Asking Permission", "Trying to ask permission");
            } else {
                // If permissions are already granted, navigate to the next fragment
                navigateToNextFragment();
            }
        });

        return view;
    }

    // Method to navigate to next fragment after permissions are granted
    private void navigateToNextFragment() {
        // Your code to navigate to another fragment
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new ShowContactsFragment());  // Replace with actual fragment
        transaction.commit();
    }
}
