package com.uas.erp.service.common.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.PathUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.DbfindSetDao;
import com.uas.erp.dao.common.DbfindSetGridDao;
import com.uas.erp.dao.common.DbfindSetUiDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.EnterpriseDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.SystemDao;
import com.uas.erp.model.DBFindSet;
import com.uas.erp.model.DBFindSetGrid;
import com.uas.erp.model.DBFindSetUI;
import com.uas.erp.model.DataDictionary;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.OracleObject;
import com.uas.erp.model.SysNavigation;
import com.uas.erp.service.common.SystemService;

@Service
public class SystemServiceImpl implements SystemService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FormDao formDao;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private DbfindSetUiDao dbfindSetUiDao;
	@Autowired
	private DbfindSetDao dbfindSetDao;
	@Autowired
	private DbfindSetGridDao dbfindSetGridDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private DataListDao dataListDao;
	@Autowired
	private SystemDao systemDao;
	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private EnterpriseDao enterpriseDao;

	final static String DB_LOCK = "select s.sid,s.serial#,s.machine,s.program,to_char(s.prev_exec_start, 'yyyy-mm-dd hh24:mi:ss') prev_exec_start,"
			+ "s.status,a.session_id,a.os_user_name,a.oracle_username,s.process,'alter system kill session '||''''||trim(s.sid)||','||trim(s.serial#)||'''' sql "
			+ "from v$locked_object a,v$session s where a.session_id=s.sid";

	/**
	 * 清除系统数据库锁定进程
	 */
	@Override
	public void killDbLock() {
		SqlRowList rs = baseDao.queryForRowSet(DB_LOCK);
		while (rs.next()) {
			baseDao.execute(rs.getString("sql"));
		}
	}

	/**
	 * 清除系统缓存
	 */
	@Override
	public void removeCache(String caches, boolean all) {
		// 约定cache名称格式统一是masterName.cacheRealName
		Collection<String> cacheNames = null;
		if (StringUtil.isEmpty(caches))
			cacheNames = cacheManager.getCacheNames();
		else
			cacheNames = Arrays.asList(caches.split(","));
		Iterator<String> iterator = cacheNames.iterator();
		while (iterator.hasNext())
			cacheManager.getCache(iterator.next()).clear();
	}

	public List<Map<String, Object>> getMyPatch() {
		File file = new File(PathUtil.getPatchPath());
		List<Map<String, Object>> explain = new ArrayList<Map<String, Object>>();
		File[] patchs = file.listFiles();
		for (File p : patchs) {
			if (p.isDirectory()) {
				String propDir = p.getAbsolutePath() + File.separator + "explain.properties";
				File prop = new File(propDir);
				if (prop.exists()) {
					explain.add(read(propDir));
				}
			}
		}
		return explain;
	}

	public static Map<String, Object> read(String path) {
		Map<String, Object> explain = new HashMap<String, Object>();
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(path));
			explain.put("version", prop.getProperty("version"));
			explain.put("name", prop.getProperty("name"));
			explain.put("desc", prop.getProperty("desc"));
			explain.put("enuu", prop.getProperty("enuu"));
			explain.put("date", new Date(Long.parseLong(prop.getProperty("date"))));
			explain.put("files", prop.getProperty("files"));
			explain.put("installed", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return explain;
	}

	private static boolean flag = true;

	/**
	 * 删除单个文件
	 * 
	 * @param sPath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public boolean deleteFile(String sPath) {
		flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 * 
	 * @param sPath
	 *            要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 */
	public boolean deleteFolder(String sPath) {
		flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(sPath);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}

	@Override
	public Object[] getSysNavigation(String caller, String path, String spath) {
		// 1.可能出现两帐套SN_ID不同，但其他配置一样
		// 2.可能出现两帐套描述不同，但其他配置一样
		String[] pid = path.split("/");
		String id = pid[pid.length - 1];
		String[] text = spath.split("/");
		SysNavigation current = baseDao.getJdbcTemplate().queryForObject("SELECT * FROM SysNavigation WHERE sn_id=?",
				new BeanPropertyRowMapper<SysNavigation>(SysNavigation.class), id);
		SysNavigation usoft = null;
		String error = null;
		String sob = SpObserver.getSp();
		try {
			SpObserver.putSp("uaserp");
			// 理想状况，直接通过叶节点ID得到优软配置
			usoft = baseDao.getJdbcTemplate().queryForObject("SELECT * FROM SysNavigation WHERE sn_id=?",
					new BeanPropertyRowMapper<SysNavigation>(SysNavigation.class), id);
		} catch (EmptyResultDataAccessException e) {
			// 按sn_displayname从上至下查找
			int _pid = 0;
			for (String s : text) {
				try {
					usoft = baseDao.getJdbcTemplate().queryForObject(
							"SELECT * FROM SysNavigation WHERE sn_displayname=? and sn_parentid=?",
							new BeanPropertyRowMapper<SysNavigation>(SysNavigation.class), s, _pid);
					_pid = usoft.getSn_Id();
				} catch (EmptyResultDataAccessException e1) {
					usoft = null;
					break;
				} catch (Exception e1) {
					usoft = null;
					break;
				}
			}
			// 按sn_parentid + caller
			if (usoft == null) {
				try {
					usoft = baseDao.getJdbcTemplate().queryForObject("SELECT * FROM SysNavigation WHERE sn_caller=? and sn_parentid=?",
							new BeanPropertyRowMapper<SysNavigation>(SysNavigation.class), caller, pid[pid.length - 2]);
				} catch (EmptyResultDataAccessException e1) {
					// 直接根据caller
					try {
						usoft = baseDao.getJdbcTemplate().queryForObject("SELECT * FROM SysNavigation WHERE sn_caller=?",
								new BeanPropertyRowMapper<SysNavigation>(SysNavigation.class), caller);
					} catch (EmptyResultDataAccessException e2) {
						error = "(无)";
					} catch (Exception e2) {
						error = "(系统错误)";
					}
				} catch (Exception e1) {
					error = "(系统错误)";
				}
			}
		} catch (CannotGetJdbcConnectionException e) {
			error = "无法连接到优软服务，请确保网络正常!";
		} catch (Exception e) {
			error = "(系统错误)";
		} finally {
			SpObserver.putSp(sob);
		}
		if (usoft == null)
			return new Object[] { error, current };
		else
			return new Object[] { usoft, current };
	}

	@Override
	public Object[] getForm(String usoftCaller, String currentCaller) {
		String sob = SpObserver.getSp();
		String error = null;
		Form current = formDao.getForm(currentCaller, sob);
		Form usoft = null;
		try {
			SpObserver.putSp("uaserp");
			usoft = formDao.getForm(usoftCaller, "uaserp");
		} catch (CannotGetJdbcConnectionException e) {
			error = "无法连接到优软服务，请确保网络正常!";
		} catch (Exception e) {
			error = "(系统错误)";
		} finally {
			SpObserver.putSp(sob);
		}
		if (usoft == null)
			return new Object[] { error, current };
		else
			return new Object[] { usoft, current };
	}

	@Override
	public Object[] getGrid(String usoftCaller, String currentCaller) {
		String sob = SpObserver.getSp();
		String error = null;
		List<DetailGrid> current = detailGridDao.getDetailGridsByCaller(currentCaller, sob);
		List<DetailGrid> usoft = null;
		try {
			SpObserver.putSp("uaserp");
			usoft = detailGridDao.getDetailGridsByCaller(usoftCaller, "uaserp");
		} catch (CannotGetJdbcConnectionException e) {
			error = "无法连接到优软服务，请确保网络正常!";
		} catch (Exception e) {
			error = "(系统错误)";
		} finally {
			SpObserver.putSp(sob);
		}
		if (usoft == null)
			return new Object[] { error, current };
		else
			return new Object[] { usoft, current };
	}

	@Override
	public Object[] getDbfindsetui(String usoftCaller, Object usoftForm, String currentCaller, Object currentForm) {
		List<DBFindSetUI> usoft = null;
		List<DBFindSetUI> current = null;
		String error = null;
		String sob = SpObserver.getSp();
		DBFindSetUI dbFind = null;
		Form form = null;
		if (currentForm != null && currentForm instanceof Form) {
			form = (Form) currentForm;
			current = new ArrayList<DBFindSetUI>();
			for (FormDetail detail : form.getFormDetails()) {
				// 这里不能根据detail.getFd_dbfind()来判断
				// 因为如果A和B的dbfind本来相同，但只是A的字段a1将fd_dbfind设置成了F,B的为T，则其实是Form配置的差异
				try {
					dbFind = dbfindSetUiDao.getDbFindSetUIByField(currentCaller, detail.getFd_field(), sob);
					if (dbFind != null) {
						current.add(dbFind);
					}
				} catch (Exception e) {

				}
			}
		}
		if (usoftForm != null && usoftForm instanceof Form) {
			form = (Form) usoftForm;
			usoft = new ArrayList<DBFindSetUI>();
			try {
				SpObserver.putSp("uaserp");
				for (FormDetail detail : form.getFormDetails()) {
					try {
						dbFind = dbfindSetUiDao.getDbFindSetUIByField(usoftCaller, detail.getFd_field(), "uaserp");
						if (dbFind != null) {
							usoft.add(dbFind);
						}
					} catch (Exception e) {

					}
				}
			} catch (CannotGetJdbcConnectionException e) {
				error = "无法连接到优软服务，请确保网络正常!";
			} catch (Exception e) {
				error = "(系统错误)";
			} finally {
				SpObserver.putSp(sob);
			}
		}
		if (usoft == null)
			return new Object[] { error, current };
		else
			return new Object[] { usoft, current };
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getDbfindset(String usoftCaller, Object usoftGrid, String currentCaller, Object currentGrid) {
		List<DBFindSet> usoft = null;
		List<DBFindSet> current = null;
		String error = null;
		String sob = SpObserver.getSp();
		DBFindSet dbFindSet = null;
		List<DetailGrid> grid = null;
		if (currentGrid != null) {
			grid = (List<DetailGrid>) currentGrid;
			current = new ArrayList<DBFindSet>();
			for (DetailGrid detail : grid) {
				String db = detail.getDg_findfunctionname();
				if (db != null && db.trim().length() > 0) {
					dbFindSet = dbfindSetDao.getDbfind(db.split("|")[0], sob);
					if (dbFindSet != null)
						current.add(dbFindSet);
				}
			}
		}
		if (usoftGrid != null) {
			grid = (List<DetailGrid>) usoftGrid;
			usoft = new ArrayList<DBFindSet>();
			try {
				SpObserver.putSp("uaserp");
				for (DetailGrid detail : grid) {
					String db = detail.getDg_findfunctionname();
					if (db != null && db.trim().length() > 0) {
						dbFindSet = dbfindSetDao.getDbfind(db.split("|")[0], "uaserp");
						if (dbFindSet != null)
							usoft.add(dbFindSet);
					}
				}
			} catch (CannotGetJdbcConnectionException e) {
				error = "无法连接到优软服务，请确保网络正常!";
			} catch (Exception e) {
				error = "(系统错误)";
			} finally {
				SpObserver.putSp(sob);
			}
		}
		if (usoft == null)
			return new Object[] { error, current };
		else
			return new Object[] { usoft, current };
	}

	@Override
	public Object[] getDbfindsetgrid(String usoftCaller, String currentCaller) {
		List<DBFindSetGrid> usoft = null;
		List<DBFindSetGrid> current = dbfindSetGridDao.getDbFindSetGridsByCaller(currentCaller);
		String error = null;
		String sob = SpObserver.getSp();
		try {
			SpObserver.putSp("uaserp");
			usoft = dbfindSetGridDao.getDbFindSetGridsByCaller(usoftCaller);
		} catch (CannotGetJdbcConnectionException e) {
			error = "无法连接到优软服务，请确保网络正常!";
		} catch (Exception e) {
			error = "(系统错误)";
		} finally {
			SpObserver.putSp(sob);
		}
		if (usoft == null)
			return new Object[] { error, current };
		else
			return new Object[] { usoft, current };
	}

	@Override
	public Object[] getDatalistCombo(String usoftCaller, String currentCaller) {
		List<DataListCombo> usoft = null;
		String sob = SpObserver.getSp();
		List<DataListCombo> current = dataListComboDao.getComboxsByCaller(currentCaller, sob);
		String error = null;
		try {
			SpObserver.putSp("uaserp");
			usoft = dataListComboDao.getComboxsByCaller(usoftCaller, "uaserp");
		} catch (CannotGetJdbcConnectionException e) {
			error = "无法连接到优软服务，请确保网络正常!";
		} catch (Exception e) {
			error = "(系统错误)";
		} finally {
			SpObserver.putSp(sob);
		}
		if (usoft == null)
			return new Object[] { error, current };
		else
			return new Object[] { usoft, current };
	}

	@Override
	public Object[] getDatalist(String usoftCaller, String currentCaller) {
		String error = null;
		String sob = SpObserver.getSp();
		DataList usoft = null;
		DataList current = dataListDao.getDataList(currentCaller, sob);
		try {
			SpObserver.putSp("uaserp");
			usoft = dataListDao.getDataList(usoftCaller, "uaserp");
		} catch (CannotGetJdbcConnectionException e) {
			error = "无法连接到优软服务，请确保网络正常!";
		} catch (Exception e) {
			error = "(系统错误)";
		} finally {
			SpObserver.putSp(sob);
		}
		if (usoft == null)
			return new Object[] { error, current };
		else
			return new Object[] { usoft, current };
	}

	@Override
	public Object[] getTableDesc(Set<String> tableNames) {
		List<Object> current = new ArrayList<Object>();
		List<Object> obj = null;
		for (String tableName : tableNames) {
			try {
				obj = baseDao.getJdbcTemplate().query("select * from user_tab_columns where table_name=?",
						new BeanPropertyRowMapper<Object>(Object.class), tableName.toUpperCase());

			} catch (EmptyResultDataAccessException e) {
				obj = null;
			} catch (Exception e) {
				obj = null;
			}
			if (obj != null) {
				current.add(obj);
			}
		}
		String sob = SpObserver.getSp();
		List<Object> usoft = null;
		String error = null;
		try {
			SpObserver.putSp("uaserp");
			usoft = new ArrayList<Object>();
			for (String tableName : tableNames) {
				try {
					obj = baseDao.getJdbcTemplate().query("select * from user_tab_columns where table_name=?",
							new BeanPropertyRowMapper<Object>(Object.class), tableName.toUpperCase());

				} catch (EmptyResultDataAccessException e) {
					obj = null;
				} catch (Exception e) {
					obj = null;
				}
				if (obj != null) {
					usoft.add(obj);
				}
			}
		} catch (CannotGetJdbcConnectionException e) {
			error = "无法连接到优软服务，请确保网络正常!";
		} catch (Exception e) {
			error = "(系统错误)";
		} finally {
			SpObserver.putSp(sob);
		}
		if (usoft == null)
			return new Object[] { error, current };
		else
			return new Object[] { usoft, current };
	}

	@Override
	public Object[] getDataDictionary(Set<String> tableNames) {
		List<DataDictionary> usoft = null;
		List<DataDictionary> current = new ArrayList<DataDictionary>();
		DataDictionary dictionary = null;
		for (String tableName : tableNames) {
			try {
				dictionary = baseDao.getJdbcTemplate().queryForObject("select * from datadictionary where dd_tablename=?",
						new BeanPropertyRowMapper<DataDictionary>(DataDictionary.class), tableName.toUpperCase());
				dictionary.setDataDictionaryDetails(baseDao.getDataDictionaryDetails(tableName));
				current.add(dictionary);
			} catch (EmptyResultDataAccessException e) {

			} catch (Exception e) {

			}
		}
		String sob = SpObserver.getSp();
		String error = null;
		try {
			SpObserver.putSp("uaserp");
			usoft = new ArrayList<DataDictionary>();
			for (String tableName : tableNames) {
				try {
					dictionary = baseDao.getJdbcTemplate().queryForObject("select * from datadictionary where dd_tablename=?",
							new BeanPropertyRowMapper<DataDictionary>(DataDictionary.class), tableName.toUpperCase());
					dictionary.setDataDictionaryDetails(baseDao.getDataDictionaryDetails(tableName));
					usoft.add(dictionary);
				} catch (EmptyResultDataAccessException e) {

				} catch (Exception e) {

				}
			}
		} catch (CannotGetJdbcConnectionException e) {
			error = "无法连接到优软服务，请确保网络正常!";
		} catch (Exception e) {
			error = "(系统错误)";
		} finally {
			SpObserver.putSp(sob);
		}
		if (usoft == null)
			return new Object[] { error, current };
		else
			return new Object[] { usoft, current };
	}

	@Override
	public Object[] getTriggers(Set<String> tableNames) {
		List<OracleObject.Trigger> usoft = null;
		List<OracleObject.Trigger> current = new ArrayList<OracleObject.Trigger>();
		List<OracleObject.Trigger> obj = null;
		for (String tableName : tableNames) {
			try {
				obj = baseDao.getJdbcTemplate().query("select * from user_triggers where table_name=?",
						new BeanPropertyRowMapper<OracleObject.Trigger>(OracleObject.Trigger.class), tableName.toUpperCase());
				current.addAll(obj);
			} catch (EmptyResultDataAccessException e) {

			} catch (Exception e) {

			}
		}
		String sob = SpObserver.getSp();
		String error = null;
		try {
			SpObserver.putSp("uaserp");
			usoft = new ArrayList<OracleObject.Trigger>();
			for (String tableName : tableNames) {
				try {
					obj = baseDao.getJdbcTemplate().query("select * from user_triggers where table_name=?",
							new BeanPropertyRowMapper<OracleObject.Trigger>(OracleObject.Trigger.class), tableName.toUpperCase());
					usoft.addAll(obj);
				} catch (EmptyResultDataAccessException e) {

				} catch (Exception e) {

				}
			}
		} catch (CannotGetJdbcConnectionException e) {
			error = "无法连接到优软服务，请确保网络正常!";
		} catch (Exception e) {
			error = "(系统错误)";
		} finally {
			SpObserver.putSp(sob);
		}
		if (usoft == null)
			return new Object[] { error, current };
		else
			return new Object[] { usoft, current };
	}

	@Override
	public Object[] getIndexes(Set<String> tableNames) {
		List<OracleObject.Index> usoft = null;
		List<OracleObject.Index> current = new ArrayList<OracleObject.Index>();
		List<OracleObject.Index> obj = null;
		for (String tableName : tableNames) {
			try {
				obj = baseDao.getJdbcTemplate().query("select * from user_indexes where table_name=?",
						new BeanPropertyRowMapper<OracleObject.Index>(OracleObject.Index.class), tableName.toUpperCase());
				current.addAll(obj);
			} catch (EmptyResultDataAccessException e) {

			} catch (Exception e) {

			}
		}
		String sob = SpObserver.getSp();
		String error = null;
		try {
			SpObserver.putSp("uaserp");
			usoft = new ArrayList<OracleObject.Index>();
			for (String tableName : tableNames) {
				try {
					obj = baseDao.getJdbcTemplate().query("select * from user_indexes where table_name=?",
							new BeanPropertyRowMapper<OracleObject.Index>(OracleObject.Index.class), tableName.toUpperCase());
					usoft.addAll(obj);
				} catch (EmptyResultDataAccessException e) {

				} catch (Exception e) {

				}
			}
		} catch (CannotGetJdbcConnectionException e) {
			error = "无法连接到优软服务，请确保网络正常!";
		} catch (Exception e) {
			error = "(系统错误)";
		} finally {
			SpObserver.putSp(sob);
		}
		if (usoft == null)
			return new Object[] { error, current };
		else
			return new Object[] { usoft, current };
	}

	final static String SYS_SEQUENCE_NUMBER = "declare v_return varchar2(1000); v_tab varchar2(30); v_pk varchar2(30); cursor cur_tab_pk is select cu.table_name,cu.column_name from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.constraint_type = 'P' and instr(cu.table_name,'$')=0 order by cu.table_name; begin open cur_tab_pk; loop fetch cur_tab_pk into v_tab,v_pk; EXIT WHEN cur_tab_pk%notfound; SYS_CHECK_SEQUENCE(v_tab, v_pk, v_return); end loop; end;";

	@Override
	public String updateSeqNumber() {
		try {
			baseDao.getJdbcTemplate().execute(SYS_SEQUENCE_NUMBER);
		} catch (Exception e) {
			return e.getMessage();
		}
		return null;
	}

	final static String SYS_CODE_NUMBER = "declare v_cal varchar2(40);v_sql varchar2(300);v_tab varchar2(150);v_lead varchar2(10);v_num varchar2(20);v_codefield varchar2(30);v_code varchar2(50);v_maxcode varchar2(50);v_err varchar2(100);cursor cur_maxnum is select mn_tablename,mn_leadcode,mn_number,fo_table,fo_codefield from maxnumbers left join form on fo_caller=mn_tablename where fo_codefield like '%code' and (case when instr(fo_table,' ')=0 then upper(fo_table) else upper(substr(fo_table,1,instr(fo_table,' ')-1)) end,upper(fo_codefield)) in(select table_name,column_name from user_tab_columns);begin open cur_maxnum; loop fetch cur_maxnum into v_cal,v_lead,v_num,v_tab,v_codefield; EXIT WHEN cur_maxnum%notfound; if nvl(v_lead,' ')<>' ' then v_code := v_lead || substr(v_num,3);v_sql := 'select max(' || v_codefield || ') from ' || v_tab || ' where length(' || v_codefield || ')>=10 and REGEXP_LIKE(' || v_codefield || ', ''^' || v_lead || '[0-9]+$'') and ' || v_codefield || ' > ''' || v_code || '''';else v_code := v_num;v_sql := 'select max(' || v_codefield || ') from ' || v_tab || ' where length(' || v_codefield || ')>=10 and REGEXP_LIKE(' || v_codefield || ', ''^[1-9][0-9]+$'') and ' || v_codefield || ' > ''' || v_code || '''';end if;execute immediate v_sql into v_maxcode;if v_maxcode is not null then if nvl(v_lead,' ')<>' ' then update maxnumbers set mn_number='20'||replace(v_maxcode, v_lead, '') where mn_tablename=v_cal;else update maxnumbers set mn_number=v_maxcode where mn_tablename=v_cal;end if;end if;end loop;close cur_maxnum;COMMIT;end;";

	@Override
	public String updateMaxnum() {
		try {
			baseDao.getJdbcTemplate().execute(SYS_CODE_NUMBER);
		} catch (Exception e) {
			return e.getMessage();
		}
		return null;
	}

	@Override
	public Map<String, Object> getSvnLogs(Integer page, Integer limit, String filter) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("count", String.valueOf(limit));
		params.put("page", String.valueOf(page));
		params.put("filter", filter);
		params.put("sorting", "{\"version\":\"DESC\"}");
		try {
			Response response = HttpUtil.sendGetRequest(Constant.manageHost() + "/public/develop/svnlog/uas", params, true);
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				return FlexJsonUtil.fromJson(response.getResponseText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getSvnVersion() {
		try {
			Response response = HttpUtil.sendGetRequest(Constant.manageHost() + "/public/develop/svnlog/uas/last", null, true);
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				return response.getResponseText();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void saveReDoLog(String url, String params, Employee employee) {
		// 记录操作

	}

}