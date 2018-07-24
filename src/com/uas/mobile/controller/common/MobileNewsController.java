package com.uas.mobile.controller.common;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.tools.ant.taskdefs.condition.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.model.News;
import com.uas.erp.model.Note;
import com.uas.mobile.service.MobileNewsService;

@Controller
public class MobileNewsController {
	@Autowired
    private MobileNewsService mobileNewsService;	
	@ResponseBody
	@RequestMapping("/mobile/news/view.action")
	private ModelAndView openNewsViewPage(HttpSession session, Integer ne_id, Integer page, Integer pageSize) {
		page = page == null ? 1 : page;
		pageSize = pageSize == null ? 10 : pageSize;
		ModelMap map = new ModelMap();
		List<News> news = mobileNewsService.getNewsByPage(page, pageSize);
		map.put("news", news);
	/*	map.put("totalPage", Math.round(Math.ceil((double) count / (double) pageSize)));*/
		map.put("page", page);
		return new ModelAndView("/mobile/news", map);
	}
	@ResponseBody
	@RequestMapping("/mobile/news/getNewsByPage.action")
	private Map<String,Object> getNewsByPage(HttpSession session, Integer ne_id, Integer page, Integer pageSize) {
		page = page == null ? 1 : page;
		pageSize = pageSize == null ? 10 : pageSize;
		ModelMap map = new ModelMap();
		List<News> news = mobileNewsService.getNewsByPage(page, pageSize);
		map.put("news", news);
		map.put("page", page);
		return map;
	}
	@ResponseBody
	@RequestMapping("/mobile/news/newsDetail.action")
	private ModelAndView openNewsViewPage(HttpSession session, Integer ne_id) {
		ModelMap map = new ModelMap();
		News news = mobileNewsService.getNewsById(ne_id);
		map.put("content", news.getNe_content());
		return new ModelAndView("/mobile/newsDetail", map);
	}
	@RequestMapping("/mobile/note/getNotesByPage.action")
	@ResponseBody
    public Map<String,Object> getNotesByPage(HttpSession  session,Integer page,Integer pageSize){
		page = page == null ? 1 : page;
		pageSize = pageSize == null ? 10 : pageSize;
		ModelMap map = new ModelMap();
		List<Note> notes = mobileNewsService.getNotesByPage(page, pageSize);
		map.put("note", notes);
		map.put("page", page);
		return map;
	}
	@RequestMapping("/mobile/note/noteDetail.action")
	@ResponseBody
    public ModelAndView noteDetail(HttpSession  session,Integer id){
		ModelMap map = new ModelMap();
		Note note = mobileNewsService.getNoteById(id);
		map.put("content", note.getNo_content());
		return new ModelAndView("/mobile/newsDetail", map);
	}
	@RequestMapping("mobile/message/getDetail.action")
	@ResponseBody
    public ModelAndView getDetail(HttpSession  session,Integer id){		
		Map<String, Object> messge = mobileNewsService.getMessageDetailById(id);
		return new ModelAndView("/mobile/messageDetail", messge);
	}
	
}
