package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.b2b.model.Enterprise;
import com.uas.b2b.model.VendorInfo;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VendorDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.ma.EnterpriseService;
import com.uas.erp.service.oa.SendMailService;
import com.uas.erp.service.scm.VendorService;

@Service("vendorService")
public class VendorServiceImpl implements VendorService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VendorDao vendorDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private SendMailService sendMailService;

	@Override
	public void saveVendor(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object vecode = store.get("ve_code").toString().trim();
		Object vename = store.get("ve_name").toString().trim();
		store.put("ve_code", vecode);
		store.put("ve_name", vename);
		// 当前编号的供应商资料已经存在,不能新增
		boolean bool = baseDao.checkByCondition("Vendor", "ve_code='" + vecode + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.vendor.save_codeHasExist"));
		}
		Object veid = store.get("ve_id");
		checkVendCode(veid, vecode, store.get("ve_sourceid"));
		String nameerror = checkName(vecode, store.get("ve_name"), caller);
		String shortnameerror = checkShortName(vecode, store.get("ve_shortname"), caller);

		// 如果没有开通b2b,则启用B2B对账或启用B2B收料均为否
		if (store.get("ve_b2benable") != null && store.get("ve_b2benable").equals("0")) {
			if ((store.get("ve_ifdeliveryonb2b") != null && !store.get("ve_ifdeliveryonb2b").equals("0"))
					|| (store.get("ve_b2bcheck") != null && !store.get("ve_b2bcheck").equals("0")))
				BaseUtil.showError("当前供应商未开通B2B，不允许启用B2B收料和B2B对账！");
		}
		// 执行保存操作
		if (store.get("ve_apvendcode") == null || store.get("ve_apvendcode").equals("")) {
			store.put("ve_apvendcode", vecode);
			store.put("ve_apvendname", vename);
		}
		checkapvend(veid, store.get("ve_apvendcode"), vecode);
		// 执行保存前的其它逻辑
		handlerService.handler("Vendor", "save", "before", new Object[] { store });
		String formSql = SqlUtil.getInsertSqlByMap(store, "Vendor");
		baseDao.execute(formSql);
		Object apvendname = baseDao.getFieldDataByCondition("Vendor", "nvl(ve_name,' ')", "ve_code='" + store.get("ve_apvendcode") + "'");
		baseDao.execute("update Vendor set ve_apvendname='" + StringUtil.nvl(apvendname, " ") + "' where ve_code<>ve_apvendcode and ve_id="
				+ veid);
		baseDao.execute("update Vendor set ve_apvendname=ve_name where ve_code=ve_apvendcode and ve_id=" + veid);
		defaultCurrencyRate(caller, store.get("ve_id"));
		// 记录操作
		baseDao.logger.save(caller, "ve_id", veid);
		// 执行保存后的其它逻辑
		handlerService.handler("Vendor", "save", "after", new Object[] { store });
		BaseUtil.showErrorOnSuccess(nameerror);
		BaseUtil.showErrorOnSuccess(shortnameerror);
	}

	public int saveVendorSimple(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);

		String ve_code = baseDao.sGetMaxNumber("VENDOR", 2);
		int ve_id = baseDao.getSeqId("VENDOR_SEQ");
		Object ve_name = store.get("ve_name");

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("ve_code", ve_code);
		map.put("ve_id", ve_id);
		map.put("ve_name", ve_name);

		// 保存sale
		String formSql = SqlUtil.getInsertSqlByMap(map, "vendor");
		baseDao.execute(formSql);

		return ve_id;
	}

	@Override
	public boolean checkVendorByEnId(int cu_enid, int cu_otherenid) {
		return vendorDao.checkVendorByEnId(cu_enid, cu_otherenid);
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

	private void checkVendCode(Object ve_id, Object vecode, Object sourceid) {
		// 判断供应商编号在供应商资料中是否存在重复
		String dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(ve_code) from Vendor where ve_code=? and ve_id<>?",
				String.class, vecode, ve_id);
		if (dets != null) {
			BaseUtil.showError("供应商编号在供应商资料表中已存在!供应商编号：" + dets);
		}
		// 判断供应商编号在新供应商引进资料中是否存在重复
		dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(ve_code) from PreVendor where ve_code=? and ve_id<>?",
				String.class, vecode, sourceid);
		if (dets != null) {
			BaseUtil.showError("供应商编号在供应商引进申请中已存在!供应商编号：" + dets);
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
	private String checkName(Object vCode, Object name, String caller) {
		Object precode = baseDao.getFieldDataByCondition("PreVendor", "ve_code", "ve_code <> '" + vCode + "' AND ve_name='" + name + "'");
		Object code = baseDao.getFieldDataByCondition("Vendor", "ve_code", "ve_code <> '" + vCode + "' AND ve_name='" + name + "'");
		if (precode != null) {
			if (baseDao.isDBSetting(caller, "allowNameRepeat"))
				return "供应商名称在供应商引进申请中已存在，申请单号：" + precode;
			else
				BaseUtil.showError("供应商名称在供应商引进申请中已存在，申请单号：" + precode);
		}
		if (code != null) {
			if (baseDao.isDBSetting(caller, "allowNameRepeat"))
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
	private String checkShortName(Object vCode, Object name, String caller) {
		Object precode = baseDao.getFieldDataByCondition("PreVendor", "ve_code", "ve_code <> '" + vCode + "' AND ve_shortname='" + name
				+ "'");
		Object code = baseDao.getFieldDataByCondition("Vendor", "ve_code", "ve_code <> '" + vCode + "' AND ve_shortname='" + name + "'");
		if (precode != null) {
			if (baseDao.isDBSetting(caller, "allowShortNameRepeat"))
				return "供应商简称在供应商引进申请中已存在，申请单号：" + precode;
			else
				BaseUtil.showError("供应商简称在新供应商引进申请中已存在，申请单号：" + precode);
		}
		if (code != null) {
			if (baseDao.isDBSetting(caller, "allowShortNameRepeat"))
				return "供应商简称在新供应商资料中已存在，供应商号：" + code;
			else
				BaseUtil.showError("供应商简称在新供应商资料中已存在，供应商号：" + code);
		}
		return null;
	}

	@Override
	public void updateVendor(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的供应商资料!
		Object status = baseDao.getFieldDataByCondition("Vendor", "ve_auditstatuscode", "ve_id=" + store.get("ve_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.vendor.update_onlyEntering"));
		}
		Object vecode = store.get("ve_code").toString().trim();
		Object vename = store.get("ve_name").toString().trim();
		store.put("ve_code", vecode);
		store.put("ve_name", vename);
		// 如果没有开通b2b,则启用B2B对账或启用B2B收料均为否
		if (store.get("ve_b2benable") != null && store.get("ve_b2benable").equals("0")) {
			if ((store.get("ve_ifdeliveryonb2b") != null && !store.get("ve_ifdeliveryonb2b").equals("0"))
					|| (store.get("ve_b2bcheck") != null && !store.get("ve_b2bcheck").equals("0")))
				BaseUtil.showError("当前供应商未开通B2B，不允许启用B2B收料和B2B对账！");
		}

		checkVendCode(store.get("ve_id"), vecode, store.get("ve_sourceid"));
		// 执行修改前的其它逻辑
		handlerService.handler("Vendor", "save", "before", new Object[] { store });
		// 执行修改操作
		if (store.get("ve_apvendcode") == null || store.get("ve_apvendcode").equals("")) {
			store.put("ve_apvendcode", vecode);
			store.put("ve_apvendname", vename);
		}
		String nameerror = checkName(vecode, vename, caller);
		String shortnameerror = checkShortName(vecode, store.get("ve_shortname"), caller);
		checkapvend(store.get("ve_id"), store.get("ve_apvendcode"), vecode);
		String sql = SqlUtil.getUpdateSqlByFormStore(store, "Vendor", "ve_id");
		baseDao.execute(sql);
		Object apvendname = baseDao.getFieldDataByCondition("Vendor", "nvl(ve_name,' ')", "ve_code='" + store.get("ve_apvendcode") + "'");
		baseDao.execute("update Vendor set ve_apvendname='" + StringUtil.nvl(apvendname, " ") + "' where ve_code<>ve_apvendcode and ve_id="
				+ store.get("ve_id"));
		baseDao.execute("update Vendor set ve_apvendname=ve_name where ve_code=ve_apvendcode and ve_id=" + store.get("ve_id"));
		defaultCurrencyRate(caller, store.get("ve_id"));
		// 记录操作
		baseDao.logger.update(caller, "ve_id", store.get("ve_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("Vendor", "save", "after", new Object[] { store });
		BaseUtil.showErrorOnSuccess(nameerror);
		BaseUtil.showErrorOnSuccess(shortnameerror);
	}

	@Override
	public void deleteVendor(int ve_id, String caller) {
		// 只能删除[在录入]的供应商资料
		Object[] status = baseDao
				.getFieldsDataByCondition("Vendor", new String[] { "ve_auditstatuscode", "ve_sourceid" }, "ve_id=" + ve_id);
		StateAssert.delOnlyEntering(status[0]);
		// 是否已产生业务数据
		baseDao.delCheck("vendor", ve_id);
		// 执行删除前的其它逻辑
		handlerService.handler("Vendor", "delete", "before", new Object[] { ve_id });
		baseDao.updateByCondition("PreVendor", "ve_turnstatuscode='UNTURN',ve_turnstatus='未转正式'", "ve_id=" + status[1]);
		baseDao.updateByCondition("PreVendor", "ve_auditstatuscode='ENTERING',ve_auditstatus='在录入'", "ve_id=" + status[1]);
		VendorStatus(caller, status[1]);
		// 执行删除操作
		baseDao.deleteById("Vendor", "ve_id", ve_id);
		/**
		 * @author wsy 反馈编号：2017040426 供应商资料反审核后删除该单时，同时删除供应商银行资料。
		 */
		baseDao.deleteById("VePaymentsDetail", "VPD_VEID", ve_id);
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			String groupName = BaseUtil.getXmlSetting("defaultSob");
			String allMasters = baseDao.getJdbcTemplate().queryForObject("select ma_soncode from " + groupName + ".master where ma_name=?",
					String.class, groupName);
			baseDao.execute("declare v_masters varchar2(1000);v_array str_table_type;v_m varchar2(30);v_sql varchar2(200);begin v_masters := '"
					+ allMasters
					+ "';v_array := parsestring(v_masters,',');for i in v_array.first()..v_array.last() loop v_m := v_array(i);v_sql := 'update '||v_m||'.Vendor set ve_auditstatuscode=''DISABLE'',ve_auditstatus=''已禁用'' where ve_id="
					+ ve_id + "';execute immediate v_sql;end loop;COMMIT;end;");
		}
		// 记录操作
		baseDao.logger.delete(caller, "ve_id", ve_id);
		// 执行删除后的其它逻辑
		handlerService.handler("Vendor", "delete", "after", new Object[] { ve_id });
	}

	@Override
	public void auditVendor(int ve_id, String caller) {
		// 只能审核[已提交]的供应商
		Object[] status = baseDao.getFieldsDataByCondition("Vendor", new String[] { "ve_auditstatuscode", "ve_code", "ve_name",
				"ve_shortname", "ve_sourceid" }, "ve_id=" + ve_id);
		StateAssert.auditOnlyCommited(status[0]);
		checkVendCode(ve_id, status[1], status[4]);
		// 应付供应商不能为空
		Object apvendcode = baseDao.getFieldDataByCondition("Vendor", "ve_apvendcode", "ve_id=" + ve_id);
		if (apvendcode == null || apvendcode.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.vendor.audit_apvendcode") + apvendcode);
		}
		String nameerror = checkName(status[1], status[2], caller);
		String shortnameerror = checkShortName(status[1], status[3], caller);
		checkapvend(ve_id, apvendcode, status[1]);
		allowZeroTax(caller, ve_id);
		// 执行审核前的其它逻辑
		handlerService.handler("Vendor", "audit", "before", new Object[] { ve_id });
		Employee employee = SystemSession.getUser();
		// 执行审核操作
		baseDao.audit("Vendor", "ve_id=" + ve_id, "ve_auditstatus", "ve_auditstatuscode", "ve_auditdate", "ve_auditman");
		addVendorContact(ve_id);
		// 集团帐套将供应商资料反写到DataCenter
		String isGroup = BaseUtil.getXmlSetting("group");
		String dataSob = BaseUtil.getXmlSetting("dataSob");
		if ("true".equals(isGroup) && dataSob != null && !employee.getCurrentMaster().getMa_name().equals(dataSob)) {
			String str = baseDao.callProcedure("sys_post", "Vendor!Post", employee.getCurrentMaster().getMa_name(), dataSob,
					String.valueOf(ve_id), employee.getEm_name(), String.valueOf(employee.getEm_id()));
			if (str != null && !"".equals(str)) {
				BaseUtil.showError(str);
			}
		}
		// 记录操作
		baseDao.logger.audit(caller, "ve_id", ve_id);
		// 执行审核后的其它逻辑
		handlerService.handler("Vendor", "audit", "after", new Object[] { ve_id });
		//将【启用B2B对账】上传到B2B
		Master master = SystemSession.getUser().getCurrentMaster();
		List<VendorInfo> vends =new ArrayList<VendorInfo>();
		Object[] ob=baseDao.getFieldsDataByCondition("vendor",  new String[] { "ve_uu", "nvl(ve_b2bcheck,0)" }, "ve_id="+ve_id);
		if(ob[0]!=null && !"".equals(ob[0]) && master.getMa_uu()!=null && !"".equals(master.getMa_uu())){
			VendorInfo vend=new VendorInfo();
			vend.setVenduu(Long.parseLong(ob[0].toString()));
			vend.setCustuu(Long.parseLong(master.getMa_uu().toString()));
			vend.setApcheck(Short.parseShort(ob[1].toString()));
			vends.add(vend);
		}
		if(vends.size()>0){
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArray(vends));
			try {
					HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/vendor/apcheck?access_id=" + master.getMa_uu(),
							params, true,master.getMa_accesssecret());
			} catch (Exception e) {

			}
		}		
		BaseUtil.showErrorOnSuccess(nameerror);
		BaseUtil.showErrorOnSuccess(shortnameerror);
	}

	@Override
	public void resAuditVendor(int ve_id, String caller) {
		// 只能反审核[已审核]的供应商
		Object status = baseDao.getFieldDataByCondition("Vendor", "ve_auditstatuscode", "ve_id=" + ve_id);
		StateAssert.resAuditOnlyAudit(status);
		// 是否已产生业务数据
		baseDao.resAuditCheck("vendor", ve_id);
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ve_id);
		// 执行反审核操作
		baseDao.resAudit("Vendor", "ve_id=" + ve_id, "ve_auditstatus", "ve_auditstatuscode", "ve_auditdate", "ve_auditman");
		// maz 反审核后 删除对应的供应商联系人数据  2017080728
		baseDao.execute("delete VENDORCONTACT where vc_veid="+ve_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ve_id", ve_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ve_id);
	}

	@Override
	public void submitVendor(int ve_id, String caller) {
		// 只能提交[在录入]的供应商
		Object[] status = baseDao.getFieldsDataByCondition("Vendor", new String[] { "ve_auditstatuscode", "ve_code", "ve_name",
				"ve_shortname", "ve_sourceid", "ve_apvendcode" }, "ve_id=" + ve_id);
		StateAssert.submitOnlyEntering(status[0]);
		checkVendCode(ve_id, status[1], status[4]);
		// 应付供应商不能为空
		Object apvendcode = baseDao.getFieldDataByCondition("Vendor", "ve_apvendcode", "ve_id=" + ve_id);
		if (apvendcode == null || apvendcode.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.vendor.submit_apvendcode") + apvendcode);
		}
		String nameerror = checkName(status[1], status[2], caller);
		String shortnameerror = checkShortName(status[1], status[3], caller);
		checkapvend(ve_id, apvendcode, status[1]);
		Object apvendname = baseDao.getFieldDataByCondition("Vendor", "nvl(ve_name,' ')", "ve_code='" + status[5] + "'");
		baseDao.execute("update Vendor set ve_apvendname='" + StringUtil.nvl(apvendname, " ") + "' where ve_code<>ve_apvendcode and ve_id="
				+ ve_id);
		baseDao.execute("update Vendor set ve_apvendname=ve_name where ve_code=ve_apvendcode and ve_id=" + ve_id);
		defaultCurrencyRate(caller, ve_id);
		allowZeroTax(caller, ve_id);
		// 执行提交前的其它逻辑
		handlerService.handler("Vendor", "commit", "before", new Object[] { ve_id });
		// 执行提交操作
		baseDao.submit("Vendor", "ve_id=" + ve_id, "ve_auditstatus", "ve_auditstatuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ve_id", ve_id);
		// 更新供应商银行资料
		int count = 0;
		Object[] bank = baseDao.getFieldsDataByCondition("Vendor", new String[] { "ve_bank", "ve_bankaccount", "ve_contact", "ve_currency",
				"ve_taxrate", "ve_bankman", "ve_bankaddress", "ve_code" }, "ve_id=" + ve_id);
		if (bank[0] != null && bank[1] != null) {
			count = baseDao.getCount("select count(*) from VePaymentsDetail where vpd_veid=" + ve_id + " and vpd_bankaccount='" + bank[1]
					+ "'");
			if (count == 0) {
				count = baseDao.getCount("select count(*) from VePaymentsDetail where vpd_remark='是' and  vpd_veid=" + ve_id);
				if (count == 0) {
					Object maxdetno = baseDao.getFieldDataByCondition("VePaymentsDetail", "max(vpd_detno)", "vpd_veid=" + ve_id);
					count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
					baseDao.execute(
							"Insert into VePaymentsDetail(vpd_id, vpd_detno, vpd_veid, vpd_bank, vpd_bankaccount, vpd_contact,vpd_currency,vpd_taxrate,vpd_bankman,vpd_bankaddress ,vpd_vecode ,vpd_remark) values (?,?,?,?,?,?,?,?,?,?,?,?)",
							new Object[] { baseDao.getSeqId("VEPAYMENTSDETAIL_SEQ"), count + 1, ve_id, bank[0], bank[1], bank[2], bank[3],
									bank[4], bank[5], bank[6], bank[7], "是" });
				} else {
					baseDao.updateByCondition("VePaymentsDetail", "vpd_remark='否'", "vpd_remark='是' and vpd_veid=" + ve_id);
					Object maxdetno = baseDao.getFieldDataByCondition("VePaymentsDetail", "max(vpd_detno)", "vpd_veid=" + ve_id);
					count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
					baseDao.execute(
							"Insert into VePaymentsDetail(vpd_id, vpd_detno, vpd_veid, vpd_bank, vpd_bankaccount, vpd_contact,vpd_currency,vpd_taxrate,vpd_bankman,vpd_bankaddress,vpd_remark , vpd_vecode) values (?,?,?,?,?,?,?,?,?,?,?,?)",
							new Object[] { baseDao.getSeqId("VEPAYMENTSDETAIL_SEQ"), count + 1, ve_id, bank[0], bank[1], bank[2], bank[3],
									bank[4], bank[5], bank[6], "是", bank[7] });
				}
			}
		}
		// 执行提交后的其它逻辑
		handlerService.handler("Vendor", "commit", "after", new Object[] { ve_id });
		BaseUtil.showErrorOnSuccess(nameerror);
		BaseUtil.showErrorOnSuccess(shortnameerror);
	}

	@Override
	public void resSubmitVendor(int ve_id, String caller) {
		// 只能对状态为[已提交]的单据进行反提交操作
		Object status = baseDao.getFieldDataByCondition("Vendor", "ve_auditstatuscode", "ve_id=" + ve_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler("Vendor", "resCommit", "before", new Object[] { ve_id });
		// 执行反提交操作
		baseDao.resOperate("Vendor", "ve_id=" + ve_id, "ve_auditstatus", "ve_auditstatuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ve_id", ve_id);
		handlerService.handler("Vendor", "resCommit", "after", new Object[] { ve_id });
	}

	@Override
	public void bannedVendor(int ve_id, String caller) {
		// 执行禁用前的其它逻辑
		handlerService.handler("Vendor", "banned", "before", new Object[] { ve_id });
		// 执行禁用操作
		baseDao.banned("Vendor", "ve_id=" + ve_id, "ve_auditstatus", "ve_auditstatuscode");
		// 记录操作
		baseDao.logger.banned(caller, "ve_id", ve_id);
		// 执行比例分配表的异动更新
		if (baseDao.isDBSetting("vendorRate")) {
			try {
				baseDao.callProcedure("SP_SetProdVendorRate", new Object[] { "异动更新" });
			} catch (Exception e) {
			}
		}
		// 执行禁用后的其它逻辑
		handlerService.handler("Vendor", "banned", "after", new Object[] { ve_id });
	}

	@Override
	public void resBannedVendor(int ve_id, String caller) {
		// 执行反禁用操作
		baseDao.resOperate("Vendor", "ve_id=" + ve_id, "ve_auditstatus", "ve_auditstatuscode");
		// 记录操作
		baseDao.logger.resBanned(caller, "ve_id", ve_id);
	}

	@Override
	public void updateUU(Integer id, String uu, String name, String shortName, String isb2b, 
			String b2bcheck, boolean checked, String caller,String ve_webserver,String ve_legalman,String ve_add1) {
		uu = uu.trim();
		String code = baseDao.getFieldValue("vendor", "ve_code", "ve_uu='" + uu + "' and ve_id<>" + id, String.class);
		if (code != null) {
			BaseUtil.showError("UU号已经存在！供应商号：" + code);
		}
		String b2b = "";
		if(b2bcheck!=""){
			b2b=b2b+",ve_b2bcheck="+b2bcheck;
		}else{
			b2bcheck="0";
		}
		if(isb2b!=""){
			b2b=b2b+",ve_ifdeliveryonb2b="+isb2b;
		}
		ve_webserver = StringUtil.hasText(ve_webserver)?ve_webserver:"";
		ve_legalman = StringUtil.hasText(ve_legalman)?ve_legalman:"";
		ve_add1 = StringUtil.hasText(ve_add1)?ve_add1:"";
		baseDao.updateByCondition(
				"Vendor",
				"ve_webserver='"+ve_webserver+"',ve_legalman='"+ve_legalman+"',ve_add1='"+ve_add1+"',ve_uu='" + uu + "',ve_b2benable=" + (checked ? Constant.YES : Constant.NO) + b2b + (StringUtil.hasText(name) ? (",ve_name='" + name + "'") : "")
						+ (StringUtil.hasText(shortName) ? (",ve_shortname='" + shortName + "'") : ""), "ve_id=" + id);
		//将【启用B2B对账】上传到B2B
		Master master = SystemSession.getUser().getCurrentMaster();
		List<VendorInfo> vends =new ArrayList<VendorInfo>();
		VendorInfo vend=new VendorInfo();
		vend.setVenduu(Long.parseLong(uu));
		if(master.getMa_uu()!=null && !"".equals(master.getMa_uu())){
			vend.setCustuu(Long.parseLong(master.getMa_uu().toString()));
		}
		vend.setApcheck(Short.parseShort(b2bcheck));
		vends.add(vend);
		if(vends.size()>0){
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArray(vends));
			try {
					HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/vendor/apcheck?access_id=" + master.getMa_uu(),
							params, true,master.getMa_accesssecret());
			} catch (Exception e) {

			}
		}
		baseDao.logger.others("修改操作", "msg.updateSuccess", "Vendor", "ve_id", id);
	}

	@Override
	public void batchUpdateVendor(String data, String caller) {
		List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(data, "Vendor", "ve_id");
		baseDao.execute(sqls);
	}

	@Override
	public void updateLevel(Integer id, String ve_level, String caller) {
		baseDao.updateByCondition("Vendor", "ve_level='" + ve_level + "'", "ve_id=" + id);
		baseDao.logger.others("修改供应商等级", "msg.updateSuccess", "Vendor", "ve_id", id);
	}

	@Override
	public void updateInfo(int id, String text) {
		baseDao.updateByCondition("Vendor", "ve_ifdeliveryonb2b=" + text, "ve_id=" + id);
		// 记录操作
		baseDao.logger.others("更新B2B收料信息", "msg.updateSuccess", "Vendor", "ve_id", id);
	}

	// 本位币允许税率为0
	private void allowZeroTax(String caller, Object ve_id) {
		if (!baseDao.isDBSetting("Purchase", "allowZeroTax")) {
			String currency = baseDao.getDBSetting("defaultCurrency");
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(ve_currency) from vendor where nvl(ve_taxrate,0)=0 and ve_currency='" + currency + "' and ve_id=?",
					String.class, ve_id);
			if (dets != null) {
				BaseUtil.showError("本位币税率为0，不允许提交进行当前操作!币别：" + dets);
			}
		}
	}

	// 审核后联系人插入联系人资料
	private void addVendorContact(Object ve_id) {
		int count = baseDao.getCount("select * from vendorcontact left join vendor on vc_veid=ve_id where vc_veid=" + ve_id
				+ " and vc_name=ve_contact and nvl(ve_contact,' ')<>' '");
		if (count == 0) {
			int id = baseDao.getSeqId("VendorContact_seq");
			baseDao.execute("insert into vendorcontact(vc_name,vc_job,vc_mobile,vc_officeemail,vc_id,vc_veid,vc_vecode,vc_isvendor) select ve_contact,ve_degree,ve_mobile,ve_email,"
					+ id + ",ve_id,ve_code,-1 from vendor where ve_id=" + ve_id);
			baseDao.execute("update VendorContact set vc_isvendor=0 where vc_id<>" + id + " and vc_veid=" + ve_id);
		}
	}

	// 税率强制等于币别表的默认税率
	private void defaultCurrencyRate(String caller, Object ve_id) {
		if (baseDao.isDBSetting("Vendor", "defaultCurrencyRate")) {
			baseDao.execute("update vendor set ve_taxrate=(select nvl(cr_taxrate,0) from currencys where ve_currency=cr_name and cr_statuscode='CANUSE')"
					+ " where ve_id=" + ve_id);
		}
	}

	// 税率强制等于币别表的默认税率
	private void VendorStatus(String caller, Object ve_id) {
		String PVStatus = baseDao.getDBSetting("Vendor", "VendorStatus");
		String status = "AUDITED";
		if (PVStatus != null) {
			// 状态是已审核
			if ("1".equals(PVStatus)) {
				status = "AUDITED";
			}
			// 状态是在录入
			if ("0".equals(PVStatus)) {
				status = "ENTERING";
			}
		}
		baseDao.updateByCondition("PREVENDOR", "ve_auditstatuscode='" + status + "',ve_auditstatus='" + BaseUtil.getLocalMessage(status)
				+ "'", "ve_id=" + ve_id);
	}

	@Override
	public String getVendorKindNum(String vk_kind) {
		Object[] objs = baseDao.getFieldsDataByCondition("VendorKind", "vk_maxnum,vk_length,VK_EXCODE", "VK_KIND='" + vk_kind + "'");
		if (objs != null && objs[2] != null) {
			int ret = Integer.parseInt(objs[0].toString());
			int length = Integer.parseInt(objs[1].toString());
			String kind = objs[2].toString();
			SqlRowList rs = baseDao.queryForRowSet("select nvl( max(nvl(vk_maxnum,0)),0) from VendorKind where VK_EXCODE=?", kind);
			if (rs.next()) {
				ret = rs.getGeneralInt(1);
			}
			ret++;
			baseDao.updateByCondition("VendorKind", "VK_MAXNUM=" + ret, "VK_EXCODE='" + kind + "'");
			String number = "";
			length -= String.valueOf(ret).length();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					number += "0";
				}
			}
			number += String.valueOf(ret);
			number = kind + number;
			return number;
		}
		return null;
	}

	@Override
	public void checkVendorUU(String data, String caller) {
		/**
		 * 反馈编号：2017040928；开通/取消b2b记录日志。
		 * 
		 * @author wsy
		 */
		String log = "";
		if ("Vendor!CheckUU".equals(caller)) {
			log = "开通B2B";
			baseDao.updateByCondition("vendor", "ve_b2benable=1,ve_ifdeliveryonb2b=-1,ve_b2bcheck=-1", "ve_id in (" + data + ")");
		} else {
			log = "取消B2B";
			baseDao.updateByCondition("vendor", "ve_b2benable=0,ve_ifdeliveryonb2b=0,ve_b2bcheck=0", "ve_id in (" + data + ")");
		}
		String[] ids = data.split(",");
		for (String id : ids) {
			baseDao.logger.others(log, "msg.updateSuccess", "Vendor", "ve_id", id);
		}
	}

	@Override
	public void regB2BVendor(int id) {
		Enterprise enterprise = new Enterprise();
		Object[] objs = baseDao.getFieldsDataByCondition("Vendor", "ve_name,ve_shortname,ve_webserver,ve_contact,ve_mobile,ve_email",
				"ve_id=" + id);
		if (objs != null) {
			if (objs[0] == null || objs[3] == null || objs[4] == null) {
				BaseUtil.showError("供应商名称、联系人、联系人手机号都不能为空，请检查后再设置开通!");
			} else {
				enterprise.setEnName(objs[0].toString());
				enterprise.setEnShortname(objs[1].toString());
				enterprise.setEnBussinessCode(objs[2] == null ? "" : objs[2].toString());
				enterprise.setEnAdminName(objs[3].toString());
				enterprise.setEnAdminTel(objs[4].toString());
				enterprise.setEnAdminEmail(objs[5] == null ? "" : objs[5].toString());
				if (enterprise != null) {
					Map<String, Object> regInfos = enterpriseService.regEnterprise(enterprise);
					if (regInfos != null) {
						if (regInfos.get("ok").equals(true)) {
							// 更新供应商UU
							baseDao.updateByCondition("Vendor", "ve_uu='" + regInfos.get("enUU") + "'", "ve_id=" + id);
							regB2BVendor_sendMail(id);
						} else {
							BaseUtil.showError(regInfos.get("error").toString());
						}
					} else {
						BaseUtil.showError("注册失败！");
					}
				}
			}
		}

	}

	public void regB2BVendor_sendMail(Integer id) {
		Object email = baseDao.getFieldDataByCondition("Vendor", "ve_email", "ve_id=" + id);
		if (email == null || "".equals(email.toString().trim()) || "null".equals(email.toString().trim())) {
			return;
			// BaseUtil.showError("供应商邮箱为空，无法发送邮件!");
		}
		// 标题和内容一致
		String encop = baseDao.getFieldDataByCondition("enterprise", "en_name", "1=1").toString();
		Object[] objs = baseDao.getFieldsDataByCondition("Vendor",
				new String[] { "ve_code", "ve_name", "ve_uu", "ve_contact", "ve_mobile" }, "ve_id=" + id);
		String title = "请查看供应商资料，供应商编号：" + objs[0];
		String contextdetail = "<P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>"
				+ objs[1]
				+ "，您好！：<SPAN lang=EN-US><?xml:namespace prefix = 'o' ns = 'urn:schemas-microsoft-com:office:office' /><o:p></o:p></SPAN></SPAN></P>"
				+ "<P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN lang=EN-US style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'><SPAN style='mso-spacerun: yes'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ " </SPAN></SPAN><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>您的合作伙伴<SPAN style='COLOR: blue'>"
				+ "（<SPAN lang=EN-US>"
				+ encop
				+ "</SPAN>）</SPAN>将您列为其合格供应商(客户),并为您注册了优软商务平台，登录账号为贵司的<SPAN lang=EN-US style='COLOR: blue'>"
				+ objs[3]
				+ "</SPAN>的手机号码，密码默认6个1。"
				+ "<SPAN lang=EN-US>,</SPAN>请及时登入优软商务平台查看相关的业务信息<SPAN lang=EN-US>!<o:p></o:p></SPAN></SPAN></P><P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>登入平台的地址：<SPAN lang=EN-US><A href='http://www.ubtob.com/'><FONT color=#0000ff>www.ubtob.com</FONT></A>"
				+ "<o:p></o:p></SPAN></SPAN></P><P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>如在使用平台过程中，遇到任何操作问题，请及时与深圳市优软科技有限公司客服人员（连小姐）联系，联系电话：<SPAN lang=EN-US>0755-26996828<o:p></o:p></SPAN></SPAN></P>"
				+ "<SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑; mso-font-kerning: 1.0pt; mso-ansi-language: EN-US; mso-fareast-language: ZH-CN; mso-bidi-language: AR-SA'>致敬！</SPAN>";
		sendMailService.sendSysMail(title, contextdetail, email.toString().trim());
	}

	@Override
	public void updateB2BPro(String data, String cond) {
		/**
		 * 反馈编号：2017040928 批量获取供应商记录日志。
		 * 
		 * @author wsy
		 */
		String log = "";
		if ("ve_ifdeliveryonb2b=-1".equals(cond))
			log = "启用B2B收料";
		if ("ve_ifdeliveryonb2b=0".equals(cond))
			log = "取消B2B收料";
		if ("ve_b2bcheck=-1".equals(cond))
			log = "启用B2B对账";
		if ("ve_b2bcheck=0".equals(cond))
			log = "取消B2B对账";
		baseDao.updateByCondition("vendor", cond, "ve_id in (" + data + ")");
		//将【启用B2B对账】上传到B2B
		Master master = SystemSession.getUser().getCurrentMaster();
		List<VendorInfo> vends =new ArrayList<VendorInfo>();
		String[] ids = data.split(",");
		for (String id : ids) {
			Object[] ob=baseDao.getFieldsDataByCondition("vendor",  new String[] { "ve_uu", "nvl(ve_b2bcheck,0)" }, "ve_id="+id);
			if(ob[0]!=null && !"".equals(ob[0])&&master.getMa_uu()!=null && !"".equals(master.getMa_uu())){
				VendorInfo vend=new VendorInfo();
				vend.setVenduu(Long.parseLong(ob[0].toString()));
				vend.setCustuu(Long.parseLong(master.getMa_uu().toString()));
				vend.setApcheck(Short.parseShort(ob[1].toString()));
				vends.add(vend);
			}
			baseDao.logger.others(log, "msg.updateSuccess", "Vendor", "ve_id", id);
		}
		if(vends.size()>0){
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArray(vends));
			try {
					Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/vendor/apcheck?access_id=" + master.getMa_uu(),
							params, true,master.getMa_accesssecret());
					if (response.getStatusCode() == HttpStatus.OK.value()) {
						Map<String, Object> backInfo = FlexJsonUtil.fromJson(
								response.getResponseText(), HashMap.class);
						if (backInfo.get("ok").equals(true)) {

						} else {
							System.out.println("错误："+backInfo.get("error").toString());
						}
					}
			} catch (Exception e) {
			}
		}
	}
}