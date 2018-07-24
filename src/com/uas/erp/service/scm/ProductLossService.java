package com.uas.erp.service.scm;

public interface ProductLossService {
	void saveProductLoss(String formStore, String gridStore, String caller);
	void deleteProductLoss(int pl_id, String caller);
	void updateProductLoss(String formStore,String gridStore, String caller);
}
