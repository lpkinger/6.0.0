package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
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
import com.uas.erp.service.pm.FeederService;

@Service
public class FeederServiceImpl implements FeederService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveFeeder(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Feeder",
				"fe_code='" + store.get("fe_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Feeder",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
					baseDao.logger.save(caller, "fe_id", store.get("fe_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateFeederById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("Feeder",
				"fe_statuscode", "fe_id=" + store.get("fe_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Feeder",
				"fe_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "fe_id", store.get("fe_id"));
		// 更新上次采购价格、供应商
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteFeeder(int fe_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Feeder",
				"fe_statuscode", "fe_id=" + fe_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { fe_id });
		// 删除
		baseDao.deleteById("Feeder", "fe_id", fe_id);
		// 记录操作
		baseDao.logger.delete(caller, "fe_id", fe_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { fe_id });
	}

	@Override
	public void auditFeeder(int fe_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Feeder",
				"fe_statuscode", "fe_id=" + fe_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { fe_id });
		baseDao.audit("Feeder", "fe_id=" + fe_id, "fe_status",
				"fe_statuscode", "fe_auditdate", "fe_auditman");
		baseDao.logger.audit(caller, "fe_id", "fe_id");
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { fe_id });
	}

	@Override
	public void resAuditFeeder(int fe_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Feeder",
				"fe_statuscode", "fe_id=" + fe_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("Feeder", "fe_id=" + fe_id, "fe_status",
				"fe_statuscode", "fe_auditdate", "fe_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "fe_id", fe_id);
	}

	@Override
	public void submitFeeder(int fe_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Feeder",
				"fe_statuscode", "fe_id=" + fe_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { fe_id });
		// 执行提交操作
		baseDao.submit("Feeder", "fe_id=" + fe_id, "fe_status",
				"fe_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "fe_id", fe_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { fe_id });
	}

	@Override
	public void resSubmitFeeder(int fe_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Feeder",
				"fe_statuscode", "fe_id=" + fe_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { fe_id });
		// 执行反提交操作
		baseDao.resOperate("Feeder", "fe_id=" + fe_id, "fe_status",
				"fe_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "fe_id", fe_id);
		handlerService.afterResSubmit(caller, new Object[] { fe_id });
	}

	@Override
	public void saveFeederRepairLog(String caller, int fe_id, String remark,
			boolean ifclear) {
		Object status = baseDao.getFieldDataByCondition("Feeder",
				"fe_statuscode", "fe_id=" + fe_id);
		if(status != null){
			if(!status.equals("AUDITED")){
				BaseUtil.showError("飞达未审核!");
			}
		}else{
			BaseUtil.showError("飞达不存在!");
		}
		int clear = 1;
		if(!ifclear){
			clear = 0;
		}
		//更新飞达最后修改时间，使用次数是否情况
		baseDao.execute("update feeder set fe_lastrepairdate=sysdate,fe_nowruntimes=case when "+clear+"<>0 then 0 else fe_nowruntimes end where fe_id="+fe_id);
		//插入一条维修记录
		baseDao.execute("insert into feederLog (fl_id,fl_feid,fl_fecode,fl_remark,fl_type,fl_man,fl_date)select feederlog_seq.nextval,fe_id,fe_code,'"+remark+"','维修','"+SystemSession.getUser().getEm_name()+"',sysdate from feeder where fe_id="+fe_id);
	}

	@Override
	public void saveFeederScrapLog(String caller, int fe_id, String remark) {
		Object status = baseDao.getFieldDataByCondition("Feeder",
				"fe_statuscode", "fe_id=" + fe_id);
		if(status != null){
			if(!status.equals("AUDITED")){
				BaseUtil.showError("飞达未审核!");
			}
		}else{
			BaseUtil.showError("飞达不存在!");
		}
		//更新飞达为已报废
		baseDao.updateByCondition("feeder", "fe_usestatus='已报废'", "fe_id="+fe_id);
		//并插入日志到FeederLog
		baseDao.execute("insert into feederLog (fl_id,fl_feid,fl_fecode,fl_remark,fl_type,fl_man,fl_date)select feederlog_seq.nextval,fe_id,fe_code,'"+remark+"','报废','"+SystemSession.getUser().getEm_name()+"',sysdate from feeder where fe_id="+fe_id);
	}

	@Override
	public void vastTurnMaintain(String caller, String data) {
		// TODO Auto-generated method stub
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls = new ArrayList<String>();
		for(Map<Object,Object> map:store){
			//更新feeder表的fe_lastmaintain=sysdate,fe_nowruntimes=0  
			sqls.add("update Feeder set fe_lastmaintain=sysdate,fe_nowruntimes=0 where fe_id="+map.get("fe_id"));
			//插入记录到feederlog ，fl_type=保养
			sqls.add("insert into feederLog (fl_id,fl_feid,fl_fecode,fl_remark,fl_type,fl_man,fl_date) select feederlog_seq.nextval,fe_id,fe_code,'','保养','"+SystemSession.getUser().getEm_name()+"',sysdate from feeder where fe_id ="+map.get("fe_id"));
		}
		baseDao.execute(sqls);
	}

}
