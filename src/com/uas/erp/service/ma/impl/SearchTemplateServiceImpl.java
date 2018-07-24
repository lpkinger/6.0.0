package com.uas.erp.service.ma.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ClassUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.SearchDao;
import com.uas.erp.model.DataDictionaryDetail;
import com.uas.erp.model.DataRelation;
import com.uas.erp.model.Employee;
import com.uas.erp.model.SearchTemplate;
import com.uas.erp.service.common.SystemService;
import com.uas.erp.service.ma.SearchTemplateService;

@Service
public class SearchTemplateServiceImpl implements SearchTemplateService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SearchDao searchDao;
	@Autowired
	private SystemService systemService;

	@Override
	@CacheEvict(value = "searchtemplate", allEntries = true)
	public void save(String caller, String title, String datas, String condition, String sorts, String limits) {
		int st_id = baseDao.getSeqId("SearchTemplate_seq");
		Employee employee = SystemSession.getUser();
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(datas);
		List<Map<Object, Object>> props = new ArrayList<Map<Object, Object>>();
		for (Map<Object, Object> m : maps) {
			m.put("stg_stid", st_id);
			if (m.containsKey("modeItems")) {
				@SuppressWarnings("unchecked")
				List<Map<Object, Object>> items = (List<Map<Object, Object>>) m.get("modeItems");
				props.addAll(items);
				for (Map<Object, Object> item : items) {
					item.put("st_id", st_id);
				}
				m.remove("modeItems");
			}
		}
		List<String> sqls = SqlUtil.getInsertSqlbyGridStore(maps, "SearchTemplateGrid");
		sqls.addAll(SqlUtil.getInsertSqlbyGridStore(props, "SearchTemplateProp"));
		sqls.add("insert into SearchTemplate(st_id,st_caller,st_title,st_man,st_date,st_condition,st_sorts,st_limits) values(" + st_id
				+ ",'" + caller + "','" + title + "','" + employee.getEm_name() + "',sysdate,"
				+ (condition == null ? "null" : "'" + condition.replace("'", "''") + "'") + ","
				+ (sorts == null ? "null" : "'" + sorts.replace("'", "''") + "'") + ","
				+ (limits == null ? "null" : "'" + limits.replace("'", "''") + "'") + ")");
		sqls.add("update SearchTemplate set st_detno=nvl((select max(st_detno) from SearchTemplate where st_caller='" + caller
				+ "'),0)+1 where st_id=" + st_id);
		sqls.add("update searchtemplate set st_usedtable=(select wmsys.wm_concat(tab) from (select distinct stg_table as tab from searchtemplategrid where stg_stid="
				+ st_id
				+ " union select distinct stg_tokentab1 as tab from searchtemplategrid where stg_stid="
				+ st_id
				+ " and stg_tokentab1 is not null union select distinct stg_tokentab2 as tab from searchtemplategrid where stg_stid="
				+ st_id + " and stg_tokentab2 is not null" + ")) where st_id=" + st_id);
		sqls.add("update searchtemplate set st_tablesql=tablerelation.getSql(st_usedtable) where st_id=" + st_id);
		baseDao.execute(sqls);
	}

	@Override
	public List<SearchTemplate> getSearchTemplates(String caller) {
		return searchDao.getSearchTemplates(caller, SpObserver.getSp());
	}

	@Override
	public List<SearchTemplate> exportSearchTemplates(String caller) {
		List<SearchTemplate> templates = searchDao.getSearchTemplates(caller, SpObserver.getSp());
		if (null != templates) {
			// 考虑到DataRelation可能也需要用到
			for (SearchTemplate template : templates) {
				String tabs = SqlUtil.splitToSqlString(template.getSt_usedtable());
				List<DataRelation> relations = baseDao.query("select * from DataRelation where table_name_x in (" + tabs
						+ ") and table_name_y in (" + tabs + ")", DataRelation.class);
				template.setRelations(relations);
			}
		}
		return templates;
	}

	@Override
	@CacheEvict(value = "searchtemplate", allEntries = true)
	public void update(String caller, Integer sId, String datas, String condition, String sorts, String limits, String preHook) {
		String man = baseDao.getJdbcTemplate().queryForObject("select st_man from SearchTemplate where st_id=?", String.class, sId);
		Employee employee = SystemSession.getUser();
		if (!"admin".equals(employee.getEm_type()) && !employee.getEm_name().equals(man)) {
			BaseUtil.showError("请联系管理员，查询方案只允许管理员修改!");
		}
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(datas);
		List<Map<Object, Object>> props = new ArrayList<Map<Object, Object>>();
		for (Map<Object, Object> m : maps) {
			if (m.containsKey("modeItems")) {
				@SuppressWarnings("unchecked")
				List<Map<Object, Object>> items = (List<Map<Object, Object>>) m.get("modeItems");
				props.addAll(items);
				m.remove("modeItems");
			}
		}
		List<String> sqls = SqlUtil.getInsertSqlbyGridStore(maps, "SearchTemplateGrid");
		sqls.addAll(SqlUtil.getInsertSqlbyGridStore(props, "SearchTemplateProp"));
		sqls.add(0, "delete from SearchTemplateGrid where stg_stid=" + sId);
		sqls.add(0, "delete from SearchTemplateProp where st_id=" + sId);
		sqls.add("update searchtemplate set st_condition=" + (condition == null ? "null" : "'" + condition.replace("'", "''") + "'")
				+ ",st_sorts=" + (sorts == null ? "null" : "'" + sorts.replace("'", "''") + "'") + ",st_limits="
				+ (limits == null ? "null" : "'" + limits.replace("'", "''") + "'") + ",pre_hook="
				+ (preHook == null ? "null" : "'" + preHook.replace("'", "''") + "'") + " where st_id=" + sId);
		sqls.add("update searchtemplate set st_usedtable=(select wmsys.wm_concat(tab) from (select distinct stg_table as tab from searchtemplategrid where stg_stid="
				+ sId
				+ " union select distinct stg_tokentab1 as tab from searchtemplategrid where stg_stid="
				+ sId
				+ " and stg_tokentab1 is not null union select distinct stg_tokentab2 as tab from searchtemplategrid where stg_stid="
				+ sId
				+ " and stg_tokentab2 is not null" + ")) where st_id=" + sId);
		sqls.add("update searchtemplate set st_tablesql=tablerelation.getSql(st_usedtable) where st_id=" + sId);
		baseDao.execute(sqls);
	}

	@Override
	@CacheEvict(value = "searchtemplate", allEntries = true)
	public void delete(String caller, Integer sId) {
		String man = baseDao.getJdbcTemplate().queryForObject("select st_man from SearchTemplate where st_id=?", String.class, sId);
		if ("标准".equals(man)) {
			BaseUtil.showError("标准的查询方案不允许删除!");
		}
		Employee employee = SystemSession.getUser();
		if (!"admin".equals(employee.getEm_type()) && !employee.getEm_name().equals(man)) {
			BaseUtil.showError("请联系管理员，查询方案只允许管理员删除!");
		}
		baseDao.execute("delete from SearchTemplateProp where st_id=?", sId);
		baseDao.execute("delete from SearchTemplateGrid where stg_stid=?", sId);
		baseDao.execute("delete from SearchTemplate where st_id=?", sId);
	}

	@Override
	public Integer getLastSearchLog(String caller) {
		return baseDao.getJdbcTemplate().queryForObject("select max(sl_stid) from searchlog where sl_caller=? and sl_emid=?",
				Integer.class, caller, SystemSession.getUser().getEm_id());
	}

	@Override
	public void log(String caller, Integer sId) {
		Employee employee = SystemSession.getUser();
		baseDao.execute("delete from searchlog where sl_caller=? and sl_emid=?", caller, employee.getEm_id());
		baseDao.execute("insert into searchlog(sl_caller,sl_stid,sl_emid)values(?,?,?)", caller, sId, employee.getEm_id());
	}

	@SuppressWarnings("unchecked")
	private Object[] parseHookArguments(String[] arguments, Map<String, Object> filter) {
		// 涉及到组合字段，values.length ≠ arguments.length
		Object[] values = new Object[] {};
		if (null != arguments && arguments.length > 0) {
			Object value;
			for (String arg : arguments) {
				if (arg.charAt(0) == ':') {// 筛选参数
					value = filter.get(arg.substring(1));
					if (null != value && value instanceof Map) {// 这里是有序LinkedHashMap
						Map<String, Object> valMap = (Map<String, Object>) value;
						for (Object val : valMap.values()) {
							values = Arrays.copyOf(values, values.length + 1);
							values[values.length - 1] = val;
						}
						continue;
					}
				} else {// 固定常量参数
					value = arg;
				}
				values = Arrays.copyOf(values, values.length + 1);
				values[values.length - 1] = value;
			}
		}
		return values;
	}

	/**
	 * 执行查询前钩子
	 */
	private void execPreHook(SearchTemplate template, Map<String, Object> filter) throws Exception {
		String hook = template.getPre_hook();
		if (null != hook) {
			// java:beanName.methodName(:ARG_1,CONST_ARG_2,:ARG_3)
			// procedure:procedureName(:ARG_1,CONST_ARG_2,:ARG_3)
			Pattern pattern = Pattern.compile("(.+):(.+)\\((.*)\\)");
			Matcher matcher = pattern.matcher(hook);
			if (matcher.find()) {
				String type = matcher.group(1);
				String execName = matcher.group(2);
				String arguments = matcher.group(3);
				Object[] values = parseHookArguments(arguments.split(","), filter);
				if ("java".equals(type)) {
					// 例如：searchTemplateServiceImpl
					String beanName = execName.split("\\.")[0];
					// 例如：oneMethod
					String methodName = execName.split("\\.")[1];
					Object bean = ContextUtil.getBean(beanName);
					if (null != bean) {
						// 常量参数类型统一String，其余的按实际值类型
						Method method = bean.getClass().getMethod(methodName, ClassUtil.getObjectsClasses(values));
						method.invoke(bean, values);
					}
				} else if ("procedure".equals(type)) {
					baseDao.procedure(execName, values);
				}
			}
		}
	}

	@Override
	public SqlRowList getData(Integer sId, Map<String, Object> filter, String sorts, Integer start, Integer end) {
		SearchTemplate template = searchDao.getSearchTemplate(sId, SpObserver.getSp());
		String limitCondition = parseLimits(template.getSt_limits());
		String filterCondition = SqlUtil.parseFilter(filter);
		String condition = template.getSt_condition();
		if (!StringUtils.isEmpty(filterCondition)) {
			if (!StringUtils.isEmpty(condition)) {
				condition = "(" + condition + ") AND (" + filterCondition + ")";
			} else
				condition = filterCondition;
		}
		if (!StringUtils.isEmpty(limitCondition)) {
			if (!StringUtils.isEmpty(condition)) {
				condition = "(" + condition + ") AND (" + limitCondition + ")";
			} else
				condition = limitCondition;
		}
		try {
			execPreHook(template, filter);
		} catch (Exception e) {
			throw new SystemException(e);
		}
		return baseDao.queryForRowSet(template.getSql(condition, sorts, start, end));
	}

	@Override
	public String getRelation(String tables) {
		return baseDao.getJdbcTemplate().queryForObject("select tablerelation.getSql('" + tables + "') from dual", String.class);
	}

	/**
	 * 权限约束，解析成SQL
	 * 
	 * @return
	 */
	private static String parseLimits(String limits) {
		if (limits != null && !"".equals(limits)) {
			Employee employee = SystemSession.getUser();
			if (limits.startsWith("CU")) {
				String custCol = null;
				String emCol = null;
				if (limits.contains(",")) {
					custCol = limits.substring(3, limits.indexOf(","));
					emCol = limits.substring(limits.indexOf(",") + 1, limits.lastIndexOf(")"));
				} else {
					custCol = limits.substring(3, limits.indexOf(")"));
				}
				return (emCol != null ? emCol + "='" + employee.getEm_code() + "' AND " : "")
						+ "EXISTS (SELECT 1 FROM CustomerDistr C_1 WHERE C_1.CD_CUSTCODE=" + custCol + " AND C_1.CD_SELLERCODE='"
						+ employee.getEm_code() + "')";
			} else if (limits.startsWith("VE")) {
				String vendCol = null;
				String emCol = null;
				if (limits.contains(",")) {
					vendCol = limits.substring(3, limits.indexOf(","));
					emCol = limits.substring(limits.indexOf(",") + 1, limits.lastIndexOf(")"));
				} else {
					vendCol = limits.substring(3, limits.indexOf(")"));
				}
				return (emCol != null ? emCol + "='" + employee.getEm_code() + "' AND " : "")
						+ "EXISTS (SELECT 1 FROM VendorDistr V_1 WHERE V_1.VD_VECODE=" + vendCol + " AND V_1.VD_PERSONCODE='"
						+ employee.getEm_code() + "')";
			} else if (limits.startsWith("EM")) {
				String emCol = limits.substring(limits.indexOf("(") + 1, limits.lastIndexOf(")"));
				return emCol + "='" + employee.getEm_code() + "'";
			} else if (limits.startsWith("DP")) {
				String dpCol = limits.substring(limits.indexOf("(") + 1, limits.lastIndexOf(")"));
				return dpCol + "='" + employee.getEm_departmentcode() + "'";
			}
		}
		return null;
	}

	@Override
	@CacheEvict(value = "searchtemplate", allEntries = true)
	public void copy(String title, Integer sId) {
		SearchTemplate template = searchDao.getSearchTemplate(sId, SpObserver.getSp());
		if (template != null) {
			int st_id = baseDao.getSeqId("SearchTemplate_seq");
			template.setSt_id(st_id);
			template.setSt_man(SystemSession.getUser().getEm_name());
			template.setSt_title(title);
			template.setSt_date(new Date());
			baseDao.save(template);
			baseDao.execute(
					"insert into searchtemplategrid(STG_ID,STG_STID,STG_DETNO,STG_FIELD,STG_OPERATOR,STG_VALUE,STG_LOCK,STG_GROUP,STG_SUM,STG_DBFIND,STG_DOUBLE,STG_QUERY,STG_USE,STG_TYPE,STG_TABLE,STG_WIDTH,STG_TEXT,STG_FORMAT,STG_MODE,STG_LINK,STG_TOKENTAB1,STG_TOKENCOL1,STG_TOKENTAB2,STG_TOKENCOL2,STG_FORMULA) select SEARCHTEMPLATEGRID_SEQ.NEXTVAL,"
							+ st_id
							+ ",STG_DETNO,STG_FIELD,STG_OPERATOR,STG_VALUE,STG_LOCK,STG_GROUP,STG_SUM,STG_DBFIND,STG_DOUBLE,STG_QUERY,STG_USE,STG_TYPE,STG_TABLE,STG_WIDTH,STG_TEXT,STG_FORMAT,STG_MODE,STG_LINK,STG_TOKENTAB1,STG_TOKENCOL1,STG_TOKENTAB2,STG_TOKENCOL2,STG_FORMULA from searchtemplategrid where stg_stid=?",
					sId);
			baseDao.execute("insert into SearchTemplateProp(ST_ID,STG_FIELD,NUM,DISPLAY,VALUE) select " + st_id
					+ ",STG_FIELD,NUM,DISPLAY,VALUE from SearchTemplateProp where st_id=?", sId);
		}
	}

	@Override
	@CacheEvict(value = "searchtemplate", allEntries = true)
	public void updateTitle(String title, Integer sId) {
		baseDao.updateByCondition("searchtemplate", "st_title='" + title + "'", "st_id=" + sId);
	}

	@Override
	public String checkCaller(String caller, String title) {
		boolean checkCaller = baseDao.checkByCondition("SYSNAVIGATION", "sn_isleaf='T' and sn_caller='" + caller + "'");
		if (checkCaller)
			BaseUtil.showError("导航栏不存在，请检查CALLER:" + caller);
		else {
			boolean checkTemp = baseDao.checkIf("SearchTemplate", "st_caller='" + caller + "' and st_title='" + title + "'");
			if (checkTemp)
				return "该查询方案已存在，确认覆盖吗？";
		}
		return null;
	}

	@Override
	public void duplTemp(String caller, Integer sId, String title) {
		SearchTemplate template = searchDao.getSearchTemplate(sId, SpObserver.getSp());
		if (template != null) {
			baseDao.execute("delete from SearchtemplateGrid where stg_stid in (select st_id from Searchtemplate  where st_caller='"
					+ caller + "' and st_title='" + title + "')");
			baseDao.execute("delete from SearchTemplateProp where st_id in (select st_id from Searchtemplate  where st_caller='" + caller
					+ "' and st_title='" + title + "')");
			baseDao.execute("delete from Searchtemplate where st_caller='" + caller + "' and st_title='" + title + "'");
			int st_id = baseDao.getSeqId("SearchTemplate_seq");
			int stg_id = baseDao.getSeqId("SEARCHTEMPLATEGRID_SEQ");
			template.setSt_id(st_id);
			template.setSt_caller(caller);
			template.setSt_man(SystemSession.getUser().getEm_name());
			template.setSt_title(title);
			template.setSt_date(new Date());
			baseDao.save(template);
			baseDao.execute(
					"insert into searchtemplategrid(STG_ID,STG_STID,STG_DETNO,STG_FIELD,STG_OPERATOR,STG_VALUE,STG_LOCK,STG_GROUP,STG_SUM,STG_DBFIND,STG_DOUBLE,STG_QUERY,STG_USE,STG_TYPE,STG_TABLE,STG_WIDTH,STG_TEXT,STG_FORMAT,STG_MODE,STG_LINK,STG_TOKENTAB1,STG_TOKENCOL1,STG_TOKENTAB2,STG_TOKENCOL2,STG_FORMULA) select "
							+ stg_id
							+ ","
							+ st_id
							+ ",STG_DETNO,STG_FIELD,STG_OPERATOR,STG_VALUE,STG_LOCK,STG_GROUP,STG_SUM,STG_DBFIND,STG_DOUBLE,STG_QUERY,STG_USE,STG_TYPE,STG_TABLE,STG_WIDTH,STG_TEXT,STG_FORMAT,STG_MODE,STG_LINK,STG_TOKENTAB1,STG_TOKENCOL1,STG_TOKENTAB2,STG_TOKENCOL2,STG_FORMULA from searchtemplategrid where stg_stid=?",
					sId);
			baseDao.execute("insert into SearchTemplateProp(ST_ID,STG_FIELD,NUM,DISPLAY,VALUE) select " + st_id
					+ ",STG_FIELD,NUM,DISPLAY,VALUE from SearchTemplateProp where ST_ID=?", sId);
		}

	}

	@Override
	@CacheEvict(value = "searchtemplate", allEntries = true)
	@Transactional
	public void saveSearchTemplates(List<SearchTemplate> templates) {
		String caller = templates.get(0).getSt_caller();
		List<SearchTemplate> olds = getSearchTemplates(caller);
		if (!CollectionUtils.isEmpty(olds)) {
			// 覆盖旧方案
			for (SearchTemplate old : olds) {
				baseDao.execute("delete from SearchTemplateProp where st_id=?", old.getSt_id());
				// datalink也可能有变，需要覆盖
				baseDao.execute(
						"delete from DataLink where exists (select 1 from SearchTemplateGrid where stg_stid=? and stg_table=dl_tablename and stg_field=dl_fieldname)",
						old.getSt_id());
				// datarelation也需要覆盖
				String tabs = SqlUtil.splitToSqlString(old.getSt_usedtable());
				baseDao.execute("delete from DataRelation where table_name_x in (" + tabs + ") and table_name_y in (" + tabs + ")");
				baseDao.execute("delete from SearchTemplateGrid where stg_stid=?", old.getSt_id());
				baseDao.execute("delete from SearchTemplate where st_id=?", old.getSt_id());
			}
		}
		Set<DataDictionaryDetail.Link> links = new HashSet<DataDictionaryDetail.Link>();
		Set<DataRelation> relations = new HashSet<DataRelation>();
		List<SearchTemplate.Grid> grids = new ArrayList<SearchTemplate.Grid>();
		List<SearchTemplate.Property> props = new ArrayList<SearchTemplate.Property>();
		for (SearchTemplate template : templates) {
			// 重新取ID
			int newId = baseDao.getSeqId("SearchTemplate_SEQ");
			template.setSt_id(newId);
			if (null != template.getItems()) {
				for (SearchTemplate.Grid grid : template.getItems()) {
					grid.setStg_stid(newId);
					grid.setStg_id(baseDao.getSeqId("SearchTemplateGrid_SEQ"));
					if (grid.getLinks() != null)
						links.addAll(grid.getLinks());
				}
				grids.addAll(template.getItems());
			}
			if (null != template.getProperties()) {
				for (SearchTemplate.Property prop : template.getProperties()) {
					prop.setSt_id(newId);
				}
				props.addAll(template.getProperties());
			}
			if (null != template.getRelations()) {
				relations.addAll(template.getRelations());
			}
		}
		baseDao.save(links, "DataLink");
		baseDao.save(relations, "DataRelation");
		baseDao.save(grids, "SearchTemplateGrid");
		baseDao.save(props, "SearchTemplateProp");
		baseDao.save(templates, "SearchTemplate");
	}

	@Override
	@CacheEvict(value = "searchtemplate", allEntries = true)
	public void saveAppuse(Integer st_id, Integer check) {
		// TODO Auto-generated method stub
		if (st_id != null && check != null)
			baseDao.execute("update searchtemplate set st_appuse=" + check + " where st_id=" + st_id);
	}

}
