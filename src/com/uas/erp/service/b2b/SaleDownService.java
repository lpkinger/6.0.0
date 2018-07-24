package com.uas.erp.service.b2b;

import java.util.List;

import com.uas.b2b.model.SaleReply;

public interface SaleDownService {

	void updateSaleDownById(String caller, String formStore, String gridStore);

	void replyAll(int id, String caller);
	
	String printSaleDown(int id, String caller);

	void updateReplyInfo(String data, String caller);

	int turnSale(int id, String caller);

	List<SaleReply> findReplyBySaid(int id);

	String vastReplyInfo(String caller, String data);

}
