package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.VoucherDetailAss;

public interface VoucherDao {
	/**
	 * 获取凭证号<br>
	 * 包括中间的断号
	 * 
	 * @param currentMonth
	 *            期间
	 * @param lead
	 *            凭证字
	 * @param fromNumber
	 *            开始号
	 * @return
	 */
	String getVoucherNumber(String currentMonth, String lead, Integer fromNumber);

	Map<String, Object> getJustPeriods(String period);

	Map<String, Object> getPeriodsDate(String periods, Integer date);

	int getPeriodsFromDate(String periods, String date);

	int getNowPddetno(String periods);

	int getNowPddetnoByType(String type);

	int getEndPddetno(String periods);

	void validVoucher(int vId);

	String unCreate(String vs_code, String mode, String datas, String vo_code, String vo_source);

	/**
	 * 按ID获取凭证辅助核算
	 * 
	 * @param vo_id
	 * @return
	 */
	List<VoucherDetailAss> getAssByVoucherId(int vo_id);
}
