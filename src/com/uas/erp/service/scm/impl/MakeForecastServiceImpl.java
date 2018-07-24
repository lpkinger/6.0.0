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
import com.uas.erp.service.scm.MakeForecastService;

@Service("makeForecastService")
public class MakeForecastServiceImpl implements MakeForecastService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveMakeForecast(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, grid});
		//保存MakeForecast
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MakeForecast", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存MakeForecastDetail
		for(int i=0;i<grid.size();i++){
			grid.get(i).put("mfd_id", baseDao.getSeqId("MAKEFORECASTDETAIL_SEQ"));
			grid.get(i).put("mfd_status", BaseUtil.getLocalMessage("ENTERING"));
			grid.get(i).put("mfd_statuscode", "ENTERING");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "MakeForecastDetail");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "mf_id", store.get("mf_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, grid});
	}
	
	@Override
	public void deleteMakeForecast(int mf_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MakeForecast", "mf_statuscode", "mf_id=" + mf_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, mf_id);
		//删除MakeForecast
		baseDao.deleteById("MakeForecast", "mf_id", mf_id);
		//删除MakeForecastDetail
		baseDao.deleteById("MakeForecastdetail", "mfd_mfid", mf_id);
		baseDao.logger.save(caller, "mf_id", mf_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, mf_id);
	}
	
	@Override
	public void updateMakeForecastById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MakeForecast", "mf_statuscode", "mf_id=" + store.get("mf_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MakeForecast", "mf_id");
		baseDao.execute(formSql);
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "MakeForecastDetail", "mfd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("mfd_id") == null || s.get("mfd_id").equals("") || s.get("mfd_id").equals("0") ||
					Integer.parseInt(s.get("mfd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("MAKEFORECASTDETAIL_SEQ");
				s.put("mfd_status", BaseUtil.getLocalMessage("ENTERING"));
				s.put("mfd_statuscode", "ENTERING");
				String sql = SqlUtil.getInsertSqlByMap(s, "MakeForecastDetail", new String[]{"mfd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "mf_id", store.get("mf_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
	}
	
	@Override
	public void printMakeForecast(int mf_id, String caller) {
		//执行打印前的其它逻辑
		handlerService.beforePrint(caller, mf_id);
		//执行打印操作
		//TODO
		//记录操作
		baseDao.logger.post(caller, "mf_id", mf_id);
		//执行打印后的其它逻辑
		handlerService.afterPrint(caller, mf_id);
	}
	
	@Override
	public void auditMakeForecast(int mf_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeForecast", "mf_statuscode", "mf_id=" + mf_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, mf_id);
		//执行审核操作
		baseDao.audit("MakeForecast", "mf_id=" + mf_id, "mf_status", "mf_statuscode", "mf_auditdate", "mf_auditman");
		baseDao.audit("MakeForecastDetail", "mfd_mfid=" + mf_id, "mfd_status", "mfd_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "mf_id", mf_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, mf_id);
	}
	
	@Override
	public void resAuditMakeForecast(int mf_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeForecast", "mf_statuscode", "mf_id=" + mf_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("MakeForecast", "mf_id=" + mf_id, "mf_status", "mf_statuscode");
		baseDao.resOperate("MakeForecastDetail", "mfd_mfid=" + mf_id, "mf_status", "mf_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "mf_id", mf_id);
	}
	
	@Override
	public void submitMakeForecast(int mf_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeForecast", "mf_statuscode", "mf_id=" + mf_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, mf_id);
		//执行提交操作
		baseDao.submit("MakeForecast", "mf_id=" + mf_id, "mf_status", "mf_statuscode");
		baseDao.submit("MakeForecastDetail", "mfd_mfid=" + mf_id, "mf_status", "mf_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "mf_id", mf_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, mf_id);
	}
	
	@Override
	public void resSubmitMakeForecast(int mf_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeForecast", "mf_statuscode", "mf_id=" + mf_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, mf_id);
		//执行反提交操作
		baseDao.resOperate("MakeForecast", "mf_id=" + mf_id, "mf_status", "mf_statuscode");
		baseDao.resOperate("MakeForecastDetail", "mfd_mfid=" + mf_id, "mf_status", "mf_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "mf_id", mf_id);
		handlerService.afterResSubmit(caller, mf_id);
	}
}
