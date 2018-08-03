package com.perawat.yacob.perawat;

/**
 * Created by yacob on 3/3/2018.
 */

public class Perawat {
    protected String Jumlah_Pasien;
    protected String NIK_Perawat;
    protected String Nama_Perawat;

    public Perawat() {

    }

    public Perawat(String jumlah_Pasien,String nik_Perawat,String nama_Perawat) {
        this.Jumlah_Pasien = jumlah_Pasien;
        this.NIK_Perawat=nik_Perawat;
        this.Nama_Perawat=nama_Perawat;
    }

    public String getJumlah_Pasien() {
        return Jumlah_Pasien;
    }

    public void setJumlah_Pasien(String jumlah_Pasien) {
        Jumlah_Pasien = jumlah_Pasien;
    }

    public String getNIK_Perawat() {
        return NIK_Perawat;
    }

    public void setNIK_Perawat(String NIK_Perawat) {
        this.NIK_Perawat = NIK_Perawat;
    }

    public String getNama_Perawat() {
        return Nama_Perawat;
    }

    public void setNama_Perawat(String nama_Perawat) {
        Nama_Perawat = nama_Perawat;
    }
}
