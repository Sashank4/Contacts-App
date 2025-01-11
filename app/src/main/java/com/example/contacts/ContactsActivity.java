package com.example.contacts;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class ContactsActivity extends AppCompatActivity {
    private Button listViewButton, gridViewButton, recyclerViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_activity);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.show_contacts_toolbar);
        toolbar.setTitle(getString(R.string.show_contacts_screen_title));

        listViewButton = findViewById(R.id.btn_list_view);
        gridViewButton = findViewById(R.id.btn_grid_view);
        recyclerViewButton = findViewById(R.id.btn_recycler_view);

        listViewButton.setOnClickListener(v -> loadListFragment(new ListViewFragment()));
        gridViewButton.setOnClickListener(v -> switchView(true));
        recyclerViewButton.setOnClickListener(v -> switchView(false));
        if (savedInstanceState == null) {
            loadListFragment(new ListViewFragment());
        }
    }


    public void loadListFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void switchView(Boolean isGridView) {
        // Make ListView visible and hide RecyclerView & GridView
        RecyclerViewFragment fragment = (RecyclerViewFragment) getSupportFragmentManager().findFragmentByTag(RecyclerViewFragment.class.getSimpleName());

        if (fragment != null) {
            // Call the method in RecyclerViewFragment to update layout
            fragment.updateLayout(isGridView);
        } else {
            // If the fragment isn't found, you can initialize it and add it dynamically
            fragment = new RecyclerViewFragment();

            // Pass the isGridView value using a Bundle
            Bundle args = new Bundle();
            args.putBoolean("isGridView", isGridView);  // Add the isGridView value to the arguments
            fragment.setArguments(args);

            // Replace the fragment in the container
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, RecyclerViewFragment.class.getSimpleName()) // Replace with RecyclerViewFragment
                    .commit();
        }
    }

}