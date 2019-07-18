package pd.sahang.mas.palembang.smds.ahp.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pd.sahang.mas.palembang.smds.ahp.models.AhpModelHelper;

public class AhpHelper {

    private final ArrayList<AhpModelHelper> ahpModelHelpers;
    private final int jumlahData;
    private int position;
    private double[][] matriksa;
    private double[][] matriksb;
    private double[] jmlmpb;
    private double[] jmlmnk;
    private double[] priorityVektor;

    private double eigenVektor;
    private double consIndex;
    private double consRatio;

    public AhpHelper(ArrayList<AhpModelHelper> ahpModelHelpers, int jumlahData) {
        this.ahpModelHelpers = ahpModelHelpers;
        this.jumlahData = jumlahData;
    }

    private void initAhp() {
        try {
            if (getItemCount() == 0) throw new IndexOutOfBoundsException();
        } catch (IndexOutOfBoundsException ex) {
            Log.e(AhpHelper.class.getSimpleName(), "initAhp: ", ex);
        }
        position = 0;

        matriksa = new double[jumlahData][jumlahData];
        matriksb = new double[jumlahData][jumlahData];
        jmlmpb = new double[jumlahData];
        jmlmnk = new double[jumlahData];
        priorityVektor = new double[jumlahData];
    }

    public int getItemCount() {
        return ahpModelHelpers.size() > 0 ? ahpModelHelpers.size() : 0;
    }

    public void running(boolean start) {
        if (start) {
            initAhp();

            setMapMatriks();

            onCountMatriks();

        } else {
            throw new RuntimeException(AhpHelper.class.getSimpleName() + " required running start true");
        }
    }

    private void setMapMatriks() {
        for (int i = 0; i <= (jumlahData - 2); i++) {
            for (int j = (i+1); j <= (jumlahData - 1); j++) {
                if (ahpModelHelpers.get(position).getValue() == 1) {
                    matriksa[i][j] = ahpModelHelpers.get(position).getKepentingan();
                    matriksa[j][i] = (double) 1 / ahpModelHelpers.get(position).getKepentingan();
                } else {
                    matriksa[i][j] = (double) 1 / ahpModelHelpers.get(position).getKepentingan();
                    matriksa[j][i] = ahpModelHelpers.get(position).getKepentingan();
                }
                position++;
            }
        }
        for (int i = 0; i <= (jumlahData - 1); i++) {
            matriksa[i][i] = 1;
        }
        for (int i = 0; i <= (jumlahData - 1); i++) {
            jmlmnk[i] = 0;
            jmlmpb[i] = 0;
        }
    }

    private void onCountMatriks() {
        for (int i = 0; i <= (jumlahData - 1); i++) {
            for (int j = 0; j <= (jumlahData - 1); j++) {
                double v = matriksa[i][j];
                jmlmpb[j] += v;
            }
        }

        for (int i = 0; i <= (jumlahData - 1); i++) {
            for (int j = 0; j <= (jumlahData - 1); j++) {
                matriksb[i][j] = matriksa[i][j] / jmlmpb[j];
                double v = matriksb[i][j];
                jmlmnk[i] += v;
            }
            priorityVektor[i] = jmlmnk[i] / (double) jumlahData;
        }
        eigenVektor = setOnEigenVector(jmlmpb,jmlmnk,jumlahData);
        consIndex = setOnConsIndex(jmlmpb,jmlmnk,jumlahData);
        consRatio = setOnConsRatio(jmlmpb,jmlmnk,jumlahData);

        setEigenVektor(eigenVektor);
        setConsIndex(consIndex);
        setConsRatio(consRatio);
        setPriorityVektor(priorityVektor);
        setJmlmpb(jmlmpb);
        setJmlmnk(jmlmnk);
    }

    public double[][] getMatriksa() {
        return matriksa;
    }

    public double[][] getMatriksb() {
        return matriksb;
    }

    public double[] getJmlmpb() {
        return jmlmpb;
    }

    private void setJmlmpb(double[] jmlmpb) {
        this.jmlmpb = jmlmpb;
    }

    public double[] getJmlmnk() {
        return jmlmnk;
    }

    private void setJmlmnk(double[] jmlmnk) {
        this.jmlmnk = jmlmnk;
    }

    private double getNilaiIR(final int c) {
        double nilai = 0;
        HashMap<Object, Object> ir = new HashMap<>();
        ir.put(1,0);
        ir.put(2,0);
        ir.put(3,0.58);
        ir.put(4,0.9);
        ir.put(5,1.12);
        ir.put(6,1.24);
        ir.put(7,1.32);
        ir.put(8,1.41);
        ir.put(9,1.45);
        ir.put(10,1.49);
        ir.put(11,1.51);
        ir.put(12,1.48);
        ir.put(13,1.56);
        ir.put(14,1.57);
        ir.put(15,1.59);

        for (Map.Entry n : ir.entrySet()) {
            if (n.getKey().equals(c)) {
                nilai = (double) n.getValue();
            }
        }
        return nilai;
    }

    private double setOnEigenVector(double[] a, double[] b, int c) {
        double ev = 0;
        for (int i = 0; i <= (c - 1); i++) {
            ev += (a[i] * ((b[i]) / c));
        }
        return ev;
    }

    private double setOnConsIndex(double[] a, double[] b, int c) {
        double ev = setOnEigenVector(a, b, c);
        return (ev - c) / (c - 1);
    }

    private double setOnConsRatio(double[] a, double[] b, int c) {
        double ci = setOnConsIndex(a,b,c);
        return ci / getNilaiIR(c);
    }

    public double getEigenVektor() {
        return eigenVektor;
    }

    private void setEigenVektor(double eigenVektor) {
        this.eigenVektor = eigenVektor;
    }

    public double getConsIndex() {
        return consIndex;
    }

    private void setConsIndex(double consIndex) {
        this.consIndex = consIndex;
    }

    public double getConsRatio() {
        return consRatio;
    }

    private void setConsRatio(double consRatio) {
        this.consRatio = consRatio;
    }

    public double[] getPriorityVektor() {
        return priorityVektor;
    }

    private void setPriorityVektor(double[] priorityVektor) {
        this.priorityVektor = priorityVektor;
    }
}
