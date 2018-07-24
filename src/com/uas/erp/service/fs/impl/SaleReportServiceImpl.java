package com.uas.erp.service.fs.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fs.SaleReportService;


@Service
public class SaleReportServiceImpl implements SaleReportService{
	
	@Autowired
	private BaseDao baseDao;
	
	@Override
	public Map<String, Object> getSaleReportData(String custcode, String ordercode, String action) {
		Map<String, Object> result = new HashMap<String,Object>();
		
		SqlRowList rs = baseDao.queryForRowSet(
				"select cu_webserver,cu_whichsystem,cu_secret FROM  CustomerInfor where cu_code = ? and nvl(cu_issys,0)<>0", custcode);
		if (rs.next()) {
			String website = rs.getGeneralString("cu_webserver");
			String master = rs.getGeneralString("cu_whichsystem");
			String secret = rs.getGeneralString("cu_secret");
			if (!StringUtil.hasText(website) || !StringUtil.hasText(master)) {
				BaseUtil.showError("客户资料的网址或账套不明，无法正常取数！");
			}
			if (!StringUtil.hasText(secret)) {
				BaseUtil.showError("密钥为空，不能获取客户订单进度情况！");
			}
			Map<String, String> params = new HashMap<String, String>();

			params.put("ordercode", ordercode);
			
			try {
				Response response = HttpUtil.sendPostRequest(website+action+"?master="+master, params, true, secret);
				
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					String data = response.getResponseText();
					if (StringUtil.hasText(data)) {
						result = FlexJsonUtil.fromJson(data);
					}
				}else {
					throw new RuntimeException("连接客户账套失败," + response.getStatusCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError("错误：" + e.getMessage());
			}
		}
		
		return result;
	}
	
}
