package com.android.laporan.oop;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class item implements Serializable {

    @Expose
    String nip,tanggal,jam,hari,id_kegiatan,kendala,nama_kegiatan,
            status,tanggal_penanganan,solusi,gambar,last_update;

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String getTanggal_penanganan() {
        return tanggal_penanganan;
    }

    public void setTanggal_penanganan(String tanggal_penanganan) {
        this.tanggal_penanganan = tanggal_penanganan;
    }

    public String getSolusi() {
        return solusi;
    }

    public void setSolusi(String solusi) {
        this.solusi = solusi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKendala() {
        return kendala;
    }

    public String getNama_kegiatan() {
        return nama_kegiatan;
    }

    public void setNama_kegiatan(String nama_kegiatan) {
        this.nama_kegiatan = nama_kegiatan;
    }

    public void setKendala(String kendala) {
        this.kendala = kendala;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getId_kegiatan() {
        return id_kegiatan;
    }

    public void setId_kegiatan(String id_kegiatan) {
        this.id_kegiatan = id_kegiatan;
    }
}
