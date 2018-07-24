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
import com.uas.erp.service.pm.ProdFeatureService;


@Service
public class ProdFeatureServiceImpl implements ProdFeatureService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveProdFeature( String gridStore, String caller) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		Object prid = null;
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ProdFeature");  
		if (grid.size()>0 && BaseUtil.groupMap(grid, "pf_fecode").keySet().size()!=grid.size()){
			BaseUtil.showError("特征编号不能重复!");
			return;
		}
		for(Map<Object, Object> map:grid){
			map.put("pf_id", baseDao.getSeqId("PRODFEATURE_SEQ"));
			prid = map.get("pf_prid");
		} 
		baseDao.execute(gridSql); 
		try{
			//记录操作
			baseDao.logger.save(caller, "pr_id", prid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void deleteProdFeature(int pr_id, String caller) {	
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{pr_id});
		/*//删除
		baseDao.deleteById("ECNDetail", "bd_id", bd_id);*/
		//删除Detail
		baseDao.deleteById("ProdFeature", "pf_prid", pr_id);
		//记录操作
		baseDao.logger.delete(caller, "pr_id", pr_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{pr_id});
	}
	
	@Override
	public void updateProdFeatureById( String gridStore, String caller) { 
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "ProdFeature", "pf_id");
		Object prid = gstore.get(0).get("pf_prid");
		Object prcode = gstore.get(0).get("pf_prodcode");
		if (gstore.size()>0 && BaseUtil.groupMap(gstore, "pf_fecode").keySet().size()!=gstore.size()){
			BaseUtil.showError("特征编号不能重复！");
		} 
		SqlRowList rs =baseDao.queryForRowSet("select pr_code,pr_statuscode,pr_refno  from product where pr_id='"+prid+"'");
		if (rs.next()){
			for(Map<Object, Object> s:gstore){
				s.remove("pf_prodcode");
				s.put("pf_prodcode",rs.getString("pr_code"));
				if(s.get("pf_id") == null || s.get("pf_id").equals("") || s.get("pf_id").equals("0") ||
						Integer.parseInt(s.get("pf_id").toString()) == 0){//新添加的数据，id不存在
					int id = baseDao.getSeqId("PRODFEATURE_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "ProdFeature", new String[]{"pf_id"}, new Object[]{id});
					gridSql.add(sql);
					prid = s.get("pf_prid");
				}
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update Product set PR_SPECDESCRIPTION=(select wm_concat(pf_fecode||'|'||pf_valuecode) from (select pf_fecode,pf_valuecode from ProdFeature where pf_prodcode=?)) where pr_code=?", prcode, prcode);
		baseDao.execute("update Product set PR_SPECDESCRIPTION2=(select wm_concat(pf_fename||'|'||pf_value) from (select pf_fename,pf_value from ProdFeature where pf_prodcode=?)) where pr_code=?", prcode, prcode);
		baseDao.execute("update Product set pr_self=-1 where pr_code=? and nvl(pr_refno,' ')<>' ' and PR_SPECDESCRIPTION=(select fp_description from FeatureProduct where pr_refno=fp_code)", prcode);
		baseDao.execute("update Product set pr_self=0 where pr_code=? and nvl(pr_refno,' ')<>' ' and PR_SPECDESCRIPTION <> (select fp_description from FeatureProduct where pr_refno=fp_code)", prcode);
		baseDao.execute("update PreProduct set pre_self=(select pr_self from Product where pr_code =pre_code) where pre_code=? and nvl(pre_refno,' ')<>' '", prcode);
		//记录操作
		baseDao.logger.update(caller, "pr_id", prid);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void addProdFeature(String formStore, String caller) {
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(formStore); 
		Object pf_prid=map.get("pf_prid");
		//存在BOM的虚拟特征件不能直接新增特征
		Boolean bool=baseDao.checkByCondition("bom","bo_mothercode='"+map.get("pf_prodcode").toString()+"'");
		if(!bool){
			SqlRowList rs0=baseDao.queryForRowSet("select bd_detno,bd_soncode from bom,bomdetail,prodfeature where bo_mothercode='" + map.get("pf_prodcode").toString() + "' and bd_bomid=bo_id and pf_prodcode=bd_soncode and  pf_fecode='" + map.get("pf_fecode") + "'");
			if (!rs0.hasNext()){
				BaseUtil.showError("请在子件料号上添加特征!");
				  return;
			} 
		}
		//判定初始特征值是否指定
		if (map.get("fe_valuecode").toString().equals("")){
			 BaseUtil.showError("必须指定初始特征值!");
			 return;
		}else{
			bool=baseDao.checkByCondition("featureDetail","fd_code='"+map.get("pf_fecode")+"' and fd_valuecode='"+map.get("fe_valuecode").toString()+"'");
			if(bool){
			  BaseUtil.showError("指定的初始特征值不存在!");
			  return;
			}
		}
		//同一个特征项不能重复出现
		bool=baseDao.checkByCondition("ProdFeature","pf_prid="+pf_prid+" AND pf_fecode='"+map.get("pf_fecode")+"'");
		if(!bool){
		  BaseUtil.showError("该特征项已经存在!");
		  return;
		}
		//所有母件都添加特征
		//所有特征件都添加此特征的初始值		
		String res = baseDao.callProcedure("MM_BomMutiBack", new Object[] { map.get("pf_prodcode").toString(),0 });
		if (res != null && res.length() > 0) {
			BaseUtil.showError("多级反查失败，不能添加特征");
		}  
		String adddescription = "|" +map.get("pf_fecode") + ":" +map.get("fe_valuecode");
		SqlRowList frs=baseDao.queryForRowSet("select distinct bm_mothercode,pr_id from bommutiback left join product on pr_code=bm_mothercode where bm_prcode='"+map.get("pf_prodcode").toString()+"' and pr_specvalue='NOTSPECIFIC' and pr_code not in (select pf_prodcode from prodfeature where pf_fecode='" + map.get("pf_fecode") + "')");
		while (frs.next()){
			int maxdetno=0;
			Map<Object,Object> thismap = map;
			//添加特征
			SqlRowList thissl=baseDao.queryForRowSet("select NVL(max(pf_detno),0) from prodfeature where pf_prid="+frs.getObject("pr_id").toString());
			if(thissl.next()) maxdetno=thissl.getInt(1);
			thismap.remove("pf_id");
			thismap.remove("pf_detno");
			thismap.remove("pf_prodcode");
			thismap.remove("pf_prid");
			thismap.put("pf_id",baseDao.getSeqId("PRODFEATURE_SEQ"));
			thismap.put("pf_detno",maxdetno+1);			
			thismap.put("pf_prodcode",frs.getObject("bm_mothercode"));
			thismap.put("pf_prid",frs.getObject("pr_id"));
			thismap.remove("fe_valuecode");
			thismap.remove("fe_value");
			baseDao.execute(SqlUtil.getInsertSqlByMap(thismap,"PRODFEATURE")); 
			//特征件添加description
			baseDao.execute("update product set pr_specdescription=case when NVL(pr_specdescription,' ')=' ' then substr('" + adddescription + "',2,800) else  pr_specdescription||'" + adddescription + "' end where pr_refno='" + frs.getObject("bm_mothercode") + "' and pr_specvalue='SPECIFIC' and pr_specdescription||'|' not like '%|" + adddescription + "|%' ");
		}
		
		int detno=0;
		//获取最大的序号
		SqlRowList sl=baseDao.queryForRowSet("select NVL(max(pf_detno),0) from prodfeature where pf_prid="+pf_prid);
		if(sl.next()) detno=sl.getInt(1);
		Map<Object,Object> baseMap=BaseUtil.parseFormStoreToMap(formStore); 
		baseMap.put("pf_id",baseDao.getSeqId("PRODFEATURE_SEQ"));
		baseMap.put("pf_detno",detno+1);
		baseMap.remove("fe_valuecode");
		baseMap.remove("fe_value");
		baseDao.execute(SqlUtil.getInsertSqlByMap(baseMap,"PRODFEATURE"));
		//特征件添加description
		baseDao.execute("update product set pr_specdescription=case when NVL(pr_specdescription,' ')=' ' then substr('" + adddescription + "',2,800) else  pr_specdescription||'" + adddescription + "' end where pr_refno='" + baseMap.get("pf_prodcode").toString() + "' and pr_specvalue='SPECIFIC' and pr_specdescription||'|' not like '%" + adddescription + "|%' ");
	
	}
}
