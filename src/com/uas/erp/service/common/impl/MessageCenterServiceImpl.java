package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.MessageCenterService;

@Service
public class MessageCenterServiceImpl implements MessageCenterService {
	// 我接收的未审批(集团版)
	// static final String GET_PROCESS_UNDO_GROUP=" SELECT * FROM (SELECT
	// JPROCESSVIEW.*, ROW_NUMBER() OVER(ORDER BY JP_ID,JP_LAUNCHTIME) RN FROM
	// JPROCESSVIEW WHERE JP_NODEDEALMAN=? AND JP_STATUS='待审批' OR
	// (JP_LAUNCHERID=? AND JP_STATUS='未通过' ) ORDER BY JP_ID,JP_LAUNCHTIME)
	// WHERE RN<=? AND RN>? ORDER BY CASE TYPECODE WHEN 'process' THEN 1 WHEN
	// 'transferprocess' THEN 2 WHEN 'procand' THEN 3 END";
	static final String GET_PROCESS_UNDO_GROUP = "SELECT * FROM (SELECT JP_NAME||' '||JP_CODEVALUE JP_PROCESSDESC,JP_CODEVALUE,JP_NODEID,CURRENTMASTER,JP_FLAG,JP_FORM,JP_KEYVALUE,JP_LAUNCHERNAME,JP_NAME,nvl(JP_PROCESSNOTE,' ') JP_PROCESSNOTE,to_char(JP_LAUNCHTIME,'yyyy-mm-dd HH24:mi:ss') JP_LAUNCHTIME,TYPECODE,ROW_NUMBER() OVER(ORDER BY JP_LAUNCHTIME) RN FROM JPROCESSVIEW WHERE JP_NODEDEALMAN=? AND JP_STATUS='待审批'  OR (JP_LAUNCHERID=? AND JP_STATUS='未通过' ) ) WHERE RN<=? AND RN>?  ORDER BY CASE TYPECODE  WHEN 'process' THEN 1 WHEN 'transferprocess' THEN 2 WHEN 'procand' THEN 3 END,RN";
	// 我接收的未审批
	// static final String GET_PROCESS_UNDO="SELECT * FROM (SELECT
	// PROCESS_UNDO_VIEW.*,ROW_NUMBER() OVER(ORDER BY JP_ID,JP_LAUNCHTIME) RN
	// FROM PROCESS_UNDO_VIEW WHERE (JP_NODEDEALMAN=? AND JP_STATUS='待审批') OR
	// (JP_LAUNCHERID=? AND JP_STATUS='未通过' ) ORDER BY JP_ID,JP_LAUNCHTIME)
	// WHERE RN<=? AND RN>? ORDER BY CASE TYPECODE WHEN 'process' THEN 1 WHEN
	// 'transferprocess' THEN 2 WHEN 'procand' THEN 3 END";
	static final String GET_PROCESS_UNDO = "SELECT * FROM (SELECT JP_NAME||' '||JP_CODEVALUE JP_PROCESSDESC,TO_CHAR(JP_REMINDDATE,'yyyy-mm-dd HH24:mi:ss') JP_REMINDDATE,JP_CODEVALUE,JP_NODEID,JP_LAUNCHERNAME,JP_NAME,nvl(JP_PROCESSNOTE,' ') JP_PROCESSNOTE,to_char(JP_LAUNCHTIME,'yyyy-mm-dd HH24:mi:ss') JP_LAUNCHTIME,TYPECODE,ROW_NUMBER() OVER(ORDER BY JP_LAUNCHTIME) RN FROM PROCESS_UNDO_VIEW WHERE (JP_NODEDEALMAN=? AND JP_STATUS='待审批')  OR (JP_LAUNCHERID=? AND JP_STATUS='未通过' ) ) WHERE RN<=? AND RN>? ORDER BY CASE TYPECODE  WHEN 'process' THEN 1 WHEN 'transferprocess' THEN 2 WHEN 'procand' THEN 3 END,RN";
	// 我接收的已审批
	static final String GET_PROCESS_ALREADYDO = "SELECT * FROM (SELECT JP_NAME||' '||JP_CODEVALUE JP_PROCESSDESC,TO_CHAR(JP_LAUNCHTIME,'yyyy-mm-dd HH24:mi:ss') JP_LAUNCHTIME,nvl(JP_PROCESSNOTE,' ') JP_PROCESSNOTE,to_char(JP_REMINDDATE,'yyyy-mm-dd HH24:mi:ss') JP_REMINDDATE,JN_NAME,JN_INFORECEIVER||JN_OPERATEDDESCRIPTION JN_OPERATEDDESCRIPTION,JN_NODEDESCRIPTION,JN_DEALMANID,JN_DEALMANNAME,JN_DEALTIME,JN_DEALRESULT,JP_CODEVALUE,JP_NODEID,JP_KEYVALUE,JP_NAME,JP_LAUNCHERID,ROW_NUMBER() OVER(ORDER BY JP_LAUNCHTIME DESC) RN,JP_LAUNCHERNAME  FROM JNODE LEFT JOIN JPROCESS ON JN_PROCESSINSTANCEID=JP_PROCESSINSTANCEID AND JN_NAME=JP_NODENAME WHERE JP_CODEVALUE IS NOT NULL AND JN_DEALMANID=? ORDER BY JP_LAUNCHTIME DESC ) WHERE RN<=? AND RN>?";
	// 我发起的未审批
	static final String GET_PROCESS_ALREADYLAUNCH_UNDO = "SELECT *  FROM (SELECT JP_NAME||' '||JP_CODEVALUE JP_PROCESSDESC,JP_NODEID,JP_NODEDEALMAN,JP_NODEDEALMANNAME,JP_NAME,JP_CODEVALUE,to_char(JP_LAUNCHTIME,'yyyy-mm-dd HH24:mi:ss') JP_LAUNCHTIME,ROW_NUMBER() OVER(ORDER BY JP_LAUNCHTIME asc) RN FROM PROCESS_LAUNCH_VIEW WHERE JP_LAUNCHERID=? AND JP_STATUS='待审批' ORDER BY JP_LAUNCHTIME asc) WHERE RN<=? AND RN>?";
	// 我发起的已审批
	static final String GET_PROCESS_ALREADYLAUNCH_DONE = "SELECT *  FROM (SELECT JP_NAME||' '||JP_CODEVALUE JP_PROCESSDESC,JP_NODEID,JP_NODEDEALMANNAME,JP_NAME,JP_CODEVALUE,to_char(JP_LAUNCHTIME,'yyyy-mm-dd HH24:mi:ss') JP_LAUNCHTIME,JN_DEALTIME,ROW_NUMBER() OVER(ORDER BY JP_LAUNCHTIME desc) RN FROM PROCESS_LAUNCH_VIEW left join jnode on jp_form=JNODE.JN_PROCESSINSTANCEID and jp_nodename=jn_name and jp_nodedealman=JNODE.JN_DEALMANID WHERE JP_LAUNCHERID=? AND JP_STATUS='已审批' ORDER BY JP_LAUNCHTIME desc) WHERE  RN<=? AND RN>?";
	// 我的未发起
	static final String GET_PROCESS_TOLUANCH = "SELECT  * FROM (SELECT PAGELINK,CODE,TITLE,ROWNUM RN FROM table(GET_REMINDDATA(?))) WHERE RN<=? AND RN>?";
	
