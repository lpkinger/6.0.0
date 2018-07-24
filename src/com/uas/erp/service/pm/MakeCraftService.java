package com.uas.erp.service.pm;

import java.util.Map;


public interface MakeCraftService {
	void saveMakeCraft(String formStore, String gridStore, String caller);

	void updateMakeCraftById(String formStore, String gridStore, String caller);

	void deleteMakeCraft(int mc_id, String caller);

	void printMakeCraft(int mc_id, String caller);

	void auditMakeCraft(int mc_id, String caller);

	void resAuditMakeCraft(int mc_id, String caller);

	void submitMakeCraft(int mc_id, String caller);

	void resSubmitMakeCraft(int mc_id, String caller);
	
	void endMakeCraft( String caller,int mc_id);
	
	void forceEndMakeCraft( String caller,int mc_id);

	void resEndMakeCraft(int mc_id, String caller);

	public Map<String, Object> getMakeCraft(String caller);

	Map<String, Object> getWorkCenter(String wc_code);
	
	String vastTurnCraftTransfer(String caller,String data);
	
	String vastTurnCraftJump (String caller,String data);
	
	String vastTurnMadeIN (String caller,String data);
	
	String vastTurnCraftBack (String caller,String data);

	String vastTurnCraftReturn (String caller,String data);
	
	String vastTurnCraftScrap (String caller,String data);
	
	void updateOSVendor(Integer id, String vendcode, String currency, String taxrate, String price, String paymc, String paym,
			String mc_servicer, String remark, String apvendcode, String caller);
	
}
