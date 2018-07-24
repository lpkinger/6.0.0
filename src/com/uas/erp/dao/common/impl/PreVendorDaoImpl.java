package com.uas.erp.dao.common.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.PreVendorDao;
import com.uas.erp.model.Key;

@Repository
public class PreVendorDaoImpl extends BaseDao implements PreVendorDao {

	@Autowired
	private TransferRepository transferRepository;

	@Override
	public int turnVendor(int id) {
		Key key = transferRepository.transfer("PreVendor", id);
		int veid = key.getId();
		String vendStatus = getDBSetting("PreVendor", "VendStatus");
		String statuscode = "AUDITED";
		if (vendStatus != null) {
			// 状态是已审核
			if ("1".equals(vendStatus)) {
				statuscode = "AUDITED";
			}
			// 状态是在录入
			if ("0".equals(vendStatus)) {
				statuscode = "ENTERING";
			}
		}
		updateByCondition("Vendor",
				"ve_auditstatuscode='" + statuscode + "',ve_auditstatus='" + BaseUtil.getLocalMessage(statuscode) + "'", "ve_id=" + veid);
		updateByCondition("Vendor", "ve_auditman='" + SystemSession.getUser().getEm_name() + "', ve_auditdate=sysdate ", "ve_id=" + veid
				+ " and ve_auditstatuscode='AUDITED'");
		updateByCondition("Vendor", "ve_auditman=null, ve_auditdate=null ", "ve_id=" + veid + " and ve_auditstatuscode='ENTERING'");
		return veid;
	}
}
