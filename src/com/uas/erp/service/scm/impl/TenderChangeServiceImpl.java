package com.uas.erp.service.scm.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.scm.TenderChangeService;
@Service
public class TenderChangeServiceImpl implements TenderChangeService{

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveTenderChange(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		SqlRowList tender = baseDao.queryForRowSet("select questionEndDate,publishDate from Tender where code = ?", store.get("tc_ttcode"));
		if(tender.next()){
			if (tender.getDate("questionEndDate")!=null&&DateUtil.overDate(tender.getDate("questionEndDate"), -1).after(DateUtil.parse(store.get("tc_newendtime").toString(), Constant.YMD))) {
				BaseUtil.showError("投标截止时间必须大于提问截止时间("+DateUtil.format(tender.getDate("questionEndDate"), Constant.YMD_HMS)+")！");
			}else if (DateUtil.compare(store.get("tc_newendtime").toString(), DateUtil.format(tender.getDate("publishDate"), Constant.YMD))>=0) {
				BaseUtil.showError("投标截止时间必须小于公布结果时间("+DateUtil.format(tender.getDate("publishDate"), Constant.YMD)+")！");
			}
		}
		
		store.put("tc_code", baseDao.sGetMaxNumber("TenderChange", 2));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		try {
			String formSql = SqlUtil.getInsertSqlByFormStore(store, "TenderChange",new String[] {}, new Object[] {});
			baseDao.execute(formSql);
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误："+e.getMessage());
		}
		// 记录操作
		baseDao.logger.save(caller, "tc_id", store.get("tc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateTenderChangeById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改状态为[在录入]的单据!
		Object status = baseDao.getFieldDataByCondition("TenderChange","tc_statuscode", "tc_id=" + store.get("tc_id"));
		StateAssert.updateOnlyEntering(status);
		SqlRowList tender = baseDao.queryForRowSet("select questionEndDate,publishDate from Tender where code = ?", store.get("tc_ttcode"));
		if(tender.next()){
			if (tender.getDate("questionEndDate")!=null&&DateUtil.overDate(tender.getDate("questionEndDate"), -1).after(DateUtil.parse(store.get("tc_newendtime").toString(), Constant.YMD))) {
				BaseUtil.showError("投标截止时间必须大于提问截止时间("+DateUtil.format(tender.getDate("questionEndDate"), Constant.YMD_HMS)+")！");
			}else if (DateUtil.compare(store.get("tc_newendtime").toString(), DateUtil.format(tender.getDate("publishDate"), Constant.YMD))>=0) {
				BaseUtil.showError("投标截止时间必须小于公布结果时间("+DateUtil.format(tender.getDate("publishDate"), Constant.YMD)+")！");
			}
		}
		
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		try {
			String formSql = SqlUtil.getUpdateSqlByFormStore(store, "TenderChange", "tc_id");
			baseDao.execute(formSql);
		} catch (Exception e) {
			BaseUtil.showError("更新失败，错误："+e.getMessage());
		}
		
		// 记录操作
		baseDao.logger.update(caller, "tc_id", store.get("tc_id"));
		
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteTenderChange(int tc_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("TenderChange","tc_statuscode", "tc_id=" + tc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { tc_id });
		// 删除
		baseDao.deleteById("TenderChange", "tc_id", tc_id);
		// 记录操作
		baseDao.logger.delete(caller, "tc_id", tc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { tc_id });
	}

	@Override
	public void auditTenderChange(int tc_id, String caller) {
		Object[] status = baseDao.getFieldsDataByCondition("TenderChange","tc_statuscode,tc_ttcode,tc_type,tc_newendtime", "tc_id=" + tc_id);
		StateAssert.auditOnlyCommited(status[0]);
		SqlRowList tender = baseDao.queryForRowSet("select questionEndDate,publishDate,id,tt_statuscode from Tender where code = ?", status[1]);
		if(tender.next()){
			if (tender.getDate("questionEndDate")!=null&&DateUtil.overDate(tender.getDate("questionEndDate"), -1).after(DateUtil.parse(status[3].toString(), Constant.YMD))) {
				BaseUtil.showError("投标截止时间必须大于提问截止时间("+DateUtil.format(tender.getDate("questionEndDate"), Constant.YMD_HMS)+")！");
			}else if (DateUtil.compare(status[3].toString(), DateUtil.format(tender.getDate("publishDate"), Constant.YMD))>=0) {
				BaseUtil.showError("投标截止时间必须小于公布结果时间("+DateUtil.format(tender.getDate("publishDate"), Constant.YMD)+")！");
			}
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { tc_id });
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			String id = tender.getString("id");
			params.put("id",id);
			params.put("date",String.valueOf(DateUtil.parse(status[3].toString(),Constant.YMD).getTime()));
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/tender/decide/inAdvance?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				//审核
				baseDao.audit("TenderChange", "tc_id=" + tc_id, "tc_status", "tc_statuscode","tc_auditdate", "tc_auditman");
				//记录操作
				baseDao.logger.audit(caller, "tc_id", tc_id);
				
				baseDao.updateByCondition("Tender", "endDate = "+DateUtil.parseDateToOracleString(Constant.YMD_HMS, status[3].toString()), "code = '"+status[1]+"'");
				// 记录操作
				if (tender.getString("tt_statuscode")!=null&&"AUDITED".equals(tender.getString("tt_statuscode"))) {
					baseDao.logger.others(status[2].toString()+"("+status[3].toString().substring(0, 10)+")", "开标成功", "TenderEstimate", "id", id);
				}else{
					baseDao.logger.others(status[2].toString()+"("+status[3].toString().substring(0, 10)+")", "开标成功", "Tender", "id", id);
				}
				// 执行审核后的其它逻辑
				handlerService.afterAudit(caller, new Object[] { tc_id });
			}else {
				throw new Exception("连接平台失败！"+response.getStatusCode());
			} 
		} catch (Exception e) {
			BaseUtil.showError("开标失败，错误："+e.getMessage());
		}
		
	}

	@Override
	public void submitTenderChange(int tc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("TenderChange","tc_statuscode,tc_ttcode,tc_newendtime", "tc_id=" + tc_id);
		StateAssert.submitOnlyEntering(status[0]);
		SqlRowList tender = baseDao.queryForRowSet("select questionEndDate,publishDate from Tender where code = ?", status[1]);
		if(tender.next()){
			if (tender.getDate("questionEndDate")!=null&&DateUtil.overDate(tender.getDate("questionEndDate"), -1).after(DateUtil.parse(status[2].toString(), Constant.YMD))) {
				BaseUtil.showError("投标截止时间必须大于提问截止时间("+DateUtil.format(tender.getDate("questionEndDate"), Constant.YMD_HMS)+")！");
			}else if (DateUtil.compare(status[2].toString(), DateUtil.format(tender.getDate("publishDate"), Constant.YMD))>=0) {
				BaseUtil.showError("投标截止时间必须小于公布结果时间("+DateUtil.format(tender.getDate("publishDate"), Constant.YMD)+")！");
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { tc_id });
		// 执行提交操作
		baseDao.submit("TenderChange", "tc_id=" + tc_id, "tc_status", "tc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "tc_id", tc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { tc_id });
	}
	
	@Override
	public void resSubmitTenderChange(int tc_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("TenderChange","tc_statuscode", "tc_id=" + tc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { tc_id });
		// 执行反提交操作
		baseDao.resOperate("TenderChange", "tc_id=" + tc_id, "tc_status","tc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "tc_id", tc_id);
		handlerService.afterResSubmit(caller, new Object[] { tc_id });
	}

}
