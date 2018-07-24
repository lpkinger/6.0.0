package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.VoucherDetailService;

@Service("voucherDetailService")
public class VoucherDetailServiceImpl implements VoucherDetailService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveVoucherDetail(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Voucher",
				"vo_code='" + store.get("vo_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存Dispatch
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Voucher",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存DispatchDetail
		Object[] vd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			vd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				vd_id[i] = baseDao.getSeqId("VOUCHERDETAIL_SEQ");
			}
		} else {
			vd_id[0] = baseDao.getSeqId("VOUCHERDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"VoucherDetail", "vd_id", vd_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "vo_id", store.get("vo_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteVoucherDetail(int vo_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Voucher",
				"vo_statuscode", "vo_id=" + vo_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, vo_id);
		// 删除Dispatch
		baseDao.deleteById("Voucher", "vo_id", vo_id);
		// 删除DispatchDetail
		baseDao.deleteById("VoucherDetail", "vd_void", vo_id);
		// 记录操作
		baseDao.logger.delete(caller, "vo_id", vo_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, vo_id);
	}

	@Override
	public void updateVoucherDetailById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Voucher",
				"vo_statuscode", "vo_id=" + store.get("vo_id"));
		StateAssert.updateOnlyEntering(status);

		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改Dispatch
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Voucher",
				"vo_id");
		baseDao.execute(formSql);
		// 修改DispatchDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"VoucherDetail", "vd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("vd_id") == null || s.get("vd_id").equals("")
					|| s.get("vd_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("VOUCHERDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "VoucherDetail",
						new String[] { "vd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "vo_id", store.get("vo_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditVoucherDetail(int vo_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Voucher",
				"vo_statuscode", "vo_id=" + vo_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, vo_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"Voucher",
				"vo_statuscode='AUDITED',vo_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',vo_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',vo_auditdate=sysdate", "vo_id=" + vo_id);
		// 记录操作
		baseDao.logger.audit(caller, "vo_id", vo_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, vo_id);
	}

	@Override
	public void resAuditVoucherDetail(int vo_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Voucher",
				"vo_statuscode", "vo_id" + vo_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, vo_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"VoucherDetail",
				"vo_statuscode='ENTERING',vo_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',vo_auditer='',vo_auditdate=null", "vo_id=" + vo_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "vo_id", vo_id);
		handlerService.afterResAudit(caller, vo_id);
	}

	@Override
	public void submitVoucherDetail(int vo_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Voucher",
				"vo_statuscode", "vo_id=" + vo_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, vo_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"Voucher",
				"vo_statuscode='COMMITED',vo_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "vo_id="
						+ vo_id);
		// 记录操作
		baseDao.logger.submit(caller, "vo_id", vo_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, vo_id);
	}

	@Override
	public void resSubmitVoucherDetail(int vo_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Voucher",
				"vo_statuscode", "vo_id=" + vo_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, vo_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"Voucher",
				"vo_statuscode='ENTERING',vo_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "vo_id="
						+ vo_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "vo_id", vo_id);
		handlerService.afterResSubmit(caller, vo_id);
	}

}
