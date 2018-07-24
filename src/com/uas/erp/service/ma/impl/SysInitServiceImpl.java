package com.uas.erp.service.ma.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.drools.lang.DRLParser.and_constr_return;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Form;
import com.uas.erp.model.Initialize;
import com.uas.erp.model.JProcessDeploy;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.ProcessService;
import com.uas.erp.service.ma.SysInitService;

@Service
public class SysInitServiceImpl implements SysInitService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private FormDao formDao;
	@Autowired
	private ProcessService processService;
	HttpSession session;
	@Override
	@CacheEvict(value = { "interceptors", "configs" }, allEntries = true)
	public void saveInitSet(String update, String argType) {
		// TODO Auto-generated method stub
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(update);
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(map, argType, "id"));
	}

	@Override
	public List<DataListCombo> getComboDataByField(String caller, String field) {
		// TODO Auto-generated method stub
		List<DataListCombo> combos = dataListComboDao.getComboxsByCallerAndField(caller, field);
		return combos;
	}

	@Override
	public List<Initialize> getImportDataItem() {
		// TODO Auto-generated method stub
		try {
			List<Initialize> sns = baseDao
					.getJdbcTemplate()
					.query("select a.*,B.In_Desc parentname from initialize a left join initialize b on a.in_pid=b.in_id where a.in_pid is not null  and nvl(a.in_type,' ')='import' order by a.in_pid,a.in_detno",
							new BeanPropertyRowMapper<Initialize>(Initialize.class));
			return sns;
		} catch (EmptyResultDataAccessException exception) {
			return null;
		}
	}

	@Override
	public void InitDataFromStandard(String table) {
		// TODO Auto-generated method stub

	}

	@Override
	public void InitHrDataFromStandard() {
		// TODO Auto-generated method stub
		/**
		 * 根据标准帐套载入人事信息 (部门，组织，岗位)
		 */
		Employee currentEm = SystemSession.getUser();
		Master currentMaster = currentEm.getCurrentMaster();
		String standard_master = "uas_standard_" + (currentMaster != null ? currentMaster.getMa_installtype() : "make");
		/**
		 * 部门资料
		 * */
		List<String> sqls = new ArrayList<String>();
		sqls.add("delete department");
		sqls.add("insert into department select * from " + standard_master + ".department");
		sqls.add("delete hrorg");
		sqls.add("insert into hrorg select * from " + standard_master + ".hrorg");
		sqls.add("delete job");
		sqls.add("insert into job select  * from " + standard_master + ".job");
		baseDao.execute(sqls);
		baseDao.callProcedure("SYS_CHECK_SEQUENCE", new Object[] { "department", "dp_id" });
		baseDao.callProcedure("SYS_CHECK_SEQUENCE", new Object[] { "hrorg", "or_id" });
		baseDao.callProcedure("SYS_CHECK_SEQUENCE", new Object[] { "job", "jo_id" });
	}

	private String getStandardMaster(String master) {
		String standard_master = baseDao.getFieldValue("MASTER", "Ma_Installtype", "ma_domain='" + master + "'", String.class);
		return "uas_standard_" + (standard_master == null ? "make" : standard_master);
	}

	@Override
	public void finishInit() {
		Employee em = SystemSession.getUser();
		try {
			String currentMaster = em.getCurrentMaster().getMa_name();
			baseDao.updateByCondition("master", "ma_init=1", "ma_name='" + currentMaster + "'");
			em.getCurrentMaster().setMa_init(Constant.YES);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void InitProcessDataFromStandard() {
		Employee currentEm = SystemSession.getUser();
		Master currentMaster = currentEm.getCurrentMaster();
		String standard_master = "uas_standard_" + (currentMaster != null ? currentMaster.getMa_installtype() : "make");
		/**
		 * 部门资料
		 * */
		List<String> sqls = new ArrayList<String>();
		sqls.add("delete jprocessdeploy");
		sqls.add("insert into jprocessdeploy select * from " + standard_master + ".jprocessdeploy");
		sqls.add("delete jprocessset");
		sqls.add("insert into jprocessset select * from " + standard_master + ".jprocessset");
		baseDao.execute(sqls);
		baseDao.callProcedure("SYS_CHECK_SEQUENCE", new Object[] { "jprocessdeploy", "jd_id" });
		baseDao.callProcedure("SYS_CHECK_SEQUENCE", new Object[] { "jprocessset", "js_id" });
	}

	@Override
	public void saveBefore(String caller, int keyValue) {
		// TODO Auto-generated method stub
		updateFormStatus(caller, keyValue, "before");
	}
	/**
	 * 新增beforeDelete方法
	 */
	@Override
	public void beforeDelete(String status , int keyValue,String table,String statuscode,String keyField){
		baseDao.updateByCondition(table,""+status+"='在录入',"+statuscode+"='ENTERING'", "" + keyField +"=" + keyValue);
	}
	@Override
	public void saveAfter(String caller, int keyValue) {
		// TODO Auto-generated method stub
		updateFormStatus(caller, keyValue, "after");
	}

	private void updateFormStatus(String caller, int keyValue, String type) {
		Form form = formDao.getForm(caller, SpObserver.getSp());
		String tableName = form.getFo_table(), keyField = form.getFo_keyfield(), statusCodeField = form.getFo_statuscodefield(), statusField = form
				.getFo_statusfield();
		if (tableName != null && keyField != null && statusCodeField != null && statusField != null) {
			String upperTable = tableName.toUpperCase();
			tableName = tableName.indexOf("LEFT JOIN") > 0 ? upperTable.split("LEFT JOIN ")[0] : tableName;
			if (type.equals("before")) {
				baseDao.resOperate(tableName, keyField + "=" + keyValue, statusField, statusCodeField);
			} else
				baseDao.audit(tableName, keyField + "=" + keyValue, statusField, statusCodeField);
		}
	}

	@Override
	public void finishUcloud() {
		Employee em = SystemSession.getUser();		
		try {
			String master = BaseUtil.getXmlSetting("defaultSob");
			String currentMaster = em.getCurrentMaster().getMa_name();
			int ma_init = Integer.parseInt(baseDao.getFieldDataByCondition(master+".master", "ma_init", "ma_name='" + currentMaster + "'").toString());
			if(ma_init==0){
				List<JProcessDeploy> lists = processService.getValidJProcessDeploys();
				for (JProcessDeploy list : lists) {	
					processService.setUpProcess(list.getJd_xmlString(), list.getJd_caller(), list.getJd_processDefinitionName(), list.getJd_processDescription(),
							list.getJd_enabled(),list.getJd_ressubmit(), list.getJd_parentId(),null);
				}
				baseDao.updateByCondition(master+".master", "ma_init=1", "ma_name='" + currentMaster + "'");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	@Override
	public void insertReadStatus(String status, int man,String sourcekind) {
		if(status.equals("true")){
			String sql1 = "select count(*) from readstatus where sourcekind='ucloud' and man="+man;
			int count = baseDao.getCount(sql1);
			if(count>0){
				baseDao.updateByCondition("readstatus", "sourcekind='ucloud'", "man="+man);
			}else{
				String sql = "insert into readstatus(man,sourcekind) values("+man+",'"+sourcekind+"')";
				baseDao.execute(sql);
			}
		}else{
			baseDao.deleteByCondition("readstatus", "sourcekind='ucloud' and man="+man);
		}
	}

	@Override
	public boolean getStatus(int man,String em_code) {
		//如果已经记录过，则不弹出界面
		int count3 = baseDao.getCount("select count(*) from readstatus where sourcekind='ucloud' and man="+man);
		if(count3>0){
			return false;
		}
		String em_name = SystemSession.getUser().getEm_name();
		String em_mobile = SystemSession.getUser().getEm_mobile();
		int count = baseDao.getCount("select count(*) from enterprise where EN_ADMINNAME='"+em_name+"' and EN_ADMINPHONE='"+(em_mobile==null?"0":em_mobile)+"'");
		//如果企业表存在该用户，则显示界面
		if(count>0){
			return true;
		}else{
			//如果组织表存在该用户，显示界面
			int count2 = baseDao.getCount("select count(*) from hrorg where or_headmancode='"+em_code+"'");
			if(count2>0){
				return true;
			}
		}
		return false;
	}

	@Override
	public List<?> getFieldsDatas(String tablename, String fields,
			String relfields, String condition) {
		StringBuffer sb = new StringBuffer("SELECT * FROM (SELECT '1' ");
		sb.append(fields).append(" FROM ").append(tablename).append(" WHERE ").append(condition);
		sb.append(" UNION SELECT '0' ").append(fields).append(" FROM ");
		if(BaseUtil.getXmlSetting("saas.domain") != null){//saas
			sb.append(" UAS_STANDARD_MAKE.").append(tablename);
		}else{
			sb.append(tablename).append("@UAS");
		}
		sb.append(" WHERE ").append(condition);
		sb.append(" AND  (").append(relfields).append(") NOT IN(SELECT ").append(relfields).append(" FROM ").append(tablename).append(" WHERE ").append(condition);
		sb.append(" )) ORDER BY ENABLE DESC ");  //增加排序
		SqlRowList list = baseDao.queryForRowSet(sb.toString());
		List<Object> data = new ArrayList<Object>();
		while (list.next()) {
			data.add(list.getJSONObject());
		}
		return data;
	}

}
