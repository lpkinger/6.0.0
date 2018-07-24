package com.uas.erp.service.crm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;

import com.uas.erp.service.crm.ChanceService;

@Service
public class ChanceServiceImpl implements ChanceService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveChance(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Chance",
				"ch_code='" + store.get("ch_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Chance",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "ch_id", store.get("ch_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteChance(int ch_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ch_id });
		// 删除purchase
		baseDao.deleteById("Chance", "ch_id", ch_id);
		// 记录操作
		baseDao.logger.delete(caller, "ch_id", ch_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ch_id);
	}

	@Override
	public void updateChance(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改Chance
		SqlRowList rs = baseDao
				.queryForRowSet("select * from Chance where ch_id="
						+ store.get("ch_id"));
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Chance",
				"ch_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "ch_id", store.get("ch_id"));
		if (rs.next()) {
			if (!rs.getString("ch_code").equals(store.get("ch_code"))
					|| !rs.getString("ch_title").equals(store.get("ch_title"))
					|| !rs.getString("ch_cucode")
							.equals(store.get("ch_cucode"))) {
				updateOther(store.get("ch_code"), store.get("ch_title"),
						store.get("ch_cuname"), store.get("ch_cucode"),
						rs.getString("ch_code"));
			}
		}
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	public void updateOther(Object ch_code, Object ch_title, Object ch_cuname,
			Object ch_cucode, Object oldCode) {
		String scheduleFormStore = "update schedule set sc_chcode='" + ch_code
				+ "',sc_chname='" + ch_title + "',sc_cucode='" + ch_cucode
				+ "',sc_cuname='" + ch_cuname + "' where sc_chcode='" + oldCode
				+ "'";
		String schedulerFormStore = "update scheduler set sc_chcode='"
				+ ch_code + "',sc_chtitle='" + ch_title + "',sc_cucode='"
				+ ch_cucode + "',sc_cuname='" + ch_cuname
				+ "' where sc_chcode='" + oldCode + "'";
		String solutionFormStore = "update solution set so_chcode='" + ch_code
				+ "',so_chname='" + ch_title + "',so_cucode='" + ch_cucode
				+ "',so_cuname='" + ch_cuname + "' where so_chcode='" + oldCode
				+ "'";
		String competitorFormStore = "update competitor set co_chcode='"
				+ ch_code + "',co_chname='" + ch_title + "' where co_chcode='"
				+ oldCode + "'";
		baseDao.execute(scheduleFormStore);
		baseDao.execute(schedulerFormStore);
		baseDao.execute(solutionFormStore);
		baseDao.execute(competitorFormStore);
	}

	@Override
	public void turnStatus(String gridStore, String caller) {
		String sql = "UPDATE Chance SET ch_tasker=?,ch_status='已分配' WHERE ch_id=?";
		List<Map<Object, Object>> maps = BaseUtil
				.parseGridStoreToMaps(gridStore);
		for (Map<Object, Object> map : maps) {
			if (map.get("ch_tasker") == null || "".equals(map.get("ch_tasker"))) {
				BaseUtil.showError("请填写任务执行人再提交！");
			}
			baseDao.execute(sql,
					new Object[] { map.get("ch_tasker"), map.get("ch_id") });
			baseDao.logger.update(caller, "ch_id", map.get("ch_id"));
		}
	}

	@Override
	public void turnEnd(String gridStore, String caller) {
		String sql = "UPDATE chance SET ch_status='已结案' WHERE ch_id=?";
		List<Map<Object, Object>> maps = BaseUtil
				.parseGridStoreToMaps(gridStore);
		for (Map<Object, Object> map : maps) {
			baseDao.execute(sql, new Object[] { map.get("ch_id") });
			baseDao.logger.update(caller, "ch_id", map.get("ch_id"));
		}
	}

	@Override
	public Map<String, Object> getFunnelData(String caller, String condition) {
		JSONObject d = JSONObject.fromObject(condition);// condition:{"ch_tasker":"\u9648\u864e2","chq_cucode":{"ch_cucode":"7.009"}}
		String con = " 1=1";
		if (d.containsKey("ch_tasker") && d.getString("ch_tasker") != null) {
			con += " AND ch_tasker='" + d.getString("ch_tasker") + "'";
		}
		if (d.containsKey("chq_cucode")) {// 蛋疼的API，没这个KEY的时候，居然是报错，不是返回null
			JSONObject chq_custcode = d.get("chq_cucode") == null ? null
					: (JSONObject) d.get("chq_cucode");
			if (chq_custcode != null && chq_custcode.containsKey("ch_cucode")
					&& chq_custcode.getString("ch_cucode") != null) {
				con += " AND ch_cucode='" + chq_custcode.getString("ch_cucode")
						+ "' ";
			}
		}
		String sql = "select sn_name,nvl2(total,total,0) total,sn_detno from StageName b left join "
				+ "(SELECT ch_stage ,count(ch_stage) total from chance where  "
				+ con
				+ "  group by ch_stage) a "
				+ "on b.sn_name=a.ch_stage order by sn_detno ";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> data = null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			data = new HashMap<String, Object>();
			data.put(rs.getObject(1) + "", rs.getObject(2));
			list.add(data);
		}
		map.put("data", list);
		map.put("success", true);
		return map;
	}

	@Override
	public void auditChance(int ch_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Chance",
				"ch_statucode", "ch_id=" + ch_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ch_id);
		// 执行审核操作
		baseDao.updateByCondition("Chance", "ch_statucode='AUDITED',ch_statu='"
				+ BaseUtil.getLocalMessage("AUDITED") + "'", "ch_id=" + ch_id);
		// 记录操作
		baseDao.logger.audit(caller, "ch_id", ch_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ch_id);
		/*
		 * Object[] ch = baseDao.getFieldsDataByCondition("Chance", new
		 * String[]{"ch_prjcode", "ch_prjname", "ch_prjassignto", "ch_code",
		 * "ch_title", "ch_cucode", "ch_cuname"}, "ch_id="+ch_id);
		 */
		/* 更新商机所有阶段的状态 */
		String allstatus = haveAllChancestatus(ch_id, caller);
		System.out.println(allstatus);
		/*
		 * Object
		 * chcode=baseDao.getFieldsDataByCondition("prjprogress","pp_chcode"
		 * ," pp_chcode='"+ch[3]+"'"); if(chcode != null && !chcode.equals("")){
		 * 
		 * }else{ 插入商机进度管理表 int ppid= baseDao.getSeqId("PRJPROGRESS_SEQ");
		 * String sql =
		 * "insert into PrjProgress(pp_id,pp_prjcode,pp_prjname,pp_prjassignto,pp_chcode,pp_chtitle,pp_cucode,pp_cuname) "
		 * + "values("+ppid+",'" + ch[0]+ "','" + ch[1]+ "','" + ch[2]+ "','" +
		 * ch[3]+ "','" + ch[4]+ "','" + ch[5]+ "','" + ch[6]+ "')";
		 * baseDao.execute(sql); }
		 */

	}

	@Override
	public void resAuditChance(int ch_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Chance",
				"ch_statucode", "ch_id=" + ch_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ch_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"Chance",
				"ch_statucode='ENTERING',ch_statu='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ch_auditer='',ch_auditdate=null", "ch_id=" + ch_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ch_id", ch_id);
		handlerService.afterResAudit(caller, ch_id);
	}

	@Override
	public void submitChance(int ch_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Chance",
				"ch_statucode", "ch_id=" + ch_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ch_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"Chance",
				"ch_statucode='COMMITED',ch_statu='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ch_id="
						+ ch_id);
		// 记录操作
		baseDao.logger.submit(caller, "ch_id", ch_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ch_id);
	}

	@Override
	public void resSubmitChance(int ch_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Chance",
				"ch_statuscode", "ch_id=" + ch_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ch_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"Chance",
				"ch_statucode='ENTERING',ch_statu='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ch_id="
						+ ch_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ch_id", ch_id);
		handlerService.afterResSubmit(caller, ch_id);
	}

	@Override
	public String haveAllChancestatus(int ch_id, String caller) {
		Object ch = baseDao.getFieldDataByCondition("Chance", "ch_code",
				"ch_id=" + ch_id);
		String str = "";
		List<Object> objects = baseDao.getFieldDatasByCondition("STAGENAME",
				"sn_name", " 1=1 order by sn_detno");
		for (Object object : objects) {
			int count = baseDao.getCountByCondition("STAGECHANGE",
					" sc_chcode='" + ch + "' and sc_newstage='" + object + "'");
			if (count > 0) {
				str = str + "1";
			} else {
				str = str + "0";
			}
		}
		baseDao.updateByCondition("Chance", "ch_allstatus='" + str + "'",
				"ch_id=" + ch_id);
		return str;
	}

	@Override
	public String haveAllstatus(String gridStore, String caller) {
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		for (Map<Object, Object> mapa : grid) {
			List<Object> objects = baseDao.getFieldDatasByCondition(
					"STAGENAME", "sn_name", " 1=1 order by sn_detno");
			for (Object object : objects) {
				int count = baseDao.getCountByCondition("STAGECHANGE",
						" sc_chcode='" + mapa.get("ch_code")
								+ "' and sc_newstage='" + object
								+ "' and sc_status='已审核'");
				if (count > 0) {
					count = 1;
				} else {
					count = 0;
				}
				map = new HashMap<String, Object>();
				map.put("chcode", mapa.get("ch_code"));
				map.put("name", object);
				map.put("result", count);
				maps.add(map);
			}
		}
		return BaseUtil.parseGridStore2Str(maps);
	}

}
