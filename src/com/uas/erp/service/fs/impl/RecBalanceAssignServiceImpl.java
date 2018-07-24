package com.uas.erp.service.fs.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.model.Key;
import com.uas.erp.service.fs.RecBalanceAssignService;

@Service
public class RecBalanceAssignServiceImpl implements RecBalanceAssignService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private TransferRepository transferRepository;

	@Override
	public void updateRecBalanceAssign(String caller, String formStore, String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridStore = BaseUtil.parseGridStoreToMaps(param);

		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gridStore });

		if (!StringUtil.hasText(store.get("ra_recorder"))) {
			store.put("ra_recorder", SystemSession.getUser().getEm_name());
		}

		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "RecBalanceAssign", "ra_id");
		baseDao.execute(formSql);

		List<String> gridSql = new ArrayList<String>();
		for (Map<Object, Object> map : gridStore) {
			if (StringUtil.hasText(map.get("rad_id")) && Integer.parseInt(String.valueOf(map.get("rad_id"))) > 0) {
				gridSql.add(SqlUtil.getUpdateSqlByFormStore(map, "RecBalanceAssignDet", "rad_id"));
			} else {
				map.put("rad_id", baseDao.getSeqId("RECBALANCEASSIGNDET_SEQ"));
				gridSql.add(SqlUtil.getInsertSql(map, "RecBalanceAssignDet", "rad_id"));
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ra_id", store.get("ra_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gridStore });
	}

	@Override
	public void deleteRecBalanceAssign(int ra_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { ra_id });
		// 删除主表内容
		baseDao.deleteById("RecBalanceAssign", "ra_id", ra_id);

		// 删除从表
		baseDao.deleteById("RecBalanceAssignDet", "rad_raid", ra_id);

		// 记录日志
		baseDao.logger.delete(caller, "ra_id", ra_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { ra_id });
	}

	@Override
	public void submitRecBalanceAssign(int ra_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("RecBalanceAssign", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.submitOnlyEntering(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat('序号['||rad_detno||']订单号['||rad_sacode) from RecBalanceAssignDet a where rad_raid=? and exists (select 1 from RecBalanceAssign left join RecBalanceAssignDet b on b.rad_raid=ra_id where ra_statuscode in ('COMMITED','AUDITED') and ra_id<>? and b.rad_sacode=a.rad_sacode)",
						String.class, ra_id, ra_id);
		if (dets != null) {
			BaseUtil.showError("订单存在于其他未审核的应收账款转让单！" + dets);
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { ra_id });

		// 执行提交操作
		baseDao.submit("RecBalanceAssign", "ra_id=" + ra_id, "ra_status", "ra_statuscode");

		// 记录操作
		baseDao.logger.submit(caller, "ra_id", ra_id);

		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { ra_id });
	}

	@Override
	public void resSubmitRecBalanceAssign(int ra_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("RecBalanceAssign", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { ra_id });

		// 执行反提交操作
		baseDao.resOperate("RecBalanceAssign", "ra_id=" + ra_id, "ra_status", "ra_statuscode");

		// 记录操作
		baseDao.logger.resSubmit(caller, "ra_id", ra_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { ra_id });
	}

	@Override
	public void auditRecBalanceAssign(int ra_id, String caller) {
		// 只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("RecBalanceAssign", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.auditOnlyCommited(status);

		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { ra_id });
		baseDao.audit("RecBalanceAssign", "ra_id=" + ra_id, "ra_status", "ra_statuscode", "ra_auditdate", "ra_auditman");

		// 记录操作
		baseDao.logger.audit(caller, "ra_id", ra_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { ra_id });
	}

	@Override
	public void assignRecBalance(int id, String caller) {
		boolean bool = baseDao.checkIf("RECBALANCEASSIGN", "NVL(RA_ISASSIGN,0)<>0 AND RA_ID =" + id);
		if (bool) {
			BaseUtil.showError("应收账款转让已转，不能重复转！");
		}
		Object[] recBalanceAssign = baseDao.getFieldsDataByCondition(
				"FINANCECORPORATION INNNER JOIN RECBALANCEASSIGN ON FC_CODE = RA_FCCODE", new String[] { "RA_CQCODE", "FC_URL",
						"FC_WHICHSYSTEM","ra_custcode","ra_custname" }, "RA_ID =" + id);

		if (recBalanceAssign == null) {
			BaseUtil.showError("保理公司账套信息不存在，请维护！");
		}
		SqlRowList rs = baseDao.queryForRowSet("select * from RECBALANCEASSIGNDET where rad_raid = ?", id);
		if (!rs.hasNext()) {
			BaseUtil.showError("没有明细订单，不能应收账款转让！");
		}
		List<Map<String, Object>> sales = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> sale = new HashMap<String, Object>();
			sale.put("sa_id", rs.getGeneralLong("rad_said"));
			sale.put("sa_contractno", rs.getString("rad_contractno"));
			sale.put("sa_contractamount", rs.getGeneralDouble("rad_contractamount"));
			sale.put("sa_code", rs.getString("rad_sacode"));
			sale.put("sa_date", DateUtil.format(rs.getDate("rad_date"), Constant.YMD_HMS));
			sale.put("sa_currency", rs.getString("rad_currency"));
			sale.put("sa_rate", rs.getGeneralDouble("rad_rate"));
			sale.put("sa_mfcustname", rs.getString("rad_apcustname"));
			sale.put("sa_total", rs.getGeneralDouble("rad_amount"));
			sale.put("sa_payment", rs.getString("rad_payments"));
			sales.add(sale);
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("custcode", String.valueOf(recBalanceAssign[3]));
		params.put("custname", String.valueOf(recBalanceAssign[4]));
		params.put("cqcode", String.valueOf(recBalanceAssign[0]));
		params.put("sales", FlexJsonUtil.toJsonArray(sales));
		try {
			Response response = HttpUtil.sendPostRequest(recBalanceAssign[1]+"/openapi/applicant/AssignRecBalance.action?master="+recBalanceAssign[2], params, true);
			
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				baseDao.updateByCondition("RECBALANCEASSIGN", "RA_ISASSIGN = -1", "RA_ID =" + id);
				baseDao.logger.others("应收账款转让", "转让成功", caller, "ra_id", id);
			} else {
				throw new Exception("连接保理公司失败," + response.getStatusCode());
			}
		} catch (Exception e) {
			BaseUtil.showError("错误：" + e.getMessage());
		}
	}

	
	@Override
	@Transactional
	public String turnRecBalanceAssign(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "sa_id"), ",");
		String codes = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(distinct '转让单号['||ra_code||']，订单编号['||rad_sacode||']' ) from RecBalanceAssign,RECBALANCEASSIGNDET where ra_id=rad_raid and rad_said in ("
								+ ids + ")", String.class);
		if (codes != null) {
			BaseUtil.showError("勾选的订单在其他应收款转让单中已存在！" + codes);
		}
		String log = "";
		StringBuffer sb = new StringBuffer();
		int index = 0;
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "sa_apcustcode", "cuc_id" });
		Set<Object> mapSet = groups.keySet();
		List<Map<Object, Object>> items;
		Object cuc_id = null;
		for (Object s : mapSet) {
			items = groups.get(s);
			cuc_id = items.get(0).get("cuc_id");
			Key key = transferRepository.transfer("RecBalanceAssign!Deal", cuc_id);
			if (key != null) {
				int ra_id = key.getId();
				index++;
				// 转入明细
				transferRepository.transfer("RecBalanceAssign!Deal", items, key);
				baseDao.execute(
						"update RecBalanceAssign set ra_quota=round((select sum(rad_amount) from RECBALANCEASSIGNDET where ra_id=rad_raid),2) where ra_id=?",
						ra_id);
				log = "应收款转让单:<a href=\"javascript:openUrl('jsps/fs/cust/recBalanceAssign.jsp?formCondition=ra_idIS" + ra_id
						+ "&gridCondition=rad_raidIS" + ra_id + "')\">" + key.getCode() + "</a>&nbsp;";
				sb.append(index).append("、 ").append(log).append("<hr>");
			}
		}
		return sb.toString();
	}

}
