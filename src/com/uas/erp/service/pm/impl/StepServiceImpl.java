package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.StepService;

@Service
public class StepServiceImpl implements StepService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveStep(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Step",
				"st_code='" + store.get("st_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		if(!baseDao.isDBSetting(caller, "StepRepeat")){
			//名称也不能重复
			boolean bool1 = baseDao.checkByCondition("Step",
					"st_name='" + store.get("st_name") + "' and st_id<>"+store.get("st_id"));
			if (!bool1) {
				BaseUtil.showError(BaseUtil
						.getLocalMessage("common.save_nameHasExist"));
			}
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Step",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "st_id", store.get("st_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateStepById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("Step",
				"st_statuscode", "st_id=" + store.get("st_id"));
		StateAssert.updateOnlyEntering(status);
		if(!baseDao.isDBSetting(caller, "StepRepeat")){
			//名称也不能重复
			boolean bool1 = baseDao.checkByCondition("Step",
					"st_name='" + store.get("st_name") + "' and st_id<>"+store.get("st_id"));
			if (!bool1) {
				BaseUtil.showError(BaseUtil
						.getLocalMessage("common.save_nameHasExist"));
			}
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil
				.getUpdateSqlByFormStore(store, "Step", "st_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "st_id", store.get("st_id"));
		// 更新上次采购价格、供应商
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteStep(int st_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Step",
				"st_statuscode", "st_id=" + st_id);
		StateAssert.delOnlyEntering(status);
		//如果工序已经使用限制删除
		String str =  baseDao.getJdbcTemplate().queryForObject("select wm_concat(cr_code) from craft left join craftdetail on cd_crid=cr_id left join step on st_code=cd_stepcode where st_id=? and rownum<20",String.class,st_id);
		if(str != null){
			BaseUtil.showError("工序在工艺路线中被使用不允许删除，工艺路线["+str+"]");
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { st_id });
		// 删除
		baseDao.deleteById("Step", "st_id", st_id);
		// 记录操作
		baseDao.logger.delete(caller, "st_id", st_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { st_id });
	}

	@Override
	public void auditStep(int st_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Step",
				"st_statuscode", "st_id=" + st_id);
		StateAssert.auditOnlyCommited(status);
		if(!baseDao.isDBSetting(caller, "StepRepeat")){
			Object st_name = baseDao.getFieldDataByCondition("Step", "st_name", "st_id="+st_id);
			//名称也不能重复
			boolean bool1 = baseDao.checkByCondition("Step",
					"st_name='" + st_name + "' and st_id<>"+st_id);
			if (!bool1) {
				BaseUtil.showError(BaseUtil
						.getLocalMessage("common.save_nameHasExist"));
			}
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { st_id });
		//审核
		baseDao.audit("Step", "st_id=" + st_id, "st_status", "st_statuscode",
				"st_auditdate", "st_auditman");
		//记录操作
		baseDao.logger.audit(caller, "st_id", st_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { st_id });
	}

	@Override
	public void resAuditStep(int st_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Step",
				"st_statuscode", "st_id=" + st_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("Step", "st_id=" + st_id, "st_status",
				"st_statuscode", "st_auditdate", "st_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "st_id", st_id);
	}

	@Override
	public void submitStep(int st_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Step",
				"st_statuscode", "st_id=" + st_id);
		StateAssert.submitOnlyEntering(status);
		if(!baseDao.isDBSetting(caller, "StepRepeat")){
			Object st_name = baseDao.getFieldDataByCondition("Step", "st_name", "st_id="+st_id);
			//名称也不能重复
			boolean bool1 = baseDao.checkByCondition("Step",
					"st_name='" + st_name + "' and st_id<>"+st_id);
			if (!bool1) {
				BaseUtil.showError(BaseUtil
						.getLocalMessage("common.save_nameHasExist"));
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { st_id });
		// 执行提交操作
		baseDao.submit("Step", "st_id=" + st_id, "st_status", "st_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "st_id", st_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { st_id });
	}
	
	@Override
	public void resSubmitStep(int st_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Step",
				"st_statuscode", "st_id=" + st_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { st_id });
		// 执行反提交操作
		baseDao.resOperate("Step", "st_id=" + st_id, "st_status",
				"st_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "st_id", st_id);
		handlerService.afterResSubmit(caller, new Object[] { st_id });
	}

}
