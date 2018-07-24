package com.uas.erp.dao.common.impl;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.BenchDao;
import com.uas.erp.model.Bench.BenchBusiness;
import com.uas.erp.model.Bench.BenchButton;
import com.uas.erp.model.Bench.SceneButton;
import com.uas.erp.model.Bench.BenchScene;
import com.uas.erp.model.Bench.BenchSceneGrid;
import com.uas.erp.model.Bench;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;

/**
 * 
 */

@Repository("benchDao")
public class BenchDaoImpl extends BaseDao implements BenchDao{
	
	private String getJobs(Employee employee) {
		String condition = "";
		if (employee.getEm_defaulthsid()!=null) {
			condition =","+employee.getEm_defaulthsid();
		}
		
		for (EmpsJobs empsJob : employee.getEmpsJobs()) {
			condition += "," + empsJob.getJob_id();
		}
		condition = condition.substring(1); 
		if (!"".equals(condition)) {
			return condition;
		}
		
		return "";
	}
	
	@Override
	@Cacheable(value = "bench", key = "#sob + '@' + #bccode + 'getBench'",unless="#result==null")
	public Bench getBench(String bccode, String sob) {
		Bench bench = null;
		try {
			bench = getJdbcTemplate().queryForObject("select * from bench where bc_code=?", new BeanPropertyRowMapper<Bench>(Bench.class),bccode);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return bench;
	}
	
	@Override
	public List<BenchButton> getBenchButtons(String bccode) {
		List<BenchButton> benchButtons = new ArrayList<Bench.BenchButton>();
		try {
			benchButtons = getJdbcTemplate().query("select * from benchbutton where bb_bccode=? order by bb_detno asc", 
					new BeanPropertyRowMapper<BenchButton>(BenchButton.class), bccode);
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchButtons;
	}
	
	@Override
	@Cacheable(value = "button", key = "#sob + '@' + #bccode + 'getBenchButtons'",unless="#result==null")
	public Map<String, List<BenchButton>> getBenchButtons(String bccode, String sob) {
		
		String GETBENCHBUTTON = "select BB_CALLER,BB_LISTURL,BB_BUSINGROUP,BB_ID,BB_CODE,BB_BCCODE,BB_URL,BB_TEXT,BB_DETNO,BB_GROUP from "
				+ "benchbutton where bb_bccode= ? and nvl(bb_group,'业务制单') = ? and nvl(bb_busingroup,' ') = ' ' union all "
				+ "select null,null,BB_BUSINGROUP,null,null,null,null,BB_BUSINGROUP,min(BB_DETNO),null from benchbutton "
				+ "where bb_bccode=? and nvl(bb_group,'业务制单') = ? and nvl(bb_busingroup,' ') <> ' ' group by bb_busingroup order by BB_DETNO";
		
		Map<String, List<BenchButton>> result = new HashMap<String, List<BenchButton>>();
		List<BenchButton> benchButtons = new ArrayList<Bench.BenchButton>();
		try {
			benchButtons = getJdbcTemplate().query(GETBENCHBUTTON, 
					new BeanPropertyRowMapper<BenchButton>(BenchButton.class), bccode, "业务制单", bccode, "业务制单");
			benchButtons = getBenchButtons(benchButtons, bccode, "业务制单", "");
			result.put("makeOrder", benchButtons);
			
			benchButtons = getJdbcTemplate().query(GETBENCHBUTTON, 
					new BeanPropertyRowMapper<BenchButton>(BenchButton.class), bccode, "基础资料", bccode, "基础资料");
			benchButtons = getBenchButtons(benchButtons, bccode, "基础资料", "");
			result.put("basicData", benchButtons);
			
			benchButtons = getJdbcTemplate().query(GETBENCHBUTTON, 
					new BeanPropertyRowMapper<BenchButton>(BenchButton.class), bccode, "更多操作", bccode, "更多操作");
			benchButtons = getBenchButtons(benchButtons, bccode, "更多操作", "");
			result.put("moreOperat", benchButtons);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return null;
	}
	
	private List<BenchButton> getBenchButtons(List<BenchButton> benchButtons, String bccode, String type, String condition){
		for (BenchButton benchButton : benchButtons) {
			if(StringUtil.hasText(benchButton.getBb_busingroup())){
				List<BenchButton> buttons = new ArrayList<Bench.BenchButton>();
				buttons = getJdbcTemplate().query("select * from benchbutton where bb_bccode=? and nvl(bb_group,'业务制单') = ? and bb_busingroup = ? " + condition + " order by bb_detno asc", 
							new BeanPropertyRowMapper<BenchButton>(BenchButton.class), bccode, type, benchButton.getBb_busingroup());
				benchButton.setMenuButtons(buttons);
			}
		}
		return benchButtons;
	}
	
	
	@Cacheable(value = "button", key = "#employee.em_master + '@' + #bccode + #employee.em_id + 'getBenchButtons'",unless="#result==null")
	public Map<String, List<BenchButton>> getBenchButtonsByPower(String bccode, Employee employee){
		
		String jobs = getJobs(employee);
		String condition = " and (bb_caller in (select distinct pp_caller from positionpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_joid in(" + jobs + "))  or bb_caller in (select distinct pp_caller from personalpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_emid=" + employee.getEm_id()+"))";
		String GETBENCHBUTTON = "select BB_CALLER,BB_LISTURL,BB_BUSINGROUP,BB_ID,BB_CODE,BB_BCCODE,BB_URL,BB_TEXT,BB_DETNO,BB_GROUP from benchbutton where bb_bccode= ? and nvl(bb_group,'业务制单') = ? and nvl(bb_busingroup,' ') = ' ' " + condition + " union all select null,null,BB_BUSINGROUP,null,null,null,null,BB_BUSINGROUP,min(BB_DETNO),null from benchbutton where bb_bccode=? and nvl(bb_group,'业务制单') = ? and nvl(bb_busingroup,' ') <> ' ' " + condition + " group by bb_busingroup order by BB_DETNO";
		
		Map<String, List<BenchButton>> result = new HashMap<String, List<BenchButton>>();
		List<BenchButton> benchButtons = new ArrayList<Bench.BenchButton>();
		try {
			benchButtons = getJdbcTemplate().query(GETBENCHBUTTON, 
					new BeanPropertyRowMapper<BenchButton>(BenchButton.class), bccode, "业务制单", bccode, "业务制单");
			benchButtons = getBenchButtons(benchButtons, bccode, "业务制单", condition);
			result.put("makeOrder", benchButtons);
			
			benchButtons = getJdbcTemplate().query(GETBENCHBUTTON, 
					new BeanPropertyRowMapper<BenchButton>(BenchButton.class), bccode, "基础资料", bccode, "基础资料");
			benchButtons = getBenchButtons(benchButtons, bccode, "基础资料", condition);
			result.put("basicData", benchButtons);
			
			benchButtons = getJdbcTemplate().query(GETBENCHBUTTON, 
					new BeanPropertyRowMapper<BenchButton>(BenchButton.class), bccode, "更多操作", bccode, "更多操作");
			benchButtons = getBenchButtons(benchButtons, bccode, "更多操作", condition);
			result.put("moreOperat", benchButtons);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return null;
	}
	
	@Override
	@Cacheable(value = "benchbusiness", key = "#employee.em_master + '@' + #bccode + 'getBenchBusinesses'",unless="#result==null")
	public List<BenchBusiness> getBenchBusinesses(String bccode, Employee employee) {
		List<BenchBusiness> benchBusinesses = new ArrayList<Bench.BenchBusiness>();
		try {
			benchBusinesses = getJdbcTemplate().query("select bb_code,bb_name from benchscene,benchbusiness where bs_bbcode = bb_code"
					+ " and bs_bccode=? and (not exists(select 1 from BenchSceneEmps where be_bccode = ? and be_emid = ?) or "
					+ "exists(select 1 from BenchSceneEmps where be_bccode = ? and be_emid = ? and be_bscode = bs_code)) and bb_statuscode <> 'DISABLE' and nvl(bs_enable,0) <> 0 group by bb_name,bb_detno,bb_code order by bb_detno asc,bb_code asc", 
					new BeanPropertyRowMapper<BenchBusiness>(BenchBusiness.class), bccode, bccode, employee.getEm_id(), bccode, employee.getEm_id());
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchBusinesses;
	}
	
	@Override
	@Cacheable(value = "benchbusiness", key = "#employee.em_master + '@' + #bccode + #employee.em_id + 'getHideBusinesses'",unless="#result==null")
	public List<BenchBusiness> getHideBusinesses(String bccode, Employee employee) {
		List<BenchBusiness> benchBusinesses = new ArrayList<Bench.BenchBusiness>();
		try {
			benchBusinesses = getJdbcTemplate().query("select bb_code,bb_name from benchscene,benchbusiness where bs_bbcode = bb_code"
				+ " and bs_bccode=? and bb_statuscode <> 'DISABLE' and bb_code not in (SELECT bbe_bbcode from BENCHBUSINESSEMPS where"
				+ " bbe_bccode = bs_bccode and bbe_emid = ? and (not exists(select 1 from BenchSceneEmps where be_bccode = ? and be_emid = ?) or "
					+ "exists(select 1 from BenchSceneEmps where be_bccode = ? and be_emid = ? and be_bscode = bs_code)) and bb_statuscode <> 'DISABLE') and nvl(bs_enable,0) <> 0 group by bb_name,bb_detno,bb_code order by bb_detno asc,bb_code asc", 
					new BeanPropertyRowMapper<BenchBusiness>(BenchBusiness.class), bccode, employee.getEm_id(), bccode, employee.getEm_id(), bccode, employee.getEm_id());
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchBusinesses;
	}
	
	@Override
	@Cacheable(value = "benchbusiness", key = "#employee.em_master + '@' + #bccode + #employee.em_id + 'getBenchBusinessesByPower'",unless="#result==null")
	public List<BenchBusiness> getBenchBusinessesByPower(String bccode, Employee employee) {
		List<BenchBusiness> benchBusinesses = new ArrayList<Bench.BenchBusiness>();
		try {
			String jobs = getJobs(employee);
			String sql = "select bb_code,bb_name from benchscene,benchbusiness where bs_bbcode = bb_code"
					+ " and bs_bccode=? and ((nvl(bs_caller,' ') <> ' ' and bs_caller in (select distinct pp_caller from (select pp_caller from positionpower where"
					+ " nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_joid in("+jobs+")) union (select pp_caller from personalpower"
					+ " where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_emid= ?))) or (nvl(bs_caller,' ') = ' ')) "
					+ "and (not exists(select 1 from BenchSceneEmps where be_bccode = ? and be_emid = ?) or exists(select 1 from BenchSceneEmps "
					+ "where be_bccode = ? and be_emid = ? and be_bscode = bs_code)) and bb_statuscode <> 'DISABLE' and nvl(bs_enable,0) <> 0 group by bb_name,bb_detno,bb_code order by bb_detno asc,bb_code asc";
			benchBusinesses = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<BenchBusiness>(BenchBusiness.class), bccode, employee.getEm_id(), bccode, employee.getEm_id(), bccode, employee.getEm_id());
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchBusinesses;
	}
	
	@Override
	@Cacheable(value = "benchbusiness", key = "#employee.em_master + '@' + #bccode + #employee.em_id + 'getHideBusinessesByPower'",unless="#result==null")
	public List<BenchBusiness> getHideBusinessesByPower(String bccode, Employee employee) {
		List<BenchBusiness> benchBusinesses = new ArrayList<Bench.BenchBusiness>();
		try {
			String jobs = getJobs(employee);
			String sql = "select bb_code,bb_name from benchscene,benchbusiness where bs_bbcode = bb_code"
					+ " and bs_bccode=? and ((nvl(bs_caller,' ') <> ' ' and bs_caller in (select distinct pp_caller from "
					+ "(select pp_caller from positionpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 "
					+ "and pp_joid in("+jobs+")) union (select pp_caller from personalpower where (pp_alllist=1 or pp_selflist=1 "
					+ "or pp_jobemployee=1 or pp_see=1) and pp_emid= ?))) or (nvl(bs_caller,' ') = ' ')) and (not exists(select 1 from "
					+ "BenchSceneEmps where be_bccode = ? and be_emid = ?) or exists(select 1 from BenchSceneEmps where be_bccode = ? and be_emid = ? and be_bscode = bs_code))"
					+ " and bb_statuscode <> 'DISABLE' and bb_code not in (SELECT bbe_bbcode from BENCHBUSINESSEMPS where bbe_bccode = bs_bccode"
					+ " and bbe_emid = ?  and bb_statuscode <> 'DISABLE') and nvl(bs_enable,0) <> 0 group by bb_name,bb_detno,bb_code order by bb_detno asc,bb_code asc";
			benchBusinesses = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<BenchBusiness>(BenchBusiness.class), bccode, employee.getEm_id(), bccode, employee.getEm_id(), bccode, employee.getEm_id(), employee.getEm_id());
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchBusinesses;
	}
	
	@Override
	@Cacheable(value = "benchbusiness", key = "#employee.em_master + '@' + #bccode + #employee.em_id + 'getSelfBenchBusinesses'",unless="#result==null")
	public List<BenchBusiness> getSelfBenchBusinesses(String bccode, Employee employee) {
		List<BenchBusiness> benchBusinesses = new ArrayList<Bench.BenchBusiness>();
		try {
			benchBusinesses = getJdbcTemplate().query("SELECT bb_code,bb_name from benchscene,benchbusiness,BENCHBUSINESSEMPS where bs_bbcode = bb_code and bbe_bbcode = bb_code"
				+ " and bbe_bccode = ? and bbe_emid = ? and (not exists(select 1 from BenchSceneEmps where be_bccode = ? and be_emid = ?) or exists(select 1 from BenchSceneEmps "
					+ "where be_bccode = ? and be_emid = ? and be_bscode = bs_code)) and bb_statuscode <> 'DISABLE' group by bb_name,bbe_bbdetno,bb_code order by bbe_bbdetno asc", 
				new BeanPropertyRowMapper<BenchBusiness>(BenchBusiness.class), bccode, employee.getEm_id(), bccode, employee.getEm_id(), bccode, employee.getEm_id());
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchBusinesses;
	}
	
	@Override
	@Cacheable(value = "benchbusiness", key = "#employee.em_master + '@' + #bccode + #employee.em_id + 'getSelfBenchBusinesses'",unless="#result==null")
	public List<BenchBusiness> getSelfBenchBusinessesByPower(String bccode, Employee employee) {
		List<BenchBusiness> benchBusinesses = new ArrayList<Bench.BenchBusiness>();
		try {
			String jobs = getJobs(employee);
			benchBusinesses = getJdbcTemplate().query("SELECT bb_code,bb_name from benchscene,benchbusiness,BENCHBUSINESSEMPS where bs_bbcode = bb_code and bbe_bbcode = bb_code"
				+ " and exists(select 1 from benchscene where bs_bbcode = bb_code and ((nvl(bs_caller,' ') <> ' ' and bs_caller in (select distinct pp_caller from "
				+ "(select pp_caller from positionpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 "
				+ "and pp_joid in("+jobs+")) union (select pp_caller from personalpower where (pp_alllist=1 or pp_selflist=1 "
				+ "or pp_jobemployee=1 or pp_see=1) and pp_emid= ?))) or (nvl(bs_caller,' ') = ' ')) and (not exists(select 1 from "
				+ "BenchSceneEmps where be_bccode = ? and be_emid = ?) or exists(select 1 from BenchSceneEmps where be_bccode = ? and be_emid = ? "
				+ "and be_bscode = bs_code)) and nvl(bs_enable,0) <> 0) and bbe_bccode = ? and bbe_emid = ? and bb_statuscode <> 'DISABLE' group by bb_name,bbe_bbdetno,bb_code order by bbe_bbdetno asc", 
				new BeanPropertyRowMapper<BenchBusiness>(BenchBusiness.class), employee.getEm_id(), bccode, employee.getEm_id(), bccode, employee.getEm_id(), bccode, employee.getEm_id());
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchBusinesses;
	}
	
	@Override
	public List<BenchScene> getBenchScenes(String bccode, String sob) {
		List<BenchScene> benchScenes = new ArrayList<Bench.BenchScene>();
		try {
			benchScenes = getJdbcTemplate().query("select * from benchscene,benchbusiness where bs_bbcode = bb_code and bs_bccode=? order by bb_detno,bs_detno asc", 
					new BeanPropertyRowMapper<BenchScene>(BenchScene.class), bccode);
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchScenes;
	}
	
	
	@Override
	@Cacheable(value = "benchscenes", key = "#sob + '@' + #bccode + #bbcode + 'getBenchScenes'",unless="#result==null")
	public List<BenchScene> getBenchScenes(String bccode, String bbcode, String sob) {
		List<BenchScene> benchScenes = null;
		try {
			benchScenes = getJdbcTemplate().query("select * from benchscene where bs_bccode=? and bs_bbcode = ? and nvl(bs_enable,0) <> 0 order by bs_detno asc", 
					new BeanPropertyRowMapper<BenchScene>(BenchScene.class), bccode, bbcode);
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchScenes;
	}
	
	@Override
	@Cacheable(value = "benchscenes", key = "#employee.em_master + '@' + #bccode + #bbcode + #employee.em_id + 'getBenchScenesByPower'",unless="#result==null")
	public List<BenchScene> getBenchScenesByPower(String bccode, String bbcode,  Employee employee) {
		List<BenchScene> benchScenes = null;
		try {
			String jobs = getJobs(employee);
			String sql = "select * from benchscene where ((nvl(bs_caller,' ') <> ' ' and bs_caller in (select distinct pp_caller from "
					+ "(select pp_caller from positionpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 "
					+ "and pp_joid in("+jobs+")) union (select pp_caller from personalpower where (pp_alllist=1 or pp_selflist=1 "
					+ "or pp_jobemployee=1 or pp_see=1) and pp_emid= ?))) or (nvl(bs_caller,' ') = ' ')) and bs_bccode=? and bs_bbcode = ? "
					+ "and nvl(bs_enable,0) <> 0 order by bs_detno asc";
			benchScenes = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<BenchScene>(BenchScene.class), employee.getEm_id(), bccode, bbcode);
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchScenes;
	}
	

	@Override
	@Cacheable(value = "benchscenes", key = "#employee.em_master + '@' + #bccode + #bbcode + #employee.em_id + 'getBenchScenes'",unless="#result==null")
	public List<BenchScene> getSelfBenchScenes(String bccode, String bbcode, Employee employee) {
		
		List<BenchScene> benchScenes = null;
		try {
			benchScenes = getJdbcTemplate().query("SELECT BS_ID,BS_CODE,BS_BCCODE,BS_TITLE,BS_TABLE,BE_BSDETNO BS_DETNO,"
				+ "BE_ISCOUNT BS_ISCOUNT,BS_CONDITION,BS_GROUPBY,BS_ORDERBY,BS_KEYFIELD,BS_SELFFIELD,BS_BATCHSET,BS_FIXCOND,BS_CALLER,BS_BBCODE,BS_BBNAME,BS_ISLIST FROM "
				+ "BENCHSCENE,BENCHSCENEEMPS WHERE BS_CODE = BE_BSCODE AND BS_BCCODE = ?  and bs_bbcode = ? AND BE_EMID = ? and nvl(bs_enable,0) <> 0 ORDER BY BE_BSDETNO ASC", 
				new BeanPropertyRowMapper<BenchScene>(BenchScene.class), bccode, bbcode, employee.getEm_id());
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchScenes;
	}
	
	@Override
	@Cacheable(value = "benchscenes", key = "#employee.em_master + '@' + #bccode + #bbcode + #employee.em_id + 'getBenchScenes'",unless="#result==null")
	public List<BenchScene> getSelfBenchScenesByPower(String bccode, String bbcode, Employee employee) {
		
		List<BenchScene> benchScenes = null;
		try {
			String jobs = getJobs(employee);
			String sql = "SELECT BS_ID,BS_CODE,BS_BCCODE,BS_TITLE,BS_TABLE,BE_BSDETNO BS_DETNO,"
				+ "BE_ISCOUNT BS_ISCOUNT,BS_CONDITION,BS_GROUPBY,BS_ORDERBY,BS_KEYFIELD,BS_SELFFIELD,BS_BATCHSET,BS_FIXCOND,BS_CALLER,BS_BBCODE,BS_BBNAME,BS_ISLIST FROM "
				+ "BENCHSCENE,BENCHSCENEEMPS WHERE BS_CODE = BE_BSCODE and ((nvl(bs_caller,' ') <> ' ' and bs_caller in (select distinct pp_caller from (select pp_caller from positionpower where"
				+ " nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_joid in("+jobs+")) union (select pp_caller from personalpower"
				+ " where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_emid= ?))) or (nvl(bs_caller,' ') = ' ')) "
				+ "AND BS_BCCODE = ?  and bs_bbcode = ? AND BE_EMID = ? and nvl(bs_enable,0) <> 0 ORDER BY BE_BSDETNO ASC";
			benchScenes = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<BenchScene>(BenchScene.class), employee.getEm_id(), bccode, bbcode, employee.getEm_id());
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchScenes;
	}
	
	@Cacheable(value = "benchscene", key = "#sob + '@' + #bscode + 'getBenchScene'",unless="#result==null")
	public BenchScene getBenchScene(String bscode, String sob) {
		BenchScene benchScene = null;
		try {
			benchScene = getJdbcTemplate().queryForObject("select * from benchscene where bs_code=? and nvl(bs_enable,0) <> 0 order by bs_detno asc", 
					new BeanPropertyRowMapper<BenchScene>(BenchScene.class), bscode);
			
			List<BenchSceneGrid> benchSceneGrids = getJdbcTemplate().query("select * from benchscenegrid where sg_bscode=? order by sg_detno asc", 
					new BeanPropertyRowMapper<BenchSceneGrid>(BenchSceneGrid.class), bscode);
			
			benchScene.setBenchSceneGrids(benchSceneGrids);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchScene;
	}
	
	@Override
	@Cacheable(value = "button", key = "#sob + '@' + #bscode + 'getSceneButtons'",unless="#result==null")
	public List<SceneButton> getSceneButtons(String bscode, String sob) {
		List<SceneButton> sceneButtons = null;
		try {
			sceneButtons = getJdbcTemplate().query("select * from benchscenebutton where sb_bscode=? order by sb_detno asc", 
					new BeanPropertyRowMapper<SceneButton>(SceneButton.class), bscode);
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return sceneButtons;
	}
	
	@Override
	@Cacheable(value = "button", key = "#employee.em_master + '@' + #bscode + #employee.em_id + 'getSceneButtonsByPower'",unless="#result==null")
	public List<SceneButton> getSceneButtonsByPower(String bscode, Employee employee) {
		List<SceneButton> sceneButtons = null;
		try {
			String jobs = getJobs(employee);
			sceneButtons = getJdbcTemplate().query("SELECT * FROM BENCHSCENEBUTTON WHERE (EXISTS (SELECT 1 FROM SPECIALPOWER "
					+ "WHERE SP_SSPID IN (SELECT SSP_ID FROM SYSSPECIALPOWER WHERE SSP_VALID=-1 AND SSP_ACTION=SB_SPACTION) AND "
					+ "(SP_JOID IN ("+jobs+") OR SP_EMID = ?)) OR (NOT EXISTS (SELECT 1 FROM SYSSPECIALPOWER WHERE SSP_VALID=-1 AND "
					+ "SSP_ACTION=SB_SPACTION))) AND SB_BSCODE=?  ORDER BY SB_DETNO ASC", 
					new BeanPropertyRowMapper<SceneButton>(SceneButton.class), employee.getEm_id(), bscode);
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return sceneButtons;
	}

	@Override
	@Cacheable(value = "empsrelativesettings", key = "#caller + #kind + #emid + 'getRelativesettings'",unless="#result==null")
	public String getRelativesettings(String caller, String kind, int emid) {
		SqlRowList sl = queryForRowSet("select es_field||es_conditionstr  from  empsrelativesettings  where es_emid=?  and es_pagecaller=? "
				+ "and nvl(es_kind,' ')=?",emid,caller,kind);
		String conditionstr = "";
		while (sl.next()) {
			conditionstr += sl.getString(1) + " and ";
		}
		if ("".equals(conditionstr))
			return null;
		else
			return conditionstr.substring(0, conditionstr.length() - 4);
	}

	@Override
	public List<Map<String, Object>> getSceneGridData(BenchScene benchScene,
			String condition, Employee employee, Integer page, Integer pageSize, String orderby, Boolean jobemployee) {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		String sql = null;
		String sort = benchScene.getBs_orderby();
		if (StringUtils.hasText(orderby))
			sort = orderby;
		try{
			sql = benchScene.getSearchSql(condition, sort, employee, page, pageSize);
			if(jobemployee){
				sql = getSqlWithJobEmployee(employee) + sql;
			}
			SqlRowList rs = queryForRowSet(sql);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (BenchSceneGrid detail : benchScene.getBenchSceneGrids()) {
					String field = detail.getSg_field();
					if (field.contains(" ")) {// column有取别名
						String[] strs = field.split(" ");
						field = strs[strs.length - 1];
					}
					Object value = rs.getObject(field);
					value = value == null || value.equals("null") ? "" : SqlRowList.parseValue(value);
						map.put(field, value);
				}
				datas.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return datas;
	}
	
	@Override
	public List<Map<String, Object>> getSummaryData(BenchScene benchScene, String condition, Boolean jobemployee) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		try{
			String sql = benchScene.getSummarySql(condition);
			if (!StringUtils.hasText(sql))
				return lists;
			if(jobemployee){
				sql = getSqlWithJobEmployee(null) + sql;
			}
			SqlRowList rs = queryForRowSet(sql);
			Map<String, Object> map = null;
			int index = 0;
			if (rs.next()) {
				for (BenchSceneGrid detail : benchScene.getBenchSceneGrids()) {
					if (StringUtils.hasText(detail.getSg_summarytype())) {
						index++;
						map = new HashMap<String, Object>();
						map.put("field", detail.getSg_field());
						map.put("value", rs.getObject(index));
						map.put("type", detail.getSg_summarytype());
						lists.add(map);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return lists;
	}
	
	@Override
	public List<Bench> getAllBenchs(String sob) {
		List<Bench> benchs = new ArrayList<Bench>();
		try{
			benchs = getJdbcTemplate().query("select * from bench order by bc_detno asc", new BeanPropertyRowMapper<Bench>(Bench.class));
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchs;
	}

	@Override
	public List<Bench> getBenchList(String condition,Employee employee) {
		List<Bench> benchs = new ArrayList<Bench>();
		try{
			benchs = getJdbcTemplate().query("select * from bench where " + condition + " order by bc_detno asc", new BeanPropertyRowMapper<Bench>(Bench.class));
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchs;
	}
	
	@Override
	public String getCode(Object benchcode, String name){
		int count = queryForObject("select nvl(max(substr(BS_CODE,-2)),0) from BENCHSCENE where REGEXP_LIKE(BS_CODE,'\\w+[0-9]{5}') and bs_bccode= ?",Integer.class, benchcode);
		
		NumberFormat nf = NumberFormat.getInstance();
		// 设置是否使用分组
		nf.setGroupingUsed(false);
		// 设置最大整数位数
		nf.setMaximumIntegerDigits(2);
		// 设置最小整数位数
		nf.setMinimumIntegerDigits(2);
		// 输出测试语句
		String bccode = String.valueOf(benchcode);
		String Code = bccode.substring(0, bccode.length()-3);
		
		if(!name.equals(Code)){
			bccode = bccode.replace(Code, name);
		}
		String code = bccode + nf.format(count + 1);
		return code;
	}

	@Override
	@Caching(evict = {
		@CacheEvict(value = "benchscenes", key = "#sob + '@' + #bccode + #bbcode + 'getBenchScenes'"),
		@CacheEvict(value = "benchbusiness", allEntries = true)
	})
	public Map<String, Object> copy(Integer id, String sob, Object bccode, Object bbcode, Object bscode, String newtitle) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<String> sqls = new ArrayList<String>();
		try {
			String code = getCode(bccode, sob);
			result.put("bs_code", code);
			
			Integer maxDetno = queryForObject("select nvl(max(bs_detno),0) from benchscene where bs_bccode = ?",Integer.class, bccode);
			sqls.add("INSERT INTO BENCHSCENE(BS_ID,BS_CODE,BS_BCCODE,BS_TITLE,BS_TABLE,BS_DETNO,BS_ISCOUNT,"
					+ "BS_CONDITION,BS_GROUPBY,BS_ORDERBY,BS_KEYFIELD,BS_SELFFIELD,BS_BATCHSET,BS_ENABLE,BS_BBCODE,BS_BBNAME,BS_CALLER,BS_ISLIST) SELECT BENCHSCENE_SEQ.nextval,'"
					+code+"','"+bccode+"','"+newtitle+"',BS_TABLE,"+(maxDetno+1)+",BS_ISCOUNT,BS_CONDITION,"
					+ "BS_GROUPBY,BS_ORDERBY,BS_KEYFIELD,BS_SELFFIELD,BS_BATCHSET,BS_ENABLE,BS_BBCODE,BS_BBNAME,BS_CALLER,BS_ISLIST FROM BENCHSCENE WHERE BS_ID = " + id);
			sqls.add("INSERT INTO BENCHSCENEGRID(SG_ID,SG_BSCODE,SG_DETNO,SG_FIELD,SG_TEXT,SG_WIDTH,SG_TYPE,SG_TEXT_FAN,SG_TEXT_EN,"
					+ "SG_EDITABLE,SG_ISDESKTOP,SG_RENDER,SG_SUMMARYTYPE,SG_TABLE) SELECT BENCHSCENEGRID_SEQ.nextval,'"+code+"',SG_DETNO,"
					+ "SG_FIELD,SG_TEXT,SG_WIDTH,SG_TYPE,SG_TEXT_FAN,SG_TEXT_EN,SG_EDITABLE,SG_ISDESKTOP,SG_RENDER,SG_SUMMARYTYPE,SG_TABLE "
					+ "FROM BENCHSCENEGRID WHERE SG_BSCODE = '"+bscode+"'");
			sqls.add("INSERT INTO BENCHSCENEBUTTON(SB_ID,SB_BSCODE,SB_DETNO,SB_ALIAS,SB_TITLE,SB_CONDITION,SB_REQUESTTYPE,SB_URL,"
					+ "SB_RELATIVECALLER,SB_SPACTION) SELECT BENCHSCENEBUTTON_SEQ.nextval,'"+code+"',SB_DETNO,SB_ALIAS,SB_TITLE,"
					+ "SB_CONDITION,SB_REQUESTTYPE,SB_URL,SB_RELATIVECALLER,SB_SPACTION FROM BENCHSCENEBUTTON WHERE SB_BSCODE = '"+bscode+"'");
			execute(sqls);

			result.put("maxDetno", maxDetno+1);
		} catch (Exception e) {
			BaseUtil.showError("复制失败，错误：" + e.getMessage());
		}
		return result;
	}

	@Override
	public List<BenchSceneGrid> getSetByCaller(String caller) {
		List<BenchSceneGrid> benchSceneGrids = new ArrayList<Bench.BenchSceneGrid>();
		try {
			benchSceneGrids = getJdbcTemplate().query("select dld_detno sg_detno,dld_field sg_field,dld_caption sg_text,dld_width sg_width,"
				+ "dld_fieldtype sg_type,dld_caption_fan dg_text_fan,dld_caption_en dg_text_en,dld_editable sg_editable,dld_render sg_render,"
				+ "dld_summarytype sg_summarytype,dld_table sg_table from Datalist,DatalistDetail where dl_id = dld_dlid and dl_caller=? order by dld_detno asc", 
					new BeanPropertyRowMapper<BenchSceneGrid>(BenchSceneGrid.class), caller);
			
		} catch (EmptyResultDataAccessException e) {
			return benchSceneGrids;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return benchSceneGrids;
	}

	@Override
	public List<JSONObject> getFlowchartConfig(String bccode) {
		String[] fields = new String[]{"ID_", "BC_ID", "TYPE_", "TEXT_", "X_", "Y_", "WIDTH_", "HEIGHT_", "ITEMS_", "X2_", "Y2_", "ARROW1_", "ARROW2_", "BG_COLOR", "COLOR_", "FONT_SIZE", "DOT_", "POINTS_", "TABNAME_"};
		String condition = "bc_id='"+bccode+"'";
		List<JSONObject> result = this.getFieldsJSONDatasByCondition("BENCH_FLOWCHART", fields, condition);
		return result;
	}

	@Override
	@Caching(evict = {
		@CacheEvict(value = "benchbusiness", allEntries = true),
		@CacheEvict(value = "benchscenes", allEntries = true),
	    @CacheEvict(value = "benchscene", key = "#employee.em_master + '@' + #scenecode + 'getBenchScene'"),
	    @CacheEvict(value = "button", allEntries = true)
	})
	public void deleteScene(String caller, Employee employee, Object bccode, Object bbcode, String scenecode) {
		deleteByCondition("BENCHSCENEBUTTON", "sb_bscode = ?",scenecode);
		deleteByCondition("BENCHSCENEGRID", "sg_bscode = ?",scenecode);
		deleteByCondition("BENCHSCENE", "bs_code = ?", scenecode);
		deleteByCondition("BENCHSCENEEMPS", "be_bscode = ?", scenecode);
		deleteByCondition("DATALISTCONFIG", "CALLER_ = ?",scenecode);
		deleteByCondition("DATALISTCONFIG$EMP", "CALLER$EMP = ?",scenecode);
		logger.delete(caller, "bs_code", scenecode);
		
	}

	@Override
	@CacheEvict(value = "benchscenes", key = "#employee.em_master + '@' + #bccode + #bbcode + #employee.em_id + 'getBenchScenes'")
	public void clearScene(Employee employee, String bccode, String bbcode) {
		
	}
	
	@Override
	public String getSqlWithJobEmployee(Employee employee) {
		if(employee==null){
			employee = SystemSession.getUser();
		}
		
		Integer jobId = employee.getEm_defaulthsid();
		
		String jobIds = null;
		if(jobId!=null){
			jobIds = String.valueOf(jobId);
		}
		for (EmpsJobs empsJob : employee.getEmpsJobs()) {
			if(empsJob.getJob_id()!=null){
				jobIds += "," + empsJob.getJob_id();
			}
		}
		String sql = "with " + Constant.TEMP_TABLE_NAME + " as (select hj_joid,hj_em_id,hj_em_code,hj_em_name from  hrjobemployees where nvl(hj_joid,0) in ("+jobIds+") union all select " + employee.getEm_defaulthsid()+ "," + employee.getEm_id() + ",'"+employee.getEm_code()+"','"+employee.getEm_name()+"' from dual) ";
		return sql;
	}


	@Override
	public boolean isSelfShow(String bccode, String bbcode, Employee employee) {
		String jobs = getJobs(employee);
		return getCountByCondition("BenchSceneEmps inner join BenchScene on be_bscode = bs_code", "((nvl(bs_caller,' ') <> ' ' and bs_caller in "
			+ "(select distinct pp_caller from (select pp_caller from positionpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_joid in("+jobs+")) "
			+ "union (select pp_caller from personalpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_emid= " + employee.getEm_id() + "))) or (nvl(bs_caller,' ') = ' ')) and "
			+ "be_bccode = '" + bccode + "' and bs_bbcode = '" + bbcode + "' and be_emid = " + employee.getEm_id() + " and be_iscount<>0 and nvl(bs_enable,0) <> 0")>1;
	}

	@Override
	public boolean isShow(String bccode, String bbcode, Employee employee) {
		String jobs = getJobs(employee);
		return getCountByCondition("BenchScene", "((nvl(bs_caller,' ') <> ' ' and bs_caller in (select distinct pp_caller from "
			+ "(select pp_caller from positionpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_joid in("+jobs+")) "
			+ "union (select pp_caller from personalpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_emid= " + employee.getEm_id() + "))) or (nvl(bs_caller,' ') = ' ')) and "
			+ "bs_bccode = '" + bccode + "' and bs_bbcode = '" + bbcode + "' and bs_iscount<>0 and nvl(bs_enable,0) <> 0")>1;
	}

	/**
	 * 搜索工作台场景、场景按钮
	 **/
	@Override
	public List<Map<String, Object>> searchBenchScene(String benchcode,
			Employee employee, String search, boolean noControl) {
		
		List<Map<String, Object>> businesses = new ArrayList<Map<String, Object>>();
		String powerCondition = "", btnPowerCondition = "";
		if (!noControl) {
			String jobs = getJobs(employee);
			powerCondition = " and ((nvl(bs_caller,' ') <> ' ' and bs_caller in (select distinct pp_caller from (select pp_caller from positionpower where"
					+ " nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_joid in("+jobs+")) union (select pp_caller from personalpower"
					+ " where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_emid= "+employee.getEm_id()+"))) or (nvl(bs_caller,' ') = ' '))";
			
			btnPowerCondition = " AND (EXISTS (SELECT 1 FROM SPECIALPOWER WHERE SP_SSPID IN (SELECT SSP_ID FROM SYSSPECIALPOWER WHERE SSP_VALID=-1 AND SSP_ACTION=SB_SPACTION) AND "
					+ "(SP_JOID IN ("+jobs+") OR SP_EMID = "+employee.getEm_id()+")) OR (NOT EXISTS (SELECT 1 FROM SYSSPECIALPOWER WHERE SSP_VALID=-1 AND "
					+ "SSP_ACTION=SB_SPACTION)))";
		}
		
		
		String bussql = "SELECT BS_BBCODE CODE,BC_TITLE||'-'||BB_NAME TITLE FROM BENCH,BENCHBUSINESS,BENCHSCENE,BENCHSCENEEMPS WHERE BS_CODE = BE_BSCODE AND BS_BCCODE = BC_CODE AND BS_BBCODE = BB_CODE AND (BS_TITLE LIKE '%"+search+"%' OR EXISTS(SELECT 1 FROM BENCHSCENEBUTTON WHERE SB_BSCODE = BS_CODE AND SB_TITLE LIKE '%"+search+"%')) "+powerCondition+" AND BS_BCCODE = ? AND BE_EMID = ? AND BB_STATUSCODE <> 'DISABLE' AND NVL(BS_ENABLE,0) <> 0 GROUP BY BS_BBCODE,BC_TITLE,BB_NAME,BB_DETNO ORDER BY BB_DETNO ASC";
		
		SqlRowList rs = queryForRowSet(bussql, benchcode, employee.getEm_id());
		if (!rs.hasNext()) {
			bussql = "SELECT BS_BBCODE CODE,BC_TITLE||'-'||BB_NAME TITLE FROM BENCH,BENCHBUSINESS,BENCHSCENE WHERE BS_BCCODE = BC_CODE AND BS_BBCODE = BB_CODE AND (BS_TITLE LIKE '%"+search+"%' OR EXISTS(SELECT 1 FROM BENCHSCENEBUTTON WHERE SB_BSCODE = BS_CODE AND SB_TITLE LIKE '%"+search+"%')) "+powerCondition+" AND BS_BCCODE = ? AND BB_STATUSCODE <> 'DISABLE' AND NVL(BS_ENABLE,0) <> 0 GROUP BY BS_BBCODE,BC_TITLE,BB_NAME,BB_DETNO ORDER BY BB_DETNO ASC";
			rs = queryForRowSet(bussql, benchcode);
		}
		
		String scenesql = "SELECT BS_CODE CODE,BS_TITLE TITLE FROM BENCHSCENE LEFT JOIN BENCHSCENEEMPS ON BS_CODE = BE_BSCODE AND BE_EMID = ? WHERE BS_TITLE LIKE '%"+search+"%' "+powerCondition+" AND  BS_BCCODE = ? AND BS_BBCODE = ? order by BE_BSDETNO,BS_DETNO";
		String btnsql = "select BS_CODE,BS_CODE||SB_DETNO CODE,BS_TITLE||'-'||SB_TITLE TITLE from BENCHSCENEBUTTON,BENCHSCENE where SB_BSCODE = BS_CODE and SB_TITLE LIKE '%"+search+"%' "+powerCondition+btnPowerCondition+" AND  BS_BCCODE = ? AND BS_BBCODE =? order by SB_DETNO";
		String baseUrl = "jsps/common/bench/bench.jsp?bench=" + benchcode;
		String bencnTitle = queryForObject("select bc_title from Bench where bc_code = ?", String.class, benchcode);
		while (rs.next()) {
			Map<String, Object> business = new HashMap<String, Object>();
			String bbcode = rs.getGeneralString("code");
			business.put("code", bbcode);
			business.put("title", rs.getGeneralString("title"));
			List<Map<String, Object>> scenes = new ArrayList<Map<String, Object>>();
			
			SqlRowList rs1 = queryForRowSet(scenesql, employee.getEm_id(), benchcode, bbcode);
			while (rs1.next()) {
				Map<String, Object> scene = new HashMap<String, Object>();
				String bscode = rs1.getGeneralString("code");
				scene.put("type", "scene");
				scene.put("code", bscode);
				scene.put("title", rs1.getGeneralString("title"));
				scene.put("business", bbcode);
				scene.put("scene", bscode);
				String url = baseUrl + "&business="+bbcode+"&scene="+bscode;
				scene.put("url", url);
				scene.put("benchcode", benchcode);
				scene.put("benchtitle", bencnTitle);
				scenes.add(scene);
			}
			rs1 = queryForRowSet(btnsql, benchcode, bbcode);
			while (rs1.next()) {
				Map<String, Object> scene = new HashMap<String, Object>();
				String bscode = rs1.getGeneralString("bs_code");
				scene.put("type", "scene");
				scene.put("code", rs1.getGeneralString("code"));
				scene.put("title", rs1.getGeneralString("title"));
				scene.put("business", bbcode);
				scene.put("scene", bscode);
				String url = baseUrl + "&business="+bbcode+"&scene="+bscode;
				scene.put("url", url);
				scene.put("benchcode", benchcode);
				scene.put("benchtitle", bencnTitle);
				scenes.add(scene);
			}
			business.put("items", scenes);
			businesses.add(business);
		}
		
		return businesses;
	}

	/**
	 * 搜索工作台按钮
	 **/
	@Override
	public List<Map<String, Object>> searchBenchButton(String benchcode,
			Employee employee, String search, boolean noControl) {
		
		List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>(3);
		
		String bencnTitle = queryForObject("select bc_title from Bench where bc_code = ?", String.class, benchcode);
		Map<String, Object> group = new HashMap<String, Object>();
		List<Map<String, Object>> btns = getBtnItems(employee, benchcode, search, "基础资料", noControl);
		if (btns.size()>0) {
			group.put("code", "basicData_" + benchcode);
			group.put("title", bencnTitle + "-基础资料");
			group.put("items", btns);
			groups.add(group);
		}
		
		btns = getBtnItems(employee, benchcode, search, "业务制单", noControl);
		if (btns.size()>0) {
			group = new HashMap<String, Object>();
			group.put("code", "makeOrder_" + benchcode);
			group.put("title", bencnTitle + "-业务制单");
			group.put("items", btns);
			groups.add(group);
		}
		
		btns = getBtnItems(employee, benchcode, search, "更多操作", noControl);
		if (btns.size()>0) {
			group = new HashMap<String, Object>();
			group.put("code", "moreOperat_" + benchcode);
			group.put("title", bencnTitle + "-更多操作");
			group.put("items", btns);
			groups.add(group);
		}
		
		return groups;
	}
	
	/**
	 * 搜索工作台按钮（分组）
	 **/
	private List<Map<String, Object>> getBtnItems(Employee employee, String benchcode, String search, String type, boolean noControl){
		
		String powerCondition = "";
		if (!noControl) {
			String jobs = getJobs(employee);
			powerCondition = " and (bb_caller in (select distinct pp_caller from positionpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_joid in(" + jobs + "))  or bb_caller in (select distinct pp_caller from personalpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_emid=" + employee.getEm_id()+"))";
		}
		String btnsql = "select BB_CODE,case when nvl(BB_BUSINGROUP,' ') = ' ' then BB_TEXT else BB_BUSINGROUP||'-'||BB_TEXT end BB_TEXT,REPLACE(BB_LISTURL,'''','\\''') BB_LISTURL,REPLACE(BB_URL,'''','\\''') BB_URL from BENCHBUTTON where (BB_TEXT like '%"+search+"%' OR BB_BUSINGROUP like '%"+search+"%') " + powerCondition + " and nvl(bb_group,'业务制单') = ? and bb_bccode = ? ";
		List<Map<String, Object>> btns = new ArrayList<Map<String, Object>>();
		SqlRowList rs = queryForRowSet(btnsql, type, benchcode);
		while (rs.next()) {
			Map<String, Object> btn = new HashMap<String, Object>();
			btn.put("type", "button");
			btn.put("code", rs.getGeneralString("bb_code"));
			btn.put("title", rs.getGeneralString("bb_text"));
			btn.put("url", rs.getGeneralString("bb_listurl"));
			btn.put("addurl", rs.getGeneralString("bb_url"));
			btns.add(btn);
		}
		return btns;
	}
	
	/**
	 * 判断对应工作台、业务、场景是否存在
	 **/
	@Override
	public boolean isExist(boolean noControl, Employee employee, String bench, String business, String scene) {
		try {
			String condition = "bs_bccode = '"+bench+"' and bs_bbcode = '"+business+"' and bs_code = '"+scene+"' and nvl(bc_used,0) <> 0 and bb_statuscode <> 'DISABLE' and nvl(bs_enable,0) <> 0";
			//非管理员，进行权限判断
			if (!noControl) {
				String jobs = getJobs(employee);
				condition = "((nvl(bs_caller,' ') <> ' ' and bs_caller in (select distinct pp_caller from (select pp_caller from positionpower where"
						+ " nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_joid in("+jobs+")) union (select pp_caller from personalpower"
						+ " where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_emid= " + employee.getEm_id() +"))) or (nvl(bs_caller,' ') = ' ')) and " + condition;
			}
			return checkIf("bench inner join benchscene on bc_code = bs_bccode inner join benchbusiness on bs_bbcode = bb_code", condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
