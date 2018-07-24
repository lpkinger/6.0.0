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
public class SimpleDAO extends BaseDao {
	private SqlRowList rs = null;
	private int maxId;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map dataList(String st,String condition,String table,String fieldsString) throws SQLException {
		String farray[] = null ;
		boolean isFieldString=false;
		
		if(condition!=null){
			condition= " where  " + condition;
		}else if(st!=null){
			JSONArray jsonArray = JSONArray.fromObject(st);
			for(Object ftemp:jsonArray){
				JSONObject fobj = JSONObject.fromObject(ftemp);
				String field=fobj.getString("property");
				String fieldVal=fobj.getString("value");
				condition="where "  + field + " = '"+ fieldVal + "' ";
			}
		}else  {
			condition=" where 1=2";
		}
		Map success = new HashMap();
		ArrayList datalisttemp = new ArrayList();
		String fs=null;
			if (!fieldsString.equals("")){
		    	StringBuffer sb= new StringBuffer();
		    	sb.delete(0, sb.length());
		    	farray=fieldsString.split(",");
		    	for(String stemp:farray){ 
		    		sb.append(stemp.split(":")[1]+",");
				}
		    	sb.delete(sb.length()-1, sb.length());
		    	fs=sb.toString();
		    	isFieldString=true;
		    } else{
		    	fs="*";
		    	isFieldString=false;
		    }
			rs=queryForRowSet("select "+ fs+ " from "+ table +" " + condition);
			
			int rows=rs.getResultList().size();
			if (rows<1){
				success.put("data", datalisttemp);
				success.put("success", "true");
				return success;
			}
			Map<String, Object> b = rs.getResultList().get(0);
			Object[] keyName = b.keySet().toArray();
			for (int j =0 ; j < rows; j++) {
				Map<String, Object>crs=rs.getResultList().get(j);
				HashMap productRow = new HashMap();
				for (int i = 0; i < keyName.length; i++) {
					String keyname=keyName[i].toString();					
					if(!isFieldString){
						productRow.put(keyname.toLowerCase(), crs.get(keyname)==null?" ":crs.get(keyname).toString());
					}else{
						productRow.put(getBackName(keyname,farray), crs.get(keyname)==null?" ":crs.get(keyname).toString());
					}
				}
				datalisttemp.add(productRow);
			}
			success.put("data", datalisttemp);
			success.put("success", "true");
		
		return success;//datalist;
	}
	private String getBackName(String columnName, String[] farray) {
		for(int i =0;i<farray.length ;i++ ){
			if(columnName.toLowerCase().equals(farray[i].split(":")[1].toLowerCase())){
				return farray[i].split(":")[0];
			}
		}
		return null;
	}
	public String dataDelete(String st ,String table) throws SQLException{
	   boolean isNew =false;
	   if(st.toString().indexOf('[')>-1){
		   JSONArray jsonArray = JSONArray.fromObject(st);
		   for(Object ftemp:jsonArray){
				JSONObject fobj = JSONObject.fromObject(ftemp);
				if (fobj.containsKey("id")){
					isNew=checkData(fobj.getString("id"),table);
					if(!isNew){
						execute("delete from " + table + " where id= " + fobj.getString("id"));
					}
				}
		   }
	   }else{
		   JSONObject fobj = JSONObject.fromObject(st);
			if (fobj.containsKey("id")){
				isNew=checkData(fobj.getString("id"),table);
				if(!isNew){
					execute("delete from "+ table+ " where id= " + fobj.getString("id"));
				}
			}
	   }
	   return "{\"success\":true}";
   }
   public String dataUpdate(String st ,String subkey,String subkeyval,String table,boolean autoId,String fieldsString) throws SQLException{
	   boolean isNew=false;
	   String sqlString ="";
	   StringBuffer updateSb=new StringBuffer();
	   StringBuffer fsb=new StringBuffer();
	   StringBuffer vsb=new StringBuffer();
	   //st=st.replace("index", "ind");
	   
	   String key="";
	   String farray[]=null;
	   if(fieldsString!=null){
		   if(fieldsString.indexOf(",")>-1){
			   farray=fieldsString.split(",");
		   }
	   }
	   
	   if(st.toString().indexOf('[') > -1){
		   JSONArray jsonArray = JSONArray.fromObject(st);
		   for(Object ftemp:jsonArray){
				JSONObject fobj = JSONObject.fromObject(ftemp);
				if (fobj.containsKey("id")){
					isNew=checkData(fobj.getString("id"),table);
				}
				Iterator it=fobj.keys();
				if(isNew){
					if(subkey!=null){
						fsb.append("("+subkey+",");
						vsb.append("('"+subkeyval+"',");
					}else{
						fsb.append("(");
						vsb.append("(");
					}
					while(it.hasNext()){
						key=(String) it.next();
						if(!key.equals("id")){
							/*fsb.append(getKey(farray,key)+ ",");
							String value = fobj.getString(key);  
							vsb.append("'"+value+"',");*/
							fsb.append(getKey(farray,key)+ ",");
							vsb.append(fieldDataFormate(key,fobj.get(key)) +",");
						}else{
							if(!autoId){
								fsb.append(getKey(farray,key)+ ",");
								vsb.append("'"+getMaxId(table)+"',");
							}
						}
					}
					fsb.delete(fsb.length()-1, fsb.length());
					fsb.append(")");
					vsb.delete(vsb.length()-1, vsb.length());
					vsb.append(")");
					sqlString="insert into   " + table +"  " + fsb.toString()+ " values " + vsb.toString() ;
					System.out.println("180:   "+sqlString);
					execute(sqlString);
					sqlString ="";
					fsb.delete(0,  fsb.length());
				    vsb.delete(0,  vsb.length());
				    isNew=false;
				    sqlString ="";
				} else{
					updateSb.delete(0, updateSb.length());
					updateSb.append("update  "+ table + " set ");
					while(it.hasNext()){
						key=(String) it.next();
						if(fobj.getString(key)!=null && !fobj.getString(key).equals("null")){//如果值为空,则不更新这个字段的值
							updateSb.append(getKey(farray,key)+" = '" + fobj.get(key) + "',");
						}
					};
					updateSb.delete(updateSb.length()-1, updateSb.length());
					updateSb.append("  where id=" + fobj.get("id") );
					System.out.println("197:  " + updateSb.toString());
					execute(updateSb.toString());
					updateSb.delete(0, updateSb.length());
				}
				
			}
			
	   }else{
		   JSONObject fobj = JSONObject.fromObject(st);
			if (fobj.containsKey("id")){
				System.out.println("id: " + fobj.getString("id"));
				isNew=checkData(fobj.getString("id"),table);
			}
			Iterator it=fobj.keys();
			if(isNew){
				if(subkey!=null){
					fsb.append("("+subkey+",");
					vsb.append("('"+subkeyval+"',");
				}else{
					fsb.append("(");
					vsb.append("(");
				}
				while(it.hasNext()){
					key=(String) it.next();
					if(!key.equals("id")){
						fsb.append(getKey(farray,key)+ ",");
						/*String value = fobj.getString(key);  
						vsb.append("'"+value+"',");*/
						vsb.append(fieldDataFormate(key,fobj.get(key))+",");
					}else{
						if(!autoId){
							fsb.append(getKey(farray,key)+ ",");
							vsb.append("'"+getMaxId(table)+"',");
						}
					}
				}
				fsb.delete(fsb.length()-1, fsb.length());
				fsb.append(")");
				vsb.delete(vsb.length()-1, vsb.length());
				vsb.append(")");
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
					if(fobj.getString(key)!=null && !fobj.getString(key).equals("null")){//如果值为空,则不更新这个字段的值
						//updateSb.append(getKey(farray,key)+" = '" + fobj.getString(key) + "',");
						updateSb.append(getKey(farray,key)+" = " + fieldDataFormate(key,fobj.get(key)) + ",");
					}
				};
				updateSb.delete(updateSb.length()-1, updateSb.length());
				updateSb.append("  where id=" + fobj.getString("id")+ "" );
				execute(updateSb.toString());
				updateSb.delete(0, updateSb.length());
			}
	   }
	   return "{\"success\":true}";
   }
private String fieldDataFormate(String key, Object fieldvalue) {
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
private int getMaxId(String table) {
	   SqlRowList rstemp=null;
		rstemp=queryForRowSet("select max(id) from " + table);
	   if(rstemp.next()){
		   maxId=rstemp.getInt(1)+1;
	   }else{
		   maxId=1;
	   }
	   
	return maxId;
}
private String getKey(String[] farray, String key) {
	if(farray.length==0){
		return key;
	}else{
		for(String stemp:farray){ 
    		if(stemp.split(":")[0].equals(key)){
    			return stemp.split(":")[1];
    		}
		}
	}
	return key;
}
private boolean checkData(String id,String table) throws SQLException{
	   boolean isNew=false;
	   SqlRowList idrs = queryForRowSet("select id from "+ table +"  where id =" + id);
	   if(!idrs.next()){
		    isNew=true;
	   }

	   return isNew;
}
}
