package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.TaskDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.ProjectTask;

@Component
public class TaskUtilService {
	@Autowired
	private TaskDao taskDao;
	@Autowired
	private BaseDao baseDao;
	private static String NORMAL = "NORMAL";
	private static String RESSUBMIT = "RESSUBMIT";
	private static String SUBMIT = "SUBMIT";
	private static String RESSUBMITSTART = "RESSUBMITSTART";
	private static String RESSUBMITSTOP = "RESSUBMITSTOP";
	private static String STOP = "STOP";
	private static String START = "START";
	private static String STARTTIME = "";
	private static String ENDTIME = "";
	private static int WEEKDAYS = 5;
	private static int DAYHOURS = 8;

	/**
	 * 反提交任务节点
	 * */
	public void resSubmitTask(int id, String remark, Employee employee) {
		// 获得所有的前置任务
		List<ProjectTask> tasks = taskDao.getAllParentTasks(id, "nvl(handstatuscode,'')<>FINISHED");
		if (tasks.size() > 0) {
			for (ProjectTask task : tasks) {
				baseDao.updateByCondition("Project", "handstatus='反提交激活',handstatuscode='RESACTIVE',description='"
						+ remark + "'", "ID=" + task.getId());
			}
		}
	}

	public void updateDate(Object id, String baseStartDate, String condition) {
		if(!baseDao.checkIf("ProjectTask", "nvl(ptid,0)="+id)) return;
		SetBaseData();
		String QuerySql = "select id,pretaskdetno,detno,duration from projectTask  where ptid=" + id;
		if (condition != null) {
			QuerySql += " AND " + condition;
		}
		SqlRowList sl = baseDao.queryForRowSet(QuerySql + " order by detno asc ");
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		Object pretaskdetno = null;
		while (sl.next()) {
			maps.add(sl.getCurrentMap());
		}
		for (int i = 0; i < maps.size(); i++) {
			map = maps.get(i);
			pretaskdetno = map.get("pretaskdetno");
			if (pretaskdetno != null && !pretaskdetno.equals("") && !pretaskdetno.equals("0")) {
				String startdate = maxDate(maps, i, pretaskdetno.toString());
				map.put("startdate", startdate);
				map.put("enddate",
						getenddateByStartDate(startdate, Float.parseFloat(map.get("duration").toString()) / DAYHOURS)
						+ ENDTIME);
			} else {
				map.put("startdate", baseStartDate + STARTTIME);
				map.put("enddate",
						getenddateByStartDate(baseStartDate, Float.parseFloat(map.get("duration").toString())
								/ DAYHOURS)
								+ ENDTIME);
			}
		}
		try {
			baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(BaseUtil.parseGridStore2Str(maps), "ProjectTask", "ID"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更改后续任务的开始时间和结束时间
	 * */

	/**
	 * 插入新任务后更新操作
	 **/
	public void insertTaskAfter(int maxId, int ptid) {
		SqlRowList  newTasks=baseDao.queryForRowSet("select id from projecttask where ptid="+ptid+" and  id>"+maxId+" order by id asc");
		boolean bool=false;
		while(newTasks.next()){
			/**查看前置任务是否已全部完成*/	
			bool=baseDao.checkIf("projecttask left join dependency  on id=de_from", "nvl(handstatuscode,' ')<>'FINISHED' and de_from in  (select de_from from Dependency tab where de_to="+newTasks.getInt(1)+")");
			if(!bool){
		    	addResourceassignmentByTask(newTasks.getString(1));		    	
		    }
		}
	}

	/**
	 * 完成任务 1 .normal 2 。ressubmit
	 * */
	public void CompleteTask(int id, String type) {
		Object[] data = baseDao.getFieldsDataByCondition("ProjectTask", "duration,resourcetimerate", "id=" + id);
		float usehours = getUseHours(id);
		String timerate=StringUtil.hasText(data[1])?data[1].toString():"100";
		float point = ((NumberUtil.subFloat(2 * Float.valueOf(data[0].toString()) - usehours, 1)) * (Float
				.valueOf(timerate))) / 100;
		String currentDate = DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date());
		// 更新任务完成率 状态 实际用时 实际结束时间 分数
		baseDao.execute("update projecttask set USEHOURS=" + usehours + ",percentdone=100,handstatus='"
				+ BaseUtil.getLocalMessage("FINISHED") + "',handstatuscode='FINISHED',realenddate="
				+ currentDate + ",point=" + point + " WHERE id=" + id);
		baseDao.execute("UPDATE RESOURCEASSIGNMENT SET ra_statuscode='FINISHED',ra_status='"
				+ BaseUtil.getLocalMessage("FINISHED") + "',ra_laststartdate="
				+ currentDate + ",ra_taskusehours='" + usehours
				+ "',ra_taskpercentdone='" + 100 + "',ra_taskpoint='" + point + "' WHERE ra_taskid='" + id + "'");

	}

	/**
	 * 触发任务节点
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void triggerTask(int id) {
		SqlRowList sl = baseDao
				.queryForRowSet(
						"select de_from,de_to  from dependency where de_to in (select de_to from dependency where de_from=? and de_type='2') order by de_to",
						id);
		// 根据后置id分组
		System.out.println("select de_from,de_to  from dependency where de_to in (select de_to from dependency where de_from="+id+" and de_type='2') order by de_to");
		Map<String, Object> map = null;
		Map<String, List<Map<String, Object>>> listmap = new HashMap<String, List<Map<String, Object>>>();
		while (sl.next()) {
			map = sl.getCurrentMap();
			if (listmap.containsKey(map.get("de_to").toString())) {
				listmap.get(map.get("de_to").toString()).add(map);
			} else {
				List smaller = new ArrayList<Map<String, Object>>();
				smaller.add(map);
				listmap.put(map.get("de_to").toString(), smaller);
			}
		}
		for (String key : listmap.keySet()) {
			List<Map<String, Object>> list = listmap.get(key);
			String condition = " ID in (";
			for (int i = 0; i < list.size(); i++) {
				condition += "'" + list.get(i).get("de_from") + "'";
				if (i < list.size() - 1)
					condition += ",";
			}
			condition += ")";
			// 看前置任务
			SqlRowList slfrom = baseDao
					.queryForRowSet("select percentdone,tasktype from ProjectTask where percentdone!=100 AND "
							+ condition);
			// 说明前置任务都完成了 则激活后面的任务
			if (!slfrom.next()) {
				//配置模板
				setInfo(key);
				addResourceassignmentByTask(key);
			}
		}
		// 处理 多个任务的情况
		SqlRowList sl2 = baseDao.queryForRowSet("select max(ra_id) from ResourceAssignment where ra_emid="
				+ SystemSession.getUser().getEm_id() + " AND ra_statuscode='STOP'");
		while (sl2.next()) {
			startTask(sl2.getInt(1));
		}

	}
	
	public void setInfo(String id){
		Object code = baseDao.getFieldDataByCondition("projecttask left join project on prj_id=prjplanid", "PRJ_ASSIGNTOCODE", "id="+id);
		SqlRowList set = baseDao.queryForRowSet("select Wm_Concat(ra_id) from ResourceAssignment where RA_RESOURCEID is not null and ra_taskid="+id);
		if(set.next()&&set.getString("wm_concat(ra_id)")!=null){	
			//消息模板配置
			Object mmid=baseDao.getFieldDataByCondition("MESSAGEMODEL left join MESSAGEROLE on mm_id=mr_mmid", "distinct mm_id", "MR_ISUSED=-1 AND MM_ISUSED=-1 and mm_caller='TaskDone'");
				//调用生成消息的存储过程
			if (mmid != null) {
				String info=baseDao.callProcedure("SP_CREATEINFO",new Object[] { mmid,code==null?"ADMIN":code,set.getString("Wm_Concat(ra_id)"),DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) });
			}		
		}
			
	}

	/**
	 * 提交重新激活任务
	 * */
	@SuppressWarnings("unchecked")
	public void resStartTask(int id) {
		SqlRowList sl = baseDao
				.queryForRowSet(
						"select de_from,de_to  from dependency where de_to in (select de_to from dependency where de_from=? and de_type='2') order by de_to",
						id);
		// 根据后置id分组
		Map<String, Object> map = null;
		Map<String, List<Map<String, Object>>> listmap = new HashMap<String, List<Map<String, Object>>>();

		while (sl.next()) {
			map = sl.getCurrentMap();
			if (listmap.containsKey(map.get("de_to").toString())) {
				listmap.get(map.get("de_to").toString()).add(map);
			} else {
				@SuppressWarnings("rawtypes")
				List smaller = new ArrayList<Map<String, Object>>();
				smaller.add(map);
				listmap.put(map.get("de_to").toString(), smaller);
			}
		}
		for (String key : listmap.keySet()) {
			List<Map<String, Object>> list = listmap.get(key);
			String condition = " ID in (";
			for (int i = 0; i < list.size(); i++) {
				condition += "'" + list.get(i).get("de_from") + "'";
				if (i < list.size() - 1)
					condition += ",";
			}
			condition += ")";
			SqlRowList slfrom = baseDao
					.queryForRowSet("select percentdone,tasktype from ProjectTask where handstatuscode!='FINISHED' AND "
							+ condition);
			if (!slfrom.hasNext()) {
				baseDao.updateByCondition("ResourceAssignment",
						"ra_statuscode='START', ra_status='" + BaseUtil.getLocalMessage("START") + "'",
						"ra_taskid=" + key + " AND ra_statuscode ='RESSUBMITSTOP'");
			}
		}
	}

	/**
	 * 通过主记录生成资源任务书
	 * */
	public void addResourceassignmentByTask(String key) {
		StringBuffer acsb = new StringBuffer();
		List<String> resourcesqls = new ArrayList<String>();
		Employee employee = SystemSession.getUser();
		SqlRowList slnew = baseDao.queryForRowSet("select isneedattach,startdate,enddate,resourceunits,resourcecode,resourcename,resourceemid,prjplanid,prjplanname,name,tasktype from projecttask where not Exists (select 1 from resourceassignment where ra_taskid=id) and resourcecode is not null  and ID="+ key);
		if (slnew.next()) {
			Map<String, Object> smallermap = slnew.getCurrentMap();
			String[] resourcecode = smallermap.get("resourcecode").toString().split(",");
			String[] resourcename = smallermap.get("resourcename").toString().split(",");
			String[] resourceemid = smallermap.get("resourceemid").toString().split(",");
			String[] resourceunits = smallermap.get("resourceunits").toString().split(",");
			String taskname = smallermap.get("name").toString();
			Object prjplanname = smallermap.get("prjplanname");
			Object startdate = smallermap.get("startdate");
			Object enddate = smallermap.get("enddate");
			Object isneedattach = smallermap.get("isneedattach");
			Object prjplanid = smallermap.get("prjplanid");
			for (int i = 1; i < resourcecode.length + 1; i++) {
				acsb.setLength(0);
				acsb.append("insert into ResourceAssignment (ra_id,ra_detno,ra_taskid,ra_prjid,ra_prjname,ra_resourcecode,ra_resourcename,ra_emid,ra_taskname,ra_units,ra_startdate,ra_enddate,ra_needattach,ra_status,ra_statuscode,ra_basestartdate)values(");
				acsb.append("'"
						+ baseDao.getSeqId("RESOURCEASSIGNMENT_SEQ")
						+ "','"
						+ i
						+ "','"
						+ key
						+ "','"
						+ prjplanid
						+ "','"
						+ prjplanname
						+ "','"
						+ resourcecode[i - 1]
								+ "','"
								+ resourcename[i - 1]
										+ "','"
										+ resourceemid[i - 1]
												+ "','"
												+ taskname
												+ "','"
												+ resourceunits[i - 1]
														+ "',"
														+ DateUtil.parseDateToOracleString(Constant.YMD_HMS,
																DateUtil.parseStringToDate(startdate.toString(), Constant.YMD_HMS))
																+ ","
																+ DateUtil.parseDateToOracleString(Constant.YMD_HMS,
																		DateUtil.parseStringToDate(enddate.toString(), Constant.YMD_HMS)) + ",'" + isneedattach
																		+ "','" + BaseUtil.getLocalMessage("DOING") + "','DOING',"
																		+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ")");
				resourcesqls.add(acsb.toString());
				// 暂停该员工的其他任务
				SqlRowList startsl = baseDao.queryForRowSet("select ra_id from resourceAssignment where ra_emid="
						+ resourceemid[i - 1] + " AND ra_statuscode='START'");
				while (startsl.next()) {
					stopTask(startsl.getInt(1));
				}
			}
			if (resourcecode.length > 0) {
				baseDao.updateByCondition("ProjectTask",
						"handstatuscode='DOING',handstatus='" + BaseUtil.getLocalMessage("DOING")
						+ "',realstartdate=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()),
						"ID='" + key + "'");
			}
			String tasktype = slnew.getString("tasktype");
			if (tasktype != null && tasktype.equals("test")) {
				// 说明是测试类的任务 生成相应的checkList
				int CBID = baseDao.getSeqId("CHECKLISTBASE_SEQ");
				String CBCODE = "CL_" + baseDao.sGetMaxNumber("CHECKLISTBASE", 2);
				Map<String, Object> map = new HashMap<String, Object>();
				SqlRowList checksl = baseDao
						.queryForRowSet("select * from ProjectmainTask left join projecttask on pt_id=ptid   where ID="
								+ key);
				if (checksl.next()) {
					if (baseDao.checkTableName("TURNTABLESET")
							&& baseDao.getCountByCondition("TURNTABLESET", "tts_caller='AddCheckList'") > 0) {
						map.clear();
						map.put("CBID", CBID);
						map.put("CBCODE", CBCODE);
						map.put("emcode", employee.getEm_code());
						baseDao.turnBill(map, "AddCheckList", Integer.parseInt(key), new String[] {
							"ADD", "id" });
					} else
						resourcesqls
						.add("insert into checkListBase (cb_id,cb_code,cb_prcode,cb_prjid,cb_prjcode,cb_prjname,cb_maintaskcode,cb_taskid,cb_taskname,cb_prodtype,cb_recorder,cb_recorddate,cb_assignto) values("
								+ CBID
								+ ",'"
								+ CBCODE
								+ "','"
								+ checksl.getString("PT_PRCODE")
								+ "','"
								+ checksl.getInt("PT_PRJID")
								+ "','"
								+ checksl.getString("PT_PRJCODE")
								+ "','"
								+ checksl.getString("PT_PRJNAME")
								+ "','"
								+ checksl.getString("PT_CODE")
								+ "','"
								+
								key
								+ "','"
								+ taskname
								+ "','"
								+ checksl.getString("pt_producttype")
								+ "','"
								+ resourcename[0]
										+ "',"
										+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())
										+ ",'"
										+ checksl.getObject("pt_orger") + "')");
				}
			}
			else if (tasktype != null && tasktype.equals("milestone")) {
				SqlRowList checksl = baseDao
						.queryForRowSet("select * from ProjectmainTask left join projecttask on pt_id=ptid   where ID="
								+ key);
				if (checksl.next()) {
					resourcesqls
					.add("insert into checkListBase (cb_id,cb_code,cb_prcode,cb_prjid,cb_prjcode,cb_prjname,cb_maintaskcode,cb_taskid,cb_taskname,cb_prodtype,cb_recorder,cb_recorddate,cb_assignto,cb_class) values("
							+ baseDao.getSeqId("CHECKLISTBASE_SEQ")
							+ ",'MS_"
							+ baseDao.sGetMaxNumber("CHECKLISTBASE", 2)
							+ "','"
							+ checksl.getString("pt_prcode")
							+ "','"
							+ checksl.getInt("pt_prjid")
							+ "','"
							+ checksl.getString("pt_prjcode")
							+ "','"
							+ checksl.getString("pt_prjname")
							+ "','"
							+ checksl.getString("pt_code")
							+ "','"
							+
							key
							+ "','"
							+ taskname
							+ "','"
							+ checksl.getString("pt_producttype")
							+ "','"
							+ resourcename[0]
									+ "',"
									+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())
									+ ",'"
									+ checksl.getObject("pt_orger") + "','项目交付单')");
				}
			}
			baseDao.execute(resourcesqls);
		}else {
			baseDao.updateByCondition("ProjectTask","handstatuscode='DOING',handstatus='" + BaseUtil.getLocalMessage("DOING")+ "',realstartdate=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()),"ID='" + key + "'");
			baseDao.updateByCondition("ResourceAssignment", "RA_STATUSCODE='DOING',RA_STATUS='"+BaseUtil.getLocalMessage("DOING")+"',RA_BASESTARTDATE=SYSDATE", "RA_TASKID="+key);
		}
	}
	/**
	 * 反提交移除相应产生的任务
	 * */
	public void removePageTask(String caller,int keyValue){
		baseDao.deleteByCondition("resourceAssignment","ra_taskid in (select id from projecttask where sourcecaller='"+caller+"' and sourceid="+keyValue+")");
		baseDao.deleteByCondition("ProjectTask", "sourcecaller='"+caller+"' and sourceid="+keyValue);
	}
	/**
	 * 插入到时间记录表
	 * */
	public void InsertintoRecordTime(int id, String laststartdate, String type) {
		Employee employee = SystemSession.getUser();
		StringBuffer sb = new StringBuffer();
		sb.append("insert into taskrecordtime(tr_id,tr_taskid,tr_startdate,tr_enddate,tr_usehours,tr_recorder,tr_recorderid,tr_type) values(");
		sb.append(baseDao.getSeqId("TASKRECORDTIME_SEQ") + "," + id + ",");
		sb.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, laststartdate.toString()) + ","
				+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",");
		sb.append("'" + getTime(laststartdate, new Date().toString()) + "','" + employee.getEm_name() + "',"
				+ employee.getEm_id() + ",");
		if (type.equals(RESSUBMITSTART)) {
			sb.append("'反提交耗时')");
		} else if (type.equals(RESSUBMITSTOP)) {
			sb.append("'反提交暂停')");
		} else if (type.equals(STOP)) {
			sb.append("'任务暂停')");
		} else
			sb.append("'正常耗时')");
		baseDao.execute(sb.toString());
	}

	/**
	 * 更新任务序号
	 * */
	public void updateTaskDetno(int id, int prjid, Object pretaskdetno, Employee employee) {
		Object taskdetno = 0;
		if (pretaskdetno != null && !pretaskdetno.equals("")) {
			// 更新改任务到前置任务的后面
			taskdetno = baseDao.getFieldDataByCondition("ProjectTask", "max(detno)", "prjplanid=" + prjid
					+ " And detno in(" + pretaskdetno.toString() + ")");
			// 后面的序号一次加1;
			taskdetno = taskdetno == null ? 0 : taskdetno;
			baseDao.updateByCondition("ProjectTask", "detno=detno+1", "prjplanid=" + prjid + " and detno>" + taskdetno);
		} else
			taskdetno = baseDao.getFieldDataByCondition("ProjectTask", "max(detno)", "prjplanid=" + prjid);
		taskdetno = taskdetno == null ? 0 : taskdetno;
		baseDao.updateByCondition("ProjectTask", "detno=" + (Integer.parseInt(taskdetno.toString()) + 1), "id=" + id);
	}

	public void startTask(int id) {
		SqlRowList sl = baseDao
				.queryForRowSet("select ra_basestartdate,ID,realstartdate from resourceAssignment left join projectTask on ra_taskid=ID where ra_id="
						+ id);
		if (sl.next()) {
			if (sl.getObject("ra_basestartdate") != null) {
				baseDao.updateByCondition(
						"ResourceAssignment",
						"ra_statuscode='DOING',ra_status='" + BaseUtil.getLocalMessage("DOING")
						+ "',ra_laststartdate="
						+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "ra_id=" + id);
			} else {
				baseDao.updateByCondition(
						"ResourceAssignment",
						"ra_statuscode='DOING',ra_status='" + BaseUtil.getLocalMessage("DOING")
						+ "',ra_basestartdate="
						+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "ra_id=" + id);
			}
			if (sl.getObject("realstartdate") == null) {
				baseDao.updateByCondition("ProjectTask",
						"handstatuscode='DOING',handstatus='" + BaseUtil.getLocalMessage("DOING")
						+ "',realstartdate=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()),
						"ID=" + sl.getObject("ID"));
			} else {
				baseDao.updateByCondition("ProjectTask",
						"handstatuscode='DOING',handstatus='" + BaseUtil.getLocalMessage("DOING")
						+ "',realstartdate=null", "ID=" + sl.getObject("ID"));
			}
		}

	}
	public void loadTaskTemplate(String caller,int keyValue){
		baseDao.callProcedure("SYS_AUTOTASK", new Object[]{caller,keyValue});
		triggerTask(caller,keyValue,"COMMIT");
	}
	public void triggerTask(String caller,int keyValue,String type){
		List<Object[]>ids=baseDao.getFieldsDatasByCondition("ProjectTask",new String []{"id","duration"}, "sourcecaller='"+caller+"' and sourceid="+keyValue+" and pretaskdetno is null and triggertype='"+type+"'");
		if (ids==null) return;
		for (Object[] o:ids){
			//修改任务的结束时间
			baseDao.updateByCondition("ProjectTask", "enddate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,getEndDateByDuration(DateUtil.currentDateString(Constant.YMD_HMS), Float.parseFloat(o[1].toString()))),"id="+o[0]);
			addResourceassignmentByTask(String.valueOf(o[0]));
		}
	}
	public void stopTask(int id) {
		// 暂停之前
		// 记录工作时间
		SqlRowList sl = baseDao
				.queryForRowSet("select ra_basestartdate,ra_laststartdate,ra_taskusehours,ra_taskid from resourceAssignment where ra_id="
						+ id);
		float time = (float) 0.0;
		boolean bool = true;
		Object taskid = 0;
		if (sl.next()) {
			Object startdate = sl.getObject("ra_laststartdate") != null ? sl.getObject("ra_laststartdate") : sl
					.getObject("ra_basestartdate");
			time = sl.getFloat("ra_taskusehours")
					+ getTime(startdate.toString(), DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
			taskid = sl.getObject("ra_taskid");
		}
		baseDao.updateByCondition("ResourceAssignment",
				"ra_statuscode='STOP',ra_status='" + BaseUtil.getLocalMessage("STOP") + "',ra_laststartdate="
						+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",ra_taskusehours=" + time,
						"ra_id=" + id);
		// 存在多个人干同一件事的情况 只有该任务所有分配都是暂停的时候把任务暂停 不然不暂停
		List<Object> lists = baseDao.getFieldDatasByCondition("ResourceAssignment", "ra_statuscode", "ra_taskid="
				+ taskid);
		for (Object obj : lists) {
			if (obj.equals("START")) {
				bool = false;
				break;
			}
		}
		if (bool)
			baseDao.updateByCondition("ProjectTask",
					"usehours='" + time + "',handstatus='" + BaseUtil.getLocalMessage("STOP")
					+ "',handstatuscode='STOP'", "ID='" + taskid + "'");
	}

	/**
	 * 获得任务总耗时
	 * */
	public float getUseHours(int id) {
		return Float.parseFloat(baseDao.getFieldDataByCondition("taskrecordtime", "sum(tr_usehours)",
				"tr_taskid=" + id + " AND tr_type<>'" + RESSUBMITSTOP + "' AND tr_type <> '" + STOP + "'").toString());
	}

	/**
	 * 取时间区间
	 * */
	public float getTime(String start, String end) {
		Date startDate = DateUtil.parseStringToDate(start, Constant.YMD_HMS);
		Date endDate = DateUtil.parseStringToDate(end, Constant.YMD_HMS);
		Calendar c = Calendar.getInstance();
		int result = 0;
		for (long begin = startDate.getTime(); begin <= endDate.getTime(); begin += 86400000) {
			c.setTimeInMillis(begin);
			switch (WEEKDAYS) {
			case 5:
				if (c.get(Calendar.DAY_OF_WEEK) != 1 && c.get(Calendar.DAY_OF_WEEK) != 7) {
					result++;
				}
				break;
			default:
				if (c.get(Calendar.DAY_OF_WEEK) != 7) {
					result++;
				}
				break;
			}

		}
		float minite = 0;
		if (result == 0) {
			minite = (endDate.getTime() - startDate.getTime()) / 60000;
			return NumberUtil.subFloat((float) ((float) minite / 60), 2);
		} else {
			minite = NumberUtil.subFloat(
					(float) (endDate.getTime() - 86400000 * (result - 1) - startDate.getTime()) / 60000, 2);
			// 精确到分保留两位小数 四舍五入
			return (result - 1) * 8 + NumberUtil.subFloat((float) ((float) minite / 60 + 0.005), 2);
		}

	}
	
	/**
	 * 改变关联任务的时间
	 * */
	public List<String> changeTime(List<Map<String,Object>> maps,Object taskid,Object proposer){
		List<String> sqls=new LinkedList<String>();
		for(Map<String,Object>map:maps){
			if(map.get("DE_FROM").toString().equals(taskid.toString())){
				String startDate = getStartDate(maps,taskid);
				String endDate = getenddateByStartDate2(startDate,Integer.parseInt(map.get("DURATION").toString()));
				map.put("TO_CHAR(ENDDATE,'YYYY-MM-DD')",endDate);
				//变更时间
				sqls.add("update projecttask set startdate=to_date('"+startDate+"','yyyy-mm-dd'),enddate=to_date('"+endDate+"','yyyy-mm-dd') where id="+map.get("ID"));
				sqls.add("update resourceassignment set (ra_startdate,ra_enddate)=(select startdate,enddate from projecttask where id="+ map.get("ID")+") where ra_taskid="+map.get("ID"));
				sqls.add("insert into tasklog (tl_id,tl_date,tl_recordman,tl_type,tl_startdate,tl_enddate,tl_resource,tl_resoccupy,tl_name,tl_taskid,tl_planid,tl_docname)"
				+ " select tasklog_seq.nextval,sysdate,'"+proposer+"','计划日期变更',startdate,enddate,resourcename,resourceunits,name,id,prjplanid,prjdocname from projecttask where id="+map.get("ID"));
				sqls.addAll(changeTime(maps,map.get("ID"),proposer));
			}
		}
		return sqls;
	}
	
	private String  getenddateByStartDate2(String startdate,float days){
		int duration = (int) (days);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(DateUtil.parseStringToDate(startdate, Constant.YMD).getTime());
		for (int i = 0; i < duration; i++) {
		if(i==duration-1){
			calendar.add(Calendar.DATE, 1); // 下一天
			if(calendar.get(Calendar.DAY_OF_WEEK) == 1){
				calendar.add(Calendar.DATE, -1);
				calendar.setTimeInMillis(calendar.getTime().getTime());
			}
		}
		else{
			calendar.add(Calendar.DATE, 1); // 下一天
			// 可能刚好加一天还是周末
			if (calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7) {
				// 遇到周末和周日 往后
				calendar.add(Calendar.DATE, 1); // 下一天
				calendar.setTimeInMillis(calendar.getTime().getTime());
			}
			if (WEEKDAYS == 5) {
				// 还需要判断一次
				if (calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7) {
					// 遇到周末和周日 往后
					calendar.add(Calendar.DATE, 1); // 下一天
					calendar.setTimeInMillis(calendar.getTime().getTime());
				}
			}
		}
			
		}
		return DateUtil.parseDateToString(calendar.getTime(), Constant.YMD);
	}
	
	private String getStartDate(List<Map<String,Object>> maps,Object id){
		Calendar cal = Calendar.getInstance();
		for(Map<String,Object> map:maps){
			if(map.get("ID").toString().equals(id.toString())){
				String enddate=map.get("TO_CHAR(ENDDATE,'YYYY-MM-DD')").toString();
				cal.setTimeInMillis(DateUtil.parseStringToDate(enddate, Constant.YMD).getTime());			
				int i=cal.get(Calendar.DAY_OF_WEEK);
				if(i==1){
					cal.add(Calendar.DATE, 1);
				}else if(i==7){
					cal.add(Calendar.DATE, 2);
				}	
			}
		}
		return DateUtil.parseDateToString(cal.getTime(), Constant.YMD);
	}
	
	/**
	 * 改变父任务时间
	 * */
	public List<String> changeFatherTime(List<Object> parentIds,List<Map<String,Object>> tasks ){
		List<String> sqls=new LinkedList<String>();
		for(Object pId:parentIds){
			Map<String,Object> parentMap=null;
			String parentStartDate=null;
			String parentEndDate=null;
			for(Map<String,Object> task:tasks){
				if(task.get("id").toString().equals(pId.toString())){
					parentMap=task;
				}
			}
			for(Map<String,Object> task:tasks){//与每个子任务的日期进行对比
				if(task.get("parentid").toString().equals(pId.toString())){
					String start=task.get("to_char(startdate,'yyyy-mm-dd')").toString();
					String end=task.get("to_char(enddate,'yyyy-mm-dd')").toString();
					if(!compareDate(start,parentStartDate)){
						parentStartDate=start;
					}
					if(!compareDate(parentEndDate,end)){
						parentEndDate=end;
					}
				}
			}
			parentMap.put("to_char(startdate,'yyyy-mm-dd')", parentStartDate);
			parentMap.put("to_char(enddate,'yyyy-mm-dd')", parentEndDate);
			sqls.add("update projecttask set startdate=to_date('"+parentStartDate+"','yyyy-mm-dd'),enddate=to_date('"+parentEndDate+"','yyyy-mm-dd'),duration="+countDays(parentStartDate,parentEndDate)+" where id="+parentMap.get("id"));
}
		return sqls;
	}
	
	private boolean compareDate(String d1,String d2){
		if(d1!=null&&d2!=null){
		Long t1=DateUtil.parseStringToDate(d1, Constant.YMD).getTime();
		Long t2=DateUtil.parseStringToDate(d2, Constant.YMD).getTime();
		return t1>t2?true:false;
			}
		return false;
	}
	
	/**
	 * 计算开始与结束之间的天数不算周末
	 * */
	private int countDays(String start,String end){
		if(start!=null&&end!=null){
		Calendar c1 = Calendar.getInstance();
		long d1=DateUtil.parseStringToDate(start, Constant.YMD).getTime();
		long d2=DateUtil.parseStringToDate(end, Constant.YMD).getTime();
		int total=(int) ((d2-d1)/(1000 * 60 * 60*24));
		int weekends=0;
		c1.setTimeInMillis(d1);
		for(int i=0;i<total;i++){
			c1.add(Calendar.DATE, 1);
			if (c1.get(Calendar.DAY_OF_WEEK) == 1 || c1.get(Calendar.DAY_OF_WEEK) == 7) {
				weekends++;
			}		
		}
		return total-weekends;
		}
		return 0;
	}

	/**
	 * 根据开始时间 和工时计算结束时间
	 * */
	public String getenddateByStartDate(String startdate, float days) {
		int duration = (int) (days - 1);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(DateUtil.parseStringToDate(startdate, Constant.YMD).getTime());
		for (int i = 0; i < duration; i++) {
			calendar.add(Calendar.DATE, 1); // 下一天
			// 可能刚好加一天还是周末
			if (calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7) {
				// 遇到周末和周日 往后
				calendar.add(Calendar.DATE, 1); // 下一天
				calendar.setTimeInMillis(calendar.getTime().getTime());
			}
			if (WEEKDAYS == 5) {
				// 还需要判断一次
				if (calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7) {
					// 遇到周末和周日 往后
					calendar.add(Calendar.DATE, 1); // 下一天
					calendar.setTimeInMillis(calendar.getTime().getTime());
				}
			}
		}
		return DateUtil.parseDateToString(calendar.getTime(), Constant.YMD);
	}
	public String getEndDateByDuration(String startdate, float days) {
		int duration = (int) (days+0.5);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(DateUtil.parseStringToDate(startdate, Constant.YMD_HMS).getTime());
		for (int i = 0; i < duration; i++) {
			calendar.add(Calendar.DATE, 1); // 下一天
			// 可能刚好加一天还是周末
			if (calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7) {
				// 遇到周末和周日 往后
				calendar.add(Calendar.DATE, 1); // 下一天
				calendar.setTimeInMillis(calendar.getTime().getTime());
			}
			if (WEEKDAYS == 5) {
				// 还需要判断一次
				if (calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7) {
					// 遇到周末和周日 往后
					calendar.add(Calendar.DATE, 1); // 下一天
					calendar.setTimeInMillis(calendar.getTime().getTime());
				}
			}
		}
		return DateUtil.parseDateToString(calendar.getTime(), Constant.YMD_HMS);
	}

	/**
	 * 根据确前置任务的确认开始时间
	 * */
	public String getStartDateByPretask(String pretaskdetno, int prjid) {
		String basestartdate = "";
		SqlRowList sl = baseDao
				.queryForRowSet("select * from (select enddate,duration  from projecttask  where  prjplanid=" + prjid
						+ " and  detno in (" + pretaskdetno + ")  order by enddate desc)where rownum =1");
		if (sl.next()) {
			Object enddate = sl.getObject(1);
			float days = sl.getFloat(2) / DAYHOURS;
			if ((int) (days) < days) {
				basestartdate = enddate.toString().substring(0, 10) + STARTTIME;
			} else
				basestartdate = getenddateByStartDate(enddate.toString(), 2) + STARTTIME;
		}
		return basestartdate;
	}

	/**
	 * 计算开始时间
	 * */
	public String maxDate(List<Map<String, Object>> maps, int index, String detno) {
		String[] arr = detno.split(",");
		List<String> list = Arrays.asList(arr);
		String maxdate = "";
		float days = 0;
		for (int i = 0; i < index + 1; i++) {
			Map<String, Object> map = maps.get(i);
			try {
				if (list.contains(map.get("detno").toString())) {
					if (maxdate.equals("")) {
						days = Float.parseFloat(map.get("duration").toString()) / DAYHOURS;
						if ((int) (days) < days && days<1) {
							maxdate = map.get("enddate").toString().substring(0, 10);
						} else
							maxdate = getenddateByStartDate(map.get("enddate").toString(), 2);

					} else {
						if (DateUtil.parseStringToDate(maxdate, Constant.YMD).compareTo(
								DateUtil.parseStringToDate(map.get("enddate").toString(), Constant.YMD)) < 0) {
							days = Float.parseFloat(map.get("duration").toString()) / DAYHOURS;
							if ((int) (days) < days) {
								maxdate = map.get("enddate").toString().substring(0, 10);
							}
							maxdate = getenddateByStartDate(map.get("enddate").toString(), 2);
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError("序号:" + map.get("detno") + "设置有误");
			}
		}
		return maxdate + STARTTIME;
	}

	private void SetBaseData() {
		if (STARTTIME.equals("")) {
			Object[] datas = baseDao.getFieldsDataByCondition("ProjectBaseData", new String[] { "pd_starttime",
					"pd_endtime", "pd_weekdays", "pd_dayhours" }, "1=1");
			STARTTIME = " " + String.valueOf(datas[0]);
			ENDTIME = " " + String.valueOf(datas[1]);
			WEEKDAYS = Integer.parseInt(datas[2].toString());
			DAYHOURS = Integer.parseInt(datas[3].toString());
		}
	}
}
