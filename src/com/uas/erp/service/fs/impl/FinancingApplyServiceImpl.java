package com.uas.erp.service.fs.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.fs.FinancingApplyService;

@Service
public class FinancingApplyServiceImpl implements FinancingApplyService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private EnterpriseService enterpriseService;

	@Override
	public Map<String, Object> getFinancingApply(String condition) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (StringUtil.hasText(condition)) {
			List<Map<String, Object>> data = baseDao.getJdbcTemplate().queryForList("SELECT * FROM FINANCINGAPPLY WHERE " + condition);
			if (data.size() > 0) {
				result.put("fa_id", data.get(0).get("FA_ID"));
				result.put("fa_enname", data.get(0).get("FA_ENNAME"));
				result.put("fa_contact", data.get(0).get("FA_CONTACT"));
				result.put("fa_telphone", data.get(0).get("FA_TELPHONE"));
				result.put("fa_phone", data.get(0).get("FA_PHONE"));
				result.put("fa_addr", data.get(0).get("FA_ADDR"));
				result.put("fa_appamount", data.get(0).get("FA_APPAMOUNT"));
				result.put("fa_applyman", data.get(0).get("FA_APPLYMAN"));
				result.put("fa_applydate", DateUtil.format((Date) data.get(0).get("FA_APPLYDATE"), Constant.YMD));
				result.put("fa_busincode", data.get(0).get("FA_BUSINCODE"));
				result.put("fa_facorpid", data.get(0).get("FA_FACORPID"));
				result.put("fa_facorpname", data.get(0).get("FA_FACORPNAME"));
				result.put("fa_buyercode", data.get(0).get("FA_BUYERCODE"));
				result.put("fa_buyer", data.get(0).get("FA_BUYER"));
			}
		} else {
			Employee employee = SystemSession.getUser();
			List<Map<String, Object>> data = baseDao.getJdbcTemplate().queryForList(
					"SELECT EN_NAME,EN_BUSINESSCODE,EN_CORPORATION,EN_TEL,EN_ADMINPHONE,EN_ADDRESS FROM ENTERPRISE WHERE EN_ID = "
							+ employee.getEm_enid());
			if (data.size() > 0) {
				result.put("fa_enname", data.get(0).get("EN_NAME"));
			}

			List<Map<String, Object>> data1 = baseDao.getJdbcTemplate().queryForList("select * from FINANCECORPORATION order by FC_ID");
			if (data1.size() > 0) {
				result.put("fa_facorpname", data1.get(0).get("fc_name"));
				result.put("fa_facorpcode", data1.get(0).get("fc_code"));
			}
		}
		return result;
	}

	@Override
	public Map<String, Object> submitApply(HttpSession session, String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Map<Object, Object> store1 = new HashMap<Object, Object>();
		store1.putAll(store);
		Map<String, Object> result = new HashMap<String, Object>();
		Object[] corporation = baseDao.getFieldsDataByCondition("FINANCECORPORATION", new String[] { "FC_URL", "FC_WHICHSYSTEM" },
				"fc_code = '" + store.get("fa_facorpcode") + "'");
		if (corporation == null) {
			BaseUtil.showError("保理公司信息不存在,无法申请！");
		} else if (!(StringUtil.hasText(corporation[0]) && StringUtil.hasText(corporation[1]))) {
			BaseUtil.showError("保理公司信息不完整，请核查！");
		}
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		Object[] enterprise = baseDao
				.getFieldsDataByCondition(
						"Enterprise",
						"en_name_en,en_address,en_uu,en_registercapital,en_erpurl,en_taxcode,en_whichsystem,en_landtaxcode,en_businesscode,en_type,en_corporation",
						"en_id=" + SystemSession.getUser().getEm_enid());
		store.put("en_name_en", enterprise[0]);
		store.put("en_address", enterprise[1]);
		store.put("en_uu", enterprise[2]);
		store.put("en_registercapital", enterprise[3]);
		store.put("en_erpurl", enterprise[4]);
		store.put("en_taxcode", enterprise[5]);
		store.put("en_whichsystem", master.getMa_name());
		store.put("en_landtaxcode", enterprise[7]);
		store.put("en_businesscode", enterprise[8]);
		store.put("en_type", enterprise[9]);
		store.put("en_corporation", enterprise[10]);
		store.put("capcurrency", baseDao.getDBSetting("defaultCurrency"));
		store.put("b2benable", master.getMa_enable());
		HashMap<String, String> params1 = new HashMap<String, String>();
		Integer year = null;

		try {
			year = DateUtil.getYear(new Date());
		} catch (ParseException e1) {
			e1.printStackTrace();
			BaseUtil.showError("获取报表年份失败！");
		}
		params.put("year", String.valueOf(year - 1));

		List<Integer> yearmonths = new ArrayList<Integer>();

		// 总账结账期间上一期间
		Integer GLYearmonth = baseDao
				.queryForObject(
						"Select Pd_Detno From (Select Pd_Detno From Periodsdetail Where Pd_Code = 'MONTH-A' And Pd_Status =99 Order By rpad(Pd_Detno,7,0) Desc) Where Rownum=1",
						Integer.class);
		yearmonths.add(GLYearmonth);

		for (int i = 1; i < 4; i++) {
			Integer yearmonth = baseDao.queryForObject(
					"select max(substr(frd_yearmonth,1,6)) from FAREPORTDETAIL where substr(frd_yearmonth,1,4) = ?", Integer.class, year
							- i);
			if (yearmonth != null) {
				yearmonths.add(yearmonth);
			}
		}

		params.put("yearmonths", FlexJsonUtil.toJsonArray(yearmonths));
		params1.put("custname", store.get("fa_enname").toString());
		try {
			Response response = HttpUtil.sendPostRequest(corporation[0]+"/openapi/applicant/existSecret.action?master="+corporation[1], params1, true);
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					JSONObject jObject = JSONObject.fromObject(data);
					Boolean existSecret = Boolean.parseBoolean(jObject.getString(("existSecret")));
					if (!existSecret) {
						String fssecret = master.getMa_fssecret();
						if (!StringUtil.hasText(fssecret)) {
							fssecret = StringUtil.getRandomString(32);
							master.setMa_fssecret(fssecret);
							Boolean isSaas = (Boolean) session.getAttribute("isSaas");
							if (isSaas) {
								baseDao.execute("update uas_master.master set ma_fssecret = '" + fssecret + "' where ma_id = " + master.getMa_id());
							}else{
								Master parentMaster = null;
								if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
									parentMaster = master;
								} else if (master != null && null != master.getMa_pid() && master.getMa_pid() > 0) {
									parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
								}
								if (null!=parentMaster) {
									baseDao.execute("update "+parentMaster.getMa_name()+".master set ma_fssecret = '" + fssecret + "' where ma_id = " + master.getMa_id());
								}
							}
						}
						store.put("en_finsecret", fssecret);
					}
				}
			} else {
				throw new RuntimeException("连接保理公司失败," + response.getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误：" + e.getMessage());
		}

		params.put("FinancingApply", FlexJsonUtil.toJson(store));
		
		try {
			Response response = HttpUtil.sendPostRequest(corporation[0]+"/openapi/applicant/assessFinancingApply.action?master="+corporation[1], params, true);
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					Map<Object, Object> ret = BaseUtil.parseFormStoreToMap(data);
					Object busincode = ret.get("busincode");
					if (StringUtil.hasText(busincode)) {
						int id = baseDao.getSeqId("FINANCINGAPPLY_SEQ");
						store1.put("fa_id", id);
						store1.put("fa_busincode", busincode);
						baseDao.execute(SqlUtil.getInsertSqlByFormStore(store1, "FINANCINGAPPLY", new String[] {}, new String[] {}));
						result.put("id", id);
					}
				}
			} else {
				throw new RuntimeException("连接保理公司失败," + response.getStatusCode());
			}
		} catch (Exception e) {
			BaseUtil.showError("错误：" + e.getMessage());
		}
		return result;
	}


	@Override
	public Map<String, Object> getFinancApplyProgress(String condition, String busincode) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<Object, Object>> show = new ArrayList<Map<Object, Object>>();
		if (condition == null && busincode != null) {
			Object[] corporation = baseDao.getFieldsDataByCondition(
					"FINANCECORPORATION INNER JOIN FINANCINGAPPLY ON FA_FACORPCODE = FC_CODE", new String[] { "FC_URL", "FC_WHICHSYSTEM" },
					"FA_BUSINCODE = '" + busincode + "'");
			if (corporation == null) {
				BaseUtil.showError("保理公司信息不存在,无法申请！");
			} else if (!(StringUtil.hasText(corporation[0]) && StringUtil.hasText(corporation[1]))) {
				BaseUtil.showError("保理公司信息不完整，请核查！");
			}
			Map<String, String> params = new HashMap<String, String>();
			params.put("busincode", busincode);
			try {
				Response response = HttpUtil.sendPostRequest(corporation[0]+"/openapi/applicant/FinancApplyProgress.action?master="+corporation[1], params, true);
				
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					String data = response.getResponseText();
					if (StringUtil.hasText(data)) {
						Map<String, Object> map = FlexJsonUtil.fromJson(data);
						show = BaseUtil.parseGridStoreToMaps(map.get("progress").toString());
					}
				} else {
					throw new Exception("连接保理公司失败," + response.getStatusCode());
				}
			} catch (Exception e) {
				BaseUtil.showError("错误：" + e.getMessage());
			}
		} else if (condition != null) {
			Object[] code = baseDao.getFieldsDataByCondition("FINBUSINAPPLY", "fs_cqcode,fs_custname", condition);
			if (code != null) {
				result.put("code", code[0]);
				result.put("custname", code[1]);
				SqlRowList rs = baseDao
						.queryForRowSet("select fs_status,fs_applydate,fs_crdratdate,fs_acceptdate,fs_statusdate,fs_loaddate from FINBUSINAPPLY where "
								+ condition);
				if (rs.next()) {
					Map<Object, Object> map = new HashMap<Object, Object>();
					map.put("desc", "平台申请");
					if (rs.getDate("fs_applydate") != null) {
						map.put("date", DateUtil.format(rs.getDate("fs_applydate"), "yyyy年MM月dd日"));
						map.put("isok", "greencircle");
					} else {
						map.put("date", "");
						map.put("isok", "redcircle");
					}
					show.add(map);
					if (rs.getString("fs_status") != null && "申请取消".equals(rs.getString("fs_status"))) {
						map = new HashMap<Object, Object>();
						map.put("desc", "申请取消");
						if (rs.getDate("fs_crdratdate") != null) {
							map.put("date", DateUtil.format(rs.getDate("fs_acceptdate"), "yyyy年MM月dd日"));
							map.put("isok", "greencircle");
						} else {
							map.put("date", "");
							map.put("isok", "redcircle");
						}
						show.add(map);
					} else {
						map = new HashMap<Object, Object>();
						map.put("desc", "收集材料");
						if (rs.getDate("fs_crdratdate") != null) {
							map.put("date", DateUtil.format(rs.getDate("fs_crdratdate"), "yyyy年MM月dd日"));
							map.put("isok", "greencircle");
						} else {
							map.put("date", "");
							map.put("isok", "redcircle");
						}
						show.add(map);
						map = new HashMap<Object, Object>();
						map.put("desc", "应收账款转让");
						if (rs.getDate("fs_acceptdate") != null) {
							map.put("date", DateUtil.format(rs.getDate("fs_acceptdate"), "yyyy年MM月dd日"));
							map.put("isok", "greencircle");
						} else {
							map.put("date", "");
							map.put("isok", "redcircle");
						}
						show.add(map);
						map = new HashMap<Object, Object>();
						map.put("desc", "出账审批");
						if (rs.getDate("fs_statusdate") != null) {
							map.put("date", DateUtil.format(rs.getDate("fs_statusdate"), "yyyy年MM月dd日"));
							map.put("isok", "greencircle");
						} else {
							map.put("date", "");
							map.put("isok", "redcircle");
						}
						show.add(map);
						map = new HashMap<Object, Object>();
						map.put("desc", "放款");
						if (rs.getDate("fs_loaddate") != null) {
							map.put("date", DateUtil.format(rs.getDate("fs_loaddate"), "yyyy年MM月dd日"));
							map.put("isok", "greencircle");
						} else {
							map.put("date", "");
							map.put("isok", "redcircle");
						}
						show.add(map);
					}
				}
			}
		}
		result.put("data", show);
		return result;
	}

}
