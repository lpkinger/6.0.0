package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.b2c.model.B2CUtil;
import com.uas.b2c.service.common.B2cProdUpdateService;
import com.uas.b2c.service.common.B2cReserveUpdateService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.WarehouseService;

@Service
public class WarehouseServiceImpl implements WarehouseService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	@Autowired
	private B2CUtil b2cUtil;
	@Autowired
	private B2cProdUpdateService b2cProdUpdateService;
	@Autowired
	private B2cReserveUpdateService b2cReserveUpdateService;
	static final String UPDATEINITSTATUS ="update configs set data = 1 where CODE='b2cInitStatus' AND CALLER='sys'";
	static final String INITCHECK ="update warehouse set wh_ismallstore = 1 where wh_code in ( ";
	protected static final Logger logger = Logger.getLogger("SchedualTask");
	@Override
	public void saveWarehouse(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 判断id是否存在，如果存在，不能新增!
		boolean bool = baseDao.checkByCondition("Warehouse", "wh_id=" + store.get("wh_id"));
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		handlerService.beforeSave(caller, new Object[] { store });
		baseDao.execute(SqlUtil.getInsertSqlByFormStore(store, "Warehouse", new String[] {}, new Object[] {}));
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteWarehouse(String caller, int id) {
		baseDao.delCheck("Warehouse", id);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		// 删除
		baseDao.deleteById("Warehouse", "wh_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	}

	@Override
	public void updateWarehouse(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "Warehouse", "wh_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void submitWarehouse(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Warehouse", "wh_statuscode", "wh_id=" + id);
		StateAssert.submitOnlyEntering(status);
		checkCate(id);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.submit("Warehouse", "wh_id=" + id, "wh_status", "wh_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "wh_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitWarehouse(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Warehouse", "wh_statuscode", "wh_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "resCommit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.resOperate("Warehouse", "wh_id=" + id, "wh_status", "wh_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "wh_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "resCommit", "after", new Object[] { id });
	}

	@Override
	public void auditWarehouse(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Warehouse", "wh_statuscode", "wh_id=" + id);
		StateAssert.auditOnlyCommited(status);
		checkCate(id);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.audit("Warehouse", "wh_id=" + id, "wh_status", "wh_statuscode");
		// 更新物料的不良品仓库存数
		baseDao.execute("update productonhand set po_defectonhand=(select NVL(sum(pw_onhand),0) from productwh inner join warehouse on pw_whcode=wh_code where pw_onhand>0 and wh_ifdefect<>0 and pw_prodcode=po_prodcode) where po_defectonhand<>NVL((select  NVL(sum(pw_onhand),0)from productwh inner join warehouse on pw_whcode=wh_code where  pw_onhand>0 and wh_ifdefect<>0 and pw_prodcode=po_prodcode),0)");
		// 更新物料的MRP仓库存数
		baseDao.execute("update productonhand set po_mrponhand=(select NVL(sum(pw_onhand),0) from productwh inner join warehouse on pw_whcode=wh_code where pw_onhand>0 and wh_ifmrp<>0 and pw_prodcode=po_prodcode) where po_mrponhand<>NVL((select  NVL(sum(pw_onhand),0)from productwh inner join warehouse on pw_whcode=wh_code where  pw_onhand>0 and wh_ifmrp<>0 and pw_prodcode=po_prodcode),0)");
		// 记录操作
		baseDao.logger.audit(caller, "wh_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}

	@Override
	public void resAuditWarehouse(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Warehouse", "wh_statuscode", "wh_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "resAudit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.resOperate("Warehouse", "wh_id=" + id, "wh_status", "wh_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "wh_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "resAudit", "after", new Object[] { id });
	}

	void checkCate(Object id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from Warehouse where wh_id=?", new Object[] { id });
		if (rs.next()) {
			String error = null;
			if (StringUtil.hasText(rs.getObject("wh_catecode"))) {
				error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
						String.class, rs.getObject("wh_catecode"));
				if (error != null) {
					BaseUtil.showError("填写的存货科目不存在，或者状态不等于已审核，或者不是末级科目！");
				}
				error = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MonthAccount!scm','stockCatecode'), chr(10))))",
								String.class, rs.getObject("wh_catecode"));
				if (error != null) {
					BaseUtil.showError("存货科目不是【系统参数设置-->供应链-->库存管理系统-->库存期末处理-->库存对账】中的存货科目！");
				}
			}
			if (StringUtil.hasText(rs.getObject("wh_salecatecode"))) {
				error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
						String.class, rs.getObject("wh_salecatecode"));
				if (error != null) {
					BaseUtil.showError("填写的收入科目不存在，或者状态不等于已审核，或者不是末级科目！");
				}
				error = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MakeCostClose','incomeCatecode'), chr(10))))",
								String.class, rs.getObject("wh_salecatecode"));
				if (error != null) {
					BaseUtil.showError("收入科目不是【系统参数设置-->成本会计管理-->成本核算系统-->成本凭证制作-->主营业务成本结转凭证制作】中的主营业务收入科目！");
				}
			}
			if (StringUtil.hasText(rs.getObject("wh_costcatecode"))) {
				error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
						String.class, rs.getObject("wh_costcatecode"));
				if (error != null) {
					BaseUtil.showError("填写的成本科目不存在，或者状态不等于已审核，或者不是末级科目！");
				}
				error = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MakeCostClose','costCatecode'), chr(10))))",
								String.class, rs.getObject("wh_costcatecode"));
				if (error != null) {
					BaseUtil.showError("成本科目不是【系统参数设置-->成本会计管理-->成本核算系统-->成本凭证制作-->主营业务成本结转凭证制作】中的主营业务成本科目！");
				}
			}
		}
	}

	@Override
	public List<Map<String, Object>> getWarehouse() {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		List<Object[]> obs = baseDao.getFieldsDatasByCondition("warehouse", new String[] { "wh_description","wh_code","wh_id","wh_ismallstore" }, "wh_statuscode='AUDITED' and wh_status='已审核' ORDER BY wh_id DESC");
		if (obs != null) {
			for(Object[] ob : obs){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("wh_description", ob[0]);
				map.put("wh_code", ob[1]);
				map.put("wh_id", ob[2]);
				map.put("wh_ismallstore", ob[3]);
				datas.add(map);
			}
		}
		return datas;
	}

	@Override
	public Map<String, Object> updateIsMallStore(String param) {
		Map<String, Object>  res = new HashMap<String, Object>();
		if(param!=null){
			List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
			if(grid!= null&&grid.size()>0){
				String truecondition = "";
				String falsecondition = "";
				for (Map<Object, Object> map : grid) {
					if(map.get("wh_ismallstore")!=null&&map.get("wh_ismallstore").toString().equals("true")){
						truecondition+=map.get("wh_id").toString()+",";
					}else{
						falsecondition+=map.get("wh_id").toString()+",";
					}
				}		
				if(truecondition.length()!=0){
					truecondition = truecondition.substring(0,truecondition.length() - 1);
					baseDao.updateByCondition("warehouse", "wh_ismallstore=1", "wh_id in ("+truecondition+")");
				}
				if(falsecondition.length()!=0){
					falsecondition = falsecondition.substring(0,falsecondition.length() - 1);
					baseDao.updateByCondition("warehouse", "wh_ismallstore=0", "wh_id in ("+falsecondition+")");
				}
				//判断是否初始化过  
				if(baseDao.checkIf("configs","CODE='b2cInitStatus' AND CALLER='sys' AND DATA<>0")){
					baseDao.execute(UPDATEINITSTATUS);
					logger.info(this.getClass() + " 更新初始化状态： b2cInitStatus");
					b2cUtil.insertB2CActionTaskLog("b2cInitStatus", "end", "更新初始化状态：b2cInitStatus");
				}
				res = b2cProdUpdateService.execute();//初始化物料信息、交易信息
				b2cReserveUpdateService.execute();//初始化仓库信息
			}
		}
		return res;
	}

}
