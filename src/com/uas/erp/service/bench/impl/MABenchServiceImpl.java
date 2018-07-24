package com.uas.erp.service.bench.impl;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.BenchDao;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Bench;
import com.uas.erp.model.Bench.BenchButton;
import com.uas.erp.model.Bench.BenchSceneGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.Bench.BenchScene;
import com.uas.erp.model.Master;
import com.uas.erp.service.bench.MABenchService;

@Service("maBenchService")
public class MABenchServiceImpl implements MABenchService {
	
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private BenchDao benchDao;
	@Autowired
	private HandlerService handlerService;
	
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
	public List<JSONTree> getBenchTree(Boolean isRoot) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		try {
			boolean bool = baseDao.checkByCondition("Bench", "nvl(bc_used,0) = -1");
			if (bool) {
				return null;
			}
			if (isRoot) {
				JSONTree root = new JSONTree();
				root.setCls("x-tree-cls-root");
				root.setParentId(0);
				root.setLeaf(false);
				root.setText("工作台");
				root.setQtip("工作台");
				root.setId("bench");
				root.setDetno(0);
				tree.add(root);
			} else {
				List<Bench> benchs = baseDao.getJdbcTemplate().query("select * from bench order by bc_detno asc",new BeanPropertyRowMapper<Bench>(Bench.class));
				
				for (Bench bench : benchs) {
					tree.add(new JSONTree(bench));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return tree;
	}

	@Override
	public List<JSONTree> searchBenchTree(String search) {
		List<JSONTree> tree = null;
		try {
			List<Bench> benchs = baseDao.getJdbcTemplate().query("select * from bench where bc_title like '%" + search+ "%' order by bc_detno asc",
					new BeanPropertyRowMapper<Bench>(Bench.class));
			if (CollectionUtils.isNotEmpty(benchs)) {
				tree = new ArrayList<JSONTree>();
				JSONTree root = new JSONTree();
				root.setCls("x-tree-cls-root");
				root.setParentId(0);
				root.setLeaf(false);
				root.setText("工作台");
				root.setQtip("工作台");
				root.setId("bench");
				root.setDetno(0);
				List<JSONTree> children = new ArrayList<JSONTree>();
				for (Bench bench : benchs) {
					children.add(new JSONTree(bench));
				}
				root.setChildren(children);
				tree.add(root);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return tree;
	}

	@Override
	public List<Map<String, Object>> getActionsData(String caller, String alias) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		SqlRowList rs = null;
		String con = "";
		if (StringUtil.hasText(alias)) {
			con = " and SSP_BUTTON = '"+alias+"'";
		}
		if (StringUtil.hasText(caller)) {
			rs = baseDao.queryForRowSet("select ssp_id,ssp_caller,ssp_action,ssp_desc,ssp_valid from SYSSPECIALPOWER where (ssp_caller = ? or substr(ssp_action,1,instr(ssp_action,'@')-1) = ?) "+con,caller,caller);
			if (rs.hasNext()) {
				while (rs.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("ssp_id", rs.getGeneralInt("ssp_id"));
					map.put("ssp_caller", rs.getString("ssp_caller"));
					map.put("ssp_action", rs.getString("ssp_action"));
					map.put("ssp_desc", rs.getString("ssp_desc"));
					map.put("ssp_valid", rs.getGeneralInt("ssp_valid"));
					data.add(map);
				}
			}else {
				rs = baseDao.queryForRowSet("select fo_dealurl,fo_title from form where nvl(fo_dealurl,' ')<> ' ' and fo_button4add not like '%#%' and fo_caller = ?",caller);
				while (rs.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					String action = "";
					map.put("ssp_id", 0);
					map.put("ssp_caller", caller);
					if (rs.getString("fo_dealurl").startsWith("/")) {
						action = caller+"@/ERP"+rs.getString("fo_dealurl");
					}else{
						action = caller+"@/ERP/"+rs.getString("fo_dealurl");
					}
					map.put("ssp_action", action);
					map.put("ssp_desc", rs.getString("fo_title"));
					map.put("ssp_valid", 0);
					data.add(map);
				}
			}
		}else {
			rs = baseDao.queryForRowSet("select ssp_id,ssp_caller,ssp_action,ssp_desc,ssp_valid from SYSSPECIALPOWER where 1=1"+con);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ssp_id", rs.getGeneralInt("ssp_id"));
				map.put("ssp_caller", rs.getString("ssp_caller"));
				map.put("ssp_action", rs.getString("ssp_action"));
				map.put("ssp_desc", rs.getString("ssp_desc"));
				map.put("ssp_valid", rs.getGeneralInt("ssp_valid"));
				data.add(map);
			}
		}
		
		return data;
	}

	@Override
	public List<Bench> getBenchList(String condition) {
		Employee employee = SystemSession.getUser();
		if (condition == null) {
			return benchDao.getAllBenchs(employee.getEm_master());
		} else {
			return benchDao.getBenchList(condition, employee);
		}
	}

	@Override
	public Map<String, Object> getBenchScenes(String bccode) {
		Employee employee = SystemSession.getUser();

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Integer maxDetno = 1;
		List<BenchScene> benchScenes = benchDao.getBenchScenes(bccode,employee.getEm_master());
		for (BenchScene benchScene : benchScenes) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("bstitle", benchScene.getBs_title());
			map.put("bscode", benchScene.getBs_code());
			list.add(map);
		}
		result.put("scenes", list);
		maxDetno = baseDao.queryForObject("select nvl(max(bs_detno),0) from benchscene where bs_bccode = ?",Integer.class, bccode);
		result.put("maxDetno", maxDetno);
		return result;
	}

	@Override
	public void saveBench(String bench) {
		Map<Object, Object> formStore = BaseUtil.parseFormStoreToMap(bench);

		if (StringUtil.hasText(formStore.get("bc_code"))) {
			updateBench(formStore);
		} else {
			saveBench(formStore);
		}
	}
	
	@CacheEvict(value = "tree", allEntries = true)
	private void saveBench(Map<Object, Object> formStore) {
		boolean bool = baseDao.checkIf("Bench","bc_title = '" + formStore.get("bc_title") + "'");
		if (bool) {
			BaseUtil.showError("此工作台标题已存在！");
		}
		try {
			int id = baseDao.getSeqId("BENCH_SEQ");
			Employee employee = SystemSession.getUser();
			int count = baseDao.getJdbcTemplate().queryForObject("select nvl(max(substr(BC_CODE,-3)),0) from bench",Integer.class);
			NumberFormat nf = NumberFormat.getInstance();
			// 设置是否使用分组
			nf.setGroupingUsed(false);
			// 设置最大整数位数
			nf.setMaximumIntegerDigits(3);
			// 设置最小整数位数
			nf.setMinimumIntegerDigits(3);
			// 输出测试语句
			String code = employee.getEm_master() + nf.format(count + 1);
			formStore.put("bc_id", id);
			formStore.put("bc_code", code);
			baseDao.execute(SqlUtil.getInsertSqlByFormStore(formStore, "Bench", new String[] {}, new Object[] {}));
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}
	
	@Caching(evict = {
		@CacheEvict(value = "tree", allEntries = true),
		@CacheEvict(value = "bench", key = "#sob + '@' + #form.get(\"bc_code\") + 'getBench'")
	})
	private void updateBench(Map<Object, Object> form) {
		boolean bool = baseDao.checkIf("Bench","bc_title = '" + form.get("bc_title") + "' and bc_id<>" + form.get("bc_id"));
		if (bool) {
			BaseUtil.showError("此工作台标题已存在！");
		}
		try {
			baseDao.execute(SqlUtil.getUpdateSqlByFormStore(form,"Bench", "bc_id"));
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}

	@Override
	@Caching(evict = {
		@CacheEvict(value = "tree", allEntries = true),
		@CacheEvict(value = "bench", key = "#sob + '@' + #benchcode + 'getBench'")
	})
	public void deleteBench(String benchcode) {
		Employee employee = SystemSession.getUser();
		List<BenchScene> benchScenes = benchDao.getBenchScenes(benchcode,employee.getEm_master());
		if (benchScenes != null) {
			for (BenchScene benchScene : benchScenes) {
				benchDao.deleteScene("SceneSet", employee, benchcode, benchScene.getBs_bbcode(), benchScene.getBs_code());
			}
		}
		baseDao.deleteByCondition("BENCHBUTTON", "BB_BCCODE = ?", benchcode);
		baseDao.deleteByCondition("BENCH_FLOWCHART", "BC_ID = ?", benchcode);
		baseDao.deleteByCondition("BENCH", "BC_CODE = ?", benchcode);

		baseDao.deleteByCondition("BENCHBUSINESSEMPS", "BBE_BCCODE = ?", benchcode);
	}

	@Override
	public List<BenchButton> getBenchButtons(String benchcode) {
		return benchDao.getBenchButtons(benchcode);
	}

	@Override
	@CacheEvict(value = "button", allEntries = true)
	public void saveBenchButtons(String benchButtons, String sob, String bccode) {
		List<Map<Object, Object>> BenchButtons = BaseUtil.parseGridStoreToMaps(benchButtons);
		List<String> sqls = new ArrayList<String>();
		int count = baseDao.getJdbcTemplate().queryForObject("select nvl(max(substr(BB_CODE,-2)),0) from BENCHBUTTON where bb_bccode= ?",Integer.class, bccode);
		try {
			for (Map<Object, Object> map : BenchButtons) {
				if (StringUtil.hasText(map.get("bb_code"))) {
					sqls.add(SqlUtil.getUpdateSqlByFormStore(map,"BENCHBUTTON", "bb_id"));
				} else {
					map.put("bb_id", baseDao.getSeqId("BENCHBUTTON_SEQ"));
					NumberFormat nf = NumberFormat.getInstance();
					// 设置是否使用分组
					nf.setGroupingUsed(false);
					// 设置最大整数位数
					nf.setMaximumIntegerDigits(2);
					// 设置最小整数位数
					nf.setMinimumIntegerDigits(2);
					// 输出测试语句
					Master master = SystemSession.getUser().getCurrentMaster();
					String name = master.getMa_name();
					String Code = bccode.substring(0, bccode.length()-3);
					
					if(!name.equals(Code)){
						bccode = bccode.replace(Code, name);
					}
					String code = "BTN_" + bccode + nf.format(++count);
					
					map.put("bb_code", code);
					sqls.add(SqlUtil.getInsertSqlByFormStore(map,"BENCHBUTTON", new String[] {}, new Object[] {}));
				}
			}
			baseDao.execute(sqls);
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}

	@Override
	@CacheEvict(value = "button", allEntries = true)
	public void deleteBenchButtons(String ids, String sob, String bccode) {
		try {
			baseDao.deleteByCondition("BENCHBUTTON", "bb_id in (" + ids + ")");
		} catch (Exception e) {
			BaseUtil.showError("删除失败，错误：" + e.getMessage());
		}
	}
	
	private void isRepeat(Map<Object, Object> form, boolean isUpdate){
		Object benchcode = form.get("bs_bccode");
		Object id = "";
		if(isUpdate){
			id = form.get("bs_id");
		}
		
		String condition = "bs_title = '" + form.get("bs_title") + "' and bs_bccode ='" + benchcode + "' and bs_bbcode = '" + form.get("bs_bbcode")+"'"+(isUpdate?" and bs_id <> "+id:"");
		boolean bool = baseDao.checkIf("BenchScene", condition);
		if (bool) {
			BaseUtil.showError("场景标题重复！");
		}
		
		condition = "bs_code = '" + form.get("bs_code") + "'"+(isUpdate?" and bs_id <> "+id:"");
		bool = baseDao.checkIf("BenchScene", condition);
		if (bool) {
			BaseUtil.showError("场景编号重复！");
		}
		
	}

	@Override
	@Caching(evict = {
		@CacheEvict(value = "benchscenes", key = "#sob + '@' + #form.get(\"bs_bccode\") + #form.get(\"bs_bbcode\") + 'getBenchScenes'"),
		@CacheEvict(value = "benchbusiness", allEntries = true)
	})
	public Map<String, Object> saveScene(String caller, Map<Object, Object> form,String param, String param1,String sob) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		List<Map<Object, Object>> grid1 = BaseUtil.parseGridStoreToMaps(param1);
		List<String> sqls = new ArrayList<String>();
		
		isRepeat(form, false);
		
		try {
			int id = baseDao.getSeqId("BENCHSCENE_SEQ");
			form.put("bs_id", id);
			Object benchcode = form.get("bs_bccode");
			Object code = form.get("bs_code");
			
			//编号为空自动生成
			if (!StringUtil.hasText(code)) {
				Master master = SystemSession.getUser().getCurrentMaster();
				code = benchDao.getCode(benchcode, master.getMa_name());
			}
			
			form.put("bs_code", code);
			result.put("bs_code", code);
			sqls.add(SqlUtil.getInsertSqlByFormStore(form, "BENCHSCENE",new String[] {}, new Object[] {}));
			for (Map<Object, Object> map : grid) {
				map.put("sg_id", baseDao.getSeqId("BENCHSCENEGRID_SEQ"));
				map.put("sg_bscode", code);
			}
			sqls.addAll(SqlUtil.getInsertSqlbyGridStore(grid, "BENCHSCENEGRID"));

			for (Map<Object, Object> map : grid1) {
				map.put("sb_id", baseDao.getSeqId("BENCHSCENEBUTTON_SEQ"));
				map.put("sb_bscode", code);
				getSPAction(map);
			}
			sqls.addAll(SqlUtil.getInsertSqlbyGridStore(grid1,"BENCHSCENEBUTTON"));

			baseDao.execute(sqls);

			baseDao.logger.save(caller, "bs_code", code);

			Integer index = baseDao.queryForObject("select rn from (select bs_id,rownum rn from (select bs_id from benchscene,benchbusiness where bs_bbcode = bb_code and bs_bccode = ? order by bb_detno,bs_detno)) where bs_id = ?",
							Integer.class, benchcode, id);
			result.put("index", index);
			Integer maxDetno = baseDao.queryForObject("select nvl(max(bs_detno),0) from benchscene where bs_bccode = ?",Integer.class, benchcode);
			result.put("maxDetno", maxDetno);
		} catch (EmptyResultDataAccessException e) {
			result.put("index", 0);
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
		return result;
	}

	@Override
	@Caching(evict = {
		@CacheEvict(value = "benchbusiness", allEntries = true),
		@CacheEvict(value = "benchscenes", allEntries = true),
		@CacheEvict(value = "benchscene", key = "#employee.em_master + '@' + #form.get(\"bs_code\") + 'getBenchScene'"),
		@CacheEvict(value = "button", allEntries = true)
	})
	public Map<String, Object> updateScene(String caller, Employee employee, Map<Object, Object> form,
			String param, String param1, String param2, String param3) {

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<Object, Object>> added = BaseUtil.parseGridStoreToMaps(param);
		List<Map<Object, Object>> updated = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> deleted = BaseUtil.parseGridStoreToMaps(param2);
		List<Map<Object, Object>> grid1 = BaseUtil.parseGridStoreToMaps(param3);
		List<String> sqls = new ArrayList<String>();

		Object id = form.get("bs_id");
		
		isRepeat(form, true);
		
		Object benchcode = form.get("bs_bccode");
		
		try {
			Integer from = baseDao.queryForObject("select rn from (select bs_id,rownum rn from (select bs_id from benchscene,benchbusiness where bs_bbcode = bb_code and bs_bccode = ? order by bb_detno,bs_detno)) where bs_id = ?",
							Integer.class, benchcode, id);
			result.put("fromIndex", from);

			Object code = form.get("bs_code");
			
			//编号为空自动生成
			if(!StringUtil.hasText(code)){
				Master master = SystemSession.getUser().getCurrentMaster();
				code = benchDao.getCode(benchcode, master.getMa_name());
				form.put("bs_code", code);
			}
			
			sqls.add(SqlUtil.getUpdateSqlByFormStore(form, "BENCHSCENE","bs_id"));

			for (Map<Object, Object> map : added) {
				map.put("sg_id", baseDao.getSeqId("BENCHSCENEGRID_SEQ"));
				map.put("sg_bscode", code);
			}
			sqls.addAll(SqlUtil.getInsertSqlbyGridStore(added, "BENCHSCENEGRID"));

			sqls.addAll(SqlUtil.getUpdateSqlbyGridStore(updated,"BENCHSCENEGRID", "sg_id"));

			for (Map<Object, Object> map : deleted) {
				sqls.add("DELETE FROM BENCHSCENEGRID WHERE SG_ID = " + map.get("sg_id"));
			}

			for (Map<Object, Object> map : grid1) {
				getSPAction(map);
				if (StringUtil.hasText(map.get("sb_id"))&& !"0".equals(map.get("sb_id"))&& Integer.parseInt(map.get("sb_id").toString()) != 0) {
					sqls.add(SqlUtil.getUpdateSqlByFormStore(map,"BENCHSCENEBUTTON", "sb_id"));
				} else {
					map.put("sb_id", baseDao.getSeqId("BENCHSCENEBUTTON_SEQ"));
					map.put("sb_bscode", code);
					sqls.add(SqlUtil.getInsertSqlByFormStore(map,"BENCHSCENEBUTTON", new String[] {},new Object[] {}));
				}
			}
			
			Object oldCode = baseDao.getFieldDataByCondition("BENCHSCENE", "bs_code", "bs_id = " + id);
			if (!oldCode.equals(code)) {
				sqls.add("update BENCHSCENEGRID set sg_bscode ='"+code+"' where sg_bscode='"+oldCode+"'");
				sqls.add("update BENCHSCENEBUTTON set sb_bscode ='"+code+"' where sb_bscode='"+oldCode+"'");
				result.put("bs_code", code);
			}
			
			baseDao.execute(sqls);

			// 记录日志
			baseDao.logger.update(caller, "bs_code", code);

			Integer to = baseDao.queryForObject(
							"select rn from (select bs_id,rownum rn from (select bs_id from benchscene,benchbusiness where bs_bbcode = bb_code and bs_bccode = ? order by bb_detno,bs_detno)) where bs_id = ?",
							Integer.class, benchcode, id);
			result.put("toIndex", to);
			Integer maxDetno = baseDao.queryForObject("select nvl(max(bs_detno),0) from benchscene where bs_bccode = ?",Integer.class, benchcode);
			result.put("maxDetno", maxDetno);

		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
		return result;
	}

	@Override
	public void deleteScene(String caller, String scenecode) {
		try {
			Employee employee = SystemSession.getUser();
			Object [] scene = baseDao.getFieldsDataByCondition("BENCHSCENE", new String[] {"bs_bccode","bs_bbcode"}, "bs_code = '" +scenecode + "'");
			benchDao.deleteScene(caller, employee, scene[0], scene[1], scenecode);
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("删除失败，错误：" + e.getMessage());
		}

	}

	@Override
	@CacheEvict(value = "combo", key="#caller + #sob + 'getComboxsByCaller'")
	public String resetCombo(String caller, String field, String sob) {
		BenchScene benchScene = benchDao.getBenchScene(caller, SpObserver.getSp());
		if (benchScene != null) {
			int count = baseDao.getCountByCondition("DataListCombo","dlc_caller='" + caller + "' and dlc_fieldname='" + field+ "' and dlc_value<>dlc_display");
			if (count > 0) {
				return "该字段显示值与实际值不一致，不允许设置，请联系管理员.";
			}
			// 关联列表
			/*int count1 = baseDao.getCountByCondition("DataListCombo","dlc_caller='" + benchScene.getBs_relative()+ "' and dlc_fieldname='" + field + "'");
			if (count1 > 0) {
				baseDao.deleteByCondition("DataListCombo", "dlc_caller='" + caller + "' and dlc_fieldname='" + field + "'");
				String sql = "insert into datalistcombo(dlc_id,dlc_value,dlc_value_en,dlc_value_tw,dlc_detno,dlc_caller,dlc_fieldname,dlc_display) "
						+ "select datalistcombo_seq.nextval,dlc_value,dlc_value_en,dlc_value_tw,dlc_detno,'"
						+ caller
						+ "','"
						+ field
						+ "',dlc_display from (select distinct "
						+ "dlc_value,dlc_value_en,dlc_value_tw,dlc_detno,dlc_display"
						+ " from DataListCombo where dlc_caller='"
						+ benchScene.getBs_relative()
						+ "' and dlc_fieldname='"
						+ field + "')";♣
				baseDao.execute(sql);
				return null;
			}*/
			count = baseDao.getCount("select count(1) from (select distinct " + field + " v from " + benchScene.getBs_table() + ")");
			if (count == 0) {
				return "该字段暂时无数据，不允许设置，请联系管理员.";
			} else if (count > 50) {
				return "该字段数据过多(超过50条上限)，不允许设置，请联系管理员.";
			}
			String sql = "insert into datalistcombo(dlc_id,dlc_value,dlc_value_en,dlc_value_tw,dlc_caller,dlc_fieldname,dlc_display) select datalistcombo_seq.nextval,v,v,v,'"
					+ caller
					+ "','"
					+ field
					+ "',v from (select distinct "
					+ field
					+ " v from "
					+  benchScene.getBs_table()
					+ " where "
					+ field
					+ " not in(select dlc_value from datalistcombo where dlc_caller='"
					+ caller
					+ "' and datalistcombo.dlc_fieldname='"
					+ field
					+ "'))";
			baseDao.execute(sql);
		}
		return null;
	}

	@Override
	public Map<String, Object> copy(Integer id, String newtitle) {
		Object[] benchcode = baseDao.getFieldsDataByCondition("BenchScene", "bs_bccode,bs_code,bs_bbcode", "bs_id="+id);
		boolean bool = baseDao.checkIf("BenchScene","bs_title = '" + newtitle + "' and bs_bccode ='" + benchcode[0] + "' and bs_bbcode = '" + benchcode[2] + "'");
		if (bool) {
			BaseUtil.showError("场景标题重复！");
		}
		return benchDao.copy(id, SystemSession.getUser().getEm_master(), benchcode[0], benchcode[2], benchcode[1], newtitle);
	}
	
	private void getSPAction(Map<Object, Object> map){
		Object rcaller = map.get("sb_relativecaller");
		Object sburl = map.get("sb_url");
		SqlRowList rs = null;
		
		if (StringUtil.hasText(rcaller)) {
			if("page".equals(map.get("sb_requesttype").toString())){
				if (StringUtil.hasText(map.get("sb_alias"))) {
					rs = baseDao.queryForRowSet("select ssp_action from sysspecialpower where ssp_caller = ? and ssp_button = ?",rcaller,map.get("sb_alias"));
					if (rs.next()) {
						map.put("sb_spaction", rs.getString("ssp_action"));
					}
				}else{
					rs = baseDao.queryForRowSet("select ssp_action from (select ssp_caller,ssp_action,substr(ssp_action,"
							+ "instr(ssp_action,'@',1)+6) action from sysspecialpower) inner join form on ssp_caller = fo_caller and action = fo_dealurl where ssp_caller = ?",rcaller);
					if (rs.next()) {
						map.put("sb_spaction", rs.getString("ssp_action"));
					}
				}
			}else if ("action".equals(map.get("sb_requesttype").toString())&&StringUtil.hasText(sburl)) {
				rs = baseDao.queryForRowSet("select ssp_action from sysspecialpower where ssp_caller = ? and substr(ssp_action,instr(ssp_action,'@',1)+6) = ?",rcaller,sburl);
				if (rs.next()) {
					map.put("sb_spaction", rs.getString("ssp_action"));
				}
			}
		}
	}
	
	@Override
	public Map<String, Object> getSelfBusiness(String benchcode, boolean noControl) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		Employee employee = SystemSession.getUser();
		String sql = "";
		boolean self = baseDao.checkIf("benchbusinessemps", "bbe_bccode = '"+benchcode+"' and bbe_emid = "+employee.getEm_id());
		if(noControl){
			sql = "select bb_code,bb_name,bbe_bbdetno,bbe_bbcode from(select bb_code,bb_name,bb_detno from benchscene,benchbusiness where bs_bbcode = bb_code"
				+ " and bs_bccode=? and bb_statuscode <> 'DISABLE' and nvl(bs_enable,0) <> 0 group by bb_name,bb_detno,bb_code order by bb_detno,bb_code asc) left join "
				+ "benchbusinessemps on bb_code = bbe_bbcode and bbe_emid = ? order by bbe_bbdetno,bb_detno,bb_code";
			SqlRowList rs = baseDao.queryForRowSet(sql, benchcode, employee.getEm_id());
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("bbcode", rs.getGeneralString("bb_code"));
				map.put("bbname", rs.getString("bb_name"));
				if (self) {
					map.put("show", rs.getString("bbe_bbcode")!=null);
				}else{
					map.put("show", true);
				}
				map.put("enable", true);
				data.add(map);
			}
		}else {
			String jobs = getJobs(employee);
			sql = "select bb_code,bb_name,bbe_bbdetno,bbe_bbcode,case when bb_caller in (select distinct pp_caller from (select pp_caller from positionpower "
				+ "where pp_see=1 and pp_joid in("+jobs+")) union (select pp_caller from personalpower where pp_see=1 and pp_emid= ?)) then 1 else 0 end enable "
				+ "from (select bb_code,bb_name,bb_detno,bb_caller from benchscene,benchbusiness where bs_bbcode = bb_code and bs_bccode=? and "
				+ "bb_statuscode <> 'DISABLE' and nvl(bs_enable,0) <> 0 group by bb_name,bb_detno,bb_code,bb_caller order by bb_detno,bb_code asc) left join benchbusinessemps on "
				+ "bb_code = bbe_bbcode and bbe_emid = ? order by bbe_bbdetno,bb_detno,bb_code";
			SqlRowList rs = baseDao.queryForRowSet(sql, employee.getEm_id(),benchcode, employee.getEm_id());
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("bbcode", rs.getGeneralString("bb_code"));
				map.put("bbname", rs.getString("bb_name"));
				if (self) {
					map.put("show", rs.getString("bbe_bbcode")!=null);
				}else{
					map.put("show", rs.getGeneralInt("enable")==1);
				}
				map.put("enable", rs.getGeneralInt("enable")==1);
				data.add(map);
			}
		}
		result.put("data", data);
		return result;
	}
	

	@Override
	@CacheEvict(value = "benchbusiness", allEntries = true)
	public void saveSelfBusiness(String benchcode,String datas) {
		List<Map<Object, Object>> selfSets = BaseUtil.parseGridStoreToMaps(datas);
		Employee employee = SystemSession.getUser();
		try {
			baseDao.deleteByCondition("BENCHBUSINESSEMPS", "BBE_EMID = ? AND BBE_BCCODE = ?", employee.getEm_id(), benchcode);
			for (Map<Object, Object> map : selfSets) {
				map.put("bbe_id", baseDao.getSeqId("BENCHBUSINESSEMPS_SEQ"));
				map.put("bbe_emid", employee.getEm_id());
				map.put("bbe_bccode", benchcode);
			}
			baseDao.execute(SqlUtil.getInsertSqlbyGridStore(selfSets, "BENCHBUSINESSEMPS"));
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误："+e.getMessage());
		}
	}
	
	@Override
	public Map<String, Object> getSelfScene(String benchcode, boolean noControl) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		
		Employee employee = SystemSession.getUser();
		int groupnum = 0;
		List<Map<String,Object>> groups = null;
		String condition = "1=1";
		if (noControl) {
			groups = baseDao.queryForList("select bb_code,bb_name from (select bb_code,bb_name,bb_detno from benchscene,benchbusiness "
					+ "where bs_bbcode = bb_code and bs_bccode=? and bb_statuscode <> 'DISABLE' and nvl(bs_enable,0) <> 0 group by bb_name,bb_detno,bb_code order by bb_detno,bb_code) "
					+ "left join benchbusinessemps on bb_code = bbe_bbcode and bbe_emid = ? order by bbe_bbdetno,bb_detno,bb_code", benchcode, employee.getEm_id());
		}else{
			String jobs = getJobs(employee);
			condition = "((nvl(bs_caller,' ') <> ' ' and bs_caller in (select distinct pp_caller from (select pp_caller from positionpower where (pp_alllist=1 or pp_selflist=1 or pp_jobemployee=1 or pp_see=1) and pp_joid in("+jobs+")) "
					+ "union (select pp_caller from personalpower where (pp_alllist=1 or pp_selflist=1 or pp_jobemployee=1 or pp_see=1) and pp_emid= " + employee.getEm_id() + "))) or (nvl(bs_caller,' ') = ' '))";
			
			groups = baseDao.queryForList("select bb_code,bb_name from (select bb_code,bb_name,bb_detno from benchscene,benchbusiness "
					+ "where bs_bbcode = bb_code and " + condition + " and bs_bccode=? and bb_statuscode <> 'DISABLE' and nvl(bs_enable,0) <> 0 group by bb_name,bb_detno,bb_code order by bb_detno,bb_code) "
					+ "left join benchbusinessemps on bb_code = bbe_bbcode and bbe_emid = ? order by bbe_bbdetno,bb_detno,bb_code", benchcode, employee.getEm_id());
		}
		
		for (Map<String,Object> group : groups) {
			groupnum ++;
			boolean self = baseDao.checkIf("BENCHSCENEEMPS", "BE_BCCODE = '"+benchcode+"' and BE_EMID = "+employee.getEm_id());
			SqlRowList rs = baseDao.queryForRowSet("SELECT CASE WHEN BE_ISCOUNT IS NOT NULL THEN BE_ISCOUNT ELSE BS_ISCOUNT END ISCOUNT,"
					+ "BS_CODE,BS_TITLE,BE_ID FROM BENCHSCENE LEFT JOIN BENCHSCENEEMPS ON BS_CODE = BE_BSCODE AND "
					+ "BE_EMID = ? WHERE " + condition + " and BS_BCCODE=? AND BS_BBCODE = ? and nvl(bs_enable,0) <> 0 ORDER BY BE_BSDETNO ASC,BS_DETNO ASC", employee.getEm_id(), benchcode, group.get("BB_CODE"));
			while(rs.next()){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("iscount", rs.getGeneralInt("iscount")!=0);
				map.put("code", rs.getString("bs_code"));
				map.put("text", rs.getString("bs_title"));
				map.put("bbname", group.get("BB_NAME"));
				if (self) {
					map.put("show", rs.getGeneralInt("be_id")!=0);
				}else{
					map.put("show", true);
				}
				map.put("groupnum", groupnum);
				data.add(map);
			}
		}
		result.put("data", data);
		return result;
	}
	

	@Override
	@CacheEvict(value = "benchbusiness", allEntries = true)
	public void selfReset(String benchcode, boolean isBusiness) {
		Employee employee = SystemSession.getUser();
		
		if (isBusiness) {
			baseDao.deleteByCondition("BENCHBUSINESSEMPS", "BBE_BCCODE = ? AND BBE_EMID =?", benchcode, employee.getEm_id());
		}else {
			baseDao.deleteByCondition("BENCHSCENEEMPS", "BE_BCCODE = ? AND BE_EMID =?", benchcode, employee.getEm_id());
			List<String> businesses = baseDao.queryForList("select bs_bbcode from benchscene where bs_bccode = ? group by bs_bbcode", String.class, benchcode);
			for (String bbcode : businesses) {
				benchDao.clearScene(employee, benchcode, bbcode);
			}
		}
		
	}

	@Override
	@CacheEvict(value = "benchbusiness", allEntries = true)
	public void saveSelfScene(String benchcode,String datas) {
		List<Map<Object, Object>> selfSets = BaseUtil.parseGridStoreToMaps(datas);
		Employee employee = SystemSession.getUser();
		try {
			baseDao.deleteByCondition("BENCHSCENEEMPS", "BE_EMID = ? AND BE_BCCODE = ?", employee.getEm_id(), benchcode);
			for (Map<Object, Object> map : selfSets) {
				map.put("be_id", baseDao.getSeqId("BENCHSCENEEMPS_SEQ"));
				map.put("be_emid", employee.getEm_id());
				map.put("be_bccode", benchcode);
				
			}
			baseDao.execute(SqlUtil.getInsertSqlbyGridStore(selfSets, "BENCHSCENEEMPS"));
			List<String> businesses = baseDao.queryForList("select bs_bbcode from benchscene where bs_bccode = ? group by bs_bbcode", String.class, benchcode);
			for (String bbcode : businesses) {
				benchDao.clearScene(employee, benchcode, bbcode);
			}
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误："+e.getMessage());
		}
	}


	@Override
	public List<BenchSceneGrid> getSetByCaller(String caller) {
		return benchDao.getSetByCaller(caller);
	}

	@Override
	@CacheEvict(value = "benchbusiness", allEntries = true)
	public void saveBusiness(String caller, String formStore) {
		try {
			Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
			if (!StringUtil.hasText(store.get("bb_id"))) {
				int id = baseDao.getSeqId("BENCHBUSINESS_SEQ");
				store.put("bb_id", id);
			}
			
			Employee employee = SystemSession.getUser();
			int count = baseDao.getJdbcTemplate().queryForObject("select nvl(max(substr(BB_CODE,-3)),0) from BENCHBUSINESS",Integer.class);
			NumberFormat nf = NumberFormat.getInstance();
			// 设置是否使用分组
			nf.setGroupingUsed(false);
			// 设置最大整数位数
			nf.setMaximumIntegerDigits(3);
			// 设置最小整数位数
			nf.setMinimumIntegerDigits(3);
			// 输出测试语句
			String code = employee.getEm_master() + nf.format(count + 1);
			
			store.put("bb_code", code);
			
			handlerService.beforeSave(caller, new Object[]{ store });
			
			baseDao.execute(SqlUtil.getInsertSqlByMap(store, "BENCHBUSINESS"));
			
			handlerService.afterSave(caller, new Object[]{ store });
			
			baseDao.logger.save(caller, "bb_id", store.get("bb_id"));
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
		
	}
	
	@Override
	@Caching(evict = {
		@CacheEvict(value = "benchbusiness", allEntries = true),
		@CacheEvict(value = "benchscenes", allEntries = true)
	})
	public void updateBusiness(String caller, String formStore) {
		try {
			Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
			
			handlerService.beforeUpdate(caller, new Object[]{ store });
			
			baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "BenchBusiness", "bb_id"));
			
			handlerService.afterUpdate(caller, new Object[]{ store });
			
			baseDao.logger.update(caller, "bb_id", store.get("bb_id"));
		} catch (Exception e) {
			BaseUtil.showError("更新失败，错误：" + e.getMessage());
		}
	}

