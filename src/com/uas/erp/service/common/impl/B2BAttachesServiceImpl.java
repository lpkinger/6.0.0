package com.uas.erp.service.common.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.b2b.model.TenderAttach;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.B2BAttachsService;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.common.FormAttachService;

/**
 * @author chenrh
 *
 */
@Service
public class B2BAttachesServiceImpl implements B2BAttachsService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private FilePathService filePathService;
	
	@Autowired
	private FormAttachService formAttachService;
	
	@Override
	public String getAttaches(Set<TenderAttach> tAttachs){
		String attachs = "";
    	for (TenderAttach attach : tAttachs) {
    		String path = attach.getPath();
    		if (path!=null&&(path.startsWith("http:")&&!path.startsWith("http://dfs.ubtob.com")||path.startsWith("https:")|| path.startsWith("ftp:") || path.startsWith("sftp:"))) {
    			
    			boolean bool = false;
    			Object basePath = baseDao.getFieldDataByCondition("Enterprise", "en_erpurl", "en_id="+SystemSession.getUser().getEm_enid());
    			if(path.startsWith(basePath.toString())){
					int start = path.lastIndexOf("id=");
					if (start>0) {
	    				String fileId = path.substring(path.lastIndexOf("id=")+3);
	        			bool = baseDao.checkIf("Filepath", "fp_id="+fileId);
	        			if (bool) {
	    	    			attachs+=fileId+";";
	    				}
					}
				}else {
					List<Object> fp_id = baseDao.getFieldDatasByCondition("Filepath", "fp_id", "fp_path='"+attach.getPath()+"' AND fp_name='"+attach.getName()+"'");
					if (fp_id.size()>0) {
		    			attachs+=fp_id.get(0)+";";
		    			bool = true;
					}
				}
	    		if (!bool) {
					int Id = filePathService.saveFilePath(path, attach.getSize(), attach.getName(), (Employee) SystemSession.getUser());
					attachs+=Id+";";
				}
			}else {
				List<Object> fp_id = baseDao.getFieldDatasByCondition("Filepath", "fp_id", "fp_path='B2B://file/"+attach.getId()+"' AND fp_name='"+attach.getName()+"'");
	    		if (fp_id.size()>0) {
	    			attachs+=fp_id.get(0)+";";
				}else {
					int Id = filePathService.saveFilePath("B2B://file/"+attach.getId(), attach.getSize(), attach.getName(), (Employee) SystemSession.getUser());
					attachs+=Id+";";
				}
			}
		}
    	return attachs;
	}
	
	@Override
	public Set<TenderAttach> parseAttachs(String id){
		Object basePath = baseDao.getFieldDataByCondition("Enterprise", "en_erpurl", "en_id="+SystemSession.getUser().getEm_enid());
		Set<TenderAttach> teAttachs =  new HashSet<TenderAttach>();;
		if (id!=null&&!"".equals(id)) {
			JSONArray files = formAttachService.getFiles(id);
			for (int i=0; i<files.size(); i++) {
				JSONObject obj = files.getJSONObject(i);
				TenderAttach tAttach = new TenderAttach();
				Long Id = obj.getLong("fp_id");
				String path = obj.getString("fp_path");
				Integer size = obj.getInt("fp_size");
				String name = obj.getString("fp_name");
				if (path.startsWith("B2B://")) {// 文件在云平台
					tAttach.setId(Long.parseLong((path.substring(11))));
					tAttach.setName(name);
					tAttach.setSize(size);
				} else if (path.startsWith("http:")||path.startsWith("https:")|| path.startsWith("ftp:") || path.startsWith("sftp:")) {// 文件存放在文件系统
					tAttach.setName(name);
					tAttach.setSize(size);
					tAttach.setPath(path);
				} else {
					tAttach.setName(name);
					tAttach.setSize(size);
					if (basePath.toString().endsWith("/")) {
						tAttach.setPath(basePath+"common/downloadbyId.action?id="+Id);
					}else {
						tAttach.setPath(basePath+"/common/downloadbyId.action?id="+Id);
					}
				}
				teAttachs.add(tAttach);
			}
		}
		return teAttachs;
	}

}
