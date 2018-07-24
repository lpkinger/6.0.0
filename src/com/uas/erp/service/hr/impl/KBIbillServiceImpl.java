package com.uas.erp.service.hr.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.service.hr.KBIbillService;

@Service
public class KBIbillServiceImpl implements KBIbillService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveKBIbill(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store,grid});
		// 保存KBIbill
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "KBIbill",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存Contact
		for (Map<Object, Object> s : grid) {
			s.put("kbd_id", baseDao.getSeqId("KBIbilldet_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"KBIbilldet");
		baseDao.execute(gridSql);
		// 更新总得分
		baseDao.execute("update KBIbill set kb_total=(select sum(to_number(kbd_score)) from kbibilldet where kbd_kbid="
				+ store.get("kb_id") + ") where kb_id=" + store.get("kb_id"));

		try {
			// 记录操作
			baseDao.logger.save(caller, "kb_id", store.get("kb_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store,grid});
	}

	@Override
	public void deleteKBIbill(int kb_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("KBIbill",
				"kb_statuscode", "kb_id=" + kb_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {kb_id });
		// 删除KBIbill
		baseDao.deleteById("KBIbill", "kb_id", kb_id);
		// 删除Contact
		baseDao.deleteById("KBIbilldet", "kbd_kbid", kb_id);
		// 记录操作
		baseDao.logger.delete(caller, "kb_id", kb_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] {kb_id });
	}

	@Override
	public void updateKBIbillById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("KBIbill",
				"kb_statuscode", "kb_id=" + store.get("kb_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] {store,gstore});
		// 修改KBIbill
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "KBIbill",
				"kb_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"KBIbilldet", "kbd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("kbd_id") == null || s.get("kbd_id").equals("")
					|| s.get("kbd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("KBIbilldet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "KBIbilldet",
						new String[] { "kbd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);

		// 更新总得分
		baseDao.execute("update KBIbill set kb_total=(select sum(to_number(kbd_score)) from kbibilldet where kbd_kbid="
				+ store.get("kb_id") + ") where kb_id=" + store.get("kb_id"));
		// 记录操作
		baseDao.logger.update(caller, "kb_id", store.get("kb_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] {store,gstore});
	}

	@Override
	public void submitKBIbill(int kb_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("KBIbill",
				"kb_statuscode", "kb_id=" + kb_id);
		StateAssert.submitOnlyEntering(status);
		int count = 0;
		count = baseDao
				.getCount("select count(*) from KBIbilldet where nvl(kbd_score,' ')=' ' and kbd_kbid="
						+ kb_id);
		if (count > 0) {
			BaseUtil.showError("明细还有未评价的指标项目，不能提交!");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeResSubmit(caller,  new Object[] {kb_id });
		// 执行提交操作
		baseDao.submit("KBIbill", "kb_id=" + kb_id, "kb_status", "kb_statuscode");
		// 更新总得分
		baseDao.execute("update KBIbill set kb_total=(select sum(to_number(kbd_score)) from kbibilldet where kbd_kbid="
				+ kb_id + ") where kb_id=" + kb_id);
		// 结束任务
		baseDao.execute("update projecttask set handstatus='已完成',handstatuscode='FINISHED' where nvl(name,' ') like '%KBI评估单%' and sourcecode=(select kb_code from kbibill where kb_id="
				+ kb_id + ")");
		// 结束提醒
		baseDao.execute("update ResourceAssignment set ra_status='已完成',ra_statuscode='FINISHED' where ra_taskid=(select id from  projecttask where  nvl(name,' ') like '%KBI评估单%' and sourcecode=(select kb_code from kbibill where kb_id="
				+ kb_id + "))");
		// 记录操作
		baseDao.logger.submit(caller, "kb_id", kb_id);
		// 执行提交后的其它逻辑
		handlerService.afterResSubmit(caller,  new Object[] {kb_id });
	}

	@Override
	public void resSubmitKBIbill(int kb_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("KBIbill",
				"kb_statuscode", "kb_id=" + kb_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("KBIbill", "kb_id=" + kb_id, "kb_status", "kb_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "kb_id", kb_id);
	}

	@Override
	public void auditKBIbill(int kb_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("KBIbill",
				"kb_statuscode", "kb_id=" + kb_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {kb_id});
		// 执行审核操作
		baseDao.audit("KBIbill", "kb_id=" + kb_id, "kb_status", "kb_statuscode", "kb_auditdate", "kb_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "kb_id", kb_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {kb_id});
	}

	@Override
	public void resAuditKBIbill(int kb_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("KBIbill",
				"kb_statuscode", "kb_id=" + kb_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("KBIbill", "kb_id=" + kb_id, "kb_status", "kb_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "kb_id", kb_id);
	}

	@Override
	public void endKBIBill(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			Object pdid = map.get("kb_id");
			baseDao.updateByCondition("KBIBill",
					"kb_statuscode='NULLIFIED',kb_status='已作废'", "kb_id="
							+ pdid);
			// 任务完成的动作
			baseDao.execute("update projecttask set handstatus='已完成',handstatuscode='FINISHED' where nvl(name,' ') like '%KBI评估单%' and sourcecode=(select kb_code from kbibill where kb_id="
					+ pdid + ")");
		}
	}

	@Override
	public String showKbi(String condition) {
		condition = condition + " and kb_status='已提交'";
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		int countnum = baseDao
				.getCount("select count(distinct kb_assessman) from kbibill where "
						+ condition + "");
		if (countnum > 1) {
			List<Object> spmans = baseDao.getFieldDatasByCondition("kbibill",
					" distinct kb_assessman", condition);
			for (Object spman : spmans) {
				String dtcondition = condition + " and kb_assessman='" + spman
						+ "'";
				// 取横向的列数
				List<Object> objects = baseDao.getFieldDatasByCondition(
						"kbistand", "ks_stand", " 1=1 order by ks_id");
				Map<String, Object> zmap = new HashMap<String, Object>();
				zmap.put("受评人", spman);
				// zmap.put("评价人", "总平均分");
				zmap.put("评价人归属", "平均分");
				List<Object[]> pjdepart = baseDao.getFieldsDatasByCondition(
						"kbibill", new String[] { "kb_attribution",
								"kb_schemeid", "KB_SCHEME" }, dtcondition
								+ " order by kb_attribution");
				double grzdf = 0;
				for (Object object : objects) {
					double zpjdf = 0;
					String zpjgs = null;
					String zpkbiid = null;
					for (Object[] os : pjdepart) {
						if (zpjgs != null && !zpjgs.equals(os[0].toString())) {
							double pjbl = 0;
							try {
								pjbl = Double.parseDouble(baseDao
										.getFieldDataByCondition(
												"KBISchemeDet",
												"ksd_rate",
												"ksd_ksid=" + os[1]
														+ " and ksd_type='"
														+ zpjgs + "'")
										.toString());
							} catch (Exception e) {
								BaseUtil.showError("方案" + os[2] + "中评价类型<"
										+ zpjgs + ">不存在！");
							}
							zpjdf = zpjdf
									+ Double.parseDouble(baseDao
											.getFieldDataByCondition(
													"kbibilldet left join kbibill on kb_id=kbd_kbid",
													"round(avg(kbd_score),2)",
													dtcondition
															+ " and kbd_target='"
															+ object
															+ "' and kb_attribution='"
															+ zpjgs + "'")
											.toString()) * pjbl / 100;
						} else {

						}
						zpjgs = os[0].toString();
						zpkbiid = os[1].toString();
					}
					double pjbl = Double.parseDouble(baseDao
							.getFieldDataByCondition(
									"KBISchemeDet",
									"ksd_rate",
									"ksd_ksid=" + zpkbiid + " and ksd_type='"
											+ zpjgs + "'").toString());
					zpjdf = zpjdf
							+ Double.parseDouble(baseDao
									.getFieldDataByCondition(
											"kbibilldet left join kbibill on kb_id=kbd_kbid",
											"round(avg(kbd_score),2)",
											dtcondition + " and kbd_target='"
													+ object
													+ "' and kb_attribution='"
													+ zpjgs + "'").toString())
							* pjbl / 100;
					zmap.put(object.toString(), NumberUtil.formatDouble(zpjdf, 2));
					grzdf = grzdf + NumberUtil.formatDouble(zpjdf, 2);
				}
				zmap.put("总分", NumberUtil.formatDouble(grzdf, 2));
				maps.add(zmap);
			}
		} else if (countnum == 1) {
			String spman = baseDao.getFieldDataByCondition("kbibill",
					"distinct kb_assessman", condition).toString();
			List<Object[]> list = baseDao.getFieldsDatasByCondition(
					"kbibilldet left join kbibill on kb_id=kbd_kbid",
					new String[] { "kb_season", "kb_attribution",
							"kb_recorder", "kbd_target", "kbd_score" },
					condition);
			// 取横向的列数
			List<Object> objects = baseDao.getFieldDatasByCondition("kbistand",
					"ks_stand", " 1=1 order by ks_id");
			List<Object[]> pjmans = baseDao.getFieldsDatasByCondition(
					"kbibill", new String[] { "kb_recorder", "kb_attribution",
							"kb_schemeid", "KB_SCHEME" }, condition
							+ " order by kb_attribution");
			String pjgs = null;
			int totali = 0;
			int parttotal = 0;
			String pjfaid = null;
			double zdf = 0;
			for (Object[] obj : pjmans) {
				if (pjgs != null && !pjgs.equals(obj[1].toString())) {
					totali = totali + parttotal;
					double pjbl = 0;
					try {
						pjbl = Double.parseDouble(baseDao
								.getFieldDataByCondition(
										"KBISchemeDet",
										"ksd_rate",
										"ksd_ksid=" + obj[2]
												+ " and ksd_type='" + pjgs
												+ "'").toString());
					} catch (Exception e) {
						BaseUtil.showError("方案" + obj[3] + "中评价类型<" + pjgs
								+ ">不存在！");
					}
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("受评人", spman);
					// map.put("评价人", "平均分");
					map.put("评价人归属", pjgs + "(平均分)");
					for (Object object : objects) {
						map.put(object.toString(),
								NumberUtil.formatDouble(
										Double.parseDouble(baseDao
												.getFieldDataByCondition(
														"kbibilldet left join kbibill on kb_id=kbd_kbid",
														"round(avg(kbd_score),2)",
														condition
																+ " and kbd_target='"
																+ object
																+ "' and kb_attribution='"
																+ pjgs + "'")
												.toString()), 2));
					}
					double atotal = NumberUtil.formatDouble(
							Double.parseDouble(baseDao.getFieldDataByCondition(
									"kbibill",
									"round(avg(kb_total),2)",
									condition + "  and kb_attribution='" + pjgs
											+ "'").toString()), 2);
					map.put("总分", atotal);
					// map.put("总分",BaseUtil.formatDouble(atotal/parttotal,2));
					maps.add(map);
					zdf = zdf + atotal / parttotal * pjbl / 100;
					parttotal = 1;

				} else {
					parttotal++;
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("受评人", spman);
				// map.put("评价人", obj[0]);
				map.put("评价人归属", obj[1]);
				double total = 0;
				for (Object[] o : list) {
					if (obj[0].equals(o[2])) {
						for (Object object : objects) {
							if (o[3].equals(object)) {
								map.put(object.toString(), o[4]);
								total = total
										+ Double.parseDouble(o[4].toString());
							}
						}
						map.put("总分", total);
					}
				}
				maps.add(map);
				pjgs = obj[1].toString();
				pjfaid = obj[2].toString();
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("受评人", spman);
			// map.put("评价人", "平均分");
			map.put("评价人归属", pjgs + "(平均分)");
			double pjbl = Double.parseDouble(baseDao.getFieldDataByCondition(
					"KBISchemeDet", "ksd_rate",
					"ksd_ksid=" + pjfaid + " and ksd_type='" + pjgs + "'")
					.toString());
			for (Object object : objects) {
				map.put(object.toString(),
						NumberUtil.formatDouble(
								Double.parseDouble(baseDao
										.getFieldDataByCondition(
												"kbibilldet left join kbibill on kb_id=kbd_kbid",
												"round(avg(kbd_score),2)",
												condition
														+ " and kbd_target='"
														+ object
														+ "' and kb_attribution='"
														+ pjgs + "'")
										.toString()), 2));
			}
			double btotal = Double.parseDouble(baseDao.getFieldDataByCondition(
					"kbibill", "round(avg(kb_total),2)",
					condition + "  and kb_attribution='" + pjgs + "'")
					.toString());
			map.put("总分", btotal);
			maps.add(map);
			zdf = zdf + btotal * pjbl / 100;
			Map<String, Object> zmap = new HashMap<String, Object>();
			zmap.put("受评人", spman);
			// zmap.put("评价人", "总平均分");
			zmap.put("评价人归属", "总平均分");
			List<Object[]> pjdepart = baseDao.getFieldsDatasByCondition(
					"kbibill",
					new String[] { "kb_attribution", "kb_schemeid" }, condition
							+ " order by kb_attribution");
			double grzdf = 0;
			for (Object object : objects) {
				double zpjdf = 0;
				String zpjgs = null;
				String zpkbiid = null;
				for (Object[] os : pjdepart) {
					if (zpjgs != null && !zpjgs.equals(os[0].toString())) {
						pjbl = Double.parseDouble(baseDao
								.getFieldDataByCondition(
										"KBISchemeDet",
										"ksd_rate",
										"ksd_ksid=" + os[1] + " and ksd_type='"
												+ zpjgs + "'").toString());
						zpjdf = zpjdf
								+ Double.parseDouble(baseDao
										.getFieldDataByCondition(
												"kbibilldet left join kbibill on kb_id=kbd_kbid",
												"round(avg(kbd_score),2)",
												condition
														+ " and kbd_target='"
														+ object
														+ "' and kb_attribution='"
														+ zpjgs + "'")
										.toString()) * pjbl / 100;
					} else {

					}
					zpjgs = os[0].toString();
					zpkbiid = os[1].toString();
				}
				pjbl = Double.parseDouble(baseDao
						.getFieldDataByCondition(
								"KBISchemeDet",
								"ksd_rate",
								"ksd_ksid=" + zpkbiid + " and ksd_type='"
										+ zpjgs + "'").toString());
				zpjdf = zpjdf
						+ Double.parseDouble(baseDao
								.getFieldDataByCondition(
										"kbibilldet left join kbibill on kb_id=kbd_kbid",
										"round(avg(kbd_score),2)",
										condition + " and kbd_target='"
												+ object
												+ "' and kb_attribution='"
												+ zpjgs + "'").toString())
						* pjbl / 100;
				zmap.put(object.toString(), NumberUtil.formatDouble(zpjdf, 2));
				grzdf = grzdf + NumberUtil.formatDouble(zpjdf, 2);
			}
			zmap.put("总分", NumberUtil.formatDouble(grzdf, 2));
			maps.add(zmap);
		} else {

		}
		return BaseUtil.parseGridStore2Str(maps);
	}

	@Override
	public List<GridFields> getGridFields() {
		GridFields field = null;
		List<GridFields> gridFields = new ArrayList<GridFields>();
		field = new GridFields();
		field.setName("受评人");
		gridFields.add(field);
		/*
		 * field = new GridFields(); field.setName("评价人");
		 * gridFields.add(field);
		 */
		field = new GridFields();
		field.setName("评价人归属");
		gridFields.add(field);
		List<Object> objects = baseDao.getFieldDatasByCondition("kbistand",
				"ks_stand", " 1=1 order by ks_id");
		for (Object object : objects) {
			field = new GridFields();
			field.setName(object.toString());
			gridFields.add(field);
		}
		field = new GridFields();
		field.setName("总分");
		gridFields.add(field);
		return gridFields;
	}

	@Override
	public List<GridColumns> getGridColumns() {
		GridColumns column = null;
		GridColumns dcolumn = null;
		GridColumns tcolumn = null;
		List<GridColumns> gridColumns = new ArrayList<GridColumns>();
		column = new GridColumns();
		column.setHeader("受评人");
		column.setText("受评人");
		column.setDataIndex("受评人");
		column.setWidth(100);
		column.setReadOnly(true);
		gridColumns.add(column);
		/*
		 * column = new GridColumns(); column.setHeader("评价人");
		 * column.setText("评价人"); column.setDataIndex("评价人");
		 * column.setWidth(100); column.setReadOnly(true);
		 * gridColumns.add(column);
		 */
		column = new GridColumns();
		column.setHeader("评价人归属");
		column.setText("评价人归属");
		column.setDataIndex("评价人归属");
		column.setWidth(130);
		column.setReadOnly(true);
		gridColumns.add(column);
		List<Object[]> objects = baseDao.getFieldsDatasByCondition("kbistand",
				new String[] { "ks_stand", "ks_key" }, " 1=1 order by ks_id");
		for (Object[] object : objects) {
			tcolumn = null;
			if (gridColumns != null) {
				for (GridColumns topcolumn : gridColumns) {
					if (topcolumn.getText().equals(object[1])) {
						tcolumn = topcolumn;
						break;
					}
				}
			}
			column = new GridColumns();
			column.setReadOnly(true);
			column.setWidth(100);
			if (tcolumn != null) {
				column.setDataIndex(object[0].toString());
				column.setHeader(object[0].toString());
				column.setText(object[0].toString());
				tcolumn.getGridcolumns().add(column);
			} else {
				column.setDataIndex(object[1].toString());
				column.setHeader(object[1].toString());
				column.setText(object[1].toString());
				dcolumn = new GridColumns();
				dcolumn.setReadOnly(true);
				dcolumn.setWidth(100);
				dcolumn.setDataIndex(object[0].toString());
				dcolumn.setHeader(object[0].toString());
				dcolumn.setText(object[0].toString());
				column.getGridcolumns().add(dcolumn);
				gridColumns.add(column);
			}

		}
		column = new GridColumns();
		column.setHeader("总分");
		column.setText("总分");
		column.setDataIndex("总分");
		column.setWidth(100);
		column.setReadOnly(true);
		gridColumns.add(column);
		return gridColumns;
	}

	@Override
	public List<Object> getKeys() {
		return baseDao.getFieldDatasByCondition("kbistand group by ks_key order by ks_id",
				"distinct ks_key, min(ks_id)ks_id", "");
		// select distinct min(ks_id)ks_id,ks_key from kbistand group by ks_key
		// order by ks_id
	}

	@Override
	public Map<Object, List<Object[]>> getAssessValue(String key) {
		List<Object[]> stands = baseDao.getFieldsDatasByCondition("kbistand", new String[] { "ks_id", "ks_stand" },
				"ks_key='" + key + "' order by ks_id");
		Map<Object, List<Object[]>> assessValues = new HashMap<Object, List<Object[]>>();
		for (Object[] stand : stands) {
			List<Object[]> assessValue = baseDao.getFieldsDatasByCondition("kbistanddetail", new String[] {
					"ksd_detail", "ksd_score" }, "ksd_ksid=" + stand[0] + " order by ksd_score desc");
			assessValues.put(stand[1], assessValue);
		}
		return assessValues;
	}

}
