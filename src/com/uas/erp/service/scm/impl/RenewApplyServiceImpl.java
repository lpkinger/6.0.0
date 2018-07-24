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
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.service.scm.RenewApplyService;

@Service("renewApplyService")
public class RenewApplyServiceImpl implements RenewApplyService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;
	
	@Override
	public void saveRenewApply(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("RenewApply", "ra_code='" + store.get("ra_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, grid});
		//保存RenewApply
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "RenewApply", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存RenewApplyDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "RenewApplyDetail", "rad_id");
		baseDao.execute(gridSql);
		String sql="update RenewApplyDetail set rad_amount=round(nvl(rad_price,0)*nvl(rad_qty,0),2) where rad_raid="+store.get("ra_id");
		baseDao.execute(sql);
		baseDao.logger.save(caller, "ra_id", store.get("ra_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, grid});
	}
	@Override
	public void deleteRenewApply(int ra_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("RenewApply", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{ra_id});
		//删除RenewApply
		baseDao.deleteById("RenewApply", "ra_id", ra_id);
		//删除RenewApplyDetail
		baseDao.deleteById("Renewapplydetail", "rad_raid", ra_id);
		//记录操作
		baseDao.logger.delete(caller, "ra_id", ra_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{ra_id});
	}
	
	@Override
	public void updateRenewApplyById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("RenewApply", "ra_statuscode", "ra_id=" + store.get("ra_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, gstore});
		//修改RenewApply
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "RenewApply", "ra_id");
		baseDao.execute(formSql);
		//修改RenewApplyDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "RenewApplyDetail", "rad_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("rad_id") == null || s.get("rad_id").equals("") || s.get("rad_id").equals("0") ||
					Integer.parseInt(s.get("rad_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("RENEWAPPLYDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "RenewApplyDetail", new String[]{"rad_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		String sql="update RenewApplyDetail set rad_amount=round(nvl(rad_price,0)*nvl(rad_qty,0),2) where rad_raid="+store.get("ra_id");
		baseDao.execute(sql);
		//记录操作
		baseDao.logger.update(caller, "ra_id", store.get("ra_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gstore});
	}
	@Override
	public void printRenewApply(int ra_id, String caller) {
		//执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[]{ra_id});
		//执行打印操作
		//TODO
		//记录操作
		baseDao.logger.print(caller, "ra_id", ra_id);
		//执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[]{ra_id});
	}
	@Override
	public void auditRenewApply(int ra_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("RenewApply", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ra_id);		
		baseDao.execute("update prodiodetail set pd_vendorreplydate=(select RAD_NEWRETURNDATE from renewapplydetail where rad_raid="+ra_id+" and RAD_PDID=PD_ID)where pd_id in(select RAD_PDID from renewapplydetail where rad_raid="+ra_id+")");		
		//执行审核操作
		baseDao.audit("RenewApply", "ra_id=" + ra_id, "ra_status", "ra_statuscode", "ra_auditdate", "ra_auditman");
		//记录操作
		baseDao.logger.audit(caller, "ra_id", ra_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, ra_id);
	}
	@Override
	public void resAuditRenewApply(int ra_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("RenewApply", "ra_statuscode", "ra_id=" + ra_id);
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		//执行反审核操作
		baseDao.resAudit("RenewApply", "ra_id=" + ra_id, "ra_status", "ra_statuscode", "ra_auditdate", "ra_auditman");
		//记录操作
		baseDao.logger.resAudit(caller, "ra_id", ra_id);
	}
	
	final static String SALE_PRICE_PC = "select spd_price,spd_taxrate from (select spd_price,spd_taxrate,spd_remark,spd_ratio from SalePriceDetail left join SalePrice on spd_spid=sp_id"
			+ " where spd_prodcode=? and spd_currency=? and to_char(sp_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and nvl(spd_lapqty,0)<=? and"
			+ " to_char(sp_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND spd_statuscode='VALID' and sp_statuscode='AUDITED' ORDER BY SalePrice.sp_indate DESC) where rownum<2";
	@Override
	public void submitRenewApply(int ra_id, String caller) {
		StringBuffer error = new StringBuffer();
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("RenewApply", "ra_statuscode", "ra_id=" + ra_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(rad_detno) from RenewApplyDetail left join RenewApply on rad_raid=ra_id left join BorrowCargoType on bt_name=ra_outtype "
								+ "where rad_raid=? and nvl(rad_whcode,' ')<>' ' and nvl(bt_whcodes,' ')<>' ' and rad_whcode not in (select column_value from table(parsestring(bt_whcodes,'#')))", String.class, ra_id);
		if (dets != null) {
			BaseUtil.showError("明细仓库与借货类型允许入仓库不一致，不允许进行当前操作!行号：" + dets);
		}
		//执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[]{ra_id});
		//执行提交操作
		baseDao.updateByCondition("RenewApply", "ra_statuscode='COMMITED',ra_status='" + 
				BaseUtil.getLocalMessage("COMMITED") + "'", "ra_id=" + ra_id);
		//记录操作
		baseDao.logger.submit(caller, "ra_id", ra_id);
		//执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[]{ra_id});
		if (error.length() > 0) {
			BaseUtil.appendError(error.toString());
		}
	}
	@Override
	public void resSubmitRenewApply(int ra_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("RenewApply", "ra_statuscode", "ra_id=" + ra_id);
		if(!status.equals("COMMITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		handlerService.handler("RenewApply", "resCommit", "before", new Object[]{ra_id});
		//执行反提交操作
		baseDao.updateByCondition("RenewApply", "ra_statuscode='ENTERING',ra_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "ra_id=" + ra_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "ra_id", ra_id);
		handlerService.handler("RenewApply", "resCommit", "after", new Object[]{ra_id});
	}
}
