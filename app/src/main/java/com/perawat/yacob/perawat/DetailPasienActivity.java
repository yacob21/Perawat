package com.perawat.yacob.perawat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

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

public class DetailPasienActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    RequestQueue queue;

    SessionIdPasien sessionIdPasien;
    Button btnPengkajianAwal;
    AlertDialog dialog;
    AlertDialog.Builder builder;

    TextView discharge;
    boolean CEK_PEMULANGAN = false;

    String DELETE_LIST_PERAWAT_URL = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/deleteListPerawat.php";
    int shift;
    FloatingActionButton btnObat,btnTtv,btnPesan;
    FloatingActionsMenu fam;

    RecyclerView cardListTimeline;
    List<Timeline> result = new ArrayList<>();
    listTimelineAdapter lta;

    String tanggal,nik,nama,aktivitas,nikdokter,namadokter,date,gambar,id_resep;
    Context context;
    public static String temp_pasien;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pasien);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        builder = new AlertDialog.Builder(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        queue = Volley.newRequestQueue(this);
        context = this;
        sessionIdPasien = new SessionIdPasien(getApplicationContext());
        final HashMap<String, String> idpasien = sessionIdPasien.getIdPasienDetails();
        getSupportActionBar().setTitle(Html.fromHtml("<'font color='#ffffff'>Detail Pasien</font>"));

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeaderView = navigationView.getHeaderView(0);
        discharge = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_discharge));

        cardListTimeline = findViewById(R.id.cardListTimeline);
        cardListTimeline.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardListTimeline.setLayoutManager(llm);

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
//        cekTtv(idpasien.get(SessionIdPasien.KEY_ID_PASIEN), dateFormat.format(date), shift, dateFormat.format(c.getTime()));
//        cekPemberianObat(idpasien.get(SessionIdPasien.KEY_ID_PASIEN), dateFormat.format(date), shift, dateFormat.format(c.getTime()));
        initializeCountDrawer(idpasien.get(SessionIdPasien.KEY_ID_PASIEN));
        getDataPasien(idpasien.get(SessionIdPasien.KEY_ID_PASIEN));
        cekAwal(idpasien.get(SessionIdPasien.KEY_ID_PASIEN));
        getTimeline(idpasien.get(SessionIdPasien.KEY_ID_PASIEN));



        btnPengkajianAwal = findViewById(R.id.btnPengkajianAwal);
        btnPengkajianAwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailPasienActivity.this, BarcodeActivity.class);
                i.putExtra("panduanIntent", "awal");
                startActivity(i);
            }
        });

        fam = findViewById(R.id.multiple_actions);

        btnObat = findViewById(R.id.btnObat);
        btnObat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getResep(sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_ID_PASIEN));
            }
        });
        btnTtv = findViewById(R.id.btnTtv);
        btnTtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_BARCODE).equals("yes")){
                    Intent i = new Intent(DetailPasienActivity.this, BarcodeActivity.class);
                    i.putExtra("panduanIntent", "ttv");
                    startActivity(i);
                }
                else if(sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_BARCODE).equals("no")){
                    Intent i = new Intent(DetailPasienActivity.this, ActivityTTV.class);
                    i.putExtra("panduan", "insert");
                    startActivity(i);
                }

            }
        });
        btnPesan = findViewById(R.id.btnPesan);
        btnPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailPasienActivity.this, PesanActivity.class);
                i.putExtra("panduanIntent", "pesan");
                startActivity(i);
            }
        });



    }

    @Override
    public void onBackPressed() {
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_image, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        popupWindow.dismiss();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(fam.isExpanded()){
            fam.collapse();
        }
        else{

            startActivity(new Intent(DetailPasienActivity.this,HomeActivity.class));
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_beranda) {

        } else if (id == R.id.nav_resep) {
            Intent i = new Intent (DetailPasienActivity.this,ResepActivity.class);
            startActivity(i);

        }
        else if(id == R.id.nav_hubungi){
            getNamaDokter(sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_ID_PASIEN));
        }
        else if (id == R.id.nav_discharge) {
            pemulangan();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void getDataPasien(String id){
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeaderView = navigationView.getHeaderView(0);
        final TextView tvNama=  navHeaderView.findViewById(R.id.tvNama);
        final TextView tvJenisKelamin= navHeaderView.findViewById(R.id.tvJenisKelamin);
        final TextView tvKamar= navHeaderView.findViewById(R.id.tvKamar);
        final TextView tvTanggalMasuk= navHeaderView.findViewById(R.id.tvTanggalMasuk);
        final TextView tvTanggalLahir= navHeaderView.findViewById(R.id.tvTanggalLahir);
        final TextView tvGolonganDarah=navHeaderView.findViewById(R.id.tvGolonganDarah);
        final TextView tvTinggi= navHeaderView.findViewById(R.id.tvTinggi);
        final TextView tvBerat=  navHeaderView.findViewById(R.id.tvBerat);
        final TextView tvAlergi= navHeaderView.findViewById(R.id.tvAlergi);
        final TextView tvDiagnosa=  navHeaderView.findViewById(R.id.tvDiagnosa);
        final TextView tvDiagnosa2=  navHeaderView.findViewById(R.id.tvDiagnosa2);
        String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectPasienById.php?id_pasien="+id;
        JsonObjectRequest req = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray users = null;
                        try {
                            users = response.getJSONArray("result");
                            //Toast.makeText(context, String.valueOf(users.length()), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONObject obj = null;
                        if(users.length() ==0){
                            Toast.makeText(context, "Tidak ada data pasien", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            try {
                                obj = users.getJSONObject(0);
                                tvNama.setText(obj.getString("Nama_Pasien"));
                                temp_pasien = obj.getString("Nama_Pasien");
                                //Toast.makeText(context, obj.getString("Nama_Pasien"), Toast.LENGTH_SHORT).show();
                                tvJenisKelamin.setText(obj.getString("Jenis_Kelamin"));
                                tvKamar.setText(obj.getString("Nomor_Kamar"));
                                tvTanggalMasuk.setText(obj.getString("Tanggal_Masuk"));
                                tvTanggalLahir.setText(obj.getString("Tanggal_Lahir"));
                                tvGolonganDarah.setText(obj.getString("Golongan_Darah"));
                                tvTinggi.setText(obj.getString("Tinggi") + " cm");
                                tvBerat.setText(obj.getString("Berat") + " Kg");
                                tvAlergi.setText(obj.getString("Alergi"));
                                tvDiagnosa.setText(obj.getString("Diagnosa_1"));
                                tvDiagnosa2.setText(obj.getString("Diagnosa_2"));
                                //tvDiagnosa.setText(obj.getString("Diagnosa_1")+obj.getString("Diagnosa_2")+obj.getString("Diagnosa_3"));
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
    public void pemulangan(){
        if(CEK_PEMULANGAN==true){
            builder.setMessage("Apakah Anda yakin ingin memulangan pasien?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    JSONObject objAdd = new JSONObject();
                    try {
                        JSONArray arrData = new JSONArray();
                        JSONObject objDetail = new JSONObject();
                        objDetail.put("id_pasien",sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_ID_PASIEN));
                        arrData.put(objDetail);
                        objAdd.put("data",arrData);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, DELETE_LIST_PERAWAT_URL, objAdd,
                            new Response.Listener<JSONObject>() {
                                @Override

                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response.getString("status").equals("OK")) {
                                            Intent i = new Intent(DetailPasienActivity.this,HomeActivity.class);
                                            startActivity(i);
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
                    RequestQueue requestQueue = Volley.newRequestQueue(DetailPasienActivity.this);
                    requestQueue.add(stringRequest);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog = builder.show();
        }
        else if(CEK_PEMULANGAN==false){
            Toast.makeText(this, "Tidak dapat melakukan pemulangan tanpa persetujuan Dokter", Toast.LENGTH_SHORT).show();
        }

    }
    public void getNamaDokter(final String id){
        String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectPhoneDokter.php?id="+id;
        JsonObjectRequest req = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray users = null;
                        try {
                            users = response.getJSONArray("result");
                            if(users.length() ==0){
                                Toast.makeText(context, "Tidak ada data pasien", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONObject obj = null;
                        try {
                            obj = users.getJSONObject(0);
                            builder.setMessage("Ingin menghubungi "+obj.getString("Nama_Dokter")+"?");
                            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    callDokter(id);
                                }
                            });

                            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            dialog = builder.show();

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
    public void callDokter(String id){
        String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectPhoneDokter.php?id="+id;
        JsonObjectRequest req = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray users = null;
                        try {
                            users = response.getJSONArray("result");
                            if(users.length() ==0){
                                Toast.makeText(context, "Tidak ada data pasien", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONObject obj = null;
                        try {
                            obj = users.getJSONObject(0);
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+obj.getString("Nomor_Handphone")));
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(req);
    }
    private void initializeCountDrawer(String id){
        discharge.setGravity(Gravity.CENTER_VERTICAL);
        discharge.setTypeface(null, Typeface.BOLD);
        discharge.setTextColor(Color.parseColor("#ff0000"));
        String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectIdPasienPemulangan.php?id_pasien="+id;
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
                        if(users.length() == 0 ){
                            CEK_PEMULANGAN=false;
                        }
                        else{
                            CEK_PEMULANGAN=true;
                            discharge.setText(String.valueOf(users.length()));
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(req);
    }
    private Drawable setBadgeCount(Context context, int res, int badgeCount){
        LayerDrawable icon = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.ic_badge_drawable);
        Drawable mainIcon = ContextCompat.getDrawable(context, res);
        BadgeDrawable badge = new BadgeDrawable(context);
        badge.setCount(String.valueOf(badgeCount));
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
        icon.setDrawableByLayerId(R.id.ic_main_icon, mainIcon);
        return icon;
    }
    public void cekAwal(String id){
        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectCekPengkajianAwal.php?id_pasien="+id;
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
                        JSONObject obj = null;
                        if(users.length() == 0){
                            btnPengkajianAwal.setVisibility(View.VISIBLE);
                            menuMultipleActions.setVisibility(View.GONE);
                        }
                        else {
                            btnPengkajianAwal.setVisibility(View.GONE);
                            menuMultipleActions.setVisibility(View.VISIBLE);
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
//    public void cekTtv(String id,String date,int shift,String date2){
//        if(shift==1){
//            String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectTtv1.php?id_pasien="+id+"&date="+date;
//            JsonObjectRequest req = new JsonObjectRequest(url, null,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            JSONArray users = null;
//                            try {
//                                users = response.getJSONArray("result");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            JSONObject obj = null;
//                            if(users.length()==0){
//                                btnTtv.setVisibility(View.VISIBLE);
//                            }
//                            else{
//                                btnTtv.setVisibility(View.GONE);
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    //Toast.makeText(ListPasien.this,"Terjadi Kendala Koneksi",Toast.LENGTH_LONG ).show();
//                }
//            });
//
//            queue.add(req);
//        }
//        else if(shift ==2){
//            String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectTtv2.php?id_pasien="+id+"&date="+date;
//            JsonObjectRequest req = new JsonObjectRequest(url, null,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            JSONArray users = null;
//                            try {
//                                users = response.getJSONArray("result");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            JSONObject obj = null;
//                            if(users.length()==0){
//                                btnTtv.setVisibility(View.VISIBLE);
//                            }
//                            else{
//                                btnTtv.setVisibility(View.GONE);
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    //Toast.makeText(ListPasien.this,"Terjadi Kendala Koneksi",Toast.LENGTH_LONG ).show();
//                }
//            });
//
//            queue.add(req);
//        }
//        else if(shift ==3){
//            String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectTtv3.php?id_pasien="+id+"&date="+date+"&date2="+date2;
//            JsonObjectRequest req = new JsonObjectRequest(url, null,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            JSONArray users = null;
//                            try {
//                                users = response.getJSONArray("result");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            JSONObject obj = null;
//                            if(users.length()==0){
//                                btnTtv.setVisibility(View.VISIBLE);
//                            }
//                            else{
//                                btnTtv.setVisibility(View.GONE);
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    //Toast.makeText(ListPasien.this,"Terjadi Kendala Koneksi",Toast.LENGTH_LONG ).show();
//                }
//            });
//
//            queue.add(req);
//        }
//    }
//    public void cekPemberianObat(String id,String date,int shift,String date2){
//        if(shift==1){
//            String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectPemberianObat1.php?id_pasien="+id+"&date="+date;
//            JsonObjectRequest req = new JsonObjectRequest(url, null,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            JSONArray users = null;
//                            try {
//                                users = response.getJSONArray("result");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            JSONObject obj = null;
//                            if(users.length()==0){
//                                btnObat.setVisibility(View.VISIBLE);
//                            }
//                            else{
//                                btnObat.setVisibility(View.GONE);
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    //Toast.makeText(ListPasien.this,"Terjadi Kendala Koneksi",Toast.LENGTH_LONG ).show();
//                }
//            });
//
//            queue.add(req);
//        }
//        else if(shift ==2){
//            String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectPemberianObat2.php?id_pasien="+id+"&date="+date;
//            JsonObjectRequest req = new JsonObjectRequest(url, null,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            JSONArray users = null;
//                            try {
//                                users = response.getJSONArray("result");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            JSONObject obj = null;
//                            if(users.length()==0){
//                                btnObat.setVisibility(View.VISIBLE);
//                            }
//                            else{
//                                btnObat.setVisibility(View.GONE);
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    //Toast.makeText(ListPasien.this,"Terjadi Kendala Koneksi",Toast.LENGTH_LONG ).show();
//                }
//            });
//
//            queue.add(req);
//        }
//        else if(shift ==3){
//            String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectPemberianObat3.php?id_pasien="+id+"&date="+date+"&date2="+date2;
//            JsonObjectRequest req = new JsonObjectRequest(url, null,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            JSONArray users = null;
//                            try {
//                                users = response.getJSONArray("result");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            JSONObject obj = null;
//                            if(users.length()==0){
//                                btnObat.setVisibility(View.VISIBLE);
//                            }
//                            else{
//                                btnObat.setVisibility(View.GONE);
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    //Toast.makeText(ListPasien.this,"Terjadi Kendala Koneksi",Toast.LENGTH_LONG ).show();
//                }
//            });
//
//            queue.add(req);
//        }
//    }
    public void getTimeline(String id){
        String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectAktivitasPasien.php?id_pasien="+id;
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
                            cardListTimeline.setVisibility(View.GONE);
                        }
                        else {
                            cardListTimeline.setVisibility(View.VISIBLE);
                            for (int i = 0; i < users.length(); i++) {
                                try {
                                    JSONObject obj = users.getJSONObject(i);
                                    tanggal = obj.getString("Tanggal");
                                    nama = obj.getString("Nama_Perawat");
                                    nik = obj.getString("NIK_Perawat");
                                    aktivitas = obj.getString("Aktivitas");
                                    nikdokter = obj.getString("NIK_Dokter");
                                    namadokter = obj.getString("Nama_Dokter");
                                    date = obj.getJSONObject("Date").getString("date");
                                    gambar = obj.getString("Gambar");
                                    id_resep = obj.getString("Id_Resep");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                result.add(new Timeline(tanggal, aktivitas, nik, nama,nikdokter,namadokter,date,gambar,id_resep));
                                lta = new listTimelineAdapter(context, result);
                                cardListTimeline.setAdapter(lta);
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
    public void getResep(String id){
        String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectResepPasien.php?id_pasien="+id;
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

                            Toast.makeText(context, "Pasien belum memiliki resep", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            try {
                                JSONObject obj = users.getJSONObject(0);
                                if(sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_BARCODE).equals("yes")){
                                    Intent i = new Intent(DetailPasienActivity.this,BarcodeActivity.class);
                                    i.putExtra("panduanIdResep",obj.getString("Id_Resep"));
                                    i.putExtra("panduanIntent", "resep");
                                    startActivity(i);
                                }
                                else if(sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_BARCODE).equals("no")){
                                    Intent x = new Intent(DetailPasienActivity.this, PemberianObat.class);
                                    x.putExtra("panduanIdResep",obj.getString("Id_Resep"));
                                    x.putExtra("panduan", "insert");
                                    startActivity(x);
                                }


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


}
