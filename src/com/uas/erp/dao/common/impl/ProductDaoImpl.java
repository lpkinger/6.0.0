package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ProductDao;

@Repository
public class ProductDaoImpl extends BaseDao implements ProductDao{

	@Transactional
	@Override
	public synchronized String[] postProduct(int[] id, String from, String to) {
		String[] log = new String[id.length];
		//先取当前dataSource下Product的数据
		List<SqlRowList> lists = new ArrayList<SqlRowList>();//Product
		SqlRowList rs = null;
		for(int i:id){
			rs = queryForRowSet("SELECT * FROM Product where pr_id=" + i);
			lists.add(rs);
		}
		//再切换dataSource
		SpObserver.putSp(to);
		Map<String, Object> map;
		boolean bool;
		String sql;
		int count = 0;
		JSONObject json;
		Object oldId;
		try {
			for(SqlRowList list:lists){
				json = new JSONObject();
				if(list.next()){
					map = list.getCurrentMap();
					oldId = map.get("PR_ID");
					bool = checkByCondition("Product", "pr_code='" + map.get("PR_CODE") + "'");//判断料号是否已存在
					if(bool){
						map.put("PR_ID", getSeqId("PRODUCT_SEQ"));//ID重新生成
						sql = SqlUtil.getInsertSqlByMap(map, "Product");
						try{
							execute(sql);
							json.put("id", oldId);
							json.put("success", true);
						} catch (Exception e) {
							json.put("id", oldId);
							json.put("success", false);
							json.put("error", e.getMessage());
						}
					} else {
						json.put("id", oldId);
						json.put("success", false);
						json.put("error", "该物料在帐套" + to + "已存在");
					}
				}
				log[count] = json.toString();
				count++;
			}
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
		}
		return log;
	}

}
