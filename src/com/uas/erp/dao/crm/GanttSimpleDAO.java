package com.uas.erp.dao.crm;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class GanttSimpleDAO extends BaseDao {
	private SqlRowList rs = null;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
	private boolean checkRoot=false;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map dataList(String st,String table) throws SQLException {
		String condition=" where ";
		if(st!=null){
			JSONArray jsonArray = JSONArray.fromObject(st);
			for(Object ftemp:jsonArray){
				JSONObject fobj = JSONObject.fromObject(ftemp);
				String field=fobj.getString("property");
				String fieldVal=fobj.getString("value");
				condition=condition  + field + " = '"+ fieldVal + "' ";
			}
		}else {
			condition=" where 1=1";
		}
		
		Map success = new HashMap();
		ArrayList datalist = new ArrayList();
		ArrayList datalisttemp = new ArrayList();
			String query= "select * from "+ table +" " + condition;
			rs=queryForRowSet(query);
			int rows=rs.getResultList().size();
			if (rows<0){
				return success;
			}
			Map<String, Object> b = rs.getResultList().get(0);
			 Object[] keyName = b.keySet().toArray();
			for (int j =0 ; j < rows; j++) {
				Map<String, Object>crs=rs.getResultList().get(j);
				HashMap productRow = new HashMap();
				for (int i = 0; i < keyName.length; i++) {
					String keyname=keyName[i].toString();
					productRow.put(keyname.toLowerCase(), crs.get(keyname)==null?" ":crs.get(keyname).toString());
				}
				datalisttemp.add(productRow);
			}
			success.put("data", datalisttemp);
			success.put("success", "true");
			
		return success;//datalist;
	}
	public String dataDelete(String st ,String table,int rootid) throws SQLException{
	   boolean isNew =false;
	   if(st.toString().indexOf('[')>-1){
		   JSONArray jsonArray = JSONArray.fromObject(st);
		   for(Object ftemp:jsonArray){
				JSONObject fobj = JSONObject.fromObject(ftemp);
				if (fobj.containsKey("id")){
					isNew=checkData(fobj.getString("id"),table,rootid);
					if(!isNew){
						String query="delete from " + table + " where id= " + fobj.getString("id");
						execute(query);
					}
				}
		   }
	   }else{
		   JSONObject fobj = JSONObject.fromObject(st);
			if (fobj.containsKey("id")){
				isNew=checkData(fobj.getString("id"),table,rootid);
				if(!isNew){
					String query="delete from " + table + " where id= " + fobj.getString("id");
					execute(query);
				}
			}
	   }
	   
	   return "{\"success\":true}";
   }
    public String dataUpdate(String st ,String table,String condition,boolean autoId,int rootid) throws SQLException{
	   boolean isNew=false;
	   String sqlString ="";
	   String projectId=condition.split("=")[1];
	   StringBuffer updateSb=new StringBuffer();
	   StringBuffer fsb=new StringBuffer();
	   StringBuffer vsb=new StringBuffer();
	   fsb.append("(");
	   vsb.append("(");
	   st=st.replace("index", "ind");
	   String key="";
	   String query="select * from " + table + " where id=0";
 	   SqlRowList rstemp=queryForRowSet(query);	
	   int colNum=rstemp.getResultList().size();
	   System.out.println("st: " + st);
	   if(st.toString().indexOf('[') > -1){
		   JSONArray jsonArray = JSONArray.fromObject(st);
		   for(Object ftemp:jsonArray){
			   System.out.println("ftemp:"  + ftemp.toString());
				JSONObject fobj = JSONObject.fromObject(ftemp);
				if (fobj.containsKey("id")){
					isNew=checkData(fobj.getString("id"),table,rootid);
				}
				Iterator<?> it=fobj.keys();
				if(isNew){
					while(it.hasNext()){
						key=(String) it.next();
						if(!key.toLowerCase().equals("id")){
							fsb.append(key+ ",");
							vsb.append(fieldDataFormate(key,fobj.get(key)) +",");
						}
					}
					if(autoId){
						fsb.append("prjplanid)");
						vsb.append(projectId+")");
					}else{
						fsb.append("prjplanid,id)");
						vsb.append(projectId+","+ maxid()+")");
					}
					
					sqlString="insert into   " + table +"  " + fsb.toString()+ " values " + vsb.toString() ;//...............................
					execute(sqlString);
					sqlString ="";
					fsb.delete(1,  fsb.length());
				    vsb.delete(1,  vsb.length());
				    isNew=false;
				    sqlString ="";
				} else{
					updateSb.delete(0, updateSb.length());
					updateSb.append("update  "+ table + " set ");
					while(it.hasNext()){
							key=(String) it.next();
							if(!((key.equals("ind")||key.toLowerCase().equals("parentid"))&&checkRoot)){
								updateSb.append(key+" = " + fieldDataFormate(key,fobj.get(key)) + ",");
							}
					};
					updateSb.append(condition+"  where id='" + fobj.getString("id")+ "'" );
					execute(updateSb.toString());
					updateSb.delete(0, updateSb.length());
				}
				
			}
			
	   }else{
		   JSONObject fobj = JSONObject.fromObject(st);
			if (fobj.containsKey("id")){
				isNew=checkData(fobj.getString("id"),table,rootid);
			}
			Iterator it=fobj.keys();
			if(isNew){
				while(it.hasNext()){
					key=(String) it.next();
					if(!key.toLowerCase().equals("id")){
						fsb.append(key+ ",");
						vsb.append(fieldDataFormate(key,fobj.get(key))+",");
					}
				}
				if(autoId){
					fsb.append("prjplanid)");
					vsb.append(projectId+")");
				}else{
					fsb.append("prjplanid,id)");
					vsb.append(projectId+","+ maxid()+")");
				}
				sqlString="insert into   " + table +"  " + fsb.toString()+ " values " + vsb.toString() ;
				execute(sqlString);
				fsb.delete(1,  fsb.length());
			    vsb.delete(1,  vsb.length());
			    isNew=false;
			    sqlString ="";
			} else{
				updateSb.delete(0, updateSb.length());
				updateSb.append("update  "+ table +" set ");
				while(it.hasNext()){
						key=(String) it.next();
						if(!((key.equals("ind")||key.toLowerCase().equals("parentid"))&&checkRoot)){
							updateSb.append(key+" = " + fieldDataFormate(key,fobj.get(key)) + ",");
						}
				};
				updateSb.append(condition + "  where id='" + fobj.getString("id")+ "'" );
				execute(updateSb.toString());
				updateSb.delete(0, updateSb.length());
			}
	   }
	   SqlRowList rst=queryForRowSet("select id,phantomid from mprojecttask where phantomid is not null and " + condition);
	   if (rst.next()){
		   int rows=rst.getResultList().size();
		   for(int i=0;i<rows;i++){
			   Map<String, Object> crst = rst.getResultList().get(i);
			   if(crst.get("phantomid".toUpperCase())!=null && !(crst.get("phantomid".toUpperCase()).equals(""))){
				   execute("update mprojecttask set parentid= " + crst.get("ID") + "  where phantomparentid='" + crst.get("phantomid".toUpperCase())+ "' and " + condition);
			   }
		   }
	   }
	   
	   execute("update mprojecttask set phantomid=null ,phantomparentid=null where ( phantomid is not null or phantomparentid is not null ) and " + condition);
	   	  
	   return "{\"success\":true}";
   }
    private Integer maxid() throws SQLException {
    	SqlRowList maxrs = null;
		maxrs=queryForRowSet("select max(id) from mprojecttask");
		if(maxrs.next()){
			int maxidvalue=maxrs.getInt(1)+1;
			return maxidvalue;
		}
		
		return 1;
	}
	
    private String fieldDataFormate(String key,Object fieldvalue){
    	
    	if(fieldvalue.equals(null)){
    		if((key.toLowerCase()).equals("parentid")){
    			return 0+ "";
    		}
    		return null;
    	}else{
	    	if(fieldvalue.getClass().equals("java.lang.Long")||fieldvalue.getClass().equals("java.math.BigDecimal")||fieldvalue.getClass().equals("java.lang.Integer")){
	    		return fieldvalue.toString();
	    	}else if(fieldvalue.getClass().equals("java.sql.Date")){
	    		return "to_date('" + fieldvalue.toString().split("T")[0]+"','yyyy-mm-dd')";
	    	}else {
	    		String isDate=fieldvalue.toString().split("T")[0];
	    		if(isDate!=null){
	    			try {
						format.parse(isDate);
						return "to_date('" + isDate+"','yyyy-mm-dd')";
					} catch (ParseException e) {
						return "'"+fieldvalue.toString()+"'";
					}
	    		}
	    		System.out.println("kye:"+ key + "   value:"+fieldvalue.toString() +"  ; className:"+ fieldvalue.getClass());
	    		return "'"+fieldvalue.toString()+"'";
	    	}
    	}
    }
    
   private boolean checkData(String id,String table,int rootid) throws SQLException{
	   boolean isNew=false;
	   SqlRowList idrs = queryForRowSet("select id from "+ table +"  where id =" + id);
	   if(!idrs.next()){
		    isNew=true;
	   }else {
		   if(Integer.parseInt(id)==rootid){
			   checkRoot=true;
		   }else{
			   checkRoot=false;
		   }
	   }

	   return isNew;
   }
}
