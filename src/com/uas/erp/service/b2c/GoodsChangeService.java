package com.uas.erp.service.b2c;

import java.util.List;

import com.uas.api.b2c_erp.seller.model.GoodsSimpleUas;

public interface GoodsChangeService {

	void saveGoodsChange(String formStore, String caller, String param);

	void deleteGoodsChange(int id, String caller);

	void updateGoodsChangeById(String formStore, String caller,
			String param);

	void submitGoodsChange(int id, String caller);

	void resSubmitGoodsChange(int id, String caller);

	void auditGoodsChange(int id, String caller);

	void resAuditGoodsChange(int id, String caller);

	String turnAppropriationOut(int id, String caller);
	
	public List<GoodsSimpleUas> sendData (int id);

}
