package com.uas.erp.service.scm.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.b2b.model.Enterprise;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.Assert;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.CustomerDao;
import com.uas.erp.model.Master;
import com.uas.erp.service.ma.EnterpriseService;
import com.uas.erp.service.oa.SendMailService;
import com.uas.erp.service.scm.CustomerBaseService;

@Service("customerBaseService")
public class CustomerBaseServiceImpl implements CustomerBaseService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private SendMailService sendMailService;

	@Transactional
	@Override
	public void saveCustomer(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);

		// /当前编号的记录已经存在,不能新增
		boolean bool = baseDao.checkByCondition("Customer", "cu_code='" + store.get("cu_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.save_codeHasExist"));
		}
		if (store.get("cu_id") == null || store.get("cu_id").equals("") || store.get("cu_id").equals("0")
				|| Integer.parseInt(store.get("cu_id").toString()) == 0) {
			store.put("cu_id", baseDao.getSeqId("CUSTOMER_SEQ"));
		}
		Object cuid = store.get("cu_id");
		Object cucode = store.get("cu_code");
		checkCustCode(cuid, cucode);

		Object cuname = store.get("cu_name");

		// 检查客户编号和客户名称前后是否有空格
		String patternAll = "^\\s+.*\\s+$";
		String patternBefore = "^\\s+.*$";
		String patternAfter = "^.*\\s+$";
		if (cucode.toString().matches(patternAll) || cucode.toString().matches(patternBefore) || cucode.toString().matches(patternAfter)) {
			BaseUtil.showError("客户编号前后不能有空白符");
		}
		if (cuname.toString().matches(patternAll) || cuname.toString().matches(patternBefore) || cuname.toString().matches(patternAfter)) {
			BaseUtil.showError("客户名称前后不能有空白符");
		}

		String nameerror = checkName(cucode, store.get("cu_name"), caller);
		String shortnameerror = checkShortName(cucode, store.get("cu_shortname"), caller);
		if (store.get("cu_shcustcode") == null || store.get("cu_shcustcode").equals("")) {
			store.put("cu_shcustcode", store.get("cu_code"));
			store.put("cu_shcustname", store.get("cu_name"));
		}
		if (store.get("cu_arcode") == null || store.get("cu_arcode").equals("")) {
			store.put("cu_arcode", store.get("cu_code"));
			store.put("cu_arname", store.get("cu_name"));
		}
		checkarshcode(cuid, store.get("cu_arcode"), store.get("cu_shcustcode"), store.get("cu_code"));
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByMap(store, "Customer");
		baseDao.execute(formSql);

		// 录入客户的时候默认最后跟进时间为录入时间
		baseDao.execute("update customer set cu_lastdate=sysdate where cu_code='" + cucode + "'");

		Object arname = baseDao.getFieldDataByCondition("Customer", "nvl(cu_name,' ')", "cu_code='" + store.get("cu_arcode") + "'");
		baseDao.execute("update Customer set cu_arname='" + StringUtil.nvl(arname, " ") + "' where cu_code<>cu_arcode and cu_id=" + cuid);
		baseDao.execute("update Customer set cu_arname=cu_name where cu_code=cu_arcode and cu_id=" + cuid);
		Object shname = baseDao.getFieldDataByCondition("Customer", "nvl(cu_name,' ')", "cu_code='" + store.get("cu_shcustcode") + "'");
		baseDao.execute("update Customer set cu_shcustname='" + StringUtil.nvl(shname, " ") + "' where cu_code<>cu_shcustcode and cu_id="
				+ cuid);
		baseDao.execute("update Customer set cu_shcustname=cu_name where cu_code=cu_shcustcode and cu_id=" + cuid);
		defaultCurrencyRate(caller, store.get("cu_id"));
		// 记录操作
		baseDao.logger.save(caller, "cu_id", store.get("cu_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
		CustomerDistr(store.get("cu_id"));
		if (nameerror != null) {
			BaseUtil.appendError(nameerror);
		}
		if (shortnameerror != null) {
			BaseUtil.appendError(shortnameerror);
		}
	}

	public void checkarshcode(Object cu_id, Object arcode, Object shcode, Object code) {
		String dets = null;
		if (!arcode.equals(code)) {
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(cu_code) from Customer where cu_code=? and cu_auditstatuscode='AUDITED'", String.class, arcode);
			if (dets == null) {
				BaseUtil.showError("应收客户不存在或者状态不等于已审核!应收客户号：" + arcode);
			}
		}
		if (!shcode.equals(code)) {
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(cu_code) from Customer where cu_code=? and cu_auditstatuscode='AUDITED'", String.class, shcode);
			if (dets == null) {
				BaseUtil.showError("收货客户不存在或者状态不等于已审核!收货客户号：" + shcode);
			}
		}
	}

	private void checkCustCode(Object cu_id, Object cucode) {
		// 判断客户编号在客户资料中是否存在重复
		String dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(cu_code) from Customer where cu_code=? and cu_id<>?",
				String.class, cucode, cu_id);
		if (dets != null) {
			BaseUtil.showError("客户编号在客户资料表中已存在!客户编号：" + dets);
		}
		// 判断客户编号在新客户引进资料中是否存在重复
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(cu_code) from PreCustomer where cu_code=? and cu_auditstatuscode<>'TURNED'", String.class, cucode);
		if (dets != null) {
			BaseUtil.showError("客户编号在客户引进申请中已存在!客户编号：" + dets);
		}
	}

	/**
	 * 客户全称重复
	 * 
	 * @param vCode
	 *            客户号
	 * @param name
	 *            客户全称
	 * @param language
	 *            语言
	 * @throws RuntimeException
	 */
	private String checkName(Object vCode, Object name, String caller) {
		Object precode = baseDao.getFieldDataByCondition("PreCustomer", "cu_code", "cu_code <> '" + vCode + "' AND cu_name='" + name + "'");
		Object code = baseDao.getFieldDataByCondition("Customer", "cu_code", "cu_code <> '" + vCode + "' AND cu_name='" + name + "'");
		if (precode != null) {
			if (baseDao.isDBSetting(caller, "allowNameRepeat"))
				return "客户名称在客户引进申请中已存在，申请单号：" + precode;
			else
				BaseUtil.showError("客户名称在客户引进申请中已存在，申请单号：" + precode);
		}
		if (code != null) {
			if (baseDao.isDBSetting(caller, "allowNameRepeat"))
				return "客户名称在客户资料中已存在，客户编号：" + code;
			else
				BaseUtil.showError("客户名称在客户资料中已存在，客户编号：" + code);
		}
		return null;
	}

	/**
	 * 客户简称重复
	 * 
	 * @param vCode
	 *            客户号
	 * @param name
	 *            客户简称
	 * @param language
	 *            语言
	 * @throws RuntimeException
	 */
	private String checkShortName(Object vCode, Object name, String caller) {
		Object precode = baseDao.getFieldDataByCondition("PreCustomer", "cu_code", "cu_code <> '" + vCode + "' AND cu_shortname='" + name
				+ "'");
		Object code = baseDao.getFieldDataByCondition("Customer", "cu_code", "cu_code <> '" + vCode + "' AND cu_shortname='" + name + "'");
		if (precode != null) {
			if (baseDao.isDBSetting(caller, "allowShortNameRepeat"))
				return "客户简称在客户引进申请中已存在，申请单号：" + precode;
			else
				BaseUtil.showError("客户简称在新客户引进申请中已存在，申请单号：" + precode);
		}
		if (code != null) {
			if (baseDao.isDBSetting(caller, "allowShortNameRepeat"))
				return "客户简称在新客户资料中已存在，客户编号：" + code;
			else
				BaseUtil.showError("客户简称在新客户资料中已存在，客户编号：" + code);
		}
		return null;
	}

	@Override
	public boolean checkCustomerByEnId(int cu_enid, int cu_otherenid) {
		return customerDao.checkCustomerByEnId(cu_enid, cu_otherenid);
	}

	@Override
	public void updateCustomer(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);

		if (store.get("cu_id") == null || store.get("cu_id").equals("") || store.get("cu_id").equals("0")
				|| Integer.parseInt(store.get("cu_id").toString()) == 0) {
			saveCustomer(formStore, caller);
		} else {
			// ID有数据，但不存在于数据库
			boolean isExist = baseDao.checkIf("Customer", "cu_id=" + store.get("cu_id"));
			if (!isExist) {
				saveCustomer(formStore, caller);
				return;
			}
			// 只能修改[在录入]的客户资料
			Object[] status = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_auditstatuscode", "cu_sellercode",
					"cu_servicecode" }, "cu_id=" + store.get("cu_id"));
			if (!"ENTERING".equals(status[0])) {
				BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.update_onlyEntering"));
			}

			Object cucode = store.get("cu_code");
			Object cuname = store.get("cu_name");
			// 检查客户编号和客户名称前后是否有空格
			String patternAll = "^\\s+.*\\s+$";
			String patternBefore = "^\\s+.*$";
			String patternAfter = "^.*\\s+$";
			if (cucode.toString().matches(patternAll) || cucode.toString().matches(patternBefore)
					|| cucode.toString().matches(patternAfter)) {
				BaseUtil.showError("客户编号前后不能有空白符");
			}
			if (cuname.toString().matches(patternAll) || cuname.toString().matches(patternBefore)
					|| cuname.toString().matches(patternAfter)) {
				BaseUtil.showError("客户名称前后不能有空白符");
			}
			Object cuid = store.get("cu_id");
			Object oldsellercode = status[1];
			Object newsellercode = store.get("cu_sellercode");
			Object oldservicecode = status[2];
			Object newservicecode = store.get("cu_servicecode");
			checkCustCode(cuid, store.get("cu_code"));
			if (store.get("cu_shcustcode") == null || store.get("cu_shcustcode").equals("")) {
				store.put("cu_shcustcode", store.get("cu_code"));
				store.put("cu_shcustname", store.get("cu_name"));
			}
			if (store.get("cu_arcode") == null || store.get("cu_arcode").equals("")) {
				store.put("cu_arcode", store.get("cu_code"));
				store.put("cu_arname", store.get("cu_name"));
			}
			String nameerror = checkName(store.get("cu_code"), store.get("cu_name"), caller);
			String shortnameerror = checkShortName(store.get("cu_code"), store.get("cu_shortname"), caller);
			checkarshcode(cuid, store.get("cu_arcode"), store.get("cu_shcustcode"), store.get("cu_code"));
			// 执行修改前的其它逻辑
			handlerService.handler(caller, "save", "before", new Object[] { store });
			// 执行修改操作
			String sql = SqlUtil.getUpdateSqlByFormStore(store, "Customer", "cu_id");
			baseDao.execute(sql);
			Object arname = baseDao.getFieldDataByCondition("Customer", "nvl(cu_name,' ')", "cu_code='" + store.get("cu_arcode") + "'");
			baseDao.execute("update Customer set cu_arname='" + StringUtil.nvl(arname, " ") + "' where cu_code<>cu_arcode and cu_id="
					+ cuid);
			baseDao.execute("update Customer set cu_arname=cu_name where cu_code=cu_arcode and cu_id=" + cuid);
			Object shname = baseDao.getFieldDataByCondition("Customer", "nvl(cu_name,' ')", "cu_code='" + store.get("cu_shcustcode") + "'");
			baseDao.execute("update Customer set cu_shcustname='" + StringUtil.nvl(shname, " ")
					+ "' where cu_code<>cu_shcustcode and cu_id=" + cuid);
			baseDao.execute("update Customer set cu_shcustname=cu_name where cu_code=cu_shcustcode and cu_id=" + cuid);
			baseDao.execute("update Customer set cu_updatedate=sysdate where  cu_id=" + cuid);
			defaultCurrencyRate(caller, store.get("cu_id"));
			// 记录操作
			baseDao.logger.update(caller, "cu_id", cuid);
			// 执行修改后的其它逻辑
			handlerService.handler(caller, "save", "after", new Object[] { store });
			CustomerDistr(cuid);
			if (StringUtil.hasText(oldsellercode) && !oldsellercode.equals(newsellercode) && !oldsellercode.equals(newservicecode)) {
				int count = baseDao.getCount("select count(*) from CustomerDistr where cd_cuid=" + cuid + " and cd_sellercode='"
						+ oldsellercode + "'");
				if (count > 0) {
					baseDao.execute("delete from CustomerDistr where cd_cuid=" + cuid + " and cd_sellercode='" + oldsellercode + "'");
					baseDao.execute("update CustomerDistr set cd_detno=rownum where cd_cuid=" + cuid);

				}
			}
			if (StringUtil.hasText(oldservicecode) && !oldservicecode.equals(newservicecode) && !oldservicecode.equals(newsellercode)) {
				int count = baseDao.getCount("select count(*) from CustomerDistr where cd_cuid=" + cuid + " and cd_sellercode='"
						+ oldservicecode + "'");
				if (count > 0) {
					baseDao.execute("delete from CustomerDistr where cd_cuid=" + cuid + " and cd_sellercode='" + oldservicecode + "'");
					baseDao.execute("update CustomerDistr set cd_detno=rownum where cd_cuid=" + cuid);

				}
			}
			baseDao.execute("update CustomerDistr set cd_remark=(select case when cd_cuid=cu_id and cd_sellercode=cu_sellercode then '是' else '否' end from customer where cu_id="
					+ cuid + " ) where cd_cuid=" + cuid + "");
			if (nameerror != null) {
				BaseUtil.appendError(nameerror);
			}
			if (shortnameerror != null) {
				BaseUtil.appendError(shortnameerror);
			}
		}
	}

	@Override
	public void deleteCustomer(int cu_id, String caller) {
		// 只能删除[在录入]的客户资料
		Object status = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_id=" + cu_id);
		Assert.isEquals("common.delete_onlyEntering", "ENTERING", status);
		Object code = baseDao.getFieldDataByCondition("Customer", "cu_code", "cu_id=" + cu_id);
		// 是否已产生业务数据
		baseDao.delCheck("customer", cu_id);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { cu_id });
		baseDao.execute("update PreCustomer set cu_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "', cu_auditstatuscode='ENTERING' WHERE cu_code='" + code + "'");
		// 执行删除操作
		baseDao.deleteById("Customer", "cu_id", cu_id);
		baseDao.execute("delete from CustomerAddress where ca_cuid=" + cu_id);
		baseDao.execute("delete from CustomerPayments where cp_cuid=" + cu_id);
		baseDao.execute("delete from CustomerDistr where cd_cuid=" + cu_id);
		baseDao.execute("delete from customermarks where CM_CUSTID=" + cu_id);
		// 记录操作
		baseDao.logger.delete(caller, "cu_id", cu_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { cu_id });
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 资料中心
			String groupName = BaseUtil.getXmlSetting("defaultSob");
			String allMasters = baseDao.getJdbcTemplate().queryForObject("select ma_soncode from " + groupName + ".master where ma_name=?",
					String.class, groupName);
			baseDao.execute("declare v_masters varchar2(1000);v_array str_table_type;v_m varchar2(30);v_sql varchar2(200);begin v_masters := '"
					+ allMasters
					+ "';v_array := parsestring(v_masters,',');for i in v_array.first()..v_array.last() loop v_m := v_array(i);v_sql := 'update '||v_m||'.Customer set cu_auditstatuscode=''DISABLE'',cu_auditstatus=''已禁用'' where cu_id="
					+ cu_id + "';execute immediate v_sql;end loop;COMMIT;end;");
		}
	}

	@Override
	public void auditCustomer(int cu_id, String caller) {
		// 只能审核[已提交]的客户
		Object[] status = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_auditstatuscode", "cu_code", "cu_shcustcode",
				"cu_arcode", "cu_name", "cu_shortname", "nvl(cu_credit,0)", "cu_uu", "cu_enablecredit" }, "cu_id=" + cu_id);
		boolean bool = baseDao.checkIf("CustomerCredit", "cuc_custcode='" + status[1] + "'");
		if (!bool && status[8] != null && "是".equals(status[8])) {
			baseDao.execute(
					"Insert into CustomerCredit(cuc_id, cuc_custcode,cuc_custname,cuc_recorder,cuc_status,cuc_statuscode,cuc_credit) values (customercredit_seq.nextval,?,?,'ADMIN','已审核','AUDITED',?)",
					new Object[] { status[1], status[4], status[6] });
		}
		StateAssert.auditOnlyCommited(status[0]);
		String nameerror = checkName(status[1], status[4], caller);
		String shortnameerror = checkShortName(status[1], status[5], caller);
		checkarshcode(cu_id, status[3], status[2], status[1]);
		allowZeroTax(caller, cu_id);
		defaultCurrencyRate(caller, cu_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, cu_id);
		//插入到客户分配表
		if (caller.equals("Customer!Base")) {
			Object[] cu_distr = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_distr" }, "cu_id=" + cu_id);
			if (cu_distr[0]!=null) {
				String[] emcodes = cu_distr[0].toString().split("#");
				for (int i = 0; i < emcodes.length; i++) {
					Object emname = baseDao.getFieldDataByCondition("employee", "em_name", "em_code='"+emcodes[i]+"'");					
					if (emname!=null) {
						//判断是否已经存在
						int count = baseDao.getCount("select count(*) from  CustomerDistr where cd_cuid="+ cu_id+" and cd_sellercode='"+emcodes[i]+"'");
						if (count==0) {
							int detno = baseDao.getCount("select max(cd_detno) from CustomerDistr where cd_cuid=" + cu_id);							
							baseDao.execute("Insert into CustomerDistr(cd_id, cd_detno, cd_cuid, cd_sellercode, cd_seller, cd_custcode, cd_remark) values (?,?,?,?,?,?,'否')",
									new Object[] { baseDao.getSeqId("CUSTOMERDISTR_SEQ"), detno + 1, cu_id, emcodes[i], emname.toString(), status[1] });							
						}
					}
				}
			}
		}		
		baseDao.audit("Customer", "cu_id=" + cu_id, "cu_auditstatus", "cu_auditstatuscode", "CU_AUDITDATE", "CU_AUDITMAN");
		baseDao.execute("update CustomerCredit set cuc_custname='" + status[4] + "' where cuc_custcode='" + status[1] + "'");
		// 记录操作
		baseDao.logger.audit(caller, "cu_id", cu_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, cu_id);
		// 执行客户资料将联系人信息关联到客户联系人
		customerToContact(caller, cu_id);
		// 审核之后去平台建立客户关系
		if (StringUtil.hasText(status[7])) {
			relationship(status[7].toString());

		}
		if (nameerror != null) {
			BaseUtil.appendError(nameerror);
		}
		if (shortnameerror != null) {
			BaseUtil.appendError(shortnameerror);
		}
	}

	private void customerToContact(String caller, int cu_id) {
		// 先判断客户联系人是否已经存数据
		Object[] ct_id = baseDao.getFieldsDataByCondition("contact", new String[] { "ct_id" }, "ct_cuid=" + cu_id);
		Object[] customerdata = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_contact", "cu_mobile", "cu_tel",
				"cu_degree", "cu_email" }, "cu_id=" + cu_id);
		if (ct_id == null || ct_id.length <= 0) {
			baseDao.execute(
					"Insert into contact(ct_id,ct_detno,ct_remark,ct_name,ct_mobile,ct_officephone,ct_position,ct_personemail,ct_cuid) values (contact_seq.nextval,1,'是',"
							+ "?,?,?,?,?,?)", new Object[] { customerdata[0], customerdata[1], customerdata[2], customerdata[3],
							customerdata[4], cu_id });
		}

	}

	@Override
	public void resAuditCustomer(int cu_id, String caller) {
		// 只能反审核[已审核]的客户
		Object status = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_id=" + cu_id);
		Assert.isEquals("common.resAudit_onlyAudit", "AUDITED", status);
		// 是否已产生业务数据
		baseDao.resAuditCheck("customer", cu_id);
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, cu_id);
		// 执行反审核操作
		baseDao.resAudit("Customer", "cu_id=" + cu_id, "cu_auditstatus", "cu_auditstatuscode", "CU_AUDITDATE", "CU_AUDITMAN");
		// 删除客户联系人信息
		baseDao.execute("delete (select * from contact where ct_cuid=" + cu_id + ")");
		//反审核根据cu_distr字段进行循环删除对应客户分配表的数据。
		if (caller.equals("Customer!Base")) {
			Object[] cu_distr = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_distr" }, "cu_id=" + cu_id);
			if (cu_distr[0]!=null) {
				String[] emcodes = cu_distr[0].toString().split("#");
				for (int i = 0; i < emcodes.length; i++) {
					Object emname = baseDao.getFieldDataByCondition("employee", "em_name", "em_code='"+emcodes[i]+"'");					
					if (emname!=null) {
						//判断是否已经存在
						int count = baseDao.getCount("select count(*) from  CustomerDistr where cd_cuid="+ cu_id+" and cd_sellercode='"+emcodes[i]+"'");
						if (count!=0) {
							baseDao.execute("delete CustomerDistr where cd_cuid="+cu_id+" and cd_sellercode='"+emcodes[i]+"'");
						}
					}
				}
			}
		}	
		// 记录操作
		baseDao.logger.resAudit(caller, "cu_id", cu_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, cu_id);
	}

	private void CustomerDistr(Object cu_id) {
		Object[] seller = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_sellercode", "cu_sellername", "cu_servicecode",
				"cu_servicename", "cu_code" }, "cu_id=" + cu_id);
		int count = 0;
		if (seller != null) {
			if (seller[0] != null && !"".equals(seller[0])) {
				count = baseDao.getCount("select count(*) from CustomerDistr where cd_cuid=" + cu_id + " and cd_sellercode='" + seller[0]
						+ "'");
				if (count == 0) {
					Object maxdetno = baseDao.getFieldDataByCondition("CustomerDistr", "max(cd_detno)", "cd_cuid=" + cu_id);
					count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
					baseDao.execute(
							"Insert into CustomerDistr(cd_id, cd_detno, cd_cuid, cd_sellercode, cd_seller, cd_custcode, cd_remark) values (?,?,?,?,?,?,'是')",
							new Object[] { baseDao.getSeqId("CUSTOMERDISTR_SEQ"), count + 1, cu_id, seller[0], seller[1], seller[4] });
				}
			}
			if (seller[2] != null && !"".equals(seller[2])) {
				count = baseDao.getCount("select count(*) from CustomerDistr where cd_cuid=" + cu_id + " and cd_sellercode='" + seller[2]
						+ "'");
				if (count == 0) {
					Object maxdetno = baseDao.getFieldDataByCondition("CustomerDistr", "max(cd_detno)", "cd_cuid=" + cu_id);
					count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
					baseDao.execute(
							"Insert into CustomerDistr(cd_id, cd_detno, cd_cuid, cd_sellercode, cd_seller, cd_custcode,cd_remark) values (?,?,?,?,?,?,'否')",
							new Object[] { baseDao.getSeqId("CUSTOMERDISTR_SEQ"), count + 1, cu_id, seller[2], seller[3], seller[4] });
				}
			}
		}
	}

	@Override
	public void submitCustomer(int cu_id, String caller) {
		if (baseDao.isDBSetting("Customer!Base", "creditControl")) {
			baseDao.execute("update customer set cu_enablecredit=(select pa_creditcontrol from payments where pa_code=cu_paymentscode and pa_class='收款方式') where cu_id="
					+ cu_id + " and nvl(cu_paymentscode,' ')<>' '");
		}
		// 只能提交[在录入]的资料
		Object[] status = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_auditstatuscode", "cu_code", "cu_shcustcode",
				"cu_arcode", "cu_name", "cu_shortname", "cu_enablecredit", "cu_paymentscode" }, "cu_id=" + cu_id);
		Assert.isEquals("common.submit_onlyEntering", "ENTERING", status[0]);
		String log = null;
		if (status[6] != null) {
			if (baseDao.checkIf("Payments", "pa_creditcontrol is not null and pa_class='收款方式' and pa_code='" + status[7]
					+ "' and pa_creditcontrol!='" + status[6] + "'")) {
				if (baseDao.isDBSetting("Customer!Base", "creditnoControl")) {
					log = "该客户的收款方式对应的是否额度管控与收款方式基础设置中对应的是否额度管控不一致";
				}else{
					BaseUtil.showError("该客户的收款方式对应的是否额度管控与收款方式基础设置中对应的是否额度管控不一致，请修改后再提交");
				}
			}
		}
		baseDao.execute(
				"update customer set cu_sellerid=nvl((select em_id from employee where em_code=cu_sellercode),0) where cu_id=? and nvl(cu_sellercode,' ')<>' '",
				cu_id);
		baseDao.execute(
				"update customer set cu_servicerid=nvl((select em_id from employee where em_code=cu_servicecode),0) where cu_id=? and nvl(cu_servicecode,' ')<>' '",
				cu_id);
		String nameerror = checkName(status[1], status[4], caller);
		String shortnameerror = checkShortName(status[1], status[5], caller);
		checkarshcode(cu_id, status[3], status[2], status[1]);
		Object addressObject = baseDao.getFieldDataByCondition("Customer", "cu_add1", "cu_id=" + cu_id);
		Object[] paymentcode = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_paymentscode", "cu_payments" }, "cu_id="
				+ cu_id);
		// 插入客户收货地址时电话不为空时取电话，电话为空时取手机号
		Object[] adds = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_contact", "nvl(cu_tel,cu_mobile)", "cu_fax",
				"cu_shadd" }, "cu_id=" + cu_id);
		allowZeroTax(caller, cu_id);
		defaultCurrencyRate(caller, cu_id);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { cu_id });
		// 执行提交操作
		baseDao.updateByCondition("Customer", "cu_auditstatuscode='COMMITED', cu_auditstatus='" + BaseUtil.getLocalMessage("COMMITED")
				+ "'", "cu_id=" + cu_id);
		int count = 0;
		if (addressObject != null) {
			String address = addressObject.toString().replace("'", "''");
			count = baseDao.getCount("select count(*) from CustomerAddress where ca_cuid=" + cu_id + " and ca_address='" + address.replace("'", "''") + "'");
			if (count == 0) {
				count = baseDao.getCount("select count(*) from CustomerAddress where ca_remark='是' and  ca_cuid=" + cu_id);
				if (count == 0) {
					Object maxdetno = baseDao.getFieldDataByCondition("CustomerAddress", "max(ca_detno)", "ca_cuid=" + cu_id);
					count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
					baseDao.execute(
							"Insert into CustomerAddress(ca_id, ca_detno, ca_cuid, ca_address,ca_remark,ca_person,ca_phone,ca_fax, ca_shcustname) values (?,?,?,?,?,?,?,?,?)",
							new Object[] { baseDao.getSeqId("CUSTOMERADDRESS_SEQ"), count + 1, cu_id, address, "是", adds[0], adds[1],
									adds[2], adds[3] });
				} else {
					baseDao.updateByCondition("CustomerAddress", "ca_remark=''", "ca_remark='是' and ca_cuid=" + cu_id);
					Object maxdetno = baseDao.getFieldDataByCondition("CustomerAddress", "max(ca_detno)", "ca_cuid=" + cu_id);
					count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
					baseDao.execute(
							"Insert into CustomerAddress(ca_id, ca_detno, ca_cuid, ca_address,ca_remark,ca_person,ca_phone,ca_fax, ca_shcustname) values (?,?,?,?,?,?,?,?,?)",
							new Object[] { baseDao.getSeqId("CUSTOMERADDRESS_SEQ"), count + 1, cu_id, address, "是", adds[0], adds[1],
									adds[2], adds[3] });
				}
			}
		}
		if (paymentcode != null && paymentcode[0] != null) {
			count = baseDao.getCount("select count(*) from CustomerPayments where cp_cuid=" + cu_id + " and cp_paymentcode='"
					+ paymentcode[0] + "'");
			if (count == 0) {
				count = baseDao.getCount("select count(*) from CustomerPayments where cp_isdefault='是' and  cp_cuid=" + cu_id);
				if (count == 0) {
					Object maxdetno = baseDao.getFieldDataByCondition("CustomerPayments", "max(cp_detno)", "cp_cuid=" + cu_id);
					count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
					baseDao.execute(
							"Insert into CustomerPayments(cp_id, cp_detno, cp_cuid, cp_paymentcode, cp_payment, cp_isdefault) values (?,?,?,?,?,?)",
							new Object[] { baseDao.getSeqId("CUSTOMERPAYMENTS_SEQ"), count + 1, cu_id, paymentcode[0], paymentcode[1], "是" });
				} else {
					baseDao.updateByCondition("CustomerPayments", "cp_isdefault=''", "cp_isdefault='是' and cp_cuid=" + cu_id);
					Object maxdetno = baseDao.getFieldDataByCondition("CustomerPayments", "max(cp_detno)", "cp_cuid=" + cu_id);
					count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
					baseDao.execute(
							"Insert into CustomerPayments(cp_id, cp_detno, cp_cuid, cp_paymentcode, cp_payment, cp_isdefault) values (?,?,?,?,?,?)",
							new Object[] { baseDao.getSeqId("CUSTOMERPAYMENTS_SEQ"), count + 1, cu_id, paymentcode[0], paymentcode[1], "是" });
				}
			}
		}
		CustomerDistr(cu_id);
		// 记录操作
		baseDao.logger.submit(caller, "cu_id", cu_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { cu_id });
		BaseUtil.showErrorOnSuccess(nameerror);
		BaseUtil.showErrorOnSuccess(shortnameerror);
		BaseUtil.showErrorOnSuccess(log);
	}

	@Override
	public void resSubmitCustomer(int cu_id, String caller) {
		// 只能对状态为[已提交]的合同进行反提交操作
		Object status = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_id=" + cu_id);
		Assert.isEquals("common.resSubmit_onlyCommited", "COMMITED", status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { cu_id });
		// 执行反提交操作
		baseDao.updateByCondition("Customer", "cu_auditstatuscode='ENTERING', cu_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "'", "cu_id=" + cu_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "cu_id", cu_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { cu_id });
	}

	@Override
	public void bannedCustomer(int cu_id, String caller) {
		// 执行禁用操作
		baseDao.updateByCondition("Customer", "cu_auditstatuscode='DISABLE', cu_auditstatus='" + BaseUtil.getLocalMessage("DISABLE") + "'",
				"cu_id=" + cu_id);
		// 记录操作
		baseDao.logger.banned(caller, "cu_id", cu_id);
	}

	@Override
	public void resBannedCustomer(int cu_id, String caller) {
		// 执行反禁用操作
		baseDao.updateByCondition("Customer", "cu_auditstatuscode='ENTERING', cu_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "'", "cu_id=" + cu_id);
		// 记录操作
		baseDao.logger.resBanned(caller, "cu_id", cu_id);
	}

	@Override
	public void updateCustomerCreditSet(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String sql = SqlUtil.getUpdateSqlByFormStore(store, "Customer", "cu_id");
		baseDao.execute(sql);
		// 记录操作
		baseDao.logger.others("更新客户信用额度", "msg.updateSuccess", caller, "cu_id", store.get("cu_id"));
	}

	@Override
	public void submitHandleHangCustomerBase(int id, String caller) {
		boolean bool = baseDao.checkIf("Jprocess", "jp_caller='Customer!HandleHang' and jp_keyvalue=" + id + "  and jp_status='待审批'");
		if (!bool) {
			bool = baseDao.checkIf("Jprocand", "jp_caller='Customer!HandleHang' and jp_keyvalue=" + id
					+ "  and jp_flag=1 and jp_status='待审批'");
		}
		if (bool) {
			BaseUtil.showError("当前单据存在相应的审批流 不允许重复提交!");
		} else {
			handlerService.launchProcess("Customer!HandleHang", id, "销售解挂流程");
		}
	}

	@Override
	public void HandleHangCustomerBase(int id, String caller) {
		baseDao.updateByCondition("Customer", "cu_auditstatus='已审核',cu_auditstatuscode='AUDITED',,cu_status='长期', cu_statuscode=null",
				"CU_ID=" + id);
	}

	// 本位币允许税率为0
	private void allowZeroTax(String caller, Object cu_id) {
		if (!baseDao.isDBSetting("Sale", "allowZeroTax")) {
			String currency = baseDao.getDBSetting("defaultCurrency");
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(cu_currency) from Customer where nvl(cu_taxrate,0)=0 and cu_currency='" + currency + "' and cu_id=?",
					String.class, cu_id);
			if (dets != null) {
				BaseUtil.showError("本位币税率为0，不允许进行当前操作!币别：" + dets);
			}
		}
	}

	// 税率强制等于币别表的默认税率
	private void defaultCurrencyRate(String caller, Object cu_id) {
		if (baseDao.isDBSetting("Customer!Base", "defaultCurrencyRate")) {
			baseDao.execute("update customer set cu_taxrate=(select nvl(cr_taxrate,0) from currencys where cu_currency=cr_name and cr_statuscode='CANUSE')"
					+ " where cu_id=" + cu_id);
		}
	}

	@Override
	public String getCustomerKindNum(String cu_kind) {
		Object[] objs = baseDao.getFieldsDataByCondition("CustomerKind", "ck_maxnum,ck_length,CK_EXCODE", "CK_KIND='" + cu_kind + "'");
		if (objs != null && objs[2] != null) {
			int ret = Integer.parseInt(objs[0].toString());
			int length = Integer.parseInt(objs[1].toString());
			String kind = objs[2].toString();
			SqlRowList rs = baseDao.queryForRowSet("select nvl( max(nvl(ck_maxnum,0)),0) from CustomerKind where CK_EXCODE=?", kind);
			if (rs.next()) {
				ret = rs.getGeneralInt(1);
			}
			ret++;
			baseDao.updateByCondition("CustomerKind", "ck_maxnum=" + ret, "CK_EXCODE='" + kind + "'");
			String number = "";
			length -= String.valueOf(ret).length();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					number += "0";
				}
			}
			number += String.valueOf(ret);
			number = kind + number;
			return number;
		}
		return null;
	}

	@Override
	public void hungCustomer(int id, String caller) {
		// 挂起
		baseDao.updateByCondition("Customer", "cu_statuscode='HUNG',cu_status='挂起'", "cu_id=" + id);
		// 记录操作
		baseDao.logger.others("挂起操作", "挂起成功", caller, "cu_id", id);
	}

	@Override
	public void reHungCustomer(int id, String caller) {
		// 挂起
		baseDao.updateByCondition("Customer", "cu_statuscode=null,cu_status='长期'", "cu_id=" + id);
		// 记录操作
		baseDao.logger.others("解挂操作", "解挂成功", caller, "cu_id", id);
	}

	// 去平台中建立客户关系
	public String relationship(String custuu) {
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		if (StringUtil.hasText(master.getMa_uu()) && master.getMa_uu() > 0) {
			params.put("otheruu", custuu.toString());
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/relationship?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
					if (backInfo.get("ok").equals(true)) {

					} else {
						return backInfo.get("error").toString();
					}
				}
			} catch (Exception e) {

			}
		}
		return null;
	}

	@Override
	public void regB2BCustomer(int id) {
		Enterprise enterprise = new Enterprise();
		Object[] objs = baseDao.getFieldsDataByCondition("Customer", "cu_name,cu_shortname,cu_businesscode,cu_contact,cu_mobile,cu_email",
				"cu_id=" + id);
		if (objs != null) {
			if (objs[0] == null || objs[3] == null || objs[4] == null) {
				BaseUtil.showError("客户名称、客户联系人、联系人电话都不能为空，请检查后再设置开通!");
			} else {
				enterprise.setEnName(objs[0].toString());
				enterprise.setEnShortname(objs[1].toString());
				enterprise.setEnBussinessCode(objs[2] == null ? "" : objs[2].toString());
				enterprise.setEnAdminName(objs[3].toString());
				enterprise.setEnAdminTel(objs[4].toString());
				enterprise.setEnAdminEmail(objs[5] == null ? "" : objs[5].toString());
				if (enterprise != null) {
					Map<String, Object> regInfos = enterpriseService.regEnterprise(enterprise);
					if (regInfos != null && regInfos.get("ok").equals(true)) {
						// 更新客户UU
						baseDao.updateByCondition("Customer", "cu_uu='" + regInfos.get("enUU") + "'", "cu_id=" + id);
						regB2BCustomer_sendMail(id);
					} else {
						BaseUtil.showError(regInfos.get("error").toString());
					}
				}
			}
		}
	}

	public void regB2BCustomer_sendMail(Integer id) {
		Object email = baseDao.getFieldDataByCondition("Customer", "cu_email", "cu_id=" + id);
		if (email == null || "".equals(email.toString().trim()) || "null".equals(email.toString().trim())) {
			return;
			// BaseUtil.showError("供应商邮箱为空，无法发送邮件!");
		}
		// 标题和内容一致
		String encop = baseDao.getFieldDataByCondition("enterprise", "en_name", "1=1").toString();
		Object[] objs = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_code", "cu_name", "cu_uu", "cu_contact",
				"cu_mobile" }, "cu_id=" + id);
		String title = "请查看客户资料，客户编号：" + objs[0];
		String contextdetail = "<P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>"
				+ objs[1]
				+ "，您好！：<SPAN lang=EN-US><?xml:namespace prefix = 'o' ns = 'urn:schemas-microsoft-com:office:office' /><o:p></o:p></SPAN></SPAN></P>"
				+ "<P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN lang=EN-US style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'><SPAN style='mso-spacerun: yes'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ " </SPAN></SPAN><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>您的合作伙伴<SPAN style='COLOR: blue'>"
				+ "（<SPAN lang=EN-US>"
				+ encop
				+ "</SPAN>）</SPAN>将您列为其合格供应商(客户),并为您注册了优软商务平台，登录账号为贵司的<SPAN lang=EN-US style='COLOR: blue'>"
				+ objs[3]
				+ "</SPAN>的手机号码，密码默认6个1。"
				+ "<SPAN lang=EN-US>,</SPAN>请及时登入优软商务平台查看相关的业务信息<SPAN lang=EN-US>!<o:p></o:p></SPAN></SPAN></P><P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>登入平台的地址：<SPAN lang=EN-US><A href='http://www.ubtob.com/'><FONT color=#0000ff>www.ubtob.com</FONT></A>"
				+ "<o:p></o:p></SPAN></SPAN></P><P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>如在使用平台过程中，遇到任何操作问题，请及时与深圳市优软科技有限公司客服人员（连小姐）联系，联系电话：<SPAN lang=EN-US>0755-26996828<o:p></o:p></SPAN></SPAN></P>"
				+ "<SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑; mso-font-kerning: 1.0pt; mso-ansi-language: EN-US; mso-fareast-language: ZH-CN; mso-bidi-language: AR-SA'>致敬！</SPAN>";
		sendMailService.sendSysMail(title, contextdetail, email.toString());
	}

}
