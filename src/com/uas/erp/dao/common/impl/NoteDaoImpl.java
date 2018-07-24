package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.NoteDao;

@Repository("noteDao")
public class NoteDaoImpl extends BaseDao implements NoteDao {
    final static String Strsql="select * from (select T.*,ROW_NUMBER() over( order by no_apptime desc) rn from Note T) where no_id = ?";
    final static String PrevAndNextsql="select * from (select T.*,ROW_NUMBER() over( order by no_apptime desc) rn from Note T) where rn = ?";
	@Override
	public Map<String, Object> getNote(int id) {
		// TODO Auto-generated method stub		
		Map<String,Object> map=new HashMap<String,Object>();
		try {
			SqlRowList rs = queryForRowSet(Strsql, new Object[]{id});			
			if(rs.next()){
				map.put("no_title", rs.getString("no_title"));
				map.put("date", rs.getString("no_apptime"));
				map.put("approver", rs.getString("no_approver"));
				map.put("content", rs.getString("no_content"));	
				map.put("no_emergency", rs.getString("no_emergency"));
				SqlRowList rs2 = queryForRowSet(PrevAndNextsql, rs.getInt("rn")-1);
				if(rs2.next()){
					map.put("prevNOID", rs2.getString("no_id"));
					map.put("prevTitle", rs2.getString("no_title"));
				}else{
					map.put("prevNOID", null);
				}
				SqlRowList rs3 = queryForRowSet(PrevAndNextsql, rs.getInt("rn")+1);
				if(rs3.next()){
					map.put("nextNOID", rs3.getString("no_id"));
					map.put("nextTitle", rs3.getString("no_title"));
				}else{
					map.put("nextNOID", null);
				}
				String attachs=null;
				String condition="(";
				attachs=rs.getString("no_attachs");
				List<String> attachwithnames=new ArrayList<String>();
				if(attachs!=""&&attachs!=null&&attachs.contains(";")){
					for(int i=0;i<=attachs.split(";").length-1;i++){
						condition+="'"+attachs.split(";")[i]+"',";
					}
					condition=condition.substring(0, condition.length()-1)+")";
				    SqlRowList sl=queryForRowSet("select fp_id ,fp_path,fp_name from filepath where fp_id in "+condition);
				    while(sl.next()){
					attachwithnames.add(sl.getInt("fp_id")+"#"+sl.getString("fp_name"));
				   }
				
				}
				map.put("no_attachs", attachwithnames);				
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return map;
	}

}
