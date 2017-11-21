/**
 * Created by Michael on 21/11/2017.
 */

package com.michael.apps.agriculturerepository.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.michael.apps.agriculturerepository.model.Config;
import com.michael.apps.agriculturerepository.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private int SELECT_PICTURE = 1;

    private ImageView imageView;
    private Bitmap bitmap;
    private Uri photoPath;

    private EditText editTextNama;
    private EditText editTextTanggalLahir;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextEmail;

    Calendar myCalendar = Calendar.getInstance();

    private RadioGroup radioJK;
    private RadioButton radioLaki;
    private RadioButton radioPerempuan;

    private Button buttonFoto;
    private Button buttonRegister;

    private String nama;
    private String jenisKelamin;
    private String tanggalLahir;
    private String username;
    private String password;
    private String konfirmasiPassword;
    private String email;
    private String foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar mactionBar = getSupportActionBar();
        mactionBar.setSubtitle("Agriculture Repository");
        mactionBar.setDisplayHomeAsUpEnabled(true);

        editTextNama = (EditText) findViewById(R.id.editTextNama);
        editTextTanggalLahir = (EditText) findViewById(R.id.editTextTanggalLahir);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        editTextEmail= (EditText) findViewById(R.id.editTextEmail);


        editTextTanggalLahir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        radioJK = (RadioGroup) findViewById(R.id.radioJK);
        radioLaki = (RadioButton) findViewById(R.id.radioLaki);
        radioPerempuan = (RadioButton) findViewById(R.id.radioPerempuan);

        imageView = (ImageView) findViewById(R.id.imageFoto);

        buttonFoto = (Button) findViewById(R.id.buttonFoto);
        buttonFoto.setOnClickListener(this);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(this);
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editTextTanggalLahir.setText(sdf.format(myCalendar.getTime()));
    }

    private void registerUser() {
        nama = editTextNama.getText().toString().trim();
        username = editTextUsername.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        email = editTextEmail.getText().toString().trim();
        tanggalLahir = editTextTanggalLahir.getText().toString().trim();
        jenisKelamin = ((RadioButton)findViewById(radioJK.getCheckedRadioButtonId())).getText().toString().trim();

        Toast.makeText(RegisterActivity.this, nama, Toast.LENGTH_LONG).show();
        Toast.makeText(RegisterActivity.this, jenisKelamin, Toast.LENGTH_LONG).show();
        Toast.makeText(RegisterActivity.this, tanggalLahir, Toast.LENGTH_LONG).show();

        if(bitmap != null) {
            foto = getStringImage(bitmap);
        }
        else {
            foto = "";
        }

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Mendaftar...", "Mohon tunggu...", true, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        response = response.replaceFirst(" ","");
                        response = response.trim();

                        //Showing toast message of the response
                        Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();

                        switch (response) {
                            case "Registrasi berhasil": {
                                showAlertSuccess();
                                break;
                            }
                            case "Username sudah digunakan": {
                                showAlertUsername();
                                break;
                            }
                            case "Email sudah pernah didaftarkan": {
                                showAlertEmail();
                                break;
                            }
                            default:
                                showAlertFailed();
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        showAlertInfo();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Map<String,String> params = new HashMap<String, String>();
                params.put(Config.KEY_NAME, nama);
                params.put(Config.KEY_GENDER, jenisKelamin);
                params.put(Config.KEY_DATE_OF_BIRTH, tanggalLahir);
                params.put(Config.KEY_USERNAME, username);
                params.put(Config.KEY_PASSWORD, password);
                params.put(Config.KEY_EMAIL, email);

                //Adding parameters
                params.put(Config.KEY_IMAGE, foto);

                //returning parameters
                return params;
            }

        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void showFileChooser() {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String pickTitle = "Pilih atau ambil foto profil";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                new Intent[]{takePhotoIntent}
        );

        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Sports Hi");
        imagesFolder.mkdir();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File image = new File(imagesFolder, "Foto_" + timeStamp +".jpg");
        photoPath = Uri.fromFile(image);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Adding our menu to toolbar
        getMenuInflater().inflate(R.menu.menu_config, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_refresh).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent intentB = new Intent(getApplicationContext(), AboutActivity.class);
                intentB.putExtra("about", true);
                startActivity(intentB);
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showAlertInfo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
        alertDialog.setTitle("Gagal");
        alertDialog.setMessage("Silahkan cek koneksi internet anda");
        alertDialog.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertSuccess() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
        alertDialog.setTitle("Registrasi Berhasil");
        alertDialog.setMessage("Silahkan cek email anda.");
        alertDialog.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog.show();
    }

    public void showAlertUsername() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
        alertDialog.setTitle("Registrasi Gagal");
        alertDialog.setMessage("Username sudah digunakan");
        alertDialog.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertEmail() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
        alertDialog.setTitle("Registrasi Gagal");
        alertDialog.setMessage("Email sudah pernah didaftarkan");
        alertDialog.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showAlertFailed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
        alertDialog.setTitle("Registrasi Gagal");
        alertDialog.setMessage("Ukuran foto profil terlalu besar. Silahkan kecilkan ukuran gambar terlebih dahulu, kemudian coba kembali.");
        alertDialog.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void onClick(View v) {
        if(v == buttonFoto){
            showFileChooser();
        }
        if(v == buttonRegister){
            nama = editTextNama.getText().toString();
            if(nama.length() == 0) {
                editTextNama.setError("Nama diperlukan!");
            }

            tanggalLahir = editTextTanggalLahir.getText().toString();
            if(tanggalLahir.length() == 0) {
                editTextTanggalLahir.setError("Tanggal lahir diperlukan!");
            }

            username = editTextUsername.getText().toString();
            if(username.length() == 0) {
                editTextUsername.setError("Username diperlukan!");
            }

            password = editTextPassword.getText().toString();
            if(password.length() == 0) {
                editTextPassword.setError("Password diperlukan!");
            }
            if(password.length() <= 3) {
                editTextPassword.setError("Password terlalu pendek!");
            }

            konfirmasiPassword = editTextConfirmPassword.getText().toString();
            if(konfirmasiPassword.length() == 0) {
                editTextConfirmPassword.setError("Konfirmasi password diperlukan!");
            }

            if(!konfirmasiPassword.equals(password)) {
                editTextConfirmPassword.setError("Konfirmasi password tidak sesuai!");
            }

            email = editTextEmail.getText().toString();
            if(email.length() == 0) {
                editTextEmail.setError("Email diperlukan!");
            }
            else {
                if (!isValidEmail(email)) {
                    editTextEmail.setError("Email tidak valid!");
                }
            }

            if( (nama.length() != 0) && (tanggalLahir.length() != 0) && (username.length() != 0) && (password.length() > 3) && (konfirmasiPassword.equals(password)) && (isValidEmail(email))) {
                registerUser();
            }
        }
    }
}
