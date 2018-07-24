package com.uas.erp.service.ma.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.ErrorCode;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ButtonDao;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DbfindSetDao;
import com.uas.erp.dao.common.DbfindSetGridDao;
import com.uas.erp.dao.common.DbfindSetUiDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.model.DBFindSet;
import com.uas.erp.model.DBFindSetDetail;
import com.uas.erp.model.DBFindSetGrid;
import com.uas.erp.model.DBFindSetUI;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.FormWrap;
import com.uas.erp.model.GridButton;
import com.uas.erp.model.RelativeSearch;
import com.uas.erp.service.common.ProcessService;
import com.uas.erp.service.ma.MAFormService;
import com.uas.erp.service.ma.RelativeSearchService;

@Service
public class MAFormServiceImpl implements MAFormService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FormDao formDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private ProcessService processService;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private DbfindSetGridDao dbfindSetGridDao;
	@Autowired
	private DbfindSetDao dbfindSetDao;
	@Autowired
	private DbfindSetUiDao dbfindSetUiDao;
	@Autowired
	private ButtonDao buttonDao;
	@Autowired
	private RelativeSearchService relativeSearchService;

	static final String INVALID_IDENTIFIER_CAUSE_CN = "java.sql.SQLSyntaxErrorException: ORA-00904: \"%s\": 标识符无效";
	static final String INVALID_IDENTIFIER_CAUSE_EN = "java.sql.SQLSyntaxErrorException: ORA-00904: \"%s\": invalid identifier";

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(String form, String formdetail) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(form);
		// 判断caller是否已存在
		if (!checkCaller((String) store.get("fo_caller"))) {
			BaseUtil.showError(BaseUtil.getLocalMessage("ma.form_callerExist"));
		}
		//导出文件名fo_exportitle，当不为空时，判断字段是否存在fo_table配置的表中
		checkExportitle(store.get("fo_exportitle"),store.get("fo_table"));
		// 保存form
		String formSql = SqlUtil.getInsertSqlByMap(store, "Form");
		baseDao.execute(formSql);
		// 保存formdetail
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(formdetail);
		for (Map<Object, Object> m : gstore) {
			// FormDetail 触发器
			m.put("fd_foid", store.get("fo_id"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "FormDetail");
		baseDao.execute(gridSql);
		// 重置按钮的顺序
		baseDao.procedure("SP_RESETBUTTON", new Object[] { store.get("fo_caller") });
		// 根据form配置 添加加流程
		if (checkProcessDeployCaller((String) store.get("fo_flowcaller")) && "-1".equals(store.get("fo_isautoflow"))) {
			InsertIntoProcessDeploy(store);
		}
		if (checkProcessSetCaller((String) store.get("fo_flowcaller")) && "-1".equals(store.get("fo_isautoflow"))) {
			InsertIntoProcessSet(store);
		}
		// 插入流程按钮
		if ("-1".equals(store.get("fo_isautoflow")))
			InsertIntoJprocessButton((String) store.get("fo_flowcaller"));

		baseDao.logger.save("Form", "fo_id", store.get("fo_id"));
		// 缓存
		formDao.cacheEvict(SpObserver.getSp(), store.get("fo_caller").toString());
		// 勾选的APP使用字段插入表MOBILEFORMDETAIL
		updateAPPFormFields(store.get("fo_caller"));

	}

	@Override
	public boolean checkCaller(String caller) {
		return baseDao.checkByCondition("form", "fo_caller='" + caller + "'");
	}

	@Override
	public boolean checkProcessDeployCaller(String caller) {
		return baseDao.checkByCondition("JprocessDeploy", "jd_caller='" + caller + "'");
	}

	@Override
	public boolean checkProcessSetCaller(String caller) {
		return baseDao.checkByCondition("JprocessSet", "js_caller='" + caller + "'");
	}

	@Override
	public int getIdByCaller(String caller) {
		return Integer.parseInt(baseDao.getFieldDataByCondition("form", "fo_id", "fo_caller='" + caller + "'").toString());
	}

	@Override
	public void update(String form, String add, String update, String del) {
		// 多个form
		List<Map<Object, Object>> formStores = BaseUtil.parseGridStoreToMaps(form);
		
		//导出文件名fo_exportitle，当不为空时，判断字段是否存在fo_table配置的表中
		for (Map<Object, Object> formMap : formStores) {
			checkExportitle(formMap.get("fo_exportitle"),formMap.get("fo_table"));
		}
		List<String> sqls = new ArrayList<String>();
		Map<String, List<Map<Object, Object>>> foids = new HashMap<String, List<Map<Object, Object>>>();
		List<Map<Object, Object>> test = null;
		// 修改form
		sqls.addAll(SqlUtil.getUpdateSqlbyGridStore(formStores, "Form", "fo_id"));
		// 修改formDetail
		// added
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(add);
		for (Map<Object, Object> m : gstore) {
			// FormDetail 触发器
			sqls.add(SqlUtil.getInsertSqlByMap(m, "FormDetail"));
			if (!foids.containsKey(m.get("fd_foid"))) {
				test = new ArrayList<Map<Object, Object>>();
			} else {
				test = foids.get(m.get("fd_foid"));
			}
			test.add(new HashMap<Object, Object>(m));
			foids.put(m.get("fd_foid").toString(), test);
		}
		// updated
		gstore = BaseUtil.parseGridStoreToMaps(update);
		for (Map<Object, Object> m : gstore) {
			sqls.add(SqlUtil.getUpdateSqlByFormStore(m, "FormDetail", "fd_id"));
			if (!foids.containsKey(m.get("fd_foid"))) {
				test = new ArrayList<Map<Object, Object>>();
			} else {
				test = foids.get(m.get("fd_foid"));
			}
			test.add(new HashMap<Object, Object>(m));
			foids.put(m.get("fd_foid").toString(), test);
		}
		Set<String> testSet = foids.keySet();
		for (String s : testSet) {
			List<Map<Object, Object>> list = foids.get(s);
			String tablename = "";
			boolean check = false;
			for (Map<Object, Object> formMap : formStores) {
				if (formMap.get("fo_id").toString().equals(s)) {
					tablename = formMap.get("fo_table").toString();
					if ((formMap.get("fo_button4add") != null && formMap.get("fo_button4add").toString().indexOf("erpSaveButton") > -1)
							|| (formMap.get("fo_button4rw") != null && (formMap.get("fo_button4rw").toString().indexOf("erpSaveButton") > -1 || formMap
									.get("fo_button4rw").toString().indexOf("erpUpdateButton") > -1))) {
						check = true;
					}
				}
			}
			if (check) {
				StringBuffer sb = new StringBuffer();
				for (Map<Object, Object> m : list) {
					sb.append(m.get("fd_field")).append(",");
				}
				try {
					baseDao.execute("select " + sb.substring(0, sb.length() - 1) + " from " + tablename + " where 1=2");
				} catch (BadSqlGrammarException ex) {
					String cause = ex.getCause().toString();
					if (ex.getSQLException().getErrorCode() == ErrorCode.INVALID_IDENTIFIER.code()) {
						String[] params = StringUtil.parse(cause, StringUtil.hasChinese(cause) ? INVALID_IDENTIFIER_CAUSE_CN
								: INVALID_IDENTIFIER_CAUSE_EN);
						BaseUtil.showError("字段" + params[0].toLowerCase() + "不存在");
					}
				}
			}
		}
		// deleted
		gstore = BaseUtil.parseGridStoreToMaps(del);
		for (Map<Object, Object> m : gstore) {
			sqls.add(SqlUtil.getDeleteSql("FormDetail", "fd_id=" + m.get("fd_id")));
		}
		baseDao.execute(sqls);
		for (Map<Object, Object> store : formStores) {
			// 重置按钮的顺序
			baseDao.procedure("SP_RESETBUTTON", new Object[] { store.get("fo_caller") });
			if (checkProcessDeployCaller((String) store.get("fo_flowcaller")) && "-1".equals(store.get("fo_isautoflow"))) {
				InsertIntoProcessDeploy(store);
			}
			if (checkProcessSetCaller((String) store.get("fo_flowcaller")) && "-1".equals(store.get("fo_isautoflow"))) {
				InsertIntoProcessSet(store);
			}
			// 插入流程按钮
			if ("-1".equals(store.get("fo_isautoflow")))
				InsertIntoJprocessButton((String) store.get("fo_flowcaller"));
			// 记录操作
			baseDao.logger.update("Form", "fo_id", store.get("fo_id"));
			// 缓存
			formDao.cacheEvict(SpObserver.getSp(), store.get("fo_caller").toString());
			// formdetail中字符串类型字段得字段长度大于对应表结构的长度时更新对应表结构的长度
			/*
			 * SqlRowList rowList = baseDao.queryForRowSet("select SUBSTR(UPPER(TRIM(Fo_TABLE)) ,0,CASE WHEN INSTR(TRIM(Fo_TABLE), ' ')>0 " + "THEN INSTR(TRIM(Fo_TABLE), ' ') ELSE LENGTH(TRIM(Fo_TABLE)) END) tablename,fd_fieldlength,upper(fd_field) field from " + "formdetail left join form on fd_foid=fo_id left join USER_TAB_COLUMNS on table_name= SUBSTR(UPPER(TRIM(Fo_TABLE))  ,0,CASE WHEN INSTR(TRIM(Fo_TABLE), " +
			 * "' ')>0 THEN INSTR(TRIM(Fo_TABLE), ' ') ELSE LENGTH(TRIM(Fo_TABLE)) END) and column_name=upper(fd_field) where fo_id=?" + "and data_type='VARCHAR2' and FD_FIELDLENGTH>DATA_LENGTH", store.get("fo_id")); List<String> dictionarysqls = new ArrayList<String>(); if (rowList.next()) { String table=rowList.getString("tablename"); int fieldlength=rowList.getInt("fd_fieldlength"); if(fieldlength>4000) fieldlength=4000;//字符串最大长度4000 String field=rowList.getString("field");
			 * if(table!=null&&fieldlength!=0&&field!=null){ dictionarysqls.add("alter table "+table+" modify "+field+" VARCHAR2("+fieldlength+")"); } } baseDao.execute(dictionarysqls);
			 */
		}
		// 勾选的APP使用字段插入表MOBILEFORMDETAIL
		for (Map<Object, Object> store : formStores) {
			updateAPPFormFields(store.get("fo_caller"));
		}
	}
	
	/**
	 * 导出文件名fo_exportitle,检查导出文件名是否存在fo_table设置的表中
	 * @author lidy
	 * @param exportitle 导出文件名
	 * @param fo_table  表名
	 */
	private void checkExportitle(Object exportitle , Object fo_table){
		if(exportitle!=null&&!exportitle.toString().trim().equals("")){
			String exportitles = exportitle.toString().trim().replace("#", ",");
			StringBuilder sql = new StringBuilder();
			sql.append("select ");
			sql.append(exportitles);
			sql.append(" from ");
			sql.append(fo_table);
			try{
				baseDao.execute(sql.toString());
			}catch(Exception e){
				String err = e.getMessage();
				int start = err.indexOf("ORA-00904");
				if(start!=-1){
					int end = err.lastIndexOf(":");
					BaseUtil.showError("字段"+err.substring(start+11, end)+"在对应表中不存在，请确认");
				}else{						
					BaseUtil.showError("字段\""+exportitles+"\"在对应表中不存在，请确认");
				}
			}
		}
	}

	private void updateAPPFormFields(Object caller) {
		// 先删除，-2表示ID或APP端不需显示但是有默认值的字段,不做处理
		baseDao.execute("delete from MOBILEFORMDETAIL where mfd_caller='" + caller + "'");
		baseDao.execute("insert into MOBILEFORMDETAIL (MFD_CALLER,MFD_ISDEFAULT,MFD_FIELD,MFD_CAPTION) select '" + caller
				+ "',nvl(FD_MOBILEUSED,-1),fd_field,fd_caption from formdetail where  nvl(FD_LOGICTYPE,' ')<>'ignore' and fd_foid=(select fo_id from form where fo_Caller='"
				+ caller + "') and not exists (select 1 from MOBILEFORMDETAIL where mfd_caller='" + caller
				+ "' and upper(mfd_field)=upper(fd_field))");
	}

	private void updateAPPGridFields(Object caller) {
		// 先删除，-2表示ID或APP端不需显示但是有默认值的字段,不做处理
		baseDao.execute("delete from MOBILEDETAILGRID where mdg_caller='" + caller + "'");
		baseDao.execute("insert into MOBILEDETAILGRID (MDG_CALLER,MDG_ISDEFAULT,MDG_FIELD,MDG_CAPTION) select '" + caller
				+ "',nvl(DG_MOBILEUSED,-1),dg_field,dg_caption from detailgrid where nvl(DG_LOGICTYPE,' ')<>'ignore' and dg_caller='" + caller
				+ "' and not exists (select 1 from MOBILEDETAILGRID where mdg_caller='" + caller
				+ "' and upper(mdg_field)=upper(dg_field))");
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveMultiForm(String formStore, String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<String> sqls = new ArrayList<String>();
		// 修改form
		String sql = SqlUtil.getInsertSqlByMap(store, "FORM");
		sqls.add(sql);
		// 修改formDetail
		// added
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		for (Map<Object, Object> m : gstore) {
			m.put("fd_foid", store.get("fo_id"));
			// FormDetail 触发器
			sqls.add(SqlUtil.getInsertSqlByMap(m, "FormDetail"));
		}
		baseDao.execute(sqls);
		if (checkProcessDeployCaller((String) store.get("fo_flowcaller"))) {
			InsertIntoProcessDeploy(store);
		}
		if (checkProcessSetCaller((String) store.get("fo_flowcaller"))) {
			InsertIntoProcessSet(store);
		}
		// 插入流程按钮
		if ("-1".equals(store.get("fo_isautoflow")))
			InsertIntoJprocessButton((String) store.get("fo_flowcaller"));
		// 记录操作
		baseDao.logger.update("Form", "fo_id", store.get("fo_id"));
		// 缓存
		formDao.cacheEvict(SpObserver.getSp(), store.get("fo_caller").toString());

		// 勾选的APP使用字段插入表MOBILEFORMDETAIL
		updateAPPFormFields(store.get("fo_caller"));

	}

	@Override
	public void saveDetailGrid(String detailparam) {
		List<String> sqls = new ArrayList<String>();
		Set<Object> objects = new HashSet<Object>();
		Object object = null;
		// added
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(detailparam);
		for (Map<Object, Object> m : gstore) {
			// DetailGrid 触发器
			sqls.add(SqlUtil.getInsertSqlByMap(m, "DetailGrid"));
			object = m.get("dg_caller");
			if (object != null) {
				if (!objects.contains(object))
					objects.add(object);
			}
		}
		baseDao.execute(sqls);
		// 勾选的APP使用字段插入表updateAPPGridFields
		for (Object obj : objects) {
			updateAPPGridFields(obj);
		}
	}

	@Override
	public void delete(int id) {
		String caller = baseDao.getJdbcTemplate().queryForObject("select fo_caller from form where fo_id=?", String.class, id);
		List<String> sqls = new ArrayList<String>();
		sqls.add("delete FormDetail where fd_foid=" + id);
		sqls.add("delete form where fo_id=" + id);
		baseDao.execute(sqls);
		// 记录操作
		baseDao.logger.delete("Form", "fo_id", id);
		// 缓存
		formDao.cacheEvict(SpObserver.getSp(), caller);
		// 勾选的APP使用字段插入表MOBILEFORMDETAIL
		updateAPPFormFields(caller);

	}

	@Override
	public void mdelete(int id) {
		Object obj = baseDao.getFieldDataByCondition("form", "fo_caller", "fo_id=" + id);
		// 删除form
		baseDao.deleteById("Form", "fo_id", id);
		// 删除formDetail
		baseDao.deleteByCondition("FormDetail", "fd_foid=" + id);
		// detailgrid
		if (obj != null) {
			baseDao.deleteByCondition("DetailGrid", "dg_caller='" + obj + "'");
		}
		// 记录操作
		baseDao.logger.delete("Form", "fo_id", id);
		// 更新APP需使用的表
		updateAPPFormFields(obj);
		updateAPPGridFields(obj);
	}

	@Override
	@CacheEvict(value = "gridpanel", allEntries = true)
	@Transactional
	public void updateDetailGrid(String add, String update, String del) {
		List<String> sqls = new ArrayList<String>();
		Set<Object> objects = new HashSet<Object>();
		Object object = null;
		// added
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(add);
		for (Map<Object, Object> m : gstore) {
			// DetailGrid 触发器
			sqls.add(SqlUtil.getInsertSqlByMap(m, "DetailGrid"));
			object = m.get("dg_caller");
			String dgfield= m.get("dg_field")==null?"":m.get("dg_field").toString().replace("'","''");
			// 记录日志
			baseDao.logger.save("DetailGrid", "dg_caller", object + "|" + dgfield);
			if (object != null) {
				objects.add(object);
			}
		}
		// updated
		gstore = BaseUtil.parseGridStoreToMaps(update);
		for (Map<Object, Object> m : gstore) {
			sqls.add(SqlUtil.getUpdateSqlByFormStore(m, "DetailGrid", "dg_id"));
			object = m.get("dg_caller");
			String dgfield= m.get("dg_field")==null?"":m.get("dg_field").toString().replace("'","''");
			// 记录日志
			baseDao.logger.update("DetailGrid", "dg_caller", object + "|" + dgfield);
			if (object != null) {
				objects.add(object);
			}
		}
		// deleted
		gstore = BaseUtil.parseGridStoreToMaps(del);
		for (Map<Object, Object> m : gstore) {
			sqls.add(SqlUtil.getDeleteSql("DetailGrid", "dg_id=" + m.get("dg_id")));
			object = m.get("dg_caller");
			String dgfield= m.get("dg_field")==null?"":m.get("dg_field").toString().replace("'","''");
			// 记录日志
			baseDao.logger.delete("DetailGrid", "dg_caller", object + "|" + dgfield);
			if (object != null) {
				objects.add(object);
			}
		}
		baseDao.execute(sqls);
		// 勾选的APP使用字段插入表updateAPPGridFields
		for (Object obj : objects) {
			updateAPPGridFields(obj);
		}
	}

	@Override
	public List<DataListCombo> getComboDataByField(String caller, String field) {
		List<DataListCombo> combos = dataListComboDao.getComboxsByCallerAndField(caller, field);
		return combos;
	}

	@Override
	@CacheEvict(value = "combo", allEntries = true)
	public void saveCombo(String gridStore) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(gridStore);
		List<String> sqls = new ArrayList<String>();
		// 可能存在更新或者删除的操作
		for (int i = 0; i < maps.size(); i++) {
			Map<Object, Object> map = maps.get(i);
			// 如果Id 是存在的 则保存否则更新
			Object id = map.get("dlc_id");
			if (id != null && !id.equals("null")) {
				boolean rs = baseDao.checkByCondition("DatalistCombo", "DLC_CALLER='" + map.get("dlc_caller") + "' and DLC_FIELDNAME='"
						+ map.get("dlc_fieldname") + "' and DLC_VALUE='" + map.get("dlc_value") + "' and dlc_id<>" + id);
				if (!rs) {
					BaseUtil.showError("名称不能相同!");
				} else {
					sqls.add(SqlUtil.getUpdateSqlByFormStore(map, "DatalistCombo", "dlc_id"));
				}
			} else {
				id = baseDao.getSeqId("DATALISTCOMBO_SEQ");
				map.remove("dlc_id");
				map.put("dlc_id", id);
				boolean rs = baseDao.checkByCondition("DatalistCombo", "DLC_CALLER='" + map.get("dlc_caller") + "' and DLC_FIELDNAME='"
						+ map.get("dlc_fieldname") + "' and DLC_VALUE='" + map.get("dlc_value") + "'");
				if (!rs) {
					BaseUtil.showError("名称不能相同!");
				} else {
					sqls.add(SqlUtil.getInsertSqlByMap(map, "DatalistCombo"));
				}
			}
		}
		baseDao.execute(sqls);
	}

	@Override
	@CacheEvict(value = "combo", allEntries = true)
	public void deleteCombo(String id) {
		baseDao.deleteByCondition("DataListCombo", "dlc_id='" + id + "'");
	}

	public void InsertIntoProcessDeploy(Map<Object, Object> store) {
		String xml = "<process  xmlns='http://jbpm.org/4.4/jpdl' name='" + store.get("fo_caller") + "'> "
				+ "<start g='253,67,48,48' name='start 1'>" + "<transition to='end 1'/></start>"
				+ "<end g='247,239,48,48' name='end 1'/></process>";
		processService.setUpProcess(xml, String.valueOf(store.get("fo_caller")), String.valueOf(store.get("fo_title")), null, "是", "否", 0,
				"");

	}

	@Override
	public <T> void InsertIntoProcessSet(Map<T, Object> store) {
		String insertSql = "insert into jprocessset(js_id,js_caller,js_formKeyName,js_formStatusName,js_table,js_formDetailKey,js_formurl,JS_BEAN,JS_SERVICECLASS,JS_AUDITMETHOD)values('"
				+ baseDao.getSeqId("Jprocessset_SEQ")
				+ "','"
				+ store.get("fo_flowcaller")
				+ "','"
				+ store.get("fo_keyfield")
				+ "','"
				+ store.get("fo_statusfield") + "','" + store.get("fo_table") + "','" + store.get("fo_detailmainkeyfield") + "'";
		if (store.get("fo_table").toString().contains("CUSTOMTABLE")
				&& (store.get("fo_detailmainkeyfield") == null || "".equals(store.get("fo_detailmainkeyfield")))) {
			baseDao.execute(insertSql + ",'jsps/oa/custom/singleform.jsp?whoami=" + store.get("fo_caller")
					+ "','CustomPageSerive','CustomPageSerive','auditPage')");
		} else if (store.get("fo_table").toString().contains("CUSTOMTABLE") && store.get("fo_detailmainkeyfield") != null) {
			baseDao.execute(insertSql + ",'jsps/oa/custom/maindetail.jsp?whoami=" + store.get("fo_caller")
					+ "','CustomPageSerive','CustomPageSerive','auditPage')");
		} else
			baseDao.execute(insertSql + ",null,null,null,null)");
	}

	public void InsertIntoJprocessButton(String caller) {
		String sql = "Insert into JPROCESSBUTTON (JB_CALLER,JB_BUTTONNAME,JB_BUTTONID,JB_FIELDS,JB_MESSAGE,JB_ID) SELECT CALLER,GROUPNAME,'button'||JPROCESSBUTTON_SEQ.NEXTVAL,GROUPNAME,'请保存相应的资料!',JPROCESSBUTTON_SEQ.NEXTVAL FROM (select distinct Fo_CALLER CALLER,fd_group GROUPNAME from formdetail left join form on fd_foid=fo_id where fo_Caller='"
				+ caller
				+ "' AND fd_group IS NOT NULL) where not exists (select 1 from JPROCESSBUTTON where jb_caller=CALLER and jb_fields=GROUPNAME)";
		baseDao.execute(sql);
	}

	@Override
	public void saveFormBook(Integer foid, String text) {
		int count = baseDao.getCountByCondition("FormBook", "fb_foid=" + foid);
		if (count > 0) {
			baseDao.execute("update FormBook set FB_CONTENT='" + text + "' where fb_foid=" + foid);
		} else {
			baseDao.execute("insert into FormBook(fb_foid,FB_CONTENT) values (" + foid + ",'" + text + "')");
		}
	}

	@Override
	public boolean checkFields(String table, String field) {
		String tables = baseDao.getJdbcTemplate().queryForObject("select tablerelation.gettables(:1) from dual", String.class, table);
		String[] tabs = tables.split(",");
		int count = baseDao.getJdbcTemplate().queryForObject(
				new StringBuilder("select count(1) from user_tab_cols where table_name in (").append(CollectionUtil.toSqlString(tabs))
						.append(") and column_name=?").toString(), Integer.class, field.toUpperCase());
		if (count == 0)
			throw new SystemException(String.format("字段%s不存在", field));
		return true;
	}

	final static String INSERT_DATALIST = "insert into dataList (dl_id,dl_caller,dl_tablename,dl_pagesize,dl_title,dl_relative,dl_lockpage,dl_pffield,dl_orderby,dl_keyfield) values(?,?,?,'25',?,?,?,?,?,?)";

	@Override
	@CacheEvict(value = { "datalistEm", "datalist", "empsrelativesettings" }, allEntries = true)
	public String setListCaller(String caller, String dl_caller, String lockpage) {
		String str = null;
		List<String> sql = new ArrayList<String>();
		// 判断是否已有fo_dlcaller
		if (dl_caller == null || dl_caller.equals("")) {
			BaseUtil.showError("列表Caller不允许设置为空");
		}
		SqlRowList rs = baseDao
				.queryForRowSet("select fo_caller,fo_dlcaller,fo_dlrelativecaller,fo_keyfield,fo_id,fo_detailmainkeyfield,fo_table,fo_title from form where fo_caller='"
						+ caller + "'");
		if (rs.next()) {
			String oldCaller = rs.getString("fo_dlcaller");
			String tables = baseDao.getJdbcTemplate().queryForObject(
					"select tablerelation.getTables('" + rs.getString("fo_table") + "') from dual", String.class);
			if (oldCaller != null && !oldCaller.equals("")) {// 已有
				// 判断是否等于原值
				if (oldCaller.equals(dl_caller)) {// 等于原值，将会重置原来的datalist配置
					int dl_id = baseDao.getSeqId("DATALIST_SEQ");
					Object ob = baseDao.getFieldDataByCondition("datalist", "nvl(dl_relative,'')", "dl_caller='" + caller + "'");
					baseDao.deleteByCondition("datalistdetail", "dld_dlid=(select dl_id from datalist where dl_caller='" + oldCaller + "')");
					baseDao.deleteByCondition("datalist", "dl_caller='" + oldCaller + "'");
					// 生成主表数据
					baseDao.execute(
							INSERT_DATALIST,
							new Object[] { dl_id, dl_caller, rs.getString("fo_table"), rs.getString("fo_title") + "列表", ob, lockpage,
									rs.getString("fo_detailmainkeyfield"), "order by " + rs.getString("fo_keyfield") + " desc",
									rs.getString("fo_keyfield") });
					// 生成datalistdetail 数据
					sql = details(dl_caller, rs.getString("fo_keyfield"), tables.split(",")[0], rs.getInt("fo_id"), dl_id);
					baseDao.execute(sql);

					baseDao.updateByCondition("datalist", "dl_relative='" + dl_caller + "'", "dl_relative='" + rs.getString("fo_dlcaller")
							+ "'");
				} else {// 不等于原值，更新dl_caller,更新dld_caller,更新关联列表中的dl_relative
					baseDao.execute("update datalist set dl_caller='" + dl_caller + "' where dl_caller='" + rs.getString("fo_dlcaller")
							+ "'");
					baseDao.updateByCondition("datalistdetail", "dld_caller='" + dl_caller + "'",
							"dld_dlid=(select dl_id from datalist where dl_caller='" + rs.getString("fo_dlcaller") + "')");
					baseDao.updateByCondition("datalist", "dl_relative='" + dl_caller + "'",
							"dl_caller=(select dl_relative from datalist where dl_caller='" + caller + "')");
				}
			} else {// 新生成
				int dl_id = baseDao.getSeqId("DATALIST_SEQ");
				// 生成主表数据
				baseDao.execute(INSERT_DATALIST, new Object[] { dl_id, dl_caller, rs.getString("fo_table"),
						rs.getString("fo_title").replace("维护", "") + "列表", " ", lockpage, rs.getString("fo_detailmainkeyfield"),
						"order by " + rs.getString("fo_keyfield") + " desc", rs.getString("fo_keyfield") });
				// 生成datalistdetail 数据
				sql = details(dl_caller, rs.getString("fo_keyfield"), tables.split(",")[0], rs.getInt("fo_id"), dl_id);
				baseDao.execute(sql);

				baseDao.updateByCondition("datalist", "dl_relative='" + dl_caller + "'", "dl_relative='" + rs.getString("fo_dlcaller")
						+ "'");
			}
		}
		baseDao.updateByCondition("form", "fo_dlcaller='" + dl_caller + "'", "fo_caller='" + caller + "'");
		return str;
	}

	@Override
	@CacheEvict(value = { "datalistEm", "datalist", "empsrelativesettings" }, allEntries = true)
	public String setRelativeCaller(String caller, String re_caller, String lockpage) {
		String str = null;
		List<String> sql = new ArrayList<String>();
		// 判断是否已有fo_dlcaller
		if (re_caller == null || re_caller.equals("")) {
			BaseUtil.showError("关联列表Caller不允许设置为空");
		}
		// 判断是否存在从表
		int cn = baseDao.getCount("select count(1) from detailgrid where dg_caller='" + caller + "'");
		if (cn == 0) {
			BaseUtil.showError("单据不存在从表，不需要配置关联列表");
		}
		SqlRowList rs = baseDao
				.queryForRowSet("select fo_caller,fo_dlcaller,fo_dlrelativecaller,fo_keyfield,fo_codefield,fo_statusfield,fo_id,fo_detailmainkeyfield,fo_table,fo_title,fo_detailtable,fo_detailkeyfield from form where fo_caller='"
						+ caller + "'");
		if (rs.next()) {
			String oldCaller = rs.getString("fo_dlrelativecaller");
			String tables = baseDao.getJdbcTemplate().queryForObject(
					"select tablerelation.getTables('" + rs.getString("fo_table") + "') from dual", String.class);
			String table = rs.getString("fo_detailtable") + " left join " + tables.split(",")[0] + " on " + rs.getString("fo_keyfield")
					+ "=" + rs.getString("fo_detailmainkeyfield");
			tables = baseDao.getJdbcTemplate().queryForObject(
					"select tablerelation.getTables('" + rs.getString("fo_detailtable") + "') from dual", String.class);
			Object ob = baseDao.getFieldDataByCondition("datalist", "nvl(dl_relative,'')", "dl_caller='" + caller + "'");
			if (oldCaller != null && !oldCaller.equals("")) {// 已有
				// 判断是否等于原值
				if (oldCaller.equals(re_caller)) {// 等于原值，将会重置原来的datalist配置
					int dl_id = baseDao.getSeqId("DATALIST_SEQ");
					baseDao.deleteByCondition("datalistdetail", "dld_dlid=(select dl_id from datalist where dl_caller='" + oldCaller + "')");
					baseDao.deleteByCondition("datalist", "dl_caller='" + oldCaller + "'");

					// 生成主表数据
					baseDao.execute(INSERT_DATALIST, new Object[] { dl_id, re_caller, table,
							rs.getString("fo_title").replace("维护", "") + "关联列表", ob, lockpage, rs.getString("fo_detailmainkeyfield"),
							"order by " + rs.getString("fo_keyfield") + " desc", rs.getString("fo_keyfield") });
					// 生成datalistdetail 数据
					sql = redetails(tables.split(",")[0], dl_id, re_caller, caller, rs.getCurrentMap());
					baseDao.execute(sql);

					baseDao.updateByCondition("datalist", "dl_relative='" + re_caller + "'", "dl_relative='" + rs.getString("fo_dlcaller")
							+ "' and dl_caller='" + ob + "'");
				} else {// 不等于原值，更新dl_caller,更新dld_caller,更新关联列表中的dl_relative
					baseDao.execute("update datalist set dl_caller='" + re_caller + "' where dl_caller='" + rs.getString("fo_dlcaller")
							+ "'");
					baseDao.updateByCondition("datalistdetail", "dld_caller='" + re_caller + "'",
							"dld_dlid=(select dl_id from datalist where dl_caller='" + rs.getString("fo_dlcaller") + "')");
					baseDao.updateByCondition("datalist", "dl_relative='" + re_caller + "'", "dl_caller='" + ob + "' and dl_relative='"
							+ rs.getString("fo_dlcaller") + "'");
				}
			} else {// 新生成
				int dl_id = baseDao.getSeqId("DATALIST_SEQ");
				//增加判断主表界面名称是否为空
				if("".equals(rs.getString("fo_title"))){
					BaseUtil.showError("主表界面名称不能为空");
				}
				// 生成主表数据
				baseDao.execute(INSERT_DATALIST, new Object[] { dl_id, re_caller, table,
						rs.getString("fo_title").replace("维护", "") + "关联列表", " ", lockpage, rs.getString("fo_detailmainkeyfield"),
						"order by " + rs.getString("fo_keyfield") + " desc", rs.getString("fo_keyfield") });
				// 生成datalistdetail 数据
				sql = redetails(tables.split(",")[0], dl_id, re_caller, caller, rs.getCurrentMap());
				baseDao.execute(sql);

				baseDao.updateByCondition("datalist", "dl_relative='" + re_caller + "'", "dl_relative='" + rs.getString("fo_dlcaller")
						+ "'");
			}
		}
		baseDao.updateByCondition("form", "fo_dlrelativecaller='" + re_caller + "'", "fo_caller='" + caller + "'");
		return str;
	}

	/**
	 * 列表
	 * 
	 * @param caller
	 * @param keyfield
	 * @param table
	 * @param fo_id
	 * @param dl_id
	 * @return
	 */
	private List<String> details(String caller, String keyfield, String table, int fo_id, int dl_id) {
		String type;
		int detno = 1;
		List<String> sql = new ArrayList<String>();
		SqlRowList rs1;
		// 生成datalistdetail 数据
		rs1 = baseDao
				.queryForRowSet("select fd_caption,fd_field,fd_type,fd_captionfan,fd_captionen,fd_logictype from formdetail where fd_foid="
						+ fo_id);
		if (rs1.next()) {
			for (Map<String, Object> map : rs1.getResultList()) {
				type = map.get("fd_type").toString();
				// 判断类型
				if (!keyfield.equals(map.get("fd_field")) && !type.equals("H") && !type.equals("Bar") && !type.equals("PF")) {
					if (type.equals("N") || type.equals("IN") || type.equals("SN")) {
						sql.add("insert into dataListDetail (dld_id,dld_dlid,dld_caller,dld_detno,dld_field,dld_caption,dld_width,dld_fieldtype,"
								+ "dld_table,dld_caption_fan,dld_caption_en,dld_editable,dld_flex,dld_mobileused)"
								+ "  values (DATALISTDETAIL_SEQ.nextval,"
								+ dl_id
								+ ",'"
								+ caller
								+ "','"
								+ detno
								+ "','"
								+ map.get("fd_field")
								+ "','"
								+ map.get("fd_caption")
								+ "',100, 'N',"
								+ "'"
								+ table
								+ "','"
								+ map.get("fd_captionfan") + "','" + map.get("fd_captionen") + "',0, '0', 0 )");
					} else if (type.equals("YN")) {
						sql.add("insert into dataListDetail (dld_id,dld_dlid,dld_caller,dld_detno,dld_field,dld_caption,dld_width,dld_fieldtype,"
								+ "dld_table,dld_caption_fan,dld_caption_en,dld_editable,dld_flex,dld_mobileused)"
								+ "  values (DATALISTDETAIL_SEQ.nextval,"
								+ dl_id
								+ ",'"
								+ caller
								+ "','"
								+ detno
								+ "','"
								+ map.get("fd_field")
								+ "','"
								+ map.get("fd_caption")
								+ "',100, 'YN',"
								+ "'"
								+ table
								+ "','"
								+ map.get("fd_captionfan") + "','" + map.get("fd_captionen") + "',0, '0', 0 )");
					} else if (type.equals("D") || type.equals("DT") || type.equals("TF")) {// 日期
						sql.add("insert into dataListDetail (dld_id,dld_dlid,dld_caller,dld_detno,dld_field,dld_caption,dld_width,dld_fieldtype,"
								+ "dld_table,dld_caption_fan,dld_caption_en,dld_editable,dld_flex,dld_mobileused)"
								+ "  values (DATALISTDETAIL_SEQ.nextval,"
								+ dl_id
								+ ",'"
								+ caller
								+ "','"
								+ detno
								+ "','"
								+ map.get("fd_field")
								+ "','"
								+ map.get("fd_caption")
								+ "',100, 'D',"
								+ "'"
								+ table
								+ "','"
								+ map.get("fd_captionfan") + "','" + map.get("fd_captionen") + "',0, '0', 0 )");
					} else {// 其余类型为字符串
						sql.add("insert into dataListDetail (dld_id,dld_dlid,dld_caller,dld_detno,dld_field,dld_caption,dld_width,dld_fieldtype,"
								+ "dld_table,dld_caption_fan,dld_caption_en,dld_editable,dld_flex,dld_mobileused)"
								+ "  values (DATALISTDETAIL_SEQ.nextval,"
								+ dl_id
								+ ",'"
								+ caller
								+ "','"
								+ detno
								+ "','"
								+ map.get("fd_field")
								+ "','"
								+ map.get("fd_caption")
								+ "',100, 'S',"
								+ "'"
								+ table
								+ "','"
								+ map.get("fd_captionfan") + "','" + map.get("fd_captionen") + "',0, '0', 0 )");
					}
					detno++;
				} else if (keyfield.equals(map.get("fd_field"))) {
					sql.add("insert into dataListDetail (dld_id,dld_dlid,dld_caller,dld_detno,dld_field,dld_caption,dld_width,dld_fieldtype,"
							+ "dld_table,dld_caption_fan,dld_caption_en,dld_editable,dld_flex,dld_mobileused)"
							+ "  values (DATALISTDETAIL_SEQ.nextval,"
							+ dl_id
							+ ",'"
							+ caller
							+ "','"
							+ detno
							+ "','"
							+ map.get("fd_field")
							+ "','"
							+ map.get("fd_caption")
							+ "',0, 'N',"
							+ "'"
							+ table
							+ "','"
							+ map.get("fd_captionfan")
							+ "','"
							+ map.get("fd_captionen") + "',0, '0', 0 )");
					detno++;
				}
			}
		}
		return sql;
	}

	/**
	 * 关联列表
	 * 
	 * @param table
	 * @param dl_id
	 * @param caller
	 * @param map0
	 * @return
	 */
	private List<String> redetails(String table, int dl_id, String re_caller, String caller, Map<String, Object> map0) {
		List<String> sql = new ArrayList<String>();
		int detno = 1;
		SqlRowList rs0 = baseDao
				.queryForRowSet("select fd_caption,fd_field,fd_type,fd_captionfan,fd_captionen from formdetail where fd_foid="
						+ map0.get("fo_id"));
		if (rs0.next()) {
			for (Map<String, Object> map : rs0.getResultList()) {
				// 判断类型
				if (map0.get("fo_keyfield").equals(map.get("fd_field"))) {
					sql.add("insert into dataListDetail (dld_id,dld_dlid,dld_caller,dld_detno,dld_field,dld_caption,dld_width,dld_fieldtype,"
							+ "dld_table,dld_caption_fan,dld_caption_en,dld_editable,dld_flex,dld_mobileused)"
							+ "  values (DATALISTDETAIL_SEQ.nextval,"
							+ dl_id
							+ ",'"
							+ re_caller
							+ "','"
							+ detno
							+ "','"
							+ map.get("fd_field")
							+ "','"
							+ map.get("fd_caption")
							+ "',0, 'N',"
							+ "'"
							+ table
							+ "','"
							+ map.get("fd_captionfan") + "','" + map.get("fd_captionen") + "',0, '0', 0 )");
					detno++;
				} else if (map0.get("fo_codefield").equals(map.get("fd_field")) || map0.get("fo_statusfield").equals(map.get("fd_field"))) {
					sql.add("insert into dataListDetail (dld_id,dld_dlid,dld_caller,dld_detno,dld_field,dld_caption,dld_width,dld_fieldtype,"
							+ "dld_table,dld_caption_fan,dld_caption_en,dld_editable,dld_flex,dld_mobileused)"
							+ "  values (DATALISTDETAIL_SEQ.nextval,"
							+ dl_id
							+ ",'"
							+ re_caller
							+ "','"
							+ detno
							+ "','"
							+ map.get("fd_field")
							+ "','"
							+ map.get("fd_caption")
							+ "',120, 'S',"
							+ "'"
							+ table
							+ "','"
							+ map.get("fd_captionfan") + "','" + map.get("fd_captionen") + "',0, '0', 0 )");
					detno++;
				}
			}
		}
		rs0 = baseDao
				.queryForRowSet("select dg_caption,dg_visible,dg_field,dg_captionfan,dg_captionen,dg_type,dg_logictype,dg_width from detailgrid where dg_caller='"
						+ caller + "'");
		if (rs0.next()) {
			for (Map<String, Object> map : rs0.getResultList()) {
				if (!map.get("dg_visible").equals("-1") && !map.get("dg_width").equals(0)
						&& !map.get("dg_field").equals(map.get("fo_detailkeyfield"))
						&& !map.get("dg_field").equals(map.get("fo_detailmainkeyfield"))) {
					if (map.get("dg_type").equals("numbercolumn") || rs0.getString("dg_type").equals("floatcolumn")
							|| map.get("dg_type").toString().matches("^floatcolumn\\d{1}$")) {
						sql.add("insert into dataListDetail (dld_id,dld_dlid,dld_caller,dld_detno,dld_field,dld_caption,dld_width,dld_fieldtype,"
								+ "dld_table,dld_caption_fan,dld_caption_en,dld_editable,dld_flex,dld_mobileused)"
								+ "  values (DATALISTDETAIL_SEQ.nextval,"
								+ dl_id
								+ ",'"
								+ re_caller
								+ "','"
								+ detno
								+ "','"
								+ map.get("dg_field")
								+ "','"
								+ map.get("dg_caption")
								+ "',100, 'N',"
								+ "'"
								+ table
								+ "','"
								+ map.get("dg_captionfan") + "','" + map.get("dg_captionen") + "',0, '0', 0 )");
					} else if (map.get("dg_type").equals("datecolumn") || map.get("dg_type").equals("datetimecolumn")
							|| map.get("dg_type").equals("datetimecolumn2") || map.get("dg_type").equals("timecolumn")) {
						sql.add("insert into dataListDetail (dld_id,dld_dlid,dld_caller,dld_detno,dld_field,dld_caption,dld_width,dld_fieldtype,"
								+ "dld_table,dld_caption_fan,dld_caption_en,dld_editable,dld_flex,dld_mobileused)"
								+ "  values (DATALISTDETAIL_SEQ.nextval,"
								+ dl_id
								+ ",'"
								+ re_caller
								+ "','"
								+ detno
								+ "','"
								+ map.get("dg_field")
								+ "','"
								+ map.get("dg_caption")
								+ "',100, 'D',"
								+ "'"
								+ table
								+ "','"
								+ map.get("dg_captionfan") + "','" + map.get("dg_captionen") + "',0, '0', 0 )");
					} else if (map.get("dg_type").equals("yncolumn")) {
						sql.add("insert into dataListDetail (dld_id,dld_dlid,dld_caller,dld_detno,dld_field,dld_caption,dld_width,dld_fieldtype,"
								+ "dld_table,dld_caption_fan,dld_caption_en,dld_editable,dld_flex,dld_mobileused)"
								+ "  values (DATALISTDETAIL_SEQ.nextval,"
								+ dl_id
								+ ",'"
								+ re_caller
								+ "','"
								+ detno
								+ "','"
								+ map.get("dg_field")
								+ "','"
								+ map.get("dg_caption")
								+ "',100, 'YN',"
								+ "'"
								+ table
								+ "','"
								+ map.get("dg_captionfan") + "','" + map.get("dg_captionen") + "',0, '0', 0 )");
					} else {
						sql.add("insert into dataListDetail (dld_id,dld_dlid,dld_caller,dld_detno,dld_field,dld_caption,dld_width,dld_fieldtype,"
								+ "dld_table,dld_caption_fan,dld_caption_en,dld_editable,dld_flex,dld_mobileused)"
								+ "  values (DATALISTDETAIL_SEQ.nextval,"
								+ dl_id
								+ ",'"
								+ re_caller
								+ "','"
								+ detno
								+ "','"
								+ map.get("dg_field")
								+ "','"
								+ map.get("dg_caption")
								+ "',100, 'S',"
								+ "'"
								+ table
								+ "','"
								+ map.get("dg_captionfan") + "','" + map.get("dg_captionen") + "',0, '0', 0 )");
					}
					detno++;
				} else if (map.get("dg_field").equals(map.get("fo_detailkeyfield"))
						|| map.get("dg_field").equals(map.get("fo_detailmainkeyfield"))) {
					sql.add("insert into dataListDetail (dld_id,dld_dlid,dld_caller,dld_detno,dld_field,dld_caption,dld_width,dld_fieldtype,"
							+ "dld_table,dld_caption_fan,dld_caption_en,dld_editable,dld_flex,dld_mobileused)"
							+ "  values (DATALISTDETAIL_SEQ.nextval,"
							+ dl_id
							+ ",'"
							+ re_caller
							+ "','"
							+ detno
							+ "','"
							+ map.get("dg_field")
							+ "','"
							+ map.get("dg_caption")
							+ "',0, 'N',"
							+ "'"
							+ table
							+ "','"
							+ map.get("dg_captionfan") + "','" + map.get("dg_captionen") + "',0, '0', 0)");
					detno++;
				}
			}
		}
		return sql;
	}

	@Override
	@CacheEvict(value = "gridpanel", allEntries = true)
	public void updateDetail(String table, String gridAdded, String gridUpdated, String gridDeleted) {
		List<String> sqls = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		Set<Object> objects = new HashSet<Object>();
		Object object = null;
		// added
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridAdded);
		for (Map<Object, Object> m : gstore) {
			// DetailGrid 触发器
			sqls.add(SqlUtil.getInsertSqlByMap(m, "DetailGrid"));
			sb.append(m.get("dg_field")).append(",");
			object = m.get("dg_caller");
			if (object != null) {
				if (!objects.contains(object))
					objects.add(object);
			}
		}
		// updated
		gstore = BaseUtil.parseGridStoreToMaps(gridUpdated);
		for (Map<Object, Object> m : gstore) {
			sqls.add(SqlUtil.getUpdateSqlByFormStore(m, "DetailGrid", "dg_id"));
			sb.append(m.get("dg_field")).append(",");
			object = m.get("dg_caller");
			if (object != null) {
				if (!objects.contains(object))
					objects.add(object);
			}
		}
		if (sb.length() > 0) {
			String test = "select " + sb.substring(0, sb.length() - 1) + " from " + table + " where 1=2";
			try {
				baseDao.execute(test);
			} catch (BadSqlGrammarException ex) {
				String cause = ex.getCause().toString();
				if (ex.getSQLException().getErrorCode() == ErrorCode.INVALID_IDENTIFIER.code()) {
					String[] params = StringUtil.parse(cause, StringUtil.hasChinese(cause) ? INVALID_IDENTIFIER_CAUSE_CN
							: INVALID_IDENTIFIER_CAUSE_EN);
					BaseUtil.showError("字段" + params[0].toLowerCase() + "不存在");
				}
			}
		}
		// deleted
		gstore = BaseUtil.parseGridStoreToMaps(gridDeleted);
		for (Map<Object, Object> m : gstore) {
			sqls.add(SqlUtil.getDeleteSql("DetailGrid", "dg_id=" + m.get("dg_id")));
			object = m.get("dg_caller");
			if (object != null) {
				if (!objects.contains(object))
					objects.add(object);
			}
		}
		baseDao.execute(sqls);
		// 勾选的APP使用字段插入表updateAPPGridFields
		for (Object obj : objects) {
			updateAPPGridFields(obj);
		}
	}

	@Override
	public FormWrap exportForms(String[] ids, String[] gridCallers) {
		FormWrap wrap = new FormWrap();
		List<Form> forms = new ArrayList<Form>();
		Set<String> callers = new HashSet<String>();
		List<RelativeSearch> searchs = new ArrayList<RelativeSearch>();
		List<DataListCombo> combos = new ArrayList<DataListCombo>();
		List<DBFindSetUI> dbFindSetUIs = new ArrayList<DBFindSetUI>();
		String sob = SpObserver.getSp();
		for (String id : ids) {
			String caller = baseDao.queryForObject("select fo_caller from form where fo_id=?", String.class, id);
			if (null != caller) {
				if (!callers.contains(caller)) {
					Form form = formDao.getForm(caller, sob);
					forms.add(form);
					callers.add(caller);
					// 包括关联查询
					searchs.addAll(formDao.getRelativeSearchs(caller, sob));
					// 包括下拉框设置
					combos.addAll(dataListComboDao.getComboxsByCaller(caller, sob));
					// 包括放大镜设置
					if (null != form.getFormDetails()) {
						for (FormDetail detail : form.getFormDetails()) {
							if ("T".equals(detail.getFd_dbfind()) || "M".equals(detail.getFd_dbfind())) {
								// 暂不考虑ds_caller为空的通配dbfindsetui
								DBFindSetUI dbFindSetUI=dbfindSetUiDao.getDbFindSetUIByCallerAndField(caller, detail.getFd_field());
								if (dbFindSetUI!=null)
								dbFindSetUIs.add(dbFindSetUI);
							}
						}
					}
				}
			}
		}
		wrap.setForms(forms);
		wrap.setSearchs(searchs);
		wrap.setDbFindSetUIs(dbFindSetUIs);

		List<DetailGrid> grids = new ArrayList<DetailGrid>();
		List<DBFindSet> dbFindSets = new ArrayList<DBFindSet>();
		List<DBFindSetGrid> dbFindSetGrids = new ArrayList<DBFindSetGrid>();
		List<GridButton> buttons = new ArrayList<GridButton>();
		for (String caller : gridCallers) {
			callers.add(caller);
			grids.addAll(detailGridDao.getDetailGridsByCaller(caller, sob));
			// 包括下拉框设置
			combos.addAll(dataListComboDao.getComboxsByCaller(caller, sob));
			// 包括放大镜设置
			DBFindSet dbFindSet = dbfindSetDao.getDbfind(caller, sob);
			if (null != dbFindSet)
				dbFindSets.add(dbFindSet);
			dbFindSetGrids.addAll(dbfindSetGridDao.getDbFindSetGridsByCaller(caller));
			// 包括按钮
			buttons.addAll(buttonDao.getGridButtons(sob, caller));
		}
		wrap.setCallers(callers.toArray(new String[] {}));
		wrap.setGrids(grids);
		wrap.setDbFindSets(dbFindSets);
		wrap.setDbFindSetGrids(dbFindSetGrids);
		wrap.setCombos(combos);
		wrap.setButtons(buttons);

		return wrap;
	}

	@Override
	@CacheEvict(value = { "formpanel", "relativesearch", "dbfindsetui", "gridpanel", "dbfind", "gridbutton", "combo" }, allEntries = true)
	@Transactional
	public void importForms(FormWrap formWrap) {
		String callerStr = CollectionUtil.toSqlString(formWrap.getCallers());
		List<Form> forms = formWrap.getForms();
		if (!CollectionUtils.isEmpty(forms)) {
			List<FormDetail> details = new ArrayList<FormDetail>();
			for (Form form : forms) {
				Integer newId = baseDao.queryForObject("select fo_id from form where fo_caller=?", Integer.class, form.getFo_caller());
				if (null != newId) {
					// 覆盖
					baseDao.deleteById("formdetail", "fd_foid", newId);
					baseDao.deleteById("form", "fo_id", newId);
				} else {
					newId = baseDao.getSeqId("Form_SEQ");
				}
				form.setFo_id(newId);
				if (!CollectionUtils.isEmpty(form.getFormDetails())) {
					for (FormDetail detail : form.getFormDetails()) {
						detail.setFd_foid(newId);
						detail.setFd_id(baseDao.getSeqId("FormDetail_SEQ"));
					}
					details.addAll(form.getFormDetails());
				}
			}
			baseDao.save(forms);
			baseDao.save(details);
		}

		List<RelativeSearch> searchs = formWrap.getSearchs();
		if (!CollectionUtils.isEmpty(searchs)) {
			for (RelativeSearch search : searchs) {
				relativeSearchService.saveRelativeSearch(search);
			}
		}

		List<DataListCombo> combos = formWrap.getCombos();
		if (!CollectionUtils.isEmpty(combos)) {
			baseDao.deleteByCondition("datalistcombo", "dlc_caller in (" + callerStr + ")");
			for (DataListCombo combo : combos) {
				combo.setDlc_id(null);// trigger
			}
			baseDao.save(combos);
		}

		List<DetailGrid> grids = formWrap.getGrids();
		if (!CollectionUtils.isEmpty(grids)) {
			baseDao.deleteByCondition("DetailGrid", "dg_caller in (" + callerStr + ")");
			for (DetailGrid grid : grids) {
				grid.setDg_id(baseDao.getSeqId("DetailGrid_SEQ"));
			}
			baseDao.save(grids);
		}

		List<DBFindSet> dbFindSets = formWrap.getDbFindSets();
		if (!CollectionUtils.isEmpty(dbFindSets)) {
			List<DBFindSetDetail> details = new ArrayList<DBFindSetDetail>();
			for (DBFindSet dbFindSet : dbFindSets) {
				Integer newId = baseDao.queryForObject("select ds_id from DBFindSet where ds_caller=?", Integer.class,
						dbFindSet.getDs_caller());
				if (null != newId) {
					// 覆盖
					baseDao.deleteById("DBFindSetDetail", "dd_dsid", newId);
					baseDao.deleteById("DBFindSet", "ds_id", newId);
				} else {
					newId = baseDao.getSeqId("DBFindSet_SEQ");
				}
				dbFindSet.setDs_id(newId);
				if (!CollectionUtils.isEmpty(dbFindSet.getDbFindSetDetails())) {
					for (DBFindSetDetail detail : dbFindSet.getDbFindSetDetails()) {
						detail.setDd_dsid(newId);
						detail.setDd_id(baseDao.getSeqId("DBFindSetDetail_SEQ"));
					}
					details.addAll(dbFindSet.getDbFindSetDetails());
				}
			}
			baseDao.save(dbFindSets);
			baseDao.save(details);
		}

		List<DBFindSetGrid> dbFindSetGrids = formWrap.getDbFindSetGrids();
		if (!CollectionUtils.isEmpty(dbFindSetGrids)) {
			baseDao.deleteByCondition("DBFindSetGrid", "ds_caller in (" + callerStr + ")");
			for (DBFindSetGrid dbFindSetGrid : dbFindSetGrids) {
				dbFindSetGrid.setDs_id(baseDao.getSeqId("DBFindSetGrid_SEQ"));
			}
			baseDao.save(dbFindSetGrids);
		}

		List<DBFindSetUI> dbFindSetUIs = formWrap.getDbFindSetUIs();
		if (!CollectionUtils.isEmpty(dbFindSetUIs)) {
			baseDao.deleteByCondition("DBFindSetUI", "ds_caller in (" + callerStr + ")");
			for (DBFindSetUI dbFindSetUI : dbFindSetUIs) {
				dbFindSetUI.setDs_id(baseDao.getSeqId("DBFindSetUI_SEQ"));
			}
			baseDao.save(dbFindSetUIs);
		}

		List<GridButton> buttons = formWrap.getButtons();
		if (!CollectionUtils.isEmpty(buttons)) {
			baseDao.deleteByCondition("GridButton", "gb_caller in (" + callerStr + ")");
			for (GridButton button : buttons) {
				button.setGb_id(baseDao.getSeqId("GridButton_SEQ"));
			}
			baseDao.save(buttons);
		}
	}
}
