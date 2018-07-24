package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.TaskDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.ProjectTask;
import com.uas.erp.model.Task;
import com.uas.erp.model.TaskTemplate;

@Repository("taskDao")
public class TaskDaoImpl extends BaseDao implements TaskDao  {

	@Override
	public List<ProjectTask> getTasks(String condition) {
		int id=Integer.parseInt(condition.split("=")[1]);
		List<ProjectTask> tasks= getJdbcTemplate().query("select *  from ProjectTask tab where prjplanid=? order by detno asc", 				
				new BeanPropertyRowMapper<ProjectTask>(ProjectTask.class),id);
		return tasks;
	}
	@Override
	public List<TaskTemplate> getTaskTemplateByParentId(int parentId,String condition) {
		condition=(condition=="")?"":condition+" AND ";
		try{
			List<TaskTemplate> tt = getJdbcTemplate().query(
					"select * from TaskTemplate where "+condition+" tt_parentid=? order by tt_id ", 
					new BeanPropertyRowMapper<TaskTemplate>(TaskTemplate.class),parentId);
			return tt;
		} catch(EmptyResultDataAccessException exception){
			return null;
		}
	}
	@Override
	public void saveProjectTask(String jsonGridData,String keyfield,Object[]othervalues){
		List<String> sqls = new ArrayList<String>();
		jsonGridData=jsonGridData.substring(0, jsonGridData.lastIndexOf("#@"));
		if(jsonGridData.contains("#@")){//明细行有多行数据哦
			String[] datas = jsonGridData.split("#@,");
			int count = 0;
			for(String data:datas){
				data = data.replace("{", "").replace("}", "");
				if(data.contains("[")){
					data=data.substring(0,data.indexOf("["))+"null"+data.substring(data.lastIndexOf("]")+1, data.length());
				}
				String[] strs = data.split(",");
				StringBuffer sb1 = new StringBuffer("INSERT into ProjectTask (");
				StringBuffer sb2 = new StringBuffer(" ");
				for(String str:strs){
					Object field = str.split(":")[0].replace("\"", "");
					Object value = str.split(":")[1].replace("\"", "");
					if(field.equals(keyfield)){
						value = othervalues[count++];
					}
					field=field.toString().toLowerCase();
					if(field.equals("index")){
						sb1.append("ind");
					}else{
						sb1.append(field);
					}
					sb1.append(",");
					if(field.toString().contains("date")&&(value.equals("null")||value==null)){
						sb2.append("to_date('1970-01-01','YYYY-MM-DD')");
					}else if(field.toString().endsWith("date")&&(value!=null)){
						if(value.toString().length()>11){
							sb2.append("to_date('" + value.toString().substring(0,value.toString().length()-3) + "','YYYY-MM-DD')");
						}else{
							sb2.append("to_date('" + value + "','YYYY-MM-DD')");
						}
					} else if(field.equals("parentid")&&(value.equals("null")||value==null)){
						sb2.append("'0'");
					}
					else {
						sb2.append("'" + value + "'");
					}
					sb2.append(",");
				}
				sqls.add(sb1.substring(0,sb1.length()-1) + ") VALUES (" + sb2.substring(0,sb2.length()-1) + ")");
			}
		} else {

			jsonGridData = jsonGridData.substring(jsonGridData.indexOf("{")+1, jsonGridData.lastIndexOf("}"));
			if(jsonGridData.contains("[")){
				jsonGridData=jsonGridData.substring(0,jsonGridData.indexOf("["))+"null"+jsonGridData.substring(jsonGridData.lastIndexOf("]")+1, jsonGridData.length());
			}
			String[] strs = jsonGridData.split(",");
			StringBuffer sb1 = new StringBuffer("INSERT into ProjectTask (");
			StringBuffer sb2 = new StringBuffer(" ");
			for(String str:strs){
				Object field = str.split(":")[0].replace("\"", "");
				Object value = str.split(":")[1].replace("\"", "");
				if(field.equals(keyfield)){//字段重复了哦
					value = othervalues[0];//优先选用传递过来的value
				}
				field=field.toString().toLowerCase();
				if(field.equals("index")){
					sb1.append("ind");
				}else{
					sb1.append(field);
				}
				sb1.append(",");
				if(field.toString().endsWith("date")&&(value.equals("null")||value==null||value.equals(""))){
					sb2.append("to_date('1970-01-01','YYYY-MM-DD')");
				}else if(field.toString().endsWith("date")&&(value!=null)){
					if(value.toString().length()>11){
						sb2.append("to_date('" + value.toString().substring(0,value.toString().length()-3) + "','YYYY-MM-DD')");
					}else{
						sb2.append("to_date('" + value + "','YYYY-MM-DD')");
					}
				}  else if(field.equals("parentid")&&(value.equals("null")||value==null)){
					sb2.append("'0'");
				}
				else {
					sb2.append("'" + value + "'");
				}
				sb2.append(",");
			}

			sqls.add(sb1.substring(0,sb1.length()-1) + ") VALUES (" + sb2.substring(0,sb2.length()-1) + ")");
		}
		execute(sqls);
	}
	@Override
	public void updateProjectTask(String jsonData,String keyfield) {
		jsonData=jsonData.substring(0, jsonData.lastIndexOf("#@"));
		List<String> sqls = new ArrayList<String>();
		if(jsonData.contains("#@")){//明细行有多行数据哦
			String[] datas = jsonData.split("#@,");
			for(String data:datas){
				data = data.replace("{", "").replace("}", "");
				if(data.contains("[")){
					data=data.substring(0,data.indexOf("["))+"null"+data.substring(data.lastIndexOf("]")+1, data.length());
				}
				String[] strs = data.split(",");
				StringBuffer sb1 = new StringBuffer("UPDATE ProjectTask SET ");
				Object keyValue = "";
				for(String str:strs){
					Object field = str.split(":")[0].replace("\"", "");
					Object value = str.split(":")[1].replace("\"", "");
					if(field.equals(keyfield)){
						keyValue = value;
					} 
					if(!field.equals("id"))
					{
						if(field.equals("index")){
							sb1.append("ind");
						}else{
							field=field.toString().toLowerCase();
							sb1.append(field);
						}										
						if(field.toString().endsWith("date")&&(value.equals("")||value==null)){//判断是否是形如yyyy-mm-dd格式的日期类型数据
							sb1.append("=to_date('1970-01-01','YYYY-MM-DD')");
						}else if(field.toString().endsWith("date")&&(value!=null)){
							if(value.toString().length()>11){
								sb1.append("=to_date('" + value.toString().substring(0,value.toString().length()-3) + "','YYYY-MM-DD')");
							}else{
								sb1.append("=to_date('" + value + "','YYYY-MM-DD')");
							}
						}else if(field.equals("parentid")&&(value.equals("null")||value==null)){
							sb1.append("='0'");
						}   else if(field.equals("children")){
							sb1.append("='null'");
						}
						else {
							sb1.append("='" + value + "'");
						}
						sb1.append(",");
					}
				}
				sqls.add(sb1.substring(0,sb1.length()-1) + " WHERE " + keyfield.toLowerCase() + "='" + keyValue + "'");
			}
		} else {
			jsonData = jsonData.substring(jsonData.indexOf("{")+1, jsonData.lastIndexOf("}"));
			if(jsonData.contains("[")){
				jsonData=jsonData.substring(0,jsonData.indexOf("["))+"null"+jsonData.substring(jsonData.lastIndexOf("]")+1, jsonData.length());
			}
			String[] strs = jsonData.split(",");
			StringBuffer sb1 = new StringBuffer("UPDATE ProjectTask SET ");
			Object keyValue = "";
			for(String str:strs){
				Object field = str.split(":")[0].replace("\"", "");
				Object value = str.split(":")[1].replace("\"", "");
				if(field.equals(keyfield)){
					keyValue = value;
				}
				if(!field.equals("id"))
				{
					if(field.equals("index")){
						sb1.append("ind");
					}  else{
						field=field.toString().toLowerCase();
						sb1.append(field);
					}	
					if(field.toString().endsWith("date")&&(value.equals("")||value==null)){//判断是否是形如yyyy-mm-dd格式的日期类型数据
						sb1.append("=to_date('1970-01-01','YYYY-MM-DD')");
					}else if(field.toString().endsWith("date")&&(value!=null)){
						if(value.toString().length()>11){
							sb1.append("=to_date('" + value.toString().substring(0,value.toString().length()-3) + "','YYYY-MM-DD')");
						}else{
							sb1.append("=to_date('" + value + "','YYYY-MM-DD')");
						}
					}else if(field.equals("parentid")&&(value.equals("null")||value==null)){
						sb1.append("='0'");
					}  else if(field.equals("children")){
						sb1.append("='null'");
					}
					else {
						sb1.append("='" + value + "'");
					}
					sb1.append(",");
				}

			}
			sqls.add(sb1.substring(0,sb1.length()-1) + " WHERE " + keyfield.toLowerCase() + "='" + keyValue + "'");
		}
		execute(sqls);


	}
	@Override
	public void deleteProjectTask(String jsonData, String keyfield) {
		jsonData=jsonData.substring(0, jsonData.lastIndexOf("#@"));
		List<String> sqls = new ArrayList<String>();
		if(jsonData.contains("#@")){//明细行有多行数据哦
			String[] datas = jsonData.split("#@,");
			String keyValue="";
			for(String data:datas){
				data = data.replace("{", "").replace("}", "");
				if(data.contains("[")){
					data=data.substring(0,data.indexOf("["))+"null"+data.substring(data.lastIndexOf("]")+1, data.length());
				}
				String[] strs = data.split(",");
				for(String str:strs){
					Object field = str.split(":")[0].replace("\"", "");
					Object value = str.split(":")[1].replace("\"", "");
					if(field.equals(keyfield)){//字段重复了哦
						keyValue=value.toString();
					}		
				}
				sqls.add("delete from ProjectTask where id='"+keyValue+"'");
			}
		}else {
			jsonData = jsonData.substring(jsonData.indexOf("{")+1, jsonData.lastIndexOf("}"));
			if(jsonData.contains("[")){
				jsonData=jsonData.substring(0,jsonData.indexOf("["))+"null"+jsonData.substring(jsonData.lastIndexOf("]")+1, jsonData.length());
			}
			String[] strs = jsonData.split(",");
			String keyValue="";
			for(String str:strs){
				Object field = str.split(":")[0].replace("\"", "");
				Object value = str.split(":")[1].replace("\"", "");
				if(field.equals(keyfield)){
					keyValue = value.toString();
				}

			}
			sqls.add("delete from ProjectTask where id='"+keyValue+"'");

		}
		execute(sqls);
	}
	@Override
	public List<Map<String, Object>> getDependencies(String prjid) {
		int id=Integer.parseInt(prjid);
		String sql="SELECT * FROM DEPENDENCY  WHERE DE_PRJID='"+id+"'";
		return  getJdbcTemplate().queryForList(sql);
		
	}
	@Override
	public void saveDependency(String jsonData,String keyField,Object[]OtherValues,String prjid) {
		List<String> sqls = new ArrayList<String>();
		if(jsonData.contains("},")){//明细行有多行数据哦
			String[] datas = jsonData.split("},");
			int count = 0;
			for(String data:datas){
				data = data.replace("{", "").replace("}", "");
				String[] strs = data.split(",");
				StringBuffer sb1 = new StringBuffer("INSERT into DEPENDENCY (");
				StringBuffer sb2 = new StringBuffer(" ");
				for(String str:strs){
					Object field = str.split(":")[0].replace("\"", "");
					Object value = str.split(":")[1].replace("\"", "");
					if(field.equals(keyField)){
						value = OtherValues[count++];
					}
					sb1.append("de_"+field.toString().toLowerCase());
					sb1.append(",");
					sb2.append("'" + value + "'");
					sb2.append(",");
				}
				sb1.append("de_prjid");
				sb2.append("'" + prjid + "'");
				sb2.append(",");
				sqls.add(sb1.substring(0,sb1.length()) + ") VALUES (" + sb2.substring(0,sb2.length()-1) + ")");
			}
		} else {
			jsonData = jsonData.substring(jsonData.indexOf("{")+1, jsonData.lastIndexOf("}"));
			String[] strs = jsonData.split(",");
			StringBuffer sb1 = new StringBuffer("INSERT into DEPENDENCY (");
			StringBuffer sb2 = new StringBuffer(" ");
			for(String str:strs){
				Object field = str.split(":")[0].replace("\"", "");
				Object value = str.split(":")[1].replace("\"", "");
				if(field.equals(keyField)){
					value = OtherValues[0];
				}
				sb1.append("de_"+field.toString().toLowerCase());
				sb1.append(",");
				sb2.append("'" + value + "'");
				sb2.append(",");
			}
			sb1.append("de_prjid");
			sb2.append("'" + prjid + "'");
			sb2.append(",");
			sqls.add(sb1.substring(0,sb1.length()) + ") VALUES (" + sb2.substring(0,sb2.length()-1) + ")");
		}
		execute(sqls);
	}
	@Override
	public void updateDependency(String jsonData, String keyField) {
		// TODO Auto-generated method stub
		List<String> sqls = new ArrayList<String>();
		if(jsonData.contains("},")){//明细行有多行数据哦
			String[] datas = jsonData.split("},");
			for(String data:datas){
				data = data.replace("{", "").replace("}", "");
				String[] strs = data.split(",");
				StringBuffer sb1 = new StringBuffer("UPDATE DEPENDENCY SET ");
				Object keyValue = "";
				for(String str:strs){
					Object field = str.split(":")[0].replace("\"", "");
					Object value = str.split(":")[1].replace("\"", "");
					if(field.equals(keyField)){
						keyValue = value;
					}
					sb1.append("de_"+field.toString().toLowerCase());
					sb1.append("='" + value + "'");
					sb1.append(",");
				}
				sqls.add(sb1.substring(0,sb1.length()-1) + " WHERE " + keyField + "='" + keyValue + "'");
			}
		} else {
			jsonData = jsonData.substring(jsonData.indexOf("{")+1, jsonData.lastIndexOf("}"));
			String[] strs = jsonData.split(",");
			StringBuffer sb1 = new StringBuffer("UPDATE DEPENDENCY SET ");
			Object keyValue = "";
			for(String str:strs){
				Object field = str.split(":")[0].replace("\"", "");
				Object value = str.split(":")[1].replace("\"", "");
				if(field.equals(keyField)){
					keyValue = value;
				}
				sb1.append("de_"+field.toString().toLowerCase());
				sb1.append("='" + value + "'");
				sb1.append(",");
			}
			sqls.add(sb1.substring(0,sb1.length()-1) + " WHERE " + keyField + "='" + keyValue + "'");
		}
		execute(sqls);
	}
	@Override
	public void deleteDependency(String jsonData, String keyField) {
		// TODO Auto-generated method stub
		List<String> sqls = new ArrayList<String>();
		if(jsonData.contains("},")){
			String[] datas = jsonData.split(",");
			String keyValue="";
			for(String data:datas){
				data = data.replace("{", "").replace("}", "");
				String[] strs = data.split(",");
				for(String str:strs){
					Object field = str.split(":")[0].replace("\"", "");
					Object value = str.split(":")[1].replace("\"", "");
					if(field.equals(keyField)){
						keyValue=value.toString();
					}		
				}
				sqls.add("delete from DEPENDENCY where de_id='"+keyValue+"'");
			}
		}else {
			jsonData = jsonData.substring(jsonData.indexOf("{")+1, jsonData.lastIndexOf("}"));
			String[] strs = jsonData.split(",");
			String keyValue="";
			for(String str:strs){
				Object field = str.split(":")[0].replace("\"", "");
				Object value = str.split(":")[1].replace("\"", "");
				if(field.equals(keyField)){
					keyValue = value.toString();
				}

			}
			sqls.add("delete from DEPENDENCY where de_id='"+keyValue+"'");	
		}
		execute(sqls);
	}
	@Override
	public void saveCheckList(String formStore) {
		// TODO Auto-generated method stub
		//生成checklist
		int cl_id=getSeqId("CHECKLIST_SEQ");
		Map<Object, Object> store=BaseUtil.parseFormStoreToMap(formStore);
		StringBuffer sb=new StringBuffer();
		String fields=getformFieldsbyTable("CheckList");
		sb.append("INSERT INTO CHECKLIST (");
		sb.append(fields);
		sb.append(") values(");
		sb.append("'"+sGetMaxNumber("CheckList",1)+"',");
		sb.append("'"+store.get("id")+"',");
		sb.append("'"+store.get("name")+"',");
		sb.append("'ENTERING',");
		sb.append("'"+store.get("recorder")+"',");
		sb.append("to_date('" + store.get("recorddate") + "','YYYY-MM-DD'),");
		sb.append("'"+cl_id+"',");
		sb.append("'"+store.get("prjplanname")+"',");
		sb.append("'"+store.get("prjplanid")+"',");		
		sb.append("'"+store.get("prjtestmancode")+"')");
		String insertsql=sb.toString();
		execute(insertsql);
	}
	@Override
	public Task getTaskByCode(String code) {
		try{
			return getJdbcTemplate().queryForObject("select * from PROJECTTASK where taskcode like '" + code + "%'",
					new BeanPropertyRowMapper<Task>(Task.class));			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	@Override
	public List<ProjectTask> getAllParentTasks(int id,String condition) {
		// TODO Auto-generated method stub
		List<ProjectTask> tasks=new ArrayList<ProjectTask>();
		getParentTasks(id, condition, tasks);
		return tasks;
	}
	private void getParentTasks(int id ,String condition,List<ProjectTask> tasks){
		Object parentId=getFieldDataByCondition("ProjectTask","parentId","id="+id);
		if(parentId!=null&&!parentId.equals("0")){
			ProjectTask task=getJdbcTemplate().queryForObject("select *  from ProjectTask where id=? and ?", 
					new BeanPropertyRowMapper<ProjectTask>(ProjectTask.class),parentId,condition);
			if(task!=null) {
				tasks.add(task);
				getParentTasks(task.getId(),condition,tasks);
			}
		}
	}
	@Override
	public Map<String,Object> saveAgenda(String formStore, Employee employee, String language) {
		Map<Object,Object> store=BaseUtil.parseFormStoreToMap(formStore);
		List<String>sqls=new ArrayList<String>();
		store.put("taskcode", sGetMaxNumber("ProjectTask", 2));
		int id =getSeqId("PROJECTTASK_SEQ");
		store.put("id", id);
		store.put("recorder", employee.getEm_name());
		store.put("status", "已审核");
		store.put("class", "agendatask");
		store.put("statuscode", "AUDITED");
		store.put("handstatus", "已启动");
		store.put("handstatuscode", "DOING");

		String resourcecode = String.valueOf(store.get("resourcecode"));
		SqlRowList rs = queryForRowSet("select em_id,em_code,em_name from employee where em_code in ("
				+ toSqlString(resourcecode) + ")");
		List<String> codes = new ArrayList<String>();
		List<Integer> ids = new ArrayList<Integer>();
		String sourcelink=String.valueOf(store.get("sourcelink")),sourcecode="";
		sourcelink=sourcelink.replaceAll("='", "IS").replaceAll("'","");
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("id", id);
		map.put("prjplanid", store.get("prjplanid"));
		/*jo.put("prjplanid", rs.getObject("prjplanid"));
		jo.put("tasktype", rs.getObject("class"));
		jo.put("manuallyscheduled", rs.getObject("manuallyscheduled"));*/
		if("ExpandPlan".equals(store.get("caller"))){//市场推广立项
			sourcelink="jsps/crm/customermgr/customervisit/expandPlan.jsp?formCondition=ep_idIS"+store.get("prjplanid")+"&gridCondition=epd_epidIS"+store.get("prjplanid");
			Object []datas=getFieldsDataByCondition("ExpandPlan", new String[]{"ep_code","ep_prname","ep_prbrand","ep_prmodel","ep_principal","ep_belong"}, "ep_id="+store.get("prjplanid"));
			sourcecode=String.valueOf(datas[0]);
			StringBuffer sb=new StringBuffer();
			sb.append("项目名称: "+datas[1]+"<br/>");
			sb.append("推广品牌: "+datas[2]+"<br/>");
			sb.append("产品型号: "+datas[3]+"<br/>");
			sb.append("项目负责人: "+datas[4]+"<br/>");
			sb.append("行业归属: "+datas[5]+"<br/>");
			store.put("remindmsg", sb.toString());
		    store.put("sourcecode", sourcecode);
		    store.put("statuscode", "ENTERING");
		    store.put("status", "在录入");
		    map.put("tasktype", "agendatask");
		    map.put("manuallyscheduled", "");
		  
		}
		if("ExpandPlan!DY".equals(store.get("caller"))){//市场调研立项
			sourcelink="jsps/crm/customermgr/customervisit/expandPlan.jsp?whoami=ExpandPlan!DY&formCondition=ep_idIS"+store.get("prjplanid")+"&gridCondition=epd_epidIS"+store.get("prjplanid");
			Object []datas=getFieldsDataByCondition("ExpandPlan", new String[]{"ep_code","ep_prname","ep_tpcode","ep_tpname","ep_starttime","ep_endtime"}, "ep_id="+store.get("prjplanid"));
			sourcecode=String.valueOf(datas[0]);
			StringBuffer sb=new StringBuffer();
			sb.append("项目名称: "+datas[1]+"<br/>");
			sb.append("模板编号: "+datas[2]+"<br/>");
			sb.append("模板名称: "+datas[3]+"<br/>");
			sb.append("开始时间: "+datas[4]+"<br/>");
			sb.append("结束时间: "+datas[5]+"<br/>");
			store.put("remindmsg", sb.toString());
		    store.put("sourcecode", sourcecode);
		    store.put("statuscode", "ENTERING");
		    store.put("status", "在录入");
		    store.put("class", "researchtask");//调研任务,新类型
		    store.put("manuallyscheduled", datas[2]);//模板类型caller
		    map.put("tasktype", "researchtask");
		    map.put("manuallyscheduled", datas[2]);
		}
		store.put("sourcelink", sourcelink);
		store.remove("caller");
		int detno = 1;
		StringBuffer sb = new StringBuffer();
		sb.append("任务提醒&nbsp;&nbsp;&nbsp;&nbsp;[");
		sb.append(DateUtil.parseDateToString(null, "MM-dd HH:mm"));
		sb.append("]</br>#<a style=\"color:blue\" href=\"javascript:openUrl(''jsps/plm/task/task.jsp?formCondition=idIS");
		sb.append(id);
		sb.append("&gridCondition=ra_taskidIS");
		sb.append(id);
		sb.append("'')\">");
		sb.append(store.get("name"));
		sb.append("#&nbsp;&nbsp;&nbsp;&nbsp;<a style=\"color:red\" href=\"javascript:openUrl(''");		
		sb.append(sourcelink);
		sb.append("'')\">");
		sb.append(store.get("sourcecode"));
		sb.append("</a>");
		while (rs.next()) {
			codes.add(rs.getString("em_code"));
			ids.add(rs.getInt("em_id"));
			sqls.add("insert into resourceassignment(ra_id,ra_taskid,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,ra_status,ra_statuscode,ra_units,ra_type) values (resourceassignment_seq.nextval,'"
					+ id
					+ "','"
					+ rs.getInt("em_id")
					+ "','"
					+ rs.getString("em_code")
					+ "','"
					+ rs.getString("em_name") + "'," + detno++ + ",'进行中','START',100,'billtask')");		
		}
		store.put("resourcecode", BaseUtil.parseList2Str(codes, ",", false));
		store.put("resourceemid", BaseUtil.parseList2Str(ids, ",", false));
		sqls.add(SqlUtil.getInsertSqlByMap(store, "ProjectTask"));
		sqls.add("update projecttask set recorddate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+" where id=" + id);
		sqls.add("update resourceassignment set (ra_taskname,ra_startdate,ra_enddate)=(select name,startdate,enddate from ProjectTask where id=ra_taskid) where ra_taskid="
				+ id);

		execute(sqls);
		map.put("success",true);
		return map;
	}
	@Override
	public void updateAgenda(String updateData, Employee employee, String language) {
		// TODO Auto-generated method stub
		List<Map<Object,Object>> store=BaseUtil.parseGridStoreToMaps(updateData);
	}
	@Override
	public void deleteAgenda(String deleteData, Employee employee, String language) {
		// TODO Auto-generated method stub
		List<Map<Object,Object>> store=BaseUtil.parseGridStoreToMaps(deleteData);
	}
	private String toSqlString(String str) {
		if (str != null) {
			String[] strs = str.split(",");
			StringBuffer sb = new StringBuffer();
			for (String k : strs) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append("'").append(k).append("'");
			}
			return sb.toString();
		}
		return null;
	}
}