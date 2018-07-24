package com.uas.erp.service.crm;



public interface GiacceptanceService {
		void saveGiacceptance(String formStore, String gridStore,String caller);
		
		void updateGiacceptanceById(String formStore, String gridStore,String caller);
		
		void deleteGiacceptance(int ga_id,String caller);
		
		void auditGiacceptance(int ga_id,String caller);
		
		void resAuditGiacceptance(int ga_id,String caller);
		
		void submitGiacceptance(int ga_id,String caller);
		
		void resSubmitGiacceptance(int ga_id,String caller);
		
		void turnOainstorage(String formdata,String griddata,String caller);

}
