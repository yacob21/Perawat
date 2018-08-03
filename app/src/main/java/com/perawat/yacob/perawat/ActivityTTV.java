package com.perawat.yacob.perawat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ActivityTTV extends AppCompatActivity {
    EditText etTekananDarah,etDenyutNadi,etLajuPernapasan,etSuhuTubuh;
    Button btnSimpan;
    String INSERT_TTV_URL = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/insertTtv.php";
    RequestQueue queue;
    Context context;
    SessionIdPasien sessionIdPasien;
    SessionLogin sessionLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttv);
        getSupportActionBar().setTitle(Html.fromHtml("<'font color='#ffffff'>Tanda-tanda Vital</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        queue =  Volley.newRequestQueue(this);
        sessionIdPasien = new SessionIdPasien(getApplicationContext());
        sessionLogin = new SessionLogin(getApplicationContext());
        context = this;
        etTekananDarah = findViewById(R.id.etTekananDarah);
        etDenyutNadi = findViewById(R.id.etDenyutNadi);
        etLajuPernapasan = findViewById(R.id.etLajuPernapasan);
        etSuhuTubuh = findViewById(R.id.etSuhuTubuh);
        btnSimpan = findViewById(R.id.btnSimpan);

        Intent i = getIntent();
        if(i.getStringExtra("panduan").equals("view")){
            btnSimpan.setVisibility(View.GONE);
            etSuhuTubuh.setKeyListener(null);
            etTekananDarah.setKeyListener(null);
            etDenyutNadi.setKeyListener(null);
            etLajuPernapasan.setKeyListener(null);
            getTtv(sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_ID_PASIEN),i.getStringExtra("aktivitas"),i.getStringExtra("tanggal"));
        }
        else{
            btnSimpan.setVisibility(View.VISIBLE);
        }


        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    insertTtv(Integer.parseInt(sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_ID_PASIEN)),sessionLogin.getUserDetails().get(SessionLogin.KEY_NIP),
                            etTekananDarah.getText().toString(),etSuhuTubuh.getText().toString().replace(",","."),etDenyutNadi.getText().toString(),
                            etLajuPernapasan.getText().toString());
            }
        });
    }

    public void insertTtv(int Id_Pasien,String NIK_Perawat,String Tekanan_Darah,String Suhu,String Denyut_Nadi,String Laju_Pernapasan){
        JSONObject objAdd = new JSONObject();
        try {
            JSONArray arrData = new JSONArray();
            JSONObject objDetail = new JSONObject();
            objDetail.put("Id_Pasien",Id_Pasien);
            objDetail.put("NIK_Perawat",NIK_Perawat);
            objDetail.put("Tekanan_Darah",Tekanan_Darah);
            objDetail.put("Suhu",Suhu);
            objDetail.put("Denyut_Nadi",Denyut_Nadi);
            objDetail.put("Laju_Pernapasan",Laju_Pernapasan);
            arrData.put(objDetail);
            objAdd.put("data",arrData);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, INSERT_TTV_URL, objAdd,
                new Response.Listener<JSONObject>() {
                    @Override

                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("OK")) {
                                    Intent i = new Intent(ActivityTTV.this, DetailPasienActivity.class);
                                    //i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    startActivity(i);
                            }
                            else{
                                Toast.makeText(ActivityTTV.this, "Gagal", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                }) ;
        RequestQueue requestQueue = Volley.newRequestQueue(ActivityTTV.this);
        requestQueue.add(stringRequest);
    }
    public void getTtv(String id,String aktivitas,String tanggal){
        String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectDetailAktivitas.php?id_pasien="+id+"&aktivitas="+aktivitas
                +"&tanggal="+tanggal;
        JsonObjectRequest req = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray users = null;
                        try {
                            users = response.getJSONArray("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < users.length(); i++) {

                            try {
                                JSONObject obj = users.getJSONObject(i);
                                etDenyutNadi.setText(obj.getString("Denyut_Nadi"));
                                etLajuPernapasan.setText(obj.getString("Laju_Pernapasan"));
                                etSuhuTubuh.setText(obj.getString("Suhu")+"\u00b0C");
                                etTekananDarah.setText(obj.getString("Tekanan_Darah")+" MmHg");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ListPasien.this,"Terjadi Kendala Koneksi",Toast.LENGTH_LONG ).show();
            }
        });

        queue.add(req);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(ActivityTTV.this, DetailPasienActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(ActivityTTV.this, DetailPasienActivity.class);
        startActivity(i);
    }
}
