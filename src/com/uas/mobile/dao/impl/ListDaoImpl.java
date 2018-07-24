package com.uas.mobile.dao.impl;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DataListDetail;
import com.uas.mobile.dao.ListDao;
import com.uas.mobile.model.ListQuerySet;
@Repository("listDao")
public class ListDaoImpl extends BaseDao implements ListDao {
	@Override
	@Cacheable(value="datalist",key="#caller + #sob + 'getListView'")	
	public DataList getListView(String caller, String sob) {
		try{
			DataList dataList = getJdbcTemplate().queryForObject("select *  from datalist where dl_caller=?", 
					new BeanPropertyRowMapper<DataList>(DataList.class),caller);		
			List<DataListDetail> dataListDetails = getJdbcTemplate(dataList.getDl_tablename()).query(
					"select * from datalistdetail where dld_dlid=? and nvl(dld_mobileused,0)<>0 order by dld_detno", 
					new BeanPropertyRowMapper<DataListDetail>(DataListDetail.class), dataList.getDl_id());
			dataList.setDataListDetails(dataListDetails);
			return dataList;
		} catch (EmptyResultDataAccessException e){
			e.printStackTrace();
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	@Override
	@Cacheable(value="empsrelativesettings",key="#caller + #kind + #emid + 'getRelativesettings'")
	public String getRelativesettings(String caller,String kind, int emid) {
		SqlRowList sl=queryForRowSet("select es_field||es_conditionstr  from  empsrelativesettings  where es_emid="+emid+"  and es_pagecaller='"+caller+"' and nvl(es_kind,' ')='"+kind+"'");
		String conditionstr="";
		while (sl.next()){
			conditionstr+=sl.getString(1)+ " and ";
	    }
		if ("".equals(conditionstr)) return null;
		else  return conditionstr.substring(0,conditionstr.length()-4);
	}
	@Override
	public List<ListQuerySet> getListViewQuerySet(String caller) {
		// TODO Auto-generated method stub
		List<ListQuerySet> lists=getJdbcTemplate().query("select * from mobilelistqueryset where ls_caller=? order by ls_detno asc",new BeanPropertyRowMapper<ListQuerySet>(ListQuerySet.class),caller);		
		return lists;
	}
}
