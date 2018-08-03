package com.perawat.yacob.perawat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;


public class ListPasienAdapter extends RecyclerView.Adapter<ListPasienAdapter.PasienViewHolder> {
    private List<Pasien> pasienList;
    public int count = 0;
    Context context;
    SessionIdPasien sessionIdPasien;
    SessionLogin sessionLogin;
    RequestQueue queue;



    public ListPasienAdapter(Context c, List<Pasien> pasienList) {
        this.pasienList=pasienList;
        this.context=c;
    }


    @Override
    public void onBindViewHolder(final PasienViewHolder pasienViewHolder, final int i) {

        final Pasien pi = pasienList.get(i);
        sessionLogin = new SessionLogin(context);
        queue = Volley.newRequestQueue(context);
        HashMap<String, String> user = sessionLogin.getUserDetails();
        pasienViewHolder.tvTanggal.setText(pi.Tanggal_Masuk);
        pasienViewHolder.tvNama.setText(pi.Nama_Pasien);
        pasienViewHolder.tvKamar.setText(pi.Nomor_Kamar);
        //Toast.makeText(context, pi.Obat + pi.Awal + pi.Ttv, Toast.LENGTH_SHORT).show();
//
            if (user.get(sessionLogin.KEY_KEPALA).equals("N")) {
                if (pi.Obat.equals("0") || pi.Ttv.equals("0") || pi.Awal.equals("0")) {
                    pasienViewHolder.ivDown.setImageResource(R.drawable.merah);
                } else if (!pi.Obat.equals("0") && !pi.Ttv.equals("0") && !pi.Awal.equals("0")) {
                    pasienViewHolder.ivDown.setImageResource(R.drawable.ic_chevron_right_black_36dp);
                }
            } else {
                if (pi.NIK_Perawat.equals("null")) {
                    pasienViewHolder.ivDown.setImageResource(R.drawable.tindakan_belum_ico);
                } else if ((pi.Obat.equals("0") || pi.Ttv.equals("0") || pi.Awal.equals("0")) && !pi.NIK_Perawat.equals("null")) {
                    pasienViewHolder.ivDown.setImageResource(R.drawable.merah);
                } else if (!pi.Obat.equals("0") && !pi.Ttv.equals("0") && !pi.Awal.equals("0") && !pi.NIK_Perawat.equals("null")) {
                    pasienViewHolder.ivDown.setImageResource(R.drawable.ic_chevron_right_black_36dp);
                }

            }


            pasienViewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, String> user = sessionLogin.getUserDetails();
                    if (user.get(sessionLogin.KEY_KEPALA).equals("N")) {
                        sessionIdPasien = new SessionIdPasien(view.getContext());
                        sessionIdPasien.createIdPasienSession(pi.Id_Pasien,"yes");
                        Intent i = new Intent(view.getContext(), DetailPasienActivity.class);
                        view.getContext().startActivity(i);
                    } else {
                        if (pi.NIK_Perawat.equals("null")) {
                            Intent i = new Intent(view.getContext(), PenempatanTugasPerawatActivity.class);
                            i.putExtra("panduanIdPasien", pi.Id_Pasien);
                            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            view.getContext().startActivity(i);
                        } else {
                            sessionIdPasien = new SessionIdPasien(view.getContext());
                            sessionIdPasien.createIdPasienSession(pi.Id_Pasien,"yes");
                            Intent i = new Intent(view.getContext(), DetailPasienActivity.class);
                            i.putExtra("panduan", "barcode");
                            view.getContext().startActivity(i);
                        }
                    }


                }

            });
    }


    @Override
    public int getItemCount() {
        return pasienList.size();
    }

    @Override
    public ListPasienAdapter.PasienViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_pasien, viewGroup, false);

        return new ListPasienAdapter.PasienViewHolder(itemView);
    }

    public static class PasienViewHolder extends  RecyclerView.ViewHolder {
        protected TextView tvTanggal;
        protected TextView tvNama;
        protected TextView tvKamar;
        protected LinearLayout btnDetail;
        protected ImageView ivDown;


        public PasienViewHolder(View v) {
            super(v);
            ivDown = v.findViewById(R.id.ivDown);
            tvTanggal = v.findViewById(R.id.tvTanggal);
            tvNama = v.findViewById(R.id.tvNama);
            tvKamar = v.findViewById(R.id.tvKamar);
            btnDetail =  v.findViewById(R.id.btnDetail);
        }
    }




}