	@Override
	@Caching(evict = {
		@CacheEvict(value = "benchbusiness", allEntries = true),
		@CacheEvict(value = "benchscenes", allEntries = true)
	})
	public void deleteBusiness(String caller, Integer id) {
		boolean bool = baseDao.checkIf("BenchScene inner join BenchBusiness on bs_bbcode = bb_code", "bb_id = " + id);
		if (bool) {
			BaseUtil.showError("存在使用此业务的工作台，无法删除！");
		}
		
		handlerService.beforeDel(caller, id);
		
		baseDao.deleteById("BenchBusiness", "bb_id", id);
		
		handlerService.afterDel(caller, id);
		
		baseDao.logger.delete(caller, "bb_id", id);
	}

	@Override
	@Caching(evict = {
		@CacheEvict(value = "benchbusiness", allEntries = true),
		@CacheEvict(value = "benchscenes", allEntries = true)
	})
	public void bannedBusiness(String caller, Integer id) {
		// 只能对状态为[已审核]的单据进行禁用操作!
		Object status = baseDao.getFieldDataByCondition("BenchBusiness", "bb_statuscode", "bb_id =" + id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("只能对状态为[已审核]的单据进行禁用操作!"));
		}
		// 执行禁用前的其它逻辑
		handlerService.handler(caller, "banned", "before", new Object[] { id });
		// 执行禁用操作
		baseDao.updateByCondition("BenchBusiness","bb_statuscode='DISABLE',bb_status='" + BaseUtil.getLocalMessage("DISABLE") + "'", "bb_id =" + id);
		// 记录操作
		try {
			baseDao.logger.banned(caller, "bb_id", id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行禁用后的其它逻辑
		handlerService.handler(caller, "banned", "after", new Object[] { id });
		
	}

	@Override
	@CacheEvict(value = "benchbusiness", allEntries = true)
	public void resBannedBusiness(String caller, Integer id) {
		// 只能对状态为[已禁用]的单据进行反禁用操作!
		Object status = baseDao.getFieldDataByCondition("BenchBusiness", "bb_statuscode", "bb_id =" + id);
		if (!status.equals("DISABLE")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resBanned_onlyBanned"));
		}
		// 执行禁用前的其它逻辑
		handlerService.handler(caller, "resBanned", "before", new Object[] { id });
		// 执行反禁用操作
		baseDao.updateByCondition("BenchBusiness", "bb_statuscode='ENTERING',bb_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "bb_id =" + id);
		// 记录操作
		try {
			baseDao.logger.resBanned(caller, "bb_id", id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行禁用后的其它逻辑
		handlerService.handler(caller, "resBanned", "after", new Object[] { id });
		
	}

}
