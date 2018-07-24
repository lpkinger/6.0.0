package com.uas.erp.service.b2c.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.b2c.B2CSettingService;

@Service("b2CSettingService")
public class B2CSettingServiceImpl implements B2CSettingService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public Map<String, Object> getB2CSetting(String caller) {
		Map<String, Object> map = new HashMap<String, Object>();
		SqlRowList rs = baseDao.queryForRowSet(
				"select data,cu_name from configs left join customer on cu_code=data where caller=? and code=?", caller, "B2CCusomter");
		if (rs.next()) {
			map.put("b2ccusomter", rs.getObject("data"));
			map.put("b2ccusomtername", rs.getObject("cu_name"));
		}
		rs = baseDao.queryForRowSet("select data,ve_name from configs left join vendor on ve_code=data where caller=? and code=?", caller,
				"B2CVendor");
		if (rs.next()) {
			map.put("b2cvendor", rs.getObject("data"));
			map.put("b2cvendorname", rs.getObject("ve_name"));
		}
		rs = baseDao.queryForRowSet("select data from configs where caller=? and code=?", caller, "startB2C");
		if (rs.next()) {
			map.put("startCheck", rs.getObject("data"));
		}
		rs = baseDao.queryForRowSet("select sk_code,sk_name from salekind where nvl(sk_ifb2c,0)<>0");
		if (rs.next()) {
			map.put("b2csalekind", rs.getObject("sk_code"));
			map.put("b2csalekindname", rs.getObject("sk_name"));
		}
		return map;
	}

	@Override
	public void saveB2CCustomer(String param, String caller) {
		// 判断客户是否存在，客户状态是否已审核
		SqlRowList rs = baseDao.queryForRowSet("select cu_auditstatuscode from customer where cu_code=?", param);
		if (rs.next()) {
			if ("AUDITED".equals(rs.getObject("cu_auditstatuscode"))) {
				// 保存configs
				baseDao.updateByCondition("configs", "data='" + param + "'", "caller='" + caller + "' and code='B2CCusomter'");
			} else if ("DISABLE".equals(rs.getObject("cu_auditstatuscode"))) {
				BaseUtil.showError("客户编号：" + param + ",已禁用");
			} else {
				BaseUtil.showError("客户编号：" + param + ",未审核");
			}
		} else {
			BaseUtil.showError("客户编号：" + param + ",不存在");
		}
		// 保存configs
		baseDao.updateByCondition("configs", "data='" + param + "'", "caller='" + caller + "' and code='B2CCusomter'");
	}

	@Override
	public void saveB2CVendor(String param, String caller) {
		// 判断供应商是否存在，供应商状态是否已审核
		SqlRowList rs = baseDao.queryForRowSet("select ve_auditstatuscode from vendor where ve_code=?", param);
		if (rs.next()) {
			if ("AUDITED".equals(rs.getObject("ve_auditstatuscode"))) {
				// 保存configs
				baseDao.updateByCondition("configs", "data='" + param + "'", "caller='" + caller + "' and code='B2CVendor'");
			} else if ("DISABLE".equals(rs.getObject("ve_auditstatuscode"))) {
				BaseUtil.showError("供应商编号：" + param + ",已禁用");
			} else {
				BaseUtil.showError("供应商编号：" + param + ",未审核");
			}
		} else {
			BaseUtil.showError("供应商编号：" + param + ",不存在");
		}
	}

	@Override
	public void startB2C(String caller) {
       
	}

	@Override
	public void saveB2CSaleKind(String param, String caller) {
		// 判断销售类型是否存在，状态是否已审核
		SqlRowList rs = baseDao.queryForRowSet("select sk_statuscode from salekind where sk_code=?", param);
		if (rs.next()) {
			if (!rs.getObject("sk_statuscode").equals("AUDITED")) {
				BaseUtil.showError("销售类型：" + param + ",未审核");
			}
		} else {
			BaseUtil.showError("销售类型：" + param + ",不存在");
		}
		// 更新
		baseDao.updateByCondition("salekind", "sk_ifb2c=0", "nvl(sk_ifb2c,0)<>0");
		baseDao.updateByCondition("salekind", "sk_ifb2c=-1", "sk_code='" + param + "'");
	}
}