	// 我接收的未审批(集团版)
	/*static final String PROCESS_UNDO_GROUP_COUNT = " SELECT JP_NAME||' '||JP_CODEVALUE JP_PROCESSDESC,JP_CODEVALUE,JP_NODEID,CURRENTMASTER,JP_FLAG,JP_FORM,JP_KEYVALUE,JP_LAUNCHERNAME,JP_NAME,nvl(JP_PROCESSNOTE,' '), JP_LAUNCHTIME,TYPECODE,ROW_NUMBER() OVER(ORDER BY JP_LAUNCHTIME) RN FROM JPROCESSVIEW ";
*/	// 我接收的未审批
	/*static final String PROCESS_UNDO_COUNT =" SELECT JP_NAME||' '||JP_CODEVALUE JP_PROCESSDESC,JP_REMINDDATE,JP_CODEVALUE,JP_NODEID,JP_LAUNCHERNAME,JP_NAME,nvl(JP_PROCESSNOTE,' ')  JP_PROCESSNOTE,JP_LAUNCHTIME,TYPECODE,ROW_NUMBER() OVER(ORDER BY JP_LAUNCHTIME) RN FROM PROCESS_UNDO_VIEW ";
	*/
	static final String PROCESS_UNDO_GROUP_DATAFIELD = " SELECT JP_NAME||' '||JP_CODEVALUE JP_PROCESSDESC,JP_CODEVALUE,JP_NODEID,JP_LAUNCHERNAME,JP_NAME,nvl(JP_PROCESSNOTE,' ') JP_PROCESSNOTE,to_char(JP_LAUNCHTIME,'yyyy-mm-dd HH24:mi:ss') JP_LAUNCHTIME,TYPECODE,ROW_NUMBER() OVER(ORDER BY JP_LAUNCHTIME) RN  ";
	static final String PROCESS_UNDO_CONDITION =" WHERE ((JP_NODEDEALMAN=? AND JP_STATUS='待审批')  OR (JP_LAUNCHERID=? AND JP_STATUS='未通过')) ";
	static final String PROCESS_UNDO_PAGING ="  ) WHERE RN<=? AND RN>?  ORDER BY CASE TYPECODE  WHEN 'process' THEN 1 WHEN 'transferprocess' THEN 2 WHEN 'procand' THEN 3 END,RN ";
	// 我接收的已审批 JN_DEALTIME
	static final String PROCESS_ALREADYDO ="SELECT JP_NAME||' '||JP_CODEVALUE JP_PROCESSDESC,TO_CHAR(JP_LAUNCHTIME,'yyyy-mm-dd HH24:mi:ss') JP_LAUNCHTIME,nvl(JP_PROCESSNOTE,' ') JP_PROCESSNOTE, to_char(JP_REMINDDATE,'yyyy-mm-dd HH24:mi:ss') JP_REMINDDATE,JN_NAME,JN_INFORECEIVER||JN_OPERATEDDESCRIPTION JN_OPERATEDDESCRIPTION,JN_NODEDESCRIPTION,JN_DEALMANID,JN_DEALMANNAME,JN_DEALTIME,JN_DEALRESULT,JP_CODEVALUE,JP_NODEID,JP_KEYVALUE,JP_NAME,JP_LAUNCHERID,ROW_NUMBER() OVER(ORDER BY JP_LAUNCHTIME DESC) RN,JP_LAUNCHERNAME,EM_DEPART FROM JNODE LEFT JOIN JPROCESS ON JN_PROCESSINSTANCEID=JP_PROCESSINSTANCEID AND JN_NAME=JP_NODENAME "
			+ "LEFT JOIN EMPLOYEE ON EM_CODE=JP_LAUNCHERID";
	static final String PROCESS_ALREADYDO_CONDITION =" WHERE JP_CODEVALUE IS NOT NULL AND JN_DEALMANID=? ";
			
	// 我发起的未审批
	static final String PROCESS_ALREADYLAUNCH_UNDO = " SELECT JP_NAME||' '||JP_CODEVALUE JP_PROCESSDESC,JP_NODEID,JP_NODEDEALMAN,JP_NODEDEALMANNAME,JP_NAME,JP_CODEVALUE,to_char(JP_LAUNCHTIME,'yyyy-mm-dd HH24:mi:ss') JP_LAUNCHTIME,ROW_NUMBER() OVER(ORDER BY JP_LAUNCHTIME asc) RN ,JP_STATUS,JP_LAUNCHERID,EM_DEPART FROM PROCESS_LAUNCH_VIEW  LEFT JOIN EMPLOYEE ON EM_CODE=JP_NODEDEALMAN";
	static final String PROCESS_ALREADYLAUNCH_UNDO_CONDITION =" WHERE JP_LAUNCHERID=? AND JP_STATUS='待审批'  ";
	// 我发起的已审批 JN_DEALTIME JP_PROCESSDESC
	static final String PROCESS_ALREADYLAUNCH_DONE = "SELECT JP_NAME||' '||JP_CODEVALUE JP_PROCESSDESC,JP_NODEID,JP_NODEDEALMANNAME,JP_NAME,JP_CODEVALUE,to_char(JP_LAUNCHTIME,'yyyy-mm-dd HH24:mi:ss') JP_LAUNCHTIME, JN_DEALTIME,ROW_NUMBER() OVER(ORDER BY JP_LAUNCHTIME desc) RN ,JP_STATUS,JP_LAUNCHERID,EM_DEPART FROM PROCESS_LAUNCH_VIEW left join jnode on jp_form=JNODE.JN_PROCESSINSTANCEID and jp_nodename=jn_name and jp_nodedealman=JNODE.JN_DEALMANID LEFT JOIN EMPLOYEE ON EM_CODE=JP_NODEDEALMAN";
	static final String PROCESS_ALREADYLAUNCH_DONE_CONDITION =" WHERE JP_LAUNCHERID=? AND JP_STATUS='已审批'  ";
	
	// 我的未发起
	static final String PROCESS_TOLUANCH = "SELECT PAGELINK,CODE,TITLE,ROWNUM RN FROM table(GET_REMINDDATA(?))";
	//消息中心数据转移
	static final String MESSAGE_TOHIST = "insert into message_center_hist (mch_id,mch_manid,mch_title,mch_type,mch_date,mch_remark,mch_taskid,mch_messageid,mch_flowid,mch_changedate,mch_changeremark,mch_mancode)" +
						"select mc_id,mc_manid,mc_title,mc_type,mc_date,mc_remark,mc_taskid,mc_messageid,mc_flowid,sysdate,'消息推送',mc_mancode from message_center where  mc_type=? and  mc_manid=?";
	static final String MESSAGE_DELETE = "delete from message_center where mc_type=? and  mc_manid=?";
	@Autowired
	private BaseDao baseDao;
	private EmployeeDao employeeDao;
	private DateUtil dateUtil;
	
