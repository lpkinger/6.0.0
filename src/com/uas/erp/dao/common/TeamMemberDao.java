package com.uas.erp.dao.common;

import com.uas.erp.model.Teammember;

public interface TeamMemberDao {
	
	Teammember getTeammemberByIdCode(int team_id, String employee_code);

}
