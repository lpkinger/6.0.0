package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FileUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.FileUploadTempDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUploadTemp;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.common.FileUploadTempService;

@Service
public class FileUploadTempServiceImpl implements FileUploadTempService {
	
	@Autowired
	private FilePathService filePathService;
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private FileUploadTempDao fileUploadTempDao;
	
	@Override
	public List<FileUploadTemp> getGridData(int id) {
		return fileUploadTempDao.getFileUploadById(id);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void doMatchData(String datatype, int id) {
		String out = baseDao.callProcedure("SP_UPLOADFILEMATCH", new Object[]{datatype,id});
		//baseDao.procedure("SP_UPLOADFILEMATCH", new Object[]{datatype,id});
		if(out!=null) {
			BaseUtil.showError(out);
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public int putGridData(String data) {
		List<Map<Object, Object>> filenamesMaps = BaseUtil.parseGridStoreToMaps(data);
		int id = baseDao.getSeqId("FILEUPLOADTEMP_SEQ");
		for (Map<Object, Object> map : filenamesMaps) {
			baseDao.execute(SqlUtil.getInsertSqlByMap(map, "FILEUPLOADTEMP",
					new String[] {"fl_id"},new Object[] {id}));
		}
		return id;
	}

	
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void updatefileUploadTemp(String update, String condition) {
		baseDao.updateByCondition("FILEUPLOADTEMP",update, condition);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void updateDataAttach(String datatype, int code,String filename,Object matchcode) {
		//物料资料
		if ("product".equals(datatype)) {
			Object attach = baseDao.getFieldDataByCondition("product", "pr_attach", "pr_code = '"+matchcode.toString()+"'");
			if (attach==null) {
				//没有附件，直接添加
				baseDao.updateByCondition("product", "pr_attach = '"+code+";'", "pr_code = '"+matchcode.toString()+"'");
			}else {
				//判断是否重复文件,将其替换
				String str = attach.toString();
				String[] arr = str.split(";");
				Boolean isSame = false;
				for (int i = 0; i < arr.length; i++) {
					Object name = baseDao.getFieldDataByCondition("filepath", "fp_name", "fp_id = "+arr[i]);
					if (toUpper(filename).equals(toUpper(name.toString()))) {
						//执行替换
						arr[i] = String.valueOf(code);
						isSame = true;
						break;
					}
				}
				if (isSame) {
					//有重复文件执行替换
					str = StringUtils.join(arr,";")+";";
					baseDao.updateByCondition("product", "pr_attach = '"+str+"'", "pr_code = '"+matchcode.toString()+"'");
				}else {
					//无重复文件执行追加
					str = str + code+";";
					baseDao.updateByCondition("product", "pr_attach = '"+str+"'", "pr_code = '"+matchcode.toString()+"'");
				}
			}
		}
		
	}
	
	public  String toUpper(String filename){
		String name = filename.substring(0, filename.lastIndexOf(".")+1);
		String suffix = filename.substring(filename.lastIndexOf(".")+1, filename.length()).toUpperCase();
		return name+suffix;
	}
	
	@Override
	public Boolean uploadMulti(HttpSession session, MultipartFile file, String em_code,int id,
			String datatype) {
		String condition = "fl_name="+"'"+file.getOriginalFilename()+"'"+" AND "+"fl_id="+id;
		try {
			if (file.getSize() > 104857600) {
				updatefileUploadTemp("fl_uploadres='上传失败' , fl_uploadstatus=0", condition);
				return false;
			}else {
				String path = FileUtil.saveFile(file, em_code);
				int code = filePathService.saveFilePath(path, (int) file.getSize(), file.getOriginalFilename(), (Employee) session.getAttribute("employee"));
				Object matchcode = baseDao.getFieldDataByCondition("fileuploadtemp", "fl_matchcode", condition);
				Object pr_id = baseDao.getFieldDataByCondition("product", "pr_id", "pr_code = '" + matchcode + "'");
				Object em_name = baseDao.getFieldDataByCondition("employee", "em_name", "em_code = '" + em_code + "'");
				//更新到对应单据的附件字段
				updateDataAttach(datatype, code, file.getOriginalFilename(),matchcode);
				//记录附件添加操作到日志里
				baseDao.logMessage(new MessageLog(em_name.toString(),"添加附件操作,附件名为:"
				+ baseDao.getFieldDataByCondition("filepath", "fp_name", "fp_id=" + code), "添加附件成功", "Product|pr_id="+pr_id));	
				//更新临时上传表记录的状态
				updatefileUploadTemp("fl_uploadres='上传成功' , fl_uploadstatus=1", condition);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			updatefileUploadTemp("fl_uploadres='上传失败' , fl_uploadstatus=0", condition);
			return false;
		}
		return true;
	}

	public List<Map<String, Object>> uploadFiles(HttpSession session,MultipartFile[] files, String em_code,String caller) {
		Map<String, Object> map = null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		long size;
		String path = "";
		int code;
		for (int i = 0; i < files.length; i++) {
			map = new HashMap<String, Object>();
			try {
				size = files[i].getSize();
				path = FileUtil.saveFile(files[i], em_code);
				code = filePathService.saveFilePath(path, (int) files[i].getSize(), 
						files[i].getOriginalFilename(), (Employee) session.getAttribute("employee"));
				map.put("filepath", code);
				map.put("size", size);
				map.put("path", path);
				map.put("success", true);
			} catch (Exception e) {
				// TODO: handle exception
				map.put("success", false);
			}
			list.add(map);
		}
		return list;
	}
}
