package com.example.contactappbydatabinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactDataAdapter extends RecyclerView.Adapter<ContactDataAdapter.ContactViewHolder> {

    private ArrayList<Contact> contacts;


    public ContactDataAdapter(ArrayList<Contact> contacts){
        this.contacts=contacts;
    }

    @NonNull
    @Override
    public ContactDataAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.contact_list_item,parent,false);

        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactDataAdapter.ContactViewHolder holder, int i) {
        Contact currentcontact=contacts.get(i);
        holder.name.setText(currentcontact.getName());
        holder.email.setText(currentcontact.getEmail());


    }

    @Override
    public int getItemCount() {
        if(contacts!=null){
            return  contacts.size();
        }
        else {
            return 0;
        }
    }


    public  void setContacts(ArrayList<Contact> contacts){   //this is setter for array list
        this.contacts=contacts;
        notifyDataSetChanged();

    }

    class ContactViewHolder extends RecyclerView.ViewHolder{
        private TextView name,email;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name=itemView.findViewById(R.id.tvName);
            this.email=itemView.findViewById(R.id.tvEmail);
        }
    }
}
