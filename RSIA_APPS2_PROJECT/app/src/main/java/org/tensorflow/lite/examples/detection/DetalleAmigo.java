package org.tensorflow.lite.examples.detection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class DetalleAmigo extends AppCompatActivity {

   // ListView listObjetos;
    //private AdapterObjectsFriend adapterObjects;

    String friendUID;

    private ArrayList<String> detalleCircles = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_amigo);


       // listObjetos = findViewById(R.id.lstobjetos);



        Intent intent = getIntent();
        friendUID = intent.getStringExtra("id");



     //   initListFriendsObjects();


        RecyclerView recVwRestaurantes = findViewById(R.id.recVwObject);
        recVwRestaurantes.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL);
        recVwRestaurantes.addItemDecoration(mDividerItemDecoration);


        FirebaseDatabaseControl.setUpDataBase();
        recVwRestaurantes.setAdapter(FirebaseDatabaseControl.Adapter2(friendUID));



    }

    private void initListFriendsObjects() {
        detalleCircles.clear();

        FirebaseDatabaseControl.setUpDataBase();
        FirebaseDatabaseControl.getDatabaseReference()
                .child("Users")
                .child("Customers")
                .child(friendUID)
                .child("objetos")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            HashMap map = (HashMap) snapshot.getValue();

                            Set<String> keySet = map.keySet();
                            ArrayList<String> listOfKeys = new ArrayList<String>(keySet);



                          //  listObjetos.setAdapter(new ArrayAdapter<String>(
                            //        getApplicationContext(),
                              //      android.R.layout.simple_list_item_1,listOfKeys
                           // ));

                            // adapterObjects = new AdapterObjectsFriend(getApplicationContext(),listOfKeys);
                           // listObjetos.setAdapter(adapterObjects);


                      }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

}