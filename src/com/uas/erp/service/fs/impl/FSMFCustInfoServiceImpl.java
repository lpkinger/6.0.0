package com.uas.erp.service.fs.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fs.FSMFCustInfoService;

@Service
public class FSMFCustInfoServiceImpl implements FSMFCustInfoService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveFSMFCustInfo(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		Object detno = baseDao.getFieldDataByCondition("FSMFCustInfo", "max(nvl(mf_detno,0))", "mf_cqid=" + store.get("mf_cqid"));
		detno = detno == null ? 0 : detno;
		int no = Integer.parseInt(detno.toString()) + 1;
		store.put("mf_detno", no);
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "FSMFCustInfo"));
		baseDao.execute("update FSMFCustInfo set (mf_status,mf_statuscode,mf_recorder,mf_indate)=(select cq_status,cq_statuscode,cq_recorder,cq_indate from CustomerQuota where mf_cqid=cq_id) where mf_id="
				+ store.get("mf_id") + " and nvl(mf_recorder,' ')=' '");
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "FSMFCustInfoDet", "mfd_id"));
		baseDao.execute("update FSMFCustInfoDet set mfd_cqid=" + store.get("mf_cqid") + " where mfd_mfid=" + store.get("mf_id"));
		baseDao.logger.save(caller, "mf_id", store.get("mf_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	@Override
	public void updateFSMFCustInfo(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "FSMFCustInfo", "mf_id"));
		Object detno = baseDao.getFieldDataByCondition("FSMFCustInfo", "max(nvl(mf_detno,0))", "mf_cqid=" + store.get("mf_cqid"));
		detno = detno == null ? 0 : detno;
		baseDao.execute("update FSMFCustInfo set mf_detno=" + (Integer.parseInt(detno.toString()) + 1) + " where mf_id="
				+ store.get("mf_id") + " and nvl(mf_detno,0)=0");
		baseDao.execute("update FSMFCustInfo set (mf_status,mf_statuscode,mf_recorder,mf_indate)=(select cq_status,cq_statuscode,cq_recorder,cq_indate from CustomerQuota where mf_cqid=cq_id) where mf_id="
				+ store.get("mf_id") + " and nvl(mf_recorder,' ')=' '");
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "FSMFCustInfoDet", "mfd_id"));
		baseDao.execute("update FSMFCustInfoDet set mfd_cqid=" + store.get("mf_cqid") + " where mfd_mfid=" + store.get("mf_id"));
		// 记录操作
		baseDao.logger.update(caller, "mf_id", store.get("mf_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void deleteFSMFCustInfo(int mf_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { mf_id });
		// 删除主表内容
		baseDao.deleteById("FSMFCustInfo", "mf_id", mf_id);
		baseDao.logger.delete(caller, "mf_id", mf_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { mf_id });
	}

	@Override
	public void submitFSMFCustInfo(int mf_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("FSMFCustInfo", new String[] { "mf_statuscode", "mf_cqid" }, "mf_id=" + mf_id);
		Object detno = baseDao.getFieldDataByCondition("FSMFCustInfo", "max(nvl(mf_detno,0))", "mf_cqid=" + status[1]);
		detno = detno == null ? 0 : detno;
		baseDao.execute("update FSMFCustInfo set mf_detno=" + (Integer.parseInt(detno.toString()) + 1) + " where mf_id=" + mf_id
				+ " and nvl(mf_detno,0)=0");
		baseDao.execute("update FSMFCustInfo set (mf_status,mf_statuscode,mf_recorder,mf_indate)=(select cq_status,cq_statuscode,cq_recorder,cq_indate from CustomerQuota where mf_cqid=cq_id) where mf_id="
				+ mf_id + " and nvl(mf_recorder,' ')=' '");
		baseDao.execute("update FSMFCustInfoDet set mfd_cqid=" + status[1] + " where mfd_mfid=" + mf_id);
		StateAssert.submitOnlyEntering(status[0]);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { mf_id });
		// 执行提交操作
		baseDao.submit("FSMFCustInfo", "mf_id=" + mf_id, "mf_status", "mf_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mf_id", mf_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { mf_id });
	}

	@Override
	public void resSubmitFSMFCustInfo(int mf_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FSMFCustInfo", "mf_statuscode", "mf_id=" + mf_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { mf_id });
		// 执行反提交操作
		baseDao.resOperate("FSMFCustInfo", "mf_id=" + mf_id, "mf_status", "mf_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mf_id", mf_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { mf_id });
	}

	@Override
	public void auditFSMFCustInfo(int mf_id, String caller) {
		// 只能对已提交进行审核操作
		Object[] status = baseDao.getFieldsDataByCondition("FSMFCustInfo", new String[] { "mf_statuscode", "mf_cqid" }, "mf_id=" + mf_id);
		StateAssert.auditOnlyCommited(status[0]);
		baseDao.execute("update CustomerQuota set cq_quota=nvl((select sum(mf_credit) from FSMFCUSTINFO where mf_cqid=cq_id),0) where cq_id="
				+ status[1]);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { mf_id });
		baseDao.audit("FSMFCustInfo", "mf_id=" + mf_id, "mf_status", "mf_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "mf_id", mf_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { mf_id });
	}

	@Override
	public void resAuditFSMFCustInfo(int mf_id, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("FSMFCustInfo", "mf_statuscode", "mf_id=" + mf_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("FSMFCustInfo", mf_id);
		handlerService.beforeResAudit(caller, new Object[] { mf_id });
		// 执行反审核操作
		baseDao.resOperate("FSMFCustInfo", "mf_id=" + mf_id, "mf_status", "mf_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "mf_id", mf_id);
		handlerService.afterResAudit(caller, new Object[] { mf_id });
	}

	@Override
	public void saveFSMFCustInfoDet(String gridStore) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		try {
			baseDao.execute(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "FSMFCustInfoDet", "mfd_id"));
			Object mfid = grid.get(0).get("mfd_mfid");
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select mfd_id,mfd_amount,fi_num10 from FSMFCUSTINFODET,CustomerQuota,faitems where mfd_cqid=cq_id and fi_cuname=cq_custname and substr(fi_year,0,4)=mfd_year and mfd_mfid=?",
							mfid);
			if (rs.next()) {
				if (rs.getGeneralDouble("fi_num10") != 0) {
					baseDao.execute("update FSMFCUSTINFODET set mfd_ratio=round("
							+ (rs.getGeneralDouble("mfd_amount") / rs.getGeneralDouble("fi_num10")) + ",2) where mfd_id="
							+ rs.getObject("mfd_id"));
				}
			}
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}

}
