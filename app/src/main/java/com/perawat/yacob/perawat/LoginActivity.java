package com.perawat.yacob.perawat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    SessionLogin sessionLogin;
    RequestQueue queue;
    ProgressBar pb;
    AlertDialog dialog;
    AlertDialog.Builder builder;
    Button btnReset;

    public String LOGIN_DOKTER_URL = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/loginPerawat.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        builder = new AlertDialog.Builder(this);
        queue = Volley.newRequestQueue(this);
        //mAuth = FirebaseAuth.getInstance();
        sessionLogin = new SessionLogin(getBaseContext());
        mLoginFormView = findViewById(R.id.login_form);
        mEmailView =  findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        pb = findViewById(R.id.login_progress);
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin(mEmailView.getText().toString(),mPasswordView.getText().toString());
            }
        });
        btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
            }
        });
    }


    public void doLogin(final String nik,final String password){
        JSONObject objAdd = new JSONObject();
        try {
            JSONArray arrData = new JSONArray();
            JSONObject objDetail = new JSONObject();
            objDetail.put("nik",nik);
            objDetail.put("password",password);
            arrData.put(objDetail);
            objAdd.put("data",arrData);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_DOKTER_URL, objAdd,
                new Response.Listener<JSONObject>() {
                    @Override

                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("OK")) {
                                getDataPerawat(nik);

                            }
                            else{
                                Toast.makeText(LoginActivity.this, "NIK atau Kata Sandi yang Anda masukkan Salah", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) ;
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);

}


    public void getDataPerawat(final String nik){
        String urlPerawat = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectPerawatByNik.php?nip="+nik;
        JsonObjectRequest reqPerawat = new JsonObjectRequest(urlPerawat, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray users = null;
                        try {
                            users = response.getJSONArray("result");
                            JSONObject obj = users.getJSONObject(0);
                            //Toast.makeText(LoginActivity.this, obj.getString("Kepala_Perawat"), Toast.LENGTH_SHORT).show();
                            if(obj.getString("Kepala_Perawat").equals("null")){
                                if(!obj.getString("Jumlah_Pasien_Shift_1").equals("0") && obj.getString("Jumlah_Pasien_Shift_2").equals("0") && obj.getString("Jumlah_Pasien_Shift_3").equals("0") ){
                                    //Toast.makeText(LoginActivity.this, nik+"Y", Toast.LENGTH_SHORT).show();
                                    sessionLogin.createLoginSession(nik,"Y","1");
                                    Intent i = new Intent(LoginActivity.this,HomeActivity.class);
                                    startActivity(i);
                                }
                                else if(obj.getString("Jumlah_Pasien_Shift_1").equals("0") && !obj.getString("Jumlah_Pasien_Shift_2").equals("0") && obj.getString("Jumlah_Pasien_Shift_3").equals("0") ){

                                    sessionLogin.createLoginSession(nik,"Y","2");
                                    Intent i = new Intent(LoginActivity.this,HomeActivity.class);
                                    startActivity(i);
                                }
                                else if(obj.getString("Jumlah_Pasien_Shift_1").equals("0") && obj.getString("Jumlah_Pasien_Shift_2").equals("0") && !obj.getString("Jumlah_Pasien_Shift_3").equals("0") ){

                                    sessionLogin.createLoginSession(nik,"Y","3");
                                    Intent i = new Intent(LoginActivity.this,HomeActivity.class);
                                    startActivity(i);
                                }
                            }
                            else{
                                sessionLogin.createLoginSession(nik,"N","0");
                                Intent i = new Intent(LoginActivity.this,HomeActivity.class);
                                startActivity(i);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ListPasien.this,"Terjadi Kendala Koneksi",Toast.LENGTH_LONG ).show();
            }
        });

        queue.add(reqPerawat);
    }


    @Override
    protected void onStart() {
        super.onStart();
        sessionLogin.checkLogin2();
    }

    @Override
    public void onBackPressed() {
        builder.setMessage("Ingin keluar dari aplikasi ?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                return;
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog = builder.show();
    }
}
