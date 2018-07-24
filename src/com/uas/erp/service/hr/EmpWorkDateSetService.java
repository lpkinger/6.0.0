package com.uas.erp.service.hr;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.uas.erp.model.JSONTree;

import net.sf.json.JSONObject;

public interface EmpWorkDateSetService {

    public void saveEmpWorkDateSet(String formStore, String gridStore, String  caller) throws ParseException;

    public List<JSONTree> getWdTreeAndEmployees(String  caller);

    List<JSONObject> search(String likestring);
    
    List<Map<String, Object>> getDatas(String condition);

	String deleteEmpworkdate(String caller, String data);

}
