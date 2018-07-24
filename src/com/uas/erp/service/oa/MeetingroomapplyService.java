package com.uas.erp.service.oa;

import java.text.ParseException;


public interface MeetingroomapplyService {
	void saveMeetingroomapply(String formStore, String gridStore,String  caller) throws ParseException ;
	void deleteMeetingroomapply(int ma_id, String  caller);
	void updateMeetingroomapply(String formStore,String gridStore, String  caller) throws ParseException;
	void submitMeetingroomapply(int ma_id, String  caller);
	void resSubmitMeetingroomapply(int ma_id, String  caller);
	void auditMeetingroomapply(int ma_id, String  caller);
	void resAuditMeetingroomapply(int ma_id, String  caller);
	void confirmMan(String gridStore,String  caller);
	String turnDoc(int ma_id,String  caller);
	void reLoad(int ma_id,String caller);
	void cancel(int ma_id,String caller);
}
