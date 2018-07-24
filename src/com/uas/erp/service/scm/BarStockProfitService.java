package com.uas.erp.service.scm;

public interface BarStockProfitService {

	void saveProfit(String formStore, String param, String caller);

	void deleteProfit(int id, String caller);

	void updateProfit(String formStore, String param, String caller);

	void auditProfit(int id, String caller);

	void resAduitProfit(int id, String caller);

	void submitProfit(int id, String caller);

	void resSubmitProfit(int id, String caller);

	void batchGenBarcode(int id, String caller);



}
