package com.uas.opensys.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.JNode;
import com.uas.erp.model.JProcess;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.JProcessService;
import com.uas.erp.service.common.ProcessService;

@Controller
public class OpenSysProcessController {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private JProcessService jprocessService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired 
	private ProcessService processService;
	
	@RequestMapping(value="/opensys/getCurrentJnodes.action")
	@ResponseBody 
	public Map<String,Object> getCurrentJnodes(HttpSession session,String caller,String condition){
		int keyValue = getKeyValue(caller,condition);
		Map<String,Object> map=new HashMap<String,Object>();
		List<Map<String,Object>> lists=jprocessService.SetCurrentJnodes(caller, keyValue);
		Map<String,Object>  currentmap=jprocessService.getJprocessNode(caller, keyValue,"current");
		List<JProcess> processs=processService.getJProcesssByInstanceId(String.valueOf(currentmap.get("instanceId")));
		List<String>jprocands=processService.getJProCandByByInstanceId(String.valueOf(currentmap.get("instanceId")));
		List <JNode> nodes=processService.getAllHistoryNode(String.valueOf(currentmap.get("instanceId")),null);
		map.put("data", lists);
		map.put("nodes", nodes);
		map.put("jprocands", jprocands);
		map.put("currentnode", currentmap);
		map.put("processs",processs);
		map.put("success", true);
		return  map;
	}
	
	public int getKeyValue(String caller,String condition){
		int id = 0;
		SqlRowList rs = baseDao.queryForRowSet("select fo_keyfield,fo_table from form where fo_caller='"+caller+"'");
		if(rs.next()){
			String keyField = rs.getString("fo_keyfield");
			String table = rs.getString("fo_table");
			Object obj = baseDao.getFieldDataByCondition(table, keyField, condition);
			if(obj!=null){
				id = Integer.parseInt(obj.toString());
			}
		}
		return id;
	}
}
