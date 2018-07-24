package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.ProdReplaceService;


@Service("prodReplaceService")
public class ProdReplaceServiceImpl implements ProdReplaceService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveProdReplace(String gridStore, String caller) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		Object bdid = null;
		for(Map<Object, Object> map:grid){
			map.put("pre_id", baseDao.getSeqId("PRODREPLACE_SEQ"));
			bdid = map.get("pre_bdid");
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { grid, grid });
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ProdReplace");
		baseDao.execute(gridSql);
		int countrep = baseDao.getCountByCondition("prodreplace","pre_bdid>0 and pre_bdid='" + bdid + "' and NVL(pre_statuscode,' ')<>'DISABLE'");
		if (countrep>0){
			countrep=-1; 
		}
		else{
			countrep=0;
		}
		//更新BOMID，序号，子件编号，母件编号
		baseDao.execute("update prodreplace set (pre_bomid,pre_bddetno,pre_soncode,pre_prodcode)=(select bd_bomid,bd_detno,bd_soncode,bd_mothercode from bomdetail where bd_id=pre_bdid) where pre_bdid="+bdid);	
				
		baseDao.execute("update bomdetail set bd_ifrep='" + countrep + "' where bd_id='" + bdid + "'");	
		try{
			//记录操作
			baseDao.logger.save(caller, "bd_id", bdid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{grid,grid});
	}
	@Override
	public void deleteProdReplace(int bd_id, String caller) {	
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{bd_id});
		/*//删除
		baseDao.deleteById("BOMDetail", "bd_id", bd_id);*/
		//删除明细
		baseDao.deleteById("PRODREPLACE", "pre_bdid", bd_id);
		baseDao.execute("update bomdetail set bd_ifrep='0' where bd_id='" + bd_id + "'");	
		//记录操作
		baseDao.logger.delete(caller, "bd_id", bd_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{bd_id});
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProdReplaceById(String gridStore, String caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "ProdReplace", "pre_id");
		Object bdid = null; 
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { gstore, gstore });
		for(Map<Object, Object> s:gstore){
			bdid = s.get("pre_bdid");
			if(s.get("pre_id") == null || s.get("pre_id").equals("") || s.get("pre_id").equals("0") ||
					Integer.parseInt(s.get("pre_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODREPLACE_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProdReplace", new String[]{"pre_id"}, new Object[]{id});
				gridSql.add(sql); 
			}
		} 		
		SqlRowList rs= baseDao.queryForRowSet("select bo_status from bom left join  bomdetail on bo_id=bd_bomid where bd_id='"+bdid+"' and NVL(bo_statuscode,'')<>'ENTERING'");
		if(rs.next()){
		    BaseUtil.showError("BOM单据的最新状态为"+rs.getString("bo_status")+"不允许操作");
		 }
		baseDao.execute(gridSql);
		int countrep = baseDao.getCountByCondition("prodreplace","pre_bdid>0 and pre_bdid='" + bdid + "' and NVL(pre_statuscode,' ')<>'DISABLE'");
		if (countrep>0){
			countrep = -1; 
		}
		else{
			countrep = 0;
		} 	
		//更新BOMID，序号，子件编号，母件编号
		baseDao.execute("update prodreplace set (pre_bomid,pre_bddetno,pre_soncode,pre_prodcode)=(select bd_bomid,bd_detno,bd_soncode,bd_mothercode from bomdetail where bd_id=pre_bdid) where pre_bdid="+bdid);	
		baseDao.execute("update bomdetail set bd_ifrep='" + countrep + "'where bd_id='" + bdid + "'");	
		//更新bomdetail表中的替代料编号的字段
		baseDao.execute("update bomdetail set bd_repcode=(select wm_concat(pre_repcode) from prodreplace where bd_id=pre_bdid) where bd_id='"+bdid+"'");
		//记录操作
		baseDao.logger.update(caller, "bd_id", bdid);
		// 判断主料和替代料不能重复
		 String SQLStr = "select count(1) n  from prodreplace  where pre_bdid=" + bdid
				+ " and pre_soncode=pre_repcode and NVL(pre_statuscode,' ')<>'DISABLE'";
		 rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("n") > 0) {
				BaseUtil.showError("主料和替代料不能是相同料号!");
			}
		}
		// 判断替代料不能重复
		SQLStr = "select * from (select  pre_repcode,count(1) num from prodreplace where pre_bdid=" + bdid
				+ " and NVL(pre_statuscode,' ')<>'DISABLE'  group by pre_repcode)A where num>1";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("num") > 0) {
				BaseUtil.showError("替代料：" + rs.getString("pre_repcode") + "重复建立!");
			}
		}
		// 判断替代料和BOM母件编号重复
		SQLStr = "select * from (select pre_repcode,count(1) cn from prodreplace  where pre_bdid="+bdid+"and pre_prodcode=pre_repcode and NVL(pre_statuscode,' ')<>'DISABLE'  group by pre_repcode)A ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				BaseUtil.showError("替代料：" + rs.getString("pre_repcode") + "与BOM母件重复!");
			}
		}
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{gstore,gstore});
		
		//更新成功之后调用存储过程修改比例
		try {
			baseDao.callProcedure("MM_SETREPLACEGROUP",new String[] {});
		} catch (Exception e) {
			
		}
	}
	@Override
	public void setMain(int pre_id, String caller) {
		String SQLStr=""; 
		SQLStr = "SELECT pre_id,pre_repcode,pre_statuscode,bd_soncode,bd_id,bd_bomid,bd_detno FROM prodreplace,bomdetail where pre_id="+pre_id+" and pre_bdid=bd_id ";
		SqlRowList rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getString("pre_statuscode")!=null && rs.getString("pre_statuscode").equals("DISABLE")){
				BaseUtil.showError("已禁用的替代料不能设置成主料!");
			}
			baseDao.execute("update bomdetail set bd_soncode='"+rs.getString("pre_repcode")+"' where bd_id="+rs.getString("bd_id"));
			baseDao.execute("update prodreplace set pre_repcode='"+rs.getString("bd_soncode")+"' where pre_id="+rs.getString("pre_id"));
			baseDao.execute("update prodreplace set pre_soncode='"+rs.getString("pre_repcode")+"' where pre_bdid="+rs.getString("bd_id"));
			//更新bomdetail表中的替代料编号的字段
			baseDao.execute("update bomdetail set bd_repcode=(select wm_concat(pre_repcode) from prodreplace where bd_id=pre_bdid) where bd_id='"+rs.getString("bd_id")+"'");
			//记录操作
			baseDao.logger.others("设为主料", "原替代料:"+rs.getString("pre_repcode")+"原主料："+rs.getString("bd_soncode"), caller, "bd_id",  rs.getString("bd_id"));
			//记录操作
			baseDao.logger.others("设为主料", "序号:"+rs.getString("bd_detno")+"原替代料:"+rs.getString("pre_repcode")+"原主料："+rs.getString("bd_soncode"), "BOM", "bo_id", rs.getString("bd_bomid"));
		}
	}
}
