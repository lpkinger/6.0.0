package com.uas.erp.service.scm.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.NoRuleService;

@Service("noRuleService")
public class NoRuleServiceImpl implements NoRuleService {
	@Autowired  
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveNoRule(String caller,String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		
		boolean bool = baseDao.checkByCondition("Norule", "nr_code='" + store.get("nr_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		if(store.get("nr_custcode") != null && !"".equals(store.get("nr_custcode"))){
			if(("Single").equals(store.get("nr_type"))){				
				bool = baseDao.checkByCondition("Norule", "nr_custcode='" + store.get("nr_custcode") + "' and nr_type = 'Single'");			
				if(!bool){
					BaseUtil.showError("客户编号 :"+store.get("nr_custcode")+"已经存在对应的单盘条码规则");
				}
			}else if(("Out").equals(store.get("nr_type"))){
				bool = baseDao.checkByCondition("Norule", "nr_custcode='" + store.get("nr_custcode") + "' and nr_type = 'Out'");			
				if(!bool){
					BaseUtil.showError("客户编号 :"+store.get("nr_custcode")+"已经存在对应的外箱条码规则");
				}
			}
		}
		Set<Object> isRepeat = new HashSet<Object>();
		for(int i = 0 ; i < grid.size(); i++){
			Object sql = grid.get(i).get("nrd_sql");
			Object detno = grid.get(i).get("nrd_detno");
			Object valuetype = grid.get(i).get("nrd_type");
			Object length = grid.get(i).get("nrd_length");
			Object radix = grid.get(i).get("nrd_radix");
			isRepeat.add(detno);
			if(("流水").equals(valuetype)){
				if(length== null || "".equals(length) || (Integer.valueOf(length.toString()) == 0)){					
					BaseUtil.showError("类型为流水,参数长度不允许为空");
				}else if(Integer.valueOf(length.toString()) < 0){
					BaseUtil.showError("参数长度不合法");
				}
				if(radix== null || "".equals(radix) || (Integer.valueOf(radix.toString()) == 0)){					
					BaseUtil.showError("类型为流水,流水进制不允许为空");
				}else if(Integer.valueOf(radix.toString()) < 0){
					BaseUtil.showError("流水进制不合法");
				}
			}
			if(sql!=null&&!"".equals(sql)){
				if(!("常量").equals(valuetype)){					
					checkSql(sql.toString(),detno);
				}
			}else{
				if(("SQL").equals(valuetype)){
					BaseUtil.showError("类型为SQL,sql语句不允许为空");
				}else if(("常量").equals(valuetype)){
					BaseUtil.showError("类型为常量,sql语句不允许为空");
				}
			}
		}
		if(isRepeat.size()!=grid.size()){
			BaseUtil.showError("序号重复");
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] {store, grid});	
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Norule", new String[] {}, new Object[] {});
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "NoruleDetail", "nrd_id");
		/*baseDao.execute(gridSql);*/
		String errors = baseDao
				.executeWithCheck(gridSql, null,
						"select wm_concat(nrd_type) from (select distinct nrd_type from NoRuleDetail where nrd_nrid = "+store.get("nr_id")+" and nrd_type='流水' group by nrd_type having count(1)>1)");
		if(errors != null && errors != "null"){
			BaseUtil.showError("参数类型为流水的只能存在一个");
		}
		baseDao.execute(formSql);
		baseDao.execute("update norule a set nr_isdefault = 0 where exists(select 1 from norule b where b.nr_id=? and nvl(b.nr_isdefault,0)<>0 and nvl(b.nr_custcode,' ')=nvl(a.nr_custcode,' ') and a.nr_id<>?) "
				+ "and nvl(a.nr_isdefault,0)<>0",store.get("nr_id"),store.get("nr_id"));
		baseDao.execute("update NoRuleDetail  set nrd_nrcode = (select nr_code from norule where nrd_nrid = nr_id) where nrd_nrid = ? and nvl(nrd_nrcode,' ')=' '",store.get("nr_id"));
		
