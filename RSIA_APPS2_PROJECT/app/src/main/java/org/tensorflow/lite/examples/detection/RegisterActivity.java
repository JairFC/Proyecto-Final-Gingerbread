package org.tensorflow.lite.examples.detection;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;



public class RegisterActivity extends AppCompatActivity {

    private static final int IMAGE_INTENT = 33;
    private static final int ACCES_STORAGE_PERMISSION_REQUEST = 103;
    private static final String PREFERENCE_FILENAME = "";
    private EditText nName, nLastName, nEmail, nPassword, n2Password, nTelephone;
    private Button nRegistration;
    private TextView myTextView;
    private FirebaseAuth nAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ProgressBar progressBar;
    Integer counter = 1;
    private String Uid;
    private CharSequence abc = "ABCDEFGHIJKLMNOPQRSTUVWYXZ0123456789";
    private ImageView imgVwProfilePic;
    private Uri picUri = null;
    private StorageReference mStorageReference;
    private File compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nName = findViewById(R.id.Rnombre);
        nLastName = findViewById(R.id.Rapellido_pat);
        nEmail = findViewById(R.id.Remail);
        nPassword = findViewById(R.id.Rpassword);
        n2Password = findViewById(R.id.R2password);
        nRegistration = findViewById(R.id.Rregistration);
        nTelephone = findViewById(R.id.Rtel);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        imgVwProfilePic = findViewById(R.id.imgVwProfilePic);
        imgVwProfilePic.setImageResource(R.drawable.camera);
        imgVwProfilePic.setOnClickListener(v -> {
            openGalery();
        });
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(1000);

        //GENERAMOS EL ID DEL USUARIO
        String user_id = "";
        for (int x = 0; x <= 5; x++) {
            user_id = user_id.concat(abc.charAt(generateRandomNumber()) + "");
        }

        //Recepcion de datos.
        Bundle parametros = this.getIntent().getExtras();
        if (parametros != null) {
            String dato = parametros.getString("dato");
            nTelephone.setText(dato);
        }

        SharedPreferences gameSettings = getSharedPreferences(PREFERENCE_FILENAME, MODE_PRIVATE);
        Uid = gameSettings.getString("UID", "x");


        nName = findViewById(R.id.Rnombre);
        nLastName = findViewById(R.id.Rapellido_pat);
        nEmail = findViewById(R.id.Remail);
        nPassword = findViewById(R.id.Rpassword);
        n2Password = findViewById(R.id.R2password);
        nRegistration = findViewById(R.id.Rregistration);
        nTelephone = findViewById(R.id.Rtel);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(1000);


        nAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = firebaseAuth -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
//                    Intent intent = new Intent(RegisterActivity.this,  CustomerMapActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivityForResult(intent,100);
//                    finish();
//                    return;
            }
        };


        String finalUser_id = user_id;
        nRegistration.setOnClickListener(v -> {
            final String nombre = nName.getText().toString().trim();
            final String apepat = nLastName.getText().toString().trim();


            final String email = nEmail.getText().toString().trim();

            final String password = nPassword.getText().toString().trim();
            final String password2 = n2Password.getText().toString().trim();


            final String telsincod = nTelephone.getText().toString().trim();
            final String ccp = "+52";
            String newString = telsincod.replace("(", "").replace(")", "").replace("-", "").replace(" ", "");
            final String telfinal = ccp + newString.trim();


            if (picUri != null) {
                File file = new File(FileUtils.getRealPath(getApplicationContext(), picUri));
                compressedImageFile = null;
                try {
                    compressedImageFile = new Compressor(this).compressToFile(file);
                    StorageReference riversRef = mStorageReference.child("picProfiles/" + finalUser_id);
                    riversRef.putFile(Uri.parse(compressedImageFile.toURI().toString()))
                            .addOnSuccessListener(taskSnapshot -> {
                                // Get a URL to the uploaded content

                                //here
                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;
                                Uri downloadUrl = urlTask.getResult();

                                final String downloadUri = String.valueOf(downloadUrl);

                                if (downloadUri != null) {
                                    Log.d("URL DOWNLOAD", downloadUri);
                                    //ENVIAMOS LA DATA DEL NUEVO USUARIO

                                    nRegistration.setVisibility(View.INVISIBLE);
                                    counter = 1;
                                    progressBar.setVisibility(View.VISIBLE);
                                    progressBar.setProgress(0);
                                    switch (v.getId()) {
                                        case R.id.btnIngresar:
                                            new MyAsyncTask().execute(10);
                                            break;
                                    }

                                    final Query telquery = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").orderByChild("phone").equalTo(telfinal);

                                    telquery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && email != null && password != null && !TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(apepat) && nombre != null && apepat != null && !TextUtils.isEmpty(telsincod) && telsincod != null) {
                                                if (password.equals(password2)) {
                                                    if (telsincod.length() < 15) {
                                                        progressBar.setVisibility(View.GONE);
                                                        nRegistration.setVisibility(View.VISIBLE);
                                                        Toast.makeText(RegisterActivity.this, "Ingresa un Telefono Valido.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        if (dataSnapshot.getChildrenCount() > 0) {
                                                            progressBar.setVisibility(View.GONE);
                                                            nRegistration.setVisibility(View.VISIBLE);
                                                            Toast.makeText(RegisterActivity.this, "El Telefono que Ingresaste ya se Encuentra Registrado.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(finalUser_id);
                                                            progressBar.setVisibility(View.INVISIBLE);

                                                            Map newPost = new HashMap();

                                                            newPost.put("name", nombre);
                                                            newPost.put("lastname", apepat);
                                                            newPost.put("phone", telfinal);
                                                            newPost.put("email", email);
                                                            newPost.put("password", password);
                                                            newPost.put("id", finalUser_id);
                                                            newPost.put("picUrl", downloadUri);


                                                            current_user_db.setValue(newPost);
                                                            Toast.makeText(RegisterActivity.this, "REGISTRO EXITOSO", Toast.LENGTH_LONG).show();

                                                            //GUARDAMOS EL USERID PARA FUTUROS USOS INTERNOS EN UN SHARED PREFERENCES
                                                            SharedPreferences.Editor editor = getSharedPreferences("USERID", MODE_PRIVATE).edit();
                                                            editor.putString("key", finalUser_id);
                                                            editor.apply();

                                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                            return;


                                                        }
                                                    }

                                                }
                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                                nRegistration.setVisibility(View.VISIBLE);
                                                Toast.makeText(RegisterActivity.this, "Debes Llenar Todos los Campos.", Toast.LENGTH_SHORT).show();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            })
                            .addOnProgressListener(taskSnapshot -> {
                            })
                            .addOnFailureListener(exception -> Log.d("ERROR SUBIENDO", ""));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error image"+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                final Query telquery = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").orderByChild("phone").equalTo(telfinal);

                telquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && email != null && password != null && !TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(apepat) && nombre != null && apepat != null && !TextUtils.isEmpty(telsincod) && telsincod != null) {
                            if (password.equals(password2)) {
                                if (telsincod.length() < 15) {
                                    progressBar.setVisibility(View.GONE);
                                    nRegistration.setVisibility(View.VISIBLE);
                                    Toast.makeText(RegisterActivity.this, "Ingresa un Telefono Valido.", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        progressBar.setVisibility(View.GONE);
                                        nRegistration.setVisibility(View.VISIBLE);
                                        Toast.makeText(RegisterActivity.this, "El Telefono que Ingresaste ya se Encuentra Registrado.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(finalUser_id);
                                        progressBar.setVisibility(View.INVISIBLE);

                                        Map newPost = new HashMap();

                                        newPost.put("name", nombre);
                                        newPost.put("lastname", apepat);
                                        newPost.put("phone", telfinal);
                                        newPost.put("email", email);
                                        newPost.put("password", password);
                                        newPost.put("id", finalUser_id);

                                        current_user_db.setValue(newPost);
                                        Toast.makeText(RegisterActivity.this, "REGISTRO EXITOSO", Toast.LENGTH_LONG).show();

                                        //GUARDAMOS EL USERID PARA FUTUROS USOS INTERNOS EN UN SHARED PREFERENCES
                                        SharedPreferences.Editor editor = getSharedPreferences("USERID", MODE_PRIVATE).edit();
                                        editor.putString("key", finalUser_id);
                                        editor.apply();

                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;


                                    }
                                }

                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            nRegistration.setVisibility(View.VISIBLE);
                            Toast.makeText(RegisterActivity.this, "Debes Llenar Todos los Campos.", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void openGalery() {

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
            Log.d("PERMISOS", "PERMISO GARANTIZADO");
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("image/*");
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            startActivityForResult(photoPickerIntent, IMAGE_INTENT);
        } else {
            Log.d("PERMISOS", "PERMISO NO GARANTIZADO");
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permisos")
                            .setMessage("Es necesario conceder el permiso de lectura")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                                intent.setData(uri);
                                this.startActivity(intent);
                            })
                            .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            })
                            .show();
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            ACCES_STORAGE_PERMISSION_REQUEST);
                }
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        nAuth.removeAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        nAuth.removeAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onBackPressed() {

        Intent intent2 = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent2);
        super.onBackPressed();
    }

    class MyAsyncTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            for (; counter <= params[0]; counter++) {
                try {
                    Thread.sleep(1000);
                    publishProgress(counter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Tarea completa!. =)";
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            progressBar.setProgress(values[0]);
        }


    }

    public int generateRandomNumber() {
        return (int) (Math.random() * 36);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_INTENT) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri imageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        imgVwProfilePic.setImageBitmap(bitmap);
                        picUri = imageUri;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        }
    }
}
