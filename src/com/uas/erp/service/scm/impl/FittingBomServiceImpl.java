package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.FittingBomService;

@Service("fittingBomService")
public class FittingBomServiceImpl implements FittingBomService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveFittingBom(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("FittingBom", "fb_code='" + store.get("fb_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave("FittingBom", new Object[]{store, grid});
		//保存FittingBom
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FittingBom", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存FittingBomDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "FittingBomDetail", "fbd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save("FittingBom", "fb_id", store.get("fb_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave("FittingBom", new Object[]{store, grid});
	}
	@Override
	public void deleteFittingBom(int fb_id) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("FittingBom", "fb_statuscode", "fb_id=" + fb_id);
		StateAssert.delOnlyEntering(status);
		baseDao.delCheck("FittingBom", fb_id);
		//执行删除前的其它逻辑
		handlerService.beforeDel("FittingBom", fb_id);
		//删除FittingBom
		baseDao.deleteById("FittingBom", "fb_id", fb_id);
		//删除FittingBomDetail
		baseDao.deleteById("FittingBomdetail", "fbd_fbid", fb_id);
		//记录操作
		baseDao.logger.delete("FittingBom", "fb_id", fb_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("FittingBom", fb_id);
	}
	
	@Override
	public void updateFittingBomById(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("FittingBom", "fb_statuscode", "fb_id=" + store.get("fb_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave("FittingBom", new Object[]{store, gstore});
		//修改FittingBom
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FittingBom", "fb_id");
		baseDao.execute(formSql);
		//修改FittingBomDetail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "FittingBomDetail", "fbd_id");
		baseDao.execute(gridSql);
		baseDao.logger.update("FittingBom", "fb_id", store.get("fb_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave("FittingBom", new Object[]{store, gstore});
	}
	@Override
	public void auditFittingBom(int fb_id) {
		//只能对状态为[已提交]的单据进行审核操作!
		Object status = baseDao.getFieldDataByCondition("FittingBom", "fb_statuscode", "fb_id=" + fb_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit("FittingBom", fb_id);
		//执行审核操作
		baseDao.audit("FittingBom", "fb_id=" + fb_id, "fb_status", "fb_statuscode", "fb_auditdate", "fb_auditman");
		//记录操作
		baseDao.logger.audit("FittingBom", "fb_id", fb_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit("FittingBom", fb_id);
	}
	
	@Override
	public void resAuditFittingBom(int fb_id) {
		//只能对状态为[已审核]的单据进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("FittingBom", "fb_statuscode", "fb_id=" + fb_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("FittingBom", fb_id);
		//执行反审核操作
		baseDao.resAudit("FittingBom", "fb_id=" + fb_id, "fb_status", "fb_statuscode", "fb_auditdate", "fb_auditman");
		//记录操作
		baseDao.logger.resAudit("FittingBom", "fb_id", fb_id);
	}
	@Override
	public void submitFittingBom(int fb_id) {
		//只能对状态为[在录入]的单据进行提交操作!
		Object status = baseDao.getFieldDataByCondition("FittingBom", "fb_statuscode", "fb_id=" + fb_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit("FittingBom", fb_id);
		//执行提交操作
		baseDao.submit("FittingBom", "fb_id=" + fb_id, "fb_status", "fb_statuscode");
		//记录操作
		baseDao.logger.submit("FittingBom", "fb_id", fb_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit("FittingBom", fb_id);
	}
	
	@Override
	public void resSubmitFittingBom(int fb_id) {
		//只能对状态为[已提交]的单据进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FittingBom", "fb_statuscode", "fb_id=" + fb_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("FittingBom", fb_id);
		//执行反提交操作
		baseDao.resOperate("FittingBom", "fb_id=" + fb_id, "fb_status", "fb_statuscode");
		//记录操作
		baseDao.logger.resSubmit("FittingBom", "fb_id", fb_id);
		//执行提交后的其它逻辑
		handlerService.afterResSubmit("FittingBom", fb_id);
	}
}
