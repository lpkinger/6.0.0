package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.FeatureTempletService;

@Service("featureTempletService")
public class FeatureTempletServiceImpl implements FeatureTempletService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveFeatureTemplet(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("FeatureTemplet", "ft_code='"
				+ store.get("ft_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存ProductSMT
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "FeatureTemplet"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid,
				"FeatureTempletDetail", "fd_id"));
		baseDao.execute("update FeatureTemplet set (ft_keyfecode,ft_keyfename)=(select fd_fecode, fd_fename from FeatureTempletDetail where fd_ftid=" 
				+ store.get("ft_id") + " and nvl(fd_iskey,0)<>0) where ft_id = " + store.get("ft_id"));
		baseDao.execute("update FeatureTempletDetail set fd_code=(select ft_code from FeatureTemplet where fd_ftid=ft_id) where fd_ftid = " + store.get("ft_id"));
		// 记录操作
		baseDao.logger.save(caller, "ft_id", store.get("ft_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteFeatureTemplet(int ft_id, String caller) {
		// 只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("FeatureTemplet",
				"ft_statuscode", "ft_id=" + ft_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ft_id });
		// 删除FeatureTemplet
		baseDao.deleteById("FeatureTemplet", "ft_id", ft_id);
		// 删除purchaseDetail
		baseDao.deleteById("FeatureTempletdetail", "fd_ftid", ft_id);
		// 记录操作
		baseDao.logger.delete(caller, "ft_id", ft_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { ft_id });
	}

	@Override
	public void updateFeatureTempletById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的工艺资料!
		Object status = baseDao.getFieldDataByCondition("FeatureTemplet",
				"ft_statuscode", "ft_id=" + store.get("ft_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改ProductSMT
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store,
				"FeatureTemplet", "ft_id"));
		// 修改ProductSMTDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore,
				"FeatureTempletDetail", "fd_id"));
		baseDao.execute("update FeatureTemplet set (ft_keyfecode,ft_keyfename)=(select fd_fecode, fd_fename from FeatureTempletDetail where fd_ftid=" 
				+ store.get("ft_id") + " and nvl(fd_iskey,0)<>0) where ft_id = " + store.get("ft_id"));
		baseDao.execute("update FeatureTempletDetail set fd_code=(select ft_code from FeatureTemplet where fd_ftid=ft_id) where fd_ftid = " + store.get("ft_id"));
		// 记录操作
		baseDao.logger.update(caller, "ft_id", store.get("ft_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void auditFeatureTemplet(int ft_id, String caller) {
		// 只能对状态为[已提交]的工艺资料进行审核操作!
		Object status = baseDao.getFieldDataByCondition("FeatureTemplet",
				"ft_statuscode", "ft_id=" + ft_id);
		StateAssert.auditOnlyCommited(status);
		baseDao.execute("update FeatureTempletDetail set fd_code=(select ft_code from FeatureTemplet where fd_ftid=ft_id) where fd_ftid = " + ft_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { ft_id });
		// 执行审核操作
		baseDao.audit("FeatureTemplet", "ft_id=" + ft_id, "ft_status",
				"ft_statuscode", "ft_auditdate", "ft_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "ft_id", ft_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { ft_id });
	}

	@Override
	public void resAuditFeatureTemplet(int ft_id, String caller) {
		// 只能对状态为[已审核]的工艺资料进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("FeatureTemplet",
				"ft_statuscode", "ft_id=" + ft_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("FeatureTemplet", "ft_id=" + ft_id, "ft_status",
				"ft_statuscode", "ft_auditdate", "ft_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "ft_id", ft_id);
	}

	@Override
	public void submitFeatureTemplet(int ft_id, String caller) {
		// 只能对状态为[在录入]的工艺资料进行提交操作!
		Object status = baseDao.getFieldDataByCondition("FeatureTemplet",
				"ft_statuscode", "ft_id=" + ft_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { ft_id });
		baseDao.execute("update FeatureTempletDetail set fd_code=(select ft_code from FeatureTemplet where fd_ftid=ft_id) where fd_ftid = " + ft_id);
		// 执行提交操作
		baseDao.submit("FeatureTemplet", "ft_id=" + ft_id, "ft_status",
				"ft_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ft_id", ft_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { ft_id });
	}

	@Override
	public void resSubmitFeatureTemplet(int ft_id, String caller) {
		// 只能对状态为[已提交]的工艺资料进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FeatureTemplet",
				"ft_statuscode", "ft_id=" + ft_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { ft_id });
		// 执行反提交操作
		baseDao.resOperate("FeatureTemplet", "ft_id=" + ft_id, "ft_status",
				"ft_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ft_id", ft_id);
		handlerService.afterResSubmit(caller, new Object[] { ft_id });
	}

}
