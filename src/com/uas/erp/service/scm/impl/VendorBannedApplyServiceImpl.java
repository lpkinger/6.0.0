package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.service.scm.VendorBannedApplyService;
import com.uas.erp.service.scm.VendorService;
import com.uas.erp.service.scm.CustomerBaseService;


@Service("vendorBannedApplyService")
public class VendorBannedApplyServiceImpl implements VendorBannedApplyService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private VendorService vendorService;
	@Autowired
	private CustomerBaseService customerBaseService;
	@Override
	public void saveVendorBannedApply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("VendorBannedApply", "vba_code='" + store.get("vba_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VendorBannedApply", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "vba_id", store.get("vba_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	
	@Override
	public void deleteVendorBannedApply(int vba_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("VendorBannedApply", "vba_statuscode", "vba_id=" + vba_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, vba_id);
		baseDao.delCheck("VendorBannedApply", vba_id);
		//删除payments
		baseDao.deleteById("VendorBannedApply", "vba_id", vba_id);		
		//记录操作
		baseDao.logger.delete(caller, "vba_id", vba_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, vba_id);
	}
	
	@Override
	public void updateVendorBannedApplyById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("VendorBannedApply", "vba_statuscode", "vba_id=" + store.get("vba_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VendorBannedApply", "vba_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "vba_id", store.get("vba_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	
	@Override
	public void auditVendorBannedApply(int vba_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("VendorBannedApply", new String[]{"vba_statuscode", "vba_vendcode"}, "vba_id=" + vba_id);
		StateAssert.auditOnlyCommited(status[0]);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, vba_id);
		if("VendorBannedApply".equals(caller) || "VendorResBannedApply".equals(caller)){
			Object veid = baseDao.getFieldDataByCondition("Vendor", "ve_id", "ve_code='" + status[1] + "'");
			if(veid != null && "VendorBannedApply".equals(caller)){
				vendorService.bannedVendor(Integer.parseInt(veid.toString()), "Vendor");
			} else if(veid != null && "VendorResBannedApply".equals(caller)){
				vendorService.resBannedVendor(Integer.parseInt(veid.toString()), "Vendor");
			} else {
				BaseUtil.showError("供应商[" + status[1] +"]不存在！");
			}
		}
		if("CustBannedApply".equals(caller) || "CustResBannedApply".equals(caller)){
			Object cuid = baseDao.getFieldDataByCondition("Customer", "cu_id", "cu_code='" + status[1] + "'");
			if(cuid != null && "CustBannedApply".equals(caller)){
				customerBaseService.bannedCustomer(Integer.parseInt(cuid.toString()), "Customer!Base");
			} else if(cuid != null && "CustResBannedApply".equals(caller)){
				customerBaseService.resBannedCustomer(Integer.parseInt(cuid.toString()), "Customer!Base");
			} else {
				BaseUtil.showError("客户[" + status[1] +"]不存在！");
			}
		}
		//执行审核操作
		baseDao.audit("VendorBannedApply", "vba_id=" + vba_id, "vba_status", "vba_statuscode", "vba_auditdate", "vba_auditman");
		//记录操作
		baseDao.logger.audit(caller, "vba_id", vba_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, vba_id);
	}
	
	@Override
	public void resAuditVendorBannedApply(int vba_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("VendorBannedApply", "vba_statuscode", "vba_id=" + vba_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("VendorBannedApply", vba_id);
		//执行反审核操作
		baseDao.resAudit("VendorBannedApply", "vba_id=" + vba_id, "vba_status", "vba_statuscode", "vba_auditdate", "vba_auditman");
		//记录操作
		baseDao.logger.resAudit(caller, "vba_id", vba_id);
	}
	
	@Override
	public void submitVendorBannedApply(int vba_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("VendorBannedApply", "vba_statuscode", "vba_id=" + vba_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, vba_id);
		//执行提交操作
		baseDao.submit("VendorBannedApply", "vba_id=" + vba_id, "vba_status", "vba_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "vba_id", vba_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, vba_id);
	}
	
	@Override
	public void resSubmitVendorBannedApply(int vba_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("VendorBannedApply", "vba_statuscode", "vba_id=" + vba_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeSubmit(caller, vba_id);
		//执行反提交操作
		baseDao.resOperate("VendorBannedApply", "vba_id=" + vba_id, "vba_status", "vba_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "vba_id", vba_id);
		handlerService.afterResSubmit(caller, vba_id);
	}
}
