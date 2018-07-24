package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.PreVendorDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.scm.PreVendorService;
import com.uas.erp.service.scm.VendorService;

@Service("preVendorService")
public class PreVendorServiceImpl implements PreVendorService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private PreVendorDao preVendorDao;

	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private VendorService vendorService;

	/**
	 * 编号的记录已经存在
	 * 
	 * @param vCode
	 *            供应商号
	 * @param language
	 *            语言
	 * @throws RuntimeException
	 */
	private void checkCode(Object vId, Object vCode) {
		boolean bool = baseDao.checkByCondition("PreVendor", "ve_code='" + vCode + "' and ve_id<>" + vId);
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		} else {
			bool = baseDao.checkByCondition("Vendor", "ve_code='" + vCode + "'");
			if (!bool) {
				BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
			}
		}
	}

	/**
	 * 供应商全称重复
	 * 
	 * @param vCode
	 *            供应商号
	 * @param name
	 *            供应商全称
	 * @param language
	 *            语言
	 * @throws RuntimeException
	 */
	private String checkName(Object vCode, Object name, String caller, Object vid) {
		Object precode = baseDao.getFieldDataByCondition("PreVendor", "ve_code", "ve_id <> " + vid + " AND ve_name='" + name + "'");
		Object code = baseDao.getFieldDataByCondition("Vendor", "ve_code", "ve_code <> '" + vCode + "' AND ve_name='" + name + "'");
		if (precode != null) {
			if (baseDao.isDBSetting("Vendor", "allowNameRepeat"))
				return "供应商名称在供应商引进申请中已存在，申请单号：" + precode;
			else
				BaseUtil.showError("供应商名称在供应商引进申请中已存在，申请单号：" + precode);
		}
		if (code != null) {
			if (baseDao.isDBSetting("Vendor", "allowNameRepeat"))
				return "供应商名称在供应商资料中已存在，供应商号：" + code;
			else
				BaseUtil.showError("供应商名称在供应商资料中已存在，供应商号：" + code);
		}
		return null;
	}

	/**
	 * 供应商简称重复
	 * 
	 * @param vCode
	 *            供应商号
	 * @param name
	 *            供应商简称
	 * @param language
	 *            语言
	 * @throws RuntimeException
	 */
	private String checkShortName(Object vId, Object vCode, Object name, String caller) {
		Object precode = baseDao.getFieldDataByCondition("PreVendor", "ve_code", "ve_id <> " + vId + " AND ve_shortname='" + name
				+ "'");
		Object code = baseDao.getFieldDataByCondition("Vendor", "ve_code", "ve_code <> '" + vCode + "' AND ve_shortname='" + name + "'");
		if (precode != null) {
			if (baseDao.isDBSetting("Vendor", "allowShortNameRepeat"))
				return "供应商简称在供应商引进申请中已存在，申请单号：" + precode;
			else
				BaseUtil.showError("供应商简称在新供应商引进申请中已存在，申请单号：" + precode);
		}
		if (code != null) {
			if (baseDao.isDBSetting("Vendor", "allowShortNameRepeat"))
				return "供应商简称在新供应商资料中已存在，供应商号：" + code;
			else
				BaseUtil.showError("供应商简称在新供应商资料中已存在，供应商号：" + code);
		}
		return null;
	}

	@Override
	public void savePreVendor(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		// 需要同时检测PreVendor和Vendor
		checkCode(store.get("ve_id"), store.get("ve_code"));
		// 供应商全称重复
		String nameerror = checkName(store.get("ve_code"), store.get("ve_name"), "PreVendor", store.get("ve_id"));
		// 供应商简称重复
		String shortnameerror = checkShortName(store.get("ve_id"), store.get("ve_code"), store.get("ve_shortname"), "PreVendor");
		if (store.get("ve_apvendcode") == null || store.get("ve_apvendcode").equals("")) {
			store.put("ve_apvendcode", store.get("ve_code"));
			store.put("ve_apvendname", store.get("ve_name"));
		}
		checkapvend(store.get("ve_id"), store.get("ve_apvendcode"), store.get("ve_code"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave("PreVendor", new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PreVendor", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		defaultCurrencyRate("PreVendor", store.get("ve_id"));
		Object apvendname = baseDao.getFieldDataByCondition("Vendor", "nvl(ve_name,' ')", "ve_code='" + store.get("ve_apvendcode") + "'");
		baseDao.execute("update PreVendor set ve_apvendname='" + StringUtil.nvl(apvendname, " ")
				+ "' where ve_code<>ve_apvendcode and ve_id=" + store.get("ve_id"));
		baseDao.execute("update PreVendor set ve_apvendname=ve_name where ve_code=ve_apvendcode and ve_id=" + store.get("ve_id"));
		baseDao.logger.save("PreVendor", "ve_id", store.get("ve_id"));
		handlerService.afterSave("PreVendor", new Object[] { store });
		BaseUtil.showErrorOnSuccess(nameerror);
		BaseUtil.showErrorOnSuccess(shortnameerror);
	}

	public void checkapvend(Object ve_id, Object ve_apvendcode, Object code) {
		if (!ve_apvendcode.equals(code)) {
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(ve_code) from Vendor where ve_code=? and ve_auditstatuscode='AUDITED'", String.class, ve_apvendcode);
			if (dets == null) {
				BaseUtil.showError("应付供应商不存在或者状态不等于已审核!应付供应商号：" + ve_apvendcode);
			}
		}
	}

	@Override
	public void deletePreVendor(int ve_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PreVendor", "ve_auditstatuscode", "ve_id=" + ve_id);
		StateAssert.delOnlyEntering(status);
		baseDao.delCheck("PreVendor", ve_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel("PreVendor", ve_id);
		// 删除
		baseDao.deleteById("preVendor", "ve_id", ve_id);
		// 记录操作
		baseDao.logger.delete("PreVendor", "ve_id", ve_id);
		handlerService.afterDel("PreVendor", ve_id);
	}

	@Override
	public void updatePreVendorById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改未提交的申请单
		Object status = baseDao.getFieldDataByCondition("PreVendor", "ve_auditstatuscode", "ve_id=" + store.get("ve_id"));
		StateAssert.updateOnlyEntering(status);
		// 当前编号的记录已经存在,不能修改!
		// 需要同时检测PreVendor和Vendor
		checkCode(store.get("ve_id"), store.get("ve_code"));
		// 供应商全称重复
		String nameerror = checkName(store.get("ve_code"), store.get("ve_name"), "PreVendor", store.get("ve_id"));
		// 供应商简称重复
		String shortnameerror = checkShortName(store.get("ve_id"), store.get("ve_code"), store.get("ve_shortname"), "PreVendor");
		if (store.get("ve_apvendcode") == null || store.get("ve_apvendcode").equals("")) {
			store.put("ve_apvendcode", store.get("ve_code"));
			store.put("ve_apvendname", store.get("ve_name"));
		}
		checkapvend(store.get("ve_id"), store.get("ve_apvendcode"), store.get("ve_code"));
		// 执行修改前的其它逻辑
		handlerService.beforeSave("PreVendor", new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PreVendor", "ve_id");
		baseDao.execute(formSql);
		defaultCurrencyRate("PreVendor", store.get("ve_id"));
		Object apvendname = baseDao.getFieldDataByCondition("Vendor", "nvl(ve_name,' ')", "ve_code='" + store.get("ve_apvendcode") + "'");
		baseDao.execute("update PreVendor set ve_apvendname='" + StringUtil.nvl(apvendname, " ")
				+ "' where ve_code<>ve_apvendcode and ve_id=" + store.get("ve_id"));
		baseDao.execute("update PreVendor set ve_apvendname=ve_name where ve_code=ve_apvendcode and ve_id=" + store.get("ve_id"));
		// 记录操作
		baseDao.logger.update("PreVendor", "ve_id", store.get("ve_id"));
		handlerService.afterSave("PreVendor", new Object[] { store });
		BaseUtil.showErrorOnSuccess(nameerror);
		BaseUtil.showErrorOnSuccess(shortnameerror);
	}

	@Override
	public void auditPreVendor(int ve_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("PreVendor", new String[]{"ve_auditstatuscode","ve_uu"}, "ve_id=" + ve_id);
		StateAssert.auditOnlyCommited(status[0]);
		Object[] objs = baseDao.getFieldsDataByCondition("PreVendor", "ve_code,ve_name,ve_shortname,ve_apvendcode,ve_uu,ve_remark", "ve_id=" + ve_id);
		// 需要同时检测PreVendor和Vendor
		checkCode(ve_id, objs[0]);
		// 供应商全称重复
		String nameerror = checkName(objs[0], objs[1], "PreVendor",ve_id);
		// 供应商简称重复
		String shortnameerror = checkShortName(ve_id, objs[0], objs[2], "PreVendor");
		allowZeroTax("PreVendor", ve_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("PreVendor", ve_id);
		// 执行审核操作
		baseDao.audit("PreVendor", "ve_id=" + ve_id, "ve_auditstatus", "ve_auditstatuscode", "ve_auditdate", "ve_auditman");
		Object apvendname = baseDao.getFieldDataByCondition("Vendor", "nvl(ve_name,' ')", "ve_code='" + objs[3] + "'");
		baseDao.execute("update PreVendor set ve_apvendname='" + StringUtil.nvl(apvendname, " ")
				+ "' where ve_code<>ve_apvendcode and ve_id=" + ve_id);
		baseDao.execute("update PreVendor set ve_apvendname=ve_name where ve_code=ve_apvendcode and ve_id=" + ve_id);
		addVendorContact(ve_id);
		// maz  如果该供应商引起是公开询价 B2B产生，审核时更新所有该供应商UU在采购询价单明细中的物料编号 17-09-27
		if("来源于B2B".equals(objs[5])){
			baseDao.execute("update inquirydetail set id_vendcode='"+objs[0]+"' where id_venduu='"+objs[4]+"'");
		}
		if (!baseDao.isDBSetting("PreVendor", "noAutoVend")) {
			turnVendor(ve_id);
			Employee employee = SystemSession.getUser();
			if (baseDao.isDBSetting("PreVendor", "autoSync")) {
				Object custatus = baseDao.getFieldDataByCondition("Vendor", "ve_auditstatuscode", "ve_id=" + ve_id);
				if(custatus != null && "AUDITED".equals(custatus)){
					Master master = employee.getCurrentMaster();
					if (master != null && master.getMa_soncode() != null) {// 资料中心
						String res = null;
						res = baseDao.callProcedure("SYS_POST",
								new Object[] { "Vendor!Post", SpObserver.getSp(), master.getMa_soncode(), String.valueOf(ve_id), employee.getEm_name(), employee.getEm_id() });
						if (res != null) {
							BaseUtil.appendError(res);
						}
					}
				}
			}
		}
		//供应商引进审核时,自动更新uu号为该供应商引进uu号且ppd_vendcode为空的物料核价单明细行  maz  2018-03-09
		baseDao.execute("update purchasepricedetail a set (ppd_vendcode,ppd_vendname)=(select ve_code,ve_name from prevendor where ve_uu=a.ppd_venduu) where ppd_venduu='"+status[1]+"' and ppd_vendcode is null");
		// 记录操作
		baseDao.logger.audit("PreVendor", "ve_id", ve_id);
		handlerService.afterAudit("PreVendor", ve_id);
		BaseUtil.showErrorOnSuccess(nameerror);
		BaseUtil.showErrorOnSuccess(shortnameerror);
	}

	@Override
	public void resAuditPreVendor(int ve_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PreVendor", "ve_auditstatuscode", "ve_id=" + ve_id);
		StateAssert.resAuditOnlyAudit(status);
		Object veid = baseDao.getFieldDataByCondition("Vendor", "ve_id", "ve_sourceid=" + ve_id);
		if(veid != null && Integer.parseInt(veid.toString()) != 0){
			BaseUtil.showError("已转供应商，不允许反审核");;
		}
		baseDao.resAuditCheck("PreVendor", ve_id);
		handlerService.beforeResAudit("PreVendor", ve_id);
		// 执行反提交操作
		baseDao.resOperate("PreVendor", "ve_id=" + ve_id, "ve_auditstatus", "ve_auditstatuscode");
		// 记录操作
		baseDao.logger.resAudit("PreVendor", "ve_id", ve_id);
		handlerService.afterResAudit("PreVendor", ve_id);
	}

	@Override
	public void submitPreVendor(int ve_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PreVendor", "ve_auditstatuscode", "ve_id=" + ve_id);
		StateAssert.submitOnlyEntering(status);
		Object[] objs = baseDao.getFieldsDataByCondition("PreVendor", "ve_code,ve_name,ve_shortname,ve_apvendcode", "ve_id=" + ve_id);
		defaultCurrencyRate("PreVendor", ve_id);
		allowZeroTax("PreVendor", ve_id);
		// 当前编号的记录已经存在,不能提交!
		// 需要同时检测PreVendor和Vendor
		checkCode(ve_id, objs[0]);
		// 供应商全称重复
		String nameerror = checkName(objs[0], objs[1], "PreVendor",ve_id);
		// 供应商简称重复
		String shortnameerror = checkShortName(ve_id, objs[0], objs[2], "PreVendor");
		checkapvend(ve_id, objs[3], objs[0]);
		Object apvendname = baseDao.getFieldDataByCondition("Vendor", "nvl(ve_name,' ')", "ve_code='" + objs[3] + "'");
		baseDao.execute("update PreVendor set ve_apvendname='" + StringUtil.nvl(apvendname, " ")
				+ "' where ve_code<>ve_apvendcode and ve_id=" + ve_id);
		baseDao.execute("update PreVendor set ve_apvendname=ve_name where ve_code=ve_apvendcode and ve_id=" + ve_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit("PreVendor", ve_id);
		// 执行反提交操作
		baseDao.submit("PreVendor", "ve_id=" + ve_id, "ve_auditstatus", "ve_auditstatuscode");
		// 记录操作
		baseDao.logger.submit("PreVendor", "ve_id", ve_id);
		handlerService.afterSubmit("PreVendor", ve_id);
		BaseUtil.showErrorOnSuccess(nameerror);
		BaseUtil.showErrorOnSuccess(shortnameerror);
	}

	@Override
	public void resSubmitPreVendor(int ve_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PreVendor", "ve_auditstatuscode", "ve_id=" + ve_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("PreVendor", ve_id);
		// 执行反提交操作
		baseDao.resOperate("PreVendor", "ve_id=" + ve_id, "ve_auditstatus", "ve_auditstatuscode");
		// 记录操作
		baseDao.logger.resSubmit("PreVendor", "ve_id", ve_id);
		handlerService.afterResSubmit("PreVendor", ve_id);
	}

	/**
	 * 转正式供应商
	 */
	@Override
	public int turnVendor(int ve_id) {
		int veid = 0;
		// 判断该供应商申请单是否已经转入过供应商
		Object code = baseDao.getFieldDataByCondition("prevendor", "ve_code", "ve_id=" + ve_id);
		code = baseDao.getFieldDataByCondition("Vendor", "ve_code", "ve_sourcecode='" + code + "'");
		if (StringUtil.hasText(code)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.prevendor.haveturn") + code);
		} else {
			// 转供应商
			veid = preVendorDao.turnVendor(ve_id);
			baseDao.updateByCondition("PreVendor", "ve_turnstatuscode='TURNFM',ve_turnstatus='" + BaseUtil.getLocalMessage("TURNFM") + "'",
					"ve_id=" + ve_id);
		}
		// 更新供应商银行资料
				int count = 0;
				Object[] bank = baseDao.getFieldsDataByCondition("Vendor", new String[] { "ve_bank", "ve_bankaccount" , "ve_contact" , "ve_currency" , "ve_taxrate" , "ve_bankman" , "ve_bankaddress" , "ve_code" }, "ve_id="
						+ veid);
				if(bank[0] !=null && bank[1] != null){
					count = baseDao.getCount("select count(*) from VePaymentsDetail where vpd_veid=" + veid + " and vpd_bankaccount='" + bank[1]
							+ "'");	
					if(count==0){
							count = baseDao.getCount("select count(*) from VePaymentsDetail where vpd_remark='是' and  vpd_veid=" + veid);
								if(count==0){
									Object maxdetno = baseDao.getFieldDataByCondition("VePaymentsDetail", "max(vpd_detno)", "vpd_veid=" + veid);
									count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
									baseDao.execute(
											"Insert into VePaymentsDetail(vpd_id, vpd_detno, vpd_veid, vpd_bank, vpd_bankaccount, vpd_contact,vpd_currency,vpd_taxrate,vpd_bankman,vpd_bankaddress,vpd_remark , vpd_vecode) values (?,?,?,?,?,?,?,?,?,?,?,?)",
											new Object[] { baseDao.getSeqId("VEPAYMENTSDETAIL_SEQ"),  count+1, veid, bank[0] , bank[1] , bank[2] , bank[3] , bank[4] , bank[5] , bank[6] , "是" ,bank[7]  });
								}else{
									baseDao.updateByCondition("VePaymentsDetail", "vpd_remark='否'", "vpd_remark='是' and vpd_veid=" + veid);
									Object maxdetno = baseDao.getFieldDataByCondition("VePaymentsDetail", "max(vpd_detno)", "vpd_veid=" + veid);
									count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
									baseDao.execute(
											"Insert into VePaymentsDetail(vpd_id, vpd_detno, vpd_veid, vpd_bank, vpd_bankaccount, vpd_contact,vpd_currency,vpd_taxrate,vpd_bankman,vpd_bankaddress,vpd_remark) values (?,?,?,?,?,?,?,?,?,?,?,?)",
											new Object[] { baseDao.getSeqId("VEPAYMENTSDETAIL_SEQ"),  count+1, veid, bank[0] , bank[1] , bank[2] , bank[3] , bank[4] , bank[5] , bank[6] , "是" , bank[7] });
								}
						}
				}
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "转供应商操作","转入成功","PreVendor|ve_id="+ ve_id));
		return veid;
	}

	/**
	 * 转供应商基本资料
	 */
	@Override
	public int turnVendorBase(int ve_id) {
		int veid = 0;
		// 判断该供应商申请单是否已经转入过供应商
		Object code = baseDao.getFieldDataByCondition("Vendor", "ve_code", "ve_sourceid=" + ve_id);
		if (StringUtil.hasText(code)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.prevendor.haveturn")
					+ "<a href=\"javascript:openUrl('jsps/scm/purchase/vendor.jsp?formCondition=ve_codeIS" + code + "')\">" + code
					+ "</a>&nbsp;");
		} else {
			// 转供应商
			veid = preVendorDao.turnVendor(ve_id);
			baseDao.updateByCondition("Vendor",
					"ve_auditstatuscode='ENTERING',ve_auditstatus='" + BaseUtil.getLocalMessage("ENTERING") + "'", "ve_id=" + veid);
			baseDao.updateByCondition("PreVendor", "ve_turnstatuscode='TURNTP',ve_turnstatus='" + BaseUtil.getLocalMessage("TURNTP") + "'",
					"ve_id=" + ve_id);
		}
		return veid;
	}

	// 本位币允许税率为0
	private void allowZeroTax(String caller, Object ve_id) {
		if (!baseDao.isDBSetting("Purchase", "allowZeroTax")) {
			String currency = baseDao.getDBSetting("defaultCurrency");
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(ve_currency) from prevendor where nvl(ve_taxrate,0)=0 and ve_currency='" + currency + "' and ve_id=?",
					String.class, ve_id);
			if (dets != null) {
				BaseUtil.showError("本位币税率为0，不允许进行当前操作!币别：" + dets);
			}
		}
	}

	// 税率强制等于币别表的默认税率
	private void defaultCurrencyRate(String caller, Object ve_id) {
		if (baseDao.isDBSetting("PreVendor", "defaultCurrencyRate")) {
			baseDao.execute("update prevendor set ve_taxrate=(select nvl(cr_taxrate,0) from currencys where ve_currency=cr_name and cr_statuscode='CANUSE')"
					+ " where ve_id=" + ve_id);
		}
	}
	// 审核后联系人插入联系人资料
	private void addVendorContact(Object ve_id) {
		int count = baseDao.getCount("select * from vendorcontact left join Prevendor on vc_veid=ve_id where vc_veid=" + ve_id
				+ " and vc_name=ve_contact and nvl(ve_contact,' ')<>' '");
		if (count == 0) {
			int id = baseDao.getSeqId("VendorContact_seq");
			baseDao.execute("insert into vendorcontact(vc_name,vc_job,vc_mobile,vc_officeemail,vc_id,vc_veid,vc_vecode,vc_isvendor) select ve_contact,ve_degree,ve_mobile,ve_email,"
					+ id + ",ve_id,ve_code,-1 from prevendor where ve_id=" + ve_id);
			baseDao.execute("update VendorContact set vc_isvendor=0 where vc_id<>" + id + " and vc_veid=" + ve_id);
		}
	}
}
