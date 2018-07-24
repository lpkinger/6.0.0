package com.uas.erp.service.plm.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Operation;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.TaskDao;
import com.uas.erp.model.DataList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.model.ProjectTask;
import com.uas.erp.model.TaskInfo;
import com.uas.erp.service.plm.GanttService;
import com.uas.erp.service.plm.RecordService;

@Service
public class GanttServiceImpl implements GanttService {
	@Autowired
	private TaskDao taskDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DataListDao dataListDao;
	@Autowired
	private TaskUtilService taskUtilService;
	final static String RESOURCEASSIGNMENT_SQL="INSERT INTO  RESOURCEASSIGNMENT(RA_ID,RA_DETNO,RA_TASKID,RA_PRJID,RA_RESOURCECODE,RA_RESOURCENAME,RA_EMID,RA_UNITS,RA_TYPE,RA_BASESTARTDATE) VALUES(?,?,?,?,?,?,?,?,?,?)";
	
	@Override
	public List<JSONObject> getJsonGantt(String condition, String actul) {
		List<ProjectTask> list = taskDao.getTasks(condition);
		List<JSONObject> jsons=new ArrayList<JSONObject>();
		for (int i = 0; i < list.size(); i++) {
			ProjectTask g = list.get(i);
			if (g.getParentid() == 0) {				
				recursionFn(list, g, actul);
				jsons.add(g.getGantData(actul));
			}
		}
		//实时甘特图参数控制
		if (actul != null && "true".equals(actul)) {
			dealTasks(jsons);
			return jsons;
		} else {
			return jsons;
		}
	}
	
	//处理实时甘特图
	@SuppressWarnings("unchecked")
	public void dealTasks(List<JSONObject> jsons) {
		for (JSONObject json : jsons) {
			if ((boolean) json.get("leaf")) {
				dealSonTask(json);
			} else {
				List<JSONObject> sons = (List<JSONObject>)json.get("children");
				dealTasks(sons);
			} 
		}
	}
	
	//实时甘特图处理子节点的json数据
	public void dealSonTask(JSONObject json) {
		if  ((boolean) json.get("leaf") && ("已启动".equals(json.get("handstatus")) || "已激活".equals(json.get("handstatus")) || "已完成".equals(json.get("handstatus")))) {//只对激活,完成的子任务进行处理
			Object taskId = json.get("Id");
			if (StringUtil.hasText(taskId)) {
				Object[] date = baseDao.getFieldsDataByCondition("taskrecordtime", new String[]{"tr_startdate","tr_enddate"}, "tr_taskid=" + taskId);
				if (date != null) {	//如果taskrecordtime有数据
					json.put("StartDate", date[0]);
					json.put("EndDate", date[1]);
				} else {
					//如果没有数据,推出结束日期
					Object enddate = baseDao.getFieldDataByCondition("projecttask", "to_char(sysdate+nvl(duration,0)*(1-PERCENTDONE/100),'yyyy-mm-dd hh24:mi:ss')", "id=" + taskId);
					json.put("EndDate", enddate);
				}
			}
		}
	}

	// 拼成json数据
	private void recursionFn(List<ProjectTask> list, ProjectTask g, String actul) {
		List<JSONObject> childList = getChildList(list, g, actul);
		if (childList.size() > 0) {
			g.setChildren(childList);
			g.setLeaf(false);
		}else g.setLeaf(true);	
	}
	// 获取子节点列表
	private List<JSONObject> getChildList(List<ProjectTask> list, ProjectTask g, String actul) {
		List<JSONObject> li = new ArrayList<JSONObject>();
		Iterator<ProjectTask> it = list.iterator();
		while (it.hasNext()) {
			ProjectTask t = (ProjectTask) it.next();
			if (t.getParentid() == g.getId()) {
				recursionFn(list, t, actul);
				li.add(t.getGantData(actul));
			}
		}
		return li;
	}

	@Override
	public JSONObject getData(String condition, Employee employee) {
		int id = baseDao.getSeqId("PROJECTTASK_SEQ");
		DataList dataList = dataListDao.getDataList("Project", employee.getEm_master());
		JSONObject jo = new JSONObject();
		jo.put("id", id);
		jo.put("prjplandata", BaseUtil.parseGridStore2Str(dataListDao.getDataListData(dataList, condition, employee, 1, 1, 0, false, null,false)));
		return jo;
	}

