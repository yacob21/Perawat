package com.perawat.yacob.perawat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;

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

public class PenempatanTugasActivity extends AppCompatActivity {
    RecyclerView cardListPasien;
    RequestQueue queue;
    List<Pasien> result = new ArrayList<>();
    ListPenempatanPasienAdapter lppa;
    SessionLogin sessionLogin;
    Context context;
    String id_pasien,nama_pasien,tanggal_masuk,jenis_kamar,nomor_kamar,nik_perawat,nama_perawat;
    AlertDialog dialog;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penempatan_tugas);
        getSupportActionBar().setTitle(Html.fromHtml("<'font color ='#ffffff'>Penempatan Tugas</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        builder = new AlertDialog.Builder(this);
        queue = Volley.newRequestQueue(this);
        context = this;
        cardListPasien = findViewById(R.id.cardListPasien);
        cardListPasien.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardListPasien.setLayoutManager(llm);
        sessionLogin = new SessionLogin(getApplicationContext());
        final HashMap<String, String> user = sessionLogin.getUserDetails();
        getPasienKepalaPerawat(user.get(SessionLogin.KEY_NIP),user.get(SessionLogin.KEY_SHIFT));
    }

    public void getPasienKepalaPerawat(String nik,final String shift){
        String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectPasienKepalaPerawat.php?nip="+nik+"&shift="+shift+"&date=&date2=";
        Log.v("urlnya",url);
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
                                id_pasien = obj.getString("Id_Pasien");
                                nama_pasien = obj.getString("Nama_Pasien");
                                tanggal_masuk = obj.getString("Tanggal_Masuk");
                                jenis_kamar = obj.getString("Jenis_Kamar");
                                nomor_kamar = obj.getString("Nomor_Kamar");
                                if(shift.equals("1")){
                                    nama_perawat = obj.getString("Nama_Perawat_Shift_1");
                                    nik_perawat = obj.getString("NIK_Perawat_Shift_1");
                                }
                                else if(shift.equals("2")){
                                    nama_perawat = obj.getString("Nama_Perawat_Shift_2");
                                    nik_perawat = obj.getString("NIK_Perawat_Shift_2");
                                }
                                else if(shift.equals("3")){
                                    nama_perawat = obj.getString("Nama_Perawat_Shift_3");
                                    nik_perawat = obj.getString("NIK_Perawat_Shift_3");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            result.add(new Pasien(id_pasien, nama_pasien, tanggal_masuk, jenis_kamar, nomor_kamar,nik_perawat,nama_perawat));
                            lppa = new ListPenempatanPasienAdapter(context, result);
                            cardListPasien.setAdapter(lppa);
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
                startActivity(new Intent(PenempatanTugasActivity.this,HomeActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PenempatanTugasActivity.this,HomeActivity.class));
    }
}
