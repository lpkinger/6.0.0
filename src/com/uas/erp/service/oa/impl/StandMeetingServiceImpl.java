package com.uas.erp.service.oa.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.oa.StandMeetingService;

@Service
public class StandMeetingServiceImpl implements StandMeetingService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveStandMeeting(String formStore, String caller) throws ParseException {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int count=baseDao.getCount("select count(1) from StandMeeting where sm_code='"+store.get("sm_code")+"'");		
		if(count!=0){
			BaseUtil.showError("此单据编号已存在！");
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String weekstr = "";
		if (store.get("sm_mon").equals("1")) {
			weekstr = weekstr + "星期一、";
		}
		if (store.get("sm_tues").equals("1")) {
			weekstr = weekstr + "星期二、";
		}
		if (store.get("sm_wed").equals("1")) {
			weekstr = weekstr + "星期三、";
		}
		if (store.get("sm_thur").equals("1")) {
			weekstr = weekstr + "星期四、";
		}
		if (store.get("sm_frid").equals("1")) {
			weekstr = weekstr + "星期五、";
		}
		if (weekstr.length() > 0) {
			store.put("sm_week", weekstr.substring(0, weekstr.length() - 1));
		} else {
			store.put("sm_week", weekstr);
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "StandMeeting",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "sm_id", store.get("sm_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteStandMeeting(int sm_id, String caller) {
		int count=baseDao.getCount("select count(1) from Meetingroomapply where ma_smcode=(select sm_code from StandMeeting where sm_id='"+sm_id+"')");		
		if(count!=0){
			BaseUtil.showError("此例会已经产生相关会议申请，不能删除！");
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, sm_id);
		// 删除
		baseDao.deleteById("StandMeeting", "sm_id", sm_id);
		// 记录操作
		baseDao.logger.delete(caller, "sm_id", sm_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, sm_id);
	}

	@Override
	public void updateStandMeeting(String formStore, String caller) throws ParseException {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int count1=baseDao.getCount("select count(1) from StandMeeting where sm_id<> "+store.get("sm_id")+" and sm_code='"+store.get("sm_code")+"'");
		if(count1!=0){
			BaseUtil.showError("此单据编号已存在！");
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改StandMeeting
		String weekstr = "";
		if (store.get("sm_mon").equals("1")) {
			weekstr = weekstr + "星期一、";
		}
		if (store.get("sm_tues").equals("1")) {
			weekstr = weekstr + "星期二、";
		}
		if (store.get("sm_wed").equals("1")) {
			weekstr = weekstr + "星期三、";
		}
		if (store.get("sm_thur").equals("1")) {
			weekstr = weekstr + "星期四、";
		}
		if (store.get("sm_frid").equals("1")) {
			weekstr = weekstr + "星期五、";
		}
		if (weekstr.length() > 0) {
			store.put("sm_week", weekstr.substring(0, weekstr.length() - 1));
		} else {
			store.put("sm_week", weekstr);
		}
		store.put("sm_updateman", SystemSession.getUser().getEm_name());
		store.put("sm_updatedate", DateUtil.currentDateString(Constant.YMD_HMS));
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "StandMeeting",
				"sm_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "sm_id", store.get("sm_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	@Transactional
	public String turnMeeting(String data, String caller) {
		// [{"sm_id":3002,"sm_fromdate":"","sm_todate":""},{"sm_id":3001,"sm_fromdate":"","sm_todate":""}]
		Employee employee = SystemSession.getUser();
		//String log = null;
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		int count=0;//生成会议申请数量
		for (Map<Object, Object> map : maps) {
			String fromdate = map.get("sm_fromdate").toString();
			String todate = map.get("sm_todate").toString();
			if (fromdate.length() < 1 || todate.length() < 1
					|| DateUtil.compare(fromdate, todate) == 1) {
				BaseUtil.showError("开始日期和结束日期不能为空或者所选择区间不对！");
				// log="开始日期和结束日期不能为空或者区间不对！";
			}
			List<Object> objdate = new ArrayList<Object>();
			try {
				objdate = DateUtil.findDates(fromdate, todate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			List<Object[]> objects = baseDao.getFieldsDatasByCondition(
					"StandMeeting", new String[] { "sm_name", "sm_mrname",
							"sm_host", "sm_mon", "sm_tues", "sm_wed",
							"sm_thur", "sm_frid", "sm_satur", "sm_depart",
							"sm_starttime", "sm_endtime", "sm_groupid","sm_group","sm_type","sm_id" ,"sm_mrcode","sm_remark","sm_code"},
					"sm_id=" + map.get("sm_id"));	
			for (Object[] os : objects) {
				for (Object od : objdate) {
					for (int i = 3; i < 8; i++) {
						if ("1".equals(os[i])
								&& DateUtil.getWeekDay1(od.toString())==(i-2)) {// 如果在日期区间内有对应的星期几则添加
							// 拼凑开始时间和结束时间
							String starttime = "to_date('"
									+ od.toString().trim() + " " + os[10]
									+ "','yyyy-mm-dd hh24:mi')";
							String endtime = "to_date('" + od.toString().trim()
									+ " " + os[11] + "','yyyy-mm-dd hh24:mi')";
							String code = baseDao.sGetMaxNumber(
									"Meetingroomapply", 2);
							int maid = baseDao.getSeqId("Meetingroomapply_SEQ");
							String sql = "insert into Meetingroomapply(ma_code,ma_status,ma_recorder,ma_recorddate,ma_mrname,ma_date,ma_host," +
									"ma_remark,ma_statuscode,ma_id,ma_department,ma_starttime,ma_endtime,ma_group,ma_groupid,ma_type,ma_smid,ma_mrcode,ma_theme,ma_isturndoc,ma_smcode)values('"
									+ code
									+ "','在录入','"
									+ employee.getEm_name()
									+ "',sysdate,'"
									+ os[1]
									+ "',to_date('"
									+ od.toString()
									+ "','yyyy-mm-dd'),'"
									+ os[2]
									+ "','"
									+ os[17]
									+ "','ENTERING',"
									+ maid
									+ ",'"
									+ os[9]
									+ "',"
									+ starttime
									+ ","
									+ endtime
									+ ",'"+os[13]+"','"+os[12]+"','"+os[14]+"',"+os[15]+",'"+os[16]+"','"+os[0]+"','否'"+",'"+os[18]+"')";
							baseDao.execute(sql);
							count++;
							// 确认与会人员job#8;employee#106;employee#107;org#5
							Set<String> jobs = new HashSet<String>();// 岗位
							Set<String> emps = new HashSet<String>();// 人员
							Set<String> orgs = new HashSet<String>();// 组织
							if(os[12]!=""&&os[12]!=null){
							for (String group : os[12].toString().split(";")) {
								String[] g = group.split("#");
								if ("job".equals(g[0])) {
									jobs.add(g[1]);
								}
								if ("employee".equals(g[0])) {
									emps.add(g[1]);
								}
								if ("org".equals(g[0])) {
									orgs.add(g[1]);
								}
							}
							}
							if (orgs.size() > 0) {
								for (String org : orgs) {
									List<Object> empids = baseDao
											.getFieldDatasByCondition(
													"HRORGEMPLOYEES", "OM_EMID",
													"OM_ORID=" + org);
									for (Object empid : empids) {
										emps.add(empid.toString());
									}
								}
							}
							if (jobs.size() > 0) {
								for (String job : jobs) {
									List<Object> empids = baseDao
											.getFieldDatasByCondition(
													"employee", "EM_id",
													"EM_DEFAULTHSID=" + job);
									for (Object empid : empids) {
										emps.add(empid.toString());
									}
								}
							}
							List<String> sqls = new ArrayList<String>();
							int detno = 1;
							for (String empid : emps) {
								String sqldetail = "insert into MeetingDetail(md_detno,md_maid,md_participants,md_emcode,md_isnoticed,md_id) select "
										+ detno++
										+ ","
										+ maid
										+ ",em_name,em_code,0,MeetingDetail_seq.nextval from employee where em_id="
										+ empid;
								sqls.add(sqldetail);
							}
							baseDao.execute(sqls);
						}
					}
				}
			}
		}
		if(count==0){
			BaseUtil.showError("生成失败！");
		}
		return "转入成功";
	}

	@Override
	public void banStandMeeting(int sm_id, String caller) {
		baseDao.execute("update StandMeeting set sm_statuscode='BANNED',sm_status='已禁用' where sm_id="+sm_id);
		baseDao.logger.banned(caller, "sm_id", sm_id);
	}

	@Override
	public void resBanStandMeeting(int sm_id, String caller) {
		baseDao.execute("update StandMeeting set sm_statuscode='AUDITED',sm_status='已审核' where sm_id="+sm_id);
		baseDao.logger.resBanned(caller, "sm_id", sm_id);
	}

	@Override
	public void submitStandMeeting(int id, String caller) {
		//只能对状态为[在录入]的例会进行提交操作!
		Object status = baseDao.getFieldDataByCondition("StandMeeting", "sm_statuscode", "sm_id=" + id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{id});
		//执行提交操作
		baseDao.submit("StandMeeting", "sm_id=" + id, "sm_status", "sm_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "sm_id", id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{id});
	}

	@Override
	public void resSubmitStandMeeting(int id, String caller) {
		//只能对状态为[已提交]的例会进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("StandMeeting", "sm_statuscode", "sm_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, new Object[]{id});
		//执行反提交操作
		baseDao.resOperate("StandMeeting", "sm_id=" + id, "sm_status", "sm_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "sm_id", id);	
		//执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, new Object[]{id});
	}

	@Override
	public void auditStandMeeting(int id, String caller) {
		//只能对状态为[已提交]的例会进行审核操作!
		Object status = baseDao.getFieldDataByCondition("StandMeeting", "sm_statuscode", "sm_id=" + id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[]{id});
		//执行审核操作
		baseDao.audit("StandMeeting", "sm_id=" + id, "sm_status", "sm_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "sm_id", id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[]{id});		
	}

	@Override
	public void resAuditStandMeeting(int id, String caller) {
		//只能对状态为[已审核]的例会进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("StandMeeting", "sm_statuscode", "sm_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller,new Object[]{id});
		//执行反审核操作
		baseDao.resOperate("StandMeeting", "sm_id=" + id, "sm_status", "sm_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "sm_id", id);
		//执行反审核后的其它逻辑
		handlerService.afterResAudit(caller,new Object[]{id});
	}

}
