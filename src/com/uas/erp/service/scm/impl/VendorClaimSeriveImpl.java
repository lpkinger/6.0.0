package com.uas.erp.service.scm.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Key;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.scm.VendorClaimService;
import com.uas.erp.service.scm.VendorPerformanceAssessService;

/**
 * 标准界面的基本逻辑
 */
@Service
public class VendorClaimSeriveImpl implements VendorClaimService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveVendorClaim(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("vc_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("VendorClaim", "vc_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 更新主表索赔金额字段
		Double num = (double) 0;
		for (Map<Object, Object> m : grid) {
			num+=Double.parseDouble(m.get("vcd_claimmny").toString());
		}
		store.put("vc_claimmny", num);
		if("0".equals(store.get("vc_negotiatemny").toString())){
			store.put("vc_negotiatemny", num);
		}
		// 保存VendorClaim
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VendorClaim", new String[] {},
				new Object[] {});
		baseDao.execute(formSql);
		// 保存VendorClaimDetail
		for (Map<Object, Object> m : grid) {
			m.put("vcd_id", baseDao.getSeqId("VEBDORCLAIMDETAIL_SEQ"));
			m.put("vcd_vcid", store.get("vc_id"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "VendorClaimDetail");
		baseDao.execute(gridSql);
		
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateVendorClaim(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });		
		// 修改VendorClaim
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VendorClaim", "vc_id");
		baseDao.execute(formSql);
		// 修改VendorClaimDetail
		boolean isupdate = false;
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "VendorClaimDetail", "vcd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("vcd_id") == null || s.get("vcd_id").equals("") || s.get("vcd_id").equals("0")
					|| Integer.parseInt(s.get("vcd_id").toString()) == 0) {// 新添加的数据，id不存在
				s.put("vcd_vcid", store.get("vc_id"));
				s.put("vcd_id", baseDao.getSeqId("VEBDORCLAIMDETAIL_SEQ"));
				String sql = SqlUtil.getInsertSqlByMap(s, "VendorClaimDetail");
				gridSql.add(sql);
			}
			if (s.get("vcd_claimmny") != null || !s.get("vcd_claimmny").equals("")
					|| !s.get("vcd_claimmny").equals("0")) {
				isupdate = true;
			}
		}
		baseDao.execute(gridSql);
		// 更新主表索赔金额字段		
		if (isupdate) {
			Double num = (double) 0;
			List<Object> objs = baseDao.getFieldDatasByCondition("VendorClaimDetail", "vcd_claimmny",
					"vcd_vcid=" + store.get("vc_id"));
			for (Object obj : objs) {
				num += Double.parseDouble(obj.toString());
			}
			baseDao.updateByCondition("VendorClaim", "vc_claimmny=" + num + ",vc_negotiatemny=" + num,
					"vc_id=" + store.get("vc_id"));
		}
		// 记录操作
		baseDao.logger.update(caller, "vc_id", store.get("vc_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteVendorClaim(String caller, int id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("VendorClaim", "vc_statuscode", "vc_id=" + id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		// 删除VendorClaim
		baseDao.deleteById("VendorClaim", "vc_id", id);
		// 删除VendorClaimDetail
		baseDao.deleteById("VendorClaimDetail", "vcd_vcid", id);
		baseDao.logger.delete(caller, "vc_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	}

	@Override
	public String auditVendorClaim(int id, String caller) {
		// 只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("VendorClaim", "vc_statuscode", "vc_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		baseDao.audit("VendorClaim", "vc_id=" + id, "vc_status", "vc_statuscode", "vc_auditdate",
				"vc_auditor");
		//供应商确认为否，自动转其它应付单
		String message = "";
		Object vendorverify = baseDao.getFieldDataByCondition("VendorClaim", "vc_vendorverify", "vc_id=" + id);
		if("否".equals(vendorverify.toString())){
			message+= "审批成功！审批后自动"+turnAPBillVendorClaim(caller,id);
		}
		// 记录操作
		baseDao.logger.audit(caller, "vc_id", id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
		if(message.length()==0&&message.equals("")){
			return null;
		}else{
			return message;
		}					
	}

	@Override
	public void resAuditVendorClaim(String caller, int id) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("VendorClaim", "vc_statuscode", "vc_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		//判断是否已转其它应付单
		Object vc_code=baseDao.getFieldDataByCondition("VendorClaim","vc_code","vc_id="+id);
		Object[] ob = baseDao.getFieldsDataByCondition("APBill",new String[] { "ab_id", "ab_code"},
				"nvl(ab_source,' ')='" + vc_code+"'");
		if(ob!=null){
			BaseUtil.showError("该索赔单已转入过其它应付单:<a href=\"javascript:openUrl('jsps/fa/ars/apbill.jsp?formCondition=ab_idIS" + ob[0]
					+ "&gridCondition=abd_abidIS" + ob[0] + "&whoami=APBill!OTDW')\">" + ob[1] + "</a> ,请先删除！&nbsp;<hr>");
		}
		// 执行反审核操作
		baseDao.resAudit("VendorClaim", "vc_id=" + id, "vc_status", "vc_statuscode", "vc_auditdate",
				"vc_auditor");
		baseDao.resOperate("VendorClaim", "vc_id=" + id, "vc_status", "vc_statuscode");
		//反审后回写转其它应付单字段
		baseDao.updateByCondition("VendorClaim", "vc_turnapbill='否'", "vc_id="+id);
		// 记录操作
		baseDao.logger.resAudit(caller, "vc_id", id);
	}

	@Override
	public void submitVendorClaim(String caller, int id) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("VendorClaim", "vc_statuscode", "vc_id=" + id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.submit("VendorClaim", "vc_id=" + id, "vc_status", "vc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "vc_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitVendorClaim(String caller, int id) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("VendorClaim", "vc_statuscode", "vc_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { id });
		// 执行反提交操作
		baseDao.resOperate("VendorClaim", "vc_id=" + id, "vc_status", "vc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "vc_id", id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { id });
	}

	@Override
	public String turnAPBillVendorClaim(String caller, int id) {
		Object vc_code=baseDao.getFieldDataByCondition("VendorClaim","vc_code","vc_id="+id);
		Object[] ob = baseDao.getFieldsDataByCondition("APBill",new String[] { "ab_id", "ab_code"},
				"nvl(ab_source,' ')='" + vc_code+"'");
		if(ob!=null){
			return "该索赔单已转入过其它应付单:<a href=\"javascript:openUrl('jsps/fa/ars/apbill.jsp?formCondition=ab_idIS" + ob[0]
					+ "&gridCondition=abd_abidIS" + ob[0] + "&whoami=APBill!OTDW')\">" + ob[1] + "</a>&nbsp;<hr>";
		}			
		//转单字段配置
		SqlRowList list = baseDao.queryForRowSet("select * from VendorClaim where vc_id=?", id);
		SqlMap map = null;
		String ab_code = baseDao.sGetMaxNumber("APBill!OTDW", 2);
		int ab_id = baseDao.getSeqId("APBILL_SEQ");
		int abd_id = baseDao.getSeqId("APBILLDETAIL_SEQ");
		String paymentcode = "";
		while (list.next()) {
			map = new SqlMap("APBill");
			map.set("ab_id", ab_id);
			map.set("ab_code", ab_code);
			map.set("ab_date", Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)));
			map.set("ab_yearmonth", DateUtil.currentDateString(Constant.ym));
			map.set("ab_vendcode", list.getObject("vc_vendorcode"));
			map.set("ab_vendname", list.getObject("vc_vendorname"));
			map.set("ab_currency", list.getObject("vc_currency"));
			map.set("ab_rate", list.getObject("vc_rate"));
			if(list.getObject("vc_purchaseman")==null){
				Object ve_buyername=baseDao.getFieldDataByCondition("Vendor","ve_buyername","ve_code='"+list.getObject("vc_vendorcode")+"'");
				map.set("ab_buyer", ve_buyername);
			}else{
				map.set("ab_buyer", list.getObject("vc_purchaseman"));
			}
			if(list.getObject("vc_payments")==null){
				Object[] objs=baseDao.getFieldsDataByCondition("Vendor",new String[] { "ve_paymentcode", "ve_payment"},"ve_code='"+list.getObject("vc_vendorcode")+"'");
				map.set("ab_payments", objs[1].toString());
				paymentcode = objs[0].toString();
			}else{
				map.set("ab_payments", list.getObject("vc_payments"));
				paymentcode = list.getObject("vc_paytype").toString();
			}									
			map.set("ab_paydate", list.getObject("pi_invoiceremark"));//根据日期和付款方式计算		
			map.set("ab_class", "其它应付单");
			map.set("ab_paystatus", "未付款");
			map.set("ab_recorder", list.getObject("vc_recorder"));
			map.set("ab_indate", Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)));
			map.set("ab_printstatus", "未打印");
			map.set("ab_printstatuscode", "UNPRINT");
			map.set("ab_status", "已过账");
			map.set("ab_statuscode", "POSTED");
			map.set("ab_apamount", list.getObject("vc_negotiatemny"));
			map.set("ab_payamount", 0);
			map.set("ab_kind", "货款调账");
			map.set("ab_source", vc_code);
			map.set("ab_sourceid", id);
			map.set("ab_sendstatus", "未上传");
			map.execute();
			//一条从表
			map = new SqlMap("APBillDetail");
			map.set("abd_detno", 1);
			map.set("abd_id", abd_id);
			map.set("abd_abid", ab_id);
			map.set("abd_qty", 1);
			map.set("abd_price", list.getObject("vc_negotiatemny"));
			map.set("abd_apamount", list.getObject("vc_negotiatemny"));
			map.execute();
		}
		//应付日期计算
		if(paymentcode!=""){
			Object ve_duedays = baseDao.getFieldDataByCondition("vendor", "nvl(ve_duedays,0)", "ve_code='" + list.getObject("vc_vendorcode") + "'");
			if ("".equals(ve_duedays) || "null".equals(ve_duedays)) {
				ve_duedays = 0;
			}
			String res = baseDao.callProcedure("SP_GETPAYDATE_VEND",
					new Object[] { Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), paymentcode, ve_duedays , list.getObject("vc_vendorcode").toString() });
			baseDao.updateByCondition("apbill", "ab_paydate='" + res + "'", " ab_id=" + ab_id);
		}
		//转单后回写转其它应付单字段
		baseDao.updateByCondition("VendorClaim", "vc_turnapbill='是'", "vc_id="+id);
		return "转入其它应付成功,其它应付单号:<a href=\"javascript:openUrl('jsps/fa/ars/apbill.jsp?formCondition=ab_idIS" + ab_id
					+ "&gridCondition=abd_abidIS" + ab_id + "&whoami=APBill!OTDW')\">" + ab_code + "</a>&nbsp;<hr>";
	}
}
