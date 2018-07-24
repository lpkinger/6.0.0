package com.uas.erp.service.fs.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fs.PledgeService;
@Service
public class PledgeServiceImpl implements PledgeService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void savePledge(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FSPLEDGE", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "pl_id", store.get("pl_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updatePledge(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FSPLEDGE", "pl_id");
		baseDao.execute(formSql);
		//修改最近更新日期
		baseDao.updateByCondition("FSPLEDGE", "pl_lastupdate=sysdate", "pl_id="+store.get("pl_id"));
		//记录操作
		baseDao.logger.update(caller, "pl_id", store.get("pl_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deletePledge(int pl_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{pl_id});
		//删除
		baseDao.deleteById("FSPLEDGE", "pl_id", pl_id);
		//记录操作
		baseDao.logger.delete(caller, "pl_id", pl_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{pl_id});
	}

	@Override
	public void submitPledge(int pl_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("FSPLEDGE",
				"pl_statuscode", "pl_id=" + pl_id);
		if(status==null){
			BaseUtil.showError("该单已不存在");
		}
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pl_id);
		// 执行提交操作
		baseDao.submit("FSPLEDGE", "pl_id="+pl_id, "pl_status", "pl_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "wd_id", pl_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pl_id);
	}

	@Override
	public void resSubmitPledge(int pl_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FSPLEDGE",
				"pl_statuscode", "pl_id=" + pl_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, pl_id);
		// 执行反提交操作
		baseDao.resOperate("FSPLEDGE", "pl_id="+pl_id, "pl_status", "pl_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pl_id", pl_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, pl_id);
	}

	@Override
	public void auditPledge(int pl_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("FSPLEDGE",
				"pl_statuscode", "pl_id=" + pl_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pl_id);
		// 执行审核操作
		baseDao.audit("FSPLEDGE", "pl_id="+pl_id, "pl_status", "pl_statuscode", "pl_auditdate", "pl_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "pl_id", pl_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pl_id);
	}

	@Override
	public void resAuditPledge(int pl_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("FSPLEDGE",
				"pl_statuscode", "pl_id=" + pl_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, pl_id);
		// 执行反审核操作
		baseDao.resAudit("FSPLEDGE", "pl_id="+pl_id, "pl_status", "pl_statuscode", "pl_auditman", "pl_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "pl_id", pl_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, pl_id);
	}

}
