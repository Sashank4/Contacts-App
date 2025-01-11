package com.example.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ContactBaseAdapter extends BaseAdapter {
    private final List<Contact> contactList;
    private final Context context;
    private final ContactAdapter.OnContactClickListener mListener;

    public ContactBaseAdapter(Context context, List<Contact> contactList, ListViewFragment listener) {
        this.context = context;
        this.contactList = contactList;
        this.mListener = listener;
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

        // Set click listener on the item view
        convertView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onContactClick(contact);
            }
        });

        return convertView;
    }
}
