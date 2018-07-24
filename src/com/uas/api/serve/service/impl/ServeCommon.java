package com.uas.api.serve.service.impl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.service.common.FormAttachService;

@Service
public class ServeCommon {
	
	@Autowired
	private FormAttachService formAttachService;
	
	@Autowired 
	BaseDao baseDao;
	
	protected String getLogoUrl(String basePath, String logo) {
		
		if (StringUtil.hasText(logo)) {
			JSONArray paths = formAttachService.getFiles(logo);
			if (paths==null||paths.size()<1) {
				return "";
			}else {
				JSONObject obj = paths.getJSONObject(0);
				String path = obj.getString("fp_path");
				if (StringUtil.hasText(path)&&!(path.startsWith("http:")||path.startsWith("https:")|| path.startsWith("ftp:") || path.startsWith("sftp:"))) {
					String outUrl = baseDao.getFieldValue("Enterprise", "en_erpurl", "1=1",String.class);
					String baseUrl = "";
					if(StringUtil.hasText(outUrl)&&(outUrl.startsWith("http://")||outUrl.startsWith("https://"))){
						baseUrl = outUrl + (outUrl.endsWith("/")?"":"/");
					}else{
						baseUrl = basePath;
					}
					String sob = SpObserver.getSp();
					path = baseUrl + "common/downloadbyId.action?id="+obj.get("fp_id")+"&master="+sob;
				}
				return path;
			}
		}else{
			return "";
		}
	}
}
