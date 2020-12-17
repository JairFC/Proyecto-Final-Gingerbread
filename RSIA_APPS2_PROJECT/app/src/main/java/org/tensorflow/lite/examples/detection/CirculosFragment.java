package org.tensorflow.lite.examples.detection;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.plugins.annotation.Circle;
//import com.google.zxing.qrcode.encoder.QRCode;


import java.util.ArrayList;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class CirculosFragment extends Fragment implements View.OnClickListener {

    ListView listCirculos;
    private AdapterCircles adapterCircles;

    private ArrayList<Circles> detalleCircles = new ArrayList<>();
    private AlertDialog codeDialog;
    public CirculosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_circulos, container, false);
        initListCircles();

        ImageView imgVwQR = view.findViewById(R.id.imgVwQRCode);
        imgVwQR.setImageResource(R.drawable.camera);


        Button btnAddCircle = view.findViewById(R.id.btnAddCirculo);
        btnAddCircle.setOnClickListener(this);

        SharedPreferences prefs = getActivity().getSharedPreferences("USERID", Context.MODE_PRIVATE);
        String userID = prefs.getString("key", null);
        TextView txtVwCode = view.findViewById(R.id.txtVwQRCode);
        txtVwCode.setText(userID);
      //  Bitmap myBitmap = QRCode.from(userID).withSize(500, 500).bitmap();
        //imgVwQR.setImageBitmap(myBitmap);
        listCirculos = view.findViewById(R.id.listCirculos);
        return view;
    }


    @Override
    public void onClick(View v) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogcircles, null);
        EditText eTxtCode = view.findViewById(R.id.etxtCodigo);
        Button btnAddCircle = view.findViewById(R.id.btnSendCode);
        btnAddCircle.setOnClickListener(v1 -> {
            if (eTxtCode.getText().length() > 0) {
                checkCode(eTxtCode.getText().toString());
            } else {
                Toast.makeText(getContext(), "Ingresa un codigo valido", Toast.LENGTH_SHORT).show();
            }


        });
        codeDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .create();
        codeDialog.show();

    }

    private void checkCode(String userCode) {
        FirebaseDatabaseControl.setUpDataBase();
        FirebaseDatabaseControl.getDatabaseReference()
                .child("Users")
                .child("Customers")
                .child(userCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.d("CHECK CODE", "EL USUARIO SI EXISTE");
                            addNewCircleToUser(userCode);
                        } else {
                            Log.d("CHECK CODE", "EL USUARIO NO EXISTE");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void addNewCircleToUser(String userCode) {
        SharedPreferences prefs = getActivity().getSharedPreferences("USERID", Context.MODE_PRIVATE);
        String userID = prefs.getString("key", null);

        FirebaseDatabaseControl.getDatabaseReference()
                .child("Users")
                .child("Customers")
                .child(userID)
                .child("circles")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList<String> circles = (ArrayList<String>) dataSnapshot.getValue();
                            circles.add(userCode);

                            FirebaseDatabaseControl.getDatabaseReference()
                                    .child("Users")
                                    .child("Customers")
                                    .child(userID)
                                    .child("circles")
                                    .setValue(circles);
                            codeDialog.dismiss();
                            Toast.makeText(getContext(), "Contacto añadido con exito a tus circulos!", Toast.LENGTH_SHORT).show();
                            initListCircles();
                            //updateListCircles();
                        } else {
                            ArrayList<String> circless = new ArrayList<>();
                            circless.add(userCode);

                            FirebaseDatabaseControl.getDatabaseReference()
                                    .child("Users")
                                    .child("Customers")
                                    .child(userID)
                                    .child("circles")
                                    .setValue(circless);
                            codeDialog.dismiss();
                            Toast.makeText(getContext(), "Contacto añadido con exito a tus circulos!", Toast.LENGTH_SHORT).show();

                            initListCircles();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void initListCircles() {
        detalleCircles.clear();
        SharedPreferences prefs = getActivity().getSharedPreferences("USERID", Context.MODE_PRIVATE);
        String userID = prefs.getString("key", null);
        FirebaseDatabaseControl.setUpDataBase();
        FirebaseDatabaseControl.getDatabaseReference()
                .child("Users")
                .child("Customers")
                .child(userID)
                .child("circles")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList<String> circles = (ArrayList<String>) dataSnapshot.getValue();

                            for (String c : circles) {
                                Log.d("circulos que sigue", c);
                                FirebaseDatabaseControl.getDatabaseReference()
                                        .child("Users")
                                        .child("Customers")
                                        .child(c)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                Circles perfil = dataSnapshot.getValue(Circles.class);
                                                detalleCircles.add(perfil);
                                                adapterCircles = new AdapterCircles(getContext(), detalleCircles);
                                                listCirculos.setAdapter(adapterCircles);

                                                listCirculos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                        Circles circle = detalleCircles.get(i);

                                                        Intent intent = new Intent(getApplicationContext(), DetalleAmigo.class);

                                                        String Extra = circle.getId();
                                                        intent.putExtra("id",Extra);
                                                        startActivity(intent);
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
    }


}
