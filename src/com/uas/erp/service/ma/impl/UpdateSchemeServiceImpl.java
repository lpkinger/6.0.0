package com.uas.erp.service.ma.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.dao.common.UpdateSchemeDao;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.UpdateScheme;
import com.uas.erp.model.UpdateSchemeData;
import com.uas.erp.model.UpdateSchemeDetail;
import com.uas.erp.model.UpdateSchemeLog;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.common.SingleGridPanelService;
import com.uas.erp.service.common.impl.UpdateDataUtil;
import com.uas.erp.service.ma.UpdateSchemeService;

@Service("updateSchemeServiceImpl")
public class UpdateSchemeServiceImpl implements UpdateSchemeService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private SingleFormItemsService singleFormItemsService;
	@Autowired
	private SingleGridPanelService singleGridPanelService;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private UpdateSchemeDao updateSchemeDao;
	@Autowired
	private EmployeeDao employeeDao;

	@Override
	public void saveUpdateScheme(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gStore = BaseUtil.parseGridStoreToMaps(gridStore);
		store.put("indexfields_", store.get("indexfields_").toString().toUpperCase());
		StringBuffer test = new StringBuffer("select ");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < gStore.size(); i++) {
			Map<Object, Object> map = gStore.get(i);
			sb.append(map.get("field_")).append(",");
		}
		try {
			if (sb.length() == 0) {
				test.append(" * from " + store.get("table_").toString());
			} else {
				test.append(sb.substring(0, sb.length() - 1)).append(" from " + store.get("table_").toString());
			}
			if (store.get("condition_") != null && !store.get("condition_").equals("")) {
				test.append(" where " + store.get("condition_"));
			}
			if (store.get("indexfields_") != null && !store.get("indexfields_").equals("")) {
				test.append(" order by " + store.get("indexfields_").toString().replace("#", ","));
			}
			baseDao.execute(test.toString());
		} catch (Exception exception) {
			BaseUtil.showError("配置有误!请检查【更新表】、【默认条件】、【更新依据】及明细行【字段名】是否匹配.");
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "updateScheme", new String[] {}, new Object[] {});
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gStore, "updateSchemeDetail");
		baseDao.execute(formSql);
		baseDao.execute(gridSql);
		String condition = "'" + store.get("indexfields_").toString().replace("#", "','").toUpperCase() + "'";
		baseDao.deleteByCondition("updateSchemeDetail", "Scheme_Id_=" + store.get("id_") + "and field_ in (" + condition + ")");
		try {
			// 记录操作
			baseDao.logger.save(caller, "id_", store.get("id_"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gStore });
	}

	@Override
	public List<JSONTree> getTreeNode(String condition) {
		String con = "1=1";
		if (condition != null && condition != "") {
			con = con + "and " + condition;
		}
		List<JSONTree> treeList = new ArrayList<JSONTree>();
		SqlRowList rs = baseDao.queryForRowSet("select  id_,title_ from updateScheme where " + con + " order by table_");

		while (rs.next()) {
			JSONTree jsonTree = new JSONTree();
			jsonTree.setId(rs.getInt("id_"));
			jsonTree.setText(rs.getString("title_"));
			jsonTree.setAllowDrag(true);
			jsonTree.setLeaf(true);
			jsonTree.setCls("x-tree-parent");
			treeList.add(jsonTree);
		}
		return treeList;
	}

	@Override
	public List<Map<String, Object>> getColumns(String tablename) {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao
				.queryForRowSet("select a.COLUMN_NAME field_,a.DATA_TYPE ,a.data_length,a.data_precision,a.Data_Scale,B.Comments caption_ from user_tab_columns  a left join User_Col_Comments  b on a.table_name=B.Table_Name and A.Column_Name=B.Column_Name left join Tab_Col_property C on a.table_name=C.TableName_ and A.Column_Name=C.Colname_ where a.table_name= upper('"
						+ tablename + "') and C.ALLOWBATCHUPDATE_='1' order by column_id");
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("field_", rs.getString("field_"));
			String type = rs.getString("DATA_TYPE");
			if (type.equals("VARCHAR2")) {
				map.put("type_", rs.getString("DATA_TYPE") + "(" + rs.getString("data_length") + ")");
			} else if (type.equals("NUMBER")) {
				if (rs.getString("data_precision") != null && rs.getString("Data_Scale") != null) {
					map.put("type_", rs.getString("DATA_TYPE") + "(" + rs.getString("data_precision") + "," + rs.getString("Data_Scale")
							+ ")");
				} else if (rs.getString("data_precision") == null && rs.getString("Data_Scale") != null) {
					map.put("type_", rs.getString("DATA_TYPE") + "(38," + rs.getString("Data_Scale") + ")");
				} else {
					map.put("type_", rs.getString("DATA_TYPE"));
				}
			} else if (type.equals("FLOAT")) {
				int b = (int) Math.ceil(rs.getInt("data_precision") * 0.30103);
				map.put("type_", rs.getString("DATA_TYPE") + "(" + b + ")");
			} else {
				map.put("type_", rs.getString("DATA_TYPE"));
			}
			map.put("caption_", rs.getString("caption_"));
			map.put("width_", 80);
			datas.add(map);
		}
		return datas;
	}

	@Override
	public List<UpdateSchemeDetail> getIndexFields(Integer id) {
		List<UpdateSchemeDetail> schemeDetails = new ArrayList<UpdateSchemeDetail>();
		try {
			Object[] objs = baseDao.getFieldsDataByCondition("UpdateScheme", new String[] { "table_", "Indexfields_" }, "id_=" + id);
			if (objs != null) {
				String[] indexfields = String.valueOf(objs[1]).split("#");
				for (int i = 0; i < indexfields.length; i++) {
					UpdateSchemeDetail schemeDetail = new UpdateSchemeDetail();
					Object caption = baseDao.getFieldDataByCondition("User_Col_Comments", "comments",
							"table_name='" + String.valueOf(objs[0]).toUpperCase() + "' and column_name='" + indexfields[i].toUpperCase()
									+ "'");
					Object type = baseDao.getFieldDataByCondition("user_tab_columns", "data_type", "table_name='"
							+ String.valueOf(objs[0]).toUpperCase() + "' and column_name='" + indexfields[i].toUpperCase() + "'");
					schemeDetail.setScheme_Id_(id);
					schemeDetail.setField_(indexfields[i]);
					schemeDetail.setCaption_(String.valueOf(caption));
					schemeDetail.setType_(String.valueOf(type));
					schemeDetail.setWidth_(100);
					schemeDetail.setChecked_(1);
					schemeDetails.add(schemeDetail);
				}
			}
		} catch (Exception e) {
			return null;
		}
		return schemeDetails;

	}

	@Override
	public List<UpdateSchemeDetail> getUpdateDetails(Integer id, String condition) {
		String sql = "";
		if (condition != "")
			sql = "select * from updateschemedetail where " + condition + " and scheme_id_=?";
		else
			sql = "select * from updateschemedetail where scheme_id_=? order by detno_";
		try {
			List<UpdateSchemeDetail> schemeDetails = baseDao.getJdbcTemplate().query(sql,
					new BeanPropertyRowMapper<UpdateSchemeDetail>(UpdateSchemeDetail.class), id);
			return schemeDetails;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> getUpdateSchemes(String em_code) {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select emps_,title_,id_ from UpdateScheme where emps_ is not null");
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			String[] emps_ = rs.getString("emps_").split("#");
			for (String emp : emps_) {
				if (emp.equals(em_code)) {
					map.put("title_", rs.getString("title_"));
					map.put("id_", rs.getString("id_"));
					datas.add(map);
				}
			}
		}
		return datas;
	}

	// 更新checkbox选中状态
	@Override
	public void updateChecked(Integer id, String condition) {
		baseDao.updateByCondition("updateSchemedetail", "checked_=0", "Scheme_Id_=" + id);
		baseDao.updateByCondition("updateSchemedetail", "checked_=1", condition + " and Scheme_Id_=" + id);
	}

	/**
	 * 保存从excel解析的数据到表updateSchemeData
	 */
	@Override
	public int saveUpdateData(Integer id, List<String> data, Integer ulid) {
		int detno = 1;
		if (ulid == null) {
			int sequence = 0;
			Object obj = baseDao.getFieldDataByCondition("Updateschemelog ", "max(ul_sequence)", "ul_usid=" + id);
			sequence = obj == null ? 1 : (Integer.parseInt(obj.toString()) + 1);
			ulid = baseDao.getSeqId("UPDATESCHEMELOG_SEQ");
			StringBuffer sb = new StringBuffer("INSERT INTO Updateschemelog(ul_id,ul_usid,ul_sequence,ul_man,ul_count) VALUES(");
			sb.append(ulid);
			sb.append(",'");
			sb.append(id);
			sb.append("',");
			sb.append(sequence);
			sb.append(",'");
			sb.append(SystemSession.getUser().getEm_code());
			sb.append("',");
			sb.append(data.size());
			sb.append(")");
			baseDao.execute(sb.toString());
		} else {
			Object obj = baseDao.getFieldDataByCondition("updateschemedata", "max(ud_detno)", "ud_ulid=" + ulid);
			detno = obj == null ? 1 : (Integer.parseInt(obj.toString()) + 1);
			baseDao.updateByCondition("updateschemelog", "ul_count=ul_count+" + data.size(), "ul_id=" + ulid);
		}
		List<UpdateSchemeData> datas = new ArrayList<UpdateSchemeData>();
		for (String d : data) {
			datas.add(new UpdateSchemeData(d, ulid, detno++));
		}
		if (datas.size() > 0) {
			updateSchemeDao.save(datas);
		}
		return ulid;
	}

	@Override
	public Map<String, Object> getUpdateScheme(String id) {
		Employee employee = SystemSession.getUser();
		String _master = employee != null ? employee.getEm_master() : SpObserver.getSp();
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, Object> formdata = singleFormItemsService.getFormData("UpdateScheme", "id_=" + id, false);
		if (formdata != null) {
			modelMap.put("formdata", BaseUtil.parseMap2Str(formdata));
		}
		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller("UpdateScheme", _master);
		modelMap.put("griddata", baseDao.getDataStringByDetailGrid(detailGrids, "Scheme_Id_=" + id, null, null));
		List<Map<String, Object>> otherdatas = new ArrayList<Map<String, Object>>();
		otherdatas = getOtherData(id);
		if (otherdatas != null) {
			modelMap.put("otherdatas", otherdatas);
		}
		return modelMap;
	}

	@Override
	public void updateUpdateScheme(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		store.put("indexfields_", store.get("indexfields_").toString().toUpperCase());
		StringBuffer test = new StringBuffer("select ");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < gstore.size(); i++) {
			Map<Object, Object> map = gstore.get(i);
			sb.append(map.get("field_")).append(",");
		}
		try {
			if (sb.length() == 0) {
				test.append(" * from " + store.get("table_").toString());
			} else {
				test.append(sb.substring(0, sb.length() - 1)).append(" from " + store.get("table_").toString());
			}
			if (store.get("condition_") != null && !store.get("condition_").equals("")) {
				test.append(" where " + store.get("condition_"));
			}
			if (store.get("indexfields_") != null && !store.get("indexfields_").equals("")) {
				test.append(" order by " + store.get("indexfields_").toString().replace("#", ","));
			}
			baseDao.execute(test.toString());
		} catch (Exception exception) {
			BaseUtil.showError("配置有误!请检查【更新表】、【默认条件】、【更新依据】及明细行【字段名】是否匹配.");
		}
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "updateScheme", "id_");
		baseDao.execute(formSql);
		baseDao.deleteByCondition("updateSchemeDetail", "Scheme_Id_=" + store.get("id_"));
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "updateSchemeDetail");
		baseDao.execute(gridSql);
		String condition = "'" + store.get("indexfields_").toString().replace("#", "','").toUpperCase() + "'";
		baseDao.execute("delete updateSchemeDetail where Scheme_Id_=" + store.get("id_") + " and field_ in" + "(" + condition + ")");
		baseDao.logger.update(caller, "id_", store.get("id_"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });

	}

	@Override
	public void deleteUpdateScheme(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { id });
		baseDao.deleteById("updateScheme", "id_", id);
		baseDao.deleteById("updateSchemeDetail", "Scheme_Id_", id);
		// 记录操作
		baseDao.logger.delete(caller, "id_", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { id });

	}

	@Override
	public List<Map<String, Object>> getOtherData(String id) {
		List<Map<String, Object>> otherdatas = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao
				.queryForRowSet("select colname_ field_,b.DATA_TYPE ,b.data_length,b.data_precision,b.Data_Scale,c.Comments caption_ from TAB_COL_PROPERTY a left join user_tab_columns b on a.tablename_=b.table_name and "
						+ "a.colname_=b.column_name left join  User_Col_Comments  c on b.table_name=c.Table_Name and b.Column_Name=c.Column_Name where a.TABLENAME_"
						+ "=(select table_ from updatescheme where id_="
						+ id
						+ ") and allowBatchUpdate_='1' and colname_ not in (select field_ from updateSchemedetail where "
						+ "Scheme_Id_="
						+ id + ")");
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("field_", rs.getString("field_"));
			String type = rs.getString("DATA_TYPE");
			if (type.equals("VARCHAR2")) {
				map.put("type_", rs.getString("DATA_TYPE") + "(" + rs.getString("data_length") + ")");
			} else if (type.equals("NUMBER")) {
				if (rs.getString("data_precision") != null && rs.getString("Data_Scale") != null) {
					map.put("type_", rs.getString("DATA_TYPE") + "(" + rs.getString("data_precision") + "," + rs.getString("Data_Scale")
							+ ")");
				} else if (rs.getString("data_precision") == null && rs.getString("Data_Scale") != null) {
					map.put("type_", rs.getString("DATA_TYPE") + "(38," + rs.getString("Data_Scale") + ")");
				} else {
					map.put("type_", rs.getString("DATA_TYPE"));
				}
			} else if (type.equals("FLOAT")) {
				int b = (int) Math.ceil(rs.getInt("data_precision") * 0.30103);
				map.put("type_", rs.getString("DATA_TYPE") + "(" + b + ")");
			} else {
				map.put("type_", rs.getString("DATA_TYPE"));
			}
			map.put("caption_", rs.getString("caption_"));
			map.put("width_", 80);
			otherdatas.add(map);
		}
		return otherdatas;
	}

	@Override
	public List<UpdateSchemeData> getUpdateDatas(String condition) {
		try {
			List<UpdateSchemeData> datas = baseDao.getJdbcTemplate().query(
					"select * from updateschemedata where " + condition + " ORDER BY ud_detno",
					new BeanPropertyRowMapper<UpdateSchemeData>(UpdateSchemeData.class));
			return datas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void checkData(Integer ulid) {
		// TODO 校验数据
		String[] fields = { "field_", "type_", "caption_" };
		List<Object[]> fieldsType = baseDao.getFieldsDatasByCondition("updateschemedetail", fields,
				"scheme_id_ in (select ul_usid from updateschemelog where ul_id=" + ulid + ") AND checked_=1");
		List<Object> data = baseDao.getFieldDatasByCondition("updateschemedata", "UD_DATA", "ud_ulid=" + ulid);
		if (data != null) {
			List<Map<Object, Object>> storeList = new ArrayList<Map<Object, Object>>();
			for (int i = 0; i < data.size(); i++) {
				Map<Object, Object> store = BaseUtil.parseFormStoreToMap(data.get(i).toString());
				storeList.add(store);
			}
			for (int i = 0; i < fieldsType.size(); i++) {
				if ("DATE".equals((String) fieldsType.get(i)[1])) {
					Pattern p = Pattern.compile(Constant.REG_DATE);   //日期 yyyy-MM-dd
					for (int j = 0; j < storeList.size(); j++) {
						String str = (String) storeList.get(j).get((String) fieldsType.get(i)[0]);
						if(str==null){
							continue;
						}
						Matcher m = p.matcher(str);
						if (!m.matches()) {
							Pattern p1 = Pattern.compile(Constant.REG_DATETIME);   //时间yyyy-MM-dd HH:mm:ss
							Matcher m1 = p1.matcher(str);
							if(!m1.matches()){								
								BaseUtil.showError("第" + (j + 1) + "行的'" + (String) fieldsType.get(i)[2] + "'字段不是时间格式(yyyy-MM-dd)且不为空。");
							}
						}
					}
				} else if ("NUMBER".equals((String) fieldsType.get(i)[1])) {
					Pattern p = Pattern.compile(Constant.REG_NUM);
					for (int j = 0; j < storeList.size(); j++) {
						String str = (String) storeList.get(j).get((String) fieldsType.get(i)[0]);
						if(str==null){
							continue;
						}
						Matcher m = p.matcher(str);
						if (!m.matches()) {
							BaseUtil.showError("第" + (j + 1) + "行的'" + (String) fieldsType.get(i)[2] + "'字段不是数字格式且不为空。");
						}
					}
				}
			}
		}
		List<String> mList = baseDao.callProcedureWithOut("SP_CHECKUPDATEDATA", new Object[] { ulid }, new Integer[] { 1 }, new Integer[] {
				2, 3 });
		if (mList.get(0) != null) {
			baseDao.updateByCondition("UPDATESCHEMELOG", "UL_RESULT='" + mList.get(1) + "'", "UL_ID='" + ulid + "'");
			// / String[] mString = mList.get(1).split(",");
			String mString = mList.get(1).substring(1, mList.get(1).length());
			baseDao.updateByCondition("UPDATESCHEMEDATA", "UD_CHECKED=1", "UD_ID not in (" + mString + ") and UD_ULID=" + ulid);
			BaseUtil.showError(mList.get(0));
		} else {
			baseDao.updateByCondition("UPDATESCHEMELOG", "UL_CHECKED=1", "UL_ID='" + ulid + "'");
			baseDao.updateByCondition("UPDATESCHEMEDATA", "UD_CHECKED=1", "UD_ULID ='" + ulid + "'");
		}
	}

	@Override
	public void updateData(Employee employee, Integer ulid) {
		Object obj = baseDao.getFieldDataByCondition("updateschemelog", "ul_usid", "ul_id=" + ulid + " AND nvl(ul_success,0) = 0");
		if (obj != null) {
			Object tableName = baseDao.getFieldDataByCondition("UpdateScheme", "table_", "id_=" + obj);
			Object condition = baseDao.getFieldDataByCondition("UpdateScheme", "condition_", "id_=" + obj);
			if (tableName == null) {
				BaseUtil.showError("请检查更新方案!");
			} else {
				List<UpdateSchemeData> datas = getUpdateDatas("ud_ulid=" + ulid);
				List<UpdateSchemeDetail> indexfields = getIndexFields(Integer.valueOf(obj.toString()));
				List<UpdateSchemeDetail> updatedetails = getUpdateDetails(Integer.valueOf(obj.toString()), "checked_=1");
				String error = updateSchemeDao.updateData(null, tableName.toString(), new UpdateDataUtil(indexfields, updatedetails, datas,
						employee, String.valueOf(tableName), String.valueOf(condition)).getFormals());
				if (error != null)
					throw new SystemException(error);
				else {
					baseDao.updateByCondition("updateschemelog", "ul_success=1,ul_man='" + SystemSession.getUser().getEm_code() + "'",
							"ul_id=" + ulid);
					baseDao.updateByCondition("updateschemedata", "ud_success=1", "ud_ulid=" + ulid);
				}
			}
		} else {
			throw new SystemException("数据已删除或已更新,无法执行更新操作!");
		}
	}

	@Override
	public List<UpdateSchemeLog> getUpdateHistory(Integer id) {
		try {
			List<UpdateSchemeLog> datas = baseDao.getJdbcTemplate().query(
					"select * from UpdateSchemeLog where ul_usid=" + id + " ORDER BY ul_sequence",
					new BeanPropertyRowMapper<UpdateSchemeLog>(UpdateSchemeLog.class));
			return datas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getEmpdbfindData(String fields, String condition, int page, int pagesize) {
		List<Map<String, Object>> maps = employeeDao.getEmployeedata(fields, condition, page, pagesize);
		return BaseUtil.parseGridStore2Str(maps);
	}

	@Override
	public UpdateScheme exportUpdateScheme(String id) {
		return updateSchemeDao.getUpdateScheme(id);
	}

	@Override
	@Transactional
	public void importUpdateScheme(UpdateScheme scheme) {
		// title 作为唯一标识
		Integer id = baseDao.queryForObject("select id_ from UpdateScheme where title_=?", Integer.class, scheme.getTitle_());
		if (null != id) {
			// 覆盖原方案
			deleteUpdateScheme(id, "UpdateScheme");
			baseDao.deleteByCondition("TAB_COL_PROPERTY", "tablename_='" + scheme.getTable_() + "'");
		}
		// 重新取ID
		int newId = baseDao.getSeqId("UpdateScheme_SEQ");
		scheme.setId_(newId);
		if (null != scheme.getDetails()) {
			for (UpdateSchemeDetail det : scheme.getDetails()) {
				det.setScheme_Id_(newId);
			}
		}
		updateSchemeDao.saveUpdateScheme(scheme);
	}

	@Override
	public List<Map<Object, Object>> getErrData(int id) {
		SqlRowList rs = baseDao.queryForRowSet("select UD_DATA from UPDATESCHEMEDATA where UD_CHECKED=0 and UD_ULID=" + id);
		List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
		while (rs.next()) {
			Map<Object, Object> data = FlexJsonUtil.fromJson(rs.getString(1));
			list.add(data);
		}
		return list;
	}

}
