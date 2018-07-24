package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.AssetsCardChangeDao;

import com.uas.erp.service.fa.AssetsCardChangeService;

@Service("assetsCardChangeService")
public class AssetsCardChangeServiceImpl implements AssetsCardChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private AssetsCardChangeDao assetsCardChangeDao;

	@Override
	public void saveAssetsCardChange(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("AssetsCardChange",
				"acc_code='" + store.get("acc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"AssetsCardChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "acc_id", store.get("acc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateAssetsCardChangeById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("AssetsCardChange",
				"acc_statuscode", "acc_id=" + store.get("acc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"AssetsCardChange", "acc_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "acc_id", store.get("acc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteAssetsCardChange(int acc_id, String caller) {
		// 只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("AssetsCardChange",
				"acc_statuscode", "acc_id=" + acc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, acc_id);
		// 删除
		baseDao.deleteById("AssetsCardChange", "acc_id", acc_id);
		// 记录操作
		baseDao.logger.delete(caller, "acc_id", acc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, acc_id);
	}

	@Override
	public void auditAssetsCardChange(int acc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("AssetsCardChange",
				"acc_statuscode", "acc_id=" + acc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, acc_id);
		// 信息自动反馈到卡片
		String accode = assetsCardChangeDao.turnAssetsCard(acc_id, caller);
		// 执行审核操作
		baseDao.audit("AssetsCardChange", "acc_id=" + acc_id, "acc_status", "acc_statuscode", "acc_auditdate", "acc_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "acc_id", acc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, acc_id);
		BaseUtil.showErrorOnSuccess("信息已自动反馈到卡片&nbsp;&nbsp;"
				+ "<a href=\"javascript:openUrl('jsps/fa/fix/assetsCard.jsp?formCondition=ac_codeIS"
				+ accode + "')\">点击查看</a>&nbsp;");
	}

	@Override
	public void submitAssetsCardChange(int acc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("AssetsCardChange",
				"acc_statuscode", "acc_id=" + acc_id);
		StateAssert.submitOnlyEntering(status);
		// 同一卡片只能存在一张已提交未审核的变更单
		Object accode = baseDao.getFieldDataByCondition("AssetsCardChange",
				"acc_accode", "acc_id=" + acc_id);
		int count = baseDao.getCountByCondition("AssetsCardChange",
				"acc_accode='" + accode + "' and acc_statuscode = 'COMMITED'");
		if (count > 1) {
			BaseUtil.showError("卡片[" + accode + "]只能存在一张已提交未审核的变更单");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, acc_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"AssetsCardChange",
				"acc_statuscode='COMMITED',acc_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "acc_id="
						+ acc_id);
		// 记录操作
		baseDao.logger.submit(caller, "acc_id", acc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, acc_id);
	}

	@Override
	public void resSubmitAssetsCardChange(int acc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("AssetsCardChange",
				"acc_statuscode", "acc_id=" + acc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, acc_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"AssetsCardChange",
				"acc_statuscode='ENTERING',acc_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "acc_id="
						+ acc_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "acc_id", acc_id);
		handlerService.afterResSubmit(caller, acc_id);
	}
}
