package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.LabelPrintSettingService;

@Service("labelPrintSettingService")
public class LabelPrintSettingServiceImpl implements LabelPrintSettingService{

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveLPSetting(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("LabelPrintSetting",
				"lps_code='" + store.get("lps_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		if("MESLPSetting".equals(caller)){
			//判断取值SQL语句是否合法
			checkSql(store.get("lps_sql").toString());
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });		
		// 保存
		String formSql = SqlUtil.getInsertSqlByMap(store, "LabelPrintSetting");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "lps_id", store.get("lps_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteLPSetting(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("LabelPrintSetting",
				"lps_statuscode", "lps_id=" +id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {id});
		// 删除
		baseDao.deleteById("LabelPrintSetting", "lps_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "lps_id",id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] {id});		
	}

	@Override
	public void updateLPSetting(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("LabelPrintSetting",
				"lps_statuscode", "lps_id=" + store.get("lps_id"));
		StateAssert.updateOnlyEntering(status);
		if("MESLPSetting".equals(caller)){
			//判断取值SQL语句是否合法
			checkSql(store.get("lps_sql").toString());
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "LabelPrintSetting",
				"lps_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "lps_id", store.get("lps_id"));
		// 更新上次采购价格、供应商
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store});		
	}

	@Override
	public void submitLPSetting(int id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("LabelPrintSetting",
				"lps_statuscode", "lps_id=" + id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] {id});
		// 执行提交操作
		baseDao.submit("LabelPrintSetting", "lps_id=" + id, "lps_status",
				"lps_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "lps_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {id});		
	}

	@Override
	public void resSubmitLPSetting(int id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("LabelPrintSetting",
				"lps_statuscode", "lps_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { id });
		// 执行反提交操作
		baseDao.resOperate("LabelPrintSetting", "lps_id=" + id, "lps_status",
				"lps_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "lps_id", id);
		handlerService.afterResSubmit(caller, new Object[] {id});		
	}

	@Override
	public void auditLPSetting(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("LabelPrintSetting",
				"lps_statuscode", "lps_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { id });
		baseDao.audit("LabelPrintSetting", "lps_id=" + id, "lps_status",
				"lps_statuscode", "lps_auditdate", "lps_auditman");
		baseDao.logger.audit(caller, "lps_id", "lps_id");
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { id });	
	}

	@Override
	public void resAuditLPSetting(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("LabelPrintSetting",
				"lps_statuscode", "lps_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("LabelPrintSetting", "lps_id=" + id, "lps_status",
				"lps_statuscode", "lps_auditdate", "lps_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "lps_id",id);
	}
	
	private void checkSql(String sql){
		String value = sql;
		String regex = "\\{(?:[A-Za-z][A-Za-z0-9_]*)\\}";			 
		String va = value.replaceAll(regex, "\\'1\\'");		
		va += " and 1=2"  ;
		try {
			baseDao.execute(va);
		} catch (Exception e) {
			// TODO: handle exception
			BaseUtil.showError("取值SQL语句不合法！");
		}	
	}

	@Override
	public Map<String, Object> getPrintCaller(String code, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select lps_caller,lps_labelurl from LabelPrintSetting where lps_code=? and lps_statuscode='AUDITED'",code);
		if(rs.next()){
			return rs.getCurrentMap();
		}else{
			BaseUtil.showError("标签模板编号:"+code+",不存在或者未审核");
		}
		return null;
	}
}
