package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.poi.ss.usermodel.Workbook;

import com.uas.erp.model.VoucherDetailAss;

public interface VoucherService {
	void saveVoucher(String formStore, String[] gridStore, String caller);

	void updateVoucherById(String formStore, String[] gridStore, String caller);

	void deleteVoucher(int vo_id, String caller);

	void auditVoucher(int vo_id, String caller);

	void resAuditVoucher(int vo_id, String caller);

	void submitVoucher(int vo_id, String caller);

	void resSubmitVoucher(int vo_id, String caller);

	String validVoucher(int id);

	JSONObject copyVoucher(int id);

	void insertBreakVoNumber(String data, String caller);

	String accountVoucher(Integer month, String caller);

	String resAccountVoucher(Integer month, String caller);

	/**
	 * 批量审核
	 * 
	 * @param language
	 * @param employee
	 * @param caller
	 * @param data
	 */
	void vastAudit(String caller, String data);

	/**
	 * 批量取消审核
	 * 
	 * @param language
	 * @param employee
	 * @param caller
	 * @param data
	 */
	void vastUnAudit(String caller, String data);

	boolean ImportExcel(int id, Workbook wbs, String substring, String caller);

	String[] printVoucher(int vo_id, String reportName, String condition, String caller);

	Map<String, Object> getVoucherCount();

	/**
	 * 获取凭证辅助核算
	 * 
	 * @param vo_id
	 * @return
	 */
	List<VoucherDetailAss> findAss(int vo_id);

	/**
	 * 审计期间设置
	 */
	void auditDuring(int year, boolean myear, boolean eyear);

	JSONObject rushRedVoucher(int id);
}
