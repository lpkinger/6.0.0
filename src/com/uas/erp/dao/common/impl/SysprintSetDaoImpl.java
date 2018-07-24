package com.uas.erp.dao.common.impl;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.SysPrintSetDao;
import com.uas.erp.model.SysPrintSet;

@Repository
public class SysprintSetDaoImpl extends BaseDao implements SysPrintSetDao {

	@Override
	public SysPrintSet getSysPrintSet(String caller, String reportname) {
		if(reportname==null||"".equals(reportname)){
			int count=getCount("select count(1) from SysPrintSet where caller='"+caller+"'");
			if(count>1){
				try{
					SysPrintSet sysPrintSet=getJdbcTemplate().queryForObject("SELECT * from SysPrintSet WHERE caller=? AND isdefault=-1", 
							new BeanPropertyRowMapper<SysPrintSet>(SysPrintSet.class), caller);
					return sysPrintSet;
				}catch (EmptyResultDataAccessException e){
					BaseUtil.showError("找不到报表配置!");
					return null;
				}
			}else if(count == 0){
				BaseUtil.showError("找不到报表配置!");
				return null;
			}else{
				SysPrintSet sysPrintSet = getJdbcTemplate().queryForObject("SELECT * from SysPrintSet WHERE caller=?", 
						new BeanPropertyRowMapper<SysPrintSet>(SysPrintSet.class), caller);
				return sysPrintSet;
			}
		}else{
			int count=getCount("select count(1) from SysPrintSet where caller='"+caller+"' and reportname='"+reportname+"'");
			if(count == 0){
				BaseUtil.showError("找不到报表配置!");
				return null;
			}else{
				SysPrintSet sysPrintSet=getJdbcTemplate().queryForObject("SELECT * from SysPrintSet WHERE caller=? AND "
						+ "reportname=?",new BeanPropertyRowMapper<SysPrintSet>(SysPrintSet.class), caller,reportname);
				return sysPrintSet;
			}
		}
	}

	@Override
	public List<SysPrintSet> getData(String condition, int page,
			int pageSize) {
		if("".equals(condition)){
			condition="1=1";
		}
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		String sql="select * from (select TT.*, ROWNUM rn from (select id,caller,reportname,printtype,title,isdefault,needaudit,nopost,needenoughstock,"
				+ "countfield,statusfield,statuscodefield,allowmultiple,handlermethod,defaultcondition,tablename"
				+ " from (select tab.* from sysprintset tab where "+condition+")  order by caller,id desc )TT "
				+ "where ROWNUM <= ?) where rn >= ?";
		List<SysPrintSet> sysPrintSet=getJdbcTemplate("SysPrintSet").query(sql,new BeanPropertyRowMapper<SysPrintSet>(SysPrintSet.class),end,start);
		return sysPrintSet;
	}
}
