package com.uas.erp.service.drp;



/**
 * Created by IntelliJ IDEA.
 * User: USOFTPC30
 * Date: 13-5-21
 * Time: 下午5:00
 * To change this template use File | Settings | File Templates.
 */
public interface RepairreserveService {

    public void saveRepairreserve(String formStore, String gridStore,  String caller);

	public void updateRepairreserveById(String formStore, String gridStore,  String caller);

	public void deleteRepairreserve(int ma_id,  String caller);

	public void auditRepairreserve(int ma_id,  String caller);

	public void resAuditRepairreserve(int ma_id,  String caller);

	public void submitRepairreserve(int ma_id,  String caller);

	public void resSubmitRepairreserve(int ma_id,  String caller);
}
