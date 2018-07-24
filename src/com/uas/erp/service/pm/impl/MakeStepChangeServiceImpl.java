package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.MakeStepChangeService;

@Service("makeStepChangeService")
public class MakeStepChangeServiceImpl implements MakeStepChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMakeStepChange(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MakeStepChange", "mc_code='" + store.get("mc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}	
		// 执行保存前的其它逻辑
		handlerService.beforeSave("MakeStepChange", new Object[] {store,grid});
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "MakeStepChangeDetail","md_id");
		baseDao.execute(gridSql);
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MakeStepChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "mc_id", store.get("mc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave("MakeStepChange",new Object[] {store,grid});
		//序号空的明细行，根据工单编号+物料编号从用料表取序号
		baseDao.execute("update makestepchangedetail set md_mmdetno=(select max(mm_detno) from makematerial where mm_code=md_makecode and mm_prodcode=md_prodcode) where md_mcid="+store.get("mc_id")+" and md_mmdetno is null");		
	}

	@Override
	public void deleteMakeStepChange(int mc_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MakeStepChange", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel("MakeStepChange",new Object[] { mc_id});
		//判断是否有来源BOM工序变更，有，不允许删除
		Object ob = baseDao.getFieldDataByCondition("MakeStepChange", "mc_code", "mc_id="+mc_id+" and mc_sourcecode is not null");
		if(ob != null){
			BaseUtil.showError("来源于BOM工序变更的制造工序变更单不允许删除");
		}
		// 删除
		baseDao.deleteById("MakeStepChange", "mc_id", mc_id);
		// 删除Detail
		baseDao.deleteById("MakeStepChangeDetail", "md_bomid", mc_id);
		// 记录操作
		baseDao.logger.delete(caller, "mc_id", mc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("MakeStepChange",new Object[] { mc_id});
	}

	@Override
	public void updateById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("MakeStepChange", "mc_statuscode", "mc_id=" + store.get("mc_id"));
		StateAssert.updateOnlyEntering(status);	
		// 执行修改前的其它逻辑
		handlerService.beforeSave("MakeStepChange", new Object[] {store,gstore});		
		// 保存MakeStepChange
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MakeStepChange", "mc_id");
		baseDao.execute(formSql);
		// 修改Detail 
		List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore, "MakeStepChangeDetail", "md_id");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "mc_id", store.get("mc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("MakeStepChange", new Object[] { store, gstore});
		//根据工单+序号从用料表取工序编号
		baseDao.execute("update MakeStepChangeDetail set md_oldstepcode=(select mm_stepcode from makematerial A where A.mm_code=md_makecode and A.mm_detno=md_mmdetno) where md_mcid="+store.get("mc_id"));
	}

	@Override
	public void auditMakeStepChange(int mc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeStepChange", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.auditOnlyCommited(status);
		checkChange(mc_id);				
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("MakeStepChange", new Object[] { mc_id});				
		String sql = "SELECT  * from MakeStepChangeDetail left join make on md_makecode=ma_code " +
				"left join product on pr_code=md_prodcode left join makematerial on md_makecode=mm_code" +
				" and mm_detno=md_mmdetno WHERE md_mcid="+mc_id+" and NVL(md_didstatus,' ') not in('执行成功','已取消','关闭') order by md_detno";
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		String stepcode ;
		while(sqlRowList.next()){
			stepcode = sqlRowList.getString("md_newstepcode");
			if(sqlRowList.getInt("mm_detno") > 0 && sqlRowList.getString("mm_prodcode").equals(sqlRowList.getString("md_prodcode")) && !stepcode.equals(sqlRowList.getString("mm_stepcode"))){
				sql = "update makematerial set mm_stepcode='"+stepcode+"' where mm_maid='"+sqlRowList.getInt("ma_id")+"' and mm_detno=" + sqlRowList.getInt("md_mmdetno");
				baseDao.execute(sql);				
				sql = "update MakeStepChangeDetail set md_didstatus='执行成功' where md_mcid='" + mc_id + "' and md_detno=" + sqlRowList.getInt("md_detno");
				baseDao.execute(sql);
			}else{
				sql = "update MakeStepChangeDetail set md_didstatus='执行失败' where md_mcid='" + mc_id + "' and md_detno=" + sqlRowList.getInt("md_detno");
				baseDao.execute(sql);
			}				
		} 
		// 执行审核操作
		baseDao.audit("MakeStepChange", "mc_id=" + mc_id, "mc_status", "mc_statuscode","mc_auditdate","mc_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "mc_id", mc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit("MakeStepChange",new Object[] { mc_id});
	}

	@Override
	public void submitMakeStepChange(int mc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeStepChange", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.submitOnlyEntering(status);	
		//不允许两条 重复的制造单号+用料表序号
        SqlRowList rs = baseDao.queryForRowSet("select count(*) cn ,wm_concat(md_mmdetno) detno from makestepchangedetail where md_mcid=? group by md_makecode,md_mmdetno having count(*)>1",mc_id);
        if(rs.next() && rs.getInt("cn")>0){
        	 BaseUtil.showError("序号:"+rs.getString("detno")+",【不同明细行】制造单号+用料表序号重复");
        }
		checkChange(mc_id);				
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit("MakeStepChange", new Object[] { mc_id});
		// 执行提交操作
		baseDao.submit("MakeStepChange", "mc_id=" + mc_id, "mc_status", "mc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mc_id", mc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit("MakeStepChange", new Object[] { mc_id});
	}

	@Override
	public void resSubmitMakeStepChange(int mc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeStepChange", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("MakeStepChange", new Object[] { mc_id });
		// 执行反提交操作
		baseDao.resOperate("MakeStepChange", "mc_id=" + mc_id, "mc_status", "mc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mc_id", mc_id);
		handlerService.afterResSubmit("MakeStepChange",new Object[] { mc_id});
	} 
	
	private void checkChange(int mc_id){
		//判断工单是否已审核、且ma_madeqty<ma_qty；
		 String errProds = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(md_detno) from MakeStepChangeDetail left join make on ma_code=md_makecode where md_mcid=? and (nvl(ma_statuscode,' ')<>'AUDITED' OR nvl(ma_madeqty,0)>=nvl(ma_qty,0)) and md_didstatus not in('已取消','关闭')  and rownum<10",
							String.class, mc_id);
		 if (errProds != null) {
			  BaseUtil.showError("行:" + errProds+"的工单必须是已审核并且已完工数小于制单数");
		 }
		//判断制造单号+序号+物料编号，是否存在于用料表；
		 errProds = baseDao
			.getJdbcTemplate()
			.queryForObject(
					"select wm_concat(md_detno) from MakeStepChangeDetail where md_mcid=? and not exists (select 1 from makematerial B where B.mm_code=md_makecode and B.mm_detno=md_mmdetno and B.mm_prodcode=md_prodcode) and rownum<10",
					String.class, mc_id);
		 if (errProds != null) {
			 BaseUtil.showError("行:" + errProds+"的制造单号+工单序号+物料编号不存在用料表中");
		 }
		//判断新的工序是否在step表存在；
		errProds = baseDao
			.getJdbcTemplate()
			.queryForObject(
					"select wm_concat(md_detno) from MakeStepChangeDetail  where md_mcid=? and not exists (select 1 from Step where st_code=md_newstepcode AND st_statuscode='AUDITED') and rownum<10",
					String.class, mc_id);
		if (errProds != null) {
			BaseUtil.showError("行:" + errProds+"的新工序编号不在工序表中或者为空");
		}
		//原工序不允许等于新工序
		errProds = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(md_detno) from MakeStepChangeDetail  where md_mcid=? and nvl(md_oldstepcode,' ')=md_newstepcode and rownum<10",
						String.class, mc_id);
		if (errProds != null) {
			BaseUtil.showError("行:" + errProds+"的新工序编号不允许等于原工序编号");
		}
	}
	
	@Override
	public void MakeStepChangeCloseDet(int id, String caller) { 
		String SQLStr = "";
		SqlRowList rs;
		int mc_id=0;
		SQLStr = "SELECT mc_code,md_id,md_detno,md_mcid,mc_statuscode,md_didstatus from makeStepchange,makeStepchangeDetail where mc_id=md_mcid and md_id="+id;
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			mc_id=rs.getInt("md_mcid");
			if(rs.getString("mc_statuscode").equals("AUDITED") ){
				BaseUtil.showError("不能操作已审核的单据");
				return;
			}
			if (rs.getString("md_didstatus")!=null && rs.getString("md_didstatus").equals("执行成功")){
				BaseUtil.showError("不能操作已执行成功的明细行");
				return;
			}
			if (rs.getObject("md_didstatus")!=null && rs.getObject("md_didstatus").equals("已取消")) {
				BaseUtil.showError("当前行已处于[已取消]状态");
			}  
			baseDao.updateByCondition("makeStepchangeDetail", "md_didstatus='已取消'", "md_id=" + id); 
			// 记录操作
			baseDao.logger.getMessageLog("转取消执行,行号:"+rs.getInt("md_detno"), "明细行取消执行成功", caller, "mc_id", mc_id);
		}  
	}
	@Override
	public void MakeStepChangeOpenDet(int id, String caller) { 
		String SQLStr = "";
		SqlRowList rs;
		int mc_id=0;
		SQLStr = "SELECT mc_code,md_id,md_detno,md_mcid,mc_statuscode,md_didstatus from makeStepchange,makeStepchangeDetail where mc_id=md_mcid and md_id="+id;
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			mc_id=rs.getInt("md_mcid");
			if(rs.getString("mc_statuscode").equals("AUDITED") ){
				BaseUtil.showError("不能操作已审核的单据");
				return;
			}
			if (rs.getString("md_didstatus")!=null && rs.getString("md_didstatus").equals("执行成功")){
				BaseUtil.showError("不能操作已执行成功的明细行");
				return;
			}
			if (rs.getObject("md_didstatus")!=null && !rs.getObject("md_didstatus").equals("已取消")) {
				BaseUtil.showError("只能操作[已取消]的明细行");
			}  			
			baseDao.updateByCondition("makeStepchangeDetail", "md_didstatus='待执行'", "md_id=" + id); 
			// 记录操作
			baseDao.logger.getMessageLog("转待执行,行号:"+rs.getInt("md_detno"), "明细行待执行成功", caller, "mc_id", mc_id); 
		}  			 
	}
}
