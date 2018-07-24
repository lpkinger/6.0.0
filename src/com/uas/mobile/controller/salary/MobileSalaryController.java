package com.uas.mobile.controller.salary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.uas.erp.core.PathUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.salary.SalaryService;
import com.uas.mobile.service.MobileSalaryService;

@Controller
public class MobileSalaryController {
	
	@Autowired
	private MobileSalaryService mobileSalaryService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private FilePathService filePathService;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SalaryService salaryService;
	
	@RequestMapping("/mobile/salary/getEmSalary.action")
	@ResponseBody
	public Map<String,Object> getEmSalary(HttpSession session,String emcode,String password,String phone,
			String date,String master){
		SpObserver.putSp(master);
		Map<String,Object> salary=new HashMap<String,Object>();
		boolean f=false;
		if(password!=null)
			password=password.replace("'", "''");
		f = baseDao.checkIf("salarypassword", "sp_emcode='"+emcode+"' and sp_password='"+password+"'");
		if(f){
			salary.put("salary", mobileSalaryService.getEmSalary(emcode, date,phone));
		}else{
			 salary.put("reason", "密码或者手机号不正确!");
		}
		 salary.put("success", f);
		 return salary;
	}
	
	@RequestMapping("/mobile/salary/getSalaryByDate.action")
	@ResponseBody
	public Map<String,Object> getSalaryByDate(HttpSession session,String emcode,String phone,
			String date,String master){
		Map<String,Object> salary=new HashMap<String,Object>();
		salary.put("salary", mobileSalaryService.getEmSalary(emcode, date,phone));
		 return salary;
	}
	
	@RequestMapping("/mobile/salary/salaryWrong.action")
	@ResponseBody
	public Map<String,Object> salaryWrong(String sl_id,String emcode,String msg,boolean result,String master){
		SpObserver.putSp(master);
		Map<String,Object>model=new HashMap<String,Object>();
		Employee employee = employeeService.getEmployeeByName(emcode);
		if(employee!=null){
			msg=msg==null?"":msg.replace("'","''");
			mobileSalaryService.updateSalary(result,sl_id,0,msg,employee);
		}else{
			model.put("success",false);
			model.put("error","人员不存在!");
		}
		model.put("success",true);
		return model;
	}
	
	@RequestMapping("/mobile/salary/salaryBack.action")
	@ResponseBody
	public Map<String,Object> postBack(HttpServletRequest request,String sl_id,String emcode,boolean result,
			MultipartFile img,String master){
		SpObserver.putSp(master);
		int fp_id=0;
		Map<String,Object> model=new HashMap<String,Object>();
		Employee employee = employeeService.getEmployeeByName(emcode);
		if(employee==null){
			model.put("success",false);
			model.put("error","人员不存在!");
			return model;
		}
		if(img!=null){
			String filename = img.getOriginalFilename();
			long size = img.getSize();
			if (size > 104857600) {
				model.put("error","文件过大");
				return model;
			}
			String path=getFilePath(filename,emcode);
			File file = new File(path);
			BufferedOutputStream bos = null;
			BufferedInputStream bis = null;
			try {
				bos = new BufferedOutputStream(new FileOutputStream(file));
				bis = new BufferedInputStream(img.getInputStream());
				int c;
				while ((c = bis.read()) != -1) {
					bos.write(c);
					bos.flush();
				}
				bos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			fp_id = filePathService.saveFilePath(path, (int) size, filename,
					employee);
		}	
		mobileSalaryService.updateSalary(result,sl_id,fp_id,null,employee);
		model.put("success", true);
		return model;
	}
	
	@RequestMapping("/mobile/salary/salaryBackNoSignature.action")
	@ResponseBody
	public Map<String,Object> noSignature(HttpServletRequest request,String sl_id,String emcode,boolean result,String master){
		SpObserver.putSp(master);
		Map<String,Object> model=new HashMap<String,Object>();
		Employee employee = employeeService.getEmployeeByName(emcode);
		if(employee==null){
			model.put("success",false);
			model.put("error","人员不存在!");
			return model;
		}
		mobileSalaryService.updateSalary(result,sl_id,0,null,employee);
		model.put("success", true);
		return model;
	}
	
	/**
	 * 文件实际存放的硬盘路径
	 * 
	 * @param fileName
	 * @return
	 */
	private String getFilePath(String fileName, String em_code) {
		String uuid = UUID.randomUUID().toString().replaceAll("\\-", "");
		String suffix = fileName.indexOf(".") != -1 ? fileName.substring(
				fileName.lastIndexOf("."), fileName.length()) : "";
		String path = PathUtil.getFilePath() + "postattach";
		File file = new File(path);
		if (!file.isDirectory()) {
			file.mkdir();
			path = path + File.separator + em_code;
			new File(path).mkdir();
		} else {
			path = path + File.separator + em_code;
			file = new File(path);
			if (!file.isDirectory()) {
				file.mkdir();
			}
		}
		return path + File.separator + uuid + suffix;
	}
	
	@RequestMapping("/mobile/salary/checkPassword.action")
	@ResponseBody
	public Map<String,Object> checkPassword(String emcode,String phone,String master){
		SpObserver.putSp(master);
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("success",baseDao.checkIf("salarypassword","sp_emcode='"+emcode+"' and sp_phone='"+phone+"' and sp_statuscode='AUDITED'"));
		return map;
		}
	
	@RequestMapping("/mobile/salary/changePassword.action")
	@ResponseBody
	public Map<String,Object>changePassword(String em_uu,String emcode,String phone,String password,String master){
		SpObserver.putSp(master);
		Map<String,Object> salary=new HashMap<String,Object>();
		boolean f = mobileSalaryService.changePassword(em_uu,emcode,phone,password);
		 salary.put("success", f);
		 return salary;	
	}
	
	@RequestMapping("/mobile/salary/verificationCode.action")
	@ResponseBody
	public Map<String,Object>verificationCode(HttpSession session,String phone){
		Map<String,Object> model= salaryService.verify(phone, "login");
		return model;		
	}
	
}
