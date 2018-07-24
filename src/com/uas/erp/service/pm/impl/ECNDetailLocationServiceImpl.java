package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.ECNDetailLocationService;


@Service("ECNDetailLocationService")
public class ECNDetailLocationServiceImpl implements ECNDetailLocationService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveECNDetailLocation( String gridStore, String caller) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		Object edid = null;
		Object[] ecrdet = null;
		for(Map<Object, Object> map:grid){
			map.put("edl_id", baseDao.getSeqId("ECNDETAILOCATION_SEQ"));
			edid = map.get("edl_edid");
			if (ecrdet==null)
			{
				ecrdet = baseDao.getFieldsDataByCondition("ECNDetail", "ed_ecnid,ed_detno,ed_code", "ed_id="+ map.get("edl_edid"));
			}
			map.put("edl_ecnid",ecrdet[0]);
			map.put("edl_eddetno",ecrdet[1]);
			map.put("edl_ecncode",ecrdet[2]); 
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ECNDetailLocation");
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ed_id", edid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void deleteECNDetailLocation(int ed_id, String caller) {	
		//执行删除前的其它逻辑
		handlerService.beforeDel("ECNDetailLocation", new Object[]{ed_id});		/*//删除
		baseDao.deleteById("ECNDetail", "bd_id", bd_id);*/
		//删除Detail
		baseDao.deleteById("ECNDetailLocation", "edl_edid", ed_id);
		//记录操作
		baseDao.logger.delete(caller, "ed_id", ed_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("ECNDetailLocation", new Object[]{ed_id});
	}
	
	@Override
	public void updateECNDetailLocationById( String gridStore, String caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "ECNDetailLocation", "edl_id");
		Object edid = null;
		Object[] ecrdet = null;
		for(Map<Object, Object> s:gstore){
			if(s.get("edl_id") == null || s.get("edl_id").equals("") || s.get("edl_id").equals("0") ||
					Integer.parseInt(s.get("edl_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("ECNDETAILOCATION_SEQ");
				if (ecrdet==null)
				{
					ecrdet = baseDao.getFieldsDataByCondition("ECNDetail", "ed_ecnid,ed_detno,ed_code", "ed_id="+ s.get("edl_edid"));
				}
				s.put("edl_ecnid",ecrdet[0]);
				s.put("edl_eddetno",ecrdet[1]);
				s.put("edl_ecncode",ecrdet[2]); 
				String sql = SqlUtil.getInsertSqlByMap(s, "ECNDetailLocation", new String[]{"edl_id"}, new Object[]{id});
				gridSql.add(sql);
				edid = s.get("edl_edid"); 
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ed_id", edid);
	}
}
