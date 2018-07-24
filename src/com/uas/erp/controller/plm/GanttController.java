package com.uas.erp.controller.plm;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpp.MPPReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.FileUtil;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.plm.GanttService;

@Controller
public class GanttController {
	@Autowired
	private GanttService ganttService;
	@Autowired
	private FilePathService filePathService;
	@RequestMapping(value = "plm/gantt.action")
	@ResponseBody
	public List<JSONObject> getTree(HttpServletResponse resp, String condition, String Live) throws Exception {
		try {
			return ganttService.getJsonGantt(condition, Live);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@RequestMapping(value = "plm/gantt/sync.action")
	@ResponseBody
	public Map<String, Object> syncTask(String Taskcreate,String Taskupdate,String Taskremove,String Assigncreate,String Assignupdate,String Assignremove,String Dependencycreate,String Dependencyupdate,String Dependencyremove,String detnos,int prjId) throws Exception {
		Map<String, Object> mapModel = new HashMap<String, Object>();
		ganttService.sync(Taskcreate,Taskupdate,Taskremove,Assigncreate,Assignupdate,Assignremove,Dependencycreate,Dependencyupdate,Dependencyremove,detnos,prjId);
		mapModel.put("success", true);
		return mapModel;
	}
/*	@RequestMapping(value = "plm/gantt/syncTask.action")
	@ResponseBody
	public Map<String, Object> syncTask(HttpServletResponse response, String create,String update,String remove,int prjId) throws Exception {
		Map<String, Object> mapModel = new HashMap<String, Object>();
		ganttService.syncTask(create,update,remove,prjId);
		mapModel.put("success", true);
		return mapModel;
	}*/
/*	@RequestMapping(value = "plm/gantt/syncDependency.action")
	@ResponseBody
	public Map<String, Object> syncDependency(HttpServletResponse response, String create,String update,String remove,int prjId) throws Exception {
		Map<String, Object> mapModel = new HashMap<String, Object>();
		ganttService.syncDependency(create,update,remove,prjId);
		mapModel.put("success", true);
		return mapModel;
	}
	@RequestMapping(value = "plm/gantt/syncAssigns.action")
	@ResponseBody
	public Map<String, Object> syncAssigns(HttpServletResponse response, String create,String update,String remove,int prjId) throws Exception {
		Map<String, Object> mapModel = new HashMap<String, Object>();
		ganttService.syncAssigns(create,update,remove,prjId);
		mapModel.put("success", true);
		return mapModel;
	}*/
	@RequestMapping(value = "plm/gantt/activeTask.action")
	@ResponseBody
	public Map<String, Object> activeTask(HttpServletResponse response, String data, int prjId) throws Exception {
		Map<String, Object> mapModel = new HashMap<String, Object>();
		ganttService.activeTask(data,prjId);
		mapModel.put("success", true);
		return mapModel;
	}
	
	@RequestMapping(value = "plm/gantt/endTask.action")
	@ResponseBody
	public Map<String, Object> endTask(HttpServletResponse response, int id, int prjId) throws Exception {
		Map<String, Object> mapModel = new HashMap<String, Object>();
		ganttService.endTask(id,prjId);
		mapModel.put("success", true);
		return mapModel;
	}
	
	@RequestMapping(value = "plm/gantt/ganttupdate.action")
	@ResponseBody
	public Map<String, Object> update(HttpServletResponse resp, String jsonData) throws Exception {
		Map<String, Object> mapModel = new HashMap<String, Object>();
		ganttService.updateGantt(jsonData);
		mapModel.put("success", true);
		return mapModel;
	}


	@RequestMapping(value = "plm/gantt/ganttcreate.action")
	@ResponseBody
	public Map<String, Object> create(HttpServletResponse resp, HttpServletRequest resq, String jsonData)
			throws Exception {
		Map<String, Object> mapModel = new HashMap<String, Object>();
		ganttService.saveGantt(jsonData);
		mapModel.put("success", true);
		return mapModel;
	}

	@RequestMapping(value = "plm/gantt/getData.action")
	@ResponseBody
	public Map<String, Object> getData(HttpSession session, HttpServletResponse resp, String condition)
			throws Exception {
		Map<String, Object> mapModel = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		mapModel.put("data", ganttService.getData(condition, employee));
		mapModel.put("success", true);
		return mapModel;
	}

	@RequestMapping(value = "plm/gantt/ganttdelete.action")
	@ResponseBody
	public Map<String, Object> delete(HttpServletResponse resp, String jsonData) throws Exception {
		Map<String, Object> mapModel = new HashMap<String, Object>();
		ganttService.deleteGantt(jsonData);
		mapModel.put("success", true);
		return mapModel;
	}

	// 对依赖关系的处理
	@RequestMapping(value = "plm/gantt/getdependency.action")
	@ResponseBody
	public Map<String, Object> getdependency(HttpServletResponse resp, String condition) throws Exception {
		String prjid = condition.split("=")[1];
		Map<String, Object> mapModel = new HashMap<String, Object>();
		mapModel.put("dependency", ganttService.getDependencies(prjid));
		mapModel.put("success", true);
		return mapModel;
	}

	@RequestMapping(value = "plm/gantt/dependencycreate.action")
	@ResponseBody
	public Map<String, Object> dependencycreate(HttpServletResponse resp, String jsonData, String condition)
			throws Exception {
		Map<String, Object> mapModel = new HashMap<String, Object>();
		ganttService.saveDependency(jsonData, condition);
		mapModel.put("success", true);
		return mapModel;
	}

	@RequestMapping(value = "plm/gantt/dependencyupdate.action")
	@ResponseBody
	public Map<String, Object> dependencyupdate(HttpServletResponse resp, String jsonData) throws Exception {
		Map<String, Object> mapModel = new HashMap<String, Object>();
		ganttService.updateDependency(jsonData);
		mapModel.put("success", true);
		return mapModel;

	}

	@RequestMapping(value = "plm/gantt/dependencydelete.action")
	@ResponseBody
	public Map<String, Object> dependencydelete(HttpServletResponse resp, String jsonData) throws Exception {
		Map<String, Object> mapModel = new HashMap<String, Object>();
		ganttService.deleteDependency(jsonData);
		mapModel.put("success", true);
		return mapModel;
	}
	@RequestMapping("plm/gantt/ImportMpp.action")
	@ResponseBody	
	public String ImportMpp(HttpSession session, int prjId,FileUpload uploadItem) {
		InputStream is = null;
		MPPReader reader = null;
		ProjectFile pf = null;
		Employee employee=SystemSession.getUser();
		try {
			long size = uploadItem.getFile().getSize();
			if (size > 104857600) {
				return "{error: '文件过大'}";
			}
			String path = FileUtil.saveFile(uploadItem.getFile(), employee.getEm_code());
			is = new FileInputStream(new File(path));
			reader = new MPPReader();
			pf = reader.read(is);
			boolean bool = ganttService.ImportMpp(prjId, pf);
			if (bool) {
				File file = new File(path);
				if (file.isFile() && file.exists()) {
					file.delete();
				}
			}
			String r = "{success: true}";
			return r;
		} catch(SystemException e){
			String error=e.getMessage()!=null?"{error: '"+e.getMessage()+"'}":"{error: 'MPP文件的格式不太规范,导入失败<hr>可以尝试将文件另存为,然后导入'}";
			try {
				return new String(error.getBytes("utf-8"), "iso8859-1");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				return "{success: false}";
			}
		}catch (Exception e) {
			try {
				return new String("{error: '文件格式不正确，请重新选择文件！'}".getBytes("utf-8"), "iso8859-1");
			} catch (UnsupportedEncodingException e1) {
				return "{success: false}";
			}
		} finally {
			try {
				is.close();
			} catch (IOException e) {

			}
		}
	}
	@RequestMapping("plm/gantt/setDoc.action")
	@ResponseBody
	public Map<String, Object> setDoc(HttpServletResponse resp, int prjId,int taskId, String docName,String docId){
		Map<String, Object> mapModel = new HashMap<String, Object>();
		ganttService.setDoc(prjId,taskId, docName, docId);
		mapModel.put("success", true);
		return mapModel;
	}
	
	@RequestMapping("plm/gantt/getPhaseTree.action")
	@ResponseBody
	public Map<String, Object> getProjectPhase(String condition,String checked){
		Map<String, Object> mapModel = new HashMap<String, Object>();
		mapModel.put("tree",ganttService.getProjectPhase(condition,checked));
		mapModel.put("success", true);
		return mapModel;
	}
	
	@RequestMapping("plm/gantt/setPhase.action")
	@ResponseBody
	public  Map<String,Object> setPhase(String prjId,String phaseid,String phase,String taskId,String detno){
		Map<String, Object> mapModel = new HashMap<String, Object>();
		ganttService.linkPhase(prjId,phaseid,phase,taskId,detno);
		mapModel.put("success", true);
		return mapModel;
	}
	
	@RequestMapping("plm/gantt/getLogByCondition.action")
	@ResponseBody
	public Map<String,Object> getLogByCondition(String prjplanid,String docname,int page,int start,int limit ){
		Map<String,Object> map=ganttService.getLogByCondition(prjplanid,docname,page,start,limit);	
		return map;
	}
	
}
