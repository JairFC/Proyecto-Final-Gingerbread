package org.tensorflow.lite.examples.detection;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity {

    private BroadcastReceiver MyReceiver = null;
    private TextView mLossingPass, mRegisterHere, mLoginDriver;
    private Button mIngres;
    private EditText editTextPhone, editTextPass;
    public static String UIDS;
    String lada = "+52";
    String nTel;
    String code;
    String phone;
    String codeSent;
    FirebaseAuth mAuth;
    String UIDtel;
    EditText input;
    EditText input2;
    public static final String PREFERENCE_FILENAME = "AppDrivePrefs";
    ImageView photoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //MyReceiver = new MyReceiver();
        broadcastIntent();

        mAuth = FirebaseAuth.getInstance();
        input = new EditText(LoginActivity.this);
        input2 = new EditText(LoginActivity.this);

        editTextPhone = findViewById(R.id.txt_phone);
        editTextPass = findViewById(R.id.txt_passLog);
        mIngres = findViewById(R.id.btnIngresar);
        mLossingPass = findViewById(R.id.losspass);
        mRegisterHere = findViewById(R.id.mReg);
        photoImageView = findViewById(R.id.imageView);


        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Login");
        dialog.setMessage("Espere...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);

        //Glide.with(this).load(R.drawable.splash_water).into(photoImageView);
        // mRegisterHere.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        mIngres.setOnClickListener(v -> {
            String gettingPhone = editTextPhone.getText().toString();
            String newString = gettingPhone.replace("(", "").replace(")", "").replace("-", "").replace(" ", "");
            String phone = lada + newString;
            dialog.show();

            String gettingPass = editTextPass.getText().toString();
            if (!TextUtils.isEmpty(gettingPhone) && !TextUtils.isEmpty(gettingPass) && gettingPhone != null && gettingPass != null) {
                final Query userUID = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").orderByChild("phone").equalTo(phone);
                userUID.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {

                            userUID.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {


                                    if (dataSnapshot.child("password").getValue().equals(editTextPass.getText().toString())) {

                                        //Logueo Exitoso
                                        //Guardar UID como variable globar aquiii!!!

                                        SharedPreferences gameSettings = getSharedPreferences(PREFERENCE_FILENAME, MODE_PRIVATE);
                                        SharedPreferences.Editor prefEditor = gameSettings.edit();
                                        //valores agregados
                                        prefEditor.putString("UID", dataSnapshot.getKey());
                                        prefEditor.commit();


                                        //GUARDAMOS EL USERID PARA FUTUROS USOS INTERNOS EN UN SHARED PREFERENCES
                                        SharedPreferences.Editor editor = getSharedPreferences("USERID", MODE_PRIVATE).edit();
                                        editor.putString("key", dataSnapshot.child("id").getValue().toString());
                                        editor.apply();

                                        final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        //  Toast.makeText(MainActivity.this, "Logueo Exitoso", Toast.LENGTH_SHORT).show();jkhdldf


                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                    }
//                                    Toast.makeText(MainActivity.this, ""+dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(MainActivity.this, ""+dataSnapshot.child("password").getValue(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });


                        } else {
                            Toast.makeText(LoginActivity.this, "El telefono no esta registrado", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } else {
                Toast.makeText(LoginActivity.this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        mLossingPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PENDIENTE CORREGIR RECUPERAR CONTRASEÑA
                Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent1);
                System.out.println("CODIFOGOGOGOGOGOGOG"+R.drawable.portada);
                finish();
                return;
            }

        });

        mRegisterHere.setOnClickListener(v -> {
            dialog.show();
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
            alertDialog.setCancelable(false);
            dialog.dismiss();
            alertDialog.setTitle("Registro");
            alertDialog.setMessage("Ingrese su Numero");

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);

            alertDialog.setPositiveButton("Enviar",
                    (dialog1, which) -> {

                        nTel = input.getText().toString();
                        final String newString = nTel.replace("(", "").replace(")", "").replace("-", "").replace(" ", "");
                        phone = lada + newString;
                        final Query telquery = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").orderByChild("phone").equalTo(phone);

                        telquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (newString.length() != 10) {
                                    dialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Ingresa un Telefono Valido.", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (dataSnapshot.getChildrenCount() >= 0) {

                                        Toast.makeText(LoginActivity.this, "Codigo Enviado por SMS.", Toast.LENGTH_SHORT).show();

                                        sendVerificationCode();

                                        dialog.dismiss();

                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Este Telefono aun no se encuentra registrado ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    });

            alertDialog.setNegativeButton("Cancelar",
                    (dialog1, which) -> dialog1.cancel());

            alertDialog.show();

        });


        editTextPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            //we need to know if the user is erasing or inputing some new character
            private boolean backspacingFlag = false;
            //we need to block the :afterTextChanges method to be called again after we just replaced the EditText text
            private boolean editedFlag = false;
            //we need to mark the cursor position and restore it after the edition
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //we store the cursor local relative to the end of the string in the EditText before the edition
                cursorComplement = s.length() - editTextPhone.getSelectionStart();
                //we check if the user ir inputing or erasing a character
                if (count > after) {
                    backspacingFlag = true;
                } else {
                    backspacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // nothing to do here =D
            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                //what matters are the phone digits beneath the mask, so we always work with a raw string with only digits
                String phone = string.replaceAll("[^\\d]", "");

                //if the text was just edited, :afterTextChanged is called another time... so we need to verify the flag of edition
                //if the flag is false, this is a original user-typed entry. so we go on and do some magic
                if (!editedFlag) {

                    //we start verifying the worst case, many characters mask need to be added
                    //example: 999999999 <- 6+ digits already typed
                    // masked: (999) 999-999
                    if (phone.length() >= 8 && !backspacingFlag) {
                        //we will edit. next call on this textWatcher will be ignored
                        editedFlag = true;
                        //here is the core. we substring the raw digits and add the mask as convenient
                        String ans = "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6, 8) + "-" + phone.substring(8);
                        editTextPhone.setText(ans);
                        //we deliver the cursor to its original position relative to the end of the string
                        editTextPhone.setSelection(editTextPhone.getText().length() - cursorComplement);

                        //we end at the most simple case, when just one character mask is needed
                        //example: 99999 <- 3+ digits already typed
                        // masked: (999) 99
                    } else if (phone.length() >= 3 && !backspacingFlag) {
                        editedFlag = true;
                        String ans = "(" + phone.substring(0, 3) + ") " + phone.substring(3);
                        editTextPhone.setText(ans);
                        editTextPhone.setSelection(editTextPhone.getText().length() - cursorComplement);
                    }
                    // We just edited the field, ignoring this cicle of the watcher and getting ready for the next
                } else {
                    editedFlag = false;
                }
            }
        });

        input.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            //we need to know if the user is erasing or inputing some new character
            private boolean backspacingFlag = false;
            //we need to block the :afterTextChanges method to be called again after we just replaced the EditText text
            private boolean editedFlag = false;
            //we need to mark the cursor position and restore it after the edition
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //we store the cursor local relative to the end of the string in the EditText before the edition
                cursorComplement = s.length() - input.getSelectionStart();
                //we check if the user ir inputing or erasing a character
                if (count > after) {
                    backspacingFlag = true;
                } else {
                    backspacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // nothing to do here =D
            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                //what matters are the phone digits beneath the mask, so we always work with a raw string with only digits
                String phone = string.replaceAll("[^\\d]", "");

                //if the text was just edited, :afterTextChanged is called another time... so we need to verify the flag of edition
                //if the flag is false, this is a original user-typed entry. so we go on and do some magic
                if (!editedFlag) {

                    //we start verifying the worst case, many characters mask need to be added
                    //example: 999999999 <- 6+ digits already typed
                    // masked: (999) 999-999
                    if (phone.length() >= 8 && !backspacingFlag) {
                        //we will edit. next call on this textWatcher will be ignored
                        editedFlag = true;
                        //here is the core. we substring the raw digits and add the mask as convenient
                        String ans = "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6, 8) + "-" + phone.substring(8);
                        input.setText(ans);
                        //we deliver the cursor to its original position relative to the end of the string
                        input.setSelection(input.getText().length() - cursorComplement);

                        //we end at the most simple case, when just one character mask is needed
                        //example: 99999 <- 3+ digits already typed
                        // masked: (999) 99
                    } else if (phone.length() >= 3 && !backspacingFlag) {
                        editedFlag = true;
                        String ans = "(" + phone.substring(0, 3) + ") " + phone.substring(3);
                        input.setText(ans);
                        input.setSelection(input.getText().length() - cursorComplement);
                    }
                    // We just edited the field, ignoring this cicle of the watcher and getting ready for the next
                } else {
                    editedFlag = false;
                }
            }
        });

    }

    public void broadcastIntent() {
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    private void verfySingInCode() {
        code = input2.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void sendVerificationCode() {

        phone = lada + nTel;

        if (phone.isEmpty()) {
            input.setError("Numero de Telefono Necesario");
            input.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            input.setError("Ingrese Un Numero Valido");
            input.requestFocus();
            return;
        }


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NotNull PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(),"ERROR CON EL SERVIDOR, NO SE HA PODIDO ENVIAR SU CODIGO, INTENTE MAS TARDE.", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
            Toast.makeText(getApplicationContext(),
                    "Se Ha Enviado Su Codigo Verifique Sus SMS", Toast.LENGTH_LONG).show();
            final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(LoginActivity.this);
            alertDialog2.setCancelable(false);
            alertDialog2.setTitle("Registro");
            alertDialog2.setMessage("Ingrese su Codigo");

            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            input2.setInputType(InputType.TYPE_CLASS_NUMBER);
            input2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            input2.setLayoutParams(lp2);
            alertDialog2.setView(input2);


            alertDialog2.setPositiveButton("Aceptar",
                    (dialog1, which) -> {
                        verfySingInCode();
                        dialog1.cancel();
                    });


            alertDialog2.setNegativeButton("Cancelar",
                    (dialog1, which) -> dialog1.cancel());
            alertDialog2.show();
        }


    };


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        //AQUI OBTIENE OTRO UID DIFERENTE

                        //GUARDAR EL UID DEL USUARIO LOGUEADO

                        /// String UIDtel = mAuth.getCurrentUser().getUid();
                        //  task.getResult().getUser().getEmail();
                        UIDtel = task.getResult().getUser().getUid();
                        SharedPreferences gameSettings = getSharedPreferences(PREFERENCE_FILENAME, MODE_PRIVATE);
                        SharedPreferences.Editor prefEditor = gameSettings.edit();
                        //valores agregados
                        prefEditor.putString("UID", UIDtel);
                        prefEditor.apply();


                        final Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        String dato = input.getText().toString(); //Obtienes el texto del EditText
                        intent.putExtra("dato", dato);
                        startActivity(intent);
                        finish();
                        return;


                        // ...
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getApplicationContext(),
                                    "Codigo De Verificacion Incorrecto, Intente Nuevamente", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }


    public void onclickIngresar(View v) {


    }


}
