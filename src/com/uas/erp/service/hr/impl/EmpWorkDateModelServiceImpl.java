package com.uas.erp.service.hr.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.hr.EmpWorkDateModelService;

/**
 * Created by IntelliJ IDEA.
 * User: USOFTPC30
 * Date: 13-6-17
 * Time: 上午9:26
 * To change this template use File | Settings | File Templates.
 */
@Service
public class EmpWorkDateModelServiceImpl implements EmpWorkDateModelService {

    @Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

    @Override
    public void saveEmpWorkDateModel(String formStore, String gridStore, String  caller) {
        Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
    	List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
    	 handlerService.beforeSave(caller, new Object[]{store,gstore});
                String formSql = SqlUtil.getInsertSqlByFormStore(store, "EMPWORKDATEMODEL",
                        new String[]{}, new Object[]{});
                baseDao.execute(formSql);

                Object[] emd_id = new Object[1];
                if(gridStore.contains("},")){//明细行有多行数据哦
                    String[] datas = gridStore.split("},");
                    emd_id = new Object[datas.length];
                    for(int i=0;i<datas.length;i++){
                    	emd_id[i] = baseDao.getSeqId("EMPWORKDATEMODELDETAIL_SEQ");
                    }
                } else {
                	emd_id[0] = baseDao.getSeqId("EMPWORKDATEMODELDETAIL_SEQ");
                }
                List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "EMPWORKDATEMODELDETAIL",
                        "emd_id", emd_id);
                baseDao.execute(gridSql);
                try{
                    //记录操作
                    baseDao.logger.save(caller, "em_id", store.get("em_id"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //执行保存后的其它逻辑
                handlerService.afterSave(caller, new Object[]{store,gstore});

    }

    @Override
    public void updateEmpWorkDateModelById(String formStore, String gridStore, String  caller) {
        Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "EMPWORKDATEMODEL", "em_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "EMPWORKDATEMODELDETAIL", "emd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("emd_id") == null || s.get("emd_id").equals("") || s.get("emd_id").toString().equals("0")
					){//新添加的数据，id不存在
				int id = baseDao.getSeqId("EMPWORKDATEMODELDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "EMPWORKDATEMODELDETAIL", new String[]{"emd_id"},
                        new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "em_id", store.get("em_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
    }
    @Override
    public void deleteEmpWorkDateModel(int em_id, String  caller) {
        //执行删除前的其它逻辑
    	handlerService.beforeDel(caller, new Object[]{em_id});
		//删除purchase
		baseDao.deleteById("EMPWORKDATEMODEL", "em_id", em_id);
		//删除purchaseDetail
		baseDao.deleteById("EMPWORKDATEMODELDETAIL", "emd_emid", em_id);
		//记录操作
		baseDao.logger.delete(caller, "em_id", em_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{em_id});
    }

	@Override
	public void setEmpWorkDateModel(int wdid, String condition,
			String caller) {
        baseDao.updateByCondition("employee", "em_wdid=" + wdid, condition);
        }
	@Override
	public void cancelEmpWorkDateModel( String condition,
			String caller) {
        baseDao.updateByCondition("employee", "em_wdid=null" , condition);
        }
	

	/*
	 * params : List<String> emids -->需要更新员工班次表的员工Id
	 * params : String startdate  -->更新开始时间(日期)  eg.  2013-12-16
	 * params : String enddate  -->更新结束时间(日期)  eg.  2013-12-16
	 * params : Employee employee  -->录入人信息
	 * params : String language -->所选语言
	 * 
	 * (non-Javadoc)
	 * @see com.uas.erp.service.hr.EmpWorkDateModelService#updateEmpWorkDateList(java.util.List, java.lang.String, java.lang.String, com.uas.erp.model.Employee, java.lang.String)
	 */
	@Override
	public String updateEmpWorkDateList(String[] emids, String startdate,
			String enddate, String caller) {
		// TODO Auto-generated method stub
		String startdate_str = startdate;
		String enddate_str = enddate;
		
		//截取日期前十位  2013-12-16
		if (startdate != null && startdate.length()>10){
			startdate_str = startdate.substring(0, 10);
		}
		//截取日期前十位  2013-12-16
		if (enddate != null && enddate.length()>10){
			enddate_str = enddate.substring(0, 10);
		}
		boolean b= true;
		for (String emid :emids){
			String returnStr = baseDao.callProcedure( "EM_SETEMPWORKDATELIST", new Object []{Integer.valueOf(emid),startdate_str,enddate_str});
			if(!returnStr .equals("ok")){
				b = false;
			}
		}
		if(!b){
			return "存在没有绑定模板的员工,部分更新";
		}else{
			return "ok";
		}
		
	}

	@Override
	public String loadGridDate(String emid, String startdate, String enddate,
			String caller) {
		// TODO Auto-generated method stub
		String sql = "select eml_id, eml_emid, to_char(eml_date,'yyyy-mm-dd') eml_date, eml_wdid, to_char(eml_updatetime,'yyyy-mm-dd hh:mi:ss') eml_updatetime, wd_name "
				+"   from (select "
				+"    eml_id, "
		        +"    eml_emid, "
                +"    eml_date, "
            	+"    eml_wdid, "
    		    +"    eml_updatetime, "
    		    +"    row_number() over(partition by eml_date order by eml_updatetime desc) mm "
    		    +"    from EMPWORKDATELIST "
        		+"    where eml_date>= to_date('"+ startdate +"','yyyy-mm-dd') and eml_date<= to_date('"+enddate+"','yyyy-mm-dd') and eml_emid = '"+emid+"') "
        		+"    left join workdate "
        		+"    on eml_wdid = wd_id "
        		+"    where mm = 1 "
        		+"    order by eml_date";
		
		SqlRowList rs = baseDao.queryForRowSet(sql);
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = null;
		while(rs.next()){
			map = new HashMap<String, Object>();
			map.put("eml_id", rs.getObject("eml_id"));
			map.put("eml_emid", rs.getObject("eml_emid"));
			map.put("eml_date", rs.getObject("eml_date"));
			map.put("eml_wdid", rs.getObject("eml_wdid"));
			map.put("eml_updatetime", rs.getObject("eml_updatetime"));
			map.put("wd_name", rs.getObject("wd_name"));
			list.add(map);
		}
		
		String dataString = BaseUtil.parseGridStore2Str(list);
		
		return dataString;
	}
}
