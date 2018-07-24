package com.uas.erp.dao.crm;
import java.sql.Date;
//import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;


@Service
public class TreeDAO extends BaseDao  {
	
	private SqlRowList rs = null;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");     //private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
	private StringBuffer Sb=new StringBuffer();
	
	@SuppressWarnings({"rawtypes" })
	public ArrayList getTreeData(String condition,int rootid) throws InvalidResultSetAccessException, SQLException{
		rs=queryForRowSet("select *  from mProjectTask where "+ condition+ "  order by ind" );
		int rows=rs.getResultList().size();
		
		if(rows<1){
			return null;
		}
		if(rows>0){
			if(rootid==0){
				return getTree(rs,rootid,rows,"PARENTID");
			}else{
				return getTree(rs,rootid,rows,"ID");
			}
		}
		return null;
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
						if(!fobj.getString("id").equals("null")){
							nodeid=Integer.parseInt(fobj.getString("id"));
							deleteTreeData(condition,nodeid);
						}
					}
			   }
		}else{
			JSONObject fobj = JSONObject.fromObject(st);
			if (fobj.containsKey("id")){
				if(!fobj.getString("id").equals("null")){
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