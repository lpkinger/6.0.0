package com.uas.pdaio.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.PersonalPower;
import com.uas.erp.model.PositionPower;
import com.uas.pdaio.service.PdaioPowerDao;

@Repository("pdaioPowerDaoImpl")
public class PdaioPowerDaoImpl implements PdaioPowerDao {
	
	@Autowired
	private PowerDao powerDao;

	@Override
	public boolean preSaveHandle(String caller)  {
		Employee employee = SystemSession.getUser();
		if(employee == null){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "您已断开连接,请重新登录");
		}
		if (caller != null && employee != null ) {
			//基础资料维护设置账套权限
			for(String powerCallers:powerDao.getUnEditableCallers(SpObserver.getSp())){		
				for(String powerCaller:powerCallers.split(",")){						
					boolean bool = caller.equals(powerCaller);
					if(bool){
						BaseUtil.showError("ERR_POWER_301:当前账套没有<新增>该单据的权限!");
					}
				}
			}
			if (!"admin".equals(employee.getEm_type())) {
				boolean bool = checkJobSavePower(caller, employee);
				if (!bool) {
					// 查看是否有个人权限
					bool = powerDao.getSelfPowerByType(caller, PersonalPower.ADD, employee);
					if (!bool) {
						BaseUtil.showError("ERR_POWER_001:您没有<新增>单据的权限!");
					}
				} else {
					return true;
				}
			}
		}
		return true;
	}

	private boolean checkJobSavePower(String caller, Employee employee) {
		String sob = employee.getEm_master();
		// 默认岗位设置
		boolean bool = powerDao.getPowerByType(caller, PositionPower.ADD, sob, employee.getEm_defaulthsid());
		if (!bool && employee.getEmpsJobs() != null) {
			// 按员工岗位关系取查找权限
			for (EmpsJobs empsJob : employee.getEmpsJobs()) {
				bool = powerDao.getPowerByType(caller, PositionPower.ADD, sob, empsJob.getJob_id());
				if (bool)
					break;
			}
		}
		return bool;
	}

	@Override
	public boolean preDeleteHandle(String caller,Integer id) {
		Employee employee = SystemSession.getUser();
		if(employee == null){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "您已断开连接,请重新登录");
		}
		if (caller != null && employee != null && !"admin".equals(employee.getEm_type())) {
			boolean bool = checkJobDelPower(caller, PositionPower.DELETE, employee);
			if (!bool) {
				bool = powerDao.getSelfPowerByType(caller, PersonalPower.DELETE, employee);// 个人权限
				if (!bool) {
					BaseUtil.showError("ERR_POWER_007:您没有<删除>该单据的权限!");
				} else {
					if (id != null) {
						bool = powerDao.getOtherSelfPowerByType(caller, Integer.parseInt(id.toString()), PersonalPower.DELETE_OTHER,
								employee);
						if (!bool) {
							BaseUtil.showError("ERR_POWER_007:您没有<删除他人>单据的权限!");
						} else {
							return true;
						}
					}
				}
			} else {
				if (id != null) {
					bool = powerDao.getOtherPowerByType(caller, Integer.parseInt(id.toString()), PositionPower.DELETE_OTHER, employee);
					if (!bool) {
						bool = powerDao.getOtherSelfPowerByType(caller, Integer.parseInt(id.toString()), PersonalPower.DELETE_OTHER,
								employee);
						if (!bool) {
							BaseUtil.showError("ERR_POWER_007:您没有<删除他人>单据的权限!");
						} else {
							return true;
						}
					} else {
						return true;
					}
				}
				return true;
			}
		}
		return true;
	
	}	
	
	private boolean checkJobDelPower(String caller, String powerType, Employee employee) {
		String sob = employee.getEm_master();
		// 默认岗位设置
		boolean bool = powerDao.getPowerByType(caller, powerType, sob, employee.getEm_defaulthsid());
		if (!bool && employee.getEmpsJobs() != null) {
			// 按员工岗位关系取查找权限
			for (EmpsJobs empsJob : employee.getEmpsJobs()) {
				bool = powerDao.getPowerByType(caller, powerType, sob, empsJob.getJob_id());
				if (bool)
					break;
			}
		}
		return bool;
	}

	@Override
	public boolean preChangeHandle(String caller, Integer id) {
		System.out.println(caller);
		Employee employee = SystemSession.getUser();
		if(employee == null){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "您已断开连接,请重新登录");
		}
		if (caller != null && employee != null && !"admin".equals(employee.getEm_type())) {
			boolean bool = checkJobChangePower(caller, PositionPower.END, employee);
			if (!bool) {
				bool = powerDao.getSelfPowerByType(caller, PersonalPower.END, employee);// 个人权限
				if (!bool) {
					BaseUtil.showError("ERR_POWER_008:您没有<操作>该单据的权限!");
				} else {
					if (id != null) {
						bool = powerDao.getOtherSelfPowerByType(caller, id, PersonalPower.END_OTHER, employee);
						if (!bool) {
							BaseUtil.showError("ERR_POWER_009:您没有<操作他人>单据的权限!");
						} else {
							return true;
						}
					}
				}
			} else {
				if (id != null) {
					bool = powerDao.getOtherPowerByType(caller, id, PositionPower.END_OTHER, employee);
					if (!bool) {
						bool = powerDao.getOtherSelfPowerByType(caller, id, PersonalPower.END_OTHER, employee);
						if (!bool) {
							BaseUtil.showError("ERR_POWER_009:您没有<操作他人>单据的权限!");
						} else {
							return true;
						}
					} else {
						return true;
					}
				}
				return true;
			}
		}
		return true;
	}
	
	private boolean checkJobChangePower(String caller, String powerType, Employee employee) {
		String sob = employee.getEm_master();
		// 默认岗位设置
		boolean bool = powerDao.getPowerByType(caller, powerType, sob, employee.getEm_defaulthsid());
		if (!bool && employee.getEmpsJobs() != null) {
			// 按员工岗位关系取查找权限
			for (EmpsJobs empsJob : employee.getEmpsJobs()) {
				bool = powerDao.getPowerByType(caller, powerType, sob, empsJob.getJob_id());
				if (bool)
					break;
			}
		}
		return bool;
	}

	@Override
	public boolean preSeeAllHandle(String caller) {
		Employee employee = SystemSession.getUser();
		if(employee == null){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "您已断开连接,请重新登录");
		}
		if (caller != null && employee != null && !"admin".equals(employee.getEm_type())) {
			// 先看是否有查看所有的权限
			boolean bool = checkJobSeeAllPower(caller, PositionPower.ALL_LIST, employee);// 岗位权限表--all
			if (!bool) {
				bool = checkJobEmployeePower(caller,PersonalPower.JOBEMPLOYEE_LIST,employee);
						bool = powerDao.getSelfPowerByType(caller, PersonalPower.ALL_LIST, employee);// 个人权限表--all
						if (!bool) {
							// 是否可以查看录入人为自己的数据
							bool = checkJobSeeAllPower(caller, PositionPower.SELF_LIST, employee);// 岗位权限表--self
							if (!bool) {
								bool = powerDao.getSelfPowerByType(caller, PersonalPower.SELF_LIST, employee);// 个人权限表--self
								if (!bool) {
									/*BaseUtil.showError("ERR_POWER_025:您没有执行查看列表的权限!");*/
									return false;
								}
							}
						}									
			}
		}
		return true;
	}
	
	private boolean checkJobSeeAllPower(String caller, String powerType, Employee employee) {
		String sob = employee.getEm_master();
		// 默认岗位设置
		boolean bool = powerDao.getPowerByType(caller, powerType, sob, employee.getEm_defaulthsid());
		if (!bool && employee.getEmpsJobs() != null) {
			// 按员工岗位关系取查找权限
			for (EmpsJobs empsJob : employee.getEmpsJobs()) {
				bool = powerDao.getPowerByType(caller, powerType, sob, empsJob.getJob_id());
				if (bool)
					break;
			}
		}
		return bool;
	}
	
	//检查是否有"浏览岗位下属"权限
	private boolean checkJobEmployeePower(String caller, String powerType, Employee employee) {
		String sob = employee.getEm_master();
		Integer jobId = employee.getEm_defaulthsid();
		boolean jobEmployeeExists = powerDao.checkJobEmployeeExists(jobId);
		boolean bool = false;
		if(!jobEmployeeExists){
			for (EmpsJobs empsJob : employee.getEmpsJobs()) {
				jobEmployeeExists = powerDao.checkJobEmployeeExists(empsJob.getJob_id());
				if (jobEmployeeExists)
					break;
			}			
		}
		if(!jobEmployeeExists){
			return false;
		}else{
			// 默认岗位设置
			bool = powerDao.getJobEmployeePowerByType(caller,powerType,sob,employee.getEm_defaulthsid());
			if (!bool && employee.getEmpsJobs() != null) {
				// 按员工岗位关系取查找权限
				for (EmpsJobs empsJob : employee.getEmpsJobs()) {
					bool = powerDao.getJobEmployeePowerByType(caller,powerType,sob,empsJob.getJob_id());
					if (bool)
						break;
				}
			}			
			if(!bool){
				//检查个人权限
				bool = powerDao.getJobEmployeePowerByType(caller, PersonalPower.JOBEMPLOYEE_LIST, employee.getEm_master(), employee);
			}			
		}
		return bool;
	}
}
