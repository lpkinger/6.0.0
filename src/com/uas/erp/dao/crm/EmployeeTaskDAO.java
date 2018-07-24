package com.uas.erp.dao.crm;
import java.io.Serializable;
import java.sql.Date;
//import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;


@Service
public class EmployeeTaskDAO extends BaseDao  {
	
	private SqlRowList rs = null;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");     //private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
	private StringBuffer Sb=new StringBuffer();
	
	@SuppressWarnings("unchecked")
	public Map<String, Serializable> getEmployeeData(String startdate,String enddate) throws InvalidResultSetAccessException, SQLException, Exception, ParseException{
		@SuppressWarnings("rawtypes")
		ArrayList<HashMap> localArray= new ArrayList();
		String start=startdate.split("T")[0];
		String end =enddate.split("T")[0];
		SqlRowList dprs =null;
		SqlRowList emrs =null;
		dprs=queryForRowSet("select dp_id, dp_name from department ");
		emrs=queryForRowSet("select em_id,em_name,em_depart from employee where em_depart in(select dp_name from department)");
		String tasksql="select b.id bid,b.ra_resourcename,b.ra_emid,a.id aid,a.name,a.startdate,a.enddate,a.percentdone,a.prjplanname prjname" +
				" from mprojecttask a, mresourceassignment b where b.ra_taskid=a.id and (ra_emid is not null)" +
				"and(a.enddate is not null) and (a.startdate is not null) " +
				"and ((a.enddate > to_date('"+ start+ "','yyyy-mm-dd')  and a.enddate < to_date('" + end +"','yyyy-mm-dd')) " +
				"or(a.startdate > to_date('"+ start+ "','yyyy-mm-dd')  and a.startdate < to_date('" + end +"','yyyy-mm-dd')) " +
				"or(a.startdate < to_date('"+ start+ "','yyyy-mm-dd')  and a.enddate > to_date('" + end +"','yyyy-mm-dd'))) ";
		SqlRowList effortrs = queryForRowSet(tasksql);
		List<Map<String, Object>> effortlist = effortrs.getResultList();
		int efrows=effortrs.getResultList().size();
		int dprows=dprs.getResultList().size();
		int emrows=emrs.getResultList().size();
		if(dprows>0 && emrows>0){
			for(int i=0;i<dprows;i++){
				Map<String, Object>dpcrs=dprs.getResultList().get(i);
				HashMap<String, Object> ItemHash =new HashMap<String, Object>();
				ItemHash.put("Id", Integer.parseInt(dpcrs.get("dp_id").toString()));
				ItemHash.put("Name", dpcrs.get("dp_name"));
				ItemHash.put("loaded", true);
				ArrayList<HashMap<String, Object>> emarr=new ArrayList<HashMap<String, Object>>();
				emarr=(ArrayList<HashMap<String, Object>>) getChildren(effortlist,efrows,emrs,emrows,dpcrs.get("dp_name").toString(),start,end);
				if(emarr.toArray().length>0){
					ItemHash.put("expanded", true);
					ItemHash.put("children",emarr );
				}
				localArray.add(ItemHash);
			}
		}
		HashMap<String, Serializable> endHashMap=new HashMap<String, Serializable>();
		endHashMap.put("Id", 0);
		endHashMap.put("children", localArray);
		return  endHashMap;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList<?> getChildren( List<Map<String, Object>> efforts,int efrows,SqlRowList emrs, int emrows,String dpname,String start,String end) throws NumberFormatException, ParseException{
		ArrayList empArr=new  ArrayList();
		for(int i=0;i<emrows;i++){
			Map<String, Object>emcrs=emrs.getResultList().get(i);
			if(emcrs.get("em_depart").equals(dpname)){
				HashMap emHash=new HashMap();
				emHash.put("Id", emcrs.get("em_id"));
				emHash.put("Name", emcrs.get("em_name"));
				emHash.put("expanded", true);
				emHash.put("leaf",true);
				emHash.put("efforts", getEfforts(efforts,start,end, Integer.parseInt(emcrs.get("em_id").toString()),efrows));
				empArr.add(emHash);
			}
		}
		return empArr;
	}
	private String getEfforts(List<Map<String, Object>> efforts, String start, String end,int id,int efrows) throws ParseException {
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date flagStart=null;
		java.util.Date realStart=null;
		java.util.Date flagEnd=null;
		java.util.Date realEnd=null;
		int workdays=0;
	    flagStart = formatDate.parse(start);
		flagEnd = formatDate.parse(end);
		for (int i=0;i<efrows;i++){
			Map<String, Object> efcrs = efforts.get(i);
			if(Integer.parseInt(efcrs.get("ra_emid").toString())==id){
				//System.out.println("i: "+ i + "   id: " + id + "   emid" + efcrs.get("ra_emid").toString());
				java.util.Date efStart = formatDate.parse(efcrs.get("startdate").toString());
				java.util.Date efEnd = formatDate.parse(efcrs.get("enddate").toString());
				int sdf=flagStart.compareTo(efStart);
				int edf=flagEnd.compareTo(efEnd);
				if(!(sdf<0)){
					realStart=flagStart;
				}else{
					realStart=efStart;
				};
				if(!(edf>0)){
					realEnd= flagEnd;
				}else{
					realEnd=efEnd;
				}
				workdays = (int) ((realEnd.getTime()-realStart.getTime())/(1000*3600*24))+ workdays;
				
			}
		}
		if(workdays==0){
			return null;
		}
		return workdays+"";
	}
	@SuppressWarnings({"rawtypes", "unchecked" })
	public ArrayList getEmployeeTaskData(String startdate,String enddate) throws InvalidResultSetAccessException, SQLException{
		ArrayList<HashMap<String, Object>> emtask=new  ArrayList();
		String start=startdate.split("T")[0];
		String end =enddate.split("T")[0];
		String tasksql="select b.id bid,b.ra_resourcename,b.ra_emid,a.id aid,a.name,a.startdate,a.enddate,a.percentdone,a.prjplanname prjname" +
				" from mprojecttask a, mresourceassignment b where b.ra_taskid=a.id " +
				"and ((a.enddate > to_date('"+ start+ "','yyyy-mm-dd')  and a.enddate < to_date('" + end +"','yyyy-mm-dd')) " +
				"or(a.startdate > to_date('"+ start+ "','yyyy-mm-dd')  and a.startdate < to_date('" + end +"','yyyy-mm-dd')) " +
				"or(a.startdate < to_date('"+ start+ "','yyyy-mm-dd')  and a.enddate > to_date('" + end +"','yyyy-mm-dd'))) ";
		rs=queryForRowSet(tasksql);
		int rows=rs.getResultList().size();
		if(rows<1){
			return null;
		}else {
			for(int i=0;i<rows;i++){
				Map<String, Object>crs=rs.getResultList().get(i);
				HashMap<String, Object> emtaskHash=new HashMap<String, Object>();
				emtaskHash.put("StartDate", crs.get("startdate").toString());
				emtaskHash.put("EndDate", crs.get("enddate").toString());
				emtaskHash.put("ResourceId", crs.get("ra_emid"));
				emtaskHash.put("ra_taskname","<h style=\"color: Green\"><b>"+ crs.get("name") + "</b></h>  项目名称: "+ crs.get("prjname"));
				emtaskHash.put("ra_taskid", crs.get("aid"));
				emtaskHash.put("ra_taskpercentdone", crs.get("percentdone"));
				emtaskHash.put("ra_projectname", crs.get("prjplanname"));
				emtaskHash.put("id", crs.get("bid"));
				
				emtask.add(emtaskHash);
				
			}
		}
		return emtask;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList getTree(SqlRowList rs, int node,int rows,String field) throws SQLException {
		ArrayList<HashMap> localArray= new ArrayList();
		int currentId=0;
		for(int i=0;i<rows;i++){
			Map<String, Object>crs=rs.getResultList().get(i);
			if (Integer.parseInt(crs.get(field).toString())==node){
				HashMap ItemHash =new HashMap();
				currentId=Integer.parseInt(crs.get("ID").toString());
				ItemHash.put("id", Integer.parseInt(crs.get("ID").toString()));
				ItemHash.put("StartDate",crs.get("STARTDATE") == null ? null : crs.get("STARTDATE").toString());
				ItemHash.put("EndDate", crs.get("ENDDATE") == null ? null : crs.get("ENDDATE").toString());
				ItemHash.put("PercentDone", crs.get("PERCENTDONE") == null ? null : Long.parseLong(crs.get("PERCENTDONE").toString()));
				ItemHash.put("Name", crs.get("NAME").toString()==null?null:crs.get("NAME").toString());
				ItemHash.put("Priority",  crs.get("PRIORITY")==null?null:crs.get("PRIORITY").toString());//优先级
				ItemHash.put("Duration",  crs.get("DURATION")==null?null:crs.get("DURATION").toString());
				ItemHash.put("DurationUnit",  crs.get("DURATIONUNIT")==null?null:crs.get("DURATIONUNIT").toString());
				ItemHash.put("parentId",  crs.get("PARENTID")==null? "0":crs.get("PARENTID").toString());
				ItemHash.put("PhantomId",  crs.get("PHANTOMID")==null?null:crs.get("PHANTOMID").toString());
				ItemHash.put("PhantomParentId",  crs.get("PHANTOMPARENTID")==null?null:crs.get("PHANTOMPARENTID").toString());
				ItemHash.put("index",  crs.get("IND")==null?null:crs.get("IND").toString());
				ItemHash.put("expanded",  crs.get("EXPANDED")==null?null:crs.get("EXPANDED").toString());
				ItemHash.put("TaskColor",  crs.get("TaskColor")==null?null:crs.get("TaskColor").toString());
				ItemHash.put("Draggable",  crs.get("ID").toString().equals("19")?false:true);//
				ItemHash.put("Resizable", crs.get("ID").toString().equals("19")?false:true);//TaskColor
				
				if(checkChild(rs, Integer.parseInt(crs.get("ID").toString()),rows)){
					ItemHash.put("children", getTree(rs,currentId,rows,"PARENTID"));
				}else{
					ItemHash.put("leaf", true);
				}
				localArray.add(ItemHash);
			}
		}
		return localArray;
	}
	private Object dateFormate(Date date) {
		if(!(date==null)){
			return format.format(date);
		}
		return null;
	}
	private boolean checkChild(SqlRowList rs,int nodeId,int rows) throws SQLException{
		boolean checkchild=false;
		for(int i=0;i<rows;i++){
			Map<String, Object>crs=rs.getResultList().get(i);
			if(Integer.parseInt(crs.get("PARENTID").toString())==nodeId){
				checkchild=true;
				i=rows;
			}
		}
		return checkchild;
	}
	
	public String getDeleteId(String st,String condition) throws InvalidResultSetAccessException, DataAccessException, SQLException{
		int nodeid=0;
		
		if(st.toString().indexOf('[') > -1){
			   JSONArray jsonArray = JSONArray.fromObject(st);
			   for(Object ftemp:jsonArray){
					JSONObject fobj = JSONObject.fromObject(ftemp);
					if (fobj.containsKey("id")){
						if((fobj.getString("id")!=null)&&(!(fobj.getString("id").equals("0")))){
							nodeid=Integer.parseInt(fobj.getString("id"));
							deleteTreeData(condition,nodeid);
						}
					}
			   }
		}else{
			JSONObject fobj = JSONObject.fromObject(st);
			if (fobj.containsKey("id")){
				if((fobj.getString("id")!=null)&&(!(fobj.getString("id").equals("0")))){
					nodeid=Integer.parseInt(fobj.getString("id"));
					deleteTreeData(condition,nodeid);
				}
			}
		}
		return null;
	}
	private String deleteTreeData(String condition,int nodeId) throws InvalidResultSetAccessException, DataAccessException, SQLException{
		rs=queryForRowSet("select id,parentid  from mProjectTask where " + condition + "  order by id" );
		Sb.append(" where ( id=" +nodeId);
		if(rs.next()){
			execute("delete from mprojecttask  " + getChildren(rs,nodeId,rs.getResultList().size())+")");
			Sb.delete(0, Sb.length());
		}
		return null;
	}
	private String getChildren(SqlRowList rs, int node, int rows) throws NumberFormatException, SQLException {
		int currentId=0;
		for(int i=0;i<rows;i++){
			Map<String, Object>crs=rs.getResultList().get(i);
			currentId=Integer.parseInt(crs.get("ID").toString());
			if(Integer.parseInt(crs.get("PARENTID").toString())==node){
				Sb.append(" or id="+Integer.parseInt(crs.get("ID").toString()));
					getChildren(rs,currentId,rows);
			}
		}
		return Sb.toString();
	}
}


