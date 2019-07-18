package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

public class MatriksNilaiKriteria {
    private String namaKriteria;
    private double jumlah;
    private double pv;

    public MatriksNilaiKriteria(String namaKriteria, double jumlah, double pv) {
        this.namaKriteria = namaKriteria;
        this.jumlah = jumlah;
        this.pv = pv;
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

    public double getPv() {
        return pv;
    }

    public void setPv(double pv) {
        this.pv = pv;
    }

    @NonNull
    @Override
    public String toString() {
        return "MatriksNilaiKriteria{" +
                "namaKriteria='" + namaKriteria + '\'' +
                ", jumlah=" + jumlah +
                ", pv=" + pv +
                '}';
    }
}