	//传入start end 返回区间数据
	static final String GET_FLOW_PENDING = "SELECT * FROM (SELECT FLOW_PENDING_VIEW.*,ROW_NUMBER() OVER(ORDER BY FI_TIME DESC) RN FROM FLOW_PENDING_VIEW WHERE (FIR_MANCODE=? AND FI_STATUS='using')) WHERE RN>? and RN<=?";
	static final String GET_FLOW_PROCESSED = "SELECT * FROM (SELECT FLOW_PROCESSED_VIEW.*,ROW_NUMBER() OVER(ORDER BY FI_TIME DESC) RN FROM FLOW_PROCESSED_VIEW WHERE FIR_MANCODE=?) WHERE RN>? and RN<=?";
	static final String GET_FLOW_CREATED = "SELECT * FROM (SELECT FLOW_CREATED_VIEW.*,ROW_NUMBER() OVER(ORDER BY FI_TIME DESC) RN FROM FLOW_CREATED_VIEW WHERE (FI_STARTMANCODE=?)) WHERE RN>? and RN<=?";
	static final String GET_FLOW_DATACENTER = "SELECT * FROM (SELECT FLOW_DATACENTER_VIEW.*,ROW_NUMBER() OVER(ORDER BY FI_TIME DESC) RN FROM FLOW_DATACENTER_VIEW WHERE (FIR_MANCODE=? AND FI_STATUS='using')) WHERE RN>? and RN<=?";
	
	//集团流程待处理总数
	static final String COUNT_FLOW_DATACENTER = "SELECT COUNT(1) AS COUNT FROM (SELECT FLOW_DATACENTER_VIEW.*,ROW_NUMBER() OVER(ORDER BY FI_ID,FI_TIME) RN FROM FLOW_DATACENTER_VIEW WHERE (FIR_MANCODE=? AND FI_STATUS='using')  ORDER BY FI_ID,FI_TIME ) ";
	
	@Override
	public Map<String, Object> getCount(Employee employee, String timestr) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> message = new HashMap<String, Object>();
		Map<String, Object> task = new HashMap<String, Object>();
		Map<String, Object> process = new HashMap<String, Object>();
		Master master = employee.getCurrentMaster();
		String name = employee.getEm_name();
		int id = employee.getEm_id();
		String emcode = employee.getEm_code();

		String currentTime = dateUtil.currentDateString(Constant.YMD_HMS);
		map.put("time", currentTime);

		boolean flag = true;
		if (timestr == null || "".equals(timestr)) {
			flag = false;
		}
		
