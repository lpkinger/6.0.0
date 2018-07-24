package com.uas.mobile.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.oa.VacationService;
import com.uas.mobile.service.OAMeetingService;

@Service("mobileOAMeetingService")
public class OAMeetingServiceImpl implements OAMeetingService {
	public static String menuConfigField = "mm_caller,mm_name,mm_link";
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private VacationService vacationService;
	
	@Override
	public List<Map<String,Object>> getMenuConfig(String condition){
		return baseDao.queryForList("select " + menuConfigField + " from mobile_menuconfig" + (condition==null?"":" where " + condition));
	}
	@Override
	public List<Map<String,Object>> getoaconifg(){
		return baseDao.queryForList("select mo_name,mo_caller from mobile_oaconifg where 1=1");
	}
	@Override
	public Map<String, Object> getMeetingDetailParticipants(String ma_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String sql1 = "select em_id,em_code,em_name,em_depart,em_defaulthsname from MeetingDetail left join Meetingroomapply on ma_id=md_maid left join employee on em_code=md_emcode where nvl(md_isconfirmed,0)=-1 and ma_code='"+ma_code+"'";
		List<Map<String, Object>> confirmed=baseDao.getJdbcTemplate().queryForList(sql1);
		
		String sql2 = "select em_id,em_code,em_name,em_depart,em_defaulthsname from MeetingDetail left join Meetingroomapply on ma_id=md_maid left join employee on em_code=md_emcode where nvl(md_isconfirmed,0)=0 and ma_code='"+ma_code+"'";
		List<Map<String, Object>> unconfirmed=baseDao.getJdbcTemplate().queryForList(sql2);		
		modelMap.put("confirmed",confirmed);
		modelMap.put("unconfirmed",unconfirmed);
		return modelMap;
	}

	@Override
	public void meetingSignMobile(String em_code, String ma_code, String caller) {
		String updateSQL="update meetingdetail set md_isconfirmed=-1 where md_maid="+ma_code+" and md_emcode='"+em_code+"'";
		baseDao.execute(updateSQL);
	}