	@Override
	public void saveGantt(String jsonData) {
		Object[] id = new Object[1];
		// jsonData=jsonData.substring(0,jsonData.lastIndexOf("#@"));
		if (jsonData.contains("#@,")) {// 明细行有多行数据哦
			String[] datas = jsonData.split("#@,");
			id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				id[i] = baseDao.getSeqId("PROJECTTASK_SEQ");
			}
		} else {
			id[0] = baseDao.getSeqId("PROJECTTASK_SEQ");
		}
		taskDao.saveProjectTask(jsonData, "id", id);
	}

	@Override
	public void updateGantt(String jsonData) {
		String language = SystemSession.getLang();
		Employee employee = SystemSession.getUser();
		List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(jsonData);
		List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(lists, "ProjectTask", "Id");
		baseDao.execute(sqls);
		baseDao.logMessage(new MessageLog(employee.getEm_name(), "修改甘特图", Operation.UPDATE.getResult(language), "Gannt"));
	}

	@Override
	public void deleteGantt(String jsonData) {
		taskDao.deleteProjectTask(jsonData, "Id");
	}

	@Override
	public List<Map<String, Object>> getDependencies(String prjid) {
		return taskDao.getDependencies(prjid);
	}

	@Override
	public void saveDependency(String jsonData, String condition) {
		String prjid = condition.split("=")[1];
		Object[] id = new Object[1];
		if (jsonData.contains("},")) {// 明细行有多行数据哦
			String[] datas = jsonData.split("},");
			id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				id[i] = baseDao.getSeqId("DEPENDENCY_SEQ");
			}
		} else {
			id[0] = baseDao.getSeqId("DEPENDENCY_SEQ");
		}
		taskDao.saveDependency(jsonData, "Id", id, prjid);
	}

	@Override
	public void updateDependency(String jsonData) {
		taskDao.updateDependency(jsonData, "Id");
	}

	@Override
	public void deleteDependency(String jsonData) {
		taskDao.deleteDependency(jsonData, "Id");
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void sync(String taskcreate, String taskupdate, String taskremove,
			String assigncreate, String assignupdate, String assignremove,
			String dependencycreate, String dependencyupdate,
			String dependencyremove,String detnos, int prjId) {
		Map<Object,Integer> TaskMap=new HashMap<Object, Integer>();
		syncTask(taskcreate, taskupdate, taskremove,detnos, TaskMap,prjId);
		syncAssigns(assigncreate, assignupdate, assignremove,TaskMap, prjId);
		syncDependency(dependencycreate, dependencyupdate, dependencyremove, prjId);
		
	}

	public void syncTask(String create, String update, String delete,String detnos,Map<Object,Integer> TaskMap, int prjId) {
		// TODO Auto-generated method stub
		String language = SystemSession.getLang();
		Employee employee = SystemSession.getUser();
		StringBuffer ids=new StringBuffer();
		List<String>logSqls=new LinkedList<String>();
		String prstatuscode=baseDao.getFieldValue("PROJECT","prj_prstatuscode", "PRJ_ID="+prjId, String.class);
		boolean bool=false;
		/**新增*/
		String prjName=baseDao.getFieldValue("PROJECT","PRJ_NAME", "PRJ_ID="+prjId, String.class);
		List<String> executes=new ArrayList<String>();
		if(StringUtil.hasText(create)){
			List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(create);
			for(Map<Object,Object> map:lists){
				createTask(map,TaskMap,prjName,employee);
				executes.add(SqlUtil.getInsertSqlByMap(map, "ProjectTask"));
				executes.add("update projecttask set taskclass='"+prstatuscode + "' where prjplanid="+prjId +" and id="+map.get("Id"));
			}
		   bool=true;
		}
		if(StringUtil.hasText(update)){
			List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(update);
			for(Map<Object,Object> map:lists){
				map.put("prjplanname", prjName);
				if(map.get("Id")==null && StringUtil.hasText(map.get("Name"))){
					createTask(map,TaskMap,prjName,employee);
					executes.add(SqlUtil.getInsertSqlByMap(map, "ProjectTask"));
					executes.add("update projecttask set taskclass='"+prstatuscode + "' where prjplanid="+prjId +" and id="+map.get("Id"));
				}else if(map.get("Id")!=null){
					executes.add(SqlUtil.getUpdateSqlByFormStore(map,"ProjectTask", "Id"));	
					//更新任务对应的文件信息
					executes.add("UPDATE PROJECTDOC SET PD_TASKNAME='"+map.get("Name")+"' WHERE NVL(PD_TASKID,0)='"+map.get("Id")+"' AND PD_PRJID="+prjId);
					executes.add("update projecttask set taskclass='"+prstatuscode + "' where prjplanid="+prjId +" and id="+map.get("Id"));
					//录入日志
					String  id=getId();
					ids.append(id+",");
					logSqls.add(InsertLog(map,id));
				}		
			}		
			 bool=true;
		}
		if(StringUtil.hasText(delete)){
			List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(delete);
			for(Map<Object,Object> map:lists){
				if(map.get("Id")!=null){
					executes.add(SqlUtil.getDeleteSql("ProjectTask", "Id="+map.get("Id")));
					//删除任务更新任务对应的文件信息
					executes.add("UPDATE PROJECTDOC SET PD_TASKID=NULL,PD_TASKNAME=NULL WHERE NVL(PD_TASKID,0)='"+map.get("Id")+"' AND PD_PRJID="+prjId);
					//录入日志					
					logSqls.add(log("删除操作",map.get("Id").toString(),""));	  
				}
			}
			 bool=true;
		}
		
		baseDao.execute(logSqls);
		if(ids.length()>0)
		baseDao.execute("update tasklog set tl_type=(case when tl_name<>(select name from projecttask where id=tl_taskid) then '任务名称变更    ' else null end)||(case when tl_startdate<>(select startdate from projecttask where id=tl_taskid) then '计划日期变更'  when tl_enddate<>(select enddate from projecttask where tl_taskid=id) then '计划日期变更' else null end) where tl_id in("+ids.substring(0, ids.length()-1)+")");	
		baseDao.execute(executes);
		//刷新任务序号
		if(bool) baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(detnos,"projecttask", "id"));	
		//情况默认存在主任务的资源清空
	    baseDao.execute("UPDATE PROJECTTASK A SET LEAF=-2,RESOURCECODE=NULL,RESOURCEEMID=NULL,RESOURCENAME=NULL,RESOURCEUNITS=NULL  WHERE RESOURCECODE IS NOT NULL AND NVL(LEAF,' ')<>'-2' AND EXISTS (SELECT  1 FROM PROJECTTASK B WHERE A.ID=B.PARENTID ) AND PRJPLANID=?",prjId);
	    baseDao.deleteByCondition("Resourceassignment", " not exists (select 1 from projecttask where id=ra_taskid AND NVL(LEAF,' ')<>'-2') and ra_prjid="+prjId);
		baseDao.execute("UPDATE PROJECTDOC SET PD_TASKID=NULL,PD_TASKNAME=NULL WHERE EXISTS ( SELECT 1 FROM PROJECTTASK WHERE NVL(PD_TASKID,0)=ID AND NVL(LEAF,' ')='-2') AND PD_PRJID=?",prjId);
		
		baseDao.logMessage(new MessageLog(employee.getEm_name(), "修改任务计划", Operation.UPDATE.getResult(language), "Gannt"));
		
	}
	
	private String getId(){
		return baseDao.getFieldDataByCondition("dual", "tasklog_seq.nextval", "1=1").toString();
	}
	
	private String InsertLog(Map<Object,Object> map,String id){
		StringBuffer sql=new StringBuffer("insert into tasklog (tl_id,tl_date,tl_recordman,tl_startdate,tl_enddate,tl_resource,tl_resoccupy,tl_name,tl_taskid,tl_planid,tl_docname) select "+id+",");
		Date date=new Date();
		Employee employee = SystemSession.getUser();
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sql.append("to_date('"+sf.format(date)+"' ,'yyyy-mm-dd hh24:mi:ss'),");
		sql.append("'"+employee.getEm_name()+"',");
		sql.append("to_date('"+map.get("StartDate").toString()+"','yyyy-mm-dd hh24:mi:ss'),");
		sql.append("to_date('"+map.get("EndDate").toString()+"','yyyy-mm-dd hh24:mi:ss'),");
		sql.append("resourcename,resourceunits,'"+map.get("Name").toString()+"',id,prjplanid,PRJDOCNAME from projecttask where id="+map.get("Id").toString());
		return sql.toString();
	}

	private void createTask(Map<Object,Object> map ,Map<Object,Integer> TaskMap,String prjName,Employee employee){
		int id= baseDao.getSeqId("PROJECTTASK_SEQ");
		TaskMap.put(map.get("detno"),id);
		map.put("Id",id);
		map.put("handstatus", "未激活");
		map.put("leaf",0);
		map.put("handstatuscode","UNACTIVE");
		map.put("prjplanname", prjName);
		map.put("class", "projecttask");
		map.put("recorder", employee.getEm_name());
		map.put("recorderid", employee.getEm_id());
	}
	public void syncDependency(String create, String update, String remove,int prjId) {
		// TODO Auto-generated method stub
		String language = SystemSession.getLang();
		Employee employee = SystemSession.getUser();
		List<String> executes=new ArrayList<String>();
		String id=null;
		String remark=null;
		if(StringUtil.hasText(create)){
			List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(create);
			for(Map<Object,Object> map:lists){
				id=map.get("DE_TO").toString();
				remark="From :"+map.get("DE_FROM").toString()+" To :"+map.get("DE_TO").toString();
				executes.add(log("添加前置任务",id,remark));
			}
			executes.addAll(SqlUtil.getInsertSqlbyList(lists, "Dependency", "DE_ID"));
			
		}
		if(StringUtil.hasText(update)){
			List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(update);
			for(Map<Object,Object> map:lists){
				id=map.get("DE_FROM").toString();
				remark="From :"+map.get("DE_FROM").toString()+" To :"+map.get("DE_TO").toString();
				executes.add(log("更新依赖关系",id,remark));
			}
			executes.addAll(SqlUtil.getInsertSqlbyList(lists, "Dependency", "DE_ID"));
		}
		if(StringUtil.hasText(remove)){
			List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(remove);
			for(Map<Object,Object> map:lists){		
				executes.add(SqlUtil.getDeleteSql("Dependency", "DE_ID="+map.get("DE_ID")));
				id=map.get("DE_FROM").toString();
				remark="From :"+map.get("DE_FROM").toString()+" To :"+map.get("DE_TO").toString();
				executes.add(log("删除依赖关系",id,remark));
			}			
		}
		baseDao.execute(executes);
		baseDao.logMessage(new MessageLog(employee.getEm_name(), "修改依赖关系", Operation.UPDATE.getResult(language), "Gannt"));
	}
	
	
	private List<String> beforeChange(String create,String update,String remove){
		List<String> ids=new LinkedList<String>();
		List<Map<Object,Object>> c=null;
		List<Map<Object,Object>> u=null;
		List<Map<Object,Object>> r=null;
		if(create!=null){
			c=BaseUtil.parseGridStoreToMaps(create);
			ids.addAll(getTaskId(c));
		}
		if(update!=null){
			u=BaseUtil.parseGridStoreToMaps(update);
			ids.addAll(getTaskId(u));
		}
		if(remove!=null){
			r=BaseUtil.parseGridStoreToMaps(remove);
			ids.addAll(getTaskId(r));
		}
		return ids;	
	}
	
	private List<String> getTaskId(List<Map<Object,Object>> maps){
		List<String> id=new LinkedList<String>();
		for(Map<Object,Object> map:maps){
			if(map.get("TaskId")!=null)
				id.add(map.get("TaskId").toString());
		}
		return id;
	}
	
	
	public void syncAssigns(String create, String update, String remove,final Map<Object,Integer> TaskMap,final int prjId) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> beforeMaps =null;
		List<Map<String, Object>> afterMaps =null;
		List<String> ids = beforeChange( create, update, remove);
		String taskId = checkId(ids);
		if(taskId!=null)
			beforeMaps = baseDao.queryForList("select * from projecttask where id in("+taskId+") and PERCENTDONE>0 and PERCENTDONE<100");
		if(create!=null){
			final List<Map<Object,Object>> adds=BaseUtil.parseGridStoreToMaps(create);
			baseDao.getJdbcTemplate().batchUpdate(RESOURCEASSIGNMENT_SQL, new BatchPreparedStatementSetter() {
				
				@Override
				public int getBatchSize() {
					return adds.size();
				}

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					Map<Object,Object> o=adds.get(i);
					Object taskId=adds.get(i).get("TaskId"); 
					if(taskId == null)taskId=TaskMap.get(o.get("TaskDetno"));
					if(taskId==null) {
						BaseUtil.showError("请添加任务名称！");
					}
					ps.setObject(1, baseDao.getSeqId("RESOURCEASSIGNMENT_SEQ"));
					ps.setInt(2, 0);
					ps.setObject(3, taskId);
					ps.setInt(4, prjId);
					ps.setObject(5, o.get("EmCode"));
					ps.setObject(6, o.get("EmName"));
					ps.setInt(7, Integer.parseInt(o.get("ResourceId").toString()));
					ps.setObject(8,  Integer.parseInt(o.get("Units").toString()));
					ps.setString(9, "projecttask");   
					ps.setDate(10, new java.sql.Date(System.currentTimeMillis()));
				}
			});
		}
		if(update!=null){
			final List<Map<Object,Object>> updates=BaseUtil.parseGridStoreToMaps(update);
			for(Map<Object,Object> map:updates){
				baseDao.execute("UPDATE RESOURCEASSIGNMENT SET RA_UNITS=? WHERE RA_TASKID=? AND RA_EMID=?",new  Object[]{map.get("Units"),map.get("TaskId"),map.get("ResourceId")});				
			}						
		}
		if(remove!=null){
			List<Map<Object,Object>> dels=BaseUtil.parseGridStoreToMaps(remove);
			for(Map<Object,Object> m:dels){
				//部分或者全部完成任务不允许删除任务
				Object[] resource = baseDao.getFieldsDataByCondition("RESOURCEASSIGNMENT",new String[]{"nvl(ra_taskpercentdone,0)","ra_resourcename"},"ra_taskid="+m.get("TaskId")+" and ra_emid="+m.get("ResourceId"));
				if(!"0".equals(resource[0].toString())){
					BaseUtil.showError(resource[1]+" 已经完成了部分任务，不允许删除此资源!");
				}
				baseDao.execute("DELETE RESOURCEASSIGNMENT WHERE  RA_TASKID=? AND RA_EMID=? ",new Object[]{m.get("TaskId"),m.get("ResourceId")});
			}
		}	
		baseDao.execute("UPDATE RESOURCEASSIGNMENT SET (RA_PRJNAME,RA_TASKNAME,RA_STARTDATE,RA_ENDDATE,RA_NEEDATTACH,RA_STATUS,RA_STATUSCODE)=(SELECT PRJPLANNAME,NAME,STARTDATE,ENDDATE,ISNEEDATTACH,HANDSTATUS,HANDSTATUSCODE FROM PROJECTTASK WHERE ID=RA_TASKID) WHERE RA_PRJID=?",prjId);
		baseDao.execute("UPDATE PROJECTTASK SET (RESOURCEEMID,RESOURCECODE,RESOURCENAME,RESOURCEUNITS)=(SELECT  WMSYS.Wm_Concat(RA_EMID),WMSYS.Wm_concat(RA_RESOURCECODE),WMSYS.Wm_Concat(RA_RESOURCENAME),WMSYS.Wm_Concat(RA_UNITS)  FROM RESOURCEASSIGNMENT WHERE RA_TASKID=ID) WHERE PRJPLANID=? AND Exists (SELECT  1 FROM Resourceassignment  WHERE RA_TASKID=ID)",prjId);
		if(taskId!=null)
			afterMaps = baseDao.queryForList("select * from projecttask where id in("+taskId+") and  PERCENTDONE>0 and PERCENTDONE<100");
		if(afterMaps!=null&&beforeMaps!=null&&beforeMaps.size()>0&&afterMaps.size()>0){
			baseDao.execute(compareResource(beforeMaps,afterMaps));
		}	
	   if(taskId!=null)
		   baseDao.execute(log("资源更新",taskId,""));
	}
	
	private List<String> compareResource(List<Map<String,Object>> before,List<Map<String,Object>> after){
		List<String> sqls=new LinkedList<String>();
		for(Map<String,Object> bef:before){
			for(Map<String,Object> aft:after){
					if(bef.get("id").toString().equals(aft.get("id").toString())){
						if(!bef.get("RESOURCECODE").equals(aft.get("RESOURCECODE"))||!bef.get("RESOURCEUNITS").equals(aft.get("RESOURCEUNITS"))){
								sqls.addAll(analyseResource(aft,bef));
						}
						break;
					}						
			}  
		}
		return sqls;
	}
	//资源重新分配分析
	private List<String>  analyseResource(Map<String,Object> after,Map<String,Object> before){
		Map<String,Object>resourceMap=new HashMap<String,Object>();
		List<String> sqls=new LinkedList<String>();
		Object name = baseDao.getFieldDataByCondition("projecttask", "name", "id="+after.get("id"));
		Object taskId=after.get("id");
		String[] aCode=after.get("resourcecode").toString().split(",");
		String[] aUnit=after.get("resourceunits").toString().split(",");
		int len=aCode.length;
		Object taskDone = baseDao.getFieldDataByCondition("projecttask", "percentdone", "id="+taskId);
		double taskleftdone=100-Double.parseDouble(taskDone.toString());
		double canDone=0;
		
		for(int i=0;i<aCode.length;i++){
			//取最近的任务提交完成率
			SqlRowList set = baseDao.queryForRowSet("select ra_taskpercentdone from resourceassignment where ra_taskid="+taskId+" and ra_resourcecode='"+aCode[i]+"'");
			if(set.next()){
					double data= set.getDouble("ra_taskpercentdone");
					resourceMap.put(aCode[i],data);
					if(data-100==0){
						len--;
					}
					canDone=canDone+Double.parseDouble(aUnit[i])*(100-data);
			}
		}
		if(canDone-taskleftdone*100!=0){//剩余可以提交的完成率与总任务未完成率
			if(len==0)BaseUtil.showError("任务"+name+":分配资源不合理");
			double perDone=(canDone/100-taskleftdone)/len;//任务平均分配到各个资源
			for(int i=0;i<aCode.length;i++){
				if((Double)resourceMap.get(aCode[i])-100<0){
					double unit=Double.parseDouble(aUnit[i]);
					double aleardyDone=(Double)resourceMap.get(aCode[i]);
					if((100-aleardyDone)*unit/100<perDone||(perDone*100+aleardyDone*unit)<0){
						BaseUtil.showError("任务"+name+":分配资源不合理");
					}else{
						double percent=NumberUtil.formatDouble((perDone/Double.parseDouble(aUnit[i])*100),0);
						//插入workrecord数据
						sqls.add("insert into workrecord (wr_id,wr_percentdone,wr_taskpercentdone,wr_taskid,wr_prjid,wr_raid,wr_taskname,wr_prjname,wr_recorderemid,wr_recorddate,wr_redcord) "
								+ "select WORKRECORD_SEQ.nextval,"+percent+",ra_taskpercentdone,ra_taskId,ra_prjid,ra_id,ra_taskname,ra_prjname,ra_emid,sysdate,'系统自动弥补提交率' from resourceassignment where ra_taskid="+taskId+" and ra_resourcecode='"+aCode[i]+"'");
						//修改累计完成率
						sqls.add("update resourceassignment  set ra_taskpercentdone=(select ra_taskpercentdone+"+percent+" from resourceassignment where  ra_taskid="+taskId+" and ra_resourcecode='"+aCode[i]+"') where  ra_taskid="+taskId+" and ra_resourcecode='"+aCode[i]+"'");
					}
				}
			}						
		}
		return sqls;
	}
	
	private String checkId(List<String> taskId){
		if(taskId.size()>0){
		List<Integer> remove=new LinkedList<Integer>();
		StringBuffer ids =new StringBuffer();
		if(taskId.size()>0){
		for(int i=0;i<taskId.size();i++){
				Object status = baseDao.getFieldDataByCondition("projecttask","handstatuscode", "id="+taskId.get(i));
				if("UNACTIVE".equals(status.toString())){
					remove.add(i);
					continue;
			}
			for(int j=i+1;j<taskId.size();j++){
				if(taskId.get(i).equals(taskId.get(j))){
					remove.add(i);
					break;
				}
			}
		}
		}
		for(int j=0;j<taskId.size();j++){
			boolean flag=true;
				for(int i=0;i<remove.size();i++){
					if(j==(int)remove.get(i)){
						flag=false;
						break;
						}
					}
					if(flag)
						ids.append(taskId.get(j)+",");
			}	
			return ids.length()>0?ids.substring(0,ids.length()-1):null;		
		}else
			return null;	
	}
	
	private String log(String type,String id,String remark ){
		StringBuffer sql=new StringBuffer("insert into tasklog (tl_id,tl_date,tl_recordman,tl_type,tl_startdate,tl_enddate,tl_resource,tl_resoccupy,tl_name,tl_taskid,tl_planid,tl_docname,tl_remark) select tasklog_seq.nextval,");
		Date date=new Date();
		Employee employee = SystemSession.getUser();
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sql.append("to_date('"+sf.format(date)+"' ,'yyyy-mm-dd hh24:mi:ss'),");
		sql.append("'"+employee.getEm_name()+"','"+type+"',");
		sql.append("startdate,enddate,RESOURCENAME,resourceunits,name,id,prjplanid,PRJDOCNAME,");
		if(remark!=null&&!"".equals(remark))
			sql.append("'"+remark+"'");
		else
			sql.append("null");
		sql.append(" from projecttask where id in ("+id+") ");
		return sql.toString();
	}
	@Override
	public void activeTask(String data, int prjId) {
		// TODO Auto-generated method stub
		Map<Object,Object> task=BaseUtil.parseFormStoreToMap(data);
		//研发任务书未激活不允许激活
		boolean  maincheck=baseDao.checkByCondition("PROJECTMAINTASK", "NVL(PT_STATUSCODE,' ')<>'DOING' AND PT_PRJID="+prjId);
		if(!maincheck) BaseUtil.showError("当前项目产品开发任务未启动无法激活!"); 
		//若任务存在未完成前置任务不允许激活
		boolean  bool=baseDao.checkByCondition("DEPENDENCY LEFT JOIN PROJECTTASK ON DE_FROM=ID", "DE_TO="+task.get("Id") +" AND NVL(HANDSTATUSCODE,' ')<>'FINISHED'");
		if(!bool) BaseUtil.showError("当前任务存在未完成前置任务，无法手动激活！");
		bool=baseDao.checkByCondition("RESOURCEASSIGNMENT", "RA_TASKID="+task.get("Id"));
		if(bool) BaseUtil.showError("当前任务未分配资源无法激活!");
		baseDao.execute("UPDATE PROJECTTASK SET HANDSTATUS='已启动',handstatuscode='DOING',realstartdate=sysdate WHERE ID=?",task.get("Id"));
		baseDao.execute("UPDATE RESOURCEASSIGNMENT SET RA_STATUS='已启动',ra_statuscode='DOING' WHERE RA_TASKID=?",task.get("Id"));
		baseDao.execute(log("手动激活操作",task.get("Id").toString(),""));
		//消息模板配置
		Object ids= baseDao.getFieldDataByCondition("resourceassignment", "wm_concat(ra_id)","ra_taskid="+task.get("Id") );
		Object mmid=baseDao.getFieldDataByCondition("MESSAGEMODEL left join MESSAGEROLE on mm_id=mr_mmid", "distinct mm_id", "MR_ISUSED=-1 AND MM_ISUSED=-1 and mm_caller='Gantt'");
			//调用生成消息的存储过程
		if (mmid != null) {
			Employee user = SystemSession.getUser();
			baseDao.callProcedure("SP_CREATEINFO",new Object[] { mmid,user.getEm_code(), ids,DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) });
			}			
	}
	
	@Override
	public void endTask(int id, int prjId) {
		// TODO Auto-generated method stub
		List<String> sqls=new LinkedList<String>();
		Employee user = SystemSession.getUser();
		Date date=new Date();
		float time=0;
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String current = sf.format(date);
		Object start = baseDao.getFieldDataByCondition("projecttask", "to_char(realstartdate,'yyyy-mm-dd hh24:mi:ss')","id="+id);
		if(start!=null)
			time = taskUtilService.getTime(start.toString(), current);
		sqls.add("update projecttask set USEHOURS="+time+",handstatuscode='FINISHED',handstatus='"+BaseUtil.getLocalMessage("FINISHED")+"',realenddate=sysdate,percentdone=100 where id="+id);
		sqls.add("UPDATE RESOURCEASSIGNMENT SET ra_taskpercentdone=100,ra_statuscode='FINISHED',ra_status='"+BaseUtil.getLocalMessage("FINISHED")+"',ra_laststartdate=to_date('"+current+"','yyyy-mm-dd hh24:mi:ss'),ra_taskusehours='" +time
				+ "' WHERE ra_taskid='" + id + "'");
		baseDao.execute(sqls);
		taskUtilService.triggerTask(id);
		commitPhase(id);	
		baseDao.execute("insert into tasklog select tasklog_seq.nextval,sysdate,'"+user.getEm_name()+"','结束任务',STARTDATE,enddate,RESOURCECODE,RESOURCEUNITS,name,id,prjplanid,remark,PRJDOCNAME from projecttask where id="+id);
	}
	
	private void commitPhase(int taskId){
		try {
			Object phaseId=baseDao.getFieldDataByCondition("projecttask", "phaseid", "id="+taskId);
			if(phaseId!=null && Integer.parseInt(String.valueOf(phaseId))!=0){
				//判断前面阶段是否已完成
				int prjId=baseDao.getFieldValue("ProjectPhase", "pp_prjid", "pp_id="+phaseId, Integer.class);
				boolean bool=baseDao.checkByCondition("ProjectPhase"," nvl(pp_status,' ')<>'已完成' and  pp_detno < (select pp_detno from ProjectPhase  where pp_id="+phaseId+") and  pp_prjid="+prjId);
				if(bool){
					baseDao.updateByCondition("ProjectPhase", "pp_status='已完成',pp_realenddate=sysdate", "pp_id="+phaseId);				
					Object detno=baseDao.getFieldDataByCondition("ProjectPhase","min(pp_detno)", "pp_detno>(select pp_detno from ProjectPhase where pp_id="+phaseId+") and  pp_prjid="+prjId);
					baseDao.updateByCondition("ProjectPhase","pp_status='进行中',pp_realstartdate=sysdate", "pp_detno="+detno+" and pp_prjid="+prjId);
					baseDao.execute("update project set prj_phase =(select pp_phase from ProjectPhase where pp_detno="+detno+" and pp_prjid="+prjId+") where  prj_id="+prjId);
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
	}

	public boolean ImportMpp(int prjId, ProjectFile pf) {
		List<String> sqls = new ArrayList<String>();
		Employee em=SystemSession.getUser();
		Map<Integer,Map<String,Object>> resourceMap=new HashMap<Integer, Map<String,Object>>();
		boolean bool=baseDao.checkIf("Projecttask", " nvl(handstatuscode,' ')='DOING' and prjplanid="+prjId);
		if(bool)BaseUtil.showError("该项目已存在已启动的任务，不能重新导入！");
		sqls.add("delete projecttask  where prjplanid="+prjId + " and nvl(taskclass,' ')<>'pretask'");
		sqls.add("delete resourceassignment where ra_prjid="+prjId + " and ra_taskid in (select id from projecttask where prjplanid="+prjId+" and nvl(taskclass,' ')<>'pretask')");
		sqls.add("delete Dependency  where de_prjid="+prjId + " and nvl(de_from,0) in (select id from projecttask where prjplanid="+prjId+" and nvl(taskclass,' ')<>'pretask') and nvl(de_to,0) in (select id from projecttask where prjplanid="+prjId+" and nvl(taskclass,' ')<>'pretask')");
		sqls.add("update projectdoc set pd_taskname=null,pd_taskid=null where pd_prjid=" + prjId);
		sqls.add("delete from projecttaskattach where ptt_taskid in (select id from projecttask where prjplanid="+prjId+" and nvl(taskclass,' ')<>'pretask')");
		
		//查询预立项任务最大序号，主任务序号进行叠加
		int preTaskCount = 0;
		boolean preTaskCheck = baseDao.checkIf("projecttask", "prjplanid="+prjId+" and nvl(taskclass,' ')='pretask'");
		if(preTaskCheck){
			Object obj =  baseDao.getFieldDataByCondition("projecttask", "max(detno)","prjplanid="+prjId+" and nvl(taskclass,' ')='pretask'");
			preTaskCount = Integer.parseInt(obj.toString());
		}
		
		StringBuffer sb=new StringBuffer();
		// 所有任务信息
		List<Task> tasks = pf.getAllTasks();
		//任务信息
		List<TaskInfo> infos=new ArrayList<TaskInfo>();
		TaskInfo taskinfo=null;
		Map<Integer,Integer> subofMap=new HashMap<Integer,Integer>();
		String prjName=baseDao.getFieldValue("project", "prj_name", "prj_id="+prjId, String.class);
		//分析资源
		getResources(pf,resourceMap,prjId);
		for (Task task : tasks) {
			List<Relation> relations = task.getPredecessors();
			if (task.getID() != 0) {
				taskinfo=new TaskInfo();
				taskinfo.setTask_id(task.getID() + preTaskCount); //加上预立项数量
				if(task.getChildTasks().size()>0){
					for(Task t:task.getChildTasks()){
						subofMap.put(t.getID() + preTaskCount,task.getID() + preTaskCount); //加上预立项数量
					}
				}
				taskinfo.setTask_start_date(task.getStart());
				if(task.getFinish()!=null){
					taskinfo.setTask_end_date(DateUtil.parseStringToDate(DateUtil.parseDateToString(task.getFinish(),Constant.YMD)+" 23:59:59" ,Constant.YMD_HMS));
				}								
				taskinfo.setTask_name(task.getName());
				taskinfo.setTask_duration(task.getDuration().getDuration());
				taskinfo.setReal_task_id(baseDao.getSeqId("PROJECTTASK_SEQ"));
				String pretask = "";
				if (relations != null && !relations.isEmpty()) {
					for (Relation relation : relations) {
						if (relation.getType().toString().equals("FS")) {
							pretask += relation.getTargetTask().getID()+ ",";
						}
					}
				}			
				if (pretask.length() > 1) {
					pretask = pretask.substring(0, pretask.lastIndexOf(","));
				}
				if(!"".equals(pretask)) taskinfo.setTask_predecessors(pretask);
				setResouceAssignments(task,taskinfo,resourceMap,prjId,prjName,sqls);
				infos.add(taskinfo);
			}
		}
		Integer subofId=0;
		if(infos.size()>1){
			for(TaskInfo info:infos){
				subofId=subofMap.get(info.getTask_id());				
				sb.setLength(0);				
				sb.append("insert into projecttask (id,detno,parentid,name,pretaskdetno,duration,startdate,enddate,prjplanid,prjplanname,leaf,recorder,recorderid) values ("+info.getReal_task_id()+",");
				sb.append(info.getTask_id()+",");
				sb.append(subofId!=null?subofId:0);
				sb.append(",'"+info.getTask_name()+"',");
				sb.append((info.getTask_predecessors()!=null? "'"+getPreTaskDetno(info.getTask_predecessors(),preTaskCount)+"',":"null,"));
				sb.append(info.getTask_duration()+",");
				sb.append(info.getTask_start_date()!=null ?DateUtil.parseDateToOracleString(Constant.YMD, info.getTask_start_date())+",":"null," );
				sb.append(info.getTask_end_date()!=null ?DateUtil.parseDateToOracleString(Constant.YMD_HMS,info.getTask_end_date())+",":"null," );
				sb.append(prjId+",'"+prjName+"',0,");
				sb.append("'"+em.getEm_name()+"',"+em.getEm_id()+")");
				sqls.add(sb.toString());
			}			
		}
		baseDao.execute(sqls);
		sqls.clear();
		//处理前后置关系
		SqlRowList sl = baseDao.queryForRowSet("select id,pretaskdetno from projectTask where prjplanid=" + prjId+" and pretaskdetno is not null and nvl(taskclass,' ')<>'pretask'");		
		while(sl.next()){
			sqls.add("INSERT INTO Dependency(DE_ID,DE_FROM,DE_TO,DE_TYPE,DE_PRJID) SELECT Dependency_SEQ.NEXTVAL,ID,"+sl.getObject(1)+",2,"+prjId +" FROM PROJECTTASK  WHERE DETNO IN "
					+ "(SELECT  *  FROM TABLE (SELECT PARSESTRING(pretaskdetno,',') FROM PROJECTTASK A WHERE A.ID="+sl.getObject(1)+" )) AND PRJPLANID="+prjId + " and nvl(taskclass,' ')<>'pretask'");
		}
		baseDao.execute(sqls);		
		//刷新上下级关系
		baseDao.execute("update projecttask A set parentid=(select id from projecttask B where A.parentid=B.detno and B.prjplanid=?) where prjplanid=? and parentid!=0  and nvl(taskclass,' ')<>'pretask'",prjId,prjId);
		baseDao.execute("UPDATE PROJECTTASK SET (RESOURCEEMID,RESOURCECODE,RESOURCENAME,RESOURCEUNITS)=(SELECT  WMSYS.Wm_Concat(RA_EMID),WMSYS.Wm_concat(RA_RESOURCECODE),WMSYS.Wm_Concat(RA_RESOURCENAME),"
		+ "WMSYS.Wm_Concat(RA_UNITS)  FROM RESOURCEASSIGNMENT WHERE RA_TASKID=ID) WHERE PRJPLANID=? AND Exists (SELECT  1 FROM Resourceassignment  WHERE RA_TASKID=ID)  and nvl(taskclass,' ')<>'pretask'",prjId);
		//baseDao.execute("update projecttask set Baselinestartdate=startdate,Baselineenddate=enddate  where prjplanid=? and Baselinestartdate is null and startdate is not null and enddate  is not null",prjId);
		return true;
	}
	
	//由于预立项任务需要保留，所以序号要根据预立项任务的数量进行叠加
	private String getPreTaskDetno(String preTaskDetno,int preTaskCount){
		if(preTaskDetno==null||"".equals(preTaskDetno)){
			return null;
		}
		String[] detnos = preTaskDetno.split(",");
		StringBuffer sb = new StringBuffer();
		int det;
		for(String detno:detnos){
			det = Integer.parseInt(detno) + preTaskCount;
			sb.append("," + det);
		}
		return sb.substring(1);
	}
	
	//处理资源分配
	private void setResouceAssignments(Task task,TaskInfo taskInfo,Map<Integer,Map<String,Object>> resourceMap,int prjId,String prjName,List<String> sqls){
		List<ResourceAssignment> resourceAssignments=task.getResourceAssignments();
		if(resourceAssignments.size()>0){
			int units=0,unit=0;
			for( int i=1;i<resourceAssignments.size()+1;i++){
				ResourceAssignment assign=resourceAssignments.get(i-1);
				unit=assign.getUnits().intValue();
				units+=unit;				
				Map<String,Object> rs=resourceMap.get(assign.getResourceUniqueID());
				sqls.add("INSERT INTO  RESOURCEASSIGNMENT(RA_ID,RA_DETNO,RA_TASKID,RA_TASKNAME,RA_PRJID,RA_PRJNAME,RA_RESOURCECODE,RA_RESOURCENAME,RA_EMID,RA_UNITS,RA_TYPE,RA_STARTDATE,RA_ENDDATE,RA_STATUS,RA_STATUSCODE) "
						+ "VALUES(RESOURCEASSIGNMENT_SEQ.NEXTVAL,"+i+","+taskInfo.getReal_task_id()+",'"+taskInfo.getTask_name()+"',"+prjId+",'"+prjName+"','"+rs.get("resourcecode")+"','"+rs.get("resourcename")+"','"+rs.get("resourceemid")
						+"',"+unit+",'projecttask',"+DateUtil.parseDateToOracleString(Constant.YMD, taskInfo.getTask_start_date())+","+DateUtil.parseDateToOracleString(Constant.YMD, taskInfo.getTask_end_date())+",'"+BaseUtil.getLocalMessage("UNACTIVE")+"','UNACTIVE'"+")");			}
		   if(units!=100) BaseUtil.showError("任务【"+task.getName()+"】资源分配之和不等于100%!");	
		}	
	}
	//分析项目资源信息
	private void getResources(ProjectFile pf,Map<Integer,Map<String,Object>> resourceMap,int prjId){
		List<net.sf.mpxj.Resource> resources=pf.getAllResources();
		Object[] teaminfo=null; 
		for(net.sf.mpxj.Resource resource:resources){
			Map<String,Object> map=new HashMap<String, Object>();
			if(resource.getName()!=null){
				if(resource.getCode()==null){
					BaseUtil.showError("资源【"+resource.getName()+"】，未设置代码！");
				}
				teaminfo=baseDao.getFieldsDataByCondition("Teammember", new String[]{"tm_employeename","tm_employeeid"}, "tm_prjid="+prjId+" and tm_employeecode='"+resource.getCode()+"'");
				if(teaminfo==null){
					BaseUtil.showError("项目团队资源中未找到【"+resource.getName()+"】！");
				}     
				map.put("resourcecode",resource.getCode());
				map.put("resourcename",teaminfo[0]);
				map.put("resourceemid",teaminfo[1]);
				resourceMap.put(resource.getUniqueID(),map);
			}
			
		}
	}

	@Override
	public void setDoc(int prjId,int taskId, String docName, String docId) {
		// TODO Auto-generated method stub
		boolean flag=false;
		Date date=new Date();
		Employee employee = SystemSession.getUser();
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 List<String>sqls=new ArrayList<String>();
		 String TaskName=baseDao.getFieldValue("ProjectTask", "name", "Id="+taskId, String.class);
		if(StringUtil.hasText(docName)){
			String idStr="select wm_concat(ptt_taskid) from projecttaskattach  where ptt_taskid in (select id from projecttask where prjplanid="+prjId+") and ptt_prjdocid in ("+docId+")";
			String Id=baseDao.queryForObject(idStr, String.class);
			String code="select wm_concat(handstatuscode) from projecttask where id in ("+Id+")";
			String handstatuscode=baseDao.queryForObject(code, String.class);
			if(handstatuscode!=null){
				String[] s=handstatuscode.split(",");
				for(String str:s){
					if(!"FINISHED".equals(str)){
						flag=true;
						break;
					}
				}
			}
			if(flag){
			String checkSQL="select WMSYS.Wm_Concat(name) from projecttaskattach left join projecttask  on ptt_taskid=id  where ptt_prjdocid in ("+docId+") and ptt_taskid<>"+taskId;
		    String err=baseDao.queryForObject(checkSQL, String.class);
		    if(err!=null) BaseUtil.showError("该文件任务【"+err+"】已选择，不能重复选择！");
			}
			String[] namearr=docName.split(",");
			String[] idarr=docId.split(",");
			String status="";
			sqls.add("update projectdoc set pd_taskname=null,pd_taskid=null where pd_taskid = "+taskId);
			for(int i=0;i<namearr.length;i++){
				status+="0,";
			    sqls.add("update projectdoc set pd_taskname='"+TaskName+"',pd_taskid="+taskId+" where pd_id="+idarr[i]);	
			}
		    sqls.add("delete Projecttaskattach where ptt_taskid="+taskId);
		    sqls.add("insert into Projecttaskattach(ptt_id,ptt_prjdocid,ptt_filename,ptt_taskid) select Projecttaskattach_seq.nextval,pd_id,pd_name,"+taskId+"  from  projectdoc where pd_id in ("+docId+")");		    
		    sqls.add("update projecttask set prjdocname='"+docName+"',prjdocid='"+docId+"',prjdocstatus='"+status.substring(0,status.length()-1)+"' where id="+taskId);
		    sqls.add("insert into tasklog select tasklog_seq.nextval,to_date('"+sf.format(date)+"','yyyy-mm-dd hh24:mi:ss'),'"+employee.getEm_name()+"','更新文件',startdate,enddate,RESOURCECODE,RESOURCEUNITS,name,id,prjplanid,'','"+docName+"' from projecttask where id="+taskId);
		}else {
			sqls.add("delete Projecttaskattach where ptt_taskid="+taskId);
			sqls.add("update projecttask set prjdocname=null,prjdocid=null,prjdocstatus=null where id="+taskId);
			sqls.add("update projectdoc set pd_taskname=null,pd_taskid=null where nvl(pd_taskid,0)="+taskId);
			sqls.add("insert into tasklog select tasklog_seq.nextval,to_date('"+sf.format(date)+"','yyyy-mm-dd hh24:mi:ss'),'"+employee.getEm_name()+"','删除文件',startdate,enddate,RESOURCECODE,RESOURCEUNITS,name,id,prjplanid,'','' from projecttask where id="+taskId);
		}
	    baseDao.execute(sqls);
	}

	@Override
	public Map<String, Object> getLogByCondition(String id,
			String docname, int page, int start, int limit) {
		int num=0;
		List<Map<String, Object>> maps=new LinkedList<Map<String,Object>>();
		Map<String,Object> res=new HashMap<String,Object>();
		String tab=(docname!=null&&!"".equals(docname))?"select * from tasklog where tl_planid="+id+" and tl_type is not null and tl_name like'%"+docname+"%' order by tl_id DESC":"select * from tasklog where tl_planid="+id+" and tl_type is not null order by tl_id DESC";
		List<Map<String, Object>> list = baseDao.queryForList(tab);
		if((num=list.size())>0){
			StringBuffer sql=new StringBuffer("select * from (select tt.*,rownum rn from ("+tab+")tt where rownum<="+(page*limit)+") where rn>"+start+"");
			 maps= baseDao.queryForList(sql.toString());
			 res.put("logs", maps);		 
		}		
		res.put("num",num);
			return res;
	}

	@Override
	public List<Map<String, Object>> getProjectPhase(String prjId, String checked) {
		List<Map<String,Object>> phases=new LinkedList<Map<String,Object>>();
		Map<String,Object> phase=null;
		List<Object[]> ids=baseDao.getFieldsDatasByCondition("projecttask",new String[]{"phaseid","name"}, "PRJPLANID="+prjId+" and nvl(phaseid,0)<>0 and phasename is not null");
		SqlRowList rs = baseDao.queryForRowSet("select * from projectphase where pp_prjid="+prjId+" order by pp_detno ");
		while(rs.next()){
			phase=new HashMap<String,Object>();
			StringBuffer taskName=new StringBuffer();
			for(Object[] id:ids){
				if(rs.getInt("pp_id")==Integer.parseInt(id[0].toString())){
					taskName.append(id[1]+",");
				}
			}
			phase.put("pp_taskname", taskName.length()>0?taskName.substring(0, taskName.length()-1):"");
			phase.put("pp_phase", rs.getString("pp_phase"));
			phase.put("pp_id", rs.getInt("pp_id"));
			phase.put("pp_detno",rs.getInt("pp_detno"));
			phase.put("leaf", true);
			phase.put("checked", false);
			phase.put("id", rs.getInt("pp_id"));
			phases.add(phase);
		}
		return phases;
	}

	@Override
	public void linkPhase(String prjId, String phaseid,String phase ,String taskId,
			String pp_detno) {
		// TODO Auto-generated method stub
		List<String> sqls=new LinkedList<String>();
		Employee user = SystemSession.getUser();
		if(StringUtil.hasText(phaseid)){
			List<Object> before = baseDao.getFieldDatasByCondition("projecttask", "phaseid","prjplanid="+prjId+" and nvl(phaseid,0)<>0 and phasename is not null and detno<(select detno from projecttask where id="+taskId+")" );
			for(Object id:before){
				Object detno = baseDao.getFieldDataByCondition("projectphase", "pp_detno", "pp_id="+id);
				if(Integer.parseInt(pp_detno)<Integer.parseInt(detno.toString())){
					BaseUtil.showError("项目阶段与项目任务关联顺序混乱！");
				}
				if(Integer.parseInt(id.toString())==Integer.parseInt(phaseid))
					BaseUtil.showError("项目阶段:"+phase+" 关联重复!");
			}
			List<Object> after = baseDao.getFieldDatasByCondition("projecttask", "phaseid","prjplanid="+prjId+" and nvl(phaseid,0)<>0 and phasename is not null and detno>(select detno from projecttask where id="+taskId+")" );
			for(Object id:after){
				Object detno = baseDao.getFieldDataByCondition("projectphase", "pp_detno", "pp_id="+id);
				if(Integer.parseInt(pp_detno)>Integer.parseInt(detno.toString())){
					BaseUtil.showError("项目阶段与项目任务关联顺序混乱！");
				}
				if(Integer.parseInt(id.toString())==Integer.parseInt(phaseid))
					BaseUtil.showError("项目阶段:"+phase+" 关联重复!");
			}
			sqls.add("update projecttask set phaseid="+phaseid+",phasename='"+phase+"' where id="+taskId);
			sqls.add("insert into tasklog select tasklog_seq.nextval,sysdate,'"+user.getEm_name()+"','更新项目阶段:"+phase+"',STARTDATE,enddate,RESOURCECODE,RESOURCEUNITS,name,id,prjplanid,remark,PRJDOCNAME from projecttask where id="+taskId);
		}else{
			sqls.add("update projecttask set phaseid=null,phasename=null where id="+taskId);
			sqls.add("insert into tasklog select tasklog_seq.nextval,sysdate,'"+user.getEm_name()+"','删除项目阶段',STARTDATE,enddate,RESOURCECODE,RESOURCEUNITS,name,id,prjplanid,remark,PRJDOCNAME from projecttask where id="+taskId);
		}
		baseDao.execute(sqls);
	}

	

}

