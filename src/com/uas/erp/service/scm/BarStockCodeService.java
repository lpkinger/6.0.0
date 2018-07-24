package com.uas.erp.service.scm;

public interface BarStockCodeService {

	void batchGenBarcode(String caller, int id,String data);

	void saveBarcode(String caller, String gridStore);

	void deleteAllBarDetails(String caller, String no,String bddids);

	void batchGenBO(String caller, String formStore);


}
