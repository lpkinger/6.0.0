package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DbfindSetGridDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.model.DBFindSetGrid;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.Dbfind;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.LimitFields;
import com.uas.erp.service.pm.MRPDataService;

@Service("MRPDataService")
public class MRPDataServiceImpl implements MRPDataService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private HrJobDao hrJobDao;
	@Autowired
	private DbfindSetGridDao dbfindSetGridDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMRPData(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MRPData", "md_id='" + store.get("md_id") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MRPData", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "md_id", store.get("md_id"));
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteMRPData(int md_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MRPData", "md_statuscode", "md_id=" + md_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { md_id });
		// 删除BOM
		baseDao.deleteById("MRPData", "md_id", md_id);
		// 记录操作
		baseDao.logger.delete(caller, "md_id", md_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { md_id });
	}

	@Override
	public void updateMRPDataById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MRPData", "md_statuscode", "md_id" + store.get("md_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MRPData", "md_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "md_id", store.get("md_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void auditMRPData(int md_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MRPData", "md_statuscode", "md_id=" + md_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { md_id }); // 执行审核操作
		baseDao.audit("MRPData", "md_id=" + md_id, "md_status", "md_statuscode", "md_auditdate", "md_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "md_id", md_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { md_id });
	}

	@Override
	public void resAuditMRPData(int md_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MRPData", "md_statuscode", "md_id=" + md_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("MRPData", "md_id=" + md_id, "md_status", "md_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "md_id", md_id);
	}

	@Override
	public void submitMRPData(int md_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MRPData", "md_statuscode", "md_id=" + md_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { md_id });
		// 执行提交操作
		baseDao.submit("MRPData", "md_id=" + md_id, "md_status", "md_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "md_id", md_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { md_id });
	}

	@Override
	public void resSubmitMRPData(int md_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MRPData", "md_statuscode", "md_id=" + md_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { md_id });
		// 执行反提交操作
		baseDao.resOperate("MRPData", "md_id=" + md_id, "md_status", "md_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "md_id", md_id);
		handlerService.afterResSubmit(caller, new Object[] { md_id });
	}

	@Override
	public void updateFieldData(String caller, String data, String field, String keyField, String keyValue) {
		// TODO Auto-generated method stub
		String sql = "";
		Object tablename = baseDao.getFieldDataByCondition("Form", "fo_detailtable", "fo_caller='" + caller + "'");
		if (tablename != null) {
			tablename = tablename.toString().split("left")[0];
		}
		Object old_data = baseDao.getFieldDataByCondition(tablename.toString(), field, keyField + "=" + keyValue);
		if (data != null && data.matches(SqlUtil.REG_D)) {
			// 日期格式
			sql = "update " + tablename + " set " + field + " =" + DateUtil.parseDateToOracleString(Constant.YMD, data) + "  where "
					+ keyField + "=" + keyValue;
		} else{
			sql = "update " + tablename + " set " + field + " =" + data + " where " + keyField + "=" + keyValue;
		}
		baseDao.execute(sql);
		baseDao.logger.others("修改建议变更数", "原值："+old_data.toString()+"，新值："+data+"，修改建议变更数成功", caller, keyField, keyValue);
	}

	@Override
	public JSONObject getMrpData(String caller, String condition, int page, int start, int limit) {
		JSONObject obj = new JSONObject();
		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, SystemSession.getUser().getEm_master());
		if (detailGrids != null && detailGrids.size() > 0) {
			List<Map<String, Object>> data = baseDao.getDetailGridData(detailGrids, condition, SystemSession.getUser(), null, null);
			obj.put("data", data);
			obj.put("totalCount", data.size());

		}
		return obj;
	}

	@Override
	public GridPanel getMRPThrowConfig(String caller, String condition) {
		// TODO Auto-generated method stub
		GridPanel gridPanel = new GridPanel();
		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, SystemSession.getUser().getEm_master());
		if (detailGrids != null && detailGrids.size() > 0) {
			List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, SystemSession.getUser().getEm_master());
			List<GridFields> fields = new ArrayList<GridFields>();// grid
																	// store的字段fields
			List<GridColumns> columns = new ArrayList<GridColumns>();// grid的列信息columns
			List<LimitFields> limits = new ArrayList<LimitFields>();
			if (!"admin".equals(SystemSession.getUser().getEm_type())) {
				limits = hrJobDao.getLimitFieldsByType(caller, null, 0, SystemSession.getUser().getEm_defaulthsid(), SystemSession
						.getUser().getEm_master());
			}
			gridPanel.setLimits(limits);// 权限控制字段
			for (DetailGrid grid : detailGrids) {
				// 从数据库表detailgrid的数据，通过自定义的构造器，转化为extjs识别的fields格式，详情可见GridFields的构造函数
				fields.add(new GridFields(grid));
				columns.add(new GridColumns(grid, combos));
			}
			gridPanel.setGridColumns(columns);
			gridPanel.setGridFields(fields);
			List<DBFindSetGrid> dbFindSetGrids = dbfindSetGridDao.getDbFindSetGridsByCaller(caller);
			List<Dbfind> dbfinds = new ArrayList<Dbfind>();
			for (DBFindSetGrid dbFindSetGrid : dbFindSetGrids) {
				dbfinds.add(new Dbfind(dbFindSetGrid));
			}
			gridPanel.setDbfinds(dbfinds);
		}
		return gridPanel;
	}
}
