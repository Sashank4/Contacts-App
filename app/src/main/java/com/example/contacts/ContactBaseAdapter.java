package com.example.contacts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.util.List;

public class ContactBaseAdapter extends BaseAdapter {
    private final List<Contact> contactList;
    private final Context context;

    public ContactBaseAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // If the view is null, inflate the contact_card layout
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.contact_card, parent, false);
        }

        // Get the current contact
        Contact contact = contactList.get(position);

        // Bind data to the views inside the contact_card layout
        TextView nameTextView = convertView.findViewById(R.id.contact_name);
        TextView phoneTextView = convertView.findViewById(R.id.contact_phone);

        nameTextView.setText(contact.getName());
        phoneTextView.setText(contact.getPhoneNumber());

        convertView.setOnClickListener(v -> {
            if (context != null && context instanceof FragmentActivity) {
                FragmentActivity activity = (FragmentActivity) context;

                // Create a new instance of AddUpdateContactFragment
                AddUpdateContactFragment fragment = new AddUpdateContactFragment();

                // Pass data using a Bundle
                Bundle bundle = new Bundle();
                bundle.putLong(String.valueOf(R.string.contact_id_key), contact.getContactId());
                bundle.putString(String.valueOf(R.string.contact_name_key), contact.getName());
                bundle.putString(String.valueOf(R.string.contact_number_key), contact.getPhoneNumber());

                // Set the arguments to the fragment
                fragment.setArguments(bundle);

                // Begin the transaction to replace the current fragment
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)  // Replace with your fragment container's ID
                        .addToBackStack(null)  // Optionally add to back stack
                        .commit();
            }
        });

        return convertView;
    }
}
