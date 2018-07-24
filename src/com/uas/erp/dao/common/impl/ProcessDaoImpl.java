package com.uas.erp.dao.common.impl;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.OracleLobHandler;
import org.springframework.jdbc.support.nativejdbc.CommonsDbcpNativeJdbcExtractor;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.dao.common.JProcessSetDao;
import com.uas.erp.dao.common.ProcessDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JNode;
import com.uas.erp.model.JProCand;
import com.uas.erp.model.JProcess;
import com.uas.erp.model.JProcessDeploy;
import com.uas.erp.model.JProcessSet;
import com.uas.erp.model.JTask;
import com.uas.erp.model.Job;
import com.uas.erp.model.JprocessButton;
import com.uas.erp.model.JprocessCommunicate;
import com.uas.erp.model.Master;

@Repository("processDao")
public class ProcessDaoImpl extends BaseDao implements ProcessDao {
	@Autowired
	private JProcessSetDao processSetDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private BaseDao baseDao;

	private Logger logger = Logger.getLogger(ProcessDaoImpl.class.getName());

	@Override
	public List<Map<String, Object>> getAllAsignees() {
		final String sql = "select em_id,em_code,em_name from employee ";
		return getJdbcTemplate().queryForList(sql);
	}

	@Override
	public boolean alterFormState(String table, String keyName, String formStatus, int id, String statuscode, String caller) {
		boolean feedback = true;
		if (table != null && table.toUpperCase().contains("LEFT")) {
			table = table.toUpperCase().split("LEFT")[0];
		}
		try {
			String sql = "";
			Employee employee = SystemSession.getUser();
			if (caller.equals("Make") || caller.equals("Make!Base")) {
				if (!statuscode.equals("COMMITED")) {
					sql = "UPDATE " + table + " SET ma_checkstatus='已批准' , ma_checkstatuscode='APPROVE' , ma_checkman='"
							+ employee.getEm_name() + "',ma_checkdate=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())
							+ " where ma_id=" + id;
				}
			} else if (caller.equals("Sale!TurnFormal")) {
				if (!statuscode.equals("COMMITED")) {
					sql = "update sale set sa_source='正常',sa_relativecode='非正常转',sa_commitstatus='已转正常' where sa_id=" + id;
				}
			} else if (caller.equals("PreProduct")) {
				if (!statuscode.equals("COMMITED")) {
					sql = "update PreProduct set pre_statuscode='TURNFM',pre_status='" + BaseUtil.getLocalMessage("TURNFM")
							+ "' where pre_id=" + id;
				}
			} else
				sql = "UPDATE " + table + " SET　" + formStatus + " ='" + BaseUtil.getLocalMessage(statuscode) + "'," + formStatus
						+ "code='" + statuscode + "' WHERE " + keyName + " = " + id;
			if (!sql.equals(""))
				execute(sql);

		} catch (DataAccessException e) {
			feedback = false;
		}
		return feedback;
	}

	@Override
	public Map<String, Object> getCallerInfoByProcessInstanceId(String processInstanceId) {
		final String sql = "SELECT distinct JP_KEYVALUE,JP_TABLE,JP_KEYNAME,JP_FORMSTATUS,JP_CALLER FROM JPROCESS WHERE JP_PROCESSINSTANCEID = ? and rownum=1";
		Map<String, Object> map = getJdbcTemplate().queryForMap(sql, new Object[] { processInstanceId },
				new int[] { java.sql.Types.VARCHAR });
		return map;
	}

	@Override
	public int getIdForCallerByProcessInstanceId(String processInstanceId) {
		final String sql = "SELECT distinct JP_KEYVALUE FROM JPROCESS WHERE JP_PROCESSINSTANCEID = ? ";
		Integer id = getJdbcTemplate().queryForObject(sql, new Object[] { processInstanceId }, new int[] { java.sql.Types.VARCHAR },
				Integer.class);
		return id;
	}

	@Override
	public void saveJProcess(String sql) {
		getJdbcTemplate().execute(sql);

	}

	@Override
	public int getIdBySeq(String seq) {
		return getSeqId(seq);
	}

	@Override
	public void updateJProcess(String sql, Object[] args, int[] argTypes) {
		getJdbcTemplate().update(sql, args, argTypes);

	}

	@Override
	public void alterJProcessState(String taskId, String processInstanceId, String status) {
		final String sql = "UPDATE JProcess SET JP_STATUS = '" + status + "' where JP_PROCESSINSTANCEID ='" + processInstanceId
				+ "' and jp_nodeId='" + taskId + "'";
		execute(sql);
	}

