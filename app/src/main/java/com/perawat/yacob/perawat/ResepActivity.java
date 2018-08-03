package com.perawat.yacob.perawat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

public class ResepActivity extends AppCompatActivity {
    TextView tvKosong;
    RequestQueue queue;
    SessionIdPasien sessionIdPasien;
    RecyclerView cardListResep;
    List<Resep> result = new ArrayList<>();
    listResepAdapter lra;
    Context context;
    String tanggal,jumlah,id_resep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resep);
        getSupportActionBar().setTitle(Html.fromHtml("<'font color='#ffffff'>Resep</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        queue = Volley.newRequestQueue(this);
        sessionIdPasien = new SessionIdPasien(getApplicationContext());
        final HashMap<String, String> idpasien = sessionIdPasien.getIdPasienDetails();
        tvKosong  = findViewById(R.id.tvKosong);
        cardListResep = findViewById(R.id.cardListResep);
        cardListResep.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardListResep.setLayoutManager(llm);
        getResep(idpasien.get(sessionIdPasien.KEY_ID_PASIEN));

    }


    public void getResep(String id){
        String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectResepById.php?id_pasien="+id;
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
                        if(users.length() == 0){
                            tvKosong.setVisibility(View.VISIBLE);
                        }
                        else {
                            cardListResep.setVisibility(View.VISIBLE);
                            for (int i = 0; i < users.length(); i++) {
                                try {
                                    JSONObject obj = users.getJSONObject(i);
                                    jumlah = obj.getString("Jumlah");
                                    tanggal = obj.getString("Tanggal_Dibuat");
                                    id_resep = obj.getString("Id_Resep");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                result.add(new Resep(tanggal, jumlah, id_resep));
                                lra = new listResepAdapter(context, result);
                                cardListResep.setAdapter(lra);
                            }
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}