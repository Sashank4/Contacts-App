package com.example.contacts;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private final List<Contact> contactList;
    private final OnContactClickListener mListener;
    private boolean isGridView;

    // Interface for callback
    public interface OnContactClickListener {
        void onContactClick(Contact contact);
    }

    // Constructor accepting the listener and layout type (grid or list)
    public ContactAdapter(List<Contact> contactList, OnContactClickListener listener, boolean isGridView) {
        this.contactList = contactList;
        this.mListener = listener;
        this.isGridView = isGridView;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isGridView) {
            // Inflate the grid item layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_grid_card, parent, false);
        } else {
            // Inflate the list item layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card, parent, false);
        }
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phoneTextView.setText(contact.getPhoneNumber());

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onContactClick(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    // Method to dynamically update layout (list or grid)
    public void setGridView(boolean isGridView) {
        this.isGridView = isGridView;
        notifyDataSetChanged();  // Refresh the adapter to apply the new layout
    }

    // ViewHolder for binding data to the view
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, phoneTextView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneTextView = itemView.findViewById(R.id.contact_phone);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Return different view types for grid and list layout
        return isGridView ? VIEW_TYPE_GRID : VIEW_TYPE_LIST;
    }

    // View types constants
    private static final int VIEW_TYPE_GRID = 1;
    private static final int VIEW_TYPE_LIST = 2;
}
