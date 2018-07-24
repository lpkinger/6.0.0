package com.uas.erp.service.ma.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.FormAttachDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.ma.LoginImgService;

@Service
public class LoginImgServiceImpl implements LoginImgService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FormAttachDao formAttachDao;
	
	@Override
	public int save(String path, int size, String fileName, Employee employee) {
		String sob = SpObserver.getSp();
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		//保存到filepath
		int id = baseDao.getSeqId("EMAILFILEPATH");
		/**
		 * 文件名含单引号无法下载*/
		fileName=fileName.replaceAll(",", "，");
		baseDao.execute("INSERT INTO filepath(fp_id,fp_path,fp_size,fp_man,fp_date,fp_name) values(" + id + ",'" + path + "'," + size
				+ ",'" + employee.getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'" + fileName
				+ "')");
		
		boolean bool = baseDao.checkByCondition("configs", "caller='sys' and code='sys_loginimg'");
		//保存到参数配置
		if(bool){
			baseDao.getJdbcTemplate().execute("insert into configs "
					+ "(code,title,data_type,data,caller,id,editable,help) "
					+ "values "
					+ "('sys_loginimg','系统登录背景图片路径','VARCHAR2','"+path+"','sys',configs_seq.nextval,0,'系统维护管理->基础设置->登录背景设置')");
		}else{
			baseDao.updateByCondition("configs", "data='"+path+"'", "caller='sys' and code='sys_loginimg'");
		}
		
		SpObserver.putSp(sob);
		baseDao.execute("INSERT INTO MessageLog(ml_id,ml_date,ml_man,ml_content,ml_result) "
				+ " VALUES(MessageLog_seq.nextval,sysdate,'"+employee.getEm_name()+"("+employee.getEm_code()+")','登录背景修改','修改背景图片')");
		return id;
	}
	@Override
	public Map<String,Object> hasLoginImg() {
		Map<String , Object> map = new HashMap<String,Object>();
		String sob = SpObserver.getSp();
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		
		Object filepath = baseDao.getFieldDataByCondition("configs", "data", "caller='sys' and code='sys_loginimg'");
		
		SpObserver.putSp(sob);
		
		if(filepath==null||"".equals(filepath)){
			map.put("success", false);
			return map;
		}
		map.put("filepath", filepath);
		map.put("success", true);
		return map;
	}
	@Override
	public void deleteLoginImg() {
		String sob = SpObserver.getSp();
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		
		boolean bool = baseDao.checkByCondition("configs", "caller='sys' and code='sys_loginimg'");
		if(bool){
			baseDao.getJdbcTemplate().execute("insert into configs "
					+ "(code,title,data_type,data,caller,id,editable,help) "
					+ "values "
					+ "('sys_loginimg','系统登录背景图片路径','VARCHAR2','','sys',configs_seq.nextval,0,'系统维护管理->基础设置->登录背景设置')");
		}else{
			baseDao.updateByCondition("configs", "data=''", "caller='sys' and code='sys_loginimg'");
		}
		SpObserver.putSp(sob);
		Employee employee = SystemSession.getUser();
		baseDao.execute("INSERT INTO MessageLog(ml_id,ml_date,ml_man,ml_content,ml_result) "
				+ " VALUES(MessageLog_seq.nextval,sysdate,'"+employee.getEm_name()+"("+employee.getEm_code()+")','登录背景修改','恢复默认背景')");
	}
	
	
	@Override
	public void getLoginImg(HttpServletResponse response, HttpServletRequest request)  throws IOException, KeyManagementException, NoSuchAlgorithmException{
		//切换主账套
		String sob = SpObserver.getSp();
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		
		Object filepath = baseDao.getFieldDataByCondition("configs", "data", "caller='sys' and code='sys_loginimg'");
		
		SpObserver.putSp(sob);
		
		String size = "0";
		InputStream in = null;

		File file = new File(String.valueOf(filepath));
		in = new FileInputStream(file);
		size = String.valueOf(file.length());

		//切换回账套
		SpObserver.putSp(sob);
		
		OutputStream os = response.getOutputStream();
		response.addHeader("Content-Disposition", "attachment;filename="
				+ new String("532.jpg".getBytes("utf-8"), "iso-8859-1"));
		response.addHeader("Content-Length", size);
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/octec-stream");
		int data = 0;
		while ((data = in.read()) != -1) {
			os.write(data);
		}
		in.close();
		os.close();
	}

}
