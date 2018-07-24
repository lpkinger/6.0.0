package com.uas.api.serve.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.api.serve.service.ServeMainPageService;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;

@Service
public class ServeMainPageServiceImpl extends ServeCommon implements ServeMainPageService {
	
	@Autowired
	private BaseDao baseDao;

	@Override
	public Map<String, Object> getRecyclePics(String basePath, String kind) {
		Map<String, Object> pics = new HashMap<String, Object>();
		String [] types = null;
		if (StringUtil.hasText(kind)&&"app".equals(kind)) {
			types = new String[]{"app"};
		}else{
			types = new String[]{"index","serve"};
		}
		for (String type : types) {
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			SqlRowList rs = baseDao.queryForRowSet("select * from ServeRecPic where srp_type = ? order by srp_detno",type);
			while (rs.next()) {
				Map<String, Object> pic = new HashMap<String, Object>();
				pic.put("id", rs.getGeneralInt("srp_id"));
				pic.put("name", rs.getString("srp_name"));
				String path = rs.getString("srp_url");
				if (StringUtil.hasText(path)&&!(path.startsWith("http:")||path.startsWith("https:")|| path.startsWith("ftp:") || path.startsWith("sftp:"))) {
					path = basePath + "api/serve/download.action?path="+path;
				}
				pic.put("url", path);
				pic.put("desc", rs.getString("srp_desc"));
				list.add(pic);
			}
			pics.put(type, list);
		}
		return pics;
	}
	
	@Override
	public List<Map<String, Object>> getServices(String basePath, String kind, String type) {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		
		SqlRowList rs = baseDao.queryForRowSet("SELECT ST_ID,ST_NAME,ST_DETNO,ST_TAG,ST_ICON FROM SERVICETYPE WHERE ST_KIND = ? ORDER BY ST_DETNO",kind);
		while(rs.next()){
			Map<String, Object> serveType = new HashMap<String, Object>();
			serveType.put("st_id", rs.getGeneralInt("st_id"));
			serveType.put("st_name", rs.getString("st_name"));
			serveType.put("st_tag", rs.getString("st_tag"));
			serveType.put("st_icon", getLogoUrl(basePath, rs.getString("st_icon")));
			SqlRowList rs1 = null;
			if (type!=null) {
				rs1 = baseDao.queryForRowSet("select * from SERVICE where NVL(SV_ENABLE,0)<>0 and SV_STID = ? and nvl(sv_type,'common') = ? order by SV_DETNO", rs.getGeneralInt("st_id"),type);
			}else{
				rs1 = baseDao.queryForRowSet("select * from SERVICE where NVL(SV_ENABLE,0)<>0 and SV_STID = ? order by SV_DETNO", rs.getGeneralInt("st_id"));
			}
			List<Map<String, Object>> serves = new ArrayList<Map<String,Object>>();
			while(rs1.next()){
				Map<String, Object> serve = new HashMap<String, Object>();
				serve.put("sv_id", rs1.getGeneralInt("sv_id"));
				serve.put("sv_name", rs1.getString("sv_name"));
				serve.put("sv_tag", rs1.getString("sv_tag"));
				
				Map<String, Object> logourl = new HashMap<String, Object>();
				logourl.put("mobile", getLogoUrl(basePath, rs1.getString("sv_moblogo")));
				logourl.put("platform", getLogoUrl(basePath, rs1.getString("sv_platlogo")));
				serve.put("sv_logourl", logourl);
				serve.put("sv_url", rs1.getString("sv_url"));
				serves.add(serve);
			}
			serveType.put("serves", serves);
			if (serves.size()>0) {
				result.add(serveType);
			}
		}
		return result;
	}
}
