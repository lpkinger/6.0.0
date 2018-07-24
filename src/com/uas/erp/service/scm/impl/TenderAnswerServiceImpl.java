package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.b2b.model.PurchaseTenderAnswer;
import com.uas.b2b.model.PurchaseTenderQuestion;
import com.uas.b2b.model.SaleTenderQuestion;
import com.uas.b2b.model.TenderAttach;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Master;
import com.uas.erp.service.scm.TenderAnswerService;
import com.uas.erp.service.common.B2BAttachsService;

@Service("tenderAnswerService")
public class TenderAnswerServiceImpl implements TenderAnswerService{

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private B2BAttachsService B2BAttachsService;
	
	
	@Override
	public Map<String, Object> getTenderQuestionList(String page,String limit, String search, String date,String status) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		try {
			Master master = SystemSession.getUser().getCurrentMaster();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("count", limit);
			params.put("page", page);
			String searchFilter = "";
			if (search!=null&&!"".equals(search.trim())) {
				searchFilter += ",\"keyword\":\""+search+"\"";
			}
			if (date!=null&&!"".equals(date.trim())) {
				searchFilter += ","+date;
			}
			if (searchFilter.length()>0) {
				params.put("searchFilter", "{"+searchFilter.substring(1)+"}");
			}
			if (!"all".equals(status)) {
				params.put("_state", status);
			}
			
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/tender/question?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					JSONObject obj = JSONObject.fromObject(data);
					result.put("total", obj.getInt("totalElement"));
					result.put("content", FlexJsonUtil.fromJsonArray(obj.getString("content"), SaleTenderQuestion.class));
				}
			}else {
				throw new Exception("连接平台失败！"+response.getStatusCode());
			}   
		} catch (Exception e) {
			BaseUtil.showError("错误："+e.getMessage());
		}
		return result;
	}
	
	@Override
	public Map<String, Object> getTenderQuestion(String id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		HashMap<String, Object> result = new HashMap<String, Object>();
		try {
			params.put("id", id);
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/tender/question/detail?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					SaleTenderQuestion saleTenderQuestion = FlexJsonUtil.fromJson(data, SaleTenderQuestion.class);
					saleTenderQuestion.setAttachs(B2BAttachsService.getAttaches(saleTenderQuestion.getQuestionAttaches()));
					result.put("tenderQuestion", saleTenderQuestion);
				}
			}else {
				throw new Exception("连接平台失败！"+response.getStatusCode());
			}  
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return result;
	}
	
	@Override
	public Map<String, Object> getQuestionsByTender(String tenderCode) {
		Object tenderId = baseDao.getFieldDataByCondition("Tender", "id", "code = '"+tenderCode+"'");
		if(tenderId==null){
			BaseUtil.showError("此招标单不存在！");
		}
		
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		HashMap<String, Object> result = new HashMap<String, Object>();
		try {
			params.put("tenderId",String.valueOf(tenderId));
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/tender/question/this?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					PurchaseTenderAnswer purchaseTenderAnswer = FlexJsonUtil.fromJson(data, PurchaseTenderAnswer.class);
					purchaseTenderAnswer.setAttachs(B2BAttachsService.getAttaches(purchaseTenderAnswer.getAnswerAttaches()));
					List<Map<String, Object>> questions = new ArrayList<Map<String,Object>>();
					int count = 0;
					for (PurchaseTenderQuestion pQuestion : purchaseTenderAnswer.getPurchaseTenderQuestions()) {
						Map<String, Object> question = new HashMap<String, Object>();
						question.put("id", pQuestion.getId());
						question.put("tsid", purchaseTenderAnswer.getId());
						question.put("detno", ++count);
						question.put("code", pQuestion.getCode());
						question.put("venduu", pQuestion.getVendUU());
						question.put("vendname", pQuestion.getVendor().getEnName());
						question.put("topic", pQuestion.getTopic());
						question.put("indate", (pQuestion.getInDate()==null?"":DateUtil.format(pQuestion.getInDate(), Constant.YMD_HMS)));
						List<TenderAttach> attachs = new ArrayList<TenderAttach>(pQuestion.getQuestionAttaches());
						if (attachs.size()>0) {
							question.put("attachs", attachs.get(0).getName()+";"+B2BAttachsService.getAttaches(pQuestion.getQuestionAttaches()));
						}else{
							question.put("attachs",null);
						}
						question.put("content", pQuestion.getContent());
						questions.add(question);
					}
					result.put("tenderAnswer", purchaseTenderAnswer);
					result.put("tenderQuestions", questions);
				}else {
					BaseUtil.showError("此招标单没有供应商提问！");
				}
			}else {
				throw new Exception("连接平台失败！"+response.getStatusCode());
			}  
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return result;
	}
	
	@Override
	public void saveTenderAnswer(String caller, String formStore, String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		if (StringUtil.hasText(store.get("questionenddate"))) {
			Date questionenddate = DateUtil.parse(store.get("questionenddate").toString(), Constant.YMD_HMS);
			if (!questionenddate.before(new Date())) {
				BaseUtil.showError("未过提问截止时间，请继续等待！");
			}
		}
		List<Map<Object, Object>> gridStore = BaseUtil.parseGridStoreToMaps(param);
		
		if (!StringUtil.hasText(store.get("code"))) {
			store.put("code", baseDao.sGetMaxNumber("TenderAnswer", 2));
		}
		
		// 删除
		baseDao.deleteById("TenderAnswer", "id", Long.parseLong(store.get("id").toString()));
		baseDao.deleteByCondition("TenderQuestion", "tsid = ?", store.get("id"));
		
		handlerService.beforeSave(caller, new Object[] {store});
		
		try {
			List<String> sqls = new ArrayList<String>();
			sqls.add(SqlUtil.getInsertSqlByFormStore(store, "TenderAnswer", new String []{}, new Object[]{}));
			sqls.addAll(SqlUtil.getInsertSqlbyGridStore(gridStore, "TenderQuestion"));
			// 执行保存操作
			baseDao.execute(sqls);
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误："+e.getMessage());
		}
		
		// 记录操作
		baseDao.logger.save(caller, "id", store.get("id"));
		
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store});
	}
	
	@Override
	public void updateTenderAnswer(String caller, String formStore,String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		handlerService.beforeUpdate(caller, new Object[] {store});
		
		try {
			// 执行保存操作
			baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "TenderAnswer", "id"));
		} catch (Exception e) {
			BaseUtil.showError("更新失败，错误："+e.getMessage());
		}
		
		// 记录操作
		baseDao.logger.update(caller, "id", store.get("id"));
		
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] {store});
		
	}

	@Override
	public void deleteTenderAnswer(String caller, int id) {
		Object status = baseDao.getFieldDataByCondition("TenderAnswer","auditstatuscode", "id=" + id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { id });
		// 删除
		baseDao.deleteById("TenderAnswer", "id", id);
		baseDao.deleteByCondition("TenderQuestion", "tsid = ?", id);
		// 记录操作
		baseDao.logger.delete(caller, "id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { id });
		
	}

	@Override
	public void submitTenderAnswer(int id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("TenderAnswer","auditstatuscode,tendercode", "id=" + id);
		StateAssert.submitOnlyEntering(status[0]);
		
		Date questionenddate = baseDao.queryForObject("select questionenddate from Tender where code = ?", Date.class, status[1]);
		if (questionenddate!=null) {
			if (!questionenddate.before(new Date())) {
				BaseUtil.showError("未过提问截止时间，请继续等待！");
			}
		}
		
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { id });
		
		// 执行提交操作
		baseDao.submit("TenderAnswer", "id=" + id, "auditstatus", "auditstatuscode");
		
		// 记录操作
		baseDao.logger.submit(caller, "id", id);
		
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { id });
		
	}

	@Override
	public void resSubmitTenderAnswer(int id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("TenderAnswer","auditstatuscode", "id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, id);
		// 执行反提交操作
		baseDao.resOperate("TenderAnswer", "id="+id, "auditstatus", "auditstatuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "id", id);
		//执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, id);
	}
	
	@Override
	public void auditTenderAnswer(int id, String caller) {
		Object[] status = baseDao.getFieldsDataByCondition("TenderAnswer","auditstatuscode,tendercode", "id=" + id);
		StateAssert.auditOnlyCommited(status[0]);
		
		Date questionenddate = baseDao.queryForObject("select questionenddate from Tender where code = ?", Date.class, status[1]);
		if (questionenddate!=null) {
			if (!questionenddate.before(new Date())) {
				BaseUtil.showError("未过提问截止时间，请继续等待！");
			}
		}
		
		// 执行投标前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { id });
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			PurchaseTenderAnswer purchaseTenderAnswer = baseDao.queryBean("select * from TenderAnswer where id = ?", PurchaseTenderAnswer.class, id);
			Map<String,Object> answer = new HashMap<String, Object>();
			answer.put("id", purchaseTenderAnswer.getId());
			answer.put("code", purchaseTenderAnswer.getCode());
			answer.put("enUU", master.getMa_uu());
			answer.put("tenderCode", purchaseTenderAnswer.getTendercode());
			answer.put("tenderTitle", purchaseTenderAnswer.getTendertitle());
			answer.put("questionEndDate", purchaseTenderAnswer.getQuestionEndDate());
			answer.put("inDate", purchaseTenderAnswer.getInDate());
			answer.put("recorder", purchaseTenderAnswer.getRecorder());
			answer.put("auditDate", new Date());
			answer.put("replyDate", new Date());
			answer.put("remark", purchaseTenderAnswer.getRemark());
			answer.put("status", 201);
			answer.put("answerAttaches", B2BAttachsService.parseAttachs(purchaseTenderAnswer.getAttachs()));
			params.put("data", FlexJsonUtil.toJsonDeep(answer));
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/tender/question/answer/save?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				// 执行审核操作
				baseDao.audit("TenderAnswer", "id="+id, "auditstatus", "auditstatuscode", "auditdate", "auditman");
				// 记录操作
				baseDao.logger.audit(caller, "id", id);
				// 执行审核后的其它逻辑
				handlerService.afterAudit(caller,new Object[] { id });
				//更新回复状态
				baseDao.updateByCondition("TenderAnswer", "replydate=sysdate,status=201", "id="+id);
				
			}else {
				throw new Exception("连接平台失败！"+response.getStatusCode());
			} 
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("答疑失败，错误："+e.getMessage());
		}
	}

	@Override
	public void resAuditTenderAnswer(int id, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("TenderAnswer","auditstatuscode", "id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		
		//执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[]{id});
		
		baseDao.resAuditCheck("TenderAnswer", "id");
		
		baseDao.resAudit("TenderAnswer", "id = "+id, "auditstatus", "auditstatuscode", "auditman", "auditdate");
		
		//更新回复状态
		baseDao.updateByCondition("TenderAnswer", "replydate=null,status=200", "id="+id);
		
		// 记录操作
		baseDao.logger.resAudit(caller, "id", id);
		//执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[]{id});
	}

}
