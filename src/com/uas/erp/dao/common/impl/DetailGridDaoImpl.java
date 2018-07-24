package com.uas.erp.dao.common.impl;

import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.model.DetailGrid;

@Repository("detailGridDao")
public class DetailGridDaoImpl extends BaseDao implements DetailGridDao{

	@Override
	@Cacheable(value="gridpanel",key="#sob + '@' + #caller + 'getDetailGridsByCaller'",unless="#result==null")
	public List<DetailGrid> getDetailGridsByCaller(String caller, String sob) {
		try{
			List<DetailGrid> list = getJdbcTemplate().query(
					"SELECT * FROM detailgrid WHERE dg_caller=? ORDER BY dg_sequence ", 
					new BeanPropertyRowMapper<DetailGrid>(DetailGrid.class), caller);
			/*if(list != null && list.size() > 0){
				checkTable(list.get(0).getDg_table());
			}*/
			return  list;
		} catch (EmptyResultDataAccessException e){
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
