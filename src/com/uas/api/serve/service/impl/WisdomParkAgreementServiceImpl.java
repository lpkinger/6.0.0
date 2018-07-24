package com.uas.api.serve.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.api.serve.service.WisdomParkAgreementService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;

@Service
public class WisdomParkAgreementServiceImpl extends ServeCommon implements WisdomParkAgreementService{
	
	@Autowired 
	BaseDao baseDao;
	
	@Override
	public String getAgreementContent(HttpServletRequest request, String type) {
		String outUrl = baseDao.getFieldValue("Enterprise", "en_erpurl", "1=1",String.class);
		String rootUrl = "";
		if(StringUtil.hasText(outUrl)&&(outUrl.startsWith("http://")||outUrl.startsWith("https://"))){
			rootUrl = outUrl + (outUrl.endsWith("/")?"":"/");
		}else{
			rootUrl = BaseUtil.getBasePath(request);
		}
		
		String content = baseDao.queryForObject("select replace(ag_content,'=\"/public/download.action?','=\""+rootUrl+"public/download.action?') ag_content from Agreement where ag_type = ? and nvl(ag_status,'草稿箱') = '已发布'", String.class, type);
		
		return content;
	}

}
