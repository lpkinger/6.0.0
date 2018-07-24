package com.uas.erp.service.common.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.common.MailTempleteService;

@Service
public class MailTempleteServiceImpl implements MailTempleteService {
	
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	

	/**
	 * 保存
	 * @param formStore
	 * @param caller
	 */
	public void saveMailTemplete(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean bool = baseDao.checkByCondition("mailtemp", "mt_code='" + store.get("mt_code") + "'");
		if (!bool) {
			BaseUtil.showError("模板编号已存在!");
		}
		String sql = "select count(*) from mailtemp where mt_name = '" + store.get("mt_name") + "'";
		int count = baseDao.getCount(sql);
		if(count > 0){
			BaseUtil.showError("模板名称已存在!");
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "mailtemp", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}
	
	/**
	 * 删除
	 * @param mtId
	 * @param caller
	 */
	public void deleteMailTempleteById(int mtId, String caller) {
		// 只能删除在录入的邮件模板!
		Object status = baseDao.getFieldDataByCondition("mailtemp", "mt_statuscode", "mt_id=" + mtId);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { mtId });
		baseDao.delCheck("mailtemp", mtId);
		// 删除
		baseDao.deleteById("mailtemp", "mt_id", mtId);
		// 记录操作
		baseDao.logger.delete(caller, "mt_id", mtId);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { mtId });
	}
	
	/**
	 * 修改
	 * @param formStore
	 * @param caller
	 */
	public void updateMailTemplete(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改在录入的邮件模板!
		Object status = baseDao.getFieldDataByCondition("mailtemp", "mt_statuscode", "mt_id=" + store.get("mt_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "mailtemp", "mt_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "mt_id", store.get("mt_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}
	
	/**
	 * 提交
	 * @param mtId
	 * @param caller
	 */
	public void submitMailTemplete(int mtId, String caller) {
		// 只能对状态为[在录入]的邮件模板进行提交操作!
		Object status = baseDao.getFieldDataByCondition("mailtemp", "mt_statuscode", "mt_id=" + mtId);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { mtId });
		// 执行提交操作
		baseDao.submit("mailtemp", "mt_id=" + mtId, "mt_status", "mt_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mt_id", mtId);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { mtId });
	}
	
	/**
	 * 反提交
	 * @param mtId
	 * @param caller
	 */
	public void resSubmitMailTemplete(int mtId, String caller) {
		// 只能对状态为[已提交]的邮件模板进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("mailtemp", "mt_statuscode", "mt_id=" + mtId);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { mtId });
		// 执行反提交操作
		baseDao.resOperate("mailtemp", "mt_id=" + mtId, "mt_status", "mt_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mt_id", mtId);
		handlerService.handler(caller, "resCommit", "after", new Object[] { mtId });
	}
	
	/**
	 * 审核
	 * @param mtId
	 * @param caller
	 */
	public void auditMailTemplete(int mtId, String caller) {
		// 只能对状态为[已提交]的邮件模板进行审核操作!
		Object status = baseDao.getFieldDataByCondition("mailtemp", "mt_statuscode", "mt_id=" + mtId);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { mtId });
		// 执行审核操作
		baseDao.audit("mailtemp", "mt_id=" + mtId, "mt_status", "mt_statuscode", "mt_auditdate", "mt_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "mt_id", mtId);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { mtId });
	}

	/**
	 * 反审核
	 * @param mtId
	 * @param caller
	 */
	public void resAuditMailTemplete(int mtId, String caller) {
		// 只能对状态为[已审核]的邮件模板进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("mailtemp", "mt_statuscode", "mt_id=" + mtId);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[] { mtId });
		baseDao.resAuditCheck("mailtemp", mtId);
		// 执行反审核操作
		baseDao.resAudit("mailtemp", "mt_id=" + mtId, "mt_status", "mt_statuscode", "mt_auditdate", "mt_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "mt_id", mtId);
		handlerService.afterResAudit(caller, new Object[] { mtId });
	}
	
}
