package com.uas.erp.service.wisdomPark.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.wisdomPark.AgreementService;

@Service("agreementService")
public class AgreementServiceImpl implements AgreementService{
	
	@Autowired BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	
	private void setStatus(Map<Object, Object> store){
		
		if(!StringUtil.hasText(store.get("ag_status"))||"垃圾箱".equals(store.get("ag_status"))){
			store.put("ag_status", "草稿箱");
		}
		
		store.put("ag_update", DateUtil.format(new Date(), Constant.YMD_HMS));
	}
	
	//保存服务协议
	public void saveAgreement(String caller, String formStore){	
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		boolean bool = baseDao.checkIf("Agreement", "ag_type = '" + store.get("ag_type")+"' and nvl(ag_status,'草稿箱') = '已发布'");
		if (bool) {
			BaseUtil.showError(store.get("ag_type")+"已存在，不能新增！");
		}
		
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		
		if(!StringUtil.hasText(store.get("ag_id"))){
			store.put("ag_id", baseDao.getSeqId("AGREEMENT_SEQ"));
		}
		
		setStatus(store);
		
		//处理超长字符
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		Object value = null;
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		// 保存ag_id
		String formSql = SqlUtil.getInsertSqlByMap(store, "Agreement");
		baseDao.execute(formSql);
		baseDao.saveClob("Agreement", clobFields, clobStrs, "ag_id=" + store.get("ag_id"));
		// 记录操作
		baseDao.logger.save(caller, "ag_id", store.get("ag_id"));
		
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}
	
	//更新服务协议
	public void updateAgreement(String caller, String formStore){	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		boolean bool = baseDao.checkIf("Agreement", "ag_type = '" + store.get("ag_type") + "' and nvl(ag_status,'草稿箱') = '已发布' and ag_id <> " + store.get("ag_id"));
		if (bool) {
			BaseUtil.showError(store.get("ag_type")+"已存在！");
		}
		
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		
		setStatus(store);
		
		//处理超长字符
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		Object value = null;
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		
		// 修改Agreement
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Agreement", "ag_id");
		baseDao.execute(formSql);
		baseDao.saveClob("Agreement", clobFields, clobStrs, "ag_id=" + store.get("ag_id"));
		// 记录操作
		baseDao.logger.update(caller, "ag_id", store.get("ag_id"));
		
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}
	
	//删除服务协议
	public void deleteAgreement(String caller, int id){
		
		// 只能删除未发布的服务协议!
		boolean bool = baseDao.checkIf("Agreement", "ag_id = " + id +" and nvl(ag_status,'草稿箱') = '已发布'");
		if (bool) {
			BaseUtil.showError("服务协议已发布，不能删除！");
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, id);
		
		// 删除Agreement
		baseDao.deleteById("Agreement", "ag_id", id);
		
		// 记录操作
		baseDao.logger.delete(caller, "ag_id", id);
		
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, id);
	}
	
	//发布服务协议
	public void publishAgreement(String caller, int id){	
		
		boolean bool = baseDao.checkIf("Agreement", "ag_id = " + id +" and nvl(ag_status,'草稿箱') = '已发布'");
		if (bool) {
			BaseUtil.showError("服务协议已发布，不用重复发布！");
		}
		
		Employee employee = SystemSession.getUser();
		baseDao.updateByCondition("Agreement", "ag_status = '已发布',ag_publisher = '"+employee.getEm_name()+"',ag_publishdate = "+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "ag_id = " + id);
		
		//记录日志
		baseDao.logger.others("发布服务协议", "发布成功", caller, "ag_id", id);
	}
	
	@Override
	public void cancelAgreement(String caller, int id) {
		boolean bool = baseDao.checkIf("Agreement", "ag_id = " + id +" and nvl(ag_status,'草稿箱') <> '已发布'");
		if (bool) {
			BaseUtil.showError("服务协议未发布，不用重复撤销！");
		}
		
		baseDao.updateByCondition("Agreement", "ag_status = '垃圾箱',ag_publisher = '',ag_publishdate = ''", "ag_id = " + id);
		
		//记录日志
		baseDao.logger.others("取消服务协议", "取消成功", caller, "ag_id", id);
		
	}

}
