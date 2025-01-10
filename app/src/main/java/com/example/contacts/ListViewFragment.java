package com.example.contacts;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ListViewFragment extends BaseContactsFragment implements ContactAdapter.OnContactClickListener {

    private ListView listView;
    private ConstraintLayout permissionDeniedView;
    private ConstraintLayout listContactView;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);

        listView = view.findViewById(R.id.list_view);
        permissionDeniedView = view.findViewById(R.id.list_contact_denied);
        listContactView = view.findViewById(R.id.list_contact_view);

        // Check and request permissions
        checkAndRequestPermissions();

        // Set up listener for the CTA button
        setupCTAListener(view, R.id.give_permission_button);

        // Set up FAB click listener
        fab = view.findViewById(R.id.add_contact);
        fab.setOnClickListener(v -> {
            AddUpdateContactFragment fragment = new AddUpdateContactFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // Adds to back stack to allow navigating back
                    .commit();
        });

        return view;
    }

    @Override
    protected void giveCTAClicked() {
        checkAndRequestPermissions();
        Toast.makeText(getContext(), "Permission requested from CTA", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPermissionsGranted() {
        super.onPermissionsGranted();
        // Load contacts or perform actions requiring permissions
        loadContacts();
        permissionDeniedView.setVisibility(View.GONE);
        listContactView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPermissionsDenied() {
        super.onPermissionsDenied();
        permissionDeniedView.setVisibility(View.VISIBLE);
        listContactView.setVisibility(View.GONE);
    }

    private void loadContacts() {
        // Fetch contacts
        List<Contact> contactList = fetchContacts();

        // Pass the OnContactClickListener to the adapter
        ContactBaseAdapter listAdapter = new ContactBaseAdapter(getContext(), contactList, this);
        listView.setAdapter(listAdapter);
    }

    private List<Contact> fetchContacts() {
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
