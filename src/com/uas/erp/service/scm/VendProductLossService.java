package com.uas.erp.service.scm;
/**
 * 
 * @author zjh
 *
 */
public interface VendProductLossService {
	void saveVendProductLoss(String formStore, String caller);
	void deleteVendProductLoss(int vpl_id, String caller);
	void updateVendProductLoss(String formStore, String caller);
}
