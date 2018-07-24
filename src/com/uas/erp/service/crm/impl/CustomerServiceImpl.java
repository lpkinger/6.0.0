package com.uas.erp.service.crm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Master;
import com.uas.erp.service.crm.CustomerService;

@Service("CustomereService")
public class CustomerServiceImpl implements CustomerService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustomer(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存PreCustomer
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PreCustomer",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存Contact
		for (Map<Object, Object> s : grid) {
			s.put("ct_id", baseDao.getSeqId("CONTACT_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "Contact");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "cu_id", store.get("cu_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteCustomer(int cu_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("PreCustomer",
				"cu_statuscode", "cu_id=" + cu_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, cu_id);
		// 删除PreCustomer
		baseDao.deleteById("PreCustomer", "cu_id", cu_id);
		// 删除Contact
		baseDao.deleteById("Contact", "ct_cuid", cu_id);
		// 记录操作
		baseDao.logger.delete(caller, "cu_id", cu_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, cu_id);
	}

	@Override
	public void updateCustomerById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PreCustomer",
				"cu_statuscode", "cu_id=" + store.get("cu_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, grid });
		// 修改PreCustomer
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PreCustomer",
				"cu_id");
		baseDao.execute(formSql);
		if (gridStore != null && !gridStore.equals("")) {
			// 修改Contact
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
					"Contact", "ct_id");
			baseDao.execute(gridSql);
		}
		// 记录操作
		baseDao.logger.update(caller, "cu_id", store.get("cu_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, grid });
	}

	@Override
	public void submitCustomer(int cu_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PreCustomer",
				"cu_statuscode", "cu_id=" + cu_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, cu_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"PreCustomer",
				"cu_statuscode='COMMITED',cu_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "cu_id="
						+ cu_id);
		// 记录操作
		baseDao.logger.submit(caller, "cu_id", cu_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, cu_id);
	}

	@Override
	public void resSubmitCustomer(int cu_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PreCustomer",
				"cu_statuscode", "cu_id=" + cu_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, cu_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"PreCustomer",
				"cu_statuscode='ENTERING',cu_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "cu_id="
						+ cu_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "cu_id", cu_id);
		handlerService.afterResSubmit(caller, cu_id);
	}

	@Override
	public void auditCustomer(int cu_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PreCustomer",
				"cu_statuscode", "cu_id=" + cu_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, cu_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"PreCustomer",
				"cu_statuscode='AUDITED',cu_status='"
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
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PreCustomer",
				"cu_statuscode", "cu_id=" + cu_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, cu_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"PreCustomer",
				"cu_statuscode='ENTERING',cu_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',CU_AUDITMAN='',CU_AUDITDATE=null", "cu_id="
						+ cu_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "cu_id", cu_id);
		handlerService.afterResAudit(caller, cu_id);
	}

	@Override
	public String getSallerCodeByCustomerUU(Long customerUU) {
		return baseDao.getFieldValue("customer", "cu_sellercode", "cu_uu="
				+ customerUU, String.class);
	}

	@Override
	public String getNameByCustomerUU(Long customerUU) {
		return baseDao.getFieldValue("customer", "cu_name", "cu_uu="
				+ customerUU, String.class);
	}

	@Override
	public void checkCustomerUU(String data) {
		List<String> sqls = new ArrayList<String>();
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> m : maps) {
			if (m.get("cu_id") != null) {
				if (StringUtil.hasText(m.get("cu_uu"))
						&& m.get("cu_b2benable") != null
						&& Double.parseDouble(String.valueOf(m
								.get("cu_b2benable"))) == 0) {
					if (Double.parseDouble(String.valueOf(m.get("cu_uu"))) > 0) {
						String error = relationship(String.valueOf(m
								.get("cu_uu")));
						if (error != null) {
							BaseUtil.showError(error);
						} else {
							sqls.add("update Customer set cu_b2benable=1 where cu_id = "
									+ m.get("cu_id"));
						}
					}
				} else {
					sqls.add("update Customer set cu_b2benable=0 where  cu_id = "
							+ m.get("cu_id"));
				}
			}
		}
		if (sqls.size() > 0) {
			baseDao.execute(sqls);
		}
	}

	public String relationship(String custuu) {
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		if (StringUtil.hasText(master.getMa_uu())&&master.getMa_uu() > 0) {
			params.put("otheruu", custuu.toString());
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite()
								+ "/erp/relationship?access_id="
								+ master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					Map<String, Object> backInfo = FlexJsonUtil.fromJson(
							response.getResponseText(), HashMap.class);
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
}
