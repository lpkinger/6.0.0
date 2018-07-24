package com.uas.sysmng.service.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.sysmng.service.SysmngBasicService;

@Service
public class SysmngBasicServiceImpl implements SysmngBasicService{
	@Autowired
	private BaseDao baseDao;
	@Override
	public List<Map<String, Object>> getDictionaryData(String condition, int page, int pageSize,String tableName) {
		if("".equals(condition)){
			condition="1=1";
		}
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		String sql = "select *　from (select a.*,rownum rn from (select * from "+tableName+" where "+condition+" and rownum<="+end+" order by OBJECT_NAME ASC)a) where rn >="+start+ " order by OBJECT_NAME ASC";
		
		return baseDao.getJdbcTemplate().queryForList(sql);
	}

	@Override
	public List<Map<String,Object>> getGrid1PanelByCaller(String caller) {
		String sql;
		if(caller.equals("1=1")){
			
			
			 sql="select FO_CALLER, FD_ID,FD_TABLE,FD_FIELD,FD_CAPTION,FD_TYPE,FD_ISFIXED from  form ,formdetail where fo_id=fd_foid and rownum < 100 order by FD_ID asc ";
			 
		}else{
			 sql="select FO_CALLER,FD_ID,FD_TABLE,FD_FIELD,FD_CAPTION,FD_TYPE,FD_ISFIXED from  form ,formdetail where fo_id=fd_foid and lower(fo_caller)=lower('"+caller+"') order by FD_ID asc";
		}
		
		//FD_TABLE,FD_FIELD,FD_CAPTION,FD_TYPE
		
		try{
			
			return baseDao.getJdbcTemplate().queryForList(sql);
		
			
		} catch (EmptyResultDataAccessException e){
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public List<Map<String, Object>> getGrid2PanelByCaller(String caller) {
		// TODO Auto-generated method stub
		
		String sql;
		if(caller.equals("1=1")){
			
			
			 sql="select DG_CALLER, DG_ID,DG_TABLE,DG_FIELD,DG_CAPTION,DG_TYPE,DG_ISFIXED from DETAILGRID  where rownum < 100 order by DG_ID asc";
			 
		}else{
			 sql="select DG_CALLER, DG_ID,DG_TABLE,DG_FIELD,DG_CAPTION,DG_TYPE,DG_ISFIXED from  DETAILGRID where lower(DG_CALLER)=lower('"+caller+"' )order by DG_ID asc";
		}
		
		//FD_TABLE,FD_FIELD,FD_CAPTION,FD_TYPE
		
		try{
			
			return baseDao.getJdbcTemplate().queryForList(sql);
		
			
		} catch (EmptyResultDataAccessException e){
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public boolean saveGrid1PanelById(String addid,String deleteid) {
		// TODO Auto-generated method stub\
		String sql1="update formdetail set FD_ISFIXED=-1 where fd_id in ("+addid+")";
		String  sql2="update formdetail set FD_ISFIXED=0 where fd_id in ("+deleteid+")";
		try {
			baseDao.execute(sql1);
			baseDao.execute(sql2);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
			
		}
		
	
	}
	
	
	public boolean saveGrid2PanelById(String addid,String deleteid){
	
		String sql1="update detailgrid set DG_ISFIXED=-1 where DG_ID in ("+addid+")";
		String  sql2="update detailgrid set DG_ISFIXED=0 where DG_ID in ("+deleteid+")";
		try {
			baseDao.execute(sql1);
			baseDao.execute(sql2);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
	   }
	
		
		
	}


	public boolean checkModulePower(String emCode,String moduleCode){
		return baseDao.checkIf("MODULEPOWER", "mp_emcode='" + emCode + "' and mp_mdcode='" + moduleCode + "'");
	}
}