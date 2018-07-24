package com.uas.erp.service.plm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.PurchaseChangeDao;
import com.uas.erp.service.plm.PurchaseChangePLMService;

@Service("purchaseChangePLMService")
public class PurchaseChangePLMServiceImpl implements PurchaseChangePLMService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PurchaseChangeDao purchaseChangeDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void savePurchaseChange(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PurchaseChange", "pc_code='" + store.get("pc_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, grid});
		//保存PurchaseChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PurchaseChange", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存PurchaseChangeDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "PurchaseChangeDetail", "pcd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "pc_id", store.get("pc_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{formStore});
	}
	@Override
	public void deletePurchaseChange(int pc_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PurchaseChange", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{pc_id});
		//删除PurchaseChange
		baseDao.deleteById("PurchaseChange", "pc_id", pc_id);
		//删除PurchaseChangeDetail
		baseDao.deleteById("PurchaseChangedetail", "pcd_pcid", pc_id);
		//记录操作
		baseDao.logger.delete(caller, "pc_id", pc_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{pc_id});
	}
	
	@Override
	public void updatePurchaseChangeById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PurchaseChange", "pc_statuscode", "pc_id=" + store.get("pc_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, gstore});
		//修改Inquiry
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PurchaseChange", "pc_id");
		baseDao.execute(formSql);
		//修改InquiryDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PurchaseChangeDetail", "pcd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pcd_id") == null || s.get("pcd_id").equals("") || s.get("pcd_id").equals("0") ||
					Integer.parseInt(s.get("pcd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PURCHASECHANGEDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PurchaseChangeDetail", new String[]{"pcd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "pc_id", store.get("pc_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gstore});
	}
	@Override
	public void printPurchaseChange(int pc_id, String caller) {
		//执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[]{pc_id});
		//执行打印操作
		//TODO
		//记录操作
		baseDao.logger.print(caller, "pc_id", pc_id);
		//执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[]{pc_id});
	}
	@Override
	public void auditPurchaseChange(int pc_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseChange", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{pc_id});
		//执行审核操作
		baseDao.audit("PurchaseChange", "pc_id=" + pc_id, "pc_status", "pc_statuscode", "pc_auditdate", "pc_auditman");
		//信息自动反馈到采购单
		String pucode = purchaseChangeDao.turnPurchase(pc_id);
		//记录操作
		baseDao.logger.audit(caller, "pc_id", pc_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { pc_id });
		BaseUtil.showError("AFTERSUCCESS信息已自动反馈到采购单&nbsp;&nbsp;" + 
					"<a href=\"javascript:openUrl('jsps/scm/purchase/purchase.jsp?formCondition=pu_codeIS" + pucode + "&gridCondition=pd_codeIS" + pucode + "')\">点击查看</a>&nbsp;");
	}
	@Override
	public void resAuditPurchaseChange(int pc_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseChange", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("PurchaseChange", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pc_id", pc_id);
	}
	@Override
	public void submitPurchaseChange(int pc_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseChange", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.submitOnlyEntering(status);
		//只能选择已审核的供应商!
		Object code = baseDao.getFieldDataByCondition("PurchaseChange", "pc_vendcode", "pc_id=" + pc_id);
		status = baseDao.getFieldDataByCondition("Vendor", "ve_auditstatuscode", "ve_code='" + code + "'");
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("vendor_onlyAudited") + 
				"<a href=\"javascript:openUrl('jsps/scm/purchase/vendor.jsp?formCondition=ve_codeIS" + code +  "')\">" + code + "</a>&nbsp;");
		}
		//只能选择已审核的物料!
		List<Object> codes = baseDao.getFieldDatasByCondition("PurchaseChangeDetail", "pcd_newprodcode", "pcd_pcid=" + pc_id);
		for(Object c:codes){
			status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_code='" + c + "'");
			if(!status.equals("AUDITED")){
				BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited") + 
					"<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS" + c +  "')\">" + c + "</a>&nbsp;");
			}
		}
		//执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pc_id });
		// 执行提交操作
		baseDao.submit("PurchaseChange", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pc_id", pc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pc_id });
	}
	@Override
	public void resSubmitPurchaseChange(int pc_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseChange", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { pc_id });
		// 执行反提交操作
		baseDao.resOperate("PurchaseChange", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pc_id", pc_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pc_id });
	}
	@Override
	public String turnPurchase(int pc_id, String caller) {
		String pucode = null;
		//转采购
		pucode = purchaseChangeDao.turnPurchase(pc_id);
		//记录操作
		baseDao.logger.turn("msg.turnPurchase", caller, "pc_id", pc_id);
		//删除PurchaseChange
		baseDao.deleteById("PurchaseChange", "pc_id", pc_id);
		//删除PurchaseChangeDetail
		baseDao.deleteById("PurchaseChangedetail", "pcd_pcid", pc_id);
		return pucode;
	}
}
