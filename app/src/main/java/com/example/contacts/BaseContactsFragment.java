package com.example.contacts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class BaseContactsFragment extends Fragment implements ContactAdapter.OnContactClickListener{

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

    protected List<Contact> fetchContacts() {
        List<Contact> contactList = new ArrayList<>();
        Cursor cursor = requireContext().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                },
                null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            HashSet<Long> uniqueContacts = new HashSet<>();
            while (cursor.moveToNext()) {
                @SuppressLint("Range") long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (uniqueContacts.add(contactId)) {
                    contactList.add(new Contact(contactId, name, phoneNumber));
                }
            }
            cursor.close();
        }
        return contactList;
    }

    @Override
    public void onContactClick(Contact contact) {
        // Create a new instance of AddUpdateContactFragment
        AddUpdateContactFragment fragment = new AddUpdateContactFragment();

        // Pass data using a Bundle
        Bundle bundle = new Bundle();
        bundle.putLong(getString(R.string.contact_id_key), contact.getContactId());
        bundle.putString(getString(R.string.contact_name_key), contact.getName());
        bundle.putString(getString(R.string.contact_number_key), contact.getPhoneNumber());
        fragment.setArguments(bundle);

        // Begin the transaction to replace the current fragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
