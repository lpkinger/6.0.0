package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ButtonDao;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DbfindSetGridDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.model.DBFindSetGrid;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.Dbfind;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.GridButton;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.LimitFields;
import com.uas.erp.model.Master;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.SingleGridPanelService;

@Service("singleGridPanelService")
public class SingleGridPanelServiceImpl implements SingleGridPanelService {
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private DbfindSetGridDao dbfindSetGridDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private HrJobDao hrJobDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ButtonDao buttonDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public GridPanel getGridPanelByCaller(String caller, String condition, Integer start, Integer end, Integer _m, boolean isCloud,String _copyConf) {
		// grid
		Employee employee = SystemSession.getUser();
		String _master = employee != null ? employee.getEm_master() : SpObserver.getSp();
		GridPanel gridPanel = new GridPanel();
		List<DetailGrid> detailGrids = null;
		List<DataListCombo> combos = null;
		List<DBFindSetGrid> dbFindSetGrids = null;
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			detailGrids = detailGridDao.getDetailGridsByCaller(caller, Constant.UAS_CLOUD);
			combos = dataListComboDao.getComboxsByCaller(caller, Constant.UAS_CLOUD);
			dbFindSetGrids = dbfindSetGridDao.getDbFindSetGridsByCaller(caller);
			SpObserver.putSp(_master);
		} else {
			detailGrids = detailGridDao.getDetailGridsByCaller(caller, _master);
			combos = dataListComboDao.getComboxsByCaller(caller, _master);
			dbFindSetGrids = dbfindSetGridDao.getDbFindSetGridsByCaller(caller);
		}
		if (detailGrids != null && detailGrids.size() > 0) {
			List<GridFields> fields = new ArrayList<GridFields>();// grid //
																	// store的字段fields
			List<GridColumns> columns = new ArrayList<GridColumns>();// grid的列信息columns
			Master master = employee.getCurrentMaster();
			// 多帐套，加帐套名称
			if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
				if (_m != null && 0 == _m) {
					master = null;
				} else {
					fields.add(new GridFields("CURRENTMASTER"));
					columns.add(new GridColumns("CURRENTMASTER", "帐套", 80, true));
				}
			}
			List<LimitFields> limits = new ArrayList<LimitFields>();
			if (!"admin".equals(employee.getEm_type())) {
				limits = hrJobDao.getLimitFieldsByType(caller, null, 0, employee.getEm_defaulthsid(), employee.getEm_master());
			}
			gridPanel.setLimits(limits);// 权限控制字段
			for (DetailGrid grid : detailGrids) {
				// 从数据库表detailgrid的数据，通过自定义的构造器，转化为extjs识别的fields格式，详情可见GridFields的构造函数
				fields.add(new GridFields(grid));
				columns.add(new GridColumns(grid, combos));
			}
			gridPanel.setGridColumns(columns);
			gridPanel.setGridFields(fields);

