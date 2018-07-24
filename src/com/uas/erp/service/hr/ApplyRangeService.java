package com.uas.erp.service.hr;

import java.util.List;

import com.uas.erp.model.CheckBoxTree;

public interface ApplyRangeService {
	
	public List<CheckBoxTree> getAllHrOrgsTree(String caller);

    public void setEmpAttendItem(int aiid, String ids, String caller);
}
