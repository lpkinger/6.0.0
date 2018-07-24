package com.uas.erp.service.fa.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.fa.CarryGlService;

@Service
public class CarryGlServiceImpl implements CarryGlService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	public String create(String yearmonth, String ca_code, Boolean account) {
		Object[] vo = baseDao.getFieldsDataByCondition("Voucher", "vo_id,vo_code", "vo_yearmonth=" + yearmonth
				+ " AND vo_explanation='结转损益'");
		if (vo != null && vo[1] != null) {
			return "已存在转损益凭证，请先删除凭证!" + "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + vo[0]
					+ "&gridCondition=vd_voidIS" + vo[0] + "')\">" + vo[1] + "</a>";
		}
		Employee employee = SystemSession.getUser();
		String res = baseDao.callProcedure("FA_CARRYGL", new Object[] { yearmonth, ca_code, employee.getEm_id(), employee.getEm_name() });
		if (res != null && res.length() > 0) {
			return res;
		}
		vo = baseDao.getFieldsDataByCondition("Voucher", "vo_id,vo_code", "vo_yearmonth=" + yearmonth + " AND vo_explanation='结转损益'");
		if (vo != null && vo[1] != null) {
			String codeStr = "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + vo[0]
					+ "&gridCondition=vd_voidIS" + vo[0] + "')\">" + vo[1] + "</a>";
			voucherDao.validVoucher(Integer.parseInt(String.valueOf(vo[0])));
			String error = baseDao.getJdbcTemplate().queryForObject("select vo_errstring from Voucher Where vo_id=?", String.class, vo[0]);
			if (error != null && error.trim().length() > 0) {
				return "产生的凭证有问题，请打开凭证查看!<br>" + codeStr;
			} else {
				if (account) {
					baseDao.updateByCondition("Voucher", "vo_status='" + BaseUtil.getLocalMessage("AUDITED")
							+ "',vo_statuscode='AUDITED',vo_checkby='" + employee.getEm_name() + "'", "vo_id=" + vo[0]);
					baseDao.callProcedure("SP_WriteVoucher", new Object[] { yearmonth });
					return "已成功产生结转损益凭证并记账!<br>" + codeStr;
				} else {
					return "结转损益成功,凭证号:" + codeStr;
				}
			}
		}
		return "未产生结转损益凭证!";
	}

}
