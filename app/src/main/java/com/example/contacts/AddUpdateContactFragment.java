package com.example.contacts;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class AddUpdateContactFragment extends Fragment {

    private TextInputEditText editContactName;
    private TextInputEditText editContactNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_update_contact, container, false);

        editContactName = view.findViewById(R.id.editContactName);
        editContactNumber = view.findViewById(R.id.editContactNumber);

        Bundle arguments = getArguments();
        long contactId;
        String contactName = null;
        String contactNumber = null;

        if (arguments != null) {
            contactId = arguments.getLong(String.valueOf((R.string.contact_id_key)), -1); // Default value is -1 if not found
            contactName = arguments.getString(String.valueOf(R.string.contact_name_key));
            contactNumber = arguments.getString(String.valueOf(R.string.contact_number_key));
        } else {
            contactId = -1;
        }

        // Check if contactId is valid (not -1), meaning we're updating an existing contact
        if (contactId != -1) {
            editContactName.setText(contactName);
            editContactNumber.setText(contactNumber);
            view.findViewById(R.id.addContactButton).setOnClickListener(v -> updateContact(contactId));
        } else {
            // If no contactId, we are adding a new contact
            view.findViewById(R.id.addContactButton).setOnClickListener(v -> addContact());
        }

        return view;
    }

    private void updateContact(long contactId) {
        String contactName = editContactName.getText().toString().trim();
        String contactNumber = editContactNumber.getText().toString().trim();

        if (TextUtils.isEmpty(contactName) || TextUtils.isEmpty(contactNumber)) {
            Toast.makeText(getContext(), "Please enter both name and number", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName);

        requireContext().getContentResolver().update(
                ContactsContract.Data.CONTENT_URI,
                values,
                ContactsContract.Data.CONTACT_ID + " = ? AND " +
                        ContactsContract.Data.MIMETYPE + " = ?",
                new String[]{
                        String.valueOf(contactId),
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                }
        );

        ContentValues phoneValues = new ContentValues();
        phoneValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        phoneValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contactNumber);
        phoneValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

        requireContext().getContentResolver().update(
                ContactsContract.Data.CONTENT_URI,
                phoneValues,
                ContactsContract.Data.CONTACT_ID + " = ? AND " +
                        ContactsContract.Data.MIMETYPE + " = ?",
                new String[]{
                        String.valueOf(contactId),
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                }
        );

        Toast.makeText(getContext(), "Contact updated successfully!", Toast.LENGTH_SHORT).show();
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void addContact() {
        String contactName = editContactName.getText().toString().trim();
        String contactNumber = editContactNumber.getText().toString().trim();

        if (TextUtils.isEmpty(contactName) || TextUtils.isEmpty(contactNumber)) {
            Toast.makeText(getContext(), "Please enter both name and number", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, "com.google");
        values.put(ContactsContract.RawContacts.ACCOUNT_NAME, "example@gmail.com");
        Uri rawContactUri = requireContext().getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);

        long rawContactId = ContentUris.parseId(rawContactUri);

        ContentValues nameValues = new ContentValues();
        nameValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        nameValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        nameValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName);
        requireContext().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, nameValues);

        ContentValues phoneValues = new ContentValues();
        phoneValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        phoneValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        phoneValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contactNumber);
        phoneValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        requireContext().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, phoneValues);

        Toast.makeText(getContext(), "Contact added successfully!", Toast.LENGTH_SHORT).show();
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
