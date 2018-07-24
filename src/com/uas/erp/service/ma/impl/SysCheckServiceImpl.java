package com.uas.erp.service.ma.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.OracleLobHandler;
import org.springframework.jdbc.support.nativejdbc.CommonsDbcpNativeJdbcExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.dao.common.HrOrgStrDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.HROrg;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.ma.SysCheckService;

@Service
public class SysCheckServiceImpl implements SysCheckService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private HrOrgStrDao hrOrgStrDao;

	@Override
	public void saveSysCheckFormula(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object sf_id = store.get("sf_id");
		// Object caller = store.get("sf_caller");
		/*
		 * boolean bool = baseDao.checkByCondition("SysCheckFormula",
		 * "sf_caller='" + caller + "'"); if (!bool) {
		 * BaseUtil.showError("该单据已设置相应的规则!"); }
		 */
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "SysCheckFormula").replaceAll("@@", "'"));
		baseDao.logger.save("SysCheckFormula", "sf_id", sf_id);
	}

	@Override
	public void deleteSysCheckFormula(int id) {
		baseDao.deleteByCondition("SysCheckFormula", "sf_id=" + id);
		baseDao.logger.delete("SysCheckFormula", "sf_id", id);
	}

	@Override
	public void updateSysCheckFormula(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object sf_id = store.get("sf_id");
		String updateSql = SqlUtil.getUpdateSqlByFormStore(store, "SysCheckFormula", "sf_id");
		baseDao.execute(updateSql.replaceAll("@@", "'"));
		baseDao.logger.update("SysCheckFormula", "sf_id", sf_id);
	}

	@Override
	public List<JSONTree> getAllHrTree() {
		JSONTree jt = new JSONTree();
		jt.setId(-1);
		jt.setText("所有员工");
		jt.setParentId(0);
		jt.setLeaf(false);
		jt.setExpanded(true);
		jt.setCls("x-tree-cls-root");
		jt.setChildren(get(SystemSession.getLang()));
		List<JSONTree> tree = new ArrayList<JSONTree>();
		tree.add(jt);
		return tree;
	}

	public List<JSONTree> get(String language) {
		List<HROrg> orgList = hrOrgStrDao.getAllHrOrgs(null);
		List<JSONTree> treeList = new ArrayList<JSONTree>();
		for (HROrg hrOrg : orgList) {
			JSONTree ct = new JSONTree(hrOrg, hrOrg.getOr_subof(), "org");

			List<Employee> emList = employeeDao.getEmployeesByOrId(hrOrg.getOr_id());
			List<JSONTree> children = new ArrayList<JSONTree>();
			if (emList.size() > 0) {
				// 说明组织下面跟的是人
				ct.setCls("employee");
			} else
				ct.setCls("org");
			treeList.add(ct);
			for (Employee employee : emList) {
				if (!employee.getEm_code().equals(ct.getQtip())) {
					JSONTree jt = new JSONTree(hrOrg.getOr_id());
					jt.setCls("");
					children.add(jt);
				}
			}
			ct.setChildren(children);
		}
		List<JSONTree> root = new ArrayList<JSONTree>();
		for (JSONTree ct : treeList) {
			if (Integer.parseInt(ct.getParentId().toString()) == 0) {
				root.add(ct);
			}
		}
		getTree(treeList, root);
		return root;
	}

	public void getTree(List<JSONTree> list, List<JSONTree> root) {
		for (JSONTree ct : root) {
			List<JSONTree> cts = findById(list, ct.getId());
			if (cts.size() != 0) {
				ct.getChildren().addAll(findById(list, ct.getId()));
				getTree(list, ct.getChildren());
			}
		}
	}

	public List<JSONTree> findById(List<JSONTree> list, Object id) {
		List<JSONTree> cts = new ArrayList<JSONTree>();
		for (JSONTree ct : list) {
			if (ct.getParentId().toString().equals(id.toString())) {
				cts.add(ct);
			}
		}
		return cts;
	}

	/*
	 * @Override public List<JSONTree> getAllHrTree(String language, Employee
	 * employee) { List<JSONTree> tree = new ArrayList<JSONTree>();
	 * List<Employee> employees = employeeDao.getEmployees(SpObserver.getSp());
	 * JSONTree jt = new JSONTree(); jt.setId(-1); jt.setText("所有员工");
	 * jt.setParentId(0); jt.setLeaf(false); jt.setExpanded(true);
	 * jt.setCls("x-tree-cls-root"); List<JSONTree> enTree = new
	 * ArrayList<JSONTree>(); List<HROrg> orgList = hrOrgStrDao.getAllHrOrgs();
	 * JSONTree ojt; List<JSONTree> orgTree; for(HROrg org:orgList){ ojt = new
	 * JSONTree(org, -1); ojt.setCls(""); orgTree = new
	 * ArrayList<JSONTree>(); for(Employee e:employees){
	 * if(e.getEm_defaultorid() == org.getOr_id()){ JSONTree tr=new JSONTree(e,
	 * language, ojt.getId()); tr.setCls(""); orgTree.add(tr); } }
	 * ojt.setChildren(orgTree); enTree.add(ojt); } jt.setChildren(enTree);
	 * tree.add(jt); return tree; }
	 */

	@Override
	public String getDataByOrg(int parentid, String type) {
		List<Map<String, Object>> map = new ArrayList<Map<String, Object>>();
		Map<String, Object> modelmap = null;
		int hrid = 0;
		if (!"employee".equals(type)) {
			String sql = "select distinct or_id,or_name,or_headmanname from hrorg where or_parentid=" + parentid;

			String hrname = null;
			String details = "";
			// 说明的所有组织
			SqlRowList sl = baseDao.queryForRowSet(sql);
			while (sl.next()) {
				hrid = sl.getInt("or_id");
				hrname = sl.getString("or_name");
				String selectSql = "select a.c_1,a.s_1,b.c_2 from (select count(1) as c_1,sum(scd_punishamount) as s_1 from syscheckdata where scd_method=-1 and scd_orid ="
						+ hrid + ") a,(select count(1) as c_2 from syscheckdata where scd_method=0 and scd_orid= " + hrid + ") b ";
				SqlRowList sl2 = baseDao.queryForRowSet(selectSql);
				modelmap = new HashMap<String, Object>();
				if (sl2.next()) {
					modelmap.clear();
					modelmap.put("orgid", hrid);
					modelmap.put("orgname", hrname);
					modelmap.put("orgheader", sl.getObject("or_headmanname"));
					modelmap.put("warncount", sl2.getInt("c_2"));
					modelmap.put("publishcount", sl2.getInt("c_1"));
					if (sl2.getInt("s_1") == -1) {
						modelmap.put("publishamountcount", 0);
					} else
						modelmap.put("publishamountcount", sl2.getInt("s_1"));

				}
				SqlRowList sl3 = baseDao.queryForRowSet("select scd_title,count(*) as cou from syscheckdata  where syscheckdata.scd_orid="
						+ hrid + " group by scd_title");
				details = "";
				while (sl3.next()) {
					details += sl3.getString(1) + "(" + sl3.getInt(2) + "),";
				}
				if (details.equals("")) {
					modelmap.put("details", "");
				} else
					modelmap.put("details", details.substring(0, details.lastIndexOf(",")));
				map.add(modelmap);
			}

		} else {
			String findSql = "select scd_emid as orgid,scd_emname as orgname,sum(scd_punishamount) as publishamountcount,count(case when scd_method =0 then 1 else null end) as warncount,count(case when scd_method =-1 then 1 else null end) as publishcount from syscheckdata where scd_orid="
					+ parentid + " group by scd_emid,scd_emname";
			SqlRowList emsl = baseDao.queryForRowSet(findSql);
			while (emsl.next()) {
				modelmap = new HashMap<String, Object>();
				modelmap.put("orgid", emsl.getObject("orgid"));
				modelmap.put("orgname", emsl.getString("orgname"));
				modelmap.put("details", "autoshow");
				modelmap.put("warncount", emsl.getInt("warncount"));
				modelmap.put("publishcount", emsl.getInt("publishcount"));
				if (emsl.getInt("publishamountcount") == -1) {
					modelmap.put("publishamountcount", 0);
				} else
					modelmap.put("publishamountcount", emsl.getInt("publishamountcount"));
				map.add(modelmap);
			}
		}
		return BaseUtil.parseGridStore2Str(map);
	}

	@SuppressWarnings({ "rawtypes", "null" })
	@Override
	public void TurnReandpunish(String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> map = null;
		List<String> sqls = new ArrayList<String>();
		String idstore = "";
		final OracleLobHandler lobHandler = new OracleLobHandler();
		CommonsDbcpNativeJdbcExtractor extractor = new CommonsDbcpNativeJdbcExtractor();
		lobHandler.setNativeJdbcExtractor(extractor);
		final StringBuffer sb = new StringBuffer();
		Employee employee = SystemSession.getUser();
		Map<Object, List<Map<Object, Object>>> groupmap = BaseUtil.groupMap(maps, "scd_title");
		/*
		 * for(int i=0;i<maps.size();i++){ map=maps.get(i); //插入到 惩罚表 sqls.add(
		 * "insert into Reandpunish (rp_id,rp_status,rp_title,rp_getter,rp_type,rp_class,rp_date,rp_content,rp_recordor,rp_statuscode,rp_recordorid,rp_amount) values("
		 * + baseDao.getSeqId("REANDPUNISH_SEQ")+",'"+BaseUtil.getLocalMessage(
		 * "AUDITED",
		 * language)+"','"+map.get("scd_title")+"-"+map.get("scd_sourcecode"
		 * )+"','"
		 * +map.get("scd_emname")+"','惩罚','',"+DateUtil.parseDateToOracleString
		 * (Constant.YMD, new
		 * Date())+",'系统生成','"+employee.getEm_name()+"','AUDITED',"
		 * +employee.getEm_id()+",'"+ map.get("scd_punishamount")+"')");
		 * idstore+="'"+map.get("scd_id")+"',"; //同时生成到 通知单 缺省生成一张 通知单
		 * 
		 * }
		 */
		sb.append("<ul style='list-style-type:none;margin:0;padding:0;margin-left:32px;width:680px;overflow:hidden;'>");
		int index = 0;
		for (Iterator it = groupmap.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			List<Map<Object, Object>> groups = groupmap.get(key);
			if (groups.size() > 0) {
				String keyField = groups.get(0).get("scd_keyfield").toString();
				String mainField = groups.get(0).get("scd_mainfield") != null ? groups.get(0).get("scd_mainfield").toString() : null;
				String url = groups.get(0).get("scd_url") != null ? groups.get(0).get("scd_url").toString() : null;
				sb.append("</br><li style='font-size:16px; font-weight:bold;font-color:black;width:680px;float:left;display:inline;background-color:#CDC8B1;'>"
						+ key + " &nbsp;&nbsp;&nbsp;&nbsp; 共" + groups.size() + "项 </li>");
				for (int i = 0; i < groups.size(); i++) {
					map = groups.get(i);
					sqls.add("insert into Reandpunish (rp_id,rp_status,rp_title,rp_getter,rp_type,rp_class,rp_date,rp_content,rp_recordor,rp_statuscode,rp_recordorid,rp_amount) values("
							+ baseDao.getSeqId("REANDPUNISH_SEQ")
							+ ",'"
							+ BaseUtil.getLocalMessage("AUDITED")
							+ "','"
							+ map.get("scd_title")
							+ "-"
							+ map.get("scd_sourcecode")
							+ "','"
							+ map.get("scd_emname")
							+ "','惩罚','',"
							+ DateUtil.parseDateToOracleString(Constant.YMD, new Date())
							+ ",'系统生成','"
							+ employee.getEm_name()
							+ "','AUDITED'," + employee.getEm_id() + ",'" + map.get("scd_punishamount") + "')");
					idstore += "'" + map.get("scd_id") + "',";
					if (url != null) {
						sb.append("<li style='height:24px;text-align: left;line-height:24px;width:340px;background-color:#E0EEE0;float:left;display:inline;'>");
						sb.append(getHref(url, keyField, mainField, Integer.parseInt(map.get("scd_sourceid").toString())) + ""
								+ map.get("scd_title") + "-" + map.get("scd_sourcecode") + "&nbsp;&nbsp;" + map.get("scd_emname")
								+ "</a></li>");
					} else
						sb.append(map.get("scd_title") + "-" + map.get("scd_sourcecode") + "&nbsp;&nbsp;" + map.get("scd_emname"));
					index++;
				}
			}

		}
		sb.append("</ul>");
		final String noteSql = "insert into  note (no_id,no_title,no_approver,no_apptime,no_emergency,no_content) values ("
				+ baseDao.getSeqId("NOTE_SEQ") + ",'系统处罚通知 &nbsp;&nbsp;&nbsp;" + DateUtil.parseDateToString(new Date(), Constant.YMD)
				+ "','" + employee.getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD, new Date()) + ",'一般',?)";
		baseDao.getJdbcTemplate().execute(noteSql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
			@Override
			protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException, DataAccessException {
				lobHandler.getLobCreator().setClobAsString(ps, 1, sb.toString());// String转化成Clob
			}
		});
		String updateSql = "update syscheckdata set scd_ispunished=-1 where scd_id in (" + idstore.substring(0, idstore.lastIndexOf(","))
				+ ")";
		sqls.add(updateSql);
		baseDao.execute(sqls);
	}

	@Override
	public void RunCheck() {
		String str = baseDao.callProcedure("SYS_DoCheck", new Object[] { "" });
		if (str != null && !"".equals(str)) {
			BaseUtil.showError(str);
		}
	}

	@Override
	public String getTreeData(String condition) {
		return BaseUtil.parseGridStore2Str(Toget(SystemSession.getLang(), condition));
	}

	public List<Map<String, Object>> Toget(String language, String condition) {
		List<HROrg> orgList = hrOrgStrDao.getAllHrOrgs(null);
		List<Map<String, Object>> map = new ArrayList<Map<String, Object>>();
		Map<String, Object> modelmap = null;
		for (HROrg hrOrg : orgList) {
			String selectSql = "select a.c_1,a.s_1,b.c_2 from (select count(1) as c_1,sum(scd_punishamount) as s_1 from syscheckdata where scd_method=-1 and scd_orid ="
					+ hrOrg.getOr_id()
					+ ") a,(select count(1) as c_2 from syscheckdata where scd_method=0 and scd_orid= "
					+ hrOrg.getOr_id() + ") b ";
			SqlRowList sl2 = baseDao.queryForRowSet(selectSql);
			modelmap = new HashMap<String, Object>();
			if (sl2.next()) {
				modelmap.clear();
				modelmap.put("orgid", hrOrg.getOr_id());
				modelmap.put("orgname", hrOrg.getOr_name());
				modelmap.put("orgheader", hrOrg.getOr_headmanname());
				modelmap.put("warncount", sl2.getInt("c_2"));
				modelmap.put("publishcount", sl2.getInt("c_1"));
				modelmap.put("leaf", false);
				/* modelmap.put("cls", "x-tree-cls-node"); */
				modelmap.put("parentid", hrOrg.getOr_subof());
				if (sl2.getInt("s_1") == -1) {
					modelmap.put("publishamountcount", 0);
				} else
					modelmap.put("publishamountcount", sl2.getInt("s_1"));

			}

			String findSql = "select scd_emid as orgid,scd_emname as orgname,sum (case when scd_method =-1 then scd_punishamount else 0 end)  as publishamountcount,count(case when scd_method =0 then 1 else null end) as warncount,count(case when scd_method =-1 then 1 else null end) as publishcount from syscheckdata where scd_orid="
					+ hrOrg.getOr_id() + " group by scd_emid,scd_emname";
			SqlRowList emsl = baseDao.queryForRowSet(findSql);
			List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
			while (emsl.next()) {
				Map<String, Object> smallermap = new HashMap<String, Object>();
				smallermap.put("orgid", emsl.getObject("orgid"));
				smallermap.put("orgname", emsl.getString("orgname"));
				smallermap.put("details", "autoshow");
				smallermap.put("warncount", emsl.getInt("warncount"));
				smallermap.put("publishcount", emsl.getInt("publishcount"));
				smallermap.put("leaf", true);
				/* smallermap.put("cls", "x-tree-cls-root"); */
				smallermap.put("parentid", hrOrg.getOr_id());
				if (emsl.getInt("publishamountcount") == -1) {
					smallermap.put("publishamountcount", 0);
				} else
					smallermap.put("publishamountcount", emsl.getInt("publishamountcount"));
				lists.add(smallermap);

			}
			modelmap.put("children", lists);
			map.add(modelmap);

		}
		List<Map<String, Object>> root = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> ct : map) {
			if (Integer.parseInt(ct.get("parentid").toString()) == 0) {
				root.add(ct);
			}
		}
		setTree(map, root);
		return root;
	}

	@SuppressWarnings("unchecked")
	public void setTree(List<Map<String, Object>> list, List<Map<String, Object>> root) {
		for (Map<String, Object> ct : root) {
			List<Map<String, Object>> cts = findByORId(list, ct.get("orgid"));
			if (cts.size() != 0) {
				List<Map<String, Object>> childrens = findByORId(list, ct.get("orgid"));
				((List<Map<String, Object>>) ct.get("children")).addAll(childrens);
				int warncount = Integer.parseInt(ct.get("warncount").toString());
				int punishicount = Integer.parseInt(ct.get("publishcount").toString());
				int publishamountcount = Integer.parseInt(ct.get("publishamountcount").toString());
				for (int i = 0; i < childrens.size(); i++) {
					Map<String, Object> m = childrens.get(i);
					warncount += Integer.parseInt(m.get("warncount").toString());
					punishicount += Integer.parseInt(m.get("publishcount").toString());
					publishamountcount += Integer.parseInt(m.get("publishamountcount").toString());
				}
				ct.remove("warncount");
				ct.remove("publishcount");
				ct.remove("publishamountcount");
				ct.put("warncount", warncount);
				ct.put("publishcount", punishicount);
				ct.put("publishamountcount", publishamountcount);
				setTree(list, ((List<Map<String, Object>>) ct.get("children")));
			}
		}
	}

	public List<Map<String, Object>> findByORId(List<Map<String, Object>> list, Object id) {
		List<Map<String, Object>> cts = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> ct : list) {
			if (ct.get("parentid").toString().equals(id.toString())) {
				cts.add(ct);
			}
		}
		return cts;
	}

	public String getHref(String url, String keyfield, String mainField, int keyValue) {
		if (url != null) {
			if (url.indexOf('?') > 0) {
				if (keyfield != null) {
					// 主从记录都有
					url = url + "&formCondition=" + keyfield + "IS" + keyValue + "&gridCondition=" + mainField + "IS" + keyValue;
				} else
					url = url + "&formCondition=" + keyfield + "IS" + keyValue;
			} else {
				if (keyfield != null) {
					// 主从记录都有
					url = url + "?formCondition=" + keyfield + "IS" + keyValue + "&gridCondition=" + mainField + "IS" + keyValue;
				} else
					url = url + "?formCondition=" + keyfield + "IS" + keyValue;
			}
			return "<a style='text-decoration:none;' href=javascript:openUrl(" + "\"" + url + "\");>";
		} else
			return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void vastUpdateSysCheckFormula(String data) {
		List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(data, "SysCheckFormula", "sf_id");
		baseDao.execute(sqls);
	}
}
