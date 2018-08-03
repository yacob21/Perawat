package com.perawat.yacob.perawat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    RecyclerView cardListPasien;
    RequestQueue queue;

    List<Pasien> resultINAP = new ArrayList<>();
    List<Pasien> resultICU = new ArrayList<>();
    List<Pasien> result = new ArrayList<>();
    ListPasienAdapter lpa;
    SessionLogin sessionLogin;
    //sessionToken sessionToken;
    Context context;

    ImageView ivTotal,ivInap,ivIcu;
    TextView tvTotal,tvInap,tvIcu,tvJudul;

    String id_pasien,nama_pasien,tanggal_masuk,jenis_kamar,nomor_kamar,nik_perawat,awal,obat,ttv;
    AlertDialog dialog;
    AlertDialog.Builder builder;
    int shift =0;

    public String UPDATE_TOKEN_URL = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/updateTokenPerawat.php";
    public String UPDATE_NULL_TOKEN_URL = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/updateNullTokenPerawat.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        builder = new AlertDialog.Builder(this);
        queue = Volley.newRequestQueue(this);
        context = this;

        cardListPasien = findViewById(R.id.cardListPasien);
        cardListPasien.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardListPasien.setLayoutManager(llm);

        ivIcu = findViewById(R.id.ivIcu);
        ivInap = findViewById(R.id.ivInap);
        ivTotal = findViewById(R.id.ivTotal);
        tvIcu = findViewById(R.id.tvIcu);
        tvInap = findViewById(R.id.tvInap);
        tvTotal = findViewById(R.id.tvTotal);
        tvJudul = findViewById(R.id.tvJudul);
        sessionLogin = new SessionLogin(getApplicationContext());

        HashMap<String, String> user = sessionLogin.getUserDetails();
        sessionLogin.checkLogin();
        getJumlahPasien(user.get(SessionLogin.KEY_NIP), user.get(SessionLogin.KEY_KEPALA));

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat jam = new SimpleDateFormat("HH");
        DateFormat menit = new SimpleDateFormat("mm");
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);

        if (Integer.parseInt(jam.format(date)) > 07 && Integer.parseInt(jam.format(date)) < 15) {
            if (Integer.parseInt(jam.format(date)) == 15 && Integer.parseInt(menit.format(date)) >= 00) {
                shift = 2;
            } else if (Integer.parseInt(jam.format(date)) == 07 && Integer.parseInt(menit.format(date)) >= 00) {
                shift = 1;
            } else {
                shift = 1;
            }
        } else if (Integer.parseInt(jam.format(date)) >= 15 && Integer.parseInt(jam.format(date)) <= 21) {
            if (Integer.parseInt(jam.format(date)) == 15 && Integer.parseInt(menit.format(date)) >= 00) {
                shift = 2;
            } else if (Integer.parseInt(jam.format(date)) == 21 && Integer.parseInt(menit.format(date)) >= 00) {
                shift = 3;
            } else {
                shift = 2;
            }
        } else if (Integer.parseInt(jam.format(date)) >= 21 || Integer.parseInt(jam.format(date)) <= 07) {
            if (Integer.parseInt(jam.format(date)) == 21 && Integer.parseInt(menit.format(date)) >= 00) {
                shift = 3;
            } else if (Integer.parseInt(jam.format(date)) == 07 && Integer.parseInt(menit.format(date)) >= 00) {
                shift = 1;
            } else {
                shift = 3;
            }
        }



        getListPasien(user.get(SessionLogin.KEY_KEPALA), user.get(SessionLogin.KEY_NIP),shift,dateFormat.format(date),dateFormat.format(c.getTime()));
        ivTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivTotal.setImageResource(R.drawable.total_b);
                ivIcu.setImageResource(R.drawable.icu_a);
                ivInap.setImageResource(R.drawable.inap_a);
                tvJudul.setText("PASIEN");
                lpa = new ListPasienAdapter(context, result);
                cardListPasien.setAdapter(lpa);
            }
        });

        ivInap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultINAP.clear();
                for (int i = 0; i < result.size(); i++) {
                    if (result.get(i).getJenis_Kamar().toLowerCase().trim().equals("inap")) {
                        resultINAP.add(result.get(i));
                    }
                }
                ivTotal.setImageResource(R.drawable.total_a);
                ivIcu.setImageResource(R.drawable.icu_a);
                ivInap.setImageResource(R.drawable.inap_b);
                tvJudul.setText("INAP");
                lpa = new ListPasienAdapter(context, resultINAP);
                cardListPasien.setAdapter(lpa);
                cardListPasien.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            }
        });

        ivIcu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultICU.clear();
                for (int i = 0; i < result.size(); i++) {
                    if (result.get(i).getJenis_Kamar().toLowerCase().trim().equals("icu")) {
                        resultICU.add(result.get(i));
                    }
                }
                ivTotal.setImageResource(R.drawable.total_a);
                ivIcu.setImageResource(R.drawable.icu_b);
                ivInap.setImageResource(R.drawable.inap_a);
                tvJudul.setText("ICU");
                lpa = new ListPasienAdapter(context, resultICU);
                cardListPasien.setAdapter(lpa);
                cardListPasien.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            }
        });


    }

    public void getJumlahPasien(String nik,String key){
        if(isKepala(key) == false){
            String urlJumlah = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectJumlahPasienPerawat.php?nip="+nik;
            JsonObjectRequest reqJumlah = new JsonObjectRequest(urlJumlah, null,
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
                                    tvInap.setText(obj.getString("Jumlah_Inap"));
                                    tvIcu.setText(obj.getString("Jumlah_Icu"));
                                    tvTotal.setText(obj.getString("Jumlah_Total"));
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

            queue.add(reqJumlah);
        }
        else{
            String urlJumlah = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectJumlahPasienKepalaPerawat.php?nip="+nik;
            JsonObjectRequest reqJumlah = new JsonObjectRequest(urlJumlah, null,
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
                                    tvInap.setText(obj.getString("Jumlah_Inap"));
                                    tvIcu.setText(obj.getString("Jumlah_Icu"));
                                    tvTotal.setText(obj.getString("Jumlah_Total"));
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

            queue.add(reqJumlah);
        }



    }

    public void getListPasien(String key,String nik,int shift,String date,String date2){
        if(isKepala(key) == false){
            String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectPasienPerawat.php?nip="+nik+"&shift="+shift+"&date="+date+"&date2="+date2;
            Log.v("URL:",url);
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
                                    awal = obj.getString("Awal");
                                    ttv = obj.getString("TTV");
                                    obat = obj.getString("Obat");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                result.add(new Pasien(id_pasien, nama_pasien, tanggal_masuk, jenis_kamar, nomor_kamar,awal,obat,ttv));
                                lpa = new ListPasienAdapter(context, result);
                                cardListPasien.setAdapter(lpa);
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
        else if(isKepala(key) == true){
            String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectPasienKepalaPerawat.php?nip="+nik+"&shift="+shift+"&date="+date+"&date2="+date2;
            Log.v("URL:",url);
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
                            HashMap<String, String> user = sessionLogin.getUserDetails();
                            for (int i = 0; i < users.length(); i++) {

                                try {
                                    JSONObject obj = users.getJSONObject(i);
                                    id_pasien = obj.getString("Id_Pasien");
                                    nama_pasien = obj.getString("Nama_Pasien");
                                    tanggal_masuk = obj.getString("Tanggal_Masuk");
                                    jenis_kamar = obj.getString("Jenis_Kamar");
                                    nomor_kamar = obj.getString("Nomor_Kamar");
                                    if(user.get(SessionLogin.KEY_SHIFT).equals("1")){
                                        nik_perawat = obj.getString("NIK_Perawat_Shift_1");
                                    }
                                    else if(user.get(SessionLogin.KEY_SHIFT).equals("2")){
                                        nik_perawat = obj.getString("NIK_Perawat_Shift_2");
                                    }
                                    else if(user.get(SessionLogin.KEY_SHIFT).equals("3")){
                                        nik_perawat = obj.getString("NIK_Perawat_Shift_3");
                                    }
                                    awal = obj.getString("Awal");
                                    ttv = obj.getString("TTV");
                                    obat = obj.getString("Obat");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                result.add(new Pasien(id_pasien, nama_pasien, tanggal_masuk, jenis_kamar, nomor_kamar,nik_perawat,awal,obat,ttv));
                                lpa = new ListPasienAdapter(context, result);
                                cardListPasien.setAdapter(lpa);
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
    }

    public boolean isKepala(String key){
        if(key.equals("N")){
            return false;
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        sessionLogin.checkLogin();
        updateToken(sessionLogin.getUserDetails().get(SessionLogin.KEY_NIP),FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        sessionLogin = new SessionLogin(getApplicationContext());
        HashMap<String, String> user = sessionLogin.getUserDetails();
        if(user.get(sessionLogin.KEY_KEPALA).equals("N")){
            MenuItem item = menu.findItem(R.id.btnPenempatanTugas);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btnProfil:
                Intent x = new Intent(HomeActivity.this,ProfilActivity.class);
                //x.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(x);
                return true;
            case R.id.btnPasienDarurat:
                Intent z = new Intent(HomeActivity.this,BarcodeActivity.class);
                z.putExtra("panduanIntent","darurat");
                startActivity(z);
                return true;
            case R.id.btnPenempatanTugas:
                Intent i = new Intent(HomeActivity.this,PenempatanTugasActivity.class);
                startActivity(i);
                return true;
            case R.id.btnKeluar:
                    updateNullToken(sessionLogin.getUserDetails().get(SessionLogin.KEY_NIP));
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    public void updateToken(String nik,String token){
        JSONObject objAdd = new JSONObject();
        try {
            JSONArray arrData = new JSONArray();
            JSONObject objDetail = new JSONObject();
            objDetail.put("nik",nik);
            objDetail.put("token",token);
            arrData.put(objDetail);
            objAdd.put("data",arrData);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, UPDATE_TOKEN_URL, objAdd,
                new Response.Listener<JSONObject>() {
                    @Override

                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("OK")) {

                            }
                            else{

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
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
        requestQueue.add(stringRequest);
    }

    public void updateNullToken(String nik){
        JSONObject objAdd = new JSONObject();
        try {
            JSONArray arrData = new JSONArray();
            JSONObject objDetail = new JSONObject();
            objDetail.put("nik",nik);
            arrData.put(objDetail);
            objAdd.put("data",arrData);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, UPDATE_NULL_TOKEN_URL, objAdd,
                new Response.Listener<JSONObject>() {
                    @Override

                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("OK")) {
                                sessionLogin.logoutUser();
                            }
                            else{

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
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
        requestQueue.add(stringRequest);
    }

}
