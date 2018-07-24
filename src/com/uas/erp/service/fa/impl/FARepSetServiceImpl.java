package com.uas.erp.service.fa.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Status;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.FARepSet;
import com.uas.erp.model.FARepSet.FARepSetDet;
import com.uas.erp.service.fa.FARepSetService;

@Service("faRepSetService")
public class FARepSetServiceImpl implements FARepSetService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveFARepSet(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("FaRepSet", "fs_code='" + store.get("fs_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存FARepSet
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FaRepSet", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存FARepSetDetail
		Object[] fsd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			fsd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				fsd_id[i] = baseDao.getSeqId("FARepSetDET_SEQ");
			}
		} else {
			fsd_id[0] = baseDao.getSeqId("FARepSetDET_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "FARepSetDet", "fsd_id", fsd_id);
		baseDao.execute(gridSql);
		baseDao.execute("update FARepSetDet set fsd_code=(select fs_code from FARepSet where fsd_fsid=fs_id) where fsd_fsid=?",
				store.get("fs_id"));
		try {
			// 记录操作
			baseDao.logger.save(caller, "fs_id", store.get("fs_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteFARepSet(int fs_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, fs_id);
		// 删除Dispatch
		baseDao.deleteById("FARepSet", "fs_id", fs_id);
		// 删除DispatchDetail
		baseDao.deleteById("FARepSetDet", "fsd_fsid", fs_id);
		// 记录操作
		baseDao.logger.delete(caller, "fs_id", fs_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, fs_id);
	}

	@Override
	public void updateFARepSetById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);

		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改Dispatch
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FARepSet", "fs_id");
		baseDao.execute(formSql);
		// 修改DispatchDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "FARepSetDet", "fsd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("fsd_id") == null || s.get("fsd_id").equals("") || s.get("fsd_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("FAREPSETDET_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "FARepSetDet", new String[] { "fsd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update FARepSetDet set fsd_code=(select fs_code from FARepSet where fsd_fsid=fs_id) where fsd_fsid=?",
				store.get("fs_id"));
		// 记录操作
		baseDao.logger.update(caller, "fs_id", store.get("fs_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void auditFARepSet(int fs_id, String caller) {
		// 只能对状态为[已提交]的单据进行审核操作!
		Object status = baseDao.getFieldDataByCondition("FARepSet", "fs_statuscode", "fs_id=" + fs_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, fs_id);
		// 执行审核操作
		baseDao.updateByCondition("FARepSet", "fs_statuscode='AUDITED',fs_status='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',fs_auditer='" + SystemSession.getUser().getEm_name() + "',fs_auditdate=sysdate", "fs_id=" + fs_id);
		// 记录操作
		baseDao.logger.audit(caller, "fs_id", fs_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, fs_id);
	}

	@Override
	public void resAuditFARepSet(int fs_id, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("FARepSet", "fs_statuscode", "fs_id=" + fs_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, fs_id);
		// 执行反审核操作
		baseDao.updateByCondition("FARepSet", "fs_statuscode='ENTERING',fs_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',fs_auditer='',fs_auditdate=null", "fs_id=" + fs_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "fs_id", fs_id);
		handlerService.afterResAudit(caller, fs_id);
	}

	@Override
	public void submitFARepSet(int fs_id, String caller) {
		// 只能对状态为[在录入]的进行提交操作!
		Object status = baseDao.getFieldDataByCondition("FARepSet", "fs_statuscode", "fs_id=" + fs_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, fs_id);
		// 执行提交操作
		baseDao.updateByCondition("FARepSet", "fs_statuscode='COMMITED',fs_status='" + BaseUtil.getLocalMessage("COMMITED") + "'", "fs_id="
				+ fs_id);
		// 记录操作
		baseDao.logger.submit(caller, "fs_id", fs_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, fs_id);
	}

	@Override
	public void resSubmitFARepSet(int fs_id, String caller) {
		// 只能对状态为[已提交]的单据进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FARepSet", "fs_statuscode", "fs_id=" + fs_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, fs_id);
		// 执行反提交操作
		baseDao.updateByCondition("FARepSet", "fs_statuscode='ENTERING',fs_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "fs_id="
				+ fs_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "fs_id", fs_id);
		handlerService.afterResSubmit(caller, fs_id);
	}

	@Override
	public FARepSet getFARepSet(int fs_id) {
		FARepSet repSet = baseDao.queryBean("select * from FARepSet where fs_id=?", FARepSet.class, fs_id);
		if (null != repSet) {
			repSet.setDets(baseDao.query("select * from FARepSetDet where fsd_fsid=?", FARepSetDet.class, fs_id));
		}
		return repSet;
	}

	@Override
	public void saveFARepSet(FARepSet repSet) {
		Integer oldId = baseDao.queryForObject("select fs_id from FARepSet where fs_code=?", Integer.class, repSet.getFs_code());
		if (null != oldId) {
			// 覆盖原公式
			baseDao.deleteById("FARepSet", "fs_id", oldId);
			baseDao.deleteById("FARepSetDet", "fsd_fsid", oldId);
		}
		// 重新取ID
		int newId = baseDao.getSeqId("FARepSet_SEQ");
		repSet.setFs_id(newId);
		repSet.setFs_indate(new Date());
		if (null != repSet.getDets()) {
			for (FARepSetDet det : repSet.getDets()) {
				det.setFsd_fsid(newId);
			}
		}
		baseDao.save(repSet, "FARepSet");
		baseDao.save(repSet.getDets(), "FARepSetDet");
	}

	/**
	 * 复制
	 */
	@Override
	public JSONObject copyFARepSet(int id, String caller) {
		Map<String, Object> dif = new HashMap<String, Object>();
		int nId = baseDao.getSeqId("FAREPSET_SEQ");
		dif.put("fs_id", nId);
		String code = baseDao.sGetMaxNumber("FARepSet", 2);
		dif.put("fs_code", "'" + code + "'");
		dif.put("fs_recorder", "'" + SystemSession.getUser().getEm_name() + "'");
		dif.put("fs_status", "'" + Status.ENTERING.display() + "'");
		dif.put("fs_statuscode", "'" + Status.ENTERING.code() + "'");
		dif.put("fs_indate", "sysdate");
		dif.put("fs_auditer", "null");
		dif.put("fs_auditdate", "null");
		baseDao.copyRecord("FARepSet", "FARepSet", "fs_id=" + id, dif);
		// Copy明细
		SqlRowList list = baseDao.queryForRowSet("SELECT fsd_id FROM FARepSetDet WHERE fsd_fsid=?", id);
		Integer dId = null;
		while (list.next()) {
			dif = new HashMap<String, Object>();
			dId = baseDao.getSeqId("FAREPSETDET_SEQ");
			dif.put("fsd_id", dId);
			dif.put("fsd_fsid", nId);
			baseDao.copyRecord("FARepSetDet", "FARepSetDet", "fsd_id=" + list.getInt("fsd_id"), dif);
		}
		baseDao.logger.others("报表公式复制", "成功复制到" + code, caller, "fs_id", id);
		JSONObject obj = new JSONObject();
		obj.put("fs_id", nId);
		obj.put("fs_code", code);
		return obj;
	}
}
