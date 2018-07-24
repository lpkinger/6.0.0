package com.uas.erp.controller.oa;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.model.News;
import com.uas.erp.service.oa.NewsService;

@Controller
public class NewsController {

	@Autowired
	private NewsService newsService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/news/saveNews.action")
	@ResponseBody
	public Map<String, Object> save(String caller, News news,String ne_attachs) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		newsService.saveNews(news, caller,ne_attachs);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/news/updateNews.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		newsService.updateNews(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/news/deleteNews.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		newsService.deleteNews(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 读新闻
	 */
	@RequestMapping("/oa/news/getNews.action")
	@ResponseBody
	public Map<String, Object> getNews(HttpServletRequest request,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("news", newsService.getNews(id));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 提交评论
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/news/sendComment.action")
	@ResponseBody
	public Map<String, Object> sendComment(String caller, int id, String comment) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		newsService.sendComment(id, comment);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/news/view.action")
	private ModelAndView openNewsViewPage(HttpSession session, Integer ne_id, Integer page, Integer pageSize) {
		page = page == null ? 1 : page;
		pageSize = 10;
		ModelMap map = new ModelMap();
		List<News> snapshot = newsService.getNewsSnapshot(page, pageSize);
		map.put("snapshot", snapshot);
		int count = newsService.getNewsCount();
		map.put("totalCount", count);
		map.put("totalPage", Math.round(Math.ceil((double) count / (double) pageSize)));
		map.put("page", page);
		if (ne_id == null && snapshot.size() > 0)
			ne_id = snapshot.get(0).getNe_id();
		News news = newsService.getNews(ne_id);
		if (news.getNe_attachs().size() > 0) {
			JSONArray attachs = new JSONArray();
			for (String ne_attach : news.getNe_attachs()) {
				JSONObject attach = new JSONObject();
				attach.put("fp_id", ne_attach.substring(0, ne_attach.indexOf("#")));
				attach.put("fp_name", ne_attach.substring(ne_attach.indexOf("#") + 1));
				attach.put("fp_type", ne_attach.substring(ne_attach.lastIndexOf(".") + 1).toLowerCase());
				attachs.add(attach);
			}
			map.put("attachs", attachs);
		}
		map.put("current", news);
		return new ModelAndView("/oa/news/news_view", map);
	}

	@RequestMapping("/oa/news/view/sendComment.action")
	private void sendComment(HttpSession session, @RequestParam(value = "nc_neid", required = true) Integer nc_neid,
			@RequestParam(value = "nc_comment", required = true) String nc_comment, Integer page, Integer pageSize,
			HttpServletResponse response) throws IOException {		
		newsService.sendComment(nc_neid, nc_comment);
		page = page == null ? 1 : page;
		pageSize = pageSize == null ? 10 : pageSize;
		response.sendRedirect("../view.action?ne_id=" + nc_neid + "&page=" + page + "&pageSize=" + pageSize);
	}
}
