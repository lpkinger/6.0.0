package com.uas.erp.service.hr.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DataListDetail;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.PersonalPower;
import com.uas.erp.model.PositionPower;
import com.uas.erp.model.RolePower;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.hr.JobPowerService;

@Service
public class JobPowerServiceImpl implements JobPowerService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PowerDao powerDao;
	@Autowired
	private FormDao formDao;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private DataListDao dataListDao;
	@Autowired
	protected LobHandler lobHandler;
	@Autowired
	protected SingleFormItemsService singleFormItemsService;
	/**
	 * 修改岗位权限
	 */
	@Override
	@CacheEvict(value = { "positionpower", "power", "tree", "benchbusiness", "benchscenes", "button" }, allEntries = true)
	public void update(String update, String caller, Boolean _self) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(update);
		String tableName = _self ? "PersonalPower" : "PositionPower";
		Object pageCaller = gstore.get(0).get("pp_caller");
		// 修改
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(update, tableName, "pp_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pp_id") == null || s.get("pp_id").equals("") || s.get("pp_id").equals("null")
					|| (Integer) s.get("pp_id") == 0 || s.get("pp_id").equals("0")) {// 新添加的数据，id不存在
				if (s.get("pp_alllist") == null)
					s.put("pp_alllist", 0);
				if (s.get("pp_selflist") == null)
					s.put("pp_selflist", 0);
				gridSql.add(SqlUtil.getInsertSql(s, tableName, "pp_id"));
			}		
		}
		baseDao.execute(gridSql);
		gridSql.clear();
		for (Map<Object, Object> s : gstore) {
			if(_self){
				Object seepower=baseDao.getFieldDataByCondition("personalpower", "pp_see", "pp_emid='"+s.get("pp_emid")+"' and pp_caller='"+s.get("pp_caller")+"'");
				if(seepower!=null && seepower.toString().equals("0")){
					gridSql.add(SqlUtil.getDeleteSql("commonuse", "cu_emid in('"+s.get("pp_emid")+"') and nvl(cu_caller,0) in('"+s.get("pp_caller")+"')"));
				}
			}else{
				Object seepower=baseDao.getFieldDataByCondition("positionpower", "pp_see", "pp_joid='"+s.get("pp_joid")+"' and pp_caller='"+s.get("pp_caller")+"'");
				if(seepower!=null && seepower.toString().equals("0")){
					gridSql.add(SqlUtil.getDeleteSql("commonuse", "cu_emid in(select em_id from employee where em_defaulthsid in('"+s.get("pp_joid")+"')) and nvl(cu_caller,0) in('"+s.get("pp_caller")+"')"));
				}
			}
		}
		baseDao.execute(gridSql);
		if (!_self) {
			// 遍历插入父节点
			String joids = BaseUtil.parseArray2Str(CollectionUtil.pluck(gstore, "pp_joid"), ",");			
			//baseDao.procedure("sp_checkpower", new Object[] { joids, pageCaller });
			procedure("sp_checkpower",joids,String.valueOf(pageCaller));
		} else {
			String emids = BaseUtil.parseArray2Str(CollectionUtil.pluck(gstore, "pp_emid"), ",");
			baseDao.procedure("sp_checkpersonalpower", new Object[] { emids, pageCaller });			
		}
		// 记录操作
		try {
			for (Map<Object, Object> map : gstore) {
				if (map.get("pp_id") == null || map.get("pp_id").equals("") || map.get("pp_id").equals("null")){//新添加数据
					if (!_self) savePowerLog(pageCaller,map.get("pp_joid"),0,"add",map.toString());
					else savePowerLog(pageCaller,0,map.get("pp_emid"),"add",map.toString());
				}else{
					if (!_self) savePowerLog(pageCaller,map.get("pp_joid"),0,"update",map.toString());
					else savePowerLog(pageCaller,0,map.get("pp_emid"),"update",map.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 修改角色权限
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@CacheEvict(value = { "positionpower","rolepower", "power", "tree","hrjob", "benchbusiness", "benchscenes", "button"}, allEntries = true)
	public void updateRolePower(String update, String caller, Boolean _self) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(update);
		String tableName = _self ? "PersonalPower" : "RolePower";
		Object pageCaller = gstore.get(0).get("pp_caller");
		String logType = null;
		// 修改
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(update, tableName, "rp_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("rp_id") == null || s.get("rp_id").equals("") || s.get("rp_id").equals("null")
					|| (Integer) s.get("rp_id") == 0 || s.get("rp_id").equals("0")) {// 新添加的数据，id不存在
				if (s.get("pp_alllist") == null)
					s.put("pp_alllist", 0);
				if (s.get("pp_selflist") == null)
					s.put("pp_selflist", 0);
				gridSql.add(SqlUtil.getInsertSql(s, tableName, "rp_id"));
			}
		}
		baseDao.execute(gridSql);
		
		String rpids = BaseUtil.parseArray2Str(CollectionUtil.pluck(gstore, "rp_roid"), ",");
		//调用存储过程刷新权限
		procedure("SP_ROLEPOWERTOPOSITION",rpids,String.valueOf(pageCaller));

		// 记录操作
		try {
			for (Map<Object, Object> map : gstore) {
				if (map.get("rp_id") == null || map.get("rp_id").equals("") || map.get("rp_id").equals("null")){//新添加数据
					logType = "add";
				}else{
					logType = "update";
				}
				saveRolePowerLog(pageCaller,map.get("rp_roid"),logType,map.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void procedure(final String procedureName,final String clobargs,final String args) {
		try {
				String sql ="{call "+procedureName+" (?,?)}";
				baseDao.getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
					@Override
					protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException, DataAccessException {
						lob.setClobAsString(ps, 1, clobargs);
						ps.setString(2, args);						
					}
				});					
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
		}
	}

	@Override
	public List<PositionPower> getPositionPowersByCaller(String caller) {
		return powerDao.getPositionPowersByCaller(caller, SpObserver.getSp());
	}
	
	public List<PersonalPower> getPersonalPowersByCaller(String caller) {
		return powerDao.getPersonalPowersByCaller(caller, SpObserver.getSp());
	}

	public List<PersonalPower> getPersonalPowersByEm(String caller, String emid) {
		if (emid != null)
			return powerDao.getPersonalPowersByEm(caller, emid);
		return null;
	}
	
	public List<FormDetail> getFormDetails(String caller) {
		try {
			return formDao.getForm(caller, SpObserver.getSp()).getFormDetails();
		} catch (Exception e) {
			return null;
		}
	}
	public Map<String, Object> getDetailGrids(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		try {
			List<List<DetailGrid>> list  = new  ArrayList<List<DetailGrid>>();
			List<Object> lists  = new ArrayList<Object>();
			List<Object> listc  = new ArrayList<Object>();
			List<Object[]> callers = baseDao.getFieldsDatasByCondition("MOBILEAUDITDETAIL", new String[] { "mad_caller","mad_name" }, "MAD_MANCALLER='"+caller+"'  order by mad_mancaller");
			if(callers.size()>0){
				for (Object[] c : callers) {
					List<DetailGrid> detailegrid = detailGridDao.getDetailGridsByCaller((String)c[0], SpObserver.getSp());
					list.add(detailegrid);
					lists.add(c[1]);
					listc.add(c[0]);
				}	
			}else{
				list.add(detailGridDao.getDetailGridsByCaller(caller, SpObserver.getSp()));
			}
			modelMap.put("detailgrid", list);
			modelMap.put("title", lists);
			modelMap.put("callers", listc);
			return modelMap;
		} catch (Exception e) {
			return null;
		}
	}
	public List<Map<String, Object>> getRelativeSearchs(String caller) {
		try {
			return singleFormItemsService.getRelativeSearchs(caller);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@CacheEvict(value = "limitfields", allEntries = true)
	public void saveLimitFields(String data, String caller,String relativeCaller, int jo_id, Boolean _self ,Boolean islist) {
		List<Map<Object, Object>> limits = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls = new ArrayList<String>();
		boolean isList = false;
		isList=islist;
		String tableName = _self ? "SelfLimitFields" : "LimitFields";
		String kName = _self ? "lf_emid" : "lf_joid";
		for (Map<Object, Object> m : limits) {
			sqls.add(SqlUtil.getInsertSqlByMap(m, tableName));
			if (!isList && Integer.parseInt(m.get("lf_isform").toString()) == 2) {
				isList = true;
			}
		}
		if(isList){//删除列表关联列表
			baseDao.execute("delete from "+tableName+" where lf_caller in(?,?) and lf_isform=2 and "+kName+" =?",
							caller,relativeCaller,jo_id);
		}else{//删除form，grid，关联查询
			baseDao.execute("delete from "+tableName+" where (lf_caller =? or lf_caller like (?)) and lf_isform<>2 and "+kName+" =?",
							caller,caller+"|%",jo_id);
			baseDao.execute("delete "+tableName+" where lf_caller in(select mad_caller from MOBILEAUDITDETAIL where mad_mancaller='"+caller+"')");
		}
		//重新插入
		baseDao.execute(sqls);
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@CacheEvict(value = { "rolelimitfields", "limitfields"}, allEntries = true)
	public void saveRoleLimitFields(String data, String caller,
			String relativeCaller, int id, Boolean _self, Boolean islist) {
		List<Map<Object, Object>> limits = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls = new ArrayList<String>();
		String sql = null;
		boolean isList = false;
		isList = islist;
		String tableName = _self ? "SelfLimitFields" : "RoleLimitFields";
		String kName = _self ? "lf_emid" : "lf_roid";

		Object jobIdsObj = baseDao.getFieldDataByCondition("job", "wm_concat(jo_id)", "jo_roid=" + id);
		
		if(isList){//删除列表关联列表
			sql = "delete from "+tableName+" where lf_caller in('"+caller+"','"+relativeCaller+"') and lf_isform=2 and "+kName+" ="+id;
			sqls.add(sql);
			
			//删除limitfields的数据
			if(jobIdsObj!=null){
				sql = "delete from limitfields where lf_caller in('"+caller+"','"+relativeCaller+"') and lf_isform=2 and nvl(lf_joid,0) in ("+jobIdsObj + ")";
				sqls.add(sql);	
			}

		}else{//删除form，grid，关联查询
			sql = "delete from "+tableName+" where (lf_caller ='"+caller+"' or lf_caller like ('"+caller+"|%"+"')) and lf_isform<>2 and "+kName+" =" + id;
			sqls.add(sql);
			sql = "delete "+tableName+" where lf_caller in(select mad_caller from MOBILEAUDITDETAIL where mad_mancaller='"+caller+"')";
			sqls.add(sql);
			
			if(jobIdsObj!=null){
				sql = "delete from limitfields where (lf_caller ='"+caller+"' or lf_caller like ('"+caller+"|%"+"')) and lf_isform<>2 and lf_joid in ("+jobIdsObj + ")";
				sqls.add(sql);			
				sql = "delete limitfields where lf_caller in(select mad_caller from MOBILEAUDITDETAIL where mad_mancaller='"+caller+"')";
				sqls.add(sql);
			}
		}
		
		//删除
		baseDao.execute(sqls);
		sqls.clear();
		
		//插入
		for (Map<Object, Object> m : limits) {
			sqls.add(SqlUtil.getInsertSqlByMap(m, tableName));
			if (!isList && Integer.parseInt(m.get("lf_isform").toString()) == 2) {
				isList = true;
			}
		}
		baseDao.execute(sqls);
		sqls.clear();
		
		//从rolelimitfields复制到rolelimitfields
		if(isList){
			sql = "insert into limitfields(lf_joid,lf_caller,lf_field,lf_isform) select jo_id,lf_caller,lf_field,lf_isform from rolelimitfields left join job on lf_roid=jo_roid where jo_roid=" + id + " and lf_isform=2 and lf_caller in('"+caller+"','"+relativeCaller+"')";
		}else{
			sql = "insert into limitfields(lf_joid,lf_caller,lf_field,lf_isform) select jo_id,lf_caller,lf_field,lf_isform from rolelimitfields left join job on lf_roid=jo_roid where jo_roid=" + id+ " and lf_isform<>2 and (lf_caller='"+caller+"' or lf_caller like ('"+caller+"|%"+"') or lf_caller in (select mad_caller from MOBILEAUDITDETAIL where mad_mancaller='"+caller+"'))";
		}
		baseDao.execute(sql);
	}
	
	@Override
	public Map<String,List<DataListDetail>> getDataList(String caller) {
		Employee employee=SystemSession.getUser();
		Map<String,List<DataListDetail>> map=new HashMap<String,List<DataListDetail>>();
		DataList dataList = dataListDao.getDataList(caller, employee.getEm_master()),relativeDataList=null;
		if(dataList !=null ){
			relativeDataList=dataListDao.getDataList(dataList.getDl_relative(), employee.getEm_master());
		}
		map.put("datalist", dataList!=null?dataList.getDataListDetails():null);
		map.put("relativedatalist", relativeDataList!=null?relativeDataList.getDataListDetails():null);
		return map;
	}

	@Override
	@Transactional
	@CacheEvict(value = "specialpower", allEntries = true)
	public void saveSpecialPower(String caller,String data, int jo_id, Boolean _self) {
		String field = _self ? "sp_emid" : "sp_joid";
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> m : store) {
			sb.append(m.get("sp_sspid")).append(",");
			Boolean bool = (Boolean) m.get("checked");
			if (bool) {
				m.remove("checked");
				m.put(field, jo_id);
				sqls.add(SqlUtil.getInsertSqlByMap(m, "SpecialPower"));
			}
		}
		if (sb.length() > 0) {
			baseDao.deleteByCondition("SpecialPower",
					field + "=" + jo_id + " AND sp_sspid in (" + sb.substring(0, sb.length() - 1) + ")");
			baseDao.execute(sqls);
		}
		// 记录操作
		try {
			if (!_self) saveSpecialPowerLog(caller,jo_id,0,"update",store.toString());
			else saveSpecialPowerLog(caller,0,jo_id,"update",store.toString());			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Override
	@Transactional
	@CacheEvict(value = "specialpower", allEntries = true)
	public void saveRoleSpecialPower(String caller, String data, int ro_id,
			Boolean _self) {
		String field = _self ? "sp_emid" : "sp_roid";
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls = new ArrayList<String>();
		String sql = null;
		String sspids = null;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> m : store) {
			sb.append(m.get("sp_sspid")).append(",");
			Boolean bool = (Boolean) m.get("checked");
			if (bool) {
				m.remove("checked");
				m.put(field, ro_id);
				sqls.add(SqlUtil.getInsertSqlByMap(m, "RoleSpecialPower"));
			}
		}
		if (sb.length() > 0) {
			sspids = sb.substring(0, sb.length() - 1);
			baseDao.deleteByCondition("RoleSpecialPower",
					field + "=" + ro_id + " AND sp_sspid in (" + sspids + ")");
			baseDao.execute(sqls);
			sqls.clear();
		}
		
		//从rolespecialpower复制到specialpower
		Object jobIdsObj = baseDao.getFieldDataByCondition("job", "wm_concat(jo_id)", "jo_roid=" + ro_id);
		if(jobIdsObj!=null){
			sql = "delete from specialpower where sp_joid in ("+jobIdsObj+") and sp_sspid in (" + sspids + ")";
			sqls.add(sql);
			sql = "insert into specialpower(sp_id,sp_sspid,sp_joid) select specialpower_seq.nextval,sp_sspid,jo_id from rolespecialpower left join job on jo_roid=sp_roid where jo_roid=" + ro_id + " and sp_sspid in (" + sspids + ")";
			sqls.add(sql);
			baseDao.execute(sqls);
			sqls.clear();
		}
		
		// 记录操作
		try {
			saveRoleSpecialPowerLog(caller,ro_id,"update",store.toString());			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void savePowerLog(Object caller, Object joid, Object emid,String type, String powerstring) {
		Employee employee = SystemSession.getUser();
		int pl_id=baseDao.getSeqId("POWERLOG_SEQ");
		String sql = "insert into PowerLog (pl_id,pl_caller,pl_joid,pl_emid,pl_type,pl_powerstring,pl_updatetime,pl_updater,pl_updatercode) values"
				+"("+pl_id+",'"+caller+"',"+joid+","+emid+",'"+type+"','"+powerstring+"',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+",'"+employee.getEm_name()+"','"+employee.getEm_code()+"')";		
		baseDao.execute(sql);
	}

	public void saveRolePowerLog(Object caller, Object roid,String type, String powerstring) {
		Employee employee = SystemSession.getUser();
		int pl_id=baseDao.getSeqId("POWERLOG_SEQ");
		String sql = "insert into PowerLog (pl_id,pl_caller,pl_joid,pl_emid,pl_type,pl_powerstring,pl_updatetime,pl_updater,pl_updatercode,pl_roid) values"
				+"("+pl_id+",'"+caller+"',0,0,'"+type+"','"+powerstring+"',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+",'"+employee.getEm_name()+"','"+employee.getEm_code()+"',"+roid+")";		
		baseDao.execute(sql);
	}
	
	public void saveSpecialPowerLog(Object caller, int joid, int emid,String type, String powerstring) {
		Employee employee = SystemSession.getUser();
		int pl_id=baseDao.getSeqId("POWERLOG_SEQ");
		String sql = "insert into PowerLog (pl_id,pl_caller,pl_joid,pl_emid,pl_type,pl_powerstring,pl_updatetime,pl_updater,pl_updatercode,pl_kind) values"
				+"("+pl_id+",'"+caller+"',"+joid+","+emid+",'"+type+"','"+powerstring+"',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+",'"+employee.getEm_name()+"','"+employee.getEm_code()+"','特殊权限')";		
		baseDao.execute(sql);
	}
	
	public void saveRoleSpecialPowerLog(Object caller, int roid,String type, String powerstring) {
		Employee employee = SystemSession.getUser();
		int pl_id=baseDao.getSeqId("POWERLOG_SEQ");
		String sql = "insert into PowerLog (pl_id,pl_caller,pl_joid,pl_emid,pl_type,pl_powerstring,pl_updatetime,pl_updater,pl_updatercode,pl_kind,pl_roid) values"
				+"("+pl_id+",'"+caller+"',0,0,'"+type+"','"+powerstring+"',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+",'"+employee.getEm_name()+"','"+employee.getEm_code()+"','特殊权限',"+roid+")";		
		baseDao.execute(sql);
	}
	
	@Override
	public String vastPostPower(String caller, String to) {		
		try {
			String[] masters = to.split(",");
			String sql = "begin ";
			for(String master:masters){
				sql+="delete from "+master+".positionpower;"
					+ "delete from "+master+".personalpower;"
					+ "delete from "+master+".LIMITFIELDS;"
					+ "delete from "+master+".SelfLimitFields;"
					+ "delete from "+master+".SPECIALPOWER;"
					+ "delete from "+master+".sysspecialpower;";
				sql+="insert into "+master+".positionpower select * from positionpower;"
					+"insert into "+master+".personalpower select * from personalpower;"
					+"insert into "+master+".LIMITFIELDS select * from LIMITFIELDS;"
					+"insert into "+master+".SelfLimitFields select * from SelfLimitFields;"
					+"insert into "+master+".SPECIALPOWER select * from SPECIALPOWER;"
					+"insert into "+master+".sysspecialpower select * from sysspecialpower;";				
			}
			sql += " end;";
			baseDao.execute(sql);
			baseDao.logger.others("权限覆盖","权限成功覆盖到"+to, caller, "masters", to);
			return null;
		} catch (Exception e) {
			return e.getMessage();
		}		
	}

	@Override
	public void vastRefreshPower() {				                             
		String res1 = baseDao.callProcedure("SP_NAVIGATIONPOWER", new Object[] {null});
		String res2 = baseDao.callProcedure("SP_NAVIGATIONPERSONALPOWER", new Object[] {null});
			if ((res1!=null && !"".equals(res1))||(res2!=null && !"".equals(res2)))
				BaseUtil.showError("刷新岗位权限："+res1+"<br>刷新个人权限："+res2);	
			baseDao.logger.others("权限刷新","权限刷新成功", "REFRESHPOWER", "", "");
	}

	@Override
	public List<RolePower> getRolePowersByCaller(String caller) {
		return powerDao.getRolePowersByCaller(caller, SpObserver.getSp());
	}

}
