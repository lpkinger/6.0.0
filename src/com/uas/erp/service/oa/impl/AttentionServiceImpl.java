package com.uas.erp.service.oa.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.HrOrgStrDao;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Form;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.HROrg;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.oa.AttentionService;

@Service
public class AttentionServiceImpl implements AttentionService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private HrOrgStrDao hrOrgStrDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private FormDao formDao;

	@Override
	public void deleteAttentionGrade(String data, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> s : store) {
			baseDao.deleteById("AttentionGrade", "ag_id", Integer.parseInt(s.get("ag_id").toString()));
		}
	}

	@Override
	public void saveAttentionGrade(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller,new Object[] { store });
		String scope = store.get("ag_scope_from") + "% ~ " + store.get("ag_scope_to") + "%";
		store.remove("ag_scope_from");
		store.remove("ag_scope_to");
		store.put("ag_scope", scope);
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AttentionGrade", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ag_id", store.get("ag_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller,new Object[] { store });

	}

	@Override
	public void saveAttentionSub(String caller, String formStore, String param, String mutiselected) {
		Map<Object, Object> formstore = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstore = BaseUtil.parseGridStoreToMaps(param);
		List<Map<Object, Object>> selects = BaseUtil.parseGridStoreToMaps(mutiselected);
		List<String> selectcode = new ArrayList<String>();
		List<String> insertSqls = new ArrayList<String>();
		if (caller.equals("AttentionPerson")) {
			for (Map<Object, Object> select : selects) {
				selectcode.add(select.get("ap_subcode").toString());
			}
			String[] attentedemid = formstore.get("ap_attentedemid").toString().split("#");
			String[] attentedemname = formstore.get("ap_attentedemname").toString().split("#");
			for (int i = 0; i < attentedemid.length; i++) {
				int ap_attentedemid = Integer.parseInt(attentedemid[i].toString());
				for (Map<Object, Object> grid : gridstore) {
					if (selectcode.contains(grid.get("ap_subcode"))) {
						grid.remove("ap_isuse");
						grid.put("ap_isuse", 1);
					}
					grid.remove("ap_emid");
					grid.put("ap_emid", SystemSession.getUser().getEm_id());
					grid.put("ap_attentedemid", ap_attentedemid);
					grid.put("ap_attentedemname", attentedemname[i]);
					grid.put("ap_id", baseDao.getSeqId("AttentionPerson_SEQ"));
					insertSqls.add(SqlUtil.getInsertSqlByFormStore(grid, "AttentionPerson", new String[] {},
							new Object[] {}));
				}
			}
		} else {
			for (Map<Object, Object> grid : gridstore) {
				if (selectcode.contains(grid.get("ap_subcode"))) {
					grid.remove("ap_isuse");
					grid.put("ap_isuse", 1);
				}
				grid.remove("aa_emid");
				grid.put("aa_emid", SystemSession.getUser().getEm_id());
				grid.put("aa_accreditedemid", formstore.get("aa_accreditedemid"));
				grid.put("aa_accreditedemname", formstore.get("aa_accreditedemname"));
				grid.put("aa_id", baseDao.getSeqId("AccreditAttention_SEQ"));
				insertSqls.add(SqlUtil.getInsertSqlByFormStore(grid, "AccreditAttention", new String[] {},
						new Object[] {}));
			}

		}
		baseDao.execute(insertSqls);
	}

	@Override
	public JSONArray getData(String caller, String condition,int page, int pageSize) {
		JSONArray dataarray = new JSONArray();
		SqlRowList rs1 = baseDao.queryForRowSet("select * from AttentionPerson where   ap_emid=0 And ap_isuse=" + 1);
		List<Map<String, Object>> Maps = new ArrayList<Map<String, Object>>();
		while (rs1.next()) {
			Maps.add(rs1.getCurrentMap());
		}
		HROrg hrOrg = hrOrgStrDao.getHrOrgByEmId(SystemSession.getUser().getEm_id());
		if (hrOrg.getOr_headmancode()!=null && hrOrg.getOr_headmancode().equals(SystemSession.getUser().getEm_code())) {
			List<Employee> employees = getAllEmployees(hrOrg);
			int lastcount = employees.size();
			if (employees.size() > page * pageSize) {
				lastcount = page * pageSize;
			}
			if (employees.size() > 0) {
				for (int i = (page - 1) * pageSize; i < lastcount; i++) {
					Employee em = employees.get(i);
					JSONObject jt = new JSONObject();
					jt.put("ap_attentedemid", em.getEm_id());
					jt.put("ap_attentedemname", em.getEm_name());
					jt.put("ap_allcount", 0);
					jt.put("ap_handledcount", 0);
					jt.put("ap_untreatedcount", 0);
					jt.put("ap_percentdone", 0);
					jt.put("ap_status", "很差");
					jt.put("ap_color", "FF0000");
					String maindata = "";
					int count = 0;
					for (Map<String, Object> map : Maps) {
						int days = Integer.parseInt(map.get("ap_days").toString());
						count++;
						String name = map.get("ap_name").toString();
						maindata += count + "." + name + "(" + days + " 天 ) ;    ";
						jt.put("ap_maindata", maindata);
						String value = rs1.getString("ap_subcode");
						if (value.equalsIgnoreCase("WorkDaily")) {
						} else if (value.equalsIgnoreCase("Agenda")) {
						} else if (value.equalsIgnoreCase("WorkAttendance")) {
						} else if (value.equalsIgnoreCase("JProcess2!DealByMe")) {
						} else if (value.equalsIgnoreCase("JProcess!Deal")) {
						} else if (value.equalsIgnoreCase("ProjectPlan")) {
						} else if (value.equalsIgnoreCase("WorkRecord")) {
							SqlRowList rs2 = baseDao
									.queryForRowSet("select count(*) from workrecord where wr_recorderemid="
											+ em.getEm_id() + " And wr_recorddate>to_date('" + getPreviousDate(days)
											+ "','yyyy-MM-dd') AND wr_recorddate<to_date('" + dateformat(new Date())
											+ "','yyyy-MM-dd')");
							while (rs2.next()) {
								jt.put("ap_allcount", rs2.getInt(1));
								jt.put("ap_handledcount", rs2.getInt(1));
								jt.put("ap_untreatedcount", 0);
							}
						} else if (value.equalsIgnoreCase("newSynergy")) {
						} else if (value.equalsIgnoreCase("ProjectFeePlease")) {

						} else if (value.equalsIgnoreCase("ProjectFeeClaim")) {
						} else if (value.equalsIgnoreCase("Meeting")) {
						}

					}
					dataarray.add(jt);
				}
			}

		}

		return dataarray;
	}

	@Override
	public JSONArray getAaccreditData(String caller, String condition) {
		JSONArray dataarray = new JSONArray();
		SqlRowList rs = baseDao.queryForRowSet("select aa_emid from AccreditAttention where aa_accreditedemid="
				+ SystemSession.getUser().getEm_id() + " group by aa_emid ");
		while (rs.next()) {
			SqlRowList rs1 = baseDao
					.queryForRowSet("select ap_name,ap_attentedemid,ap_attentedemname,aa_days,aa_subcode,aa_accreditemname  from AttentionPerson left join AccreditAttention on ap_emid=aa_emid where aa_isuse=1 aa_emid="
							+ rs.getInt(1));
			int days = rs1.getInt("aa_days");
			while (rs1.next()) {
				JSONObject jt = new JSONObject();
				jt.put("aa_accreditemname", rs1.getInt("aa_accreditemname"));
				jt.put("ap_attentedemid", rs1.getInt("ap_attentedemid"));
				jt.put("ap_attentedemname", rs1.getString("ap_attentedemname"));
				jt.put("ap_name", rs1.getString("ap_name"));
				jt.put("ap_allcount", 0);
				jt.put("ap_handledcount", 0);
				jt.put("ap_untreatedcount", 0);
				jt.put("ap_percentdone", 0);
				jt.put("ap_status", "很差");
				jt.put("ap_color", "FF0000");
				dataarray.add(jt);
				String value = rs1.getString("aa_subcode");
				if (value.equalsIgnoreCase("WorkDaily")) {
				} else if (value.equalsIgnoreCase("Agenda")) {
				} else if (value.equalsIgnoreCase("WorkAttendance")) {
				} else if (value.equalsIgnoreCase("JProcess2!DealByMe")) {
				} else if (value.equalsIgnoreCase("JProcess!Deal")) {
				} else if (value.equalsIgnoreCase("ProjectPlan")) {
				} else if (value.equalsIgnoreCase("WorkRecord")) {
					SqlRowList rs2 = baseDao.queryForRowSet("select count(*) from workrecord where wr_recorderemid="
							+ rs1.getInt("ap_attentedemid") + " And wr_recorddate>to_date('" + getPreviousDate(days)
							+ "','yyyy-MM-dd') AND wr_recorddate<to_date('" + dateformat(new Date())
							+ "','yyyy-MM-dd')");
					while (rs2.next()) {
						jt.put("ap_allcount", rs2.getInt(1));
						jt.put("ap_handledcount", rs2.getInt(1));
						jt.put("ap_untreatedcount", 0);
					}
				} else if (value.equalsIgnoreCase("newSynergy")) {
				} else if (value.equalsIgnoreCase("ProjectFeePlease")) {

				} else if (value.equalsIgnoreCase("ProjectFeeClaim")) {
				} else if (value.equalsIgnoreCase("Meeting")) {
				}
			}
		}
		return dataarray;
	}

	private String getPreviousDate(int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String time = sdf.format(date);
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		cd.add(Calendar.DATE, -10);// 减10天
		return sdf.format(cd.getTime());
	}

	private String dateformat(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}

	@Override
	public GridPanel getGridPanel(String caller) {
		GridPanel panel = new GridPanel();
		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, SpObserver.getSp());
		if (detailGrids != null && detailGrids.size() > 0) {
			List<GridFields> fields = new ArrayList<GridFields>();
			List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, SpObserver.getSp());
			List<GridColumns> columns = new ArrayList<GridColumns>();
			for (DetailGrid grid : detailGrids) {
				// 从数据库表detailgrid的数据，通过自定义的构造器，转化为extjs识别的fields格式，详情可见GridFields的构造函数
				fields.add(new GridFields(grid));
				columns.add(new GridColumns(grid, combos));
			}
			panel.setGridFields(fields);
			panel.setGridColumns(columns);
		}
		return panel;
	}

	@Override
	public JSONArray getAttentionsByEmId(String caller, int attentedemid) {
		JSONArray arr = new JSONArray();
		SqlRowList sl = baseDao.queryForRowSet("select ap_subcode,ap_days from attentionperson where ap_emid=" + 0
				+ " AND ap_isuse=1 order by ap_detno asc");
		while (sl.next()) {
			JSONObject jo = new JSONObject();
			jo.put("caller", sl.getString("ap_subcode"));
			jo.put("days", sl.getInt("ap_days"));
			arr.add(sl);
		}
		return arr;
	}

	@Override
	public Map<String, Object> getEmployeeData(String caller, int emid) {
		Form form = formDao.getForm(caller, SystemSession.getUser().getEm_master());
		return baseDao.getFormData(form, "em_id=" + emid);
	}

	@Override
	public String ChekOnlineEmployee(int emid) {
		String date = DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date(
				System.currentTimeMillis() - 10 * 1000));
		Object data = baseDao.getFieldDataByCondition("Employee", "em_id", " em_id=" + emid + " AND em_lastin > "
				+ date);
		if (data != null) {
			return "YES";
		}
		return "NO";
	}

	@Override
	public void deleteAttentions(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		List<String> deleteSqls = new ArrayList<String>();
		for (Map<Object, Object> map : maps) {
			Object attentedemid = map.get("ap_attentedemid");
			deleteSqls.add(" delete from  AttentionPerson where ap_attentedemid=" + attentedemid + " AND ap_emid="
					+ SystemSession.getUser().getEm_id());
		}
		baseDao.execute(deleteSqls);
	}

	@Override
	public List<JSONTree> getEmployees(String caller, int emid) {
		List<JSONTree> trees = new ArrayList<JSONTree>();
		HROrg hrOrg = hrOrgStrDao.getHrOrgByEmId(emid);
		if (hrOrg.getOr_headmancode()!=null && hrOrg.getOr_headmancode().equals(SystemSession.getUser().getEm_code())) {
			JSONTree hrtree = new JSONTree();
			List<HROrg> hrOrgs = hrOrgStrDao.getAllHrOrgs(null);
			for (HROrg org : hrOrgs) {
				if (org.getOr_id() == hrOrg.getOr_id()) {
					hrtree = recursionJSONTree(hrOrgs, org);
					List<Employee> emList = employeeDao.getEmployeesByOrId(org.getOr_id());
					List<JSONTree> children = new ArrayList<JSONTree>();
					for (Employee employee : emList) {
						if (!employee.getEm_code().equals(hrtree.getQtip())) {
							children.add(new JSONTree(org.getOr_id()));
						}
					}
					hrtree.setCls("x-tree-cls-parent");
					hrtree.getChildren().addAll(children);
					trees.add(hrtree);
				}
			}
		}
		return trees;
	}

	public JSONTree recursionJSONTree(List<HROrg> hrOrgs, HROrg hrOrg) {

		JSONTree JSONTree = new JSONTree();
		JSONTree.setId(hrOrg.getOr_id());
		JSONTree.setParentId(hrOrg.getOr_subof());
		JSONTree.setText(hrOrg.getOr_headmanname() + "  (" + hrOrg.getOr_department() + ")");
		JSONTree.setQtip(hrOrg.getOr_headmancode());
		List<HROrg> hList = new ArrayList<HROrg>();
		List<JSONTree> childrenBoxTrees = new ArrayList<JSONTree>();
		if (hrOrg.getOr_isleaf() == 1) {
			JSONTree.setAllowDrag(true);
			JSONTree.setLeaf(false);
			JSONTree.setChildren(new ArrayList<JSONTree>());
		} else {
			JSONTree.setLeaf(false);
			JSONTree childrenBoxTree = new JSONTree();
			hList = getChildren(hrOrgs, hrOrg);
			Iterator<HROrg> hIterator = hList.iterator();
			while (hIterator.hasNext()) {
				HROrg hrOrg2 = (HROrg) hIterator.next();
				childrenBoxTree = recursionJSONTree(hrOrgs, hrOrg2);
				List<Employee> emList = employeeDao.getEmployeesByOrId(hrOrg2.getOr_id());
				List<JSONTree> children = new ArrayList<JSONTree>();
				for (Employee employee : emList) {
					if (!employee.getEm_code().equals(childrenBoxTree.getQtip())) {
						children.add(new JSONTree(hrOrg2.getOr_id()));
					}
				}
				childrenBoxTree.getChildren().addAll(children);
				childrenBoxTree.setCls("x-tree-cls-parent");
				childrenBoxTrees.add(childrenBoxTree);
			}
			JSONTree.setChildren(childrenBoxTrees);
		}
		return JSONTree;
	}

	public List<HROrg> getChildren(List<HROrg> hrOrgs, HROrg hrOrg) {

		List<HROrg> hrOrgList = new ArrayList<HROrg>();
		Iterator<HROrg> hrOrgIterator = hrOrgs.iterator();
		HROrg hOrg = new HROrg();
		while (hrOrgIterator.hasNext()) {
			hOrg = hrOrgIterator.next();
			if (hOrg.getOr_subof() == hrOrg.getOr_id()) {
				hrOrgList.add(hOrg);
			}
		}
		return hrOrgList;
	}

	public List<Employee> getAllEmployees(HROrg hrOrg) {
		List<Employee> employees = new ArrayList<Employee>();
		addEmployees(employees, hrOrg);
		return employees;
	}

	private void addEmployees(List<Employee> employees, HROrg hrOrg) {
		employees.addAll(employeeDao.getEmployeesByOrId(hrOrg.getOr_id()));
		List<HROrg> orgs = hrOrgStrDao.getHrOrgbyParentId(hrOrg.getOr_id());
		if (orgs.size() > 0) {
			for (HROrg hrorg : orgs) {
				addEmployees(employees, hrorg);
			}
		}
	}

	@Override
	public int getAttentionCounts(String caller) {
		HROrg hrorg = hrOrgStrDao.getHrOrgByEmId(SystemSession.getUser().getEm_id());
		List<Employee> employees = new ArrayList<Employee>();
		addEmployees(employees, hrorg);
		return employees.size();
	}

	@Override
	public Map<String, Object> getEmployeeDataByParam(String caller, String param) {
		Form form = formDao.getForm(caller, SystemSession.getUser().getEm_master());
		String condition = "";
		if (param.contains("-")) {
			condition = "em_id=" + Integer.parseInt(param.replaceFirst("-", ""));
		} else
			condition = "em_code='" + param + "'";
		return baseDao.getFormData(form, condition);
	}

	@Override
	public JSONArray getAttentionDataByParam(String param, String caller) {
		JSONArray dataarray = new JSONArray();
		String condition = "";
		if (param.contains("-")) {
			condition = "em_id=" + Integer.parseInt(param.replaceFirst("-", ""));
		} else
			condition = "em_code='" + param + "'";
		Employee employee = employeeDao.getEmployeeByConditon(condition);

		SqlRowList rs1 = baseDao.queryForRowSet("select * from AttentionPerson where   ap_emid=0 And ap_isuse=" + 1);
		List<Map<String, Object>> Maps = new ArrayList<Map<String, Object>>();
		while (rs1.next()) {
			Maps.add(rs1.getCurrentMap());
		}
		for (Map<String, Object> map : Maps) {
			JSONObject jt = new JSONObject();
			int days = Integer.parseInt(map.get("ap_days").toString());
			jt.put("ap_attentedemid", employee.getEm_id());
			jt.put("ap_attentedemname", employee.getEm_name());
			jt.put("ap_allcount", 0);
			jt.put("ap_handledcount", 0);
			jt.put("ap_untreatedcount", 0);
			jt.put("ap_percentdone", 0);
			jt.put("ap_status", "很差");
			jt.put("ap_color", "FF0000");
			jt.put("ap_name", map.get("ap_name"));
			String value = rs1.getString("ap_subcode");
			if (value.equalsIgnoreCase("WorkDaily")) {
			} else if (value.equalsIgnoreCase("Agenda")) {
			} else if (value.equalsIgnoreCase("WorkAttendance")) {
			} else if (value.equalsIgnoreCase("JProcess2!DealByMe")) {
			} else if (value.equalsIgnoreCase("JProcess!Deal")) {
			} else if (value.equalsIgnoreCase("ProjectPlan")) {
			} else if (value.equalsIgnoreCase("WorkRecord")) {
				SqlRowList rs2 = baseDao.queryForRowSet("select count(*) from workrecord where wr_recorderemid="
						+ employee.getEm_id() + " And wr_recorddate>to_date('" + getPreviousDate(days)
						+ "','yyyy-MM-dd') AND wr_recorddate<to_date('" + dateformat(new Date()) + "','yyyy-MM-dd')");
				while (rs2.next()) {
					jt.put("ap_allcount", rs2.getInt(1));
					jt.put("ap_handledcount", rs2.getInt(1));
					jt.put("ap_untreatedcount", 0);
				}
			} else if (value.equalsIgnoreCase("newSynergy")) {
			} else if (value.equalsIgnoreCase("ProjectFeePlease")) {

			} else if (value.equalsIgnoreCase("ProjectFeeClaim")) {
			} else if (value.equalsIgnoreCase("Meeting")) {
			}
			dataarray.add(jt);
		}
		return dataarray;
	}
}
