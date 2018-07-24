package com.uas.mobile.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.lang.dsl.DSLMapParser.variable_definition2_return;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.News;
import com.uas.erp.model.Note;
import com.uas.erp.model.PagingRelease;
import com.uas.mobile.service.MobileNewsService;

@Service
public class MobileNewsServiceImpl implements MobileNewsService {
@Autowired
private BaseDao baseDao;
	@Override
	public List<News> getNewsByPage(int page, int pageSize) {
		// TODO Auto-generated method stub
		try {
			return baseDao.getJdbcTemplate().query("select * from (select ne_id,ne_releaser,ne_theme,ne_releasedate,substr(getCNText(substr(ne_content,1,1000)), 1, 50) ne_content,case when instr(ne_content,'src=\"')>0 then "
					+ "substr(ne_content,instr(ne_content,'src=\"')+5,instr(ne_content,'\"',instr(ne_content,'src=\"')+5)- instr(ne_content,'src=\"')-5) else to_clob('images/photo1.jpg')  end  as headerImg,rownum r from (select ne_id,ne_releaser,ne_theme,ne_releasedate,ne_content from news order by ne_releasedate desc) T where rownum < ?) where r > ?",
					new BeanPropertyRowMapper<News>(News.class), page*pageSize + 1, (page - 1) * pageSize);
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public News getNewsById(Integer ne_id) {
		// TODO Auto-generated method stub
		try {
			return baseDao.getJdbcTemplate().queryForObject("select * from news where ne_id=?",
					new BeanPropertyRowMapper<News>(News.class),ne_id);
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public List<Note> getNotesByPage(Integer page, Integer pageSize) {
		// TODO Auto-generated method stub
		try {
			return baseDao.getJdbcTemplate().query("select * from (select no_id,no_title,no_apptime,no_approver,substr(getCNText(substr(no_content,1,1000)), 1, 50) no_content, rownum r from (select no_id,no_title,no_apptime,no_approver,no_content from note order by no_apptime desc) T where rownum < ?) where r > ?",
					new BeanPropertyRowMapper<Note>(Note.class), page*pageSize + 1, (page - 1) * pageSize);
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public Note getNoteById(Integer id) {
		// TODO Auto-generated method stub
		try {
			return baseDao.getJdbcTemplate().queryForObject("select * from note where no_id=?",
					new BeanPropertyRowMapper<Note>(Note.class),id);
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public Map<String, Object> getMessageDetailById(Integer id) {
		// TODO Auto-generated method stub
		String sql="SELECT PR_CODEVALUE as code,PR_RELEASER as people,to_char(PR_DATE,'yyyy-mm-dd HH24:mi:ss') as mdate,PR_FROM as mfrom,PR_CONTEXT as context FROM PAGINGRELEASE where  PR_ID="+id;
		List<Map<String, Object>> List = baseDao.queryForList(sql);
		if(List.size()==0){
			String sqlString="SELECT IH_CODEVALUE as code,IH_CALL as people,to_char(IH_DATE,'yyyy-mm-dd HH24:mi:ss') as mdate,IH_CONTEXT as context ,IH_FROM as mfrom FROM ICQHISTORY where IH_PRID="+id;
			List<Map<String, Object>> List2=baseDao.queryForList(sqlString);
			if(List2.size()==0){
				return null;
			}else {
				String iString=List2.get(0).get("context").toString().replaceAll("'","\\\\'");				
				List2.get(0).put("context", iString);
				if("note".equals(List2.get(0).get("mfrom"))){
					Object[] data=baseDao.getFieldsDataByCondition("note", "NO_APPROVER,NO_APPTIME,NO_CONTENT,NO_TITLE,NO_ATTACHS", "NO_ID='"+List2.get(0).get("code")+"'");
					Object attachs=data[4];
					String condition="";
					List<String> attachwithnames=new ArrayList<String>();
					if(attachs!=""&&attachs!=null&&attachs.toString().contains(";")){
						for(int i=0;i<=attachs.toString().split(";").length-1;i++){
							condition+="'"+attachs.toString().split(";")[i]+"',";
						}
						condition=condition.substring(0, condition.length()-1)+")";
					    SqlRowList sl=baseDao.queryForRowSet("select fp_id ,fp_path,fp_name from filepath where fp_id in ( "+condition);
					    while(sl.next()){
						attachwithnames.add(sl.getInt("fp_id")+"#"+sl.getString("fp_name"));
					   }
					}
					List2.get(0).put("NO_APPROVER",data[0] );
					List2.get(0).put("NO_APPTIME",data[1] );
					List2.get(0).put("NO_CONTENT",data[2]!=null?data[2].toString().replaceAll("'","\\\\'"):null );
					List2.get(0).put("NO_TITLE", data[3]);
					List2.get(0).put("attachwithnames",attachwithnames );
				}else {
					List2.get(0).put("NO_APPROVER", List2.get(0).get("PEOPLE"));
					List2.get(0).put("NO_APPTIME",List2.get(0).get("MDATE"));
				}
				return List2.get(0);
			}
		}else {
			String iString=List.get(0).get("context").toString().replaceAll("'","\\\\'");
			List.get(0).put("context", iString);
			if("note".equals(List.get(0).get("mfrom"))){
				Object[] data=baseDao.getFieldsDataByCondition("note", "NO_APPROVER,NO_APPTIME,NO_CONTENT,NO_TITLE,NO_ATTACHS", "NO_ID='"+List.get(0).get("code")+"'");
				Object attachs=data[4];
				String condition="";
				List<String> attachwithnames=new ArrayList<String>();
				if(attachs!=""&&attachs!=null&&attachs.toString().contains(";")){
					for(int i=0;i<=attachs.toString().split(";").length-1;i++){
						condition+="'"+attachs.toString().split(";")[i]+"',";
					}
					condition=condition.substring(0, condition.length()-1)+")";
				    SqlRowList sl=baseDao.queryForRowSet("select fp_id ,fp_path,fp_name from filepath where fp_id in ( "+condition);
				    while(sl.next()){
					attachwithnames.add(sl.getInt("fp_id")+"#"+sl.getString("fp_name"));
				   }
				}
				List.get(0).put("NO_APPROVER",data[0] );
				List.get(0).put("NO_APPTIME",data[1] );
				List.get(0).put("NO_CONTENT",data[2]!=null?data[2].toString().replaceAll("'","\\\\'"):null );
				List.get(0).put("NO_TITLE", data[3]);
				List.get(0).put("attachwithnames",attachwithnames );
			}else {
				List.get(0).put("NO_APPROVER", List.get(0).get("PEOPLE"));
				List.get(0).put("NO_APPTIME",List.get(0).get("MDATE"));
			}
			return List.get(0);
		}
		
		
		
	}

}
