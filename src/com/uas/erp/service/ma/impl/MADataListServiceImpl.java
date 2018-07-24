package com.uas.erp.service.ma.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.ErrorCode;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.DataListDetail;
import com.uas.erp.model.DataListWrap;
import com.uas.erp.service.ma.MADataListService;

@Service
public class MADataListServiceImpl implements MADataListService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DataListDao dataListDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	static final String INVALID_IDENTIFIER_CAUSE_CN = "java.sql.SQLSyntaxErrorException: ORA-00904: \"%s\": 标识符无效";
	static final String INVALID_IDENTIFIER_CAUSE_EN = "java.sql.SQLSyntaxErrorException: ORA-00904: \"%s\": invalid identifier";

	@Override
	public boolean checkCaller(String caller) {
		return baseDao.checkByCondition("datalist", "dl_caller='" + caller + "'");
	}

	@Override
	public void save(String form, String formdetail) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(form);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(formdetail);
		StringBuffer sb = new StringBuffer();
		StringBuffer test = new StringBuffer("select ");
		// 判断caller是否已存在
		if (!checkCaller((String) store.get("dl_caller"))) {
			BaseUtil.showError(BaseUtil.getLocalMessage("ma.datalist_callerExist"));
		}
		for (Map<Object, Object> m : grid) {
			String field = m.get("dld_field").toString();
			sb.append(field).append(",");
		}
		try {
			if (sb.length() > 0) {
				test.append(sb.substring(0, sb.length() - 1));
			} else {
				test.append("count(1)");
			}
			test.append(" from ").append(store.get("dl_tablename"));
			if (store.get("dl_condition") != null && !"".equals(store.get("dl_condition"))) {
				test.append(" where 1=2 and ").append(store.get("dl_condition"));
			} else {
				test.append(" where 1=2");
			}

			if (store.get("dl_orderby") != null && !"".equals(store.get("dl_orderby"))) {
				test.append(" ").append(store.get("dl_orderby"));
			}
			baseDao.execute(test.toString());
		} catch (BadSqlGrammarException ex) {
			String cause = ex.getCause().toString();
			if (ex.getSQLException().getErrorCode() == ErrorCode.INVALID_IDENTIFIER.code()) {
				String[] params = StringUtil.parse(cause, StringUtil.hasChinese(cause) ? INVALID_IDENTIFIER_CAUSE_CN
						: INVALID_IDENTIFIER_CAUSE_EN);
				BaseUtil.showError("字段" + params[0].toLowerCase() + "不存在");
			} else {
				BaseUtil.showError("请检查表名、条件SQL及排序语句是否正确");
			}

		}
		// 分割表，获取表名
		String tables = baseDao.getJdbcTemplate().queryForObject("select tablerelation.gettables(:1) from dual", String.class,
				store.get("dl_tablename"));
		String[] tabs = tables.split(",");
		for (int i = 0; i < tabs.length; i++) {
			tabs[i] = tabs[i].split(" ")[0];
		}
		// 判断字段类型是否与数据库中的字段类型一致
		String st = checkDataType(formdetail, tabs);
		if (st != null) {
			BaseUtil.showError(st);
		}
		// 保存
		String formSql = SqlUtil.getInsertSqlByMap(store, "DataList");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(grid, "DataListDetail", "dld_id");
		baseDao.execute(gridSql);
		baseDao.execute("update DataListDetail set dld_caller=(select dl_caller from DataList where dld_dlid=dl_id) where dld_dlid="
				+ store.get("dl_id"));
		baseDao.logger.save("DataList", "dl_id", store.get("dl_id"));
	}

	@Override
	@CacheEvict(value = { "datalist", "datalistEm" }, allEntries = true)
	public void update(String form, String add, String update, String del) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(form);
		List<String> sqls = new ArrayList<String>();
		StringBuffer st = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		StringBuffer test = new StringBuffer("select ");
		// 分割表，获取表名
		String tables = baseDao.getJdbcTemplate().queryForObject("select tablerelation.gettables(:1) from dual", String.class,
				store.get("dl_tablename"));
		String[] tabs = tables.split(",");
		for (int i = 0; i < tabs.length; i++) {
			tabs[i] = tabs[i].split(" ")[0];
		}
		// 判断字段类型是否与数据库中的字段类型一致
		st.append(checkDataType(add, tabs));
		st.append(checkDataType(update, tabs));
		if (st.length() != 0) {
			BaseUtil.showError(st.toString());
		}
		// 修改form
		String sql = SqlUtil.getUpdateSqlByFormStore(store, "DataList", "dl_id");
		sqls.add(sql);
		// 更新列表个性设置表中的caller
		sqls.add("update DATALISTDETAILEMPS set dde_caller='" + store.get("dl_caller") + "' where dde_dlid=" + store.get("dl_id"));

		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(del);
		// deleted
		for (Map<Object, Object> m : gstore) {
			sqls.add(SqlUtil.getDeleteSql("DataListDetail", "dld_id=" + m.get("dld_id")));
			// 删除列表字段时，同时删除列表个性设置中该字段
			sqls.add("delete from DATALISTDETAILEMPS where dde_dlid=" + store.get("dl_id") + " and dde_field='" + m.get("dld_field") + "'");
		}
		// updated
		gstore = BaseUtil.parseGridStoreToMaps(update);
		for (Map<Object, Object> m : gstore) {
			// 更新字段对应修改列表个性设置中的字段
			String field = m.get("dld_field").toString();
			Object oldfield = baseDao.getFieldDataByCondition("DataListDetail", "dld_field", "dld_id=" + m.get("dld_id")
					+ " and dld_field<>'" + field + "'");
			if (oldfield != null)
				sqls.add("update DATALISTDETAILEMPS set dde_field='" + field + "' where dde_field='" + oldfield + "' and dde_dlid="
						+ store.get("dl_id"));
			sb.append(field).append(",");
			sqls.add(SqlUtil.getUpdateSqlByFormStore(m, "DataListDetail", "dld_id"));
		}
		// added
		gstore = BaseUtil.parseGridStoreToMaps(add);
		int i = 1;
		for (Map<Object, Object> m : gstore) {
			String field = m.get("dld_field").toString();
			sb.append(field).append(",");
			m.put("dld_dlid", store.get("dl_id"));
			sqls.add(SqlUtil.getInsertSqlByMap(m, "DataListDetail"));
			// 列表新增字段插入到列表个性设置表中
			sqls.add("insert into DATALISTDETAILEMPS(DDE_FIELD,DDE_WIDTH,DDE_DETNO,DDE_EMID,DDE_DLID,DDE_CALLER,DDE_PRIORITY) "
					+ "select distinct '" + field + "','" + m.get("dld_width") + "',max(dde_detno)+" + i + ",dde_emid,dde_dlid,'"
					+ store.get("dl_caller") + "',null from DATALISTDETAILEMPS where dde_dlid=" + store.get("dl_id")
					+ " group by dde_emid,dde_dlid");
			i++;
		}
		try {
			if (sb.length() > 0) {
				test.append(sb.substring(0, sb.length() - 1));
			} else {
				test.append("count(1)");
			}
			test.append(" from ").append(store.get("dl_tablename"));
			if (store.get("dl_condition") != null && !"".equals(store.get("dl_condition"))) {
				test.append(" where 1=2 and ").append(store.get("dl_condition"));
			} else {
				test.append(" where 1=2");
			}
			if (store.get("dl_orderby") != null && !"".equals(store.get("dl_orderby"))) {
				test.append(" ").append(store.get("dl_orderby"));
			}
			baseDao.execute(test.toString());
		} catch (BadSqlGrammarException ex) {
			String cause = ex.getCause().toString();
			if (ex.getSQLException().getErrorCode() == ErrorCode.INVALID_IDENTIFIER.code()) {
				String[] params = StringUtil.parse(cause, StringUtil.hasChinese(cause) ? INVALID_IDENTIFIER_CAUSE_CN
						: INVALID_IDENTIFIER_CAUSE_EN);
				BaseUtil.showError("字段" + params[0].toLowerCase() + "不存在");
			} else {
				BaseUtil.showError("请检查表名、条件SQL及排序语句是否正确");
			}

		}
		// 检测字段是否重复
		String check = baseDao.executeWithCheck(sqls, null,
				"select WMSYS.WM_CONCAT(DLD_FIELD) from datalistdetail where dld_dlid=" + store.get("dl_id")
						+ " and upper(dld_field) in (select upper(dld_field) from datalistdetail where dld_dlid=" + store.get("dl_id")
						+ " group by upper(dld_field) having count(upper(dld_field)) > 1)");
		if (check != null && check.length() > 0) {
			BaseUtil.showError("字段：" + check + "重复");
		}

		baseDao.execute("update DataListDetail set dld_caller=(select dl_caller from DataList where dld_dlid=dl_id) where dld_dlid="
				+ store.get("dl_id"));
		// 记录操作
		baseDao.logger.update("DataList", "dl_id", store.get("dl_id"));
	}

	@Override
	@CacheEvict(value = "datalist", allEntries = true)
	public void delete(int id) {
		// 删除Detail
		baseDao.deleteById("DataListDetail", "dld_dlid", id);
		// 删除
		baseDao.deleteById("DataList", "dl_id", id);
		// 记录操作
		baseDao.logger.delete("DataList", "dl_id", id);
	}

	@Override
	@CacheEvict(value = "combo", allEntries = true)
	public String resetCombo(String caller, String field) {
		DataList dataList = dataListDao.getDataList(caller, SpObserver.getSp());
		if (dataList != null) {
			int count = baseDao.getCountByCondition("DataListCombo", "dlc_caller='" + caller + "' and dlc_fieldname='" + field
					+ "' and dlc_value<>dlc_display");
			if (count > 0) {
				return "该字段显示值与实际值不一致，不允许设置，请联系管理员.";
			}
			// 关联列表
			int count1 = baseDao.getCountByCondition("DataListCombo", "dlc_caller='" + dataList.getDl_relative() + "' and dlc_fieldname='"
					+ field + "'");
			if (count1 > 0) {
				baseDao.deleteByCondition("DataListCombo", "dlc_caller='" + caller + "' and dlc_fieldname='" + field + "'");
				String sql = "insert into datalistcombo(dlc_id,dlc_value,dlc_value_en,dlc_value_tw,dlc_detno,dlc_caller,dlc_fieldname,dlc_display) "
						+ "select datalistcombo_seq.nextval,dlc_value,dlc_value_en,dlc_value_tw,dlc_detno,'"
						+ caller
						+ "','"
						+ field
						+ "',dlc_display from (select distinct "
						+ "dlc_value,dlc_value_en,dlc_value_tw,dlc_detno,dlc_display"
						+ " from DataListCombo where dlc_caller='" + dataList.getDl_relative() + "' and dlc_fieldname='" + field + "')";
				baseDao.execute(sql);
				return null;
			}
			count = baseDao.getCount("select count(1) from (select distinct " + field + " v from " + dataList.getDl_tablename() + ")");
			if (count == 0) {
				return "该字段暂时无数据，不允许设置，请联系管理员.";
			} else if (count > 50) {
				return "该字段数据过多(超过50条上限)，不允许设置，请联系管理员.";
			}
			// baseDao.deleteByCondition("DataListCombo", "dlc_caller='" + caller + "' and dlc_fieldname='" + field + "'");
			String sql = "insert into datalistcombo(dlc_id,dlc_value,dlc_value_en,dlc_value_tw,dlc_caller,dlc_fieldname,dlc_display) select datalistcombo_seq.nextval,v,v,v,'"
					+ caller
					+ "','"
					+ field
					+ "',v from (select distinct "
					+ field
					+ " v from "
					+ dataList.getDl_tablename()
					+ " where "
					+ field
					+ " not in(select dlc_value from datalistcombo where dlc_caller='"
					+ caller
					+ "' and datalistcombo.dlc_fieldname='" + field + "'))";
			baseDao.execute(sql);
		}
		return null;
	}

	private String checkDataType(String gridData, String[] tabs) {
		List<Map<Object, Object>> gridStore = BaseUtil.parseGridStoreToMaps(gridData);
		StringBuffer error = new StringBuffer();
		String type;
		String tables = CollectionUtil.toSqlString(tabs);
		for (Map<Object, Object> m : gridStore) {
			if (m.get("dld_field") != null) {
				String column_name = m.get("dld_field").toString();
				if (column_name.contains("'")) {
					column_name = column_name.replaceAll("'", "''");
				}
				if (m.get("dld_fieldtype").toString().equals("S") || m.get("dld_fieldtype").toString().equals("LS")
						|| m.get("dld_fieldtype").toString().equals("tfcolumn")) {// 字符串
					type = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(data_type) from user_tab_cols where table_name IN(" + tables + ") and " + "column_name ='"
									+ column_name.toUpperCase()
									+ "' and data_type not in ('CHAR','VARCHAR2','NVARCHAR2','CLOB','RAW','BLOB')", String.class);
					if (type != null) {
						error.append("字段[" + m.get("dld_field") + "]设置类型与实际类型[" + type + "]不符<br>");
					}
				} else if (m.get("dld_fieldtype").toString().equals("N") || m.get("dld_fieldtype").toString().equals("PN")
						|| m.get("dld_fieldtype").toString().matches("^F\\d{0}$")
						|| m.get("dld_fieldtype").toString().matches("^PF\\d{0}$") || m.get("dld_fieldtype").toString().equals("YN")
						|| m.get("dld_fieldtype").toString().equals("yncolumn")) {
					// 数字类型
					type = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(data_type) from user_tab_cols where table_name IN(" + tables + ") and " + "column_name ='"
									+ column_name.toUpperCase() + "' and data_type NOT IN('NUMBER','FLOAT')", String.class);
					if (type != null) {
						error.append("字段[" + m.get("dld_field") + "]设置类型与实际类型[" + type + "]不符<br>");
					}
				} else if (m.get("dld_fieldtype").toString().equals("D") || m.get("dld_fieldtype").toString().equals("DT")) {
					// 日期类型
					type = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(data_type) from user_tab_cols where table_name IN(" + tables + ") and " + "column_name ='"
									+ column_name.toString().toUpperCase() + "' and data_type not in('DATE','TIMESTAMP')", String.class);
					if (type != null) {
						error.append("字段[" + m.get("dld_field") + "]设置类型与实际类型[" + type + "]不符<br>");
					}
				}
			}
		}
		if (error.length() != 0) {
			return error.toString();
		} else {
			return "";
		}
	}

	@Override
	public DataListWrap exportDataList(Integer dl_id) {
		String caller = baseDao.queryForObject("select dl_caller from datalist where dl_id=?", String.class, dl_id);
		return exportDataListWithRelative(caller, true);
	}

	private DataListWrap exportDataListWithRelative(String caller, boolean relative) {
		DataListWrap wrap = new DataListWrap();
		String sob = SpObserver.getSp();
		DataList dataList = dataListDao.getDataList(caller, sob);
		if (null != dataList) {
			wrap.setDataList(dataList);
			// 包括关联列表
			if (relative && !StringUtils.isEmpty(dataList.getDl_relative()) && !dataList.getDl_relative().equals(caller)) {
				wrap.setRelative(exportDataListWithRelative(dataList.getDl_relative(), false));
			}
			wrap.setCombos(dataListComboDao.getComboxsByCaller(caller, sob));
		}
		return wrap;
	}

	@Override
	@Transactional
	@CacheEvict(value = { "datalist", "datalistEm", "combo" }, allEntries = true)
	public void importDataList(DataListWrap dataListWrap) {
		DataList dataList = dataListWrap.getDataList();
		Integer newId = baseDao.queryForObject("select dl_id from datalist where dl_caller=?", Integer.class, dataList.getDl_caller());
		if (null != newId) {
			// 覆盖
			baseDao.deleteById("DataListDetail", "dld_dlid", newId);
			baseDao.deleteById("DataList", "dl_id", newId);
		} else {
			newId = baseDao.getSeqId("DataList_SEQ");
		}
		dataList.setDl_id(newId);
		baseDao.save(dataList);
		if (!CollectionUtils.isEmpty(dataList.getDataListDetails())) {
			for (DataListDetail detail : dataList.getDataListDetails()) {
				detail.setDld_dlid(newId);
				detail.setDld_id(baseDao.getSeqId("DataListDetail_SEQ"));
			}
			baseDao.save(dataList.getDataListDetails());
		}
		List<DataListCombo> combos = dataListWrap.getCombos();
		if (!CollectionUtils.isEmpty(combos)) {
			baseDao.deleteByCondition("datalistcombo", "dlc_caller=?", dataList.getDl_caller());
			for (DataListCombo combo : combos) {
				combo.setDlc_id(null);// trigger
			}
			baseDao.save(combos);
		}

		if (null != dataListWrap.getRelative())
			importDataList(dataListWrap.getRelative());
	}

	@Override
	public String copy(int id, String newCaller) {
		String result = "";
		boolean bool = baseDao.checkByCondition("DATALIST", "DL_CALLER = '"+newCaller+"'");
		if (bool) {
			int newid = baseDao.getSeqId("DATALIST_SEQ");
			String formSql = "INSERT INTO DATALIST(DL_ID,DL_CALLER,DL_TABLENAME,DL_PAGESIZE,DL_TITLE,DL_RELATIVE,DL_LOCKPAGE,DL_PFCAPTION,"
					+ "DL_FIXEDCONDITION,DL_SEARCH,DL_RECORDFIELD,DL_GROUPBY,DL_TOTAL,DL_ORDERBY,DL_DISTINCT,DL_POPEDOMMODULE,DL_TITLE_EN,"
					+ "DL_TITLE_FAN,DL_KEYFIELD,DL_ENID,DL_PFFIELD,DL_FIXEDCOLS,DL_STATUSFIELD,DL_STATUSCODEFIELD,DL_LINKPAGE,DL_CONDITION,"
					+ "DL_ENTRYFIELD) SELECT "+newid+",'"+newCaller+"',DL_TABLENAME,DL_PAGESIZE,DL_TITLE,DL_RELATIVE,DL_LOCKPAGE,DL_PFCAPTION,"
					+ "DL_FIXEDCONDITION,DL_SEARCH,DL_RECORDFIELD,DL_GROUPBY,DL_TOTAL,DL_ORDERBY,DL_DISTINCT,DL_POPEDOMMODULE,DL_TITLE_EN,"
					+ "DL_TITLE_FAN,DL_KEYFIELD,DL_ENID,DL_PFFIELD,DL_FIXEDCOLS,DL_STATUSFIELD,DL_STATUSCODEFIELD,DL_LINKPAGE,DL_CONDITION,"
					+ "DL_ENTRYFIELD FROM DATALIST WHERE DL_ID = "+id;
			String gridSql = "INSERT INTO DATALISTDETAIL(DLD_ID,DLD_CALLER,DLD_DETNO,DLD_FIELD,DLD_CAPTION,DLD_WIDTH,DLD_FIELDTYPE,"
					+ "DLD_ALIGNMENT,DLD_TABLE,DLD_CAPTION_FAN,DLD_CAPTION_EN,DLD_DLID,DLD_EDITABLE,DLD_RENDER,DLD_FLEX,DLD_MOBILEUSED,"
					+ "DLD_SUMMARYTYPE) SELECT DATALISTDETAIL_SEQ.nextval,'"+newCaller+"',DLD_DETNO,DLD_FIELD,DLD_CAPTION,DLD_WIDTH,DLD_FIELDTYPE,"
					+ "DLD_ALIGNMENT,DLD_TABLE,DLD_CAPTION_FAN,DLD_CAPTION_EN,"+newid+",DLD_EDITABLE,DLD_RENDER,DLD_FLEX,DLD_MOBILEUSED,"
					+ "DLD_SUMMARYTYPE FROM DATALISTDETAIL WHERE DLD_DLID = "+id;
			try {	
				baseDao.execute(formSql);
				baseDao.execute(gridSql);
				result = "复制成功,新caller:" + "<a href=\"javascript:openUrl('jsps/ma/dataList.jsp?formCondition=dl_idIS"+newid+"&gridCondition=dld_dlidIS"+newid+"')\">" + newCaller + "</a>&nbsp;";
			} catch (Exception e) {
				BaseUtil.showError("复制失败，错误："+e.getMessage());
			}
		}else{
			BaseUtil.showError("已经存在相同Caller的DataList，请确认！");
		}
		return result;
	}
}
