package com.perawat.yacob.perawat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;

public class PemberianObat extends AppCompatActivity {
    Button btnSimpan;
    List<Resep> result = new ArrayList<>();
    DetailResepAdapter dra;
    RecyclerView cardListDetailResep;
    RequestQueue queue;
    Context context;

    String namaobat,dosis,sehari,sekali,totalhari,instruksi,tambahan;
    AlertDialog dialog;
    AlertDialog.Builder builder;
    String INSERT_PEMBERIAN_OBAT = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/insertPemberianObat.php";

    SessionIdPasien sessionIdPasien;
    SessionLogin sessionLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemberian_obat);
        getSupportActionBar().setTitle(Html.fromHtml("<'font color='#ffffff'>Pemberian Obat</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnSimpan = findViewById(R.id.btnSimpan);
        context = this;

        sessionIdPasien = new SessionIdPasien(getApplicationContext());
        sessionLogin = new SessionLogin(getApplicationContext());
        builder = new AlertDialog.Builder(this);
        queue = Volley.newRequestQueue(this);
        cardListDetailResep = findViewById(R.id. cardListDetailResep);
        cardListDetailResep.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardListDetailResep.setLayoutManager(llm);
        final Intent i = getIntent();
        if(i.getStringExtra("panduan").equals("view")){
            btnSimpan.setVisibility(View.GONE);
        }
        else{
            btnSimpan.setVisibility(View.VISIBLE);
        }

        getDetailResep(i.getStringExtra("panduanIdResep"));


        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setMessage("Apakah Anda yakin telah memberikan obat kepada pasien?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    insertPemberianObat(Integer.parseInt(sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_ID_PASIEN)),
                            sessionLogin.getUserDetails().get(SessionLogin.KEY_NIP),
                            i.getStringExtra("panduanIdResep"));
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog = builder.show();
            }
        });

    }

    public void getDetailResep(String id){
        String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectDetailResep.php?id_resep="+id;
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
                        for (int i = 0 ;i< users.length();i++) {
                            try {
                                JSONObject obj = users.getJSONObject(i);
                                namaobat = obj.getString("Nama_Obat");
                                dosis = obj.getString("Strength");
                                sehari = obj.getString("Sehari");
                                sekali = obj.getString("sekali");
                                totalhari = obj.getString("Total_Hari");
                                instruksi = obj.getString("Cara_Pemakaian");
                                tambahan = obj.getString("Instruksi_Tambahan");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            result.add(new Resep(namaobat,dosis,sehari,sekali,totalhari,instruksi,tambahan,"N"));
                            dra = new DetailResepAdapter(context,result);
                            cardListDetailResep.setAdapter(dra);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(req);
    }

    public void insertPemberianObat(int Id_Pasien,String NIK_Perawat,String Id_Resep){
        JSONObject objAdd = new JSONObject();
        try {
            JSONArray arrData = new JSONArray();
            JSONObject objDetail = new JSONObject();
            objDetail.put("Id_Pasien",Id_Pasien);
            objDetail.put("NIK_Perawat",NIK_Perawat);
            objDetail.put("Id_Resep",Id_Resep);
            arrData.put(objDetail);
            objAdd.put("data",arrData);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, INSERT_PEMBERIAN_OBAT, objAdd,
                new Response.Listener<JSONObject>() {
                    @Override

                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("OK")) {
                                Intent i = new Intent(PemberianObat.this, DetailPasienActivity.class);
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(PemberianObat.this, "Gagal", Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(PemberianObat.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(PemberianObat.this, DetailPasienActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(PemberianObat.this, DetailPasienActivity.class);
        startActivity(i);
    }
}
