package com.uas.erp.service.hr.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;

/**
 * @author jiangly
 * 
 */
@Service("HrHandler")
public class HrHandler {
	@Autowired
	private BaseDao baseDao;

	public void vacation_submit_after_countannualleave(Integer va_id) {

		Object[] o = baseDao.getFieldsDataByCondition(
				"Vacation left join employee on va_emcode=em_code",
				new String[] { "va_code", "va_vacationtype", "va_startime",
						"va_endtime", "va_alldays", "va_date", "va_emcode",
						"em_indate" }, "va_id = '" + va_id.toString() + "'");

		if (o[1] != null) {
			if (o[1].toString().equals("年假")) {

				if (o[7] == null || o[7].equals("null")
						|| o[7].toString().equals("")) {
					// 没有入职时间 不能请年假
					BaseUtil.showError("员工没有入职时间  不能请年假");
				}
				List<String> list = baseDao.callProcedureWithOut(
						"SP_CANCOMMITVACATION", new Object[] { va_id },
						new Integer[] { 1 }, new Integer[] { 2, 3 });
				if (list.get(0).equals("0")) {
					// 不通过
					BaseUtil.showError("年假天数不足,不能提交! 剩余年假: " + list.get(1)
							+ " 天");

				}

			}else if(o[1]!=null){
				List<String> list = baseDao.callProcedureWithOut(
						"SP_CANCOMMITVACATION", new Object[] { va_id },
						new Integer[] { 1 }, new Integer[] { 2, 3 });
				 Pattern pattern = Pattern.compile("[0-9]*"); 
				 Matcher isNum = null;
				 if(list.get(0)!=null){
					 isNum = pattern.matcher(list.get(0).toString());
					 if( !isNum.matches() ){
						 BaseUtil.showError(list.get(0).toString());
					 }
				 }
			}

		}

	}
	
	
	

