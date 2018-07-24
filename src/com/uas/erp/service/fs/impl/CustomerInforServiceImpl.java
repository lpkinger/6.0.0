package com.uas.erp.service.fs.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.b2b.model.CustomerInfo;
import com.uas.b2b.model.CustomerInfo.AssociateCompanyInfo;
import com.uas.b2b.model.CustomerInfo.ChangesInstructionInfo;
import com.uas.b2b.model.CustomerInfo.CustomerExcutiveInfo;
import com.uas.b2b.model.CustomerInfo.ShareholdersInfo;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.fs.CustomerInforService;

@Service
public class CustomerInforServiceImpl implements CustomerInforService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private EnterpriseService enterpriseService;

	// 更改子级保理账套的状态
	private void changeStatus(int cu_id, String statuscode) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			String[] syncSon = baseDao.getDBSettingArray("Customer!Infor", "syncSon");
			if (StringUtil.hasText(syncSon)) {
				String[] masters = CollectionUtil.toString(syncSon).split(",");
				for (String ma : masters) {
					baseDao.updateByCondition(ma + ".CustomerInfor",
							"cu_statuscode='" + statuscode + "', cu_status='" + BaseUtil.getLocalMessage(statuscode) + "'", "cu_id="
									+ cu_id);
				}
			}
		}
	}

	@Override
	public void saveCustomerInfor(String formStore, String caller, String param1, String param2, String param3, String param4, String param5) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean bool = baseDao.checkIf("CustomerInfor", "cu_name='" + store.get("cu_name") + "' and cu_id<>" + store.get("cu_id"));
		if (bool) {
			BaseUtil.showError("该客户名称已存在!");
		}
		bool = baseDao.checkIf("CustomerInfor",
				"cu_paperstype='" + store.get("cu_paperstype") + "' and cu_paperscode='" + store.get("cu_paperscode") + "' and cu_id<>"
						+ store.get("cu_id"));
		if (bool) {
			BaseUtil.showError("该客户信息已存在!");
		}
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(param3);
		List<Map<Object, Object>> grid4 = BaseUtil.parseGridStoreToMaps(param4);

		String code = baseDao.sGetMaxNumber("CustomerInfor", 2);
		store.put("cu_code", code);

		handlerService.handler(caller, "save", "before", new Object[] { store, grid, grid2, grid3, grid4 });

		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CustomerInfor", new String[] {}, new Object[] {});
		baseDao.execute(formSql);

		// 保存CustomerExcutive
		if (param1 != null && !"".equals(param1)) {
			for (Map<Object, Object> m : grid) {
				m.put("ce_id", baseDao.getSeqId("CustomerExcutive_SEQ"));
			}
		}
		// 保存CustomerShareHolder
		if (param2 != null && !"".equals(param2)) {
			for (Map<Object, Object> m : grid2) {
				m.put("cs_id", baseDao.getSeqId("CustomerShareHolder_SEQ"));
			}
		}

		// 保存CustomerInverstment
		if (param3 != null && !"".equals(param3)) {
			for (Map<Object, Object> m : grid3) {
				m.put("ci_id", baseDao.getSeqId("CustomerInverstment_SEQ"));
			}
		}
		// 保存CustomerUDStream
		if (param4 != null && !"".equals(param4)) {
			for (Map<Object, Object> m : grid4) {
				m.put("cud_id", baseDao.getSeqId("CustomerUDStream_SEQ"));
			}
		}

		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "CustomerExcutive"));
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid2, "CustomerShareHolder"));
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid3, "CustomerInverstment"));
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid4, "CustomerUDStream"));
		int count = baseDao.getCount("select count(1) from FSCHANGESINSTRUCTION  where cs_cuid=" + store.get("cu_id"));
		if (count == 0) {
			baseDao.execute("insert into FSCHANGESINSTRUCTION (CS_ID,CS_CUID,CD_DETNO,CD_TYPE) values(FSCHANGESINSTRUCTION_SEQ.NEXTVAL, "
					+ store.get("cu_id") + ", 1, '股东')");
			baseDao.execute("insert into FSCHANGESINSTRUCTION (CS_ID,CS_CUID,CD_DETNO,CD_TYPE) values(FSCHANGESINSTRUCTION_SEQ.NEXTVAL, "
					+ store.get("cu_id") + ", 2, '法人')");
			baseDao.execute("insert into FSCHANGESINSTRUCTION (CS_ID,CS_CUID,CD_DETNO,CD_TYPE) values(FSCHANGESINSTRUCTION_SEQ.NEXTVAL, "
					+ store.get("cu_id") + ", 3, '住所')");
		}
		// 更新更新日期
		baseDao.updateByCondition("CustomerShareHolder", "cs_updatedate=sysdate", "cs_cuid = " + store.get("cu_id"));
		baseDao.updateByCondition("CustomerInverstment", "ci_updatedate=sysdate", "ci_cuid = " + store.get("cu_id"));
		baseDao.updateByCondition("CustomerUDStream", "cud_updatedate=sysdate", "cud_cuid = " + store.get("cu_id"));
		// 记录日志
		baseDao.logger.save(caller, "cu_id", store.get("cu_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateCustomerInfor(String formStore, String caller, String param1, String param2, String param3, String param4,
			String param5) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean bool = baseDao.checkIf("CustomerInfor", "cu_name='" + store.get("cu_name") + "' and cu_id <>" + store.get("cu_id"));
		if (bool) {
			BaseUtil.showError("该客户名称已存在!");
		}
		bool = baseDao.checkIf("CustomerInfor",
				"cu_paperstype='" + store.get("cu_paperstype") + "' and cu_paperscode='" + store.get("cu_paperscode") + "' and cu_id <>"
						+ store.get("cu_id"));
		if (bool) {
			BaseUtil.showError("该客户信息已存在!");
		}
		int count = baseDao.getCount("select count(1) from FSCHANGESINSTRUCTION  where cs_cuid=" + store.get("cu_id"));
		if (count == 0) {
			baseDao.execute("insert into FSCHANGESINSTRUCTION (CS_ID,CS_CUID,CD_DETNO,CD_TYPE) values(FSCHANGESINSTRUCTION_SEQ.NEXTVAL, "
					+ store.get("cu_id") + ", 1, '股东')");
			baseDao.execute("insert into FSCHANGESINSTRUCTION (CS_ID,CS_CUID,CD_DETNO,CD_TYPE) values(FSCHANGESINSTRUCTION_SEQ.NEXTVAL, "
					+ store.get("cu_id") + ", 2, '法人')");
			baseDao.execute("insert into FSCHANGESINSTRUCTION (CS_ID,CS_CUID,CD_DETNO,CD_TYPE) values(FSCHANGESINSTRUCTION_SEQ.NEXTVAL, "
					+ store.get("cu_id") + ", 3, '住所')");
		}
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(param3);
		List<Map<Object, Object>> grid4 = BaseUtil.parseGridStoreToMaps(param4);
		List<Map<Object, Object>> grid5 = BaseUtil.parseGridStoreToMaps(param5);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid, grid2, grid3, grid4 });

		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CustomerInfor", "cu_id");
		baseDao.execute(formSql);

		List<String> gridSql = new ArrayList<String>();
		// 更新CustomerExcutive
		if (param1 != null && !"".equals(param1)) {
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "CustomerExcutive", "ce_id"));
		}

		// 更新CustomerShareHolder
		if (param2 != null && !"".equals(param2)) {
			for (Map<Object, Object> m : grid2) {
				m.put("cs_updatedate", DateUtil.getCurrentDate());
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid2, "CustomerShareHolder", "cs_id"));
		}
		// 更新CustomerInverstment
		if (param3 != null && !"".equals(param3)) {
			for (Map<Object, Object> m : grid3) {
				m.put("ci_updatedate", DateUtil.getCurrentDate());
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid3, "CustomerInverstment", "ci_id"));
		}
		// 更新CustomerUDStream
		if (param4 != null && !"".equals(param4)) {
			for (Map<Object, Object> m : grid4) {
				m.put("cud_updatedate", DateUtil.getCurrentDate());
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid4, "CustomerUDStream", "cud_id"));
		}
		// 更新Fschangesinstruction
		if (param5 != null && !"".equals(param5)) {
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid5, "Fschangesinstruction", "cs_id"));
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "cu_id", store.get("cu_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteCustomerInfor(int cu_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { cu_id });
		// 是否已产生业务数据
		baseDao.delCheck("CustomerInfor", cu_id);
		Object custname = baseDao.getFieldDataByCondition("CustomerInfor", "cu_name", "cu_id="+cu_id);
		// 删除主表内容
		baseDao.deleteById("CustomerInfor", "cu_id", cu_id);
		// 删除CustomerExcutive
		baseDao.deleteById("CustomerExcutive", "ce_cuid", cu_id);
		// 删除CustomerShareHolder
		baseDao.deleteById("CustomerShareHolder", "cs_cuid", cu_id);
		// 删除CustomerInverstment
		baseDao.deleteById("CustomerInverstment", "ci_cuid", cu_id);
		// 删除CustomerUDStream
		baseDao.deleteById("CustomerUDStream", "cud_cuid", cu_id);
		// 删除CustPersonInfo
		baseDao.deleteById("CustPersonInfo", "cp_custid", cu_id);
		// 删除CUSTOMERPAYTAXES
		baseDao.deleteById("CUSTOMERPAYTAXES", "ct_cuid", cu_id);
		// 删除FSCHANGESINSTRUCTION
		baseDao.deleteById("FSCHANGESINSTRUCTION", "cs_cuid", cu_id);
		//删除信用评级申请
		baseDao.deleteByCondition("CUSTCREDITRATINGAPPLY", "cra_cuvename = '"+custname+"' and cra_valid IS NULL");
		// 记录日志
		baseDao.logger.delete(caller, "cu_id", cu_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { cu_id });
		// 删除之后，更新子帐套保理客户资料为已禁用
		changeStatus(cu_id, "DISABLE");
	}

	@Override
	public void submitCustomerInfor(int cu_id, String caller) {
		baseDao.execute("update CustomerInfor set cu_actcontroller=(select max(ce_name) from CUSTOMEREXCUTIVE where ce_cuid=cu_id and nvl(ce_iscontroller,0)<>0) where cu_id="
				+ cu_id + " and nvl(cu_actcontroller,' ')=' '");
		baseDao.execute("update CustomerInfor set cu_corporation=(select max(ce_name) from CUSTOMEREXCUTIVE where ce_cuid=cu_id and nvl(ce_islegrep,0)<>0) where cu_id="
				+ cu_id + " and nvl(cu_corporation,' ')=' '");
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("CustomerInfor", "cu_statuscode", "cu_id=" + cu_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { cu_id });

		// 执行提交操作
		baseDao.submit("CustomerInfor", "cu_id=" + cu_id, "cu_status", "cu_statuscode");

		// 记录操作
		baseDao.logger.submit(caller, "cu_id", cu_id);

		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { cu_id });
	}

	@Override
	public void resSubmitCustomerInfor(int cu_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("CustomerInfor", "cu_statuscode", "cu_id=" + cu_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { cu_id });

		// 执行反提交操作
		baseDao.resOperate("CustomerInfor", "cu_id=" + cu_id, "cu_status", "cu_statuscode");

		// 记录操作
		baseDao.logger.resSubmit(caller, "cu_id", cu_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { cu_id });
	}

	@Override
	public void auditCustomerInfor(int cu_id, String caller) {
		// 只能对已提交进行审核操作
		Object[] status = baseDao.getFieldsDataByCondition("CustomerInfor", new String [] {
				"cu_statuscode","cu_name","cu_engname","cu_accmgncode","cu_accmgner","cu_enuu",
				"cu_corporation","cu_contact","cu_contactnum","cu_contactphone","cu_capcurrency",
				"cu_regcapital","cu_officeadd","cu_businesscode"}, "cu_id=" + cu_id);
		
		StateAssert.auditOnlyCommited(status[0]);
		
		backInfoToB2B(cu_id);
		
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { cu_id });
		baseDao.audit("CustomerInfor", "cu_id=" + cu_id, "cu_status", "cu_statuscode", "cu_auditdate", "cu_auditman");
		
		//生成客户资料
		String str = "";
		String custatus = baseDao.getFieldValue("Customer", "cu_auditstatuscode", "cu_name = '"+status[1]+"'", String.class);
		if (custatus==null||"DISABLE".equals(custatus)) {
			Map<Object, Object> customer = new HashMap<Object, Object>();
			Object emId = baseDao.getFieldDataByCondition("Employee", "em_id", "em_code = '"+status[3]+"'");
			
			Employee employee = SystemSession.getUser();
			String code = baseDao.sGetMaxNumber("Customer", 2);
			customer.put("cu_id", baseDao.getSeqId("CUSTOMER_SEQ"));
			customer.put("cu_code", code);
			customer.put("cu_name", status[1]);
			customer.put("cu_engname", status[2]);
			customer.put("cu_sellerid", emId);
			customer.put("cu_sellercode", status[3]);
			customer.put("cu_sellername", status[4]);
			customer.put("cu_servicerid", emId);
			customer.put("cu_servicecode", status[3]);
			customer.put("cu_servicename", status[4]);
			customer.put("cu_uu", status[5]);
			customer.put("cu_b2benable", StringUtil.hasText(status[5])?-1:0);
			customer.put("cu_recordman", employee.getEm_name());
			customer.put("cu_recorddate", DateUtil.getCurrentDate());
			customer.put("cu_auditman", employee.getEm_name());
			customer.put("cu_auditdate", DateUtil.getCurrentDate());
			customer.put("cu_auditstatus", BaseUtil.getLocalMessage("AUDITED"));
			customer.put("cu_auditstatuscode", "AUDITED");
			customer.put("cu_lawman", status[6]);
			customer.put("cu_contact", status[7]);
			customer.put("cu_tel", status[8]);
			customer.put("cu_mobile", status[9]);
			customer.put("cu_credit", 0);
			customer.put("cu_enablecredit", "否");
			customer.put("cu_currency", status[10]);
			Object[] rate = baseDao.getFieldsDataByCondition("CURRENCYS", new String [] {"cr_rate", "cr_taxrate"}, "cr_statuscode ='CANUSE' and cr_name = '"+status[10]+"'");
			if (rate!=null) {
				customer.put("cu_rate", rate[0]);
				customer.put("cu_taxrate", rate[1]);
			}
			customer.put("cu_regamount", status[11]);
			customer.put("cu_add1", status[12]);
			customer.put("cu_status", "长期");
			customer.put("cu_shcustcode", code);
			customer.put("cu_shcustname", status[1]);
			customer.put("cu_arcode", code);
			customer.put("cu_arname", status[1]);
			customer.put("cu_businesscode", status[13]);
			customer.put("cu_monthsend", "月底");
			baseDao.execute(SqlUtil.getInsertSqlByMap(customer, "Customer"));
		}else if(!"AUDITED".equals(custatus)){
			str = "系统已存在未完善客户资料，请进行客户资料完善！";
		}
		
		// 记录操作
		baseDao.logger.audit(caller, "cu_id", cu_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { cu_id });
		
		if (!"".equals(str)) {
			BaseUtil.showErrorOnSuccess(str);
		}
		
	}

	@Override
	public void resAuditCustomerInfor(int cu_id, String caller) {
		// 只能对状态为[已审核]的采购单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("CustomerInfor", "cu_statuscode", "cu_id=" + cu_id);
		StateAssert.resAuditOnlyAudit(status);
		// 是否已产生业务数据
		baseDao.resAuditCheck("CustomerInfor", cu_id);
		handlerService.beforeResAudit(caller, new Object[] { cu_id });
		// 执行反审核操作
		baseDao.resAudit("CustomerInfor", "cu_id=" + cu_id, "cu_status", "cu_statuscode", "cu_auditman", "cu_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "cu_id", cu_id);
		handlerService.afterResAudit(caller, new Object[] { cu_id });
	}

	@Override
	public void bannedCustomerInfor(int cu_id, String caller) {
		// 执行禁用操作
		baseDao.updateByCondition("CustomerInfor", "cu_statuscode='DISABLE', cu_status='" + BaseUtil.getLocalMessage("DISABLE") + "'",
				"cu_id=" + cu_id);
		// 禁用之后，更新子帐套保理客户资料为已禁用
		changeStatus(cu_id, "DISABLE");
		// 记录操作
		baseDao.logger.banned(caller, "cu_id", cu_id);
	}

	@Override
	public void resBannedCustomerInfor(int cu_id, String caller) {
		// 执行反禁用操作
		baseDao.updateByCondition("CustomerInfor", "cu_statuscode='AUDITED', cu_status='" + BaseUtil.getLocalMessage("AUDITED") + "'",
				"cu_id=" + cu_id);
		// 反禁用之后，更新子帐套保理客户资料为已审核
		changeStatus(cu_id, "AUDITED");
		// 记录操作
		baseDao.logger.resBanned(caller, "cu_id", cu_id);
	}
	
	private void backInfoToB2B(int id){
		boolean isB2B = baseDao.checkIf("CustomerInfor", "cu_id = "+id+" and nvl(cu_b2benable,0)<>0 and cu_enuu is not null");
		if (isB2B) {
			try {
				Master master = SystemSession.getUser().getCurrentMaster();
				Master FSMaster = null;
				if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
					String fsmaster = baseDao.getDBSetting("FS_master");
					if (null!=fsmaster) {
						String sob = SpObserver.getSp();
						FSMaster = enterpriseService.getMasterByName(fsmaster);
						SpObserver.putSp(sob);
					}
				} else {
					FSMaster = master;
				}
				
				if (null!=FSMaster) {
					Map<String, String> params = new HashMap<String, String>();
					
					CustomerInfo customer = baseDao.getJdbcTemplate().queryForObject("select a.*,cu_id erpId,cu_enuu enuu from CustomerInfor a where cu_id = ?", 
							new BeanPropertyRowMapper<CustomerInfo>(CustomerInfo.class), id);
					
					List<CustomerExcutiveInfo> excutiveInfos = baseDao.getJdbcTemplate().query("select a.*,ce_id erpId from CUSTOMEREXCUTIVE a where ce_cuid = ? order by ce_detno asc", 
							new BeanPropertyRowMapper<CustomerExcutiveInfo>(CustomerExcutiveInfo.class), id);
					for (CustomerExcutiveInfo customerExcutiveInfo : excutiveInfos) {
						customerExcutiveInfo.setCusId(customer.getId());
					}
					customer.setCustomerExcutives(excutiveInfos);
					
					List<ShareholdersInfo> shareholdersInfos = baseDao.getJdbcTemplate().query("select a.*,cs_id erpId from CUSTOMERSHAREHOLDER a where cs_cuid = ? order by cs_detno asc", 
							new BeanPropertyRowMapper<ShareholdersInfo>(ShareholdersInfo.class), id);
					for (ShareholdersInfo shareholdersInfo : shareholdersInfos) {
						shareholdersInfo.setCusId(customer.getId());
					}
					customer.setShareholders(shareholdersInfos);
					
					List<AssociateCompanyInfo> associateCompanyInfos = baseDao.getJdbcTemplate().query("select a.*,cud_id erpId from CUSTOMERUDSTREAM a where cud_cuid = ? order by cud_detno asc", 
							new BeanPropertyRowMapper<AssociateCompanyInfo>(AssociateCompanyInfo.class), id);
					for (AssociateCompanyInfo associateCompanyInfo : associateCompanyInfos) {
						associateCompanyInfo.setCusId(customer.getId());
					}
					customer.setAssociateCompanies(associateCompanyInfos);
					
					List<ChangesInstructionInfo> changesInstructionInfos = baseDao.getJdbcTemplate().query("select a.*,cs_id erpId from FSCHANGESINSTRUCTION a where cs_cuid = ? order by cd_detno asc", 
							new BeanPropertyRowMapper<ChangesInstructionInfo>(ChangesInstructionInfo.class), id);
					for (ChangesInstructionInfo changesInstructionInfo : changesInstructionInfos) {
						changesInstructionInfo.setCusId(customer.getId());
					}
					customer.setChangesInstructions(changesInstructionInfos);
					params.put("customer", FlexJsonUtil.toJsonDeep(customer));
					Response response = HttpUtil.sendPostRequest(FSMaster.getMa_finwebsite() + "/customer/erp/updateinfo?access_id=" + FSMaster.getMa_uu(), params,
							true, FSMaster.getMa_accesssecret());
					if (response.getStatusCode() != HttpStatus.OK.value()) {
						throw new Exception("连接平台失败," + response.getStatusCode());
					}else {
						String data = response.getResponseText();
						if (StringUtil.hasText(data)) {
							JSONObject result = JSONObject.fromObject(data);
							if (result.has("customer")) {
								baseDao.execute(SqlUtil.getUpdateSqlByFormStore(BaseUtil.parseFormStoreToMap(result.getString("customer")), "CustomerInfor", "cu_id"));
							}
							if (result.has("customerExcutive")) {
								baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(result.getString("customerExcutive"), "CUSTOMEREXCUTIVE", "ce_id"));
							}
							if (result.has("shareholders")) {
								baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(result.getString("shareholders"), "CUSTOMERSHAREHOLDER", "cs_id"));
							}
							if (result.has("associateCompany")) {
								baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(result.getString("associateCompany"), "CUSTOMERUDSTREAM", "cud_id"));
							}
							if (result.has("changeInstruction")) {
								baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(result.getString("changeInstruction"), "FSCHANGESINSTRUCTION", "cs_id"));
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError("错误：" + e.getMessage());
			}
		}
	}

}
