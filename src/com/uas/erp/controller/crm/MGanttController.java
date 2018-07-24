package com.uas.erp.controller.crm;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.dao.crm.GanttSimpleDAO;
import com.uas.erp.dao.crm.SimpleDAO;
import com.uas.erp.dao.crm.TreeDAO;
/**
 * @author chenzhm
 *
 */
@Controller
public class MGanttController {
	/*@Autowired
	private MGanttService ganttService;*/
	@Autowired
	private TreeDAO treeDao;
	@Autowired
	private GanttSimpleDAO simpleDao;
	@Autowired
	private SimpleDAO sd;
	@RequestMapping(value="/market/getGantt.action")
	@ResponseBody
	public ArrayList getMTree(HttpServletResponse resp,String condition,int level) throws Exception{
		return treeDao.getTreeData(condition,level);
	}
	@RequestMapping(value="/market/gantt/updateGantt.action")
	@ResponseBody
	public String Mupdate(@RequestBody String st,String condition,int level) throws Exception{
		simpleDao.dataUpdate(st,"mprojecttask",condition,false,level);
		return  "{success:true}";
	}
	/*@RequestMapping(value="market/gantt/deleteGantt.action")
	@ResponseBody
	public Map<String,Object> deleteM(HttpServletResponse resp,String jsonData) throws Exception{
		Map<String,Object> mapModel=new HashMap<String, Object>();
		ganttService.deleteGantt(jsonData);
		mapModel.put("success", true);
		return mapModel;
	}
*/	
	@RequestMapping(value = "/market/gantt/deleteGantt.action")
	public @ResponseBody
	String projectGntDelete(@RequestBody String st,  String condition) throws Exception {
		//simpleDao.dataUpdate(st, "projecttask",true);
		treeDao.getDeleteId(st, condition);
		return  "{success:true}";
	}
	
	//对依赖关系的处理
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/market/gantt/getDependency.action")
	@ResponseBody
	public Map<String,Object> getMdependency(HttpServletResponse resp,String condition) throws Exception{
		condition="de_prjid="+ condition.split("=")[1];
		SimpleDAO dsd=new SimpleDAO();
		return sd.dataList(null, condition, "Mdependency", "id:id,From:de_from,To:de_to,LagUnit:de_lagunit,Lag:de_lag,Cls:de_cls,Type:de_type");
	}
	@RequestMapping(value="/market/gantt/updateDependency.action")
	@ResponseBody
	public String Mupdatedependency(@RequestBody String st,String condition) throws Exception{
		String subkey="de_prjid";
		String subKeyVal=condition.split("=")[1];
		SimpleDAO dsd=new SimpleDAO();
		return sd.dataUpdate(st,subkey,subKeyVal, "Mdependency", false, "id:id,From:de_from,To:de_to,Lag:de_lag,LagUnit:de_lagunit,Cls:de_cls,Type:de_type");
	
	}
	/*@RequestMapping(value="market/gantt/deleteDependency.action")
	@ResponseBody
	public Map<String ,Object> Mdependencydelete(HttpServletResponse resp,String jsonData) throws Exception{
		Map<String,Object>mapModel=new HashMap<String ,Object>();
		ganttService.deleteDependency(jsonData);
		mapModel.put("success",true );
		return mapModel;
	}*/
	@RequestMapping(value = "/market/gantt/deleteDependency.action")
	public @ResponseBody
	String dependencyDelete(@RequestBody String st,  String condition) throws Exception {
		//simpleDao.dataUpdate(st, "projecttask",true);
		SimpleDAO dsd=new SimpleDAO();
		sd.dataDelete(st, "Mdependency");
		return  "{success:true}";
	}
}
