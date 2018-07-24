package com.uas.erp.service.b2c;

import java.util.List;

import com.uas.api.b2c_erp.seller.model.GoodsSimpleUas;

public interface GoodsUpApplicationService {

	void saveGoodsUpApplication(String formStore, String caller, String param);

	void deleteGoodsUpApplication(int id, String caller);

	void updateGoodsUpApplicationById(String formStore, String caller,
			String param);

	void submitGoodsUpApplication(int id, String caller);

	void resSubmitGoodsUpApplication(int id, String caller);

	void auditGoodsUpApplication(int id, String caller);

	void resAuditGoodsUpApplication(int id, String caller);

	void splitDetail(String formdata, String data);

	String turnAppropriationOut(int id, String caller);

	String getUUId(int id, String caller);

	String goodsUp(int id, String caller);

	void upToB2C(int id);

	List<GoodsSimpleUas> sendData(int id);
}
