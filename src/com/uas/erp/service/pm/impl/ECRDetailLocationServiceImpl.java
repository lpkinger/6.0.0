package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.ECRDetailLocationService;


@Service("ECRDetailLocationService")
public class ECRDetailLocationServiceImpl implements ECRDetailLocationService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveECRDetailLocation( String gridStore, String caller) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		Object ecrdid = null; 
		Object[]  ecrdet = null;
		for(Map<Object, Object> map:grid){
			map.put("edl_id", baseDao.getSeqId("ECRDETAILOCATION_SEQ"));
			ecrdid = map.get("edl_ecrdid"); 
			if (ecrdet==null)
			{
				ecrdet=baseDao.getFieldsDataByCondition("ECRDETAIL", "ecrd_ecrid,ecrd_detno", "ecrd_id="+ map.get("edl_ecrdid"));
			}
			map.put("edl_ecrid",ecrdet[0]);
			map.put("edl_ecrddetno",ecrdet[1]);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ECRDetailLocation");
		handlerService.beforeSave("ECRDetailLocation",new Object[]{grid});
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ecrd_id", ecrdid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave("ECRDetailLocation",new Object[]{grid});
	}
	
	@Override
	public void deleteECRDetailLocation(int ecrd_id, String caller) {	
		//执行删除前的其它逻辑
		handlerService.beforeDel("ECRDetailLocation",new Object[]{ecrd_id});		/*//删除
		baseDao.deleteById("ECRDetail", "ecrd_id", ecrd_id);*/
		//删除Detail
		baseDao.deleteById("ECRDetailLocation", "edl_ecrdid", ecrd_id);
		//记录操作
		baseDao.logger.delete(caller, "ecrd_id", ecrd_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("ECRDetailLocation",new Object[]{ecrd_id});
	}
	
	@Override
	public void updateECRDetailLocationById( String gridStore, String caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "ECRDetailLocation", "edl_id");
		Object ecrdid = null;
		Object[] ecrdet = null;
		handlerService.beforeUpdate("ECRDetailLocation",new Object[]{gstore});
		for(Map<Object, Object> s:gstore){
			if(s.get("edl_id") == null || s.get("edl_id").equals("") || s.get("edl_id").equals("0") ||
					Integer.parseInt(s.get("edl_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("ECRDETAILOCATION_SEQ");
				if (ecrdet==null)
				{
					ecrdet=baseDao.getFieldsDataByCondition("ECRDETAIL", "ecrd_ecrid,ecrd_detno", "ecrd_id="+ s.get("edl_ecrdid"));
				}
				s.put("edl_ecrid",ecrdet[0]);
				s.put("edl_ecrddetno",ecrdet[1]); 
				String sql = SqlUtil.getInsertSqlByMap(s, "ECRDetailLocation", new String[]{"edl_id"}, new Object[]{id});
				gridSql.add(sql);
				ecrdid = s.get("edl_ecrdid");
			}
		}
		baseDao.execute(gridSql);
		handlerService.afterUpdate("ECRDetailLocation",new Object[]{gstore});
		//记录操作
		baseDao.logger.update(caller, "ecrd_id", ecrdid);
	}
}
