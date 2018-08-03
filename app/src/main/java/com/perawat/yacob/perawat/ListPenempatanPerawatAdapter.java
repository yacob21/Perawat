package com.perawat.yacob.perawat;

/**
 * Created by yacob on 3/3/2018.
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;


public class ListPenempatanPerawatAdapter extends RecyclerView.Adapter<ListPenempatanPerawatAdapter.PerawatViewHolder> {
    private List<Perawat> perawatList;
    public int count = 0;
    Context context;
    SessionIdPasien sessionIdPasien;

    public ListPenempatanPerawatAdapter(Context c, List<Perawat> perawatList) {
        this.perawatList=perawatList;
        this.context=c;
    }


     public static int lastSelectedPosition = -1;

    @Override
    public void onBindViewHolder(final PerawatViewHolder perawatViewHolder, final int i) {
        final Perawat pe = perawatList.get(i);
        perawatViewHolder.tvNama.setText(pe.Nama_Perawat);
        perawatViewHolder.tvNik.setText(pe.NIK_Perawat);
        perawatViewHolder.tvJumlahPasien.setText(pe.Jumlah_Pasien);
//        Intent intent = ((Activity) context).getIntent();
//        if(pe.NIK_Perawat.equals(intent.getStringExtra("panduanNik"))){
//            lastSelectedPosition=perawatViewHolder.getLayoutPosition();
//        }
//        else {
//            lastSelectedPosition=-1;
//        }

        perawatViewHolder.rb.setChecked(lastSelectedPosition == i);
    }




    @Override
    public int getItemCount() {
        return perawatList.size();
    }

    @Override
    public ListPenempatanPerawatAdapter.PerawatViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_pilih_perawat, viewGroup, false);

        return new ListPenempatanPerawatAdapter.PerawatViewHolder(itemView);
    }

    public class PerawatViewHolder extends  RecyclerView.ViewHolder {
        protected RadioButton rb;
        protected TextView tvJumlahPasien;
        protected TextView tvNik;
        protected  TextView tvNama;
        protected LinearLayout btnDetail;

        public PerawatViewHolder(View v) {
            super(v);
            rb = v.findViewById(R.id.rb);
            btnDetail = v.findViewById(R.id.btnDetail);
            tvJumlahPasien = v.findViewById(R.id.tvJumlahPasien);
            tvNama = v.findViewById(R.id.tvNama);
            tvNik = v.findViewById(R.id.tvNik);


            btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lastSelectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }


}
