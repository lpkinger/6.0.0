package com.uas.erp.dao.common;

import com.uas.erp.model.Team;

public interface TeamDao {
	
	Team getTeamByCode(String code);
}
