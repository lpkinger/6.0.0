package com.uas.erp.service.pm.impl;

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
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.pm.BOMStepChangeService;

@Service("bomStepChangeService")
public class BOMStepChangeServiceImpl implements BOMStepChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBOMStepChange(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BOMStepChange", "bc_code='" + store.get("bc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}			
		// 执行保存前的其它逻辑
		handlerService.beforeSave("BOMStepChange", new Object[] {store,grid});
		for(Map<Object,Object> map:grid){
			//当前编号的记录已经存在,不能新增!
			map.put("bd_didstatus", BaseUtil.getLocalMessage("OPEN"));
			map.put("bd_didstatuscode", "OPEN");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "bomStepChangeDetail","bd_id");
		baseDao.execute(gridSql);		
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BOMStepChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "bc_id", store.get("bc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave("BOMStepChange",new Object[] {store,grid});
		//根据BOM+序号更新原工序
		baseDao.execute("merge into bomStepChangeDetail B using bomdetail A on (A.bd_bomid=B.bd_bomid and A.bd_detno=B.bd_bddetno and B.bd_bcid="+store.get("bc_id")+") when matched then update set B.bd_oldstepcode = A.bd_stepcode");
	}

	@Override
	public void deleteBOMStepChange(int bc_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BOMStepChange", "bc_statuscode", "bc_id=" + bc_id);
		StateAssert.delOnlyEntering(status);	
		//判断是否有来源ECN ，如果有的话，不允许删除
		Object ob = baseDao.getFieldDataByCondition("bomstepchange", "bc_code", "bc_id="+bc_id+" and bc_sourcecode is not null");
		if(ob != null){
			BaseUtil.showError("有来源ECN的BOM工序变更单不允许删除");
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel("BOMStepChange",new Object[] { bc_id});
		// 删除
		baseDao.deleteById("BOMStepChange", "bc_id", bc_id);
		// 删除Detail
		baseDao.deleteById("bomStepChangeDetail", "bd_bomid", bc_id);
		// 记录操作
		baseDao.logger.delete(caller, "bc_id", bc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("BOMStepChange",new Object[] { bc_id});
	}

	@Override
	public void updateById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("BOMStepChange", "bc_statuscode", "bc_id=" + store.get("bc_id"));
		StateAssert.updateOnlyEntering(status);	
		// 执行修改前的其它逻辑
		handlerService.beforeSave("BOMStepChange", new Object[] {store,gstore});		
		// 保存BOMStepChange
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BOMStepChange", "bc_id");
		baseDao.execute(formSql);		
		// 修改Detail 
		List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore, "bomStepChangeDetail", "bd_id");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "bc_id", store.get("bc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("BOMStepChange", new Object[] { store, gstore});
		//根据BOM+序号更新原工序
		baseDao.execute("merge into bomStepChangeDetail B using bomdetail A on (A.bd_bomid=B.bd_bomid and A.bd_detno=B.bd_bddetno and B.bd_bcid="+store.get("bc_id")+") when matched then update set B.bd_oldstepcode = A.bd_stepcode");		
	}

	@Override
	public void auditBOMStepChange(int bc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("BOMStepChange", new String[]{"bc_statuscode","bc_code"}, "bc_id=" + bc_id);
		StateAssert.auditOnlyCommited(status[0]);
		checkChange(bc_id);				
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("BOMStepChange", new Object[] { bc_id});
		// 执行审核操作
		baseDao.audit("BOMStepChange", "bc_id=" + bc_id, "bc_status", "bc_statuscode","bc_auditdate","bc_auditman");
		String str = baseDao.callProcedure("SP_DOSTEPCHANGE",new String[]{status[1].toString()});
		if(str != null && !str.trim().equals("")){
			BaseUtil.showError(str);
		} 
		// 记录操作
		baseDao.logger.audit(caller, "bc_id", bc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit("BOMStepChange",new Object[] { bc_id});
	}

	@Override
	public void submitBOMStepChange(int bc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BOMStepChange", "bc_statuscode", "bc_id=" + bc_id);
		StateAssert.submitOnlyEntering(status);	
		checkChange(bc_id);		
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit("BOMStepChange", new Object[] { bc_id});
		// 执行提交操作
		baseDao.submit("BOMStepChange", "bc_id=" + bc_id, "bc_status", "bc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "bc_id", bc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit("BOMStepChange", new Object[] { bc_id});
	}

	@Override
	public void resSubmitBOMStepChange(int bc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BOMStepChange", "bc_statuscode", "bc_id=" + bc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("BOMStepChange", new Object[] { bc_id });
		// 执行反提交操作
		baseDao.resOperate("BOMStepChange", "bc_id=" + bc_id, "bc_status", "bc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "bc_id", bc_id);
		handlerService.afterResSubmit("BOMStepChange",new Object[] { bc_id});
	} 
	
	private void checkChange(int bc_id){
		//判断BOM+序号+子件编号是否存在于BOMDetail；	
        String errProds = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(bd_detno) from bomStepChange left join bomStepChangeDetail on bd_bcid=bc_id where bc_id=? and bd_didstatuscode<>'CLOSE' and not exists (select 1 from bomdetail B where B.bd_bomid=bd_bomid and B.bd_detno=bd_bddetno and B.bd_soncode=bd_soncode) and rownum<10",
						String.class, bc_id);
		if (errProds != null) {
			BaseUtil.showError("行:" + errProds+"的BOM+BOM序号+子件编号不存在BOM明细表中");
		}		
		//判断新的工序是否在step表存在；
		errProds = baseDao
			.getJdbcTemplate()
			.queryForObject(
					"select wm_concat(bd_detno) from bomStepChangeDetail  where bd_bcid=? and bd_didstatuscode<>'CLOSE' and not exists (select 1 from Step where st_code=bd_newstepcode and st_statuscode='AUDITED') and rownum<10",
					String.class, bc_id);
		if (errProds != null) {
			BaseUtil.showError("行:" + errProds+"的新工序编号不在工序表或者为空");
		}
		//原工序不允许等于新工序
		errProds = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(bd_detno) from bomStepChangeDetail where bd_bcid=? and bd_didstatuscode<>'CLOSE' and nvl(bd_oldstepcode,' ')=bd_newstepcode and rownum<10",
						String.class, bc_id);
		if (errProds != null) {
			BaseUtil.showError("行:" + errProds+"的新工序编号不允许等于原工序编号");
		}
	}

	@Override
	public void BOMStepChangeOpenDet(int id, String caller) {
		String SQLStr = "";
		SqlRowList rs;
		int bc_id=0;
		SQLStr = "SELECT bd_id,bd_didstatus,bd_didstatuscode,bd_bcid,bc_code,bd_detno from bomstepchange,bomstepchangedetail where bc_id=bd_bcid and bd_id="+id;
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()){
			bc_id = rs.getInt("bc_id");
			if (rs.getObject("bd_didstatuscode") != null && !rs.getObject("bd_didstatuscode").equals("CLOSE")) {
				BaseUtil.showError("只能打开当前状态【关闭】的明细行");
			}  
			baseDao.updateByCondition("bomStepChangeDetail", "bd_didstatuscode='OPEN',bd_didstatus='" + BaseUtil.getLocalMessage("OPEN")
							+ "'", "bd_id=" + id); 
			// 记录操作
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "打开BOM工序变更单:"+rs.getString("bc_code")+"行号:"+rs.getInt("bd_detno"),
					"明细行打开成功", "BOMStepChange|bc_id=" + bc_id));	
		}  
	}

	@Override
	public void BOMStepChangeCloseDet(int id, String caller) {
		String SQLStr = "";
		SqlRowList rs;
		int bc_id=0;
		SQLStr = "SELECT bd_id,bd_didstatus,bd_didstatuscode,bd_bcid,bc_code,bd_detno from bomstepchange,bomstepchangedetail where bc_id=bd_bcid and bd_id="+id;
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()){
			bc_id = rs.getInt("bc_id");
			if (rs.getObject("bd_didstatuscode") != null && !rs.getObject("bd_didstatuscode").equals("OPEN")) {
				BaseUtil.showError("只能关闭当前状态【打开】的明细行");
			}  
			baseDao.updateByCondition("bomStepChangeDetail", "bd_didstatuscode='CLOSE',bd_didstatus='" + BaseUtil.getLocalMessage("CLOSE")
							+ "'", "bd_id=" + id); 
			// 记录操作
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "关闭BOM工序变更单:"+rs.getString("bc_code")+"行号:"+rs.getInt("bd_detno"),
					"明细行关闭成功", "BOMStepChange|bc_id=" + bc_id));	
		}  
	}
}
