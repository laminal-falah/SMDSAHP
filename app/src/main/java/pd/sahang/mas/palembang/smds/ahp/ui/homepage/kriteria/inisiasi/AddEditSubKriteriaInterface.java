package pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.inisiasi;

import pd.sahang.mas.palembang.smds.ahp.models.SubKriteria;

public interface AddEditSubKriteriaInterface {
    void onAuthenticationFirebase();
    void onInitFirestore();
    void onSatuan();
    void setSubKriteria(SubKriteria subKriteria);
    boolean validate();
    void onResetForm();
}
