package org.tensorflow.lite.examples.detection;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class RutinasFragment extends Fragment implements View.OnClickListener {


    public RutinasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rutinas, container, false);

        RecyclerView recVwRestaurantes = view.findViewById(R.id.recVwRoutine);
        recVwRestaurantes.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        recVwRestaurantes.addItemDecoration(mDividerItemDecoration);

        SharedPreferences prefs = getActivity().getSharedPreferences("USERID", Context.MODE_PRIVATE);
        String userID = prefs.getString("key", null);

        FirebaseDatabaseControl.setUpDataBase();
        recVwRestaurantes.setAdapter(FirebaseDatabaseControl.Adapter(userID));

        FloatingActionButton fBtnAddRutina = view.findViewById(R.id.fBtnAddRutina);
        fBtnAddRutina.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getContext(), FormularioRutinasActivity.class));
    }
}
