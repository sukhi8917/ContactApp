package com.example.contactappbydatabinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.contactappbydatabinding.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ContactAppDatabase contactAppDatabase;
    private ArrayList<Contact> contacts=new ArrayList<>();
    private ContactDataAdapter contactDataAdapter;

    //binding
    private ActivityMainBinding activityMainBinding;
    private MainActivityClickHandlers handlers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //data binding
        activityMainBinding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        handlers=new MainActivityClickHandlers(this);
        activityMainBinding.setClickHandler(handlers);


        //RecyclerView
         RecyclerView recyclerView=findViewById(R.id.recyclerView);
       //  recyclerView=activityMainBinding.recyclerView;
         recyclerView.setLayoutManager(new LinearLayoutManager(this));
         recyclerView.setHasFixedSize(true);


         //Adapter
        contactDataAdapter=new ContactDataAdapter(contacts);
        recyclerView.setAdapter(contactDataAdapter);

        //Database
        contactAppDatabase= Room.databaseBuilder(
                getApplicationContext(),
                ContactAppDatabase.class,
                "ContactDB"
        ).allowMainThreadQueries().build();

        //add data
        LoadData();


        //Handling swiping
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Contact contact=contacts.get(viewHolder.getAdapterPosition());
                DeleteContact(contact);

            }
        }).attachToRecyclerView(recyclerView);




        //Fab
//        FloatingActionButton fab=findViewById(R.id.floatingActionButton);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i=new Intent(MainActivity.this,AddNewContactActivity.class);
//                startActivityForResult(i,1);
//            }
//        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK){
            assert data != null;
            String name=data.getStringExtra("NAME");
            String email=data.getStringExtra("EMAIL");

            Contact contact=new Contact(name,email,0);

            AddNewContact(contact);
        }
    }

    private void LoadData(){
        //load the data from the database into the recycler view
        ExecutorService executor= Executors.newSingleThreadExecutor();
        Handler handler=new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                //OnBackground
                contacts.addAll(contactAppDatabase.getContactDao().getAllContacts());


                //On Post execution
                handler.post(new Runnable() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        contactDataAdapter.setContacts(contacts);
                        contactDataAdapter.notifyDataSetChanged();

                    }
                });
            }
        });
    }



    private void AddNewContact(Contact contact){
        ExecutorService executor= Executors.newSingleThreadExecutor();
        Handler handler=new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                //OnBackground
                contactAppDatabase.getContactDao().insert(contact);
                contacts.add(contact);



                //On post execution
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        contactDataAdapter.notifyDataSetChanged();


                    }
                });

            }
        });

    }


    //deleting contact
    private void DeleteContact(Contact contact){

        ExecutorService executor= Executors.newSingleThreadExecutor();
        Handler handler=new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                //OnBackground
                contactAppDatabase.getContactDao().delete(contact);
                contacts.remove(contact);


                //On post execution
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        contactDataAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }


    public class MainActivityClickHandlers{
        Context context;

        public MainActivityClickHandlers(Context context) {
            this.context = context;
        }


        public void onFABClicked(View view){
            Intent i=new Intent(MainActivity.this,AddNewContactActivity.class);
            startActivityForResult(i,1);
        }
    }
}