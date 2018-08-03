package com.perawat.yacob.perawat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class PengkajianAwalActivity5 extends AppCompatActivity {
    private CanvasView canvas = null;
    Button btnRedo,btnUndo,btnSimpan;
    Context context;
    SessionIdPasien sessionIdPasien;
    private String selectedPath;
    public String INSERT_GAMBAR_LUKA_URL = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/insertGambarLuka.php";
    RequestQueue queue;
    AlertDialog dialog;
    AlertDialog.Builder builder;

    TextView textView11;
    ImageView iv;
    LinearLayout linearEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengkajian_awal_activiy5);
        getSupportActionBar().setTitle(Html.fromHtml("<'font color='#ffffff'>Pengkajian Awal</font>"));
        btnRedo = findViewById(R.id.btnRedo);
        btnUndo = findViewById(R.id.btnUndo);
        btnSimpan = findViewById(R.id.btnSimpan);

        textView11 = findViewById(R.id.textView11);
        iv= findViewById(R.id.iv);
        linearEdit= findViewById(R.id.linearEdit);
        context = this;
        queue = Volley.newRequestQueue(this);
        sessionIdPasien = new SessionIdPasien(getApplicationContext());
        builder = new AlertDialog.Builder(this);

        this.canvas = (CanvasView) this.findViewById(R.id.canvas);
        canvas.setPaintStrokeColor(Color.parseColor("#ff0000"));
        canvas.setPaintStrokeWidth(10);
        this.canvas.setMode(CanvasView.Mode.DRAW);
        final Intent x = getIntent();
        if(x.getStringExtra("panduan").equals("view")){
            linearEdit.setVisibility(View.GONE);
            btnSimpan.setText("Kembali");
            getPengkajianAwal(sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_ID_PASIEN));
            textView11.setText("Gambar luka Bakar pasien:");
        }

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvas.undo();
            }
        });
        btnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvas.redo();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(x.getStringExtra("panduan").equals("view")){
                    Intent i = new Intent(PengkajianAwalActivity5.this,DetailPasienActivity.class);
                    startActivity(i);
                }
                else {
                    saveSignature();
                    uploadAssess();
                    insertGambarLuka(sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_ID_PASIEN),
                            "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/bakar/" + sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_ID_PASIEN) + ".jpg");
                }
            }
        });
    }

    public Bitmap saveSignature(){
        Bitmap  bitmap =this.canvas.getBitmap();

        File file = new File("storage/emulated/0/Pictures/"+sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_ID_PASIEN)+".jpeg");

        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap ;
    }

    private void uploadAssess() {
        selectedPath = "storage/emulated/0/Pictures/"+sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_ID_PASIEN)+".jpeg";
        class UploadAssess extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(PengkajianAwalActivity5.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.uploadAssess(selectedPath,getApplicationContext());
                return msg;
            }
        }
        UploadAssess uv = new UploadAssess();
        uv.execute();
    }

    public CanvasView getCanvas(){
        CanvasView.Mode mode = this.canvas.getMode();
        return this.canvas;
    }

    public  void getPengkajianAwal(String id){
        iv.setVisibility(View.VISIBLE);
        String url = "http://rumahsakit.gearhostpreview.com/Rumah_Sakit/selectPengkajianAwal4.php?id_pasien="+id;
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
                                Picasso.with(context)
                                        .load(obj.getString("Gambar_Luka_Bakar"))
                                        .config(Bitmap.Config.RGB_565)
                                        .placeholder(R.drawable.badan)
                                        .error(R.drawable.badan)
                                        .fit()
                                        .centerInside()
                                        .into(iv);
                                //Toast.makeText(context, obj.getString("Gambar_Luka_Bakar"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
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


    public void insertGambarLuka(String Id_Pasien,String Gambar_Luka_Bakar){
        JSONObject objAdd = new JSONObject();
        try {
            JSONArray arrData = new JSONArray();
            JSONObject objDetail = new JSONObject();
            objDetail.put("Id_Pasien",Id_Pasien);
            objDetail.put("Gambar_Luka_Bakar",Gambar_Luka_Bakar);
            arrData.put(objDetail);
            objAdd.put("data",arrData);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, INSERT_GAMBAR_LUKA_URL, objAdd,
                new Response.Listener<JSONObject>() {
                    @Override

                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("OK")) {
                                Intent i = new Intent(PengkajianAwalActivity5.this,DetailPasienActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(PengkajianAwalActivity5.this, "Gagal", Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(PengkajianAwalActivity5.this);
        requestQueue.add(stringRequest);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        builder.setMessage("Harap selesaikan pengisian kajian awal");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//        dialog = builder.show();
    }
}
