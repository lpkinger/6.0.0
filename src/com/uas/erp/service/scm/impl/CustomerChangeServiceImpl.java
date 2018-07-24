package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.CustomerChangeService;

@Service
public class CustomerChangeServiceImpl implements CustomerChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustomerChange(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService
				.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "CustomerChange"));
		baseDao.logger.save(caller, "cc_id", store.get("cc_id"));
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteCustomerChange(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CustomerChange",
				"cc_statuscode", "cc_id=" + id);
		StateAssert.delOnlyEntering(status);
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		// 删除CustomerChange
		baseDao.deleteById("CustomerChange", "cc_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "cc_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });

	}

	@Override
	public void updateCustomerChange(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		StateAssert.updateOnlyEntering(store.get("cc_statuscode"));
		handlerService.handler(caller, "update", "before",
				new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store,
				"CustomerChange", "cc_id"));
		handlerService.handler(caller, "update", "after",
				new Object[] { store });
		baseDao.logger.update(caller, "cc_id", store.get("cc_id"));

	}

	@Override
	public void auditCustomerChange(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CustomerChange",
				"cc_statuscode", "cc_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		// 反应到客户表中
		List<String> sqls = new ArrayList<String>();
		sqls.add("update Customer set(cu_sellerid,cu_sellercode,cu_sellername,cu_kind,"
				+ "cu_agenttype,cu_currency,cu_rate,cu_taxrate,cu_invoicetype,cu_paymentid,"
				+ "cu_paymentscode,cu_payments,cu_shipment,cu_contact,cu_degree,cu_mobile,cu_tel,cu_fax,cu_email) ="
				+ "(select cc_newsellerid,cc_newsellercode,cc_newsellername,cc_newcukind,"
				+ "cc_newagenttype,cc_newcurrency,cc_newrate,cc_newtaxrate,cc_newinvoicetype,cc_newpaymentid,"
				+ "cc_newpaymentscode,cc_newpayments,cc_newshipment,cc_newcontact,cc_newdegree,cc_newmobile,"
				+ "cc_newtel,cc_newfax,cc_newemail from CustomerChange where cc_cucode=cu_code and cc_id="
				+ id +") where exists (select 1 from CustomerChange where cc_cucode=cu_code and cc_id="
				+ id + ")");
		SqlRowList rs = baseDao.queryForRowSet("select * from CustomerChange where cc_id=?", id);
		boolean bool = false;
		String sellecode = null;
		String sellename = null;
		String cucode = null;
		if (rs.next()) {
			cucode = rs.getGeneralString("cc_cucode");
			if (!rs.getGeneralString("cc_newagenttype").equals(rs.getGeneralString("cc_agenttype"))) {
				sqls.add("update Customer set cu_agenttype='" + rs.getGeneralString("cc_newagenttype") + "' where cu_code='" + cucode + "'");
			}
			if (!rs.getGeneralString("cc_newprovince").equals(rs.getGeneralString("cc_province"))) {
				sqls.add("update Customer set cu_province='" + rs.getGeneralString("cc_newprovince") + "' where cu_code='" + cucode + "'");
			}
			if (!rs.getGeneralString("cc_newarcustcode").equals(rs.getGeneralString("CC_ARCUSTCODE"))) {
				sqls.add("update Customer set CU_ARCUSTCODE='" + rs.getGeneralString("cc_newarcustcode") + "' where cu_code='" + cucode + "'");
			}
			if (!rs.getGeneralString("cc_newarcustname").equals(rs.getGeneralString("CC_ARCUSTNAME"))) {
				sqls.add("update Customer set CU_ARCUSTNAME='" + rs.getGeneralString("cc_newarcustname") + "' where cu_code='" + cucode + "'");
			}
			if (!rs.getGeneralString("cc_newmonthsend").equals(rs.getGeneralString("cc_monthsend"))) {
				sqls.add("update Customer set cu_monthsend='" + rs.getGeneralString("cc_newmonthsend") + "' where cu_code='" + cucode + "'");
			}
			if (!rs.getGeneralString("cc_newwebserver").equals(rs.getGeneralString("cc_webserver"))) {
				sqls.add("update Customer set cu_webserver='" + rs.getGeneralString("cc_newwebserver") + "' where cu_code='" + cucode + "'");
			}
			if (!rs.getGeneralString("cc_newdistrict").equals(rs.getGeneralString("CC_DISTRICT"))) {
				sqls.add("update Customer set CU_DISTRICT='" + rs.getGeneralString("cc_newdistrict") + "' where cu_code='" + cucode + "'");
			}
			if(!rs.getGeneralString("cc_newsellercode").equals(rs.getGeneralString("cc_sellercode"))){
				bool = true;
				sellecode = rs.getGeneralString("cc_newsellercode");
				sellename = rs.getGeneralString("cc_newsellername");
			}
			int argCount = baseDao.getCountByCondition("user_tab_columns",
					"table_name='CUSTOMERCHANGE' and column_name in ('CC_NEWCUNAME')");
			if (argCount == 1) {
				if (!rs.getGeneralString("cc_newcuname").equals(rs.getGeneralString("cc_cuname"))) {
					sqls.add("update Customer set cu_name='" + rs.getGeneralString("cc_newcuname") + "' where cu_code='" + cucode + "'");
					sqls.add("update Customer set cu_arname='" + rs.getGeneralString("cc_newcuname") + "' where cu_arcode='" + cucode + "'");
					sqls.add("update Customer set cu_shcustname='" + rs.getGeneralString("cc_newcuname") + "' where cu_shcustcode='" + cucode + "'");
				}
			}
			argCount = baseDao.getCountByCondition("user_tab_columns",
					"table_name='CUSTOMERCHANGE' and column_name in ('CC_NEWSHORTCUNAME','CC_SHORTCUNAME')");
			if (argCount == 2) {
				if(rs.getObject("cc_newshortcuname") != null){
					if (!rs.getGeneralString("cc_newshortcuname").equals(rs.getGeneralString("cc_shortcuname"))) {
						sqls.add("update Customer set cu_shortname='" + rs.getGeneralString("cc_newshortcuname") + "' where cu_code='" + cucode + "'");
					}
				}
			}
			argCount = baseDao.getCountByCondition("user_tab_columns",
					"table_name='CUSTOMERCHANGE' and column_name in ('CC_NEWENGNAME','CC_ENGNAME')");
			if (argCount == 2) {
				if(rs.getObject("cc_newengname") != null){
					if (!rs.getGeneralString("cc_newengname").equals(rs.getGeneralString("cc_engname"))) {
						sqls.add("update Customer set cu_engname='" + rs.getGeneralString("cc_newengname") + "' where cu_code='" + cucode + "'");
					}
				}
			}
		}
		baseDao.execute(sqls);
		if(bool && StringUtil.hasText(cucode) && StringUtil.hasText(sellecode)){
			SqlRowList rs1 = baseDao.queryForRowSet("select cu_id from customer where cu_code=?", cucode);
			if(rs1.next()){
				int count = baseDao.getCount("select count(*) from CustomerDistr where cd_cuid=" + rs1.getInt("cu_id") + " and cd_sellercode='" + sellecode
						+ "'");
				if (count == 0) {
					Object maxdetno = baseDao.getFieldDataByCondition("CustomerDistr", "max(cd_detno)", "cd_cuid=" + rs1.getInt("cu_id"));
					count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
					baseDao.execute("Insert into CustomerDistr(cd_id, cd_detno, cd_cuid, cd_sellercode, cd_seller, cd_custcode) values (?,?,?,?,?,?)",
							new Object[] { baseDao.getSeqId("CUSTOMERDISTR_SEQ"), count + 1, rs1.getInt("cu_id"), sellecode, sellename, cucode});
				}
			}
		}
		// 执行审核操作
		baseDao.audit("CustomerChange", "cc_id=" + id, "cc_status",
				"cc_statuscode", "cc_auditdate", "cc_auditer");
		baseDao.logger.audit(caller, "cc_id", id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}

	@Override
	public void submitCustomerChange(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CustomerChange",
				"cc_statuscode", "cc_id=" + id);
		StateAssert.submitOnlyEntering(status);
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		// 执行反审核操作
		baseDao.submit("CustomerChange", "cc_id=" + id, "cc_status",
				"cc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "cc_id", id);
		handlerService.handler(caller, "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitCustomerChange(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CustomerChange",
				"cc_statuscode", "cc_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		handlerService.handler(caller, "resCommit", "before",
				new Object[] { id });
		baseDao.resOperate("CustomerChange", "cc_id=" + id, "cc_status",
				"cc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "cc_id", id);
		handlerService.handler(caller, "resCommit", "after",
				new Object[] { id });
	}

	@Override
	public void resAuditCustomerChange(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CustomerChange",
				"cc_statuscode", "cc_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.handler(caller, "resAudit", "before",
				new Object[] { id });
		// 反应到客户表中
		String sql = "update Customer set(cu_sellerid,cu_sellercode,cu_sellername,cu_kind,"
				+ "cu_agenttype,cu_currency,cu_rate,cu_taxrate,cu_invoicetype,cu_paymentid,"
				+ "cu_paymentscode,cu_payments,cu_shipment,cu_contact,cu_degree,cu_mobile,cu_tel,cu_fax,cu_email) ="
				+ "(select cc_sellerid,cc_sellercode,cc_sellername,cc_cukind,"
				+ "cc_agenttype,cc_currency,cc_rate,cc_taxrate,cc_invoicetype,cc_paymentid,"
				+ "cc_paymentscode,cc_payments,cc_shipment,cc_contact,cc_degree,cc_mobile,"
				+ "cc_tel,cc_fax,cc_email from CustomerChange where cc_cucode=cu_code) where "
				+ "exists(select 1 from CustomerChange where cc_cucode=cu_code and cc_id="
				+ id + ")";
		baseDao.execute(sql);
		// 执行反审核操作
		baseDao.resOperate("CustomerChange", "cc_id=" + id, "cc_status",
				"cc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "cc_id", id);
		handlerService
				.handler(caller, "resAudit", "after", new Object[] { id });
	}

}
