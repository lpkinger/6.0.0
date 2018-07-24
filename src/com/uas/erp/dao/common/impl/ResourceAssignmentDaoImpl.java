package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ResourceAssignmentDao;

@Repository("resourceAssignmentDao")
public class ResourceAssignmentDaoImpl extends BaseDao implements ResourceAssignmentDao {
	@Override
	public JSONArray  getData(String caller, String condition) {
		String str="Select * from "+caller+" tab ";
		String sql= condition.equals("") ? str : str + " where " + condition;
		SqlRowList rs = queryForRowSet(sql);
		JSONArray arr=new JSONArray();
		String resourceunits=null;
		String resourceemid=null;
		String resourcename=null;
		String []emidArr=null;
		String []unitsArr=null;
		String []nameArr=null;
		int id=1;
		if(caller.equalsIgnoreCase("Teammember")){
			while(rs.next()){
				JSONObject jo=	new JSONObject();
				jo.put("Id", rs.getInt("tm_employeeid"));
				jo.put("Name", rs.getString("tm_employeename"));
				jo.put("EmCode", rs.getString("tm_employeecode"));
				jo.put("EmId", rs.getInt("tm_employeeid"));	
				jo.put("EmRole", rs.getString("tm_functional"));
				arr.add(jo);
			}
		}else{
			while(rs.next()){
				JSONObject jo=	new JSONObject();
				resourceemid=rs.getString("resourceemid");
				resourcename=rs.getString("resourcename");
				resourceunits=rs.getString("resourceunits");	
				if(resourcename!=null){
					if(resourcename!=null && resourcename.indexOf(",")>0 && resourceunits!=null && resourceunits.indexOf(",")>0){
						unitsArr=resourceunits.split(",");
						emidArr =resourceemid.split(",");
						nameArr =resourcename.split(",");
						for(int j=0;j<nameArr.length;j++){
							jo=new JSONObject();
							jo.put("Id", id);
							jo.put("TaskId", rs.getInt("id"));
							jo.put("ResourceId", Integer.parseInt(emidArr[j]));
							jo.put("ResourceName",nameArr[j]);
							jo.put("Units", unitsArr[j]);							
							id++;
							arr.add(jo);
						}
					}else {
						jo.put("Id", id);
						jo.put("TaskId", rs.getInt("id"));
						jo.put("ResourceId", Integer.parseInt(resourceemid));
						jo.put("ResourceName",resourcename);
						jo.put("Units", resourceunits);
						id++;
						arr.add(jo);
					}			
				}

			}	
		}
		return arr;
	}

	@Override
	public JSONObject getResourceData(String prjplanid) {
		String teamembersql="";
		if(prjplanid!=null&&prjplanid!=""){
			int id=Integer.parseInt(prjplanid);
			teamembersql="Select tm_id,tm_employeename from Teammember tab where tm_prjid='"+id+"'";		
		} else teamembersql="Select tm_id,tm_employeename from Teammember ";			
		SqlRowList rs1 = queryForRowSet(teamembersql);
		JSONArray arr1=new JSONArray();
		JSONObject obj=new JSONObject();
		List<Integer>a=new ArrayList<Integer>();
		while(rs1.next()){
			JSONObject jo=	new JSONObject();
			jo.put("Id", rs1.getInt("tm_id"));
			a.add( rs1.getInt("tm_id"));
			jo.put("Name",rs1.getString("tm_employeename"));
			arr1.add(jo);
		}
		//String resourcesql="Select * from ResourceAssignment tab where  ra_resourceid in"+a.toString().replace("[", "(").replace("]", ")");
		String resourcesql="Select * from ResourceAssignment tab where  ra_resourceid in"+a.toString().replace("[", "(").replace("]", ")");
		SqlRowList rs2 = queryForRowSet(resourcesql);
		JSONArray arr2=new JSONArray();
		while(rs2.next()){
			if(rs2.getInt("ra_units")!=0){
				JSONObject jo=	new JSONObject();
				jo.put("ResourceId", rs2.getInt("ra_resourceid"));
				jo.put("PercentAllocated",rs2.getInt("ra_units"));
				jo.put("Name",rs2.getString("ra_taskname"));
				jo.put("StartDate",rs2.getString("ra_startdate") );
				jo.put("EndDate", rs2.getString("ra_enddate"));
				arr2.add(jo);
			}
		}
		obj.put("resource", arr1);
		obj.put("event", arr2);
		return obj;
	}
	//task start here
	@Override
	public JSONArray getTaskResourceData(String caller, String condition) {
		String str="Select * from "+caller+" tab ";
		String sql= condition.equals("") ? str : str + " where " + condition;
		SqlRowList rs = queryForRowSet(sql);
		JSONArray arr=new JSONArray();
		while(rs.next()){
			JSONObject jo=	new JSONObject();
			jo.put("Id", rs.getInt("tm_id"));
			jo.put("Name", rs.getString("tm_employeename"));
			jo.put("EmCode", rs.getString("tm_employeecode"));
			jo.put("EmId", rs.getInt("tm_employeeid"));	

			arr.add(jo);
		}
		return arr;
	}
	@Override
	public JSONArray getTaskAssignmentData(String caller, String condition) {
		String str="Select * from "+caller+" tab ";
		String sql= condition.equals("") ? str : str + " where " + condition;
		SqlRowList rs = queryForRowSet(sql);
		JSONArray arr=new JSONArray();
		while(rs.next()){
			JSONObject jo=	new JSONObject();
			jo.put("Id", rs.getInt("ra_id"));
			jo.put("TaskId", rs.getInt("ra_taskid"));
			jo.put("ResourceId", rs.getInt("ra_emid"));
			jo.put("Units", rs.getInt("ra_units"));
			jo.put("Name",rs.getString("ra_taskname"));
			jo.put("StartDate",rs.getString("ra_startdate") );
			jo.put("EndDate", rs.getString("ra_enddate"));
			arr.add(jo);
		}	

		return arr;
	}