	/**
	 * 提交前计算明细的加班时间(万利达科技)
	 * 
	 */
	public void workovertime_commitbefore_counttime(Integer id) {    
		List<Object[]> grid = baseDao.getFieldsDatasByCondition(
				"Workovertimedet", new String[] { "wod_id", "wod_isallday",
						"wod_startdate", "wod_enddate", "wod_jias1",
						"wod_jiax1" }, "wod_woid=" + id);
		List<String> sqls = new ArrayList<String>();
		for (Object[] o : grid) {
			if ("是".equals(o[1] + "")) {// 如果是全天加班，则两日期相减
				Date starDate = DateUtil.parseStringToDate(o[2].toString(),
						"yyyy-MM-dd HH:mm:ss");
				Date endDate = DateUtil.parseStringToDate(o[3].toString(),
						"yyyy-MM-dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				cal.setTime(starDate);
				long startime = cal.getTimeInMillis();
				cal.setTime(endDate);
				long endtime = cal.getTimeInMillis();
				int days = Integer.parseInt(String.valueOf(Math
						.abs((endtime - startime) / (1000 * 3600 * 24)))) + 1;
				sqls.add("update Workovertimedet set wod_count=" + days * 7.5
						+ " where wod_id=" + o[0]);// 每天算加班7.5小时
			} else {// 如果不是全天，则两时间相减
				try {
					if (o[4] != null && !"".equals(o[4] + "")) {
						String wod_jias1 = o[4] + "";
						String wod_jiax1 = o[5] + "";
						double jias1 = Double
								.parseDouble(wod_jias1.split(":")[0])
								+ Double.parseDouble(wod_jias1.split(":")[1])
								/ 60;
						double jiax1 = Double
								.parseDouble(wod_jiax1.split(":")[0])
								+ Double.parseDouble(wod_jiax1.split(":")[1])
								/ 60;
						double count = Math.abs(jiax1 - jias1);
						sqls.add("update Workovertimedet set wod_count="
								+ count + " where wod_id=" + o[0]);
					}
				} catch (Exception e) {
					BaseUtil.showError("时间格式填写不正确,必须如：14:30");
				}
			}
		}
		baseDao.execute(sqls);
	}

	/**
	 * 计算明细的加班时间(万利达科技)
	 * 
	 */
	public void workovertime_saveorupdate_counttime(
			HashMap<Object, Object> store,
			ArrayList<Map<Object, Object>> gstore) {
		for (Map<Object, Object> map : gstore) {
			if ("是".equals(map.get("wod_isallday") + "")) {// 如果是全天加班，则两日期相减
				Date starDate = DateUtil.parseStringToDate(
						map.get("wod_startdate").toString(), null);
				Date endDate = DateUtil.parseStringToDate(map
						.get("wod_enddate").toString(), null);
				Calendar cal = Calendar.getInstance();
				cal.setTime(starDate);
				long startime = cal.getTimeInMillis();
				cal.setTime(endDate);
				long endtime = cal.getTimeInMillis();
				int days = Integer.parseInt(String.valueOf(Math
						.abs((endtime - startime) / (1000 * 3600 * 24)))) + 1;
				map.put("wod_count", days * 7.5);// 每天算加班7.5小时
			} else {// 如果不是全天，则两时间相减
				try {
					if (map.get("wod_jias1") != null
							&& !"".equals(map.get("wod_jias1") + "")) {
						String wod_jias1 = map.get("wod_jias1") + "";
						String wod_jiax1 = map.get("wod_jiax1") + "";
						double jias1 = Double
								.parseDouble(wod_jias1.split(":")[0])
								+ Double.parseDouble(wod_jias1.split(":")[1])
								/ 60;
						double jiax1 = Double
								.parseDouble(wod_jiax1.split(":")[0])
								+ Double.parseDouble(wod_jiax1.split(":")[1])
								/ 60;
						double count = Math.abs(jiax1 - jias1);
						map.put("wod_count", count);
					}
				} catch (Exception e) {
					BaseUtil.showError("时间格式填写不正确,必须如：14:30");
				}
			}
		}
	}

	/**
	 * 更新人员新岗位以及组织ID 更新人员新岗位以及组织(百得力)
	 * 
	 */
	public void turnposition_auditafter_updateposition(Integer id) {
		String sqlstr = "update employee set (em_defaultorname,em_depart,em_defaulthsname)=(select td_newhrorg,td_newdepart,td_newposition from Turnpositiondetail where em_code=td_code) where em_code in (select td_code from Turnpositiondetail where td_tpid="
				+ id + " )";
		// 更新人员新岗位以及组织
		baseDao.execute(sqlstr);
		// 更新人员新岗位以及组织ID
		String sqlstrs = "update employee set em_defaultorid=(select or_id from  HrOrg where or_name=em_defaultorname),em_defaulthsid=(select jo_id from job where jo_name=em_defaulthsname  and jo_orgname=em_defaultorname) where em_code in (select td_code from Turnpositiondetail where td_tpid="
				+ id + ")";
		baseDao.execute(sqlstrs);
	}

	/**
	 * employeemanager_save 人员资料录入时，如果是离职员工，则提示(百得力)
	 */
	public void employeemanager_save_checkman(HashMap<Object, Object> store,
			String language) {
		int count = baseDao.getCount("select count(1) from employee where em_iccode='" + store.get("em_iccode") + "'");
		if (count != 0) {
			BaseUtil.showError("此员工已存在!");
		}
	}

	/**
	 * 请假申请单:提交之前判断一年中事假是否大于20天!(万利达) HR->vacation->commit_before 2014-9-24
	 * 18:25:38
	 */
	public void vacation_commit_before_alldays(Integer id) {
		Object[] va = baseDao.getFieldsDataByCondition("vacation", new String[] { "va_emcode",
				"to_char(va_date,'yyyy')", "va_alldays" }, "va_id=" + id + "and va_vacationtype='事假'");
		if (va != null) {
			double days = baseDao.getSummaryByField("vacation", "va_alldays", "to_char(va_date,'yyyy')='" + va[1]
					+ "' and va_emcode='" + va[0]
					+ "' and va_vacationtype='事假' and va_statuscode IN ('COMMITED','AUDITED') and va_id<>" + id);
			if (days + Double.parseDouble(va[2].toString()) > 20) {
				BaseUtil.showError("请假天数合计大于20天，不允许提交!已请假天数：" + days);
			}
		}
	}
	
	/**
	 * 录用申请单：明细删除前，还原来源应聘人员信息 
	 */
	public void careerapply_deletedetail_before(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("Careerapplydetail", new String[] { "nvl(cd_sourceid,0)" }, "cd_id=" + id);
		if (objs != null && objs[0] != null) {
			baseDao.updateByCondition("recuitinfo", "re_enroll='否',re_isincaree='0'", "re_id=" + objs[0]);
		}
	}
	/**
	 * @author wsy
	 * 请假申请单 :保存更新后调用存储过程counttime计算请假时长。
	 * 存储过程多传一个参数"H" or "D"来判断是请假申请单还是出差申请单("H"为请假申请单，"D"为出差申请单)。
	 */
	public void vacation_counttime(HashMap<Object,Object> store){
		int count = baseDao.getCount("select * from empworkdate");
		Object va_alltimes = store.get("va_alltimes");
		Object va_emcode = baseDao.getFieldDataByCondition("vacation", "va_emcode", "va_id = "+store.get("va_id"));
		if(count>0 || (va_alltimes==null || "".equals(va_alltimes) || "0".equals(va_alltimes))){
			List<String> list = baseDao.callProcedureWithOut("counttime", new Object[]{store.get("va_startime").toString(),store.get("va_endtime").toString(),va_emcode.toString(),"H"}, new Integer[]{1,2,3,4}, new Integer[]{5,6});
			//if("年假".equals(store.get("va_vacationtype"))){
				baseDao.updateByCondition("Vacation", "va_alltimes="+Double.parseDouble((list.get(0)==null?"0":list.get(0)))+",va_alldays="+Double.parseDouble((list.get(1)==null?"0":list.get(1)))+"", "va_id="+store.get("va_id"));
//			}else{
//				baseDao.updateByCondition("Vacation", "va_alltimes="+Double.parseDouble((list.get(0)==null?"0":list.get(0)))+"", "va_id="+store.get("va_id"));
//			}
		}
		
	}
	/**
	 * @author wsy
	 * 出差申请单 :保存更新后调用存储过程counttime计算出差天数.
	 * 存储过程多传一个参数"H" or "D"来判断是请假申请单还是出差申请单("H"为请假申请单，"D"为出差申请单)。
	 */
	public void businesstrip_countday(HashMap<Object,Object> store,ArrayList<Map<Object,Object>> gStore){
		int count = baseDao.getCount("select * from empworkdate");
		Object FP_N6 = store.get("FP_N6");
		if(count>0 || (FP_N6==null || "".equals(FP_N6) || "0".equals(FP_N6))){
			List<String> list = baseDao.callProcedureWithOut("counttime", new Object[]{store.get("fp_prestartdate").toString(),store.get("fp_preenddate").toString(),store.get("FP_PEOPLE2").toString(),"D"}, new Integer[]{1,2,3,4}, new Integer[]{5,6});
			baseDao.updateByCondition("FeePlease", "FP_N6="+Double.parseDouble((list.get(0)==null?"0":list.get(0)))+"", "fp_id="+store.get("fp_id"));
		
		}
		
	}
	
	/**
	 * @author hey
	 *   反馈编号：2018060319
	 *   1.离职申请中，自动带出该员工未冲借款。其借款冲回，或者转交给部门其他人，离职申请单才能审批，同其它交接单逻辑一样。
     *   2.离职申请中，固定资产移交，必须交接给其部门其他人员，确保资产完整交接，离职申请单才能审批，同其它交接单逻辑一样。
	 */
	public void employee_hasbills_turnover(Integer id){
		String em_code = String.valueOf((baseDao.getFieldDataByCondition("Turnover", "to_applymancode", "to_id="+id)));//申请人编号
		String em_name = String.valueOf((baseDao.getFieldDataByCondition("Turnover", "to_applyman", "to_id="+id)));//申请人姓名
		String Sql = "select (sum(fp_n1)-sum(fp_n3)) as num from FeePlease where fp_pleasemancode = '"+em_code+"' and FP_STATUSCODE = 'AUDITED' and fp_kind='借款申请单'";
		List<Map<String, Object>> list = baseDao.queryForList(Sql);
		//借款还款差额判断
		String money = String.valueOf(list.get(0).get("NUM"));
		if(!"0".equals(money)&&!"null".equals(money)){
			String codeSql = "select wm_concat(fp_code) as code from FeePlease where fp_pleasemancode = '"+em_code+"' and FP_STATUSCODE = 'AUDITED' and fp_kind='借款申请单' and fp_n1!=fp_n3";
			List<Map<String, Object>> codeList = baseDao.queryForList(codeSql);
			BaseUtil.showError("申请人存在未还的借款，借款单号：" + codeList.get(0).get("CODE"));
		}
		//未移交的固定资产判断
		boolean hasAssets = baseDao.checkIf("ASSETSCARD", "ac_useman = '"+em_name+"' and ac_cvalue !=0");
		if(hasAssets){
			BaseUtil.showError("申请人存在未移交的固定资产,请移交！");
		}
	}
}
