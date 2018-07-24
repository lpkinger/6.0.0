package com.uas.erp.service.drp.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.CustomerDao;

import com.uas.erp.model.MessageLog;
import com.uas.erp.service.drp.CustomerTypeService;

@Service("customerTypeService")
public class CustomerTypeServiceImpl implements CustomerTypeService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustomer(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);

		// /当前编号的记录已经存在,不能新增
		boolean bool = baseDao.checkByCondition("Customer",
				"cu_code='" + store.get("cu_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("drp.distribution.customer.save_codeHasExist"));
		}
		if (store.get("cu_id") == null || store.get("cu_id").equals("")
				|| store.get("cu_id").equals("0")
				|| Integer.parseInt(store.get("cu_id").toString()) == 0) {
			store.put("cu_id", baseDao.getSeqId("CUSTOMER_SEQ"));
		}

		if (store.get("cu_shcustcode") == null
				|| store.get("cu_shcustcode").equals("")) {
			store.put("cu_shcustcode", String.valueOf(store.get("cu_code")));
			store.put("cu_shcustname", String.valueOf(store.get("cu_name")));
		}
		if (store.get("cu_arcode") == null || store.get("cu_arcode").equals("")) {
			store.put("cu_arcode", String.valueOf(store.get("cu_code")));
			store.put("cu_arname", String.valueOf(store.get("cu_name")));
		}
		// 执行修改前的其它逻辑
		handlerService.handler("Customer!Base", "save", "before",
				new Object[] { store.get(0) });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Customer",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);

		// 记录操作
		baseDao.logger.save(caller, "cu_id", store.get("cu_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("Customer!Base", "save", "after",
				new Object[] { store.get(0) });
	}

	@Override
	public boolean checkCustomerByEnId(int cu_enid, int cu_otherenid) {
		return customerDao.checkCustomerByEnId(cu_enid, cu_otherenid);
	}

	@Override
	public void updateCustomer(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if (store.get("cu_id") == null || store.get("cu_id").equals("")
				|| store.get("cu_id").equals("0")
				|| Integer.parseInt(store.get("cu_id").toString()) == 0) {
			saveCustomer(formStore, SystemSession.getUser().getEm_name());
		} else {
			// 只能修改[在录入]的客户资料
			Object status = baseDao.getFieldDataByCondition("Customer",
					"cu_auditstatuscode", "cu_id=" + store.get("cu_id"));
			if (!status.equals("ENTERING")) {
				BaseUtil.showError(BaseUtil
						.getLocalMessage("drp.distribution.customer.update_onlyEntering"));
			}
			if (store.get("cu_shcustcode") == null
					|| store.get("cu_shcustcode").equals("")) {
				store.put("cu_shcustcode", store.get("cu_code"));
				store.put("cu_shcustname", store.get("cu_name"));
			}
			if (store.get("cu_arcode") == null
					|| store.get("cu_arcode").equals("")) {
				store.put("cu_arcode", store.get("cu_code"));
				store.put("cu_arname", store.get("cu_name"));
			}
			// 执行修改前的其它逻辑
			handlerService.beforeUpdate(caller, new Object[] { store });
			// 执行修改操作
			String sql = SqlUtil.getUpdateSqlByFormStore(store, "Customer",
					"cu_id");
			baseDao.execute(sql);
			// 客户的名称改了之后，应收客户的名称也需要一起改
			baseDao.execute(
					"update customer set cu_arname=cu_name where cu_id=? and cu_arcode=cu_code",
					store.get("cu_id"));
			baseDao.execute(
					"update customer set cu_shcustname=cu_name where cu_id=? and cu_shcustcode=cu_code",
					store.get("cu_id"));
			// 记录操作
			baseDao.logger.update(caller, "cu_id", store.get("cu_id"));
			// 执行修改后的其它逻辑
			handlerService.afterUpdate(caller, new Object[] { store });
		}
	}

	@Override
	public void deleteCustomer(int cu_id, String caller) {
		// 只能删除[在录入]的客户资料
		Object status = baseDao.getFieldDataByCondition("Customer",
				"cu_auditstatuscode", "cu_id=" + cu_id);
		StateAssert.delOnlyEntering(status);
		Object code = baseDao.getFieldDataByCondition("Customer", "cu_code",
				"cu_id=" + cu_id);
		// 不能删除已有销售合同的客户资料
		Object sacode = baseDao.getFieldDataByCondition("Sale", "sa_code",
				"sa_custcode='" + code + "'");
		if (sacode != null) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("scm.sale.customer.delete_cucodeHasExist"
							+ sacode));
		}
		// 不能删除已有发货单的客户资料
		Object picode = baseDao.getFieldDataByCondition("ProdInOut", "pi_code",
				"pi_cardcode='" + code + "'");
		if (picode != null) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("scm.sale.customer.delete_picodeHasExist"
							+ picode));
		}
		// 不能删除已有销售发票的客户资料
		Object abcode = baseDao.getFieldDataByCondition("ApBill", "ab_code",
				"ab_vendcode='" + code + "'");
		if (abcode != null) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("scm.sale.customer.delete_abcodeHasExist"
							+ abcode));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, cu_id);
		// 执行删除操作
		baseDao.deleteById("Customer", "cu_id", cu_id);
		// 记录操作
		baseDao.logger.delete(caller, "cu_id", cu_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, cu_id);
	}

	@Override
	public void auditCustomer(int cu_id, String caller) {
		// 只能审核[已提交]的客户
		Object status = baseDao.getFieldDataByCondition("Customer",
				"cu_auditstatuscode", "cu_id=" + cu_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, cu_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"Customer",
				"cu_auditstatuscode='AUDITED', cu_auditstatus='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',CU_AUDITMAN='"
						+ SystemSession.getUser().getEm_name()
						+ "',CU_AUDITDATE=sysdate", "cu_id=" + cu_id);
		// 记录操作
		baseDao.logger.audit(caller, "cu_id", cu_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, cu_id);
	}

	@Override
	public void resAuditCustomer(int cu_id, String caller) {
		// 只能反审核[已审核]的客户
		Object status = baseDao.getFieldDataByCondition("Customer",
				"cu_auditstatuscode", "cu_id=" + cu_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, cu_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"Customer",
				"cu_auditstatuscode='ENTERING', cu_auditstatus='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',CU_AUDITMAN='',CU_AUDITDATE=null", "cu_id="
						+ cu_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "cu_id", cu_id);
		handlerService.afterResAudit(caller, cu_id);
	}

	@Override
	public void submitCustomer(int cu_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Customer",
				"cu_auditstatuscode", "cu_id=" + cu_id);
		StateAssert.submitOnlyEntering(status);
		baseDao.execute(
				"update customer set cu_sellerid=nvl((select em_id from employee where em_code=cu_sellercode),0) where cu_id=? and nvl(cu_sellercode,' ')<>' '",
				cu_id);
		baseDao.execute(
				"update customer set cu_servicerid=nvl((select em_id from employee where em_code=cu_servicecode),0) where cu_id=? and nvl(cu_servicecode,' ')<>' '",
				cu_id);
		baseDao.execute(
				"update customer set cu_arname=cu_name,cu_arcode=cu_code where cu_id=? and nvl(cu_arname,' ')=' '",
				cu_id);
		baseDao.execute(
				"update customer set cu_shcustname=cu_name,cu_shcustcode=cu_code where cu_id=? and nvl(cu_shcustcode,' ')=' '",
				cu_id);
		Object addressObject = baseDao.getFieldDataByCondition("Customer",
				"cu_add1", "cu_id=" + cu_id);
		Object[] adds = baseDao.getFieldsDataByCondition("Customer",
				new String[] { "cu_contact", "cu_tel", "cu_fax" }, "cu_id="
						+ cu_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, cu_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"Customer",
				"cu_auditstatuscode='COMMITED', cu_auditstatus='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "cu_id="
						+ cu_id);
		// 记录操作
		baseDao.logger.submit(caller, "cu_id", cu_id);
		int count = baseDao
				.getCount("select count(*) from CustomerAddress where ca_cuid="
						+ cu_id + " and ca_address='" + addressObject + "'");
		if (count == 0) {
			count = baseDao
					.getCount("select count(*) from CustomerAddress where ca_remark='是' and  ca_cuid="
							+ cu_id);
			if (count == 0) {
				Object maxdetno = baseDao.getFieldDataByCondition(
						"CustomerAddress", "max(ca_detno)", "ca_cuid=" + cu_id);
				count = maxdetno == null ? 0 : Integer.valueOf(maxdetno
						.toString());
				baseDao.execute(
						"Insert into CustomerAddress(ca_id, ca_detno, ca_cuid, ca_address,ca_remark,ca_person,ca_phone,ca_fax) values (?,?,?,?,?,?,?,?)",
						new Object[] { baseDao.getSeqId("CUSTOMERADDRESS_SEQ"),
								count + 1, cu_id, addressObject, "是", adds[0],
								adds[1], adds[2] });
			} else {
				baseDao.updateByCondition("CustomerAddress", "ca_remark=''",
						"ca_remark='是' and ca_cuid=" + cu_id);
				Object maxdetno = baseDao.getFieldDataByCondition(
						"CustomerAddress", "max(ca_detno)", "ca_cuid=" + cu_id);
				count = maxdetno == null ? 0 : Integer.valueOf(maxdetno
						.toString());
				baseDao.execute(
						"Insert into CustomerAddress(ca_id, ca_detno, ca_cuid, ca_address,ca_remark,ca_person,ca_phone,ca_fax) values (?,?,?,?,?,?,?,?)",
						new Object[] { baseDao.getSeqId("CUSTOMERADDRESS_SEQ"),
								count + 1, cu_id, addressObject, "是", adds[0],
								adds[1], adds[2] });
			}
		}
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, cu_id);
	}

	@Override
	public void resSubmitCustomer(int cu_id, String caller) {
		// 只能对状态为[已提交]的合同进行反提交操作
		Object status = baseDao.getFieldDataByCondition("Customer",
				"cu_auditstatuscode", "cu_id=" + cu_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, cu_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"Customer",
				"cu_auditstatuscode='ENTERING', cu_auditstatus='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "cu_id="
						+ cu_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "cu_id", cu_id);
		handlerService.afterResSubmit(caller, cu_id);
	}

	@Override
	public void bannedCustomer(int cu_id, String caller) {
		// 执行禁用操作
		baseDao.updateByCondition(
				"Customer",
				"cu_auditstatuscode='DISABLE', cu_auditstatus='"
						+ BaseUtil.getLocalMessage("DISABLE") + "'", "cu_id="
						+ cu_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.banned"), BaseUtil
						.getLocalMessage("msg.bannedSuccess"), "cu_id=" + cu_id));
	}

	@Override
	public void resBannedCustomer(int cu_id, String caller) {
		// 执行反禁用操作
		baseDao.updateByCondition(
				"Customer",
				"cu_auditstatuscode='ENTERING', cu_auditstatus='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "cu_id="
						+ cu_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.resBanned"), BaseUtil
						.getLocalMessage("msg.resBannedSuccess"), "cu_id="
						+ cu_id));
	}
}
