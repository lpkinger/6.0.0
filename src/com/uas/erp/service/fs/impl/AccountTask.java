package com.uas.erp.service.fs.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.uas.b2b.service.common.AbstractTask;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;

@Component
@EnableAsync
@EnableScheduling
public class AccountTask  extends AbstractTask{
	
	@Autowired
	private BaseDao baseDao;
	
	@Scheduled(cron = "0 0/30 1-3 * * ?")
	@Async
	public void execute() {
		super.execute();
	}
	
	/**
	 * 定时传递还款计划
	 */
	@Override
	protected void onExecute(Master master) {
		if (StringUtil.hasText(master.getMa_finwebsite())) {
			List<Object []> aacodes = baseDao.getFieldsDatasByCondition("AccountApply", new String [] {"aa_code","aa_aaid"}, "nvl(aa_aaid,0)>0 and "
					+ "not EXISTS (select 1 from ReimbursementPlan where rp_aacode = aa_code and nvl(REIMBURSEMENTPLAN.ID,0)>0)");
			for (Object[] aacode : aacodes) {
				try {
					Map<String, String> params = new HashMap<String, String>();
					List<Map<String, Object>> plans = new ArrayList<Map<String,Object>>();
					SqlRowList rs = baseDao.queryForRowSet("select rp_id,rp_backdate,rp_currency,rp_principal,rp_interest,"
							+ "rp_iscloseoff,rp_truster from ReimbursementPlan where rp_aacode = ?",aacode[0]);
					while (rs.next()) {
						Map<String, Object> plan = new HashMap<String, Object>();
						plan.put("rp_id", rs.getGeneralInt("rp_id"));
						plan.put("rp_backdate", rs.getDate("rp_backdate"));
						plan.put("rp_currency", rs.getGeneralString("rp_currency"));
						plan.put("rp_principal", rs.getGeneralDouble("rp_principal"));
						plan.put("rp_interest", rs.getGeneralDouble("rp_interest"));
						plan.put("rp_iscloseoff", rs.getGeneralString("rp_iscloseoff"));
						plan.put("rp_truster", rs.getGeneralString("rp_truster"));
						plan.put("aaid", aacode[1]);
						plans.add(plan);
					}
					if (CollectionUtils.isEmpty(plans)) {
						continue;
					}
					params.put("list", FlexJsonUtil.toJsonArray(plans));
					Response response = HttpUtil.sendPostRequest(master.getMa_finwebsite() + "/repayment/erp/getfromuas?access_id=" + master.getMa_uu(), params,
							true, master.getMa_accesssecret());
					if (response.getStatusCode() != HttpStatus.OK.value()) {
						throw new Exception("连接平台失败," + response.getStatusCode());
					}else {
						String data = response.getResponseText();
						if (StringUtil.hasText(data)) {
							baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(data, "REIMBURSEMENTPLAN", "rp_id"));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					BaseUtil.showError("错误：" + e.getMessage());
				}
			}
		}
	}
}
