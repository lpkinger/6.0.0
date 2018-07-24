package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.bind.Status;
import com.uas.erp.core.support.Assert;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.AccountRegisterBankService;
import com.uas.erp.service.oa.CustomPageService;

/**
 * 标准界面的基本逻辑
 */
@Service("CustomPageSerive")
public class CustomPageSeriveImpl implements CustomPageService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DataListDao dataListDao;
	@Autowired
	private AccountRegisterBankService accountRegisterBankService;
	@Autowired
	private FormDao formDao;
	@Autowired
	private HandlerService handlerService;
	private static String maintable = "CUSTOMTABLE";
	private static String detailtable = "CUSTOMTABLEDETAIL";
	private static String mainkeyField = "CT_ID";
	private static String detailmainkeyField = "CD_CTID";
	private static String detailkeyField = "CD_ID";
	private static String mainstatusField = "CT_STATUS";
	private static String mainstatuscodeField = "CT_STATUSCODE";
	
	private static String approvesstatusField = "CT_APPROVESTATUS";
	private static String approvesstatuscodeField = "CT_APPROVESTATUSCODE";

	@Override
	public void savePage(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 每条记录插入caller 方便数据查询
		store.put("CT_CALLER", caller);
		List<Map<Object, Object>> datas = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, datas });
		// 改为使用支持lob操作的sql方法
		baseDao.execute(SqlUtil.getSqlMap(store, maintable,false));
		// //保存detailgrid
		if (gridStore != null && !"".equals(gridStore)) {
			if (datas.size() > 0) {
				baseDao.execute(SqlUtil.getInsertSqlbyList(datas, detailtable, detailkeyField));
				// 根据明细行申请金额，批准金额更新主表申请金额，批准金额
				String sql = "select sum(cd_pleaseamount),sum(cd_amount) from customtabledetail where cd_ctid=" + store.get(mainkeyField);
				SqlRowList rs = baseDao.queryForRowSet(sql);
				if (rs.next()) {
					if (rs.getGeneralDouble(1, 6) > 0) {
						baseDao.updateByCondition(maintable, "ct_pleaseamount=" + rs.getGeneralDouble(1, 6),
								"ct_id=" + store.get(mainkeyField));
					}
					if (rs.getGeneralDouble(2, 6) > 0) {
						baseDao.updateByCondition(maintable, "ct_amount=" + rs.getGeneralDouble(2, 6), "ct_id=" + store.get(mainkeyField));
					}
				}
			}
		}
		// 记录操作
		try {
			baseDao.logger.save(caller, mainkeyField, store.get(mainkeyField));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, datas });
	}

	@Override
	public void updatePageById(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> datas = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, datas });
		// 修改form
		// 改为使用支持lob操作的sql方法
		baseDao.execute(SqlUtil.getSqlMap(store, maintable, mainkeyField,false));
		// 修改Grid
		if (gridStore != null && !"".equals(gridStore)) {
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, detailtable, detailkeyField);
			for (Map<Object, Object> s : datas) {
				if (s.get(detailkeyField) == null || s.get(detailkeyField).equals("") || s.get(detailkeyField).equals("0")
						|| Integer.parseInt(s.get(detailkeyField).toString()) == 0) {
					s.put(detailkeyField, baseDao.getSeqId("CUSTOMTABLEDETAIL_SEQ"));
					gridSql.add(SqlUtil.getInsertSqlByMap(s, detailtable));
				}		
			}
			baseDao.execute(gridSql);
			// 根据明细行申请金额，批准金额更新主表申请金额，批准金额
			String sql = "select sum(cd_pleaseamount),sum(cd_amount) from customtabledetail where cd_ctid=" + store.get(mainkeyField);
			SqlRowList rs = baseDao.queryForRowSet(sql);
			if (rs.next()) {
				if (rs.getGeneralDouble(1, 6) > 0) {
					baseDao.updateByCondition(maintable, "ct_pleaseamount=" + rs.getGeneralDouble(1, 6), "ct_id=" + store.get(mainkeyField));
				}
				if (rs.getGeneralDouble(2, 6) > 0) {
					baseDao.updateByCondition(maintable, "ct_amount=" + rs.getGeneralDouble(2, 6), "ct_id=" + store.get(mainkeyField));
				}
			}
		}

		// 记录操作
		try {
			baseDao.logger.update(caller, mainkeyField, store.get(mainkeyField));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, datas });
	}

	@Override
	public void deletePage(String caller, int id) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { id });
		// 删除form
		baseDao.deleteById(maintable, mainkeyField, id);
		// 删除DetailGrid
		baseDao.deleteByCondition(detailtable, detailmainkeyField + "=" + id);
		// 记录操作
		try {
			baseDao.logger.delete(caller, mainkeyField, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { id });
	}

	@Override
	public void printPage(String caller, int id) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield" }, "fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		String statuscode=baseDao.getFieldValue(maintable, mainstatuscodeField, mainkeyField+"="+id, String.class);
		if (baseDao.isDBSetting(caller, "printNeedAudit")) {
			StateAssert.printOnlyAudited(statuscode);
		}
		if (objs != null) {
			// 执行打印前的其它逻辑
			handlerService.beforePrint(caller, new Object[] { id });
			// 执行打印操作
			// 记录操作
			try {
				baseDao.logger.print(caller, objs[1].toString(), id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 执行打印后的其它逻辑
			handlerService.afterPrint(caller, new Object[] { id });
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("print_tableisnull"));
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, timeout = 20)
	public void auditPage(int id, String caller) throws Exception {
		// 只能对状态为[已提交]的单据进行审核操作!
		Object status = baseDao.getFieldDataByCondition(maintable, mainstatuscodeField, mainkeyField + "=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, id);
		// 执行审核操作
		baseDao.audit(maintable, mainkeyField + "=" + id, mainstatusField, mainstatuscodeField, "ct_auditdate", "ct_auditman");
		int count = baseDao.getCount("select count(*) from user_tab_columns where table_name='CURNAVIGATION'");
		if(count > 0 ){//客户服务单据
			Object[] ob= baseDao.getFieldsDataByCondition("CURNAVIGATION", new String[]{"cn_url","cn_caller"}, "cn_uascaller='"+caller+"'");
			if(ob!=null){
				Object[] custinfo=baseDao.getFieldsDataByCondition("customtable",new String[]{"ct_code","custlinkmantel"},"ct_id="+id);
				baseDao.execute("insert into CURNOTIFY(cn_id,cn_url,cn_caller,cn_desc,cn_keyfield,cn_keyvalue,cn_man"
						+ ",cn_date,cn_status,cn_emuu,cn_enuu) select CURNOTIFY_SEQ.nextval,cn_url,cn_caller,fo_title||ct_code,"
						+ "fo_keyfield,ct_id,ct_recorder,to_date(to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd hh24:mi:ss'),'未确认',custlinkmanuu,custuu from customtable left join curnavigation on ct_caller=cn_uascaller left join form on ct_caller=fo_caller where ct_id="+id);
				Map<String, String> params = new HashMap<String, String>();
				params.put("master", SpObserver.getSp());
				params.put("telephone",custinfo[1].toString());//推送目标用户，取客户联系人手机号
				params.put("title", "你有新的商务消息");
				params.put("content", "单据编号"+custinfo[0]);//单据编号取系统单据号
				params.put("enUU", "");
				params.put("masterId", "");
				params.put("url", "");
				params.put("pageTitle", "商务消息");
				params.put("platform", "ERP");
				Response response = HttpUtil.sendPostRequest("http://113.105.74.140:8092"
						+ "/tigase/servicePush", params, false);
				FlexJsonUtil fj=null;
				JSONObject obj=new JSONObject();
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					Map<String, Object> backInfo = fj.fromJson(response.getResponseText());
					obj.put("result", "success");
				}else{
					obj.put("result", "fail");
				}
			}
		}
		// 记录操作
		baseDao.logger.audit(caller, mainkeyField, id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, id);
	}

	@Override
	public void resAuditPage(String caller, int id) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object status = baseDao.getFieldDataByCondition(maintable, mainstatuscodeField, mainkeyField + "=" + id);
		StateAssert.resAuditOnlyAudit(status);
		//已转费用报销的OA自定义单据不允许反审核
		Object field = baseDao.getFieldDataByCondition(maintable,
				"nvl(CT_TURNAMOUNT ,0)", mainkeyField + "=" + id);
		if ( !"0".equals(field.toString())) {
			BaseUtil.showError("已转费用报销，不能反审核!");
		}		
		// 执行审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[] { id });
		// 执行审核操作
		baseDao.resAudit(maintable, mainkeyField + "=" + id, mainstatusField, mainstatuscodeField, "ct_auditman","ct_auditdate");
		// 记录操作
		try {
			baseDao.logger.resAudit(caller, mainkeyField, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] { id });
	}

	@Override
	public void submitPage(String caller, int id) {
		Object status = baseDao.getFieldDataByCondition(maintable, mainstatuscodeField, mainkeyField + "=" + id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { id });
		// 执行提交操作
		baseDao.submit(maintable, mainkeyField + "=" + id, mainstatusField, mainstatuscodeField);
		// 记录操作
		try {
			baseDao.logger.submit(caller, mainkeyField, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行审核后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { id });
	}

	@Override
	public void resSubmitPage(String caller, int id) {
		Object status = baseDao.getFieldDataByCondition(maintable, mainstatuscodeField, mainkeyField + "=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		handlerService.beforeResSubmit(caller, new Object[] { id });
		baseDao.resOperate(maintable, mainkeyField + "=" + id, mainstatusField, mainstatuscodeField);
		// 记录操作
		try {
			baseDao.logger.resSubmit(caller, mainkeyField, id);
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterResSubmit(caller, new Object[] { id });
	}

	@Override
	public void bannedPage(String caller, int id) {
		Object status = baseDao.getFieldDataByCondition(maintable, mainstatuscodeField, mainkeyField + "=" + id);
		if (!status.equals("CANUSE")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("Page.banned_onlyCanuse"));
		}
		// 执行禁用前的其它逻辑
		handlerService.handler(caller, "banned", "before", new Object[] { id });
		// 执行禁用操作
		baseDao.banned(maintable, mainkeyField + "=" + id, mainstatusField, mainstatuscodeField);
		// 记录操作
		try {
			baseDao.logger.banned(caller, mainkeyField, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行禁用后的其它逻辑
		handlerService.handler(caller, "banned", "after", new Object[] { id });

	}

	@Override
	public void resBannedPage(String caller, int id) {
		Object status = baseDao.getFieldDataByCondition(maintable, mainstatuscodeField, mainkeyField + "=" + id);
		if (!status.equals("DISABLE")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("Page.resBanned_onlyBanned"));
		}
		// 执行反禁用操作
		baseDao.resOperate(maintable, mainkeyField + "=" + id, mainstatusField, mainstatuscodeField);
		// 记录操作
		try {
			baseDao.logger.resBanned(caller, mainkeyField, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	@Transactional
	public String turnBankRegister(int id, String paymentcode, String payment, double thispayamount) {
		Object status = baseDao.getFieldDataByCondition("CUSTOMTABLE", "CT_STATUSCODE", "CT_ID=" + id);
		Double debit = 0.0;
		if (!status.equals("AUDITED")) {
			BaseUtil.showError("审核后才能转银行登记!");
		}
		// 更新科目信息
		if (paymentcode != null && !"".equals(paymentcode)) {
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, paymentcode);
			if (error != null) {
				BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，不允许转应付票据！");
			}
			error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(CA_ISCASHBANK,0)=0", String.class, paymentcode);
			if (error != null) {
				BaseUtil.showError("付款科目有误，请填写银行现金科目！");
			}
			baseDao.execute("update CUSTOMTABLE set ct_varchar50_11=?,ct_varchar500_4=? where CT_ID=?", paymentcode, payment, id);
		}
		boolean success = false;
		int ar_id = baseDao.getSeqId("ACCOUNTREGISTER_SEQ");
		String code = baseDao.sGetMaxNumber("AccountRegister", 2);
		Object[] cate = baseDao.getFieldsDataByCondition(
				"Category left join currencysmonth on ca_currency=cm_crname and cm_yearmonth=to_char(sysdate,'yyyymm')", new String[] {
						"ca_id", "ca_currency", "nvl(cm_crrate,0)", "ca_description" }, "ca_code='" + paymentcode + "'");
		payment = cate[3].toString();
		double oldamount = thispayamount;
		Double fprate = 0.0;
		Object currency = baseDao.getFieldDataByCondition("CUSTOMTABLE", "ct_varchar50_1", "ct_id=" + id);
		if (currency != null && cate[1] != null) {
			if (!currency.equals(cate[1])) {
				fprate = baseDao.getFieldValue("Currencysmonth", "nvl(cm_crrate,0)", "cm_crname='" + currency
						+ "' and cm_yearmonth=to_char(sysdate, 'yyyymm')", Double.class);
				// 月度汇率为空则提示
				if (fprate == null) {
					BaseUtil.showError("月度汇率未设置，请先设置!");
				}

				if (Double.parseDouble(cate[2].toString()) != 0) {
					thispayamount = NumberUtil.formatDouble(thispayamount * fprate / Double.parseDouble(cate[2].toString()), 2);
				} else {
					thispayamount = NumberUtil.formatDouble(thispayamount * fprate, 2);
				}
			}
		}
			Object[] data = baseDao.getFieldsDataByCondition("CUSTOMTABLE left join department on ct_depart=dp_name", new String[] {
					"CT_CODE", "ct_amount", "ct_depart", "dp_code", "ct_varchar2000_4", "ct_person", "ct_number_3", "ct_emcode", "ct_varchar2000_1" }, "ct_id=" + id);
			String insertSql = "insert into AccountRegister ("
					+ "ar_id,ar_recorddate,ar_date,ar_payment,ar_type,"
					+ "ar_code,ar_sourceid,ar_source,ar_sourcetype,ar_statuscode,ar_status,ar_recordman,"
					+ "ar_accountcode,ar_accountname,ar_accountcurrency,ar_cateid,ar_departmentcode,ar_departmentname,ar_pleaseman,ar_memo,ar_apamount) values("
					+ "?,sysdate,sysdate,?,?,?,?,?,?,'ENTERING',?,?,?,?,?,?,?,?,?,?,?)";
			String remark = data[5] + "费用报销比例（" + data[4] + "）";// 银行登记备注默认为申请人+费用报销比例+原单据备注，若原备注为空，则备注为申请人+费用报销比例
			if (!StringUtil.hasText(data[4])) {
				remark = data[5] + "报销";
			}
			remark = remark + " " + data[8];// 银行登记备注添加费用报销的【费用内容】
			baseDao.execute(insertSql,
					new Object[] { ar_id, thispayamount, "费用", code, id, data[0], "费用比例报销单", BaseUtil.getLocalMessage("ENTERING"),
							SystemSession.getUser().getEm_name(), paymentcode, payment, cate[1], cate[0], data[3], data[2], data[5],
							remark, oldamount });
			baseDao.execute("update accountregister set ar_accountrate=(select nvl(cm_crrate,0) from currencysmonth where ar_accountcurrency=cm_crname and to_char(ar_date,'yyyymm')=cm_yearmonth) where ar_code='"
					+ code + "'");
			String insertDetSql = "insert into AccountRegisterDetail ("
					+ "ard_detno,ard_debit,ard_id,ard_arid,ard_explanation,ard_catecode) values (?,?,?,?,?,?)";
			List<Object[]> detData = baseDao.getFieldsDatasByCondition("CUSTOMTABLEDETAIL left join Category on cd_varchar50_6=ca_code",
					new String[] { "CD_DETNO", "cd_varchar50_5", "cd_amount", "cd_varchar50_6", "ca_currency" }, "CD_CTID=" + id);
			for (Object[] o : detData) {
				if(o[2]==null||"".equals(o[2].toString().trim())){
					debit=0.0;
				}else{
				if (Double.parseDouble(cate[2].toString()) != 0) {
					debit = NumberUtil.formatDouble(Double.parseDouble(o[2].toString()) * fprate / Double.parseDouble(cate[2].toString()), 2);
				} else {
					debit = NumberUtil.formatDouble(Double.parseDouble(o[2].toString()) * fprate, 2);
				}
				}
				int ardid = baseDao.getSeqId("ACCOUNTREGISTERDETAIL_SEQ");
				baseDao.execute(insertDetSql, new Object[] {  o[0], debit, ardid, ar_id, o[1], o[3] });
				if (data[3] != null) {
					String sql = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) "
							+ "select accountregisterdetailass_seq.nextval,ard_id,1,'部门','"
							+ data[3]
							+ "','"
							+ data[2]
							+ "','AccountRegister!Bank' from accountregisterdetail,category where ard_catecode=ca_code and (ca_asstype like '%Dept%') and ard_id="
							+ ardid;
					String sql2 = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) "
							+ "select accountregisterdetailass_seq.nextval,ard_id,2,'员工','"
							+ data[7]
							+ "','"
							+ data[5]
							+ "','AccountRegister!Bank' from accountregisterdetail,category where ard_catecode=ca_code and (ca_asstype like '%Empl%') and ard_id="
							+ ardid;
					baseDao.execute(sql);
					baseDao.execute(sql2);
				}
			}
			String adidstr = "";
			adidstr += "," + id;
			if (!adidstr.equals("")) {
				adidstr = adidstr.substring(1);
			}
			baseDao.execute("update accountregisterdetail set ard_catedesc=(select ca_description from category where ard_catecode=ca_code) where ard_arid="
					+ ar_id + " and nvl(ard_catecode,' ')<>' '");
			baseDao.execute("update accountregister set ar_currencytype=(select max(ca_currencytype) from category where ca_code in (select ard_catecode from accountregisterdetail where ar_id=ard_arid and nvl(ard_catecode,' ')<>' ')) where ar_id="
					+ ar_id);
			SqlRowList rs = baseDao.queryForRowSet("select * from accountregister where ar_id=? and nvl(ar_currencytype,0)<>0", ar_id);
			if (rs.next()) {
				baseDao.execute("update accountregisterdetail set ard_currency=(select ct_varchar50_1 from CUSTOMTABLE where ct_id in (" + id
						+ ")) where ard_arid=" + ar_id);
				baseDao.execute("update accountregisterdetail set ard_rate=(select nvl(cm_crrate,0) from currencysmonth where cm_yearmonth=to_char(sysdate,'yyyymm') and ard_currency=cm_crname) where ard_arid="
						+ ar_id + " and nvl(ard_currency,' ')<>' '");
				baseDao.execute("update accountregisterdetail set ard_doubledebit=ard_debit where ard_arid=" + ar_id
						+ " and nvl(ard_currency,' ')<>' '");
				baseDao.execute("update accountregisterdetail set ard_doublecredit=ard_credit where ard_arid=" + ar_id
						+ " and nvl(ard_currency,' ')<>' '");
				baseDao.execute("update accountregisterdetail set ard_debit=round(ard_doubledebit*ard_rate,2),ard_credit=round(ard_doublecredit*ard_rate,2) where ard_arid="
						+ ar_id + " and nvl(ard_currency,' ')<>' '");
			}
			success = true;
		if (success) {
			baseDao.updateByCondition("CUSTOMTABLE", "ct_number_2=ct_number_2+" + oldamount, "ct_id=" + id);
			baseDao.execute("update CUSTOMTABLE set ct_varchar50_13='已支付' where nvl(ct_amount,0)=nvl(ct_number_2,0)+nvl(ct_number_3,0) and ct_id=" + id);
			baseDao.execute("update CUSTOMTABLE set ct_varchar50_13='部分支付' where nvl(ct_amount,0)>nvl(ct_number_2,0)+nvl(ct_number_3,0) and ct_id=" + id);
			baseDao.execute("update CUSTOMTABLE set ct_varchar50_13='未支付' where nvl(ct_number_2,0)+nvl(ct_number_3,0)=0 and ct_id=" + id);
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "转银行登记,本次金额为:" + thispayamount, "转入成功", "FYBLRATE"
					+ "|ct_id=" + id));
			accountRegisterBankService.updateErrorString(ar_id);
			return "转入成功,银行登记单号:<a href=\"javascript:openUrl('jsps/fa/gs/accountRegister.jsp?formCondition=ar_idIS" + ar_id
					+ "&gridCondition=ard_aridIS" + ar_id + "&whoami=AccountRegister!Bank')\">" + code + "</a>&nbsp;";
		}
		return "";
	}

	@Override
	public void postPage(String caller, int id) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield" }, "fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		if (objs != null) {
			// 只能对状态为[已审核]的单据进行过账操作!
			Object status = baseDao.getFieldDataByCondition((String) objs[0], (String) objs[3], (String) objs[1] + "=" + id);
			if (!status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil.getLocalMessage("Page.post_onlyEntering"));
			}
			// 执行过账前的其它逻辑
			handlerService.handler(caller, "post", "before", new Object[] { id });
			// 执行过账操作
			// 存储过程
			Object[] vals = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_class", "pi_inoutno" }, "pi_id=" + id);
			String res = baseDao.callProcedure("Sp_CommitProdInout",
					new Object[] { vals[0].toString(), vals[1].toString(), String.valueOf(SystemSession.getUser().getEm_id()) });
			if (res != null && !res.trim().equals("")) {
				BaseUtil.showError(res);
			}
			baseDao.updateByCondition((String) objs[0], objs[3] + "='POSTED'," + objs[2] + "='" + BaseUtil.getLocalMessage("POSTED") + "'",
					objs[1] + "=" + id);
			// 记录操作
			try {
				baseDao.logger.post(caller, objs[1].toString(), id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 执行过账后的其它逻辑
			handlerService.afterPost(caller, new Object[] { id });
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("post_tableisnull"));
		}

	}

	@Override
	public void confirmPage(String caller, int id) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield" }, "fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		// 先判断物料对照关系，如果没有建立对照关系，则不允许确认操作
		String prodcode = "";
		String detno = "";
		String prodcustcode = "";
		int sdid = 0;
		int pdid = 0;
		String prodvendcode = "";
		if (objs != null) {
			if (objs[0].equals("Sale")) {
				String selectSQL = "select sd_prodcode,sd_detno,sd_prodcustcode,sd_id from SaleDetail where sd_said='" + id + "'";
				SqlRowList rs = baseDao.queryForRowSet(selectSQL);
				while (rs.next()) {
					prodcode = rs.getString("sd_prodcode");
					detno = rs.getString("sd_detno").toString();
					prodcustcode = rs.getString("sd_prodcustcode");
					sdid = rs.getInt("sd_id");
					// 去物料资料对照表判断是否建立对应关系
					String matchSQL = "select pm_prodcustcode from ProductMatch where pm_prodcode='" + prodcode + "'";
					SqlRowList rss = baseDao.queryForRowSet(matchSQL);
					if (!rss.next()) {
						// 给出提示信息
						BaseUtil.showError("序号" + detno + "物料" + prodcode + "没有建立物料对应关系！");
						return;
					} else {
						// 先判断ERP本身的料号是否为空
						if (prodcode.equals(null) || prodcode.equals("")) {
							// 更新ERP本身的料号，根据客户物料号去ProductMatch查找物料编号
							String erpprodcode = baseDao.getFieldDataByCondition("ProductMatch", "pm_prodcode",
									"pm_prodcustcode='" + prodcustcode + "'").toString();
							String updateSQL = "update SaleDetail set sd_prodcode='" + erpprodcode + "' where sd_id='" + sdid + "'";
							baseDao.execute(updateSQL);
						}
					}
				}
			}
			if (objs[0].equals("Purchase")) {
				String selectSQL = "select pd_prodcode,pd_detno,pd_prodvendcode,pd_id from PurchaseDetail where pd_puid='" + id + "'";
				SqlRowList rs = baseDao.queryForRowSet(selectSQL);
				while (rs.next()) {
					prodcode = rs.getString("pd_prodcode");
					detno = rs.getString("pd_detno");
					pdid = rs.getInt("pd_id");
					prodvendcode = rs.getString("sd_prodvendcode");
					// 去物料资料对照判断是否建立对应关系
					String matchSQL = "select pm_prodvendcode from ProductMatch where pm_prodcode='" + prodcode + "'";
					SqlRowList rss = baseDao.queryForRowSet(matchSQL);
					if (!rss.next()) {
						// 给出提示信息
						BaseUtil.showError("序号" + detno + "物料" + prodcode + "没有建立物料对应关系！");
						return;
					} else {
						// 先判断ERP本身的料号是否为空
						if (prodcode.equals("") || prodcode.equals(null)) {
							// 更新ERP本身的料号，根据客户物料号去ProductMatch查找物料编号
							String erpprodcode = baseDao.getFieldDataByCondition("ProductMatch", "pm_prodcode",
									"pm_prodvendcode='" + prodvendcode + "'").toString();
							String updateSQL = "update PurchaseDetail set pd_prodcode='" + erpprodcode + "' where pd_id='" + pdid + "'";
							baseDao.execute(updateSQL);
						}
					}
				}
			}
		}
		if (objs != null) {
			// 更新单据状态，录入人，客户/供应商编号,更改上传状态(从B2B下载下来的PO就不需要再上传)
			if (objs[0].equals("Sale")) {
				String updateSQL = "update Sale set sa_confirmstatus='已确认',sa_status='在录入',sa_recorder='"
						+ SystemSession.getUser().getEm_name() + "',sa_uploadstatus='B2BDownLoad' where sa_id='" + id + "'";
				try {
					baseDao.execute(updateSQL);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 根据客户UU号更新对应的客户编号
				try {
					String uu = String.valueOf(baseDao.getFieldDataByCondition("Sale", "sa_customeruu", "sa_id='" + id + "'"));
					String custcode = String.valueOf(baseDao.getFieldDataByCondition("Customer", "cu_code", "cu_uu='" + uu + "'"));
					String thesql = "update Sale set sa_custcode='" + custcode + "' where sa_id='" + id + "'";
					baseDao.execute(thesql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (objs[0].equals("Purchase")) {
				String updateSQL = "update Purchase set pu_confirmstatus='已确认',pu_status='在录入',pu_recordman='"
						+ SystemSession.getUser().getEm_name() + "',pu_sendstatus='B2BDownLoad' where pu_id='" + id + "'";
				try {
					baseDao.execute(updateSQL);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 根据供应商UU号更新对应的供应商编号
				try {
					String uu = String.valueOf(baseDao.getFieldDataByCondition("Purchase", "pu_vendoruu", "pu_id='" + id + "'"));
					String custcode = String.valueOf(baseDao.getFieldDataByCondition("Vendor", "ve_code", "ve_uu='" + uu + "'"));
					String thesql = "update Purchase set pu_vendcode='" + custcode + "' where pu_id='" + id + "'";
					baseDao.execute(thesql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 记录操作
			try {
				baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("msg.onConfirm"), BaseUtil.getLocalMessage("msg.onConfirmSuccess"),
						caller, objs[1].toString(), id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void IfDatalist(String caller) {
		Object datalist = dataListDao.getDataList(caller, SpObserver.getSp());
		if (datalist == null) {
			BaseUtil.showError("未配置列表!");
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@CacheEvict(value = "datalist", allEntries = true)
	public void ToDataListByForm(String caller, String type) {
		Form form = formDao.getForm(caller, SpObserver.getSp());
		List<FormDetail> details = form.getFormDetails();
		int dl_id = baseDao.getSeqId("DATALIST_SEQ");
		String fieldtype = "";
		List<String> sqls = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("insert into datalist (dl_id,dl_caller,dl_tablename,dl_pagesize,dl_title,dl_lockpage,dl_pffield,dl_orderby,dl_keyfield,dl_condition,dl_entryfield)values(");
		sb.append("'" + dl_id + "','" + caller + "','" + maintable + "',25,'" + form.getFo_title() + "列表','");
		if (type.equals("single")) {
			sb.append("jsps/oa/custom/singleform.jsp?whoami=" + caller + "',null,'");
		} else
			sb.append("jsps/oa/custom/maindetail.jsp?whoami=" + caller + "','" + detailmainkeyField + "','");
		sb.append("order by " + mainkeyField + " desc','" + mainkeyField + "','ct_caller=''" + caller + "''','CT_RECORDER@N')");
		sqls.add(sb.toString());
		for (FormDetail detail : details) {
			fieldtype = detail.getFd_type();
			if (!"H".equals(fieldtype) && detail.getFd_columnwidth() != 0) {
				sb.setLength(0);
				sb.append("insert into datalistdetail(dld_id,dld_caller,dld_detno,dld_field,dld_caption,dld_width,dld_fieldtype,dld_table,dld_editable,dld_dlid)values(");
				sb.append(baseDao.getSeqId("DATALISTDETAIL_SEQ") + ",'" + caller + "','" + detail.getFd_detno() + "','"
						+ detail.getFd_field() + "','");
				sb.append(detail.getFd_caption() + "',120,'" + fieldtype + "','" + maintable + "',0," + dl_id + ")");
				sqls.add(sb.toString());
			}
		}
		baseDao.execute(sqls);
		int count=baseDao.getCount("select count(1) from datalistdetail where dld_dlid =(select dl_id from datalist "
				+ "where dl_caller='"+caller+"') and UPPER(dld_field)='CT_ID'");
		if(count==0){
			baseDao.execute("insert into datalistdetail(dld_id,dld_caller,dld_detno,dld_field,dld_caption,dld_width,dld_fieldtype,dld_table,dld_editable,dld_dlid)values("
					+ baseDao.getSeqId("DATALISTDETAIL_SEQ") + ",'" + caller + "',1" +",'CT_ID','ID',0,'H','"+maintable+"',0,"+dl_id+")");
		}
	}

	@Override
	public void orderByJprocess(String data, String caller) {
		List<Map<Object, Object>> datas = BaseUtil.parseGridStoreToMaps(data);
		baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(datas, "JPROCESSDEPLOY", "jd_id"));
	}

	@Override
	public void confirm(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition(maintable, mainstatuscodeField, mainkeyField + "=" + id);
		StateAssert.resAuditOnlyAudit(status);		
		baseDao.updateByCondition(maintable, "ct_confirmstatus='已确认',ct_confirmstatuscode='CONFIRMED'", mainkeyField+"=" + id);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.confirm"), BaseUtil
				.getLocalMessage("msg.confirmSuccess"), caller + "|" +mainkeyField+ "=" + id));
	}

	@Override
	public void resConfirm(int id, String caller) {
		baseDao.updateByCondition(maintable, "ct_confirmstatus='未确认',ct_confirmstatuscode='UNCONFIRMED'", mainkeyField+"=" + id);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.resConfirm"), BaseUtil
				.getLocalMessage("msg.resConfirmSuccess"), caller + "|" +mainkeyField+ "=" + id));
	}

	@Override
	public void submitApproves(String caller, int id) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition(maintable, mainstatuscodeField, mainkeyField + "=" + id);
		
		StateAssert.resAuditOnlyAudit(status);
		// 执行提交操作
		baseDao.submit(maintable, mainkeyField + "=" + id, approvesstatusField, approvesstatuscodeField);
		// 记录操作
		try {
			baseDao.logger.submit(caller.indexOf("!Confirm")>0?caller.replace("!Confirm", ""):caller, mainkeyField, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行审核后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { id });
		
	}

	@Override
	public void resSubmitApproves(String caller, int id) {
		// TODO Auto-generated method stub

		Object status = baseDao.getFieldDataByCondition(maintable, approvesstatuscodeField, mainkeyField + "=" + id);
		
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		handlerService.beforeResSubmit(caller, new Object[] { id });
		baseDao.resOperate(maintable, mainkeyField + "=" + id, approvesstatusField, approvesstatuscodeField);
		// 记录操作
		try {
			baseDao.logger.resSubmit(caller.indexOf("!Confirm")>0?caller.replace("!Confirm", ""):caller, mainkeyField, id);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterResSubmit(caller, new Object[] { id });

	}

	@Override
	public void approvePage(int id, String caller) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition(maintable, approvesstatuscodeField, mainkeyField + "=" + id);		
		Assert.isEquals("只能批准已经提交(批准)的单据", Status.COMMITED.code(), status);
		// 执行反提交操作		
		handlerService.handler(caller, "approve", "before", new Object[] { id });		
		String sql="update customtable set ct_approvestatuscode='APPROVE',ct_approvestatus='"+BaseUtil.getLocalMessage("APPROVE")+"',ct_approveman='"+SystemSession.getUser().getEm_name()+"',ct_approvedate=sysdate where ct_id='"+id+"'";
		baseDao.execute(sql);
		// 记录操作
		try {
			baseDao.logger.approve(caller.indexOf("!Confirm")>0?caller.replace("!Confirm", ""):caller, mainkeyField, id);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.handler(caller, "approve", "after", new Object[] { id });
		
	}
	/**
	 * 博信  新增OA单转销售报价、物料核价单。 maz
	 */
	@Override
	public void turnPage(int id,String caller,String data){
		if(data!=null && "Sale".equals(data)){
			handlerService.handler(caller, "turnSale", "after", new Object[] { id });
		}else if(data!=null && "Pur".equals(data)){
			handlerService.handler(caller, "turnPur", "after", new Object[] { id });
		}
	}
	/**
	 * 高凌  新增OA单 文档借用转归还 wuyx
	 */
	@Override
	public String turnDocPage(int id,String caller,String data){
			try {
				List<Map<Object,Object>> detailList = BaseUtil.parseGridStoreToMaps(data);
				Employee employee  = SystemSession.getUser();
				String ids ="";
				for (int i = 0; i < detailList.size(); i++) {
					ids+=detailList.get(i).get("CD_ID")+",";
				}
				ids = ids.substring(0,ids.length()-1);
				
				String res = baseDao.callProcedure("SP_TURNDOCRETURN", id,ids,employee.getEm_name());
				
				if (res != null && !res.trim().equals("")) {
					if(res.indexOf("转入成功")>=0){
						return res;
					}else{ 
						BaseUtil.showError(res);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
			return "";
	}
	
	@Override
	public void endPage(String caller, int id) {

			// 执行禁用前的其它逻辑
			handlerService.handler(caller, "end", "before", new Object[] { id });
			// 执行禁用操作
			baseDao.updateByCondition("CUSTOMTABLE",
					"CT_STATUSCODE='FINISH'," + "CT_STATUS='" + BaseUtil.getLocalMessage("FINISH") + "'", "CT_ID=" + id);
			// 记录操作
			try {
				baseDao.logger.end(caller, "CT_ID", id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 执行禁用后的其它逻辑
			handlerService.handler(caller, "end", "after", new Object[] { id });
	
	}

	@Override
	public void resEndPage(String caller, int id) {
			// 执行反禁用操作
			baseDao.updateByCondition("CUSTOMTABLE",
					 "CT_STATUSCODE='ENTERING'," + "CT_STATUS='" + BaseUtil.getLocalMessage("ENTERING") + "'", "CT_ID=" + id);
			// 记录操作
			try {
				baseDao.logger.resEnd(caller, "CT_ID", id);
			} catch (Exception e) {
				e.printStackTrace();
			}
	
	}	
	
}
