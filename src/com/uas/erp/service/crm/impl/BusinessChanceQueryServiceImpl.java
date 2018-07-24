package com.uas.erp.service.crm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.model.BusinessChanceStage;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.service.crm.BusinessChanceQueryService;
@Service
public class BusinessChanceQueryServiceImpl implements BusinessChanceQueryService {
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private BaseDao baseDao;
	@Override
	public Map<String,Object> getBusinessChanceQueryConfigs(String condition,Integer start,Integer end) {
		// TODO Auto-generated method stub
		Map<String,Object> map=new HashMap<String,Object>();		
		List<GridColumns> columns=new ArrayList<GridColumns>();
		List<GridFields> fields=new ArrayList<GridFields>();
		List<BusinessChanceStage> stages=getStages();
		List<DetailGrid> d= getDetailGrids(stages);
		for(DetailGrid detail:d){
			columns.add(new GridColumns(detail,null));
			fields.add(new GridFields(detail));
		}
		map.put("fields", fields);
		map.put("columns", columns);
		map.put("stages", stages);
		return map;
	}
	@Override
	public String getProcessDataByCondition(String condition) {
		// TODO Auto-generated method stub
		List<BusinessChanceStage> stages=getStages();
		List<DetailGrid> d= getDetailGrids(stages);
		return baseDao.getDataStringByDetailGrid(d, condition, null,null);

	}
	private List<BusinessChanceStage> getStages(){
		try{
			return baseDao.getJdbcTemplate().query("select * from BusinessChanceStage order by bs_detno asc", 
					new BeanPropertyRowMapper<BusinessChanceStage>(BusinessChanceStage.class));
		} catch (EmptyResultDataAccessException e){
			e.printStackTrace();
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}
	private List<DetailGrid> getDetailGrids(List<BusinessChanceStage> stages){
		DetailGrid grid=null;
		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller("BusinessChance!Process", SpObserver.getSp());
		List<DetailGrid> d=new ArrayList<DetailGrid>();
		d.addAll(detailGrids);
		for(int i=1;i<stages.size()+1;i++){
			grid=new DetailGrid();
			grid.setDg_caption(stages.get(i-1).getBs_name());
			grid.setDg_field("bc_desc"+i);
			grid.setDg_type("text");
			grid.setDg_width(150);
			grid.setDg_locked(0);
			grid.setDg_dbbutton(Float.parseFloat("0"));
			grid.setDg_editable(Float.parseFloat("0"));
			grid.setDg_logictype("process");
			d.add(grid);
			grid=new DetailGrid();
			grid.setDg_field("bc_date"+i);
			grid.setDg_width(0);
			grid.setDg_type("datecolumn");
			grid.setDg_locked(0);
			grid.setDg_logictype("process");
			grid.setDg_dbbutton(Float.parseFloat("0"));
			grid.setDg_editable(Float.parseFloat("0"));
			d.add(grid);
		}
		return d;
	}
	@Override
	public List<JSONObject> getHopperByCondition(String condition) {
		// TODO Auto-generated method stub
		condition=condition!=null?condition:"1=1";
	    String querysql="select bs_name name,bs_color color,nvl(con,0) count  from   Businesschancestage  left join (select  count(*) con,bc_currentprocess from Businesschance where "+condition+" group by bc_currentprocess) on bc_currentprocess=Businesschancestage.bs_name order by bs_detno ";
	    List<JSONObject> objs=new ArrayList<JSONObject>();
	    JSONObject o=null;
	    SqlRowList sl=baseDao.queryForRowSet(querysql);
	    while(sl.next()){
	    	o=new JSONObject();
	    	o.put("name",sl.getObject("name"));
	    	o.put("color",sl.getObject("color"));
	    	o.put("count",sl.getObject("count"));
	    	objs.add(o);
	    }
	    return objs;
	}
	@Override
	public List<Map<String,Object>> getChanceDatasById(int id) {
		// TODO Auto-generated method stub
		List<Map<String,Object>> maps=new ArrayList<Map<String,Object>>();
		SqlRowList sl=baseDao.queryForRowSet("select  bcd_bsname,bcd_man,to_char(bcd_date,'yyyy-mm-dd') bcd_date ,bcd_sourcecode,bcd_sourcelink from businesschancedata where bcd_bcid="+id +" order by bcd_date asc");
		while(sl.next()){
			maps.add(sl.getCurrentMap());
		}
		return maps;
	}


}
