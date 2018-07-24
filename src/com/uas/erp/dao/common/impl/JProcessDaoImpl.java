package com.uas.erp.dao.common.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.lob.OracleLobHandler;
import org.springframework.jdbc.support.nativejdbc.CommonsDbcpNativeJdbcExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.JProcessDao;
import com.uas.erp.dao.common.JProcessSetDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JProcess;
import com.uas.erp.model.JProcessSet;
import com.uas.erp.model.JTask;
import com.uas.erp.model.JnodeRelation;

@Repository
public class JProcessDaoImpl extends BaseDao implements JProcessDao {
	@Autowired
	private  JProcessSetDao processSetDao;
	private static String updatetemplateSql="update JprocessTemplate set pt_text =? where pt_id =?";
    private static String updateautoprocess="update autoprocess set ap_text=? where ap_id=?";
	@Override
	public List<JProcess> getAllJProcess(int page, int pageSize) {
		String sql = "select jp_id, jp_name, jp_form, jp_nodeName, jp_launcherId, jp_launcherName, jp_launchTime, jp_stayMinutes, jp_nodeDealMan, jp_nodeId, jp_status ";
		sql = sql + " FROM("
				+ sql
				+ ",row_number()over(order by jp_id desc) rn FROM jprocess ) WHERE rn between "
				+ ((page - 1) * pageSize + 1) + " and " + page * pageSize;
		return getJdbcTemplate().query(sql,new BeanPropertyRowMapper<JProcess>(JProcess.class));

	}
	@Override
	public int getAllJProcessCount() {
		return getCountByTable("jprocess");
	}
	@Override
	public void delete(int id) {
		try {
			getJdbcTemplate().execute(
					"delete from jprocess where jp_id = " + id);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Override
	public List<JProcess> search(String condition, int page, int pageSize) {
		String sql = "select jp_id, jp_name, jp_form, jp_nodeName, jp_launcherId, jp_launcherName, jp_launchTime, jp_stayMinutes, jp_nodeDealMan, jp_nodeId, jp_status ";
		sql = sql + " FROM("
				+ sql
				+ ",row_number()over(order by jp_id desc) rn FROM jprocess WHERE " + condition + ") WHERE rn between "
				+ ((page - 1) * pageSize + 1) + " and " + page * pageSize;
		return getJdbcTemplate().query(sql,new BeanPropertyRowMapper<JProcess>(JProcess.class));
	}
	@Override
	public int searchCount(String condition) {
		return getCountByCondition("jprocess", condition);
	}
	@Override
	public List<JProcess> getAllReviewedJProcess(int page, int pageSize) {
		String sql = "select jp_id, jp_name, jp_form, jp_nodeName, jp_launcherId, jp_launcherName, jp_launchTime, jp_stayMinutes, jp_nodeDealMan, jp_nodeId, jp_status ";
		sql = sql + " FROM("
				+ sql
				+ ",row_number()over(order by jp_id desc) rn FROM jprocess where jp_status = '已审批') WHERE rn between "
				+ ((page - 1) * pageSize + 1) + " and " + page * pageSize;
		List<JProcess> jps = null ;
		try {
			jps = getJdbcTemplate().query(sql,new BeanPropertyRowMapper<JProcess>(JProcess.class));
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("数据库连接异常 !");
		}
		return jps;


	}
	@Override
	public long getDurationOfInProcessInstance(String pInstanceId) {
		final String sql = "select START_ ,END_ from JBPM4_HIST_PROCINST where ID_ = ?";
		long minutes = 0 ;
		try {
			Map<String,Object> map = getJdbcTemplate().queryForMap(sql, new Object[]{pInstanceId});


			//System.out.println("时间一 :"+((Date) map.get("END_")).getTime());
			//System.out.println("时间二:"+((Date) map.get("START_")).getTime());
			minutes = (((Date) map.get("END_")).getTime() -((Date) map.get("START_")).getTime())/(1000*60);

		} catch (DataAccessException e) {

			e.printStackTrace();

			return minutes;

		}		
		//System.out.println(" ID为 "+pInstanceId+"的流程 结束审批红耗时  :"+minutes +"分钟 。");
		return minutes;
	}
	@Override
	public int getSumOfNode(String pInstanceId) {
		String sql = "select count(*) cn from jprocess where jp_form = '"+pInstanceId+"' "; 
		SqlRowSet rs = getJdbcTemplate().queryForRowSet(sql);
		if (rs.next()) {
			return rs.getInt(1);
		} else {
			try {
				throw new Exception("Empty Data");
			} catch (Exception e) {
				return 0;
			}
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, Integer> getDuedateOfJNode(String processDefId) {
		Map taskInfo = new HashMap<String, String>();
		final String sql = "select * from JTask where jt_processDefId = ?";
		List<JTask> tasks = getJdbcTemplate().query(sql, new Object[]{processDefId}, new BeanPropertyRowMapper<JTask>(JTask.class));
		for(JTask task :tasks){
			taskInfo.put(task.getJt_name(), task.getJt_duedate());
		}


		return taskInfo;
	}
	@Override
	public Map<String, Object> getDecisionConditionData(String caller,int keyValue) {
		// TODO Auto-generated method stub
		Map<String,Object> result = new HashMap<String,Object>();
		JProcessSet js = processSetDao.getCallerInfo(caller);
		String condition = js.getJs_decisionCondition();
		String []cs= condition.split("#");
		String formKey = js.getJs_formKeyName();
		String formTable = js.getJs_table();
		String decisionVaribles = js.getJs_decisionVariables();
		String groupby=js.getJs_groupby();
		StringBuffer sql =new StringBuffer();
		String querySql="";
		sql.append("select ");
		if(cs.length>1){
			for(String s:cs){

				sql.append(s+",");
			}
			sql.deleteCharAt(sql.length()-1).append(" from "+formTable+" where "+formKey+ "= ?");
			querySql=sql.toString();
			querySql=groupby!=null?querySql+" "+groupby:querySql;
			Map<String, Object> map = getJdbcTemplate().queryForMap(querySql, new Object[]{keyValue});
			String [] variables = decisionVaribles.split("#");
			for(int i=0;i<cs.length;i++){
				if(map.get(variables[i])!=null&&map.get(variables[i]).toString().matches("\\d*")){
					int fixedValue = Integer.parseInt(map.get(variables[i]).toString());
					result.put(variables[i],fixedValue);
				}else{
					result.put(variables[i],map.get(variables[i]));
				};  
			}
		}else{
			sql.append(cs[0]+" from " +formTable +" where "+formKey+ "= ?" );
			querySql=sql.toString();
			querySql=groupby!=null?querySql+" "+groupby:querySql;
			Map<String, Object> map = getJdbcTemplate().queryForMap(sql.toString(), new Object[]{keyValue});
			result.put(decisionVaribles,map.get(decisionVaribles));
		}
		return result;
	}
	@Override
	public List<JnodeRelation> getJnodeRelationsByDefId(String DefId) {
		// TODO Auto-generated method stub
		String querySql="select * from JnodeRelation where jr_processdefid=? order by jr_id asc";
		List<JnodeRelation> relations = getJdbcTemplate().query(querySql,new Object[]{DefId},new BeanPropertyRowMapper<JnodeRelation>(JnodeRelation.class));
		return relations;
	}
	@Override
	public void saveJprocessTemplate(String formStore, String clobtext,String language, Employee employee) {
		// TODO Auto-generated method stub
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(formStore);
		Object KeyValue=map.get("pt_id");
		Object caller = map.get("pt_caller");
		boolean bool=checkIf("JprocessDeploy", "jd_caller='"+caller+"'");
		if(bool){
			BaseUtil.showError("当前定义CALLER已经存在已定义流程中,请重新设置!");
		}
		boolean bool2 =checkIf("JprocessTemplate","pt_caller='"+caller+"'");
		if(bool2){
			BaseUtil.showError("当前定义CALLER已经存在于已定义流程模板中!");
		}
		execute(SqlUtil.getInsertSqlByMap(map, "JprocessTemplate"));
		updateClobText(KeyValue, clobtext,updatetemplateSql);

	}
	@Override
	public void updateJprocessTemplate(String formStore, String clobtext,String language, Employee employee) {
		// TODO Auto-generated method stub
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(formStore);
		Object KeyValue=map.get("pt_id");
		Object caller = map.get("pt_caller");
		boolean bool=checkIf("JprocessDeploy", "jd_caller='"+caller+"'");
		if(bool){
			BaseUtil.showError("当前定义CALLER已经存在已定义流程中,请重新设置!");
		}
		boolean bool2 =checkIf("JprocessTemplate","pt_caller='"+caller+"' and pt_id<>"+KeyValue);
		if(bool2){
			BaseUtil.showError("当前定义CALLER已经存在于已定义流程模板中!");
		}
		execute(SqlUtil.getUpdateSqlByFormStore(map, "JprocessTemplate", "pt_id"));
		updateClobText(KeyValue, clobtext,updatetemplateSql);
	}
	private void updateClobText( final Object KeyValue, final String clobtext,final String Sql){
		final OracleLobHandler lobHandler = new OracleLobHandler();
		CommonsDbcpNativeJdbcExtractor extractor = new CommonsDbcpNativeJdbcExtractor();
		lobHandler.setNativeJdbcExtractor(extractor);
		final String sql=Sql;						
		getJdbcTemplate().update(sql, new PreparedStatementSetter(){
			@Override
			public void setValues(PreparedStatement ps)throws SQLException {
				lobHandler.getLobCreator().setClobAsString(ps, 1, clobtext);
				ps.setObject(2, KeyValue);
			}	
		});
	}
	@Override
	public void saveAutoJprocess(String formStore, String clobtext,String language, Employee employee) {
		// TODO Auto-generated method stub
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(formStore);
		Object KeyValue=map.get("ap_id");
		execute(SqlUtil.getInsertSqlByMap(map, "AutoProcess"));
		updateClobText(KeyValue, clobtext,updateautoprocess);
	}
	@Override
	public void updateAutoJprocess(String formStore, String clobtext,String language, Employee employee) {
		// TODO Auto-generated method stub
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(formStore);
		Object KeyValue=map.get("ap_id");
		execute(SqlUtil.getUpdateSqlByFormStore(map, "AutoProcess","ap_id"));
		updateClobText(KeyValue, clobtext,updateautoprocess);
	}

}
