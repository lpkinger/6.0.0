package com.uas.erp.controller.ac;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.uas.erp.ac.service.common.InvitationRecordService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.web.ExcelViewUtils;
import com.uas.erp.model.Employee;


/**
 * 获取平台的邀请记录
 * 
 * @author hejq
 * @time 创建时间：2017年6月7日
 */
@RestController
public class InvitationRecordController {

	@Autowired
	InvitationRecordService invitationRecordService;

	/**
	 * 获取平台邀请记录
	 */
	@RequestMapping("ac/invitations.action")
	@ResponseBody
	public Map<String, Object> invitations(String keyword, Integer start, Integer page, Integer limit,int value)
			throws Exception {
		return invitationRecordService.invitations(keyword, start, page, limit,value);
	}
	/**
	 * 获取平台个人邀请记录
	 */
	@RequestMapping("ac/invitationsRecord.action")
	@ResponseBody
	public Map<String, Object> invitationsRecord(Integer start, Integer page, Integer limit,String _state,String keyword){
		return invitationRecordService.getInvitationsRecord(start, page, limit,_state,keyword);
	}
	/**
	 * 获取统计数据
	 */
	@RequestMapping("ac/invitationCount.action")
	@ResponseBody
	public Map<String, Object> invitationCount(){
		return invitationRecordService.getInvitationCount();
	}
	/**
	 * 生成excel导出
	 * 
	 * @throws IOException
	 */
	@RequestMapping("ac/excel/exportInviteGrid.xls")
	public ModelAndView exportInviteGrid(HttpSession session, HttpServletResponse response, HttpServletRequest request,
			String id,String columns,String title,int dataCount) throws IOException {
		if(dataCount == 0){
			Map<String, Object> allCount = invitationRecordService.getInvitationCount();
			Map<Object, Object> count = BaseUtil.parseFormStoreToMap(allCount.get("data").toString());
			dataCount = Integer.valueOf(count.get(id).toString());
		}
		Map<String, Object>  map = invitationRecordService.getInvitationsRecord(0, 1, dataCount,id,"");
		String datas = JSONArray.toJSONString(map.get("data"));
		columns = new String(columns.getBytes("ISO-8859-1"), "UTF-8");
		title = title + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		Employee employee = (Employee) session.getAttribute("employee");
		List<Map<String, Object>> colList = FlexJsonUtil.fromJsonArray(columns, HashMap.class);
		List<Map<String, Object>> dataList = FlexJsonUtil.fromJsonArray(datas, HashMap.class);
		return new ModelAndView(ExcelViewUtils.getView(colList, dataList, title, employee));
	}
}