	//task end here

	@Override
	public void saveAssignment(String jsonData, String keyField,Object[]otherValues) {
		// TODO Auto-generated method stub
		List<String> sqls = new ArrayList<String>();
		if(jsonData.contains("},")){//明细行有多行数据哦
			String[] datas = jsonData.split("},");
			int count = 0;
			for(String data:datas){
				data = data.replace("{", "").replace("}", "");
				String[] strs = data.split(",");
				StringBuffer sb1 = new StringBuffer("INSERT into RESOURCEASSIGNMENT (");
				StringBuffer sb2 = new StringBuffer(" ");
				for(String str:strs){
					Object field = str.split(":")[0].replace("\"", "");
					Object value = str.split(":")[1].replace("\"", "");
					if(field.equals(keyField)){
						value = otherValues[count++];
					}
					sb1.append("ra_"+field.toString().toLowerCase());
					sb1.append(",");
					if(field.toString().endsWith("Date")){
						sb2.append("to_date('" + value.toString().substring(0,value.toString().length()-3) + "','YYYY-MM-DD')");
					}
					else sb2.append("'" + value + "'");
					sb2.append(",");
				}
				sqls.add(sb1.substring(0,sb1.length()-1) + ") VALUES (" + sb2.substring(0,sb2.length()-1) + ")");
			}
		} else {
			jsonData = jsonData.substring(jsonData.indexOf("{")+1, jsonData.lastIndexOf("}"));
			String[] strs = jsonData.split(",");
			StringBuffer sb1 = new StringBuffer("INSERT into RESOURCEASSIGNMENT (");
			StringBuffer sb2 = new StringBuffer(" ");
			for(String str:strs){
				Object field = str.split(":")[0].replace("\"", "");
				Object value = str.split(":")[1].replace("\"", "");
				if(field.equals(keyField)){
					value = otherValues[0];
				}
				sb1.append("ra_"+field.toString().toLowerCase());
				sb1.append(",");
				if(field.toString().endsWith("Date")){
					sb2.append("to_date('" + value.toString().substring(0,value.toString().length()-3) + "','YYYY-MM-DD')");
				}
				else sb2.append("'" + value + "'");
				sb2.append(",");
			}
			sqls.add(sb1.substring(0,sb1.length()-1) + ") VALUES (" + sb2.substring(0,sb2.length()-1) + ")");
		}
		execute(sqls);

	}

	@Override
	public void updateAssignment(String jsonData, String keyField) {
		// TODO Auto-generated method stub
		List<String> sqls = new ArrayList<String>();
		if(jsonData.contains("},")){//明细行有多行数据哦
			String[] datas = jsonData.split("},");
			for(String data:datas){
				data = data.replace("{", "").replace("}", "");
				String[] strs = data.split(",");
				StringBuffer sb1 = new StringBuffer("UPDATE RESOURCEASSIGNMENT SET ");
				Object keyValue = "";
				for(String str:strs){
					Object field = str.split(":")[0].replace("\"", "");
					Object value = str.split(":")[1].replace("\"", "");
					if(field.equals(keyField)){
						keyValue = value;
					}if(field.equals("Units")){
						sb1.append("ra_"+field.toString().toLowerCase());
						sb1.append("='" + value + "'");
						sb1.append(",");
					}
				}
				sqls.add(sb1.substring(0,sb1.length()-1) + " WHERE ra_id='" + keyValue + "'");
			}
		} else {
			jsonData = jsonData.substring(jsonData.indexOf("{")+1, jsonData.lastIndexOf("}"));
			String[] strs = jsonData.split(",");
			StringBuffer sb1 = new StringBuffer("UPDATE  RESOURCEASSIGNMENT SET ");
			Object keyValue = "";
			for(String str:strs){
				Object field = str.split(":")[0].replace("\"", "");
				Object value = str.split(":")[1].replace("\"", "");
				if(field.equals(keyField)){
					keyValue = value;
				}if(field.equals("Units")){
					sb1.append("ra_"+field.toString().toLowerCase());
					sb1.append("='" + value + "'");
					sb1.append(",");
				}

			}
			sqls.add(sb1.substring(0,sb1.length()-1) + " WHERE ra_id='" + keyValue + "'");
		}
		execute(sqls);

	}
	@Override
	public void deleteAssignment(String jsonData, String keyField) {
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
				sqls.add("delete from RESOURCEASSIGNMENT where ra_id='"+keyValue+"'");
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
			sqls.add("delete from RESOURCEASSIGNMENT where ra_id='"+keyValue+"'");	
		}
		execute(sqls);
	}

}
