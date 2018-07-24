package com.uas.erp.service.plm;

import java.util.Map;

import com.uas.erp.model.Team;

public interface TeamService {
   void saveTeam(String formStore, String gridStore);
   void deleteTeam(int team_id);
   void deleteDetail(int tm_id);
   void updateTeamById(String formStore, String gridStore);
   Team getTeamByCode(String code);
   void insert(String formStore);
   void copyTeam(int id,String code, String formStore, String param, String caller);
   Map<String, Object> teamToMeeting(String caller, String id);
}
