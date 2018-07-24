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


import com.uas.erp.service.crm.VendorDistrApplyService;

@Service(value = "vendorDistrApplyService")
public class VendorDistrApplyServiceImpl implements VendorDistrApplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveVendorDistrApply(String formStore,
			String gridStore,  String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		for (Map<Object, Object> m : gstore) {
			m.put("vad_id", baseDao.getSeqId("VendorDistrApplyDet_SEQ"));
			m.put("vad_veid", store.get("va_veid"));
		}
		handlerService.beforeSave(caller, new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VendorDistrApply", new String[] {}, new String[] {});
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "VendorDistrApplyDet");
		baseDao.execute(gridSql);
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "va_id", store.get("va_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});
	}

	@Override
	public void deleteVendorDistrApply(int va_id,  String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, va_id);
		baseDao.deleteById("VendorDistrApplyDet", "vad_vaid", va_id);
		baseDao.deleteById("VendorDistrApply", "va_id", va_id);
		// 记录操作
		baseDao.logger.delete(caller, "va_id", va_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, va_id);
	}

	@Override
	public void updateVendorDistrApply(String formStore, String gridStore,  String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VendorDistrApply", "va_id");
		// 修改MProjectPlanDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "VendorDistrApplyDet", "vad_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("vad_id") == null || s.get("vad_id").equals("") || s.get("vad_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("VendorDistrApplyDet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "VendorDistrApplyDet", new String[] { "vad_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(formSql);
		baseDao.execute(gridSql);
		// 如果主记录改变了客户，反应到明细上
		baseDao.updateByCondition("VendorDistrApplyDet", "vad_veid=" + store.get("va_veid"),
				"vad_vaid=" + store.get("va_id"));
		// 记录操作
		baseDao.logger.update(caller, "va_id", store.get("va_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
	}

	@Override
	public void submitVendorDistrApply(int va_id,  String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("VendorDistrApply",
				"va_statuscode", "va_id=" + va_id);
		StateAssert.submitOnlyEntering(status);
		// 如果客户已分配了此业务员，则不允许提交
		List<Object[]> gridData = baseDao.getFieldsDatasByCondition("VendorDistrApplyDet", new String[] { "vad_veid",
				"vad_personcode", "vad_person" }, "vad_vaid=" + va_id);
		for (Object[] data : gridData) {
			int i = baseDao.getCountByCondition("VendorDistr", "vd_veid=" + data[0] + " and vd_personcode='" + data[1]
					+ "'");
			if (i > 0) {
				BaseUtil.showError(data[2] + "已存在于该供应商的的分配列表中");
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, va_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"VendorDistrApply",
				"va_statuscode='COMMITED',va_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'",
				"va_id=" + va_id);
		// 记录操作
		baseDao.logger.submit(caller, "va_id", va_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, va_id);
	}

	@Override
	public void resSubmitVendorDistrApply(int va_id,  String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("VendorDistrApply",
				"va_statuscode", "va_id=" + va_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, va_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"VendorDistrApply",
				"va_statuscode='ENTERING',va_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"va_id=" + va_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "va_id", va_id);
		handlerService.afterResSubmit(caller, va_id);
	}

	@Override
	public void auditVendorDistrApply(int va_id,  String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("VendorDistrApply",
				"va_statuscode", "va_id=" + va_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, va_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"VendorDistrApply",
				"va_statuscode='AUDITED',va_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "',Va_auditor='" + SystemSession.getUser().getEm_name()
						+ "',Va_auditdate=" + DateUtil.parseDateToOracleString(null, new Date()),
				"va_id=" + va_id);
		// 反应到客户分配表中
		String insertSql = "INSERT INTO VendorDistr(vd_id,vd_veid,vd_personcode,vd_person,vd_detno) SELECT "
				+ baseDao.getSeqId("VendorDistr_SEQ") +
				",vad_veid,vad_personcode,vad_person,vad_detno from VendorDistrApplyDet where vad_vaid=" + va_id;
		baseDao.execute(insertSql);
		// 记录操作
		baseDao.logger.audit(caller, "va_id", va_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, va_id);
	}

	@Override
	public void resAuditVendorDistrApply(int va_id,  String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("VendorDistrApply",
				"va_statuscode", "va_id=" + va_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, va_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"VendorDistrApply",
				"va_statuscode='ENTERING',va_status='" + BaseUtil.getLocalMessage("ENTERING")
						+ "',va_auditor='',va_auditdate=null", "va_id=" + va_id);
		// 反应到客户分配表中
		List<Object[]> gridData = baseDao.getFieldsDatasByCondition("VendorDistrApplyDet", new String[] { "vad_veid",
				"vad_personcode" }, "vad_vaid=" + va_id);
		for (Object[] o : gridData) {
			String deleteSql = "DELETE FROM VendorDistr WHERE vd_veid=" + o[0]
					+ " AND vd_personcode='" + o[1] + "'";
			baseDao.execute(deleteSql);
			;
		}
		// 记录操作
		baseDao.logger.resAudit(caller, "va_id", va_id);
		handlerService.afterResAudit(caller, va_id);
	}

}
