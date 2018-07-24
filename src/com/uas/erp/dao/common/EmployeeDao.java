package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;
import com.uas.erp.model.EmployeeMail;
import com.uas.erp.model.Master;

public interface EmployeeDao {
	Employee getEmployeeByEmCode(String em_code);

	/**
	 * @param sob
	 *            帐套信息
	 * @return
	 */
	List<Employee> getEmployees(String sob);

	List<EmployeeMail> getEmployeeMails(int em_id);

	void saveEmployeeMail(EmployeeMail mail);

	Employee getEmployeeByEmId(long em_id);

	Employee getEmployeeByEmUu(long em_uu);

	List<Employee> getEmployeesByOrId(int or_id);

	List<Employee> getEmployeesByOrIdWithWDM(int or_id, String caller);

	Employee getEmployeeByConditon(String condition);

	List<Employee> getEmployeesByConditon(String condition);

	Master getMaster(int em_id);

	/**
	 * 按岗位查个人
	 * 
	 * @param code
	 * @return
	 */
	List<Employee> getEmployeesByJob(String jobcode);
	/**
	 * 按多个岗位变化查个人
	 * 
	 * @param code
	 * @return
	 */
	List<Employee> getEmployeesByJobs(String[] jobCodes);

	Employee getEmployeeByEmTel(String username);

	Employee getEmployeeByEmcode(String emcode);

	List<Map<String, Object>> getEmployeedata(String fields, String condition, int page, int pagesize);

	List<Employee> getEmployeesByOrgId(int orgId);

	List<Employee> getHrorgEmployeesByEmcode(String emcode);

	Employee getEmployeeByTelOrCode(String str);
	/**
	 * 查找employee人员资料的一条记录，多个字段，字段里面包含密码字段的，会进行解密。
	 * @param fields  字段：new String[] {"field1","field2",..}
	 * @param condition 查询条件
	 * @return
	 */
	Object[] getFieldsEmployeeByCondition(String[] fields, String condition);
}
