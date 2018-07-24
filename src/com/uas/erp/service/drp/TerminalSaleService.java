package com.uas.erp.service.drp;



/**
 * Created by IntelliJ IDEA.
 * User: USOFTPC30
 * Date: 13-5-28
 * Time: 下午4:01
 * To change this template use File | Settings | File Templates.
 */
public interface TerminalSaleService {

    void saveTerminalSale(String formStore, String gridStore,  String caller);

	void updateTerminalSaleById(String formStore, String gridStore,  String caller);

	void deleteTerminalSale(int ts_id,  String caller);

	void auditTerminalSale(int ts_id,  String caller);

	void resAuditTerminalSale(int ts_id,  String caller);

	void submitTerminalSale(int ts_id,  String caller);

	void resSubmitTerminalSale(int ts_id,  String caller);

}
