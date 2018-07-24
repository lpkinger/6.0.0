package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.ProductVendorService;

@Service("productVendorService")
public class ProductVendorServiceImpl implements ProductVendorService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void updateProductVendorById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);		
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});	
		//修改ProductVendor
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProductVendor", "pv_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pv_id") == null || s.get("pv_id").equals("") || s.get("pv_id").equals("0") ||
					Integer.parseInt(s.get("pv_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODUCTVENDOR_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProductVendor", new String[]{"pv_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update productvendor set (pv_vendcode,pv_vendname)=(select ve_code,ve_name from vendor where ve_id=pv_vendid) where pv_prodid=" + store.get("pr_id"));
		baseDao.execute("update productvendor set pv_prodcode=(select pr_code from product where pv_prodid=pr_id) where pv_prodid=" + store.get("pr_id"));
		//记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public List<Map<String, Object>>  loadProductVendor(String prodcode) {
		// TODO Auto-generated method stub
		//重新载入供应商首先将原本的供应商分配比例清空
		String sql1="delete  from ProductVendorRate where pv_prodcode='"+prodcode+"'";
		baseDao.execute(sql1);
		//根据物料编号重新载入供应商
		String sql=" select pr_id, ppd_vendcode pv_vendor,ve_name,ppd_prodcode,ppd_currency pv_currency ,ppd_price pv_price ,NVL(ppd_rate,0) pv_taxrate from ("
                   +" SELECT ve_name,ppd_id,ppd_prodcode,ppd_vendcode,ppd_currency,ppd_price,ppd_rate,pr_id"
                   +", rank() over(partition by ppd_prodcode,ppd_vendcode,ppd_currency"
                   +" order by ppd_price * NVL(ppd_currate, 1) * (1 - NVL(ppd_rate, 0) / (100 + NVL(ppd_rate, 0)))) mm "
                   +" FROM PurchasePriceDetail ,PurchasePrice , currencys ,vendor,product"
                   +" WHERE  pp_id = ppd_ppid "
                   +" and ppd_vendcode=ve_code"
                   +" and pr_code=ppd_prodcode"
                   +" AND pp_statuscode = 'AUDITED'"
                   +" AND ppd_status = '有效' "
                   +" AND ppd_currency = cr_name"
                   +" AND ppd_prodcode='"+prodcode+"') where mm=1";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if (rs.hasNext()) {
			return rs.getResultList();
		}else 
			return null;
	}

	@Override
	public void updateVendorRate(String gridStore, String formStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// TODO Auto-generated method stub
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProductVendorRate", "pv_id");
		
		//判断供应商分配的比例之和是否超过100%
		int rate=0;
		for(Map<Object, Object> s:gstore){
			rate+=Integer.valueOf(s.get("pv_setrate").toString());			
		}
		if (rate!=100){
			BaseUtil.showError("物料供应商的分配比例之和应该为100");
		}
		//更新供应商分配比例
		for(Map<Object, Object> s:gstore){
			if(s.get("pv_id") == null || s.get("pv_id").equals("") || s.get("pv_id").equals("0") ||
					Integer.parseInt(s.get("pv_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODUCTVENDORRATE_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProductVendorRate", new String[]{"pv_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		
		//更新物料表中是否按供应商比例采购字段为-1（是）
		String sqlu="update product set PR_ISVENDORRATE=-1 where pr_id='"+store.get("pr_id")+"'";
		baseDao.execute(sqlu);
		//记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
	}
}
