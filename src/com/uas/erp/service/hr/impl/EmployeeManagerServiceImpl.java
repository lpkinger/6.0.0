package com.uas.erp.service.hr.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.PasswordEncryUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.AccountCenterService;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.hr.EmployeeManagerService;

@Service
public class EmployeeManagerServiceImpl implements EmployeeManagerService {

	static final String selectEmpl = "select em_code,em_name,em_sex,em_depart,em_defaultorname,em_position,"
			+ "em_indate from employee where em_id=?";
	static final String turnFullempMain = "insert into Turnfullmemb(tf_code,tf_recordor,tf_recordorid,tf_date,"
			+ "tf_status,tf_statuscode,tf_id)values(?,?,?,?,?,?,?)";

	static final String turnFullempDetail = "insert into Turnfullmembdetail(td_detno,td_code,td_name,td_sex,"
			+ "td_depart,td_hrorg,td_position,td_date,td_tfid,td_id)values(?,?,?,?,?,?,?,?,?,?)";

	static final String updateSql = "update employee set em_depart=?,em_defaultorname=?,em_position=? where em_code=?";

	static final String insertCaree = "insert into Careerapply(ca_code,ca_recordorid,ca_recordor,ca_date,"
			+ "ca_status,ca_statuscode,ca_id)values(?,?,?,?,?,?,?)";

	static final String insertCareeDetail = "insert into  Careerapplydetail(cd_detno,cd_name,cd_sex,cd_depart,"
			+ "cd_position,cd_caid,cd_id,cd_emcode,cd_hrorg)values(?,?,?,?,?,?,?,?,?)";

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private AccountCenterService accountCenterService;

	@Override
	@CacheEvict(value = { "employees", "employee" }, allEntries = true)
	public void saveEmployee(String formStore, String jobItems, String caller, Boolean JobOrgNoRelation) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean isExist = baseDao.checkIf("employee", "em_code='" + store.get("em_code") + "'");
		if (isExist) {
			BaseUtil.showError("员工编号已存在，请修改编号再保存！");
		}
		/**
		 * 问题反馈单号:2016120871 UAS标准版: 人员资料:新增一个参数设置->人员资料员工名称不允许重复 默认启用.
		 * 
		 * @author wsy
		 */
		boolean bool = baseDao.isDBSetting(caller, "EM_NAMERepeat");
		if (bool == true) {
			Object em_name = store.get("em_name");
			List<Object> codes = baseDao.getFieldDatasByCondition("Employee", "em_code", "em_name='" + em_name + "'");
			// int count = baseDao.getCountByCondition("Employee",
			// "em_name='"+em_name+"'");
			if (codes.size() > 0) {
				BaseUtil.showError("员工姓名重复！员工姓名:'" + em_name + "'已在员工编号:'" + codes.get(0) + "'中出现，请修改！");
			}
		}
		/**
		 * 问题反馈单号：2016120377 UAS标准版：人员资料-人员档案
		 * 保存更新时若手机号（em_mobile）不为空或不为0检测是否已被使用
		 * 
		 * @author wsy
		 */
		Object em_mobile = store.get("em_mobile");
		if (em_mobile != null && !"".equals(em_mobile) && !"0".equals(em_mobile)) {
			int count = baseDao.getCountByCondition("Employee", "em_mobile='" + em_mobile + "'");
			if (count > 0) {
				BaseUtil.showError("该手机号已经存在，不允许保存！");
			}
		}
		store.put("em_enid", SystemSession.getUser().getEm_enid());
		store.put("em_defaulthsname", store.get("em_position"));
		handlerService.handler("Employeemanager", "save", "before", new Object[] { store });
		List<String> sqls = new ArrayList<String>();
		Object empId = store.get("em_id");
		// 取出empsjobs
		if (jobItems != null) {
			boolean flag = false;
			List<Map<Object, Object>> items = BaseUtil.parseGridStoreToMaps(jobItems);
			for (Map<Object, Object> item : items) {
				if (item.get("or_id") == null || "".equals(item.get("or_id").toString())) {
					flag = true;
					item.put("or_id", 0);
				}
				sqls.add("insert into empsjobs(emp_id,job_id,org_id) values (" + empId + "," + item.get("jo_id") + "," + item.get("or_id")
						+ ")");
			}
			if (JobOrgNoRelation && flag) {
				BaseUtil.showError("组织信息不能为空");
			}
		}
		/**
		 * 判断em_password密码字段是否为空，
		 * 为空则设置默认111111，
		 * 然后都进行加密
		 */
		if(store.get("em_password")==null||store.get("em_password").equals("")){
			store.put("em_password", PasswordEncryUtil.encryptPassword("111111", String.valueOf(store.get("em_mobile"))));
		}else{
			store.put("em_password", PasswordEncryUtil.encryptPassword(String.valueOf(store.get("em_password")), String.valueOf(store.get("em_mobile"))));
		}
		/* 去除邮箱首尾空格 */
		if(!"".equals(String.valueOf(store.get("em_email"))) && !"null".equalsIgnoreCase(String.valueOf(store.get("em_email")))){
			store.put("em_email", String.valueOf(store.get("em_email")).trim());
		}
		
