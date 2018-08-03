package com.perawat.yacob.perawat;

import android.content.Context;
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
import java.util.HashMap;
import java.util.List;

public class PenempatanTugasPerawatActivity extends AppCompatActivity {
    RecyclerView cardListPerawat;
    RequestQueue queue;
    List<Perawat> result = new ArrayList<>();
    ListPenempatanPerawatAdapter lppa;
    SessionLogin sessionLogin;
    Context context;
    String nik_perawat,nama_perawat,jumlah_pasien;
    AlertDialog dialog;
    AlertDialog.Builder builder;
    Button btnSimpan;

    String UPDATE_PENEMPATAN_TUGAS_URL = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/updatePenempatanTugas.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penempatan_tugas_perawat);
        getSupportActionBar().setTitle(Html.fromHtml("<'font color ='#ffffff'>Penempatan Tugas</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        builder = new AlertDialog.Builder(this);
        queue = Volley.newRequestQueue(this);
        context = this;
        btnSimpan = findViewById(R.id.btnSimpan);
        cardListPerawat = findViewById(R.id.cardListPerawat);
        cardListPerawat.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardListPerawat.setLayoutManager(llm);
        sessionLogin = new SessionLogin(getApplicationContext());
        final HashMap<String, String> user = sessionLogin.getUserDetails();
        getPerawatUntukPenempatanTugas(user.get(sessionLogin.KEY_NIP));
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x = getIntent();
                if(ListPenempatanPerawatAdapter.lastSelectedPosition == -1){
                    Toast.makeText(context, "Harap pilih Perawat terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendTugas("Penempatan Tugas","Anda memiliki pasien baru",result.get(ListPenempatanPerawatAdapter.lastSelectedPosition).NIK_Perawat);
                    simpanTugas(x.getStringExtra("panduanIdPasien"),user.get(SessionLogin.KEY_SHIFT));
                }
            }
        });
    }

    public void simpanTugas(String id,String shift){
        JSONObject objAdd = new JSONObject();
        try {
            JSONArray arrData = new JSONArray();
            JSONObject objDetail = new JSONObject();
            objDetail.put("nik",result.get(ListPenempatanPerawatAdapter.lastSelectedPosition).NIK_Perawat);
            objDetail.put("id_pasien",id);
            objDetail.put("shift",shift);
            arrData.put(objDetail);
            objAdd.put("data",arrData);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, UPDATE_PENEMPATAN_TUGAS_URL, objAdd,
                new Response.Listener<JSONObject>() {
                    @Override

                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("OK")) {
                                Toast.makeText(context, "Berhasil update penempatan tugas", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(PenempatanTugasPerawatActivity.this,PenempatanTugasActivity.class);
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(PenempatanTugasPerawatActivity.this, "Gagal menempatkan tugas", Toast.LENGTH_SHORT).show();

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
        RequestQueue requestQueue = Volley.newRequestQueue(PenempatanTugasPerawatActivity.this);
        requestQueue.add(stringRequest);
    }

    public void sendTugas(final String title,final String pesan,String nik){
        String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectTokenPerawat.php?nip="+nik;
        JsonObjectRequest req = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray users = null;
                        try {
                            users = response.getJSONArray("result");
                            JSONObject obj = users.getJSONObject(0);
                            String urlJumlah = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/notifikasi/notifPesanPerawat.php?title="+title+"&pesan="+pesan+"&token="+obj.getString("Token");
                            JsonObjectRequest reqJumlah = new JsonObjectRequest(urlJumlah, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //Toast.makeText(context,"Gagal",Toast.LENGTH_LONG ).show();
                                }
                            });
                            queue.add(reqJumlah);
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
        queue.add(req);
    }


    public void getPerawatUntukPenempatanTugas(String nik){
        String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectPerawatUntukPenempatanTugas.php?nip="+nik+"&shift="+sessionLogin.getUserDetails().get(SessionLogin.KEY_SHIFT);
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
                                nama_perawat = obj.getString("Nama_Perawat");
                                nik_perawat = obj.getString("NIK");
                                jumlah_pasien = obj.getString("Jumlah_Pasien");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            result.add(new Perawat(jumlah_pasien,nik_perawat,nama_perawat));
                            lppa = new ListPenempatanPerawatAdapter(context, result);
                            cardListPerawat.setAdapter(lppa);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(req);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ListPenempatanPerawatAdapter.lastSelectedPosition = -1;
    }
}
