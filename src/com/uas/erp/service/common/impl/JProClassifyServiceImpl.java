package com.uas.erp.service.common.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.ProcessDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JProClassify;
import com.uas.erp.model.JProcessDeploy;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.JProClassifyService;
@Service("jProClassifyService")
public class JProClassifyServiceImpl implements JProClassifyService {
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ProcessDao processDao;
	@Autowired
	private FormDao formDao;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<JProClassify> getJProClassifies(int start, int limit) {
		
		final String sql = "select jc_id,jc_code,jc_name from (select jc_id,jc_code,jc_name, row_number()  over(order by jc_id ) rn from JProclassify )  where rn between ? and ?";
		
		List<JProClassify> pcs=null;
		try {
			pcs = baseDao.getJdbcTemplate().query(sql, new Object[]{start, limit},new BeanPropertyRowMapper(JProClassify.class));
			
		} catch (DataAccessException e) {
			
			e.printStackTrace();
			throw new RuntimeException("查询表 JProClassify 异常 :"+ e.getMessage());
		}
		
		return pcs;
	}

	@Override
	public void saveJProClassify(String formStore, String param,String language, Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean bool = baseDao.checkByCondition("JProClassify", "jc_code='" + store.get("jc_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist", language));
		}
		String formSql =SqlUtil.getInsertSqlByMap(store, "JProClassify");
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.save", language), 
					BaseUtil.getLocalMessage("msg.saveSuccess", language), "JProClassify|jc_id=" + store.get("jc_id")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void deleteJProClassify(int id, String language, Employee employee) {
		boolean boolOA=baseDao.checkIf("JprocessTemplate","pt_jcid ="+id);
		if(boolOA){BaseUtil.showError("流程分类已被使用,不允许删除!");}
		baseDao.deleteById("JProClassify", "jc_id", id);
		baseDao.deleteById("JProcessDeploy", "jd_id", id);		
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.delete", language), 
				BaseUtil.getLocalMessage("msg.deleteSuccess", language), "JProcessClassify|jc_id=" + id));
		
	}

	@Override
	public void updateJProClassifyById(String formStore, String param, String language, Employee employee) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		store.put("jc_updater", employee.getEm_name());
		store.put("jc_updatetime",DateUtil.currentDateString(Constant.YMD_HMS));
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "JProClassify", "jc_id");
		baseDao.execute(formSql);
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.update", language), 
				BaseUtil.getLocalMessage("msg.updateSuccess", language), "JProClassify|jc_id=" + store.get("jc_id")));
	}

	@Override
	public List<Object[]> getAllJProClassify(String language,Employee employee) {
		// TODO Auto-generated method stub
		return baseDao.getFieldsDatasByCondition("jproclassify", new String[]{"jc_id","jc_name"},"1=1");
	}

	@Override
	public void removeToOtherClassify(int id, String data, String language,
			Employee employee) {
		// TODO Auto-generated method stub
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		for(Map<Object, Object> s:store){
	       baseDao.updateByCondition("JprocessDeploy","jd_selfid="+id, "jd_id="+Integer.parseInt( s.get("jd_id").toString()));
		}
	}
	@Override
	public Map<Object,Object> getAllJprocessDeployInfo() {
		// TODO Auto-generated method stub
	   Map<Object,Object>map=null;
       Map<Object,List<Map<Object,Object>>> deploymap=new HashMap<Object, List<Map<Object,Object>>>();
       List<Map<Object,Object>> maps=new ArrayList<Map<Object,Object>>();
       SqlRowList sl1=baseDao.queryForRowSet("select jd_id,jd_selfid ,jd_caller,jd_processdefinitionname,js_formurl from jprocessdeploy left join jprocessset on jd_caller=js_caller where jd_detno!=-1 order by jd_detno,jd_processdefinitionname ");
       while(sl1.next()){
    	 map=new HashMap<Object,Object>();
    	 map.put("jd_id", sl1.getObject("jd_id"));
    	 map.put("jd_selfid",sl1.getObject("jd_selfid"));
    	 map.put("jd_caller", sl1.getObject("jd_caller"));
    	 map.put("jd_processdefinitionname", sl1.getObject("jd_processdefinitionname"));
    	 map.put("js_formurl", sl1.getObject("js_formurl"));
    	 maps.add(map);
       }
       deploymap=BaseUtil.groupMap(maps, "jd_selfid");
       SqlRowList sl2=baseDao.queryForRowSet("select jc_id,jc_name from jproclassify order by jc_detno");
       List<Map<String,Object>> classifymaps=new ArrayList<Map<String,Object>>();
       while(sl2.next()){
    	   classifymaps.add(sl2.getCurrentMap());
       }
       map=new HashMap<Object,Object>();
       map.put("deploy", deploymap);
       map.put("classify", classifymaps);
       return map;
	}

	@Override
	public List<Map<Object, Object>> getProcessInfoByCondition(String condition) {
		// TODO Auto-generated method stub
		  List<Map<Object,Object>> maps=new ArrayList<Map<Object,Object>>();
		  Map<Object,Object>map=null;
	       SqlRowList sl1=baseDao.queryForRowSet("select jd_id,jd_selfid ,jd_caller,jd_processdefinitionname,js_formurl from jprocessdeploy left join jprocessset on jd_caller=js_caller where "+condition+" order by jd_detno,jd_processdefinitionname ");
	       while(sl1.next()){
	    	 map=new HashMap<Object,Object>();
	    	 map.put("jd_id", sl1.getObject("jd_id"));
	    	 map.put("jd_selfid",sl1.getObject("jd_selfid"));
	    	 map.put("jd_caller", sl1.getObject("jd_caller"));
	    	 map.put("jd_processdefinitionname", sl1.getObject("jd_processdefinitionname"));
	    	 map.put("js_formurl", sl1.getObject("js_formurl"));
	    	 maps.add(map);
	       }
		return maps;
	}

	@Override
	public void orderByJprocess(String data, String language, Employee employee) {
		// TODO Auto-generated method stub
		List<Map<Object,Object>> datas=BaseUtil.parseGridStoreToMaps(data);
		baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(datas, "JPROCESSDEPLOY", "jd_id"));
	}

	@Override
	public List<JProcessDeploy> getJpTree(String condition) {
		List<JProcessDeploy> jProcessDeploy=processDao.getJProcessDeploysByCondition(condition);
		return jProcessDeploy;
	}
	

}
