package com.perawat.yacob.perawat;

/**
 * Created by yacob on 3/2/2018.
 */


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class ListPenempatanPasienAdapter extends RecyclerView.Adapter<ListPenempatanPasienAdapter.PasienViewHolder> {
    private List<Pasien> pasienList;
    public int count = 0;
    Context context;
    SessionIdPasien sessionIdPasien;

    public ListPenempatanPasienAdapter(Context c, List<Pasien> pasienList) {
        this.pasienList=pasienList;
        this.context=c;
    }


    @Override
    public void onBindViewHolder(final PasienViewHolder pasienViewHolder, final int i) {

        final Pasien pi = pasienList.get(i);
        pasienViewHolder.tvTanggal.setText(pi.Tanggal_Masuk);
        pasienViewHolder.tvNama.setText(pi.Nama_Pasien);
        pasienViewHolder.tvKamar.setText(pi.Nomor_Kamar);
        if (pi.NIK_Perawat.equals("null")){
            pasienViewHolder.ivEdit.setImageResource(R.drawable.tindakan_belum_ico);
            pasienViewHolder.bawah.setVisibility(View.GONE);
        }
        else{
            pasienViewHolder.ivEdit.setImageResource(R.drawable.ic_edit_black_24dp);
            pasienViewHolder.bawah.setVisibility(View.VISIBLE);
            pasienViewHolder.tvNikPerawat.setText(pi.NIK_Perawat);
            pasienViewHolder.tvNamaPerawat.setText(pi.Nama_Perawat);
        }



        pasienViewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(view.getContext(), PenempatanTugasPerawatActivity.class);
                if(!pi.NIK_Perawat.equals("null")){
                    i.putExtra("panduanNik",pi.NIK_Perawat);
                    i.putExtra("panduanIdPasien",pi.Id_Pasien);
//                    i.putExtra("panduan","update");
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                }
                else{
                    i.putExtra("panduanIdPasien",pi.Id_Pasien);
//                    i.putExtra("panduan","insert");
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                }
                view.getContext().startActivity(i);
            }
        });


    }


    @Override
    public int getItemCount() {
        return pasienList.size();
    }

    @Override
    public ListPenempatanPasienAdapter.PasienViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_penempatan, viewGroup, false);

        return new ListPenempatanPasienAdapter.PasienViewHolder(itemView);
    }

    public static class PasienViewHolder extends  RecyclerView.ViewHolder {
        protected TextView tvTanggal;
        protected TextView tvNama;
        protected TextView tvKamar;
        protected LinearLayout btnDetail;
        protected LinearLayout bawah;
        protected ImageView ivEdit;
        protected TextView tvNikPerawat;
        protected  TextView tvNamaPerawat;

        public PasienViewHolder(View v) {
            super(v);
            ivEdit = v.findViewById(R.id.ivEdit);
            tvTanggal = (TextView) v.findViewById(R.id.tvTanggal);
            tvNama = (TextView) v.findViewById(R.id.tvNama);
            tvKamar = (TextView) v.findViewById(R.id.tvKamar);
            btnDetail = (LinearLayout) v.findViewById(R.id.btnDetail);
            bawah = v.findViewById(R.id.bawah);
            tvNamaPerawat = v.findViewById(R.id.tvNamaPerawat);
            tvNikPerawat = v.findViewById(R.id.tvNikPerawat);
        }
    }
}