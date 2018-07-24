package com.uas.erp.service.ma.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.RoleSpecialPower;
import com.uas.erp.model.SpecialPower;
import com.uas.erp.model.SysSpecialPower;
import com.uas.erp.service.ma.PowerService;

@Service
public class PowerServiceImpl implements PowerService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PowerDao powerDao;

	@Override
	public List<Map<String, Object>> getPowerData(String condition, int page, int pageSize,String tableName) {
		if("".equals(condition)){
			condition="1=1";
		}
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		String sql = "select *　from (select a.*,rownum rn from (select * from "+tableName+" where "+condition+" and rownum<="+end+")a) where rn >="+start;
		return baseDao.getJdbcTemplate().queryForList(sql);
	}
	@Override
	public int getPowerCount(String condition,String tableName) {
		if("".equals(condition)){
			condition="1=1";
		}
		return baseDao.getCount("select count(1) from "+ tableName+ " where "+condition);
	}

	@Override
	@CacheEvict(value = "power", allEntries = true)
	public void save(String save) {
		// 保存
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(save);
		for (Map<Object, Object> map : store) {
			map.put("po_id", baseDao.getSeqId("POWER_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(store, "Power");
		baseDao.execute(gridSql);
		for (Map<Object, Object> map : store) {
			// 记录操作
			baseDao.logger.save("Power", "po_id", map.get("po_id"));
		}
	}

	@Override
	@CacheEvict(value = "power", allEntries = true)
	public void update(String update) {
		// 修改
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(update, "Power", "po_id");
		baseDao.execute(gridSql);
		// 记录操作
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(update);
		for (Map<Object, Object> map : store) {
			// 记录操作
			baseDao.logger.update("Power", "po_id", map.get("po_id"));
		}
	}

	@Override
	@CacheEvict(value = "power", allEntries = true)
	public void delete(int id) {
		// 删除
		deleteChilds(id);
		// 记录操作
		baseDao.logger.delete("Power", "po_id", id);
	}

	/**
	 * @param id
	 */
	public void deleteChilds(int id) {
		baseDao.deleteByCondition("Power", "po_id=" + id);
		// 判断是否有子元素
		boolean bool = baseDao.checkByCondition("Power", "po_parentid=" + id);
		if (!bool) {
			List<Object> objs = baseDao.getFieldDatasByCondition("Power", "po_id", "po_parentid=" + id);
			for (Object obj : objs) {
				deleteChilds(Integer.parseInt("" + obj));
			}
		}
	}

	/**
	 * 
	 */
	@Override
	public List<SysSpecialPower> getSysSpecialPowers(String caller) {
		return powerDao.getSysSpecialPowers(caller, SpObserver.getSp());
	}

	@Override
	@CacheEvict(value = { "sysspecialpowers", "specialactions" }, allEntries = true)
	public void saveSysSpecialPowers(String caller, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		baseDao.execute(SqlUtil.getInsertOrUpdateSqlbyGridStore(store, "SysSpecialPower", "ssp_id"));
	}

	@Override
	@CacheEvict(value = { "power", "positionpower" }, allEntries = true)
	public void copyPower(int fromId, String toIds) {
		String[] tos = toIds.split(",");
		for (String toId : tos) {
			if (!toId.equals(String.valueOf(fromId))) {
				//复制岗位权限
				baseDao.execute("begin delete from positionpower where pp_joid="
						+ toId
						+ ";for rs in(select * from positionpower where pp_joid="
						+ fromId
						+ ")loop rs.pp_joid:="
						+ toId
						+ ";rs.pp_id:=positionpower_seq.nextval;insert into positionpower values rs;end loop;commit;exception when others then rollback;end;");
				//复制特殊权限
				baseDao.execute("begin delete from specialpower where sp_joid="
						+ toId
						+ ";for rs in(select * from specialpower where sp_joid="
						+ fromId
						+ ")loop rs.sp_joid:="
						+ toId
						+ ";rs.sp_id:=specialpower_seq.nextval;insert into specialpower values rs;end loop;commit;exception when others then rollback;end;");
			}
		}
	}

	@Override
	@CacheEvict(value = { "power", "positionpower" }, allEntries = true)
	public void copypowerFromStandard(String param) {
		// 插入到日志记录
		Employee employee = SystemSession.getUser();
		String InstallType = "Make";
		try {
			InstallType = employee.getCurrentMaster().getMa_installtype();
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(param);
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : maps) {
			sqls.add("delete positionpower where pp_joid=" + map.get("cp_toid"));
			sqls.add("update copypower set CP_HASCHANGE=0 where cp_toid=" + map.get("cp_toid"));
			sqls.add("insert into positionpower (PP_ID,PP_JOID,PP_SEE,PP_ADD,PP_DELETE,PP_SAVE,PP_COMMIT,PP_UNCOMMIT,PP_AUDIT,PP_UNAUDIT,PP_PRINT,PP_DISABLE,PP_UNDISABLE,"
					+ "PP_CLOSED,PP_UNCLOSED,PP_POSTING,PP_UNPOSTING,PP_CALLER,PP_ALLLIST,PP_SAVEOTH,PP_PRINTOTH,PP_SELFLIST) select positionpower_seq.nextval ,"
					+ map.get("cp_toid")
					+ ",PP_SEE,PP_ADD,PP_DELETE,PP_SAVE,PP_COMMIT,PP_UNCOMMIT,PP_AUDIT,PP_UNAUDIT,PP_PRINT,PP_DISABLE,PP_UNDISABLE,PP_CLOSED,PP_UNCLOSED,PP_POSTING,PP_UNPOSTING,PP_CALLER,PP_ALLLIST,PP_SAVEOTH,PP_PRINTOTH,PP_SELFLIST from Uas_Standard_"
					+ InstallType + ".positionpower where pp_joid=" + map.get("cp_fromid"));
			if (map.get("cp_haschange") != null) {
				map.put("cp_id", baseDao.getSeqId("COPYPOWER_SEQ"));
				map.put("cp_man", employee.getEm_name());
				map.put("cp_haschange", 1);
				sqls.add(SqlUtil.getInsertSqlByMap(map, "copypower"));
			}
		}
		baseDao.execute(sqls);
	}

	@Override
	@CacheEvict(value = { "sysspecialpowers", "button" }, allEntries = true)
	public void deleteSysSpecialPowerById(int id,Integer sbid) {
		baseDao.execute("delete SysSpecialPower where ssp_id=" + id);
		baseDao.execute("delete SpecialPower where sp_sspid=" + id);
		if (sbid!=null) {
			baseDao.execute("update benchscenebutton set sb_spaction = null where sb_id=" + sbid);
		}
	}

	@Override
	public List<SpecialPower> getSpecialPowerByEmpl(String caller, Integer em_id) {
		try {
			return baseDao.getJdbcTemplate().query(
					"select * from SpecialPower where sp_sspid in (select ssp_id from SysSpecialPower where ssp_caller=?) and sp_emid=?",
					new BeanPropertyRowMapper<SpecialPower>(SpecialPower.class), caller, em_id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<SpecialPower> getSpecialPowerByJob(String caller, Integer jo_id) {
		try {
			return baseDao.getJdbcTemplate().query(
					"select * from SpecialPower where sp_sspid in (select ssp_id from SysSpecialPower where ssp_caller=?) and sp_joid=?",
					new BeanPropertyRowMapper<SpecialPower>(SpecialPower.class), caller, jo_id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public String compareJobPower(String jobs) {
		// TODO Auto-generated method stub
		
		
		return "岗位间存在差异";
	}

	@Override
	public void syncPower(String caller,String to, String data) {
		Employee employee=SystemSession.getUser();
		String returnString=baseDao.callProcedure("SP_SYNCPOWER", new Object[] {caller,SpObserver.getSp(),to,data,employee.getEm_name(),employee.getEm_id()});		
	    if(StringUtils.hasText(returnString))
	    	BaseUtil.showError(returnString);
	}
	@Override
	public void refreshPower(String to) {
		String[] masters = to.split(",");
		String sql="";
		for (String master : masters) {
			sql+=master+".SP_NAVIGATIONPOWER(null,OUT_RETURN);";
			sql+=master+".SP_NAVIGATIONPERSONALPOWER(null,OUT_RETURN);";
		}
		if(sql!=""){
			sql="DECLARE OUT_RETURN VARCHAR2(200);BEGIN "+sql+"END;";
			baseDao.execute(sql);
			}		
	}
	@Override
	public List<Map<String, Object>> getSceneBtnPowers(String benchcode, Integer joid, Integer emid) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Integer epid  = 0;
		String sql = "";
		SqlRowList rs = null;
		String bstitle = "";
		int groupnum = 0;
		if (joid!=null) {
			epid = joid;
			sql = "SELECT BS_TITLE,SB_TITLE,SB_ID,SB_RELATIVECALLER,SB_ALIAS,SSP_CALLER,SSP_ID,SSP_DESC,SB_SPACTION,NVL(SSP_VALID,0),NVL(SP_JOID,0),NVL(SP_ID,0) FROM BENCHSCENE INNER JOIN BENCHSCENEBUTTON  ON BS_CODE = SB_BSCODE LEFT JOIN SYSSPECIALPOWER ON SB_SPACTION =SSP_ACTION  AND SB_RELATIVECALLER = SSP_CALLER LEFT JOIN (SELECT * FROM SPECIALPOWER WHERE SP_JOID = ?) ON SSP_ID = SP_SSPID  WHERE BS_BCCODE = ? and nvl(bs_enable,0) <> 0 ORDER BY BS_DETNO,BS_TITLE,SB_DETNO";
			rs = baseDao.queryForRowSet(sql, epid, benchcode);
		}else if (emid!=null) {
			sql = "SELECT BS_TITLE,SB_TITLE,SB_ID,SB_RELATIVECALLER,SSP_CALLER,SB_ALIAS,SSP_ID,SSP_DESC,SB_SPACTION,NVL(SSP_VALID,0),NVL(SP_EMID,0),NVL(SP_ID,0) FROM BENCHSCENE INNER JOIN BENCHSCENEBUTTON  ON BS_CODE = SB_BSCODE LEFT JOIN SYSSPECIALPOWER ON SB_SPACTION =SSP_ACTION  AND SB_RELATIVECALLER = SSP_CALLER LEFT JOIN (SELECT * FROM SPECIALPOWER WHERE SP_EMID = ?) ON SSP_ID = SP_SSPID  WHERE BS_BCCODE = ? and nvl(bs_enable,0) <> 0 ORDER BY BS_DETNO,BS_TITLE,SB_DETNO";
			epid = emid;
			rs = baseDao.queryForRowSet(sql, epid, benchcode);
		}else {
			sql = "SELECT BS_TITLE,SB_TITLE,SB_ID,SB_RELATIVECALLER,SSP_CALLER,SB_ALIAS,SSP_ID,SSP_DESC,SB_SPACTION,NVL(SSP_VALID,0) FROM BENCHSCENE INNER JOIN BENCHSCENEBUTTON  ON BS_CODE = SB_BSCODE LEFT JOIN SYSSPECIALPOWER ON SB_SPACTION =SSP_ACTION  AND SB_RELATIVECALLER = SSP_CALLER  where BS_BCCODE = ? and nvl(bs_enable,0) <> 0 ORDER BY BS_DETNO,BS_TITLE,SB_DETNO";
			rs = baseDao.queryForRowSet(sql, benchcode);
		}
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("bs_title", rs.getString("bs_title"));
			if(!bstitle.equals(rs.getString("bs_title"))){
				bstitle = rs.getString("bs_title");
				groupnum ++;
			}
			map.put("groupnum", groupnum);
			if (rs.getGeneralInt(10)==0) {
				map.put("ok_", true);
			}else if(epid==0){
				map.put("ok_", false);
			}else{
				map.put("ok_", rs.getGeneralInt(11)==epid);
			}
			map.put("sb_title", rs.getString("sb_title"));			
			map.put("sb_id", rs.getGeneralInt("sb_id"));			
			map.put("sb_relativecaller", rs.getString("sb_relativecaller"));
			if (StringUtil.hasText(rs.getString("sb_alias"))&&rs.getString("sb_alias").indexOf("#")==-1) {
				map.put("sb_alias", rs.getString("sb_alias"));
			}else {
				String alias = baseDao.queryForObject("select fo_button4add from form where nvl(fo_dealurl,' ')<> ' ' and fo_button4add not like '%#%' and fo_caller = ?", String.class, rs.getString("sb_relativecaller"));
				if (StringUtil.hasText(alias)) {
					map.put("sb_alias", alias);
				}else{
					map.put("sb_alias", "");
				}
			}
			map.put("ssp_caller", rs.getString("ssp_caller"));	
			map.put("ssp_id", rs.getGeneralInt("ssp_id"));			
			map.put("ssp_desc", rs.getString("ssp_desc"));			
			map.put("sb_spaction", rs.getString("sb_spaction"));			
			map.put("ssp_valid", rs.getGeneralInt(10)==0?0:1);
			map.put("sp_id", rs.getGeneralInt(12));		
			list.add(map);
		}
		return list;
	}
	@Override
	@CacheEvict(value = { "button" }, allEntries = true)
	public void saveSceneBtnPowers(String benchcode,String joid, String emid, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String epid = null;
		if (joid!=null) {
			epid = joid;
		}
		if (emid!=null) {
			epid = emid;
		}
		List<String> sqls = new ArrayList<String>();
		try {
			for (Map<Object, Object> map : maps) {
				sqls.add("update BENCHSCENEBUTTON set SB_SPACTION = '"+map.get("sb_spaction")+"' where sb_id = "+map.get("sb_id"));
				int sspid = Integer.parseInt(map.get("ssp_id").toString());
				if (sspid>0) {
					boolean bool = baseDao.checkByCondition("SYSSPECIALPOWER", "SSP_ACTION = '"+map.get("sb_spaction")+"' AND SSP_CALLER = '"+map.get("sb_relativecaller")+"' AND SSP_ID="+sspid);
					if (bool) {
						sspid = 0;
					}
				}
				
				Map<Object,Object> map1 = new HashMap<Object, Object>();
				map1.put("ssp_action", map.get("sb_spaction"));
				map1.put("ssp_desc", map.get("ssp_desc"));
				map1.put("ssp_valid", map.get("ssp_valid"));
				if (sspid==0) {
					sspid = baseDao.getSeqId("SYSSPECIALPOWER_SEQ");
					map1.put("ssp_id", sspid);
					map1.put("ssp_caller", map.get("sb_relativecaller"));
					map1.put("ssp_business", 0);
					map1.put("ssp_button", map.get("sb_alias"));
					sqls.add(SqlUtil.getInsertSqlByFormStore(map1, "SYSSPECIALPOWER", new String[]{},new Object[]{}));
				}else {
					map1.put("ssp_id", sspid);
					sqls.add(SqlUtil.getUpdateSqlByFormStore(map1, "SYSSPECIALPOWER", "ssp_id"));
				}
				if(epid!=null&&Integer.parseInt(map.get("ssp_valid").toString())!=0&&Boolean.parseBoolean(map.get("ok_").toString())){
					String[] epids = epid.split(",");
					for (String id : epids) {
						String condition = "";
						if (joid!=null) {
							condition = "sp_sspid = "+sspid+" and sp_joid = "+id;
						}else {
							condition = "sp_sspid = "+sspid+" and sp_emid = "+id;
						}
						boolean bool = baseDao.checkByCondition("SPECIALPOWER", condition);
						if (bool) {
							Map<Object,Object> map2 = new HashMap<Object, Object>();
							map2.put("sp_sspid", sspid);
							if (joid!=null) {
								map2.put("sp_joid", id);
							}else {
								map2.put("sp_emid", id);
							}
							sqls.add(SqlUtil.getInsertSql(map2, "SPECIALPOWER", "sp_id"));
						}
					}
				}
				
				if (epid!=null&&!Boolean.parseBoolean(map.get("ok_").toString())) {
					String condition = "sp_sspid = "+sspid+" and "+(joid!=null?"sp_joid":"sp_emid")+ " in ("+epid+")";
					sqls.add("delete from SPECIALPOWER where "+condition);
				}
			}
			baseDao.execute(sqls);
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
	}

	@Override
	public List<RoleSpecialPower> getSpecialPowerByRole(String caller, Integer ro_id) {
		try {
			return baseDao.getJdbcTemplate().query(
					"select * from RoleSpecialPower where sp_sspid in (select ssp_id from SysSpecialPower where ssp_caller=?) and sp_roid=?",
					new BeanPropertyRowMapper<RoleSpecialPower>(RoleSpecialPower.class), caller, ro_id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
