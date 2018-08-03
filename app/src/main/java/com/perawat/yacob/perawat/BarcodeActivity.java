package com.perawat.yacob.perawat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class BarcodeActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    SessionIdPasien sessionIdPasien;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZBarScannerView(this);
        setContentView(mScannerView);
        sessionIdPasien = new SessionIdPasien(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.v("Hasil Scanner", rawResult.getContents()); // Prints scan results
        Log.v("Hasil Scanner_RAW", rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)
        Intent i = getIntent();
        if(i.getStringExtra("panduanIntent").equals("darurat") && !rawResult.getContents().toString().matches("[a-zA-Z ]+")){
            sessionIdPasien.createIdPasienSession(rawResult.getContents(),"no");
            Intent y = new Intent(BarcodeActivity.this, DetailPasienActivity.class);
            startActivity(y);
        }
        else {
            if (rawResult.getContents().equals(sessionIdPasien.getIdPasienDetails().get(SessionIdPasien.KEY_ID_PASIEN))) {
                if (i.getStringExtra("panduanIntent").equals("ttv")) {
                    Intent x = new Intent(BarcodeActivity.this, ActivityTTV.class);
                    x.putExtra("panduan", "insert");
                    startActivity(x);
                } else if (i.getStringExtra("panduanIntent").equals("awal")) {
                    Intent x = new Intent(BarcodeActivity.this, PengkajianAwalActivity.class);
                    x.putExtra("panduan", "insert");
                    startActivity(x);
                } else if (i.getStringExtra("panduanIntent").equals("resep")) {
                    Intent x = new Intent(BarcodeActivity.this, PemberianObat.class);
                    x.putExtra("panduanIdResep", i.getStringExtra("panduanIdResep"));
                    x.putExtra("panduan", "insert");
                    startActivity(x);
                }
            } else {
                Toast.makeText(this, "Scan tidak sesuai", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

//        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
    }
}