		sqls.add(SqlUtil.getInsertSqlByMap(store, "Employee"));
		sqls.add("update employee set em_enid=nvl((select max(en_id) from enterprise),0) where em_id=" + empId);
		// 更新组织ID 岗位ID
		sqls.add("update employee set em_defaulthsid=(select jo_id from job where jo_code=em_defaulthscode) where em_defaulthscode is not null and em_id="
				+ empId);
		sqls.add("update employee set em_defaultorid=(select or_id from hrorg where or_code=em_defaultorcode) where em_defaultorcode is not null and em_id="
				+ empId);
		/*
		 * 入职日期为空时，赋值为当天
		 */
		if ("".equals(store.get("em_indate"))) {
			sqls.add("update employee set em_indate=sysdate where em_id=" + empId);
		}
		baseDao.execute(sqls);
		Object[] ob = baseDao.getFieldsDataByCondition("Employee", new String[] { "em_defaulthsid", "em_defaultorid" }, "em_id=" + empId);
		/*
		 * //删除兼职岗位中与Employee岗位信息相同的记录 if (ob[0] != null &&
		 * !"".equals(ob[0].toString()))
		 * baseDao.execute("delete from empsjobs where emp_id=" + empId +
		 * " and job_id=" + ob[0]);
		 */
		// 同步输入一条记录到Contract
		insertContract(store, caller);
		// 同步将or_id和em_id插入到表hrorgemployees中
		baseDao.deleteByCondition("HrorgEmployees", "om_emid=" + store.get("em_id"));
		if (ob[1] != null && !"".equals(ob[1].toString())) {
			insertHrorgEmp(Integer.parseInt(store.get("em_id").toString()), Integer.parseInt(ob[1].toString()));
		}
		/**
		 * 贝腾商机管理新功能需求 单据编号：2017010319
		 * 
		 * @author wsy
		 */
		// 同步将jo_id和em_id插入到表hrjobemployees中
		baseDao.deleteByCondition("hrjobemployees ", "hj_em_id=" + store.get("em_id"));
		if (ob[0] != null && !"".equals(ob[0].toString()) && !"0".equals(ob[0].toString())) {
			Object or_subof = baseDao.getFieldDataByCondition("job", "jo_subof", "jo_id=" + ob[0]);
			if (or_subof != null && !"".equals(or_subof.toString()) && !"0".equals(or_subof.toString())) {
				insertHrjobEmp(Integer.parseInt(store.get("em_id").toString()), Integer.parseInt(or_subof.toString()));
			}
		}
		/*
		 * 将兼职信息插入组织人员表
		 */
		List<Object> jo_orgids = new ArrayList<Object>();
		if (JobOrgNoRelation) {
			jo_orgids = baseDao.getFieldDatasByCondition("Empsjobs", "org_id", "emp_id=" + empId);
		} else {
			jo_orgids = baseDao.getFieldDatasByCondition("job", "jo_orgid",
					"jo_id in(select job_id from empsjobs where emp_id=" + store.get("em_id") + ")");
		}
		for (Object jo_orgid : jo_orgids) {
			if (jo_orgid != null && !"".equals(jo_orgid.toString())) {
				insertHrorgEmp(Integer.parseInt(store.get("em_id").toString()), Integer.parseInt(jo_orgid.toString()));
			}
		}
		try {
			// 记录操作
			baseDao.logger.save(caller, "em_id", store.get("em_id"));
			if (BaseUtil.getXmlSetting("saas.domain") == null) {
				Object newEm_code = store.get("em_code");
				Object newEm_name = store.get("em_name");
				String b = BaseUtil.getXmlSetting("defaultSob");
				String codes = baseDao.getJdbcTemplate().queryForObject("select wm_concat(ma_user) from " + b + ".master ", String.class);
				if (codes != null) {
					String[] sobs = codes.split(",");
					for (String s : sobs) {
						baseDao.updateByCondition(s + ".employee", "em_name='" + newEm_name + "'", "em_code='" + newEm_code + "'");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	@CacheEvict(value = { "employees", "employee" }, allEntries = true)
	public void updateEmployeeById(String formStore, String jobItems, String extra, String caller, Boolean JobOrgNoRelation) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		store.remove("msg");
		/**
		 * 问题反馈单号：2017060776 UAS标准版： 人员资资料：更新时，限制人员编号不能重复
		 * 
		 * @author zyc
		 * 
		 */
		Object oldem_code = baseDao.getFieldDataByCondition("employee", "em_code", "em_id='" + store.get("em_id") + "'");
		if (oldem_code != null && !"".equals(oldem_code) && store.get("em_code") != null && !"".equals(store.get("em_code"))
				&& !oldem_code.equals(store.get("em_code"))) {

			if (baseDao.checkIf("employee", "em_code='" + store.get("em_code") + "' and em_id<>'" + store.get("em_id") + "'")) {
				BaseUtil.showError("人员编号重复,请重新输入！");
			}
		}

		/**
		 * 问题反馈单号：2016120871 UAS标准版: 人员资料:新增一个参数设置->人员资料员工名称不允许重复 默认启用.
		 * 
		 * @author wsy
		 */
		boolean bool = baseDao.isDBSetting(caller, "EM_NAMERepeat");
		if (bool == true) {
			Object em_name = store.get("em_name");
			List<Object> codes = baseDao.getFieldDatasByCondition("Employee", "em_code",
					"em_name='" + em_name + "' and em_id<>" + store.get("em_id"));
			if (codes.size() > 0) {
				BaseUtil.showError("员工姓名重复！员工姓名:'" + em_name + "'已在员工编号:'" + codes.get(0) + "'中出现，请修改！");
			}
		}
		/**
		 * 问题反馈单号：2016120377 UAS标准版：人员资料-人员档案
		 * 保存更新时若手机号（em_mobile）不为空或不为0检测是否已被使用
		 * 
		 * @author wsy
		 */
		Object em_mobile = store.get("em_mobile");
		if (em_mobile != null && !"".equals(em_mobile) && !"0".equals(em_mobile)) {
			int count = baseDao.getCount("select count(1) from employee where em_mobile='" + em_mobile + "' and em_id<>"
					+ store.get("em_id"));
			if (count > 0) {
				BaseUtil.showError("该手机号已被使用！");
			}
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		List<String> sqls = new ArrayList<String>();
		Object empId = store.get("em_id");
		// 取出empsjobs
		if (jobItems != null) {
			boolean flag = false;
			sqls.add("delete from empsjobs where emp_id=" + empId);
			List<Map<Object, Object>> items = BaseUtil.parseGridStoreToMaps(jobItems);
			for (Map<Object, Object> item : items) {
				if (item.get("or_id") == null || "".equals(item.get("or_id").toString())) {
					flag = true;
					item.put("or_id", 0);
				}
				sqls.add("insert into empsjobs(emp_id,job_id,org_id) values (" + empId + "," + item.get("jo_id") + "," + item.get("or_id")
						+ ")");
			}
			if (JobOrgNoRelation && flag) {
				BaseUtil.showError("组织信息不能为空");
			}
		}
		/* 去除邮箱首尾空格 */
		if(!"".equals(String.valueOf(store.get("em_email"))) && !"null".equalsIgnoreCase(String.valueOf(store.get("em_email")))){
			store.put("em_email", String.valueOf(store.get("em_email")).trim());
		}
		// 修改
		sqls.add(SqlUtil.getUpdateSqlByFormStore(store, "Employee", "em_id"));
		if (store.get("em_defaulthsid") != null && !"".equals(store.get("em_defaulthsid")))
			sqls.add("delete from empsjobs where emp_id=" + empId + " and job_id=" + store.get("em_defaulthsid"));
		sqls.add("update employee set em_defaulthsname=em_position where em_id=" + empId);
		if (store.get("em_indate") != null && "".equals(store.get("em_indate"))) {
			sqls.add("update employee set em_indate=sysdate where em_id=" + empId);
		}
		// 更新组织ID 岗位ID
		sqls.add("update employee set em_defaulthsid=(select jo_id from job where jo_code=em_defaulthscode) where em_defaulthscode is not null and em_id="
				+ empId);
		sqls.add("update employee set em_defaultorid=(select or_id from hrorg where or_code=em_defaultorcode) where em_defaultorcode is not null and em_id="
				+ empId);
		baseDao.execute(sqls);
		Object[] ob = baseDao.getFieldsDataByCondition("Employee", new String[] { "em_defaulthsid", "em_defaultorid" }, "em_id=" + empId);
		/*
		 * if (ob[0]!= null && !"".equals(ob[0].toString()))
		 * baseDao.execute("delete from empsjobs where emp_id=" + empId +
		 * " and job_id=" + ob[0]);
		 */
		// 同步更新到Contract
		String unique = baseDao.getDBSetting("Employee.Contract");
		if ("true".equals(unique)) {
			updateContract(store, caller);
		}
		// 同步将or_id和em_id插入到表hrorgemployees中
		baseDao.deleteByCondition("HrorgEmployees", "om_emid=" + store.get("em_id"));
		List<Object> jo_orgids = new ArrayList<Object>();
		if (ob[1] != null && !"".equals(ob[1].toString())) {
			insertHrorgEmp(Integer.parseInt(store.get("em_id").toString()), Integer.parseInt(ob[1].toString()));
		}
		if (JobOrgNoRelation) {
			jo_orgids = baseDao.getFieldDatasByCondition("Empsjobs", "org_id", "emp_id=" + empId);
		} else {
			jo_orgids = baseDao.getFieldDatasByCondition("job", "jo_orgid",
					"jo_id in(select job_id from empsjobs where emp_id=" + store.get("em_id") + ")");
		}
		for (Object jo_orgid : jo_orgids) {
			if (jo_orgid != null && jo_orgid.toString() != "") {
				insertHrorgEmp(Integer.parseInt(store.get("em_id").toString()), Integer.parseInt(jo_orgid.toString()));
			}
		}
		// 记录操作
		extra = extra == null ? "" : extra;
		baseDao.logger.update(caller, "em_id", store.get("em_id"));
		
		/**
		 * 如果手机号修改了，则重新对密码进行加密保存
		 * @author lidy
		 */
		if (em_mobile != null && !"".equals(em_mobile) && !"0".equals(em_mobile)) {
			String emId = store.get("em_id").toString();
			String em_password = employeeService.getPassword("em_id = " + emId);
			baseDao.updateByCondition("employee", "em_password='"+PasswordEncryUtil.encryptPassword(em_password, String.valueOf(em_mobile))+"'", "em_id = " + emId);
		}
		
		// 更新账户中心和b2b信息
		if ("离职".equals(store.get("em_class"))) {
			Object str = baseDao.getFieldDataByCondition("EMPLOYEE", "EM_UU", "EM_ID=" + store.get("em_id"));
			if (str != null) {
				String emUus = str.toString();
				String emId = store.get("em_id").toString();
				emUus = emUus.replaceAll("'", "");
				Master master = SystemSession.getUser().getCurrentMaster();
				Long en_uu = master.getMa_uu();
				String ma_accesssecret = master.getMa_accesssecret();
				String b2burl = null;
				if (master.getMa_b2bwebsite() == null || "".equals(master.getMa_b2bwebsite())) {
					b2burl = "http://uas.ubtob.com";
				} else {
					b2burl = master.getMa_b2bwebsite();
				}
				try {
					if (en_uu != null && en_uu > 0) {
						Employee employeeNew = employeeService.getByCondition("em_id = " + emId ,"Employee");
						if(employeeNew!=null){
							accountCenterService.unbind(employeeNew, master);
						}
						Response response = HttpUtil.sendDeleteRequest(b2burl + "/erp/account/user/" + emUus + "?access_id=" + en_uu + "",
								null, true, ma_accesssecret);
						if (response.getStatusCode() == 200) {
							baseDao.execute("update employee set em_uu=null,em_imid=null,em_b2benable=0 where em_id =" + emId);
						} else {
							BaseUtil.showError("操作失败！");
						}
					} else {
						baseDao.execute("update employee set em_uu=null,em_imid=null,em_b2benable=0 where em_id =" + emId);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// 考虑到现有的没改em_code但是已经存在同编号不同名称的。
		Object[] objs = baseDao.getFieldsDataByCondition("employee", new String[] { "em_code", "em_name" }, "em_id='" + empId + "'");
		Object newEm_code = store.get("em_code") == null ? objs[0] : store.get("em_code");
		Object newEm_name = store.get("em_name") == null ? objs[1] : store.get("em_name");
		if (BaseUtil.getXmlSetting("saas.domain") == null) {
			String b = BaseUtil.getXmlSetting("defaultSob");
			String codes = baseDao.getJdbcTemplate().queryForObject("select wm_concat(ma_user) from " + b + ".master", String.class);
			if (codes != null) {
				String[] sobs = codes.split(",");
				for (String s : sobs) {
					baseDao.updateByCondition(s + ".employee", "em_name='" + newEm_name + "'", "em_code='" + newEm_code + "'");
				}
			}
		}
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	@CacheEvict(value = { "employees", "employee" }, allEntries = true)
	public void deleteEmployee(int em_id, String caller) {
		Employee employee = employeeService.getEmployeeById(em_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { em_id });
		baseDao.delCheck("Employee", em_id);
		if(employee.getEm_uu()!=null&&employee.getEm_uu()!=0){		  //UU号不为空才进行删除云账号	
			String error = employeeService.deleteFromAccountCenter(employee);
			if (!StringUtils.isEmpty(error)) {
				BaseUtil.showError("无法删除云账户，原因:" + error);
			}
		}
		/*
		 * 录用申请转试用人员试用人员删除时，录用申请单已转试用 字段更新成 “否”
		 */
		Object[] ob = baseDao.getFieldsDataByCondition("employee", new String[] { "em_code", "nvl(em_class,'')" }, "em_id=" + em_id);
		if (ob[1] != null && "试用".equals(ob[1].toString())) {
			int count = baseDao.getCount("select count(1) from Careerapplydetail where cd_emcode='" + ob[0] + "'");
			if (count > 0) {
				baseDao.updateByCondition("Careerapplydetail", " cd_isturn=0", " cd_emcode = '" + ob[0] + "'");
			}
		}
		// 删除
		baseDao.deleteById("Employee", "em_id", em_id);
		// 记录操作
		baseDao.logger.delete(caller, "em_id", em_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { em_id });
	}

	@Override
	@CacheEvict(value = { "employees", "employee" }, allEntries = true)
	public void vastTurnOver(String caller, Integer[] id) {
		baseDao.updateByCondition("Employee", " em_class='离职'", " em_id in (" + BaseUtil.parseArray2Str(id, ",") + ")");
	}

	@Override
	@CacheEvict(value = { "employees", "employee" }, allEntries = true)
	public void vastTurnfullmemb(String caller, Integer[] id) {
		baseDao.updateByCondition("Employee", " em_class='正式'", " em_id in (" + BaseUtil.parseArray2Str(id, ",") + ")");
	}

	@Override
	public void turnFullmemb(String caller, int[] id) {
		int i = 0, idvalue;
		int turnfullId = baseDao.getSeqId("Turnfullmemb_SEQ");
		String code = baseDao.sGetMaxNumber("Turnfullmemb", 2);
		int careDetailId;
		try {
			boolean bool = baseDao.execute(
					turnFullempMain,
					new Object[] { code, SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id(),
							Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), BaseUtil.getLocalMessage("ENTERING"),
							"ENTERING", turnfullId });
			if (bool) {
				for (i = 0; i < id.length; i++) {
					idvalue = id[i];
					SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet(selectEmpl, new Object[] { idvalue });
					careDetailId = baseDao.getSeqId("Careerapplydetail_SEQ");
					while (rs.next()) {
						baseDao.execute(turnFullempDetail,
								new Object[] { i + 1, rs.getObject(1), rs.getObject(2), rs.getObject(3), rs.getObject(4), rs.getObject(5),
										rs.getObject(6), rs.getObject(7), turnfullId, careDetailId });
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转单失败");
		}
	}

	@Override
	@CacheEvict(value = { "employees", "employee" }, allEntries = true)
	public void updatePosition(String param, String caller) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		for (Map<Object, Object> map : grid) {
			baseDao.execute(updateSql,
					new Object[] { map.get("td_newdepart"), map.get("td_newhrorg"), map.get("td_newposition"), map.get("td_code") });
			baseDao.execute("update employee set em_defaulthsid=(select jo_id from job where jo_name=em_position) where nvl(em_position,' ')<>' ' and em_code='"
					+ map.get("td_code") + "'");
			baseDao.execute("update employee set em_defaultorid=(select or_id from HrOrg where or_name=em_defaultorname) where nvl(em_defaultorname,' ')<>' ' and em_code='"
					+ map.get("td_code") + "'");
		}
	}

	@Override
	public void turnCaree(String caller, int[] id) {
		String careeCode = baseDao.sGetMaxNumber("Careerapply", 2);
		int careeId = baseDao.getSeqId("Careerapply_SEQ");
		int careeDetailId = 0;
		try {
			boolean bool = baseDao.execute(
					insertCaree,
					new Object[] { careeCode, SystemSession.getUser().getEm_id(), SystemSession.getUser().getEm_name(),
							Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), BaseUtil.getLocalMessage("ENTERING"),
							"ENTERING", careeId });
			if (bool) {
				for (int i = 0; i < id.length; i++) {
					careeDetailId = id[i];
					SqlRowList rs = baseDao.queryForRowSet("select * from employee where em_id=?", new Object[] { careeDetailId });
					while (rs.next()) {
						baseDao.execute(
								insertCareeDetail,
								new Object[] { i + 1, rs.getObject("em_name"), rs.getObject("em_sex"), rs.getObject("em_depart"),
										rs.getObject("em_position"), careeId, baseDao.getSeqId("Careerapplydetail_SEQ"),
										rs.getObject("em_code"), rs.getObject("em_defaultorname") });
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转单失败");
		}
	}

	private void insertHrorgEmp(int em_id, int or_id) {
		int count = baseDao.getCountByCondition("HrorgEmployees", "om_emid=" + em_id + " and om_orid=" + or_id);
		if (count == 0) {
			baseDao.execute("insert into HrorgEmployees(om_emid,om_orid) values (" + em_id + "," + or_id + ") ");
			Object or_subof = baseDao.getFieldDataByCondition("hrorg", "or_subof", "or_id=" + or_id);
			if (or_subof != null && Integer.parseInt(or_subof.toString()) != 0) {
				insertHrorgEmp(em_id, Integer.parseInt(or_subof.toString()));
			}
		}
	}

	/**
	 * @author wsy
	 * @param em_id
	 * @param or_id
	 */
	private void insertHrjobEmp(int em_id, int jo_subof) {
		int count = baseDao.getCountByCondition("HrjobEmployees", "hj_em_id=" + em_id + " and hj_joid=" + jo_subof);
		Object[] em = baseDao.getFieldsDataByCondition("Employee", new String[]{"em_code","em_name"},"em_id="+em_id);
		if (count == 0) {
			baseDao.execute("insert into HrjobEmployees(hj_em_id,hj_joid,hj_em_code,hj_em_name) values (" + em_id + "," + jo_subof + ",'"+em[0]+"','"+em[1]+"') ");
			Object subof = baseDao.getFieldDataByCondition("job", "jo_subof", "jo_id=" + jo_subof);
			if (subof != null && Integer.parseInt(subof.toString()) != 0) {
				insertHrjobEmp(em_id, Integer.parseInt(subof.toString()));
			}
		}
	}

	private void insertContract(Map<Object, Object> store, String caller) {
		Object[] objs = baseDao.getFieldsDataByCondition("enterprise", new String[] { "en_shortname", "en_address" }, "1=1");
		StringBuffer sb = new StringBuffer();
		sb.append("insert into contract (co_id,co_code,co_title,co_depart,co_company,co_manager,co_connecter,co_phone,co_address,co_contractor,co_sex,");
		sb.append("co_card,co_conadd,co_conphone,co_conclass,co_begintime,co_endtime,co_probation,co_position,co_workaddress,co_salary,co_contratime,");
		sb.append("co_time,co_recordor,co_recordorid,co_contractorcode) values(");
		int id = baseDao.getSeqId("Contract_seq");
		sb.append(id + ",");
		sb.append("'" + baseDao.sGetMaxNumber("Contract", 2) + "',");
		sb.append("'人事合同',");// co_title
		sb.append("'" + store.get("em_depart") + "',");
		sb.append("'" + objs[0] + "',");// co_company
		sb.append("'',");// co_manager
		sb.append("'',");// co_connecter
		sb.append("'" + store.get("em_mobile") + "',");
		sb.append("'" + objs[1] + "',");// co_address是单位地址
		sb.append("'" + store.get("em_name") + "',");
		sb.append("'" + store.get("em_sex") + "',");
		sb.append("'" + store.get("em_iccode") + "',");
		sb.append("'" + store.get("em_address") + "',");
		sb.append("'" + store.get("em_tel") + "',");
		sb.append("'',");// co_conclass
		sb.append(DateUtil.parseDateToOracleString(null, store.get("em_indate") + "") + ",");
		sb.append(DateUtil.parseDateToOracleString(null, store.get("em_cancellingdate") + "") + ",");
		String co_probation = "";// 转正时间减入职时间得出试用期
		if ((store.get("em_indate") != null && !"".equals(store.get("em_indate") + ""))
				&& (store.get("em_zzdate") != null && !"".equals(store.get("em_zzdate") + ""))) {
			long indate = DateUtil.parseStringToDate(store.get("em_indate") + "", null).getTime();
			long zzdate = DateUtil.parseStringToDate(store.get("em_zzdate") + "", null).getTime();
			long d = (zzdate - indate) / (1000 * 60 * 60 * 24) + 2;// +2为了补充二月的28天
			co_probation = Integer.parseInt(d + "") / 30 + "月";
		}
		sb.append("'" + co_probation + "',");
		sb.append("'" + store.get("em_position") + "',");
		sb.append("'" + objs[1] + "',");// co_workaddress
		sb.append("'" + store.get("em_salary") + "',");
		sb.append(DateUtil.parseDateToOracleString(null, store.get("em_indate") + "") + ",");// 签约时间co_contractime
		sb.append(DateUtil.parseDateToOracleString(null, new Date()) + ",");// co_date
		sb.append("'" + SystemSession.getUser().getEm_name() + "',");
		sb.append(SystemSession.getUser().getEm_id() + ",");
		sb.append("'" + store.get("em_code") + "')");
		baseDao.execute(sb.toString());
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("msg.insert"), BaseUtil.getLocalMessage("msg.insertSuccess"), caller,
				"co_id", id);
	}

	private void updateContract(Map<Object, Object> store, String caller) {
		StringBuffer sb = new StringBuffer();
		sb.append("update Contract set co_depart='" + store.get("em_depart") + "',");
		sb.append("co_phone='" + store.get("em_mobile") + "',");
		sb.append("co_contractor='" + store.get("em_name") + "',");
		sb.append("co_sex='" + store.get("em_sex") + "',");
		sb.append("co_card='" + store.get("em_iccode") + "',");
		sb.append("co_conadd='" + store.get("em_address") + "',");
		sb.append("co_begintime=" + DateUtil.parseDateToOracleString(null, store.get("em_indate") + "") + ",");
		sb.append("co_endtime=" + DateUtil.parseDateToOracleString(null, store.get("em_cancellingdate") + "") + ",");
		String co_probation = "";// 转正时间减入职时间得出试用期
		if ((store.get("em_indate") != null && !"".equals(store.get("em_indate") + ""))
				&& (store.get("em_zzdate") != null && !"".equals(store.get("em_zzdate") + ""))) {
			long indate = DateUtil.parseStringToDate(store.get("em_indate") + "", null).getTime();
			long zzdate = DateUtil.parseStringToDate(store.get("em_zzdate") + "", null).getTime();
			long d = (zzdate - indate) / (1000 * 60 * 60 * 24) + 2;// +2为了补充二月的28天
			co_probation = Integer.parseInt(d + "") / 30 + "月";
		}
		sb.append("co_probation='" + co_probation + "',");
		sb.append("co_position='" + store.get("em_position") + "',");
		sb.append("co_salary='" + store.get("em_salary") + "',");
		sb.append("co_contratime=" + DateUtil.parseDateToOracleString(null, store.get("em_indate") + "") + ",");// 签约时间co_contractime
		sb.append("co_time=" + DateUtil.parseDateToOracleString(null, new Date()) + ",");
		sb.append("co_recordor='" + SystemSession.getUser().getEm_name() + "',");
		sb.append("co_recordorid=" + SystemSession.getUser().getEm_id());
		sb.append(" where co_contractorcode='" + store.get("em_code") + "'");
		baseDao.execute(sb.toString());
		baseDao.logger.update(caller, "em_code", store.get("em_code"));
	}

	@Override
	public String[] printUnpackApply(int id, String caller, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { id });
		// 执行打印操作
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		/*
		 * // 修改打印状态 baseDao.updateByCondition("UnpackApply",
		 * "ua_printstatuscode='PRINTED',ua_printstatus='" +
		 * BaseUtil.getLocalMessage("PRINTED") + "'", "ua_id=" + id);
		 */
		// 记录操作
		baseDao.logger.print(caller, "ua_id", id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { id });
		return keys;
	}

	@Override
	public void deleteExtraJob(int empId, int jobId, String caller) {
		baseDao.execute("delete from empsjobs where emp_id=? and job_id=?", empId, jobId);
	}

	@Override
	public List<String> searchEmployeesByKey(String keyword) {
		List<String> emps = new ArrayList<String>();
		SqlRowList rs = baseDao
				.queryForRowSet("select em_defaulthsname||'\n'||em_code||'\n'||em_name||'\n'||em_id from employee where (em_name like '%"
						+ keyword + "%' or em_code like '%" + keyword
						+ "%') and em_class<>'离职' order by NLSSORT(substr(em_name, 1, 1), 'NLS_SORT=SCHINESE_PINYIN_M'),em_code");
		while (rs.next()) {
			emps.add(rs.getString(1));
		}
		return emps;
	}

	static final String insertTurnfullmemb = "insert into Turnfullmemb(tf_code,tf_recordorid,tf_recordor,tf_date,"
			+ "tf_status,tf_statuscode,tf_id)values(?,?,?,?,?,?,?)";
	static final String turndetailSql = "insert into Turnfullmembdetail(td_detno,td_name,td_code,td_sex,"
			+ "td_position,td_depart,td_hrorg,td_date,td_auditor,td_tfid,td_id)values(?,?,?,?,?,?,?,?,?,?,?)";

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String vastTurnfullmemb(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		String log = null;
		int idvalue = 0;
		int careDetailId;
		int index = 0;
		if (maps.size() > 0) {
			int tf_id = baseDao.getSeqId("TURNFULLMEMB_SEQ");
			String code = baseDao.sGetMaxNumber("Turnfullmemb", 2);
			boolean bool = baseDao.execute(
					insertTurnfullmemb,
					new Object[] { code, SystemSession.getUser().getEm_id(), SystemSession.getUser().getEm_name(),
							Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), BaseUtil.getLocalMessage("ENTERING"),
							"ENTERING", tf_id });
			if (bool) {
				for (Map<Object, Object> map : maps) {
					idvalue = Integer.parseInt(map.get("em_id").toString());
					SqlRowList rs = baseDao.queryForRowSet("select * from employee where em_id=?", new Object[] { idvalue });
					careDetailId = baseDao.getSeqId("TURNFULLMEMBDETAIL_SEQ");
					if (rs.next()) {
						baseDao.execute(
								turndetailSql,
								new Object[] { index + 1, rs.getObject("em_name"), rs.getObject("em_code"), rs.getObject("em_sex"),
										rs.getObject("em_position"), rs.getObject("em_depart"), rs.getObject("em_defaultorname"),
										rs.getObject("em_indate"), SystemSession.getUser().getEm_name(), tf_id, careDetailId });
					}
				}
				log = "转入成功,转正申请单号:" + "<a href=\"javascript:openUrl('jsps/hr/emplmana/employee/turnfullmemb.jsp?formCondition=tf_idIS"
						+ tf_id + "&gridCondition=td_tfidIS" + tf_id + "')\">" + code + "</a>&nbsp;";
				sb.append(log).append("<hr>");
			}
		}
		return sb.toString();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String vastLZTurnZS(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		if (maps.size() > 0) {
			for (Map<Object, Object> map : maps) {
				baseDao.execute("update Employee set em_class='正式',em_indate=sysdate where em_id=?", map.get("em_id"));
				baseDao.logger.others("转正操作", "转正成功", "Employeemanager", "em_id", map.get("em_id"));
			}
		}
		return "转正成功！";
	}

	static final String insertcontract = "insert into Contract(co_contratime,CO_CODE,CO_DEPART,CO_COMPANY,"
			+ "CO_CONTRACTOR,CO_SEX,CO_CARD,CO_CONADD,co_probation,CO_CONTRACTORCODE,"
			+ "CO_CONPHONE,CO_POSITION,CO_RECORDOR,CO_RECORDORID,co_time,CO_ID,co_status,co_statuscode) "
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,'在录入','ENTERING')";

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String vastTurnContract(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		String log = null;
		int idvalue = 0;
		Object starttime = null;
		Object endtime = null;
		if (maps.size() > 0) {
			int id = baseDao.getSeqId("CONTRACT_SEQ");
			String code = baseDao.sGetMaxNumber("Contract", 2);
			for (Map<Object, Object> map : maps) {
				idvalue = Integer.parseInt(map.get("em_id").toString());
				starttime = map.get("em_startdate");
				endtime = map.get("em_cancellingdate");
				SqlRowList rs = baseDao.queryForRowSet("select * from employee where em_id=?", new Object[] { idvalue });
				if (rs.next()) {
					boolean bool = baseDao.execute(
							insertcontract,
							new Object[] { rs.getObject("em_zzdate"), code, rs.getObject("em_depart"), rs.getObject("em_cop"),
									rs.getObject("em_name"), rs.getObject("em_sex"), rs.getObject("em_iccode"), rs.getObject("em_address"),
									rs.getObject("em_shmonth"), rs.getObject("em_code"), rs.getObject("em_mobile"),
									rs.getObject("em_position"), SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id(),
									id });
					if (bool) {
						if (starttime != null) {
							baseDao.execute("update Contract set co_begintime=to_date('" + starttime
									+ "','yyyy-mm-dd hh24:mi:ss') where co_id=" + id);
						}
						if (endtime != null) {
							baseDao.execute("update Contract set co_endtime=to_date('" + endtime
									+ "','yyyy-mm-dd hh24:mi:ss') where co_id=" + id);
						}
					}
				}
				log = "转入成功,转正申请单号:" + "<a href=\"javascript:openUrl('jsps/hr/emplmana/contract/contract.jsp?formCondition=co_idIS" + id
						+ "')\">" + code + "</a>&nbsp;";
				sb.append(log).append("<hr>");
			}
		}
		return sb.toString();
	}

	@Override
	public void postEmployee(String id, String to) {
		String Fields = baseDao
				.getFieldValue(
						"FormDetail",
						"wmsys.wm_concat(fd_field)",
						"fd_foid=(select fo_id from form where fo_caller='Employeemanager') and upper(fd_field) in(select column_name from user_tab_columns where table_name='EMPLOYEE')",
						String.class);
		List<String> sqls = new ArrayList<String>();
		if (to != null) {
			String[] masters = to.split(",");
			String em_code = baseDao.getFieldValue("employee", "em_code", "em_id=" + id, String.class);
			for (String master : masters) {
				if (baseDao.checkIf(master + ".Employee", "(em_id=" + id + " and em_code<>'" + em_code + "') or (em_code='" + em_code
						+ "' and em_id<>" + id + ")"))
					BaseUtil.showError(master + "账套该员工ID、编号与当前账套不一致");
				if (baseDao.checkIf(master + ".Employee", "em_id=" + id)) {
					sqls.add("update " + master + ".Employee set (" + Fields + ") =(select " + Fields + " from Employee where em_id=" + id
							+ ") where em_id=" + id);
				} else
					sqls.add("insert into " + master + ".employee select  *  from  employee where em_id=" + id);
				// 同步明细empsjobs
				sqls.add("delete " + master + ".Empsjobs where emp_id=" + id);
				sqls.add("insert into " + master
						+ ".Empsjobs (emp_id,job_id,org_id) select emp_id,job_id,org_id from empsjobs where emp_id=" + id);
			}
		}
		baseDao.logger.others("人员资料同步到：" + to, "同步成功", "Employeemanager", "em_id", id);
		baseDao.execute(sqls);
	}

	@Override
	public String checkEmcode(String emcode, String emname) {
		if (BaseUtil.getXmlSetting("saas.domain") == null) {
			String b = BaseUtil.getXmlSetting("defaultSob");
			String codes = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(ma_user) from " + b + ".master where ma_user<>'" + b + "' ", String.class);
			if (codes != null) {
				String[] sobs = codes.split(",");
				for (String s : sobs) {
					boolean bool = baseDao.checkIf(s + ".employee", "em_code='" + emcode + "' and em_name<>'" + emname + "'");
					if (bool) {
						return "true";
					}
				}
			}
		}
		return "false";
	}
}
