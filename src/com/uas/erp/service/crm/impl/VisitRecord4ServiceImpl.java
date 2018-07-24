package com.uas.erp.service.crm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.JSONUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.VisitRecord4Service;

@Service
public class VisitRecord4ServiceImpl implements VisitRecord4Service {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional
	public void saveVisitRecord(String formStore, String[] gridStore,
			String caller) {
		Map<Object, Object> store = JSONUtil.toMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("VisitRecord", "vr_code='"
				+ store.get("vr_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler("VisitRecord!Resource", "save", "before",
				new Object[] { store });
		// 保存VisitRecord
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VisitRecord",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存VisitRecordDetail
		List<Map<Object, Object>> grid0 = BaseUtil
				.parseGridStoreToMaps(gridStore[0]);
		for (Map<Object, Object> map : grid0) {
			map.put("vrd_id", baseDao.getSeqId("VisitRecordDetail_SEQ"));
		}
		List<String> gridSql0 = SqlUtil.getInsertSqlbyGridStore(grid0,
				"VisitRecordDetail");
		baseDao.execute(gridSql0);

		// 保存Players
		List<Map<Object, Object>> grid1 = BaseUtil
				.parseGridStoreToMaps(gridStore[1]);
		for (Map<Object, Object> map : grid1) {
			map.put("pl_id", baseDao.getSeqId("Players_SEQ"));
		}
		List<String> gridSql1 = SqlUtil.getInsertSqlbyGridStore(grid1,
				"Players");
		baseDao.execute(gridSql1);
		// 保存CuPlayers
		List<Map<Object, Object>> grid2 = BaseUtil
				.parseGridStoreToMaps(gridStore[2]);
		for (Map<Object, Object> map : grid2) {
			map.put("cup_id", baseDao.getSeqId("CuPlayers_SEQ"));
		}
		List<String> gridSql2 = SqlUtil.getInsertSqlbyGridStore(grid2,
				"CuPlayers");
		baseDao.execute(gridSql2);
		// 保存Marketing
		List<Map<Object, Object>> grid3 = BaseUtil
				.parseGridStoreToMaps(gridStore[3]);
		for (Map<Object, Object> map : grid3) {
			map.put("ma_id", baseDao.getSeqId("Marketing_SEQ"));
		}
		List<String> gridSql3 = SqlUtil.getInsertSqlbyGridStore(grid3,
				"Marketing");
		baseDao.execute(gridSql3);
		// 保存VenderMaketing
		List<Map<Object, Object>> grid4 = BaseUtil
				.parseGridStoreToMaps(gridStore[4]);
		for (Map<Object, Object> map : grid4) {
			map.put("vm_id", baseDao.getSeqId("VenderMaketing_SEQ"));
		}
		List<String> gridSql4 = SqlUtil.getInsertSqlbyGridStore(grid4,
				"VenderMaketing");
		baseDao.execute(gridSql4);
		// 保存Rival
		List<Map<Object, Object>> grid5 = BaseUtil
				.parseGridStoreToMaps(gridStore[5]);
		for (Map<Object, Object> map : grid5) {
			map.put("ri_id", baseDao.getSeqId("Rival_SEQ"));
		}
		List<String> gridSql5 = SqlUtil.getInsertSqlbyGridStore(grid5, "Rival");
		baseDao.execute(gridSql5);
		// 保存Price
		List<Map<Object, Object>> grid6 = BaseUtil
				.parseGridStoreToMaps(gridStore[6]);
		for (Map<Object, Object> map : grid6) {
			map.put("pr_id", baseDao.getSeqId("Price_SEQ"));
		}
		List<String> gridSql6 = SqlUtil.getInsertSqlbyGridStore(grid6, "Price");
		baseDao.execute(gridSql6);
		// 保存Expect
		List<Map<Object, Object>> grid7 = BaseUtil
				.parseGridStoreToMaps(gridStore[7]);
		for (Map<Object, Object> map : grid7) {
			map.put("ex_id", baseDao.getSeqId("Expect_SEQ"));
		}
		List<String> gridSql7 = SqlUtil
				.getInsertSqlbyGridStore(grid7, "Expect");
		baseDao.execute(gridSql7);
		// 保存ProductPlanning
		List<Map<Object, Object>> grid8 = BaseUtil
				.parseGridStoreToMaps(gridStore[8]);
		for (Map<Object, Object> map : grid8) {
			map.put("pp_id", baseDao.getSeqId("ProductPlanning_SEQ"));
		}
		List<String> gridSql8 = SqlUtil.getInsertSqlbyGridStore(grid8,
				"ProductPlanning");
		baseDao.execute(gridSql8);

		try {
			// 记录操作
			baseDao.logger.save(caller, "vr_id", store.get("vr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.handler("VisitRecord!Resource", "save", "after",
				new Object[] { store });
	}

	@Override
	@Transactional
	public void updateVisitRecordById(String formStore, String[] gridStore,
			String caller) {
		Map<Object, Object> store = JSONUtil.toMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("VisitRecord",
				"vr_statuscode", "vr_id=" + store.get("vr_id"));
		StateAssert.updateOnlyEntering(status);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VisitRecord",
				"vr_id");
		baseDao.execute(formSql);
		// 修改VisitRecordDetail
		List<Map<Object, Object>> grid0 = BaseUtil
				.parseGridStoreToMaps(gridStore[0]);
		List<String> gridSql0 = null;
		if (grid0.size() > 0) {
			gridSql0 = SqlUtil.getUpdateSqlbyGridStore(grid0,
					"VisitRecordDetail", "vrd_id");
			for (Map<Object, Object> s : grid0) {
				Object aid = s.get("vrd_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("VisitRecordDetail_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s,
							"VisitRecordDetail", new String[] { "vrd_id" },
							new Object[] { id });
					gridSql0.add(sql);
				}
			}
			baseDao.execute(gridSql0);
		}
		// 修改Players
		List<Map<Object, Object>> grid1 = BaseUtil
				.parseGridStoreToMaps(gridStore[1]);
		List<String> gridSql1 = null;
		if (grid1.size() > 0) {
			gridSql1 = SqlUtil.getUpdateSqlbyGridStore(grid1, "Players",
					"pl_id");
			for (Map<Object, Object> s : grid1) {
				Object aid = s.get("pl_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("PLAYERS_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "Players",
							new String[] { "pl_id" }, new Object[] { id });
					gridSql1.add(sql);
				}
			}
			baseDao.execute(gridSql1);
		}
		// 修改CuPlayers
		List<Map<Object, Object>> grid2 = BaseUtil
				.parseGridStoreToMaps(gridStore[2]);
		List<String> gridSql2 = null;
		if (grid2.size() > 0) {
			gridSql2 = SqlUtil.getUpdateSqlbyGridStore(grid2, "CuPlayers",
					"cup_id");
			for (Map<Object, Object> s : grid2) {
				Object aid = s.get("cup_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("CuPlayers_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "CuPlayers",
							new String[] { "cup_id" }, new Object[] { id });
					gridSql2.add(sql);
				}
			}
			baseDao.execute(gridSql2);
		}
		// 修改Marketing
		List<Map<Object, Object>> grid3 = BaseUtil
				.parseGridStoreToMaps(gridStore[3]);
		List<String> gridSql3 = null;
		if (grid3.size() > 0) {
			gridSql3 = SqlUtil.getUpdateSqlbyGridStore(grid3, "Marketing",
					"ma_id");
			for (Map<Object, Object> s : grid3) {
				Object aid = s.get("ma_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("Marketing_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "Marketing",
							new String[] { "ma_id" }, new Object[] { id });
					gridSql3.add(sql);
				}
			}
			baseDao.execute(gridSql3);
		}
		// 修改VenderMaketing
		List<Map<Object, Object>> grid4 = BaseUtil
				.parseGridStoreToMaps(gridStore[4]);
		List<String> gridSql4 = null;
		if (grid4.size() > 0) {
			gridSql4 = SqlUtil.getUpdateSqlbyGridStore(grid4, "VenderMaketing",
					"vm_id");
			for (Map<Object, Object> s : grid4) {
				Object aid = s.get("vm_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("VenderMaketing_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "VenderMaketing",
							new String[] { "vm_id" }, new Object[] { id });
					gridSql4.add(sql);
				}
			}
			baseDao.execute(gridSql4);
		}
		// 修改Rival
		List<Map<Object, Object>> grid5 = BaseUtil
				.parseGridStoreToMaps(gridStore[5]);
		List<String> gridSql5 = null;
		if (grid5.size() > 0) {
			gridSql5 = SqlUtil.getUpdateSqlbyGridStore(grid5, "Rival", "ri_id");
			for (Map<Object, Object> s : grid5) {
				Object aid = s.get("ri_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("Rival_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "Rival",
							new String[] { "ri_id" }, new Object[] { id });
					gridSql5.add(sql);
				}
			}
			baseDao.execute(gridSql5);
		}
		// 修改Price
		List<Map<Object, Object>> grid6 = BaseUtil
				.parseGridStoreToMaps(gridStore[6]);
		List<String> gridSql6 = null;
		if (grid6.size() > 0) {
			gridSql6 = SqlUtil.getUpdateSqlbyGridStore(grid6, "Price", "pr_id");
			for (Map<Object, Object> s : grid6) {
				Object aid = s.get("pr_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("Price_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "Price",
							new String[] { "pr_id" }, new Object[] { id });
					gridSql6.add(sql);
				}
			}
			baseDao.execute(gridSql6);
		}
		// 修改Expect
		List<Map<Object, Object>> grid7 = BaseUtil
				.parseGridStoreToMaps(gridStore[7]);
		List<String> gridSql7 = null;
		if (grid7.size() > 0) {
			gridSql7 = SqlUtil
					.getUpdateSqlbyGridStore(grid7, "Expect", "ex_id");
			for (Map<Object, Object> s : grid7) {
				Object aid = s.get("ex_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("Expect_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "Expect",
							new String[] { "ex_id" }, new Object[] { id });
					gridSql7.add(sql);
				}
			}
			baseDao.execute(gridSql7);
		}
		// 修改Expect
		List<Map<Object, Object>> grid8 = BaseUtil
				.parseGridStoreToMaps(gridStore[8]);
		List<String> gridSql8 = null;
		if (grid8.size() > 0) {
			gridSql8 = SqlUtil.getUpdateSqlbyGridStore(grid8,
					"ProductPlanning", "pp_id");
			for (Map<Object, Object> s : grid8) {
				Object aid = s.get("pp_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("ProductPlanning_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s,
							"ProductPlanning", new String[] { "pp_id" },
							new Object[] { id });
					gridSql8.add(sql);
				}
			}
			baseDao.execute(gridSql8);
		}
		// 记录操作
		baseDao.logger.update(caller, "vr_id", store.get("vr_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("VisitRecord!Resource", "save", "after",
				new Object[] { store });
	}

	@Override
	@Transactional
	public void deleteVisitRecord(int vr_id, String caller) {
		// 删除
		baseDao.deleteById("VisitRecord", "vr_id", vr_id);
		// 删除CuPlayers
		baseDao.deleteById("CuPlayers", "cup_vrid", vr_id);
		// 删除VisitRecordDetail
		baseDao.deleteById("VisitRecordDetail", "vrd_vrid", vr_id);
		// 删除Players
		baseDao.deleteById("Players", "pl_vrid", vr_id);
		// 删除Marketing
		baseDao.deleteById("Marketing", "ma_vrid", vr_id);
		// 删除VenderMaketing
		baseDao.deleteById("VenderMaketing", "vm_vrid", vr_id);
		// 删除Rival
		baseDao.deleteById("Rival", "ri_vrid", vr_id);
		// 删除Price
		baseDao.deleteById("Price", "pr_vrid", vr_id);
		// 删除Expect
		baseDao.deleteById("Expect", "ex_vrid", vr_id);
		// 删除ProductPlanning
		baseDao.deleteById("ProductPlanning", "pp_vrid", vr_id);
		// 记录操作
		baseDao.logger.delete(caller, "vr_id", vr_id);
		// 执行删除后的其它逻辑
		handlerService.handler("VisitRecord!Resource", "delete", "after",
				new Object[] { vr_id });
	}

	@Transactional
	@Override
	public String turnFeePlease(int vr_id, String caller) {// 又变成转差旅费报销了
		int id = baseDao.getSeqId("FeePlease_seq");
		String code = baseDao.sGetMaxNumber("FeePlease!FYBX", 2);// 差旅费报销取费用报销同样的caller取编号
		Object[] data = baseDao.getFieldsDataByCondition(
				"VisitRecord left join employee on vr_recorder=em_name",
				new String[] { "em_name", "em_depart", "vr_code" }, "vr_id="
						+ vr_id);// 取原表单的录入人作为出差费用申请的申请人
		String insertSql = "insert into FeePlease(fp_code,fp_pleaseman,fp_department,fp_status,fp_recordman,fp_kind,fp_recorddate,fp_sourcecode,fp_sourcekind,fp_id,fp_statuscode)"
				+ " values(?,?,?,?,?,?,sysdate,?,?,?,?)";
		baseDao.execute(insertSql, new Object[] { code, data[0], data[1],
				"在录入", SystemSession.getUser().getEm_name(), "差旅费报销单", data[2],
				"资源开发记录", id, "ENTERING" });
		String insertDetSql = "insert into FeePleasedetail (fpd_detno,fpd_d1,fpd_n7,fpd_d3,fpd_id,fpd_fpid) "
				+ "select vrd_detno,vrd_d1,vrd_n7,vrd_d3,FeePleasedetail_seq.nextval,"
				+ id + " from VisitRecorddetail where vrd_vrid=" + vr_id;
		baseDao.execute(insertDetSql);
		baseDao.updateByCondition("VisitRecord", "vr_isturnfeeplease='1'",
				"vr_id=" + vr_id);
		String log = "转入成功,差旅费报销单号:"
				+ "<a href=\"javascript:openUrl('jsps/oa/fee/feePlease.jsp?whoami=FeePlease!CLFBX&formCondition=fp_idIS"
				+ id + "&gridCondition=fpd_fpidIS" + id + "')\">" + code
				+ "</a>";
		return log;
	}

}
