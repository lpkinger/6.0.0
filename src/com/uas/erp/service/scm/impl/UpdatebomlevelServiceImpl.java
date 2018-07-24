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
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.UpdatebomlevelService;

@Service
public class UpdatebomlevelServiceImpl implements UpdatebomlevelService {

	static final String selectcode = "select ub_stype from Updatebomlevel where ub_id=?";

	static final String getProductCode = "select ud_prodcode from Updatebomleveldetail where ud_cpid=?";

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveUpdatebomlevel(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		if (store.get("ub_code").toString().trim().equals("")) {
			String code = baseDao.sGetMaxNumber("Updatebomlevel", 2);
			store.put("ub_code", code);
		}
		formStore = BaseUtil.parseMap2Str(store);
		handlerService.handler("Updatebomlevel", "save", "before", new Object[] { store, grid });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Updatebomlevel", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存UpdatebomlevelDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "UpdatebomlevelDetail", "ud_id");
		baseDao.execute(gridSql);
		baseDao.logger.save("Updatebomlevel", "ub_id", store.get("ub_id"));
		// 执行保存后的其它逻辑
		handlerService.handler("Updatebomlevel", "save", "after", new Object[] { store, grid });
	}

	@Override
	public void updateUpdatebomlevelById(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Updatebomlevel", "ub_statuscode", "ub_id=" + store.get("ub_id"));
		StateAssert.updateOnlyEntering(status);
		handlerService.handler("Updatebomlevel", "save", "before", new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Updatebomlevel", "ub_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "UpdatebomlevelDetail", "ud_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ud_id") == null || s.get("ud_id").equals("") || s.get("ud_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("UpdatebomlevelDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "UpdatebomlevelDetail", new String[] { "ud_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update("Updatebomlevel", "ub_id", store.get("ub_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("Updatebomlevel", "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void deleteUpdatebomlevel(int ub_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Updatebomlevel", "ub_statuscode", "ub_id=" + ub_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler("Updatebomlevel", "delete", "before", new Object[] { ub_id });
		// 删除purchase
		baseDao.deleteById("Updatebomlevel", "ub_id", ub_id);
		// 删除purchaseDetail
		baseDao.deleteById("Updatebomleveldetail", "ud_cpid", ub_id);
		// 记录操作
		baseDao.logger.delete("Updatebomlevel", "ub_id", ub_id);
		// 执行删除后的其它逻辑
		handlerService.handler("Updatebomlevel", "delete", "after", new Object[] { ub_id });
	}

	@Override
	public void auditUpdatebomlevel(int ub_id) {
		SqlRowList rs0,rs1; 
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Updatebomlevel", "ub_statuscode", "ub_id=" + ub_id);
		StateAssert.auditOnlyCommited(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('变更单号：'||ub_code||'，BOM：'||ud_bomid) from Updatebomlevel left join Updatebomleveldetail on ub_id=ud_cpid where nvl(ub_statuscode,' ') in ('COMMITED','ENTERING') and ub_id<>?"
								+ " and ud_bomid in (select ud_bomid from Updatebomleveldetail where ud_cpid=?)",
						String.class, ub_id, ub_id);
		if (dets != null) {
			BaseUtil.showError("BOM存在未审核的BOM等级变更单，不允许提交!" + dets);
		}
		Object bl_code=baseDao.getFieldDataByCondition("Updatebomlevel", "ub_stype", "ub_id=" + ub_id);
		if (bl_code==null || bl_code.equals("")){
			BaseUtil.showError("[变更BOM等级]不能为空");
		} 
		//BOM等级有效性检测
		BOM_Check_Level(ub_id);
		//判断子件BOM等级是否到达母件的BOM等级
		int bl_grade=Integer.parseInt(baseDao.getFieldDataByCondition("BOMlevel", "NVL(bl_grade,0)", "bl_code='"+bl_code.toString()+"'").toString());
		rs1= baseDao.queryForRowSet("select bo_id from Updatebomleveldetail left join bom on bo_id=ud_bomid left join bomlevel on bl_code=bo_level  where ud_cpid="+ub_id+" and bo_id>0");
		while (rs1.next()) { 
			rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat(bd_detno) detno from bomdetail left join bom on bo_mothercode=bd_soncode left join product on pr_code=bd_soncode left join bomlevel on bl_code=bo_level where bd_bomid='" + rs1.getInt("bo_id")+ "' and pr_manutype in ('MAKE','OSMAKE') and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(bl_grade,0)<" + bl_grade+" and bo_id>0 and not exists(select 1 from Updatebomleveldetail where ud_cpid="+ub_id+" and ud_prodcode=bd_soncode )");
			if (rs0.next()) {
				if (rs0.getInt("num")>0){ 
					BaseUtil.showError("子件的BOM等级不能低于母件的等级，BOM："+rs1.getInt("bo_id")+"序号：："+rs0.getString("detno")+"");
				} 
			} 
		}
		// 执行审核前的其它逻辑
		handlerService.handler("Updatebomlevel", "audit", "before", new Object[] { ub_id });
		// 执行审核操作
		baseDao.audit("Updatebomlevel", "ub_id=" + ub_id, "ub_status", "ub_statuscode", "ub_auditdate", "ub_auditman");
		baseDao.execute("update bom set bo_level='"+bl_code+"' where bo_id in (select ud_bomid from Updatebomleveldetail where ud_cpid="+ub_id+" and ud_bomid>0)");
		baseDao.logger.audit("Updatebomlevel", "ub_id", ub_id);
		// 执行审核后的其它逻辑
		handlerService.handler("Updatebomlevel", "audit", "after", new Object[] { ub_id });
	}

	@Override
	public void resAuditUpdatebomlevel(int ub_id) {
		Object status = baseDao.getFieldDataByCondition("Updatebomlevel", "ub_statuscode", "ub_id=" + ub_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resOperate("Updatebomlevel", "ub_id=" + ub_id, "ub_status", "ub_statuscode");
		// 记录操作
		baseDao.logger.resAudit("Updatebomlevel", "ub_id", ub_id);
	}

	@Override
	public void submitUpdatebomlevel(int ub_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Updatebomlevel", "ub_statuscode", "ub_id=" + ub_id);
		StateAssert.submitOnlyEntering(status); 
		// 执行提交前的其它逻辑
		handlerService.handler("Updatebomlevel", "commit", "before", new Object[] { ub_id });
		//是否只允许一条明细
		if(baseDao.isDBSetting("Updatebomlevel", "OnlyOneDetail")){
			if(baseDao.getCount("select count(*) from Updatebomleveldetail where ud_cpid="+ub_id)!=1){
				BaseUtil.showError("BOM等级变更单只能有一条明细!");
			}
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('变更单号：'||ub_code||'，BOM：'||ud_bomid) from Updatebomlevel left join Updatebomleveldetail on ub_id=ud_cpid where nvl(ub_statuscode,' ') in ('COMMITED','ENTERING') and ub_id<>?"
								+ " and ud_bomid in (select ud_bomid from Updatebomleveldetail where ud_cpid=?)",
						String.class, ub_id, ub_id);
		if (dets != null) {
			BaseUtil.showError("BOM存在未审核的BOM等级变更单，不允许提交!" + dets);
		}
		//BOM等级有效性检测
		BOM_Check_Level(ub_id);
		// 执行提交操作
		baseDao.submit("Updatebomlevel", "ub_id=" + ub_id, "ub_status", "ub_statuscode");
		// 记录操作
		baseDao.logger.submit("Updatebomlevel", "ub_id", ub_id);
		// 执行提交后的其它逻辑
		handlerService.handler("Updatebomlevel", "commit", "after", new Object[] { ub_id });
	}

	@Override
	public void resSubmitUpdatebomlevel(int ub_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Updatebomlevel", "ub_statuscode", "ub_id=" + ub_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler("Updatebomlevel", "resCommit", "before", new Object[] { ub_id });
		// 执行反提交操作
		baseDao.resOperate("Updatebomlevel", "ub_id=" + ub_id, "ub_status", "ub_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("Updatebomlevel", "ub_id", ub_id);
		handlerService.handler("Updatebomlevel", "resCommit", "after", new Object[] { ub_id });

	}
	/**
	 * 子件BOM等级系数不能低于母件、物料等级须符合BOM等级要求
	 * @author ZHONGYL
	 * */
	public void BOM_Check_Level(Integer ub_id){
		String SQLStr = "";
		SqlRowList rs,rs0,rs1; 
		Object bl_code=baseDao.getFieldDataByCondition("Updatebomlevel", "ub_stype", "ub_id=" + ub_id);
		if (bl_code==null || bl_code.equals("")){
			BaseUtil.showError("[变更BOM等级]不能为空");
		} 
		rs1= baseDao.queryForRowSet("select bo_id from Updatebomleveldetail left join bom on bo_id=ud_bomid left join bomlevel on bl_code=bo_level  where ud_cpid="+ub_id+" and bo_id>0");
		while (rs1.next()) { 
			//判断物料等级是否满足BOM等级要求
			SQLStr = "select NVL(sum((case when NVL(pd_useable,0)=0 then 1 else 0 end)),0) as disnum,count(1) as allnum  from Productleveldetail left join bomlevel on bl_id=pd_blid  where bl_code='"+bl_code.toString()+"'  ";
			rs = baseDao.queryForRowSet(SQLStr);
			if (rs.next()) {
				if (rs.getInt("disnum") > 0) {
					//判断是否有禁用的物料等级
					rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat(bd_detno) detno from bomdetail left join product on pr_code=bd_soncode where bd_bomid='" + rs1.getInt("bo_id")+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and pr_level in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='"+bl_code.toString()+"') ");
					if (rs0.next()) {
						if (rs0.getInt("num")>0){
							BaseUtil.showError("BOM："+rs1.getInt("bo_id")+"序号："+rs0.getString("detno")+"的物料优选等级在BOM等级定义里面被禁用");
						} 
					}
					//判断替代料是否有禁用的物料等级
					rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat('序号:'||bd_detno||'替代料:'||pre_repcode) detno from prodreplace left join bomdetail on pre_bdid=bd_id left join product on pr_code=pre_repcode where pre_bomid='" + rs1.getInt("bo_id")+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(pre_statuscode,' ')<>'DISABLE' and pr_level in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='"+bl_code.toString()+"') ");
					if (rs0.next()) {
						if (rs0.getInt("num")>0){
							BaseUtil.showError("BOM："+rs1.getInt("bo_id")+"序号："+rs0.getString("detno")+"，物料优选等级在BOM等级定义里面被禁用");
						} 
					}
				} 
				if (rs.getInt("allnum") > 0 && rs.getInt("disnum")==0){
					//判断是否有物料等级达到要求等级
					rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat(bd_detno) detno from bomdetail left join product on pr_code=bd_soncode where bd_bomid='" + rs1.getInt("bo_id")+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and pr_level not in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='"+bl_code.toString()+"')  ");
					if (rs0.next()) {
						if (rs0.getInt("num")>0){
							BaseUtil.showError("BOM："+rs1.getInt("bo_id")+"序号："+rs0.getString("detno")+"的物料优选等级还没有到达BOM等级要求");
						} 
					}
					//判断替代料是否有物料等级达到要求等级
					rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat('序号:'||bd_detno||'替代料:'||pre_repcode) detno from prodreplace left join bomdetail on pre_bdid=bd_id  left join product on pr_code=pre_repcode where bd_bomid='" + rs1.getInt("bo_id")+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(pre_statuscode,' ')<>'DISABLE' and pr_level not in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='"+bl_code.toString()+"')  ");
					if (rs0.next()) {
						if (rs0.getInt("num")>0){
							BaseUtil.showError("BOM："+rs1.getInt("bo_id")+"序号："+rs0.getString("detno")+"的物料优选等级还没有到达BOM等级要求");
						} 
					}
				}
			}  
		} 
    } 
	
}
