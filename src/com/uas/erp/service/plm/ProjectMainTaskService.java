package com.uas.erp.service.plm;

import net.sf.mpxj.ProjectFile;

import org.apache.poi.ss.usermodel.Workbook;

public interface ProjectMainTaskService {
	void saveProjectMainTask(String formStore, String param, String caller);

	void updateProjectMainTask(String fromStore, String param, String caller);

	void deleteProjectMainTask(int id, String caller);

	void submitProjectMainTask(int id, String caller);

	void resSubmitProjectMainTask(int id, String caller);

	void auditProjectMainTask(int id, String caller);

	void resAuditProjectMainTask(int id, String caller);

	void TurnTask(int id, String caller);

	void LoadTaskNode(int id, String type, String caller);

	boolean ImportExcel(int id, Workbook wbs, String substring, String startdate);

	void End(int id, String caller);

	void resEnd(int id, String caller);

	boolean ImportMpp(int id, ProjectFile pf, String substring, String startdate);
}
