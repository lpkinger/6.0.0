package com.uas.erp.service.pm;

public interface OtherExplistService {

	void saveOtherExplist(String formStore, String gridStore, String caller);

	void updateOtherExplistById(String formStore, String gridStore, String caller);

	void deleteOtherExplist(int ma_id, String caller);

	void auditOtherExplist(int ma_id, String caller);

	void resAuditOtherExplist(int ma_id, String caller);

	void submitOtherExplist(int ma_id, String caller);

	void resSubmitOtherExplist(int ma_id, String caller);

	void endOtherExplist(int ma_id, String caller);

	void resEndOtherExplist(int ma_id, String caller);

	void printOtherExplist(int ma_id, String caller);

	void updateOtherExplistDetail(String formStore, String gridStore, String caller);
	
	void updateOtherExplistInfo(int ma_id, String vecode , String currency,String param, String caller);
}
