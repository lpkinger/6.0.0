package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.CustomerDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.fa.CustomerBaseFPService;

@Service("customerBaseFPService")
public class CustomerBaseFPServiceImpl implements CustomerBaseFPService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustomerFP(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// /当前编号的记录已经存在,不能新增
		boolean bool = baseDao.checkByCondition("Customer", "cu_code='" + store.get("cu_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.save_codeHasExist"));
		}
		checkCustomerName(store);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByMap(store, "Customer");
		baseDao.execute(formSql);
		// 保存ShareholderInformation
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ShareholderInformation");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "cu_id", store.get("cu_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	/**
	 * 客户名称、简称不能重复
	 * 
	 * @param store
	 * @param language
	 */
	private void checkCustomerName(Map<Object, Object> store) {
		// 客户全称重复
		String code = baseDao.getFieldValue("Customer", "cu_code",
				"cu_code <> '" + store.get("cu_code") + "' AND cu_name='" + store.get("cu_name") + "'", String.class);
		if (code != null) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.save_nameHasExist") + code);
		}
		// 客户简称重复
		code = baseDao.getFieldValue("Customer", "cu_code",
				"cu_code <> '" + store.get("cu_code") + "' AND cu_shortname='" + store.get("cu_shortname") + "'", String.class);
		if (code != null) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.save_shortnameHasExist") + code);
		}
	}

	@Override
	public boolean checkCustomerFPByEnId(int cu_enid, int cu_otherenid) {
		return customerDao.checkCustomerByEnId(cu_enid, cu_otherenid);
	}

	@Override
	public void updateCustomerFP(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		if (store.get("cu_id") == null || store.get("cu_id").equals("") || store.get("cu_id").equals("0")
				|| Integer.parseInt(store.get("cu_id").toString()) == 0) {
			saveCustomerFP(formStore, gridStore, caller);
		} else {
			// ID有数据，但不存在于数据库
			boolean isExist = baseDao.checkIf("Customer", "cu_id=" + store.get("cu_id"));
			if (!isExist) {
				saveCustomerFP(formStore, gridStore, caller);
				return;
			}
			// 执行修改前的其它逻辑
			handlerService.beforeUpdate(caller, new Object[] { store });
			// 执行修改操作
			String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Customer", "cu_id");
			baseDao.execute(formSql);
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ShareholderInformation", "shi_id");
			for (Map<Object, Object> s : grid) {
				if (s.get("shi_id") == null || s.get("shi_id").equals("") || s.get("shi_id").equals("0")
						|| Integer.parseInt(s.get("shi_id").toString()) == 0) {// 新添加的数据，id不存在
					int id = baseDao.getSeqId("ShareholderInformation_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "ShareholderInformation", new String[] { "shi_id" }, new Object[] { id });
					gridSql.add(sql);
				}
			}
			baseDao.execute(gridSql);
			String sqls = "update shareholderinformation set shi_cucode='" + store.get("cu_code") + "' where shi_cuid="
					+ store.get("cu_id");
			baseDao.execute(sqls);
			// 记录操作
			baseDao.logger.update(caller, "cu_id", store.get("cu_id"));
			// 执行修改后的其它逻辑
			handlerService.afterUpdate(caller, new Object[] { store });
		}
	}

	@Override
	public void deleteCustomerFP(int cu_id, String caller) {
		// 只能删除[在录入]的客户资料
		Object status = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_id=" + cu_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.delete_onlyEntering"));
		}
		Object code = baseDao.getFieldDataByCondition("Customer", "cu_code", "cu_id=" + cu_id);
		// 不能删除已有销售合同的客户资料
		Object sacode = baseDao.getFieldDataByCondition("Sale", "sa_code", "sa_custcode='" + code + "'");
		if (sacode != null) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.delete_cucodeHasExist") + sacode);
		}
		// 不能删除已有发货单的客户资料
		Object picode = baseDao.getFieldDataByCondition("ProdInOut", "pi_code", "pi_cardcode='" + code + "'");
		if (picode != null) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.delete_picodeHasExist") + picode);
		}
		// 不能删除已有销售发票的客户资料
		Object abcode = baseDao.getFieldDataByCondition("ARBill", "ab_code", "ab_custcode='" + code + "'");
		if (abcode != null) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.delete_abcodeHasExist") + abcode);
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { cu_id });
		baseDao.execute("update PreCustomer set cu_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "', cu_auditstatuscode='ENTERING' WHERE cu_code='" + code + "'");
		// 执行删除操作
		baseDao.deleteById("Customer", "cu_id", cu_id);
		// 删除ShareholderInformation
		baseDao.deleteById("ShareholderInformation", "shi_cuid", cu_id);
		// 记录操作
		baseDao.logger.delete(caller, "cu_id", cu_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, cu_id);
		Employee employee = SystemSession.getUser();
		Master master = employee.getCurrentMaster();
		if (master != null && master.getMa_type() == 1) {// 资料中心
			String groupName = BaseUtil.getXmlSetting("defaultSob");
			String allMasters = baseDao.getJdbcTemplate().queryForObject("select ma_soncode from " + groupName + ".master where ma_name=?",
					String.class, groupName);
			baseDao.execute("declare v_masters varchar2(1000);v_array str_table_type;v_m varchar2(30);v_sql varchar2(100);begin v_masters := '"
					+ allMasters
					+ "';v_array := parsestring(v_masters,',');for i in v_array.first()..v_array.last() loop v_m := v_array(i);v_sql := 'update '||v_m||'.Customer set cu_auditstatuscode='DISABLE' where cu_id="
					+ cu_id + "';execute immediate v_sql;end loop;COMMIT;end;");
		}
	}

	@Override
	public void auditCustomerFP(int cu_id, String caller) {
		// 只能审核[已提交]的客户
		Object[] status = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_auditstatuscode", "cu_add1" }, "cu_id=" + cu_id);
		if (!status[0].equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.audit_uncommit"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, cu_id);
		Employee employee = SystemSession.getUser();
		baseDao.updateByCondition("Customer", "cu_auditstatuscode='AUDITED',cu_auditstatus='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',cu_auditman='" + employee.getEm_name() + "',cu_auditdate=sysdate", "cu_id=" + cu_id);
		Master master = employee.getCurrentMaster();
		if (master != null && master.getMa_soncode() != null) {// 资料中心
			String res = null;
			res = baseDao.callProcedure("SYS_POST", new Object[] { "Customer!Post", SpObserver.getSp(), master.getMa_soncode(), String.valueOf(cu_id),
					employee.getEm_name(), employee.getEm_id() });
			/*
			 * if (res != null) { BaseUtil.appendError(employee.getEm_code()); }
			 */
		}
		// 记录操作
		baseDao.logger.audit(caller, "cu_id", cu_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, cu_id);
	}

	@Override
	public void resAuditCustomerFP(int cu_id, String caller) {
		// 只能反审核[已审核]的客户
		Object status = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_id=" + cu_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.resaudit_onlyAudited"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, cu_id);
		// 执行反审核操作
		baseDao.updateByCondition("Customer", "cu_auditstatuscode='ENTERING', cu_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "'", "cu_id=" + cu_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "cu_id", cu_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, cu_id);
	}

	@Override
	public void submitCustomerFP(int cu_id, String caller) {
		// 只能提交[在录入]的资料
		Object[] status = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_auditstatuscode", "cu_code", "cu_shcustcode",
				"cu_arcode", "cu_enablecredit", "cu_paymentscode" }, "cu_id=" + cu_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.submit_onlyEntering"));
		}
		
		// 应收客户
		baseDao.execute("update customer set cu_arname=cu_name,cu_arcode=cu_code where cu_id=? and nvl(cu_arcode,' ')=' '", cu_id);
		baseDao.execute(
				"update customer set cu_sellerid=nvl((select em_id from employee where em_code=cu_sellercode),0) where cu_id=? and nvl(cu_sellercode,' ')<>' '",
				cu_id);
		baseDao.execute(
				"update customer set cu_servicerid=nvl((select em_id from employee where em_code=cu_servicecode),0) where cu_id=? and nvl(cu_servicecode,' ')<>' '",
				cu_id);

		Object addressObject = baseDao.getFieldDataByCondition("Customer", "cu_add1", "cu_id=" + cu_id);
		Object[] adds = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_contact", "cu_tel", "cu_fax" }, "cu_id=" + cu_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, cu_id);
		// 执行提交操作
		baseDao.updateByCondition("Customer", "cu_auditstatuscode='COMMITED', cu_auditstatus='" + BaseUtil.getLocalMessage("COMMITED")
				+ "'", "cu_id=" + cu_id);

		int count = baseDao.getCount("select count(*) from CustomerAddress where ca_cuid=" + cu_id + " and ca_address='" + (addressObject == null?"":addressObject.toString().replace("'", "''"))
				+ "'");
		if (count == 0) {
			count = baseDao.getCount("select count(*) from CustomerAddress where ca_remark='是' and  ca_cuid=" + cu_id);
			if (count == 0) {
				Object maxdetno = baseDao.getFieldDataByCondition("CustomerAddress", "max(ca_detno)", "ca_cuid=" + cu_id);
				count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
				baseDao.execute(
						"Insert into CustomerAddress(ca_id, ca_detno, ca_cuid, ca_address,ca_remark,ca_person,ca_phone,ca_fax) values (?,?,?,?,?,?,?,?)",
						new Object[] { baseDao.getSeqId("CUSTOMERADDRESS_SEQ"), count + 1, cu_id, addressObject, "是", adds[0], adds[1],
								adds[2] });
			} else {
				baseDao.updateByCondition("CustomerAddress", "ca_remark=''", "ca_remark='是' and ca_cuid=" + cu_id);
				Object maxdetno = baseDao.getFieldDataByCondition("CustomerAddress", "max(ca_detno)", "ca_cuid=" + cu_id);
				count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
				baseDao.execute(
						"Insert into CustomerAddress(ca_id, ca_detno, ca_cuid, ca_address,ca_remark,ca_person,ca_phone,ca_fax) values (?,?,?,?,?,?,?,?)",
						new Object[] { baseDao.getSeqId("CUSTOMERADDRESS_SEQ"), count + 1, cu_id, addressObject, "是", adds[0], adds[1],
								adds[2] });
			}
		}
		// 记录操作
		baseDao.logger.submit(caller, "cu_id", cu_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, cu_id);
	}

	@Override
	public void resSubmitCustomerFP(int cu_id, String caller) {
		// 只能对状态为[已提交]的合同进行反提交操作
		Object status = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_id=" + cu_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.ressubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, cu_id);
		// 执行反提交操作
		baseDao.updateByCondition("Customer", "cu_auditstatuscode='ENTERING', cu_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "'", "cu_id=" + cu_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "cu_id", cu_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, cu_id);
	}

	@Override
	public void bannedCustomerFP(int cu_id, String caller) {
		// 执行禁用操作
		baseDao.updateByCondition("Customer", "cu_auditstatuscode='DISABLE', cu_auditstatus='" + BaseUtil.getLocalMessage("DISABLE") + "'",
				"cu_id=" + cu_id);
	}

	@Override
	public void resBannedCustomerFP(int cu_id, String caller) {
		// 执行反禁用操作
		baseDao.updateByCondition("Customer", "cu_auditstatuscode='ENTERING', cu_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "'", "cu_id=" + cu_id);
	}

	@Override
	public void updateCustomerFPCreditSet(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String sql = SqlUtil.getUpdateSqlByFormStore(store, "Customer", "cu_id");
		baseDao.execute(sql);
	}

	public void submitHandleHangCustomerBaseFP(int cu_id, String caller) {
		boolean bool = baseDao.checkIf("Jprocess", "jp_caller='Customer!HandleHang' and jp_keyvalue=" + cu_id + "  and jp_status='待审批'");
		if (!bool) {
			bool = baseDao.checkIf("Jprocand", "jp_caller='Customer!HandleHang' and jp_keyvalue=" + cu_id
					+ "  and jp_flag=1 and jp_status='待审批'");
		}
		if (bool) {
			BaseUtil.showError("当前单据存在相应的审批流 不允许重复提交!");
		} else {
			handlerService.launchProcess("Customer!HandleHang", cu_id, "销售解挂流程");
		}

	}

	@Override
	public void HandleHangCustomerBaseFP(int cu_id, String caller) {
		baseDao.updateByCondition("Customer", "cu_auditstatus='已审核',cu_auditstatuscode='AUDITED',cu_status='长期', cu_statuscode=null",
				"CU_ID=" + cu_id);
	}

}