			List<Dbfind> dbfinds = new ArrayList<Dbfind>();
			for (DBFindSetGrid dbFindSetGrid : dbFindSetGrids) {
				dbfinds.add(new Dbfind(dbFindSetGrid));
			}
			gridPanel.setDbfinds(dbfinds);
			if (!condition.equals("")) {
				gridPanel.setDataString(baseDao.getDataStringByDetailGrid(detailGrids, condition, start, end));
			}else if(_copyConf!=null &&!"".equals(_copyConf)){//复制界面获取来源单据数据并进行替换
				Map<Object, Object> copyConf = BaseUtil.parseFormStoreToMap(_copyConf);
				Object[] obs=baseDao.getFieldsDataByCondition("form", new String[]{"fo_detailkeyfield","fo_detailmainkeyfield"}, "fo_caller='"+caller+"'");
				Object detailmainkeyfield=baseDao.getFieldDataByCondition("detailgrid","dg_field", "dg_caller='"+caller+"' and dg_logictype='mainField'");
				if(detailmainkeyfield==null || "".equals(detailmainkeyfield)){
					detailmainkeyfield = obs[1];
				}
				List<Map<String, Object>> maps = baseDao.getDetailGridData(detailGrids, detailmainkeyfield+"="+copyConf.get("keyValue"), employee, start, end);
				if(obs!=null){
					for (Map<String, Object> map : maps) {
						map.put(obs[0].toString(), "");//清空明细主键
						map.put(obs[1].toString(), "");//清空明细外键
					}
				}
				SqlRowList rs = baseDao.queryForRowSet("select cc_field,cc_copyvalue from　COPYCONFIGS where cc_findkind='detail' and cc_caller=?", caller);
				while (rs.next()) {
					for (Map<String, Object> map : maps) {
						if("null".equals(rs.getString("cc_copyvalue"))){
							map.put(rs.getString("cc_field"),"");
						}else{
							map.put(rs.getString("cc_field"), rs.getString("cc_copyvalue"));
						}
					}
				}
				gridPanel.setDataString(BaseUtil.parseGridStore2Str(maps));
			}
		}
		return gridPanel;
	}

	/**
	 * 按jprocessset表的流程caller及界面url来确定界面的caller
	 * 
	 * @param caller
	 * @param url
	 * @return
	 */
	private String getPageCallerByFlow(String caller, String url) {
		String whoami = baseDao.getJdbcTemplate().queryForObject(
				"select max(js_pagecaller) from jprocessset where js_caller=? and js_formurl=?", String.class, caller, url);
		if (whoami != null)
			return whoami;
		return caller;
	}

	@Override
	public Map<String, Object> getReadOnlyGrid(String caller, String condition, String url, String language, Employee employee,
			Integer start, Integer end) {
		String whoami = getPageCallerByFlow(caller, url);
		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(whoami, employee.getEm_master());
		Map<String, Object> rsGrid = new LinkedHashMap<String, Object>();
		if (detailGrids != null && detailGrids.size() > 0) {
			List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(whoami, employee.getEm_master());
			List<LimitFields> limits = new ArrayList<LimitFields>();
			if (!"admin".equals(employee.getEm_type())) {
				limits = hrJobDao.getLimitFieldsByType(whoami, null, 0, employee.getEm_defaulthsid(), employee.getEm_master());
			}
			List<Map<String, Object>> datas = baseDao.getDetailGridData(detailGrids, condition, employee, start, end);
			String field = null;
			Object value = null;
			for (DetailGrid column : detailGrids) {
				if (column.getDg_type() != null && column.getDg_width() > 0 && !isLimited(limits, column)) {
					field = column.getDg_field();
					if (field.contains(" ")) {// column有取别名
						String[] strs = field.split(" ");
						field = strs[strs.length - 1];
					}
					JSONArray tb = new JSONArray();
					for (Map<String, Object> data : datas) {
						value = getDisplayValue(combos, column, data.get(field));
						tb.add(value);
					}
					rsGrid.put(column.getDg_caption(), tb);
				}
			}
		}
		return rsGrid;
	}

	@Override
	public List<Map<String, Object>> getRecordByCode(String caller, String condition, Employee employee, Integer start, Integer end,
			boolean isCloud) {

		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, SpObserver.getSp());
		String master = SpObserver.getSp();
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			detailGrids = detailGridDao.getDetailGridsByCaller(caller, Constant.UAS_CLOUD);
			SpObserver.putSp(master);
		} else
			detailGrids = detailGridDao.getDetailGridsByCaller(caller, master);
		return baseDao.getDetailGridData(detailGrids, condition, employee, start, end);
	}

	/**
	 * 权限限制的字段
	 * 
	 * @param limits
	 * @param formDetail
	 * @return
	 */
	private boolean isLimited(List<LimitFields> limits, DetailGrid column) {
		for (LimitFields limit : limits) {
			if (limit.getLf_field().equals(column.getDg_field()))
				return true;
		}
		return false;
	}

	/**
	 * 配置成下拉框的，取显示值<br>
	 * 数字型的，转化格式
	 * 
	 * @param combos
	 * @param formDetail
	 * @param value
	 * @return
	 */
	private Object getDisplayValue(List<DataListCombo> combos, DetailGrid column, Object value) {
		if (value != null) {
			String type = column.getDg_type();
			if (type.equals("combo") || type.equals("editcombo")) {
				for (DataListCombo combo : combos) {
					if (combo.getDlc_fieldname().equals(column.getDg_field()) && combo.getDlc_value().equals(value)) {
						value = combo.getDlc_display();
						break;
					}
				}
			} else if (type.equals("yncolumn") || type.equals("ynnvcolumn")) {
				value = "1".equals(String.valueOf(value)) || "-1".equals(String.valueOf(value)) ? "是" : "否";
			} else if (type.equals("tfcolumn")) {
				value = "T".equals(String.valueOf(value)) ? "是" : "否";
			} else {
				if (!"".equals(value)) {
					if (type.equals("numbercolumn")) {
						value = NumberUtil.formatNumber(value, 0);
					} else if (type.equals("floatcolumn")) {
						value = NumberUtil.formatNumber(value, 2);
					} else if (type.matches("^floatcolumn\\d{1}$")) {
						int length = Integer.parseInt(type.replace("floatcolumn", ""));
						value = NumberUtil.formatNumber(value, length);
					}
				}
			}
		}
		return value;
	}

	/**
	 * 删除grid某行数据
	 */
	@Override
	@Transactional
	public void deleteDetail(String caller, String gridCaller, String condition, String autodelete,String gridReadOnly) {
		// 根据gridCaller取表名

		Employee employee = SystemSession.getUser();
		String language = SystemSession.getLang();
		Object table = baseDao.getFieldDataByCondition("DetailGrid", "dg_table", "dg_caller='" + gridCaller + "' and "
				+ "trim(dg_table) is not null order by dg_sequence ");
		if (table != null) {
			Integer id = Integer.parseInt(condition.split("=")[1].toString());
			if (!"true".equals(autodelete)) {
				String tableName = table.toString().split(" ")[0];
				if ("PRODIODETAIL".equals(tableName.toUpperCase())) {
					boolean notPosted = baseDao.checkIf("ProdInOut left join ProdIODetail on pi_id=pd_piid", "pd_id=" + id
							+ " AND pi_statuscode='UNPOST' AND nvl(pd_status,0)=0");
					if (!notPosted) {
						BaseUtil.showError("单据已过账或已删除,无法删除当前明细记录!");
					}
					//出库类型单据有条码数据不允许删除明细行
				   boolean hasBarcodeIo = baseDao.checkIf("prodiodetail inner join barcodeio on bi_piid=pd_piid and bi_batchid=pd_batchid", "pd_id="+id+" and bi_outqty>0");
				   if(hasBarcodeIo){
					   BaseUtil.showError("有出库条码采集,无法删除当前明细记录，请先撤销采集!");
				   }
				   //采购验收单明细删除时，只要对应的不良品出库单是在录入，未过账的  就可以删除明细。
					if("ProdInOut!PurcCheckin".equals(caller)){
						Object pi_id = baseDao.getFieldDataByCondition("prodiodetail", "pd_piid", condition);
						Object fcode = baseDao.getFieldDataByCondition("Prodinout", "pi_fromcode", "pi_id="+pi_id);
						if(StringUtil.hasText(fcode)){
							String dets = baseDao.getJdbcTemplate().queryForObject(
									"select wm_concat(pi_inoutno) from prodinout where pi_inoutno=(select pi_fromcode from prodinout where pi_id=?) and pi_class='不良品出库单' and pi_status='未过账' and pi_invostatus='在录入'",String.class,pi_id);
							Object[] fromcode = baseDao.getFieldsDataByCondition("prodinout", new String[]{"pi_inoutno","pi_id"}, "pi_inoutno=(select pi_fromcode from prodinout where pi_id="+pi_id+")");
							if(dets == null){
								BaseUtil.showError("关联的不良品出库单"+"<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!DefectOut&formCondition=pi_idIS" + fromcode[1] 
										+ "&gridCondition=pd_piidIS" + fromcode[1] + "')\">" + fromcode[0] + "</a>&nbsp;"+"不是在录入、未过账的状态,不允许进行当前操作");
							}
						}
					}
				}
				if (!"AccountRegister!Bank".equals(caller)) {
					SqlRowList rs = baseDao.queryForRowSet("SELECT * FROM form WHERE fo_caller=?", gridCaller);
					if (rs.next()) {
						String keyField = rs.getString("fo_keyfield");
						String sCode = rs.getString("fo_statuscodefield");
						String mainField = rs.getString("fo_detailmainkeyfield");
						String formTable = rs.getString("fo_table");
						if (keyField != null && sCode != null && mainField != null && formTable != null&&"true".equals(gridReadOnly)) {
							Object status = baseDao.getFieldDataByCondition(formTable.split(" ")[0] + " left join " + tableName + " ON "
									+ keyField + "=" + mainField, sCode, condition);
							if (status != null && !status.equals(" ")) {
								if (!("ENTERING".equals(status) || "UNPOST".equals(status) || "UNAUDIT".equals(status))) {
									BaseUtil.showError("只能删除状态为在录入、未过账的单据的明细数据!");
								}
							}
						}
					}
				}
			}
			//项目团队角色
			if("Team".equals(caller)){
				Object[] objs = baseDao.getFieldsDataByCondition("TeamMember", new String[]{"tm_teamid","tm_employeecode"}, condition);
				Object team_pricode = baseDao.getFieldDataByCondition("Team", "team_pricode", "team_id="+objs[0]);
				boolean bool = baseDao.checkIf("( ProjectTask left join ProjectMainTask on pt_id=ptid)","pt_prjcode='"+team_pricode+"' and resourcecode='" + objs[1] + "'");
				if(bool){
					BaseUtil.showError("该资源在当前项目中存在任务，不允许删除！");
				}
			}
			// 通过ECN转入的制造ECN，明细行不允许删除；
			if (baseDao.isDBSetting(caller, "deleteDetailAllow")) {
				SqlRowList str;
				str = baseDao
						.queryForRowSet("select mc_ecncode from MakeMaterialChange where mc_id=(select md_mcid from MakeMaterialChangeDet where md_id ="
								+ id + ") and  mc_ecncode is not null");
				if (str.next()) {
					BaseUtil.showError("通过工单ECN转入的ECN，明细行不允许删除");
				}
			}
			// 判断是否产生生产日报，如产生生产日报则不允许删除明细
			if (baseDao.isDBSetting(caller, "haveDispatch")) {
				int haveDispatch = baseDao
						.getCount("select count(did_makecode) as haveDispatch from dispatchdetail where (did_stepcode,did_makecode)=(select mcp_stepcode,mcp_macode from makecraftpiecework where "
								+ condition + ")");
				if (haveDispatch > 0) {
					BaseUtil.showError("当前明细已产生生产日报，不允许删除!");
				}
			}
			handlerService.handler(gridCaller, "deletedetail", "before", new Object[] { id });
			// 删除grid某条记录
			baseDao.deleteByCondition(table.toString().split(" ")[0], condition);
			// 记录日志
			try {
				// 记录操作
				baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.delete", language), BaseUtil
						.getLocalMessage("msg.deleteSuccess", language), gridCaller + "|" + condition));
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 执行删除后的其它逻辑
			handlerService.handler(gridCaller, "deletedetail", "after", new Object[] { id });
		} else {
			BaseUtil.showError("无法删除!");
		}
	}

	@Override
	public JSONArray getGridButton(String caller) {
		List<GridButton> buttons = buttonDao.getGridButtons(SpObserver.getSp(), caller);
		if (buttons != null) {
			JSONArray array = new JSONArray();
			JSONObject object = null;
			for (GridButton button : buttons) {
				object = new JSONObject();
				object.put("xtype", button.getGb_xtype());
				object.put("url", button.getGb_url());
				String conf = button.getGb_conf();
				if(conf!=null){
					Map<Object, Object> map = BaseUtil.parseFormStoreToMap(conf);
					for (Map.Entry<Object, Object> entry : map.entrySet()) {  
						String key = entry.getKey().toString();  
						String value = entry.getValue().toString();  
						object.put(key,value);
					} 
				}
				array.add(object);
			}
			return array;
		}
		return null;
	}

	/**
	 * GridPage页面grid批量保存 在detailgrid配置grid，其中，keyField和necessaryField必须配置,
	 * 如果有状态(码)字段，也要配置；在gridbutton配置使用到的button；
	 * 
	 * @param data
	 *            grid有效数据
	 */
	@Override
	public void batchSave(String language, Employee employee, String caller, String data) {
		if (employee == null)
			employee = SystemSession.getUser();
		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, employee.getEm_master());
		String keyField = null;
		String statusField = null;
		String statuscodeField = null;
		String tablename = null;
		for (DetailGrid grid : detailGrids) {
			if (grid.getDg_table() != null) {
				tablename = grid.getDg_table().split(" ")[0];
			}
			if (grid.getDg_logictype() != null) {
				if (grid.getDg_logictype().equals("keyField")) {
					keyField = grid.getDg_field();
				} else if (grid.getDg_logictype().equals("statusField")) {
					statusField = grid.getDg_field();
				} else if (grid.getDg_logictype().equals("statuscodeField")) {
					statuscodeField = grid.getDg_field();
				}
			}
		}
		if (keyField != null) {
			List<String> sqls = new ArrayList<String>();
			List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
			handlerService.beforeSave(caller, maps);
			for (Map<Object, Object> map : maps) {
				if (statusField != null) {
					map.put(statusField, BaseUtil.getLocalMessage("ENTERING", language));
				}
				if (statuscodeField != null) {
					map.put(statuscodeField, "ENTERING");
				}
				if (map.get(keyField) == null || Integer.parseInt(map.get(keyField).toString()) <= 0) {
					map.put(keyField, baseDao.getSeqId(tablename.toUpperCase() + "_SEQ"));
					sqls.add(SqlUtil.getInsertSqlByFormStore(map, tablename, new String[] {}, new Object[] {}));
				} else {
					sqls.add(SqlUtil.getUpdateSqlByFormStore(map, tablename, keyField));
				//	sqls.add("update ProdChargeDetailAN set pd_currency='" + baseDao.getDBSetting("defaultCurrency")
				//	+ "',pd_rate=1 where pd_id=" + map.get(keyField) + " and nvl(pd_currency,' ')=' '");
				}
			}
			// maz 采购收料单没有选择币别时默认赋值本位币 2017080602
			sqls.add("update ProdChargeDetailAN set pd_currency='" + baseDao.getDBSetting("defaultCurrency")
			+ "',pd_rate=1 where pd_anid=" + maps.get(0).get("pd_anid") + " and nvl(pd_currency,' ')=' '");
			baseDao.execute(sqls);
		} else {
			BaseUtil.showError("页面代号:" + caller + "未配置keyField");
		}
	}

	public void vastClose(String language, Employee employee, String caller, int[] id) {
		// 整批结案
		Object[] objs = baseDao.getFieldsDataByCondition("detailgrid", new String[] { "dg_table", "dg_field" }, "dg_caller='" + caller
				+ "' AND dg_logictype='statusField'");
		if (objs != null) {
			Object obj = baseDao
					.getFieldDataByCondition("detailgrid", "dg_field", "dg_caller='" + caller + "' AND dg_logictype='keyField'");
			for (int key : id) {
				baseDao.updateByCondition((String) objs[0], objs[1] + "='FINISH'", obj + "=" + key);
			}
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.statusFieldisnull", language));
		}
		// 记录日志
	}

	@Override
	public void setDetailDetno(String caller, String dfield, String mfield, Integer id, int detno) {
		// 根据caller取表名
		Object table = baseDao.getFieldDataByCondition("DetailGrid", "dg_table", "dg_caller='" + caller + "' and "
				+ "trim(dg_table) is not null");
		if (table != null) {
			baseDao.updateByCondition(table.toString().split(" ")[0], dfield + "=" + dfield + "-1", mfield + "=" + id + " AND " + dfield
					+ ">" + detno);
		}
	}

	@Override
	public void saveItemGrid(String language, Employee employee, String data) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(data);
		int id;
		for (Map<Object, Object> map : grid) {
			id = baseDao.getSeqId("ITEMGRID_SEQ");
			map.put("ig_id", id);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStoreWithoutDate(grid, "itemgrid"));
	}

	@Override
	public void updateItemGrid(String language, Employee employee, String data) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(data);
		baseDao.execute(SqlUtil.getInsertOrUpdateSqlbyGridStoreWithoutDate(grid, "itemgrid", "ig_id"));
	}

	@Override
	public List<DetailGrid> getDetailsByCaller(String caller) {
		List<DetailGrid> details = detailGridDao.getDetailGridsByCaller(caller, SpObserver.getSp());
		return details;
	}

	@Override
	public void batchSave(String caller, String data, int keyValue) {
		Employee employee = SystemSession.getUser();
		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, employee.getEm_master());
		String keyField = null;// 明细表主键字段
		String mainField = null;// 关联主表字段
		String tablename = null;// 表名
		String detnoField = null;// 排序字段
		int detno = 0;
		List<String> ignoreField = new ArrayList<String>();// 忽略字段
		for (DetailGrid grid : detailGrids) {
			if (grid.getDg_table() != null) {
				tablename = grid.getDg_table().split(" ")[0];
			}
			if (grid.getDg_logictype() != null) {
				if (grid.getDg_logictype().equals("keyField")) {
					keyField = grid.getDg_field();
				} else if (grid.getDg_logictype().equals("mainField")) {
					mainField = grid.getDg_field();
				} else if (grid.getDg_logictype().equals("detno")) {
					detnoField = grid.getDg_field();
				} else if (grid.getDg_logictype().equals("ignore")) {
					ignoreField.add(grid.getDg_field());
				}
			}
		}
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		if (keyField != null) {
			List<String> sqls = new ArrayList<String>();
			handlerService.beforeSave(caller, data);
			for (Map<Object, Object> map : maps) {
				for (String field : ignoreField) {// 去掉忽略字段
					if (map.get(field) != null) {
						map.remove(field);
					}
				}
				if (keyField != null) {
					map.put(keyField, baseDao.getSeqId(tablename.toUpperCase() + "_SEQ"));
				}
				if (mainField != null) {
					map.put(mainField, keyValue);
				}
				if (detnoField != null && map.get(detnoField) == null) {
					map.put(detnoField, ++detno);
				}
				sqls.add(SqlUtil.getInsertSqlByFormStore(map, tablename, new String[] {}, new Object[] {}));
			}
			baseDao.execute(sqls);
		} else {
			BaseUtil.showError("页面代号:" + caller + "未配置keyField");
		}
	}

	/**
	 * 根据caller 和主表ID删除明细数据，用于直接将Excel数据插入从表前删除原有数据
	 */
	@Override
	public void deletegridbycaller(String caller, int keyValue) {
		Employee employee = SystemSession.getUser();
		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, employee.getEm_master());
		String mainField = null;
		String tablename = null;
		for (DetailGrid grid : detailGrids) {
			if (grid.getDg_table() != null) {
				tablename = grid.getDg_table().split(" ")[0];
			}
			if (grid.getDg_logictype() != null) {
				if (grid.getDg_logictype().equals("mainField")) {
					mainField = grid.getDg_field();
				}
			}
		}
		if (mainField != null) {
			baseDao.deleteByCondition(tablename, mainField + "=" + keyValue);
		} else {
			BaseUtil.showError("页面代号:" + caller + "未配置mainField");
		}
	}
	@Override
	public GridPanel getGridDatas(String caller, String condition, Integer page, Integer pageSize, Integer _m, boolean isCloud,String _copyConf,String orderby) {
		// grid
		Employee employee = SystemSession.getUser();
		String _master = employee != null ? employee.getEm_master() : SpObserver.getSp();
		GridPanel gridPanel = new GridPanel();
		List<DetailGrid> detailGrids = null;
		List<DataListCombo> combos = null;
		List<DBFindSetGrid> dbFindSetGrids = null;
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			detailGrids = detailGridDao.getDetailGridsByCaller(caller, Constant.UAS_CLOUD);
			combos = dataListComboDao.getComboxsByCaller(caller, Constant.UAS_CLOUD);
			dbFindSetGrids = dbfindSetGridDao.getDbFindSetGridsByCaller(caller);
			SpObserver.putSp(_master);
		} else {
			detailGrids = detailGridDao.getDetailGridsByCaller(caller, _master);
			combos = dataListComboDao.getComboxsByCaller(caller, _master);
			dbFindSetGrids = dbfindSetGridDao.getDbFindSetGridsByCaller(caller);
		}
		if (detailGrids != null && detailGrids.size() > 0) {
			List<GridFields> fields = new ArrayList<GridFields>();// grid //
																	// store的字段fields
			List<GridColumns> columns = new ArrayList<GridColumns>();// grid的列信息columns
			Master master = employee.getCurrentMaster();
			// 多帐套，加帐套名称
			if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
				if (_m != null && 0 == _m) {
					master = null;
				} else {
					fields.add(new GridFields("CURRENTMASTER"));
					columns.add(new GridColumns("CURRENTMASTER", "帐套", 80, true));
				}
			}
			List<LimitFields> limits = new ArrayList<LimitFields>();
			if (!"admin".equals(employee.getEm_type())) {
				limits = hrJobDao.getLimitFieldsByType(caller, null, 0, employee.getEm_defaulthsid(), employee.getEm_master());
			}
			gridPanel.setLimits(limits);// 权限控制字段
			for (DetailGrid grid : detailGrids) {
				// 从数据库表detailgrid的数据，通过自定义的构造器，转化为extjs识别的fields格式，详情可见GridFields的构造函数
				fields.add(new GridFields(grid));
				columns.add(new GridColumns(grid, combos));
			}
			gridPanel.setGridColumns(columns);
			gridPanel.setGridFields(fields);

			List<Dbfind> dbfinds = new ArrayList<Dbfind>();
			for (DBFindSetGrid dbFindSetGrid : dbFindSetGrids) {
				dbfinds.add(new Dbfind(dbFindSetGrid));
			}
			gridPanel.setDbfinds(dbfinds);
			gridPanel.setDataCount(baseDao.getCount(baseDao.getDataCount(detailGrids,condition)));
			gridPanel.setDataString(baseDao.getDataStringByDetailGrid(detailGrids, condition, (page-1)*pageSize+1, pageSize*page));
		}
		return gridPanel;
	}
}
