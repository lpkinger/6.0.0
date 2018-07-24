package com.uas.erp.service.plm;

import com.uas.erp.model.Teammember;

public interface TeamMemberService {

	Teammember getTeamMemberByIdCode(int team_id, String employee_code);

	void saveTeamMember(String formStore, String param);
}