	@Override
	public void saveOutSign(String formStore, String caller) {
		//Map<Object, Object> store = JSONUtil.toMap(formStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存主表
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "mobile_outsign",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });		
	}

	@Override
	public List<Map<String, Object>> workdata(String condition) {
		// TODO Auto-generated method stub
		/*Map<String, Object> modelMap = new HashMap<String, Object>();
		String sql1 = "select to_char(cl_time,'yyyy-mm-dd hh24:mi:ss') cl_time  from cardlog where "+condition+" order by cl_time ASC";
		System.out.println(sql1);
		List<Map<String, Object>> lisdata=baseDao.getJdbcTemplate().queryForList(sql1);
		modelMap.put("lisdata",lisdata);*/
	    
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		String sql = "select to_char(cl_time,'yyyy-MM-dd hh24:mi:ss'),'false' apprecord from cardlog where "
				+ condition
				+ " union select to_char(ms_signtime,'yyyy-MM-dd hh24:mi:ss'),'true' apprecord from mobile_signcard where "
				+ condition.replace("cl_emcode", "ms_emcode").replace(
						"cl_time", "ms_signtime") + "";
		SqlRowSet rs=baseDao.getJdbcTemplate().queryForRowSet(sql);
		while(rs.next()){
			map = new HashMap<String, Object>();
			map.put("cl_time", rs.getString(1));
			map.put("apprecord", rs.getString(2));
			lists.add(map);
		}
		return lists;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String,Object> saveAndSubmitAskLeave(String caller,String formStore){
		Map<String,Object> modelMap = new HashMap<String,Object>();

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		String code = baseDao.sGetMaxNumber("VACATION", 2);
		int id = baseDao.getSeqId("VACATION_SEQ");
		
		if(store.get("va_code")==null||"".equals(store.get("va_code"))){
			store.put("va_code", code);
		}
		if(store.get("va_id")==null||"".equals(store.get("va_id"))){
			store.put("va_id", id);
		}
		if(store.get("va_statuscode")==null||"".equals(store.get("va_statuscode"))){
			store.put("va_statuscode", "ENTERING");
		}
		if(store.get("va_status")==null||"".equals(store.get("va_status"))){
			store.put("va_status", "在录入");
		}
		
		modelMap.put("va_id",id);
		
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VACATION",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "va_id", store.get("va_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store });

		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("vacation", "va_statuscode", "va_id=" + store.get("va_id"));
		if(status==null){
			BaseUtil.showError("保存失败!");
		}
		StateAssert.submitOnlyEntering(status);

		//判断是否启用延期限制提交
		Object time = store.get("va_startime");
		commitNeedCheck(caller,id,time);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{id});
		//执行提交操作
		baseDao.submit("Vacation", "va_id=" + id, "va_status", "va_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "va_id", id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{id});
		
		return modelMap;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String,Object> saveAndSubmitFYBX(String caller,String formStore,String gridStore,String gridStore2,String emcode,String emname){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Employee employee = employeeDao.getEmployeeByEmcode(emcode);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		String code = baseDao.sGetMaxNumber("FeePlease", 2);
		int id = baseDao.getSeqId("FeePlease_seq");
		store.put("fp_id", id);
		store.put("fp_code", code);
		modelMap.put("fp_id", id);
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		store.put("fp_printstatuscode", "UNPRINT");
		 Date dt=new Date();
	     SimpleDateFormat matter1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		store.put("fp_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
			if(store.get("fp_billdate")==null)store.put("fp_billdate", matter1.format(dt));
			if(store.get("fp_recordman")==null)store.put("fp_recordman", emname);
			if(store.get("fp_status")==null)store.put("fp_status", BaseUtil.getLocalMessage("ENTERING"));
			if(store.get("fp_statuscode")==null)store.put("fp_statuscode", "ENTERING");
			if(store.get("fp_recorddate")==null)store.put("fp_recorddate", matter1.format(dt));
			if(store.get("fp_pleasemancode")==null)store.put("fp_pleasemancode ", emcode);
			if(store.get("fp_pleaseman")==null)store.put("fp_pleaseman", emname);
			if(store.get("fp_department")==null)store.put("fp_department", employee.getEm_depart());
			if(store.get("fp_v7")==null)store.put("fp_v7", "未支付");	
			if(store.get("fp_kind")==null)store.put("fp_kind", "费用报销单");

		if ("FeePlease!FYBX".equals(caller)) {// 费用报销
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select FP_SUMWGH,FP_THISHKAMOUNT from FeePlease where fp_id=? and nvl(FP_SUMWGH,0)>0 and nvl(FP_THISHKAMOUNT,0)<nvl(FP_SUMWGH,0)",
							store.get("fp_id"));
			if (rs.next()) {
				/*BaseUtil.appendError("报销人还有借款未归还，请先冲销借款金额！本次还款总额(本位币)[" + rs.getGeneralDouble("FP_THISHKAMOUNT") + "],总的未归还金额(本位币)["
						+ rs.getGeneralDouble("FP_SUMWGH") + "]");*/
			}
			/**
			 * fp_n1 借款申请单已转金额 sum(fb_back)已提交和已审核的费用报销单中还款金额的和
			 * sum(fp_pleaseamount)已提交和已审核的还款申请单还款金额的和 结果fee:待还款金额
			 * */
			/*
			 * String sql = "select sum(p.fp_n1- " +
			 * "nvl((select sum(FB_BACK) from feeback left join FEEPLEASE on FB_FPID=FP_ID where FP_STATUSCODE in('AUDITED','COMMITED') and FB_JKSQCODE=p.fp_code),0)"
			 * +
			 * "-nvl((select sum(fp_pleaseamount) from feeplease where fp_sourcecode=p.fp_code and FP_STATUSCODE in ('AUDITED','COMMITED')),0))"
			 * + "from feeplease p where p.fp_pleasemancode='" +
			 * store.get("fp_pleasemancode") +
			 * "' and p.fp_kind='借款申请单' and p.fp_statuscode='AUDITED'";
			 * SqlRowList rs = baseDao.queryForRowSet(sql); if (rs.next()) {
			 * double fee = rs.getGeneralDouble(1, 6); if (fee > 0)
			 * BaseUtil.appendError("该报销人有未还款的借款单！待还款总额为：" + fee); }
			 */
		}
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FeePlease", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存FeePleaseDETAIL
		int i=1;
		for (Map<Object, Object> map : grid) {
			map.put("fpd_id", baseDao.getSeqId("FEEPLEASEDETAIL_SEQ"));
			map.put("fpd_code", store.get("fp_code"));
			map.put("fpd_class", store.get("fp_kind"));
			map.put("fpd_fpid", store.get("fp_id"));			
			map.put("fpd_detno", i);
			i++;
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "FeePleaseDETAIL");
		baseDao.execute(gridSql);
		// 更新明细表金额字段为2位小数
		// "update FeePleaseDETAIL set fpd_n2=round(nvl(fpd_n2,0),2),fpd_n1=round(nvl(fpd_n1,0),2),fpd_total=round(nvl(fpd_total,0),2) where FPD_FPID="+store.get("fp_id");
		String updatesql = "update FeePleaseDETAIL set fpd_n2=round(nvl(fpd_n2,0),2),fpd_n1=round(nvl(fpd_n1,0),2),fpd_total=round(nvl(fpd_total,0),2) where fpd_fpid='"
				+ store.get("fp_id") + "'";
		baseDao.execute(updatesql);
		// 保存还款明细
		if (gridStore2 != null) {
			List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(gridStore2);
			for (Map<Object, Object> map : grid2) {
				// 累计还款总额应小于借款单已转金额
				String sql = "select (fp_n1- "
						+ "nvl((select sum(FB_BACK) from feeback left join FEEPLEASE on FB_FPID=FP_ID where FP_STATUSCODE in('AUDITED','COMMITED') and FB_JKSQCODE='"
						+ map.get("fb_jksqcode") + "'),0)" + "-nvl((select sum(fp_pleaseamount) from feeplease where fp_sourcecode='"
						+ map.get("fb_jksqcode") + "' and FP_STATUSCODE in ('AUDITED','COMMITED')),0))fee "
						+ "from feeplease where fp_code='" + map.get("fb_jksqcode") + "' and fp_kind='借款申请单'";
				SqlRowList rs = baseDao.queryForRowSet(sql);
				if (rs.next()) {
					double fee = rs.getGeneralDouble(1, 6);
					if (Double.parseDouble(map.get("fb_back").toString()) > fee) {
						BaseUtil.appendError("还款明细第" + map.get("fb_detno") + ",待还款金额为:" + fee + "<br>");
					}
				}
				map.put("fb_id", baseDao.getSeqId("feeback_SEQ"));
			}
			List<String> gridSql2 = SqlUtil.getInsertSqlbyGridStore(grid2, "feeback");
			baseDao.execute(gridSql2);
		}
		getSumTotal(caller, store.get("fp_id"));
		// 记录操作
		baseDao.logger.save(caller, "fp_id", store.get("fp_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });

		Object fpd_id = baseDao.getFieldsDatasByCondition("FeePleaseDetail", new String[] { "fpd_id", "fpd_detno" },
				"fpd_fpid=" + store.get("fp_id"));
		modelMap.put("fpd_id", fpd_id);	
		getSumTotal(caller, store.get("fp_id"));
		Object status = baseDao.getFieldDataByCondition("FeePlease", "fp_statuscode", "fp_id=" + store.get("fp_id"));
		StateAssert.submitOnlyEntering(status);
		if ("FeePlease!FYBX".equals(caller)) {
			SqlRowList rs = baseDao.queryForRowSet(
					"select fp_amount,FP_THISHKAMOUNT from FeePlease where fp_id=? and nvl(fp_amount,0)<nvl(FP_THISHKAMOUNT,0)", store.get("fp_id"));
			if (rs.next()) {
				BaseUtil.showError("费用报销报销总额必须大于等于本次还款总额！报销总额(本位币)[" + rs.getGeneralDouble("fp_amount") + "],本次还款总额(本位币)["
						+ rs.getGeneralDouble("FP_THISHKAMOUNT") + "]");
			}
			List<Object[]> res = baseDao.getFieldsDatasByCondition("feeback", new String[] { "FB_JKSQCODE", "fb_back", "fb_detno" },
					"fb_fpid=" + store.get("fp_id"));
			if (res.size() != 0) {// 累计还款总额应小于借款单已转金额
				for (Object[] re : res) {
					String sql = "select (fp_n1- "
							+ "nvl((select sum(FB_BACK) from feeback left join FEEPLEASE on FB_FPID=FP_ID where FP_STATUSCODE in('AUDITED','COMMITED') and FB_JKSQCODE='"
							+ re[0].toString() + "'),0)" + "-nvl((select sum(fp_pleaseamount) from feeplease where fp_sourcecode='"
							+ re[0].toString()
							+ "' and fp_sourcekind in ('出差申请单','借款申请单') and FP_STATUSCODE in ('AUDITED','COMMITED')),0))fee "
							+ "from feeplease where fp_code='" + re[0].toString() + "' and fp_kind='借款申请单'";
					SqlRowList rs1 = baseDao.queryForRowSet(sql);
					if (rs1.next()) {
						double fee = rs1.getGeneralDouble(1, 6);
						if (Double.parseDouble(re[1].toString()) > fee) {
							BaseUtil.showError("还款明细第" + re[2].toString() + "行还款金额超过待还款金额，待还款金额为:" + fee);
						}
					}
				}
			}
			// 费用报销的提交逻辑
			if (baseDao.isDBSetting(caller,"comitLogicfeeCategory")) {
				comitLogic_feeCategory(store.get("fp_id"));
			}
			/*
			 * if (baseDao.isDBSetting(caller, "comitLogicfeelimit")) {
			 * comitLogic_feelimit(fp_id, caller); }
			 */
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { store.get("fp_id") });
		// 执行提交操作
		baseDao.submit("FeePlease", "fp_id=" + store.get("fp_id"), "fp_status", "fp_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "fp_id", store.get("fp_id"));
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { store.get("fp_id") });
	
		return modelMap;// 手机端需求
	
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String,Object> commonSaveAndSubmit(String caller,String formStore,String gridStore,String emcode,String emname){
		Employee employee = employeeDao.getEmployeeByEmcode(emcode);
		
		boolean detailsetting = StringUtil.hasText(gridStore)&&!"[]".equals(gridStore)&&!"{}".equals(gridStore);
		if("FeePlease!CCSQ".equals(caller)||"Workovertime".equals(caller)||"MainTain".equals(caller)||"MaterielApply".equals(caller)||"StandbyApplication".equals(caller)||"FeePlease!CCSQ!new".equals(caller)||"FeePlease!FYBX".equals(caller)){
			detailsetting = true; //表示有无明细表
		}
		
		Map<String,Object> modelMap = new HashMap<String,Object>();

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if("FeePlease!CCSQ".equals(caller)||"FeePlease!CCSQ!new".equals(caller)){
			store.put("fp_kind", "出差申请单");
		}
		
		Object[] basicFields = baseDao.getFieldsDataByCondition("form", new String[]{"fo_keyfield","fo_codefield","fo_statusfield","fo_statuscodefield","fo_seq","fo_table","fo_detailtable","fo_detailseq","fo_detailkeyfield","fo_detailmainkeyfield","fo_detaildetnofield"}, "fo_caller='" + caller + "'");
		
		String keyfield = null;
		String codefield = null;
		String statusfield = null;
		String statuscodefield = null;
		String seq = null;
		String table = null;
		String detailtable = null;
		String detailseq = null;
		String detailkeyfield = null;
		String detailmainkeyfield = null;
		String detno="";
		
		if(basicFields!=null){		
			for(int i=0;i<6;i++){
				if(basicFields[i]==null||"".equals(basicFields[i])){
					BaseUtil.showError("请检查相应表单form配置");
				}
			}
			keyfield = basicFields[0].toString();
			codefield = basicFields[1].toString();
			statusfield = basicFields[2].toString();
			statuscodefield = basicFields[3].toString();
			seq = basicFields[4].toString();
			table = basicFields[5].toString();
			if(table.toUpperCase().indexOf("LEFT JOIN")>-1){
				table = table.substring(0,table.toUpperCase().indexOf("LEFT JOIN"));
			}else if(table.toUpperCase().indexOf("RIGHT JOIN")>-1){
				table = table.substring(0,table.toUpperCase().indexOf("RIGHT JOIN"));
			}
			if(detailsetting){
				for(int i=6;i<10;i++){
					if(basicFields[i]==null||"".equals(basicFields[i])){
						BaseUtil.showError("请检查相应表单form配置");
					}
				}
				detailtable = basicFields[6].toString();
				if(detailtable.toUpperCase().indexOf("LEFT JOIN")>-1){
					detailtable = detailtable.substring(0,detailtable.toUpperCase().indexOf("LEFT JOIN"));
				}else if(detailtable.toUpperCase().indexOf("RIGHT JOIN")>-1){
					detailtable = detailtable.substring(0,detailtable.toUpperCase().indexOf("RIGHT JOIN"));
				}
				detailseq = basicFields[7].toString();
				detailkeyfield = basicFields[8].toString();
				detailmainkeyfield = basicFields[9].toString();
				if(basicFields[10]!=null){
					detno = basicFields[10].toString();
				}
			}
			
			if("CUSTOMTABLE".equals(table.toUpperCase())){
				if (!StringUtil.hasText(store.get("ct_caller"))) {
					store.put("ct_caller", caller);
				}
			}

		}else{
			BaseUtil.showError("请检查相应表单form配置");
		}
		
		if("FeePlease!CCSQ".equals(caller)||"FeePlease!CCSQ!new".equals(caller)||"FeePlease!FYBX".equals(caller)){
			table = "FEEPLEASE";
			detailtable = "FEEPLEASEDETAIL";
			detno="fpd_detno";
		}else if("Workovertime".equals(caller)){
			table = "Workovertime";
			detailtable = "Workovertimedet";
			detno="wod_detno";
		}
		
		String code = baseDao.sGetMaxNumber(table.toString(), 2);
		int id = baseDao.getSeqId(seq.toString());	
		if(store.get(codefield)==null||"".equals(store.get(codefield))){
			store.put(codefield, code);
		}
		if(store.get(keyfield)==null||"".equals(store.get(keyfield))){
			store.put(keyfield, id);
		}
		if(store.get(statuscodefield)==null||"".equals(store.get(statuscodefield))){
			store.put(statuscodefield, "ENTERING");
		}
		if(store.get(statusfield)==null||"".equals(store.get(statusfield))){
			store.put(statusfield, "在录入");
		}

		 Date dt=new Date();
	     SimpleDateFormat matter1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		//讲单据录入人，请假人，申请人等默认当前用户
		if("FeePlease!CCSQ".equals(caller)||"FeePlease!CCSQ!new".equals(caller)){
			store.put("FP_PEOPLE2", emcode);
			store.put("fp_recordman", emname);
			store.put("fp_recorddate", matter1.format(dt));
			//增加组织名称，核算部门默认值
			if(store.get("FP_V12")==null){
				store.put("FP_V12", employee.getEm_defaultorname());
			}
			if(store.get("fp_department")==null){
				store.put("fp_department",employee.getEm_depart());
			}
			if(store.get("fp_pleaseman")==null){
				store.put("fp_pleaseman",emname);
			}
		}else if("FeePlease!FYBX".equals(caller)){
			if(store.get("fp_billdate")==null)store.put("fp_billdate", matter1.format(dt));
			if(store.get("fp_recordman")==null)store.put("fp_recordman", emname);
			if(store.get("fp_recorddate")==null)store.put("fp_recorddate", matter1.format(dt));
			if(store.get("fp_pleasemancode")==null)store.put("fp_pleasemancode ", emcode);
			if(store.get("fp_pleaseman")==null)store.put("fp_pleaseman", emname);
			if(store.get("fp_department")==null)store.put("fp_department", employee.getEm_depart());
			if(store.get("fp_v7")==null)store.put("fp_v7", "未支付");	
		}else if("Workovertime".equals(caller)){
			store.put("wo_emcode", emcode);
			store.put("wo_recorder", emname);
			//添加部门和岗位和组织
			if(store.get("wo_depart")==null){
				store.put("wo_depart", employee.getEm_depart());
			}
			if(store.get("wo_job")==null){
				store.put("wo_job",employee.getEm_position());
			}
			if(store.get("wo_hrorg")==null){
				store.put("wo_hrorg",employee.getEm_defaultorname());
			}
			store.put("wo_recorddate", matter1.format(dt));
		}else if("Ask4Leave".equals(caller)){
			store.put("va_recordor", emname);
			store.put("va_emcode", emcode);
			store.put("va_emname", emname);
			store.put("va_date", matter1.format(dt));
			//添加部门和岗位
			if(store.get("va_department")==null){
				store.put("va_department", employee.getEm_depart());
			}
			if(store.get("va_position")==null){
				store.put("va_position",employee.getEm_position());
			}
		}else if("SpeAttendance".equals(caller)){
			store.put("sa_recorder", emname);
			store.put("sa_appmancode", emcode);
			store.put("sa_appman", emname);
			store.put("sa_recorddate", matter1.format(dt));
			//添加部门和岗位和组织
			if(store.get("sa_department")==null){
				store.put("sa_department", employee.getEm_depart());
			}
			if(store.get("sa_job")==null){
				store.put("sa_job",employee.getEm_position());
			}
			if(store.get("sa_hrorg")==null){
				store.put("sa_hrorg",employee.getEm_defaultorname());
			}
		}else if("MainTain".equals(caller)){
			store.put("mt_applicationdate", DateUtil.currentDateString(Constant.YMD_HMS));
			if(store.get("mt_applicationmancode")==null) store.put("mt_applicationmancode", emcode);
			if(store.get("mt_text14_user")==null) store.put("mt_text14_user", emname);
			if(store.get("mt_text15_user")==null) store.put("mt_text15_user", emcode);
			if(store.get("mt_applicationman")==null) store.put("mt_applicationman", emname);	
			if(store.get("mt_applicationdapt")==null) store.put("mt_applicationdapt", employee.getEm_depart());			
			if(store.get("mt_applicationdeptcode")==null) store.put("mt_applicationdeptcode", employee.getEm_departmentcode());			
		}else if("StandbyApplication".equals(caller)){
			store.put("sa_applicationdate", DateUtil.currentDateString(Constant.YMD_HMS));
			if(store.get("sa_applicationmancode")==null) store.put("sa_applicationmancode", emcode);
			if(store.get("sa_text1")==null) store.put("sa_text1", emname);
			if(store.get("sa_text5")==null) store.put("sa_text5", emcode);
			if(store.get("sa_applicationman")==null) store.put("sa_applicationman", emname);	
			if(store.get("sa_applicationdapt")==null) store.put("sa_applicationdapt", employee.getEm_depart());			
			if(store.get("sa_applicationdaptcode")==null) store.put("sa_applicationdaptcode", employee.getEm_departmentcode());					
		}else if("MaterielApply".equals(caller)){
			store.put("ama_applydate", DateUtil.currentDateString(Constant.YMD_HMS));
			if(store.get("ama_text3")==null) store.put("ama_text3", emcode);
			if(store.get("ama_text7_user")==null) store.put("ama_text7_user", emname);
			if(store.get("ama_recordid")==null) store.put("ama_recordid", employee.getEm_id());
			if(store.get("ama_applymen")==null) store.put("ama_applymen", emname);	
			if(store.get("ama_applydepart")==null) store.put("ama_applydepart", employee.getEm_depart());			
			if(store.get("ama_text4")==null) store.put("ama_text4", employee.getEm_departmentcode());					
			
		}
		
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store });
		
		//保存前业务逻辑
		thingsBforeSave(caller,store);
		
		
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, table,
				new String[] {}, new Object[] {});
		
		baseDao.execute(formSql);
		//保存明细表	
		if(gridStore!=null&&!"[]".equals(gridStore)&&detailsetting){
			List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
			int i=0;
			for(Map<Object, Object> m:grid){				
				i=i+1;
				m.put(detailkeyfield, baseDao.getSeqId(detailseq));
				m.put(detailmainkeyfield, id);
				if(detno!=null&&!"".equals(detno)){
					m.put(detno, i);
				};				
				if("Workovertime".equals(caller)){
					m.put("wod_empcode", emcode);
					m.put("wod_empname", emname);
					
				}
					}
			List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, detailtable);
			baseDao.execute(gridSql);	
		}
		
		id = Integer.parseInt(store.get(keyfield).toString()); 
		modelMap.put(keyfield.toString(),id);
		modelMap.put("keyvalue",id);
		modelMap.put("formcode",code);
		
		// 记录操作
		baseDao.logger.save(caller, keyfield, store.get(keyfield));
		// 执行保存后的其它逻辑
		if("FeePlease!CCSQ".equals(caller)||"FeePlease!CCSQ!new".equals(caller)){
			List<Map<Object, Object>> gridEmpty = new ArrayList<Map<Object, Object>>();
			handlerService.afterSave(caller, new Object[] {store,gridEmpty});
		}else{
			handlerService.afterSave(caller, new Object[] {store});
		}		

		if("Workovertime".equals(caller)){
			//计算加班时数
			if (baseDao.isDBSetting(caller,"autoupdateWodcount")) {
				baseDao.callProcedure("sp_Workovertime_com", new Object[] {store.get("wo_id")});
			}
		}
		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition(table, statuscodefield, keyfield + "=" + store.get(keyfield));
		if(status==null){
			BaseUtil.showError("保存失败!");
		}
		StateAssert.submitOnlyEntering(status);
		
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{store.get(keyfield)});
		
		//提交前业务逻辑
		thingsBforeSubmit(caller,Integer.parseInt(store.get(keyfield).toString()),store);
		
		//执行提交操作
		baseDao.submit(table, keyfield + "=" + store.get(keyfield), statusfield, statuscodefield);
		//记录操作
		baseDao.logger.submit(caller, keyfield, store.get(keyfield));
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{id});
		
		return modelMap;
	}	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String,Object> UpdateSubmitFYBX(String caller,String formStore,String gridStore,String gridStore2){
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Map<String,Object> modelMap=new HashMap<String,Object>();
		// 只能更新在录入的单据!
		Object status = baseDao.getFieldDataByCondition("FeePlease", "fp_statuscode", "fp_id=" + store.get("fp_id"));
		StateAssert.updateOnlyEntering(status);
		if ("FeePlease!FYBX".equals(caller)) {// 来源单据类型为OA单据批量转或公章用印申请转的费用报销单，更新时要判断金额范围并同步更新原单据已转金额。
			Object fp_source[] = baseDao.getFieldsDataByCondition("FeePlease", new String[] { "fp_sourcecaller", "fp_sourcecode",
					"fp_sourcekind" }, "fp_id=" + store.get("fp_id"));
			if ("FeePlease!YZSYSQ".equals(fp_source[0])) {
				for (Map<Object, Object> s : gstore) {
					if (s.get("fpd_d9") != null && s.get("fpd_d9").equals("FeePlease!YZSYSQ")) {
						Object[] obj = baseDao.getFieldsDataByCondition("FeePlease", new String[] { "fp_n3", "fp_n4" }, "fp_code='"
								+ fp_source[1] + "' and fp_kind='" + fp_source[2] + "'");
						Object ofpd_total = baseDao.getFieldDataByCondition("FeePleaseDetail", "fpd_total", "fpd_id=" + s.get("fpd_id"));
						double fee = Double.parseDouble(obj[0].toString()) + Double.parseDouble(ofpd_total.toString())
								- Double.parseDouble(obj[1].toString()) - Double.parseDouble(s.get("fpd_total").toString());
						if (Double.parseDouble(s.get("fpd_total").toString()) > 0 && fee >= 0) {
							baseDao.updateByCondition("FeePlease", "fp_n4=nvl(fp_n4,0)-" + ofpd_total + "+" + s.get("fpd_total"),
									"fp_code='" + fp_source[1] + "' and fp_kind='" + fp_source[2] + "'");
						} else {
							BaseUtil.showError("第" + s.get("fpd_detno") + "行报销金额超出来源单据剩余金额");
						}
					}
				}
			} else { // 如果来自OA单据批量转，更新OA单据CT_ISTURN状态
				boolean boolOA = baseDao
						.checkIf(
								"FEEPLEASE",
								"FP_SOURCECALLER IN (select FO_CALLER FROM FORM left join FORMDETAIL on fd_foid=fo_id  WHERE nvl(FD_TABLE,' ')='CUSTOMTABLE' and nvl(FD_FIELD,' ')='CT_SOURCEKIND' "
										+ "and nvl(FD_DEFAULTVALUE,' ')<>' ') AND FP_ID =" + store.get("fp_id"));
				if (boolOA) {
					Object FP_SOURCECALLER = baseDao.getFieldDataByCondition("FeePlease", "FP_SOURCECALLER", "fp_id=" + store.get("fp_id"));
					List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
					Map<String, Object> map = null;
					for (Map<Object, Object> s : gstore) {
						if (s.get("fpd_d9") != null && s.get("fpd_d9").equals(FP_SOURCECALLER)) {
							Object[] obj = baseDao.getFieldsDataByCondition("customtable", new String[] { "ct_amount", "ct_turnamount" },
									"ct_code='" + s.get("fpd_code") + "' and ct_caller='" + FP_SOURCECALLER + "'");
							Object ofpd_total = baseDao
									.getFieldDataByCondition("FeePleaseDetail", "fpd_total", "fpd_id=" + s.get("fpd_id"));
							double fee = Double.parseDouble(obj[0].toString()) + Double.parseDouble(ofpd_total.toString())
									- Double.parseDouble(obj[1].toString()) - Double.parseDouble(s.get("fpd_total").toString());
							if (Double.parseDouble(s.get("fpd_total").toString()) > 0 && fee >= 0) {
								map = new HashMap<String, Object>();
								map.put("ct_code", s.get("fpd_code"));
								map.put("ct_caller", FP_SOURCECALLER);
								map.put("ct_turnamount", Double.parseDouble(obj[1].toString()) - Double.parseDouble(ofpd_total.toString())
										+ Double.parseDouble(s.get("fpd_total").toString()));
								lists.add(map);
								continue;
							} else {
								BaseUtil.showError("第" + s.get("fpd_detno") + "行报销金额超出来源单据剩余金额");
							}
						}
					}
					for (Map<String, Object> list : lists) {
						baseDao.updateByCondition("customtable", "ct_turnamount=" + list.get("ct_turnamount"),
								"ct_code='" + list.get("ct_code") + "' and ct_caller='" + list.get("ct_caller") + "'");
					}
				}
			}
		}
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		store.put("fp_printstatuscode", "UNPRINT");
		store.put("fp_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		if ("FeePlease!FYBX".equals(caller)) {// 费用报销
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select FP_SUMWGH,FP_THISHKAMOUNT from FeePlease where fp_id=? and nvl(FP_SUMWGH,0)>0 and nvl(FP_THISHKAMOUNT,0)<nvl(FP_SUMWGH,0)",
							store.get("fp_id"));
			if (rs.next()) {
				/*BaseUtil.appendError("报销人还有借款未归还，请先冲销借款金额！本次还款总额(本位币)[" + rs.getGeneralDouble("FP_THISHKAMOUNT") + "],总的未归还金额(本位币)["
						+ rs.getGeneralDouble("FP_SUMWGH") + "]");*/
			}
			/**
			 * fp_n1 借款申请单已转金额 sum(fb_back)已提交和已审核的费用报销单中还款金额的和
			 * sum(fp_pleaseamount)已提交和已审核的还款申请单还款金额的和 结果fee:待还款金额
			 * */
			/*
			 * String sql = "select sum(p.fp_n1- " +
			 * "nvl((select sum(FB_BACK) from feeback left join FEEPLEASE on FB_FPID=FP_ID where FP_STATUSCODE in('AUDITED','COMMITED') and FB_JKSQCODE=p.fp_code),0)"
			 * +
			 * "-nvl((select sum(fp_pleaseamount) from feeplease where fp_sourcecode=p.fp_code and FP_STATUSCODE in ('AUDITED','COMMITED')),0))"
			 * + "from feeplease p where p.fp_pleasemancode='" +
			 * store.get("fp_pleasemancode") +
			 * "' and p.fp_kind='借款申请单' and p.fp_statuscode='AUDITED'";
			 * SqlRowList rs = baseDao.queryForRowSet(sql); if (rs.next()) {
			 * double fee = rs.getGeneralDouble(1, 6); if (fee > 0)
			 * BaseUtil.appendError("该报销人有未还款的借款单！待还款总额为：" + fee); }
			 */
		}
		// 修改Evaluation
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FeePlease", "fp_id");
		baseDao.execute(formSql);
		// 修改EvaluationDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "FeePleaseDetail", "fpd_id");
		gridSql.add("delete from FeePleaseDetail where fpd_fpid="+store.get("fp_id")+"");
		//int count=baseDao.getCount("select count(*) from FeePleaseDetail where fpd_fpid="+store.get("fp_id")+"");
		int i=1;
		for (Map<Object, Object> s : gstore) {
			if (/*s.get("fpd_id") == null || s.get("fpd_id").equals("") || s.get("fpd_id").equals("0")
					|| Integer.parseInt(s.get("fpd_id").toString()) == 0*/ true) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("FEEPLEASEDETAIL_SEQ");
				s.put("fpd_detno",i);
				s.put("fpd_id", id);
				s.put("fpd_code", store.get("fp_code"));
				s.put("fpd_class", store.get("fp_kind"));
				String sql = SqlUtil.getInsertSqlByMap(s, "FeePleaseDetail", new String[] { "fpd_id" }, new Object[] { id });
				gridSql.add(sql);
				i++;
			}
		}
		baseDao.execute(gridSql);
		String updatesql = "update FeePleaseDETAIL set fpd_n2=round(nvl(fpd_n2,0),2),fpd_n1=round(nvl(fpd_n1,0),2),fpd_total=round(nvl(fpd_total,0),2) where fpd_fpid='"
				+ store.get("fp_id") + "'";
		baseDao.execute(updatesql);
		// 更新还款明细
		if (gridStore2 != null) {
			if(gridStore2.length()>0){
				List<String> gridSql2 = SqlUtil.getUpdateSqlbyGridStore(gridStore2, "feeback", "fb_id");
				List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
				for (Map<Object, Object> s : gstore2) {
					if (s.get("fb_id") == null || s.get("fb_id").equals("") || s.get("fb_id").equals("0")
							|| Integer.parseInt(s.get("fb_id").toString()) == 0) {// 新添加的数据，id不存在
						int id = baseDao.getSeqId("feeback_SEQ");
						s.put("fb_id", id);
						String sql = SqlUtil.getInsertSqlByMap(s, "feeback", new String[] { "fb_id" }, new Object[] { id });
						gridSql2.add(sql);
					}
					// 累计还款总额应小于借款单已转金额
					String sql = "select (fp_n1- "
							+ "nvl((select sum(FB_BACK) from feeback left join FEEPLEASE on FB_FPID=FP_ID where FP_STATUSCODE in('AUDITED','COMMITED') and FB_JKSQCODE='"
							+ s.get("fb_jksqcode") + "'),0)" + "-nvl((select sum(fp_pleaseamount) from feeplease where fp_sourcecode='"
							+ s.get("fb_jksqcode") + "' and FP_STATUSCODE in ('AUDITED','COMMITED')),0))fee "
							+ "from feeplease where fp_code='" + s.get("fb_jksqcode") + "' and fp_kind='借款申请单'";
					SqlRowList rs = baseDao.queryForRowSet(sql);
					if (rs.next()) {
						double fee = rs.getGeneralDouble(1, 6);
						if (Double.parseDouble(s.get("fb_back").toString()) > fee) {
							BaseUtil.appendError("还款明细第" + s.get("fb_detno") + "行，待还款金额为:" + fee + "<br>");
						}
					}
				}
				baseDao.execute(gridSql2);				
					}
		}
		getSumTotal(caller, store.get("fp_id"));
		// 记录操作
		baseDao.logger.update(caller, "fp_id", store.get("fp_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
		Object fpd_id = baseDao.getFieldsDatasByCondition("FeePleaseDetail", new String[] { "fpd_id", "fpd_detno" },
				"fpd_fpid=" + store.get("fp_id"));
		modelMap.put("fpd_id", fpd_id);
		getSumTotal(caller, store.get("fp_id"));
		Object status2 = baseDao.getFieldDataByCondition("FeePlease", "fp_statuscode", "fp_id=" + store.get("fp_id"));
		StateAssert.submitOnlyEntering(status2);
		if ("FeePlease!FYBX".equals(caller)) {
			SqlRowList rs = baseDao.queryForRowSet(
					"select fp_amount,FP_THISHKAMOUNT from FeePlease where fp_id=? and nvl(fp_amount,0)<nvl(FP_THISHKAMOUNT,0)", store.get("fp_id"));
			if (rs.next()) {
				BaseUtil.showError("费用报销报销总额必须大于等于本次还款总额！报销总额(本位币)[" + rs.getGeneralDouble("fp_amount") + "],本次还款总额(本位币)["
						+ rs.getGeneralDouble("FP_THISHKAMOUNT") + "]");
			}
			List<Object[]> res = baseDao.getFieldsDatasByCondition("feeback", new String[] { "FB_JKSQCODE", "fb_back", "fb_detno" },
					"fb_fpid=" + store.get("fp_id"));
			if (res.size() != 0) {// 累计还款总额应小于借款单已转金额
				for (Object[] re : res) {
					String sql = "select (fp_n1- "
							+ "nvl((select sum(FB_BACK) from feeback left join FEEPLEASE on FB_FPID=FP_ID where FP_STATUSCODE in('AUDITED','COMMITED') and FB_JKSQCODE='"
							+ re[0].toString() + "'),0)" + "-nvl((select sum(fp_pleaseamount) from feeplease where fp_sourcecode='"
							+ re[0].toString()
							+ "' and fp_sourcekind in ('出差申请单','借款申请单') and FP_STATUSCODE in ('AUDITED','COMMITED')),0))fee "
							+ "from feeplease where fp_code='" + re[0].toString() + "' and fp_kind='借款申请单'";
					SqlRowList rs1 = baseDao.queryForRowSet(sql);
					if (rs1.next()) {
						double fee = rs1.getGeneralDouble(1, 6);
						if (Double.parseDouble(re[1].toString()) > fee) {
							BaseUtil.showError("还款明细第" + re[2].toString() + "行还款金额超过待还款金额，待还款金额为:" + fee);
						}
					}
				}
			}
			// 费用报销的提交逻辑
			if (baseDao.isDBSetting(caller, "comitLogicfeeCategory")) {
				comitLogic_feeCategory(store.get("fp_id"));
			}
			/*
			 * if (baseDao.isDBSetting(caller, "comitLogicfeelimit")) {
			 * comitLogic_feelimit(fp_id, caller); }
			 */
		}
		handlerService.handler(caller, "commit", "before", new Object[] { store.get("fp_id") });
		// 执行提交操作
		baseDao.submit("FeePlease", "fp_id=" + store.get("fp_id"), "fp_status", "fp_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "fp_id", store.get("fp_id"));
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { store.get("fp_id") });
		return modelMap;// 手机端需求
	}
	void getSumTotal(String caller, Object fp_id) {
		if ("FeePlease!FYBX".equals(caller)) {
			baseDao.execute("update FeePleaseDetail set fpd_total=fpd_n2 where nvl(fpd_total,0)=0 and fpd_fpid=" + fp_id);
			baseDao.execute("update FeePleaseDetail set fpd_n1=fpd_total where (nvl(fpd_n1,0)=0 or nvl(fpd_n1,0)<>fpd_total)  and fpd_fpid="
					+ fp_id);
			baseDao.execute("update feeplease set fp_pleaseamount=nvl((select sum(nvl(fpd_n1,0)) from feepleasedetail where fpd_fpid=fp_id),0) where fp_id="
					+ fp_id);
			baseDao.execute("update feeplease set fp_amount=round(nvl(fp_pleaseamount,0)*nvl((select nvl(cm_crrate,0) from currencysmonth where fp_v13=cm_crname and to_char(fp_recorddate,'yyyymm')=cm_yearmonth),0),2) where fp_id="
					+ fp_id);
			baseDao.execute("update FeePlease set fp_v7='已支付' where nvl(fp_pleaseamount,0)=nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='部分支付' where nvl(fp_pleaseamount,0)>nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)+nvl(fp_n6,0)=0 and fp_id=" + fp_id);
			baseDao.execute("update feeplease a set FP_SUMJK=round(nvl((select sum(nvl(fp_pleaseamount,0)*NVL(cm_crrate,0)) from feeplease b,currencysmonth where a.fp_pleasemancode=b.fp_pleasemancode AND b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单' and b.fp_statuscode in ('AUDITED','COMMITED')),0),2) where fp_kind='费用报销单' and fp_id="
					+ fp_id);
			baseDao.execute("update feeplease a set FP_SUMWGH=round(nvl((select sum((nvl(fp_pleaseamount,0)-nvl(fp_n3,0))*NVL(cm_crrate,0)) from feeplease b,currencysmonth where a.fp_pleasemancode=b.fp_pleasemancode AND b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单' and b.fp_statuscode in ('AUDITED','COMMITED')),0),2) where fp_kind='费用报销单' and fp_id="
					+ fp_id);
			baseDao.execute("update feeplease a set FP_THISHKAMOUNT=round(nvl((select sum(nvl(fb_back,0)*NVL(cm_crrate,0)) from FeeBack,feeplease b,currencysmonth where fb_fpid=a.fp_id and fb_jksqcode=b.fp_code and b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单'),0),2) where fp_kind='费用报销单' and fp_id="
					+ fp_id);
		}
		if ("FeePlease!JKSQ".equals(caller)) {
			baseDao.execute("update feeplease a set FP_SUMJK=round(nvl((select sum(nvl(fp_pleaseamount,0)*NVL(cm_crrate,0)) from feeplease b,currencysmonth where a.fp_pleasemancode=b.fp_pleasemancode AND b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单' and b.fp_statuscode in ('AUDITED','COMMITED')),0),2) where fp_id="
					+ fp_id);
			baseDao.execute("update feeplease a set FP_SUMWGH=round(nvl((select sum((nvl(fp_pleaseamount,0)-nvl(fp_n3,0))*NVL(cm_crrate,0)) from feeplease b,currencysmonth where a.fp_pleasemancode=b.fp_pleasemancode AND b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单' and b.fp_statuscode in ('AUDITED','COMMITED')),0),2) where fp_id="
					+ fp_id);
		}
		// 公章用印申请更新明细费用到主表
		if ("FeePlease!YZSYSQ".equals(caller)) {
			baseDao.execute("update FeePlease set FP_PLEASEAMOUNT=(select sum(fpd_n9) from FeePleaseDetail where fpd_fpid=" + fp_id + "),"
					+ "fp_n3=(select sum(fpd_n1) from FeePleaseDetail where fpd_fpid=" + fp_id + ") where fp_id=" + fp_id);
		}
	}
	private void comitLogic_feeCategory(Object object) {
		StringBuffer sb = new StringBuffer();
		// 差旅费报销单,费用报销单：提交之前判断费用科目是否正确,如果正确则更新fpd_catecode字段
		int isRight = baseDao.getCount("select count(1) from feeplease where fp_pleaseamount=0  and fp_pleaseamount is not null and fp_id=" + object);
		if (isRight != 0) {
			BaseUtil.showError("申请金额为0，不能提交");
		}
		Object dept = baseDao.getFieldDataByCondition("feeplease", "fp_department", "fp_id=" + object);
		SqlRowList rs1 = baseDao.queryForRowSet("select fpd_detno,fpd_d1,fpd_id from FeePleaseDetail where fpd_fpid=?", object);
		while (rs1.next()) {
			SqlRowList rs = baseDao.queryForRowSet(
					"select fcs_departmentname from FeeCategorySet where fcs_departmentname=? and fcs_itemname=?", dept,
					rs1.getObject("fpd_d1"));
			if (rs.next()) {

			} else {
				sb.append("第" + rs1.getObject("fpd_detno") + "行部门[" + dept + "]费用用途[" + rs1.getObject("fpd_d1") + "]在费用申请科目没有设置，不能提交");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		baseDao.execute("update FeePleaseDetail set fpd_catecode=(select max(fcs_catecode) from FeeCategorySet where fcs_departmentname='"
				+ dept + "' and fcs_itemname=fpd_d1) where fpd_fpid=" + object);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String,Object> saveAndSubmitMobileSignCard(String caller,String formStore){
		Map<String,Object> modelMap = new HashMap<String,Object>();

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		String code = baseDao.sGetMaxNumber("MOBILE_SIGNCARD", 2);
		int id = baseDao.getSeqId("MOBILE_SIGNCARD_SEQ");
		if(store.get("ms_code")==null||"".equals(store.get("ms_code"))){
			store.put("ms_code", code);
		}
		if(store.get("ms_id")==null||"".equals(store.get("ms_id"))){
			store.put("ms_id", id);
		}
		if(store.get("ms_statuscode")==null||"".equals(store.get("ms_statuscode"))){
			store.put("ms_statuscode", "ENTERING");
		}
		if(store.get("ms_status")==null||"".equals(store.get("ms_status"))){
			store.put("ms_status", "在录入");
		}
		store.put("MS_RECORDER", store.get("ms_emname"));
		modelMap.put("ms_id",id);
		
		//判断当前时间点，是否已经有补卡记录
		String sql="select count(*) from MOBILE_SIGNCARD where ms_emcode='"+store.get("ms_emcode")+"' and ms_signtime=to_date('"+store.get("ms_signtime")+"','yyyy-mm-dd hh24:mi:ss')";
		int count=baseDao.getCount(sql);
		if(count>0){
			BaseUtil.showError("该班次时间已经有补卡记录，不能再次补卡.");
		}
		
		/**
		 * @author lidy
		 * 获取参数配置caller=MobileSignCard,code=SignCardLimit设置每月可以不卡数
		 * 默认每月不能超过3次补卡，没有配置则使用默认值
		 */
		String limitNumber = baseDao.getDBSetting("MobileSignCard","SignCardLimit");
		int limit = 3;
		if(limitNumber!=null){
			limit = (int)Math.floor(Double.parseDouble(limitNumber));
		}
		if(limit<0){ //设置的值为负数时，默认为3
			limit = 3;
		}
		int countNum = baseDao.getCount("select count(1) from  MOBILE_SIGNCARD where ms_emcode='"+store.get("ms_emcode") + "' and to_char(ms_signtime,'yyyymm')=to_char(sysdate,'yyyymm')");
		if(countNum>=limit){
			BaseUtil.showError("每月申诉次数不能超过"+limit+"次");
		}
		
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MOBILE_SIGNCARD",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "ms_id", store.get("ms_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store });

		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("mobile_signcard", "ms_statuscode", "ms_id=" + store.get("ms_id"));
		if(status==null){
			BaseUtil.showError("保存失败!");
		}
		StateAssert.submitOnlyEntering(status);

		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{id});
		//执行提交操作
		baseDao.submit("MOBILE_SIGNCARD", "ms_id=" + id, "ms_status", "ms_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ms_id", id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{id});
		
		return modelMap;
	}
	
	// 提交前判断是否超过系统设置的可延期天数
	private void commitNeedCheck(String caller, Object id,Object time) {
		if("Ask4Leave".equals(caller)){
			if (baseDao.isDBSetting("Ask4Leave", "commitNeedCheck")) {
				String days = baseDao.getDBSetting("Ask4Leave","SetDelayDays");
				if(days!=null && Integer.parseInt(days)>0){
					boolean bool=baseDao.checkIf("dual", "DAY_COUNT(to_date('"+time.toString()+"','yyyy-mm-dd'),sysdate)-1>"+days);
					if(bool) BaseUtil.showError("系统设置的延期提交天数为"+days+"天，已超过不允许提交！");}
			}	
		}else if("Workovertime".equals(caller)){
			if (baseDao.isDBSetting("Workovertime", "commitNeedCheck")) {
				String days = baseDao.getDBSetting("Workovertime","SetDelayDays");
				if(days!=null && Integer.parseInt(days)>0){
					Object endTime = baseDao.getFieldDataByCondition("Workovertime left join Workovertimedet on wod_woid=wo_id", "to_char(min(wod_startdate),'yyyy-mm-dd')", "wo_id="+id);
					boolean bool=baseDao.checkIf("dual", "DAY_COUNT(to_date('"+endTime.toString()+"','yyyy-mm-dd'),sysdate)-1>"+days);
					if(bool) BaseUtil.showError("系统设置的延期提交天数为"+days+"天，已超过不允许提交！");}
			}
		}else if("FeePlease!CCSQ".equals(caller)){
			if (baseDao.isDBSetting("FeePlease!CCSQ", "commitNeedCheck")) {
				String days = baseDao.getDBSetting("FeePlease!CCSQ", "SetDelayDays");
				if (days != null && Integer.parseInt(days) > 0) {
					Object feetime = baseDao.getFieldDataByCondition("FeePlease left join FeePleaseDetail on fp_id=fpd_fpid",
							"to_char(min(fpd_date1),'yyyy-mm-dd')", "fp_id=" + id);
					boolean bool = baseDao.checkIf("dual", "DAY_COUNT(to_date('" + feetime.toString() + "','yyyy-mm-dd'),sysdate)-1>" + days);
					if (bool)
						BaseUtil.showError("系统设置的延期提交天数为" + days + "天，已超过不允许提交！");
				}
			}
		}
	}
	
	private void thingsBforeSave(String caller,Map<Object,Object> store){
		if("FeePlease!CCSQ".equals(caller)){
				// 出差申请
				String dets = baseDao.getJdbcTemplate().queryForObject(
						"select wm_concat(FPD_DETNO) from FeePleaseDetail where fpd_date1>fpd_date2 and FPD_FPID=?", String.class,
						store.get("fp_id"));
				if (dets != null) {
					BaseUtil.appendError("起始日期必须小于等于截止日期！行号：" + dets);
				}
		}else if("Workovertime".equals(caller)){
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(wod_DETNO) from Workovertimedet where wod_startdate>wod_enddate and wod_woid=?",
							String.class, store.get("wo_id"));
			if (dets != null) {
				BaseUtil.showError("起始时间必须小于等于截止时间！行号：" + dets);
			}
			//计算加班时数
			if (baseDao.isDBSetting(caller,"autoupdateWodcount")) {
				baseDao.execute("update workovertimedet set WOD_COUNT=ROUND((WOD_ENDDATE-WOD_STARTDATE)*24,2) where WOD_WOID="+store.get("wo_id"));
			}
		}else if("Ask4Leave".equals(caller)){
			vacationService.checkTime(store);
			String vaemcode = store.get("va_emcode")==null?store.get("VA_EMCODE").toString():store.get("va_emcode").toString();
			int num = vacationService.checkDuplicateTime(vaemcode,store.get("va_startime").toString(),store.get("va_endtime").toString());
			if(num>0){
				BaseUtil.showError("请假时间段有重复！");
			}
		}
	}
	
	private void thingsBforeSubmit(String caller,int id,Map<Object,Object> store){
		if("FeePlease!CCSQ".equals(caller)){
			List<Object[]> data = baseDao.getFieldsDatasByCondition("FeePleaseDetail",
					new String[] { "fpd_date1", "fpd_date2", "fpd_detno" }, "fpd_fpid=" + id);
			for (Object[] os : data) {
				Date start = DateUtil.parseStringToDate(os[0].toString(), "yyyy-MM-dd HH:mm:ss");
				Date end = DateUtil.parseStringToDate(os[1].toString(), "yyyy-MM-dd HH:mm:ss");
				if (start.getTime() > end.getTime()) {
					BaseUtil.showError("第" + os[2] + "行,开始时间大于结束时间!");
				}
			}
			// 判断是否启用延期限制提交
			commitNeedCheck(caller, id,store);
		}else if("Workovertime".equals(caller)){
			// 执行提交前的其它逻辑
			 List<Object[]> data= baseDao.getFieldsDatasByCondition("Workovertimedet", new String[]{"wod_startdate","wod_enddate","wod_detno"}, "wod_woid="+id);
				for(Object[] os:data){
					Date start=DateUtil.parseStringToDate(os[0].toString(), "yyyy-MM-dd HH:mm:ss");
					Date end=DateUtil.parseStringToDate(os[1].toString(), "yyyy-MM-dd HH:mm:ss");
					if(start.getTime()>end.getTime()){
						BaseUtil.showError("第"+os[2]+"行,起始时间大于截止时间!");
					}							
				}
			//判断是否启用延期限制提交
			commitNeedCheck(caller,id,store);
		};
	}
}
