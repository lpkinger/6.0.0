package com.uas.erp.service.crm.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.crm.BusinessChanceService;

import net.sf.json.JSONObject;

@Service
public class BusinessChanceServiceImpl implements BusinessChanceService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;
	@Autowired
	private SingleFormItemsService singleformitemsservice;
	@Override
	public Map<String,Double> getLngAndLat(String address){
		Map<String,Double> map=new HashMap<String, Double>();
		 String url = "http://api.map.baidu.com/geocoder/v2/?address="+address+"&output=json&ak=YOgN88tPirPFHBOk32EjRNQIeGh1z1n6";
	 String json = loadJSON(url);
	 if(!"".equals(json)&&json!=null){
		 JSONObject obj = JSONObject.fromObject(json);
		 if(obj.get("status").toString().equals("0")){
		 	double lng=obj.getJSONObject("result").getJSONObject("location").getDouble("lng");
		 	double lat=obj.getJSONObject("result").getJSONObject("location").getDouble("lat");
		 	map.put("lng", lng);
		 	map.put("lat", lat);
		 	//System.out.println("经度："+lng+"---纬度："+lat);
		 }else{
		 	//System.out.println("未找到相匹配的经纬度！");
			 map.put("lng", 0.0);
			 map.put("lat", 0.0);
		 }		 
	 }else{
		 map.put("lng", 0.0);
		 map.put("lat", 0.0);
	 }

		return map;
	}
	@Override
	 public String loadJSON (String url) {
	 StringBuilder json = new StringBuilder();
	 try {
	   URL oracle = new URL(url);
	   URLConnection yc = oracle.openConnection();
	   BufferedReader in = new BufferedReader(new InputStreamReader(
	     yc.getInputStream()));
	   String inputLine = null;
	   while ( (inputLine = in.readLine()) != null) {
	  json.append(inputLine);
	   }
	   in.close();
	    } catch (MalformedURLException e) {
	    } catch (IOException e) {
	    }
	 return json.toString();
	  }

	@Override
	public void DescriptionLimit(Map<Object,Object> store){
		Boolean flag= baseDao.checkByCondition("BusinessChance", "bc_description='"+store.get("bc_description") +"' and NVL(bc_lockstatuscode,' ')<>'LOCKED' and bc_id<>'"+store.get("bc_id")+"'");
		if(!flag){
			BaseUtil.showError("不能保存已存在的商机名称!");
		}else{
			if(!(store.get("bc_domancode")==null||"".equals(store.get("bc_domancode").toString().trim()))){					
				Object[] lockdate = baseDao.getFieldsDataByCondition("BusinessChance", "to_date(sysdate)-to_date(bc_lockdate),bc_domancode", "bc_description='"+store.get("bc_description") +"' and NVL(bc_lockstatuscode,' ')='LOCKED' and bc_domancode is not null and bc_id<>'"+store.get("bc_id")+"' order by bc_lockdate desc");
				if(lockdate!=null){
					if(lockdate[1]!=null){
						Object defaultoridOld = baseDao.getFieldDataByCondition("employee", "em_defaultorid", "em_code='"+lockdate[1]+"'");
						Object defaultoridNew = baseDao.getFieldDataByCondition("employee", "em_defaultorid", "em_code='"+store.get("bc_domancode")+"'");
						if(defaultoridOld.toString().equals(defaultoridNew.toString())){
							Object orgrecovertime =  baseDao.getFieldDataByCondition("BusinessBasis", "bd_orgrecovertime", "1=1");
							if(Integer.parseInt(lockdate[0].toString())<=Integer.parseInt(orgrecovertime.toString())){
								BaseUtil.showError("不能保存已存在的商机名称或者不符合同一组织N天不允许跟进限制!");
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void saveBusinessChance(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BusinessChance", "bc_code='" + store.get("bc_code") + "'");
		
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		if(baseDao.isDBSetting("BusinessChance", "BC_DescriptionLimit")){
			DescriptionLimit(store);
		}	
		//BusinessChance_seq
		Object bc_id= store.get("bc_id"); 
		if(bc_id==null || bc_id.equals("")){			
			int id=singleformitemsservice.getId("BusinessChance_seq");
			store.put("bc_id", id);
		}
		store.put("bc_longitude", getLngAndLat(store.get("bc_address").toString()).get("lng"));
		store.put("bc_latitude", getLngAndLat(store.get("bc_address").toString()).get("lat"));
		Object data = store.get("bc_nichehouse");		
		if(data!=null){
			Object prop = baseDao.getFieldDataByCondition("businessdatabase", "bd_prop", "bd_name='"+data.toString()+"'");
			if(prop!=null){
				if("可领取可分配".equals(prop.toString())){
					if(!"".equals(store.get("bc_doman"))||!"".equals(store.get("bc_domancode"))){
						BaseUtil.showError("公有商机库不能选择跟进人");
					}
				}
			}
		}
		
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BusinessChance", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		//更新最后跟进时间
		baseDao.execute("update businesschance set bc_lastdate=to_date('"+store.get("bc_lastdate")+"','yyyy-mm-dd HH24:MI:SS') where bc_code='"+store.get("bc_code")+"'");
		//更新商机的所属代理商
		baseDao.execute("update businesschance set bc_agency=(select bd_agency from businessdatabase where bd_name='"+store.get("bc_nichehouse")+"') where bc_code='"+store.get("bc_code")+"'");
		
		
		/*
		  Object bc_code=store.get("bc_code");
		  Object bc_doman= store.get("bc_doman"); 		
		  String code=singleformitemsservice.getCodeString("BusinessChanceData", "BusinessChanceData", 2);		
		  if(bc_doman!=null && bc_doman!="" &&!bc_doman.equals("")){
			String sql="Insert into BUSINESSCHANCEDATA (BCD_ID,BCD_BCID,BCD_MAN,BCD_DATE,BCD_REMARK,BCD_STATUS,BCD_STATUSCODE,BCD_BSCODE,BCD_BSNAME,BCD_CODE,BCD_COUNT,BCD_TYPE)  select BUSINESSCHANCEDATA_seq.nextval,bc_id,bc_doman,sysdate,'商机领取','已审核','AUDITED',bs_code,bc_currentprocess,'"+code+"',1,'商机领取' from BUSINESSCHANCE LEFT JOIN BUSINESSCHANCESTAGE ON BC_CURRENTPROCESS=BS_NAME where bc_code='"+bc_code+"'";
			baseDao.execute(sql);
		}*/
		
		// 记录操作
		baseDao.logger.save(caller, "bc_id", store.get("bc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	
	}

	@Override
	public void deleteBusinessChance(int bc_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { bc_id });
		int count = baseDao.getCount("select * from businesschancedata where bcd_bcid='"+bc_id+"'");
		if (count>0) {
			BaseUtil.showError("该商机存在商机动态，不能删除该商机");
		}
		// 删除purchase
		baseDao.deleteById("BusinessChance", "bc_id", bc_id);
		// 记录操作
		baseDao.logger.delete(caller, "bc_id", bc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, bc_id);
	}

	@Override
	public void updateBusinessChance(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		store.get("bc_id");
		Object data = store.get("bc_nichehouse");
		Object prop = baseDao.getFieldDataByCondition("businessdatabase", "bd_prop", "bd_name='"+data.toString()+"'");
		if(data!=null){
			if(prop!=null){
				if("可领取可分配".equals(prop.toString())){
					if(!"".equals(store.get("bc_doman"))||!"".equals(store.get("bc_domancode"))){
						BaseUtil.showError("公有商机库不能选择跟进人");
					}
				}
			}
		}
		
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] {store});
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BusinessChance", "bc_id");
		baseDao.execute(formSql);
		
		//更新最后跟进时间
		baseDao.execute("update businesschance set bc_lastdate=sysdate where bc_code='"+store.get("bc_code")+"'");
		
		// 记录操作
		baseDao.logger.update(caller, "bc_id", store.get("bc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] {store});
	}

	@Override
	public void auditBusinessChance(int bc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BusinessChance", "bc_statuscode", "bc_id=" + bc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, bc_id);
		// 执行审核操作
		baseDao.updateByCondition("BusinessChance", "bc_statuscode='AUDITED',bc_status='" + BaseUtil.getLocalMessage("AUDITED") + "'",
				"bc_id=" + bc_id);
		
		//更新最后跟进时间
		baseDao.execute("update businesschance set bc_lastdate=sysdate where bc_id="+bc_id);	
		
		// 记录操作
		baseDao.logger.audit(caller, "bc_id", bc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, bc_id);
	}

	@Override
	public void resAuditBusinessChance(int bc_id, String caller) {
		int count = baseDao.getCount("select * from businesschancedata where bcd_bcid='"+bc_id+"'");
		if (count>0) {
			BaseUtil.showError("该商机存在商机动态，不能反审核该商机");
		}
		Object data = baseDao.getFieldDataByCondition("BusinessChance", "bc_desc15", "bc_id=" + bc_id);//转入B2B询价单状态
		if("已转平台询价".equals(data)){
			BaseUtil.showError("该内部商机已转平台报价,不能反审核");
		}
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BusinessChance", "bc_statuscode", "bc_id=" + bc_id);
		Object bc_code = baseDao.getFieldDataByCondition("BusinessChance", "bc_code", "bc_id=" + bc_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, bc_id);
		// 有对应的 销售订单，报价单，出货单，客户拜访报告 的商机限制反审核
		int count1 = baseDao.getCount("select count(*) from Saledetail left join BusinessChance on sd_bcid=bc_id where sd_bcid=" + bc_id);
		if (count1 != 0) {
			BaseUtil.showError("商机编号:" + bc_code + " 已有对应的销售订单,不能反审核!");
		}
		int count2 = baseDao.getCount("select count(*) from Quotationdetail left join BusinessChance on qd_bcid=bc_id where qd_bcid="
				+ bc_id);
		if (count2 != 0) {
			BaseUtil.showError("商机编号:" + bc_code + " 已有对应的报价单,不能反审核!");
		}
		int count3 = baseDao.getCount("select count(*) from prodiodetail left join BusinessChance on pd_bcid=bc_id where pd_bcid=" + bc_id);
		if (count3 != 0) {
			BaseUtil.showError("商机编号:" + bc_code + " 已有对应的出货单,不能反审核!");
		}
		int count4 = baseDao.getCount("select count(*) from VisitRecord  left join ProductInfo on vr_id=pi_vrid where pi_bcid=" + bc_id);
		if (count4 != 0) {
			BaseUtil.showError("商机编号:" + bc_code + " 已有对应的客户拜访报告,不能反审核!");
		}
		// 执行反审核操作
		baseDao.updateByCondition("BusinessChance", "bc_statuscode='ENTERING',bc_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"bc_id=" + bc_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "bc_id", bc_id);
		handlerService.afterResAudit(caller, bc_id);
	}

	@Override
	public void submitBusinessChance(int bc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BusinessChance", "bc_statuscode", "bc_id=" + bc_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, bc_id);
		// 执行提交操作
		baseDao.updateByCondition("BusinessChance", "bc_statuscode='COMMITED',bc_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"bc_id=" + bc_id);
		// 记录操作
		baseDao.logger.submit(caller, "bc_id", bc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, bc_id);
	}

	@Override
	public void resSubmitBusinessChance(int bc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BusinessChance", "bc_statuscode", "bc_id=" + bc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, bc_id);

		// 执行反提交操作
		baseDao.updateByCondition("BusinessChance", "bc_statuscode='ENTERING',bc_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"bc_id=" + bc_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "bc_id", bc_id);
		handlerService.afterResSubmit(caller, bc_id);
	}

	@Transactional
	@Override
	public String SendSample(int bc_id, String caller) {// 送样
		Object status = baseDao.getFieldDataByCondition("BusinessChance", "bc_statuscode", "bc_id=" + bc_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError("审核后才能送样");
		}
		Object[] datas = baseDao.getFieldsDataByCondition("BusinessChance",
				"bc_recorder,bc_custcode,bc_custname,bc_model,bc_departmentcode,bc_department", "bc_id=" + bc_id);
		int sa_id = baseDao.getSeqId("Sale_SEQ");
		String code = baseDao.sGetMaxNumber("Sale", 2);
		String insertSql = "insert into Sale ("
				+ "sa_code,sa_id,sa_date,sa_recorder,sa_status,sa_statuscode,sa_custcode,sa_custname,sa_departmentcode,sa_departmentname) values("
				+ "?,?,sysdate,?,?,'ENTERING',?,?,?,?)";
		baseDao.execute(insertSql, new Object[] { code, sa_id, datas[0], BaseUtil.getLocalMessage("ENTERING"), datas[1], datas[2],
				datas[4], datas[5] });
		if (StringUtil.hasText(datas[3])) {
			baseDao.execute(
					"insert into saledetail(sd_id,sd_code,sd_said,sd_detno,sd_prodcode,sd_statuscode,sd_status) values (saledetail_seq.nextval,?,?,1,?,'ENTERING',?)",
					new Object[] { code, sa_id, datas[3], BaseUtil.getLocalMessage("ENTERING") });
			baseDao.execute("update saledetail set sd_prodid=(select pr_id from product where sd_prodcode=pr_code) where sd_said=" + sa_id);
		}
		return "产生销售订单,单号:<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?whoami=Sale&formCondition=sa_idIS" + sa_id
				+ "&gridCondition=sd_saidIS" + sa_id + "')\">" + code + "</a>&nbsp;";
	}

	@Transactional
	@Override
	public String Quote(int bc_id, String caller) {// 报价
		Object[] obj = baseDao.getFieldsDataByCondition("BusinessChance", "bc_statuscode,bc_from", "bc_id=" + bc_id);
		if (!obj[0].equals("AUDITED")) {
			BaseUtil.showError("审核后才能报价");
		}
		if("平台商机".equals(obj[1])){
			Map<Object, Object> map = new HashMap<Object, Object>();
			Object[] data = baseDao.getFieldsDataByCondition("BusinessChance", "bc_date13,bc_custcode,bc_custname,bc_desc10,bc_desc7,bc_desc9,bc_desc14,bc_desc15,bc_fromid", "bc_id=" + bc_id);
			if("已转平台询价".equals(data[7])){
				BaseUtil.showError("该内部商机已转平台报价");
			}
			map.put("bc_date13", data[0]);
			map.put("bc_custcode", data[1]);
			map.put("bc_custname", data[2]);
			map.put("bc_desc10", data[3]);
			map.put("bc_desc7", data[4]);
			map.put("bc_desc9", data[5]);
			map.put("bc_desc14", data[6]);
			map.put("busid", data[8]);
			//转报价单
			StringBuffer sb = new StringBuffer();
			sb.append("转入成功,B2B平台询价单号:");
			sb.append("<br>");
			map = TrunMapmping(map);
			int id = baseDao.getSeqId("QuotationDown_seq");
			String code = singleformitemsservice.getCodeString("QuotationDown", "QuotationDown", 2);
			map.put("qu_id", id);	//id
			map.put("qu_code", code);	//code
			String insertSql = SqlUtil.getInsertSqlByMap(map, "QuotationDown");
			baseDao.execute(insertSql);
			//记录跳转链接
			sb.append("<a href=\"javascript:openUrl('jsps/b2b/sale/quotationDown.jsp?formCondition=qu_idIS" + id
					+ "&gridCondition=qd_quidIS"+id+"&_config=CLOUD')\">" + code + "</a>&nbsp;<br>");
			//更新状态
			baseDao.updateByCondition("BusinessChance", "bc_desc15 = '已转平台询价'", "bc_id = " + bc_id);
			return sb.toString();
		}else{
			Object[] datas = baseDao.getFieldsDataByCondition("BusinessChance",
					"bc_recorder,bc_custcode,bc_custname,bc_model,bc_departmentcode,bc_department", "bc_id=" + bc_id);
			int qu_id = baseDao.getSeqId("Quotation_SEQ");
			String code = baseDao.sGetMaxNumber("Quotation", 2);
			String insertSql = "insert into Quotation ("
					+ "qu_code,qu_id,qu_date,qu_recorder,qu_status,qu_statuscode,qu_custcode,qu_custname，qu_departmentcode,qu_departmentname) values("
					+ "?,?,sysdate,?,?,'ENTERING',?,?,?,?)";
			baseDao.execute(insertSql, new Object[] { code, qu_id, datas[0], BaseUtil.getLocalMessage("ENTERING"), datas[1], datas[2],
					datas[4], datas[5] });
			if (StringUtil.hasText(datas[3])) {
				baseDao.execute(
						"insert into QuotationDetail(qd_id,qd_code,qd_quid,qd_detno,qd_prodcode,QD_STATUSCODE,QD_STATUS) values (QuotationDetail_seq.nextval,?,?,1,?,'ENTERING',?)",
						new Object[] { code, qu_id, datas[3], BaseUtil.getLocalMessage("ENTERING") });
				baseDao.execute("update QuotationDetail set qd_prodid=(select pr_id from product where qd_prodcode=pr_code) where qd_quid="
						+ qu_id);
			}
			return "产生报价单,单号:<a href=\"javascript:openUrl('jsps/scm/sale/quotation.jsp?whoami=Quotation&formCondition=qu_idIS" + qu_id
					+ "&gridCondition=qd_quidIS" + qu_id + "')\">" + code + "</a>&nbsp;";
		}
	}

	@Transactional
	@Override
	public String PlaceOrder(int bc_id, String caller) {// 下单
		Object status = baseDao.getFieldDataByCondition("BusinessChance", "bc_statuscode", "bc_id=" + bc_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError("审核后才能下单");
		}
		Object[] datas = baseDao.getFieldsDataByCondition("BusinessChance",
				"bc_recorder,bc_custcode,bc_custname,bc_model,bc_departmentcode,bc_department", "bc_id=" + bc_id);
		int sa_id = baseDao.getSeqId("Sale_SEQ");
		String code = baseDao.sGetMaxNumber("Sale", 2);
		String insertSql = "insert into Sale ("
				+ "sa_code,sa_id,sa_date,sa_recorder,sa_status,sa_statuscode,sa_custcode,sa_custname,sa_departmentcode,sa_departmentname) values("
				+ "?,?,sysdate,?,?,'ENTERING',?,?,?,?)";
		baseDao.execute(insertSql, new Object[] { code, sa_id, datas[0], BaseUtil.getLocalMessage("ENTERING"), datas[1], datas[2],
				datas[4], datas[5] });
		if (StringUtil.hasText(datas[3])) {
			baseDao.execute(
					"insert into saledetail(sd_id,sd_code,sd_said,sd_detno,sd_prodcode,sd_statuscode,sd_status) values (saledetail_seq.nextval,?,?,1,?,'ENTERING',?)",
					new Object[] { code, sa_id, datas[3], BaseUtil.getLocalMessage("ENTERING") });
			baseDao.execute("update saledetail set sd_prodid=(select pr_id from product where sd_prodcode=pr_code) where sd_said=" + sa_id);
		}
		return "产生销售订单,单号:<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?whoami=Sale&formCondition=sa_idIS" + sa_id
				+ "&gridCondition=sd_saidIS" + sa_id + "')\">" + code + "</a>&nbsp;";
	}

	@Transactional
	@Override
	public String Shipment(int bc_id, String caller) {// 出货
		Object status = baseDao.getFieldDataByCondition("BusinessChance", "bc_statuscode", "bc_id=" + bc_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError("审核后才能出货");
		}
		Object[] datas = baseDao.getFieldsDataByCondition("BusinessChance",
				"bc_recorder,bc_custcode,bc_custname,bc_model,bc_departmentcode,bc_department", "bc_id=" + bc_id);
		int pi_id = baseDao.getSeqId("ProdInOut_SEQ");
		String code = baseDao.sGetMaxNumber("ProdInOut", 2);
		String insertSql = "insert into ProdInOut ("
				+ "pi_class,pi_inoutno,pi_id,pi_recorddate,pi_recordman,pi_invostatus,pi_invostatuscode,pi_status,pi_statuscode,pi_cardcode,pi_title,pi_departmentcode,pi_departmentname) values("
				+ "'出货单',?,?,sysdate,?,?,'ENTERING',?,'UNPOST',?,?,?,?)";
		baseDao.execute(insertSql,
				new Object[] { code, pi_id, datas[0], BaseUtil.getLocalMessage("ENTERING"), BaseUtil.getLocalMessage("UNPOST"), datas[1],
						datas[2], datas[4], datas[5] });
		if (StringUtil.hasText(datas[3])) {
			baseDao.execute(
					"insert into ProdIODetail(pd_id,pd_inoutno,pd_piid,pd_pdno,pd_prodcode,pd_status) values (saledetail_seq.nextval,?,?,1,?,0)",
					new Object[] { code, pi_id, datas[3] });
			baseDao.execute("update ProdIODetail set pd_prodid=(select pr_id from product where pd_prodcode=pr_code) where pd_piid="
					+ pi_id);
		}
		return "产生出货单,单号:<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!Sale&formCondition=pi_idIS" + pi_id
				+ "&gridCondition=pd_piidIS" + pi_id + "')\">" + code + "</a>&nbsp;";
	}

	@Override
	public void endBusinessChance(int bc_id, String caller) {// 结案
		Object status = baseDao.getFieldDataByCondition("BusinessChance", "bc_statuscode", "bc_id=" + bc_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.end_onlyAudited"));
		}
		// 更新状态
		baseDao.updateByCondition("BusinessChance", "bc_statuscode='FINISH',bc_status='" + BaseUtil.getLocalMessage("FINISH") + "'",
				"bc_id=" + bc_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.end"), BaseUtil
				.getLocalMessage("msg.endSuccess"), "BusinessChance|bc_id=" + bc_id));
	}

	@Override
	public void resEndBusinessChance(int bc_id, String caller) {// 反结案
		// 只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("BusinessChance", "bc_statuscode", "bc_id=" + bc_id);
		if (!status.equals("FINISH")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resEnd_onlyEnd"));
		}
		// 更新状态
		baseDao.updateByCondition("BusinessChance", "bc_statuscode='AUDITED',bc_status='" + BaseUtil.getLocalMessage("AUDITED") + "'",
				"bc_id=" + bc_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.resEnd"), BaseUtil
				.getLocalMessage("msg.resEndSuccess"), "BusinessChance|bc_id=" + bc_id));
	}

	@Override
	public void callBack(String ids, String caller,String bcdids) {
		String [] stringArr= ids.split(","); 
		for(int i=0;i<stringArr.length;i++){
			
			String code=singleformitemsservice.getCodeString("BusinessChanceData", "BusinessChanceData", 2);
			String sql="Insert into BUSINESSCHANCEDATA (BCD_ID,BCD_BCID,BCD_MAN,BCD_DATE,BCD_REMARK,BCD_STATUS,"
					+ "BCD_STATUSCODE,BCD_BSCODE,BCD_BSNAME,BCD_CODE,BCD_COUNT,BCD_TYPE)  "
					+ "select BUSINESSCHANCEDATA_seq.nextval,bc_id,bc_doman,sysdate,'商机手动收回','已审核','AUDITED',"
					+ "bs_code,bc_currentprocess,'"+code+"',-1,'商机手动收回' from BUSINESSCHANCE "
					+ "LEFT JOIN BUSINESSCHANCESTAGE ON BC_CURRENTPROCESS=BS_NAME where bc_id in('"+stringArr[i]+"') "
					+ "and bc_doman is not null";
			baseDao.execute(sql);
			

			String sqlselect = "select  sourcecode from projecttask where taskorschedule='Schedule' and sourcecode=(select bc_code from BusinessChance where bc_id='"+stringArr[i]+"')"
					
					+ " and nvl(sourcecode,' ')<>' ' and nvl(handstatus,' ')<>'已完成' and to_char(startdate,'YYYYMMDD')>=TO_CHAR(SYSDATE, 'YYYYMMDD')";
			int count = baseDao.getCount(sqlselect);
			if (count > 0) {
				String delsql = "delete from projecttask where taskorschedule='Schedule' and sourcecode=(select bc_code from BusinessChance where bc_id='"+stringArr[i]+"')";
				baseDao.execute(delsql);
			}
			
			
		}
		
		if(!"".equals(bcdids)&&bcdids!=null&&!"0".equals(bcdids)){
			baseDao.execute("delete from businesschancedoman where bcd_id in ("+bcdids+")");
			baseDao.execute("update businesschance set (bc_doman,bc_domancode)=(select bcd_emname,bcd_emcode from businesschancedoman where bcd_bcid=bc_id and rownum=1) where bc_id in ("+ids+") and nvl(bc_domancode,' ') not in (select bcd_emcode from businesschancedoman where bcd_id=bc_id)"); //主表的跟进人被删则更新为下一个跟进人
			baseDao.updateByCondition("BusinessChance", "bc_doman=null,bc_domancode=null,bc_lastdate=null,bc_currentprocess=(select bs_name from businesschancestage where bs_detno=1)","bc_id in ("+ ids+") and bc_id not in (select bcd_bcid from businesschancedoman left join businesschance on bcd_bcid=bc_id)");	//如果跟进人明细为空则更新主表的跟进人为空	
		}else{
			baseDao.updateByCondition("BusinessChance", "bc_doman=null,bc_domancode=null,bc_lastdate=null,bc_currentprocess=(select bs_name from businesschancestage where bs_detno=1)","bc_id in ("+ ids+")");		
		}
		
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),"商机收回","商机收回", caller+"|bc_id=" + ids));
	}

	@Override
	public void busDistribute(String ids, String em_code, String em_name,String caller,String bcdids) {
		if(!"".equals(bcdids)&&bcdids!=null&&!"0".equals(bcdids)){
			List<String> sqls = new ArrayList<String>();
			sqls.add("insert into businesschancedoman(bcd_id,bcd_bcid,bcd_emname,bcd_emcode) select businesschancedoman_seq.nextval,bc_id,'"+em_name+"','"+em_code+"' from businesschance where bc_domancode is null and bc_id in("+ids+")");
			sqls.add("update businesschance set bc_doman='"+em_name+"',bc_domancode='"+em_code+"',bc_lastdate=sysdate where bc_domancode is null and bc_id in("+ids+")");
			sqls.add("update businesschance set bc_doman='"+em_name+"',bc_domancode='"+em_code+"' where bc_id in ("+ids+") and (bc_id,bc_domancode) in (select bcd_bcid,bcd_emcode from businesschancedoman where bcd_id in ("+bcdids+"))");
			sqls.add("update businesschancedoman set bcd_emname='"+em_name+"',bcd_emcode='"+em_code+"' where bcd_id in ("+bcdids+")");
			sqls.add("delete from businesschancedoman where bcd_id not in (select bcd_id from (select a.*,row_number() over(partition by bcd_bcid,bcd_emcode order by bcd_id desc) rn from businesschancedoman a where bcd_bcid in ("+ids+")) where rn=1) and bcd_id in ("+bcdids+")");
			baseDao.execute(sqls);
		}else{
			baseDao.updateByCondition("BusinessChance", "bc_doman='"+em_name+"',bc_domancode='"+em_code+"',bc_lastdate=sysdate","bc_id in ("+ ids+")");	
		}
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),"商机分配","商机分配", caller+"|bc_id=" + ids));
	}

	@Override
	public void transfer(String ids, String bd_name, String caller,String bcdids) {
		baseDao.updateByCondition("BusinessChance", "bc_nichehouse='"+bd_name+"'","bc_id in ("+ ids+")");
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),"商机转移","商机转移", caller+"|bc_id=" + ids));
	}

	@Override
	public void deleteBusinessDataBase(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { id });
		boolean b=baseDao.checkIf("BusinessChance", "bc_nichehouse=(select bd_name from BusinessDataBase where bd_id="+id+")");
		if(b)BaseUtil.showError("存在关联的商机,不允许删除!");
		baseDao.deleteById("BusinessDataBase", "bd_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "bd_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, id);		
	}
	
	@Override
	public Map<String,Object> getPointAndData(String parameters) {
		Map<String,Object> map = new HashMap<String,Object>();	
					
		if(parameters!=null){
			Map<Object, Object> recvMap = FlexJsonUtil.fromJson(parameters);
			if(recvMap.get("bs_code")==null||"".equals(recvMap.get("bs_code"))){
				map.put("bs_point",null);
				map.put("bs_pointflag", null);
			}else{
				Object[] bsStagePoint = baseDao.getFieldsDataByCondition("businesschancestage",new String[]{"bs_point","bs_pointflag","bs_pointdetno"}, "bs_code='"+recvMap.get("bs_code")+"'");
				if(bsStagePoint[0]!=null){
					map.put("bs_point",bsStagePoint[0].toString());
				}else{
					map.put("bs_point",null);
				}
				if(bsStagePoint[1]!=null){
					map.put("bs_pointflag",bsStagePoint[1].toString());
				}else{
					map.put("bs_pointflag",null);
				}
				if(bsStagePoint[2]!=null){
					map.put("bs_pointdetno",bsStagePoint[2].toString());
				}else{
					map.put("bs_pointdetno",null);
				}
			}

			
			String columnStr = "";
			if(recvMap.get("bcd_id")!=null&&!"".equals(recvMap.get("bcd_id"))){
				Object bsDetno = baseDao.getFieldDataByCondition("businesschancestage", "bs_pointdetno", "bs_code='"+recvMap.get("bs_code")+"'");
				if(bsDetno!=null){
					String[] dataDetnos = bsDetno.toString().split("#");
					
					for(int i=0;i<dataDetnos.length;i++){
						columnStr += "," + "bcd_column" + dataDetnos[i];
					}
					
					columnStr = columnStr.substring(1);
					
					String bcdSql = "select " + columnStr + " from businesschancedata where bcd_id=" + recvMap.get("bcd_id");
					List<Map<String,Object>> colData = baseDao.queryForList(bcdSql);
					for(Map<String,Object> colMap:colData){
						map.put("columndata", colMap);
					}
				}
			}else{
				map.put("columndata",null);
			}
		}

		
		return map;
	}
	
	//根据阶段编号取阶段要点
	@Override
	public Map<String,Object> getPoint(String parameters) {
		Map<String,Object> map = new HashMap<String,Object>();	
		
		if(parameters!=null){
			Map<Object, Object> recvMap = FlexJsonUtil.fromJson(parameters);
			if(recvMap.get("bs_code")==null||"".equals(recvMap.get("bs_code"))){
				map.put("bs_point",null);
				map.put("bs_pointflag", null);
				map.put("bs_pointdetno",null);
			}else{
				Object[] bsStagePoint = baseDao.getFieldsDataByCondition("businesschancestage",new String[]{"bs_point","bs_pointflag","bs_pointdetno"}, "bs_code='"+recvMap.get("bs_code")+"'");
				if(bsStagePoint[0]!=null){
					map.put("bs_point",bsStagePoint[0].toString());
				}else{
					map.put("bs_point",null);
				}
				if(bsStagePoint[1]!=null){
					map.put("bs_pointflag",bsStagePoint[1].toString());
				}else{
					map.put("bs_pointflag",null);
				}
				if(bsStagePoint[2]!=null){
					map.put("bs_pointdetno",bsStagePoint[2].toString());
				}else{
					map.put("bs_pointdetno",null);
				}
			}

		}
		return map;
	}
	public void isBusinesslimit(String bc_doman){
		Object BB_MAXRECVNUM = baseDao.getFieldDataByCondition("BUSINESSBASIS", "BB_MAXRECVNUM", "1=1");		
		int count=baseDao.getCountByCondition("businesschance","BC_DOMAN='"+bc_doman+"'and nvl(bc_status,' ')<>'已失效' and ((bc_custcode is not null and  bc_code not in(select cu_nichecode from customer where cu_auditstatuscode='AUDITED'))or bc_custcode is null)");	
		if(BB_MAXRECVNUM!=null){
			if (Integer.parseInt(BB_MAXRECVNUM.toString())<=count) {				
				BaseUtil.showError("已超过商机最大跟进数，不能抢占或分配或者创建");
			}
		}		
		
	}

	@Override
	public Map<String,Object> turnCustomer(int cu_id) {
		// TODO Auto-generated method stub
		Map<String, Object> model = new HashMap<String, Object>();
		handlerService.handler("BusinessChance", "transfers", "before",new Object[]{cu_id});
		Object currentprocess = baseDao.getFieldDataByCondition("BusinessChance", "bc_currentprocess", "bc_id='"+cu_id+"'");
		Object canturncust = baseDao.getFieldDataByCondition("BusinessChanceStage", "bs_canturncust", "bs_name='"+currentprocess+"'");
		if(canturncust!=null){
			if("0".equals(canturncust.toString())){
				BaseUtil.showError("当前阶段不允许转客户，请联系管理员!");
			}
		}
		boolean config = baseDao.isDBSetting("usePreCustomer");
		System.out.println(config);
		if (config) {
			Object count=baseDao.getFieldDataByCondition("PreCustomer", "count(1)", "cu_nichecode=(select bc_code from businesschance where bc_id='"+cu_id+"')");
			if(!count.toString().equals("0")){
				Object cu_code=baseDao.getFieldDataByCondition("PreCustomer", "cu_code", "cu_nichecode=(select bc_code from businesschance where bc_id='"+cu_id+"')");
				BaseUtil.showError("已转客户预录入，客户编号为:"+cu_code);
			}
			Key key = transferRepository.transfer("BusinessChanceToPreCustomer", cu_id);
			model.put("id", key.getId());	
		}else{			
			Object count=baseDao.getFieldDataByCondition("customer", "count(1)", "cu_nichecode=(select bc_code from businesschance where bc_id='"+cu_id+"')");
			if(!count.toString().equals("0")){
				Object cu_code=baseDao.getFieldDataByCondition("customer", "cu_code", "cu_nichecode=(select bc_code from businesschance where bc_id='"+cu_id+"')");
				BaseUtil.showError("已转正式客户，客户编号为:"+cu_code);
			}
			Key key =transferRepository.transfer("BusinessChanceToCustomer", cu_id);
			model.put("id", key.getId());	
		}
		model.put("config", config);
		return model;
	}
	@Override
	public void businessChanceLock(String ids, String caller) {
		baseDao.updateByCondition("BusinessChance", "BC_LOCKDATE=sysdate,BC_LOCKSTATUS='已冻结',BC_LOCKSTATUSCODE='LOCKED'", "BC_ID IN ("+ids+") AND NVL(BC_LOCKSTATUSCODE,' ')<>'LOCKED'");
	}
	@Override
	public void businessChanceRestart(String ids, String caller) {
		String [] stringArr= ids.split(","); 
		for(int i = 0 ; i < stringArr.length ; i++){
			boolean flag = baseDao.checkByCondition("BusinessChance", "BC_ID='"+stringArr[i]+"' AND NVL(BC_LOCKSTATUSCODE,' ')='LOCKED'");
			if(!flag){
				String[] fields = {"bc_description","bc_custname","bc_desc6","bc_domancode","bc_code"};
				Object[] limitData = baseDao.getFieldsDataByCondition("BusinessChance", fields, "BC_ID='"+stringArr[i]+"'");
				boolean a= baseDao.checkByCondition("BusinessChance", "(bc_description='"+limitData[0]+"' or (bc_custname is not null and bc_custname='"+limitData[1]+"') or (bc_desc6 is not null and bc_desc6='"+limitData[2]+"')) and NVL(bc_lockstatuscode,' ')<>'LOCKED' and bc_id<>'"+stringArr[i]+"'");		
				if(!a){
					BaseUtil.showError("商机编号["+limitData[4]+"]的商机名称或者企业名称或者营业执照相同,限制重启!");
				}else{
					if(!(limitData[3]==null||"".equals(limitData[3].toString().trim()))){					
						Object[] lockdate = baseDao.getFieldsDataByCondition("BusinessChance", "to_date(sysdate)-to_date(bc_lockdate),bc_domancode", "(bc_description='"+limitData[0]+"' and (bc_custname is not null and bc_custname='"+limitData[1]+"') or (bc_desc6 is not null and bc_desc6='"+limitData[2]+"')) and NVL(bc_lockstatuscode,' ')='LOCKED' and bc_domancode is not null and bc_id<>'"+stringArr[i]+"' order by bc_lockdate desc");
						if(lockdate!=null){
							if(lockdate[1]!=null){
								Object defaultoridOld = baseDao.getFieldDataByCondition("employee", "em_defaultorid", "em_code='"+lockdate[1]+"'");
								Object defaultoridNew = baseDao.getFieldDataByCondition("employee", "em_defaultorid", "em_code='"+limitData[3]+"'");
								if(defaultoridOld.toString().equals(defaultoridNew.toString())){
									Object orgrecovertime =  baseDao.getFieldDataByCondition("BusinessBasis", "bd_orgrecovertime", "1=1");
									if(Integer.parseInt(lockdate[0].toString())<=Integer.parseInt(orgrecovertime.toString())){
										BaseUtil.showError("商机编号["+limitData[4]+"]的商机名称或者企业名称或者营业执照相同或者不符合同一组织N天不允许跟进限制,限制重启!");
									}
								}
							}
						}
					}
				}				
			}
		}
		baseDao.updateByCondition("BusinessChance", "BC_LOCKDATE='',BC_LOCKSTATUS='',BC_LOCKSTATUSCODE=''", "BC_ID IN ("+ids+") AND NVL(BC_LOCKSTATUSCODE,' ')='LOCKED'");
	}
	
	@Override
	public Map<String, Object> getBBClist(String condition,int page,int pageSize) {
		Map<String, String> map = new HashMap<String, String>();
		String url = "https://api-inquiry.usoftmall.com/inquiry/public";
		Object en_uu = baseDao.getFieldDataByCondition("enterprise", "en_uu", "1=1");
		map.put("enUU", String.valueOf(en_uu));
		map.put("pageNumber", String.valueOf(page));
		map.put("pageSize", String.valueOf(pageSize));
		map.put("keyword", String.valueOf(condition));
		map.put("isAll", "0");
		try {
			if("10041559".equals(String.valueOf(en_uu))){//胜芳作为测试账套
				url = "http://218.17.158.219:24000/inquiry/public";
			}
			Response res = HttpUtil.sendGetRequest(url,map);
			if(res.getResponseText()!=null){
				Map<String, Object> resMap = new HashMap<String, Object>();
				Map<Object, Object> data = null;
				List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
				com.alibaba.fastjson.JSONObject jo = JSON.parseObject(res.getResponseText());
				com.alibaba.fastjson.JSONArray jar = jo.getJSONArray("content");
				for (Object object : jar) {
					data = new HashMap<Object, Object>();
					data.put("quoted",((com.alibaba.fastjson.JSONObject)object).get("quoted"));//是否已报价
					data.put("turnBusin",((com.alibaba.fastjson.JSONObject)object).get("allotStatus"));//是否转内部商机
					data.put("enUU",((com.alibaba.fastjson.JSONObject)((com.alibaba.fastjson.JSONObject)object).get("inquiry")).get("enUU"));//企业UU
					data.put("date",((com.alibaba.fastjson.JSONObject)object).get("date"));//发布日期
					data.put("inbrand",((com.alibaba.fastjson.JSONObject)object).get("inbrand"));//品牌
					data.put("prodTitle",((com.alibaba.fastjson.JSONObject)object).get("prodTitle"));//产品名称
					data.put("cmpCode",((com.alibaba.fastjson.JSONObject)object).get("cmpCode"));//产品型号
					data.put("spec",((com.alibaba.fastjson.JSONObject)object).get("spec"));//产品规格
					data.put("needquantity",((com.alibaba.fastjson.JSONObject)object).get("needquantity"));//数量/需求数量
					data.put("enName",((com.alibaba.fastjson.JSONObject)((com.alibaba.fastjson.JSONObject)object).get("inquiry")).get("enName"));//询价企业
					data.put("remainingTime",((com.alibaba.fastjson.JSONObject)object).get("remainingTime"));//报价截至日期
					data.put("busid",((com.alibaba.fastjson.JSONObject)object).get("id"));//商机ID
					data.put("recorder",((com.alibaba.fastjson.JSONObject)((com.alibaba.fastjson.JSONObject)object).get("inquiry")).get("recorder"));//联系人
					data.put("userTel",((com.alibaba.fastjson.JSONObject)object).get("userTel"));//电话
					data.put("endDate",((com.alibaba.fastjson.JSONObject)object).get("endDate"));//截止日期
					data.put("unit",((com.alibaba.fastjson.JSONObject)object).get("unit"));//单位   
					data.put("ship",((com.alibaba.fastjson.JSONObject)((com.alibaba.fastjson.JSONObject)object).get("inquiry")).get("ship"));//联系人
					list.add(data);
				}
				resMap.put("totalElements", jo.get("totalElements"));
				resMap.put("data", list);
				return resMap;
			}else{
				BaseUtil.showError("未查询到平台商机，请核对UU号");
			}
		} catch (Exception e) {
			BaseUtil.showError("未查询到平台商机，请核对UU号");
		}
		return null;
	}
	
	@Transactional
	public String chooseBusinessChance(String stores,String type){
		Employee employee = SystemSession.getUser();
		String url = "https://api-inquiry.usoftmall.com/inquiry/sale/allot/inquiry";
		Object en_uu = baseDao.getFieldDataByCondition("enterprise", "en_uu", "1=1");
		if(employee.getEm_uu()==null||employee.getEm_uu().equals("")){
			BaseUtil.showError("用户没有UU号");
		}
		StringBuffer sb = new StringBuffer();
		sb.append("转入成功,商机单号:");
		sb.append("<br>");
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(stores);
		for (Map<Object, Object> map : list) {
			String log = null;
			String code = singleformitemsservice.getCodeString("BusinessChance", "businesschance", 2);
			int id=singleformitemsservice.getId("BusinessChance_seq");
			map.put("bc_id", id);
			map.put("bc_code", code);
			map.put("bc_recorder", employee.getEm_name());
			//商机阶段
			String sql = "select count(bs_name) count,bs_name from businesschancestage where bs_detno=1 group by bs_name";
            List<Map<String, Object>> bsNameMap= baseDao.queryForList(sql);
            if(bsNameMap.size() == 0){
                String bct_code = baseDao.sGetMaxNumber("businesschancestage",2);
                baseDao.execute("insert into businesschancestage(bs_id,bs_code,bs_name,bs_color,bs_detno,bs_days) values(businesschancestage_seq.nextval,'"+bct_code+"','样品报价','55CC59',1,7)");
            }
            Object bs_name = baseDao.getFieldDataByCondition("businesschancestage", "bs_name", "bs_detno=1");
            map.put("bc_currentprocess", bs_name);
			String busid = String.valueOf(map.get("busid"));
			//bc_desc14 内的UU号 转 code
			String enUU = String.valueOf(map.get("bc_desc14"));
			Object cu_code = baseDao.getFieldDataByCondition("customer", "cu_code", "cu_uu = '"+enUU+"'");
			map.put("bc_custcode", cu_code);
			map.remove("busid");
			map.put("bc_fromid", busid);//来源ID
			//执行保存逻辑
			saveBusinessChance(BaseUtil.parseMap2Str(map),"BusinessChance");
			//记录跳转链接
			log = "<a href=\"javascript:openUrl('jsps/crm/chance/BusinessChance.jsp?formCondition=bc_idIS" + id
					+ "')\">" + code + "</a>&nbsp;<br>";
			sb.append(log);
			//回传平台
			Map<String, String> send = new HashMap<String, String>();
			send.put("userUU", String.valueOf(employee.getEm_uu()));
			send.put("enUU", String.valueOf(en_uu));
			send.put("itemId", busid);
			try {
				if("10041559".equals(String.valueOf(en_uu))){//胜芳作为测试账套
					url = "http://218.17.158.219:24000/inquiry/sale/allot/inquiry";
				}
				Response res = HttpUtil.sendPostRequest(url,send);
				if(res.getResponseText()!=null){
					Map<Object, Object> result = BaseUtil.parseFormStoreToMap(res.getResponseText());
					if(!result.get("success").equals(true)){
						BaseUtil.showError(String.valueOf(result.get("message")));
					}
				}else{
					BaseUtil.showError("回传平台商机失败，请重新分配");
				}
			} catch (Exception e) {
				BaseUtil.showError("未查询到平台商机，请核对UU号");
			}
			if(type=="2"){//修改商机状态 防止重复报价
				baseDao.updateByCondition("BusinessChance", "bc_desc15 = '已转平台询价'", "bc_id = " + id);
			}
		}
		sb.append("<hr>");
		return sb.toString();
	}
	
	@Transactional
	public String TrunQuotationDown(String formstore){
		StringBuffer sb = new StringBuffer();
		//转企业商机
		String bclog = chooseBusinessChance(formstore,"2");
		sb.append(bclog);
		sb.append("转入成功,B2B平台询价单号:");
		sb.append("<br>");
		//转报价单
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(formstore);
		for(Map<Object, Object> map : list){
			String log = null;
			map = TrunMapmping(map);
			int id = baseDao.getSeqId("QuotationDown_seq");
			String code = singleformitemsservice.getCodeString("QuotationDown", "QuotationDown", 2);
			map.put("qu_id", id);	//id
			map.put("qu_code", code);	//code
			String insertSql = SqlUtil.getInsertSqlByMap(map, "QuotationDown");
			baseDao.execute(insertSql);
			//记录跳转链接
			log = "<a href=\"javascript:openUrl('jsps/b2b/sale/quotationDown.jsp?formCondition=qu_idIS" + id
					+ "&gridCondition=qd_quidIS"+id+"&_config=CLOUD')\">" + code + "</a>&nbsp;<br>";
			sb.append(log);
		}
		sb.append("<hr>");
		return sb.toString();
	}
	
	private Map<Object, Object> TrunMapmping(Map<Object, Object> form){
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("qu_detno", 1);	//询价序号
		map.put("qu_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));		//询价日期
		map.put("qu_enddate", form.get("bc_date13"));		//报价有效期
		map.put("qu_custuu", form.get("bc_desc14"));			//客户UU
		//bc_custcode 内的UU号 转 code
		String enUU = String.valueOf(form.get("bc_desc14"));
		Object cu_code = baseDao.getFieldDataByCondition("customer", "cu_code", "cu_uu = '"+enUU+"'");
		map.put("qu_custcode", cu_code);		//客户编号
		map.put("qu_custname", form.get("bc_custname"));		//客户名称
		map.put("qu_status", "在录入");				//状态
		map.put("qu_custprodcode", form.get("bc_desc10"));	//客户物料号
		map.put("qu_custproddetail", form.get("bc_desc7"));	//客户物料名称
		map.put("qu_custprodspec", form.get("bc_desc9"));	//客户物料规格
		map.put("b2b_id_id", form.get("busid"));	//询价单ID
		map.put("qu_custlap", 0);					//是否分段报价
		map.put("qu_isreplace", 0);					//是否替代料报价
		map.put("qu_currency", "");					//币别
		map.put("qu_taxrate", "");					//税率(%)
		map.put("qu_statuscode", "ENTERING");		//状态码
		map.put("qu_source", "平台商机");		//询价来源
		return map;
	}

}
