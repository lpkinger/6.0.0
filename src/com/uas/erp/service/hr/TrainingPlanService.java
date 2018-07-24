package com.uas.erp.service.hr;

import java.text.ParseException;
import java.util.List;
import java.util.Map;


public interface TrainingPlanService {
	
	void saveTrainingPlan(String formStore, String gridStore,String caller) throws ParseException;
	
	void updateTrainingPlanById(String formStore, String gridStore,String caller) throws ParseException;
	
	void deleteTrainingPlan(int tp_id, String caller);
	
	void auditTrainingPlan(int tp_id, String caller) throws ParseException;
	
	void resAuditTrainingPlan(int tp_id, String caller);
	
	void submitTrainingPlan(int tp_id, String caller) throws ParseException;
	
	void resSubmitTrainingPlan(int tp_id, String caller);
	
	List<Map<String,Object>> getTrainingCourse(String code);
	
}