		baseDao.logger.save(caller, "nr_id", store.get("nr_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] {store, grid});			
	}

	@Override
	public void updateNoRule(String caller,String formStore, String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		// 当前编号的记录已经存在,不能修改!
		boolean bool = baseDao.checkByCondition("Norule", "nr_code='" + store.get("nr_code") + "' and nr_id<>"+store.get("nr_id"));
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}	
		if(store.get("nr_custcode") != null && !"".equals(store.get("nr_custcode"))){
			if(("Single").equals(store.get("nr_type"))){				
				bool = baseDao.checkByCondition("Norule", "nr_custcode='" + store.get("nr_custcode") + "' and nr_type = 'Single' and nr_id<>"+store.get("nr_id"));			
				if(!bool){
					BaseUtil.showError("客户编号 :"+store.get("nr_custcode")+"已经存在对应的单盘条码规则");
				}
			}else if(("Out").equals(store.get("nr_type"))){
				bool = baseDao.checkByCondition("Norule", "nr_custcode='" + store.get("nr_custcode") + "' and nr_type = 'Out' and nr_id<>"+store.get("nr_id"));			
				if(!bool){
					BaseUtil.showError("客户编号 :"+store.get("nr_custcode")+"已经存在对应的外箱条码规则");
				}
			}
		}
		Set<Object> isRepeat = new HashSet<Object>();
		for(int i = 0 ; i < grid.size(); i++){
			Object sql = grid.get(i).get("nrd_sql");
			Object detno = grid.get(i).get("nrd_detno");
			Object valuetype = grid.get(i).get("nrd_type");
			Object length = grid.get(i).get("nrd_length");
			Object radix = grid.get(i).get("nrd_radix");
			isRepeat.add(detno);
			if(("流水").equals(valuetype)){
				if(length== null || "".equals(length) || (Integer.valueOf(length.toString()) == 0)){					
					BaseUtil.showError("类型为流水,参数长度不允许为空");
				}else if(Integer.valueOf(length.toString()) < 0){
					BaseUtil.showError("参数长度不合法");
				}
				if(radix== null || "".equals(radix) || (Integer.valueOf(radix.toString()) == 0)){					
					BaseUtil.showError("类型为流水,流水进制不允许为空");
				}else if(Integer.valueOf(radix.toString()) < 0){
					BaseUtil.showError("流水进制不合法");
				}
			}
			if(sql!=null&&!"".equals(sql)){
				if(!("常量").equals(valuetype)){					
					checkSql(sql.toString(),detno);
				}
			}else{
				if(("SQL").equals(valuetype)){
					BaseUtil.showError("类型为SQL,sql语句不允许为空");
				}else if(("常量").equals(valuetype)){
					BaseUtil.showError("类型为常量,sql语句不允许为空");
				}
			}
		}
		if(isRepeat.size()!=grid.size()){
			BaseUtil.showError("序号重复");
		}
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Norule", "nr_id");
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(grid, "NoRuleDetail", "nrd_id");
		/*baseDao.execute(gridSql);*/
		String errors = baseDao
				.executeWithCheck(gridSql, null,
						"select wm_concat(nrd_type) from (select distinct nrd_type from NoRuleDetail where nrd_nrid = "+store.get("nr_id")+" and nrd_type='流水' group by nrd_type having count(1)>1)");
		if(errors != null && errors != "null"){
			BaseUtil.showError("参数类型为流水的只能存在一个");
		}
		baseDao.execute(formSql);
		baseDao.execute("update norule a set nr_isdefault = 0 where exists(select 1 from norule b where b.nr_id=? and nvl(b.nr_isdefault,0)<>0 and nvl(b.nr_custcode,' ')=nvl(a.nr_custcode,' ') and a.nr_id<>?) "
				+ "and nvl(a.nr_isdefault,0)<>0",store.get("nr_id"),store.get("nr_id"));
		baseDao.execute("update NoRuleDetail  set nrd_nrcode = (select nr_code from norule where nrd_nrid = nr_id) where nrd_nrid = ? and nvl(nrd_nrcode,' ')=' '",store.get("nr_id"));
		//记录操作
		baseDao.logger.update(caller, "nr_id", store.get("nr_id"));
}

	@Override
	public void deleteNoRule(String caller, int id) {
	
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, id);
		//删除BarcodeSet
		baseDao.deleteById("NoRule", "nr_id", id);
		//删除BarcodeSetdetail
		baseDao.deleteById("NoRuleDetail", "nrd_nrid", id);
		//记录操作
		baseDao.logger.delete(caller, "nr_id", id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, id);	
	}

	private void checkSql(String sql,Object detno) {
		
		String value = sql.toUpperCase().replaceAll("(\n|\t)", "");
		String regex = "[\\{].*?[\\}]";
		String va = value.replaceAll(regex, "\\'1\\'");
		if(va.matches(".*(UPDATE|DELETE|TRUNCATE|ALTER|DROP|FLUSH|INSERT|SET|CREATE)\\s+.*")){
			BaseUtil.showError("取值SQL语句不允许包含DELETE、UPDATE、DROP行号:"+detno);
		}
		try {
			baseDao.execute(va);
		} catch (Exception e) {
			BaseUtil.showError("取值SQL语句不合法!行号:"+detno);
		}
		
	}

	@Override
	public void saveRuleMaxNum(String caller, String formStore, String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean bool = baseDao.checkByCondition("RuleMaxNum", "rmn_nrcode='" + store.get("rmn_nrcode") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] {store});	
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "RuleMaxNum", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "rmn_id", store.get("rmn_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] {store});			
	
		
	}

	@Override
	public void updateRuleMaxNum(String caller, String formStore, String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能修改!
		boolean bool = baseDao.checkByCondition("RuleMaxNum", "rmn_nrcode='" + store.get("rmn_nrcode") + "' and rmn_id<>"+store.get("rmn_id"));
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}	
		
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "RuleMaxNum", "rmn_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "rmn_id", store.get("rmn_id"));
}

	@Override
	public void deleteRuleMaxNum(String caller, int id) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, id);
		//删除BarcodeSet
		baseDao.deleteById("RuleMaxNum", "rmn_id", id);
		//记录操作
		baseDao.logger.delete(caller, "rmn_id", id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, id);	
	}
	
}
