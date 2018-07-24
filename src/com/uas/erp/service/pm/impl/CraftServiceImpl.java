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
import com.uas.erp.service.pm.CraftService;

@Service("craftService")
public class CraftServiceImpl implements CraftService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCraft(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Craft", "cr_code='" + store.get("cr_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_bocodeHasExist"));
		}
		checkCraftRepeat(caller,store);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存ProductSMT
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "Craft"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "CraftDetail", "cd_id"));
		// 判断是否cd_stepno 字段是否填写，没有填写则
		int cn = baseDao.getCount("select count(1) from craftdetail left join craft on cd_crid=cr_id where cr_code='" + store.get("cr_code") + "' and cd_stepno is not null");
		if (cn == 0) {// 没有填写则，强制根据cd_detno 排序更新字段 从1开始
			baseDao.execute(" update craftdetail a set cd_stepno =(select rn from (select cd_detno,row_number() over(order by cd_detno asc) rn from craftdetail b left join craft on cr_id=cd_crid" + " where cr_code='" + store.get("cr_code") + "') b where a.cd_detno=b.cd_detno )");
		}
		//更新cr_boid,插入工艺展开明细CraftStruct
		Object boid=baseDao.getFieldDataByCondition("bom", "max(bo_id)", "bo_craftcode='"+ store.get("cr_code")+"'");
		if(boid !=null && !"0".equals(boid)){
			baseDao.updateByCondition("Craft", "cr_boid="+boid,"cr_id="+store.get("cr_id"));
			baseDao.callProcedure("MM_SETPRODBOMSTRUCT",new Object[] {  Integer.valueOf(boid.toString() ),null });
		}
		// 记录操作
		baseDao.logger.save(caller, "cr_id", store.get("cr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	private void checkCraftRepeat(String caller, Map<Object, Object> store) {
		if(!baseDao.isDBSetting(caller, "CraftRepeat")){
			Object crcode=baseDao.getFieldDataByCondition("craft", "cr_code","cr_prodcode='"+store.get("cr_prodcode")+"' and cr_id<>"+store.get("cr_id"));
			if (crcode !=null && !"".equals(crcode)){
				BaseUtil.showError("已经存在此物料的工艺路线"+crcode+"，不能重复录入");
			}
		}
	}

	@Override
	public void deleteCraft(int cr_id, String caller) {
		// 只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("Craft", "cr_statuscode", "cr_id=" + cr_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { cr_id });
		// 删除Craft
		baseDao.deleteById("Craft", "cr_id", cr_id);
		// 删除Craftdetail
		baseDao.deleteById("Craftdetail", "cd_crid", cr_id);
		// 记录操作
		baseDao.logger.delete(caller, "cr_id", cr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { cr_id });
	}

	@Override
	public void deleteDetail(int cd_id, String caller) {
		baseDao.deleteById("Craftdetail", "cd_id", cd_id);
	}

	@Override
	public void updateCraftById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的工艺资料!
		Object status = baseDao.getFieldDataByCondition("Craft", "cr_statuscode", "cr_id=" + store.get("cr_id"));
		StateAssert.updateOnlyEntering(status);
		
		
		checkCraftRepeat(caller,store);
//		Object crcode=baseDao.getFieldDataByCondition("craft", "cr_code","cr_prodcode='"+store.get("cr_prodcode")+"' and cr_id<>"+store.get("cr_id"));
//		if (crcode !=null && !"".equals(crcode)){
//			BaseUtil.showError("已经存在此物料的工艺路线"+crcode+"，不能重复录入");
//		}
		
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改Craft
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "Craft", "cr_id"));
		// 修改CraftDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "CraftDetail", "cd_id"));
		// 判断是否cd_stepno 字段是否填写，没有填写则
		int cn = baseDao.getCount("select count(1) from craftdetail  where cd_crid=" + store.get("cr_id") + " and cd_stepno is not null");
		if (cn == 0) {// 没有填写则，强制根据cd_detno 排序更新字段 从1开始
			baseDao.execute(" update craftdetail a set cd_stepno =(select rn from (select cd_detno,row_number() over(order by cd_detno asc) rn from craftdetail b " + " where cd_crid=" + store.get("cr_id") + ") b where a.cd_detno=b.cd_detno )");
		}
		//更新cr_boid,插入工艺展开明细CraftStruct
		Object boid=baseDao.getFieldDataByCondition("bom", "max(bo_id)", "bo_craftcode='"+ store.get("cr_code")+"'");
		if(boid !=null && !"0".equals(boid)){
			baseDao.updateByCondition("Craft", "cr_boid="+boid,"cr_id="+store.get("cr_id"));
			baseDao.callProcedure("MM_SETPRODBOMSTRUCT",new Object[] {  Integer.valueOf(boid.toString() ),null });
		}		
		// 记录操作
		baseDao.logger.update(caller, "cr_id", store.get("cr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
		baseDao.execute("update craft set cr_stepcount=(select count(1) from CRAFTDETAIL where cr_id =cd_crid ),cr_price=(select sum(cd_price) from CRAFTDETAIL where cr_id =cd_crid ) where cr_id="+store.get("cr_id"));
		
	}

	@Override
	public void auditCraft(int cr_id, String caller) {
		// 只能对状态为[已提交]的工艺资料进行审核操作!
		Object[] ob = baseDao.getFieldsDataByCondition("Craft", new String[] { "cr_statuscode", "cr_code",
		"cr_prodcode" }, "cr_id=" + cr_id);
		StateAssert.auditOnlyCommited(ob[0]);
		if(!baseDao.isDBSetting(caller, "CraftRepeat")){
			Object cr_prodcode = baseDao.getFieldDataByCondition("craft", "cr_prodcode", "cr_id="+cr_id);
			Object crcode=baseDao.getFieldDataByCondition("craft", "cr_code","cr_prodcode='"+cr_prodcode+"' and cr_id<>"+cr_id);
			if (crcode !=null && !"".equals(crcode)){
				BaseUtil.showError("已经存在此物料的工艺路线"+crcode+"，不能重复录入");
			}
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { cr_id });
		//更新bom.bo_craftcode
		if(ob[1]!=null && !"".equals(ob[1]) && ob[2]!=null && !"".equals(ob[2])){
			baseDao.updateByCondition("bom", "bo_craftcode='"+ob[1]+"'", "bo_mothercode='"+ob[2]+"'");
		}
		// 执行审核操作
		baseDao.audit("Craft", "cr_id=" + cr_id, "cr_status", "cr_statuscode", "cr_auditdate", "cr_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "cr_id", cr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { cr_id });
	}

	@Override
	public void resAuditCraft(int cr_id, String caller) {
		// 只能对状态为[已审核]的工艺资料进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Craft", "cr_statuscode", "cr_id=" + cr_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("Craft", "cr_id=" + cr_id, "cr_status", "cr_statuscode", "cr_auditdate", "cr_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "cr_id", cr_id);
	}

	@Override
	public void submitCraft(int cr_id, String caller) {
		// 检测工艺路线执行顺序不能出现重复
		if (baseDao.isDBSetting(caller, "checkWorkLine")) {
			SqlRowList rs1 = baseDao.queryForRowSet("select cd_stepno from craftdetail where cd_crid=" + cr_id);
			String[] stepno = new String[rs1.size()];
			while (rs1.next()) {
				stepno[rs1.getCurrentIndex()] = rs1.getString("cd_stepno");
			}
			if (BaseUtil.checkDuplicateArray(stepno)) {
				BaseUtil.showError("请检查执行顺序，不允许出现重复");
			}
		}
		// 只能对状态为[在录入]的工艺资料进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Craft", "cr_statuscode", "cr_id=" + cr_id);
		StateAssert.submitOnlyEntering(status);
		if(!baseDao.isDBSetting(caller, "CraftRepeat")){
			Object cr_prodcode = baseDao.getFieldDataByCondition("craft", "cr_prodcode", "cr_id="+cr_id);
			Object crcode=baseDao.getFieldDataByCondition("craft", "cr_code","cr_prodcode='"+cr_prodcode+"' and cr_id<>"+cr_id);
			if (crcode !=null && !"".equals(crcode)){
				BaseUtil.showError("已经存在此物料的工艺路线"+crcode+"，不能重复录入");
			}
		}
		
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { cr_id });
		// 判断是否cd_stepno 字段是否填写，没有填写则
		SqlRowList rs = baseDao.queryForRowSet("select count(1) cn ,wmsys.wm_concat(cd_detno) detno from craftdetail where cd_crid=" + cr_id + " and cd_stepno is null");
		if (rs.next() && rs.getInt("cn") > 0) {// 没有填写则，提示不让提交填写字段值
			BaseUtil.showError("序号：" + rs.getString("detno") + ",执行顺序没有填写不允许提交!");
		}
		rs = baseDao.queryForRowSet("select count(1) cn ,wmsys.wm_concat(cd_detno) detno  from craftdetail where cd_crid=" + cr_id + " group by cd_stepno   having count (*)>1");
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				BaseUtil.showError("序号：" + rs.getString("detno") + ",执行顺序重复不允许提交!");
			}
		}
		// 执行提交操作
		baseDao.submit("Craft", "cr_id=" + cr_id, "cr_status", "cr_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "cr_id", cr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { cr_id });
		baseDao.execute("update craft set cr_stepcount=(select count(1) from CRAFTDETAIL where cr_id =cd_crid ),cr_price=(select sum(cd_price) from CRAFTDETAIL where cr_id =cd_crid ) where cr_id="+cr_id);
	}

	@Override
	public void resSubmitCraft(int cr_id, String caller) {
		// 只能对状态为[已提交]的工艺资料进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Craft", "cr_statuscode", "cr_id=" + cr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { cr_id });
		// 执行反提交操作
		baseDao.resOperate("Craft", "cr_id=" + cr_id, "cr_status", "cr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "cr_id", cr_id);
		handlerService.afterResSubmit(caller, new Object[] { cr_id });
	}

	@Override
	public void saveStepCollection(String caller, String formStore, String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		// 只能对未审核的工艺进行工序采集信息维护
		Object status = baseDao.getFieldDataByCondition("Craft", "cr_statuscode", "cr_id=" + store.get("cr_id"));
		if (status.equals("AUDITED")) {
			BaseUtil.showError("工艺单已审核，不允许修改工序采集信息！");
		}
		// 修改stepProduct
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(grid, "stepProduct", "sp_id"));
		// 记录操作
		baseDao.logger.getMessageLog("明细行：" + store.get("cp_detno") + ",工序采集信息维护", "保存成功", caller, "cr_id", store.get("cr_id"));
	}
	@Override
	public void refreshCrafts(String code){
		Object boid=baseDao.getFieldDataByCondition("bom", "max(bo_id)", "bo_craftcode='"+ code+"'");
		Object crid = baseDao.getFieldDataByCondition("Craft", "cr_id", "cr_code='"+code+"'");
		if(boid !=null && !"0".equals(boid)){
			baseDao.updateByCondition("Craft", "cr_boid="+boid,"cr_id="+crid);
			baseDao.callProcedure("MM_SETPRODBOMSTRUCT",new Object[] {  Integer.valueOf(boid.toString() ),null });
		}
	}
}
