package com.uas.erp.service.scm;

public interface BarStockLossService {

	void saveLoss(String formStore, String param, String caller);

	void deleteLoss(int id, String caller);

	void updateLoss(String formStore, String param, String caller);

	void auditLoss(int id, String caller);

	void resAduitLoss(int id, String caller);

	void submitLoss(int id, String caller);

	void resSubmitLoss(int id, String caller);

}
