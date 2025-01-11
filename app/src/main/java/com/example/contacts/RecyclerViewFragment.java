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

public class RecyclerViewFragment extends BaseContactsFragment {

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



}
