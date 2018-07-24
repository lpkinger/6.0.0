package com.uas.api.serve.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.api.serve.service.WisdomParkNewsService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;

@Service
public class WisdomParkNewsServiceImpl extends ServeCommon implements WisdomParkNewsService{
	
	@Autowired 
	BaseDao baseDao;
	
	@Override
	public List<Map<String, Object>> getNewsType(String basePath) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select nt_id,nt_name,nt_desc,nt_count,nt_icon from NewsType");
		while (rs.next()) {
			Map<String, Object> news = new HashMap<String, Object>();
			news.put("nt_id", rs.getGeneralInt("nt_id"));
			news.put("nt_name", rs.getGeneralString("nt_name"));
			news.put("nt_desc", rs.getGeneralString("nt_desc"));
			news.put("nt_count", rs.getGeneralLong("nt_count"));
			
			String icon = rs.getGeneralString("nt_icon");
			String[] icons = icon.split(";");
			if(icons.length>0){
				news.put("nt_icon", getLogoUrl(basePath, icons[0]));
				news.put("nt_activeicon", getLogoUrl(basePath, icons[icons.length-1]));
			}
			
			list.add(news);
		}
		return list;
	}


	@Override
	public List<Map<String, Object>> getNewslist(String basePath, String type, Integer limit, Integer page) {

		String sql = "select * from (select T.*,rownum rn from (select nc_id,nc_title,nc_desc,nc_author,nc_type,nc_image,nc_readnum,nc_publishdate from NewsCenter where nvl(nc_status,'草稿箱') = '已发布'" + (StringUtil.hasText(type)?" and nc_type = '"+type+"'":"")+" order by nc_publishdate desc) T) where rn <=? and rn >?";
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		int start = limit*(page-1),end = limit*page;
		SqlRowList rs = baseDao.queryForRowSet(sql, end, start);
		while (rs.next()) {
			Map<String, Object> news = new HashMap<String, Object>();
			news.put("nc_id", rs.getGeneralInt("nc_id"));
			news.put("nc_title", rs.getGeneralString("nc_title"));
			news.put("nc_desc", rs.getGeneralString("nc_desc"));
			news.put("nc_author", rs.getGeneralString("nc_author"));
			news.put("nc_type", rs.getGeneralString("nc_type"));
			news.put("nc_image", getLogoUrl(basePath, rs.getGeneralString("nc_image")));
			news.put("nc_readnum", rs.getGeneralInt("nc_readnum"));
			news.put("nc_publishdate", rs.getDate("nc_publishdate"));
			list.add(news);
		}
		return list;
	}


	@Override
	public Map<String, Object> getNewsContent(HttpServletRequest request, Integer id) {
		baseDao.updateByCondition("NewsCenter", "nc_readnum = nvl(nc_readnum,0)+1", "nc_id = "+id);
		Map<String, Object> news = new HashMap<String, Object>();
		String outUrl = baseDao.getFieldValue("Enterprise", "en_erpurl", "1=1",String.class);
		String rootUrl = "";
		if(StringUtil.hasText(outUrl)&&(outUrl.startsWith("http://")||outUrl.startsWith("https://"))){
			rootUrl = outUrl + (outUrl.endsWith("/")?"":"/");
		}else{
			rootUrl = BaseUtil.getBasePath(request);
		}
		
		List<Map<String, Object>> list = baseDao.queryForList("select nc_id,nc_title,nc_desc,replace(nc_content,'=\"/public/download.action?','=\""+rootUrl+"public/download.action?') nc_content,nc_author,nc_type,nc_readnum,nc_publishdate from NewsCenter where nc_id = ?", id);
		if(!CollectionUtil.isEmpty(list)){
			news = list.get(0);
			return news;
		}
		return null;
	}


	@Override
	public Integer getNewsTotal(String type) {
		// TODO Auto-generated method stub
		return baseDao.getCountByCondition("NewsCenter", "nvl(nc_status,'草稿箱') = '已发布'" + (StringUtil.hasText(type)?" and nc_type = '"+type+"'":""));
	}

}