		String tasktype = "task";   //任务类型
		String messagetype = "message";   //消息类型
		String flowtype = "flow";    //流程类型
		/**
		 * 第一次请求时，获取实表的数据
		 */
		if(!flag){
			// 取待办任务
			String commonCondition = " nvl(ra_statuscode,' ') in ('DOING','START') and ra_emid="
					+ employee.getEm_id(); // 非未激活、已完成和已结案
			String confirmCondition = "(recorderid=" + employee.getEm_id() + " and ra_statuscode='UNCONFIRMED')";
			String taskSql = "select count(1) from projecttask left join resourceassignment on ra_taskid=id where " + commonCondition;
			String workrecordSql = "select count(1) from workrecord left join resourceassignment on wr_raid=ra_id where wr_redcord like '%将任务委托给%' and "
					+ commonCondition;
			int taskCount = baseDao.getCount(taskSql);
			// 统计待确认任务
			int confirmCount = baseDao.getCount("select count(1) from projecttask left join resourceassignment on ra_taskid=id where "
					+ confirmCondition);
			// 转移任务
			int workrecordCount = baseDao.getCount(workrecordSql);
			// 待办+待确认+转移
			if (taskCount + workrecordCount + confirmCount > 0) {
				String taskSqlData = "select recorder ,name from projecttask left join resourceassignment on ra_taskid=id where "
						+ commonCondition + " or " + confirmCondition + " order by recorddate desc";
				List<Map<String, Object>> list = baseDao.queryForList(taskSqlData);
				task.put("taskcount", list.size());
				if (list.size()>0) {
					task.put("taskdata", list.get(0).get("recorder") + "-" + list.get(0).get("name"));
				}
				map.put("task", task);
				//把临时表中的数据进行转移
				baseDao.execute(MESSAGE_TOHIST, new Object[]{tasktype,id});
				baseDao.execute(MESSAGE_DELETE, new Object[]{tasktype,id});
			} else {
				task.put("taskcount", 0);
				map.put("task", task);
			}
			//判断是否存在子账套
			if (master != null && master.getMa_soncode() != null && !master.getMa_soncode().equals(master.getMa_user())) {
				int count = baseDao.getCountByCondition("OA_MESSAGETOPUSH_VIEW", "PRD_RECIPIENTID='" + id + "'");
				if (count > 0) {
					List<Object> messagedata = baseDao.getFieldDatasByCondition(
							"OA_MESSAGEHISTORY_VIEW", "IH_CONTEXT", 
							"IHD_RECEIVEID='" + id + "' and ihd_readstatus=0 ORDER BY ih_date DESC");
					int messagecount = messagedata.size();
					message.put("allmessagecount", messagecount);
					message.put("messagecount", messagecount);
					if(messagecount > 0){					
						message.put("messagedata", messagedata.get(0));
						List<Object> CURRENTMASTER = baseDao.getFieldDatasByCondition("OA_MESSAGETOPUSH_VIEW", "distinct CURRENTMASTER",
								"PRD_RECIPIENTID='" + id + "'");
						if (CURRENTMASTER.size() > 0) {
							List<String> sqls = new ArrayList<String>();
							for (Object j : CURRENTMASTER) {
								String jmaster = j.toString();
								String update = "update " + jmaster + ".icqhistorydetail set ihd_status=-1 where ihd_status=0 and ihd_receive='" + name + "'";
								sqls.add(update);
								String delete = "Delete From " + jmaster + ".Pagingreleasedetail  where prd_recipientid=" + id;
								sqls.add(delete);
								String deletepaging = "Delete from " + jmaster + ".Pagingrelease where not exists (select 1 from " + jmaster + ".pagingreleasedetail where prd_prid=pr_id)";
								sqls.add(deletepaging);
							}
							baseDao.execute(sqls);
						}
					}
					map.put("message", message);
				} else {
					message.put("messagecount", 0);
					map.put("message", message);
				}
				//获取流程数据
				String processSql = "SELECT count(1) FROM JPROCESSVIEW where JP_NODEDEALMAN='" + employee.getEm_code()
						+ "' AND JP_STATUS='待审批'";
				int processCountAll = 0;
					processCountAll = baseDao.getCount(processSql);
				String proSql = "select count(distinct(jp_id)) from PROCESS_UNDO_VIEW left join jnode on jn_processinstanceid=JP_PROCESSINSTANCEID where JP_NODEDEALMAN='"
						+ employee.getEm_code() + "' AND JP_STATUS='待审批'";
				int processCount = baseDao.getCount(proSql);
				if (processCount > 0 || processCountAll > 0) {
					processCount = baseDao.getCount(processSql + " OR (JP_LAUNCHERID='" + emcode + "' AND JP_STATUS='未通过' )"); // 从本账套和子账套取数据
					String sqlString = "SELECT jp_launchername,jp_name FROM JPROCESSVIEW where JP_NODEDEALMAN='"
							+ employee.getEm_code()
							+ "' AND JP_STATUS='待审批'"
							+ " OR (JP_LAUNCHERID='"
							+ emcode
							+ "' AND JP_STATUS='未通过' ) order by jp_launchtime desc";
					List<Map<String, Object>> list = baseDao.queryForList(sqlString);
					process.put("processcount", list.size());
					if (list.size()>0) {
						process.put("processdata", list.get(0).get("jp_launchername") + "-" + list.get(0).get("jp_name"));
					}
					map.put("process", process);
					//把子账套的中间表数据都转移了
					messageDatatoHist(emcode,flowtype);
				} else {
					process.put("processcount", 0);
					map.put("process", process);
				}
			} else {
				//不存在子账套的
				String sql = "select count(1) from pagingrelease left join pagingreleasedetail on prd_prid=pr_id where prd_recipientid='" + id
						+ "'";
				int count = baseDao.getCount(sql);
				if (count > 0) {
					List<Object> messagedata = baseDao.getFieldDatasByCondition(
							"icqhistory left join icqhistorydetail on ihd_ihid=ih_id", "IH_CONTEXT", "IHD_RECEIVEID='" + id + "' and ihd_readstatus=0 ORDER BY ih_date DESC");
					int messagecount = messagedata.size();
					message.put("allmessagecount", messagecount);
					message.put("messagecount", messagecount);
					if(messagecount>0){
						message.put("messagedata", messagedata.get(0));
						//更新已经发送的消息知会
						updateMessage(name,id);
					}
					map.put("message", message);
				} else {
					message.put("messagecount", 0);
					map.put("message", message);
				}
				
				// 取待审批的流程
				String proSql = "select count(distinct(jp_id)) from PROCESS_UNDO_VIEW left join jnode on jn_processinstanceid=JP_PROCESSINSTANCEID where JP_NODEDEALMAN='"
						+ employee.getEm_code() + "' AND JP_STATUS='待审批'";
				int processCount = baseDao.getCount(proSql);
				if (processCount > 0) {
					processCount = baseDao.getCount(proSql + " OR (JP_LAUNCHERID='" + emcode + "' AND JP_STATUS='未通过' )");
					String sqlString = "select JP_CODEVALUE,jp_launchername,jp_name,ROWNUM  from (select  distinct (jp_id),jp_launchername,jp_name,jp_launchtime,JP_CODEVALUE from PROCESS_UNDO_VIEW left join jnode on jn_processinstanceid=JP_PROCESSINSTANCEID where JP_NODEDEALMAN='"
							+ employee.getEm_code()
							+ "' AND JP_STATUS='待审批'"
							+ " OR (JP_LAUNCHERID='"
							+ emcode
							+ "' AND JP_STATUS='未通过' )order by jp_launchtime desc) where ROWNUM=1";
					List<Map<String, Object>> list = baseDao.queryForList(sqlString);
					process.put("processcount", list.size());
					if (list.iterator().hasNext()) {
						process.put("processdata", list.get(0).get("jp_launchername") + "-" + list.get(0).get("jp_name") + "" + list.get(0).get("JP_CODEVALUE"));
					}
					map.put("process", process);
					//把临时表中的数据进行转移
					baseDao.execute(MESSAGE_TOHIST, new Object[]{flowtype,id});
					baseDao.execute(MESSAGE_DELETE, new Object[]{flowtype,id});
				} else {
					process.put("processcount", 0);
					map.put("process", process);
				}
			}
		}else{   /**之后获取数据通过中间表来获取*/
			//获取任务
			int taskcount = baseDao.getCount("select count(*) from message_center where mc_type='"+tasktype+"' and mc_manid=" + id);
			if (taskcount > 0) {
				Object taskdata = baseDao.getFieldDataByCondition("(select mc_title,rownum rn from message_center where mc_type='"+tasktype+"' and mc_manid=" + id + " order by mc_date desc)", 
						"mc_title", "rn=1");
				task.put("taskcount", taskcount);
				task.put("taskdata", (String)taskdata);
				map.put("task", task);
				//把临时表中的数据进行转移
				baseDao.execute(MESSAGE_TOHIST, new Object[]{tasktype,id});
				baseDao.execute(MESSAGE_DELETE, new Object[]{tasktype,id});
			} else {
				task.put("taskcount", 0);
				map.put("task", task);
			}
			if (master != null && master.getMa_soncode() != null && !master.getMa_soncode().equals(master.getMa_user())) {
				//获取消息
				int messagecount = baseDao.getCount("select count(*) from MESSAGE_CENTER_VIEW where mcv_type='"+messagetype+"' and mcv_mancode='"+emcode+"'");
				if(messagecount > 0) {
					Object messagedata = baseDao.getFieldDataByCondition("(select mcv_title,rownum rn from MESSAGE_CENTER_VIEW where mcv_type='"+messagetype+"' and mcv_mancode='" + emcode + "' order by mcv_date desc)", 
							"mcv_title", "rn=1");
					message.put("allmessagecount", messagecount);
					message.put("messagecount", messagecount);
					message.put("messagedata", (String)messagedata);
					map.put("message", message);
					//更新知会消息的状态
					List<Object> mastername = baseDao.getFieldDatasByCondition("MESSAGE_CENTER_VIEW", "distinct mcv_master",
							"mcv_mancode='" + emcode + "'");
					if (mastername.size() > 0) {
						List<String> sqls = new ArrayList<String>();
						for (Object j : mastername) {
							String jmaster = j.toString();
							String update = "update " + jmaster + ".icqhistorydetail set ihd_status=-1 where ihd_status=0 and ihd_receive='" + name + "'";
							sqls.add(update);
							String delete = "Delete From " + jmaster + ".Pagingreleasedetail  where prd_recipientid in (select em_id from  " + jmaster + ".employee where em_code='"+emcode+"')";
							sqls.add(delete);
							String deletepaging = "Delete from " + jmaster + ".Pagingrelease where not exists (select 1 from " + jmaster + ".pagingreleasedetail where prd_prid=pr_id)";
							sqls.add(deletepaging);
						}
						baseDao.execute(sqls);
					}
				} else {
					message.put("messagecount", 0);
					map.put("message", message);
				}
				//获取流程
				int flowcount = baseDao.getCount("select count(*) from MESSAGE_CENTER_VIEW where mcv_type='"+flowtype+"' and mcv_mancode='"+emcode+"'");
				if (flowcount > 0) {
					Object flowdata = baseDao.getFieldDataByCondition("(select mcv_title,rownum rn from MESSAGE_CENTER_VIEW where mcv_type='"+flowtype+"' and mcv_mancode='" + emcode + "' order by mcv_date desc)", 
							"mcv_title", "rn=1");
					process.put("processcount", flowcount);
					process.put("processdata", (String)flowdata);
					map.put("process", process);
					//把子账套的中间表数据都转移了
					messageDatatoHist(emcode,flowtype);
				} else {
					process.put("processcount", 0);
					map.put("process", process);
				}
			}else{
				//获取消息
				int messagecount = baseDao.getCount("select count(*) from message_center where mc_type='"+messagetype+"' and mc_manid=" + id);
				if (messagecount > 0) {
					Object messagedata = baseDao.getFieldDataByCondition("(select mc_title,rownum rn from message_center where mc_type='"+messagetype+"' and mc_manid=" + id + " order by mc_date desc)", 
							"mc_title", "rn=1");
					message.put("allmessagecount", messagecount);
					message.put("messagecount", messagecount);
					message.put("messagedata", (String)messagedata);
					map.put("message", message);
					//更新知会消息的状态
					updateMessage(name,id);
				} else {
					message.put("messagecount", 0);
					map.put("message", message);
				}
				//获取流程
				int flowcount = baseDao.getCount("select count(*) from message_center where mc_type='"+flowtype+"' and mc_manid=" + id);
				if (flowcount > 0) {
					Object flowdata = baseDao.getFieldDataByCondition("(select mc_title,rownum rn from message_center where mc_type='"+flowtype+"' and mc_manid=" + id + " order by mc_date desc)", 
							"mc_title", "rn=1");
					process.put("processcount", flowcount);
					process.put("processdata", (String)flowdata);
					map.put("process", process);
					//把临时表中的数据进行转移
					baseDao.execute(MESSAGE_TOHIST, new Object[]{flowtype,id});
					baseDao.execute(MESSAGE_DELETE, new Object[]{flowtype,id});
				} else {
					process.put("processcount", 0);
					map.put("process", process);
				}
			}
		}
		return map;
	}

	/**
	 * 把子账套中的中间表数据转移到历史表
	 * @param emcode 人员编号
	 * @param type 类型
	 */
	private void messageDatatoHist(String emcode,String type){
		//把子账套的中间表数据都转移了
		List<Object>  mastername = baseDao.getFieldDatasByCondition("MESSAGE_CENTER_VIEW", "distinct MCV_MASTER", "mcv_mancode='" + emcode + "' and mcv_type='"+type+"'");
		if (mastername.size() > 0) {
			List<String> sqls = new ArrayList<String>();
			for (Object j : mastername) {
				String jmaster = j.toString();
				String changeSql = "insert into "+jmaster+".message_center_hist (mch_id,mch_manid,mch_title,mch_type,mch_date,mch_remark,mch_taskid,mch_messageid,mch_flowid,mch_changedate,mch_changeremark,mch_mancode)" +
						"select mc_id,mc_manid,mc_title,mc_type,mc_date,mc_remark,mc_taskid,mc_messageid,mc_flowid,sysdate,'消息推送',mc_mancode from "+jmaster+".message_center where  mc_type='"+type+"' and  mc_mancode='"+emcode+"'";
				sqls.add(changeSql);
				String deleteSql = "delete from "+jmaster+".message_center where mc_type='"+type+"' and  mc_mancode='"+emcode+"'";
				sqls.add(deleteSql);
			}
			baseDao.execute(sqls);
		}
	}
	/**
	 * 更新已经发送的消息知会
	 * @param name
	 * @param id
	 */
	private void updateMessage(String name,int id){
		List<String> sqls = new ArrayList<String>();
		String update = "update icqhistorydetail set ihd_status=-1 where ihd_status=0 and ihd_receive='" + name + "'";
		sqls.add(update);
		String delete = "Delete From Pagingreleasedetail  where prd_recipientid=" + id;
		sqls.add(delete);
		String deletepaging = "Delete from Pagingrelease where not exists (select 1 from pagingreleasedetail where prd_prid=pr_id)";
		sqls.add(deletepaging);
		baseDao.execute(sqls);
	}
	
	@Override
	public List<Map<String, Object>> getMessageData(Employee employee, String condition, String likestr, Integer page, Integer pageSize) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Master master = employee.getCurrentMaster();
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		if (likestr == null || "".equals(likestr) || "null".equals(likestr)) {
			likestr = "1=1";
		}
		if (master != null && master.getMa_soncode() != null && !master.getMa_soncode().equals(master.getMa_user())) {
			String sqlString = "SELECT * FROM (SELECT CURRENTMASTER,IH_ID,IHD_ID,IHD_READSTATUS,IH_CALL,IHD_RECEIVE,TO_CHAR(IH_DATE,'yyyy-mm-dd HH24:mi:ss') IH_DATE,"
					+ "nvl(IH_FROM,'ptzh') as IH_FROM,IH_CONTEXT,TO_CHAR(IHD_READTIME,'yyyy-mm-dd HH24:mi:ss') IHD_READTIME,ROW_NUMBER() OVER(ORDER BY IH_DATE DESC) RN FROM "
					+ "OA_MESSAGEHISTORY_VIEW WHERE " + condition + " and " + likestr + ") WHERE RN>=" + start + " AND RN<=" + end;
			list = baseDao.queryForList(sqlString);
		} else {
			String sqlString = "SELECT A.*,'"
					+ master.getMa_name()
					+ "' CURRENTMASTER FROM (SELECT IH_ID,IHD_ID,IHD_READSTATUS,IH_CALL,IHD_RECEIVE,TO_CHAR(IH_DATE,'yyyy-mm-dd HH24:mi:ss') IH_DATE,"
					+ "nvl(IH_FROM,'ptzh') as IH_FROM,IH_CONTEXT,TO_CHAR(IHD_READTIME,'yyyy-mm-dd HH24:mi:ss') IHD_READTIME,ROW_NUMBER() OVER(ORDER BY IH_DATE DESC) RN FROM "
					+ "ICQHISTORY LEFT JOIN icqhistorydetail ON IH_ID=IHD_IHID WHERE " + condition + " and " + likestr + ")A WHERE RN>="
					+ start + " AND RN<=" + end;
			list = baseDao.queryForList(sqlString);
		}
		return list;
	}

	@Override
	public int getMessageTotal(Employee employee, String condition, String likestr, Integer page, Integer pageSize) {
		if (likestr == null || "".equals(likestr) || "null".equals(likestr)) {
			likestr = "1=1";
		}
		Master master = employee.getCurrentMaster();
		if (master != null && master.getMa_soncode() != null && !master.getMa_soncode().equals(master.getMa_user())) {

			return baseDao.getCountByCondition("OA_MESSAGEHISTORY_VIEW", condition + " and " + likestr);
		} else {
			return baseDao.getCountByCondition("icqhistorydetail , icqhistory ", condition + " and  ihd_ihid=ih_id and " + likestr);
		}
	}

	@Override
	public Map<String, Object> getTaskData(Employee employee, String condition, String fields, String likestr, Integer page,
			Integer pageSize, String type) {
		Map<String, Object> countMap = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		if (likestr == null || "".equals(likestr) || "null".equals(likestr)) {
			likestr = "1=1";
		}
		// 日常任务待处理
		String countCondition = "(ra_emid=" + employee.getEm_id()
				+ "  AND ra_taskpercentdone<100 AND nvl(ra_statuscode,' ')<>'ENDED') and nvl(ra_statuscode,' ')<>'UNACTIVE' ";
		String confirmCondition = "(recorderid=" + employee.getEm_id() + " and ra_statuscode='UNCONFIRMED')";
		int normalCount = baseDao
				.getCount("select count(1) from resourceassignment left join projecttask on ra_taskid=id where ra_type in ('billtask','communicatetask') and ("
						+ countCondition + " or " + confirmCondition + ") and " + likestr);
		countMap.put("normalTaskCount", normalCount);
		// 项目任务待处理
		int projectTaskCount = baseDao
				.getCount("select count(1) from resourceassignment left join projecttask on ra_taskid=id where ra_type in ('projecttask','worktask') and ("
						+ countCondition + " or " + confirmCondition + ") and " + likestr);
		countMap.put("projectTaskCount", projectTaskCount);
		map.put("count", countMap);
		String employeeCondition = " ra_resourcecode in (";
		String emcode = employee.getEm_code();
		boolean staff = false;
		StringBuffer empConditionSb = new StringBuffer();
		if (condition.indexOf("staff=1") > -1) {
			staff = true;
			condition = condition.replace(" and staff=1", "");
			Boolean bool = baseDao.checkIf("hrorg", "or_headmancode='" + emcode + "'");
			SqlRowList srl = baseDao
					.queryForRowSet("select * from employee where em_defaultorid in (select or_id from hrorg connect by prior or_id=or_subof start with or_id=(select em_defaultorid from employee where em_code='"
							+ emcode + "') and nvl(or_statuscode,' ') <> 'DISABLE')  and NVL(em_class,' ')<>'离职'");
			if (bool) {
				while (srl.next()) {
					empConditionSb.append(",'" + srl.getString("em_code") + "'");
				}
				if (empConditionSb.length() > 0) {
					employeeCondition += empConditionSb.substring(1) + ") and nvl(ra_resourcecode,' ')<>'" + emcode + "'";
				} else {
					employeeCondition += "'')"; // 下属为空
				}

			} else {
				employeeCondition += "'')"; // 下属为空
			}
		}
		if (page != null && pageSize != null) {
			int end = page * pageSize;
			int start = end - pageSize;
			String pageCondition = " rn<=" + end + " and rn>" + start;
			String sql = "select * from (select rownum rn,a.* from (select ra_id,ra_taskid,ra_type,ra_taskname,ra_resourcename,recorder,to_char(REALENDDATE,'yyyy-mm-dd') REALENDDATE,to_char(startdate,'yyyy-mm-dd') startdate,to_char(enddate,'yyyy-mm-dd') enddate,ra_taskpercentdone,ra_status,sourcecode,"
					+ "prjplanname,prj_assignto from resourceassignment left join projecttask on id=ra_taskid "
					+ "left join project on prjplanid=prj_id where "
					+ (staff ? employeeCondition + " and " : "")
					+ likestr
					+ " and "
					+ condition
					+ (condition.indexOf("or") > -1 ? (staff ? " and " + employeeCondition : "") : "")
					+ " order by "
					+ (condition.indexOf("FINISHED") > -1 ? "enddate desc" : "startdate asc") + ")a) where " + pageCondition;
			List<Map<String, Object>> datas = baseDao.queryForList(sql);
			String totalSql = "select count(1)  from projecttask left join resourceassignment on id=ra_taskid "
					+ "left join project on prjplanid=prj_id where " + (staff ? employeeCondition + " and " : "") + likestr + " and "
					+ condition + (condition.indexOf("or") > -1 ? (staff ? " and " + employeeCondition : "") : "");
			SqlRowList rs = baseDao.queryForRowSet(totalSql);
			map.put("data", datas);
			if (rs.next()) {
				map.put("total", rs.getInt(1));
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> getProcessData(Employee em, String type, String likestr, Integer page, Integer limit) {
		Map<String, Object> map = new HashMap<String, Object>();
		Master master=em.getCurrentMaster();
		String emcode = em.getEm_code();
		Integer end = page*limit;
		Integer start = end - limit;
		boolean group = false;
		
		int toDoCount, toLaunchCount, alreadyDoCount, alreadyLaunchUndoCount, alreadyLaunchDoneCount;
		if (likestr == null || "".equals(likestr) || "null".equals(likestr)) likestr = "1=1";
		if (master != null && master.getMa_soncode() != null && !master.getMa_soncode().equals(master.getMa_user())) {
			group = true; // 集团版
		}
		if ("toDo".equals(type)) { // 我接收的待审批
			if (group) {
				map.put("data",baseDao.queryForList(getfilterSql(GET_PROCESS_UNDO_GROUP, likestr,"toDo_group"), new Object[] { emcode, emcode, end, start }));
			} else {
				map.put("data", baseDao.queryForList(getfilterSql(GET_PROCESS_UNDO, likestr,"toDo"), new Object[] { emcode, emcode, end, start }));
			}
		} else if ("toLaunch".equals(type)) { // 待发起 -- 我未发起的流程
			map.put("data", baseDao.queryForList(getfilterSql(GET_PROCESS_TOLUANCH, likestr,"toLaunch"), new Object[] { emcode, end, start }));
		} else if ("alreadyDo".equals(type)) { // 已办理 --我接收的已审批
			map.put("data", baseDao.queryForList(getfilterSql(GET_PROCESS_ALREADYDO, likestr,"alreadyDo"), new Object[] { emcode, end, start }));
			alreadyDoCount = baseDao.getCountByCondition("(select * from ("+PROCESS_ALREADYDO+"))", "JP_CODEVALUE IS NOT NULL AND JN_DEALMANID='" + emcode + "' and "+likestr.replace("to_char(JN_DEALTIME,'yyyy-MM-dd')", "JN_DEALTIME").replace("to_char(JP_LAUNCHTIME,'yyyy-MM-dd')", "JP_LAUNCHTIME").replace("to_char(JP_REMINDDATE,'yyyy-MM-dd')", "JP_REMINDDATE"));
			map.put("alreadyDoCount", alreadyDoCount);
		} else if ("alreadyLaunchUndo".equals(type)) { // 已发起 未审批
			map.put("data",baseDao.queryForList(getfilterSql(GET_PROCESS_ALREADYLAUNCH_UNDO, likestr,"alreadyLaunchUndo"), new Object[] { emcode, end, start }));
			alreadyLaunchUndoCount = baseDao.getCountByCondition("(select * from ("+PROCESS_ALREADYLAUNCH_UNDO+"))", "JP_LAUNCHERID='" + emcode+ "' AND JP_STATUS='待审批' AND " + likestr.replace("to_char(JP_LAUNCHTIME,'yyyy-MM-dd')", "JP_LAUNCHTIME"));
			map.put("alreadyLaunchUndoCount", alreadyLaunchUndoCount);
		} else if ("alreadyLaunchDone".equals(type)) { // 已发起已审批
			map.put("data",baseDao.queryForList(getfilterSql(GET_PROCESS_ALREADYLAUNCH_DONE, likestr,"alreadyLaunchDone"), new Object[] { emcode, end, start }));
			alreadyLaunchDoneCount = baseDao.getCountByCondition("(select * from ("+PROCESS_ALREADYLAUNCH_DONE+"))", "JP_LAUNCHERID='" + emcode+ "' AND JP_STATUS='已审批' AND " + likestr.replace("to_char(JN_DEALTIME,'yyyy-MM-dd')", "JN_DEALTIME").replace("to_char(JP_LAUNCHTIME,'yyyy-MM-dd')", "JP_LAUNCHTIME"));
			map.put("alreadyLaunchDoneCount", alreadyLaunchDoneCount);
		}
		if (group) {
			if("toDo".equals(type)){
				toDoCount = baseDao
						.getCountByCondition(
						"(select * from ("+PROCESS_UNDO_GROUP_DATAFIELD+",JP_KEYVALUE,JP_FORM,JP_FLAG,CURRENTMASTER,JP_STATUS,JP_LAUNCHERID,JP_NODEDEALMAN FROM JPROCESSVIEW))",
						likestr+" and ((JP_NODEDEALMAN='"+emcode+"' AND JP_STATUS='待审批')  OR (JP_LAUNCHERID='"+emcode+"' AND JP_STATUS='未通过' ))"
						);
			}else{//待定
				toDoCount = baseDao.getCountByCondition("JPROCESSVIEW", "(JP_NODEDEALMAN='"+emcode+"' AND JP_STATUS='待审批')  OR (JP_LAUNCHERID='"+emcode+"' AND JP_STATUS='未通过' )");
			}
		} else {
			if("toDo".equals(type)){
				toDoCount = baseDao
					.getCountByCondition(
							"(select * from ("+PROCESS_UNDO_GROUP_DATAFIELD+",JP_STATUS,JP_LAUNCHERID,JP_NODEDEALMAN,JP_REMINDDATE FROM PROCESS_UNDO_VIEW"+"))",
							likestr.replace("to_char(JP_LAUNCHTIME,'yyyy-MM-dd')", "JP_LAUNCHTIME")+" and ((JP_NODEDEALMAN='" + emcode + "' AND JP_STATUS='待审批' ) OR (JP_LAUNCHERID='" + emcode + "' AND JP_STATUS='未通过'))"
							) ;
			}else{
				toDoCount = baseDao.getCountByCondition("PROCESS_UNDO_VIEW", "(JP_NODEDEALMAN='"+emcode+"' AND JP_STATUS='待审批')  OR (JP_LAUNCHERID='"+emcode+"' AND JP_STATUS='未通过' )");
			}
		}
		map.put("toDoCount", toDoCount);
		if ("toLaunch".equals(type)) {
			toLaunchCount = baseDao.getCountByCondition("table(GET_REMINDDATA('" + emcode + "'))", "1=1 AND " + likestr);
		} else {
			toLaunchCount = baseDao.getCountByCondition("table(GET_REMINDDATA('" + emcode + "'))", "1=1 ");
		}
		map.put("toLaunchCount", toLaunchCount);
		map.put("total", map.get(type + "Count"));
		return map;
	}
	
	@Override
	public Map<String, Object> getFlowData(Employee em, String type, String likestr, Integer page, Integer limit) {
		Map<String, Object> map = new HashMap<String, Object>();
		Master master=em.getCurrentMaster();
		String emcode = em.getEm_code();
		Integer end = page*limit;
		Integer start = end - limit;
		boolean group = false;
		int pendingCount = 0, processedCount = 0, createdCount = 0;
		if (likestr == null || "".equals(likestr) || "null".equals(likestr)) likestr = "1=1";
		if (master != null && master.getMa_soncode() != null && !master.getMa_soncode().equals(master.getMa_user())) {
			group = true; // 集团版
		}
		//处理数据
		if ("pending".equals(type)) { // 我待处理的
			if (group) {
				map.put("data",(baseDao.queryForList(GET_FLOW_DATACENTER + " and " + likestr, new Object[] { emcode, start, end })));
			} else {
				map.put("data", baseDao.queryForList(GET_FLOW_PENDING + " and " + likestr, new Object[] { emcode, start, end }));
			}
		} else if ("processed".equals(type)) { // 我已处理的
			map.put("data", baseDao.queryForList(GET_FLOW_PROCESSED + " and " + likestr, new Object[] { emcode, start, end }));
		} else if ("created".equals(type)) { // 我已发起的
			map.put("data",baseDao.queryForList(GET_FLOW_CREATED + " and " + likestr, new Object[] { emcode, start, end }));
		}
		if(group){
			List<Map<String,Object>> countMap = baseDao.queryForList(COUNT_FLOW_DATACENTER + " where " + likestr,new Object[] { emcode });
			pendingCount = Integer.valueOf(String.valueOf(countMap.get(0).get("COUNT")));
		}else{
			pendingCount = baseDao.getCountByCondition("FLOW_INSTANCE LEFT JOIN FLOW_INSTANCEROLE ON FI_ID = FIR_FIID",likestr + "AND FI_STATUS = 'using' AND FIR_MANCODE ='" + emcode + "' AND FIR_TYPE = 'duty' OR FIR_TYPE = 'actor' ORDER BY FI_ID DESC");
		}
		processedCount = baseDao.getCountByCondition("FLOW_INSTANCE LEFT JOIN FLOW_INSTANCEROLE ON FI_ID = FIR_FIID",likestr + "AND FI_STATUS = 'end' AND FI_NODENAME<>'START' AND FIR_MANCODE ='" + emcode + "' AND FIR_TYPE = 'duty' ORDER BY FI_ID DESC");
		createdCount = baseDao.getCountByCondition("FLOW_INSTANCE",likestr + "AND FI_NODENAME = 'START' AND FI_STARTMANCODE ='" + emcode + "' ORDER BY FI_ID DESC");
		map.put("pendingCount", pendingCount);
		map.put("processedCount", processedCount);
		map.put("createdCount", createdCount);
		map.put("total", map.get(type + "Count"));
		return map;
	}

	@Override
	public Map<String, Object> getmessageCount(Employee employee) {
		Map<String, Object> map = new HashMap<String, Object>();
		Master master = employee.getCurrentMaster();
		String countSql = "";
		if (master != null && master.getMa_soncode() != null && !master.getMa_soncode().equals(master.getMa_user())) {
			countSql = "select count(ihd_id) as cou,count(case when ih_from ='process' then 'process' end) process,"
					+ "count(case when ih_from ='task' then 'process' end)task,count(case when ih_from ='note' then 'note' end) note,count(case when nvl(ih_from,' ')not in ('process','task','note','b2b') then 'other' end) other,count(case when ih_from ='b2b' then 'b2b' end) b2b from "
					+ "OA_MESSAGEHISTORY_VIEW where IHD_RECEIVEID=" + employee.getEm_id() + " and IHD_READSTATUS=0";
		} else {
			countSql = "select count(ihd_id) as cou,count(case when ih_from ='process' then 'process' end) process,"
					+ "count(case when ih_from ='task' then 'process' end)task,count(case when ih_from ='note' then 'note' end) note,count(case when nvl(ih_from,' ')not in ('process','task','note','b2b') then 'other' end) other,count(case when ih_from ='b2b' then 'b2b' end) b2b from icqhistory "
					+ "left join ICQHISTORYDETAIL on ihd_ihid=ih_id  where IHD_RECEIVEID=" + employee.getEm_id() + " and IHD_READSTATUS=0";
		}
		SqlRowList rs = baseDao.queryForRowSet(countSql);
		if (rs.next()) {
			map.put("all", rs.getInt("cou"));
			map.put("process", rs.getInt("process"));
			map.put("task", rs.getInt("task"));
			map.put("note", rs.getInt("note"));
			map.put("system", rs.getInt("other"));
			map.put("b2b", rs.getInt("b2b"));
		}
		return map;
	}

	@Override
	public Boolean getMessageContent(Employee employee, Integer id, String master) {

		baseDao.updateByCondition(master + ".ICQHISTORYDETAIL", "IHD_READSTATUS=-1", "ihd_ihid='" + id + "'");

		return true;
	}

	@Override
	public Boolean updateReadstatus(String data) {
		List<Map<Object, Object>> datastore = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> i : datastore) {
			baseDao.updateByCondition(i.get("CURRENTMASTER") + ".icqhistorydetail",
					" IHD_READSTATUS=-1 , IHD_READTIME=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()),
					" IHD_ID=" + i.get("IHD_ID"));
		}

		return true;
	}

	@Override
	public Object getFieldData(String caller, String field, String condition) {
		return baseDao.getFieldDataByCondition(caller, field, condition);
	}

	@Override
	public List<Map<String, Object>> searchData(Employee employee, String condition, String type, String filed) {
		Master master = employee.getCurrentMaster();
		String emcode = employee.getEm_code();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if ("task".equals(type)) {

		} else {
			if (master != null && master.getMa_soncode() != null && !master.getMa_soncode().equals(master.getMa_user())) {
				if ("process".equals(type)) {

				} else if ("message".equals(type)) {
					String sqlString = "select * from(SELECT '"
							+ master.getMa_name()
							+ "' CURRENTMASTER,IH_ID,IHD_ID,IHD_READSTATUS,IH_CALL,IHD_RECEIVE,TO_CHAR(IH_DATE,'yyyy-mm-dd HH24:mi:ss') IH_DATE,"
							+ "IH_FROM,IH_CONTEXT,TO_CHAR(IHD_READTIME,'yyyy-mm-dd HH24:mi:ss') IHD_READTIME,ROW_NUMBER() OVER(ORDER BY IH_DATE DESC)  FROM "
							+ "OA_MESSAGEHISTORY_VIEW WHERE " + filed + ")where " + condition;
					list = baseDao.queryForList(sqlString);

				} else {
					BaseUtil.showError("返回的type类型错误");
				}
			} else {
				if ("process".equals(type)) {

					if ("toDo".equals(filed)) {
						String sqlString = "SELECT * FROM (SELECT TO_CHAR(JP_REMINDDATE,'yyyy-mm-dd HH24:mi:ss') JP_REMINDDATE,JP_CODEVALUE,JP_NODEID,JP_LAUNCHERNAME,JP_NAME,nvl(JP_PROCESSNOTE,' ') JP_PROCESSNOTE,to_char(JP_LAUNCHTIME,'yyyy-mm-dd HH24:mi:ss') JP_LAUNCHTIME,TYPECODE,ROW_NUMBER() OVER(ORDER BY JP_LAUNCHTIME) FROM PROCESS_UNDO_VIEWWHERE where (JP_NODEDEALMAN=? AND JP_STATUS='待审批')  OR (JP_LAUNCHERID=? AND JP_STATUS='未通过' )) WHERE "
								+ condition;
						list = baseDao.queryForList(sqlString, new Object[] { emcode, emcode });
					} else if ("toLaunch".equals(filed)) {

					} else if ("alreadyDo".equals(filed)) {

					} else if ("alreadyLaunchUndo".equals(filed)) {

					} else if ("alreadyLaunchDone".equals(filed)) {

					}

				} else if ("message".equals(type)) {

					String sqlString = "select * from(SELECT '"
							+ master.getMa_name()
							+ "' CURRENTMASTER,IH_ID,IHD_ID,IHD_READSTATUS,IH_CALL,IHD_RECEIVE,TO_CHAR(IH_DATE,'yyyy-mm-dd HH24:mi:ss') IH_DATE,"
							+ "IH_FROM,IH_CONTEXT,TO_CHAR(IHD_READTIME,'yyyy-mm-dd HH24:mi:ss') IHD_READTIME,ROW_NUMBER() OVER(ORDER BY IH_DATE DESC)  FROM "
							+ "ICQHISTORY LEFT JOIN icqhistorydetail ON IH_ID=IHD_IHID WHERE " + filed + ")where " + condition;
					list = baseDao.queryForList(sqlString);
				} else {
					BaseUtil.showError("返回的type类型错误");
				}

			}

		}

		return list;
	}

	/**
	 * 主要处理流程中心过滤sql
	 * 
	 * @param sql
	 *            原执行sql
	 * @param likestr
	 *            需要拼接处理条件
	 */
	public String getfilterSql(String sql, String likestr,String type) {
		String filterSql = sql;
		if(type.equals("toDo_group")){//我接收的未审批(集团版) 
			//JP_PROCESSDESC
			filterSql = "SELECT * FROM ( "+PROCESS_UNDO_GROUP_DATAFIELD+",JP_KEYVALUE,JP_FORM,JP_FLAG,CURRENTMASTER,EM_DEPART FROM JPROCESSVIEW LEFT JOIN EMPLOYEE ON EM_CODE=JP_LAUNCHERID  "+PROCESS_UNDO_CONDITION+" and "+likestr.replace("JP_PROCESSDESC", "JP_NAME||' '||JP_CODEVALUE")+PROCESS_UNDO_PAGING; 
		}else if(type.equals("toDo")){//我接收的未审批  
			//JP_PROCESSDESC
			filterSql = "SELECT * FROM ( "+PROCESS_UNDO_GROUP_DATAFIELD+",TO_CHAR(JP_REMINDDATE,'yyyy-mm-dd HH24:mi:ss') JP_REMINDDATE,EM_DEPART FROM PROCESS_UNDO_VIEW LEFT JOIN EMPLOYEE ON EM_CODE=JP_LAUNCHERID  "+PROCESS_UNDO_CONDITION+" and "+likestr.replace("JP_PROCESSDESC", "JP_NAME||' '||JP_CODEVALUE")+PROCESS_UNDO_PAGING; 
		}else if(type.equals("alreadyDo")){//我接收的已审批 
			//JP_PROCESSDESC  JN_DEALTIME JN_OPERATEDDESCRIPTION
			likestr = likestr.replace("JP_PROCESSDESC", "JP_NAME||' '||JP_CODEVALUE").replace("to_char(JN_DEALTIME,'yyyy-MM-dd')", "JN_DEALTIME").replace("JN_OPERATEDDESCRIPTION", "JN_INFORECEIVER||JN_OPERATEDDESCRIPTION");
			int index = likestr.indexOf("JN_DEALTIME=");
			if(index>=0){
				likestr=likestr.replace("JN_DEALTIME=", "JN_DEALTIME like ");
				index=likestr.indexOf("JN_DEALTIME");
				likestr=likestr.substring(0,index+28)+"%"+likestr.substring(index+28);
			}
			filterSql = "SELECT * FROM ( "+PROCESS_ALREADYDO+PROCESS_ALREADYDO_CONDITION+" and "
			+likestr+" ORDER BY JP_LAUNCHTIME DESC ) WHERE RN<=? AND RN>?"; 
		}else if(type.equals("alreadyLaunchUndo")){//我发起的未审批   
			//JP_PROCESSDESC 
			filterSql = "SELECT * FROM ( "+PROCESS_ALREADYLAUNCH_UNDO+PROCESS_ALREADYLAUNCH_UNDO_CONDITION+" and "
			+likestr.replace("JP_PROCESSDESC", "JP_NAME||' '||JP_CODEVALUE")+" ORDER BY JP_LAUNCHTIME asc ) WHERE RN<=? AND RN>?";
		}else if(type.equals("alreadyLaunchDone")){//我发起的已审批 
			//JN_DEALTIME  JP_PROCESSDESC 
			likestr = likestr.replace("JP_PROCESSDESC", "JP_NAME||' '||JP_CODEVALUE").replace("to_char(JN_DEALTIME,'yyyy-MM-dd')", "JN_DEALTIME");
			int index = likestr.indexOf("JN_DEALTIME=");
			if(index>=0){
				likestr=likestr.replace("JN_DEALTIME=", "JN_DEALTIME like ");
				index=likestr.indexOf("JN_DEALTIME");
				likestr=likestr.substring(0,index+28)+"%"+likestr.substring(index+28);
			}
			filterSql = "SELECT * FROM ( "+PROCESS_ALREADYLAUNCH_DONE+PROCESS_ALREADYLAUNCH_DONE_CONDITION+" and "
			+likestr+" ORDER BY JP_LAUNCHTIME desc )   WHERE RN<=? AND RN>?";
		}else if(type.equals("toLaunch")){// 我的未发起 
			filterSql = "SELECT * FROM ( "+PROCESS_TOLUANCH+" where "+likestr+" ) WHERE RN<=? AND RN>?";
		}
		return filterSql;
	}
}
