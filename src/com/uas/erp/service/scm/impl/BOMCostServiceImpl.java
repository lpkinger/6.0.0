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
import com.uas.erp.service.scm.BOMCostService;

@Service("BOMCostService")
public class BOMCostServiceImpl implements BOMCostService{
	
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveBOMCost(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave("BOMCost", new Object[] {store, grid});
		//保存BOMCost
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BOMCost", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存BOMCostDetail
		for(Map<Object, Object> m : grid) {
			m.put("bcd_id", baseDao.getSeqId("BOMCOSTDETAIL_SEQ"));
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "BOMCostDetail"));
		//记录操作
		baseDao.logger.save("BOMCost", "bc_id", store.get("bc_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave("BOMCost", new Object[] {store, grid});
	}
	
	@Override
	public void deleteBOMCost(int bc_id) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BOMCost", "bc_checkstatuscode", "bc_id=" + bc_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel("BOMCost", bc_id);
		//删除BOMCost
		baseDao.deleteById("BOMCost", "bc_id", bc_id);
		//删除BOMCostDetail
		baseDao.deleteById("BOMCostDetail", "bcd_bcid", bc_id);
		//记录操作
		baseDao.logger.delete("BOMCost", "bc_id", bc_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("BOMCost", bc_id);
	}
	
	@Override
	public void updateBOMCostById(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("BOMCost", "bc_checkstatuscode", "bc_id=" + store.get("bc_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave("BOMCost", new Object[] {store, gstore});
		//修改BOMCost
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BOMCost", "bc_id");
		baseDao.execute(formSql);
		//修改BOMCostDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "BOMCostDetail", "bcd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("bcd_id") == null || s.get("bcd_id").equals("") || s.get("bcd_id").equals("0") ||
					Integer.parseInt(s.get("bcd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("BOMCOSTDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BOMCostDetail", new String[]{"bcd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.updateByCondition("BOMCost", "bc_materialcost=(SELECT nvl(sum(bcd_amount),0) FROM BOMCostDetail WHERE bcd_bcid="
				+ store.get("bc_id") + ")", "bc_id=" + store.get("bc_id"));
		baseDao.updateByCondition("BOMCost", "bc_cost=(SELECT nvl((bc_materialcost+bc_makecost+bc_mancost+bc_othercost),0) FROM BOMCost WHERE bc_id="
				+ store.get("bc_id") + ")", "bc_id=" + store.get("bc_id"));
		//记录操作
		baseDao.logger.update("BOMCost", "bc_id", store.get("bc_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave("BOMCost", new Object[] {store, gstore});
	}
	@Override
	public String[] printBOMCost(int bc_id,String reportName,String condition) {
		//执行打印前的其它逻辑
		handlerService.beforePrint("BOMCost", bc_id);
		//执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		//记录操作
		baseDao.logger.print("BOMCost", "bc_id", bc_id);
		//执行打印后的其它逻辑
		handlerService.afterPrint("BOMCost", bc_id);
		return keys;
	}
	
	@Override
	public void auditBOMCost(int bc_id) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BOMCost", "bc_checkstatuscode", "bc_id=" + bc_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit("BOMCost", bc_id);
		//执行审核操作
		baseDao.audit("BOMCost", "bc_id=" + bc_id, "bc_checkstatus", "bc_checkstatuscode", "bc_auditdate", "bc_auditman");
		//记录操作
		baseDao.logger.audit("BOMCost", "bc_id", bc_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit("BOMCost", bc_id);
	}
	@Override
	public void resAuditBOMCost(int bc_id) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BOMCost", "bc_checkstatuscode", "bc_id=" + bc_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit("BOMCost", bc_id);
		//执行反审核操作
		baseDao.resOperate("BOMCost", "bc_id=" + bc_id, "bc_checkstatus", "bc_checkstatuscode");
		//记录操作
		baseDao.logger.resAudit("BOMCost", "bc_id", bc_id);
		handlerService.afterResAudit("BOMCost", bc_id);
	}
	@Override
	public void submitBOMCost(int bc_id) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BOMCost", "bc_checkstatuscode", "bc_id=" + bc_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit("BOMCost", bc_id);
		//执行提交操作
		baseDao.submit("BOMCost", "bc_id=" + bc_id, "bc_checkstatus", "bc_checkstatuscode");
		//记录操作
		baseDao.logger.submit("BOMCost", "bc_id", bc_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit("BOMCost", bc_id);
	}
	
	@Override
	public void resSubmitBOMCost(int bc_id) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BOMCost", "bc_checkstatuscode", "bc_id=" + bc_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行提交前的其它逻辑
		handlerService.beforeResSubmit("BOMCost", bc_id);
		//执行提交操作
		baseDao.resOperate("BOMCost", "bc_id=" + bc_id, "bc_checkstatus", "bc_checkstatuscode");
		//记录操作
		baseDao.logger.resSubmit("BOMCost", "bc_id", bc_id);
		//执行提交后的其它逻辑
		handlerService.afterResSubmit("BOMCost", bc_id);
	}

	@Override
	public void bannedBOMCost(int bc_id) {
		//执行禁用操作
		baseDao.banned("BOMCost", "bc_id=" + bc_id, "bc_checkstatus", "bc_checkstatuscode");
		//记录操作
		baseDao.logger.banned("BOMCost", "bc_id", bc_id);
	}

	@Override
	public void resBannedBOMCost(int bc_id) {
		//执行反禁用操作
		baseDao.resOperate("BOMCost", "bc_id=" + bc_id, "bc_checkstatus", "bc_checkstatuscode");
		//记录操作
		baseDao.logger.resBanned("BOMCost", "bc_id", bc_id);
	}
	
	static final String INSERTBOMCostDETAIL= "INSERT INTO BOMCostdetail(bcd_id, bcd_bcid, bcd_detno, bcd_prodcode, bcd_qty," +
			"bcd_currency,bcd_rate,bcd_doubleprice,bcd_price,bcd_amount,bcd_prodid,bcd_iscustprod,bcd_level,bcd_ifrep) values" +
			"(BOMCOSTDETAIL_SEQ.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTBOMCostDETAIL2= "INSERT INTO BOMCostdetail(bcd_id, bcd_bcid, bcd_detno, bcd_prodcode, bcd_bomid) values" +
			"(BOMCOSTDETAIL_SEQ.nextval,?,?,?,?)";
	
	@Override
	public void bomInsert(int bc_id) {
		int count = baseDao.getCountByCondition("BOMCostDetail", "bcd_bcid=" + bc_id);
		if(count > 0){
			BaseUtil.showError("明细里已经有产品明细不能导入!");
		} else {
			SqlRowList rs = baseDao.queryForRowSet("select bo_mothercode,bo_id from bom where bo_statuscode in ('COMMITED','AUDITED') order by bo_mothercode");
			int detno = 1;
			while(rs.next()){
				baseDao.getJdbcTemplate().update(INSERTBOMCostDETAIL2, new Object[]{bc_id, detno++, rs.getObject("bo_mothercode"),
						rs.getObject("bo_id")});
			}
			Object maxdetno = baseDao.getFieldDataByCondition("BOMCostDetail", "max(bcd_detno)+1", "bcd_bcid=" + bc_id);
			rs = baseDao.queryForRowSet("select pr_code from product where pr_specvalue='SPECIFIC' and pr_statuscode='AUDITED'");
			detno = Integer.parseInt(maxdetno.toString());
			while(rs.next()){
				baseDao.getJdbcTemplate().update(INSERTBOMCostDETAIL2, new Object[]{bc_id, detno++, rs.getObject("pr_code"), 0});
			}
		}
		//记录操作
		baseDao.logger.others("导入建立BOM的产品", "导入成功", "BOMCost", "bc_id", bc_id);
	}
	@Override
	public void bomVastCost(int bc_id) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BOMCost", "bc_checkstatuscode", "bc_id=" + bc_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError("只能对[在录入]的单据进行操作!");
		}
		//存储过程
		String res = baseDao.callProcedure("SP_CACBOMCOST", new Object[]{bc_id});
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		//记录操作
		baseDao.logger.others("批量计算产品BOM成本", "计算成功", "BOMCost", "bc_id", bc_id);
	}
}
