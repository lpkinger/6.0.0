package com.uas.erp.service.drp;

import com.uas.erp.model.DataListCombo;


import java.util.List;

/**
 * 客户报修单 service
 */
public interface AskRepairService {

    public void saveAskRepair(String formStore, String gridStore,  String caller);

	public void updateAskRepair(String formStore, String gridStore,  String caller);

	public  void deleteAskRepair(int id,  String caller);

    public List<DataListCombo> getRepairOrderType(String caller) ;

    public void turnRepairOrder(String caller, int crid, int em_uu, String em_name, String rotype, String crdids);

    public void resAuditAskRepair(int id,  String caller);

    public void auditAskRepair(int id,  String caller);

    public void resSubmitAskRepair(int id,  String caller);

    public void submitAskRepair(int id,  String caller);

	public String batchTurnRepairOrder( String caller,
			 String data);
	public String batchTurnPartCheck(String data,String caller);
	public void confirmCustomerRepair(int id,  String caller);
}

