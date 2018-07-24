package com.uas.erp.service.crm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.crm.CustomerDistrApplyService;

@Service(value = "customerDistrApplyService")
public class CustomerDistrApplyServiceImpl implements CustomerDistrApplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustomerDistrApply(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		for (Map<Object, Object> m : gstore) {
			m.put("cad_id", baseDao.getSeqId("CustomerDistrApplyDet_SEQ"));
			m.put("cad_cuid", store.get("ca_cuid"));
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CustomerDistrApply", new String[] {}, new String[] {});
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "CustomerDistrApplyDet");
		baseDao.execute(gridSql);
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ca_id", store.get("ca_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteCustomerDistrApply(int ca_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ca_id);
		// 删除purchase
		baseDao.deleteById("CustomerDistrApplyDet", "cad_caid", ca_id);
		baseDao.deleteById("CustomerDistrApply", "ca_id", ca_id);
		// 记录操作
		baseDao.logger.delete(caller, "ca_id", ca_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ca_id);
	}

	@Override
	public void updateCustomerDistrApply(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CustomerDistrApply", "ca_id");
		// 修改MProjectPlanDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "CustomerDistrApplyDet", "cad_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("cad_id") == null || s.get("cad_id").equals("") || s.get("cad_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("CustomerDistrApplyDet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "CustomerDistrApplyDet", new String[] { "cad_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(formSql);
		baseDao.execute(gridSql);
		// 如果主记录改变了客户，反应到明细上
		baseDao.updateByCondition("CustomerDistrApplyDet", "cad_cuid=" + store.get("ca_cuid"), "cad_caid=" + store.get("ca_id"));
		// 记录操作
		baseDao.logger.update(caller, "ca_id", store.get("ca_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void submitCustomerDistrApply(int ca_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("CustomerDistrApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.submitOnlyEntering(status);
		// 如果客户已分配了此业务员，则不允许提交
		List<Object[]> gridData = baseDao.getFieldsDatasByCondition("CustomerDistrApplyDet", new String[] { "cad_cuid", "cad_sellercode",
				"cad_seller" }, "cad_caid=" + ca_id);
		for (Object[] data : gridData) {
			int i = baseDao.getCountByCondition("CustomerDistr", "cd_cuid=" + data[0] + " and cd_sellercode='" + data[1] + "'");
			if (i > 0) {
				BaseUtil.showError("业务员" + data[2] + "已存在于该客户的的分配列表中，请核对后重试！");
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ca_id);
		// 执行提交操作
		baseDao.updateByCondition("CustomerDistrApply",
				"ca_statuscode='COMMITED',ca_status='" + BaseUtil.getLocalMessage("COMMITED") + "'", "ca_id=" + ca_id);
		// 记录操作
		baseDao.logger.submit(caller, "ca_id", ca_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ca_id);
	}

	@Override
	public void resSubmitCustomerDistrApply(int ca_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		handlerService.beforeResSubmit(caller, ca_id);
		Object status = baseDao.getFieldDataByCondition("CustomerDistrApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.updateByCondition("CustomerDistrApply",
				"ca_statuscode='ENTERING',ca_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "ca_id=" + ca_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ca_id", ca_id);
		handlerService.afterResSubmit(caller, ca_id);
	}

	@Override
	public void auditCustomerDistrApply(int ca_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("CustomerDistrApply", new String[] { "ca_statuscode", "ca_cucode","ca_cuid"}, "ca_id="
				+ ca_id);
		StateAssert.auditOnlyCommited(status[0]);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ca_id);
		// 执行审核操作
		baseDao.updateByCondition("CustomerDistrApply",
				"ca_statuscode='AUDITED',ca_status='" + BaseUtil.getLocalMessage("AUDITED") + "',ca_auditor='"
						+ SystemSession.getUser().getEm_name() + "',ca_auditdate=" + DateUtil.parseDateToOracleString(null, new Date()),
				"ca_id=" + ca_id);
		int detno=0;
		Object  detnoObject=baseDao.getFieldDataByCondition("CustomerDistr", "max(cd_detno)", "cd_cuid="+status[2]);
		if(detnoObject!=null){
			detno=Integer.parseInt(detnoObject.toString());
		}
		// 反应到客户分配表中
		String insertSql = "INSERT INTO CustomerDistr(cd_id,cd_cuid,cd_custcode,cd_sellercode,cd_seller,cd_detno) SELECT"
				+ " CustomerDistr_SEQ.nextval,cad_cuid,ca_cucode,cad_sellercode,cad_seller,rownum+"+detno+" from CustomerDistrApplyDet left join CustomerDistrApply on cad_caid=ca_id where cad_caid="
				+ ca_id;
		baseDao.execute(insertSql);
		// 客户分配审核后自动同步客户分配表到所有帐套
		if (baseDao.isDBSetting("CustomerDistrApply", "autoCustomerDistr")) {
			Employee employee = SystemSession.getUser();
			Object cuid = baseDao.getFieldDataByCondition("Customer", "cu_id", "cu_code='" + status[1] + "'");
			if (cuid != null) {
				Master master = employee.getCurrentMaster();
				if (master != null && master.getMa_soncode() != null) {// 资料中心
					String res = null;
					res = baseDao.callProcedure("SYS_POST", new Object[] { "CustomerDistr!Post", SpObserver.getSp(),
							master.getMa_soncode(), String.valueOf(cuid), employee.getEm_name(), employee.getEm_id() });
					if (res != null) {
						BaseUtil.showError(res);
					}
				}
			}
		}
		// 记录操作
		baseDao.logger.audit(caller, "ca_id", ca_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ca_id);
	}

	@Override
	public void resAuditCustomerDistrApply(int ca_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("CustomerDistrApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ca_id);
		// 执行反审核操作
		baseDao.updateByCondition("CustomerDistrApply", "ca_statuscode='ENTERING',ca_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',ca_auditor='',ca_auditdate=null", "ca_id=" + ca_id);
		// 反应到客户分配表中
		List<Object[]> gridData = baseDao.getFieldsDatasByCondition("CustomerDistrApplyDet", new String[] { "cad_cuid", "cad_sellercode" },
				"cad_caid=" + ca_id);
		for (Object[] o : gridData) {
			String deleteSql = "DELETE FROM CustomerDistr WHERE cd_cuid=" + o[0] + " AND cd_sellercode='" + o[1] + "'";
			baseDao.execute(deleteSql);
			;
		}
		// 记录操作
		baseDao.logger.resAudit(caller, "ca_id", ca_id);
		handlerService.afterResAudit(caller, ca_id);
	}

}
