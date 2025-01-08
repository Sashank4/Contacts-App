package com.example.contacts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ShowContactsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private GridView gridView;
    private ListView listView;
    private FloatingActionButton fab;
    private Button listViewButton, gridViewButton, recyclerViewButton;

    // ActivityResultLauncher for permission requests
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean readContactsGranted = result.getOrDefault(Manifest.permission.READ_CONTACTS, false);
                Boolean writeContactsGranted = result.getOrDefault(Manifest.permission.WRITE_CONTACTS, false);

                if (readContactsGranted && writeContactsGranted) {
                    loadContacts(); // Both permissions granted
                } else {
                    Toast.makeText(getContext(), "Permissions denied!", Toast.LENGTH_SHORT).show();
                    ShowContactsDeniedFragment fragment = new ShowContactsDeniedFragment();
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)  // Replace with your fragment container ID
                            .commit();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_contacts, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        listView = view.findViewById(R.id.list_view);
        gridView = view.findViewById(R.id.grid_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listViewButton = view.findViewById(R.id.btn_list_view);
        gridViewButton = view.findViewById(R.id.btn_grid_view);
        recyclerViewButton = view.findViewById(R.id.btn_recycler_view);

        // Set up Toolbar
        Toolbar toolbar = view.findViewById(R.id.show_contacts_toolbar);
        toolbar.setTitle(getString(R.string.show_contacts_screen_title));

        // Floating Action Button to add/update contact
        fab = view.findViewById(R.id.add_contact);
        fab.setOnClickListener(v -> {
            AddUpdateContactFragment fragment = new AddUpdateContactFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // Adds to back stack to allow navigating back
                    .commit();
        });

        listViewButton.setOnClickListener(v -> showListView());
        gridViewButton.setOnClickListener(v -> showGridView());
        recyclerViewButton.setOnClickListener(v -> showRecyclerView());

        // Request permissions
        requestContactsPermissions();

        return view;
    }

    private void requestContactsPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            requestPermissionsLauncher.launch(new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
            });
        } else {
            // Permissions are already granted
            loadContacts();
        }
    }

    private void loadContacts() {
        // RecyclerView Setup
        List<Contact> contactList = fetchContacts();
        adapter = new ContactAdapter(contactList);
        recyclerView.setAdapter(adapter);

        // Initialize the ListView
        ContactBaseAdapter listAdapter = new ContactBaseAdapter(getContext(), contactList);
        listView.setAdapter(listAdapter);

        // Initialize the GridView
        ContactBaseAdapter gridAdapter = new ContactBaseAdapter(getContext(), contactList);
        gridView.setAdapter(gridAdapter);


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

    private void showRecyclerView() {
        // Make RecyclerView visible and hide ListView & GridView
        recyclerView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);
    }

    private void showListView() {
        // Make ListView visible and hide RecyclerView & GridView
        recyclerView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.GONE);
    }

    private void showGridView() {
        // Make GridView visible and hide RecyclerView & ListView
        recyclerView.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        gridView.setVisibility(View.VISIBLE);
    }


}
