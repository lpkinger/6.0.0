package com.uas.erp.controller.oa;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.JSONUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.oa.NoteService;

@Controller
public class NoteController {
	@Autowired
	private NoteService noteService;
	@Autowired
	private SingleFormItemsService singleFormItemsService;
	@Autowired
	private BaseDao baseDao;
	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 * if(caller=="SysRemind")是判断这项内容是否是重大事项提醒的           
	 * @throws ParseException 
	 */
	@RequestMapping("/oa/note/saveNote.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		if(caller!=null && caller.equals("SysRemind")){
			Map<Object, Object> store = JSONUtil.toMap(formStore);
	
			String beginTime=store.get("no_begintime").toString();
			String endTime=store.get("no_endtime").toString();
	
			SqlRowList r=baseDao.queryForRowSet("select no_title from note where"
						+"(( to_date('"+beginTime+"','yyyy-mm-dd ')>=no_begintime and to_date('"+endTime+"','yyyy-mm-dd ')<=no_endtime ) " 
						+"or (no_endtime>=to_date('"+beginTime+"','yyyy-mm-dd ') and no_endtime <=to_date('"+endTime+"','yyyy-mm-dd hh24:mi:ss')))");
			String no_title=null;		
			if(r.next()){
				no_title=r.getString("no_title");			   
			}		
			if(no_title!=null){
				BaseUtil.showError("当前时间段已存提醒("+no_title+")，请选择其他时间");
				//modelMap.put("error","当前时间段已存在提醒，请选择其他时间");
			}else{
				noteService.saveNote(formStore,caller);
				modelMap.put("success", true);
			}
		}else{
			noteService.saveNote(formStore,caller);
			modelMap.put("success", true);
		}
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/note/updateNote.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		noteService.updateNote(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/note/deleteNote.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		noteService.deleteNote(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 读通知
	 */
	@RequestMapping("/oa/note/getNote.action")
	@ResponseBody
	public Map<String, Object> getNews(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = noteService.getNote(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/oa/note/getRemindItem.action")
	@ResponseBody
	public Map<String, Object> getRemindItem(String caller,String fields,String condition,HttpSession session){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		try {
			String read=noteService.saveReadStatus();
			if(read.equals("readed")){
				modelMap.put("data","readed");
				modelMap.put("success",true);
			}else{
				modelMap.put("data",singleFormItemsService.getFieldsData("Note", "no_title,no_content,no_remindtype,no_approver,no_isrepeat,no_apptime", "NO_INFOTYPE='Major' and sysdate>=NO_BEGINTIME and TO_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd')<=NO_ENDTIME"));
				modelMap.put("success",true);
			}
			session.setAttribute("hasReminded", true);
		} catch (Exception e) {
		}
		return modelMap;
	}
}
