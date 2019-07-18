package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

public class MatriksPerbandinganBerpasangan {
    private String namaKriteria;
    private double jumlah;

    public MatriksPerbandinganBerpasangan(String namaKriteria, double jumlah) {
        this.namaKriteria = namaKriteria;
        this.jumlah = jumlah;
    }

    public String getNamaKriteria() {
        return namaKriteria;
    }

    public void setNamaKriteria(String namaKriteria) {
        this.namaKriteria = namaKriteria;
    }

    public double getJumlah() {
        return jumlah;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    @NonNull
    @Override
    public String toString() {
        return "MatriksPerbandinganBerpasangan{" +
                "namaKriteria='" + namaKriteria + '\'' +
                ", jumlah=" + jumlah +
                '}';
    }
}
