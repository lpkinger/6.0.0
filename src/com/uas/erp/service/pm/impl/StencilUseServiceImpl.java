package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.StencilUseService;

@Service("StencilUseService")
public class StencilUseServiceImpl implements StencilUseService{

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveStencilUse(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("StencilUse",
				"su_code='" + store.get("su_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "StencilUse",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "su_id", store.get("su_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteStencilUse(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("StencilUse",
				"su_statuscode", "su_id=" + id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { id });
		// 删除
		baseDao.deleteById("StencilUse", "su_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "su_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { id });
	}

	@Override
	public void updateStencilUse(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("StencilUse",
				"su_statuscode", "su_id=" + store.get("su_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "StencilUse",
				"su_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "su_id", store.get("su_id"));
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void submitStencilUse(int id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("StencilUse",
				"su_statuscode", "su_id=" + id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { id });
		// 执行提交操作
		baseDao.submit("StencilUse", "su_id=" + id, "su_status",
				"su_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "su_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { id });
	}

	@Override
	public void resSubmitStencilUse(int id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("StencilUse",
				"su_statuscode", "su_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { id });
		// 执行反提交操作
		baseDao.resOperate("StencilUse", "su_id=" + id, "su_status",
				"su_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "su_id", id);
		handlerService.afterResSubmit(caller, new Object[] { id });
	}

	@Override
	public void auditStencilUse(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("StencilUse",
				"su_statuscode", "su_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { id });
		//判断钢网状态是否为在仓
		Object ob = baseDao.getFieldDataByCondition("Stencil left join StencilUse on su_stid=st_id", "st_id", "su_id="+id+" and st_usestatus='在仓'");
		if(ob == null){
			BaseUtil.showError("钢网状态为借出，请先归还再借！");
		}
		baseDao.audit("StencilUse", "su_id=" + id, "su_status",
				"su_statuscode", "su_auditdate", "su_auditman"); 
		//更新钢网的领用状态st_usestatus[借出]
		baseDao.updateByCondition("Stencil", "st_usestatus='借出'", "st_id=(select su_stid from stencilUse where su_id="+id+")");
		baseDao.logger.audit(caller, "su_id", "su_id");
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { id });
	}

	@Override
	public void resAuditStencilUse(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("StencilUse",
				"su_statuscode", "su_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("StencilUse", "su_id=" + id, "su_status",
				"su_statuscode", "su_auditdate", "su_auditman");
		//更新钢网的领用状态st_usestatus[在仓]
		baseDao.updateByCondition("Stencil", "st_usestatus='在仓'", "st_id=(select su_stid from stencilUse where su_id="+id+")");
		// 记录操作
		baseDao.logger.resAudit(caller, "su_id", id);
	}

	@Override
	public void backStencil(int id, String caller, String record,
			String location, String date) {
		Object status = baseDao.getFieldDataByCondition("StencilUse",
				"su_statuscode", "su_id=" + id);
		if(status != null){
			if(!status.equals("AUDITED")){
				BaseUtil.showError("单据未审核不允许归还!");
			}
		}
		//判断钢网状态是否为借出
		Object ob = baseDao.getFieldDataByCondition("Stencil left join StencilUse on su_stid=st_id", "st_id", "su_id="+id+" and st_usestatus='在仓'");
		if(ob != null){
			BaseUtil.showError("钢网已归还，请勿重复归还！");
		}
		//归还
		baseDao.updateByCondition("StencilUse", "su_backcheck='"+record+"',su_backlocation='"+location+"',su_backdate="+DateUtil.parseDateToOracleString(null, date), "su_id="+id);
		//更新钢网的领用状态st_usestatus[在仓]
		baseDao.updateByCondition("Stencil", "st_usestatus='在仓',st_location='"+location+"'", "st_id=(select su_stid from stencilUse where su_id="+id+")");
		baseDao.logger.getMessageLog("归还", "归还成功", caller, "su_id", id);
	}
}
