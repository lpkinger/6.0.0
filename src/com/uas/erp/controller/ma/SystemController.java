package com.uas.erp.controller.ma;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.JSONUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.listener.UserOnlineListener;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.DBFindSet;
import com.uas.erp.model.DBFindSetUI;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DataListDetail;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.SysNavigation;
import com.uas.erp.model.SysUpdates;
import com.uas.erp.model.UserSession;
import com.uas.erp.service.common.SystemService;
import com.uas.erp.service.common.UpgradeService;

@Controller
public class SystemController {

	@Autowired
	private SystemService systemService;
	@Autowired
	private UpgradeService upgradeService;

	/**
	 * 清除数据库锁定进程
	 */
	@RequestMapping("/ma/kill_dblock.action")
	@ResponseBody
	public Map<String, Object> kill_dblock() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		systemService.killDbLock();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 清除系统缓存
	 */
	@RequestMapping("/ma/kill_cache.action")
	@ResponseBody
	public Map<String, Object> kill_cache(String caches, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		systemService.removeCache(caches, all == null ? false : true);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新数据库序列LAST_NUMBER
	 */
	@RequestMapping("/ma/update_seq.action")
	@ResponseBody
	public Map<String, Object> updateSeq(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		systemService.updateSeqNumber();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新数据库编号MaxNumners
	 */
	@RequestMapping("/ma/update_maxnum.action")
	@ResponseBody
	public Map<String, Object> updateMaxnum(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		systemService.updateMaxnum();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 在线用户列表
	 */
	@RequestMapping("/ma/user/online.action")
	@ResponseBody
	public Map<String, Object> getOnlineList(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Set<UserSession> users = UserOnlineListener.getOnLineList();
		Set<UserSession> myUsers = new HashSet<UserSession>();
		String current = SystemSession.getUser().getCurrentMaster().getMa_name();
		for (UserSession user : users) {
			if (current.equals(user.getSob()))
				myUsers.add(user);
		}
		modelMap.put("data", myUsers);
		return modelMap;
	}

	/**
	 * 锁定在线用户
	 */
	@RequestMapping("/ma/user/lock.action")
	@ResponseBody
	public Map<String, Object> lockOnline(HttpSession session, String sid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		UserOnlineListener.lock(sid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 记录重复请求日志
	 * */
	@RequestMapping("/ma/saveReDoLog.action")
	@ResponseBody
	public Map<String, Object> saveReDoLog(HttpSession session, String url, String params) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		systemService.saveReDoLog(url, params, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 软件日志
	 */
	@RequestMapping("/ma/program/log.action")
	@ResponseBody
	public Map<String, Object> getSvnLogs(String condition, Integer page, Integer limit) {
		condition = JSONUtil.decodeUnicode(condition);
		return systemService.getSvnLogs(page, limit, condition);
	}

	/**
	 * 软件版本信息
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/ma/program/version.action")
	@ResponseBody
	public Map<String, Object> getSvnVersion(HttpSession session) throws IOException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Resource resource = ContextUtil.getApplicationContext().getResource("classpath:VERSION");
		if (resource.exists()) {
			// 当前版本
			String version = StringUtil.trimBlankChars(FileUtils.readFileToString(resource.getFile(), "UTF-8"));
			modelMap.put("active", version);
		}
		modelMap.put("newest", systemService.getSvnVersion());
		return modelMap;
	}

	/**
	 * 系统更新程序
	 */
	@RequestMapping("/ma/upgrade/plan.action")
	@ResponseBody
	public Map<String, Object> getUpgradePlan(String condition, Integer page, Integer limit) {
		condition = JSONUtil.decodeUnicode(condition);
		return upgradeService.getUpgradePlans(page, limit, condition);
	}

	/**
	 * 系统更新日志
	 */
	@RequestMapping("/ma/upgrade/log.action")
	@ResponseBody
	public List<SysUpdates> getUpgradeLog(String planIds) {
		return upgradeService.getUpgradeLog(planIds.split(","));
	}

	/**
	 * 系统更新
	 */
	@RequestMapping("/ma/upgrade.action")
	@ResponseBody
	public boolean upgradeByPlan(String planId, String type, int version) {
		boolean success = upgradeService.upgrade(planId, type, version);
		if (success)
			// 成功升级后，要清除系统缓存
			systemService.removeCache(null, false);
		return success;
	}

	/**
	 * 系统配置同步
	 */
	@RequestMapping("/ma/sync.action")
	@ResponseBody
	public Map<String, Object> sync(HttpSession session, String caller, String path, String spath) {
		// 先清除缓存
		systemService.removeCache(null, false);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 1.SysNavigation
		Object[] nav = systemService.getSysNavigation(caller, path, spath);
		modelMap.put("navigation", nav);
		String usoftCaller = caller;
		if (nav[0] instanceof SysNavigation) {
			SysNavigation sn = (SysNavigation) nav[0];
			usoftCaller = sn.getSn_caller();
			if (usoftCaller == null || "".equals(usoftCaller))
				usoftCaller = caller;
		}
		// 2.Form
		Object[] fs = systemService.getForm(usoftCaller, caller);
		modelMap.put("form", fs);
		// 3.Grid
		Object[] gs = systemService.getGrid(usoftCaller, caller);
		modelMap.put("detailgrid", gs);
		// 4.Form dbFind
		Object[] dbfindsetui = null;
		if (fs[0] != null || fs[1] != null) {
			dbfindsetui = systemService.getDbfindsetui(usoftCaller, fs[0], caller, fs[1]);
			modelMap.put("dbfindsetui", dbfindsetui);
		} else {
			modelMap.put("dbfindsetui", null);
		}
		// 5.Grid dbFind
		Object[] dbfindset = null;
		if (gs[0] != null || gs[1] != null) {
			dbfindset = systemService.getDbfindset(usoftCaller, gs[0], caller, gs[1]);
			modelMap.put("dbfindset", dbfindset);
		} else {
			modelMap.put("dbfindset", null);
		}
		// 6.Grid dbFind 对应关系
		if (gs[0] != null || gs[1] != null) {
			modelMap.put("dbfindsetgrid", systemService.getDbfindsetgrid(usoftCaller, caller));
		} else {
			modelMap.put("dbfindsetgrid", null);
		}
		// 7.DataListCombo
		modelMap.put("datalistcombo", systemService.getDatalistCombo(usoftCaller, caller));
		// 8.DataList
		Object[] datalist = systemService.getDatalist(usoftCaller, caller);
		modelMap.put("datalist", datalist);
		// 9.Table (上诉所有可能用到的表都需要校验表结构)
		// 10.DataDictionary (按Table)
		// 11.Trigger (按table，关联trigger)
		// 12.Index (按table，关联index)
		Set<String> tableNames = new HashSet<String>();
		tableNames.addAll(getAllTablesFromForm(fs));
		tableNames.addAll(getAllTablesFromGrid(gs));
		tableNames.addAll(getAllTablesFromDbfindSetUI(dbfindsetui));
		tableNames.addAll(getAllTablesFromDbfindSet(dbfindset));
		tableNames.addAll(getAllTablesFromDatalist(datalist));
		if (tableNames.size() > 0) {
			modelMap.put("table", systemService.getTableDesc(tableNames));
			modelMap.put("datadictionary", systemService.getDataDictionary(tableNames));
			modelMap.put("trigger", systemService.getTriggers(tableNames));
			modelMap.put("index", systemService.getIndexes(tableNames));
		} else {
			modelMap.put("table", null);
			modelMap.put("datadictionary", null);
			modelMap.put("trigger", null);
			modelMap.put("index", null);
		}
		modelMap.put("success", true);
		return modelMap;
	}

	private Set<String> getAllTablesFromForm(Object[] forms) {
		if (forms != null) {
			Set<String> tableNames = new HashSet<String>();
			String tableName = null;
			for (Object obj : forms) {
				if (obj != null) {
					// Form
					Form form = (Form) obj;
					tableName = form.getFo_table();
					tableNames.addAll(parseTableName(tableName));
					// FormDetail
					for (FormDetail detail : form.getFormDetails()) {
						tableNames.addAll(parseTableName(detail.getFd_table()));
					}
				}
			}
			return tableNames;
		} else {
			return null;
		}
	}

	private Set<String> parseTableName(String tableName) {
		Set<String> tableNames = new HashSet<String>();
		if (tableName != null) {
			tableName = tableName.toLowerCase();
			if (tableName.indexOf(" left join ") > 0) {
				String[] strs = tableName.split("left join");
				for (String s : strs) {
					if (s != null) {
						s = s.replaceFirst("\\s*", "");
						if (s.indexOf(" on ") > 0) {
							tableNames.add(s.substring(0, s.indexOf(" on ")).trim());
						} else {
							tableNames.add(s.split(" ")[0].trim());
						}
					}
				}
			} else if (tableName.indexOf(",") > 0) {
				String[] strs = tableName.split(",");
				for (String s : strs) {
					if (s != null) {
						tableNames.add(s.trim());
					}
				}
			} else {
				tableNames.add(tableName);
			}
		}
		return tableNames;
	}

	@SuppressWarnings("unchecked")
	private Set<String> getAllTablesFromGrid(Object[] grids) {
		if (grids != null) {
			Set<String> tableNames = new HashSet<String>();
			for (Object obj : grids) {
				if (obj != null) {
					List<DetailGrid> details = (List<DetailGrid>) obj;
					for (DetailGrid detail : details) {
						tableNames.addAll(parseTableName(detail.getDg_table()));
					}
				}
			}
			return tableNames;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private Set<String> getAllTablesFromDbfindSetUI(Object[] dbfindsetui) {
		if (dbfindsetui != null) {
			Set<String> tableNames = new HashSet<String>();
			for (Object obj : dbfindsetui) {
				if (obj != null) {
					List<DBFindSetUI> sets = (List<DBFindSetUI>) obj;
					for (DBFindSetUI set : sets) {
						tableNames.addAll(parseTableName(set.getDs_tables()));
						tableNames.addAll(parseTableName(set.getDs_whichdbfind()));
					}
				}
			}
			return tableNames;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private Set<String> getAllTablesFromDbfindSet(Object[] dbfindset) {
		if (dbfindset != null) {
			Set<String> tableNames = new HashSet<String>();
			for (Object obj : dbfindset) {
				if (obj != null) {
					List<DBFindSet> sets = (List<DBFindSet>) obj;
					for (DBFindSet set : sets) {
						tableNames.addAll(parseTableName(set.getDs_tablename()));
					}
				}
			}
			return tableNames;
		} else {
			return null;
		}
	}

	private Set<String> getAllTablesFromDatalist(Object[] datalist) {
		if (datalist != null) {
			Set<String> tableNames = new HashSet<String>();
			for (Object obj : datalist) {
				if (obj != null) {
					DataList set = (DataList) obj;
					tableNames.addAll(parseTableName(set.getDl_tablename()));
					// DataListDetail
					for (DataListDetail detail : set.getDataListDetails()) {
						tableNames.addAll(parseTableName(detail.getDld_table()));
					}
				}
			}
			return tableNames;
		} else {
			return null;
		}
	}
}
