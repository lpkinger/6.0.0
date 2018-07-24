package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.CurrencysService;

@Service
public class CurrencysServiceImpl implements CurrencysService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCurrencys(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Currencys", "cr_code='" + store.get("cr_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Currencys", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "cr_id", store.get("cr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteCurrencys(int cr_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, cr_id);
		baseDao.delCheck("Currencys", cr_id);
		// 删除
		baseDao.deleteById("Currencys", "cr_id", cr_id);
		// 记录操作
		baseDao.logger.delete(caller, "cr_id", cr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, cr_id);
	}

	@Override
	public void updateCurrencysById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Currencys", "cr_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "cr_id", store.get("cr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void bannedCurrencys(int cr_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Currencys", "cr_statuscode", "cr_id=" + cr_id);
		if (!status.equals("CANUSE")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.ARBill.audit_onlyCommited"));
		}

		baseDao.updateByCondition("Currencys", "cr_statuscode='DISABLE',cr_status='" + BaseUtil.getLocalMessage("DISABLE") + "'", "cr_id="
				+ cr_id);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.banned"), BaseUtil
				.getLocalMessage("msg.bannedSuccess"), "Currencys|cr_id=" + cr_id));
	}

	@Override
	public void resBannedCurrencys(int cr_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Currencys", "cr_statuscode", "cr_id=" + cr_id);
		if (!status.equals("DISABLE")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.ARBill.audit_onlyCommited"));
		}

		baseDao.updateByCondition("Currencys", "cr_statuscode='CANUSE',cr_status='" + BaseUtil.getLocalMessage("CANUSE") + "'", "cr_id="
				+ cr_id);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.resBanned"), BaseUtil
				.getLocalMessage("msg.resBannedSuccess"), "Currencys|cr_id=" + cr_id));
	}

	@Override
	public void updateCurrencysMonth(String gridStore, String caller, String mf) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<String> gridSql = null;
		List<Object> os = new ArrayList<Object>();
		boolean tt = false;
		int id = 0;
		if (gstore.size() > 0) {
			gridSql = new ArrayList<String>();
			for (Map<Object, Object> s : gstore) {
				if (os != null && os.contains(s.get("cm_crname"))) {
					tt = true;
				} else {
					Object oss = s.get("cm_crname");
					os.add(oss);
					s.put("cm_yearmonth", mf);
					id = baseDao.getSeqId("CURRENCYSMONTH_SEQ");
					s.put("cm_id", id);
					s.put("cm_code", id);
					Object[] old = baseDao.getFieldsDataByCondition("CurrencysMonth", new String[] { "cm_crname", "nvl(cm_crrate,0)",
							"nvl(cm_endrate,0)" }, "cm_yearmonth=" + mf + " and cm_crname='" + s.get("cm_crname") + "'");
					if (old == null && s.get("cm_crrate") != null) {
						baseDao.logger.others("增加：" + mf + " " + s.get("cm_crname") + " 月初汇率" + s.get("cm_crrate"), "新增成功",
								"CurrencysMonth", "cm_yearmonth", mf);
					} else {
						baseDao.execute("delete from CurrencysMonth where cm_yearmonth=" + mf + " and cm_crname='" + s.get("cm_crname")
								+ "'");
						String log = "修改：" + mf + " " + s.get("cm_crname");
						boolean bool = false;
						if (s.get("cm_crrate") != null && old[1] != null && !old[1].toString().equals(s.get("cm_crrate").toString())) {
							log = log + " 月度汇率" + old[1] + "改为" + s.get("cm_crrate");
							bool = true;
						}
						if (s.get("cm_endrate") != null && old[2] != null && !old[2].toString().equals(s.get("cm_endrate").toString())) {
							log = log + " 月末汇率" + old[2] + "改为" + s.get("cm_endrate");
							bool = true;
						}
						if (bool) {
							baseDao.logger.others(log, "更新成功", "CurrencysMonth", "cm_yearmonth", mf);
						}
					}
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "CurrencysMonth"));
				}
			}
			baseDao.execute(gridSql);
			if (DateUtil.getYearmonth() == Integer.parseInt(mf)) {
				baseDao.execute("update Currencys set cr_rate=(select nvl(cm_crrate,0) from CurrencysMonth where cm_yearmonth=" + mf
						+ " and cm_crname=cr_name)");
			}
			if (tt) {
				BaseUtil.showError("币别已存在！");
			}
		}

	}

	@Override
	public void deleteCurrencysMonth(String caller, String id) {
		String sql = "delete from currencysmonth where cm_id='" + id + "'";
		baseDao.execute(sql);
		baseDao.logger.others("删除月度汇率", "删除成功", "CurrencysMonth", "cm_id", id);
	}

	@Override
	public List<Map<String, Object>> getLastEndRate(String last, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select cm_id,cm_code,cm_crname,cm_endrate from CurrencysMonth where cm_yearmonth=?", last);
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		while (rs.next()) {
			map = new HashMap<String, Object>();
			map.put("cm_id", rs.getGeneralInt(1));
			map.put("cm_code", rs.getString(2));
			map.put("cm_crname", rs.getString(3));
			map.put("cm_endrate", rs.getGeneralDouble(4));
			maps.add(map);
		}
		return maps;
	}
}
