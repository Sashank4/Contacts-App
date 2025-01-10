package com.example.contacts;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RecyclerViewFragment extends BaseContactsFragment implements ContactAdapter.OnContactClickListener {

    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private boolean isGridView;  // Default to ListView layout
    private ConstraintLayout permissionDeniedView;
    private ConstraintLayout RecyclerContactView;
    private FloatingActionButton fab;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        permissionDeniedView = view.findViewById(R.id.list_contact_denied);
        RecyclerContactView = view.findViewById(R.id.list_contact_view);
        // Check and request permissions
        checkAndRequestPermissions();

        setupCTAListener(view, R.id.give_permission_button);

        // Fetch the isGridView value from arguments (default to false if not set)
        if (getArguments() != null) {
            isGridView = getArguments().getBoolean("isGridView", false);  // Default to false (list view)
            Log.e("grid view from args", "grid view from args" + isGridView);
        }

        // Initialize RecyclerView based on the isGridView value
        setupRecyclerViewLayout();

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

    private void setupRecyclerViewLayout() {
        // Initialize RecyclerView LayoutManager based on the isGridView value
        if (isGridView) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Set Grid Layout
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Set Linear Layout (List)
        }
    }

    public void updateLayout(boolean isGridView) {
        this.isGridView = isGridView;  // Store the layout type
        setupRecyclerViewLayout();  // Reapply the layout manager

        // Update the adapter with the new layout
        if (adapter != null) {
            adapter.setGridView(isGridView);  // Pass the updated layout type to the adapter
            adapter.notifyDataSetChanged();  // Notify the adapter to re-render with the new layout
        }
    }

    @Override
    protected void giveCTAClicked() {
        checkAndRequestPermissions();
        Toast.makeText(getContext(), "CTA clicked in ChildFragment2", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPermissionsGranted() {
        super.onPermissionsGranted();
        // Load contacts or perform actions requiring permissions
        loadContacts();
        permissionDeniedView.setVisibility(View.GONE);
        RecyclerContactView.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onPermissionsDenied() {
        super.onPermissionsDenied();
        permissionDeniedView.setVisibility(View.VISIBLE);
        RecyclerContactView.setVisibility(View.GONE);
    }

    private void loadContacts() {
        // Fetch contact data and initialize the adapter
        List<Contact> contactList = fetchContacts();
        Log.e("grid view in load contacts", "grid view in load contacts" + isGridView);

        // Initialize the adapter and pass isGridView to it
        adapter = new ContactAdapter(contactList, this, isGridView);  // Pass isGridView to the adapter
        recyclerView.setAdapter(adapter);
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
