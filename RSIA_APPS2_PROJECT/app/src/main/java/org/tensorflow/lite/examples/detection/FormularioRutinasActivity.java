package org.tensorflow.lite.examples.detection;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import static org.tensorflow.lite.examples.detection.LoginActivity.PREFERENCE_FILENAME;

public class FormularioRutinasActivity extends AppCompatActivity {


    public static int vez=0;

    TextView tvMyDate, tvHourIni, tvHoraFin;
    EditText etTitulo, etDesc;
    CalendarView calendarView;
    Button btnSig;
    DatePickerDialog.OnDateSetListener setListener;
    private int dia, hora, mes, year, minutos;
    String date;
    String horaI, minutoI, minutoF;
    String pulseF;
    String horaInicial, horaFinal;
    String routinId, mTitle, mDesc;
    Intent i;
    String userID;

    TextView txtPulso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_rutinas);

        SharedPreferences prefs = getSharedPreferences("USERID", Context.MODE_PRIVATE);
        userID = prefs.getString("key", null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnSig = findViewById(R.id.btnOkDay);
        tvMyDate = findViewById(R.id.txtverFecha);

        etTitulo = findViewById(R.id.ettitulocita);
        etDesc = findViewById(R.id.etDescription);

        tvHourIni = findViewById(R.id.txvHourIni);
        //tvHoraFin = findViewById(R.id.selectHourFin);


        txtPulso = findViewById(R.id.txtPulso);

        btnSig.setOnClickListener(v -> {

            mTitle = etTitulo.getText().toString();
            mDesc = etDesc.getText().toString();


            //SUBIR RUTINA

            //falta poner el uid del customer logueadoo :p

            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID).child("Rutinas").push();

            HashMap map = new HashMap();

            map.put("mfechaIni", date);
            map.put("descr", mDesc);
            map.put("mtitulo", mTitle);
            map.put("mHourIni", horaI + ":" + minutoI);
            map.put("mPulse", pulseF);

            current_user_db.setValue(map);

            Toast.makeText(FormularioRutinasActivity.this, "RUTINA Y DATOS DEL USUARIO REGISTRADOS:", Toast.LENGTH_LONG).show();
            finish();

        });


        setListener = (view, year, month, dayOfMonth) -> {

            month = month + 1;
            date = dayOfMonth + "-" + month + "-" + year;
            tvMyDate.setText(date);

        };

        vez=vez++;
        int p = vez;
    }


    public void onClickFecha(View v) {
        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        year = c.get(Calendar.YEAR);
        mes = c.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                FormularioRutinasActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                date = dayOfMonth + "-" + month + "-" + year;
                tvMyDate.setText("FECHA SELECCIONADA:     " + date);
            }
        }, year, mes, dia);
        datePickerDialog.show();
    }


    public void onClickHoraIni(View v) {
        Locale locale1 = Locale.US;
        TimeZone tz1 = TimeZone.getTimeZone("GMT");
        final Calendar c = Calendar.getInstance(tz1);

        hora = c.get(Calendar.HOUR_OF_DAY);
        minutos = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {

            horaI = String.valueOf(hourOfDay);

            if (minute < 10) {
                minutoI = "0" + minute;
            } else {
                minutoI = "" + minute;
            }

            tvHourIni.setText("HORA INICIO SELECCIONADA:    " + horaI + ":" + minutoI);
            horaInicial = horaI + ":" + minutoI;

        }, hora, minutos, false);
        timePickerDialog.show();
    }


    /*public void onClickHoraFin(View v) {
        Locale locale1 = Locale.US;
        TimeZone tz1 = TimeZone.getTimeZone("GMT");
        final Calendar c = Calendar.getInstance(tz1);

        hora = c.get(Calendar.HOUR_OF_DAY);
        minutos = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {

            horaF = String.valueOf(hourOfDay);

            if (minute < 10) {
                minutoF = "0" + minute;
            } else {
                minutoF = "" + minute;
            }

            tvHoraFin.setText("HORA DE FINALIZACION SELECCIONADA:    " + horaF + ":" + minutoF);
            horaFinal = horaF + ":" + minutoF;
        }, hora, minutos, false);
        timePickerDialog.show();
    }*/

    public void onClickTomarPulso(View view) {

       // startActivity(new Intent(getApplicationContext(), PulseActivity.class));

    }


    @Override
    protected void onResume() {


        if (vez>=1){

            SharedPreferences gameSettings = getSharedPreferences(PREFERENCE_FILENAME, MODE_PRIVATE);
            pulseF = gameSettings.getString("pulso", "x");

            if (Integer.parseInt(pulseF)!=0){

                //mostrar el pulso en un label

                if (txtPulso!=null){
                    txtPulso.setText(""+pulseF);
                }else{
                    txtPulso.setText("no se registra pulso");
                }


            }
        }
        super.onResume();
    }
}