package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.b2b.model.PurchaseTender;
import com.uas.b2b.model.PurchaseTenderProd;
import com.uas.b2b.model.SaleTenderErp;
import com.uas.b2b.model.SaleTenderItem;
import com.uas.b2b.model.TenderAttach;
import com.uas.b2b.model.Vendor;
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
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.B2BAttachsService;
import com.uas.erp.service.common.JProcessService;
import com.uas.erp.service.common.ProcessService;
import com.uas.erp.service.oa.SendMailService;
import com.uas.erp.service.scm.TenderService;

@Service("tenderService")
public class TenderServiceImpl implements TenderService{

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private ProcessService processService;
	
	@Autowired
	private B2BAttachsService B2BAttachsService;
	
	@Autowired
	private SendMailService sendMailService;
	
	@Autowired
	private JProcessService jprocessService;
	
	
	private void SendMsg(String id,String caller) throws Exception{
		Employee employee = SystemSession.getUser();
		Master master = employee.getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		boolean emailPrompt = baseDao.isDBSetting(caller, "emailPrompt");
		if(emailPrompt){
			sendMail(master,params);
			baseDao.logger.others("邮件发送","发送邮件成功",caller,"id",id);
		}
		boolean shortmegPrompt = baseDao.isDBSetting(caller, "shortmegPrompt");
		if (shortmegPrompt) {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/tender/message/mobile?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				baseDao.logger.others("短信发送","发送短信成功",caller,"id",id);
			}else{
				String data = response.getResponseText();
				JSONObject obj = JSONObject.fromObject(data);
				String error = obj.getString("error");
				if (StringUtil.hasText(error)) {
					BaseUtil.showErrorOnSuccess(error);
				}else{
					BaseUtil.showErrorOnSuccess("发送短信错误，连接平台失败！"+response.getStatusCode());
				}
			}
		}
	}
	
	private void sendMail(Master master,HashMap<String, String> params) throws Exception{
		Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/tender/thisVendors?access_id=" + master.getMa_uu(),
				params, true, master.getMa_accesssecret());
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			String data = response.getResponseText();
			if (StringUtil.hasText(data)) {
				JSONObject obj = JSONObject.fromObject(data);
				List<Vendor> vendors = FlexJsonUtil.fromJsonArray(obj.getString("vendors"), Vendor.class);
				PurchaseTender purchaseTender = FlexJsonUtil.fromJson(obj.getString("tender"), PurchaseTender.class);
				StringBuffer error = new StringBuffer();
				if (vendors!=null&&vendors.size()>0) {
					for (Vendor vendor : vendors) {
						String email = vendor.getVe_email();
						if (email == null || "".equals(email.toString().trim()) || "null".equals(email.toString().trim())) {
							continue;
						}
						// 标题和内容一致
						String title = "请查看客户招标信息【" + purchaseTender.getTitle()+ "】(招标编号："+purchaseTender.getCode()+")";
						String contextdetail = "<P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>"
								+ vendor.getVe_contact()
								+ "，您好！<SPAN lang=EN-US><?xml:namespace prefix = 'o' ns = 'urn:schemas-microsoft-com:office:office' /><o:p></o:p></SPAN></SPAN></P>"
								+ "<P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN lang=EN-US style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'><SPAN style='mso-spacerun: yes'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
								+ " </SPAN></SPAN><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>您有一条来自于<SPAN style='COLOR: blue'><SPAN lang=EN-US>"
								+ purchaseTender.getEnName()
								+ "</SPAN></SPAN>的新客户招标信息<SPAN lang=EN-US>(</SPAN>招标编号：<SPAN lang=EN-US style='COLOR: blue'>"
								+ purchaseTender.getCode()
								+ ")</SPAN>"
								+ "<SPAN lang=EN-US>,</SPAN>请及时登入UAS系统或优软商务平台查取您的客户招标信息<SPAN lang=EN-US>!<o:p></o:p></SPAN></SPAN></P><P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>登入平台的地址：<SPAN lang=EN-US><A href='http://www.ubtob.com/'><FONT color=#0000ff>www.ubtob.com</FONT></A>"
								+ "<o:p></o:p></SPAN></SPAN></P><P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'><SPAN style='mso-spacerun: yes'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
								+ " </SPAN>"
								+ "如对此招标信息存在疑问，请及时与"+purchaseTender.getEnName()+"此次招标的负责人员<SPAN style='COLOR: blue'>"+purchaseTender.getUser()+"</SPAN>联系，联系电话：<SPAN style='COLOR: blue'><SPAN lang=EN-US>"+purchaseTender.getUserTel()+"<o:p></o:p></SPAN></SPAN></SPAN></P>"
								+ "<SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑; mso-font-kerning: 1.0pt; mso-ansi-language: EN-US; mso-fareast-language: ZH-CN; mso-bidi-language: AR-SA'>致敬！</SPAN>";
						try {
							sendMailService.sendSysMail(title, contextdetail, email);
						} catch (Exception e) {
							String exName = e.getClass().getSimpleName();
							if (exName.equals("RuntimeException") || exName.equals("SystemException"))
								error.append("供应商联系人"+vendor.getVe_contact()+"的"+e.getMessage());
								error.append("</br>");
						}
					}
					BaseUtil.showErrorOnSuccess(error.toString());
				}
			}
		}else {
			BaseUtil.showErrorOnSuccess("发送邮件错误，连接平台失败,无法获取供应商！"+response.getStatusCode());
		}
	}
	
	@Override
	public Map<String, Object> getTenderList(String page,String limit, String search, String date,String status) {
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
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/tender/purc?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					JSONObject obj = JSONObject.fromObject(data);
					result.put("total", obj.getInt("totalElement"));
					result.put("content", FlexJsonUtil.fromJsonArray(obj.getString("content"), PurchaseTender.class));
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
	public Map<String, Object> getTender(String id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		HashMap<String, Object> result = new HashMap<String, Object>();
		try {
			params.put("id", id);
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/tender/detail?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					PurchaseTender purchaseTender = FlexJsonUtil.fromJson(data, PurchaseTender.class);
			    	purchaseTender.setAttachs(B2BAttachsService.getAttaches(purchaseTender.getTenderAttaches()));
			    	Object[] tender = baseDao.getFieldsDataByCondition("TENDER", "TT_STATUS,PT_RECORDMAN,PT_INDATE,TT_AUDITMAN,TT_AUDITDATE,TT_STATUSCODE", "ID ="+purchaseTender.getId());
					if (tender!=null) {
						purchaseTender.setTt_status(tender[0]);
						purchaseTender.setPt_recordman(tender[1]);
						purchaseTender.setPt_indate(tender[2]==null?null:tender[2].toString().substring(0, 10));
						purchaseTender.setTt_statuscode(tender[5]);
						if (tender[5]!=null&&!"AUDITED".equals(tender[5].toString())) {
							baseDao.updateByCondition("Tender", "pt_title = '"+purchaseTender.getTitle()+"',questionEndDate = "
									+(purchaseTender.getQuestionEndDate()==null?null:DateUtil.parseDateToOracleString(Constant.YMD_HMS,purchaseTender.getQuestionEndDate()))
									+",endDate = "+DateUtil.parseDateToOracleString(Constant.YMD,purchaseTender.getEndDate())
									+",publishDate = "+DateUtil.parseDateToOracleString(Constant.YMD,purchaseTender.getPublishDate()), "ID ="+purchaseTender.getId());
						}
					}else if(purchaseTender.getResult()==0){
						baseDao.execute("INSERT INTO TENDER(ID,CODE,PT_STATUS,PT_RECORDMAN,PT_INDATE,PT_STATUSCODE,PT_TITLE,TT_STATUS,"
								+ "TT_STATUSCODE,questionEndDate,endDate,publishDate) VALUES("+purchaseTender.getId()+",'"
								+purchaseTender.getCode()+"','在录入',null,"+DateUtil.parseDateToOracleString(Constant.YMD, purchaseTender.getDate())
								+",'ENTERING','"+purchaseTender.getTitle()+"','在录入','ENTERING',"+(purchaseTender.getQuestionEndDate()==null?null:DateUtil.parseDateToOracleString(Constant.YMD_HMS,purchaseTender.getQuestionEndDate()))
								+","+DateUtil.parseDateToOracleString(Constant.YMD,purchaseTender.getEndDate())
								+","+DateUtil.parseDateToOracleString(Constant.YMD,purchaseTender.getPublishDate())+")");
						
						purchaseTender.setTt_status("在录入");
						purchaseTender.setPt_indate(DateUtil.format(purchaseTender.getDate(), Constant.YMD));
						purchaseTender.setTt_statuscode("ENTERING");
					}
					if (purchaseTender.getIfOpen()==0) {
						List<Map<String, Object>> Vends = new ArrayList<Map<String,Object>>();
						for (SaleTenderItem sItem : purchaseTender.getPurchaseTenderProds().get(0).getSaleTenderItems()) {
							Map<String, Object> vend = new HashMap<String, Object>();
							vend.put("enName", sItem.getSaleTender().getEnterpriseBaseInfo().getEnName());
							vend.put("uu", sItem.getSaleTender().getEnterpriseBaseInfo().getUu());
							vend.put("enAddress", sItem.getSaleTender().getEnterpriseBaseInfo().getEnAddress());
							vend.put("enBussinessCode", sItem.getSaleTender().getEnterpriseBaseInfo().getEnBusinessCode());
							vend.put("contact", sItem.getSaleTender().getUser());
							vend.put("contactTel", sItem.getSaleTender().getUserTel());
							vend.put("contactEmail", sItem.getSaleTender().getUserEmail());
							Vends.add(vend);
						}
						result.put("Vends", Vends);
					}
					result.put("purchaseTender", purchaseTender);
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
	public boolean isSubmit(String caller){
		return baseDao.checkIf("form", "fo_caller='"+caller+"' AND fo_isautoflow=-1");
	}
	
	@Override
	public Map<Object, Object> saveorPublicTender(String caller, String formStore,String gridStore, String gridStore1,int isPublish) {
		Map<Object, Object> fstore = BaseUtil.parseFormStoreToMap(formStore);
		Map<Object, Object> store = new HashMap<Object, Object>();
		try {
			Master master = SystemSession.getUser().getCurrentMaster();
			HashMap<String, String> params = new HashMap<String, String>();
			store.put("pt_recordman", "".equals(fstore.get("pt_recordman"))?SystemSession.getUser().getEm_name():fstore.get("pt_recordman"));
			store.put("pt_indate", "".equals(fstore.get("pt_indate"))?DateUtil.format(new Date(), Constant.YMD):fstore.get("pt_indate"));
			store.put("pt_status", "在录入");
			store.put("pt_statuscode", "ENTERING");
			store.put("tt_status", "在录入");
			store.put("tt_statuscode", "ENTERING");
			store.put("pt_title", fstore.get("title"));
			store.put("questionEndDate", fstore.get("questionEndDate"));
			store.put("endDate", fstore.get("endDate"));
			store.put("publishDate", fstore.get("publishDate"));
			fstore.remove("pt_recordman");
			fstore.remove("pt_indate");
			Set<TenderAttach> teAttachs = B2BAttachsService.parseAttachs(fstore.get("attachs").toString());
			fstore.remove("attachs");
			Map<Object, Object> tender = fstore;
			Object Id = fstore.get("id");
			Object code = fstore.get("code");
			String url = "";
			boolean noSubmit = !isSubmit(caller);
			tender.put("isPublish", isPublish);
			if (Id!=null&&!"".equals(Id.toString())) {
				url = "/erp/tender/updateSaved";
				if (isPublish==1&&noSubmit) {
					url = "/erp/tender/publishSaved";
				}
				store.put("id", Id);
				store.put("code", code);
				handlerService.beforeUpdate(caller, new Object[] {store});
			}else{
				handlerService.beforeSave(caller, new Object[] {store});
				url = "/erp/tender/save";
				if (isPublish==1&&noSubmit) {
					url = "/erp/tender/publish";
				}
				tender.put("id", null);
				tender.put("code", "ZB"+DateUtil.format(new Date(), "yyMMddHHmmss"));
				tender.put("date", new Date());
			}
			if (teAttachs.size()>0) {
				params.put("attaches", FlexJsonUtil.toJsonArray(teAttachs));
			}
			if (!StringUtil.hasText(tender.get("questionEndDate"))) {
				tender.put("questionEndDate",null);
			}else{
				tender.put("questionEndDate", DateUtil.parse(tender.get("questionEndDate").toString(),Constant.YMD_HMS));
			}
			
			tender.put("endDate", DateUtil.parse(tender.get("endDate").toString(),Constant.YMD));
			tender.put("publishDate", DateUtil.parse(tender.get("publishDate").toString(),Constant.YMD));
			fstore.remove("attachs");
			if (gridStore!=null&&!"".equals(gridStore)) {
				List<Map<Object, Object>> Gstore = BaseUtil.parseGridStoreToMaps(gridStore);
				for (Map<Object, Object> map : Gstore) {
					String qty = map.get("qty").toString();
					if (qty.indexOf(".")>-1) {
						map.put("qty", Long.parseLong(qty.substring(0,qty.indexOf("."))));
					}
				}
				tender.put("purchaseTenderProds", Gstore);
			}
			params.put("tender", FlexJsonUtil.toJsonDeep(tender));
			if (Integer.parseInt(tender.get("ifOpen").toString())==0&&gridStore1!=null&&!"".equals(gridStore1)) {
				params.put("enInfos", gridStore1);
			}
			if (params.size()>0) {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + url+"?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					String data = response.getResponseText();
					if (StringUtil.hasText(data)) {
						Map<Object, Object> id = BaseUtil.parseFormStoreToMap(data);
						if (Id!=null&&!"".equals(Id.toString())) {
							// 执行更新操作
							baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "TENDER", "id"));
						}else{
							store.putAll(id);
							// 执行保存操作
							String formSql = SqlUtil.getInsertSqlByFormStore(store, "TENDER", new String[] {}, new Object[] {});
							baseDao.execute(formSql);
						}
						if (isPublish==0) {
							// 执行修改后的其它逻辑
							if (Id!=null&&!"".equals(Id.toString())) {
								baseDao.logger.update(caller, "id", store.get("id"));
							}else {
								baseDao.logger.save(caller, "id", store.get("id"));
							}
						}else if (noSubmit) {
							// 记录操作
							baseDao.logger.others("发布", "发布成功", caller, "id", Id);
							if (Integer.parseInt(fstore.get("ifOpen").toString())==0) {
								SendMsg(String.valueOf(store.get("id")) ,caller);
							}
						}
						
						// 执行修改后的其它逻辑
						if (Id!=null&&!"".equals(Id.toString())) {
							handlerService.afterUpdate(caller, new Object[] {store});
						}else {
							handlerService.afterSave(caller, new Object[] {store});
						}
						return id;
					}
				}else {
					throw new Exception("连接平台失败！"+response.getStatusCode());
				}  
			}
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
		}
		return null;
	}
	
	@Override
	public void publicTender(Integer id, String caller) {
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { id });
		// 执行提交操作
		baseDao.submit("TENDER", "id=" + id, "tt_status", "tt_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { id });
	}
	
	public void auditPublicTender(int id, String caller) throws Exception {
		// 执行投标前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { id });
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id",String.valueOf(id));
		Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/tender/auditTender?access_id=" + master.getMa_uu(),
				params, true, master.getMa_accesssecret());
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			String data = response.getResponseText();
			if (StringUtil.hasText(data)) {
				JSONObject obj = JSONObject.fromObject(data);
				// 执行投标操作
				baseDao.audit("TENDER", "id="+id, "tt_status", "tt_statuscode", "tt_auditdate", "tt_auditman");
				// 记录操作
				baseDao.logger.others("发布招标", "发布成功", caller, "id", id);
				String flowcaller = processService.getFlowCaller(caller);
				if (flowcaller != null) {
					// 删除该单据已实例化的流程
					processService.deletePInstance(id, flowcaller, "audit");
				}
				if (obj.getInt("ifOpen")==0) {
					SendMsg(String.valueOf(id),caller);
				}
				
				// 执行审核后的其它逻辑
				handlerService.afterAudit(caller,new Object[] { id });
			}
		}else {
			throw new Exception("连接平台失败！"+response.getStatusCode());
		} 
	}

	@Override
	public void deleteTender(int id, String caller) {
		handlerService.beforeDel(caller, new Object[] {id});
		try {
			Master master = SystemSession.getUser().getCurrentMaster();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("id", String.valueOf(id));
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/tender/delete?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				// 执行删除操作
				baseDao.deleteById("TENDER", "id", id);
				// 记录操作
				baseDao.logger.delete(caller, "id", id);
				// 执行修改后的其它逻辑
				handlerService.afterDel(caller, new Object[] {id});
			}else {
				throw new Exception("连接平台失败！"+response.getStatusCode());
			}  
		} catch (Exception e) {
			BaseUtil.showError("删除失败，"+e.getMessage());
		}
	}
	
	@Override
	public void deleteProd(int tenderProdId) {
		try {
			Master master = SystemSession.getUser().getCurrentMaster();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("tenderProdId", String.valueOf(tenderProdId));
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/tender/deleteProd?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() != HttpStatus.OK.value()) {
				throw new Exception("连接平台失败！"+response.getStatusCode());
			}  
		} catch (Exception e) {
			BaseUtil.showError("删除产品明细失败，"+e.getMessage());
		}
		
	}
	
	@Override
	public void removeSaleTender(int id, Long vendUU, String caller) {
		handlerService.beforeDel(caller, new Object[] {id});
		try {
			Master master = SystemSession.getUser().getCurrentMaster();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("id", String.valueOf(id));
			params.put("vendUU", vendUU.toString());
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/tender/removeSaleTender?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				baseDao.logger.others("删除供应商"+vendUU, "删除成功", caller, "id", id);
			}else {
				throw new Exception("连接平台失败！"+response.getStatusCode());
			}  
		} catch (Exception e) {
			BaseUtil.showError("删除供应商失败，错误："+e.getMessage());
		}
		
	}

	@Override
	public Map<String, Object> getTenderList(String page,String limit, String search, String date) {
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
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/tender/open?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					JSONObject obj = JSONObject.fromObject(data);
					result.put("total", obj.getInt("totalElement"));
					result.put("content", FlexJsonUtil.fromJsonArray(obj.getString("content"), PurchaseTender.class));
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
	public Map<String, Object> getTenderCustList(String page,String limit, String search, String date,String status) {
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
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/tender?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					JSONObject obj = JSONObject.fromObject(data);
					result.put("total", obj.getInt("totalElement"));
					result.put("content", FlexJsonUtil.fromJsonArray(obj.getString("content"), SaleTenderErp.class));
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
	public Map<String, Object> getTenderPublic(String id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		HashMap<String, Object> result = new HashMap<String, Object>();
		try {
			params.put("id", id);
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/tender/detail?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					PurchaseTender purchaseTender = FlexJsonUtil.fromJson(data, PurchaseTender.class);
			    	purchaseTender.setAttachs(B2BAttachsService.getAttaches(purchaseTender.getTenderAttaches()));
			    	Long uu = SystemSession.getUser().getCurrentMaster().getMa_uu();
			    	PurchaseTenderProd prod = purchaseTender.getPurchaseTenderProds().get(0);
		    		for (SaleTenderItem sItem : prod.getSaleTenderItems()) {
		    			if (uu.longValue()==sItem.getSaleTender().getVendUU().longValue()) {
		    				if (purchaseTender.getTurned()==null) {
		    					Short turned = 1;
		    					purchaseTender.setTurned(turned);
		    					purchaseTender.setSaleId(sItem.getSaleId());
		    					break;
							}
						}
		    		}
					result.put("purchaseTender", purchaseTender);
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
	public Object addTenderItems(String id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			params.put("id", id);
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/tender/addItems?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					Map<Object, Object> res = BaseUtil.parseFormStoreToMap(data);
					if (res!=null) {
						baseDao.execute("INSERT INTO SALETENDER(ID,CODE,ST_STATUS,ST_STATUSCODE) "
								+ "VALUES("+res.get("id")+",'"+res.get("code")+"','在录入','ENTERING')");
					}
					return res.get("id");
				}
			}else {
				throw new Exception("连接平台失败！"+response.getStatusCode());
			}  
		} catch (Exception e) {
			BaseUtil.showError("申请投标失败，错误"+e.getMessage());
		}
		return null;
	}
	
	@Override
	public Map<String, Object> getTenderSubmission(String id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		HashMap<String, Object> result = new HashMap<String, Object>();
		try {
			params.put("id", id);
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/tender/saleTender?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					JSONObject jObject =  JSONObject.fromObject(data);
					SaleTenderErp saleTenderErp = FlexJsonUtil.fromJson(jObject.getString("saleTenderTemp"), SaleTenderErp.class);
					saleTenderErp.setAttachs(B2BAttachsService.getAttaches(saleTenderErp.getBidAttaches()));
					Map<Object, Object> contactInfo = BaseUtil.parseFormStoreToMap(jObject.getString("tenderContactInfo"));
					saleTenderErp.setUser(contactInfo.get("contact").toString());
					saleTenderErp.setUserTel(contactInfo.get("contactTel").toString());
					Object[] saleTender = baseDao.getFieldsDataByCondition("SALETENDER", "ST_STATUS,ST_AUDITMAN,ST_AUDITDATE,ST_STATUSCODE", "ID ="+saleTenderErp.getId());
					if (saleTender!=null) {
						saleTenderErp.setSt_status(saleTender[0]);
						saleTenderErp.setSt_auditman(saleTender[1]);
						saleTenderErp.setSt_auditdate(saleTender[2]==null?null:saleTender[2].toString().substring(0, 10));
						saleTenderErp.setSt_statuscode(saleTender[3]);
					}else if(saleTenderErp.getOverdue()==0){
						baseDao.execute("INSERT INTO SALETENDER(ID,CODE,ST_STATUS,ST_STATUSCODE) "
								+ "VALUES("+saleTenderErp.getId()+",'"+saleTenderErp.getCode()+"','在录入','ENTERING')");
						saleTenderErp.setSt_status("在录入");
						saleTenderErp.setSt_statuscode("ENTERING");
					}
					
					result.put("saleTender", saleTenderErp);
					Set<TenderAttach> tAttachs = new HashSet<TenderAttach>();
					List<TenderAttach> tAttachs2 = FlexJsonUtil.fromJsonArray(jObject.getString("tenderAttaches"),TenderAttach.class);
					tAttachs.addAll(tAttachs2);
					result.put("tendattachs", B2BAttachsService.getAttaches(tAttachs));
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
	public void saveSaleTender(String caller, String formStore, String enBaseInfo,String gridStore,String attaches) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeUpdate(caller, new Object[] {store});
		try {
			Master master = SystemSession.getUser().getCurrentMaster();
			HashMap<String, String> params = new HashMap<String, String>();
			Object ifAll = store.get("ifAll");
			store.remove("ifAll");
			Map<String, Object> saleTenderErp = new HashMap<String, Object>();
			saleTenderErp.put("id", store.get("id"));
			if(ifAll!=null&&Integer.parseInt(ifAll.toString())==1){
				saleTenderErp.put("cycle", store.get("cycle"));
				saleTenderErp.put("taxrate", store.get("taxrate"));
				store.remove("cycle");
				store.remove("taxrate");
			}else{
				saleTenderErp.put("cycle", 0);
				saleTenderErp.put("taxrate", 0);
			}
			saleTenderErp.put("totalMoney", store.get("totalMoney"));
			store.remove("totalMoney");
			
			if (enBaseInfo!=null&&!"".equals(enBaseInfo)) {
				saleTenderErp.put("enterpriseBaseInfo", BaseUtil.parseFormStoreToMap(enBaseInfo));
			}
			if (gridStore!=null&&!"".equals(gridStore)) {
				saleTenderErp.put("saleTenderItems", BaseUtil.parseGridStoreToMaps(gridStore));
			}
			params.put("saleTender", FlexJsonUtil.toJsonDeep(saleTenderErp));
			
			if (attaches!=null&&!"".equals(attaches)){
				if ("clearAll".equals(attaches)) {
					params.put("attaches", "[]");
				}else{
					params.put("attaches", FlexJsonUtil.toJsonArray(B2BAttachsService.parseAttachs(attaches)));
				}
			}
			if (params.size()>0) {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/tender/reply?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() != HttpStatus.OK.value()) {
					throw new Exception("连接平台失败！"+response.getStatusCode());
				}
			}
			// 执行保存操作
			baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "SALETENDER", "id"));
			// 记录操作
			baseDao.logger.update(caller, "id", store.get("id"));
			// 执行修改后的其它逻辑
			handlerService.afterUpdate(caller, new Object[] {store});
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("保存失败，错误："+e.getMessage());
		}
	}

	@Override
	public void submitSaleTender(int id, String caller) {
		
		handlerService.beforeSubmit(caller,new Object[] { id });
		// 执行提交前的其它逻辑
		// 执行提交操作
		baseDao.submit("SALETENDER", "id=" + id, "st_status", "st_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { id });
		
	}

	@Override
	public void resSubmitSaleTender(int id, String caller) {
		//执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, id);
		// 执行反提交操作
		baseDao.resOperate("SALETENDER", "id="+id, "st_status", "st_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "id", id);
		//执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, id);
	}
	
	@Override
	public void resSubmitTender(int id, String caller) {
		//执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, id);
		// 执行反提交操作
		baseDao.resOperate("TENDER", "id="+id, "tt_status", "tt_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "id", id);
		//执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, id);
	}
	
	@Override
	public void auditSaleTender(int id, String caller) {
		// 执行投标前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { id });
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			params.put("id", String.valueOf(id));
			params.put("auditStatus", "1");
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/tender/audit/sale?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				// 执行投标操作
				baseDao.audit("SALETENDER", "id="+id, "st_status", "st_statuscode", "st_auditdate", "st_auditman");
				// 记录操作
				baseDao.logger.others("投标", "投标成功", caller, "id", id);
				// 执行审核后的其它逻辑
				handlerService.afterAudit(caller,new Object[] { id });
			}else {
				throw new Exception("连接平台失败！"+response.getStatusCode());
			} 
		} catch (Exception e) {
			BaseUtil.showError("投标失败，错误："+e.getMessage());
		}
	}

	@Override
	public void resAuditSaleTender(int id, String caller) {
		handlerService.beforeResAudit(caller, new Object[]{id});
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			params.put("id", String.valueOf(id));
			params.put("auditStatus", "0");
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/tender/audit/sale?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				// 执行重新投标操作
				baseDao.resAudit("SALETENDER", "id="+id, "st_status", "st_statuscode", "st_auditdate", "st_auditman");
				// 记录操作
				baseDao.logger.others("重新投标", "成功", caller, "id", id);
				handlerService.afterResAudit(caller, new Object[]{id});
			}else {
				throw new Exception("连接平台失败！"+response.getStatusCode());
			} 
		} catch (Exception e) {
			BaseUtil.showError("重新投标失败，错误："+e.getMessage());
		}
	}
	
	@Override
	public Map<String, Object> getTenderEstimate(String id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		HashMap<String, Object> result = new HashMap<String, Object>();
		try {
			params.put("id", id);
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/tender/detail?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					PurchaseTender purchaseTender = FlexJsonUtil.fromJson(data, PurchaseTender.class);
			    	purchaseTender.setAttachs(B2BAttachsService.getAttaches(purchaseTender.getTenderAttaches()));
			    	
			    	Object[] tender = baseDao.getFieldsDataByCondition("TENDER", "PT_STATUS,PT_RECORDMAN,PT_INDATE,PT_AUDITMAN,PT_AUDITDATE,"
			    			+ "PT_STATUSCODE,PT_ATTACHS,PT_TURNPURCHASE", "ID ="+purchaseTender.getId());
					if (tender!=null) {
						purchaseTender.setPt_status(tender[0]);
						purchaseTender.setPt_recordman(tender[1]);
						purchaseTender.setPt_indate(tender[2]==null?null:tender[2].toString().substring(0, 10));
						purchaseTender.setPt_auditman(tender[3]);
						purchaseTender.setPt_auditdate(tender[4]==null?null:tender[4].toString().substring(0, 10));
						purchaseTender.setPt_statuscode(tender[5]);
						purchaseTender.setPt_attachs(tender[6]);
						purchaseTender.setPt_turnPurchase(tender[7]);
						
						if (tender[5]!=null&&!"AUDITED".equals(tender[5].toString())) {
							baseDao.updateByCondition("Tender", "pt_title = '"+purchaseTender.getTitle()+"',questionEndDate = "+(purchaseTender.getQuestionEndDate()==null?null:DateUtil.parseDateToOracleString(Constant.YMD_HMS,purchaseTender.getQuestionEndDate()))
									+",endDate = "+DateUtil.parseDateToOracleString(Constant.YMD,purchaseTender.getEndDate())
									+",publishDate = "+DateUtil.parseDateToOracleString(Constant.YMD,purchaseTender.getPublishDate()),
									"ID ="+purchaseTender.getId());
						}
						
					}else if(purchaseTender.getResult()==0){
						baseDao.execute("INSERT INTO TENDER(ID,code,PT_STATUS,PT_RECORDMAN,PT_INDATE,PT_STATUSCODE,PT_TITLE,TT_STATUS,"
							+ "TT_STATUSCODE,questionEndDate,endDate,publishDate) "+ "VALUES("+purchaseTender.getId()+",'"
							+purchaseTender.getCode()+"','在录入',null,"+DateUtil.parseDateToOracleString(Constant.YMD, purchaseTender.getDate())
							+",'ENTERING','"+purchaseTender.getTitle()+"','在录入','ENTERING',"+(purchaseTender.getQuestionEndDate()==null?null:DateUtil.parseDateToOracleString(Constant.YMD_HMS,purchaseTender.getQuestionEndDate()))
							+","+DateUtil.parseDateToOracleString(Constant.YMD,purchaseTender.getEndDate())
							+","+DateUtil.parseDateToOracleString(Constant.YMD,purchaseTender.getPublishDate())+")");
						
						purchaseTender.setPt_status("在录入");
						purchaseTender.setPt_indate(DateUtil.format(purchaseTender.getDate(), Constant.YMD));
						purchaseTender.setPt_statuscode("ENTERING");
					}
					boolean turnPurchase = "AUDITED".equals(purchaseTender.getPt_statuscode())&&(purchaseTender.getPt_turnPurchase()==null||(Integer.parseInt(purchaseTender.getPt_turnPurchase().toString())!=1));
					if (turnPurchase) {
						List<Map<String, Object>> turns = new ArrayList<Map<String,Object>>();
						for (PurchaseTenderProd prod : purchaseTender.getPurchaseTenderProds()) {
							for (SaleTenderItem sItem : prod.getSaleTenderItems()) {
								if (sItem.getApplyStatus()!=null&&sItem.getApplyStatus()==1) {
									Map<String, Object> map = new HashMap<String, Object>();
									map.put("enName", sItem.getEnName());
									map.put("vendUU", sItem.getVendUU());
									map.put("prodTitle", prod.getProdTitle());
									map.put("prodCode", prod.getProdCode());
									map.put("brand", prod.getBrand());
									map.put("unit", prod.getUnit());
									map.put("prodSpec", prod.getProdSpec());
									map.put("currency", purchaseTender.getCurrency());
									map.put("qty", prod.getQty());
									map.put("cycle", sItem.getCycle());
									map.put("taxrate", sItem.getTaxrate());
									map.put("price", sItem.getPrice());
									map.put("amount", prod.getQty()*sItem.getPrice());
									map.put("description", sItem.getDescription());
									turns.add(map);
								}
							}
						}
						result.put("turns", turns);
					}
					if (purchaseTender.getIfAll()!=null&&purchaseTender.getIfAll()==1) {
						List<Map<String, Object>> prods = new ArrayList<Map<String,Object>>();
						List<Map<String, Object>> vendors = new ArrayList<Map<String,Object>>();
						
						for (PurchaseTenderProd prod : purchaseTender.getPurchaseTenderProds()) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("id", prod.getId());
							map.put("index", prod.getIndex());
							map.put("prodTitle", prod.getProdTitle());
							map.put("prodCode", prod.getProdCode());
							map.put("brand", prod.getBrand());
							map.put("unit", prod.getUnit());
							map.put("qty", prod.getQty());
							map.put("prodSpec", prod.getProdSpec());
							prods.add(map);
							if (vendors.size()==0) {
								for (SaleTenderItem sItem : prod.getSaleTenderItems()) {
									Map<String, Object> vendor = new HashMap<String, Object>();
									vendor.put("saleTenderId", sItem.getSaleId());
									vendor.put("enName", sItem.getEnName());
									vendor.put("cycle", sItem.getCycle());
									vendor.put("taxrate", sItem.getTaxrate());
									vendor.put("totalMoney", sItem.getTotalMoney());
									vendor.put("reason", sItem.getDescription());
									vendor.put("applyStatus", sItem.getApplyStatus());
									vendor.put("vendUU", sItem.getVendUU());
									vendors.add(vendor);
								}
							}
						}
						purchaseTender.setPurchaseTenderProds(null);
						result.put("purchaseTender", purchaseTender);
						result.put("prods", prods);
						result.put("vendors", vendors);
					}else{
						result.put("purchaseTender", purchaseTender);
					}
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
	public void saveEstimateTender(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Object status = baseDao.getFieldDataByCondition("Tender", "pt_statuscode", "id=" + store.get("id"));
		if ("AUDITED".equals(status)) {
			BaseUtil.showError("已审核不允许修改！");
		}
		handlerService.beforeUpdate(caller, new Object[] {store});
		try {
			Master master = SystemSession.getUser().getCurrentMaster();
			HashMap<String, String> params = new HashMap<String, String>();
			Object ifAll = store.get("ifAll");
			String url = "/erp/tender/decide";
			if (gridStore!=null&&!"".equals(gridStore)&&gstore.size()>0) {
				for (Map<Object, Object> map : gstore) {
					Object applyStatus = map.get("applyStatus");
					if (applyStatus==null) {
						map.put("applyStatus", 0);
					}
				}
				if(ifAll!=null&&!"".equals(ifAll)&&Integer.parseInt(ifAll.toString())==1){
					url = "/erp/tender/decide/all";
					params.put("data", FlexJsonUtil.toJsonArray(gstore));
				}else{
					params.put("saleTenderItems", FlexJsonUtil.toJsonArray(gstore));
				}
			}
			store.remove("ifAll");
			if (params.size()>0) {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + url+"?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() != HttpStatus.OK.value()) {
					throw new Exception("连接平台失败！"+response.getStatusCode());
				}  
			}
			// 执行保存操作
			baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "TENDER", "id"));
			// 记录操作
			baseDao.logger.update(caller, "id", store.get("id"));
			// 执行修改后的其它逻辑
			handlerService.afterUpdate(caller, new Object[] {store});
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误："+e.getMessage());
		}
	}
	
	@Override
	public void submitEstimateTender(int id, String caller) {
		
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { id });
		// 执行提交操作
		baseDao.submit("TENDER", "id=" + id, "pt_status", "pt_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { id });
		
	}

	@Override
	public void resSubmitEstimateTender(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Tender", "pt_statuscode", "id="+id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, id);
		// 执行反提交操作
		baseDao.resOperate("TENDER", "id="+id, "pt_status", "pt_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "id", id);
		//执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, id);
	}
	
	@Override
	public void auditEstimateTender(int id, String caller) {
		// 执行投标前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { id });
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			params.put("id",String.valueOf(id));
			params.put("auditStatus", "1");
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/tender/audit/purc?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				// 执行投标操作
				baseDao.audit("TENDER", "id="+id, "pt_status", "pt_statuscode", "pt_auditdate", "pt_auditman");
				// 记录操作
				baseDao.logger.others("评标", "评标成功", caller, "id", id);
				String flowcaller = processService.getFlowCaller(caller);
				if (flowcaller != null) {
					// 删除该单据已实例化的流程
					processService.deletePInstance(id, flowcaller, "audit");
				}
				// 执行审核后的其它逻辑
				handlerService.afterAudit(caller,new Object[] { id });
			}else {
				throw new Exception("连接平台失败！"+response.getStatusCode());
			} 
		} catch (Exception e) {
			BaseUtil.showError("评标失败，错误："+e.getMessage());
		}
	}

	final static String INSERTPURCHASH = "INSERT INTO PURCHASE(pu_id,pu_code,pu_date,pu_recordid,pu_recordman,pu_indate,pu_updateman,"
			+ "pu_updatedate,pu_status,pu_statuscode,pu_vendid,pu_vendcode,pu_vendname,pu_vendcontact,pu_vendcontactuu,pu_vendcontactmobile,pu_receivecode,"
			+ "pu_receivename,pu_paymentsid,pu_paymentscode,pu_payments,pu_transport,pu_vendremark,pu_vendstatus,pu_mainmark,"
			+ "pu_currency,pu_rate,pu_shipaddresscode,pu_getprice,pu_printstatus,pu_printstatuscode,pu_count,pu_sync,pu_transferbank,"
			+ "pu_source,pu_sourceid,pu_sourcecode) VALUES (?,?,sysdate,?,?,sysdate,?,sysdate,?,'ENTERING',?,?,?,?,?,?,?,?,?,?,?,?,?,?,'非标准',?,?,?,0,?,'UNPRINT',0,'未同步','未生成','招投标',?,?)";
	
	final static String INSERTPURCHASHDETAIL = "INSERT INTO PURCHASEDETAIL(pd_id,pd_puid,pd_code,pd_detno,pd_prodid,pd_prodcode,pd_qty,"
			+ "pd_yqty,pd_price,pd_rate,pd_bonded,pd_acceptqty,pd_ngacceptqty,pd_vendcode,pd_vendname,pd_source,pd_sourcecode,"
			+ "pd_remark,pd_auditstatus,pd_status,pd_mrpstatus,pd_mrpstatuscode,pd_frozenqty,pd_turnqty,pd_backqty) "
			+ "VALUES (?,?,?,?,?,?,?,0,?,?,0,0,0,?,?,?,?,?,?,'ENTERING',?,'ENTERING',0,0,0)";
	
	@Override
	public String turnPurchase(String caller, String fromStore, String param,List<Long> vendUUs) {
		String msg = "";
		try {
			Map<Object, Object> form = BaseUtil.parseFormStoreToMap(fromStore);
			List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(param);
			Object id = form.get("id");
			Object code = form.get("code");
			Employee employee = SystemSession.getUser();
			for (Long venduu : vendUUs) {
				Integer puid = baseDao.getSeqId("PURCHASE_SEQ");
				String pucode = baseDao.sGetMaxNumber("Purchase", 2);
				
				Object [] vend = baseDao.getFieldsDataByCondition("Vendor", "ve_id,ve_code,ve_name,ve_contact,ve_apvendcode,"
					+ "ve_apvendname,ve_paymentid,ve_paymentcode,ve_payment,ve_shipment,ve_remark,ve_auditstatus", "ve_uu = "+venduu);
				
				Object rate = baseDao.getFieldDataByCondition("currencysmonth left join Currencys on cr_name=cm_crname","cm_crrate", 
				"cm_yearmonth = "+DateUtil.getYearmonth(new Date())+" and cm_crname = '" + form.get("currency")+"' and nvl(cr_statuscode,' ')='CANUSE'");
				
				String emname = employee.getEm_name();
				Integer emid = employee.getEm_id();
				
				Object[] vcuu = baseDao.getFieldsDataByCondition("VendorContact","vc_uu,vc_mobile", "vc_vecode = '"+vend[1]+"' and vc_name = '"+vend[3]+"'");
				
				baseDao.execute(INSERTPURCHASH, new Object[]{puid,pucode,emid,emname,emname,BaseUtil.getLocalMessage("ENTERING"),
						vend[0],vend[1],vend[2],vend[3],vcuu[0],vcuu[1],vend[4],vend[5],vend[6],vend[7],vend[8],vend[9],vend[10],vend[11],
						form.get("currency"),rate,form.get("shipAddress"),BaseUtil.getLocalMessage("UNPRINT"),id,code});
				
				int detno = 1;
				for (Map<Object, Object> map : store) {
					if (venduu.longValue()==Long.parseLong(map.get("vendUU").toString())) {
						int pdid = baseDao.getSeqId("PURCHASEDETAIL_SEQ");
						Object[] prcode = baseDao.getFieldsDataByCondition("Product","pr_id,pr_code", "pr_detail = '"+map.get("prodTitle")+"' and pr_spec = '"+map.get("prodCode")+"'");
						Object qty = map.get("qty");
						Object price = map.get("price");
						Object taxrate = map.get("taxrate");
						baseDao.execute(INSERTPURCHASHDETAIL, new Object[]{pdid,puid,pucode,detno,prcode[0],prcode[1],qty,price,
								taxrate,vend[1],vend[2],id,code,map.get("description"),BaseUtil.getLocalMessage("ENTERING"),
								BaseUtil.getLocalMessage("ENTERING")});
						detno++;
					}
					
				}
				getTotal(puid);
				msg += "、"+"<a href=\"javascript:openUrl('jsps/scm/purchase/purchase.jsp?formCondition=pu_idIS"+puid+"&gridCondition=pd_puidIS"+puid+"')\">" + pucode + "</a>";
			}
			baseDao.updateByCondition("Tender", "pt_turnpurchase = 1", "id = "+id);
			baseDao.logger.turn("转采购合同", caller, "id", id);
		} catch (Exception e) {
			BaseUtil.showError("转采购合同失败，错误："+e.getMessage());
		}
		return "转采购合同成功，采购单"+msg.substring(1);
	}
	
	private void getTotal(Object pu_id) {
		baseDao.execute("update PurchaseDetail set pd_bgprice=pd_price where pd_puid=" + pu_id + " and nvl(pd_bgprice,0)=0");
		baseDao.execute("update PurchaseDetail set pd_total=round(pd_price*pd_qty,2) where pd_puid=" + pu_id);
		baseDao.execute("update Purchase set pu_total=(select sum(pd_total) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_id="
				+ pu_id);
		baseDao.execute("update purchasedetail set pd_netprice=round(nvl(pd_price,0)/(1+nvl(pd_rate,0)/100),8) where pd_puid=" + pu_id);
		baseDao.execute("update purchasedetail set pd_taxtotal=round(nvl(pd_netprice,0)*nvl(pd_qty,0),2) where pd_puid=" + pu_id);
		baseDao.execute("update Purchase set pu_taxtotal=(select sum(pd_taxtotal) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_id="
				+ pu_id);
		baseDao.execute("update purchasedetail set pd_code=(select pu_code from purchase where pd_puid=pu_id) where pd_puid=" + pu_id
				+ " and not exists (select 1 from purchase where pd_code=pu_code)");
		baseDao.execute("update Purchase set pu_totalupper=L2U(nvl(pu_total,0)) WHERE pu_id=" + pu_id);
	}

	@Override
	public Map<String, Object> getJProcessByForm(String finds) {
		List<Map<Object,Object>> keyList = BaseUtil.parseGridStoreToMaps(finds);
		Map<String, Object> nodes= new HashMap<String, Object>();
		for (Map<Object, Object> map : keyList) {
			String caller = (String) map.get("caller");
			int keyValue = Integer.parseInt(map.get("keyValue").toString());
			nodes.put(caller, jprocessService.getJprocessNode(caller, keyValue,"current"));
		}
		return nodes;
	}
}
