package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.BomlevelService;

@Service
public class BomlevelServiceImpl implements BomlevelService {

	static final String getproductLeveltype = "select * from Productlevel ";
	static final String getsaletype = "select * from SaleKind ";
	static final String getMaketype = "select * from MakeKind ";

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBomlevel(String formStore, String[] gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Bomlevel", "bl_code='" + store.get("bl_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		handlerService.beforeSave("Bomlevel", new Object[] { formStore });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Bomlevel", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		if (gridStore != null) {
			List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore[0]);
			if (grid == null)
				return;
			Object[] id = new Object[grid.size()];
			for (int i = 0; i < grid.size(); i++) {
				id[i] = baseDao.getSeqId("Productleveldetail_SEQ");
				grid.get(i).put("pd_id", id[i]);
			}
			List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "Productleveldetail");
			baseDao.execute(gridSql);
			// 保存VoucherDetail
			grid = BaseUtil.parseGridStoreToMaps(gridStore[1]);
			id = new Object[grid.size()];
			for (int i = 0; i < grid.size(); i++) {
				id[i] = baseDao.getSeqId("Billtypedetail_SEQ");
				grid.get(i).put("bd_id", id[i]);
			}
			gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "Billtypedetail");
			baseDao.execute(gridSql);

			grid = BaseUtil.parseGridStoreToMaps(gridStore[2]);
			Object[] idx = new Object[grid.size()];
			for (int i = 0; i < grid.size(); i++) {
				idx[i] = baseDao.getSeqId("Maketypedetail_SEQ");
				grid.get(i).put("md_id", idx[i]);
			}
			gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "Maketypedetail");
			baseDao.execute(gridSql);

		}

		try {
			// 记录操作
			baseDao.logger.save(caller, "bl_id", store.get("bl_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave("Bomlevel", new Object[] { formStore });
	}

	@Override
	public void updateBomlevelById(String formStore, String[] gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能对状态为[在录入]的单据进行修改操作!
		Object status = baseDao.getFieldDataByCondition("Bomlevel", "bl_statuscode", "bl_id=" + store.get("bl_id"));
		StateAssert.updateOnlyEntering(status);
		// 当前编号的记录已经存在!
		boolean bool = baseDao.checkByCondition("Bomlevel", "bl_code='" + store.get("bl_code") + "' and bl_id<>" + store.get("bl_id"));
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave("Bomlevel", new Object[] { store });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Bomlevel", "bl_id");
		baseDao.execute(formSql);
		if (gridStore != null) {
			List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore[0]);
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "Productleveldetail", "pd_id");
			for (Map<Object, Object> s : grid) {
				if (s.get("pd_id") == null || s.get("pd_id").equals("") || s.get("pd_id").toString().equals("0")) {// 新添加的数据，id不存在
					int id = baseDao.getSeqId("Productleveldetail_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "Productleveldetail", new String[] { "pd_id" }, new Object[] { id });
					gridSql.add(sql);
				}
			}
			baseDao.execute(gridSql);
			grid = BaseUtil.parseGridStoreToMaps(gridStore[1]);
			gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "Billtypedetail", "bd_id");
			if (grid != null) {
				for (Map<Object, Object> s : grid) {
					if (s.get("bd_id").toString().equals("0") || s.get("bd_id") == null || s.get("bd_id").equals("")) {
						int id = baseDao.getSeqId("Billtypedetail_SEQ");
						String sql = SqlUtil.getInsertSqlByMap(s, "Billtypedetail", new String[] { "bd_id" }, new Object[] { id });
						gridSql.add(sql);
					}
				}
			}
			baseDao.execute(gridSql);
			grid = BaseUtil.parseGridStoreToMaps(gridStore[2]);
			gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "Maketypedetail", "md_id");
			if (grid != null) {
				for (Map<Object, Object> s : grid) {
					if (s.get("md_id") == null || s.get("md_id").equals("") || s.get("md_id").toString().equals("0")) {
						int id = baseDao.getSeqId("Maketypedetail_SEQ");
						String sql = SqlUtil.getInsertSqlByMap(s, "Maketypedetail", new String[] { "md_id" }, new Object[] { id });
						gridSql.add(sql);
					}
				}
			}

			baseDao.execute(gridSql);
		}

		baseDao.logger.update(caller, "bl_id", store.get("bl_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("Employee", new Object[] { store });

	}

	@Override
	public void deleteBomlevel(int bl_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Bomlevel", "bl_statuscode", "bl_id=" + bl_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		handlerService.beforeDel("Bomlevel", new Object[] { bl_id });

		baseDao.deleteById("Bomlevel", "bl_id", bl_id);

		baseDao.deleteById("Billtypedetail", "bd_blid", bl_id);

		baseDao.deleteById("Maketypedetail", "md_blid", bl_id);

		baseDao.deleteById("Productleveldetail", "pd_blid", bl_id);

		baseDao.logger.delete(caller, "bl_id", bl_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("Bomlevel", new Object[] { bl_id });

	}

	@Override
	public void auditBomlevel(int bl_id, String caller) {

		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Bomlevel", "bl_statuscode", "bl_id=" + bl_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("Bomlevel", new Object[] { bl_id });
		// 执行审核操作
		baseDao.audit("Bomlevel", "bl_id=" + bl_id, "bl_status", "bl_statuscode", "bl_auditdate", "bl_auditor");
		// 记录操作
		baseDao.logger.audit(caller, "bl_id", bl_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit("Bomlevel", new Object[] { bl_id });
	}

	@Override
	public void reauditBomlevel(int bl_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Bomlevel", "bl_statuscode", "bl_id=" + bl_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.updateByCondition("Bomlevel", "bl_statuscode='ENTERING',bl_status='" + BaseUtil.getLocalMessage("ENTERING") + "',bl_auditdate='" + "',bl_auditor='" + "'", "bl_id=" + bl_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "bl_id", bl_id);
	}

	@Override
	public void submitBomlevel(int bl_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Bomlevel", "bl_statuscode", "bl_id=" + bl_id);
		StateAssert.submitOnlyEntering(status);
		// 约束方式：禁用和允许只能选择一种
		SqlRowList rs = baseDao.queryForRowSet("select count(1)num from(SELECT distinct case when NVL(pd_useable,0)=0 then 1 else 0 end FROM Productleveldetail where pd_blid='" + bl_id + "') ");
		if (rs.next()) {
			if (rs.getInt("num") > 1) {
				BaseUtil.showError("物料等级是否可用只能全部选择[是]或[否]");
			}
		}
		rs = baseDao.queryForRowSet("select count(1)num from (SELECT distinct case when NVL(md_useable,0)=0 then 1 else 0 end FROM Maketypedetail where md_blid='" + bl_id + "') ");
		if (rs.next()) {
			if (rs.getInt("num") > 1) {
				BaseUtil.showError("制造单类型是否可用只能全部选择[是]或[否]");
			}
		}
		rs = baseDao.queryForRowSet("select count(1)num from(SELECT distinct case when NVL(bd_useable,0)=0 then 1 else 0 end FROM billtypedetail where bd_blid='" + bl_id + "') ");
		if (rs.next()) {
			if (rs.getInt("num") > 1) {
				BaseUtil.showError("订单类型是否可用只能全部选择[是]或[否]");
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit("Bomlevel", new Object[] { bl_id });
		// 执行提交操作
		baseDao.updateByCondition("Bomlevel", "bl_statuscode='COMMITED',bl_status='" + BaseUtil.getLocalMessage("COMMITED") + "'", "bl_id=" + bl_id);
		// 记录操作
		baseDao.logger.submit(caller, "bl_id", bl_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit("Bomlevel", new Object[] { bl_id });

	}

	@Override
	public void resubmitBomlevel(int bl_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Bomlevel", "bl_statuscode", "bl_id=" + bl_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("Bomlevel", new Object[] { bl_id });
		// 执行反提交操作
		baseDao.updateByCondition("Bomlevel", "bl_statuscode='ENTERING',bl_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "bl_id=" + bl_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "bl_id", bl_id);
		handlerService.afterResSubmit("Bomlevel", new Object[] { bl_id });

	}

	@Override
	public void updateBomleveldetail(int id, String caller, String param1, String param2, String param3) {
		JSONArray productLevelDetailArray = JSONArray.fromObject(param1);
		JSONArray billtypeDetailArray = JSONArray.fromObject(param2);
		JSONArray maketypeDetailArray = JSONArray.fromObject(param3);
		JSONObject jsonObject = new JSONObject();
		int i, j = 0, detno = 0;
		String type = null;
		String insertSql = null;
		int productlevelCount = baseDao.getCountByTable("Productlevel");
		int billtypeDetailCount = baseDao.getCountByTable("SaleKind");
		int maketypeDetailCount = baseDao.getCountByTable("MakeKind");
		List<String> sqls = new ArrayList<String>();
		String[] productLevelType = new String[productLevelDetailArray.size()];
		String[] billtypeDetailType = new String[billtypeDetailArray.size()];
		String[] maketypeDetailType = new String[maketypeDetailArray.size()];
		if (productlevelCount != productLevelDetailArray.size()) {
			for (i = 0; i < productLevelDetailArray.size(); i++) {
				jsonObject = productLevelDetailArray.getJSONObject(i);
				productLevelType[i] = jsonObject.getString("bd_plcode");
				if (jsonObject.getInt("bd_detno") > detno) {
					detno = jsonObject.getInt("bd_detno");
				}
			}
			SqlRowList rs = baseDao.queryForRowSet(getproductLeveltype);
			while (rs.next()) {
				type = rs.getString("pl_levcode");
				for (j = 0; j < productLevelType.length; j++) {
					if (type.equals(productLevelType[j])) {
						break;
					}
				}
				if (productLevelType.length == 0) {
					detno = detno + 1;
					insertSql = "insert into Productleveldetail(bd_id,bd_detno,bd_blid,bd_plcode,bd_plid,bd_plremark)values('" + baseDao.getSeqId("Purchasetypedetail_SEQ") + "','" + detno + "','" + id + "','" + type + "','" + rs.getString("pl_id") + "','" + rs.getString("pl_remark") + "')";
					sqls.add(insertSql);
				} else if (j == productLevelType.length && !type.equals(productLevelType[j - 1])) {
					detno = detno + 1;
					insertSql = "insert into Productleveldetail(bd_id,bd_detno,bd_blid,bd_plcode,bd_plid,bd_plremark)values('" + baseDao.getSeqId("Purchasetypedetail_SEQ") + "','" + detno + "','" + id + "','" + type + "','" + rs.getString("pl_id") + "','" + rs.getString("pl_remark") + "')";
					sqls.add(insertSql);
				}
			}
		}

		if (billtypeDetailCount != billtypeDetailArray.size()) {
			detno = 0;
			for (i = 0; i < billtypeDetailArray.size(); i++) {
				jsonObject = billtypeDetailArray.getJSONObject(i);
				billtypeDetailType[i] = jsonObject.getString("bd_type");
				if (jsonObject.getInt("bd_detno") > detno) {
					detno = jsonObject.getInt("bd_detno");
				}
			}
			SqlRowList rs = baseDao.queryForRowSet(getsaletype);
			while (rs.next()) {
				type = rs.getString("sk_name");
				for (j = 0; j < billtypeDetailType.length; j++) {
					if (type.equals(billtypeDetailType[j])) {
						break;
					}
				}
				if (billtypeDetailType.length == 0) {
					detno = detno + 1;
					insertSql = "insert into Billtypedetail(bd_id,bd_detno,bd_blid,bd_type)values('" + baseDao.getSeqId("Billtypedetail_SEQ") + "','" + detno + "','" + id + "','" + type + "')";
					sqls.add(insertSql);
				} else if (j == billtypeDetailType.length && !type.equals(billtypeDetailType[j - 1])) {
					detno = detno + 1;
					insertSql = "insert into Billtypedetail(bd_id,bd_detno,bd_blid,bd_type)values('" + baseDao.getSeqId("Billtypedetail_SEQ") + "','" + detno + "','" + id + "','" + type + "')";
					sqls.add(insertSql);
				}
			}
		}

		if (maketypeDetailCount != maketypeDetailArray.size()) {
			detno = 0;
			for (i = 0; i < maketypeDetailArray.size(); i++) {
				jsonObject = maketypeDetailArray.getJSONObject(i);
				maketypeDetailType[i] = jsonObject.getString("md_prodtype");
				if (jsonObject.getInt("md_detno") > detno) {
					detno = jsonObject.getInt("md_detno");
				}
			}
			SqlRowList rs = baseDao.queryForRowSet(getMaketype);
			while (rs.next()) {
				type = rs.getString("mk_name");
				for (j = 0; j < maketypeDetailType.length; j++) {
					if (type.equals(maketypeDetailType[j])) {
						break;
					}
				}
				if (maketypeDetailType.length == 0) {
					detno = detno + 1;
					insertSql = "insert into Maketypedetail(md_id,md_detno,md_blid,md_prodtype)values('" + baseDao.getSeqId("Maketypedetail_SEQ") + "','" + detno + "','" + id + "','" + type + "')";
					sqls.add(insertSql);
				} else if (j == maketypeDetailType.length && !type.equals(maketypeDetailType[j - 1])) {
					detno = detno + 1;
					insertSql = "insert into Maketypedetail(md_id,md_detno,md_blid,md_prodtype)values('" + baseDao.getSeqId("Maketypedetail_SEQ") + "','" + detno + "','" + id + "','" + type + "')";
					sqls.add(insertSql);
				}
			}
		}
		baseDao.execute(sqls);
	}
}
