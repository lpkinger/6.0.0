package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.fa.AccountRegisterBankService;
import com.uas.erp.service.fa.AccountRegisterImportService;

@Service
public class AccountRegisterImportServiceImpl implements AccountRegisterImportService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private AccountRegisterBankService accuntRegisterBankService;

	@Override
	public void saveAccountRegisterImportById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "UPDATEMAINFORM"));
		StringBuffer sb = new StringBuffer();
		Employee employee = SystemSession.getUser();
		for (Map<Object, Object> s : grid) {
			Object type = s.get("ari_type");
			if (type == null) {
				sb.append("类型不能为空！<br>");
			} else if (!"应收款".equals(type) && !"预收款".equals(type) && !"应收退款".equals(type) && !"预收退款".equals(type) && !"费用".equals(type)
					&& !"其它收款".equals(type) && !"其它付款".equals(type) && !"转存".equals(type) && !"应付款".equals(type) && !"预付款".equals(type)
					&& !"应付退款".equals(type) && !"预付退款".equals(type)) {
				sb.append("类型[" + type + "]不允许批量导入！<br>");
			} else {
				s.put("ari_id", baseDao.getSeqId("AccountRegisterImport_SEQ"));
				s.put("ari_emid", store.get("em_id"));
				s.put("ari_emcode", employee.getEm_code());
				s.put("ari_emname", employee.getEm_name());
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "AccountRegisterImport");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "em_id", store.get("em_id"));
	}

	@Override
	public void updateAccountRegisterImportById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Object emid = store.get("em_id");
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "AccountRegisterImport", "ari_id");
		StringBuffer sb = new StringBuffer();
		Employee employee = SystemSession.getUser();
		for (Map<Object, Object> s : gstore) {
			Object type = s.get("ari_type");
			if (type == null) {
				sb.append("类型不能为空！<br>");
			} else if (!"应收款".equals(type) && !"预收款".equals(type) && !"应收退款".equals(type) && !"预收退款".equals(type) && !"费用".equals(type)
					&& !"其它收款".equals(type) && !"其它付款".equals(type) && !"转存".equals(type) && !"应付款".equals(type) && !"预付款".equals(type)
					&& !"应付退款".equals(type) && !"预付退款".equals(type)) {
				sb.append("类型[" + type + "]不允许批量导入！<br>");
			}
			if (s.get("ari_id") == null || s.get("ari_id").equals("") || s.get("ari_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("AccountRegisterImport_SEQ");
				s.put("ari_emid", emid);
				s.put("ari_emcode", employee.getEm_code());
				s.put("ari_emname", employee.getEm_name());
				String sql = SqlUtil.getInsertSqlByMap(s, "AccountRegisterImport", new String[] { "ari_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		baseDao.execute(gridSql);
		baseDao.logger.update(caller, "em_id", store.get("em_id"));
	}

	@Override
	public void delete(int emid, String caller) {
		// 删除
		baseDao.deleteById("UPDATEMAINFORM", "em_id", emid);
		baseDao.execute("delete from AccountRegisterImport where ari_emid=" + emid);
		// 记录操作
		baseDao.logger.delete(caller, "em_id", emid);
	}

	@Override
	public void cleanAccountRegisterImport(int emid, String caller) {
		baseDao.execute("delete from AccountRegisterImport where ari_emid=" + emid);
	}

	@Override
	public void cleanFailed(int emid, String caller) {
		baseDao.execute("delete from AccountRegisterImport where ari_emid=" + emid + " and nvl(ari_error,' ')<>' '");
	}

	@Override
	public void accountRegisterImport(int em_id) {
		int yearmonth = 0;
		int count = 0;
		StringBuffer sb = new StringBuffer();
		SqlRowList rs = baseDao.queryForRowSet("select * from AccountRegisterImport where ari_emid=" + em_id + " AND nvl(ari_status,0)=0");
		while (rs.next()) {
			String type = rs.getGeneralString("ari_type");
			int ari_detno = rs.getGeneralInt("ari_detno");
			int ari_id = rs.getGeneralInt("ari_id");
			String error = null;
			yearmonth = DateUtil.getYearmonth(rs.getGeneralString("ari_date"));
			if (StringUtil.hasText(rs.getObject("ari_sellercode")) && StringUtil.hasText(rs.getObject("ari_sellername"))) {
				count = baseDao.getCount("select count(*) from employee where em_code='" + rs.getGeneralString("ari_sellercode")
						+ "' and em_name='" + rs.getGeneralString("ari_sellername") + "'");
				if (count == 0) {
					error = "行号[" + ari_detno + "]业务员编号+业务员名称在人员资料中不存在！";
					baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
					sb.append(error + "<br>");
				}
			}
			if (StringUtil.hasText(rs.getObject("ari_departmentcode")) && StringUtil.hasText(rs.getObject("ari_departmentname"))) {
				count = baseDao.getCount("select count(*) from department where dp_code='" + rs.getGeneralString("ari_departmentcode")
						+ "' and dp_name='" + rs.getGeneralString("ari_departmentname") + "'");
				if (count == 0) {
					error = "行号[" + ari_detno + "]部门编号+部门名称在部门资料中不存在！";
					baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
					sb.append(error + "<br>");
				}
			}
			count = baseDao.getCount("select count(*) from periodsdetail where pd_code='MONTH-B' and pd_detno=" + yearmonth
					+ " and pd_status>0");
			if (count > 0) {
				error = "行号[" + ari_detno + "]日期所属票据资金系统模块已经结账！";
				baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
				sb.append(error + "<br>");
			}
			count = baseDao.getCount("select count(*) from periodsdetail where pd_code='MONTH-A' and pd_detno=" + yearmonth
					+ " and pd_status>0");
			if (count > 0) {
				error = "行号[" + ari_detno + "]日期所属总账模块已结账！";
				baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
				sb.append(error + "<br>");
			}
			count = baseDao
					.getCount("select count(*) from category where nvl(ca_iscashbank,0)<>0 and nvl(ca_isleaf,0)<>0 and nvl(ca_statuscode,' ')='AUDITED' and ca_code='"
							+ rs.getObject("ari_accountcode") + "'");
			if (count == 0) {
				error = "行号[" + ari_detno + "]账户编号必须是银行现金类科目、已审核的末级科目！";
				baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
				sb.append(error + "<br>");
			} else {
				baseDao.execute("update AccountRegisterImport set (ari_cateid,ari_accountname,ari_currencytype)=(select ca_id,ca_description,ca_currencytype from category where ca_code=ari_accountcode) where ari_id="
						+ ari_id);
				if (StringUtil.hasText(rs.getObject("ari_accountcurrency"))) {
					count = baseDao.getCount("select count(*) from category where ca_code='" + rs.getObject("ari_accountcode")
							+ "' and ca_currency='" + rs.getObject("ari_accountcurrency") + "'");
					if (count == 0) {
						error = "行号[" + ari_detno + "]账户币别与科目资料里的默认币别不一致！";
						baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
						sb.append(error + "<br>");
					}
				} else {
					baseDao.execute("update AccountRegisterImport set (ari_accountcurrency)=(select ca_currency from category where ari_accountcode=ca_code) where nvl(ari_accountcurrency,' ')=' ' and ari_id="
							+ ari_id);
				}
			}
			if (rs.getGeneralDouble("ari_accountrate") == 0) {
				baseDao.execute("update AccountRegisterImport set ari_accountrate=(select nvl(cm_crrate,1) from currencysmonth where ari_accountcurrency=cm_crname and to_char(ari_date,'yyyymm')=cm_yearmonth) where ari_id="
						+ ari_id);
			}
			if ("应收款".equals(type) || "预收款".equals(type) || "其它收款".equals(type) || "应付退款".equals(type) || "预付退款".equals(type)) {
				if (rs.getGeneralDouble("ari_payment") != 0) {
					error = "行号[" + ari_detno + "]支出金额必须为0！";
					baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
					sb.append(error + "<br>");
				}
				if (rs.getGeneralDouble("ari_deposit") == 0) {
					error = "行号[" + ari_detno + "]收入金额不能为0！";
					baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
					sb.append(error + "<br>");
				}
			}
			if ("应付款".equals(type) || "预付款".equals(type) || "其它付款".equals(type) || "费用".equals(type) || "应收退款".equals(type)
					|| "预收退款".equals(type) || "转存".equals(type)) {
				if (rs.getGeneralDouble("ari_payment") == 0) {
					error = "行号[" + ari_detno + "]支出金额不能为0！";
					baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
					sb.append(error + "<br>");
				}
				if (rs.getGeneralDouble("ari_deposit") != 0) {
					error = "行号[" + ari_detno + "]收入金额必须为0！";
					baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
					sb.append(error + "<br>");
				}
			}
			if ("应收款".equals(type) || "预收款".equals(type) || "应收退款".equals(type) || "预收退款".equals(type)) {
				baseDao.execute("update AccountRegisterImport set ari_arapcurrency=ari_accountcurrency,ari_araprate=1,ari_aramount=nvl(ari_payment,0)+nvl(ari_deposit,0) where ari_id="
						+ ari_id);
				if (!StringUtil.hasText(rs.getObject("ari_custcode")) && !StringUtil.hasText(rs.getObject("ari_custname"))) {
					error = "行号[" + ari_detno + "]客户编号、客户名称必须填写！";
					baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
					sb.append(error + "<br>");
				} else {
					count = baseDao.getCount("select count(*) from customer where cu_auditstatuscode='AUDITED' and cu_code='"
							+ rs.getObject("ari_custcode") + "'");
					if (count == 0) {
						error = "行号[" + ari_detno + "]客户编号不存在或者状态不等于已审核！";
						baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
						sb.append(error + "<br>");
					} else {
						count = baseDao.getCount("select count(*) from customer where cu_code='" + rs.getObject("ari_custcode")
								+ "' and cu_name='" + rs.getObject("ari_custname") + "'");
						if (count == 0) {
							error = "行号[" + ari_detno + "]客户编号+客户名称在客户资料中不存在！";
							baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
							sb.append(error + "<br>");
						}
					}
				}
				if ("应收款".equals(type) && !StringUtil.hasText(rs.getObject("ari_sellercode"))
						&& !StringUtil.hasText(rs.getObject("ari_sellername"))) {
					error = "行号[" + ari_detno + "]业务员编号、业务员名称必须填写！";
					baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
					sb.append(error + "<br>");
				}
			}
			if ("应付款".equals(type) || "预付款".equals(type) || "应付退款".equals(type) || "预付退款".equals(type)) {
				baseDao.execute("update AccountRegisterImport set ari_arapcurrency=ari_accountcurrency,ari_araprate=1,ari_aramount=nvl(ari_payment,0)+nvl(ari_deposit,0) where ari_id="
						+ ari_id);
				if (!StringUtil.hasText(rs.getObject("ari_vendcode")) && !StringUtil.hasText(rs.getObject("ari_vendname"))) {
					error = "行号[" + ari_detno + "]供应商编号、供应商名称必须填写！";
					baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
					sb.append(error + "<br>");
				} else {
					count = baseDao.getCount("select count(*) from vendor where ve_auditstatuscode='AUDITED' and ve_code='"
							+ rs.getObject("ari_vendcode") + "'");
					if (count == 0) {
						error = "行号[" + ari_detno + "]供应商编号不存在或者状态不等于已审核！";
						baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
						sb.append(error + "<br>");
					} else {
						count = baseDao.getCount("select count(*) from vendor where ve_code='" + rs.getObject("ari_vendcode")
								+ "' and ve_name='" + rs.getObject("ari_vendname") + "'");
						if (count == 0) {
							error = "行号[" + ari_detno + "]供应商编号+供应商名称在供应商资料中不存在！";
							baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
							sb.append(error + "<br>");
						}
					}
				}
			}
			if ("转存".equals(type)) {
				if (!StringUtil.hasText(rs.getObject("ari_category"))) {
					error = "行号[" + ari_detno + "]转存到科目未填写！";
					baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
					sb.append(error + "<br>");
				} else {
					count = baseDao
							.getCount("select count(*) from category where nvl(ca_iscashbank,0)<>0 and nvl(ca_isleaf,0)<>0 and nvl(ca_statuscode,' ')='AUDITED' and ca_code='"
									+ rs.getObject("ari_category") + "'");
					if (count == 0) {
						error = "行号[" + ari_detno + "]转存科目必须是银行现金类科目、已审核的末级科目！";
						baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
						sb.append(error + "<br>");
					} else {
						baseDao.execute("update AccountRegisterImport set (ari_othercateid,ari_catedesc)=(select ca_id,ca_description from category where ari_category=ca_code) where ari_id="
								+ ari_id);
						if (StringUtil.hasText(rs.getObject("ari_precurrency"))) {
							count = baseDao.getCount("select count(*) from category where ca_code='" + rs.getObject("ari_category")
									+ "' and ca_currency='" + rs.getObject("ari_precurrency") + "'");
							if (count == 0) {
								error = "行号[" + ari_detno + "]转存币别与科目资料里的默认币别不一致！";
								baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
								sb.append(error + "<br>");
							}
						} else {
							baseDao.execute("update AccountRegisterImport set ari_precurrency=(select ca_currency from category where ca_code=ari_category) where nvl(ari_precurrency,' ')=' ' and ari_id="
									+ ari_id);
						}
					}
				}
				if (rs.getGeneralDouble("ari_preamount") == 0) {
					error = "行号[" + ari_detno + "]转存金额不能为0！";
					baseDao.execute("update AccountRegisterImport set ari_error='" + error + "' where ari_id=" + ari_id);
					sb.append(error + "<br>");
				}
				baseDao.execute("update AccountRegisterImport set ari_prerate=round(nvl(ari_preamount,0)/nvl(ari_payment,0),15) where ari_id="
						+ ari_id + " and nvl(ari_payment,0)<>0");
			}
			
			String currency = baseDao.getDBSetting("defaultCurrency");
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(ari_accountcurrency) from AccountRegisterImport where ari_id=? and nvl(ari_accountrate,0)=1 and nvl(ari_accountcurrency,' ')<>'"
							+ currency + "'", String.class, ari_id);
			if (dets != null) {
				error = "行号[" + ari_detno + "]账户币别非本位币账户汇率不能为1！";
				sb.append(error + "<br>");
			}
			if (sb.length() == 0) {
				baseDao.execute("update AccountRegisterImport set ari_error=null where ari_id=" + ari_id);
				baseDao.execute("update AccountRegisterImport set ari_arid=AccountRegister_seq.nextval, ari_code='"
						+ baseDao.sGetMaxNumber("AccountRegister", 2)
						+ "' where nvl(ari_arid,0)=0 and nvl(ari_error,' ')=' ' and nvl(ari_status,0)=0 and ari_id=" + ari_id);
			}

		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		baseDao.execute("insert into AccountRegister(ar_id,ar_code,ar_recordman,ar_recorddate,ar_status,ar_statuscode,ar_date,ar_type,ar_memo,ar_deposit,ar_payment,"
				+ "ar_cateid,ar_accountcode,ar_accountname,ar_accountcurrency,ar_accountrate,ar_currencytype,ar_custcode,ar_custname,ar_sellerid,ar_sellercode,ar_sellername,"
				+ "ar_vendcode,ar_vendname,ar_arapcurrency,ar_araprate,ar_aramount,ar_othercateid,ar_category,ar_catedesc,ar_precurrency,ar_prerate,ar_preamount,"
				+ "ar_prjcode,ar_prjname,ar_departmentcode,ar_departmentname,ar_apamount,ar_refno) "
				+ "select ari_arid,ari_code,ari_emname,sysdate,'已提交','COMMITED',ari_date,ari_type,ari_memo,ari_deposit,ari_payment,"
				+ "ari_cateid,ari_accountcode,ari_accountname,ari_accountcurrency,ari_accountrate,ari_currencytype,ari_custcode,ari_custname,ari_sellerid,ari_sellercode,ari_sellername,"
				+ "ari_vendcode,ari_vendname,ari_arapcurrency,ari_araprate,ari_aramount,ari_othercateid,ari_category,ari_catedesc,ari_precurrency,ari_prerate,ari_preamount,"
				+ "ari_prjcode,ari_prjname,ari_departmentcode,ari_departmentname,0,ari_refno "
				+ "from AccountRegisterImport where nvl(ari_code,' ')<>' ' and nvl(ari_error,' ')=' ' and nvl(ari_status,0)=0 and ari_emid="
				+ em_id);
		baseDao.execute("update  AccountRegisterImport set ari_status=99,ari_error=null, ari_updatedate=sysdate where nvl(ari_code,' ')<>' ' and nvl(ari_status,0)=0 and ari_emid="
				+ em_id);
		rs = baseDao.queryForRowSet("select ari_type, ari_arid from AccountRegisterImport where ari_emid=" + em_id
				+ " AND nvl(ari_status,0)=99 and ari_type not in ('其它收款','其它付款','费用')");
		while (rs.next()) {
			accuntRegisterBankService.accountedAccountRegister(rs.getGeneralInt("ari_arid"), "AccountRegister!Bank");
		}
	}
}
