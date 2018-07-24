package com.uas.erp.core.support;

import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Master;

public class EmployeeCreater {

	public static Employee createVirtual(String user, Enterprise enterprise, Master master) {
		Employee employee = new Employee();
		employee.setEm_id(-99999);
		employee.setEm_code(user);
		employee.setEm_name("管理员");
		employee.setEm_type("admin");
		employee.setEm_class("admin_virtual");
		if (enterprise != null)
			employee.setEm_enid(enterprise.getEn_Id());
		if (master != null) {
			employee.setCurrentMaster(master);
			employee.setEm_master(master.getMa_name());
		}

		return employee;
	}

	public static Employee createVirtual(String userUU, String user,String enUU, Enterprise enterprise, Master master,Employee emp) {
		Employee employee = new Employee();
		if(emp!=null){
			employee=emp;
		}else{
			employee.setEm_id(-99999);
			employee.setEm_code(userUU);
			employee.setEm_name(user);
			employee.setEm_type("admin");
			employee.setEm_uu(Long.parseLong(userUU));
		}				
		employee.setEm_class("customer_virtual");
		employee.setVirtual_enuu(Integer.parseInt(enUU));
		if (enterprise != null)
			employee.setEm_enid(enterprise.getEn_Id());
		if (master != null) {
			employee.setCurrentMaster(master);
			employee.setEm_master(master.getMa_name());
		}
		return employee;
	}

}
