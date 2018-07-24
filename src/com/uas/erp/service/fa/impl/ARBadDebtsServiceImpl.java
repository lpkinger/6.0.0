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

import com.uas.erp.service.fa.ARBadDebtsService;

@Service("arBadDebtsService")
public class ARBadDebtsServiceImpl implements ARBadDebtsService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveARBadDebts(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ARBadDebts", "bd_code='"
				+ store.get("bd_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存SalePrice
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ARBadDebts",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存SalePriceDetail
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			map.put("bdd_id", baseDao.getSeqId("ARBADDEBTSDETAIL_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"ARBadDebtsDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "bd_id", store.get("bd_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteARBadDebts(int bd_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ARBadDebts",
				"bd_auditstatuscode", "bd_id=" + bd_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, bd_id);
		// 删除SalePrice
		baseDao.deleteById("ARBadDebts", "bd_id", bd_id);
		// 删除SalePriceDetail
		baseDao.deleteById("ARBadDebtsdetail", "bdd_bdid", bd_id);
		// 记录操作
		baseDao.logger.delete(caller, "bd_id", bd_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, bd_id);
	}

	@Override
	public void updateARBadDebtsById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ARBadDebts",
				"bd_statuscode", "bd_id=" + store.get("bd_id"));
		StateAssert.updateOnlyEntering(status);

		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改SalePrice
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ARBadDebts",
				"bd_id");
		baseDao.execute(formSql);
		// 修改SalePriceDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"ARBadDebtsDetail", "bdd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("bdd_id") == null || s.get("bdd_id").equals("")
					|| s.get("bdd_id").equals("0")
					|| Integer.parseInt(s.get("bdd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ARBADDEBTSDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ARBadDebtsDetail",
						new String[] { "bdd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "bd_id", store.get("bd_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void printARBadDebts(int bd_id, String caller) {
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("ARBadDebts",
				"bd_statuscode", "bd_id=" + bd_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.print_onlyAudit"));
		}
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, bd_id);
		// 执行打印操作
		baseDao.logger.print(caller, "bd_id", bd_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, bd_id);
	}

	@Override
	public void auditARBadDebts(int bd_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ARBadDebts",
				"bd_statuscode", "bd_id=" + bd_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, bd_id);

		// 执行审核操作
		baseDao.updateByCondition(caller, "bd_statuscode='AUDITED',bd_status='"
				+ BaseUtil.getLocalMessage("AUDITED") + "',bd_auditer='"
				+ SystemSession.getUser().getEm_name()
				+ "',bd_auditdate=sysdate", "bd_id=" + bd_id);

		// 记录操作
		baseDao.logger.audit(caller, "bd_id", bd_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, bd_id);
	}

	@Override
	public void resAuditARBadDebts(int bd_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ARBadDebts",
				"bd_statuscode", "bd_id=" + bd_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, bd_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				caller,
				"bd_statuscode='ENTERING',bd_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',bd_auditer='',bd_auditdate=null", "bd_id=" + bd_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "bd_id", bd_id);
		handlerService.afterResAudit(caller, bd_id);
	}

	@Override
	public void submitARBadDebts(int bd_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ARBadDebts",
				"bd_statuscode", "bd_id=" + bd_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, bd_id);
		// 执行提交操作
		baseDao.updateByCondition(
				caller,
				"bd_statuscode='COMMITED',bd_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "bd_id="
						+ bd_id);
		// 记录操作
		baseDao.logger.submit(caller, "bd_id", bd_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, bd_id);
	}

	@Override
	public void resSubmitARBadDebts(int bd_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ARBadDebts",
				"bd_statuscode", "bd_id=" + bd_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, bd_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"ARBadDebts",
				"bd_statuscode='ENTERING',bd_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "bd_id="
						+ bd_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "bd_id", bd_id);
		handlerService.afterSubmit(caller, bd_id);
	}

	@Override
	public void postARBadDebts(int bd_id, String caller) {
		Object[] status = baseDao.getFieldsDataByCondition("ARBadDebts",
				new String[] { "bd_statuscode" }, "bd_id=" + bd_id);
		if (status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.post_onlyUnPost"));
		}
		// 过账前的其它逻辑
		handlerService.beforePost(caller, bd_id);
		// 执行过账操作
		// Object obj = baseDao.getFieldDataByCondition("ARBadDebts", "bd_code",
		// "bd_id=" + bd_id);
		// 存储过程
		String res = baseDao.callProcedure("SP_COMMITEARBAD", new Object[] {
				bd_id, 1 });
		if (res != null && !res.trim().equals("OK")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition(
				"ARBadDebts",
				"bd_statuscode='POSTED',bd_status='"
						+ BaseUtil.getLocalMessage("POSTED") + "'", "bd_id="
						+ bd_id);
		// 记录操作
		baseDao.logger.post(caller, "bd_id", bd_id);
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller, bd_id);
	}

	@Override
	public void resPostARBadDebts(int bd_id, String caller) {
		// 只能对状态为[已过账]的单据进行反过账操作!
		Object status = baseDao.getFieldDataByCondition("ARBadDebts",
				"bd_statuscode", "bd_id=" + bd_id);
		StateAssert.resPostOnlyPosted(status);

		// 执行反过账操作
		// Object obj = baseDao.getFieldDataByCondition("ARBadDebts", "bd_code",
		// "bd_id=" + bd_id);
		handlerService.beforeResPost(caller, bd_id);
		// 存储过程
		String res = baseDao.callProcedure("SP_UNCOMMITEARBAD", new Object[] {
				bd_id, 1 });
		if (res != null && !res.trim().equals("OK")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("ARBadDebts",
				"bd_auditstatuscode='ENTERING',bd_statuscode='UNPOST',bd_auditstatus='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',bd_status='" + BaseUtil.getLocalMessage("UNPOST")
						+ "'", "bd_id=" + bd_id);
		// 记录操作
		baseDao.logger.resPost(caller, "bd_id", bd_id);
		handlerService.afterResPost(caller, bd_id);
	}

}
