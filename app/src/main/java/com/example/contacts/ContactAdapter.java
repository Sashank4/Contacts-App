package com.example.contacts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private final List<Contact> contactList;

    public ContactAdapter(List<Contact> contactList) {
        this.contactList = contactList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateContactList(List<Contact> newContactList) {
        this.contactList.clear();
        this.contactList.addAll(newContactList);
        notifyDataSetChanged(); // Notify RecyclerView to refresh
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the contact_item.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        // Bind data to the views
        Contact contact = contactList.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phoneTextView.setText(contact.getPhoneNumber());

        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
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
            } else {
                Log.e("ContactAdapter", "Context is null or not a FragmentActivity!");
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    // ViewHolder class to hold item views
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, phoneTextView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneTextView = itemView.findViewById(R.id.contact_phone);
        }
    }
}