	@Override
	public List<JNode> getAllHistoryNode(String processInstanceId, String condition) {
		String findcondition = condition != null ? "jn_processInstanceId='" + processInstanceId + "' and " + condition
				: "jn_processInstanceId='" + processInstanceId + "'";
		final String sql = "select * from JNode where " + findcondition + " order by jn_id ";
		return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<JNode>(JNode.class));

	}

	@Override
	public List<JNode> getAllHistoryNodesByNodeId(String nodeId, String condition) {
		String findcondition = condition != null ? condition : "1=1";
		final String sql = "select * from JNode where "
				+ findcondition
				+ " and jn_processInstanceId in (select distinct jp_processInstanceId from jprocess where (jp_codevalue,jp_keyvalue,jp_caller )in (select jp_codevalue,jp_keyvalue,jp_caller from jprocess where jp_nodeid="
				+ nodeId + ")) order by jn_id ";
		return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<JNode>(JNode.class));
	}

	@Override
	public String getProcessInstanceId(String nodeId) {
		final String sql = "select jp_processInstanceId from JProcess where jp_nodeId = ?";
		String p = getJdbcTemplate().queryForObject(sql, new Object[] { nodeId }, new int[] { java.sql.Types.VARCHAR }, String.class);
		return p;
	}

	@Override
	public void saveAsHistoryNode(String sql) {
		getJdbcTemplate().execute(sql);
	}

	@Override
	public JProcess getCurrentNode(String nodeId) {
		List<JProcess> list = null;
		// 出现部分串单的流程
		Object processinstanceId = getFieldDataByCondition("jbpm4_hist_task", "execution_", "dbid_=" + nodeId);
		if (processinstanceId != null) {
			String[] arr = processinstanceId.toString().split("\\.");
			processinstanceId = arr[0] + "." + arr[1];
			list = getJdbcTemplate().query("select * from JProcess where jp_nodeId = ? and jp_processinstanceId=?",
					new Object[] { nodeId, processinstanceId }, new BeanPropertyRowMapper<JProcess>(JProcess.class));
		} else
			list = getJdbcTemplate().query("select * from JProcess where jp_nodeId = ? ", new Object[] { nodeId },
					new BeanPropertyRowMapper<JProcess>(JProcess.class));
		if (list.size() > 0)
			return list.get(0);
		else
			return null;

	}

	@Override
	public JProcess getCurrentNode(String nodeName, String processInstanceId) {
		final String sql = "select * from JProcess where jp_nodename = ? and jp_processinstanceid=?";
		List<JProcess> list = getJdbcTemplate().query(sql, new Object[] { nodeName, processInstanceId },
				new BeanPropertyRowMapper<JProcess>(JProcess.class));
		if (list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	@Override
	public void savaJProcessDeploy(String sql) {
		getJdbcTemplate().execute(sql);

	}

	// @Override
	// public String getOrgAssignees(String condition) {
	// // condition 查询分成两段 #
	// String employeecondition = null;
	// String orcondition = null;
	// boolean bool = false;
	// if (condition != null) {
	// String[] arr = condition.split("#");
	// orcondition = arr[0];
	// employeecondition = arr[1];
	// }
	// String QueryOrgSql =
	// "select or_id,or_name from hrorg where nvl(or_statuscode,' ')<>'DISABLE'";
	// String QueryEmployeeSql =
	// "select * from employee where nvl(em_defaultorid,0)<>0 and nvl(em_class,' ')<>'离职'";
	// QueryOrgSql = orcondition == null ? QueryOrgSql : QueryOrgSql + " where "
	// + orcondition;
	// QueryEmployeeSql = employeecondition == null ? QueryEmployeeSql :
	// QueryEmployeeSql + " and "
	// + employeecondition;
	// List<Map<String, Object>> orgIdNames =
	// getJdbcTemplate().queryForList(QueryOrgSql);
	// List<Employee> emps = getJdbcTemplate().query(QueryEmployeeSql,
	// new BeanPropertyRowMapper<Employee>(Employee.class));
	// StringBuffer sb = new StringBuffer();
	// StringBuffer smallsb = new StringBuffer();
	// sb.append("[");
	// for (Map<String, Object> org : orgIdNames) {
	// bool = false;
	// smallsb.setLength(0);
	// smallsb.append("{id:\"" + org.get("OR_ID") + "\",text:\"" +
	// org.get("OR_NAME")
	// + "\",leaf:false, expanded:true,children:[");
	// for (Employee emp : emps) {
	// if (Integer.parseInt(org.get("OR_ID").toString()) ==
	// emp.getEm_defaultorid()) {
	// bool = true;
	// smallsb.append("{id:\"" + emp.getEm_id() + "\",text:\"" +
	// emp.getEm_name() + "(" + emp.getEm_code()
	// + ")" + "\",qtip:\"" + emp.getEm_code() + "\",leaf:true},");
	// }
	// }
	// smallsb.append("]},");
	// if (bool) {
	// sb.append(smallsb);
	// }
	// }
	// sb.setCharAt(sb.length() - 1, ']');
	// String str = sb.toString().replaceAll(",]", "]");
	// return str;
	// }

	@Override
	public String getOrgAssignees(String condition) {
		// condition 查询分成两段 #
		String employeecondition = null;
		String orcondition = null;
		boolean bool = false;
		if (condition != null) {
			String[] arr = condition.split("#");
			orcondition = arr[0];
			employeecondition = arr[1];
		}
		StringBuffer sb = new StringBuffer();
		StringBuffer smallsb = new StringBuffer();
		StringBuffer childsb = new StringBuffer();
		sb.append("[");

		String allCondition = "";
		if (orcondition != null & employeecondition != null) {
			allCondition = "and " + orcondition + "and " + employeecondition;
		}

		List<Object[]> datas = baseDao.getFieldsDatasByCondition("(hrorg left join employee on or_id=em_defaultorid)", new String[] {
				"or_id", "or_name", "em_id", "em_name", "em_code" },
				"nvl(or_statuscode,' ')<>'DISABLE' and nvl(em_defaultorid,0)<>0 and nvl(em_class,' ')<>'离职' " + allCondition
						+ " order by or_id");

		String orId = "";
		bool = false;
		if (datas != null) {
			for (Object[] obj : datas) {
				if (obj != null) {
					if (obj[0] != null) {
						// 排序好之后进行分组判断
						if (!orId.equals(obj[0].toString())) {
							// 子孩子添加完再结尾,然后再重置，注意最后一组数据要在循环结束后补全
							if (bool) {
								// smallsb.substring(0,smallsb.length()-1);
								smallsb.append("]},");
								sb.append(smallsb);
							}

							orId = obj[0].toString();
							smallsb.setLength(0);
							childsb.setLength(0);
							smallsb.append("{id:\"" + obj[0] + "\",text:\"" + obj[1] + "\",leaf:false, expanded:true,children:[");
							bool = true;
							childsb.append("{id:\"" + obj[2] + "\",text:\"" + obj[3] + "(" + obj[4] + ")" + "\",qtip:\"" + obj[4]
									+ "\",leaf:true},");
							smallsb.append(childsb);
						} else {
							childsb.setLength(0);
							childsb.append("{id:\"" + obj[2] + "\",text:\"" + obj[3] + "(" + obj[4] + ")" + "\",qtip:\"" + obj[4]
									+ "\",leaf:true},");
							smallsb.append(childsb);
						}
					}
				}
			}

			if(datas.size()>0){
				// 把最后一组数据加上，因为上一组的数据添加是因为和下一组数据不同，而最后一组数据之后没有数据对比
				smallsb.append("]},");
				sb.append(smallsb);				
			}
		}

		if(sb.length()>1){
			sb.setCharAt(sb.length() - 1, ']');
		}else{
			sb.append("]"); //查询结果为空的情况
		}
		String str = sb.toString().replaceAll(",]", "]");
		return str;
	}

	@Override
	public String getHrJob(String condition, Integer joborgnorelation) {
		String employeecondition = null;
		String orcondition = null;
		if (condition != null) {
			String[] arr = condition.split("#");
			orcondition = arr[0];
			employeecondition = arr[1];
		}

		String allCondition = "";
		if (orcondition != null & employeecondition != null) {
			allCondition = "and " + orcondition + "and " + employeecondition;
		}

		List<Job> jobs = getJdbcTemplate().query(
				"select * from Job where nvl(jo_statuscode,' ')<>'DISABLE'" + allCondition
						+ " order by NLSSORT(jo_name,'NLS_SORT = SCHINESE_PINYIN_M ') asc", new BeanPropertyRowMapper<Job>(Job.class));

		StringBuffer sb = new StringBuffer();
		sb.append("[");
		if (joborgnorelation != null && joborgnorelation == 1) {

			for (Job job : jobs) {
				sb.append("{id:\"" + job.getJo_id() + "\",text:\"" + job.getJo_name() + "(" + job.getJo_code() + ")" + "\",qtip:\""
						+ job.getJo_code() + "\",leaf:true},");// 这个加上岗位代号是临时加上去的,未提交。

			}
		} else {

			System.out.println(joborgnorelation);
			List<Map<String, Object>> orgIdNames = getJdbcTemplate().queryForList(
					"select distinct jo_orgId, jo_orgName from Job left join Hrorg on jo_orgid=or_id where nvl(or_statuscode,' ')<>'DISABLE' "
							+ allCondition
							+ "and nvl(jo_statuscode,' ')<>'DISABLE' order by NLSSORT(jo_orgName,'NLS_SORT = SCHINESE_PINYIN_M ') asc");
			for (Map<String, Object> orgIdName : orgIdNames) {
				sb.append("{id:\"" + orgIdName.get("jo_orgId") + "\",text:\"" + orgIdName.get("jo_orgName")
						+ "\",leaf:false,expanded:true,children:[");

				for (Job job : jobs) {
					if (Integer.parseInt(orgIdName.get("jo_orgId").toString()) - job.getJo_orgId() == 0) {
						sb.append("{id:\"" + job.getJo_id() + "\",text:\"" + job.getJo_name() + "(" + job.getJo_code() + ")" + "\",qtip:\""
								+ job.getJo_code() + "\",leaf:true},");// 这个加上岗位代号是临时加上去的,未提交。
					}
				}
				sb.append("]},");
			}
		}
		sb.setCharAt(sb.length() - 1, ']');
		String str = sb.toString().replaceAll(",]", "]");

		return str;

	}

	@Override
	public boolean exitsJProcessDeploy(String caller) {
		boolean result = false;
		final String sql = "select * from JProcessDeploy where jd_caller = ?";
		try {
			JProcessDeploy jd = getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<JProcessDeploy>(JProcessDeploy.class),
					caller);
			if (jd != null)
				result = true;
		} catch (DataAccessException e) {
		}
		return result;
	}

	@Override
	public void updateOrSaveJProcesDeploy(final String caller, final String processDefName, final String processDescription,
			final String processDefId, final String xml, final String enabled, final String ressubmit, final int parentId, String type) {
		final OracleLobHandler lobHandler = new OracleLobHandler();
		CommonsDbcpNativeJdbcExtractor extractor = new CommonsDbcpNativeJdbcExtractor();
		lobHandler.setNativeJdbcExtractor(extractor);
		Employee employee = SystemSession.getUser();
		if (exitsJProcessDeploy(caller)) {
			final String sql = "update JProcessDeploy set jd_processDefinitionId =?, jd_processDefinitionName =?, jd_processDescription =?,jd_xmlString = ?,jd_enabled = ?,jd_ressubmit=?,jd_updater='"
					+ employee.getEm_name()
					+ "',jd_updatetime="
					+ DateUtil.parseDateToOracleString(Constant.YMD, new Date())
					+ " where jd_caller =?";
			getJdbcTemplate().update(sql, new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, processDefId);
					ps.setString(2, processDefName);
					ps.setString(3, processDescription);
					lobHandler.getLobCreator().setClobAsString(ps, 4, xml);
					ps.setString(5, enabled);
					ps.setString(6, ressubmit);
					ps.setString(7, caller);

				}
			});

		} else {
			/*
			 * final String sql= "insert into JProcessDeploy (jd_id,jd_caller,jd_processDefinitionId,JD_PROCESSDEFINITIONNAME,JD_PROCESSDESCRIPTION,JD_XMLSTRING,jd_enabled,jd_ressubmit,jd_selfId,jd_isLeaf,jd_parentId) values" + "('"+jd_id+"','"+caller+"','"+processDefId+"','"+processDefName+ "','" +processDescription+"','"+xml+"','"+enabled+"','"+jd_id+"',1,'" +parentId+"')";
			 */
			/* savaJProcessDeploy(sql); */
			final int jd_id = getIdBySeq("JProcessDeploy_SEQ");
			String sql = "insert into JProcessDeploy (jd_id,jd_caller,jd_processDefinitionId,JD_PROCESSDEFINITIONNAME,JD_PROCESSDESCRIPTION,JD_XMLSTRING,jd_enabled,jd_ressubmit,jd_selfId,jd_isLeaf,jd_parentId) values"
					+ "(?,?,?,?,?,?,?,?,?,1,?)";
			if (type != null && !type.equals(""))
				sql = "insert into JProcessDeploy (jd_id,jd_caller,jd_processDefinitionId,JD_PROCESSDEFINITIONNAME,JD_PROCESSDESCRIPTION,JD_XMLSTRING,jd_enabled,jd_ressubmit,jd_selfId,jd_isLeaf,jd_parentId,jd_type) values"
						+ "(?,?,?,?,?,?,?,?,?,1,?,'" + type + "')";
			getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
				@Override
				protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException, DataAccessException {
					ps.setInt(1, jd_id);
					ps.setString(2, caller);
					ps.setString(3, processDefId);
					ps.setString(4, processDefName);
					ps.setString(5, processDescription);
					lobHandler.getLobCreator().setClobAsString(ps, 6, xml);// String转化成Clob
					ps.setString(7, enabled);
					ps.setString(8, ressubmit);
					ps.setInt(9, 0);
					ps.setInt(10, parentId);
				}
			});

		}

	}
	
	@Override
	public String updateOrSaveFlowChart(final String chartId,final String caller,final String shortName,final String name,final String remark,final String xmll){
		final OracleLobHandler lobHandler = new OracleLobHandler();
		CommonsDbcpNativeJdbcExtractor extractor = new CommonsDbcpNativeJdbcExtractor();
		lobHandler.setNativeJdbcExtractor(extractor);
		Employee employee = SystemSession.getUser();
		boolean Exits = baseDao.checkIf("flow_chart", "fc_caller = '"+caller+"' and fc_shortname = '"+shortName+"'");
		if (Exits) {
			final String sql = "update flow_chart set fc_remark = ?,fc_xmlstring=?,fc_updater='"
					+ employee.getEm_name()
					+ "',fc_updatetime="
					+ DateUtil.parseDateToOracleString(Constant.YMD, new Date())
					+ ",fc_updatecode='"
					+ employee.getEm_code()
					+ "' where fc_caller =? and fc_shortname = ?";
			getJdbcTemplate().update(sql, new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, remark);
					lobHandler.getLobCreator().setClobAsString(ps, 2, xmll);
					ps.setString(3, caller);
					ps.setString(4, shortName);	
				}
			});
			return "null";
		} else {
			
			String sql = "insert into flow_chart (fc_id,fc_caller,fc_shortname,fc_name,fc_remark,fc_XMLSTRING) values"
					+ "(?,?,?,?,?,?)";
			getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
				@Override
				protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException, DataAccessException {
					ps.setString(1, chartId);
					ps.setString(2, caller);
					ps.setString(3, shortName);
					ps.setString(4, name);
					ps.setString(5, remark);
					lobHandler.getLobCreator().setClobAsString(ps, 6, xmll);// String转化成Clob
				}
			});
			return chartId;
		}

	}

	@Override
	public String getProcessDefIdByCaller(String caller) {
		final String sql = "select jd_processdefinitionid from JProcessDeploy where jd_caller = '" + caller + "' and jd_enabled='是'";
		SqlRowList sl = queryForRowSet(sql);
		if (sl.next()) {
			return sl.getString(1);
		} else {
			BaseUtil.showError("AFTERSUCCESSCaller:【" + caller + "】未定义流程，自动初始化审批流失败!请先定义该单据流程.");
			return null;
		}
	}

	@Override
	public void saveJProcessDeploy(final String xml, final String caller, final String processDefinitionName,
			final String processDescription) {
		/*
		 * int jd_id =getIdBySeq("JProcessDeploy_SEQ"); final String sql= "insert into JProcessDeploy (jd_id,jd_caller,,JD_PROCESSDEFINITIONNAME,JD_PROCESSDESCRIPTION,JD_XMLSTRING) values('" + jd_id+"','"+caller+"','"+processDefinitionName+"','"+processDescription +"','"+xml+"')"; getJdbcTemplate().execute(sql);
		 */

		try {
			final OracleLobHandler lobHandler = new OracleLobHandler();
			CommonsDbcpNativeJdbcExtractor extractor = new CommonsDbcpNativeJdbcExtractor();
			lobHandler.setNativeJdbcExtractor(extractor);
			final int jd_id = getIdBySeq("JProcessDeploy_SEQ");
			String sql = "insert into JProcessDeploy (JD_ID,JD_CALLER,JD_PROCESSDEFINITIONNAME,JD_PROCESSDESCRIPTION,JD_XMLSTRING) values(?,?,?,?,?)";

			getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
				@Override
				protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException, DataAccessException {
					ps.setInt(1, jd_id);
					ps.setString(2, caller);
					ps.setString(3, processDefinitionName);
					ps.setString(4, processDescription);
					lob.setClobAsString(ps, 5, xml);// String转化成Clob
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public JProcessDeploy getJProcessDeployById(String jdId) {
		final String sql = "select * from JProcessDeploy where jd_id = ?";
		return getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<JProcessDeploy>(JProcessDeploy.class), jdId);
	}

	@Override
	public Map<String, String> getXmlInfoByJdId(String jdId, String type, String caller) {
		String sql = "select jd_xmlString,jd_caller,jd_processDefinitionName, jd_processDescription,jd_processDefinitionId,jd_enabled,jd_ressubmit  from JProcessDeploy where jd_id = ?";
		if (caller != null) {
			sql = "select jd_xmlString,jd_caller,jd_processDefinitionName, jd_processDescription,jd_processDefinitionId,jd_enabled,jd_ressubmit  from JProcessDeploy where jd_caller = ?";
		}
/*		String isGroup = BaseUtil.getXmlSetting("group");
		if ("true".equals(isGroup) && type != null && type.equals("sysnavigation")) {
			String dataCenter = BaseUtil.getXmlSetting("dataSob");
			sql = "select jd_xmlString,jd_caller,jd_processDefinitionName, jd_processDescription,jd_processDefinitionId,jd_enabled,jd_ressubmit  from "
					+ dataCenter + ".JProcessDeploy where jd_id = ?";
		}*/
		String org = caller != null ? caller : jdId;
		Map<String, Object> map = getJdbcTemplate().queryForMap(sql, new Object[] { org }, new int[] { java.sql.Types.VARCHAR });
		Map<String, String> mapInfo = new HashMap<String, String>();
		mapInfo.put("xmlInfo", (String) map.get("JD_XMLSTRING"));
		mapInfo.put("caller", (String) map.get("JD_CALLER"));
		mapInfo.put("enabled", (String) map.get("jd_enabled"));
		mapInfo.put("ressubmit", (String) map.get("jd_ressubmit"));
		mapInfo.put("processDefName", (String) map.get("JD_PROCESSDEFINITIONNAME"));
		mapInfo.put("processDescription", (String) map.get("JD_PROCESSDESCRIPTION"));
		mapInfo.put("processDefId", (String) map.get("JD_PROCESSDEFINITIONID"));
		return mapInfo;
	}

	@Override
	public JProcessDeploy getJProcessDeployByCaller(String caller) {
		final String sql = "select * from JProcessDeploy where jd_caller = ?";
		JProcessDeploy jd = null;
		try {
			jd = getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<JProcessDeploy>(JProcessDeploy.class), caller);
		} catch (DataAccessException e) {

			return null;
			/* e.printStackTrace(); */
		}
		return jd;
	}

	@Override
	public JProcess getJProcess(String jp_form) {
		final String sql = "select * from JProcess where jp_form = ? and jp_candidate is null";
		return getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<JProcess>(JProcess.class), jp_form);
	}

	@Override
	public boolean exitsJProCand(String jp_candidate, String jp_nodeId) {
		final String sql = "select * from JProCand where jp_candidate = ? and jp_nodeId = ?";
		try {
			getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<JProcess>(JProcess.class),
					new Object[] { jp_candidate, jp_nodeId });
			return true;
		} catch (DataAccessException e) {
			/* e.printStackTrace(); */// 有两条记录也会发生异常，不过按流程走应该不会有两条记录。
			return false;
		}

	}

	@Override
	public void deleteJProcess(String jp_nodeId) throws Exception {
		/*
		 * final String sql = "delete JProcess where jp_nodeId='"+jp_nodeId+ "'and jp_candidate is not null ";
		 */
		final String sql = "delete JProcess where jp_nodeId='" + jp_nodeId + "'and jp_nodeDealMan is null  ";
		getJdbcTemplate().execute(sql);

	}

	@Override
	public List<JTask> getJTaskByProcessDefId(String processDefId) {
		final String sql = "select * from JTask where jt_processDefId = ?";

		return getJdbcTemplate().query(sql, new Object[] { processDefId }, new BeanPropertyRowMapper<JTask>(JTask.class));
	}

	@Override
	public List<String> getLeaderOfEmployee(String em_code) {
		// 获得某个人的领导;;;;
		// 以下是测试用的虚拟数据！
		List<String> codes = new ArrayList<String>();
		codes.add("A017");
		codes.add("A018");
		return codes;
	}

	@Override
	public List<String> getEmployeesInSameOrgWithGivenEmployee(String em_code) {
		// 获得某与某人相同组织的人！
		// 以下是测试用的虚拟数据！
		List<String> codes = new LinkedList<String>();
		codes.add("A020");
		codes.add("A022");
		codes.add("A024");
		return codes;
	}

	@Override
	public JProCand getJProCand(String candidate, String nodeId) {
		// final String sql =
		// "select * from JProCand where jP_candidate = ? and jp_nodeId = ?";
		final String sql = "select * from JProCand where jp_nodeId = ?";
		List<JProCand> list = getJdbcTemplate().query(sql, new Object[] { nodeId }, new BeanPropertyRowMapper<JProCand>(JProCand.class));
		return list.get(0);
	}

	@Override
	public void saveJProcessFromJProCand(JProCand jc, String em_code, Master master) {
		boolean bool = checkIf("Jprocess", "jp_nodeid='" + jc.getJp_nodeId() + "'");
		if (bool) {
			BaseUtil.showError("记录已存在 不要重复操作!");
		}
		int jp_id = getIdBySeq("Process_SEQ");
		List<String> sqls = new ArrayList<String>();
		List<String> list = new ArrayList<String>();
		String jp_reminddate = DateUtil.parseDateToOracleString(Constant.YMD_HMS, DateUtil.addHours(new Date(), 8));
		StringBuffer sb = new StringBuffer();
		Object duedate = getFieldDataByCondition("JTask", "jt_duedate", "jt_processdefid='" + jc.getJp_processdefid() + "' AND Jt_name='"
				+ jc.getJp_nodeName() + "'");
		if (duedate != null) {
			if (duedate.equals(BigDecimal.ZERO))
				duedate = 8;
			list = callProcedureWithOut(
					"SP_PROCESSREMIND",
					new Object[] { em_code, DateUtil.parseDateToString(new Date(), Constant.YMD_HM).substring(0, 10),
							DateUtil.parseDateToString(new Date(), Constant.YMD_HM).substring(11), Integer.valueOf(duedate.toString()) * 60 },
					new Integer[] { 1, 2, 3, 4 }, new Integer[] { 5, 6 });
			if (list.size() != 0 && list.get(1) != null) {
				jp_reminddate = DateUtil.parseDateToOracleString(Constant.YMD_HMS, list.get(1));
			}
		}

		Object jdid = getFieldDataByCondition("JprocessDeploy", "jd_id", "jd_caller='" + jc.getJp_caller() + "'");
		final String sql = "INSERT INTO　JProcess(jp_id,jp_name,jp_launcherId,jp_launcherName,jp_form,jp_launchTime,"
				+ "jp_caller,jp_table,jp_keyValue,jp_processInstanceId,jp_nodeId,jp_nodeName,jp_nodeDealMan,jp_stayMinutes,jp_status,jp_keyName,jp_url,jp_formStatus,jp_flag,jp_formDetailKey,jp_codevalue,jp_pagingid,jp_jdid,jp_processdefid,jp_processnote,jp_realjobid,JP_REMINDDATE) "
				+ "values('"
				+ jp_id
				+ "','"
				+ jc.getJp_name()
				+ "','"
				+ jc.getJp_launcherId()
				+ "','"
				+ jc.getJp_launcherName()
				+ "','"
				+ jc.getJp_form()
				+ "',"
				+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, jc.getJp_launchTime())
				+ ",'"
				+ jc.getJp_caller()
				+ "','"
				+ jc.getJp_table()
				+ "','"
				+ jc.getJp_keyValue()
				+ "','"
				+ jc.getJp_processInstanceId()
				+ "','"
				+ jc.getJp_nodeId()
				+ "','"
				+ jc.getJp_nodeName()
				+ "','"
				+ em_code
				+ "','"
				+ jc.getJp_stayMinutes()
				+ "','"
				+ jc.getJp_status()
				+ "','"
				+ jc.getJp_keyName()
				+ "','"
				+ jc.getJp_url()
				+ "','"
				+ jc.getJp_formStatus()
				+ "','"
				+ jc.getJp_flag()
				+ "','"
				+ jc.getJp_formDetailKey()
				+ "','"
				+ jc.getJp_codevalue()
				+ "','"
				+ 0
				+ "',"
				+ jdid
				+ ",'"
				+ jc.getJp_processdefid() + "','" + jc.getJp_processnote() + "'," + jc.getJp_realjobid() + "," + jp_reminddate + ")";
		sqls.add(sql);
		execute(sqls);

	}

	@Override
	public List<JProCand> getJProCands(String jp_nodeId) {
		Object processinstanceId = getFieldDataByCondition("jbpm4_hist_task", "execution_", "dbid_=" + jp_nodeId);
		String sql = "";
		if (processinstanceId != null) {
			sql = "Select * from JProCand  where jp_nodeId = ? and instr('" + processinstanceId
					+ "',jp_processinstanceid)>0 order by jp_id desc";
		} else
			sql = "Select * from JProCand  where jp_nodeId = ? order by jp_id desc";
		return getJdbcTemplate().query(sql, new Object[] { jp_nodeId }, new BeanPropertyRowMapper<JProCand>(JProCand.class));
	}

	@Override
	public void updateFlagOfJProCands(JProCand jc) {
		String nodeId = jc.getJp_nodeId();
		execute("update jprocand set jp_flag=0 where jp_nodeId ='" + nodeId + "'");
	}

	@Override
	public Map<String, Object> getJProcessInfo(String processInstanceId, String taskId) {
		final String sql = "select distinct jp_caller ,jp_name,jp_keyValue ,jp_launcherId ,jp_launcherName,jp_processdefid from JProcess"
				+ " where jp_processInstanceId = ? and jp_nodeId = ?";
		Map<String, Object> map = getJdbcTemplate().queryForMap(sql, new Object[] { processInstanceId, taskId });
		Map<String, Object> processInfo = new HashMap<String, Object>();
		processInfo.put("caller", map.get("JP_CALLER"));
		processInfo.put("id", map.get("JP_KEYVALUE"));
		processInfo.put("code", map.get("JP_LAUNCHERID"));
		processInfo.put("name", map.get("JP_LAUNCHERNAME"));
		processInfo.put("jpName", map.get("JP_NAME"));
		processInfo.put("processDefId", map.get("jp_processdefid"));
		return processInfo;
	}

	@Override
	public void updateFlagOfJprocess(String processInstanceId, String taskId) {
		final String sql = "update JProcess set jp_flag = 0 ,jp_status='已审批' where jp_processInstanceId = ? and jp_nodeId = ?";
		getJdbcTemplate().update(sql, new Object[] { processInstanceId, taskId });

	}

	@Override
	public List<String> getEmployeesOfJob(String jo_code) {
		// hrjob 表中 最好 有 是 jo_code 与 em_ code 关联 , 而不是 em_id 相关联 ,方便查询 ,可以把
		// hrjob 表结构改一下 ，dbfind 也改一下
		// 此处用三表 连接查询 ,性能,何其劣矣 ！
		/*
		 * final String sql = " select em_code from employee join (select * from hrjob  join  job  on job.jo_name = hrjob.jo_name) g on g.jo_emid = employee.em_id where g.jo_code = '" +jo_code+"'";
		 */
		final String sql = "select em_code from employee  left join job on  em_defaulthsid=jo_id where jo_code='" + jo_code + "'";
		List<String> list = getJdbcTemplate().queryForList(sql, String.class);

		return list;
	}

	@Override
	public String getProcessInstnaceId(String nodeId) {
		final String sql = "select  jp_processInstanceId from JPRocess where jp_nodeId = ? and rownum=1";
		try {
			return getJdbcTemplate().queryForObject(sql, new Object[] { nodeId }, String.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public String getEmployeeNameByCode(String em_code) {
		final String sql = " select em_name from Employee where em_code = ?";
		return getJdbcTemplate().queryForObject(sql, new Object[] { em_code }, String.class);
	}

	@Override
	public Map<String, Object> getDecisionConditionData(Map<String, Object> processInfo, String... strings) {
		Map<String, Object> result = new HashMap<String, Object>();
		String caller = (String) processInfo.get("caller");
		int formId = Integer.parseInt(processInfo.get("id").toString());
		JProcessSet js = processSetDao.getCallerInfo(caller);
		String formKey = js.getJs_formKeyName();
		String formTable = js.getJs_table();
		String decisionVaribles = js.getJs_decisionVariables();
		String groupby = js.getJs_groupby();
		StringBuffer sql = new StringBuffer();
		String querySql = "";
		sql.append("select ");
		/*
		 * if (strings.length > 1) {
		 * 
		 * } else { sql.append(strings[0] + " from " + formTable + " where " + formKey + "= ?"); querySql = sql.toString(); querySql = groupby != null ? querySql + " " + groupby : querySql; SqlRowList sl=queryForRowSet(sql.toString(), new Object[] { formId }); if(sl.next()){ if (sl.getObject(1) != null && sl.getObject(1).toString().matches("\\d*")) { result.put(decisionVaribles, sl.getLong(1)); } else { result.put(decisionVaribles,sl.getObject(1)); } }
		 * 
		 * }
		 */
		for (String s : strings) {
			sql.append(s + ",");
		}
		sql.deleteCharAt(sql.length() - 1).append(" from " + formTable + " where " + formKey + "= ?");
		querySql = sql.toString();
		querySql = groupby != null ? querySql + " " + groupby : querySql;
		SqlRowList sl = queryForRowSet(querySql, new Object[] { formId });
		String[] variables = decisionVaribles.split("#");
		if (sl.next()) {
			for (int i = 1; i < strings.length + 1; i++) {
				if (sl.getObject(i) != null && sl.getObject(i).toString().matches("\\d*") && ((sl.getObject(i).toString().length()>1 && !sl.getObject(i).toString().startsWith("0"))||sl.getObject(i).toString().length()==1)) {
					result.put(variables[i - 1], sl.getLong(i));
				} else {
					result.put(variables[i - 1], sl.getObject(i));
				}
			}
		}
		return result;
	}

	@Override
	public List<String> getAssigneesOfHistoryTasks(String processInstanceId) {
		final String sql = "select assignee_ from jbpm4_hist_task where execution_ like '" + processInstanceId
				+ ".%' and end_ is not null order by end_  desc";
		return getJdbcTemplate().queryForList(sql, String.class);

	}

	@Override
	public void updateAssigneeOfJprocess(String taskId, String userId) {
		final String sql = "update JProcess Set jp_nodeDealMan = ?,jp_launchtime=sysdate  where jp_nodeId = ? and jp_flag = 1";
		getJdbcTemplate().update(sql, new Object[] { userId, taskId });

	}

	@Override
	public String getFlowCaller(String caller) {
		Object obj = getFieldDataByCondition("form", "fo_flowcaller", "fo_caller='" + caller + "'");
		if (obj != null) {
			return obj.toString();
		} else {
			return null;
		}

	}

	@Override
	public String getProcessDefIdByProcessInstanceId(String pInstanceId) {
		final String sql = "select  jd_processDefinitionId from jprocessdeploy left join Jprocess on jp_caller = jd_caller where jprocess.jp_processinstanceid = ?  and rownum=1";
		return getJdbcTemplate().queryForObject(sql, new Object[] { pInstanceId }, String.class);
	}

	@Override
	public List<JTask> getTaskDefByProcessDefId(String id) {
		final String sql = "select * from JTask where jt_processDefId = ?";
		return getJdbcTemplate().query(sql, new Object[] { id }, new BeanPropertyRowMapper<JTask>(JTask.class));
	}

	@Override
	public List<Map<String, Object>> getActorUsersOfPInstance(String processInstanceId) { // 尽量写成连接查询;
		final String sql = "select jp_nodeId,wm_concat(jp_candidate) as jp_candidates,jp_nodename from JProCand where jp_flag = 1 and jp_processInstanceId = ? group by jp_nodeId,jp_nodename";
		List<Map<String, Object>> lists = getJdbcTemplate().queryForList(sql, new Object[] { processInstanceId });
		if (lists.size() > 0) {
			for (Map<String, Object> list : lists) {
				List<String> users = new LinkedList<String>();
				if (list.get("JP_CANDIDATES") != null) {
					String[] jp_candidates = String.valueOf(list.get("JP_CANDIDATES")).split(",");
					for (String jp_candidate : jp_candidates) {
						final String sql2 = "select em_name from Employee where em_code = ?";
						String em_name = getJdbcTemplate().queryForObject(sql2, new Object[] { jp_candidate }, String.class);
						users.add(em_name + "(" + jp_candidate + ")");
					}
				}
				list.put("JP_CANDIDATES", users);
			}
		}
		return lists;
	}

	@Override
	public String getxmlStringFromBlob(String deploymentId) {
		String sql = "select BLOB_VALUE_ from jbpm4_lob where DEPLOYMENT_ = ? ";
		Blob xml = this.getJdbcTemplate().queryForObject(sql, new Object[] { deploymentId }, Blob.class);
		String str = null;
		try {
			str = new String(xml.getBytes(1, (int) xml.length()));

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return str;

	}

	// 假定停留分钟 指的是 流程发起至 现在的 停留分钟;
	@Override
	public void updateStayMinutesOfJProcessOrJProcand(String dealMan, String which) {
		String dealManName = null;
		if (which.equals("JPROCESS")) {
			dealManName = "jp_nodeDealMan";
		} else {
			dealManName = "jp_candidate";
		}
		final String sql = "select JP_LAUNCHTIME,jp_stayMinutes,jp_id from " + which + " where " + dealManName + " = ? and jp_flag =1 ";
		List<Map<String, Object>> results = getJdbcTemplate().queryForList(sql, new Object[] { dealMan });
		Date now = new Date();
		for (Map<String, Object> map : results) {

			Date launcherTime = (Date) map.get("JP_LAUNCHTIME");
			Object jp_id = map.get("jp_id");
			long diff = (now.getTime() - launcherTime.getTime()) / (1000 * 60);

			final String sql2 = "update " + which + " set jp_stayMinutes = '" + diff + "' where jp_id ='" + jp_id + "' ";
			getJdbcTemplate().execute(sql2);
		}

	}

	@Override
	public List<JProcessDeploy> getJProcessDeploys(int parentId) {
		final String sql = "select * from JprocessDeploy where jd_parentId = ? ";
		List<JProcessDeploy> list = null;
		try {
			list = getJdbcTemplate().query(sql, new Object[] { parentId }, new BeanPropertyRowMapper<JProcessDeploy>(JProcessDeploy.class));
			// 根据caller 去 流程设置表中 查找 对应表单url 本应该添加流程设置的同时去更新流程定义中表单url 2013-3-29
			// 18:00:01 wuw
			for (JProcessDeploy jd : list) {
				if (jd.getJd_formUrl() == null && jd.getJd_isLeaf() == 1) {
					String sql2 = "SELECT jd_caller,js_formurl FROM "
							+ "( select * from jprocessdeploy where jd_isleaf=1 and jd_formurl is null) a "
							+ "left join jprocessset on a.jd_caller = js_caller where jd_caller='" + jd.getJd_caller() + "'";
					List<Map<String, Object>> maps = getJdbcTemplate().queryForList(sql2);
					if (maps != null && maps.get(0).get("JS_FORMURL") != null) {
						jd.setJd_formUrl(maps.get(0).get("JS_FORMURL").toString());
					}
				}
			}
		} catch (DataAccessException e) {
			logger.info("查询 JProcessDeploy 异常  :" + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("查询 JProcessDeploy 异常  :" + e.getMessage());
		}
		return list;
	}

	@Override
	public void deleteProcessInstanceFromJProcess(String processInstanceId) {
		final String sql = "delete  from JProcess where jp_processInstanceId = '" + processInstanceId + "' ";
		try {
			getJdbcTemplate().execute(sql);
		} catch (DataAccessException e) {

			e.printStackTrace();
			throw new RuntimeException("不存在 ID为  【" + processInstanceId + "】的流程实例 !");
		}
	}

	@Override
	public List<JProCand> getValidJProCands(String processInstanceId) {
		List<JProCand> list = null;
		final String sql = "select distinct jp_nodeid,jp_nodename from JProCand where jp_processInstanceId = ? and jp_flag =1 ";
		try {
			list = getJdbcTemplate().query(sql, new Object[] { processInstanceId }, new BeanPropertyRowMapper<JProCand>(JProCand.class));
		} catch (DataAccessException e) {

			e.printStackTrace();
			throw new RuntimeException("数据库连接异常 !");
		}

		return list;
	}

	@Override
	public List<JProcess> getJProcesses(String processInstanceId) {
		List<JProcess> list = null;
		final String sql = "select JProcess.*,em_name jp_nodedealmanname from (JProcess left join employee on em_code=jp_nodedealman) where jp_processInstanceId = ? and nvl(em_class,' ')<>'离职' order by jp_id asc";
		try {
			list = getJdbcTemplate().query(sql, new Object[] { processInstanceId }, new BeanPropertyRowMapper<JProcess>(JProcess.class));
		} catch (DataAccessException e) {

			e.printStackTrace();
			throw new RuntimeException("数据库连接异常 !");
		}

		return list;
	}

	@Override
	public JNode getJNodeBy(String processInstanceId, String nodeName) {
		final String sql = "select * from (select * from JNode where jn_processInstanceId =? and jn_name =?  order by JN_DEALTIME desc) where rownum=1";
		JNode jn = null;
		try {
			jn = getJdbcTemplate().queryForObject(sql, new Object[] { processInstanceId, nodeName },
					new BeanPropertyRowMapper<JNode>(JNode.class));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return jn;
	}

	@Override
	public List<JProcess> getValidJProcesses(String processInstanceId) {
		List<JProcess> list = null;
		final String sql = "select * from JProcess where jp_processInstanceId = ? and jp_flag =1 ";
		try {
			list = getJdbcTemplate().query(sql, new Object[] { processInstanceId }, new BeanPropertyRowMapper<JProcess>(JProcess.class));
		} catch (DataAccessException e) {

			e.printStackTrace();
			throw new RuntimeException("数据库连接异常 !");
		}

		return list;

	}

	@SuppressWarnings("deprecation")
	@Override
	public String getDuedate(int jpid) {
		JProcess jp = getJProcessById(jpid);
		String jprocessDefid = getProcessDefIdByCaller(jp.getJp_caller());
		List<JTask> list = getJTaskByProcessDefId(jprocessDefid);
		int due = 0;
		SimpleDateFormat sdf = new SimpleDateFormat(Constant.YMD_HMS);
		for (JTask task : list) {
			if (task.getJt_name().equals(jp.getJp_nodeName())) {
				due = task.getJt_duedate();
			}
		}
		Date d = jp.getJp_launchTime();
		d.setHours(d.getHours() + due);
		return due == 0 ? "无限时" : sdf.format(d);
	}

	private JProcess getJProcessById(int jpid) {
		final String sql = "select * from JProcess where jp_id = ?";
		return getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<JProcess>(JProcess.class), jpid);
	}

	@Override
	public void updateStayminutes(String taskId, String processInstanceId, String dealTime) {
		String sql = "update jprocess set jp_stayminutes = (SELECT ceil((to_date('" + dealTime
				+ "', 'yyyy-mm-dd HH24:mi:ss') - jp_launchtime)*1440) " + "FROM jprocess where jp_nodeid = '" + taskId
				+ "') where jp_nodeid='" + taskId + "'";
		getJdbcTemplate().execute(sql);
	}

	@Override
	public void updateClassify(int id, int parentid) {
		String sql = "update jprocessdeploy set jd_parentid = " + parentid + " where jd_id = " + id;
		getJdbcTemplate().execute(sql);
	}

	@Override
	public JprocessButton getJprocessButton(String processDefId, String nodeName,String caller) {
		final String sql = "select jb_id,jb_buttonid,jb_buttonname,jb_fields,jb_message,jt_neccessaryfield  from jprocessbutton left join jtask on jb_buttonid=jt_button where jt_processdefid='"
				+ processDefId + "' AND jt_name='" + nodeName + "' and jb_caller='"+caller+"'";
		List<JprocessButton> list = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<JprocessButton>(JprocessButton.class));
		if (list.size() > 0) {
			return list.get(0);
		} else
			return null;
	}

	@Override
	public List<JprocessButton> getJprocessButtonsByCaller(String caller) {
		return query("select * from JprocessButton where jb_caller=?", JprocessButton.class, caller);
	}

	@Override
	public JTask getFinalJTask(String defid) {
		final String sql = "select * from JTask where jt_processDefId = ? order by jt_id desc";
		List<JTask> lists = getJdbcTemplate().query(sql, new Object[] { defid }, new BeanPropertyRowMapper<JTask>(JTask.class));
		if (lists.size() > 0) {
			return lists.get(0);
		} else
			return null;
	}

	private boolean IsRemind(Employee e) {
		boolean bool = true;
		try {
			Object remind = getFieldDataByCondition("Employee", "em_remind", "em_id=" + e.getEm_id());
			if (remind != null) {
				bool = 1 == Integer.parseInt(remind.toString());
			}
		} catch (Exception e1) {
		}
		return bool;
	}

	@Override
	public String getProcessDefIdByTask(String taskId) {
		final String sql = "select  jd_processDefinitionId from jprocessdeploy left join Jprocess on jp_caller = jd_caller where jprocess.jp_nodeid =?";
		return getJdbcTemplate().queryForObject(sql, new Object[] { taskId }, String.class);
	}

	@Override
	public List<JprocessCommunicate> getCommunicates(String taskId, String processInstanceId) {
		List<JprocessCommunicate> communicates = getJdbcTemplate().query(
				"select * from jprocesscommunicate where jc_nodeid=? and jc_processinstanceid=? and jc_topid=0 order by jc_id asc",
				new BeanPropertyRowMapper<JprocessCommunicate>(JprocessCommunicate.class), taskId, processInstanceId);
		for (JprocessCommunicate communicate : communicates) {
			List<JprocessCommunicate> lists = getJdbcTemplate().query(
					"select * from JprocessCommunicate where jc_nodeid=? and jc_processinstanceid=? and  jc_topid=? order by jc_id asc",
					new BeanPropertyRowMapper<JprocessCommunicate>(JprocessCommunicate.class), taskId, processInstanceId,
					communicate.getJc_id());
			communicate.setChildrens(lists);
		}
		return communicates;
	}


	@Override
	public String getCommunicates(String processInstanceId) {
		StringBuilder sb = new StringBuilder();
		
		List<JprocessCommunicate> communicates = getJdbcTemplate().query(
				"select * from jprocesscommunicate where jc_processinstanceid=? order by jc_id asc",
				new BeanPropertyRowMapper<JprocessCommunicate>(JprocessCommunicate.class), processInstanceId);
		
		for(JprocessCommunicate communicate:communicates){
			int topId = communicate.getJc_topid();
			if(topId==0){
				int subof = communicate.getJc_id();
				
				sb.append(communicate.getJc_message());
				
				for(JprocessCommunicate child:communicates){
					if(subof==child.getJc_topid()){
						sb.append(child.getJc_message());
					}
				}
			}
		}
		
		return sb.toString();
	}
	
	@Override
	public JProcess getHistJProcess(String processInstanceId, String taskName) {
		// TODO Auto-generated method stub
		String sql = "select  * from  Jprocess where JP_PROCESSINSTANCEID =? and jp_nodename=? and nvl(jp_status,' ')<>'待审批' and rownum=1";
		return getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<JProcess>(JProcess.class),
				new Object[] { processInstanceId, taskName });
	}

	@Override
	public List<JProcessDeploy> getValidJProcessDeploys() {
		final String sql = "select * from JprocessDeploy where jd_enabled='是' and jd_xmlstring like '%task%' ";
		List<JProcessDeploy> list = null;
		try {
			list = getJdbcTemplate().query(sql, new Object[] {}, new BeanPropertyRowMapper<JProcessDeploy>(JProcessDeploy.class));

		} catch (DataAccessException e) {
			logger.info("查询 JProcessDeploy 异常  :" + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("查询 JProcessDeploy 异常  :" + e.getMessage());
		}
		return list;
	}

	@Override
	public void SaveJProcesDeployLog(final String caller, final String definitionId, final String xmll) {
		Employee employee = SystemSession.getUser();
		final int jl_id = getIdBySeq("JProcessDeployLog_SEQ");
		String sql = "insert into JProcessDeployLog (jl_id,jl_caller,jl_processdefinitionid,jl_xmlstring,jl_updatetime,jl_updater,jl_updatercode) values"
				+ "(?,?,?,?,"
				+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())
				+ ",'"
				+ employee.getEm_name()
				+ "','"
				+ employee.getEm_code() + "')";
		getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
			@Override
			protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException, DataAccessException {
				ps.setInt(1, jl_id);
				ps.setString(2, caller);
				ps.setString(3, definitionId);
				lobHandler.getLobCreator().setClobAsString(ps, 4, xmll);
			}
		});

	}
	
	@Override
	public void SaveFlowChartLog(final String caller, final String shortName, final String xmll) {
		Employee employee = SystemSession.getUser();
		final int fcl_id = getIdBySeq("flow_chartlog_SEQ");
		String sql = "insert into flow_chartlog (fcl_id,fcl_caller,fcl_shortname,fcl_xmlstring,fcl_updatetime,fcl_updater,fcl_updatecode) values"
				+ "(?,?,?,?,"
				+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())
				+ ",'"
				+ employee.getEm_name()
				+ "','"
				+ employee.getEm_code() + "')";
		getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
			@Override
			protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException, DataAccessException {
				ps.setInt(1, fcl_id);
				ps.setString(2, caller);
				ps.setString(3, shortName);
				lobHandler.getLobCreator().setClobAsString(ps, 4, xmll);
			}
		});

	}

	@Override
	public List<JProcessDeploy> getJProcessDeploysByCondition(String condition) {
		final String sql = "select * from JprocessDeploy where " + condition;
		return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<JProcessDeploy>(JProcessDeploy.class));
	}

	@Override
	public String getSimpleOrgAssignees(String condition) {
		// condition 查询分成两段 #
				String employeecondition = null;
				String orcondition = null;
				boolean bool = false;
				if (condition != null) {
					String[] arr = condition.split("#");
					orcondition = arr[0];
					employeecondition = arr[1];
				}
				StringBuffer sb = new StringBuffer();
				StringBuffer smallsb = new StringBuffer();
				StringBuffer childsb = new StringBuffer();
				sb.append("[");

				String allCondition = "";
				if (orcondition != null & employeecondition != null) {
					allCondition = "and " + orcondition + "and " + employeecondition;
				}

				List<Object[]> datas = baseDao.getFieldsDatasByCondition("(hrorg left join employee on or_id=em_defaultorid)", new String[] {
						"or_id", "or_name", "em_id", "em_name", "em_code" },
						"nvl(or_statuscode,' ')<>'DISABLE' and nvl(em_defaultorid,0)<>0 and nvl(em_class,' ')<>'离职' " + allCondition
								+ " order by or_id");

				String orId = "";
				bool = false;
				if (datas != null) {
					for (Object[] obj : datas) {
						if (obj != null) {
							if (obj[0] != null) {
								// 排序好之后进行分组判断
								if (!orId.equals(obj[0].toString())) {
									// 子孩子添加完再结尾,然后再重置，注意最后一组数据要在循环结束后补全
									if (bool) {
										// smallsb.substring(0,smallsb.length()-1);
										smallsb.append("]},");
										sb.append(smallsb);
									}

									orId = obj[0].toString();
									smallsb.setLength(0);
									childsb.setLength(0);
									smallsb.append("{id:\"" + obj[0] + "\",text:\"" + obj[1] + "\",leaf:false,checked:false, expanded:true,children:[");
									bool = true;
									childsb.append("{id:\"" + obj[2] + "\",text:\"" + obj[3] + "(" + obj[4] + ")" + "\",qtip:\"" + obj[4]
											+ "\",leaf:true,checked:false},");
									smallsb.append(childsb);
								} else {
									childsb.setLength(0);
									childsb.append("{id:\"" + obj[2] + "\",text:\"" + obj[3] + "(" + obj[4] + ")" + "\",qtip:\"" + obj[4]
											+ "\",leaf:true,checked:false},");
									smallsb.append(childsb);
								}
							}
						}
					}

					// 把最后一组数据加上，因为上一组的数据添加是因为和下一组数据不同，而最后一组数据之后没有数据对比
					smallsb.append("]},");
					sb.append(smallsb);
				}

				sb.setCharAt(sb.length() - 1, ']');
				String str = sb.toString().replaceAll(",]", "]");
				return str;
	}

	@Override
	public String getSimpleHrJob(String condition, Integer joborgnorelation) {
		// TODO Auto-generated method stub
		String employeecondition = null;
		String orcondition = null;
		if (condition != null) {
			String[] arr = condition.split("#");
			orcondition = arr[0];
			employeecondition = arr[1];
		}

		String allCondition = "";
		if (orcondition != null & employeecondition != null) {
			allCondition = "and " + orcondition + "and " + employeecondition;
		}

		List<Job> jobs = getJdbcTemplate().query(
				"select * from Job where nvl(jo_statuscode,' ')<>'DISABLE'" + allCondition
						+ " order by NLSSORT(jo_name,'NLS_SORT = SCHINESE_PINYIN_M ') asc", new BeanPropertyRowMapper<Job>(Job.class));

		StringBuffer sb = new StringBuffer();
		sb.append("[");
		if (joborgnorelation != null && joborgnorelation == 1) {
			for (Job job : jobs) {
				sb.append("{id:\"" + job.getJo_id() + "\",text:\"" + job.getJo_name() + "(" + job.getJo_code() + ")" + "\",qtip:\""
						+ job.getJo_code() + "\",leaf:true,checked:false},");// 这个加上岗位代号是临时加上去的,未提交。

			}
		} else {
			System.out.println(joborgnorelation);
			List<Map<String, Object>> orgIdNames = getJdbcTemplate().queryForList(
					"select distinct jo_orgId, jo_orgName from Job left join Hrorg on jo_orgid=or_id where nvl(or_statuscode,' ')<>'DISABLE' "
							+ allCondition
							+ "and nvl(jo_statuscode,' ')<>'DISABLE' order by NLSSORT(jo_orgName,'NLS_SORT = SCHINESE_PINYIN_M ') asc");
			for (Map<String, Object> orgIdName : orgIdNames) {
				sb.append("{id:\"" + orgIdName.get("jo_orgId") + "\",text:\"" + orgIdName.get("jo_orgName")
						+ "\",leaf:false,checked:false,expanded:true,children:[");

				for (Job job : jobs) {
					if (Integer.parseInt(orgIdName.get("jo_orgId").toString()) - job.getJo_orgId() == 0) {
						sb.append("{id:\"" + job.getJo_id() + "\",text:\"" + job.getJo_name() + "(" + job.getJo_code() + ")" + "\",qtip:\""
								+ job.getJo_code() + "\",leaf:true,checked:false},");// 这个加上岗位代号是临时加上去的,未提交。
					}
				}
				sb.append("]},");
			}
		}
		sb.setCharAt(sb.length() - 1, ']');
		String str = sb.toString().replaceAll(",]", "]");

		return str;
	}

}
