package com.uas.erp.service.plm;

import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.mpxj.ProjectFile;

import org.apache.poi.ss.usermodel.Workbook;

public interface ProjectGanttTaskService {
	void saveProjectGanttTask(String formStore, String param, String caller);

	void updateProjectGanttTask(String fromStore, String param, String caller);

	void deleteProjectGanttTask(int id, String caller);

	void submitProjectGanttTask(int id, String caller);

	void resSubmitProjectGanttTask(int id, String caller);

	void auditProjectGanttTask(int id, String caller);

	void resAuditProjectGanttTask(int id, String caller);

	void TurnTask(int id, String caller);

	void LoadTaskNode(int id, String type, String caller,String startdate);

	boolean ImportExcel(int id, Workbook wbs, String substring, String startdate);

	void End(int id, String caller);

	void resEnd(int id, String caller);

	boolean ImportMpp(int id, ProjectFile pf, String substring, String startdate);
	
	Map<String,Object> getTaskCompletion(Integer taskId, Integer resourceEmpId);
	
	void getPreTask(int id, String caller);
}
