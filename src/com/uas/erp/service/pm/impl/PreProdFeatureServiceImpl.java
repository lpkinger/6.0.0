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
import com.uas.erp.service.pm.PreProdFeatureService;


@Service
public class PreProdFeatureServiceImpl implements PreProdFeatureService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void savePreProdFeature( String gridStore, String caller) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		Object prid = null;
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "PreProdFeature");  
		if (grid.size()>0 && BaseUtil.groupMap(grid, "ppf_fecode").keySet().size()!=grid.size()){
			BaseUtil.showError("特征编号不能重复!");
			return;
		}
		for(Map<Object, Object> map:grid){
			map.put("ppf_id", baseDao.getSeqId("PRODFEATURE_SEQ"));
			prid = map.get("ppf_prid");
		} 
		baseDao.execute(gridSql); 
		//记录操作
		baseDao.logger.save(caller, "pr_id", prid);
	}
	
	@Override
	public void deletePreProdFeature(int pr_id, String caller) {	
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{pr_id});
		//删除Detail
		baseDao.deleteById("PreProdFeature", "ppf_prid", pr_id);
		//记录操作
		baseDao.logger.delete(caller, "pr_id", pr_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{pr_id});
	}
	
	@Override
	public void updatePreProdFeatureById( String gridStore, String caller) { 
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "PreProdFeature", "ppf_id");
		Object prid = gstore.get(0).get("ppf_prid");
		Object prcode = gstore.get(0).get("ppf_prodcode");
		if (gstore.size()>0 && BaseUtil.groupMap(gstore, "ppf_fecode").keySet().size()!=gstore.size()){
			BaseUtil.showError("特征编号不能重复！");
		} 
		SqlRowList rs =baseDao.queryForRowSet("select pre_code,pre_statuscode,pre_refno  from preproduct where pre_id='"+prid+"'");
		if (rs.next()){
			for(Map<Object, Object> s:gstore){
				s.remove("ppf_prodcode");
				s.put("ppf_prodcode",rs.getString("pre_code"));
				if(s.get("ppf_id") == null || s.get("ppf_id").equals("") || s.get("ppf_id").equals("0") ||
						Integer.parseInt(s.get("ppf_id").toString()) == 0){//新添加的数据，id不存在
					int id = baseDao.getSeqId("PRODFEATURE_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "PreProdFeature", new String[]{"ppf_id"}, new Object[]{id});
					gridSql.add(sql);
					prid = s.get("ppf_prid");
				}
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update PreProduct set PRE_SPECDESCRIPTION=(select wm_concat(ppf_fecode||'|'||ppf_valuecode) from (select ppf_fecode,ppf_valuecode from PreProdFeature where ppf_prodcode=?)) where pre_code=?", prcode, prcode);
		baseDao.execute("update PreProduct set PRE_SPECDESCRIPTION2=(select wm_concat(ppf_fename||'|'||ppf_value) from (select ppf_fename,ppf_value from PreProdFeature where ppf_prodcode=?)) where pre_code=?", prcode, prcode);
		baseDao.execute("update PreProduct set pre_self=-1 where pre_code=? and nvl(pre_refno,' ')<>' ' and PRE_SPECDESCRIPTION=(select fp_description from FeatureProduct where pre_refno=fp_code)", prcode);
		baseDao.execute("update PreProduct set pre_self=0 where pre_code=? and nvl(pre_refno,' ')<>' ' and PRE_SPECDESCRIPTION <> (select fp_description from FeatureProduct where pre_refno=fp_code)", prcode);
		//记录操作
		baseDao.logger.update(caller, "pr_id", prid);
	}
}
