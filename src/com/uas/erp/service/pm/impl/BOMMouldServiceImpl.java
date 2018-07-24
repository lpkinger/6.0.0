package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.BOMMouldService;

@Service
public class BOMMouldServiceImpl implements BOMMouldService {
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveBOMMould(String formStore, String gridStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BOM", "bo_code='" + store.get("bo_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(grid, "BOMDETAIL", "bd_id");
		baseDao.execute(gridSql);
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BOM", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "bo_id", store.get("bo_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void updateBOMMouldById(String formStore, String gridStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + store.get("bo_id"));
		StateAssert.updateOnlyEntering(status);
		// 修改
		store.put("bo_updateman", SystemSession.getUser().getEm_name());
		store.put("bo_updatedate", DateUtil.currentDateString(null));
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "BOMDETAIL", "bd_id");
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store,gstore});
		// 修改Detail 
		baseDao.execute(gridSql);
		// 保存BOM
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BOM", "bo_id");
		baseDao.execute(formSql);
		baseDao.updateByCondition("BomDetail", "bd_mothercode='"+store.get("bo_mothercode")+"'", "bd_bomid="+store.get("bo_id"));
		// 记录操作
		baseDao.logger.update(caller, "bo_id", store.get("bo_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore});
	}

	@Override
	public void deleteBOMMould(int bo_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.delOnlyEntering(status);
		// 是否已产生业务数据
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[] { bo_id});
		// 删除
		baseDao.deleteById("BOM", "bo_id", bo_id);
		// 删除Detail
		baseDao.deleteById("BOMdetail", "bd_bomid", bo_id);
		// 记录操作
		baseDao.logger.delete(caller, "bo_id", bo_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] { bo_id});
	}

	@Override
	public void printBOMMould(int bo_id, String caller) {
		// TODO Auto-generated method stub

	}

	@Override
	public void auditBOMMould(int bo_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { bo_id});
		// 执行审核操作
		baseDao.audit("Bom", "bo_id=" + bo_id, "bo_status", "bo_statuscode","bo_auditdate","bo_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "bo_id", bo_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[] { bo_id});
	}

	@Override
	public void resAuditBOMMould(int bo_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		handlerService.beforeResAudit(caller,new Object[] { bo_id});
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.resAuditOnlyAudit(status);
		//反审核的关联业务数据判断
		baseDao.resAuditCheck("BOM", bo_id);
		// 执行反审核操作
		baseDao.resAudit("Bom", "bo_id=" + bo_id, "bo_status", "bo_statuscode", "bo_auditman", "bo_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "bo_id", bo_id);
		handlerService.afterResAudit(caller,new Object[] { bo_id});
	}

	@Override
	public void submitBOMMould(int bo_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.submitOnlyEntering(status);
		//判断【生产类型】为  制造、委外的明细行是否有维护了【加工方式】，没有维护限制提交；
		SqlRowList rs = baseDao.queryForRowSet("select  wm_concat(Bd_Detno) cn from bom left join bomdetail on bo_id =bd_bomid "
						+" left join product on bd_soncode= pr_code "
						+" where (nvl(pr_manutype,' ')='MAKE' or nvl(pr_manutype,' ')='OSMAKE') "
						+" and bo_id="+bo_id+" and not Exists (select *  from bommouldprocess where bd_id=bm_bdid )");
		if(rs.next()&&rs.getString("cn")!=null){
			BaseUtil.showError("序号："+rs.getString("cn")+",没有维护加工方式！不允许提交");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { bo_id});
		// 执行提交操作
		baseDao.submit("Bom", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "bo_id", bo_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { bo_id});
	

	}

	@Override
	public void resSubmitBOMMould(int bo_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { bo_id });
		// 执行反提交操作
		baseDao.resOperate("Bom", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "bo_id", bo_id);
		handlerService.afterResSubmit(caller,new Object[] { bo_id});

	}

	@Override
	public void updateBOMMouldProcessing(String formStore, String param, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		Object bdid = null;
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { grid, grid });
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "Bommouldprocess", "bm_id");
		for(Map<Object, Object> s:grid){
			bdid = s.get("bm_bdid");
			if(s.get("bm_id") == null || s.get("bm_id").equals("") || s.get("bm_id").equals("0") ||
					Integer.parseInt(s.get("bm_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("Bommouldprocess_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "Bommouldprocess", new String[]{"bm_id"}, new Object[]{id});
				gridSql.add(sql); 
			}
		} 
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "bm_bdid", bdid);
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { grid, grid });
	}

}
