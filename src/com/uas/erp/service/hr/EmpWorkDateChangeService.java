package com.uas.erp.service.hr;

import java.text.ParseException;
import java.util.List;

import net.sf.json.JSONObject;

import com.uas.erp.model.JSONTree;

/**
 * Created by IntelliJ IDEA.
 * User: USOFTPC30
 * Date: 13-6-17
 * Time: 上午9:25
 * To change this template use File | Settings | File Templates.
 */
public interface EmpWorkDateChangeService {

    public void saveEmpWorkDateChange(String formStore, String gridStore, String  caller);

    public void updateEmpWorkDateChangeById(String formStore, String gridStore, String  caller);

    public void deleteEmpWorkDateChange(int id, String  caller);
    
    void submitEmpWorkDateChange(int  id, String  caller);
	
    void resSubmitEmpWorkDateChange(int id, String  caller);
    
    void auditEmpWorkDateChange(int  id, String  caller)  throws ParseException;
    
	void resAuditEmpWorkDateChange(int  id, String  caller);
	
	List<JSONObject> getEmployees(String condition);
}
