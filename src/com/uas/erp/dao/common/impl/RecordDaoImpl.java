package com.uas.erp.dao.common.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.RecordDao;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.Team;
import com.uas.erp.model.Teammember;
import com.uas.erp.model.WorkRecord;

@Repository("recordDao")
public class RecordDaoImpl extends BaseDao implements RecordDao {
	@Override
	public List<JSONTree> getJSONResource(int id) {
		List<JSONTree> tree=new ArrayList<JSONTree>();  
		List<Team> teams=getJdbcTemplate().query("SELECT * FROM Team where team_prjid="+id, new BeanPropertyRowMapper<Team>(Team.class));
		if(teams.size()>0){
			for(Team team:teams){
				JSONTree jt = new JSONTree(team);
				List<Teammember> tms=getJdbcTemplate().query("SELECT * FROM Teammember where tm_teamid="+team.getTeam_id(), 
						new BeanPropertyRowMapper<Teammember>(Teammember.class));
				List<JSONTree> tmtree=new ArrayList<JSONTree>();  
				for(Teammember tm:tms){
					JSONTree ct = new JSONTree(tm);  
					tmtree.add(ct);
				}
				jt.setChildren(tmtree);
				tree.add(jt);
			}
		}
		return tree;
	}

	@Override
	public List<JSONTree> getJSONRecord(String condition) {
		List<JSONTree> tree=new ArrayList<JSONTree>();  
		SqlRowList sl=queryForRowSet("SELECT wr_recorderemid,wr_recorder FROM WorkRecord where " +condition+"  group by wr_recorderemid,wr_recorder" );
		while(sl.next()){
			WorkRecord record=new WorkRecord();
			record.setWr_id(sl.getInt("wr_recorderemid"));
			record.setWr_recorder(sl.getString("wr_recorder"));
			JSONTree jt=new JSONTree(record,"parent");
			List<JSONTree> childs=new ArrayList<JSONTree>();
			List<WorkRecord> records=getJdbcTemplate().query("SELECT * FROM WorkRecord where wr_recorderemid=" + 
					sl.getInt("wr_recorderemid")+" AND "+condition +" order by wr_id" , new BeanPropertyRowMapper<WorkRecord>(WorkRecord.class));
			for(WorkRecord workrecord:records){
				workrecord.setWr_recorder(new SimpleDateFormat("yyyy-MM-dd").format(workrecord.getWr_recorddate()));
				childs.add(new JSONTree(workrecord,"leaf"));
			}
			jt.setChildren(childs);
			tree.add(jt);
		}

		return tree;
	}

}
