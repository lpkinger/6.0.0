package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.PackingService;

@Service("packingService")
public class PackingServiceImpl implements PackingService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void savePacking(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Packing", "pi_code='" + store.get("pi_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, grid});
		//保存Packing
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Packing", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		Double total = 0.0;
		//保存PackingDetail
		for(Map<Object, Object> s:grid){
			s.put("pd_id", baseDao.getSeqId("PACKINGDETAIL_SEQ"));
			s.put("pd_code", store.get("pi_code"));
			Object qty = s.get("pd_qty");
			Object price = s.get("pd_price");
			total = NumberUtil.formatDouble(Double.parseDouble(qty.toString()) * Double.parseDouble(price.toString()), 2);
			if(qty != null && price != null){
				s.put("pd_total", total);
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "PackingDetail");
		baseDao.execute(gridSql);
		Object pi_id = store.get("pi_id");
		baseDao.execute("update Packing set pi_total=(select sum(pd_total) from PackingDetail where pd_piid="
				+ pi_id +") where pi_id="+ pi_id);
		baseDao.execute("update Packing set pi_totalupper=L2U(pi_total),pi_totalupperenhkd=L2U(pi_total/nvl(pi_rate,1)) where pi_id=" + pi_id);
		baseDao.execute("update packingdetail set pd_cartonno=replace(ltrim(rtrim(pd_cartonno)),'－','-') where pd_piid=?", store.get("pi_id"));
		baseDao.execute("update packingdetail set pd_cartons=1 + to_number(substr(pd_cartonno,instr(pd_cartonno,'-')+1))-to_number(substr(pd_cartonno,1,instr(pd_cartonno,'-')-1)) where pd_piid=? and instr(nvl(pd_cartonno,' '),'-')>0 and is_number(substr(pd_cartonno,instr(pd_cartonno,'-')+1))=1 and is_number(substr(pd_cartonno,1,instr(pd_cartonno,'-')-1))=1", store.get("pi_id"));
		baseDao.logger.save(caller, "pi_id", store.get("pi_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, grid});
	}
	
	@Override
	public void deletePacking(int pi_id, String caller) {
		//只能删除在录入的单据!
		Object[] status = baseDao.getFieldsDataByCondition("Packing", new String[]{"pi_statuscode","pi_code"}, "pi_id=" + pi_id);
		StateAssert.delOnlyEntering(status[0]);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, pi_id);
		Object inid = baseDao.getFieldDataByCondition("Invoice", "in_id", "in_code='" + status[1] + "'");
		int in_id = inid == null ? -1 : Integer.parseInt(inid.toString());
		baseDao.execute("update prodinout set pi_packingcode=null,pi_invoicecode=null where" +
				" pi_packingcode='" + status[1] + "' and pi_class in ('出货单','销售退货单','拨出单')");
		//删除Packing
		baseDao.deleteById("Packing", "pi_id", pi_id);
		baseDao.deleteById("Invoice", "in_id", in_id);
		//删除PackingDetail
		baseDao.deleteById("packingdetail", "pd_piid", pi_id);
		baseDao.deleteById("Invoicedetail", "id_inid", in_id);
		//记录操作
		baseDao.logger.delete(caller, "pi_id", pi_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, pi_id);
	}
	
	@Override
	public void updatePackingById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Packing", "pi_statuscode", "pi_id=" + store.get("pi_id"));
		StateAssert.updateOnlyEntering(status);	
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		//修改Packing
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Packing", "pi_id");
		baseDao.execute(formSql);
		//修改PackingDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PackingDetail", "pd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pd_id") == null || s.get("pd_id").equals("") || s.get("pd_id").equals("0") ||
					Integer.parseInt(s.get("pd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PACKINGDETAIL_SEQ");
				s.put("pd_code", store.get("pi_code"));
				String sql = SqlUtil.getInsertSqlByMap(s, "PackingDetail", new String[]{"pd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update packingdetail set pd_cartonno=replace(ltrim(rtrim(pd_cartonno)),'－','-') where pd_piid=?", store.get("pi_id"));
		baseDao.execute("update packingdetail set pd_cartons=1 + to_number(substr(pd_cartonno,instr(pd_cartonno,'-')+1))-to_number(substr(pd_cartonno,1,instr(pd_cartonno,'-')-1)) where pd_piid=? and instr(nvl(pd_cartonno,' '),'-')>0 and is_number(substr(pd_cartonno,instr(pd_cartonno,'-')+1))=1 and is_number(substr(pd_cartonno,1,instr(pd_cartonno,'-')-1))=1", store.get("pi_id"));
		//记录操作
		baseDao.logger.update(caller, "pi_id", store.get("pi_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
	}
	
	@Override
	public String[] printPacking(int pi_id, String caller,String reportName,String condition) {
		//执行打印前的其它逻辑
		handlerService.beforePrint(caller, pi_id);
		//执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		//记录操作
		baseDao.logger.print(caller, "pi_id", pi_id);
		//执行打印后的其它逻辑
		handlerService.afterPrint(caller, pi_id);
		return keys;
	}
	
	@Override
	public void auditPacking(int pi_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Packing", "pi_statuscode", "pi_id=" + pi_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pi_id);
		//执行审核操作
		baseDao.audit("Packing", "pi_id=" + pi_id, "pi_status", "pi_statuscode", "pi_auditdate", "pi_auditman");
		//记录操作
		baseDao.logger.audit(caller, "pi_id", pi_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, pi_id);
	}
	
	@Override
	public void resAuditPacking(int pi_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Packing", "pi_statuscode", "pi_id=" + pi_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Packing", "pi_id=" + pi_id, "pi_status", "pi_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "pi_id", pi_id);
	}
	
	@Override
	public void submitPacking(int pi_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("Packing", new String[]{"pi_statuscode","pi_code"}, "pi_id=" + pi_id);
		StateAssert.submitOnlyEntering(status[0]);
		int isbcpacking = Integer.parseInt(baseDao.getFieldDataByCondition("Packing", "nvl(isbcpacking,0)", "pi_id=" + pi_id).toString());
		if (isbcpacking == 0){
			double pisum = baseDao.getSummaryByField("PackingDetail", "pd_qty*pd_cartons", "pd_piid="+pi_id);
			double insum = baseDao.getSummaryByField("InvoiceDetail", "id_qty", "id_inid=(select in_id from Invoice where in_code='"+status[1]+"')");
			if(pisum != insum){
				BaseUtil.showError("该单据总数量" + pisum + "跟发票单["+status[1]+"]总数量" + insum + "不相等，不允许提交!");
			}
		}
		baseDao.execute("update Packing set pi_total=(select sum(pd_total) from PackingDetail where pd_piid="
				+ pi_id +") where pi_id="+ pi_id);
		baseDao.execute("update Packing set pi_totalupper=L2U(pi_total),pi_totalupperenhkd=L2U(pi_total/nvl(pi_rate,1)) where pi_id=" + pi_id);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pi_id);
		//执行提交操作
		baseDao.submit("Packing", "pi_id=" + pi_id, "pi_status", "pi_statuscode");
		if (baseDao.isDBSetting("Invoice", "shCustUse")) {
			baseDao.updateByCondition("Packing", "pi_receiveid=pi_receivecode, pi_cop=(select en_shortname from enterprise)", "pi_id="+pi_id);
		}
		//记录操作
		baseDao.logger.submit(caller, "pi_id", pi_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pi_id);
	}
	
	@Override
	public void resSubmitPacking(int pi_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Packing", "pi_statuscode", "pi_id=" + pi_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pi_id);
		//执行反提交操作
		baseDao.resOperate("Packing", "pi_id=" + pi_id, "pi_status", "pi_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "pi_id", pi_id);
		handlerService.afterResSubmit(caller, pi_id);
	}

	@Override
	public void updateMadeIn(int pi_id) {
		baseDao.execute("update packingdetail set pd_madein=(select id_madein from invoicedetail where id_code=pd_code and id_detno=pd_detno) where pd_piid=" + pi_id);
	}
}
