package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.PasswordEncryUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.Logger;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmployeeMail;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.Master;

@Repository("employeeDao")
public class EmployeeDaoImpl extends BaseDao implements EmployeeDao {

	@Override
	public Employee getEmployeeByEmCode(String em_code) {
		String sql = "select * from Employee where em_code=? and nvl(em_class, ' ')<>'离职'";
		try {
			Employee employee = getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Employee>(Employee.class), em_code);
			try {
				employee.setJoborgnorelation(isDBSetting("Job","JobOrgNoRelation")?1:0);
				//密码解密
				employee = PasswordEncryUtil.decryptEmployeePassword(employee);
			}catch (Exception e) {
			}
			try {
				List<EmpsJobs> empsJobs = getJdbcTemplate().query("select * from EmpsJobs where emp_id=?",
						new BeanPropertyRowMapper<EmpsJobs>(EmpsJobs.class), employee.getEm_id());
				employee.setEmpsJobs(empsJobs);
			} catch (EmptyResultDataAccessException e) {

			} catch (Exception e) {

			}
			return employee;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public Employee getEmployeeByEmId(long em_id) {
		String sql = "select * from Employee where em_id=?";
		try {
			Employee employee = getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Employee>(Employee.class), em_id);
			try {
				List<EmpsJobs> empsJobs = getJdbcTemplate().query("select * from EmpsJobs where emp_id=?",
						new BeanPropertyRowMapper<EmpsJobs>(EmpsJobs.class), em_id);
				employee.setEmpsJobs(empsJobs);
				//密码解密
				employee = PasswordEncryUtil.decryptEmployeePassword(employee);
			} catch (EmptyResultDataAccessException e) {

			} catch (Exception e) {

			}
			return employee;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public List<Employee> getEmployees(String sob) {
		try {
			List<Employee> list = getJdbcTemplate().query("SELECT * FROM employee", new BeanPropertyRowMapper<Employee>(Employee.class));
			return list;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<EmployeeMail> getEmployeeMails(int em_id) {
		try {
			return getJdbcTemplate().query("SELECT * FROM employeemail where emm_emid=?",
					new BeanPropertyRowMapper<EmployeeMail>(EmployeeMail.class), em_id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void saveEmployeeMail(EmployeeMail mail) {
		boolean bool = checkByCondition("employeemail", "emm_emid=" + mail.getEmm_emid() + " AND emm_friendgroup='经常联系人'");
		int id = 0;
		int myId = 0;
		if (bool) {
			id = getSeqId("MYEMPLOYEEMAIL");
			execute("INSERT INTO employeemail values(" + id + "," + mail.getEmm_emid() + ",'经常联系人','经常联系人','经常联系人',0)");
			myId = getSeqId("MYEMPLOYEEMAIL");
			execute("INSERT INTO employeemail values(" + myId + "," + mail.getEmm_emid() + ",'" + mail.getEmm_friendmail() + "','经常联系人','"
					+ mail.getEmm_friendmail() + "'," + id + ")");
		} else {
			bool = checkByCondition("employeemail", "emm_emid=" + mail.getEmm_emid() + " AND emm_friendmail='" + mail.getEmm_friendmail()
					+ "'");
			if (bool) {
				id = (Integer) getFieldDataByCondition("employeemail", "emm_id", "emm_friendgroup='经常联系人'");
				myId = getSeqId("MYEMPLOYEEMAIL");
				execute("INSERT INTO employeemail values(" + myId + "," + mail.getEmm_emid() + ",'" + mail.getEmm_friendmail()
						+ "','经常联系人','" + mail.getEmm_friendmail() + "'," + id + ")");
			}
		}
	}

	@Override
	public List<Employee> getEmployeesByOrId(int or_id) {
		try {
			List<Employee> list = getJdbcTemplate().query("SELECT * FROM employee  where em_defaultorid=? and nvl(em_class,' ')<>'离职'",
					new BeanPropertyRowMapper<Employee>(Employee.class), or_id);
			return list;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<Employee> getEmployeesByOrIdWithWDM(int or_id, String caller) {
		String sql = "";
		if (caller.equals("EmpWorkDateModelSet")) {
			sql = "SELECT employee.*,empworkdatemodel.em_code model_code,empworkdatemodel.em_name model_name FROM employee left join empworkdatemodel on empworkdatemodel.em_id = employee.em_wdid where em_defaultorid=? and nvl(em_cardcode,' ')<>' ' order by employee.em_id";
		} else {
			sql = "SELECT employee.*,empworkdatemodel.em_code model_code,empworkdatemodel.em_name model_name FROM employee left join empworkdatemodel on empworkdatemodel.em_id = employee.em_wdid where em_defaultorid=? order by employee.em_id";
		}
		try {
			List<Employee> list =  getJdbcTemplate()
					.query("SELECT employee.*,empworkdatemodel.em_code model_code,empworkdatemodel.em_name model_name FROM employee left join empworkdatemodel on empworkdatemodel.em_id = employee.em_wdid where em_defaultorid=? order by employee.em_id",
							new BeanPropertyRowMapper<Employee>(Employee.class), or_id);
			return list;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Employee getEmployeeByConditon(String condition) {
		String sql = "select * from Employee";
		if (condition != null)
			sql += " where rownum=1 and " + condition;
		try {
			Employee employee = getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Employee>(Employee.class));
			//密码解密
			employee = PasswordEncryUtil.decryptEmployeePassword(employee);
			return employee;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Master getMaster(int em_id) {
		Object maid = getFieldDataByCondition("Employee", "em_maid", "em_id=" + em_id);
		if (maid != null) {
			return getJdbcTemplate().queryForObject("select * from Master where ma_id=?", new BeanPropertyRowMapper<Master>(Master.class),
					maid);
		}
		return null;
	}

	@Override
	public List<Employee> getEmployeesByConditon(String condition) {
		String querySql = "SELECT * FROM employee ";
		querySql = StringUtils.hasText(condition) ? querySql + " where " + condition : querySql;
		try {
			List<Employee> list = getJdbcTemplate().query(querySql,
					new BeanPropertyRowMapper<Employee>(Employee.class));
			//密码解密
			for(Employee employee:list){
				employee = PasswordEncryUtil.decryptEmployeePassword(employee);
			}
			return list;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Employee> getEmployeesByJob(String jobcode) {
		try {
			List<Employee> list = getJdbcTemplate()
					.query("SELECT distinct em_id,em_code,em_name,em_defaulthsid FROM employee where (exists (select 1 from job where em_defaulthsid=jo_id and jo_code=?) or exists (select 1 from empsjobs,job where job_id=jo_id and em_id=emp_id and jo_code=?)) and NVL(em_class,' ')<>'离职'",
							new BeanPropertyRowMapper<Employee>(Employee.class), jobcode, jobcode);
			return list;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<Employee> getEmployeesByJobs(String[] jobCodes) {
		try {
			String codes = CollectionUtil.toSqlString(jobCodes);
			List<Employee> list = getJdbcTemplate()
					.query("SELECT distinct em_id,em_code,em_name,em_defaulthsid,em_email FROM employee where (exists (select 1 from job where em_defaulthsid=jo_id and jo_code in ("
							+ codes
							+ ")) or exists (select 1 from empsjobs,job where job_id=jo_id and em_id=emp_id and jo_code in ("
							+ codes + "))) and NVL(em_class,' ')<>'离职' "
							+ "order by case when em_code in (SELECT DISTINCT OR_HEADMANCODE FROM job LEFT JOIN hrOrg ON jo_orgid = or_id WHERE jo_code IN("+codes+")) then 0 else 1 end", 
							new BeanPropertyRowMapper<Employee>(Employee.class));
			return list;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Employee getEmployeeByEmUu(long em_uu) {
		String sql = "select * from Employee where em_uu=? and nvl(em_class, ' ')<>'离职'";
		try {
			Employee employee = getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Employee>(Employee.class), em_uu);
			//密码解密
			employee = PasswordEncryUtil.decryptEmployeePassword(employee);
			try {
				List<EmpsJobs> empsJobs = getJdbcTemplate().query("select * from EmpsJobs where emp_id=?",
						new BeanPropertyRowMapper<EmpsJobs>(EmpsJobs.class), employee.getEm_id());
				employee.setEmpsJobs(empsJobs);
			} catch (EmptyResultDataAccessException e) {

			} catch (Exception e) {

			}
			return employee;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Employee getEmployeeByEmTel(String moblie) {
		int count=getCount("select count(*) from employee where nvl(em_mobile,' ')='"+moblie+"' and nvl(em_class, ' ')<>'离职'");
		if(count>1){
			return null;
		}
		String sql = "select * from Employee where em_mobile=? and nvl(em_class, ' ')<>'离职'";
		try {
			Employee employee = getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Employee>(Employee.class), moblie);
			//密码解密
			employee = PasswordEncryUtil.decryptEmployeePassword(employee);
			try {
				employee.setJoborgnorelation(isDBSetting("Job","JobOrgNoRelation")?1:0);
			} catch (Exception e) {
			}
			try {
				List<EmpsJobs> empsJobs = getJdbcTemplate().query("select * from EmpsJobs where emp_id=?",
						new BeanPropertyRowMapper<EmpsJobs>(EmpsJobs.class), employee.getEm_id());
				employee.setEmpsJobs(empsJobs);
			} catch (EmptyResultDataAccessException e) {

			} catch (Exception e) {

			}
			return employee;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Employee getEmployeeByEmcode(String emcode) {
		String sql = "select * from Employee where em_code=? and nvl(em_class, ' ')<>'离职'";
		try {
			Employee employee = getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Employee>(Employee.class), emcode);
			//密码解密
			employee = PasswordEncryUtil.decryptEmployeePassword(employee);
			try {
				List<EmpsJobs> empsJobs = getJdbcTemplate().query("select * from EmpsJobs where emp_id=?",
						new BeanPropertyRowMapper<EmpsJobs>(EmpsJobs.class), employee.getEm_id());
				employee.setEmpsJobs(empsJobs);
			} catch (EmptyResultDataAccessException e) {

			} catch (Exception e) {

			}
			return employee;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> getEmployeedata(String fields,
			String condition, int page, int pagesize) {
		if(condition!=""){
			condition="where "+condition;
		}
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		if(page<0){
			list=getJdbcTemplate().queryForList("select "+fields+"  from employee "+condition+" order by em_id");
		}else{
			int start = ((page - 1) * pagesize + 1);
			int end = page * pagesize;
			list=getJdbcTemplate().queryForList("select "+fields+" from (select TT.*,ROWNUM rn from (select "+fields+" from employee "+condition+" order by em_id)TT where ROWNUM <="+end+") where rn>="+start);	
		}
		Iterator<Map<String, Object>> iter = list.iterator();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		while (iter.hasNext()) {
			map = iter.next();
			for(String field : fields.split(",")){
				Object value = map.get(field.toUpperCase());
				//存在密码字段，进行密码解密
				if(field.toUpperCase().equals("EM_PASSWORD")){
					value = PasswordEncryUtil.decryptPassword(String.valueOf(value));
				}
				map.remove(field.toUpperCase());
				map.put(field,value);
			}
			datas.add(map);
		}
		return datas;
	}
	
	@Override
	public List<Employee> getEmployeesByOrgId(int orgId) {
		try {
			List<Employee> list = getJdbcTemplate().query("SELECT distinct em_id,em_code,em_name,em_defaulthsid,em_email FROM employee where (em_defaultorid="+orgId+" or em_id "
								+ "in (select emp_id from empsjobs where org_id="+orgId+")) and NVL(em_class,' ')<>'离职' "
								+ "order by case when em_code in (select OR_HEADMANCODE from   hrOrg where or_id ="+orgId+") then 0 else 1 end", 
								new BeanPropertyRowMapper<Employee>(Employee.class));
			return list;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<Employee> getHrorgEmployeesByEmcode(String emcode) {
		try {
			List<Employee> list = getJdbcTemplate().query("select * from employee where em_defaultorid in (select or_id from hrorg connect by prior or_id=or_subof start with or_id=(select em_defaultorid from employee where em_code='"+emcode+"') and nvl(or_statuscode,' ') <> 'DISABLE')  and NVL(em_class,' ')<>'离职'", 
								new BeanPropertyRowMapper<Employee>(Employee.class));
			return list;
		}catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public Employee getEmployeeByTelOrCode(String str) {
		int count=getCount("select count(*) from employee where (nvl(em_mobile,' ')='"+str+"' or lower(em_code)=lower('"+str+"')) and nvl(em_class, ' ')<>'离职'");
		if(count>1){
			return null;
		}
		String sql = "select * from Employee where (em_mobile=? or lower(em_code)=lower(?)) and nvl(em_class, ' ')<>'离职'";
		try {
			Employee employee = getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Employee>(Employee.class), str,str);
			//密码解密
			employee = PasswordEncryUtil.decryptEmployeePassword(employee);
			try {
				employee.setJoborgnorelation(isDBSetting("Job","JobOrgNoRelation")?1:0);}
				catch (Exception e) {
				}
			try {
				List<EmpsJobs> empsJobs = getJdbcTemplate().query("select * from EmpsJobs where emp_id=?",
						new BeanPropertyRowMapper<EmpsJobs>(EmpsJobs.class), employee.getEm_id());
				employee.setEmpsJobs(empsJobs);
			} catch (EmptyResultDataAccessException e) {

			} catch (Exception e) {

			}
			return employee;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (BadSqlGrammarException e){
			System.out.println("Exception: "+SpObserver.getSp()+"|"+str);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object[] getFieldsEmployeeByCondition(String[] fields, String condition) {
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(BaseUtil.parseArray2Str(fields, ","));
		sql.append(" FROM ");
		sql.append("EMPLOYEE");
		sql.append(" WHERE ");
		sql.append(condition);
		List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql.toString());
		Iterator<Map<String, Object>> iter = list.iterator();
		int length = fields.length;
		Object[] results = new Object[length];
		Object value = null;
		if (iter.hasNext()) {
			Map<String, Object> m = iter.next();
			for (int i = 0; i < length; i++) {
				String upperField = fields[i].toUpperCase();
				if (upperField.indexOf(" AS ") > 0) {
					upperField = upperField.split(" AS ")[1].trim();
				}
				value = m.get(upperField);
				//密码解密
				if(upperField.equals("EM_PASSWORD")){
					value = PasswordEncryUtil.decryptPassword(String.valueOf(value));
				}
				if (value != null && value.getClass().getSimpleName().toUpperCase().equals("TIMESTAMP")) {
					Timestamp time = (Timestamp) value;
					try {
						value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				results[i] = value;
			}
			return results;
		}
		return null;
	}
}
