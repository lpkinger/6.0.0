package com.uas.erp.service.hr;


/**
 * Created by IntelliJ IDEA. User: USOFTPC30 Date: 13-6-17 Time: 上午9:25 To
 * change this template use File | Settings | File Templates.
 */
public interface WageStandardService {

	public void saveWageStandard(String formStore, String gridStore,
			String  caller);

	public void updateWageStandardById(String formStore, String gridStore,
			String  caller);

	public void deleteWageStandard(int ws_id, String  caller);

	public void setEmpWageStandard(int wsid, String condition,
			String caller);

	public void submitWageStandard(int ws_id, String caller);

	public void resSubmitWageStandard(int ws_id, String caller);

	public void auditWageStandard(int ws_id, String  caller);

	public void resAuditWageStandard(int ws_id, String caller);
	public void payAccount(Integer param, String  caller);

}
