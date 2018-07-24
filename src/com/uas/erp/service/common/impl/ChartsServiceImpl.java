package com.uas.erp.service.common.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.model.SubsFormula;
import com.uas.erp.model.SubsFormulaDet;
import com.uas.erp.model.SubsNum;
import com.uas.erp.model.SubsNum.SubsNumDet;
import com.uas.erp.service.common.ChartsService;

@Service
public class ChartsServiceImpl implements ChartsService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private EmployeeDao employeeDao;

	@Override
	public byte[] SaveImage(MultipartFile file, Integer id, String table) {
		byte[] bytes = null;
		try {
			bytes = file.getBytes();
			baseDao.saveBlob(table, "img_", bytes, "id_=" + id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	@Override
	public byte[] getImage(Integer id, String table) throws IOException {
		return baseDao.getBlob(table, "img_", "id_=" + id);
	}

	@Override
	public Map<String, Object> getSubsData(int id, String caller) {
		Map<String, Object> map = new HashMap<String, Object>();
		/* SqlRowList rs = baseDao.queryForRowSet("select id_,code_,title_,sql_,before_,style_,keyfield_,valuefield_,keydisplay_,valuedisplay_,unit_,status_,statuscode_,checked_,isapplied_,desc_,intro_,img_ from subsformula where id_=?",id); */
		SqlRowList rs = baseDao.queryForRowSet("select * from subsformula where id_=?", id);
		if (rs.next()) {
			map.put("id_", rs.getString("id_"));
			map.put("code_", rs.getString("code_"));
			map.put("title_", rs.getString("title_"));
			map.put("sql_", rs.getString("sql_"));
			map.put("before_", rs.getString("before_"));
			map.put("style_", rs.getString("style_"));
			map.put("keyfield_", rs.getString("keyfield_"));
			map.put("valuefield_", rs.getString("valuefield_"));
			map.put("keydisplay_", rs.getString("keydisplay_"));
			map.put("valuedisplay_", rs.getString("valuedisplay_"));
			map.put("unit_", rs.getString("unit_"));
			map.put("status_", rs.getString("status_"));
			map.put("statuscode_", rs.getString("statuscode_"));
			map.put("checked_", rs.getString("checked_"));
			map.put("isapplied_", rs.getString("isapplied_"));
			map.put("desc_", rs.getString("desc_"));
			map.put("intro_", rs.getString("intro_"));
			map.put("img_", rs.getString("img_"));
		}
		return map;
	}

	@Override
	public List<SubsFormulaDet> getSubsFormulaDet(Integer formulaId) {
		try {
			return baseDao.getJdbcTemplate().query("select * from SubsFormula_Det where formula_id_=? order by detno_",
					new BeanPropertyRowMapper<SubsFormulaDet>(SubsFormulaDet.class), formulaId);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void save(String formStore, String gridStore, String caller,String param1, String param2) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<String> gridSql;
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		String formSql = SqlUtil.getInsertSqlByMap(store, caller);
		baseDao.execute(formSql);
		if (caller != null && "SubsNum".equals(caller)) {
			for (Map<Object, Object> s : grid) {
				s.put("det_id", baseDao.getSeqId("SubsNum_det_SEQ"));
			}
			gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "SubsNum_det");
		} else {
			for (Map<Object, Object> s : grid) {
				s.put("det_id_", baseDao.getSeqId("SubsFormula_det_SEQ"));
			}
			gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "SubsFormula_det");
		}
		baseDao.execute(gridSql);
		// 保存参数设置
		List<Map<Object, Object>> conditionGrid = BaseUtil
				.parseGridStoreToMaps(param1);
		if (conditionGrid!=null && conditionGrid.size()!=0) {
			for (Map<Object, Object> map : conditionGrid) {
				map.put("id_", baseDao.getSeqId("SUBSNUM_CONDITIONS_SEQ"));
			}
			if(conditionGrid!=null&&conditionGrid.size()>0){
				List<String> conditionGridSql = SqlUtil.getInsertSqlbyGridStore(conditionGrid,
						"SUBSNUM_CONDITIONS");
				baseDao.execute(conditionGridSql);
			}
		}
		// 保存关联关系设置
		List<Map<Object, Object>> relationConfigGrid = BaseUtil
				.parseGridStoreToMaps(param2);
		if (relationConfigGrid!=null && relationConfigGrid.size()!=0) {
			for (Map<Object, Object> map : relationConfigGrid) {
				map.put("sr_id", baseDao.getSeqId("SUBSNUM_RELATIONCONFIG_SEQ"));
			}
			if(relationConfigGrid!=null&&relationConfigGrid.size()>0){
				List<String> relationConfigGridSql = SqlUtil.getInsertSqlbyGridStore(relationConfigGrid,
						"SUBSNUM_RELATIONCONFIG");
				baseDao.execute(relationConfigGridSql);
			}
		}
		baseDao.logger.save(caller, "id_", store.get("id_"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void delete(String caller, int id) {
		// 只能删除[在录入]的订单资料!
		Object status = baseDao.getFieldDataByCondition(caller, "statuscode_", "id_=" + id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, id);
		baseDao.deleteById(caller, "id_", id);
		if (caller != null && "SubsNum".equals(caller)) {
			baseDao.execute("delete from SubsNum_det where num_id=" + id);
			baseDao.execute("delete from subsnum_mans where num_id=" + id);
		} else
			baseDao.execute("delete from SubsFormula_det where formula_id_=" + id);
		// 记录操作
		baseDao.logger.delete(caller, "id_", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, id);

	}

	@Override
	public void update(String formStore, String gridStore, String caller,String param1, String param2) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> conditionGridStore = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> relationConfigGridStore = BaseUtil.parseGridStoreToMaps(param2);
		// 只能修改[在录入]的订单资料!
		Object status = baseDao.getFieldDataByCondition(caller, "statuscode_", "id_=" + store.get("id_"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, caller, "id_");
		baseDao.execute(formSql);
		if (caller != null && gstore.size() > 0) {
			String tableName = "SubsNum".equals(caller) ? "SubsNum_det" : "SubsFormula_det";
			String GId = "SubsNum".equals(caller) ? "det_id" : "det_id_";
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, tableName, GId);
			for (Map<Object, Object> s : gstore) {
				Object gid = s.get(GId);
				if (gid == null || gid.equals("") || gid.equals("0") || Integer.parseInt(gid.toString()) == 0) {// 新添加的数据，id不存在
					String sql = SqlUtil.getInsertSql(s, tableName, GId);
					gridSql.add(sql);
				}
			}
			baseDao.execute(gridSql);
		}
		//参数设置
		if (conditionGridStore!=null&&conditionGridStore.size() > 0) {
			baseDao.execute(SqlUtil.getInsertOrUpdateSql(conditionGridStore, "SUBSNUM_CONDITIONS", "id_"));
			//清除subsnum_params_instance表中该订阅号的数据
			baseDao.deleteByCondition("subsnum_params_instance", "numid_=" + store.get("id_"));
		}
		//关联关系设置
		if (relationConfigGridStore!=null&&relationConfigGridStore.size() > 0) {
			baseDao.execute(SqlUtil.getInsertOrUpdateSql(relationConfigGridStore, "SUBSNUM_RELATIONCONFIG", "sr_id"));
		}
		
		// 记录操作
		baseDao.logger.update(caller, "id_", store.get("id_"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });

	}

	@Override
	public List<Map<String, Object>> getSubsData(Integer numId, Integer mainId, Integer insId, int emId) {
		boolean bool = baseDao.checkIf("SUBS_MAN_INSTANCE", "status_=-1 and id_=" + insId + " and num_id_=" + numId + " and instance_id_="
				+ mainId + " and emp_id_=" + emId);
		if (!bool)
			baseDao.execute("update SUBS_MAN_INSTANCE set status_=-1 where id_=" + insId + " and num_id_=" + numId + " and instance_id_="
					+ mainId + " and emp_id_=" + emId);
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("SUBS_DATA", new String[] { "type_", "sontitle_", "data_", "keyfield_",
				"formula_id_", "formula_unit_", "formula_keydisplay_", "formula_valuedisplay_" }, "num_id=" + numId
				+ " and maininstance_id_=" + mainId + " and instance_id_=" + insId + " order by detno_ asc");
		for (Object[] obj : objs) {
			map = new HashMap<String, Object>();
			map.put("type", obj[0]);
			map.put("title", obj[1]);
			map.put("data", obj[2]);
			map.put("keyField", obj[3]);
			map.put("formulaId", obj[4]);
			map.put("unit", obj[5]);
			map.put("keyDisp", obj[6]);
			map.put("valueDisp", obj[7]);
			lists.add(map);
		}			
		return lists;

	}

	@Override
	public void removeSubsMans(String numIds, String emcode) {
		Employee employee = employeeDao.getEmployeeByEmcode(emcode);
		String sql = "delete from subsnum_mans where isapplied_=-1  and (num_id,emp_id) in (select num_id," + employee.getEm_id()
				+ " from subsnum_mans where num_id in (" + numIds + "))";
		baseDao.execute(sql);
		sql = "update SubsApply set status_='已取消',statuscode_='CANCEL' where empid_ =" + employee.getEm_id() + " and num_id_ in (" + numIds
				+ ")";
		baseDao.execute(sql);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "APP批量取消订阅申请", "批量取消订阅申请", "VastCancelSubsApply"));
	}

	@Override
	public List<Map<String, Object>> getSubsNums(String condition) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("subsNum", new String[] { "id_", "title_", "kind_" }, condition
				+ " order by detno_ asc");
		for (Object[] obj : objs) {
			map = new HashMap<String, Object>();
			map.put("id", obj[0]);
			map.put("title", obj[1]);
			map.put("kind", obj[2]);
			lists.add(map);
		}
		return lists;
	}

	@Override
	public List<Map<String, Object>> getSubsDataDetail(Integer formulaId, Integer insId) {
		SqlRowList rs = baseDao.queryForRowSet("select data_ from SUBS_DATADETAIL where formula_id_=" + formulaId + " and instance_id_="
				+ insId + " order by detno_");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> data = FlexJsonUtil.fromJson(rs.getString(1));
			list.add(data);
		}
		return list;
	}

	@Override
	public void submit(int id, String caller) {
		// 只能提交状态为[在录入]的单据!
		Object status = baseDao.getFieldDataByCondition(caller, "statuscode_", "id_=" + id);
		StateAssert.submitOnlyEntering(status);
		handlerService.beforeSubmit(caller, id);
		// 执行提交操作
		baseDao.submit(caller, "id_=" + id, "status_", "statuscode_");
		// 记录操作
		baseDao.logger.submit(caller, "id_", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, id);
	}

	@Override
	public void resSubmit(int id, String caller) {
		// 只能对状态为[已提交]的单据进行反提交操作!
		Object status = baseDao.getFieldDataByCondition(caller, "statuscode_", "id_=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, id);
		// 执行反提交操作
		baseDao.resOperate(caller, "id_=" + id, "status_", "statuscode_");
		// 记录操作
		baseDao.logger.resSubmit(caller, "id_", id);
		// 提交后
		handlerService.afterResSubmit(caller, id);
	}

	@Override
	public void audit(int id, String caller) {
		// 只能对状态为[已提交]的单进行审核操作!
		Object status = baseDao.getFieldDataByCondition(caller, "statuscode_", "id_=" + id);
		StateAssert.auditOnlyCommited(status);

		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, id);
		// 执行审核操作
		baseDao.audit(caller, "id_=" + id, "status_", "statuscode_");
		// 记录操作
		baseDao.logger.audit(caller, "id_", id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, id);

	}

	@Override
	public void resAudit(int id, String caller) {
		// 只能对状态为[已审核]的单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition(caller, "statuscode_", "id_=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行审核前的其它逻辑
		handlerService.beforeResAudit(caller, id);
		// 反审核操作
		baseDao.resOperate(caller, "id_=" + id, "status_", "statuscode_");
		baseDao.updateByCondition(caller, "checked_=0", "id_=" + id);
		// 记录操作
		baseDao.logger.resAudit(caller, "id_", id);
		// 执行审核后的其它逻辑
		handlerService.afterResAudit(caller, id);
	}

	@Override
	public void bannedCharts(int id, String caller) {
		// 执行禁用操作
		baseDao.banned(caller, "id_=" + id, "status_", "statuscode_");
		baseDao.updateByCondition(caller, "enable_=0", "id_=" + id);
		// 记录操作
		baseDao.logger.banned(caller, "id_", id);

	}

	@Override
	public void resBannedCharts(int id, String caller) {
		// 执行反禁用操作
		baseDao.resOperate(caller, "id_=" + id, "status_", "statuscode_");
		baseDao.updateByCondition(caller, "enable_=-1", "id_=" + id);
		// 记录操作
		baseDao.logger.resBanned(caller, "id_", id);
	}

	@Override
	public void testSubsFormula(Employee employee, int id, String caller) {
		Object[] objs = baseDao.getFieldsDataByCondition(caller, new String[] { "sql_", "style_", "before_" }, "id_=" + id);
		boolean bsql = false;
		if (objs != null && objs[0] != null && objs[1] != null) {
			String sql = String.valueOf(objs[0]).replaceAll("@EMID", String.valueOf(employee.getEm_id()))
					.replaceAll("@EMCODE", "'" + employee.getEm_code() + "'").replaceAll("@EMNAME", "'" + employee.getEm_name() + "'")
					.replaceAll("@EMDEFAULTORNAME", "'" + employee.getEm_defaultorname() + "'")
					.replaceAll("@EMDEPART", "'" + employee.getEm_depart() + "'").replaceAll(";", "")
					.replaceAll("=@[^\\s]+\\s+"," is not null ")
					.replaceAll("=@[^\\s]+$"," is not null ");
			if (objs[2] != null) {
				boolean bbefore = baseDao.checkSQL(objs[2].toString());
				if (!bbefore)
					BaseUtil.showError("测试不通过,请检查执行前语句");
			}
			if (!objs[1].equals("sum") && !objs[1].equals("list")) {// 非列表和数据汇总，查询结果判断是否有xField,yField两个字段
				bsql = baseDao.checkSQL("select xField,yField,gField from (" + sql + ")")
						|| baseDao.checkSQL("select xField,yField from (" + sql + ")");
			} else if (objs[1].equals("sum")) {
				bsql = baseDao.checkSQL("select sum from (" + sql + ")");
			} else if (objs[1].equals("list")) {
				String det = baseDao.getFieldValue("SUBSFORMULA_DET", "WMSYS.WM_CONCAT(field_)", "FORMULA_ID_=" + id, String.class);
				bsql = baseDao.checkSQL("select " + det + " from (" + sql + ")");
			}
		}
		if (!bsql)
			BaseUtil.showError("测试不通过,请检查SQL语句");
		baseDao.updateByCondition(caller, "checked_=-1", "id_=" + id);
	}

	@Override
	public String batchTestSubsFormula(Employee employee,String ids) {
		String out = baseDao.callProcedure("SP_BATCHTESTSUBSFORMULA", 
				new Object[]{employee.getEm_code(),ids});
		return out==null?"":out;
	}
	
	@Override
	public List<Map<String, Object>> getPreviewDatas(int id, String caller, String params) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(params);
		Object[] objs = baseDao.getFieldsDataByCondition(caller, new String[] { "sql_", "style_" }, "id_=" + id);
		if (objs != null && objs[0] != null && objs[1] != null) {
			String rowStr = store.get("RN") != null ? " WHERE ROWNUM<=" + store.get("RN") : "";
			String sql = String.valueOf(objs[0]).replaceAll("@EMID", String.valueOf(store.get("EMID")))
					.replaceAll("@EMCODE", "'" + store.get("EMCODE") + "'").replaceAll("@EMNAME", "'" + store.get("EMNAME") + "'")
					.replaceAll("@EMDEFAULTORNAME", "'" + store.get("EMDEFAULTORNAME") + "'")
					.replaceAll("@EMDEPART", "'" + store.get("EMDEPART") + "'").replaceAll(";", "")
					.replaceAll("=@[^\\s]+\\s+"," is not null ")
					.replaceAll("=@[^\\s]+$"," is not null ");;
			String queryFields = "";
			if (!objs[1].equals("sum") && !objs[1].equals("list")) {
				queryFields = objs[0].toString().indexOf("gField") > 0 ? "xField,yField,gField" : "xField,yField";
			} else if (objs[1].equals("sum")) {
				queryFields = "sum";
			} else if (objs[1].equals("list")) {
				queryFields = baseDao.getFieldValue("(select * from SUBSFORMULA_DET order by detno_ asc)", "WMSYS.WM_CONCAT(field_)", "FORMULA_ID_=" + id, String.class);
			}
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list = baseDao.getJdbcTemplate().queryForList("select " + queryFields + " from (" + sql + ")" + rowStr);
			Iterator<Map<String, Object>> iter = list.iterator();
			List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = null;
			while (iter.hasNext()) {
				map = iter.next();
				for (String field : queryFields.split(",")) {
					Object value = map.get(field.toUpperCase());
					map.remove(field.toUpperCase());
					map.put(field, value);
				}
				datas.add(map);
			}
			return datas;
		}
		return null;
	}

	@Override
	public void vastCancelSubsApply(String caller, String datas) {
		String sql = "delete from subsnum_mans where isapplied_=-1 and (emp_id,num_id) in (" + datas + ")";
		baseDao.execute(sql);
		sql = "update SubsApply set status_='已取消',statuscode_='CANCEL' where (empid_,num_id_) in (" + datas + ")";
		baseDao.execute(sql);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "批量取消订阅申请", "批量取消订阅申请", caller));
	}

	@Override
	public String vastAddSubsApply(String caller, String ids, Employee employee) {
		String sql = "insert into SubsApply (ID_,CODE_,NAME_,DATE_,STATUS_,STATUSCODE_,EMPNAME_,EMPCODE_,EMPDEP_,NUM_ID_,NUM_TITLE_,EMPID_) "
				+ "select SubsApply_seq.nextval,null,'订阅批量申请',sysdate,'在录入','ENTERING','"
				+ employee.getEm_name()
				+ "','"
				+ employee.getEm_code()
				+ "','"
				+ employee.getEm_depart()
				+ "',id_,title_,"
				+ employee.getEm_id()
				+ " from subsnum where id_ in ("
				+ ids
				+ ")"
				+ "and id_ not in (select NUM_ID_ from SubsApply where EMPID_="
				+ employee.getEm_id() + " and STATUSCODE_<>'CANCEL')";
		baseDao.execute(sql);
		List<Object> ApplyIds = baseDao.getFieldDatasByCondition("SubsApply", "id_", "code_ is null and EMPID_=" + employee.getEm_id());
		StringBuffer sb = new StringBuffer();
		String log = null;
		int index = 0;
		String updatesql;
		String vcode = null;
		for (Object id : ApplyIds) {
			vcode = baseDao.sGetMaxNumber("SubsApply", 0);
			updatesql = "update SubsApply set code_='" + vcode + "' where id_=" + id;
			baseDao.execute(updatesql);
			submitSubsApply(Integer.parseInt(String.valueOf(id)), "SubsApply");
			log = "成功生成订阅申请单:" + "<a href=\"javascript:openUrl('jsps/common/subsapply.jsp?formCondition=id_IS" + id + "')\">" + vcode
					+ "</a>&nbsp;";
			index++;
			sb.append(index).append(": ").append(log).append("<hr>");
		}
		return sb.toString();
	}

	public void submitSubsApply(int id, String caller) {
		// 只能提交状态为[在录入]的单据!
		Object status = baseDao.getFieldDataByCondition(caller, "statuscode_", "id_=" + id);
		StateAssert.submitOnlyEntering(status);
		handlerService.beforeSubmit(caller, id);
		// 执行提交操作
		baseDao.submit(caller, "id_=" + id, "status_", "statuscode_");
		// 记录操作
		baseDao.logger.submit(caller, "id_", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, id);
	}

	@Override
	public void auditSubsApply(int id, String caller) {
		// 只能对状态为[已提交]的单进行审核操作!
		Object status = baseDao.getFieldDataByCondition(caller, "statuscode_", "id_=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, id);
		// 执行审核操作
		baseDao.audit(caller, "id_=" + id, "status_", "statuscode_", "auditdate_", "auditman_");
		// 记录操作
		// 订阅申请单审核后自动插入表subsnum_mans
		boolean b = baseDao.checkIf("SUBSNUM_MANS", "(EMP_ID,NUM_ID) in (select empid_,num_id_ from SubsApply where id_=" + id + ")");
		if (b)
			baseDao.execute("update SUBSNUM_MANS set ISAPPLIED_=-1 where (EMP_ID,NUM_ID) in (select empid_,num_id_ from SubsApply where id_="
					+ id + ")");
		else
			baseDao.execute("insert into SUBSNUM_MANS(EMP_ID,NUM_ID,ISDEFAULT_,ISAPPLIED_) select empid_,num_id_,case when kind_='private' then -1 else 0 end,-1 from SubsApply left join subsnum on num_id_=subsnum.id_ where SubsApply.id_="
					+ id);
		baseDao.logger.audit(caller, "id_", id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, id);
	}

	@Override
	public void resAuditSubsApply(int id, String caller) {
		// 只能对状态为[已审核]的单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition(caller, "statuscode_", "id_=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行审核前的其它逻辑
		handlerService.beforeResAudit(caller, id);
		// 反审核操作
		baseDao.resAudit(caller, "id_=" + id, "status_", "statuscode_", "auditdate_", "auditman_");
		// 反审核后从subsnum_mans删除申请
		baseDao.execute("delete from subsnum_mans where isapplied_=-1 and (emp_id,num_id) in (select empid_,num_id_ from SubsApply where id_="
				+ id + ")");
		// 记录操作
		baseDao.logger.resAudit(caller, "id_", id);
		// 执行审核后的其它逻辑
		handlerService.afterResAudit(caller, id);

	}

	@Override
	public List<Map<String, Object>> getPersonalSubs(String em_code) {
		Employee employee = employeeDao.getEmployeeByEmcode(em_code);
		String sql = "SELECT NUM_ID,TITLE_,IMG_,KIND_,TYPE_,ISAPPLIED_,REMARK_ FROM (select NUM_ID,SUBSNUM.TITLE_ TITLE_,SUBSNUM.IMG_ IMG_,KIND_,TYPE_,SUBSNUM_MANS.ISAPPLIED_ ISAPPLIED_,SUBSNUM.REMARK_ REMARK_ from SUBSNUM_MANS LEFT JOIN EMPLOYEE ON EMP_ID=EM_ID LEFT JOIN SUBSNUM ON ID_=NUM_ID where emp_id="
				+ employee.getEm_id() + " and nvl(enable_,0)<>0)";
		return baseDao.getJdbcTemplate().queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getPersonalApplySubs(String em_code) {
		Employee employee = employeeDao.getEmployeeByEmcode(em_code);
		String sql = "select ID_,TITLE_,KIND_,TYPE_,REMARK_,STATUS_ from (select id_,title_,kind_,type_,remark_,case when ("
				+ employee.getEm_id()
				+ ",id_)in (select nvl(EMPID_,0),nvl(NUM_ID_,0) from SubsApply where STATUSCODE_<>'CANCEL')then 2 else 3 end status_ from (select * from subsnum where ("
				+ employee.getEm_id()
				+ ",id_)not in (select emp_id,num_id from SUBSNUM_MANS) AND nvl(enable_,0)<>0 and nvl(isapplied_,0)<>0 order by type_))";
		return baseDao.getJdbcTemplate().queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getApplySubs(String em_code) {
		Employee employee = employeeDao.getEmployeeByEmcode(em_code);
		List<Object[]> datas = baseDao
				.getFieldsDatasByCondition(
						"(select id_,title_,img_,kind_,type_,case when ("
								+ employee.getEm_id()
								+ ",id_) in (select emp_id,num_id from SUBSNUM_MANS)then 1 when ("
								+ employee.getEm_id()
								+ ",id_) not in (select emp_id,num_id from SUBSNUM_MANS) and ("
								+ employee.getEm_id()
								+ ",id_)in (select nvl(EMPID_,0),nvl(NUM_ID_,0) from SubsApply where STATUSCODE_<>'CANCEL')then 2 else 3 end status_ from (select * from subsnum where nvl(enable_,0)<>0 and nvl(isapplied_,0)<>0 order by type_))",
						new String[] { "ID_", "TITLE_", "IMG_", "KIND_", "TYPE_", "STATUS_" }, "1=1 order by type_,status_");
		String type = "";
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Object[] obj : datas) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", obj[0]);
			map.put("title", obj[1]);
			map.put("img", obj[2]);
			map.put("kind", obj[3]);
			map.put("status", obj[5]);
			if (!type.equals(obj[4])) {
				if (obj[4] != null) {
					list = new ArrayList<Map<String, Object>>();
					type = obj[4].toString();
					list.add(map);
					dataMap.put(type, list);
				}
			} else {
				list.add(map);
			}
		}

		List<Map<String, Object>> relist = new ArrayList<Map<String, Object>>();
		relist.add(dataMap);

		return relist;
	}

	@Override
	public List<Object[]> getPreviewMain(Integer id) {
		List<Object[]> detail = new ArrayList<Object[]>();
		String sql = "select subsformula.title_,subsformula.intro_,subsformula.img_,subsformula.style_ from SubsNum LEFT JOIN subsnum_det ON ID_=num_id "
				+ "LEFT JOIN subsformula ON code_=formula_code_ where SubsNum.ID_=" + id + "  order by subsnum_det.detno_ ASC";
		List<Map<String, Object>> list = baseDao.queryForList(sql);
		Iterator<Map<String, Object>> iter = list.iterator();
		while (iter.hasNext()) {
			Object[] results = new Object[4];
			Map<String, Object> m = iter.next();
			Object title = m.get("title_");
			Object intro = m.get("intro_");
			Object img = m.get("img_");
			Object style = m.get("style_");
			results[0] = title;
			results[1] = intro;
			results[2] = img;
			results[3] = style;
			detail.add(results);
		}
		return detail;
	}

	@Override
	public Object[] getPreviewsMain(Integer id) {

		Object[] detail = baseDao.getFieldsDataByCondition("SubsFormula", new String[] { "title_", "intro_", "img_", "style_" }, "id_="
				+ id);
		return detail;
	}

	@Override
	public String getPreviewTitle(Integer id) {
		String title = (String) baseDao.getFieldDataByCondition("SubsNum", "title_", "id_ = " + id);
		return title;
	}

	@Override
	public Object getMainImg(Integer id) {
		Object img = baseDao.getFieldDataByCondition("SubsNum", "img_", "id_ = " + id);
		return img;
	}

	@Override
	public SubsNum getSubsNum(int id) {
		SubsNum subs = baseDao
				.queryBean(
						"select title_,date_,kind_,freq_,sharecounts_,enable_,subscounts_,remark_,statuscode_,status_,type_,id_,isapplied_ from SubsNum where id_=?",
						SubsNum.class, id);
		if (null != subs) {
			byte[] bytes = baseDao.getBlob("SubsNum", "img_", "id_=" + id);
			// 方便导出，以base64字符串表示
			subs.setImg_(StringUtil.encodeBase64(bytes));
			List<SubsNumDet> dets = baseDao.query("select * from subsnum_det where num_id=?", SubsNumDet.class, id);
			if (null != dets) {
				// 同时考虑关联订阅项
				for (SubsNumDet det : dets) {
					Integer formulaId = baseDao.queryForObject("select id_ from SubsFormula where code_=?", Integer.class,
							det.getFormula_code_());
					det.setFormula(getSubsFormula(formulaId));
				}
				subs.setDets(dets);
			}
		}
		return subs;
	}

	@Override
	@Transactional
	public void saveSubsNum(SubsNum subs) {
		// title作为唯一标识
		Integer newId = baseDao.queryForObject("select id_ from SubsNum where title_=?", Integer.class, subs.getTitle_());
		if (null != newId) {
			// 覆盖原订阅号
			baseDao.deleteById("SubsNum", "id_", newId);
			baseDao.deleteById("subsnum_det", "num_id", newId);
		} else {
			newId = baseDao.getSeqId("SubsNum_SEQ");
		}
		subs.setId_(newId);
		baseDao.execute(
				"INSERT INTO SubsNum (ID_, TITLE_, DATE_, KIND_, FREQ_, SUBSCOUNTS_, SHARECOUNTS_, ENABLE_, CHECKED_, REMARK_, STATUS_, STATUSCODE_, TYPE_, ISAPPLIED_) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				subs.getId_(), subs.getTitle_(), subs.getDate_(), subs.getKind_(), subs.getFreq_(), 0, 0, subs.getEnable_(), 0,
				subs.getRemark_(), subs.getStatus_(), subs.getStatuscode_(), subs.getType_(), subs.getIsapplied_());
		if (!StringUtils.isEmpty(subs.getImg_())) {
			baseDao.saveBlob("SubsNum", "img_", StringUtil.decodeBase64(subs.getImg_()), "id_=" + newId);
		}
		if (null != subs.getDets()) {
			for (SubsNumDet det : subs.getDets()) {
				det.setNum_id(newId);
				det.setDet_id(baseDao.getSeqId("subsnum_det_seq"));
				if (null != det.getFormula()) {
					// 同时覆盖订阅项
					saveSubsFormula(det.getFormula());
					det.setFormula_code_(det.getFormula().getCode_());
				}
			}
			baseDao.save(subs.getDets(), "subsnum_det");
		}
	}

	@Override
	public SubsFormula getSubsFormula(int id) {
		SubsFormula formula = baseDao
				.queryBean(
						"select before_,code_,id_,keydisplay_,keyfield_,sql_,style_,title_,valuedisplay_,valuefield_,unit_,checked_,status_,statusCode_,desc_,isApplied_,intro_ from SubsFormula where id_=?",
						SubsFormula.class, id);
		if (null != formula) {
			byte[] bytes = baseDao.getBlob("SubsFormula", "img_", "id_=" + id);
			// 方便导出，以base64字符串表示
			formula.setImg_(StringUtil.encodeBase64(bytes));
			formula.setDets(baseDao.query("select * from SubsFormula_Det where formula_id_=?", SubsFormulaDet.class, id));
		}
		return formula;
	}

	@Override
	@Transactional
	public void saveSubsFormula(SubsFormula formula) {
		// title+desc作为唯一标识
		SubsFormula oldOne = baseDao.queryBean("select code_,id_ from SubsFormula where title_=? and desc_=?", SubsFormula.class,
				formula.getTitle_(), formula.getDesc_());
		String newCode = null;
		int newId = 0;
		if (null != oldOne) {
			newCode = oldOne.getCode_();
			newId = oldOne.getId_();
			// 覆盖原订阅项
			baseDao.deleteById("SubsFormula", "id_", newId);
			baseDao.deleteById("SubsFormula_det", "formula_id_", newId);
		} else {
			newCode = baseDao.sGetMaxNumber("SubsFormula", 2);
			newId = baseDao.getSeqId("SubsFormula_SEQ");
		}
		formula.setCode_(newCode);
		formula.setId_(newId);
		baseDao.execute(
				"insert into SubsFormula(before_,code_,id_,keydisplay_,keyfield_,sql_,style_,title_,valuedisplay_,valuefield_,unit_,checked_,status_,statusCode_,desc_,isApplied_,intro_) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				formula.getBefore_(), formula.getCode_(), formula.getId_(), formula.getKeydisplay_(), formula.getKeyfield_(),
				formula.getSql_(), formula.getStyle_(), formula.getTitle_(), formula.getValuedisplay_(), formula.getValuefield_(),
				formula.getUnit_(), formula.getChecked_(), formula.getStatus_(), formula.getStatusCode_(), formula.getDesc_(),
				formula.getIsApplied_(), formula.getIntro_());
		if (!StringUtils.isEmpty(formula.getImg_())) {
			baseDao.saveBlob("SubsFormula", "img_", StringUtil.decodeBase64(formula.getImg_()), "id_=" + newId);
		}
		if (null != formula.getDets()) {
			for (SubsFormulaDet det : formula.getDets()) {
				det.setFormula_id_(newId);
				det.setDet_id_(baseDao.getSeqId("SubsFormula_det_seq"));
			}
			baseDao.save(formula.getDets(), "SubsFormula_det");
		}
	}

}
