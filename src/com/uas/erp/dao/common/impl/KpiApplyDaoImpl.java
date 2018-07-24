package com.uas.erp.dao.common.impl;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.KpiApplyDao;
import com.uas.erp.model.DetailGrid;

@Repository
public class KpiApplyDaoImpl extends BaseDao implements KpiApplyDao{

	@Override
	public List<DetailGrid> getGridsByCaller(String caller,String ktcode,int count, String sob) {
		try{
			List<DetailGrid> list = getJdbcTemplate().query(
					"SELECT * FROM detailgrid WHERE dg_caller=? ORDER BY dg_sequence ", 
					new BeanPropertyRowMapper<DetailGrid>(DetailGrid.class), caller);
			int n=7;
			SqlRowList rs=queryForRowSet("select kp_name from Kpigradetype where kp_ktcode='"+ktcode+"' order by kp_id");
			for(int i=1;i<=count;i++){
				rs.next();
				if("自评".equals(rs.getString("kp_name"))){
					DetailGrid FI=new DetailGrid(n+i,rs.getString("kp_name"),0,(float) 0,"kad_ids"+i,"","text","KPIAPPLYDET",0,(float) 0,"");
					list.add(FI);
				    n++;
				    DetailGrid FN=new DetailGrid(n+i,rs.getString("kp_name"),100,(float) -1,"kad_names"+i,"","text","KPIAPPLYDET",0,(float) -1,"Employee|em_name");
				    list.add(FN);
				    n++;
				}else{
					DetailGrid FI=new DetailGrid(n+i,rs.getString("kp_name"),0,(float) 0,"kad_ids"+i,"","text","KPIAPPLYDET",0,(float) -1,"");
					list.add(FI);
				    n++;
				    DetailGrid FN=new DetailGrid(n+i,rs.getString("kp_name"),300,(float) -5,"kad_names"+i,"","text","KPIAPPLYDET",0,(float) -1,"Employee|em_name");
				    list.add(FN);
				    n++;
				}
			}
			return  list;
		} catch (EmptyResultDataAccessException e){
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
